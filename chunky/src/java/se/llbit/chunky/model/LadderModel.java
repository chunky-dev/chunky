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
public class LadderModel {
	protected static Quad[] quads = {
		// west
		new Quad(new Vector3d(1, 0, 0), new Vector3d(1, 0, 1),
				new Vector3d(1, 1, 0), new Vector4d(0, 1, 0, 1)),

		// east
		new Quad(new Vector3d(0, 0, 1), new Vector3d(0, 0, 0),
				new Vector3d(0, 1, 1), new Vector4d(1, 0, 0, 1)),

		// north
		new Quad(new Vector3d(1, 0, 1), new Vector3d(0, 0, 1),
				new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),

		// south
		new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
				new Vector3d(0, 1, 0), new Vector4d(0, 1, 0, 1)),


	};

	public static boolean intersect(Ray ray) {
		Quad quad = quads[ray.getBlockData() % 4];
		ray.t = Double.POSITIVE_INFINITY;
		if (quad.intersect(ray)) {
			float[] color = Texture.ladder.getColor(ray.u, ray.v);
			if (color[3] > Ray.EPSILON) {
				ray.color.set(color);
				ray.n.set(quad.n);
				ray.distance += ray.tNear;
				ray.x.scaleAdd(ray.tNear, ray.d);
				return true;
			}
		}
		return false;
	}
}
