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
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Triangle;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * A water block. The height of the top water block is slightly
 * lower than a regular block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class WaterModel {

  private static Quad[] fullBlock = {
      // bottom
      new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
      // top
      new DoubleSidedQuad(new Vector3(0, 1, 0), new Vector3(1, 1, 0), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1)),
      // west
      new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),
      // east
      new DoubleSidedQuad(new Vector3(1, 0, 0), new Vector3(1, 1, 0), new Vector3(1, 0, 1),
          new Vector4(0, 1, 0, 1)),
      // north
      new DoubleSidedQuad(new Vector3(0, 1, 0), new Vector3(1, 1, 0), new Vector3(0, 0, 0),
          new Vector4(0, 1, 0, 0)),
      // south
      new DoubleSidedQuad(new Vector3(0, 1, 1), new Vector3(1, 1, 1), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1)),};

  static final DoubleSidedQuad bot =
      new DoubleSidedQuad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1));
  static final Triangle[][][] t012 = new Triangle[8][8][8];
  static final Triangle[][][] t230 = new Triangle[8][8][8];
  static final Triangle[][] westt = new Triangle[8][8];
  static final Triangle[] westb = new Triangle[8];
  static final Triangle[][] northt = new Triangle[8][8];
  static final Triangle[] northb = new Triangle[8];
  static final Triangle[][] eastt = new Triangle[8][8];
  static final Triangle[] eastb = new Triangle[8];
  static final Triangle[][] southt = new Triangle[8][8];
  static final Triangle[] southb = new Triangle[8];

  /**
   * Water height levels
   */
  static final double height[] =
      {14 / 16., 12.25 / 16., 10.5 / 16, 8.75 / 16, 7. / 16, 5.25 / 16, 3.5 / 16, 1.75 / 16};

  private static final float[][][] normalMap;
  private static final int normalMapW;

  /**
   * Block data offset for water above flag
   */
  private static final int FULL_BLOCK = 12;

  static {
    // precompute normal map
    Texture waterHeight = new Texture("water-height");
    normalMapW = waterHeight.getWidth();
    normalMap = new float[normalMapW][normalMapW][2];
    for (int u = 0; u < normalMapW; ++u) {
      for (int v = 0; v < normalMapW; ++v) {

        float hx0 = (waterHeight.getColorWrapped(u, v) & 0xFF) / 255.f;
        float hx1 = (waterHeight.getColorWrapped(u + 1, v) & 0xFF) / 255.f;
        float hz0 = (waterHeight.getColorWrapped(u, v) & 0xFF) / 255.f;
        float hz1 = (waterHeight.getColorWrapped(u, v + 1) & 0xFF) / 255.f;
        normalMap[u][v][0] = hx1 - hx0;
        normalMap[u][v][1] = hz1 - hz0;
      }
    }

    // precompute water triangles
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c1 = height[j];
        for (int k = 0; k < 8; ++k) {
          double c2 = height[k];
          t012[i][j][k] =
              new Triangle(new Vector3(1, c1, 1), new Vector3(1, c2, 0), new Vector3(0, c0, 1));
        }
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c2 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c3 = height[j];
        for (int k = 0; k < 8; ++k) {
          double c0 = height[k];
          t230[i][j][k] =
              new Triangle(new Vector3(0, c3, 0), new Vector3(0, c0, 1), new Vector3(1, c2, 0));
        }
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c3 = height[j];
        westt[i][j] =
            new Triangle(new Vector3(0, c3, 0), new Vector3(0, 0, 0), new Vector3(0, c0, 1));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      westb[i] = new Triangle(new Vector3(0, 0, 1), new Vector3(0, c0, 1), new Vector3(0, 0, 0));
    }
    for (int i = 0; i < 8; ++i) {
      double c1 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c2 = height[j];
        eastt[i][j] =
            new Triangle(new Vector3(1, c2, 0), new Vector3(1, c1, 1), new Vector3(1, 0, 0));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c1 = height[i];
      eastb[i] = new Triangle(new Vector3(1, c1, 1), new Vector3(1, 0, 1), new Vector3(1, 0, 0));
    }
    for (int i = 0; i < 8; ++i) {
      double c2 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c3 = height[j];
        northt[i][j] =
            new Triangle(new Vector3(0, c3, 0), new Vector3(1, c2, 0), new Vector3(0, 0, 0));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c2 = height[i];
      northb[i] =
          new Triangle(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, c2, 0));
    }
    for (int i = 0; i < 8; ++i) {
      double c0 = height[i];
      for (int j = 0; j < 8; ++j) {
        double c1 = height[j];
        southt[i][j] =
            new Triangle(new Vector3(0, c0, 1), new Vector3(0, 0, 1), new Vector3(1, c1, 1));
      }
    }
    for (int i = 0; i < 8; ++i) {
      double c1 = height[i];
      southb[i] =
          new Triangle(new Vector3(1, 0, 1), new Vector3(1, c1, 1), new Vector3(0, 0, 1));
    }
  }

  public static boolean intersect(Ray ray) {
    ray.t = Double.POSITIVE_INFINITY;

    int data = ray.getCurrentData();
    int isFull = (data >> FULL_BLOCK) & 1;
    //int level = data >> 8;

    if (isFull != 0) {
      boolean hit = false;
      for (Quad quad : fullBlock) {
        if (quad.intersect(ray)) {
          Texture.water.getAvgColorLinear(ray.color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          ray.n.scale(QuickMath.signum(-ray.d.dot(quad.n)));
          hit = true;
        }
      }
      if (hit) {
        ray.distance += ray.t;
        ray.o.scaleAdd(ray.t, ray.d);
      }
      return hit;
    }

    boolean hit = false;
    if (bot.intersect(ray)) {
      ray.n.set(bot.n);
      ray.n.scale(-QuickMath.signum(ray.d.dot(bot.n)));
      ray.t = ray.tNext;
      hit = true;
    }

    int c0 = (0xF & (data >> 16)) % 8;
    int c1 = (0xF & (data >> 20)) % 8;
    int c2 = (0xF & (data >> 24)) % 8;
    int c3 = (0xF & (data >> 28)) % 8;
    Triangle triangle = t012[c0][c1][c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = t230[c2][c3][c0];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = westt[c0][c3];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = westb[c0];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = eastt[c1][c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = eastb[c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = southt[c0][c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = southb[c1];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    triangle = northt[c2][c3];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = northb[c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    if (hit) {
      Texture.water.getAvgColorLinear(ray.color);
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  public static boolean intersectTop(Ray ray) {
    ray.t = Double.POSITIVE_INFINITY;

    int data = ray.getCurrentData();

    boolean hit = false;
    int c0 = (0xF & (data >> 16)) % 8;
    int c1 = (0xF & (data >> 20)) % 8;
    int c2 = (0xF & (data >> 24)) % 8;
    int c3 = (0xF & (data >> 28)) % 8;
    Triangle triangle = t012[c0][c1][c2];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      hit = true;
    }
    triangle = t230[c2][c3][c0];
    if (triangle.intersect(ray)) {
      ray.n.set(triangle.n);
      ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
      ray.t = ray.tNext;
      ray.u = 1 - ray.u;
      ray.v = 1 - ray.v;
      hit = true;
    }
    if (hit) {
      Texture.water.getAvgColorLinear(ray.color);
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  /**
   * Displace the normal using the water displacement map.
   */
  public static void doWaterDisplacement(Ray ray) {
    int w = (1 << 4);
    double x = ray.o.x / w - QuickMath.floor(ray.o.x / w);
    double z = ray.o.z / w - QuickMath.floor(ray.o.z / w);
    int u = (int) (x * normalMapW - Ray.EPSILON);
    int v = (int) ((1 - z) * normalMapW - Ray.EPSILON);
    ray.n.set(normalMap[u][v][0], .15f, normalMap[u][v][1]);
    w = (1 << 1);
    x = ray.o.x / w - QuickMath.floor(ray.o.x / w);
    z = ray.o.z / w - QuickMath.floor(ray.o.z / w);
    u = (int) (x * normalMapW - Ray.EPSILON);
    v = (int) ((1 - z) * normalMapW - Ray.EPSILON);
    ray.n.x += normalMap[u][v][0] / 2;
    ray.n.z += normalMap[u][v][1] / 2;
    ray.n.normalize();
  }
}
