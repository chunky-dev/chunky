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
import se.llbit.chunky.renderer.ApertureShape;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;

/**
 * A projector for spherical depth of field.
 */
public class SphericalApertureProjector extends ApertureProjector {
  public SphericalApertureProjector(Projector wrapped, double apertureSize,
      double subjectDistance) {
    super(wrapped, apertureSize, subjectDistance);
  }

  public SphericalApertureProjector(Projector wrapped, double apertureSize, double subjectDistance, String apertureMaskFilename) {
    super(wrapped, apertureSize, subjectDistance, apertureMaskFilename);
  }

  public SphericalApertureProjector(Projector wrapped, double apertureSize, double subjectDistance, ApertureShape apertureShape) {
    super(wrapped, apertureSize, subjectDistance, apertureShape);
  }

  @Override public void apply(double x, double y, Random random, Vector3 o, Vector3 d) {
    wrapped.apply(x, y, random, o, d);

    double yaw = FastMath.atan2(d.x, d.z);
    double pitch = FastMath.atan2(d.y, FastMath.sqrt(d.x * d.x + d.z * d.z));

    d.scale(subjectDistance);

    double[] point = getPointInAperture(random);
    double rx = point[0];
    double ry = point[1];

    Vector3 aperturePoint = new Vector3(rx, ry, 0);

    Transform transform = Transform.NONE;
    transform.rotateX(-pitch).rotateY(yaw).apply(aperturePoint);

    d.sub(aperturePoint.x, aperturePoint.y, aperturePoint.z);
    o.add(aperturePoint.x, aperturePoint.y, aperturePoint.z);
  }
}
