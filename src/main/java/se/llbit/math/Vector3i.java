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
import org.apache.commons.math3.util.FastMath;

/**
 * A 3D vector of integers.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Vector3i {

	@SuppressWarnings("javadoc")
	public int x, y, z;

	/**
	 * Creates a new vector (0, 0, 0)
	 */
	public Vector3i() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new vector (i, j, k)
	 * @param i
	 * @param j
	 * @param k
	 */
	public Vector3i(int i, int j, int k) {
		x = i;
		y = j;
		z = k;
	}

	/**
	 * Set this vector equal to o
	 * @param o
	 */
	public final void set(Vector3i o) {
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
	public final void set(int d, int e, int f) {
		x = d;
		y = e;
		z = f;
	}

	/**
	 * Scale this vector by i
	 * @param i
	 */
	public void scale(int i) {
		x *= i;
		y *= i;
		z *= i;
	}
}
