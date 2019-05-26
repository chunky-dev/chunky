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

import se.llbit.chunky.world.Material;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;

/**
 * A simple triangle primitive.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TexturedTriangle implements Primitive {

  private static final double EPSILON = 0.000001;
  private final Vector3 e1 = new Vector3(0, 0, 0);
  private final Vector3 e2 = new Vector3(0, 0, 0);
  private final Vector3 o = new Vector3(0, 0, 0);
  private final Vector3 n = new Vector3(0, 0, 0);
  private final AABB bounds;
  private final Vector2 t1;
  private final Vector2 t2;
  private final Vector2 t3;
  private final Material material;

  /**
   * @param c1 first corner
   * @param c2 second corner
   * @param c3 third corner
   */
  public TexturedTriangle(Vector3 c1, Vector3 c2, Vector3 c3, Vector2 t1, Vector2 t2,
      Vector2 t3, Material material) {
    e1.sub(c2, c1);
    e2.sub(c3, c1);
    o.set(c1);
    n.cross(e2, e1);
    n.normalize();
    this.t1 = new Vector2(t2);
    this.t2 = new Vector2(t3);
    this.t3 = new Vector2(t1);
    this.material = material;

    bounds = AABB.bounds(c1, c2, c3);
  }

  @Override public boolean intersect(Ray ray) {
    // Möller-Trumbore triangle intersection algorithm!
    Vector3 pvec = new Vector3();
    Vector3 qvec = new Vector3();
    Vector3 tvec = new Vector3();

    pvec.cross(ray.d, e2);
    double det = e1.dot(pvec);
    if (det > -EPSILON && det < EPSILON) {
      return false;
    }
    double recip = 1 / det;

    tvec.sub(ray.o, o);

    double u = tvec.dot(pvec) * recip;

    if (u < 0 || u > 1) {
      return false;
    }

    qvec.cross(tvec, e1);

    double v = ray.d.dot(qvec) * recip;

    if (v < 0 || (u + v) > 1) {
      return false;
    }

    double t = e2.dot(qvec) * recip;

    if (t > EPSILON && t < ray.t) {
      double w = 1 - u - v;
      ray.u = t1.x * u + t2.x * v + t3.x * w;
      ray.v = t1.y * u + t2.y * v + t3.y * w;
      float[] color = material.getColor(ray.u, ray.v);
      if (color[3] > 0) {
        ray.color.set(color);
        ray.setCurrentMaterial(material, 0);
        ray.t = t;
        ray.n.set(n);
        return true;
      }
    }
    return false;
  }

  @Override public AABB bounds() {
    return bounds;
  }

}
