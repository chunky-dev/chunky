/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.scene.canvas.GraphicsContext;

/**
 * A renderer renders to a buffered image which is displayed by a render canvas.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface Renderer {
  void setSceneProvider(SceneProvider sceneProvider);

  void setCanvas(Repaintable canvas);

  /**
   * Instructs the renderer to change its CPU load.
   */
  void setCPULoad(int loadPercent);

  /**
   * Instructs the renderer to use the specified number of worker threads.
   */
  void setNumThreads(int numThreads);

  void setRenderListener(RenderStatusListener renderStatusListener);

  interface SampleBufferConsumer {
    void accept(double[] samples, int width, int height);
  }

  /**
   * Draws the buffered image to the given graphics context in the specified location.
   */
  void drawBufferedImage(GraphicsContext gc, double offsetX, double offsetY, double width,
      double height);

  void addSceneStatusListener(SceneStatusListener listener);

  void removeSceneStatusListener(SceneStatusListener listener);

  RenderStatus getRenderStatus();

  /**
   * Start up the renderer.
   *
   * <p>This should start all worker threads used by the renderer.
   */
  void start();

  void withSampleBufferProtected(SampleBufferConsumer consumer);

  /**
   * Shut down the renderer.
   *
   * <p>This should interrupt all worker threads used by the renderer.
   */
  void shutdown();
}
