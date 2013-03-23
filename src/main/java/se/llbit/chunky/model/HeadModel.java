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
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * A head block.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class HeadModel {
	private static final Quad[] quads = {
		// front
		new Quad(new Vector3d(12/16., 4/16., 4/16.), new Vector3d(4/16., 4/16., 4/16.),
				new Vector3d(12/16., 12/16., 4/16.), new Vector4d(12/16., 4/16., 4/16., 12/16.)),
		// back
		new Quad(new Vector3d(4/16., 4/16., 12/16.), new Vector3d(12/16., 4/16., 12/16.),
				new Vector3d(4/16., 12/16., 12/16.), new Vector4d(4/16., 12/16., 4/16., 12/16.)),

		// right
		new Quad(new Vector3d(4/16., 4/16., 4/16.), new Vector3d(4/16., 4/16., 12/16.),
				new Vector3d(4/16., 12/16., 4/16.), new Vector4d(4/16., 12/16., 4/16., 12/16.)),

		// left
		new Quad(new Vector3d(12/16., 4/16., 12/16.), new Vector3d(12/16., 4/16., 4/16.),
				new Vector3d(12/16., 12/16., 12/16.), new Vector4d(12/16., 4/16., 4/16., 12/16.)),

		// top
		new Quad(new Vector3d(12/16., 12/16., 4/16.), new Vector3d(4/16., 12/16., 4/16.),
				new Vector3d(12/16., 12/16., 12/16.), new Vector4d(12/16., 4/16., 4/16., 12/16.)),

		// bottom
		new Quad(new Vector3d(4/16., 4/16., 4/16.), new Vector3d(1, 4/16., 4/16.),
				new Vector3d(4/16., 4/16., 1), new Vector4d(4/16., 1, 4/16., 1)),
	};

	/**
	 * Find intersection between ray and block
	 * @param ray
	 * @param texture Block texture
	 * @return <code>true</code> if the ray intersected the block
	 */
	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : quads) {
			if (quad.intersect(ray)) {
				Texture.dirt.getColor(ray);
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
