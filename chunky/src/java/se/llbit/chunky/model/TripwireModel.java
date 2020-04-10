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

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TripwireModel {
  private static final Quad[] wire = {
      // north-south
      new Quad(new Vector3(8.25 / 16, 1 / 16., 1), new Vector3(8.25 / 16, 1 / 16., 0),
          new Vector3(7.75 / 16, 1 / 16., 1), new Vector4(0, 1, 14 / 16., 1)),

      // east-west
      new Quad(new Vector3(1, 1 / 16., 7.75 / 16), new Vector3(0, 1 / 16., 7.75 / 16),
          new Vector3(1, 1 / 16., 8.25 / 16), new Vector4(0, 1, 14 / 16., 1)),
  };

  public static boolean intersection(Ray ray) {
    int direction = (ray.getCurrentData() >> 12) & 1;
    return intersection(ray, direction);
  }

  public static boolean intersection(Ray ray, int direction) {
    ray.t = Double.POSITIVE_INFINITY;
    Quad quad = wire[direction];
    if (quad.intersect(ray)) {
      ray.u *= 4;
      ray.u -= (int) ray.u;
      float[] color = Texture.tripwire.getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        ray.n.set(quad.n);
        ray.distance += ray.tNext;
        ray.o.scaleAdd(ray.tNext, ray.d);
        return true;
      }
    }
    return false;
  }

}

