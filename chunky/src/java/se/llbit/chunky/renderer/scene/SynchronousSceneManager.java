/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.*;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
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
   * This stores all pending scene state changes. Until the scene edit
   * grace period has expired any changes to this scene state are also
   * applied to the stored scene state {@link #storedScene}. After that,
   * a reset confirm dialog will be shown before applying any further
   * non-transitory changes.
   *
   * <p>Multiple threads can try to read/write the mutable scene concurrently,
   * so multiple accesses are serialized by the intrinsic lock of the Scene
   * class.
   *
   * <p><strong>Important: lock ordering for scene and storedScene is always scene->storedScene!
   */
  private final Scene scene;

  /**
   * Stores the current scene configuration. When the scene edit grace period has
   * expired a reset confirm dialog will be shown before applying any further
   * non-transitory changes from the pending scene state changes in {@link #scene}.
   */
  private final Scene storedScene;

  private final RenderContext context;

  private final RenderManager renderManager;

  private RenderResetHandler resetHandler = () -> true;
  private TaskTracker taskTracker = new TaskTracker(ProgressListener.NONE);
  private Runnable onSceneLoaded = () -> {};
  private Runnable onSceneSaved = () -> {};
  private Runnable onChunksLoaded = () -> {};

  private final Set<BiConsumer<ResetReason, Scene>> resetListeners = new CopyOnWriteArraySet<>();

  public SynchronousSceneManager(RenderContext context, RenderManager renderManager) {
    this.context = context;
    this.renderManager = renderManager;

    scene = context.getChunky().getSceneFactory().newScene();
    scene.initBuffers();
    context.setSceneDirectory(new File(context.getChunky().options.sceneDir, scene.name));

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
  
  @Override
  public TaskTracker getTaskTracker() {
    return taskTracker;
  }

  public void setOnSceneLoaded(Runnable onSceneLoaded) {
    this.onSceneLoaded = onSceneLoaded;
  }

  public void setOnSceneSaved(Runnable onSceneSaved) {
    this.onSceneSaved = onSceneSaved;
  }

  public void setOnChunksLoaded(Runnable onChunksLoaded) {
    this.onChunksLoaded = onChunksLoaded;
  }

  @Override public Scene getScene() {
    return scene;
  }

  @Override
  public SceneProvider getSceneProvider() {
    return this;
  }

  @PluginApi
  @Override public void saveScene(File sceneDirectory) throws InterruptedException {
    synchronized (storedScene) {
      context.setSceneDirectory(sceneDirectory);
      saveScene(context, storedScene);
    }
  }

  @PluginApi
  @Deprecated
  @Override public void saveScene() throws InterruptedException {
    saveScene(context.getSceneDirectory());
  }

  public void saveSceneAs(String newName) throws InterruptedException {
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      synchronized (storedScene) {
        scene.setName(newName);
        storedScene.setName(newName);
      }
    }
    saveScene(resolveSceneDirectory(newName));
  }

  public void saveScene(SceneIOProvider ioContext, Scene scene) throws InterruptedException {
    try {
      String sceneName = scene.name();
      Log.info("Saving scene " + sceneName);
      File sceneDir = ioContext.getSceneDirectory();
      if (!sceneDir.isDirectory()) {
        sceneDir = resolveSceneDirectory(sceneName);
      }
      if (!sceneDir.isDirectory()) {
        boolean success = sceneDir.mkdirs();
        if (!success) {
          Log.warn("Failed to create scene directory: " + sceneDir.getAbsolutePath());
          return;
        }
      }

      // Create backup of scene description and current render dump.
      scene.backupFile(sceneDir, new File(sceneDir, sceneName + Scene.EXTENSION));
      scene.backupFile(sceneDir, new File(sceneDir, sceneName + ".dump"));

      // Copy render status over from the renderManager.
      RenderStatus status = renderManager.getRenderStatus();
      scene.renderTime = status.getRenderTime();
      scene.spp = status.getSpp();
      scene.saveScene(ioContext, taskTracker);
      Log.info("Scene saved");
      this.onSceneSaved.run();
    } catch (IOException e) {
      Log.error("Failed to save scene. Reason: " + e.getMessage(), e);
    }
  }

  @PluginApi
  @Override public void loadScene(File sceneDirectory, String sceneName)
      throws IOException, InterruptedException {

    // Do not change lock ordering here.
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      try (TaskTracker.Task ignored = taskTracker.task("Loading scene", 1)) {
        if (sceneDirectory.isDirectory()) {
          context.setSceneDirectory(sceneDirectory);
        }
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

  @PluginApi
  @Deprecated
  @Override public void loadScene(String sceneName)
    throws IOException, InterruptedException {

    // Do not change lock ordering here.
    // Lock order: scene -> storedScene.
    synchronized (scene) {
      try (TaskTracker.Task ignored = taskTracker.task("Loading scene", 1)) {
        File sceneDirectory = resolveSceneDirectory(sceneName);
        if (sceneDirectory.isDirectory()) {
          context.setSceneDirectory(sceneDirectory);
        }
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
      context.setSceneDirectory(new File(context.getChunky().options.sceneDir, scene.name));
      scene.refresh();
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.setRenderMode(RenderMode.PREVIEW);
    }
    onSceneLoaded.run();
  }

  @Override public void loadChunks(World world, Collection<ChunkPosition> chunksToLoad) {
    synchronized (scene) {
      int prevChunkCount = scene.numberOfChunks();
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
    ResetReason reason;
    synchronized (scene) {
      while (true) {
        if (scene.shouldRefresh() && (scene.getForceReset() || resetHandler.allowSceneRefresh())) {
          synchronized (storedScene) {
            storedScene.copyState(scene);
            storedScene.mode = scene.mode;
          }
          reason = scene.getResetReason();
          scene.clearResetFlags();
          break;
        } else if (scene.getMode() != storedScene.getMode()) {
          // Make sure the renderManager sees the updated render mode.
          // TODO: handle buffer finalization updates as state change.
          synchronized (storedScene) {
            storedScene.mode = scene.mode;
          }
          reason = ResetReason.MODE_CHANGE;
          break;
        }
        scene.wait();
      }
    }

    for (BiConsumer<ResetReason, Scene> listener : resetListeners) {
      listener.accept(reason, scene);
    }

    return reason;
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

  @Override
  public void addChangeListener(BiConsumer<ResetReason, Scene> listener) {
    resetListeners.add(listener);
  }

  @Override
  public void removeChangeListener(BiConsumer<ResetReason, Scene> listener) {
    resetListeners.remove(listener);
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
   * Apply pending scene changes from {@link #scene} to {@link #storedScene}.
   * <p>The changes will be loading with the scene reset {@link ResetReason#SCENE_LOADED}.
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
   * Discard pending scene changes in {@link #scene} and revert the state to {@link #storedScene}.
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
  @Deprecated /* Remove in 2.6 snapshots */
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
