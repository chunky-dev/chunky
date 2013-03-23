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
 * A class to test intersection against a three-dimensional,
 * non-degenerate triangle.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Triangle {

	/**
	 * Normal vector
	 */
	public final Vector3d n;

	private final Vector3d o;
	private final Vector3d u;
	private final Vector3d v;

	private final double d;
	private final double uv;
	private final double uu;
	private final double vv;
	private final double uv2;

	/**
	 * Construct a new triangle
	 * @param v0
	 * @param v1
	 * @param v2
	 */
	public Triangle(Vector3d v0, Vector3d v1, Vector3d v2) {

		o = new Vector3d(v0);

		n = new Vector3d();
		u = new Vector3d();
		v = new Vector3d();

		u.sub(v1, o);
		v.sub(v2, o);

		n.cross(u, v);
		n.normalize();

		d = - n.dot(o);

		uv = u.dot(v);
		uu = u.dot(u);
		vv = v.dot(v);
		uv2 = uv*uv;
	}

	/**
	 * Find intersection between the ray and this triangle
	 * @param ray
	 * @return <code>true</code> if the ray intersects the triangle
	 */
	public boolean intersect(Ray ray) {
		double ix = ray.x.x - QuickMath.floor(ray.x.x + ray.d.x * Ray.OFFSET);
		double iy = ray.x.y - QuickMath.floor(ray.x.y + ray.d.y * Ray.OFFSET);
		double iz = ray.x.z - QuickMath.floor(ray.x.z + ray.d.z * Ray.OFFSET);

		// test that the ray is heading toward the plane
		double denom = ray.d.dot(n);
		if (Math.abs(denom) > Ray.EPSILON) {

			// test for intersection with the plane at origin
			double t = - (ix*n.x + iy*n.y + iz*n.z + d) / denom;
			if (t > -Ray.EPSILON && t < ray.t) {

				// plane intersection confirmed
				// translate to get hit point relative to the triangle origin
				ix = ix + ray.d.x * t - o.x;
				iy = iy + ray.d.y * t - o.y;
				iz = iz + ray.d.z * t - o.z;

				double wu = ix*u.x + iy*u.y + iz*u.z;
				double wv = ix*v.x + iy*v.y + iz*v.z;
				double si = (uv*wv - vv*wu) / (uv2 - uu*vv);
				double ti = (uv*wu - uu*wv) / (uv2 - uu*vv);
				if ((si >= 0) && (ti >= 0) && (si+ti <= 1)) {
					ray.tNear = t;
					ray.u = si;
					ray.v = ti;
					return true;
				}
			}
		}

		return false;
	}

}
