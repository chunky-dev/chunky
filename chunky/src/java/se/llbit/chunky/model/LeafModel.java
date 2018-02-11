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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;

public class LeafModel {
  private static final AABB block = new AABB(0, 1, 0, 1, 0, 1);

  /**
   * Get leaf color at ray intersection, based on biome.
   */
  public static boolean intersect(Ray ray, Scene scene, Texture texture) {
    ray.t = Double.POSITIVE_INFINITY;
    if (block.intersect(ray)) {
      float[] color = texture.getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        float[] biomeColor;
        biomeColor = ray.getBiomeFoliageColor(scene);
        ray.color.x *= biomeColor[0];
        ray.color.y *= biomeColor[1];
        ray.color.z *= biomeColor[2];
        ray.distance += ray.tNext;
        ray.o.scaleAdd(ray.tNext, ray.d);
        return true;
      }
    }
    return false;
  }

  /**
   * Get leaf color at ray intersection, using base leaf color.
   *
   * @param leafColor base leaf color to blend the leaf texture with.
   */
  public static boolean intersect(Ray ray, Texture texture, float[] leafColor) {
    ray.t = Double.POSITIVE_INFINITY;
    if (block.intersect(ray)) {
      float[] color = texture.getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        ray.color.x *= leafColor[0];
        ray.color.y *= leafColor[1];
        ray.color.z *= leafColor[2];
        ray.distance += ray.tNext;
        ray.o.scaleAdd(ray.tNext, ray.d);
        return true;
      }
    }
    return false;
  }
}
