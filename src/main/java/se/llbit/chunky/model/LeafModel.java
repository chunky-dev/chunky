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
package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

@SuppressWarnings("javadoc")
public class LeafModel {
	private static final AABB block = new AABB(0, 1, 0, 1, 0, 1);
	
	public static boolean intersect(Ray ray) {
		ray.t = Double.POSITIVE_INFINITY;
		if (block.intersect(ray)) {
			float[] color = Texture.leaves[ray.getBlockData() & 3].getColor(ray.u, ray.v);
			if (color[3] > Ray.EPSILON) {
				ray.color.set(color);
				float[] biomeColor = ray.getBiomeColor();
				ray.color.x *= biomeColor[0];
				ray.color.y *= biomeColor[1];
				ray.color.z *= biomeColor[2];
				ray.distance += ray.tNear;
				ray.x.scaleAdd(ray.tNear, ray.d, ray.x);
				return true;
			}
		}
		return false;
	}
}
