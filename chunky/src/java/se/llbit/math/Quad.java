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
import java.util.Random;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;
import se.llbit.math.primitive.Primitive;
import se.llbit.math.primitive.TexturedTriangle;

/**
 * A quad.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Quad implements Intersectable {

  /** Note: This is public for some plugins. Stability is not guaranteed. */
  public Vector3 o = new Vector3();
  public Vector3 xv = new Vector3();
  public Vector3 yv = new Vector3();
  public Vector4 uv = new Vector4();

  /**
   * True if this quad is double-sided.
   */
  public final boolean doubleSided;

  /**
   * Normal vector
   */
  public Vector3 n = new Vector3();

  protected double d, xvl, yvl;

  /**
   * Create new Quad by copying another quad and applying a transform.
   */
  public Quad(Quad other, Transform t) {
    this.doubleSided = other.doubleSided;
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
    this.doubleSided = other.doubleSided;
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
   * Create a new single sided quad
   * <br>
   * For some visualizations, see this PR: <a href="https://github.com/chunky-dev/chunky/pull/1603">#1603</a>
   *
   * @param v0 Bottom left vector
   * @param v1 Bottom right vector
   * @param v2 Top left vector
   * @param uv Minimum and maximum U/V texture coordinates (x0,y0 bottom left)
   */
  public Quad(Vector3 v0, Vector3 v1, Vector3 v2, Vector4 uv) {
    this(v0, v1, v2, uv, false);
  }


  /**
   * Create new quad
   * <br>
   * For some visualizations, see this PR: <a href="https://github.com/chunky-dev/chunky/pull/1603">#1603</a>
   *
   * @param v0 Bottom left vector
   * @param v1 Bottom right vector
   * @param v2 Top left vector
   * @param uv Minimum and maximum U/V texture coordinates (x0,y0 bottom left)
   * @param doubleSided True to make this quad double-sided
   */
  public Quad(Vector3 v0, Vector3 v1, Vector3 v2, Vector4 uv, boolean doubleSided) {
    this.doubleSided = doubleSided;
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

  public double surfaceArea() {
    Vector3 cross = new Vector3();
    cross.cross(xv, yv);
    return cross.length();
  }

  public void sample(Vector3 loc, Random rand) {
    loc.set(o);
    loc.scaleAdd(rand.nextDouble(), xv);
    loc.scaleAdd(rand.nextDouble(), yv);
  }

  /**
   * Find intersection between the given ray and this quad
   *
   * @return <code>true</code> if the ray intersects this quad
   */
  public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    double u, v;

    double ix = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Constants.OFFSET);
    double iy = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Constants.OFFSET);
    double iz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Constants.OFFSET);

    // Test that the ray is heading toward the plane of this quad.
    double denom = ray.d.dot(n);
    if (denom < -Constants.EPSILON || (doubleSided && denom > Constants.EPSILON)) {

      // Test for intersection with the plane at origin.
      double t = -(ix * n.x + iy * n.y + iz * n.z + d) / denom;
      if (t > -Constants.EPSILON && t < intersectionRecord.distance) {

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
          intersectionRecord.uv.x = uv.x + u * uv.y;
          intersectionRecord.uv.y = uv.z + v * uv.w;
          intersectionRecord.distance = t;
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

  public void addTriangles(Collection<Primitive> primitives, Material material, Transform transform) {
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
        new Vector2(u1, v0), material, doubleSided));
    primitives.add(new TexturedTriangle(c1, c2, c3, new Vector2(u1, v0), new Vector2(u0, v1),
        new Vector2(u1, v1), material, doubleSided));
  }

  /**
   * Build a transformed copy of this quad.
   */
  public Quad transform(Transform transform) {
    return new Quad(this, transform);
  }
}
