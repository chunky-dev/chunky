/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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

import java.util.Collection;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Box primitive.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Box implements Primitive {

  private final double x0;
  private final double x1;
  private final double y0;
  private final double y1;
  private final double z0;
  private final double z1;
  private final Vector3 c000;
  private final Vector3 c001;
  private final Vector3 c010;
  private final Vector3 c011;
  private final Vector3 c100;
  private final Vector3 c101;
  private final Vector3 c110;
  private final Vector3 c111;

  /**
   * Construct a new axis-aligned Box with given bounds
   */
  public Box(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
    x0 = xmin;
    x1 = xmax;
    y0 = ymin;
    y1 = ymax;
    z0 = zmin;
    z1 = zmax;
    c000 = new Vector3(x0, y0, z0);
    c001 = new Vector3(x0, y0, z1);
    c010 = new Vector3(x0, y1, z0);
    c011 = new Vector3(x0, y1, z1);
    c100 = new Vector3(x1, y0, z0);
    c101 = new Vector3(x1, y0, z1);
    c110 = new Vector3(x1, y1, z0);
    c111 = new Vector3(x1, y1, z1);
  }

  @Override public AABB bounds() {
    return new AABB(x0, x1, y0, y1, z0, z1);
  }

  public void transform(Transform t) {
    t.apply(c000);
    t.apply(c001);
    t.apply(c010);
    t.apply(c011);
    t.apply(c100);
    t.apply(c101);
    t.apply(c110);
    t.apply(c111);
  }

  public void addFrontFaces(Collection<Primitive> primitives, Texture texture, Vector4 uv) {
    Material material = new TextureMaterial(texture);
    primitives.add(
        new TexturedTriangle(c000, c100, c010, new Vector2(uv.y, uv.z), new Vector2(uv.x, uv.z),
            new Vector2(uv.y, uv.w), material));
    primitives.add(
        new TexturedTriangle(c100, c110, c010, new Vector2(uv.x, uv.z), new Vector2(uv.x, uv.w),
            new Vector2(uv.y, uv.w), material));
  }

  public void addBackFaces(Collection<Primitive> primitives, Texture texture, Vector4 uv) {
    Material material = new TextureMaterial(texture);
    primitives.add(
        new TexturedTriangle(c101, c001, c111, new Vector2(uv.x, uv.z), new Vector2(uv.y, uv.z),
            new Vector2(uv.x, uv.w), material));
    primitives.add(
        new TexturedTriangle(c001, c011, c111, new Vector2(uv.y, uv.z), new Vector2(uv.y, uv.w),
            new Vector2(uv.x, uv.w), material));
  }

  public void addLeftFaces(Collection<Primitive> primitives, Texture texture, Vector4 uv) {
    Material material = new TextureMaterial(texture);
    primitives.add(
        new TexturedTriangle(c001, c000, c011, new Vector2(uv.y, uv.z), new Vector2(uv.x, uv.z),
            new Vector2(uv.y, uv.w), material));
    primitives.add(
        new TexturedTriangle(c000, c010, c011, new Vector2(uv.x, uv.z), new Vector2(uv.x, uv.w),
            new Vector2(uv.y, uv.w), material));
  }

  public void addRightFaces(Collection<Primitive> primitives, Texture texture, Vector4 uv) {
    Material material = new TextureMaterial(texture);
    primitives.add(
        new TexturedTriangle(c100, c101, c110, new Vector2(uv.y, uv.z), new Vector2(uv.x, uv.z),
            new Vector2(uv.y, uv.w), material));
    primitives.add(
        new TexturedTriangle(c101, c111, c110, new Vector2(uv.x, uv.z), new Vector2(uv.x, uv.w),
            new Vector2(uv.y, uv.w), material));
  }

  public void addTopFaces(Collection<Primitive> primitives, Texture texture, Vector4 uv) {
    Material material = new TextureMaterial(texture);
    primitives.add(
        new TexturedTriangle(c011, c110, c111, new Vector2(uv.y, uv.w), new Vector2(uv.x, uv.z),
            new Vector2(uv.x, uv.w), material));
    primitives.add(
        new TexturedTriangle(c011, c010, c110, new Vector2(uv.y, uv.w), new Vector2(uv.y, uv.z),
            new Vector2(uv.x, uv.z), material));
  }

  public void addBottomFaces(Collection<Primitive> primitives, Texture texture, Vector4 uv) {
    Material material = new TextureMaterial(texture);
    primitives.add(
        new TexturedTriangle(c000, c001, c100, new Vector2(uv.x, uv.z), new Vector2(uv.x, uv.w),
            new Vector2(uv.y, uv.z), material));
    primitives.add(
        new TexturedTriangle(c001, c101, c100, new Vector2(uv.x, uv.w), new Vector2(uv.y, uv.w),
            new Vector2(uv.y, uv.z), material));
  }

  @Override public boolean intersect(Ray ray) {
    // TODO Auto-generated method stub
    return false;
  }
}
