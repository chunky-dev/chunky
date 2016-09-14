/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.renderer.RenderStatusListener;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Scene manager for performing synchronized scene actions on a separate thread.
 * This class is needed to do long-running scene tasks without blocking the GUI thread.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AsynchronousSceneManager extends Thread implements SceneManager {

  /**
   * Which action to perform next.
   */
  public enum Action {
    /**
     * Do nothing.
     */
    NONE,
    /**
     * Load a scene.
     */
    LOAD_SCENE,
    /**
     * Save the scene.
     */
    SAVE_SCENE,
    /**
     * Load chunks and reset camera position.
     */
    LOAD_FRESH_CHUNKS,
    /**
     * Load chunks but do not reset camera.
     */
    LOAD_CHUNKS,
    /**
     * Reload chunks.
     */
    RELOAD_CHUNKS,
    /**
     * Merge render dump.
     */
    MERGE_DUMP
  }

  private String sceneName = "";
  private File renderDump;
  private Action action = Action.NONE;
  private Collection<ChunkPosition> chunksToLoad;
  private World world;

  private final SynchronousSceneManager sceneManager;

  public AsynchronousSceneManager(RenderContext context, Renderer renderer) {
    super("Scene Manager");

    sceneManager = new SynchronousSceneManager(context, renderer);
  }

  public SceneProvider getSceneProvider() {
    return sceneManager;
  }

  public void setRenderStatusListener(RenderStatusListener renderStatusListener) {
    sceneManager.setRenderStatusListener(renderStatusListener);
  }

  public void setResetHandler(RenderResetHandler resetHandler) {
    sceneManager.setResetHandler(resetHandler);
  }

  @Override public Scene getScene() {
    return sceneManager.getScene();
  }

  @Override public void run() {
    try {
      while (!isInterrupted()) {
        synchronized (this) {

          while (action == Action.NONE) {
            wait();
          }

          Action currentAction = action;
          action = Action.NONE;

          switch (currentAction) {
            case LOAD_SCENE:
              try {
                sceneManager.loadScene(sceneName);
              } catch (IOException e) {
                Log.warn("Could not load scene.\nReason: " + e.getMessage());
              } catch (SceneLoadingError e) {
                Log.warn("Could not open scene description.\nReason: " + e.getMessage());
              } catch (InterruptedException e) {
                Log.warn("Scene loading was interrupted.");
              }
              break;
            case SAVE_SCENE:
              try {
                sceneManager.saveScene();
              } catch (InterruptedException e1) {
                Log.warn("Scene saving was interrupted.");
              }
              break;
            case LOAD_FRESH_CHUNKS:
              sceneManager.loadFreshChunks(world, chunksToLoad);
              break;
            case LOAD_CHUNKS:
              sceneManager.loadChunks(world, chunksToLoad);
              break;
            case RELOAD_CHUNKS:
              sceneManager.reloadChunks();
              break;
            case MERGE_DUMP:
              sceneManager.mergeDump(renderDump);
              break;
            default:
              break;
          }
        }
      }
    } catch (InterruptedException e) {
      // Interrupted.
    }
  }

  /**
   * Load the given scene.
   *
   * @param name the name of the scene to load.
   */
  public synchronized void loadScene(String name) {
    sceneName = name;
    action = Action.LOAD_SCENE;
    notify();
  }

  /**
   * Save the current scene.
   */
  public synchronized void saveScene() {
    action = Action.SAVE_SCENE;
    notify();
  }

  /**
   * Load chunks and reset camera.
   */
  @Override public synchronized void loadFreshChunks(World world, Collection<ChunkPosition> chunks) {
    chunksToLoad = chunks;
    this.world = world;
    action = Action.LOAD_FRESH_CHUNKS;
    notify();
  }

  /**
   * Load chunks without moving the camera.
   */
  @Override public synchronized void loadChunks(World world, Collection<ChunkPosition> chunks) {
    chunksToLoad = chunks;
    this.world = world;
    action = Action.LOAD_CHUNKS;
    notify();
  }

  /**
   * Reload all chunks
   */
  @Override public synchronized void reloadChunks() {
    action = Action.RELOAD_CHUNKS;
    notify();
  }

  /**
   * Merge a render dump into the current render.
   */
  public synchronized void mergeRenderDump(File renderDump) {
    this.renderDump = renderDump;
    action = Action.MERGE_DUMP;
    notify();
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
    if (c > '\u007e' && c < '\u00a0') {
      return false;
    }
    return true;
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
