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
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class DoorModel {
	protected static Quad[][] faces = {
		{
			// front
			new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
					new Vector3d(1, 1, 0), new Vector4d(0, 1, 0, 1)),

			// back
			new Quad(new Vector3d(0, 0, .1875), new Vector3d(1, 0, .1875),
					new Vector3d(0, 1, .1875), new Vector4d(1, 0, 0, 1)),

			// right
			new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, .1875),
					new Vector3d(0, 1, 0), new Vector4d(0, .1875, 0, 1)),

			// left
			new Quad(new Vector3d(1, 0, .1875), new Vector3d(1, 0, 0),
					new Vector3d(1, 1, .1875), new Vector4d(0, .1875, 0, 1)),

			// top
			new Quad(new Vector3d(0, 1, 0), new Vector3d(0, 1, .1875),
					new Vector3d(1, 1, 0), new Vector4d(0, .1875, 0, 1)),

			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, .1875), new Vector4d(0, 1, 0, .1875)),
		},

		{
			// front
			new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
					new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, 1)),

			// back
			new Quad(new Vector3d(0, 0, .1875), new Vector3d(1, 0, .1875),
					new Vector3d(0, 1, .1875), new Vector4d(0, 1, 0, 1)),

			// right
			new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, .1875),
					new Vector3d(0, 1, 0), new Vector4d(0, .1875, 0, 1)),

			// left
			new Quad(new Vector3d(1, 0, .1875), new Vector3d(1, 0, 0),
					new Vector3d(1, 1, .1875), new Vector4d(0, .1875, 0, 1)),

			// top
			new Quad(new Vector3d(0, 1, 0), new Vector3d(0, 1, .1875),
					new Vector3d(1, 1, 0), new Vector4d(0, .1875, 0, 1)),

			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, .1875), new Vector4d(0, 1, 0, .1875)),
		},
	};

	private static Quad[][][] rot = new Quad[2][4][];

	static {
		rot[0][1] = faces[0];
		rot[1][1] = faces[1];

		for (int mirror = 0; mirror < 2; ++mirror) {
			rot[mirror][2] = Model.rotateY(rot[mirror][1]);
		}

		for (int mirror = 0; mirror < 2; ++mirror) {
			rot[mirror][3] = Model.rotateY(rot[mirror][2]);
		}

		for (int mirror = 0; mirror < 2; ++mirror) {
			rot[mirror][0] = Model.rotateY(rot[mirror][3]);
		}
	}

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;

		int data = ray.currentMaterial;
		int top = 0xF & (data >> BlockData.DOOR_TOP);
		int bottom = 0xF & (data >> BlockData.DOOR_BOTTOM);

		int open = 1 & (bottom>>2);
		int mirrored = 1 & top;
		int direction = 3 & bottom;

		int rotation;
		if (open != 0 && mirrored != 0)
			rotation = (direction + 3)%4;
		else
			rotation = (direction + open)%4;
		int mirror = (mirrored + open) % 2;

		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : rot[mirror][rotation]) {
			if (quad.intersect(ray)) {
				float[] color = texture.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.n.set(quad.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		if (hit) {
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
		}
		return hit;
	}
}
