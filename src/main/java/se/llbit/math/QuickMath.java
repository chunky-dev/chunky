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
 * Quick math utility methods.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class QuickMath {
	/**
	 * @param d
	 * @return The floor of d
	 */
	public static final int floor(double d) {
		int i = (int) d;
		return d < i ? i-1 : i;
	}
	/**
	 * @param d
	 * @return The ceil of d
	 */
	public static final int ceil(double d) {
		int i = (int) d;
		return d > i ? i+1 : i;
	}
	/**
	 * Get the next power of two.
	 * @param x
	 * @return The next power of two
	 */
	public static int nextPow2(int x) {
		x--;
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return x+1;
	}
	
	/**
	 * @param x
	 * @return 2-logarithm of x
	 */
	public static int log2(int x) {
		int v = 0;
		while ((x >>>= 1) != 0) {
			v += 1;
		}
		return v;
	}
	/**
	 * @param x
	 * @return The sign of x
	 */
	public static final int signum(double x) {
		return x < 0 ? -1 : 1;
	}
}
