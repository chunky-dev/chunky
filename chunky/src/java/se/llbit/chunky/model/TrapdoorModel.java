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
public class TrapdoorModel {
	private static Quad[][] faces = {
		// low
		{
			// front
			new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
					new Vector3d(1, .1875, 0), new Vector4d(1, 0, 0, .1875)),

			// back
			new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
					new Vector3d(0, .1875, 1), new Vector4d(0, 1, 0, .1875)),

			// right
			new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
					new Vector3d(0, .1875, 0), new Vector4d(0, 1, 0, .1875)),

			// left
			new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
					new Vector3d(1, .1875, 1), new Vector4d(1, 0, 0, .1875)),

			// top
			new Quad(new Vector3d(1, .1875, 0), new Vector3d(0, .1875, 0),
					new Vector3d(1, .1875, 1), new Vector4d(1, 0, 0, 1)),

			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),
		},

		// high
		{
			// front
			new Quad(new Vector3d(1, .8125, 0), new Vector3d(0, .8125, 0),
					new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, .1875)),

			// back
			new Quad(new Vector3d(0, .8125, 1), new Vector3d(1, .8125, 1),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, .1875)),

			// right
			new Quad(new Vector3d(0, .8125, 0), new Vector3d(0, .8125, 1),
					new Vector3d(0, 1, 0), new Vector4d(0, 1, 0, .1875)),

			// left
			new Quad(new Vector3d(1, .8125, 1), new Vector3d(1, .8125, 0),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, .1875)),

			// top
			new Quad(new Vector3d(1, 1, 0), new Vector3d(0, 1, 0),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),

			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),
		},

		// facing north
		{
			// north
			new Quad(new Vector3d(1, 0, .8125), new Vector3d(0, 0, .8125),
					new Vector3d(1, 1, .8125), new Vector4d(1, 0, 0, 1)),

			// south
			new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),

			// west
			new Quad(new Vector3d(0, 0, .8125), new Vector3d(0, 0, 1),
					new Vector3d(0, 1, .8125), new Vector4d(.8125, 1, 0, 1)),

			// east
			new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, .8125),
					new Vector3d(1, 1, 1), new Vector4d(1, .8125, 0, 1)),

			// top
			new Quad(new Vector3d(1, 1, .8125), new Vector3d(0, 1, .8125),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, .8125, 1)),

			// bottom
			new Quad(new Vector3d(0, 0, .8125), new Vector3d(1, 0, .8125),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, .8125, 1)),
		},

		// facing south
		{
			// north
			new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
					new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, 1)),

			// south
			new Quad(new Vector3d(0, 0, .1875), new Vector3d(1, 0, .1875),
					new Vector3d(0, 1, .1875), new Vector4d(0, 1, 0, 1)),

			// west
			new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, .1875),
					new Vector3d(0, 1, 0), new Vector4d(0, .1875, 0, 1)),

			// east
			new Quad(new Vector3d(1, 0, .1875), new Vector3d(1, 0, 0),
					new Vector3d(1, 1, .1875), new Vector4d(.1875, 0, 0, 1)),

			// top
			new Quad(new Vector3d(1, 1, 0), new Vector3d(0, 1, 0),
					new Vector3d(1, 1, .1875), new Vector4d(1, 0, 0, .1875)),

			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, .1875), new Vector4d(0, 1, 0, .1875)),
		},

		// facing west
		{
			// north
			new Quad(new Vector3d(1, 0, 0), new Vector3d(.8125, 0, 0),
					new Vector3d(1, 1, 0), new Vector4d(1, .8125, 0, 1)),

			// south
			new Quad(new Vector3d(.8125, 0, 1), new Vector3d(1, 0, 1),
					new Vector3d(.8125, 1, 1), new Vector4d(.8125, 1, 0, 1)),

			// west
			new Quad(new Vector3d(.8125, 0, 0), new Vector3d(.8125, 0, 1),
					new Vector3d(.8125, 1, 0), new Vector4d(0, 1, 0, 1)),

			// east
			new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),

			// top
			new Quad(new Vector3d(1, 1, 0), new Vector3d(.8125, 1, 0),
					new Vector3d(1, 1, 1), new Vector4d(1, .8125, 0, 1)),

			// bottom
			new Quad(new Vector3d(.8125, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(.8125, 0, 1), new Vector4d(.8125, 1, 0, 1)),
		},

		// facing east
		{
			// north
			new Quad(new Vector3d(.1875, 0, 0), new Vector3d(0, 0, 0),
					new Vector3d(.1875, 1, 0), new Vector4d(.1875, 0, 0, 1)),

			// south
			new Quad(new Vector3d(0, 0, 1), new Vector3d(.1875, 0, 1),
					new Vector3d(0, 1, 1), new Vector4d(0, .1875, 0, 1)),

			// west
			new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
					new Vector3d(0, 1, 0), new Vector4d(0, 1, 0, 1)),

			// east
			new Quad(new Vector3d(.1875, 0, 1), new Vector3d(.1875, 0, 0),
					new Vector3d(.1875, 1, 1), new Vector4d(1, 0, 0, 1)),

			// top
			new Quad(new Vector3d(.1875, 1, 0), new Vector3d(0, 1, 0),
					new Vector3d(.1875, 1, 1), new Vector4d(.1875, 0, 0, 1)),

			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(.1875, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, .1875, 0, 1)),
		},
	};

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		Quad[] state;
		int data = ray.getBlockData();
		ray.t = Double.POSITIVE_INFINITY;
		if ((ray.getBlockData() & 4) == 0) {
			// not open - top or bottom?
			state = faces[data>>3];
		} else {
			// open
			state = faces[(data & 3) + 2];
		}
		for (Quad face : state) {
			if (face.intersect(ray)) {
				float[] color = texture.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.n.set(face.n);
					ray.t = ray.tNext;
					hit = true;
				}
			}
		}
		if (hit) {
			ray.distance += ray.t;
			ray.o.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
