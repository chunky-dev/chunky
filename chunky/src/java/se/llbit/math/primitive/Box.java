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

import java.util.Collection;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Block;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector2d;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * Box primitive.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Box implements Primitive {

	private final double x0;
	private final double x1;
	private final double y0;
	private final double y1;
	private final double z0;
	private final double z1;
	private final Vector3d color;
	private final Vector3d c000;
	private final Vector3d c001;
	private final Vector3d c010;
	private final Vector3d c011;
	private final Vector3d c100;
	private final Vector3d c101;
	private final Vector3d c110;
	private final Vector3d c111;

	/**
	 * Construct a new axis-aligned Box with given bounds
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @param zmin
	 * @param zmax
	 */
	public Box(double xmin, double xmax, double ymin, double ymax,
			double zmin, double zmax) {
		this(xmin, xmax, ymin, ymax, zmin, zmax, new Vector3d(1, 0, 0));
	}

	public Box(double xmin, double xmax, double ymin, double ymax,
			double zmin, double zmax, Vector3d color) {
		x0 = xmin;
		x1 = xmax;
		y0 = ymin;
		y1 = ymax;
		z0 = zmin;
		z1 = zmax;
		this.color = new Vector3d(color);
		c000 = new Vector3d(x0, y0, z0);
		c001 = new Vector3d(x0, y0, z1);
		c010 = new Vector3d(x0, y1, z0);
		c011 = new Vector3d(x0, y1, z1);
		c100 = new Vector3d(x1, y0, z0);
		c101 = new Vector3d(x1, y0, z1);
		c110 = new Vector3d(x1, y1, z0);
		c111 = new Vector3d(x1, y1, z1);
	}

	@Override
	public boolean intersect(Ray ray) {
		double t1, t2;
		double tNear = Double.NEGATIVE_INFINITY;
		double tFar = Double.POSITIVE_INFINITY;
		Vector3d d = ray.d;
		Vector3d o = ray.x;
		double nx = 0;
		double ny = 0;
		double nz = 0;

		if (d.x != 0) {
			double rx = 1/d.x;
			t1 = (x0 - o.x) * rx;
			t2 = (x1 - o.x) * rx;

			int sign = -1;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
				sign = 1;
			}

			tNear = t1;
			nx = sign;
			ny = nz = 0;
			tFar = t2;
		}

		if (d.y != 0) {
			double ry = 1 / d.y;
			t1 = (y0 - o.y) * ry;
			t2 = (y1 - o.y) * ry;

			int sign = -1;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
				sign = 1;
			}

			if (t1 > tNear) {
				tNear = t1;
				ny = sign;
				nx = nz = 0;
			}
			if (t2 < tFar) {
				tFar = t2;
			}
		}

		if (d.z != 0) {
			double rz = 1 / d.z;
			t1 = (z0 - o.z) * rz;
			t2 = (z1 - o.z) * rz;

			int sign = -1;

			if (t1 > t2) {
				double t = t1;
				t1 = t2;
				t2 = t;
				sign = 1;
			}

			if (t1 > tNear) {
				tNear = t1;
				nz = sign;
				nx = ny = 0;
			}
			if (t2 < tFar) {
				tFar = t2;
			}
		}

		if (tNear < tFar + Ray.EPSILON && tNear >= 0 && tNear < ray.tNear) {
			ray.tNear = tNear;
			ray.currentMaterial = Block.STONE_ID;
			ray.color.set(color.x, color.y, color.z, 1);
			ray.n.set(nx, ny, nz);
			return true;
		}
		return false;
	}

	@Override
	public AABB bounds() {
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

	public void addFrontFaces(Collection<Primitive> primitives, Texture steve,
			Vector4d uv) {
		primitives.add(new TexturedTriangle(c000, c100, c010,
				new Vector2d(uv.y, uv.z), new Vector2d(uv.x, uv.z), new Vector2d(uv.y, uv.w), steve));
		primitives.add(new TexturedTriangle(c100, c110, c010,
				new Vector2d(uv.x, uv.z), new Vector2d(uv.x, uv.w), new Vector2d(uv.y, uv.w), steve));
	}

	public void addBackFaces(Collection<Primitive> primitives, Texture steve,
			Vector4d uv) {
		primitives.add(new TexturedTriangle(c101, c001, c111,
				new Vector2d(uv.x, uv.z), new Vector2d(uv.y, uv.z), new Vector2d(uv.x, uv.w), steve));
		primitives.add(new TexturedTriangle(c001, c011, c111,
				new Vector2d(uv.y, uv.z), new Vector2d(uv.y, uv.w), new Vector2d(uv.x, uv.w), steve));
	}

	public void addLeftFaces(Collection<Primitive> primitives, Texture steve,
			Vector4d uv) {
		primitives.add(new TexturedTriangle(c001, c000, c011,
				new Vector2d(uv.y, uv.z), new Vector2d(uv.x, uv.z), new Vector2d(uv.y, uv.w), steve));
		primitives.add(new TexturedTriangle(c000, c010, c011,
				new Vector2d(uv.x, uv.z), new Vector2d(uv.x, uv.w), new Vector2d(uv.y, uv.w), steve));
	}

	public void addRightFaces(Collection<Primitive> primitives, Texture steve,
			Vector4d uv) {
		primitives.add(new TexturedTriangle(c100, c101, c110,
				new Vector2d(uv.y, uv.z), new Vector2d(uv.x, uv.z), new Vector2d(uv.y, uv.w), steve));
		primitives.add(new TexturedTriangle(c101, c111, c110,
				new Vector2d(uv.x, uv.z), new Vector2d(uv.x, uv.w), new Vector2d(uv.y, uv.w), steve));
	}

	public void addTopFaces(Collection<Primitive> primitives, Texture steve,
			Vector4d uv) {
		primitives.add(new TexturedTriangle(c011, c110, c111,
				new Vector2d(uv.y, uv.w), new Vector2d(uv.x, uv.z), new Vector2d(uv.x, uv.w), steve));
		primitives.add(new TexturedTriangle(c011, c010, c110,
				new Vector2d(uv.y, uv.w), new Vector2d(uv.y, uv.z), new Vector2d(uv.x, uv.z), steve));
	}

	public void addBottomFaces(Collection<Primitive> primitives, Texture steve,
			Vector4d uv) {
		primitives.add(new TexturedTriangle(c000, c001, c100,
				new Vector2d(uv.x, uv.z), new Vector2d(uv.y, uv.z), new Vector2d(uv.x, uv.w), steve));
		primitives.add(new TexturedTriangle(c001, c101, c100,
				new Vector2d(uv.y, uv.z), new Vector2d(uv.y, uv.w), new Vector2d(uv.x, uv.w), steve));
	}
}
