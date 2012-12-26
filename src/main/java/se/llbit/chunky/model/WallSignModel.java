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
public class WallSignModel {
	
	// distance the sign is offset from the wall (mimic minecraft)
	private static final double offset = 0.02;
	
	private static Quad[][] faces = {
		{}, {},
		
		// facing north
		{
			// north
			new Quad(new Vector3d(1, .2, .875+offset), new Vector3d(0, .2, .875+offset),
					new Vector3d(1, .8, .875+offset), new Vector4d(1, 0, .2, .8)),

			// south
			new Quad(new Vector3d(0, .2, 1-offset), new Vector3d(1, .2, 1-offset),
					new Vector3d(0, .8, 1-offset), new Vector4d(0, 1, .2, .8)),
			
			// west
			new Quad(new Vector3d(0, .2, .875+offset), new Vector3d(0, .2, 1-offset),
					new Vector3d(0, .8, .875+offset), new Vector4d(.875+offset, 1-offset, .2, .8)),

			// east
			new Quad(new Vector3d(1, .2, 1-offset), new Vector3d(1, .2, .875+offset),
					new Vector3d(1, .8, 1-offset), new Vector4d(1-offset, .875+offset, .2, .8)),

			// top
			new Quad(new Vector3d(1, .8, .875+offset), new Vector3d(0, .8, .875+offset),
					new Vector3d(1, .8, 1-offset), new Vector4d(1, 0, .875+offset, 1-offset)),
			
			// bottom
			new Quad(new Vector3d(0, .2, .875+offset), new Vector3d(1, .2, .875+offset),
					new Vector3d(0, .2, 1-offset), new Vector4d(0, 1, .875+offset, 1-offset)),
		},
		
		// facing south
		{},

		// facing west
		{},
		
		// facing east
		{},
	};
	
	static {
		rotateFaceY(2, 5);
		rotateFaceY(5, 3);
		rotateFaceY(3, 4);
	}
	
	private static void rotateFaceY(int i, int j) {
		faces[j] = new Quad[faces[i].length];
		for (int k = 0; k < faces[i].length; ++k) {
			faces[j][k] = faces[i][k].getYRotated();
		}
	}

	public static boolean intersect(Ray ray) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad face : faces[ray.getBlockData() % 6]) {
			if (face.intersect(ray)) {
				Texture.oakPlanks.getColor(ray);
				ray.n.set(face.n);
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
