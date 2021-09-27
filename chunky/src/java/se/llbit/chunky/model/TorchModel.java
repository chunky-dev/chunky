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

public class TorchModel {

  private static final Quad[] quadsGround = new Quad[]{
      new Quad(
          new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 1 / 16.0, 3 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 16 / 16.0, 0 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )
  };

  private static final Quad[] quadsWall = Model.rotateZ(new Quad[]{
      new Quad(
          new Vector3(-1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(-1 / 16.0, 13.5 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
      ),
      new Quad(
          new Vector3(-1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector3(1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector3(-1 / 16.0, 3.5 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 1 / 16.0, 3 / 16.0)
      ),
      new Quad(
          new Vector3(-1 / 16.0, 19.5 / 16.0, 16 / 16.0),
          new Vector3(-1 / 16.0, 19.5 / 16.0, 0 / 16.0),
          new Vector3(-1 / 16.0, 3.5 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(1 / 16.0, 19.5 / 16.0, 0 / 16.0),
          new Vector3(1 / 16.0, 19.5 / 16.0, 16 / 16.0),
          new Vector3(1 / 16.0, 3.5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(-8 / 16.0, 19.5 / 16.0, 7 / 16.0),
          new Vector3(8 / 16.0, 19.5 / 16.0, 7 / 16.0),
          new Vector3(-8 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 19.5 / 16.0, 9 / 16.0),
          new Vector3(-8 / 16.0, 19.5 / 16.0, 9 / 16.0),
          new Vector3(8 / 16.0, 3.5 / 16.0, 9 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )
  }, Math.toRadians(-22.5), new Vector3(0, 3.5 / 16, 8 / 16.));

  private static final Quad[][] rotatedQuadsWall = new Quad[6][];

  static {
    rotatedQuadsWall[1] = quadsWall; // east
    rotatedQuadsWall[3] = Model.rotateY(rotatedQuadsWall[1]); // south
    rotatedQuadsWall[2] = Model.rotateY(rotatedQuadsWall[3]); // west
    rotatedQuadsWall[4] = Model.rotateY(rotatedQuadsWall[2]); // north
  }

  public static boolean intersect(Ray ray, Texture texture) {
    int rot = ray.getBlockData() % 6;
    return intersect(ray, texture, rot);
  }

  public static boolean intersect(Ray ray, Texture texture, int rot) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    float[] color = null;
    Quad[] quads = rot < 5 ? rotatedQuadsWall[rot] : quadsGround;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] c = texture.getColor(ray.u, ray.v);
        if (c[3] > Ray.EPSILON) {
          color = c;
          ray.setNormal(quad.n);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    }
    if (hit) {
      if (rot < 5) {
        // the wall torch model goes out of the 1x1x1 block range so we need to check if the hit
        // is actually inside of the block and ignore it if it doesn't
        double px = ray.o.x - Math.floor(ray.o.x + ray.d.x * Ray.OFFSET) + ray.d.x * ray.tNext;
        double py = ray.o.y - Math.floor(ray.o.y + ray.d.y * Ray.OFFSET) + ray.d.y * ray.tNext;
        double pz = ray.o.z - Math.floor(ray.o.z + ray.d.z * Ray.OFFSET) + ray.d.z * ray.tNext;
        if (px >= 0 && px <= 1 && py >= 0 && py <= 1 && pz >= 0 && pz <= 1) {
          ray.color.set(color);
          ray.distance += ray.t;
          ray.o.scaleAdd(ray.t, ray.d);
          return true;
        }
      } else {
        ray.color.set(color);
        ray.distance += ray.t;
        ray.o.scaleAdd(ray.t, ray.d);
        return true;
      }
    }
    return false;
  }
}
