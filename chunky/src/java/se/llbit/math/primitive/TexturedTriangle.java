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
import se.llbit.math.IntersectionRecord;
import se.llbit.chunky.world.Material;
import se.llbit.math.AABB;
import se.llbit.math.Ray2;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;

/**
 * A simple triangle primitive.
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TexturedTriangle implements Primitive {

  private static final double EPSILON = 0.000001;

  /** Note: This is public for some plugins. Stability is not guaranteed. */
  public final Vector3 e1 = new Vector3(0, 0, 0);
  public final Vector3 e2 = new Vector3(0, 0, 0);
  public final Vector3 o = new Vector3(0, 0, 0);
  public final Vector3 n = new Vector3(0, 0, 0);
  public final AABB bounds;
  public final double t1u;
  public final double t1v;
  public final double t2u;
  public final double t2v;
  public final double t3u;
  public final double t3v;
  public final Material material;
  public final boolean doubleSided;

  /**
   * @param c1 first corner
   * @param c2 second corner
   * @param c3 third corner
   */
  public TexturedTriangle(Vector3 c1, Vector3 c2, Vector3 c3, Vector2 t1, Vector2 t2,
      Vector2 t3, Material material) {
    this(c1, c2, c3, t1, t2, t3, material, true);
  }

  /**
   * @param c1 first corner
   * @param c2 second corner
   * @param c3 third corner
   */
  public TexturedTriangle(Vector3 c1, Vector3 c2, Vector3 c3, Vector2 t1, Vector2 t2,
      Vector2 t3, Material material, boolean doubleSided) {
    e1.sub(c2, c1);
    e2.sub(c3, c1);
    o.set(c1);
    n.cross(e2, e1);
    n.normalize();
    t1u = t2.x;
    t1v = t2.y;
    t2u = t3.x;
    t2v = t3.y;
    t3u = t1.x;
    t3v = t1.y;
    this.material = material;
    this.doubleSided = doubleSided;

    bounds = AABB.bounds(c1, c2, c3);
  }

  @Override public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    // Möller-Trumbore triangle intersection algorithm!
    Vector3 pvec = new Vector3();
    Vector3 qvec = new Vector3();
    Vector3 tvec = new Vector3();

    pvec.cross(ray.d, e2);
    double det = e1.dot(pvec);
    if (doubleSided) {
      if (det > -EPSILON && det < EPSILON) {
        return false;
      }
    } else if (det > -EPSILON) {
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

    if (t > EPSILON && t < intersectionRecord.distance) {
      double w = 1 - u - v;
      intersectionRecord.uv.x = t1u * u + t2u * v + t3u * w;
      intersectionRecord.uv.y = t1v * u + t2v * v + t3v * w;
      material.getColor(intersectionRecord);
      intersectionRecord.material = material;
      intersectionRecord.distance = t;
      intersectionRecord.setNormal(n);
      intersectionRecord.flags |= IntersectionRecord.NO_MEDIUM_CHANGE;
      return true;
    }
    return false;
  }

  @Override public AABB bounds() {
    return bounds;
  }

}
