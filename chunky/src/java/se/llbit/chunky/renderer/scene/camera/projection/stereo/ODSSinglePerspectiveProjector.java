/* Copyright (c) 2016-2022 Chunky contributors
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
package se.llbit.chunky.renderer.scene.camera.projection.stereo;

import se.llbit.math.Vector3;

/**
 * An Omni-Directional Stereo projector implementation which projects only one of both eyes' perspectives.
 *
 * <p>The canvas must have an aspect ratio of 2:1 for a full 360° range.
 */
public class ODSSinglePerspectiveProjector extends OmniDirectionalStereoProjector {
  private final Eye eye;

  public ODSSinglePerspectiveProjector(Eye eye) {
    this.eye = eye;
  }

  @Override
  public void apply(double x, double y, Vector3 pos, Vector3 direction) {
    switch (eye) {
      case LEFT:
        applyLeftEye(x + 0.5, y + 0.5, pos, direction);
        break;
      case RIGHT:
        applyRightEye(x + 0.5, y + 0.5, pos, direction);
        break;
    }
  }

  public enum Eye {
    LEFT,
    RIGHT
  }
}
