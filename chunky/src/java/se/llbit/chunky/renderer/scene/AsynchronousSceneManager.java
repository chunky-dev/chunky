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

/**
 * This scene manager is used for asynchronous loading and saving of scenes.
 * This class ensures that only one scene action happens at a time, and
 * the actions are performed on a separate thread to avoid blocking the GUI.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AsynchronousSceneManager extends Thread implements SceneManager {

  private final SynchronousSceneManager sceneManager;
  private Runnable currentTask = null;

  public AsynchronousSceneManager(RenderContext context, RenderManager renderManager) {
    super("Scene Manager");

    sceneManager = new SynchronousSceneManager(context, renderManager);
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
        synchronized (this) {
          while (currentTask == null) {
            wait();
          }
        }
        currentTask.run();
        synchronized (this) {
          currentTask = null;
        }
      }
    } catch (InterruptedException ignored) {
      // Interrupted.
    }
  }

  /**
   * Load the given scene.
   *
   * @param name the name of the scene to load.
   */
  @Override public synchronized void loadScene(String name) {
    if (currentTask != null) {
      Log.warn("Can't load scene right now.");
    } else {
      currentTask = () -> {
        try {
          sceneManager.loadScene(name);
        } catch (IOException e) {
          Log.warn("Could not load scene.\nReason: " + e.getMessage());
        } catch (InterruptedException e) {
          Log.warn("Scene loading was interrupted.");
        }
      };
      notifyAll();
    }
  }

  /**
   * Save the current scene.
   */
  @Override public synchronized void saveScene() {
    if (currentTask != null) {
      Log.warn("Can't save the scene right now.");
    } else {
      currentTask = () -> {
        try {
          sceneManager.saveScene();
        } catch (InterruptedException e) {
          Log.warn("Scene saving was interrupted.");
        }
      };
      notifyAll();
    }
  }

  /**
   * Load chunks and reset camera.
   */
  @Override
  public synchronized void loadFreshChunks(World world, Collection<ChunkPosition> chunks) {
    if (currentTask != null) {
      Log.warn("Can't load chunks right now.");
    } else {
      currentTask = () -> sceneManager.loadFreshChunks(world, chunks);
      notifyAll();
    }
  }

  /**
   * Load chunks without moving the camera.
   */
  @Override public synchronized void loadChunks(World world, Collection<ChunkPosition> chunks) {
    if (currentTask != null) {
      Log.warn("Can't load chunks right now.");
    } else {
      currentTask = () -> sceneManager.loadChunks(world, chunks);
      notifyAll();
    }
  }

  /**
   * Reload all chunks
   */
  @Override public synchronized void reloadChunks() {
    if (currentTask != null) {
      Log.warn("Can't load chunks right now.");
    } else {
      currentTask = sceneManager::reloadChunks;
      notifyAll();
    }
  }

  /**
   * Merge a render dump into the current render.
   */
  public synchronized void mergeRenderDump(File renderDump) {
    if (currentTask != null) {
      Log.warn("Can't merge render dump right now.");
    } else {
      currentTask = () -> sceneManager.mergeDump(renderDump);
      notifyAll();
    }
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
