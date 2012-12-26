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
 * Furnaces, chests, dispensers
 * 
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class FurnaceModel {
	protected static Quad[][] sides = {
		{}, {},
		
		// facing north
		{
			// north
			new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
					new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, 1)),
	
			// south
			new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),
			
			// west
			new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
					new Vector3d(0, 1, 0), new Vector4d(0, 1, 0, 1)),
	
			// east
			new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),
	
			// top
			new Quad(new Vector3d(1, 1, 0), new Vector3d(0, 1, 0),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),
			
			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),
		},
		
		// facing south
		{},
		
		//facing west
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
		sides[j] = new Quad[sides[i].length];
		for (int k = 0; k < sides[i].length; ++k) {
			sides[j][k] = sides[i][k].getYRotated();
		}
	}

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray, Texture[] texture) {
		boolean hit = false;
		Quad[] rot = sides[ray.getBlockData() % 6];
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
