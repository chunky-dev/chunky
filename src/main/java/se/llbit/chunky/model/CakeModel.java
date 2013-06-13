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

@SuppressWarnings("javadoc")
public class CakeModel {
	private static Quad[] quads = {
		// front
		new Quad(new Vector3d(.9375, 0, .0625), new Vector3d(.0625, 0, .0625),
				new Vector3d(.9375, .5, .0625), new Vector4d(.9375, .0625, 0, .5)),

		// back
		new Quad(new Vector3d(.0625, 0, .9375), new Vector3d(.9375, 0, .9375),
				new Vector3d(.0625, .5, .9375), new Vector4d(.0625, .9375, 0, .5)),

		// right
		new Quad(new Vector3d(.0625, 0, .0625), new Vector3d(.0625, 0, .9375),
				new Vector3d(.0625, .5, .0625), new Vector4d(.0625, .9375, 0, .5)),

		// left
		new Quad(new Vector3d(.9375, 0, .9375), new Vector3d(.9375, 0, .0625),
				new Vector3d(.9375, .5, .9375), new Vector4d(.9375, .0625, 0, .5)),

		// top
		new Quad(new Vector3d(.9375, .5, .0625), new Vector3d(.0625, .5, .0625),
				new Vector3d(.9375, .5, .9375), new Vector4d(.9375, .0625, .0625, .9375)),

		// bottom
		new Quad(new Vector3d(.0625, 0, .0625), new Vector3d(.9375, 0, .0625),
				new Vector3d(.0625, 0, .9375), new Vector4d(.0625, .9375, .0625, .9375)),
	};

	public static boolean intersect(Ray ray) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : quads) {
			if (quad.intersect(ray)) {
				if (quad.n.y > 0)
					Texture.cakeTop.getColor(ray);
				else if (quad.n.y < 0)
					Texture.cakeBottom.getColor(ray);
				else
					Texture.cakeSide.getColor(ray);
				ray.n.set(quad.n);
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
