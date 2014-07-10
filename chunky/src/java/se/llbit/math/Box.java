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
package se.llbit.math;

import se.llbit.chunky.world.Block;

/**
 * Box primitive.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Box {

	private final double x0;
	private final double x1;
	private final double y0;
	private final double y1;
	private final double z0;
	private final double z1;

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
		x0 = xmin;
		x1 = xmax;
		y0 = ymin;
		y1 = ymax;
		z0 = zmin;
		z1 = zmax;
	}

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
			ray.color.set(1, 0, 0, 1);
			ray.n.set(nx, ny, nz);
			return true;
		}
		return false;
	}
}
