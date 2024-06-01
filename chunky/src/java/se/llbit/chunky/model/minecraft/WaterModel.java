/*
 * Copyright (c) 2012-2023 Chunky contributors
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
package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.StillWaterShader;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray2;
import se.llbit.math.Triangle;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.util.VectorUtil;

/**
 * A water block. The height of the top water block is slightly
 * lower than a regular block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class WaterModel {

  private static final Quad[] fullBlock = {
      // bottom
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1), true),
      // top
      new Quad(new Vector3(0, 1, 0), new Vector3(1, 1, 0), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 1), true),
      // west
      new Quad(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1), true),
      // east
      new Quad(new Vector3(1, 0, 0), new Vector3(1, 1, 0), new Vector3(1, 0, 1),
          new Vector4(0, 1, 0, 1), true),
      // north
      new Quad(new Vector3(0, 1, 0), new Vector3(1, 1, 0), new Vector3(0, 0, 0),
          new Vector4(0, 1, 0, 0), true),
      // south
      new Quad(new Vector3(0, 1, 1), new Vector3(1, 1, 1), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1), true),};

  static final Quad bottom =
      new Quad(new Vector3(0, 0, 0), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(0, 1, 0, 1), true);
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
  static final double[] height =
      {14 / 16., 12.25 / 16., 10.5 / 16, 8.75 / 16, 7. / 16, 5.25 / 16, 3.5 / 16, 1.75 / 16};

  private static final float[] normalMap;
  private static final int normalMapW;

  /**
   * Block data offset for water above flag
   */
  private static final int FULL_BLOCK = 12;

  static {
    // precompute normal map
    Texture waterHeight = new Texture("water-height");
    normalMapW = waterHeight.getWidth();
    normalMap = new float[normalMapW*normalMapW*2];
    for (int u = 0; u < normalMapW; ++u) {
      for (int v = 0; v < normalMapW; ++v) {

        float hx0 = (waterHeight.getColorWrapped(u, v) & 0xFF) / 255.f;
        float hx1 = (waterHeight.getColorWrapped(u + 1, v) & 0xFF) / 255.f;
        float hz0 = (waterHeight.getColorWrapped(u, v) & 0xFF) / 255.f;
        float hz1 = (waterHeight.getColorWrapped(u, v + 1) & 0xFF) / 255.f;
        normalMap[(u*normalMapW + v) * 2] = hx1 - hx0;
        normalMap[(u*normalMapW + v) * 2 + 1] = hz1 - hz0;
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

  public static boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    IntersectionRecord intersectionTest = new IntersectionRecord();

    int data = intersectionRecord.material instanceof Water ? ((Water) intersectionRecord.material).data : 0;
    int isFull = (data >> FULL_BLOCK) & 1;
    //int level = data >> 8;

    if (isFull != 0) {
      boolean hit = false;
      for (Quad quad : fullBlock) {
        if (quad.intersect(ray, intersectionTest)) {
          Texture.water.getAvgColorLinear(intersectionRecord.color);
          intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, quad.n));
          hit = true;
        }
      }
      if (hit) {
        intersectionRecord.distance = intersectionTest.distance;
      }
      return hit;
    }

    boolean hit = false;
    if (bottom.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, bottom.n));
      hit = true;
    }

    int c0 = (0xF & (data >> 16)) % 8;
    int c1 = (0xF & (data >> 20)) % 8;
    int c2 = (0xF & (data >> 24)) % 8;
    int c3 = (0xF & (data >> 28)) % 8;
    Triangle triangle = t012[c0][c1][c2];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      hit = true;
    }
    triangle = t230[c2][c3][c0];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      intersectionTest.uv.x = 1 - intersectionTest.uv.x;
      intersectionTest.uv.y = 1 - intersectionTest.uv.y;
      hit = true;
    }
    triangle = westt[c0][c3];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      hit = true;
    }
    triangle = westb[c0];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      intersectionTest.uv.x = 1 - intersectionTest.uv.x;
      intersectionTest.uv.y = 1 - intersectionTest.uv.y;
      hit = true;
    }
    triangle = eastt[c1][c2];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      hit = true;
    }
    triangle = eastb[c1];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      intersectionTest.uv.x = 1 - intersectionTest.uv.x;
      intersectionTest.uv.y = 1 - intersectionTest.uv.y;
      hit = true;
    }
    triangle = southt[c0][c1];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      hit = true;
    }
    triangle = southb[c1];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      intersectionTest.uv.x = 1 - intersectionTest.uv.x;
      intersectionTest.uv.y = 1 - intersectionTest.uv.y;
      hit = true;
    }
    triangle = northt[c2][c3];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      hit = true;
    }
    triangle = northb[c2];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      intersectionTest.uv.x = 1 - intersectionTest.uv.x;
      intersectionTest.uv.y = 1 - intersectionTest.uv.y;
      hit = true;
    }
    if (hit) {
      if (!(scene.getCurrentWaterShader() instanceof StillWaterShader) && intersectionRecord.shadeN.y != 0) {
        scene.getCurrentWaterShader().doWaterShading(ray, intersectionRecord, scene.getAnimationTime());
      }
      Texture.water.getAvgColorLinear(intersectionRecord.color);
      intersectionRecord.distance += intersectionTest.distance;
    }
    return hit;
  }

  public static boolean intersectTop(Ray2 ray, IntersectionRecord intersectionRecord) {
    IntersectionRecord intersectionTest = new IntersectionRecord();

    int data = intersectionRecord.material instanceof Water ? ((Water) intersectionRecord.material).data : 0;

    boolean hit = false;
    int c0 = (0xF & (data >> 16)) % 8;
    int c1 = (0xF & (data >> 20)) % 8;
    int c2 = (0xF & (data >> 24)) % 8;
    int c3 = (0xF & (data >> 28)) % 8;
    Triangle triangle = t012[c0][c1][c2];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      hit = true;
    }
    triangle = t230[c2][c3][c0];
    if (triangle.intersect(ray, intersectionTest)) {
      intersectionRecord.setNormal(VectorUtil.orientNormal(ray.d, triangle.n));
      intersectionTest.uv.x = 1 - intersectionTest.uv.x;
      intersectionTest.uv.y = 1 - intersectionTest.uv.y;
      hit = true;
    }
    if (hit) {
      Texture.water.getAvgColorLinear(intersectionRecord.color);
      intersectionRecord.distance += intersectionTest.distance;
    }
    return hit;
  }

  /**
   * Displace the normal using the water displacement map.
   */
  public static void doWaterDisplacement(Ray2 ray, IntersectionRecord intersectionRecord) {
    int w = (1 << 4);
    double x = ray.o.x / w - QuickMath.floor(ray.o.x / w);
    double z = ray.o.z / w - QuickMath.floor(ray.o.z / w);
    int u = (int) (x * normalMapW - Constants.EPSILON);
    int v = (int) ((1 - z) * normalMapW - Constants.EPSILON);
    Vector3 n = new Vector3(normalMap[(u*normalMapW + v) * 2], .15f, normalMap[(u*normalMapW + v) * 2 + 1]);
    w = (1 << 1);
    x = ray.o.x / w - QuickMath.floor(ray.o.x / w);
    z = ray.o.z / w - QuickMath.floor(ray.o.z / w);
    u = (int) (x * normalMapW - Constants.EPSILON);
    v = (int) ((1 - z) * normalMapW - Constants.EPSILON);
    n.x += normalMap[(u*normalMapW + v) * 2] / 2;
    n.z += normalMap[(u*normalMapW + v) * 2 + 1] / 2;
    n.normalize();
    intersectionRecord.shadeN.set(n.x, n.y, n.z);
  }
}
