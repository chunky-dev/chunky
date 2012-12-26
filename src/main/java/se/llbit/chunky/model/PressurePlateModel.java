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
public class PressurePlateModel {
	private static Quad[] quads = {
		// front
		new Quad(new Vector3d(.8, 0, .2), new Vector3d(.2, 0, .2),
				new Vector3d(.8, .1, .2), new Vector4d(.8, .2, 0, .1)),

		// back
		new Quad(new Vector3d(.2, 0, .8), new Vector3d(.8, 0, .8),
				new Vector3d(.2, .1, .8), new Vector4d(.2, .8, 0, .1)),
		
		// right
		new Quad(new Vector3d(.2, 0, .2), new Vector3d(.2, 0, .8),
				new Vector3d(.2, .1, .2), new Vector4d(.2, .8, 0, .1)),

		// left
		new Quad(new Vector3d(.8, 0, .8), new Vector3d(.8, 0, .2),
				new Vector3d(.8, .1, .8), new Vector4d(.8, .2, 0, .1)),

		// top
		new Quad(new Vector3d(.8, .1, .2), new Vector3d(.2, .1, .2),
				new Vector3d(.8, .1, .8), new Vector4d(.8, .2, 0, .8)),
		
		// bottom
		new Quad(new Vector3d(.2, 0, .2), new Vector3d(.8, 0, .2),
				new Vector3d(.2, 0, .8), new Vector4d(.2, .8, 0, .8)),
				
		};

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : quads) {
			if (quad.intersect(ray)) {
				texture.getColor(ray);
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
