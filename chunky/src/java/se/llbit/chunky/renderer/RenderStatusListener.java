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

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface RenderStatusListener {
  RenderStatusListener NONE = new RenderStatusListener() {
    @Override public void setRenderTime(long time) {
    }

    @Override public void setSamplesPerSecond(int sps) {
    }

    @Override public void setSpp(int spp) {
    }

    @Override public void renderStateChanged(RenderMode state) {
    }
  };

  /**
   * Update render time status label.
   *
   * @param time Total render time in milliseconds
   */
  void setRenderTime(long time);

  /**
   * Update samples per second status label.
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
  //void sceneSaved();

  /**
   * Method to notify the render controls dialog that a scene has been loaded.
   * Causes canvas size to be updated.
   */
  //void sceneLoaded();

  /**
   * Called when the rendering activity has changed state.
   *
   * @param state the new rendering state
   */
  void renderStateChanged(RenderMode state);

  /** Determines if a render dump should be saved after this frame. */
  /*default boolean saveRenderDump(Scene scene, int nextSpp) {
    return nextSpp >= scene.getTargetSpp()
        || (scene.shouldSaveDumps()
        && (nextSpp % scene.getDumpFrequency() == 0));
  }*/

  //TaskTracker taskTracker();

  //TaskTracker.Task renderTask();
}
