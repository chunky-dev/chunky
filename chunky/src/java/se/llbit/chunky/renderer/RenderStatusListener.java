/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.ProgressListener;
import se.llbit.util.TaskTracker;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface RenderStatusListener {
  /** A singleton default render listener. */
  RenderStatusListener NONE = new RenderStatusListener() {
    @Override public void chunksLoaded() {
    }

    @Override public void setRenderTime(long time) {
    }

    @Override public void setSamplesPerSecond(int sps) {
    }

    @Override public void setSpp(int spp) {
    }

    @Override public void sceneSaved() {
    }

    @Override public void sceneLoaded() {
    }

    @Override public void renderStateChanged(RenderMode state) {
    }

    @Override public void renderJobFinished(long time, int sps) {
    }

    @Override public void frameCompleted(Scene scene, int spp) {
    }

    @Override public TaskTracker taskTracker() {
      return new TaskTracker(ProgressListener.NONE);
    }

    @Override public TaskTracker.Task renderTask() {
      return TaskTracker.Task.NONE;
    }
  };

  /**
   * Called when chunks have been loaded.
   */
  void chunksLoaded();

  /**
   * Update render time status label
   *
   * @param time Total render time in milliseconds
   */
  void setRenderTime(long time);

  /**
   * Update samples per second status label
   *
   * @param sps Samples per second
   */
  void setSamplesPerSecond(int sps);

  /**
   * Update SPP status label.
   *
   * @param spp Samples per pixel
   */
  void setSpp(int spp);

  /**
   * Called when the current scene has been saved.
   */
  void sceneSaved();

  /**
   * Method to notify the render controls dialog that a scene has been loaded.
   * Causes canvas size to be updated.
   */
  void sceneLoaded();

  /**
   * Called when the rendering activity has changed state.
   *
   * @param state the new rendering state
   */
  void renderStateChanged(RenderMode state);

  /**
   * Called when the current render job has completed.
   *
   * @param time Total rendering time
   * @param sps  Average SPS
   */
  void renderJobFinished(long time, int sps);

  /**
   * Called when a frame has been completed.
   */
  void frameCompleted(Scene scene, int spp);

  /**
   * Determines if postprocessing should be applied to this frame.
   * Postprocessing is only needed when a snapshot should be saved.
   */
  default boolean saveSnapshot(Scene scene, int nextSpp) {
    return nextSpp >= scene.getTargetSpp()
        || (scene.shouldSaveDumps()
        && scene.shouldSaveSnapshots()
        && (nextSpp % scene.getDumpFrequency() == 0));
  }

  /** Determines if a render dump should be saved after this frame. */
  default boolean saveRenderDump(Scene scene, int nextSpp) {
    return nextSpp >= scene.getTargetSpp()
        || (scene.shouldSaveDumps()
        && (nextSpp % scene.getDumpFrequency() == 0));
  }

  TaskTracker taskTracker();

  TaskTracker.Task renderTask();
}
