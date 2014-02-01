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
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

/**
 * Anvil block.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AnvilModel {
	private static final AABB[][] boxes = {
		// north-south
		{
			new AABB(3/16., 13/16., 10/16., 1, 0, 1),
			new AABB(2/16., 14/16., 0, 4/16., 2/16., 14/16.),
			new AABB(4/16., 12/16., 4/16., 5/16., 3/16., 13/16.),
			new AABB(6/16., 10/16., 5/16., 10/16., 4/16., 12/16.),
		},
		// east-west
		{
			new AABB(0, 1, 10/16., 1, 3/16., 13/16.),
			new AABB(2/16., 14/16., 0, 4/16., 2/16., 14/16.),
			new AABB(3/16., 13/16., 4/16., 5/16., 4/16., 12/16.),
			new AABB(4/16., 12/16., 5/16., 10/16., 6/16., 10/16.),
		},
	};

	public static final Texture[] topTexture = {
		Texture.anvilTop, Texture.anvilTopDamaged1, Texture.anvilTopDamaged2,
		Texture.anvilTopDamaged2
	};

	/**
	 * Find intersection between ray and block
	 * @param ray
	 * @return <code>true</code> if the ray intersected the block
	 */
	public static boolean intersect(Ray ray) {
		int data = ray.currentMaterial >> BlockData.OFFSET;
		int orientation = 1 & data;
		int damage = 3 & (data >> 2);
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < boxes[0].length; ++i) {
			if (boxes[orientation][i].intersect(ray)) {
				if (i == 0 && ray.n.y > 0) {
					double tmp = ray.v;
					ray.v = ray.u * orientation + tmp * (1-orientation);
					ray.u = tmp * orientation + ray.u * (1-orientation);
					topTexture[damage].getColor(ray);
				} else {
					Texture.anvilSide.getColor(ray);
				}
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
