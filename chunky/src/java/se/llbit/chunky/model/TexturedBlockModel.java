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

import se.llbit.chunky.block.block.Air;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * A textured block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturedBlockModel extends AABBModel {
  private static final AABB[] boxes = { new AABB(0, 1, 0, 1, 0, 1) };

  private final Texture[][] textures;

  public TexturedBlockModel(Texture north, Texture east, Texture south, Texture west, Texture top, Texture bottom) {
    this.textures = new Texture[][] {{
      north, east, south, west, top, bottom
    }};
  }

  @Override
  public AABB[] getBoxes() {
    return boxes;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }

  /**
   * Find the color of the object at the intersection point.
   *
   * @param ray ray to test
   */
  public static void getIntersectionColor(Ray ray) {
    if (ray.getCurrentMaterial() == Air.INSTANCE) {
      ray.color.x = 1;
      ray.color.y = 1;
      ray.color.z = 1;
      ray.color.w = 0;
      return;
    }
    getTextureCoordinates(ray);
    ray.getCurrentMaterial().getColor(ray);
  }

  /**
   * Calculate the UV coordinates for the ray on the intersected block.
   *
   * @param ray ray to test
   */
  private static void getTextureCoordinates(Ray ray) {
    int bx = (int) QuickMath.floor(ray.o.x);
    int by = (int) QuickMath.floor(ray.o.y);
    int bz = (int) QuickMath.floor(ray.o.z);
    Vector3 n = ray.getNormal();
    if (n.y != 0) {
      ray.u = ray.o.x - bx;
      ray.v = ray.o.z - bz;
    } else if (n.x != 0) {
      ray.u = ray.o.z - bz;
      ray.v = ray.o.y - by;
    } else {
      ray.u = ray.o.x - bx;
      ray.v = ray.o.y - by;
    }
    if (n.x > 0 || n.z < 0) {
      ray.u = 1 - ray.u;
    }
    if (n.y > 0) {
      ray.v = 1 - ray.v;
    }
  }
}
