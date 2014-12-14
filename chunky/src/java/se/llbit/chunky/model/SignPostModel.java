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
 * A generic block.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SignPostModel {
	// facing south
	protected static Quad[] sides = {
		// front
		new Quad(new Vector3d(0, .5, 9/16.), new Vector3d(1, .5, 9/16.),
				new Vector3d(0, 1, 9/16.), new Vector4d(2/64., 26/64., 18/32., 30/32.)),

		// back
		new Quad(new Vector3d(1, .5, 7/16.), new Vector3d(0, .5, 7/16.),
				new Vector3d(1, 1, 7/16.), new Vector4d(28/64., 52/64., 18/32., 30/32.)),

		// left
		new Quad(new Vector3d(0, .5, 7/16.), new Vector3d(0, .5, 9/16.),
				new Vector3d(0, 1, 7/16.), new Vector4d(0, 2/64., 18/32., 30/32.)),

		// right
		new Quad(new Vector3d(1, .5, 9/16.), new Vector3d(1, .5, 7/16.),
				new Vector3d(1, 1, 9/16.), new Vector4d(26/64., 28/64., 18/32., 30/32.)),

		// top
		new Quad(new Vector3d(1, 1, 7/16.), new Vector3d(0, 1, 7/16.),
				new Vector3d(1, 1, 9/16.), new Vector4d(2/64., 26/64., 1, 30/32.)),

		// bottom
		new Quad(new Vector3d(0, .5, 7/16.), new Vector3d(1, .5, 7/16.),
				new Vector3d(0, .5, 9/16.), new Vector4d(26/64., 50/64., 1, 30/32.)),

		// post front
		new Quad(new Vector3d(7/16., 0, 9/16.), new Vector3d(9/16., 0, 9/16.),
				new Vector3d(7/16., .5, 9/16.), new Vector4d(2/64., 4/64., 2/32., 16/32.)),

		// post back
		new Quad(new Vector3d(9/16., 0, 7/16.), new Vector3d(7/16., 0, 7/16.),
				new Vector3d(9/16., .5, 7/16.), new Vector4d(4/64., 6/64., 2/32., 16/32.)),

		// post left
		new Quad(new Vector3d(7/16., 0, 7/16.), new Vector3d(7/16., 0, 9/16.),
				new Vector3d(7/16., .5, 7/16.), new Vector4d(0, 2/64., 2/32., 16/32.)),

		// post right
		new Quad(new Vector3d(9/16., 0, 9/16.), new Vector3d(9/16., 0, 7/16.),
				new Vector3d(9/16., .5, 9/16.), new Vector4d(6/64., 8/64., 2/32., 16/32.)),

		// post bottom
		new Quad(new Vector3d(7/16., 0, 7/16.), new Vector3d(9/16., 0, 7/16.),
				new Vector3d(7/16., 0, 9/16.), new Vector4d(4/64., 6/64., 16/32., 18/32.)),

	};

	private static final Quad[][] rot = new Quad[16][];

	static {
		// rotate the sign post to face the correct direction
		rot[0] = sides;
		for (int i = 1; i < 16; ++i) {
			rot[i] = Model.rotateY(sides, - i * Math.PI/8);
		}
	}

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray) {
		boolean hit = false;
		int angle = ray.getBlockData() & 0xF;
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < sides.length; ++i) {
			Quad side = rot[angle][i];
			if (side.intersect(ray)) {
				Texture.signPost.getColor(ray);
				ray.n.set(side.n);
				ray.t = ray.tNear;
				hit = true;
			}
		}
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
