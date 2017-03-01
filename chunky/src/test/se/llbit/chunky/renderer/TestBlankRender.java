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
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

  class MockSceneProvider implements SceneProvider {
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

  /**
   * Render with a fully black sky.
   */
  @Test public void testBlackRender() throws InterruptedException {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    RenderContext context = new RenderContext(chunky);
    RenderManager renderer = new RenderManager(context, true);
    final Scene scene = new Scene();
    scene.setCanvasSize(WIDTH, HEIGHT);
    scene.setRenderMode(RenderMode.RENDERING);
    scene.setTargetSpp(2);
    scene.setName("foobar");
    scene.sky().setSkyMode(Sky.SkyMode.BLACK);
    renderer.setSceneProvider(new MockSceneProvider(scene));
    renderer.start();
    renderer.join();
    double[] samples = renderer.getBufferedScene().getSampleBuffer();
    for (int i = 0; i < 3 * WIDTH * HEIGHT; ++i) {
      if (samples[i] > Ray.EPSILON) {
        fail("Sampled pixel is outside expected value range.");
      }
    }
  }

  /**
   * Render with a gray sky.
   */
  @Test public void testGrayRender() throws InterruptedException {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    RenderContext context = new RenderContext(chunky);
    RenderManager renderer = new RenderManager(context, true);
    final Scene scene = new Scene();
    scene.setCanvasSize(WIDTH, HEIGHT);
    scene.setRenderMode(RenderMode.RENDERING);
    scene.sky().setSkyMode(Sky.SkyMode.GRADIENT);
    List<Vector4> white = new ArrayList<>();
    white.add(new Vector4(0.5, 0.5, 0.5, 0));
    white.add(new Vector4(0.5, 0.5, 0.5, 1));
    scene.sky().setGradient(white);
    scene.setTargetSpp(2);
    scene.setName("foobar");
    renderer.setSceneProvider(new MockSceneProvider(scene));
    renderer.start();
    renderer.join();
    double[] samples = renderer.getBufferedScene().getSampleBuffer();
    for (int i = 0; i < 3 * WIDTH * HEIGHT; ++i) {
      if (samples[i] < 0.5 - Ray.EPSILON || samples[i] > 0.5 + Ray.EPSILON) {
        fail("Sampled pixel is outside expected value range.");
      }
    }
  }
}
