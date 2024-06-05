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

  public void sampleFace(int face, Vector3 loc, Random rand) {
    double[] v = new double[3];
    face %= 6;
    int axis = face % 3;
    v[axis] = face > 2 ? 1 : 0;
    v[(axis + 1) % 3] = rand.nextDouble();
    v[(axis + 2) % 3] = rand.nextDouble();

    v[0] *= xmax - xmin;
    v[1] *= ymax - ymin;
    v[2] *= zmax - zmin;

    v[0] += xmin;
    v[1] += ymin;
    v[2] += zmin;

    loc.set(v[0], v[1], v[2]);
  }

  public double faceSurfaceArea(int face) {
    double[] minC = new double[3];
    double[] maxC = new double[3];

    minC[0] = xmin;
    minC[1] = ymin;
    minC[2] = zmin;

    maxC[0] = xmax;
    maxC[1] = ymax;
    maxC[2] = zmax;

    int a1 = (face + 1) % 3;
    int a2 = (face + 2) % 3;

    double sa = (maxC[a1] - minC[a1]) * (maxC[a2] - minC[a2]);

    return Math.abs(sa);
  }

  /**
   * Find intersection between the given ray and this AABB.
   *
   * @return <code>true</code> if the ray intersects this AABB
   */
  public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord) {
    double ix = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Constants.OFFSET);
    double iy = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Constants.OFFSET);
    double iz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Constants.OFFSET);
    double t;
    double u, v;
    boolean hit = false;

    t = (xmin - ix) / ray.d.x;
    if (t < intersectionRecord.distance + Constants.OFFSET && t > -Constants.EPSILON) {
      u = iz + ray.d.z * t;
      v = iy + ray.d.y * t;
      if (u >= zmin && u <= zmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        intersectionRecord.distance = t;
        intersectionRecord.uv.x = u;
        intersectionRecord.uv.y = v;
        intersectionRecord.setNormal(-1, 0, 0);
      }
    }
    t = (xmax - ix) / ray.d.x;
    if (t < intersectionRecord.distance + Constants.OFFSET && t > -Constants.EPSILON) {
      u = iz + ray.d.z * t;
      v = iy + ray.d.y * t;
      if (u >= zmin && u <= zmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        intersectionRecord.distance = t;
        intersectionRecord.uv.x = 1 - u;
        intersectionRecord.uv.y = v;
        intersectionRecord.setNormal(1, 0, 0);
      }
    }
    t = (ymin - iy) / ray.d.y;
    if (t < intersectionRecord.distance + Constants.OFFSET && t > -Constants.EPSILON) {
      u = ix + ray.d.x * t;
      v = iz + ray.d.z * t;
      if (u >= xmin && u <= xmax &&
          v >= zmin && v <= zmax) {
        hit = true;
        intersectionRecord.distance = t;
        intersectionRecord.uv.x = u;
        intersectionRecord.uv.y = v;
        intersectionRecord.setNormal(0, -1, 0);
      }
    }
    t = (ymax - iy) / ray.d.y;
    if (t < intersectionRecord.distance + Constants.OFFSET && t > -Constants.EPSILON) {
      u = ix + ray.d.x * t;
      v = iz + ray.d.z * t;
      if (u >= xmin && u <= xmax &&
          v >= zmin && v <= zmax) {
        hit = true;
        intersectionRecord.distance = t;
        intersectionRecord.uv.x = u;
        intersectionRecord.uv.y = v;
        intersectionRecord.setNormal(0, 1, 0);
      }
    }
    t = (zmin - iz) / ray.d.z;
    if (t < intersectionRecord.distance + Constants.OFFSET && t > -Constants.EPSILON) {
      u = ix + ray.d.x * t;
      v = iy + ray.d.y * t;
      if (u >= xmin && u <= xmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        intersectionRecord.distance = t;
        intersectionRecord.uv.x = 1 - u;
        intersectionRecord.uv.y = v;
        intersectionRecord.setNormal(0, 0, -1);
      }
    }
    t = (zmax - iz) / ray.d.z;
    if (t < intersectionRecord.distance + Constants.OFFSET && t > -Constants.EPSILON) {
      u = ix + ray.d.x * t;
      v = iy + ray.d.y * t;
      if (u >= xmin && u <= xmax &&
          v >= ymin && v <= ymax) {
        hit = true;
        intersectionRecord.distance = t;
        intersectionRecord.uv.x = u;
        intersectionRecord.uv.y = v;
        intersectionRecord.setNormal(0, 0, 1);
      }
    }
    return hit;
  }

  /**
   * Only test for intersection and find distance to intersection.
   *
   * @return {@code true} if there is an intersection
   */
  public double quickIntersect(Ray2 ray) {
    double tx1 = (xmin - ray.o.x) / ray.d.x;
    double tx2 = (xmax - ray.o.x) / ray.d.x;

    double ty1 = (ymin - ray.o.y) / ray.d.y;
    double ty2 = (ymax - ray.o.y) / ray.d.y;

    double tz1 = (zmin - ray.o.z) / ray.d.z;
    double tz2 = (zmax - ray.o.z) / ray.d.z;

    double tmin = Math.max(Math.max(Math.min(tx1, tx2), Math.min(ty1, ty2)), Math.min(tz1, tz2));
    double tmax = Math.min(Math.min(Math.max(tx1, tx2), Math.max(ty1, ty2)), Math.max(tz1, tz2));

    if (tmax < tmin) {
      return Double.NaN;
    } else {
      return tmin;
    }


    /*double t1, t2;
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

    if (tNear < tFar + Constants.EPSILON && tNear >= 0 && tNear < ray.t) {
      ray.tNext = tNear;
      return true;
    } else {
      return false;
    }*/
  }

  /**
   * Test if point is inside the bounding box.
   *
   * @return true if p is inside this BB.
   */
  public boolean inside(Vector3 p) {
    return (p.x > xmin && p.x < xmax) &&
           (p.y > ymin && p.y < ymax) &&
           (p.z > zmin && p.z < zmax);
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

    return tNear < tFar + Constants.EPSILON && tFar > 0;
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
