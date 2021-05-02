/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer;

import java.util.function.BooleanSupplier;

public interface Renderer {
  /**
   * Get the short id string representing this renderer.
   */
  String getIdString();

  /**
   * Get the name string of this renderer.
   */
  String getNameString();

  /**
   * The post render callback. This should be run after rendering a frame.
   * It will return {@code true} if the render should terminate.
   *
   * Generally the render loop will look like:
   * {@code
   *   while (scene.spp < scene.getTargetSpp()) {
   *     submitTiles(manager, (state, pixel) -> {});
   *     manager.pool.awaitEmpty();
   *     scene.spp += 1; // update spp
   *     if (postRender.getAsBoolean()) break;
   *   }
   * }
   *
   * Implementation details, this deals with:
   *  * Checking if the render mode has changed
   *  * Updating the task-tracker
   *  * Repainting the canvas
   *  * Updating the {@code bufferedScene}
   */
  void setPostRender(BooleanSupplier callback);

  /**
   * This is called when a render is initiated.
   *
   * * It should render a frame, merge that frame with {@code manager.bufferedScene}, and update the spp values.
   * * It should call the post-render callback (set in {@code setPostRender(callback)}) and terminate if it returns {@code true}.
   */
  void render(DefaultRenderManager manager) throws InterruptedException;

  /**
   * This is called when the scene is reset. The default implementation does nothing.
   * This is for {@code Renderer}s which need to export the scene data in some way.
   */
  default void sceneReset(DefaultRenderManager manager, ResetReason reason) {}
}
