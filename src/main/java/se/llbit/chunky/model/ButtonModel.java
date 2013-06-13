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
import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class ButtonModel {
	protected static Quad[] quads = {
		// front
		new Quad(new Vector3d(.6875, .375, .875), new Vector3d(.3125, .375, .875),
				new Vector3d(.6875, .625, .875), new Vector4d(.6875, .3125, .375, .625)),

		// back
		new Quad(new Vector3d(.3125, .375, 1), new Vector3d(.6875, .375, 1),
				new Vector3d(.3125, .625, 1), new Vector4d(.3125, .6875, .375, .625)),

		// right
		new Quad(new Vector3d(.3125, .375, .875), new Vector3d(.3125, .375, 1),
				new Vector3d(.3125, .625, .875), new Vector4d(.875, 1, .375, .625)),

		// left
		new Quad(new Vector3d(.6875, .375, 1), new Vector3d(.6875, .375, .875),
				new Vector3d(.6875, .625, 1), new Vector4d(1, .875, .375, .625)),

		// top
		new Quad(new Vector3d(.6875, .625, .875), new Vector3d(.3125, .625, .875),
				new Vector3d(.6875, .625, 1), new Vector4d(.6875, .3125, .875, 1)),

		// bottom
		new Quad(new Vector3d(.3125, .375, .875), new Vector3d(.6875, .375, .875),
				new Vector3d(.3125, .375, 1), new Vector4d(.3125, .6875, .875, 1)),

	};

	private static Quad[][] rot = new Quad[4][];
	private static final int[] index = { 0, 1, 3, 2 };

	static {
		rot[0] = quads;
		for (int angle = 1; angle < 4; ++angle) {
			rot[angle] = new Quad[quads.length];
			for (int i = 0; i < quads.length; ++i) {
				rot[angle][i] = rot[angle-1][i].getYRotated();
			}
		}
	}

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		Quad[] rotated = rot[index[ray.getBlockData() & 3]];
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : rotated) {
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
