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

public class DragonEggModel {
  private static final Quad[] quads =
      new Quad[] {
        new Quad(
            new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
            new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
            new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
            new Vector4(6 / 16.0, 10 / 16.0, 6 / 16.0, 10 / 16.0)),
        new Quad(
            new Vector3(6 / 16.0, 15 / 16.0, 6 / 16.0),
            new Vector3(10 / 16.0, 15 / 16.0, 6 / 16.0),
            new Vector3(6 / 16.0, 15 / 16.0, 10 / 16.0),
            new Vector4(6 / 16.0, 10 / 16.0, 6 / 16.0, 10 / 16.0)),
        new Quad(
            new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
            new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
            new Vector3(6 / 16.0, 15 / 16.0, 10 / 16.0),
            new Vector4(10 / 16.0, 6 / 16.0, 1 / 16.0, 0 / 16.0)),
        new Quad(
            new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
            new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
            new Vector3(10 / 16.0, 15 / 16.0, 6 / 16.0),
            new Vector4(10 / 16.0, 6 / 16.0, 1 / 16.0, 0 / 16.0)),
        new Quad(
            new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
            new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
            new Vector3(6 / 16.0, 15 / 16.0, 6 / 16.0),
            new Vector4(10 / 16.0, 6 / 16.0, 1 / 16.0, 0 / 16.0)),
        new Quad(
            new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
            new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
            new Vector3(10 / 16.0, 15 / 16.0, 10 / 16.0),
            new Vector4(10 / 16.0, 6 / 16.0, 1 / 16.0, 0 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 15 / 16.0, 11 / 16.0),
            new Vector3(11 / 16.0, 15 / 16.0, 11 / 16.0),
            new Vector3(5 / 16.0, 15 / 16.0, 5 / 16.0),
            new Vector4(5 / 16.0, 11 / 16.0, 5 / 16.0, 11 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector3(11 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector3(5 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector4(5 / 16.0, 11 / 16.0, 5 / 16.0, 11 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 15 / 16.0, 11 / 16.0),
            new Vector3(5 / 16.0, 15 / 16.0, 5 / 16.0),
            new Vector3(5 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector4(11 / 16.0, 5 / 16.0, 2 / 16.0, 1 / 16.0)),
        new Quad(
            new Vector3(11 / 16.0, 15 / 16.0, 5 / 16.0),
            new Vector3(11 / 16.0, 15 / 16.0, 11 / 16.0),
            new Vector3(11 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector4(11 / 16.0, 5 / 16.0, 2 / 16.0, 1 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 15 / 16.0, 5 / 16.0),
            new Vector3(11 / 16.0, 15 / 16.0, 5 / 16.0),
            new Vector3(5 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector4(11 / 16.0, 5 / 16.0, 2 / 16.0, 1 / 16.0)),
        new Quad(
            new Vector3(11 / 16.0, 15 / 16.0, 11 / 16.0),
            new Vector3(5 / 16.0, 15 / 16.0, 11 / 16.0),
            new Vector3(11 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector4(11 / 16.0, 5 / 16.0, 2 / 16.0, 1 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector3(11 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector3(5 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector4(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 13 / 16.0, 5 / 16.0),
            new Vector3(11 / 16.0, 13 / 16.0, 5 / 16.0),
            new Vector3(5 / 16.0, 13 / 16.0, 11 / 16.0),
            new Vector4(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector3(5 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector3(5 / 16.0, 13 / 16.0, 11 / 16.0),
            new Vector4(12 / 16.0, 4 / 16.0, 3 / 16.0, 2 / 16.0)),
        new Quad(
            new Vector3(11 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector3(11 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector3(11 / 16.0, 13 / 16.0, 5 / 16.0),
            new Vector4(12 / 16.0, 4 / 16.0, 3 / 16.0, 2 / 16.0)),
        new Quad(
            new Vector3(5 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector3(11 / 16.0, 14 / 16.0, 5 / 16.0),
            new Vector3(5 / 16.0, 13 / 16.0, 5 / 16.0),
            new Vector4(12 / 16.0, 4 / 16.0, 3 / 16.0, 2 / 16.0)),
        new Quad(
            new Vector3(11 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector3(5 / 16.0, 14 / 16.0, 11 / 16.0),
            new Vector3(11 / 16.0, 13 / 16.0, 11 / 16.0),
            new Vector4(12 / 16.0, 4 / 16.0, 3 / 16.0, 2 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 13 / 16.0, 13 / 16.0),
            new Vector3(13 / 16.0, 13 / 16.0, 13 / 16.0),
            new Vector3(3 / 16.0, 13 / 16.0, 3 / 16.0),
            new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 11 / 16.0, 3 / 16.0),
            new Vector3(13 / 16.0, 11 / 16.0, 3 / 16.0),
            new Vector3(3 / 16.0, 11 / 16.0, 13 / 16.0),
            new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 13 / 16.0, 13 / 16.0),
            new Vector3(3 / 16.0, 13 / 16.0, 3 / 16.0),
            new Vector3(3 / 16.0, 11 / 16.0, 13 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 5 / 16.0, 3 / 16.0)),
        new Quad(
            new Vector3(13 / 16.0, 13 / 16.0, 3 / 16.0),
            new Vector3(13 / 16.0, 13 / 16.0, 13 / 16.0),
            new Vector3(13 / 16.0, 11 / 16.0, 3 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 5 / 16.0, 3 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 13 / 16.0, 3 / 16.0),
            new Vector3(13 / 16.0, 13 / 16.0, 3 / 16.0),
            new Vector3(3 / 16.0, 11 / 16.0, 3 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 5 / 16.0, 3 / 16.0)),
        new Quad(
            new Vector3(13 / 16.0, 13 / 16.0, 13 / 16.0),
            new Vector3(3 / 16.0, 13 / 16.0, 13 / 16.0),
            new Vector3(13 / 16.0, 11 / 16.0, 13 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 5 / 16.0, 3 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 11 / 16.0, 14 / 16.0),
            new Vector3(14 / 16.0, 11 / 16.0, 14 / 16.0),
            new Vector3(2 / 16.0, 11 / 16.0, 2 / 16.0),
            new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 8 / 16.0, 2 / 16.0),
            new Vector3(14 / 16.0, 8 / 16.0, 2 / 16.0),
            new Vector3(2 / 16.0, 8 / 16.0, 14 / 16.0),
            new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 11 / 16.0, 14 / 16.0),
            new Vector3(2 / 16.0, 11 / 16.0, 2 / 16.0),
            new Vector3(2 / 16.0, 8 / 16.0, 14 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 8 / 16.0, 5 / 16.0)),
        new Quad(
            new Vector3(14 / 16.0, 11 / 16.0, 2 / 16.0),
            new Vector3(14 / 16.0, 11 / 16.0, 14 / 16.0),
            new Vector3(14 / 16.0, 8 / 16.0, 2 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 8 / 16.0, 5 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 11 / 16.0, 2 / 16.0),
            new Vector3(14 / 16.0, 11 / 16.0, 2 / 16.0),
            new Vector3(2 / 16.0, 8 / 16.0, 2 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 8 / 16.0, 5 / 16.0)),
        new Quad(
            new Vector3(14 / 16.0, 11 / 16.0, 14 / 16.0),
            new Vector3(2 / 16.0, 11 / 16.0, 14 / 16.0),
            new Vector3(14 / 16.0, 8 / 16.0, 14 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 8 / 16.0, 5 / 16.0)),
        new Quad(
            new Vector3(1 / 16.0, 8 / 16.0, 15 / 16.0),
            new Vector3(15 / 16.0, 8 / 16.0, 15 / 16.0),
            new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
            new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)),
        new Quad(
            new Vector3(1 / 16.0, 3 / 16.0, 1 / 16.0),
            new Vector3(15 / 16.0, 3 / 16.0, 1 / 16.0),
            new Vector3(1 / 16.0, 3 / 16.0, 15 / 16.0),
            new Vector4(1 / 16.0, 15 / 16.0, 1 / 16.0, 15 / 16.0)),
        new Quad(
            new Vector3(1 / 16.0, 8 / 16.0, 15 / 16.0),
            new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
            new Vector3(1 / 16.0, 3 / 16.0, 15 / 16.0),
            new Vector4(15 / 16.0, 1 / 16.0, 13 / 16.0, 8 / 16.0)),
        new Quad(
            new Vector3(15 / 16.0, 8 / 16.0, 1 / 16.0),
            new Vector3(15 / 16.0, 8 / 16.0, 15 / 16.0),
            new Vector3(15 / 16.0, 3 / 16.0, 1 / 16.0),
            new Vector4(15 / 16.0, 1 / 16.0, 13 / 16.0, 8 / 16.0)),
        new Quad(
            new Vector3(1 / 16.0, 8 / 16.0, 1 / 16.0),
            new Vector3(15 / 16.0, 8 / 16.0, 1 / 16.0),
            new Vector3(1 / 16.0, 3 / 16.0, 1 / 16.0),
            new Vector4(15 / 16.0, 1 / 16.0, 13 / 16.0, 8 / 16.0)),
        new Quad(
            new Vector3(15 / 16.0, 8 / 16.0, 15 / 16.0),
            new Vector3(1 / 16.0, 8 / 16.0, 15 / 16.0),
            new Vector3(15 / 16.0, 3 / 16.0, 15 / 16.0),
            new Vector4(15 / 16.0, 1 / 16.0, 13 / 16.0, 8 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 3 / 16.0, 14 / 16.0),
            new Vector3(14 / 16.0, 3 / 16.0, 14 / 16.0),
            new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
            new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 1 / 16.0, 2 / 16.0),
            new Vector3(14 / 16.0, 1 / 16.0, 2 / 16.0),
            new Vector3(2 / 16.0, 1 / 16.0, 14 / 16.0),
            new Vector4(2 / 16.0, 14 / 16.0, 2 / 16.0, 14 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 3 / 16.0, 14 / 16.0),
            new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
            new Vector3(2 / 16.0, 1 / 16.0, 14 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 15 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(14 / 16.0, 3 / 16.0, 2 / 16.0),
            new Vector3(14 / 16.0, 3 / 16.0, 14 / 16.0),
            new Vector3(14 / 16.0, 1 / 16.0, 2 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 15 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(2 / 16.0, 3 / 16.0, 2 / 16.0),
            new Vector3(14 / 16.0, 3 / 16.0, 2 / 16.0),
            new Vector3(2 / 16.0, 1 / 16.0, 2 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 15 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(14 / 16.0, 3 / 16.0, 14 / 16.0),
            new Vector3(2 / 16.0, 3 / 16.0, 14 / 16.0),
            new Vector3(14 / 16.0, 1 / 16.0, 14 / 16.0),
            new Vector4(14 / 16.0, 2 / 16.0, 15 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 1 / 16.0, 13 / 16.0),
            new Vector3(13 / 16.0, 1 / 16.0, 13 / 16.0),
            new Vector3(3 / 16.0, 1 / 16.0, 3 / 16.0),
            new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 0 / 16.0, 3 / 16.0),
            new Vector3(13 / 16.0, 0 / 16.0, 3 / 16.0),
            new Vector3(3 / 16.0, 0 / 16.0, 13 / 16.0),
            new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 1 / 16.0, 13 / 16.0),
            new Vector3(3 / 16.0, 1 / 16.0, 3 / 16.0),
            new Vector3(3 / 16.0, 0 / 16.0, 13 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 16 / 16.0, 15 / 16.0)),
        new Quad(
            new Vector3(13 / 16.0, 1 / 16.0, 3 / 16.0),
            new Vector3(13 / 16.0, 1 / 16.0, 13 / 16.0),
            new Vector3(13 / 16.0, 0 / 16.0, 3 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 16 / 16.0, 15 / 16.0)),
        new Quad(
            new Vector3(3 / 16.0, 1 / 16.0, 3 / 16.0),
            new Vector3(13 / 16.0, 1 / 16.0, 3 / 16.0),
            new Vector3(3 / 16.0, 0 / 16.0, 3 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 16 / 16.0, 15 / 16.0)),
        new Quad(
            new Vector3(13 / 16.0, 1 / 16.0, 13 / 16.0),
            new Vector3(3 / 16.0, 1 / 16.0, 13 / 16.0),
            new Vector3(13 / 16.0, 0 / 16.0, 13 / 16.0),
            new Vector4(13 / 16.0, 3 / 16.0, 16 / 16.0, 15 / 16.0))
      };

  public static boolean intersect(Ray ray) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = Texture.dragonEgg.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.setNormal(quad.n);
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
}
