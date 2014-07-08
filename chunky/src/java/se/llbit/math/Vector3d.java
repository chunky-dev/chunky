/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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
 * A 3D vector of doubles.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Vector3d {

	@SuppressWarnings("javadoc")
	public double x, y, z;

	/**
	 * Creates a new vector (0, 0, 0)
	 */
	public Vector3d() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new vector (i, j, k)
	 * @param i
	 * @param j
	 * @param k
	 */
	public Vector3d(double i, double j, double k) {
		x = i;
		y = j;
		z = k;
	}

	/**
	 * Create a new vector equal to the given vector
	 * @param o
	 */
	public Vector3d(Vector3d o) {
		x = o.x;
		y = o.y;
		z = o.z;
	}

	/**
	 * Set this vector equal to other vector
	 * @param o
	 */
	public final void set(Vector3d o) {
		x = o.x;
		y = o.y;
		z = o.z;
	}

	/**
	 * Set this vector equal to (d, e, f)
	 * @param d
	 * @param e
	 * @param f
	 */
	public final void set(double d, double e, double f) {
		x = d;
		y = e;
		z = f;
	}

	/**
	 * @param o
	 * @return The dot product of this vector and o vector
	 */
	public final double dot(Vector3d o) {
		return x*o.x + y*o.y + z*o.z;
	}

	/**
	 * Set this vector equal to a-b
	 * @param a
	 * @param b
	 */
	public final void sub(Vector3d a, Vector3d b) {
		x = a.x - b.x;
		y = a.y - b.y;
		z = a.z - b.z;
	}

	/**
	 * @return The length of this vector, squared
	 */
	public final double lengthSquared() {
		return x*x + y*y + z*z;
	}

	/**
	 * @return Length of this vector
	 */
	public final double length() {
		return FastMath.sqrt(lengthSquared());
	}

	/**
	 * Set this vector equal to the cross product of a and b
	 * @param a
	 * @param b
	 */
	public final void cross(Vector3d a, Vector3d b) {
		x = a.y*b.z - a.z*b.y;
		y = a.z*b.x - a.x*b.z;
		z = a.x*b.y - a.y*b.x;
	}

	/**
	 * Normalize this vector (scale the vector to unit length)
	 */
	public final void normalize() {
		double s = 1/FastMath.sqrt(lengthSquared());
		x *= s;
		y *= s;
		z *= s;
	}

	/**
	 * Set this vector equal to s*d + o
	 * @param s
	 * @param d
	 * @param o
	 */
	public final void scaleAdd(double s, Vector3d d, Vector3d o) {
		x = s*d.x + o.x;
		y = s*d.y + o.y;
		z = s*d.z + o.z;
	}

	/**
	 * Add s*d to this vector
	 * @param s
	 * @param d
	 * @param o
	 */
	public final void scaleAdd(double s, Vector3d d) {
		x += s*d.x;
		y += s*d.y;
		z += s*d.z;
	}

	/**
	 * Scale this vector by s
	 * @param s
	 */
	public final void scale(double s) {
		x *= s;
		y *= s;
		z *= s;
	}

	/**
	 * Set this vector equal to a+b
	 * @param a
	 * @param b
	 */
	public final void add(Vector3d a, Vector3d b) {
		x = a.x + b.x;
		y = a.y + b.y;
		z = a.z + b.z;
	}

	/**
	 * Add a to this vector
	 * @param a
	 */
	public final void add(Vector3d a) {
		x += a.x;
		y += a.y;
		z += a.z;
	}

	/**
	 * Add vector (a, b, c) to this vector
	 * @param a
	 * @param b
	 * @param c
	 */
	public final void add(double a, double b, double c) {
		x += a;
		y += b;
		z += c;
	}

	/**
	 * Subtract a from this vector
	 * @param a
	 */
	public final void sub(Vector3d a) {
		x -= a.x;
		y -= a.y;
		z -= a.z;
	}

	/**
	 * Subtract vector (a, b, c) from this vector
	 * @param a
	 * @param b
	 * @param c
	 */
	public final void sub(double a, double b, double c) {
		x -= a;
		y -= b;
		z -= c;
	}

	/**
	 * Subtract a from this vector
	 * @param a
	 */
	public final void sub(Vector3i a) {
		x -= a.x;
		y -= a.y;
		z -= a.z;
	}

	/**
	 * Set this vector equal to a
	 * @param a
	 */
	public void set(Vector3i a) {
		x = a.x;
		y = a.y;
		z = a.z;
	}

	@Override
	public String toString() {
		return String.format("(%.2f, %.2f, %.2f)", x, y, z);
	}
}
