/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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

import java.util.Collection;

import se.llbit.chunky.world.Material;
import se.llbit.math.primitive.Primitive;
import se.llbit.math.primitive.TexturedTriangle;


/**
 * A quad.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Quad {

  protected Vector3 o = new Vector3();
  protected Vector3 xv = new Vector3();
  protected Vector3 yv = new Vector3();
  protected Vector4 uv = new Vector4();

  /**
   * Normal vector
   */
  public Vector3 n = new Vector3();

  protected double d, xvl, yvl;

  /**
   * Create new Quad by copying another quad and applying a transform.
   */
  public Quad(Quad other, Transform t) {
    o.set(other.o);
    o.x -= .5;
    o.y -= .5;
    o.z -= .5;
    t.apply(o);
    o.x += .5;
    o.y += .5;
    o.z += .5;
    xv.set(other.xv);
    yv.set(other.yv);
    n.set(other.n);
    t.applyRotScale(xv);
    t.applyRotScale(yv);
    t.applyRotScale(n);
    xvl = other.xvl;
    yvl = other.yvl;
    d = -n.dot(o);
    uv.set(other.uv);
  }

  /**
   * Create transformed Quad
   */
  public Quad(Quad other, Matrix3 t) {
    o.set(other.o);
    o.x -= .5;
    o.y -= .5;
    o.z -= .5;
    t.transform(o);
    o.x += .5;
    o.y += .5;
    o.z += .5;
    xv.set(other.xv);
    yv.set(other.yv);
    n.set(other.n);
    t.transform(xv);
    t.transform(yv);
    t.transform(n);
    xvl = other.xvl;
    yvl = other.yvl;
    d = -n.dot(o);
    uv.set(other.uv);
  }

  /**
   * Create new quad
   *
   * @param v0 Bottom left vector
   * @param v1 Top right vector
   * @param v2 Bottom right vector
   * @param uv Minimum and maximum U/V texture coordinates
   */
  public Quad(Vector3 v0, Vector3 v1, Vector3 v2, Vector4 uv) {
    o.set(v0);
    xv.sub(v1, v0);
    xvl = 1 / xv.lengthSquared();
    yv.sub(v2, v0);
    yvl = 1 / yv.lengthSquared();
    n.cross(xv, yv);
    n.normalize();
    d = -n.dot(o);
    this.uv.set(uv);
    this.uv.y -= uv.x;
    this.uv.w -= uv.z;
  }

  /**
   * Find intersection between the given ray and this quad
   *
   * @return <code>true</code> if the ray intersects this quad
   */
  public boolean intersect(Ray ray) {
    double u, v;

    double ix = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
    double iy = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
    double iz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);

    // Test that the ray is heading toward the plane of this quad.
    double denom = ray.d.dot(n);
    if (denom < -Ray.EPSILON) {

      // Test for intersection with the plane at origin.
      double t = -(ix * n.x + iy * n.y + iz * n.z + d) / denom;
      if (t > -Ray.EPSILON && t < ray.t) {

        // Plane intersection confirmed.
        // Translate to get hit point relative to the quad origin.
        ix = ix + ray.d.x * t - o.x;
        iy = iy + ray.d.y * t - o.y;
        iz = iz + ray.d.z * t - o.z;
        u = ix * xv.x + iy * xv.y + iz * xv.z;
        u *= xvl;
        v = ix * yv.x + iy * yv.y + iz * yv.z;
        v *= yvl;
        if (u >= 0 && u <= 1 && v >= 0 && v <= 1) {
          ray.u = uv.x + u * uv.y;
          ray.v = uv.z + v * uv.w;
          ray.tNext = t;
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return Scaled copy of this quad
   */
  public Quad getScaled(double scale) {
    Matrix3 transform = new Matrix3();
    transform.scale(scale);
    return new Quad(this, transform);
  }

  public void addTriangles(Collection<Primitive> primitives, Material material,
      Transform transform) {
    Vector3 c0 = new Vector3(o);
    Vector3 c1 = new Vector3();
    Vector3 c2 = new Vector3();
    Vector3 c3 = new Vector3();
    c1.add(o, xv);
    c2.add(o, yv);
    c3.add(c1, yv);
    transform.apply(c0);
    transform.apply(c1);
    transform.apply(c2);
    transform.apply(c3);
    double u0 = uv.x;
    double u1 = uv.x + uv.y;
    double v0 = uv.z;
    double v1 = uv.z + uv.w;
    primitives.add(new TexturedTriangle(c0, c2, c1, new Vector2(u0, v0), new Vector2(u0, v1),
        new Vector2(u1, v0), material, false));
    primitives.add(new TexturedTriangle(c1, c2, c3, new Vector2(u1, v0), new Vector2(u0, v1),
        new Vector2(u1, v1), material, false));
  }

  /**
   * Build a transformed copy of this quad.
   */
  public Quad transform(Transform transform) {
    return new Quad(this, transform);
  }
}
