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
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This scene manager is used for asynchronous loading and saving of scenes.
 * This class ensures that only one scene action happens at a time, and
 * the actions are performed on a separate thread to avoid blocking the GUI.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AsynchronousSceneManager extends Thread implements SceneManager {

  private final SynchronousSceneManager sceneManager;
  private final LinkedBlockingQueue<Runnable> taskQueue;

  public AsynchronousSceneManager(RenderContext context, RenderManager renderManager) {
    super("Scene Manager");

    sceneManager = new SynchronousSceneManager(context, renderManager);
    taskQueue = new LinkedBlockingQueue<>();
  }

  public SceneProvider getSceneProvider() {
    return sceneManager;
  }

  public void setResetHandler(RenderResetHandler resetHandler) {
    sceneManager.setResetHandler(resetHandler);
  }

  public void setTaskTracker(TaskTracker taskTracker) {
    sceneManager.setTaskTracker(taskTracker);
  }

  public void setOnSceneLoaded(Runnable onSceneLoaded) {
    sceneManager.setOnSceneLoaded(onSceneLoaded);
  }

  public void setOnChunksLoaded(Runnable onChunksLoaded) {
    sceneManager.setOnChunksLoaded(onChunksLoaded);
  }

  @Override public Scene getScene() {
    return sceneManager.getScene();
  }

  @Override public void run() {
    try {
      while (!isInterrupted()) {
        Runnable task = taskQueue.take();
        task.run();
      }
    } catch (InterruptedException ignored) {
      // Interrupted.
    } catch (Throwable e) {
      if (e instanceof OutOfMemoryError) {
        Log.error("Chunky has run out of memory! Increase the memory given to Chunky in the launcher.", e);
      } else {
        Log.error("Scene manager has crashed due to an uncaught exception. " +
            "Chunky will not work properly until you restart it. " +
            "If you think this is a bug, please report it to the developers.", e);
      }
      throw e;
    }
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
        Log.warn("Could not load scene.\nReason: " + e.getMessage());
      } catch (InterruptedException e) {
        Log.warn("Scene loading was interrupted.");
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
        Log.warn("Could not load scene.\nReason: " + e.getMessage());
      } catch (InterruptedException e) {
        Log.warn("Scene loading was interrupted.");
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
        Log.warn("Scene saving was interrupted.");
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
        Log.warn("Scene saving was interrupted.");
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

  /**
   * Schedule a task to be run soon.
   */
  public void enqueueTask(Runnable task) {
    taskQueue.add(task);
  }

  /**
   * Find a preferred scene name by attempting to avoid name collisions.
   *
   * @return the preferred scene name
   */
  public static String preferredSceneName(RenderContext context, String name) {
    String suffix = "";
    name = sanitizedSceneName(name);
    int count = 0;
    do {
      String targetName = name + suffix;
      if (sceneNameIsAvailable(context, targetName)) {
        return targetName;
      }
      count += 1;
      suffix = "" + count;
    } while (count < 256);
    // Give up.
    return name;
  }

  /**
   * Remove problematic characters from scene name.
   *
   * @return sanitized scene name
   */
  public static String sanitizedSceneName(String name) {
    name = name.trim();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < name.length(); ++i) {
      char c = name.charAt(i);
      if (isValidSceneNameChar(c)) {
        sb.append(c);
      } else if (c >= '\u0020' && c <= '\u007e') {
        sb.append('_');
      }
    }
    String stripped = sb.toString().trim();
    if (stripped.isEmpty()) {
      return "Scene";
    } else {
      return stripped;
    }
  }

  /**
   * @return <code>false</code> if the character can cause problems on any
   * supported platform.
   */
  public static boolean isValidSceneNameChar(char c) {
    switch (c) {
      case '/':
      case ':':
      case ';':
      case '\\': // Windows file separator.
      case '*':
      case '?':
      case '"':
      case '<':
      case '>':
      case '|':
        return false;
    }
    if (c < '\u0020') {
      return false;
    }
    return c <= '\u007e' || c >= '\u00a0';
  }

  /**
   * Check for scene name collision.
   *
   * @return <code>true</code> if the scene name does not collide with an
   * already existing scene
   */
  public static boolean sceneNameIsAvailable(RenderContext context, String sceneName) {
    return !context.getSceneDescriptionFile(sceneName).exists();
  }

  /**
   * Check for scene name validity.
   *
   * @return <code>true</code> if the scene name contains only legal characters
   */
  public static boolean sceneNameIsValid(String name) {
    for (int i = 0; i < name.length(); ++i) {
      if (!isValidSceneNameChar(name.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public void discardSceneChanges() {
    sceneManager.discardSceneChanges();
  }

  public void applySceneChanges() {
    sceneManager.applySceneChanges();
  }
}
