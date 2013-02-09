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

/**
 * Flower pot block.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class FlowerPotModel {
	private static final AABB[] boxes = {
		// east
		new AABB(10/16., 11/16., 0, 6/16., 5/16., 11/16.),
		// west
		new AABB(5/16., 6/16., 0, 6/16., 5/16., 11/16.),
		// north
		new AABB(5/16., 11/16., 0, 6/16., 5/16., 6/16.),
		// south
		new AABB(5/16., 11/16., 0, 6/16., 10/16., 11/16.),
		// center
		new AABB(6/16., 10/16., 0, 4/16., 6/16., 10/16.),
	};
	
	private static final Texture[] tex = {
		Texture.flowerPot,
		Texture.flowerPot,
		Texture.flowerPot,
		Texture.flowerPot,
		Texture.dirt,
	};
	
	/**
	 * Find intersection between ray and block
	 * @param ray
	 * @return <code>true</code> if the ray intersected the block
	 */
	public static boolean intersect(Ray ray) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < boxes.length; ++i) {
			if (boxes[i].intersect(ray)) {
				tex[i].getColor(ray);
				ray.t = ray.tNear;
				hit = true;
			}
		}
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
		}
		return hit;
	}
}
