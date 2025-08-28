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

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.*;

/**
 * A water block. The height of the top water block is slightly
 * lower than a regular block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class WaterModel {
  public static final double TOP_BLOCK_GAP = 0.125;
  public static final Quad WATER_TOP = new Quad(
    new Vector3(1, 1 - TOP_BLOCK_GAP, 0),
    new Vector3(0, 1 - TOP_BLOCK_GAP, 0),
    new Vector3(1, 1 - TOP_BLOCK_GAP, 1),
    new Vector4(1, 0, 1, 0),
    true);

  public static final AABB NOT_FULL_BLOCK = new AABB(0, 1, 0, 1 - TOP_BLOCK_GAP, 0, 1);

  // Top triangles
  public static final Triangle[][][] t012 = new Triangle[8][8][8];
  public static final Triangle[][][] t230 = new Triangle[8][8][8];

  // Side top and bottom triangles
  public static final Triangle[][] westt = new Triangle[8][8];
  public static final Triangle[] westb = new Triangle[8];
  public static final Triangle[][] northt = new Triangle[8][8];
  public static final Triangle[] northb = new Triangle[8];
  public static final Triangle[][] eastt = new Triangle[8][8];
  public static final Triangle[] eastb = new Triangle[8];
  public static final Triangle[][] southt = new Triangle[8][8];
  public static final Triangle[] southb = new Triangle[8];

  /**
   * Water height levels
   */
  static final double[] height =
      {14 / 16., 12.25 / 16., 10.5 / 16, 8.75 / 16, 7. / 16, 5.25 / 16, 3.5 / 16, 1.75 / 16};

  public static final int CORNER_0 = 0;
  public static final int CORNER_1 = 4;
  public static final int CORNER_2 = 8;
  public static final int CORNER_3 = 12;

  public static final Quad FULL_BLOCK_BOTTOM_SIDE = new Quad(
    new Vector3(0, 0, 0),
    new Vector3(1, 0, 0),
    new Vector3(0, 0, 1),
    new Vector4(0, 1, 0, 1), true);

  static {
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

  public static boolean quickIntersect(Ray ray, IntersectionRecord intersectionRecord, int data) {
    boolean hit = false;

    if (FULL_BLOCK_BOTTOM_SIDE.closestIntersection(ray, intersectionRecord)) {
      intersectionRecord.setNormal(QuadModel.FULL_BLOCK_BOTTOM_SIDE.n);
      hit = true;
    }

    int c0 = (0xF & (data >> CORNER_0)) % 8;
    int c1 = (0xF & (data >> CORNER_1)) % 8;
    int c2 = (0xF & (data >> CORNER_2)) % 8;
    int c3 = (0xF & (data >> CORNER_3)) % 8;

    Triangle triangle = westt[c0][c3];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = westb[c0];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = eastt[c1][c2];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = eastb[c1];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = southt[c0][c1];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = southb[c1];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = northt[c2][c3];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = northb[c2];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = t012[c0][c1][c2];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }
    triangle = t230[c2][c3][c0];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hit = true;
    }

    return hit;
  }

  public static boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene, int data) {
    boolean hit = false;
    
    int c0 = (0xF & (data >> CORNER_0)) % 8;
    int c1 = (0xF & (data >> CORNER_1)) % 8;
    int c2 = (0xF & (data >> CORNER_2)) % 8;
    int c3 = (0xF & (data >> CORNER_3)) % 8;

    if (!ray.getCurrentMedium().isWater()) {
      if (QuadModel.FULL_BLOCK_BOTTOM_SIDE.closestIntersection(ray, intersectionRecord)) {
        intersectionRecord.setNormal(QuadModel.FULL_BLOCK_BOTTOM_SIDE.n);
        hit = true;
      }

      Triangle triangle = westt[c0][c3];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
      triangle = westb[c0];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
      triangle = eastt[c1][c2];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
      triangle = eastb[c1];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
      triangle = southt[c0][c1];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
      triangle = southb[c1];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
      triangle = northt[c2][c3];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
      triangle = northb[c2];
      if (triangle.intersect(ray, intersectionRecord)) {
        intersectionRecord.setNormal(triangle.n);
        hit = true;
      }
    }

    boolean hitTop = false;

    Triangle triangle = t012[c0][c1][c2];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hitTop = true;
    }
    triangle = t230[c2][c3][c0];
    if (triangle.intersect(ray, intersectionRecord)) {
      intersectionRecord.setNormal(triangle.n);
      hitTop = true;
    }
    Block waterPlaneMaterial;
    if (hitTop) {
      if (intersectionRecord.distance > Constants.EPSILON &&
          ray.getCurrentMedium() != (waterPlaneMaterial = scene.waterPlaneMaterial(ray.o)) &&
          !ray.getCurrentMedium().isWater()) {
        intersectionRecord.distance = 0;
        intersectionRecord.material = waterPlaneMaterial;
        waterPlaneMaterial.getColor(intersectionRecord);
        return true;
      }
      // Create a new ray at the intersection position to get the normal.
      Ray testRay = new Ray(ray);
      testRay.o.scaleAdd(intersectionRecord.distance, testRay.d);
      Vector3 shadeNormal = scene.getCurrentWaterShader().doWaterShading(testRay, intersectionRecord, scene.getAnimationTime());
      intersectionRecord.shadeN.set(shadeNormal);

      if (ray.d.dot(intersectionRecord.n) > 0) {
        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
        intersectionRecord.material = Air.INSTANCE;
      }

      return true;

    } else if (hit) {
      return ray.d.dot(intersectionRecord.n) < 0;
    } else if (ray.getCurrentMedium() != (waterPlaneMaterial = scene.waterPlaneMaterial(ray.o)) &&
        !ray.getCurrentMedium().isWater()) {
      intersectionRecord.distance = 0;
      intersectionRecord.material = waterPlaneMaterial;
      waterPlaneMaterial.getColor(intersectionRecord);
      return true;
    } else {
      return false;
    }
  }

  public static boolean isInside(Ray ray, IntersectionRecord intersectionRecord, int data) {
    if (quickIntersect(ray, intersectionRecord, data)) {
      return ray.d.dot(intersectionRecord.n) > 0;
    }
    return false;
  }
}
