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
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

@SuppressWarnings("javadoc")
public class FenceGateModel {
	private static AABB[] closed = {
			new AABB(0, .125, .3125, 1, .4375, .5625),
			new AABB(.375, .625, .375, .9375, .4375, .5625),
			new AABB(.875, 1, .3125, 1, .4375, .5625),

			new AABB(.125, .875, .375, .5625, .4375, .5625),
			new AABB(.125, .875, .75, .9375, .4375, .5625),
	};

	private static AABB[] open = {
		new AABB(0, .125, .3125, 1, .4375, .5625),
		new AABB(.875, 1, .3125, 1, .4375, .5625),

		new AABB(0, .125, .375, .5625, .5625, .8125),
		new AABB(0, .125, .75, .9375, .5625, .8125),
		new AABB(0, .125, .375, .9375, .8125, .9375),

		new AABB(.875, 1, .375, .5625, .5625, .8125),
		new AABB(.875, 1, .75, .9375, .5625, .8125),
		new AABB(.875, 1, .375, .9375, .8125, .9375),
};

	private static AABB[][][][] rot = new AABB[2][2][4][];

	static {
		rot[0][0][0] = closed;
		rot[0][0][1] = new AABB[closed.length];
		rot[0][0][2] = rot[0][0][0];
		rot[0][0][3] = rot[0][0][1];
		for (int i = 0; i < closed.length; ++i)
			rot[0][0][1][i] = closed[i].getYRotated();
		rot[0][1][0] = open;
		for (int j = 1; j < 4; ++j) {
			rot[0][1][j] = new AABB[rot[0][1][j-1].length];
			for (int i = 0; i < rot[0][1][j-1].length; ++i)
				rot[0][1][j][i] = rot[0][1][j-1][i].getYRotated();
		}
		for (int i = 0; i < rot[1].length; ++i) {
			for (int j = 0; j < rot[1][i].length; ++j) {
				rot[1][i][j] = new AABB[rot[0][i][j].length];
				for (int k = 0; k < rot[1][i][j].length; ++k) {
					rot[1][i][j][k] = rot[0][i][j][k].getTranslated(0, -3/16., 0);
				}
			}
		}
	}

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		int isOpen = (ray.getBlockData() >> 2) & 1;
		int direction = ray.getBlockData() & 3;
		int isLow = (ray.getCurrentData() >> BlockData.FENCEGATE_LOW) & 1;
		ray.t = Double.POSITIVE_INFINITY;
		for (AABB box : rot[isLow][isOpen][direction]) {
			if (box.intersect(ray)) {
				texture.getColor(ray);
				ray.t = ray.tNext;
				hit = true;
			}
		}
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.o.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
