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
public class StairModel {
	private static AABB[][][] corners = {
		{
			{
				// s-e
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0.5, 1, 0.5, 1, 0.5, 1),
			},
			{
				// s-w
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 0.5, 0.5, 1, 0.5, 1),
			},
			{
				// n-e
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0.5, 1, 0.5, 1, 0, 0.5),
			},
			{
				// n-w
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 0.5, 0.5, 1, 0, 0.5),
			},
			{
				// inner s-e
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 1, 0.5, 1, 0.5, 1),
				new AABB(0.5, 1, 0.5, 1, 0, 0.5),
			},
			{
				// inner s-w
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0.5, 1, 0.5, 1, 0.5, 1),
				new AABB(0, 0.5, 0.5, 1, 0, 1),
			},
			{
				// inner n-e
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0.5, 1, 0.5, 1, 0, 1),
				new AABB(0, 0.5, 0.5, 1, 0, 0.5),
			},
			{
				// inner n-w
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 1, 0.5, 1, 0, 0.5),
				new AABB(0, 0.5, 0.5, 1, 0.5, 1),
			},
		},
		{
			// flipped
			{
				// s-e
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0.5, 1, 0, 0.5, 0.5, 1),
			},
			{
				// s-w
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 0.5, 0, 0.5, 0.5, 1),
			},
			{
				// n-e
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0.5, 1, 0, 0.5, 0, 0.5),
			},
			{
				// n-w
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 0.5, 0, 0.5, 0, 0.5),
			},
			{
				// inner s-e
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 1, 0, 0.5, 0.5, 1),
				new AABB(0.5, 1, 0, 0.5, 0, 0.5),
			},
			{
				// inner s-w
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0.5, 1, 0, 0.5, 0.5, 1),
				new AABB(0, 0.5, 0, 0.5, 0, 1),
			},
			{
				// inner n-e
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0.5, 1, 0, 0.5, 0, 1),
				new AABB(0, 0.5, 0, 0.5, 0, 0.5),
			},
			{
				// inner n-w
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 1, 0, 0.5, 0, 0.5),
				new AABB(0, 0.5, 0, 0.5, 0.5, 1),
			},
		},
	};
	private static final AABB[][][] stairs = {
		{
			{
				// ascending east
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0.5, 1, 0.5, 1, 0, 1),
			},
			{
				// ascending west
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 0.5, 0.5, 1, 0, 1),
			},
			{
				// ascending south
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 1, 0.5, 1, 0.5, 1),
			},
			{
				// ascending north
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 1, 0.5, 1, 0, 0.5),
			},
		},
		{
			// flipped
			{
				// ascending east
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0.5, 1, 0, 0.5, 0, 1),
			},
			{
				// ascending west
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 0.5, 0, 0.5, 0, 1),
			},
			{
				// ascending south
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 1, 0, 0.5, 0.5, 1),
			},
			{
				// ascending north
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 1, 0, 0.5, 0, 0.5),
			},
		},
	};

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		int flipped = (ray.getBlockData() & 4) >> 2;
		int corner = 15 & (ray.currentMaterial >> BlockData.CORNER_OFFSET);
		int rotation = 3 & ray.getBlockData();

		ray.t = Double.POSITIVE_INFINITY;

		if (corner != 0) {
			for (AABB box : corners[flipped][7 & corner]) {
				if (box.intersect(ray)) {
					texture.getColor(ray);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		} else {
			for (AABB box : stairs[flipped][rotation]) {
				if (box.intersect(ray)) {
					texture.getColor(ray);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}

		if (hit) {
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
		}
		return hit;
	}

	public static boolean intersect(Ray ray, Texture texture,
			Texture textureTop, Texture textureBottom) {

		boolean hit = intersect(ray, texture);
		if (hit) {
			if (ray.n.y > 0) {
				textureTop.getColor(ray);
			} else if (ray.n.y < 0) {
				textureTop.getColor(ray);
			}
		}
		return hit;
	}
}
