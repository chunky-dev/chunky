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
import se.llbit.chunky.renderer.scene.PathTracer;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;

public class SpectralPathTracingRenderer extends TileBasedRenderer {
  protected final String id;
  protected final String name;
  protected final String description;
  protected RayTracer tracer;

  public SpectralPathTracingRenderer(String id, String name, String description, RayTracer tracer) {
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
    Scene scene = manager.bufferedScene;

    int width = scene.width;
    int height = scene.height;

    int fullWidth = scene.getFullWidth();
    int fullHeight = scene.getFullHeight();
    int cropX = scene.getCropX();
    int cropY = scene.getCropY();

    int sppPerPass = manager.context.sppPerPass();

    Camera cam = scene.camera();
    double halfWidth = fullWidth / (2.0 * fullHeight);
    double invHeight = 1.0 / fullHeight;

    double[] sampleBuffer = scene.getSampleBuffer();

    while (scene.spp < scene.getTargetSpp()) {
      int spp = scene.spp;
      int branchCount = (tracer instanceof PathTracer) ? scene.getCurrentBranchCount() : 1;
      double sinv = 1.0 / (sppPerPass * branchCount + spp);

      submitTiles(manager, (state, pixel) -> {
        int x = pixel.firstInt();
        int y = pixel.secondInt();

        double sr = 0;
        double sg = 0;
        double sb = 0;

        for (int k = 0; k < sppPerPass; k++) {
          double ox = state.random.nextDouble();
          double oy = state.random.nextDouble();

          cam.calcViewRay(state.ray, state.random,
              -halfWidth + (x + ox + cropX) * invHeight,
              -0.5 + (y + oy + cropY) * invHeight);
          scene.rayTrace(tracer, state);

          sr += state.color.x * branchCount;
          sg += state.color.y * branchCount;
          sb += state.color.z * branchCount;
        }

        int offset = 3 * (y*width + x);
        sampleBuffer[offset]     = (sampleBuffer[offset]     * spp + sr) * sinv;
        sampleBuffer[offset + 1] = (sampleBuffer[offset + 1] * spp + sg) * sinv;
        sampleBuffer[offset + 2] = (sampleBuffer[offset + 2] * spp + sb) * sinv;
      });

      manager.pool.awaitEmpty();
      scene.spp += sppPerPass * branchCount;
      if (postRender.getAsBoolean()) break;
    }
  }
}
