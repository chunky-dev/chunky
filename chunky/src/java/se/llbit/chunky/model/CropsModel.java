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
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CropsModel {
  private static Quad[] quads = {
      new DoubleSidedQuad(new Vector3(1, 0, .25), new Vector3(0, 0, .25),
          new Vector3(1, 1, .25), new Vector4(1, 0, 0, 1)),

      new DoubleSidedQuad(new Vector3(0, 0, .75), new Vector3(1, 0, .75),
          new Vector3(0, 1, .75), new Vector4(0, 1, 0, 1)),

      new DoubleSidedQuad(new Vector3(.25, 0, 0), new Vector3(.25, 0, 1),
          new Vector3(.25, 1, 0), new Vector4(0, 1, 0, 1)),

      new DoubleSidedQuad(new Vector3(.75, 0, 1), new Vector3(.75, 0, 0),
          new Vector3(.75, 1, 1), new Vector4(1, 0, 0, 1)),
  };

  public static boolean intersect(Ray ray, Texture texture) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = texture.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.orientNormal(quad.n);
          hit = true;
        }
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
