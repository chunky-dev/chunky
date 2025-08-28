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

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.IntersectionRecord;
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
  public static void getIntersectionColor(Ray ray, IntersectionRecord intersectionRecord) {
    if (intersectionRecord.material == Air.INSTANCE) {
      intersectionRecord.color.x = 1;
      intersectionRecord.color.y = 1;
      intersectionRecord.color.z = 1;
      intersectionRecord.color.w = 0;
      return;
    }
    getTextureCoordinates(ray, intersectionRecord);
    intersectionRecord.material.getColor(intersectionRecord);
  }

  /**
   * Calculate the UV coordinates for the ray on the intersected block.
   *
   * @param ray ray to test
   */
  private static void getTextureCoordinates(Ray ray, IntersectionRecord intersectionRecord) {
    int bx = (int) QuickMath.floor(ray.o.x);
    int by = (int) QuickMath.floor(ray.o.y);
    int bz = (int) QuickMath.floor(ray.o.z);
    Vector3 n = intersectionRecord.n;
    if (n.y != 0) {
      intersectionRecord.uv.x = ray.o.x - bx;
      intersectionRecord.uv.y = ray.o.z - bz;
    } else if (n.x != 0) {
      intersectionRecord.uv.x = ray.o.z - bz;
      intersectionRecord.uv.y = ray.o.y - by;
    } else {
      intersectionRecord.uv.x = ray.o.x - bx;
      intersectionRecord.uv.y = ray.o.y - by;
    }
    if (n.x > 0 || n.z < 0) {
      intersectionRecord.uv.x = 1 - intersectionRecord.uv.x;
    }
    if (n.y > 0) {
      intersectionRecord.uv.y = 1 - intersectionRecord.uv.y;
    }
  }
}
