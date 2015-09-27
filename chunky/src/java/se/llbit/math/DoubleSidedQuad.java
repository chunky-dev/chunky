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
 * A double-sided quad
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class DoubleSidedQuad extends Quad {

	/**
	 * @param other
	 * @param t
	 */
	private DoubleSidedQuad(Quad other, Transform t) {
		super(other, t);
	}

	/**
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param uv
	 */
	public DoubleSidedQuad(Vector3d p1, Vector3d p2, Vector3d p3, Vector4d uv) {
		super(p1, p2, p3, uv);
	}

	@Override
	public boolean intersect(Ray ray) {
		double ix = ray.o.x - QuickMath.floor(ray.o.x + ray.d.x * Ray.OFFSET);
		double iy = ray.o.y - QuickMath.floor(ray.o.y + ray.d.y * Ray.OFFSET);
		double iz = ray.o.z - QuickMath.floor(ray.o.z + ray.d.z * Ray.OFFSET);
		double denom = ray.d.dot(n);
		double u, v;

		if (QuickMath.abs(denom) > Ray.EPSILON) {
			double t = - (ix*n.x + iy*n.y + iz*n.z + d) / denom;
			if (t > -Ray.EPSILON && t < ray.t) {
				ix = ix + ray.d.x * t - o.x;
				iy = iy + ray.d.y * t - o.y;
				iz = iz + ray.d.z * t - o.z;
				u = ix*xv.x + iy*xv.y + iz*xv.z;
				u *= xvl;
				v = ix*yv.x + iy*yv.y + iz*yv.z;
				v *= yvl;
				if (u >= 0 && u <= 1 && v >= 0 && v <= 1) {
					ray.u = uv.x + u*uv.y;
					ray.v = uv.z + v*uv.w;
					ray.tNext = t;
					return true;
				}
			}
		}
		return false;
	}

	/** Build a transformed copy of this quad. */
	@Override
	public DoubleSidedQuad transform(Transform transform) {
		return new DoubleSidedQuad(this, transform);
	}
}
