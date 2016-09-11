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
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

public class StoneWallModel {
  private static AABB post = new AABB(4 / 16.0, 12 / 16.0, 0, 1, 4 / 16.0, 12 / 16.0);

  private static AABB[] plank = {new AABB(5 / 16.0, 11 / 16.0, 0, 13 / 16.0, 0, .5),
      new AABB(5 / 16.0, 11 / 16.0, 0, 13 / 16.0, .5, 1),
      new AABB(.5, 1, 0, 13 / 16.0, 5 / 16.0, 11 / 16.0),
      new AABB(0, .5, 0, 13 / 16.0, 5 / 16.0, 11 / 16.0)};

  public static boolean intersect(Ray ray, Texture texture) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    int connections = 0xF & (ray.getCurrentData() >>> BlockData.STONEWALL_CONN);
    int midsection = 1 & (ray.getCurrentData() >>> BlockData.STONEWALL_CORNER);
    // figure out if we should draw the center post
    if (midsection != 0) {
      if (post.intersect(ray)) {
        texture.getColor(ray);
        ray.t = ray.tNext;
        hit = true;
      }
    }
    for (int i = 0; i < 4; ++i) {
      if ((connections & (1 << i)) != 0) {
        if (plank[i].intersect(ray)) {
          texture.getColor(ray);
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
