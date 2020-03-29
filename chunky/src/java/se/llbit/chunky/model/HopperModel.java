/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Hopper block
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class HopperModel {
  private static final AABB[] boxes = new AABB[] {
      // East.
      new AABB(14 / 16., 1, 10 / 16., 1, 0, 1),
      // West.
      new AABB(0, 2 / 16., 10 / 16., 1, 0, 1),
      // North.
      new AABB(2 / 16., 14 / 16., 10 / 16., 1, 0, 2 / 16.),
      // South.
      new AABB(2 / 16., 14 / 16., 10 / 16., 1, 14 / 16., 1),
      // Center.
      new AABB(4 / 16., 12 / 16., 4 / 16., 10 / 16., 4 / 16., 12 / 16.),
  };

  private static final AABB[] pipe = new AABB[] {
      // Bottom.
      new AABB(6 / 16., 10 / 16., 0, 4 / 16., 6 / 16., 10 / 16.),
      // Bottom.
      new AABB(6 / 16., 10 / 16., 0, 4 / 16., 6 / 16., 10 / 16.),
      // Facing north.
      new AABB(6 / 16., 10 / 16., 4 / 16., 8 / 16., 0, 4 / 16.),
      // Facing south.
      new AABB(6 / 16., 10 / 16., 4 / 16., 8 / 16., 12 / 16., 1),
      // Facing west.
      new AABB(0 / 16., 4 / 16., 4 / 16., 8 / 16., 6 / 16., 10 / 16.),
      // Facing east.
      new AABB(12 / 16., 1, 4 / 16., 8 / 16., 6 / 16., 10 / 16.),
      // Bottom.
      new AABB(6 / 16., 10 / 16., 0, 4 / 16., 6 / 16., 10 / 16.),
      // Bottom.
      new AABB(6 / 16., 10 / 16., 0, 4 / 16., 6 / 16., 10 / 16.),
  };

  private static final Quad bottom = new DoubleSidedQuad(new Vector3(2 / 16., 10 / 16., 2 / 16.),
      new Vector3(14 / 16., 10 / 16., 2 / 16.), new Vector3(2 / 16., 10 / 16., 14 / 16.),
      new Vector4(2 / 16., 14 / 16., 2 / 16., 14 / 16.));

  public static boolean intersect(Ray ray) {
    int direction = 7 & (ray.getCurrentData() >> BlockData.OFFSET);
    return intersect(ray, direction);
  }

  public static boolean intersect(Ray ray, int direction) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (AABB box : boxes) {
      if (box.intersect(ray)) {
        if (ray.n.y > 0) {
          if (box == boxes[boxes.length - 1]) {
            Texture.hopperInside.getColor(ray);
          } else {
            Texture.hopperTop.getColor(ray);
          }
        } else {
          Texture.hopperOutside.getColor(ray);
        }
        ray.color.w = 1;
        ray.t = ray.tNext;
        hit = true;
      }
    }
    if (pipe[direction].intersect(ray)) {
      if (ray.n.y > 0) {
        Texture.hopperInside.getColor(ray);
      } else {
        Texture.hopperOutside.getColor(ray);
      }
      ray.color.w = 1;
      ray.t = ray.tNext;
      hit = true;
    }
    if (bottom.intersect(ray)) {
      ray.n.set(bottom.n);
      ray.n.scale(-QuickMath.signum(ray.d.dot(bottom.n)));
      if (ray.n.y > 0) {
        Texture.hopperInside.getColor(ray);
      } else {
        Texture.hopperOutside.getColor(ray);
      }
      ray.t = ray.tNext;
      hit = true;
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
