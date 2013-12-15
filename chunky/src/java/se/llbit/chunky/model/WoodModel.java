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
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * A block of wood.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class WoodModel {
	private static final Quad[] sides = {
		// north
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
				new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, 1)),

		// south
		new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
				new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),

		// west
		new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
				new Vector3d(0, 1, 0), new Vector4d(0, 1, 0, 1)),

		// east
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
				new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),

		// top
		new Quad(new Vector3d(1, 1, 0), new Vector3d(0, 1, 0),
				new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),

		// bottom
		new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
				new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),

	};

	private static final int[][] textureIndex = {
		{ 0, 0, 0, 0, 1, 1 },
		{ 0, 0, 1, 1, 0, 0 },
		{ 1, 1, 0, 0, 0, 0 },
		{ 0, 0, 0, 0, 0, 0 }
	};

	private static final int[][] uv = {
		{ 0, 0, 0, 0, 0, 0 },
		{ 1, 1, 0, 0, 1, 1 },
		{ 0, 0, 1, 1, 0, 0 },
		{ 0, 0, 0, 0, 0, 0 },
	};

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray, Texture[][] texture) {
		int data = ray.getBlockData();
		int direction = data >> 2;
		int type = data & 3;
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < sides.length; ++i) {
			Quad side = sides[i];

			if (side.intersect(ray)) {

				double u = ray.u;
				int uv_x = uv[direction][i];
				ray.u = (1-uv_x) * ray.u + uv_x * ray.v;
				ray.v = uv_x * u  + (1-uv_x) * ray.v;
				texture[type][textureIndex[direction][i]].getColor(ray);
				ray.n.set(side.n);
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
