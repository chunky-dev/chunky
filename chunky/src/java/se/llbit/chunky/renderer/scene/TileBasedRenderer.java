package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.InternalRenderManager;
import se.llbit.chunky.renderer.Renderer;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.math.Ray;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

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

  public int submitRenderTiles(InternalRenderManager manager, RayTracer tracer) {
    Scene bufferedScene = manager.bufferedScene;
    int width = bufferedScene.width;
    int height = bufferedScene.height;

    initTiles(manager);

    double[] sampleBuffer = manager.bufferedScene.getSampleBuffer();

    double halfWidth = width / (2.0 * height);
    double invHeight = 1.0 / height;

    Camera cam = bufferedScene.camera();
    int spp = bufferedScene.spp;
    int sppPerPass = manager.context.sppPerPass();
    double passinv = 1.0 / sppPerPass;
    double sinv = 1.0 / (sppPerPass + 1);

    cachedTiles.forEach(tile ->
      manager.pool.submit(worker -> {
        WorkerState state = new WorkerState();
        state.ray = new Ray();
        state.random = worker.random;

        for (int i = tile.x0; i < tile.x1; i++) {
          for (int j = tile.y0; j < tile.y1; j++) {
            double sr = 0;
            double sg = 0;
            double sb = 0;

            for (int k = 0; k < sppPerPass; k++) {
              double ox = worker.random.nextDouble();
              double oy = worker.random.nextDouble();

              cam.calcViewRay(state.ray, state.random,
                  -halfWidth + (i + ox) * invHeight,
                  -0.5 + (j + oy) * invHeight);
              bufferedScene.rayTrace(tracer, state);

              sr += state.ray.color.x;
              sg += state.ray.color.y;
              sb += state.ray.color.z;
            }

            int offset = 3 * (j*width + i);
            sampleBuffer[offset + 0] = (sampleBuffer[offset + 0] * spp + (sr * passinv)) * sinv;
            sampleBuffer[offset + 1] = (sampleBuffer[offset + 1] * spp + (sg * passinv)) * sinv;
            sampleBuffer[offset + 2] = (sampleBuffer[offset + 2] * spp + (sb * passinv)) * sinv;
          }
        }
      })
    );

    return sppPerPass;
  }

  public void submitPreviewTiles(InternalRenderManager manager, RayTracer tracer, int sampleNum) {
    Scene bufferedScene = manager.bufferedScene;
    int width = bufferedScene.width;
    int height = bufferedScene.height;

    initTiles(manager);

    double[] sampleBuffer = manager.bufferedScene.getSampleBuffer();

    double halfWidth = width / (2.0 * height);
    double invHeight = 1.0 / height;

    Ray target = new Ray();
    boolean hit = bufferedScene.traceTarget(target);
    int tx = (int) Math.floor(target.o.x + target.d.x * Ray.OFFSET);
    int ty = (int) Math.floor(target.o.y + target.d.y * Ray.OFFSET);
    int tz = (int) Math.floor(target.o.z + target.d.z * Ray.OFFSET);

    Camera cam = bufferedScene.camera();

    cachedTiles.forEach(tile ->
        manager.pool.submit(worker -> {
          WorkerState state = new WorkerState();
          state.ray = new Ray();
          state.random = worker.random;

          Ray ray = state.ray;

          for (int i = tile.x0; i < tile.x1; i++) {
            for (int j = tile.y0; j < tile.y1; j++) {
              int offset = 3 * (j*width + i);

              // Interlacing
              if (((i + j) % 2) == sampleNum) continue;

              // Draw crosshairs
              if (i == width / 2 && (j >= height / 2 - 5 && j <= height / 2 + 5) || j == height / 2 && (
                  i >= width / 2 - 5 && i <= width / 2 + 5)) {
                sampleBuffer[offset + 0] = 0xFF;
                sampleBuffer[offset + 1] = 0xFF;
                sampleBuffer[offset + 2] = 0xFF;
                continue;
              }

              cam.calcViewRay(ray, state.random,
                  -halfWidth + i * invHeight,
                  -0.5 + j * invHeight);
              bufferedScene.rayTrace(tracer, state);

              // Target highlighting.
              int rx = (int) Math.floor(ray.o.x + ray.d.x * Ray.OFFSET);
              int ry = (int) Math.floor(ray.o.y + ray.d.y * Ray.OFFSET);
              int rz = (int) Math.floor(ray.o.z + ray.d.z * Ray.OFFSET);
              if (hit && tx == rx && ty == ry && tz == rz) {
                ray.color.x = 1 - ray.color.x;
                ray.color.y = 1 - ray.color.y;
                ray.color.z = 1 - ray.color.z;
                ray.color.w = 1;
              }

              sampleBuffer[offset + 0] = ray.color.x;
              sampleBuffer[offset + 1] = ray.color.y;
              sampleBuffer[offset + 2] = ray.color.z;

              if (sampleNum == 0 && i < (width - 1)) {
                sampleBuffer[offset + 3] = ray.color.x;
                sampleBuffer[offset + 4] = ray.color.y;
                sampleBuffer[offset + 5] = ray.color.z;
              }
            }
          }
        })
    );
  }

  protected void initTiles(InternalRenderManager manager) {
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
