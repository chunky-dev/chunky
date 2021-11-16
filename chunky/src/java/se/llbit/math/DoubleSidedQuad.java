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

import java.util.Collection;
import se.llbit.chunky.world.Material;
import se.llbit.math.primitive.Primitive;
import se.llbit.math.primitive.TexturedTriangle;

/**
 * A double-sided quad
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class DoubleSidedQuad extends Quad {
  public final boolean doubleSided = true;

  public DoubleSidedQuad(Quad other, Transform t) {
    super(other, t);
  }

  public DoubleSidedQuad(Vector3 p1, Vector3 p2, Vector3 p3, Vector4 uv) {
    super(p1, p2, p3, uv);
  }

  @Override public boolean intersect(Ray ray) {
    double ix = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
    double iy = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
    double iz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);
    double denom = ray.d.dot(n);
    double u, v;

    if (QuickMath.abs(denom) > Ray.EPSILON) {
      double t = -(ix * n.x + iy * n.y + iz * n.z + d) / denom;
      if (t > -Ray.EPSILON && t < ray.t) {
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

  @Override
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
        new Vector2(u1, v0), material, true));
    primitives.add(new TexturedTriangle(c1, c2, c3, new Vector2(u1, v0), new Vector2(u0, v1),
        new Vector2(u1, v1), material, true));
  }

  /**
   * Build a transformed copy of this quad.
   */
  @Override public DoubleSidedQuad transform(Transform transform) {
    return new DoubleSidedQuad(this, transform);
  }
}
