/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.scene.camera.projection;

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.scene.camera.CameraUtils;
import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;

/**
 * Behaves like a pinhole camera in the vertical direction, but like a
 * spherical one in the horizontal direction.
 */
public class PanoramicSlotProjector implements Projector {
  protected final double fov;
  protected final double fovTan;

  public PanoramicSlotProjector(double fov) {
    this.fov = fov;
    this.fovTan = CameraUtils.clampedFovTan(fov);
  }

  @Override public void apply(double x, double y, Random random, Vector3 o, Vector3 d) {
    apply(x, y, o, d);
  }

  @Override public void apply(double x, double y, Vector3 o, Vector3 d) {
    double ax = QuickMath.degToRad(x * fov);
    double dz = FastMath.cos(ax);
    double dx = FastMath.sin(ax);
    double dy = fovTan * y;

    o.set(0, 0, 0);
    d.set(dx, dy, dz);
  }

  @Override public double getMinRecommendedFoV() {
    return 1;
  }

  @Override public double getMaxRecommendedFoV() {
    return 90;
  }

  @Override public double getDefaultFoV() {
    return 90;
  }
}
