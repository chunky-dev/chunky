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

import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

/**
 * A tile based renderer. Simply call {@code submitTiles} to submit a frame's worth of tiles to the work queue.
 * Call {@code manager.pool.awaitEmpty()} to block until all tiles are finished rendering.
 * Call {@code postRender.getAsBoolean()} after each frame (and terminate if it returns {@code true}).
 *
 * Implementation detail: Tiles are cached for faster rendering.
 */
public abstract class TileBasedRenderer implements Renderer {
  protected BooleanSupplier postRender = () -> true;

  private final ArrayList<RenderTile> cachedTiles = new ArrayList<>();
  private int prevWidth = -1;
  private int prevHeight = -1;

  public static class RenderTile {
    public int x0, x1;
    public int y0, y1;

    public RenderTile(int x0, int x1, int y0, int y1) {
      this.x0 = x0;
      this.x1 = x1;
      this.y0 = y0;
      this.y1 = y1;
    }
  }

  @Override
  public void setPostRender(BooleanSupplier callback) {
    postRender = callback;
  }

  /**
   * Create and submit tiles to the rendering pool.
   * Await for these tiles to finish rendering with {@code manager.pool.awaitEmpty()}.
   *
   * @param perPixel This is called on every pixel. The first argument is the worker state.
   *                 The second argument is the current pixel (x, y).
   */
  protected void submitTiles(DefaultRenderManager manager, BiConsumer<WorkerState, IntIntPair> perPixel) {
    initTiles(manager);

    cachedTiles.forEach(tile ->
        manager.pool.submit(worker -> {
          WorkerState state = new WorkerState();
          state.random = worker.random;

          IntIntMutablePair pair = new IntIntMutablePair(0, 0);

          for (int i = tile.x0; i < tile.x1; i++) {
            for (int j = tile.y0; j < tile.y1; j++) {
              pair.left(i).right(j);
              perPixel.accept(state, pair);
              state.ray.reset();
              state.intersectionRecord.reset();
              state.sampleRay.reset();
              state.sampleRecord.reset();
              state.color.set(0);
              state.throughput.set(1);
              state.emittance.set(0);
              state.sampleColor.set(0);
              state.attenuation.set(1);
            }
          }
        })
    );
  }

  private void initTiles(DefaultRenderManager manager) {
    Scene bufferedScene = manager.bufferedScene;
    int width = bufferedScene.width;
    int height = bufferedScene.height;
    int tileWidth = manager.context.tileWidth();

    if (prevWidth != width || prevHeight != height) {
      prevWidth = width;
      prevHeight = height;
      cachedTiles.clear();

      for (int i = 0; i < width; i += tileWidth) {
        for (int j = 0; j < height; j += tileWidth) {
          cachedTiles.add(new RenderTile(i, FastMath.min(i + tileWidth, width),
              j, FastMath.min(j + tileWidth, height)));
        }
      }
    }
  }
}
