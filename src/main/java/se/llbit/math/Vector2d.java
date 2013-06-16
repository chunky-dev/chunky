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
 * A 3D vector of doubles.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Vector2d {

	@SuppressWarnings("javadoc")
	public double x, y;

	/**
	 * Creates a new vector (0, 0, 0)
	 */
	public Vector2d() {
		this(0, 0);
	}

	/**
	 * Creates a new vector (i, j)
	 * @param i
	 * @param j
	 */
	public Vector2d(double i, double j) {
		x = i;
		y = j;
	}

	/**
	 * Create a new vector equal to the given vector
	 * @param o
	 */
	public Vector2d(Vector2d o) {
		x = o.x;
		y = o.y;
	}

	/**
	 * Set this vector equal to other vector
	 * @param o
	 */
	public final void set(Vector2d o) {
		x = o.x;
		y = o.y;
	}

	/**
	 * Set this vector equal to (d, e)
	 * @param d
	 * @param e
	 */
	public final void set(double d, double e) {
		x = d;
		y = e;
	}

	/**
	 * @param o
	 * @return The dot product of this vector and o vector
	 */
	public final double dot(Vector2d o) {
		return x*o.x + y*o.y;
	}

	/**
	 * Set this vector equal to a-b
	 * @param a
	 * @param b
	 */
	public final void sub(Vector2d a, Vector2d b) {
		x = a.x - b.x;
		y = a.y - b.y;
	}

	/**
	 * @return The length of this vector, squared
	 */
	public final double lengthSquared() {
		return x*x + y*y;
	}

	/**
	 * Normalize this vector (scale the vector to unit length)
	 */
	public final void normalize() {
		double s = 1/FastMath.sqrt(lengthSquared());
		x *= s;
		y *= s;
	}

	/**
	 * Set this vector equal to s*d + o
	 * @param s
	 * @param d
	 * @param o
	 */
	public final void scaleAdd(double s, Vector2d d, Vector2d o) {
		x = s*d.x + o.x;
		y = s*d.y + o.y;
	}

	/**
	 * Scale this vector by s
	 * @param s
	 */
	public final void scale(double s) {
		x *= s;
		y *= s;
	}

	/**
	 * Set this vector equal to a+b
	 * @param a
	 * @param b
	 */
	public final void add(Vector2d a, Vector2d b) {
		x = a.x + b.x;
		y = a.y + b.y;
	}

	/**
	 * Add a to this vector
	 * @param a
	 */
	public final void add(Vector2d a) {
		x += a.x;
		y += a.y;
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
	}

	/**
	 * Subtract a from this vector
	 * @param a
	 */
	public final void sub(Vector2d a) {
		x -= a.x;
		y -= a.y;
	}

	/**
	 * Subtract a from this vector
	 * @param a
	 */
	public final void sub(Vector3i a) {
		x -= a.x;
		y -= a.y;
	}

	/**
	 * Set this vector equal to a
	 * @param a
	 */
	public void set(Vector3i a) {
		x = a.x;
		y = a.y;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
}
