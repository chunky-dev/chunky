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
package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.util.Registerable;

import java.util.function.BooleanSupplier;

public interface Renderer extends Registerable {
  /**
   * Get the ID of this renderer.
   */
  @Override
  String getId();

  /**
   * Get the friendly name of this renderer.
   */
  @Override
  String getName();

  /**
   * Get the description of this renderer.
   */
  @Override
  String getDescription();

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

  interface SampleBufferConsumer {
    void accept(double[] samples, int width, int height);
  }

  /**
   * This is called when a render is initiated.
   *
   * * It should render a frame, merge that frame with {@code manager.bufferedScene}, and update the spp values.
   * * It should call the post-render callback (set in {@code setPostRender(callback)}) and terminate if it returns {@code true}.
   */
  void render(DefaultRenderManager manager) throws InterruptedException;

  /**
   * This is called when the scene is reset and this {@code Renderer} is selected as either the preview or render.
   * This is for {@code Renderer}s which need to export the scene data in some way.
   *
   * The default implementation does nothing.
   *
   * @param resetCount This is the reset count. Any reset will increment this variable. Implementations may keep track
   *                   of this count to see if it missed a reset (and should potentially re-export the scene).
   *                   The starting value will be >= 1.
   */
  default void sceneReset(DefaultRenderManager manager, ResetReason reason, int resetCount) {}

  /**
   * This should return if this renderer will postprocess on its own.
   * {@code true}:  This renderer will <bold>NOT</bold> postprocessing on its own. Postprocessing will be handled by the
   *                {@code RenderManager}.
   * {@code false}: This renderer <bold>WILL</bold> postprocessing on its own. The {@code RenderManager} will only force
   *                postprocessing on snapshots.
   */
  default boolean autoPostProcess() { return true; }
}
