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

public class EndPortalFrameModel {
  private static AABB frame = new AABB(0, 1, 0, 13 / 16.0, 0, 1);
  private static AABB eyeOfTheEnder = new AABB(.25, .75, 13 / 16.0, 1, .25, .75);

  public static boolean intersect(Ray ray) {
    boolean hasEye = (ray.getBlockData() & 4) != 0;
    return intersect(ray, hasEye);
  }

  public static boolean intersect(Ray ray, boolean hasEye) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    if (frame.intersect(ray)) {
      if (ray.n.y > 0) {
        Texture.endPortalFrameTop.getColor(ray);
      } else if (ray.n.y < 0) {
        Texture.endStone.getColor(ray);
      } else {
        Texture.endPortalFrameSide.getColor(ray);
      }
      ray.t = ray.tNext;
      hit = true;
    }
    if (hasEye && eyeOfTheEnder.intersect(ray)) {
      if (ray.n.y > 0) {
        Texture.eyeOfTheEnder.getColor(ray);
      } else {
        Texture.eyeOfTheEnder.getColor(ray);
      }
      ray.t = ray.tNext;
      hit = true;
    }
    if (hit) {
      ray.color.w = 1;
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
