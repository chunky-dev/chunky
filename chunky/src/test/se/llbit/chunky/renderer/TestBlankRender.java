/* Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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

import org.junit.Test;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ChunkyOptions;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.json.JsonObject;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Simple integration tests to verify that rendering
 * a blank scene works as it should.
 * The tests render using a small canvas size and with
 * only two samples per pixel.
 */
public class TestBlankRender {
  private static final int WIDTH = Math.max(10, Scene.MIN_CANVAS_WIDTH);
  private static final int HEIGHT = Math.max(10, Scene.MIN_CANVAS_HEIGHT);

  static class MockSceneProvider implements SceneProvider {
    private final Scene scene;
    private boolean change = true;

    public MockSceneProvider(Scene scene) {
      this.scene = scene;
    }

    @Override
    public synchronized ResetReason awaitSceneStateChange() throws InterruptedException {
      while (!change) {
        wait();
      }
      change = false;
      return ResetReason.SCENE_LOADED;
    }

    @Override public synchronized boolean pollSceneStateChange() {
      return change;
    }

    @Override public synchronized void withSceneProtected(Consumer<Scene> fun) {
      fun.accept(scene);
    }

    @Override public synchronized void withEditSceneProtected(Consumer<Scene> fun) {
      // Won't be edited by the scene manager.
    }
  }

  private static void renderAndCheckSamples(Scene scene, double[] expected)
      throws InterruptedException {
    double[] samples = render(scene);
    int offset = 0;
    for (int i = 0; i < WIDTH * HEIGHT; ++i) {
      // Check each channel value:
      for (int cc = 0; cc < 3; ++cc) {
        if (samples[offset + cc] < expected[cc] - 0.005
            || samples[offset + cc] > expected[cc] + 0.005) {
          assertEquals("Sampled pixel is outside expected value range.",
              expected[cc], samples[offset + cc], 0.005);
          fail("Sampled pixel is outside expected value range.");
        }
      }
      offset += 3;
    }
  }

  /** Renders a scene and returns the resulting sample buffer. */
  private static double[] render(Scene scene) throws InterruptedException {
    // A single worker thread is used, with fixed PRNG seed.
    // This makes the path tracing results deterministic.
    ChunkyOptions options = ChunkyOptions.getDefaults();
    options.renderThreads = 1;
    Chunky chunky = new Chunky(options);
    RenderContext context = new RenderContext(chunky);
    context.workerFactory =
        (renderManager, index, seed) -> new RenderWorker(renderManager, index, 0);
    RenderManager renderer = new RenderManager(context, true);
    renderer.setSceneProvider(new MockSceneProvider(scene));
    renderer.start();
    renderer.join();
    return renderer.getBufferedScene().getSampleBuffer();
  }

  /** Compares two sample buffers. */
  private static void compareSamples(double[] expected, double[] actual, int size, double delta)
      throws InterruptedException {
    for (int i = 0; i < size; ++i) {
      if (actual[i] < expected[i] - delta || actual[i] > expected[i] + delta) {
        assertEquals("Sampled pixel is outside expected value range.",
            expected[i], actual[i], delta);
        fail("Sampled pixel is outside expected value range.");
      }
    }
  }

  /**
   * Render with a fully black sky.
   */
  @Test public void testBlackRender() throws InterruptedException {
    final Scene scene = new Scene();
    scene.setCanvasSize(WIDTH, HEIGHT);
    scene.setRenderMode(RenderMode.RENDERING);
    scene.setTargetSpp(2);
    scene.setName("foobar");
    scene.sky().setSkyMode(Sky.SkyMode.BLACK);
    renderAndCheckSamples(scene, new double[] {0, 0, 0});
  }

  /**
   * Render with a gray sky.
   */
  @Test public void testGrayRender() throws InterruptedException {
    final Scene scene = new Scene();
    scene.setCanvasSize(WIDTH, HEIGHT);
    scene.setRenderMode(RenderMode.RENDERING);
    scene.sky().setSkyMode(Sky.SkyMode.GRADIENT);
    List<Vector4> white = new ArrayList<>();
    white.add(new Vector4(0.5, 0.5, 0.5, 0));
    white.add(new Vector4(0.5, 0.5, 0.5, 1));
    scene.sky().setGradient(white);
    scene.setTargetSpp(2);
    scene.setName("gray");
    renderAndCheckSamples(scene, new double[] {0.5, 0.5, 0.5});
  }

  /**
   * Test that render output is correct after JSON export/import.
   */
  @Test public void testJsonRoundTrip1() throws InterruptedException {
    final Scene scene = new Scene();
    scene.setCanvasSize(WIDTH, HEIGHT);
    scene.setRenderMode(RenderMode.RENDERING);
    scene.sky().setSkyMode(Sky.SkyMode.GRADIENT);
    List<Vector4> white = new ArrayList<>();
    white.add(new Vector4(0.5, 1, 0.25, 0));
    white.add(new Vector4(0.5, 1, 0.25, 1));
    scene.sky().setGradient(white);
    scene.setTargetSpp(2);
    scene.setName("json1");
    JsonObject json = scene.toJson();
    scene.fromJson(json);
    scene.setRenderMode(RenderMode.RENDERING); // Un-pause after JSON import.
    renderAndCheckSamples(scene, new double[] {0.5, 1, 0.25});
  }

  /**
   * Test that render output is correct after JSON export/import.
   */
  @Test public void testJsonRoundTrip2() throws InterruptedException {
    final Scene scene = new Scene();
    scene.setTargetSpp(2);
    scene.setName("json2");
    scene.setCanvasSize(WIDTH, HEIGHT);
    scene.setRenderMode(RenderMode.RENDERING);
    scene.sky().setSkyMode(Sky.SkyMode.SIMULATED);
    scene.camera().setProjectionMode(ProjectionMode.PANORAMIC);
    scene.camera().setFoV(100);

    int size = 3 * WIDTH * HEIGHT;
    double[] samples1 = new double[size];
    System.arraycopy(render(scene), 0, samples1, 0, size);

    JsonObject json = scene.toJson();
    scene.fromJson(json);
    scene.setRenderMode(RenderMode.RENDERING); // Un-pause after JSON import.

    compareSamples(samples1, render(scene), size, 0.005);
  }
}
