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
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class LadderModel {
  protected static Quad[] quads = {
      // West.
      new DoubleSidedQuad(new Vector3(0.95, 0, 0), new Vector3(0.95, 0, 1), new Vector3(0.95, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // East.
      new DoubleSidedQuad(new Vector3(0.05, 0, 1), new Vector3(0.05, 0, 0), new Vector3(0.05, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // North.
      new DoubleSidedQuad(new Vector3(1, 0, 0.95), new Vector3(0, 0, 0.95), new Vector3(1, 1, 0.95),
          new Vector4(1, 0, 0, 1)),

      // South.
      new DoubleSidedQuad(new Vector3(0, 0, 0.05), new Vector3(1, 0, 0.05), new Vector3(0, 1, 0.05),
          new Vector4(0, 1, 0, 1)),
  };

  public static boolean intersect(Ray ray) {
    int facing = ray.getBlockData();
    return intersect(ray, facing);
  }

  public static boolean intersect(Ray ray, int facing) {
    Quad quad = quads[facing % 4];
    ray.t = Double.POSITIVE_INFINITY;
    if (quad.intersect(ray)) {
      float[] color = Texture.ladder.getColor(ray.u, ray.v);
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
