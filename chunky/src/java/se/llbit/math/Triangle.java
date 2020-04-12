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
package se.llbit.math;

/**
 * A class to test intersection against a three-dimensional,
 * non-degenerate triangle.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Triangle {

  /**
   * Normal vector
   */
  public final Vector3 n;

  private final Vector3 o;
  private final Vector3 u;
  private final Vector3 v;

  private final double d;
  private final double uv;
  private final double uu;
  private final double vv;
  private final double uv2;

  /**
   * Build a triangle from corner vertices.
   */
  public Triangle(Vector3 v0, Vector3 v1, Vector3 v2) {
    o = new Vector3(v0);

    n = new Vector3();
    u = new Vector3();
    v = new Vector3();

    u.sub(v1, o);
    v.sub(v2, o);

    n.cross(u, v);
    n.normalize();

    d = -n.dot(o);

    uv = u.dot(v);
    uu = u.dot(u);
    vv = v.dot(v);
    uv2 = uv * uv;
  }

  /** Copy a triangle with offset. */
  public Triangle(Triangle other, Vector3 offset) {
    n = other.n;
    o = new Vector3(other.o);
    o.add(offset);
    u = other.u;
    v = other.v;
    d = other.d;
    uv = other.uv;
    uu = other.uu;
    vv = other.vv;
    uv2 = other.uv2;
  }

  /**
   * Find intersection between the ray and this triangle
   *
   * @return <code>true</code> if the ray intersects the triangle
   */
  public boolean intersect(Ray ray) {
    double ix = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
    double iy = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
    double iz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);

    // test that the ray is heading toward the plane
    double denom = ray.d.dot(n);
    if (QuickMath.abs(denom) > Ray.EPSILON) {

      // test for intersection with the plane at origin
      double t = -(ix * n.x + iy * n.y + iz * n.z + d) / denom;
      if (t > -Ray.EPSILON && t < ray.t) {

        // plane intersection confirmed
        // translate to get hit point relative to the triangle origin
        ix = ix + ray.d.x * t - o.x;
        iy = iy + ray.d.y * t - o.y;
        iz = iz + ray.d.z * t - o.z;

        double wu = ix * u.x + iy * u.y + iz * u.z;
        double wv = ix * v.x + iy * v.y + iz * v.z;
        double si = (uv * wv - vv * wu) / (uv2 - uu * vv);
        double ti = (uv * wu - uu * wv) / (uv2 - uu * vv);
        if ((si >= 0) && (ti >= 0) && (si + ti <= 1)) {
          ray.tNext = t;
          ray.u = si;
          ray.v = ti;
          return true;
        }
      }
    }

    return false;
  }
}
