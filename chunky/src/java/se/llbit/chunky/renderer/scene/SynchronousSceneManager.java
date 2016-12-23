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

import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.RenderStatus;
import se.llbit.chunky.renderer.Renderer;
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
 * the user (through the UI) and renderer.
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

  private final Renderer renderer;

  private RenderResetHandler resetHandler = () -> true;
  private TaskTracker taskTracker = new TaskTracker(ProgressListener.NONE);
  private Runnable onSceneLoaded = () -> {};
  private Runnable onChunksLoaded = () -> {};

  public SynchronousSceneManager(RenderContext context, Renderer renderer) {
    this.context = context;
    this.renderer = renderer;

    scene = context.getChunky().getSceneFactory().newScene();
    scene.initBuffers();

    // The stored scene is a copy of the mutable scene. They even share
    // some data structures that are only used by the renderer.
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

        File sceneDir = context.getSceneDirectory();
        if (!sceneDir.isDirectory()) {
          Log.warn("Scene directory does not exist. Creating directory at: "
              + sceneDir.getAbsolutePath());
          boolean success = sceneDir.mkdirs();
          if (!success) {
            Log.warn("Failed to create scene directory: " + sceneDir.getAbsolutePath());
            return;
          }
        }

        // Create backup of scene description and current render dump.
        storedScene.backupFile(context, context.getSceneDescriptionFile(sceneName));
        storedScene.backupFile(context, sceneName + ".dump");

        // Copy render status over from the renderer.
        RenderStatus status = renderer.getRenderStatus();
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
      throws IOException, SceneLoadingError, InterruptedException {

    // Do not change lock ordering here.
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      try (TaskTracker.Task ignored = taskTracker.task("Loading scene", 1)) {
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
      scene.moveCameraToCenter();
      scene.refresh();
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.setRenderMode(RenderMode.PREVIEW);
    }
    onSceneLoaded.run();
  }

  @Override public void loadChunks(World world, Collection<ChunkPosition> chunksToLoad) {
    synchronized (scene) {
      scene.loadChunks(taskTracker, world, chunksToLoad);
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
          // Make sure the renderer sees the updated render mode.
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
    } else if (scene.getMode() != storedScene.getMode()) {
      return true;
    }
    return false;
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
      renderer.withSampleBufferProtected((samples, width, height) ->{
        if (width != scene.width || height != scene.height) {
          throw new Error("Failed to merge render dump - wrong canvas size.");
        }
        scene.mergeDump(dumpFile, taskTracker);
      });
      scene.setResetReason(ResetReason.SCENE_LOADED);
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

}
