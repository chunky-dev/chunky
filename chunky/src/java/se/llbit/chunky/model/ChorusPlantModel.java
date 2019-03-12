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
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

public class ChorusPlantModel {
  private static AABB core = new AABB(4 / 16., 12 / 16., 4 / 16., 12 / 16., 4 / 16., 12 / 16.);

  private static AABB[] connector = {
      new AABB(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0, 0, 4 / 16.),
      new AABB(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0, 12 / 16., 1),
      new AABB(12 / 16., 1, 4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0),
      new AABB(0, 4 / 16., 4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0),
      // Above.
      new AABB(4 / 16., 12 / 16., 12 / 16.0, 1, 4 / 16.0, 12 / 16.0),
      // Below
      new AABB(4 / 16., 12 / 16., 0, 4 / 16.0, 4 / 16.0, 12 / 16.0),
  };

  public static boolean intersect(Ray ray) {
    int connections = ray.getCurrentData() >> BlockData.OFFSET;
    return intersect(ray, connections);
  }

  public static boolean intersect(Ray ray, int connections) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    if (core.intersect(ray)) {
      Texture.chorusPlant.getColor(ray);
      ray.t = ray.tNext;
      hit = true;
    }
    for (int i = 0; i < 6; ++i) {
      if ((connections & (1 << i)) != 0) {
        if (connector[i].intersect(ray)) {
          Texture.chorusPlant.getColor(ray);
          ray.t = ray.tNext;
          hit = true;
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
