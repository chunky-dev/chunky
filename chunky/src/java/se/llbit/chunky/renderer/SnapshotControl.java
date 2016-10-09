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

import se.llbit.chunky.renderer.scene.Scene;

/**
 * Created by jesper on 10/8/2016.
 */
public interface SnapshotControl {
  SnapshotControl DEFAULT = new SnapshotControl() { };

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

  default boolean saveRenderDump(Scene scene, int nextSpp) {
    return nextSpp >= scene.getTargetSpp()
        || (scene.shouldSaveDumps()
        && (nextSpp % scene.getDumpFrequency() == 0));
  }
}
