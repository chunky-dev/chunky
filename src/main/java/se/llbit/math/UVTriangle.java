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
import org.apache.commons.math3.util.FastMath;

/**
 * UV-mapped triangle
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class UVTriangle {

	/**
	 * Normal vector - normal to triangle vertices
	 * in counterclockwise order
	 */
	public final Vector3d n;

	private final Vector3d a;
	private final Vector3d b;
	private final Vector3d c;

	private final Vector3d b_a;
	private final Vector3d c_a;
	private final Vector3d c_b;
	private final Vector3d a_c;

	private final Vector2d sa;
	private final Vector2d sb;
	private final Vector2d sc;

	private final double d;
	private final double rn;

	/**
	 * Construct a new triangle
	 * @param v0
	 * @param v1
	 * @param v2
	 */
	@SuppressWarnings("javadoc")
	public UVTriangle(Vector3d v0, Vector3d v1, Vector3d v2,
			Vector2d s0, Vector2d s1, Vector2d s2) {

		a = new Vector3d(v0);
		b = new Vector3d(v1);
		c = new Vector3d(v2);

		this.sa = new Vector2d(s0);
		this.sb = new Vector2d(s1);
		this.sc = new Vector2d(s2);

		b_a = new Vector3d();
		c_a = new Vector3d();
		c_b = new Vector3d();
		a_c = new Vector3d();

		b_a.sub(b, a);
		c_a.sub(c, a);
		c_b.sub(c, b);
		a_c.sub(a, c);

		n = new Vector3d();

		n.cross(b_a, c_a);
		rn = 1.0 / n.length();
		n.normalize();

		d = - n.dot(a);
	}

	/**
	 * Find intersection between the ray and this triangle
	 * @param ray
	 * @return <code>true</code> if the ray intersects the triangle
	 */
	public boolean intersect(Ray ray) {
		double px = ray.x.x - QuickMath.floor(ray.x.x + ray.d.x * Ray.OFFSET);
		double py = ray.x.y - QuickMath.floor(ray.x.y + ray.d.y * Ray.OFFSET);
		double pz = ray.x.z - QuickMath.floor(ray.x.z + ray.d.z * Ray.OFFSET);

		// test that the ray is heading toward the plane
		double denom = ray.d.dot(n);
		if (denom < -Ray.EPSILON) {

			// test for intersection with the plane at origin
			double t = - (px*n.x + py*n.y + pz*n.z + d) / denom;
			if (t > -Ray.EPSILON && t < ray.t) {

				// calculate plane intersection point
				px = px + ray.d.x * t;
				py = py + ray.d.y * t;
				pz = pz + ray.d.z * t;

				// calculate barycentric coordinates
				double nax = c_b.y*(pz-b.z) - c_b.z*(py-b.y);
				double nay = c_b.z*(px-b.x) - c_b.x*(pz-b.z);
				double naz = c_b.x*(py-b.y) - c_b.y*(px-b.x);

				double nbx = a_c.y*(pz-c.z) - a_c.z*(py-c.y);
				double nby = a_c.z*(px-c.x) - a_c.x*(pz-c.z);
				double nbz = a_c.x*(py-c.y) - a_c.y*(px-c.x);

				double ncx = b_a.y*(pz-a.z) - b_a.z*(py-a.y);
				double ncy = b_a.z*(px-a.x) - b_a.x*(pz-a.z);
				double ncz = b_a.x*(py-a.y) - b_a.y*(px-a.x);

				// alpha, beta, gamma are the barycentric coordinates
				double alpha = (n.x*nax + n.y*nay + n.z*naz) * rn;
				double beta =  (n.x*nbx + n.y*nby + n.z*nbz) * rn;
				double gamma = (n.x*ncx + n.y*ncy + n.z*ncz) * rn;

				if (alpha >= 0 && beta >= 0 && gamma >= 0) {

					ray.tNear = t;
					ray.u = alpha * sa.x + beta * sb.x + gamma * sc.x;
					ray.v = alpha * sa.y + beta * sb.y + gamma * sc.y;
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @return Rotated copy of this triangle
	 */
	public UVTriangle getYRotated() {
		Vector3d ar = new Vector3d(a);
		ar.add(-0.5, -0.5, -0.5);
		Transform.rotateY.rotate(ar);
		ar.add(0.5, 0.5, 0.5);
		Vector3d br = new Vector3d(b);
		br.add(-0.5, -0.5, -0.5);
		Transform.rotateY.rotate(br);
		br.add(0.5, 0.5, 0.5);
		Vector3d cr = new Vector3d(c);
		cr.add(-0.5, -0.5, -0.5);
		Transform.rotateY.rotate(cr);
		cr.add(0.5, 0.5, 0.5);
		return new UVTriangle(ar, br, cr, sa, sb, sc);
	}

}
