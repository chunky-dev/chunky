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

import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;

import java.io.IOException;
import java.util.Collection;

/**
 * A scene manager can save and load scenes.
 */
public interface SceneManager {
  /**
   * Save the current scene.
   *
   * @throws InterruptedException
   */
  void saveScene() throws InterruptedException;

  /**
   * Load a saved scene.
   */
  void loadScene(String sceneName)
      throws IOException, SceneLoadingError, InterruptedException;

  /**
   * Load chunks and reset camera and scene.
   * The scene name should be set before the call to loadFreshChunks().
   */
  void loadFreshChunks(World world, Collection<ChunkPosition> chunks);

  /**
   * Load chunks without resetting the current scene.
   * This preserves camera position, etc.
   */
  void loadChunks(World world, Collection<ChunkPosition> chunks);

  /**
   * Attempt to reload all loaded chunks.
   */
  void reloadChunks();

  /**
   * This should only be used by the render controls dialog controller.
   * Modifications to the scene must always be protected by the intrinsic
   * lock of the scene object.
   */
  Scene getScene();
}
