/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.math;

import java.util.Random;

/**
 * Axis Aligned Bounding Box for collision detection and Bounding Volume Hierarchy
 * construction.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AABB {

  public double xmin;
  public double xmax;
  public double ymin;
  public double ymax;
  public double zmin;
  public double zmax;

  public double surfaceArea;

  public AABB(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.ymax = ymax;
    this.zmin = zmin;
    this.zmax = zmax;

    double x = xmax - xmin;
    double y = ymax - ymin;
    double z = zmax - zmin;
    this.surfaceArea = 2 * (y * z + x * z + x * y);
  }

  public void sample(Vector3 loc, Random rand) {
    double[] vec = new double[3];
    int face = rand.nextInt(6);
    int perp = face % 3;

    vec[perp] = face > 2 ? 1 : 0;
    vec[(perp + 1) % 3] = rand.nextDouble();
    vec[(perp + 1) % 3] = rand.nextDouble();

    vec[0] *= xmax - xmin;
    vec[1] *= ymax - ymin;
    vec[2] *= zmax - zmin;

    vec[0] += xmin;
    vec[1] += ymin;
    vec[2] += zmin;

    loc.set(vec[0], vec[1], vec[2]);
  }

  /**
   * Find intersection between the given ray and this AABB.
   *
   * @return <code>true</code> if the ray intersects this AABB
   */
  public boolean intersect(Ray ray) {
    double ix = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
    double iy = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
    double iz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);
    double t;
    double u, v;
    boolean hit = false;

    ray.tNext = ray.t;

    t = (xmin - ix) / ray.d.x;
    if (t < ray.tNext && t > -Ray.EPSILON) {
      u = iz + ray.d.z * t;
      v = iy + ray.d.y * t;
      if (u >= zmin && u <= zmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        ray.tNext = t;
        ray.u = u;
        ray.v = v;
        ray.setNormal(-1, 0, 0);
      }
    }
    t = (xmax - ix) / ray.d.x;
    if (t < ray.tNext && t > -Ray.EPSILON) {
      u = iz + ray.d.z * t;
      v = iy + ray.d.y * t;
      if (u >= zmin && u <= zmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        ray.tNext = t;
        ray.u = 1 - u;
        ray.v = v;
        ray.setNormal(1, 0, 0);
      }
    }
    t = (ymin - iy) / ray.d.y;
    if (t < ray.tNext && t > -Ray.EPSILON) {
      u = ix + ray.d.x * t;
      v = iz + ray.d.z * t;
      if (u >= xmin && u <= xmax &&
          v >= zmin && v <= zmax) {
        hit = true;
        ray.tNext = t;
        ray.u = u;
        ray.v = v;
        ray.setNormal(0, -1, 0);
      }
    }
    t = (ymax - iy) / ray.d.y;
    if (t < ray.tNext && t > -Ray.EPSILON) {
      u = ix + ray.d.x * t;
      v = iz + ray.d.z * t;
      if (u >= xmin && u <= xmax &&
          v >= zmin && v <= zmax) {
        hit = true;
        ray.tNext = t;
        ray.u = u;
        ray.v = v;
        ray.setNormal(0, 1, 0);
      }
    }
    t = (zmin - iz) / ray.d.z;
    if (t < ray.tNext && t > -Ray.EPSILON) {
      u = ix + ray.d.x * t;
      v = iy + ray.d.y * t;
      if (u >= xmin && u <= xmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        ray.tNext = t;
        ray.u = 1 - u;
        ray.v = v;
        ray.setNormal(0, 0, -1);
      }
    }
    t = (zmax - iz) / ray.d.z;
    if (t < ray.tNext && t > -Ray.EPSILON) {
      u = ix + ray.d.x * t;
      v = iy + ray.d.y * t;
      if (u >= xmin && u <= xmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        ray.tNext = t;
        ray.u = u;
        ray.v = v;
        ray.setNormal(0, 0, 1);
      }
    }
    return hit;
  }

  /**
   * Only test for intersection and find distance to intersection.
   *
   * @return {@code true} if there is an intersection
   */
  public boolean quickIntersect(Ray ray) {
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

    if (tNear < tFar + Ray.EPSILON && tNear >= 0 && tNear < ray.t) {
      ray.tNext = tNear;
      return true;
    } else {
      return false;
    }
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
   * Test if a ray intersects this AABB.
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

    return tNear < tFar + Ray.EPSILON && tFar > 0;
  }

  /**
   * @return AABB rotated about the Y axis
   */
  public AABB getYRotated() {
    return new AABB(1 - zmax, 1 - zmin, ymin, ymax, xmin, xmax);
  }

  /**
   * @param x X translation
   * @param y Y translation
   * @param z Z translation
   * @return A translated copy of this AABB
   */
  public AABB getTranslated(double x, double y, double z) {
    return new AABB(xmin + x, xmax + x, ymin + y, ymax + y, zmin + z, zmax + z);
  }

  /**
   * @return an AABB which encloses all given vertices
   */
  public static AABB bounds(Vector3... c) {
    double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY,
        ymin = Double.POSITIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY,
        zmin = Double.POSITIVE_INFINITY, zmax = Double.NEGATIVE_INFINITY;
    for (Vector3 v : c) {
      if (v.x < xmin) {
        xmin = v.x;
      }
      if (v.x > xmax) {
        xmax = v.x;
      }
      if (v.y < ymin) {
        ymin = v.y;
      }
      if (v.y > ymax) {
        ymax = v.y;
      }
      if (v.z < zmin) {
        zmin = v.z;
      }
      if (v.z > zmax) {
        zmax = v.z;
      }
    }
    return new AABB(xmin, xmax, ymin, ymax, zmin, zmax);
  }

  public AABB expand(AABB bb) {
    return new AABB(Math.min(xmin, bb.xmin), Math.max(xmax, bb.xmax), Math.min(ymin, bb.ymin),
        Math.max(ymax, bb.ymax), Math.min(zmin, bb.zmin), Math.max(zmax, bb.zmax));
  }
}
