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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

public class GrassModel {
  private static AABB aabb = new AABB(0, 1, 0, 1, 0, 1);

  public static boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;
    if (aabb.intersect(ray)) {
      if (ray.n.y == -1) {
        // Bottom face.
        Texture.dirt.getColor(ray);
        ray.color.w = 1;
        ray.t = ray.tNext;
      } else if (ray.n.y == 0 && (ray.getCurrentData() & (1 << 8)) != 0) {
        // Snowy side face.
        Texture.snowSide.getColor(ray);
        ray.color.w = 1;
        ray.t = ray.tNext;
      } else {
        float[] color;
        if (ray.n.y > 0) {
          color = Texture.grassTop.getColor(ray.u, ray.v);
        } else {
          color = Texture.grassSide.getColor(ray.u, ray.v);
        }
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          float[] biomeColor = ray.getBiomeGrassColor(scene);
          ray.color.x *= biomeColor[0];
          ray.color.y *= biomeColor[1];
          ray.color.z *= biomeColor[2];
          ray.t = ray.tNext;
        } else {
          Texture.grassSideSaturated.getColor(ray);
          ray.color.w = 1;
          ray.t = ray.tNext;
        }
      }
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
      return true;
    }
    return false;
  }
}
