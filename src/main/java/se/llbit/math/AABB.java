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

/**
 * Axis Aligned Bounding Box
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AABB {

	double xmin, xmax;
	double ymin, ymax;
	double zmin, zmax;

	/**
	 * @param xmin
	 * @param xmax
	 * @param ymin
	 * @param ymax
	 * @param zmin
	 * @param zmax
	 */
	public AABB(double xmin, double xmax, double ymin, double ymax,
			double zmin, double zmax) {

		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.zmin = zmin;
		this.zmax = zmax;
	}

	/**
	 * Find intersection between the given ray and this AABB
	 * @param ray
	 * @return <code>true</code> if the ray intersects this AABB
	 */
	public boolean intersect(Ray ray) {
		double ix = ray.x.x - QuickMath.floor(ray.x.x + ray.d.x * Ray.OFFSET);
		double iy = ray.x.y - QuickMath.floor(ray.x.y + ray.d.y * Ray.OFFSET);
		double iz = ray.x.z - QuickMath.floor(ray.x.z + ray.d.z * Ray.OFFSET);
		double t;
		double u, v;
		boolean hit = false;

		ray.tNear = ray.t;

		t = (xmin - ix) / ray.d.x;
		if (t < ray.tNear && t > -Ray.EPSILON) {
			u = iz + ray.d.z * t;
			v = iy + ray.d.y * t;
			if (u >= zmin && u <= zmax &&
					v >= ymin && v <= ymax) {
				hit = true;
				ray.tNear = t;
				ray.u = u;
				ray.v = v;
				ray.n.set(-1, 0, 0);
			}
		}
		t = (xmax - ix) / ray.d.x;
		if (t < ray.tNear && t > -Ray.EPSILON) {
			u = iz + ray.d.z * t;
			v = iy + ray.d.y * t;
			if (u >= zmin && u <= zmax &&
					v >= ymin && v <= ymax) {
				hit = true;
				ray.tNear = t;
				ray.u = u;
				ray.v = v;
				ray.n.set(1, 0, 0);
			}
		}
		t = (ymin - iy) / ray.d.y;
		if (t < ray.tNear && t > -Ray.EPSILON) {
			u = ix + ray.d.x * t;
			v = iz + ray.d.z * t;
			if (u >= xmin && u <= xmax &&
					v >= zmin && v <= zmax) {
				hit = true;
				ray.tNear = t;
				ray.u = u;
				ray.v = v;
				ray.n.set(0, -1, 0);
			}
		}
		t = (ymax - iy) / ray.d.y;
		if (t < ray.tNear && t > -Ray.EPSILON) {
			u = ix + ray.d.x * t;
			v = iz + ray.d.z * t;
			if (u >= xmin && u <= xmax &&
					v >= zmin && v <= zmax) {
				hit = true;
				ray.tNear = t;
				ray.u = u;
				ray.v = v;
				ray.n.set(0, 1, 0);
			}
		}
		t = (zmin - iz) / ray.d.z;
		if (t < ray.tNear && t > -Ray.EPSILON) {
			u = ix + ray.d.x * t;
			v = iy + ray.d.y * t;
			if (u >= xmin && u <= xmax &&
					v >= ymin && v <= ymax) {
				hit = true;
				ray.tNear = t;
				ray.u = u;
				ray.v = v;
				ray.n.set(0, 0, -1);
			}
		}
		t = (zmax - iz) / ray.d.z;
		if (t < ray.tNear && t > -Ray.EPSILON) {
			u = ix + ray.d.x * t;
			v = iy + ray.d.y * t;
			if (u >= xmin && u <= xmax &&
					v >= ymin && v <= ymax) {
				hit = true;
				ray.tNear = t;
				ray.u = u;
				ray.v = v;
				ray.n.set(0, 0, 1);
			}
		}
		return hit;
	}

	/**
	 * @return AABB rotated about the Y axis
	 */
	public AABB getYRotated() {
		return new AABB(
				1 - zmax, 1 - zmin,
				ymin, ymax,
				xmin, xmax);
	}

	/**
	 * @param x X translation
	 * @param y Y translation
	 * @param z Z translation
	 * @return A translated copy of this AABB
	 */
	public AABB getTranslated(double x, double y, double z) {
		return new AABB(
				xmin + x, xmax + x,
				ymin + y, ymax + y,
				zmin + z, zmax + z);
	}
}
