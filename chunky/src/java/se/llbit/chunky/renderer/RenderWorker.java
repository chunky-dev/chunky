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
package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;

import java.util.Random;

/**
 * Performs rendering work.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RenderWorker extends Thread {

  /**
   * Sleep interval (in ns).
   */
  private static final int SLEEP_INTERVAL = 75000000;

  protected final int id;
  protected final AbstractRenderManager manager;

  protected final WorkerState state;
  protected final RayTracer previewRayTracer;
  protected final RayTracer rayTracer;

  /**
   * Create a new render worker, slave to a given render manager.
   *
   * @param manager the parent render manager
   * @param id the ID for this worker
   * @param seed the random generator seed
   */
  public RenderWorker(AbstractRenderManager manager, int id, long seed) {
    super("3D Render Worker " + id);

    this.manager = manager;
    this.previewRayTracer = manager.getPreviewRayTracer();
    this.rayTracer = manager.getRayTracer();
    this.id = id;
    state = new WorkerState();
    state.random = new Random(seed);
    state.ray = new Ray();
  }

  @Override
  public void run() {
    long jobTime = 0;
    try {
      while (!isInterrupted()) {
        RenderTile job = manager.getNextJob();
        long jobStart = System.nanoTime();
        work(job);
        jobTime += System.nanoTime() - jobStart;
        manager.jobDone();

        // Sleep to manage CPU utilization.
        if (jobTime > SLEEP_INTERVAL) {
          if (manager.cpuLoad < 100 && manager.getBufferedScene().getMode() != RenderMode.PREVIEW) {
            // sleep = jobTime * (1-utilization) / utilization
            double load = (100.0 - manager.cpuLoad) / manager.cpuLoad;
            sleep((long) (jobTime / 1000000.0 * load));
          }
          jobTime = 0;
        }
      }
    } catch (InterruptedException ignored) {
      // Interrupted.
    } catch (Throwable e) {
      Log.error("Render worker " + id + " crashed with uncaught exception.", e);
    }
  }

  /**
   * Perform the rendering work for a single tile.
   *
   * @param tile describes the tile to be rendered.
   */
  private void work(RenderTile tile) {
    Scene scene = manager.getBufferedScene();

    Random random = state.random;
    Ray ray = state.ray;

    int cx = scene.crop_x;
    int cy = scene.crop_y;

    double halfWidth = scene.renderWidth() / (2.0 * scene.renderHeight());
    double invHeight = 1.0 / scene.renderHeight();

    SampleBuffer samples = scene.getSampleBuffer();
    final Camera cam = scene.camera();

    if (scene.getMode() != RenderMode.PREVIEW) {
      for (int y = tile.y0; y < tile.y1; ++y) {
        int offset = tile.x0 * 3;
        for (int x = tile.x0; x < tile.x1; ++x) {

          double sr = 0;
          double sg = 0;
          double sb = 0;

          for (int i = 0; i < manager.sppPerPass; ++i) {
            double oy = random.nextDouble();
            double ox = random.nextDouble();

            cam.calcViewRay(ray, random,
                (-halfWidth + (cx + x + ox) * invHeight),
                (-.5 + (cy + y + oy) * invHeight));

            scene.rayTrace(rayTracer, state);

            sr += ray.color.x;
            sg += ray.color.y;
            sb += ray.color.z;
          }
          samples.addSamples(x, y, sr, sg, sb, manager.sppPerPass);

          if (scene.shouldFinalizeBuffer()) {
            scene.finalizePixel(x, y);
          }

          offset += 3;
        }
      }

    } else {
      int width = scene.subareaWidth();
      int height = scene.subareaHeight();
      boolean isFullRender = scene.renderWidth()==width && scene.renderHeight()==height;

      // Preview rendering.
      Ray target = new Ray(ray);
      boolean hit = scene.traceTarget(target);
      int tx = (int) QuickMath.floor(target.o.x + target.d.x * Ray.OFFSET);
      int ty = (int) QuickMath.floor(target.o.y + target.d.y * Ray.OFFSET);
      int tz = (int) QuickMath.floor(target.o.z + target.d.z * Ray.OFFSET);

      for (int x = tile.x0; x < tile.x1; ++x)
        for (int y = tile.y0; y < tile.y1; ++y) {

          boolean firstFrame = scene.previewCount > 1;
          if (firstFrame) {
            if (((x + y) % 2) == 0) {
              continue;
            }
          } else {
            if (((x + y) % 2) != 0) {
              scene.finalizePixel(x, y);
              continue;
            }
          }

          // Draw the crosshairs.
          if (isFullRender && (
              (x == width / 2 && y >= height / 2 - 5 && y <= height / 2 + 5) ||
              (y == height / 2 && x >= width / 2 - 5 && x <= width / 2 + 5)
          )) {
            samples.setPixel(x,y,0xFF,0xFF,0xFF);
            scene.finalizePixel(x, y);
            continue;
          }

          cam.calcViewRay(ray, random, (-halfWidth + (double) (x+cx) * invHeight),
              (-.5 + (double) (y+cy) * invHeight));

          scene.rayTrace(previewRayTracer, state);

          // Target highlighting.
          int rx = (int) QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
          int ry = (int) QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
          int rz = (int) QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);
          if (hit && tx == rx && ty == ry && tz == rz) {
            ray.color.x = 1 - ray.color.x;
            ray.color.y = 1 - ray.color.y;
            ray.color.z = 1 - ray.color.z;
            ray.color.w = 1;
          }

          samples.setPixel(x,y,ray.color.x,ray.color.y,ray.color.z);

          scene.finalizePixel(x, y);

          if (firstFrame) {
            if (y % 2 == 0 && x < (width - 1)) {
              // Copy the current pixel to the next one.
              scene.copyPixel(x, y, 1);
            } else if (y % 2 != 0 && x > 0) {
              // Copy the next pixel to this pixel.
              scene.copyPixel(x, y, -1);
            }
          }
        }
    }
  }
}
