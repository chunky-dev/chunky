/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class EndPortalModel {
  private static final Quad quad =
      new DoubleSidedQuad(new Vector3(1, .75, 0), new Vector3(0, .75, 0), new Vector3(1, .75, 1),
          new Vector4(1, 0, 0, 1));

  public static boolean intersect(Ray ray) {
    ray.t = Double.POSITIVE_INFINITY;
    if (quad.intersect(ray)) {
      ray.color.set(0, 0, 0, 1);
      Vector3 n = new Vector3(quad.n);
      n.scale(-QuickMath.signum(ray.d.dot(quad.n)));
      ray.setN(n);
      ray.distance += ray.tNext;
      ray.o.scaleAdd(ray.tNext, ray.d);
      return true;
    }
    return false;
  }
}
