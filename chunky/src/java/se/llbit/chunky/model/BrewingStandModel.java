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
import se.llbit.math.AABB;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BrewingStandModel {
  private static final AABB[] boxes = {
      new AABB(7 / 16., 9 / 16., 0, 14 / 16., 7 / 16., 9 / 16.),
      new AABB(9 / 16., 15 / 16., 0, 2 / 16., 5 / 16., 11 / 16.),
      new AABB(2 / 16., 8 / 16., 0, 2 / 16., 9 / 16., 15 / 16.),
      new AABB(2 / 16., 8 / 16., 0, 2 / 16., 1 / 16., 7 / 16.),
  };

  private static final Texture[] tex = {
      Texture.brewingStandSide, Texture.brewingStandBase,
      Texture.brewingStandBase, Texture.brewingStandBase,
  };

  private static final Quad[] quads = new Quad[] {
      // east
      new DoubleSidedQuad(new Vector3(9 / 16., 0, .5), new Vector3(1, 0, .5),
          new Vector3(.5, 1, .5), new Vector4(9 / 16., 1, 0, 1)),

      // southwest 210
      new DoubleSidedQuad(new Vector3(.46, 0, 9 / 16.), new Vector3(.25, 0, .933),
          new Vector3(.46, 1, 9 / 16.), new Vector4(9 / 16., 1, 0, 1)),

      // northwest 330
      new DoubleSidedQuad(new Vector3(.46, 0, 7 / 16.), new Vector3(.25, 0, .067),
          new Vector3(.46, 1, 7 / 16.), new Vector4(9 / 16., 1, 0, 1)),
  };

  public static boolean intersect(Ray ray) {
    int data = ray.getBlockData();
    return intersect(ray, data);
  }

  public static boolean intersect(Ray ray, int data) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < boxes.length; ++i) {
      if (boxes[i].intersect(ray)) {
        ray.t = ray.tNext;
        tex[i].getColor(ray);
        ray.color.w = 1;
        hit = true;
      }
    }
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        int bottle = (data >> i) & 1;
        float[] color = Texture.brewingStandSide.getColor(bottle + (1 - 2 * bottle) * ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          ray.n.scale(QuickMath.signum(-ray.d.dot(quad.n)));
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
