/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

/**
 * This block model is used to render blocks which can face east, west, north, south, up and down.
 * For example, command blocks and observer blocks.
 */
public class DirectionalBlockModel {

  // Facing up:
  private static final Quad[] up = new Quad[] {
      // Bottom face.
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(1, 0, 0, 1)),

      // Top face.
      new Quad(new Vector3(0, 0, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),

      // West face.
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 1)),

      // East face.
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // Front face.
      new Quad(new Vector3(1, 1, 0), new Vector3(0, 1, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 0, 1)),

      // Back face.
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
  };

  private static final Quad[][] faces = new Quad[8][];

  static {
    // Rotate faces for all directions.
    faces[1] = up;
    Quad[] temp = Model.rotateX(up);
    // Facing down:
    faces[0] = Model.rotateX(temp);
    // Facing north:
    faces[2] = Model.rotateX(faces[0]);
    // Facing east:
    faces[5] = Model.rotateY(faces[2]);
    // Facing south:
    faces[3] = Model.rotateY(faces[5]);
    // Facing west:
    faces[4] = Model.rotateY(faces[3]);
    // Facing down:
    faces[6] = faces[1];
    // Facing up:
    faces[7] = up;
  }

  // Index 0 = back, 1 = front, 2 = side.
  private static final int[] textureIndex = {2, 2, 2, 2, 1, 0};

  /**
   * @param textures texture[0] is the back texture, texture[1] is the front
   * texture, and texture[2] is the side texture.
   */
  public static boolean intersect(Ray ray, Texture[] textures) {
    int direction = ray.getBlockData() & 7;
    return intersect(ray, textures, direction);
  }

  public static boolean intersect(Ray ray, Texture[] textures, int direction) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < 6; ++i) {
      Quad face = faces[direction][i];
      if (face.intersect(ray)) {
        textures[textureIndex[i]].getColor(ray);
        ray.setNormal(face.n);
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
