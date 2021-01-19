/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
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

import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.util.TaskTracker;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
   * Set a listener for render completion.
   *
   * @param listener a listener which is passed the total rendering
   * time and average samples per second.
   */
  void setOnRenderCompleted(BiConsumer<Long, Integer> listener);

  /**
   * Set a listener for frame completion.
   *
   * @param listener a listener which is called when a frame completes
   * with the current scene and the current samples per pixel.
   */
  void setOnFrameCompleted(BiConsumer<Scene, Integer> listener);

  void setSnapshotControl(SnapshotControl callback);

  void setRenderTask(TaskTracker.Task task);

  void addRenderListener(RenderStatusListener listener);

  void removeRenderListener(RenderStatusListener listener);

  void withBufferedImage(Consumer<BitmapImage> bitmap);

  interface SampleBufferConsumer {
    void accept(SampleBuffer samples, int width, int height);
  }

  void addSceneStatusListener(SceneStatusListener listener);

  void removeSceneStatusListener(SceneStatusListener listener);

  RenderStatus getRenderStatus();

  /**
   * Start up the renderer.
   *
   * <p>This should start all worker threads used by the renderer.
   */
  void start();

  /**
   * Wait for the renderer to terminate. This should only be done
   * in headless rendering, otherwise the renderer will not automatically
   * shut down after the render completes.
   * @throws InterruptedException
   */
  void join() throws InterruptedException;

  void withSampleBufferProtected(SampleBufferConsumer consumer);

  /**
   * Shut down the renderer.
   *
   * <p>This should interrupt all worker threads used by the renderer.
   */
  void shutdown();
}
