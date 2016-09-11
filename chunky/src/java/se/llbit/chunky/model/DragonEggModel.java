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
import se.llbit.math.Ray;

public class DragonEggModel {
  private static final AABB[] egg =
      new AABB[] {new AABB(5 / 16., 11 / 16., 0, 1 / 16., 5 / 16., 11 / 16.),
          new AABB(2 / 16., 14 / 16., 1 / 16., 3 / 16., 2 / 16., 14 / 16.),
          new AABB(1 / 16., 15 / 16., 3 / 16., 8 / 16., 1 / 16., 15 / 16.),
          new AABB(2 / 16., 14 / 16., 8 / 16., 11 / 16., 2 / 16., 14 / 16.),
          new AABB(3 / 16., 13 / 16., 11 / 16., 13 / 16., 3 / 16., 13 / 16.),
          new AABB(4 / 16., 12 / 16., 13 / 16., 14 / 16., 4 / 16., 12 / 16.),
          new AABB(5 / 16., 11 / 16., 14 / 16., 15 / 16., 5 / 16., 11 / 16.),
          new AABB(6 / 16., 10 / 16., 15 / 16., 16 / 16., 6 / 16., 10 / 16.),};

  public static boolean intersect(Ray ray) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (AABB eggPart : egg) {
      if (eggPart.intersect(ray)) {
        Texture.dragonEgg.getColor(ray);
        ray.t = ray.tNext;
        hit = true;
      }
    }
    if (hit) {
      ray.color.w = 1;
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

}
