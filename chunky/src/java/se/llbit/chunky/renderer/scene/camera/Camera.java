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

import se.llbit.chunky.renderer.scene.camera.projection.ProjectionPreset;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.util.JsonSerializable;

import java.util.Random;

public interface Camera extends JsonSerializable {
  /**
   * Calculate a ray shooting out of the camera based on normalized image coordinates.
   *
   * @param ray    result ray
   * @param random random number stream
   * @param x      normalized image coordinate [-0.5, 0.5]
   * @param y      normalized image coordinate [-0.5, 0.5]
   */
  void calcViewRay(Ray ray, Random random, double x, double y);

  /**
   * Calculate a ray shooting out of the camera based on normalized image coordinates.
   *
   * @param ray result ray
   * @param x   normalized image coordinate [-0.5, 0.5]
   * @param y   normalized image coordinate [-0.5, 0.5]
   */
  void calcViewRay(Ray ray, double x, double y);

  Vector3 getPosition();

  double getYaw();
  double getPitch();
  double getRoll();

  double getShiftX();
  double getShiftY();

  double getFov();

  double getDof();
  boolean isInfiniteDoF();
  double getSubjectDistance();

  ProjectionPreset getProjectionPreset();
  double getWorldDiagonalSize();

  ApertureShape getApertureShape();
  String getApertureMaskFilename();
}
