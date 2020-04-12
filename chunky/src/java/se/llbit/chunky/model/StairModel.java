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

public class StairModel {
  private static AABB[][][] corners = {
    // Not flipped:
    {
      {
        // s-e
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0.5, 1),
      }, {
        // s-w
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 0.5, 0.5, 1, 0.5, 1),
      }, {
        // n-e
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0, 0.5),
      }, {
        // n-w
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 0.5, 0.5, 1, 0, 0.5),
      }, {
        // inner s-e
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0.5, 1),
        new AABB(0.5, 1, 0.5, 1, 0, 0.5),
      }, {
        // inner s-w
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0.5, 1),
        new AABB(0, 0.5, 0.5, 1, 0, 1),
      }, {
        // inner n-e
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0, 1),
        new AABB(0, 0.5, 0.5, 1, 0, 0.5),
      }, {
        // inner n-w
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0, 0.5),
        new AABB(0, 0.5, 0.5, 1, 0.5, 1),
      },
    },
    // Flipped:
    {
      {
        // s-e
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0.5, 1),
      }, {
        // s-w
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 0.5, 0, 0.5, 0.5, 1),
      }, {
        // n-e
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0, 0.5),
      }, {
        // n-w
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 0.5, 0, 0.5, 0, 0.5),
      }, {
        // inner s-e
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0.5, 1),
        new AABB(0.5, 1, 0, 0.5, 0, 0.5),
      }, {
        // inner s-w
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0.5, 1),
        new AABB(0, 0.5, 0, 0.5, 0, 1),
      }, {
        // inner n-e
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0, 1),
        new AABB(0, 0.5, 0, 0.5, 0, 0.5),
      }, {
        // inner n-w
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0, 0.5),
        new AABB(0, 0.5, 0, 0.5, 0.5, 1),
      },
    },
  };
  private static final AABB[][][] stairs = {
    // Not flipped.
    {
      {
        // ascending east
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0.5, 1, 0.5, 1, 0, 1),
      }, {
        // ascending west
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 0.5, 0.5, 1, 0, 1),
      }, {
        // ascending south
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0.5, 1),
      }, {
        // ascending north
        new AABB(0, 1, 0, 0.5, 0, 1), new AABB(0, 1, 0.5, 1, 0, 0.5),
      },
    },
    // flipped
    {
      {
        // ascending east
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0.5, 1, 0, 0.5, 0, 1),
      }, {
        // ascending west
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 0.5, 0, 0.5, 0, 1),
      }, {
        // ascending south
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0.5, 1),
      }, {
        // ascending north
        new AABB(0, 1, 0.5, 1, 0, 1), new AABB(0, 1, 0, 0.5, 0, 0.5),
      },
    },
  };

  public static boolean intersect(Ray ray, Texture texture) {
    int flipped = (ray.getBlockData() & 4) >> 2;
    int corner = 15 & (ray.getCurrentData() >> BlockData.CORNER_OFFSET);
    int rotation = 3 & ray.getBlockData();
    return intersect(ray, texture, flipped, corner != 0, corner, rotation);
  }

  public static boolean intersect(Ray ray, Texture texture,
      int flipped, boolean isCorner, int corner, int rotation) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    if (isCorner) {
      for (AABB box : corners[flipped][7 & corner]) {
        if (box.intersect(ray)) {
          texture.getColor(ray);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    } else {
      for (AABB box : stairs[flipped][rotation]) {
        if (box.intersect(ray)) {
          texture.getColor(ray);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    }

    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  public static boolean intersect(Ray ray, Texture side, Texture top, Texture bottom) {
    boolean hit = intersect(ray, side);
    if (hit) {
      if (ray.n.y > 0) {
        top.getColor(ray);
      } else if (ray.n.y < 0) {
        bottom.getColor(ray);
      }
    }
    return hit;
  }

  public static boolean intersect(Ray ray, Texture side, Texture top, Texture bottom,
      int flipped, boolean isCorner, int corner, int rotation) {
    boolean hit = intersect(ray, side, flipped, isCorner, corner, rotation);
    if (hit) {
      if (ray.n.y > 0) {
        top.getColor(ray);
      } else if (ray.n.y < 0) {
        bottom.getColor(ray);
      }
    }
    return hit;
  }
}
