/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.RenderStatus;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.ResetReason;
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A synchronous scene manager runs its operations on the calling thread.
 *
 * <p>The scene manager stores the current scene state and pending
 * scene state changes. The scene manager is responsible for protecting
 * parts of the scene data from concurrent writes & reads by
 * the user (through the UI) and renderManager.
 */
public class SynchronousSceneManager implements SceneProvider, SceneManager {
  /**
   * This stores all pending scene state changes. When the scene edit
   * grace period has expired any changes to this scene state are not
   * copied directly to the stored scene state.
   *
   * Multiple threads can try to read/write the mutable scene concurrently,
   * so multiple accesses are serialized by the intrinsic lock of the Scene
   * class.
   *
   * NB: lock ordering for scene and storedScene is always scene->storedScene!
   */
  private final Scene scene;

  /**
   * Stores the current scene configuration. When the scene edit grace period has
   * expired a reset confirm dialog will be shown before applying any further
   * non-transitory changes to the stored scene state.
   */
  private final Scene storedScene;

  private final RenderContext context;

  private final RenderManager renderManager;

  private RenderResetHandler resetHandler = () -> true;
  private TaskTracker taskTracker = new TaskTracker(ProgressListener.NONE);
  private Runnable onSceneLoaded = () -> {};
  private Runnable onChunksLoaded = () -> {};

  public SynchronousSceneManager(RenderContext context, RenderManager renderManager) {
    this.context = context;
    this.renderManager = renderManager;

    scene = context.getChunky().getSceneFactory().newScene();
    scene.initBuffers();

    // The stored scene is a copy of the mutable scene. They even share
    // some data structures that are only used by the renderManager.
    storedScene = context.getChunky().getSceneFactory().copyScene(scene);
  }

  public void setResetHandler(RenderResetHandler resetHandler) {
    this.resetHandler = resetHandler;
  }

  public void setTaskTracker(TaskTracker taskTracker) {
    this.taskTracker = taskTracker;
  }

  public void setOnSceneLoaded(Runnable onSceneLoaded) {
    this.onSceneLoaded = onSceneLoaded;
  }

  public void setOnChunksLoaded(Runnable onChunksLoaded) {
    this.onChunksLoaded = onChunksLoaded;
  }

  @Override public Scene getScene() {
    return scene;
  }

  @Override public void saveScene() throws InterruptedException {
    try {
      synchronized (storedScene) {
        String sceneName = storedScene.name();
        Log.info("Saving scene " + sceneName);
        File sceneDir = resolveSceneDirectory(sceneName);
        context.setSceneDirectory(sceneDir);
        if (!sceneDir.isDirectory()) {
          boolean success = sceneDir.mkdirs();
          if (!success) {
            Log.warn("Failed to create scene directory: " + sceneDir.getAbsolutePath());
            return;
          }
        }

        // Create backup of scene description and current render dump.
        storedScene.backupFile(context, context.getSceneDescriptionFile(sceneName));
        storedScene.backupFile(context, new File(sceneDir, sceneName + ".dump"));

        // Copy render status over from the renderManager.
        RenderStatus status = renderManager.getRenderStatus();
        storedScene.renderTime = status.getRenderTime();
        storedScene.spp = status.getSpp();
        storedScene.saveScene(context, taskTracker);
        Log.info("Scene saved");
      }
    } catch (IOException e) {
      Log.error("Failed to save scene. Reason: " + e.getMessage(), e);
    }
  }

  @Override public void loadScene(String sceneName)
      throws IOException, InterruptedException {

    // Do not change lock ordering here.
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      try (TaskTracker.Task ignored = taskTracker.task("Loading scene", 1)) {
        context.setSceneDirectory(resolveSceneDirectory(sceneName));
        scene.loadScene(context, sceneName, taskTracker);
      }

      // Update progress bar.
      taskTracker.backgroundTask().update("Rendering", scene.getTargetSpp(), scene.spp);

      scene.setResetReason(ResetReason.SCENE_LOADED);

      // Wake up waiting threads in awaitSceneStateChange().
      scene.notifyAll();
    }
    onSceneLoaded.run();
  }

  @Override public void loadFreshChunks(World world, Collection<ChunkPosition> chunksToLoad) {
    synchronized (scene) {
      scene.clear();
      scene.loadChunks(taskTracker, world, chunksToLoad);
      scene.resetScene(null, context.getChunky().getSceneFactory());
      context.setSceneDirectory(resolveSceneDirectory(scene.name));
      scene.refresh();
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.setRenderMode(RenderMode.PREVIEW);
    }
    onSceneLoaded.run();
  }

  @Override public void loadChunks(World world, Collection<ChunkPosition> chunksToLoad) {
    synchronized (scene) {
      int prevChunkCount = scene.numberOfChunks();
      context.setSceneDirectory(resolveSceneDirectory(scene.name));
      scene.loadChunks(taskTracker, world, chunksToLoad);
      if (prevChunkCount == 0) {
        scene.moveCameraToCenter();
      }
      scene.refresh();
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.setRenderMode(RenderMode.PREVIEW);
    }
    onChunksLoaded.run();
  }

  @Override public void reloadChunks() {
    synchronized (scene) {
      scene.reloadChunks(taskTracker);
      scene.refresh();
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.setRenderMode(RenderMode.PREVIEW);
    }
    onChunksLoaded.run();
  }

  @Override public ResetReason awaitSceneStateChange() throws InterruptedException {
    synchronized (scene) {
      while (true) {
        if (scene.shouldRefresh() && (scene.getForceReset() || resetHandler.allowSceneRefresh())) {
          synchronized (storedScene) {
            storedScene.copyState(scene);
            storedScene.mode = scene.mode;
          }
          ResetReason reason = scene.getResetReason();
          scene.clearResetFlags();
          return reason;
        } else if (scene.getMode() != storedScene.getMode()) {
          // Make sure the renderManager sees the updated render mode.
          // TODO: handle buffer finalization updates as state change.
          synchronized (storedScene) {
            storedScene.mode = scene.mode;
          }
          return ResetReason.MODE_CHANGE;
        }
        scene.wait();
      }
    }
  }

  @Override public boolean pollSceneStateChange() {
    if (scene.shouldRefresh() && (scene.getForceReset() || resetHandler.allowSceneRefresh())) {
      return true;
    } else {
      return scene.getMode() != storedScene.getMode();
    }
  }

  @Override public void withSceneProtected(Consumer<Scene> fun) {
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      synchronized (storedScene) {
        storedScene.copyTransients(scene);
        fun.accept(storedScene);
      }
    }
  }

  @Override public void withEditSceneProtected(Consumer<Scene> fun) {
    synchronized (scene) {
      fun.accept(scene);
    }
  }
  /**
   * Merge a render dump into the current render.
   *
   * @param dumpFile the file to be merged.
   */
  protected void mergeDump(File dumpFile) {
    synchronized (scene) {
      renderManager.withSampleBufferProtected((samples, width, height) -> {
        if (width != scene.width || height != scene.height) {
          throw new Error("Failed to merge render dump - wrong canvas size.");
        }
        scene.mergeDump(dumpFile, taskTracker);
      });
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.pauseRender();
    }
  }

  /**
   * Discard pending scene changes.
   */
  public void applySceneChanges() {
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      synchronized (storedScene) {
        // Setting SCENE_LOADED will force the reset.
        scene.setResetReason(ResetReason.SCENE_LOADED);

        // Wake up the threads waiting in awaitSceneStateChange().
        scene.notifyAll();
      }
    }
  }

  /**
   * Apply pending scene changes.
   */
  public void discardSceneChanges() {
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      synchronized (storedScene) {
        scene.copyState(storedScene);
        scene.clearResetFlags();
      }
    }
  }

  /**
   * Find and resolve the directory for a given scene name. If the scene is saved in the /scenes/ folder, it will return
   * the scenes folder.
   *
   * If the scene is found in a folder inside the /scenes/ directory (eg. /scenes/some_scene/) that directory will be
   * returned.
   *
   * Otherwise, a new directory in the /scenes/ folder will be created for the given scene and that said directory will
   * be returned.
   *
   * @param sceneName The name of the scene to resolve the directory for.
   * @return The directory holding the given scene
   */
  public static File resolveSceneDirectory(String sceneName) {
    File defaultDirectory = new File(PersistentSettings.getSceneDirectory(), sceneName);

    if (!defaultDirectory.exists()) {

      File descFile = new File(PersistentSettings.getSceneDirectory(), sceneName + Scene.EXTENSION);
      if (descFile.exists()) {
        return PersistentSettings.getSceneDirectory();
      }

      descFile = new File(PersistentSettings.getSceneDirectory() + File.separator + sceneName, sceneName + Scene.EXTENSION);
      if (descFile.exists()) {
        return descFile.getParentFile();
      }
    }
    return defaultDirectory;
  }
}
