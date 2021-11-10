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
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class SpriteModel {

  protected static Quad[] quads =
      {new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

          new Quad(new Vector3(1, 0, 1), new Vector3(0, 0, 0), new Vector3(1, 1, 1),
              new Vector4(0, 1, 0, 1)),

          new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 1), new Vector3(1, 1, 0),
              new Vector4(0, 1, 0, 1)),

          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 1, 1),
              new Vector4(0, 1, 0, 1)),};

  static final Quad[][] orientedQuads = new Quad[6][];

  static {
    orientedQuads[4] = quads;
    orientedQuads[0] = Model.rotateX(Model.rotateX(quads));
    orientedQuads[2] = Model.rotateNegX(quads);
    orientedQuads[1] = Model.rotateY(orientedQuads[2]);
    orientedQuads[3] = Model.rotateY(orientedQuads[1]);
    orientedQuads[5] = Model.rotateY(orientedQuads[3]);
  }

  public static boolean intersect(Ray ray, Texture material) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = material.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
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

  public static boolean intersect(Ray ray, Texture material, String facing) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : orientedQuads[getOrientationIndex(facing)]) {
      if (quad.intersect(ray)) {
        float[] color = material.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
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

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "down":
        return 0;
      case "east":
        return 1;
      case "north":
        return 2;
      case "south":
        return 3;
      case "up":
        return 4;
      case "west":
        return 5;
      default:
        return 4;
    }
  }
}
