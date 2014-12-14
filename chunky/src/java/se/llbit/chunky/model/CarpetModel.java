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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

@SuppressWarnings("javadoc")
public class CarpetModel {
	private static AABB aabb = new AABB(0, 1, 0, 1/16., 0, 1);

	public static boolean intersect(Ray ray, Texture texture) {
		ray.t = Double.POSITIVE_INFINITY;
		if (aabb.intersect(ray)) {
			texture.getColor(ray);
			ray.color.w = 1;
			ray.distance += ray.tNear;
			ray.x.scaleAdd(ray.tNear, ray.d);
			return true;
		}
		return false;
	}
}
