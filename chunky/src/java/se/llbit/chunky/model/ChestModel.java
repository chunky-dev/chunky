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
 * Chests, large chests and ender chests
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChestModel {
	// single chest facing north
	protected static final Quad[] single = {
		// north
		new Quad(new Vector3d(.9375, 0, .0625), new Vector3d(.0625, 0, .0625),
				new Vector3d(.9375, .875, .0625), new Vector4d(.9375, .0625, .0625, .9375)),

		// south
		new Quad(new Vector3d(.0625, 0, .9375), new Vector3d(.9375, 0, .9375),
				new Vector3d(.0625, .875, .9375), new Vector4d(.0625, .9375, .0625, .9375)),

		// west
		new Quad(new Vector3d(.0625, 0, .0625), new Vector3d(.0625, 0, .9375),
				new Vector3d(.0625, .875, .0625), new Vector4d(.0625, .9375, .0625, .9375)),

		// east
		new Quad(new Vector3d(.9375, 0, .9375), new Vector3d(.9375, 0, .0625),
				new Vector3d(.9375, .875, .9375), new Vector4d(.9375, .0625, .0625, .9375)),

		// top
		new Quad(new Vector3d(.9375, .875, .0625), new Vector3d(.0625, .875, .0625),
				new Vector3d(.9375, .875, .9375), new Vector4d(.9375, .0625, .0625, .9375)),

		// bottom
		new Quad(new Vector3d(.0625, 0, .0625), new Vector3d(.9375, 0, .0625),
				new Vector3d(.0625, 0, .9375), new Vector4d(.0625, .9375, .0625, .9375)),

		// -- lock

		// north
		new Quad(new Vector3d(.5626, .4375, 0), new Vector3d(.4375, .4375, 0),
				new Vector3d(.5626, .6875, 0), new Vector4d(.25, .125, .625, .875)),

		// west
		new Quad(new Vector3d(.4375, .4375, 0), new Vector3d(.4375, .4375, .0625),
				new Vector3d(.4375, .6875, 0), new Vector4d(.25, .3125, .625, .875)),

		// east
		new Quad(new Vector3d(.5626, .4375, .0625), new Vector3d(.5626, .4375, 0),
				new Vector3d(.5626, .6875, .0625), new Vector4d(.0625, .125, .625, .875)),

		// top
		new Quad(new Vector3d(.5626, .6875, 0), new Vector3d(.4375, .6875, 0),
				new Vector3d(.5626, .6875, .0625), new Vector4d(.125, .25, .875, .9375)),

		// bottom
		new Quad(new Vector3d(.4375, .4375, 0), new Vector3d(.5626, .4375, 0),
			new Vector3d(.4375, .4375, .0625), new Vector4d(.375, .25, .875, .9375)),

	};

	// left part of large chest facing north
	protected static final Quad[] left = {
		// north
		new Quad(new Vector3d(1, 0, .0625), new Vector3d(.0625, 0, .0625),
				new Vector3d(1, .875, .0625), new Vector4d(1, .0625, .0625, .9375)),

		// south
		new Quad(new Vector3d(.0625, 0, .9375), new Vector3d(1, 0, .9375),
				new Vector3d(.0625, .875, .9375), new Vector4d(.0625, 1, .0625, .9375)),

		// west
		new Quad(new Vector3d(.0625, 0, .0625), new Vector3d(.0625, 0, .9375),
				new Vector3d(.0625, .875, .0625), new Vector4d(.0625, .9375, .0625, .9375)),

		// top
		new Quad(new Vector3d(1, .875, .0625), new Vector3d(.0625, .875, .0625),
				new Vector3d(1, .875, .9375), new Vector4d(1, .0625, .0625, .9375)),

		// bottom
		new Quad(new Vector3d(.0625, 0, .0625), new Vector3d(1, 0, .0625),
				new Vector3d(.0625, 0, .9375), new Vector4d(.0625, 1, .0625, .9375)),

		// -- lock

		// north
		new Quad(new Vector3d(1, .4375, 0), new Vector3d(.9375, .4375, 0),
				new Vector3d(1, .6875, 0), new Vector4d(.1875, .125, .625, .875)),

		// west
		new Quad(new Vector3d(.9375, .4375, 0), new Vector3d(.9375, .4375, .0625),
				new Vector3d(.9375, .6875, 0), new Vector4d(.25, .3125, .625, .875)),

		// top
		new Quad(new Vector3d(1, .6875, 0), new Vector3d(.9375, .6875, 0),
				new Vector3d(1, .6875, .0625), new Vector4d(.125, .1875, .875, .9375)),

		// bottom
		new Quad(new Vector3d(.9375, .4375, 0), new Vector3d(1, .4375, 0),
			new Vector3d(.9375, .4375, .0625), new Vector4d(.375, .3125, .875, .9375)),
	};

	// right part of large chest facing north
	protected static final Quad[] right = {
		// north
		new Quad(new Vector3d(.9375, 0, .0625), new Vector3d(0, 0, .0625),
				new Vector3d(.9375, .875, .0625), new Vector4d(.9375, 0, .0625, .9375)),

		// south
		new Quad(new Vector3d(0, 0, .9375), new Vector3d(.9375, 0, .9375),
				new Vector3d(0, .875, .9375), new Vector4d(0, .9375, .0625, .9375)),

		// east
		new Quad(new Vector3d(.9375, 0, .9375), new Vector3d(.9375, 0, .0625),
				new Vector3d(.9375, .875, .9375), new Vector4d(.9375, .0625, .0625, .9375)),

		// top
		new Quad(new Vector3d(.9375, .875, .0625), new Vector3d(0, .875, .0625),
				new Vector3d(.9375, .875, .9375), new Vector4d(.9375, 0, .0625, .9375)),

		// bottom
		new Quad(new Vector3d(0, 0, .0625), new Vector3d(.9375, 0, .0625),
				new Vector3d(0, 0, .9375), new Vector4d(0, .9375, .0625, .9375)),

		// -- lock

		// north
		new Quad(new Vector3d(.0625, .4375, 0), new Vector3d(0, .4375, 0),
				new Vector3d(.0625, .6875, 0), new Vector4d(.25, .1875, .625, .875)),

		// east
		new Quad(new Vector3d(.0625, .4375, .0625), new Vector3d(.0625, .4375, 0),
				new Vector3d(.0625, .6875, .0625), new Vector4d(.0625, .125, .625, .875)),

		// top
		new Quad(new Vector3d(.0625, .6875, 0), new Vector3d(0, .6875, 0),
				new Vector3d(.0625, .6875, .0625), new Vector4d(.1875, .25, .875, .9375)),

		// bottom
		new Quad(new Vector3d(0, .4375, 0), new Vector3d(.0625, .4375, 0),
			new Vector3d(0, .4375, .0625), new Vector4d(.3125, .25, .875, .9375)),
	};

	protected static final Quad[][][] variants = new Quad[3][6][];

	static {
		variants[0][0] = variants[0][1] = new Quad[0];
		variants[0][2] = single;
		variants[1][0] = variants[1][1] = new Quad[0];
		variants[1][2] = left;
		variants[2][0] = variants[2][1] = new Quad[0];
		variants[2][2] = right;
		rotateFaceY(2, 5);
		rotateFaceY(5, 3);
		rotateFaceY(3, 4);
	}

	private static void rotateFaceY(int i, int j) {
		for (int v = 0; v < 3; ++v) {
			variants[v][j] = Model.rotateY(variants[v][i]);
		}
	}

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray, Texture[] texture) {
		boolean hit = false;
		Quad[] rot = variants[(ray.currentMaterial >> 16) % 3]
				[ray.getBlockData() % 6];
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < rot.length; ++i) {
			Quad side = rot[i];
			if (side.intersect(ray)) {
				texture[i].getColor(ray);
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
