/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.RenderContext;
import se.llbit.chunky.renderer.RenderManager;
import se.llbit.chunky.renderer.SceneIOProvider;
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This scene manager is used for asynchronous loading and saving of scenes.
 * This class ensures that only one scene action happens at a time, and
 * the actions are performed on a separate thread to avoid blocking the GUI.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AsynchronousSceneManager implements SceneManager {

  private final SynchronousSceneManager sceneManager;
  private final ExecutorService executorService = Executors.newSingleThreadExecutor(runnable -> {
    Thread thread = new Thread(runnable, "AsynchronousSceneManager-Thread");
    thread.setUncaughtExceptionHandler((t, exception) -> {
      if (exception instanceof OutOfMemoryError) {
        Log.error(
          "Chunky has run out of memory! Increase the memory given to Chunky in the launcher.",
          exception);
        throw (Error) exception;
      } else if(exception instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      } else {
        Log.error("Scene manager has crashed due to an uncaught exception. \n" +
          "Chunky might not work properly until you restart it.\n" +
          "If you think this is a bug, please report it and the following lines to the developers.",
          exception);
        // a new thread will be created by the executor after the current one terminated and a new task is enqueued
      }
    });
    return thread;
  });

  public AsynchronousSceneManager(RenderContext context, RenderManager renderManager) {
    sceneManager = new SynchronousSceneManager(context, renderManager);
  }

  /**
   * Schedule a task to be run soon.
   */
  public void enqueueTask(Runnable task) {
    executorService.execute(task);
  }

  @Override
  public SceneProvider getSceneProvider() {
    return sceneManager;
  }

  public void setResetHandler(RenderResetHandler resetHandler) {
    sceneManager.setResetHandler(resetHandler);
  }

  public void setTaskTracker(TaskTracker taskTracker) {
    sceneManager.setTaskTracker(taskTracker);
  }

  @Override
  public TaskTracker getTaskTracker() {
    return sceneManager.getTaskTracker();
  }

  public void setOnSceneLoaded(Runnable onSceneLoaded) {
    sceneManager.setOnSceneLoaded(onSceneLoaded);
  }

  public void setOnSceneSaved(Runnable onSceneSaved) {
    sceneManager.setOnSceneSaved(onSceneSaved);
  }

  public void setOnChunksLoaded(Runnable onChunksLoaded) {
    sceneManager.setOnChunksLoaded(onChunksLoaded);
  }

  @Override public Scene getScene() {
    return sceneManager.getScene();
  }

  /**
   * Load the given scene.
   *
   * @param name the name of the scene to load.
   */
  @PluginApi
  @Override public void loadScene(File sceneDirectory, String name) {
    enqueueTask(() -> {
      try {
        sceneManager.loadScene(sceneDirectory, name);
      } catch (IOException e) {
        Log.warn("Could not load scene.\nReason:", e);
      } catch (InterruptedException e) {
        Log.warn("Scene loading was interrupted.", e);
      }
    });
  }

  @PluginApi
  @Deprecated
  @Override public void loadScene(String name) {
    enqueueTask(() -> {
      try {
        sceneManager.loadScene(name);
      } catch (IOException e) {
        Log.warn("Could not load scene.", e);
      } catch (InterruptedException e) {
        Log.warn("Scene loading was interrupted.", e);
      }
    });
  }

  /**
   * Save the current scene.
   */
  @PluginApi
  @Override public void saveScene(File sceneDirectory) {
    enqueueTask(() -> {
      try {
        sceneManager.saveScene(sceneDirectory);
      } catch (InterruptedException e) {
        Log.warn("Scene saving was interrupted.", e);
      }
    });
  }

  @PluginApi
  @Deprecated
  @Override public void saveScene() {
    enqueueTask(() -> {
      try {
        sceneManager.saveScene();
      } catch (InterruptedException e) {
        Log.warn("Scene saving was interrupted.", e);
      }
    });
  }

  /**
   * Save the current scene.
   * @param newName Name of the scene copy
   */
  public void saveSceneAs(String newName) {
    enqueueTask(() -> {
      try {
        sceneManager.withSceneProtected(scene -> scene.setName(newName));
        sceneManager.saveSceneAs(newName);
      } catch (InterruptedException e) {
        Log.warn("Scene saving was interrupted.", e);
      }
    });
  }

  /**
   * Save a copy of the current scene using the given io provider.
   * @param ioProvider IO provider to use for saving the scene
   * @param newName Name of the scene copy
   */
  public void saveSceneCopy(SceneIOProvider ioProvider, String newName) {
    enqueueTask(() -> {
      try {
        Scene[] copy = new Scene[1];
        sceneManager.withSceneProtected(scene -> {
          copy[0] = new Scene(scene);
        });
        copy[0].setName(newName);
        sceneManager.saveScene(ioProvider, copy[0]);
      } catch (InterruptedException e) {
        Log.warn("Scene saving was interrupted.", e);
      }
    });
  }

  /**
   * Load chunks and reset camera.
   */
  @Override
  public void loadFreshChunks(World world, Collection<ChunkPosition> chunks) {
    enqueueTask(() -> sceneManager.loadFreshChunks(world, chunks));
  }

  /**
   * Load chunks without moving the camera.
   */
  @Override
  public void loadChunks(World world, Collection<ChunkPosition> chunks) {
    enqueueTask(() -> sceneManager.loadChunks(world, chunks));
  }

  /**
   * Reload all chunks
   */
  @Override
  public void reloadChunks() {
    enqueueTask(sceneManager::reloadChunks);
  }

  /**
   * Merge a render dump into the current render.
   */
  public void mergeRenderDump(File renderDump) {
    enqueueTask(() -> sceneManager.mergeDump(renderDump));
  }

  public void discardSceneChanges() {
    sceneManager.discardSceneChanges();
  }

  public void applySceneChanges() {
    sceneManager.applySceneChanges();
  }
}
