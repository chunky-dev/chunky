/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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

public class ChorusFlowerModel {
  private static AABB[] boxes = {new AABB(0, 1, 2 / 16., 14 / 16., 2 / 16., 14 / 16.),
      new AABB(2 / 16., 14 / 16., 0, 1, 2 / 16., 14 / 16.),
      new AABB(2 / 16., 14 / 16., 2 / 16., 14 / 16., 0, 1),};

  public static boolean intersect(Ray ray) {
    boolean hit = false;
    Texture texture = ray.getBlockData() < 5 ? Texture.chorusFlower : Texture.chorusFlowerDead;
    ray.t = Double.POSITIVE_INFINITY;
    for (AABB aabb : boxes) {
      if (aabb.intersect(ray)) {
        texture.getColor(ray);
        ray.t = ray.tNext;
        hit = true;
      }
    }

    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
