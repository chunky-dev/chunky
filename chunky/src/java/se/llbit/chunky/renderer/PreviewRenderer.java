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

import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.util.TaskTracker;

public class PreviewRenderer extends TileBasedRenderer {
  protected final String id;
  protected final String name;
  protected final String description;
  protected RayTracer tracer;

  public PreviewRenderer(String id, String name, String description, RayTracer tracer) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.tracer = tracer;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void render(DefaultRenderManager manager) throws InterruptedException {
    TaskTracker.Task task = manager.getRenderTask();
    task.update("Displaying preview", 2, 0, "");

    Scene scene = manager.bufferedScene;

    int width = scene.width;
    int height = scene.height;

    int fullWidth = scene.getFullWidth();
    int fullHeight = scene.getFullHeight();
    int cropX = scene.getCropX();
    int cropY = scene.getCropY();

    Camera cam = scene.camera();
    double halfWidth = fullWidth / (2.0 * fullHeight);
    double invHeight = 1.0 / fullHeight;

    Ray target = new Ray();
    boolean hit = scene.traceTarget(target);
    int tx = (int) Math.floor(target.o.x + target.d.x * Ray.OFFSET);
    int ty = (int) Math.floor(target.o.y + target.d.y * Ray.OFFSET);
    int tz = (int) Math.floor(target.o.z + target.d.z * Ray.OFFSET);

    double[] sampleBuffer = scene.getSampleBuffer();

    for (int i = 0; i < 2; i++) {
      int sampleNum = i;

      submitTiles(manager, (state, pixel) -> {
        int sx = pixel.firstInt();
        int sy = pixel.secondInt();
        int x = sx + cropX;
        int y = sy + cropY;

        int offset = 3 * (sy*width + sx);

        // Interlacing
        if (((sx + sy) % 2) == sampleNum) return;

        // Draw crosshairs
        if (x == fullWidth / 2 && (y >= fullHeight / 2 - 5 && y <= fullHeight / 2 + 5) || y == fullHeight / 2 && (
            x >= fullWidth / 2 - 5 && x <= fullWidth / 2 + 5)) {
          sampleBuffer[offset + 0] = 0xFF;
          sampleBuffer[offset + 1] = 0xFF;
          sampleBuffer[offset + 2] = 0xFF;
          return;
        }

        cam.calcViewRay(state.ray, state.random,
            -halfWidth + x * invHeight,
            -0.5 + y * invHeight);
        scene.rayTrace(tracer, state);

        // Target highlighting.
        int rx = (int) Math.floor(state.ray.o.x + state.ray.d.x * Ray.OFFSET);
        int ry = (int) Math.floor(state.ray.o.y + state.ray.d.y * Ray.OFFSET);
        int rz = (int) Math.floor(state.ray.o.z + state.ray.d.z * Ray.OFFSET);
        if (hit && tx == rx && ty == ry && tz == rz) {
          state.ray.color.x = 1 - state.ray.color.x;
          state.ray.color.y = 1 - state.ray.color.y;
          state.ray.color.z = 1 - state.ray.color.z;
          state.ray.color.w = 1;
        }

        sampleBuffer[offset + 0] = state.ray.color.x;
        sampleBuffer[offset + 1] = state.ray.color.y;
        sampleBuffer[offset + 2] = state.ray.color.z;

        if (sampleNum == 0 && x < (width - 1)) {
          sampleBuffer[offset + 3] = state.ray.color.x;
          sampleBuffer[offset + 4] = state.ray.color.y;
          sampleBuffer[offset + 5] = state.ray.color.z;
        }
      });

      manager.pool.awaitEmpty();
      task.update(i+1);
      if (postRender.getAsBoolean()) break;
    }
  }
}
