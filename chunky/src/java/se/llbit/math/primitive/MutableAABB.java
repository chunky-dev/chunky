/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.math.primitive;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

/**
 * Axis-Aligned Bounding Box. Does not compute intersection normals.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class MutableAABB implements Primitive {
  protected double xmin;
  protected double xmax;
  protected double ymin;
  protected double ymax;
  protected double zmin;
  protected double zmax;

  /**
   * Construct a new AABB with given bounds.
   */
  public MutableAABB(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.ymax = ymax;
    this.zmin = zmin;
    this.zmax = zmax;
  }

  /**
   * Test if a ray intersects this AABB
   *
   * @return {@code true} if there is an intersection
   */
  public boolean hitTest(Ray ray) {
    double t1, t2;
    double tNear = Double.NEGATIVE_INFINITY;
    double tFar = Double.POSITIVE_INFINITY;
    Vector3 d = ray.d;
    Vector3 o = ray.o;

    if (d.x != 0) {
      double rx = 1 / d.x;
      t1 = (xmin - o.x) * rx;
      t2 = (xmax - o.x) * rx;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      tNear = t1;
      tFar = t2;
    }

    if (d.y != 0) {
      double ry = 1 / d.y;
      t1 = (ymin - o.y) * ry;
      t2 = (ymax - o.y) * ry;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      if (t1 > tNear) {
        tNear = t1;
      }
      if (t2 < tFar) {
        tFar = t2;
      }
    }

    if (d.z != 0) {
      double rz = 1 / d.z;
      t1 = (zmin - o.z) * rz;
      t2 = (zmax - o.z) * rz;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      if (t1 > tNear) {
        tNear = t1;
      }
      if (t2 < tFar) {
        tFar = t2;
      }
    }

    return tNear < tFar + Constants.EPSILON && tFar > 0;
  }

  @Override public boolean closestIntersection(Ray ray, IntersectionRecord intersectionRecord, Scene scene, Random random) {
    double t1, t2;
    double tNear = Double.NEGATIVE_INFINITY;
    double tFar = Double.POSITIVE_INFINITY;
    Vector3 d = ray.d;
    Vector3 o = ray.o;

    if (d.x != 0) {
      double rx = 1 / d.x;
      t1 = (xmin - o.x) * rx;
      t2 = (xmax - o.x) * rx;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      tNear = t1;
      tFar = t2;
    }

    if (d.y != 0) {
      double ry = 1 / d.y;
      t1 = (ymin - o.y) * ry;
      t2 = (ymax - o.y) * ry;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      if (t1 > tNear) {
        tNear = t1;
      }
      if (t2 < tFar) {
        tFar = t2;
      }
    }

    if (d.z != 0) {
      double rz = 1 / d.z;
      t1 = (zmin - o.z) * rz;
      t2 = (zmax - o.z) * rz;

      if (t1 > t2) {
        double t = t1;
        t1 = t2;
        t2 = t;
      }

      if (t1 > tNear) {
        tNear = t1;
      }
      if (t2 < tFar) {
        tFar = t2;
      }
    }

    if (tNear < tFar + Constants.EPSILON && tNear >= 0 && tNear < intersectionRecord.distance) {
      intersectionRecord.distance = tNear;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Expand this AABB to enclose the given AABB.
   */
  public void expand(AABB p) {
    if (p.xmin < xmin) {
      xmin = p.xmin;
    }
    if (p.xmax > xmax) {
      xmax = p.xmax;
    }
    if (p.ymin < ymin) {
      ymin = p.ymin;
    }
    if (p.ymax > ymax) {
      ymax = p.ymax;
    }
    if (p.zmin < zmin) {
      zmin = p.zmin;
    }
    if (p.zmax > zmax) {
      zmax = p.zmax;
    }
  }

  @Override public AABB bounds() {
    return new AABB(xmin, xmax, ymin, ymax, zmin, zmax);
  }

  @Override public String toString() {
    return String
        .format("[ %.2f, %.2f, %.2f, %.2f, %.2f, %.2f]", xmin, xmax, ymin, ymax, zmin, zmax);
  }

  /**
   * Test if point is inside the bounding box.
   *
   * @return true if p is inside this BB.
   */
  public boolean inside(Vector3 p) {
    return (p.x >= xmin && p.x <= xmax) &&
        (p.y >= ymin && p.y <= ymax) &&
        (p.z >= zmin && p.z <= zmax);
  }

  /**
   * This is used in BVH construction heuristic.
   * @return surface area of the bounding box
   */
  public double surfaceArea() {
    double x = xmax - xmin;
    double y = ymax - ymin;
    double z = zmax - zmin;
    return 2 * (y * z + x * z + x * y);
  }

}
