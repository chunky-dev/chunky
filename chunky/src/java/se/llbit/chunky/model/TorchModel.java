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
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.UVTriangle;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TorchModel {
  // facing west
  private static final Quad[] quads = {
      // west
      new Quad(new Vector3(15 / 16., 3 / 16., 0), new Vector3(15 / 16., 3 / 16., 1),
          new Vector3((11 - 12 / 10.) / 16., 1, 0), new Vector4(0, 1, 0, 13 / 16.)),

      // east
      new Quad(new Vector3((13 - 12 / 10.) / 16., 1, 0), new Vector3((13 - 12 / 10.) / 16., 1, 1),
          new Vector3(17 / 16., 3 / 16., 0), new Vector4(1, 0, 13 / 16., 0)),

      // top
      new Quad(new Vector3(13 / 16., 13 / 16., 9 / 16.), new Vector3(13 / 16., 13 / 16., 7 / 16.),
          new Vector3(11 / 16., 13 / 16., 9 / 16.),
          new Vector4(9 / 16., 7 / 16., 10 / 16., 8 / 16.)),

      // bottom
      new Quad(new Vector3(15 / 16., 3 / 16., 7 / 16.), new Vector3(17 / 16., 3 / 16., 7 / 16.),
          new Vector3(15 / 16., 3 / 16., 9 / 16.),
          new Vector4(7 / 16., 9 / 16., 0 / 16., 2 / 16.))};

  // facing west
  private static final UVTriangle[] uvtriangles = {
      // facing south
      new UVTriangle(new Vector3(8 / 16., 3 / 16., 9 / 16.),
          new Vector3(24 / 16., 3 / 16., 9 / 16.), new Vector3((4 - 12 / 10.) / 16., 1, 9 / 16.),
          new Vector2(0, 0), new Vector2(1, 0), new Vector2(0., 13 / 16.)),
      new UVTriangle(new Vector3((20 - 12 / 10.) / 16., 1, 9 / 16.),
          new Vector3((4 - 12 / 10.) / 16., 1, 9 / 16.), new Vector3(24 / 16., 3 / 16., 9 / 16.),
          new Vector2(1, 13 / 16.), new Vector2(0, 13 / 16.), new Vector2(1, 0)),

      // facing north
      new UVTriangle(new Vector3(24 / 16., 3 / 16., 7 / 16.),
          new Vector3(8 / 16., 3 / 16., 7 / 16.), new Vector3((4 - 12 / 10.) / 16., 1, 7 / 16.),
          new Vector2(1, 0), new Vector2(0, 0), new Vector2(0, 13 / 16.)),
      new UVTriangle(new Vector3((4 - 12 / 10.) / 16., 1, 7 / 16.),
          new Vector3((20 - 12 / 10.) / 16., 1, 7 / 16.), new Vector3(24 / 16., 3 / 16., 7 / 16.),
          new Vector2(0, 13 / 16.), new Vector2(1, 13 / 16.), new Vector2(1, 0))};

  private static Quad[] onGround =
      {new Quad(new Vector3(.75, 0, .4375), new Vector3(.25, 0, .4375),
          new Vector3(.75, 1, .4375), new Vector4(.75, .25, 0, 1)),

          new Quad(new Vector3(.25, 0, .5625), new Vector3(.75, 0, .5625),
              new Vector3(.25, 1, .5625), new Vector4(.25, .75, 0, 1)),

          new Quad(new Vector3(.4375, 0, .25), new Vector3(.4375, 0, .75),
              new Vector3(.4375, 1, .25), new Vector4(.25, .75, 0, 1)),

          new Quad(new Vector3(.5625, 0, .75), new Vector3(.5625, 0, .25),
              new Vector3(.5625, 1, .75), new Vector4(.75, .25, 0, 1)),

          // top
          new Quad(new Vector3(.4375, .625, .5625), new Vector3(.5625, .625, .5625),
              new Vector3(.4375, .625, .4375), new Vector4(.4375, .5625, .5, .625)),};

  private static Quad[][] rotQuads = new Quad[6][];
  private static UVTriangle[][] rotTriangles = new UVTriangle[6][];

  static {
    rotQuads[0] = new Quad[0];
    rotTriangles[0] = new UVTriangle[0];

    // pointing west
    rotQuads[2] = quads;
    rotTriangles[2] = uvtriangles;

    // pointing north
    rotQuads[4] = Model.rotateY(rotQuads[2]);
    rotTriangles[4] = Model.rotateY(rotTriangles[2]);

    // pointing east
    rotQuads[1] = Model.rotateY(rotQuads[4]);
    rotTriangles[1] = Model.rotateY(rotTriangles[4]);

    // pointing south
    rotQuads[3] = Model.rotateY(rotQuads[1]);
    rotTriangles[3] = Model.rotateY(rotTriangles[1]);

    // on ground
    rotQuads[5] = onGround;
    rotTriangles[5] = new UVTriangle[0];

  }

  public static boolean intersect(Ray ray, Texture texture) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    float[] color = null;
    int rot = ray.getBlockData() % 6;
    for (Quad quad : rotQuads[rot]) {
      if (quad.intersect(ray)) {
        float[] c = texture.getColor(ray.u, ray.v);
        if (c[3] > Ray.EPSILON) {
          color = c;
          ray.n.set(quad.n);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    }
    for (UVTriangle triangle : rotTriangles[rot]) {
      if (triangle.intersect(ray)) {
        float[] c = texture.getColor(ray.u, ray.v);
        if (c[3] > Ray.EPSILON) {
          color = c;
          ray.n.set(triangle.n);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    }
    if (hit) {
      double px = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET) + ray.d.x * ray.tNext;
      double py = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET) + ray.d.y * ray.tNext;
      double pz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET) + ray.d.z * ray.tNext;
      if (px >= 0 && px <= 1 && py >= 0 && py <= 1 && pz >= 0 && pz <= 1) {
        ray.color.set(color);
        ray.distance += ray.t;
        ray.o.scaleAdd(ray.t, ray.d);
        return true;
      }
    }
    return false;
  }
}
