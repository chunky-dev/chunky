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
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Renders the Cocoa Plant
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class CocoaPlantModel {
  private static final Quad[] large = {
      // front
      new Quad(new Vector3(12 / 16., 3 / 16., 7 / 16.), new Vector3(4 / 16., 3 / 16., 7 / 16.),
          new Vector3(12 / 16., 12 / 16., 7 / 16.),
          new Vector4(7 / 16., 15 / 16., 3 / 16., 12 / 16.)),

      // back
      new Quad(new Vector3(4 / 16., 3 / 16., 15 / 16.), new Vector3(12 / 16., 3 / 16., 15 / 16.),
          new Vector3(4 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 7 / 16., 3 / 16., 12 / 16.)),

      // left
      new Quad(new Vector3(4 / 16., 3 / 16., 7 / 16.), new Vector3(4 / 16., 3 / 16., 15 / 16.),
          new Vector3(4 / 16., 12 / 16., 7 / 16.),
          new Vector4(7 / 16., 15 / 16., 3 / 16., 12 / 16.)),

      // right
      new Quad(new Vector3(12 / 16., 3 / 16., 15 / 16.), new Vector3(12 / 16., 3 / 16., 7 / 16.),
          new Vector3(12 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 7 / 16., 3 / 16., 12 / 16.)),

      // top
      new Quad(new Vector3(12 / 16., 12 / 16., 7 / 16.), new Vector3(4 / 16., 12 / 16., 7 / 16.),
          new Vector3(12 / 16., 12 / 16., 15 / 16.), new Vector4(7 / 16., 0, 9 / 16., 1)),

      // bottom
      new Quad(new Vector3(4 / 16., 3 / 16., 7 / 16.), new Vector3(12 / 16., 3 / 16., 7 / 16.),
          new Vector3(4 / 16., 3 / 16., 15 / 16.), new Vector4(0, 7 / 16., 9 / 16., 1)),
  };
  private static final Quad[] medium = {
      // front
      new Quad(new Vector3(11 / 16., 5 / 16., 9 / 16.), new Vector3(5 / 16., 5 / 16., 9 / 16.),
          new Vector3(11 / 16., 12 / 16., 9 / 16.),
          new Vector4(9 / 16., 15 / 16., 5 / 16., 12 / 16.)),

      // back
      new Quad(new Vector3(5 / 16., 5 / 16., 15 / 16.), new Vector3(11 / 16., 5 / 16., 15 / 16.),
          new Vector3(5 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 9 / 16., 5 / 16., 12 / 16.)),

      // left
      new Quad(new Vector3(5 / 16., 5 / 16., 9 / 16.), new Vector3(5 / 16., 5 / 16., 15 / 16.),
          new Vector3(5 / 16., 12 / 16., 9 / 16.),
          new Vector4(9 / 16., 15 / 16., 5 / 16., 12 / 16.)),

      // right
      new Quad(new Vector3(11 / 16., 5 / 16., 15 / 16.), new Vector3(11 / 16., 5 / 16., 9 / 16.),
          new Vector3(11 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 9 / 16., 5 / 16., 12 / 16.)),

      // top
      new Quad(new Vector3(11 / 16., 12 / 16., 9 / 16.), new Vector3(5 / 16., 12 / 16., 9 / 16.),
          new Vector3(11 / 16., 12 / 16., 15 / 16.), new Vector4(6 / 16., 0, 10 / 16., 1)),

      // bottom
      new Quad(new Vector3(5 / 16., 5 / 16., 9 / 16.), new Vector3(11 / 16., 5 / 16., 9 / 16.),
          new Vector3(5 / 16., 5 / 16., 15 / 16.), new Vector4(0, 6 / 16., 10 / 16., 1)),
  };
  private static final Quad[] small = {
      // front
      new Quad(new Vector3(10 / 16., 7 / 16., 11 / 16.), new Vector3(6 / 16., 7 / 16., 11 / 16.),
          new Vector3(10 / 16., 12 / 16., 11 / 16.),
          new Vector4(11 / 16., 15 / 16., 7 / 16., 12 / 16.)),

      // back
      new Quad(new Vector3(6 / 16., 7 / 16., 15 / 16.), new Vector3(10 / 16., 7 / 16., 15 / 16.),
          new Vector3(6 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 11 / 16., 7 / 16., 12 / 16.)),

      // left
      new Quad(new Vector3(6 / 16., 7 / 16., 11 / 16.), new Vector3(6 / 16., 7 / 16., 15 / 16.),
          new Vector3(6 / 16., 12 / 16., 11 / 16.),
          new Vector4(11 / 16., 15 / 16., 7 / 16., 12 / 16.)),

      // right
      new Quad(new Vector3(10 / 16., 7 / 16., 15 / 16.), new Vector3(10 / 16., 7 / 16., 11 / 16.),
          new Vector3(10 / 16., 12 / 16., 15 / 16.),
          new Vector4(15 / 16., 11 / 16., 7 / 16., 12 / 16.)),

      // top
      new Quad(new Vector3(10 / 16., 12 / 16., 11 / 16.),
          new Vector3(6 / 16., 12 / 16., 11 / 16.), new Vector3(10 / 16., 12 / 16., 15 / 16.),
          new Vector4(4 / 16., 0, 12 / 16., 1)),

      // bottom
      new Quad(new Vector3(6 / 16., 7 / 16., 11 / 16.), new Vector3(10 / 16., 7 / 16., 11 / 16.),
          new Vector3(6 / 16., 7 / 16., 15 / 16.), new Vector4(0, 4 / 16., 12 / 16., 1)),
  };
  private static final Quad stemNorth =
      new DoubleSidedQuad(new Vector3(.5, 12 / 16., .5), new Vector3(.5, 12 / 16., 1),
          new Vector3(.5, 1, .5), new Vector4(.5, 1, 12 / 16., 1));

  private static final Quad[][][] fruit = new Quad[3][4][];
  private static final Quad[] stem = new Quad[4];

  static {
    fruit[0][0] = small;
    fruit[1][0] = medium;
    fruit[2][0] = large;

    stem[0] = stemNorth;
    for (int i = 1; i < 4; ++i) {
      stem[i] = stem[i - 1].transform(Transform.NONE.rotateY());
      fruit[0][i] = Model.rotateY(fruit[0][i - 1]);
      fruit[1][i] = Model.rotateY(fruit[1][i - 1]);
      fruit[2][i] = Model.rotateY(fruit[2][i - 1]);
    }
  }

  private static final Texture[] tex =
      {Texture.cocoaPlantSmall, Texture.cocoaPlantMedium, Texture.cocoaPlantLarge};

  public static boolean intersect(Ray ray) {
    int data = 0xF & (ray.getCurrentData() >> BlockData.OFFSET);
    int age = data >> 2;
    int direction = 3 & data;
    return intersect(ray, direction, age);
  }

  public static boolean intersect(Ray ray, int facing, int age) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : fruit[age][facing]) {
      if (quad.intersect(ray)) {
        tex[age].getColor(ray);
        ray.color.w = 1;
        ray.t = ray.tNext;
        ray.setN(quad.n);
        hit = true;
      }
    }
    if (stem[facing].intersect(ray)) {
      float[] color = tex[age].getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        ray.t = ray.tNext;
        Vector3 n = new Vector3(stem[facing].n);
        n.scale(-QuickMath.signum(ray.d.dot(stem[facing].n)));
        ray.setN(n);
        hit = true;
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

}
