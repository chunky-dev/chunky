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
package se.llbit.chunky.renderer.projection;

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.math.QuickMath;
import se.llbit.math.Vector3;

public class FisheyeProjector implements Projector {
  protected final double fov;

  public FisheyeProjector(double fov) {
    this.fov = fov;
  }

  @Override public void apply(double x, double y, Random random, Vector3 o, Vector3 d) {
    apply(x, y, o, d);
  }

  @Override public void apply(double x, double y, Vector3 o, Vector3 d) {
    double ay = QuickMath.degToRad(y * fov);
    double ax = QuickMath.degToRad(x * fov);
    double avSquared = ay * ay + ax * ax;
    double angleFromCenter = FastMath.sqrt(avSquared);
    double dz = FastMath.cos(angleFromCenter);
    double dv = FastMath.sin(angleFromCenter);
    double dy, dx;
    if (angleFromCenter == 0) {
      dx = dy = 0;
    } else {
      dx = dv * (ax / angleFromCenter);
      dy = dv * (ay / angleFromCenter);
    }
    o.set(0, 0, 0);
    d.set(dx, dy, dz);
  }

  @Override public double getMinRecommendedFoV() {
    return 1;
  }

  @Override public double getMaxRecommendedFoV() {
    return 180;
  }

  @Override public double getDefaultFoV() {
    return 120;
  }
}
