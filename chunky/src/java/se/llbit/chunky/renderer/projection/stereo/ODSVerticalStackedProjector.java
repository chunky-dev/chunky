/* Copyright (c) 2022 Chunky contributors
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
package se.llbit.chunky.renderer.projection.stereo;

import se.llbit.math.Vector3;

/**
 * An Omni-Directional Stereo projector implementation which aligns both eye's perspectives vertically stacked.
 * The left eye will be in the upper image half, the right eye in the lower half.
 *
 * <p>The canvas must have an aspect ratio of 1:1 for a full 360° range.
 */
public class ODSVerticalStackedProjector extends OmniDirectionalStereoProjector {
  @Override
  public void apply(double x, double y, Vector3 pos, Vector3 direction) {
    if(y < 0) {
      // -0.5 - 0.0
      applyLeftEye(x*2 + 1, y*2 + 1, pos, direction);
    } else {
      // 0.0 - 0.5
      applyRightEye(x*2 + 1, y*2, pos, direction);
    }
  }
}
