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
package se.llbit.chunky.renderer.scene.camera;

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.QuickMath;

public class CameraUtils {
  private CameraUtils() {
  }

  /**
   * @param fov Field of view, in degrees. Maximum 180.
   * @return {@code tan(fov/2)}
   */
  public static double clampedFovTan(double fov) {
    double clampedFoV = Math.max(0, Math.min(180, fov));
    return 2 * FastMath.tan(QuickMath.degToRad(clampedFoV / 2));
  }

  /**
   * Minimum Depth of Field (DoF).
   */
  public static final double MIN_DOF = .05;

  /**
   * Maximum Depth of Field (DoF).
   */
  public static final double MAX_DOF = 5000;

  /**
   * Minimum recommended subject distance.
   */
  public static final double MIN_SUBJECT_DISTANCE = 0.01;

  /**
   * Maximum recommended subject distance.
   */
  public static final double MAX_SUBJECT_DISTANCE = 1000;
}
