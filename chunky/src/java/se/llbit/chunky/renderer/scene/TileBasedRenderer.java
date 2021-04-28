package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.InternalRenderManager;
import se.llbit.chunky.renderer.RenderWorkerPool;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.math.Ray;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A tile based renderer. Simply call {@code submitTiles} to submit a frame's worth of tiles to the work queue.
 * Call {@code manager.pool.awaitEmpty()} to block until all tiles are finished rendering.
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

  public void submitTiles(InternalRenderManager manager, RayTracer tracer, boolean preview) {
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

    double[] sampleBuffer = manager.bufferedScene.getSampleBuffer();

    double halfWidth = width / (2.0 * height);
    double invHeight = 1.0 / height;

    Camera cam = bufferedScene.camera();
    int spp = bufferedScene.spp;
    double sinv = 1.0 / (spp + 1);

    cachedTiles.forEach(tile ->
      manager.pool.submit(worker -> {
        WorkerState state = new WorkerState();
        state.ray = new Ray();
        state.random = worker.random;

        for (int i = tile.x0; i < tile.x1; i++) {
          for (int j = tile.y0; j < tile.y1; j++) {
            double ox = preview ? 0 : worker.random.nextDouble();
            double oy = preview ? 0 :worker.random.nextDouble();

            cam.calcViewRay(state.ray, state.random,
                -halfWidth + (i + ox) * invHeight,
                -0.5 + (j + oy) * invHeight);
            bufferedScene.rayTrace(tracer, state);

            int offset = 3 * (j*width + i);
            if (preview) {
              sampleBuffer[offset + 0] = state.ray.color.x;
              sampleBuffer[offset + 1] = state.ray.color.y;
              sampleBuffer[offset + 2] = state.ray.color.z;
            } else {
              sampleBuffer[offset + 0] = (sampleBuffer[offset + 0] * spp + state.ray.color.x) * sinv;
              sampleBuffer[offset + 1] = (sampleBuffer[offset + 1] * spp + state.ray.color.y) * sinv;
              sampleBuffer[offset + 2] = (sampleBuffer[offset + 2] * spp + state.ray.color.z) * sinv;
            }
          }
        }
      })
    );
  }
}
