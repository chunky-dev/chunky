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

public class SnowModel {
  protected static Quad[][] quads = new Quad[8][];

  static {
    for (int i = 0; i < 8; ++i) {
      double height = (i + 1) * .125;

      quads[i] = new Quad[6];

      // front
      quads[i][0] =
          new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, height, 0),
              new Vector4(1, 0, 0, height));
      // back
      quads[i][1] =
          new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, height, 1),
              new Vector4(0, 1, 0, height));

      // right
      quads[i][2] =
          new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, height, 0),
              new Vector4(0, 1, 0, height));

      // left
      quads[i][3] =
          new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, height, 1),
              new Vector4(1, 0, 0, height));

      // top
      quads[i][4] = new Quad(new Vector3(1, height, 0), new Vector3(0, height, 0),
          new Vector3(1, height, 1), new Vector4(1, 0, 0, 1));

      // bottom
      quads[i][5] = new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1));
    }
  }

  public static boolean intersect(Ray ray) {
    int layers = ray.getBlockData();
    return intersect(ray, layers);
  }

  public static boolean intersect(Ray ray, int layers) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads[layers - 1]) {
      if (quad.intersect(ray)) {
        Texture.snowBlock.getColor(ray);
        ray.setN(quad.n);
        ray.t = ray.tNext;
        hit = true;
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
