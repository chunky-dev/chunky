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
package se.llbit.util;

/**
 * Custom Pair class.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 *
 * @param <U>
 * @param <V>
 */
public class Pair<U, V> {
	/**
	 * Thing one
	 */
	public U thing1;

	/**
	 * Thing two
	 */
	public V thing2;

	/**
	 * Create a new tuple of two things.
	 * @param one Thing one
	 * @param two Thing two
	 */
	public Pair(U one, V two) {
		thing1 = one;
		thing2 = two;
	}
}
