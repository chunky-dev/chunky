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

import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * Simulates a non-point aperture to produce a depth-of-field effect.
 * Delegates calculation of base offset/direction to another projector. If
 * apertureSize is 0 this will still work, but it will not have any effect.
 * In that case you should use the wrapped Projector directly.
 */
public class ApertureProjector implements Projector {
  protected final Projector wrapped;
  protected final double aperture;
  protected final double subjectDistance;

  public ApertureProjector(Projector wrapped, double apertureSize, double subjectDistance) {
    this.wrapped = wrapped;
    this.aperture = apertureSize;
    this.subjectDistance = subjectDistance;
  }

  @Override public void apply(double x, double y, Random random, Vector3 o, Vector3 d) {
    wrapped.apply(x, y, random, o, d);

    d.scale(subjectDistance / d.z);

    // find random point in aperture
    double rx, ry;
    while (true) {
      rx = 2 * random.nextDouble() - 1;
      ry = 2 * random.nextDouble() - 1;
      double s = rx * rx + ry * ry;
      if (s > Ray.EPSILON && s <= 1) {
        rx *= aperture;
        ry *= aperture;
        break;
      }
    }

    d.sub(rx, ry, 0);
    o.add(rx, ry, 0);
  }

  @Override public void apply(double x, double y, Vector3 pos, Vector3 direction) {
    wrapped.apply(x, y, pos, direction);
  }

  @Override public double getMinRecommendedFoV() {
    return wrapped.getMinRecommendedFoV();
  }

  @Override public double getMaxRecommendedFoV() {
    return wrapped.getMaxRecommendedFoV();
  }

  @Override public double getDefaultFoV() {
    return wrapped.getDefaultFoV();
  }
}
