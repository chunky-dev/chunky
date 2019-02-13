/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.log.Log;

/**
 * Tests for thread liveness issues in the renderer.
 */
public class TestDeadlock {
  private static final int WIDTH = Math.max(10, Scene.MIN_CANVAS_WIDTH);
  private static final int HEIGHT = Math.max(10, Scene.MIN_CANVAS_HEIGHT);

  static class PointlessWorker extends RenderWorker {
    PointlessWorker(AbstractRenderManager manager, int id) {
      super(manager, id, 0);
    }

    @Override public void run() {
      try {
        while (!isInterrupted()) {
          manager.getNextJob();
          manager.jobDone();
        }
      } catch (InterruptedException ignored) {
        // Interrupted.
      } catch (Throwable e) {
        Log.error("Render worker " + id + " crashed with uncaught exception.", e);
      }
    }
  }

  /**
   * Try to cause deadlock at start of scene rendering by starting
   * many render jobs.
   */
  private static void repeatRender(Scene scene) throws InterruptedException {
    ChunkyOptions options = ChunkyOptions.getDefaults();
    options.renderThreads = 1;
    options.tileWidth = Math.max(WIDTH, HEIGHT);
    Chunky chunky = new Chunky(options);
    RenderContext context = new RenderContext(chunky);
    context.workerFactory =
        (renderManager, id, seed) -> new PointlessWorker(renderManager, id);
    for (int i = 0; i < 2019; ++i) {
      RenderManager renderer = new RenderManager(context, true);
      renderer.setSceneProvider(new MockSceneProvider(scene));
      renderer.start();
      renderer.join();
    }
  }

  /**
   * Test for race condition leading to deadlock in render worker/render manager.
   * See issue 507: https://github.com/llbit/chunky/issues/507
   */
  @Test(timeout=60000) public void testDeadlock() throws InterruptedException {
    final Scene scene = new Scene();
    scene.setCanvasSize(WIDTH, HEIGHT);
    scene.setRenderMode(RenderMode.RENDERING);
    scene.setTargetSpp(1);
    scene.setName("deadlock");
    repeatRender(scene);
  }
}
