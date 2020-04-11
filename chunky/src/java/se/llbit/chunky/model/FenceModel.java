/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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

public class FenceModel {
  private static AABB post = new AABB(6 / 16., 10 / 16., 0, 1, 6 / 16., 10 / 16.);

  private static AABB[][] plank = {
      // Connected north.
      {
        new AABB(7 / 16.0, 9 / 16.0, 6 / 16.0, 9 / 16.0, 0, .4),
        new AABB(7 / 16.0, 9 / 16.0, 12 / 16.0, 15 / 16.0, 0, .4),
      },
      // Connected south.
      {
        new AABB(7 / 16.0, 9 / 16.0, 6 / 16.0, 9 / 16.0, .6, 1),
        new AABB(7 / 16.0, 9 / 16.0, 12 / 16.0, 15 / 16.0, .6, 1),
      },
      // Connected east.
      {
        new AABB(.6, 1, 6 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0),
        new AABB(.6, 1, 12 / 16.0, 15 / 16.0, 7 / 16.0, 9 / 16.0),
      },
      // Connected west.
      {
        new AABB(0, .4, 6 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0),
        new AABB(0, .4, 12 / 16.0, 15 / 16.0, 7 / 16.0, 9 / 16.0),
      },
  };

  public static boolean intersect(Ray ray, Texture texture) {
    int connections = ray.getBlockData();
    return intersect(ray, texture, connections);
  }

  public static boolean intersect(Ray ray, Texture texture, int connections) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    if (post.intersect(ray)) {
      texture.getColor(ray);
      ray.t = ray.tNext;
      hit = true;
    }
    for (int i = 0; i < 4; ++i) {
      if ((connections & (1 << i)) != 0) {
        for (AABB aabb : plank[i]) {
          if (aabb.intersect(ray)) {
            texture.getColor(ray);
            ray.t = ray.tNext;
            hit = true;
          }
        }
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
