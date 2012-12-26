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
 * 4D double vector
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Vector4d {
	
	@SuppressWarnings("javadoc")
	public double x, y, z, w;

	/**
	 * Create new vector
	 */
	public Vector4d() {
		this(0, 0, 0, 0);
	}
	
	/**
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 */
	public Vector4d(double i, double j, double k, double l) {
		x = i;
		y = j;
		z = k;
		w = l;
	}

	/**
	 * Set the vector equal to other vector
	 * @param other
	 */
	public final void set(Vector4d other) {
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}

	/**
	 * Set the vector
	 * @param i
	 * @param j
	 * @param k
	 * @param l
	 */
	public final void set(double i, double j, double k, double l) {
		x = i;
		y = j;
		z = k;
		w = l;
	}

	/**
	 * Scale the vector
	 * @param d
	 */
	public void scale(double d) {
		x *= d;
		y *= d;
		z *= d;
		w *= d;
	}

	/**
	 * Set the vector
	 * @param v
	 */
	public void set(float[] v) {
		x = v[0];
		y = v[1];
		z = v[2];
		w = v[3];
	}
}
