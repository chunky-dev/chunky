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

@SuppressWarnings("javadoc")
public class ComparatorModel {
	private static Quad[] north = {
		// front
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
				new Vector3d(1, .125, 0), new Vector4d(1, 0, 0, .125)),

		// back
		new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
				new Vector3d(0, .125, 1), new Vector4d(0, 1, 0, .125)),

		// right
		new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
				new Vector3d(0, .125, 0), new Vector4d(0, 1, 0, .125)),

		// left
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
				new Vector3d(1, .125, 1), new Vector4d(1, 0, 0, .125)),

		// top
		new Quad(new Vector3d(1, .125, 0), new Vector3d(0, .125, 0),
				new Vector3d(1, .125, 1), new Vector4d(1, 0, 1, 0)),
	};

	private static Quad[] torchHigh = {
		new Quad(new Vector3d(.75, 2/16., 7/16.), new Vector3d(4/16., 2/16., 7/16.),
				new Vector3d(.75, 13/16., 7/16.), new Vector4d(12/16., 4/16., 5/16., 1)),

		new Quad(new Vector3d(4/16., 2/16., 9/16.), new Vector3d(.75, 2/16., 9/16.),
				new Vector3d(4/16., 13/16., 9/16.), new Vector4d(4/16., .75, 5/16., 1)),

		new Quad(new Vector3d(7/16., 2/16., 4/16.), new Vector3d(7/16., 2/16., .75),
				new Vector3d(7/16., 13/16., 4/16.), new Vector4d(4/16., .75, 5/16., 1)),

		new Quad(new Vector3d(9/16., 2/16., .75), new Vector3d(9/16., 2/16., 4/16.),
				new Vector3d(9/16., 13/16., .75), new Vector4d(.75, 4/16., 5/16., 1)),

		// top
		new Quad(new Vector3d(7/16., 7/16., 9/16.), new Vector3d(9/16., 7/16., 9/16.),
				new Vector3d(7/16., 7/16., 7/16.), new Vector4d(7/16., 9/16., 8/16., .625)),
	};

	// Lowered 3 pixels from high version
	private static Quad[] torchLow = {
		new Quad(new Vector3d(.75, 2/16., 7/16.), new Vector3d(4/16., 2/16., 7/16.),
				new Vector3d(.75, 10/16., 7/16.), new Vector4d(12/16., 4/16., 8/16., 1)),

		new Quad(new Vector3d(4/16., 2/16., 9/16.), new Vector3d(.75, 2/16., 9/16.),
				new Vector3d(4/16., 10/16., 9/16.), new Vector4d(4/16., .75, 8/16., 1)),

		new Quad(new Vector3d(7/16., 2/16., 4/16.), new Vector3d(7/16., 2/16., .75),
				new Vector3d(7/16., 10/16., 4/16.), new Vector4d(4/16., .75, 8/16., 1)),

		new Quad(new Vector3d(9/16., 2/16., .75), new Vector3d(9/16., 2/16., 4/16.),
				new Vector3d(9/16., 10/16., .75), new Vector4d(.75, 4/16., 8/16., 1)),

		// top
		new Quad(new Vector3d(7/16., 4/16., 9/16.), new Vector3d(9/16., 4/16., 9/16.),
				new Vector3d(7/16., 4/16., 7/16.), new Vector4d(7/16., 9/16., 8/16., .625)),
	};

	private static Quad[][][] torch1 = new Quad[2][4][];
	private static Quad[][][] torch2 = new Quad[2][4][];
	private static Quad[][][] torch3 = new Quad[2][4][];

	private static final Quad[][] rot = new Quad[4][];

	private static final Texture[] blockTex = {
		Texture.comparatorOff,
		Texture.comparatorOn,
	};

	private static final Texture[] torchTex = {
		Texture.redstoneTorchOff,
		Texture.redstoneTorchOn,
	};

	static {
		rot[0] = north;
		rot[1] = Model.rotateY(rot[0]);
		rot[2] = Model.rotateY(rot[1]);
		rot[3] = Model.rotateY(rot[2]);

		torch1[0][0] = Model.translate(torchLow, 0, 0, -5/16.);
		torch1[0][1] = Model.rotateY(torch1[0][0]);
		torch1[0][2] = Model.rotateY(torch1[0][1]);
		torch1[0][3] = Model.rotateY(torch1[0][2]);

		torch1[1][0] = Model.translate(torchHigh, 0, 0, -5/16.);
		torch1[1][1] = Model.rotateY(torch1[1][0]);
		torch1[1][2] = Model.rotateY(torch1[1][1]);
		torch1[1][3] = Model.rotateY(torch1[1][2]);

		torch2[0][0] = Model.translate(torchHigh, 3/16., 0, 4/16.);
		torch2[0][1] = Model.rotateY(torch2[0][0]);
		torch2[0][2] = Model.rotateY(torch2[0][1]);
		torch2[0][3] = Model.rotateY(torch2[0][2]);

		torch2[1][0] = Model.translate(torchHigh, 3/16., 0, 4/16.);
		torch2[1][1] = Model.rotateY(torch2[1][0]);
		torch2[1][2] = Model.rotateY(torch2[1][1]);
		torch2[1][3] = Model.rotateY(torch2[1][2]);

		torch3[0][0] = Model.translate(torchHigh, 3/16., 0, 4/16.);
		torch3[0][1] = Model.rotateY(torch3[0][0]);
		torch3[0][2] = Model.rotateY(torch3[0][1]);
		torch3[0][3] = Model.rotateY(torch3[0][2]);

		torch3[1][0] = Model.translate(torchHigh, 3/16., 0, 4/16.);
		torch3[1][1] = Model.rotateY(torch3[1][0]);
		torch3[1][2] = Model.rotateY(torch3[1][1]);
		torch3[1][3] = Model.rotateY(torch3[1][2]);
	}

	/**
	 * @param ray
	 * @param lit 0 or 1
	 * @return <code>true</code> if the block was intersected
	 */
	public static boolean intersect(Ray ray, int lit) {
		boolean hit = false;
		int data = ray.getBlockData();
		int direction = data & 3;
		int active = (data >> 2) & 1;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad face: rot[direction]) {
			if (face.intersect(ray)) {
				blockTex[lit].getColor(ray);
				ray.n.set(face.n);
				ray.t = ray.tNear;
				hit = true;
			}
		}
		for (Quad face: torch1[active][direction]) {
			if (face.intersect(ray)) {
				float[] color = torchTex[active].getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.n.set(face.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		for (Quad face: torch2[lit][direction]) {
			if (face.intersect(ray)) {
				float[] color = torchTex[lit].getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.n.set(face.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		/*for (Quad face: torch2[delay][direction]) {
			if (face.intersect(ray)) {
				float[] color = torchTex[on].getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.n.set(face.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}*/
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
		}
		return hit;
	}
}
