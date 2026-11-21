/* Copyright (c) 2026 Chunky contributors
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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ChunkyOptions;
import se.llbit.chunky.renderer.scene.CanvasConfig;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.sky.Sky;
import se.llbit.math.Vector3;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Integration tests for reaching the target SPP exactly. */
public class TargetSppTest {
  @ParameterizedTest
  @CsvSource({"4, 11, 1", "16, 7, 1", "4, 11, 3", "1, 5, 3", "4, 2, 3",
      "4, 12, 1", "4, 12, 3"})
  public void testExactTargetSpp(int branchCount, int targetSpp, int sppPerPass)
      throws InterruptedException {
    Scene scene = new Scene();
    scene.setCanvasSize(Math.max(10, CanvasConfig.MIN_CANVAS_WIDTH),
        Math.max(10, CanvasConfig.MIN_CANVAS_HEIGHT));
    scene.setRenderMode(RenderMode.RENDERING);
    scene.setBranchCount(branchCount);
    scene.setTargetSpp(targetSpp);
    scene.sky().setSkyMode(Sky.SkyMode.SOLID_COLOR);
    scene.sky().setColor(new Vector3(0.9, 0.8, 1.0));

    ChunkyOptions options = ChunkyOptions.getDefaults();
    options.renderThreads = 1;
    options.sppPerPass = sppPerPass;
    Chunky chunky = new Chunky(options);
    RenderContext context = new RenderContext(chunky);
    context.renderPoolFactory = (threads, seed) -> new RenderWorkerPool(threads, 0);
    DefaultRenderManager renderer = new DefaultRenderManager(context, true);
    renderer.setSceneProvider(new MockSceneProvider(scene));
    renderer.start();
    renderer.join();

    assertEquals(targetSpp, renderer.bufferedScene.spp,
        "Rendering must reach the target SPP exactly.");
  }
}
