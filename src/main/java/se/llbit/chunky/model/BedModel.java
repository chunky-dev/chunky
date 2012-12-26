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
public class BedModel {
	private static Quad[] foot = {
		// end
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
				new Vector3d(1, .5, 0), new Vector4d(1, 0, 0, .5)),
		
		// right side
		new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
				new Vector3d(0, .5, 0), new Vector4d(0, 1, 0, .5)),
	
		// left side
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
				new Vector3d(1, .5, 1), new Vector4d(1, 0, 0, .5)),
	
		// top
		new Quad(new Vector3d(1, .5, 0), new Vector3d(0, .5, 0),
				new Vector3d(1, .5, 1), new Vector4d(1, 0, 0, 1)),
		
		// bottom
		new Quad(new Vector3d(0, .25, 0), new Vector3d(1, .25, 0),
				new Vector3d(0, .25, 1), new Vector4d(0, 1, 0, 1)),
	};
	
	private static Quad[] head = {
		// end
		new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
				new Vector3d(0, .5, 1), new Vector4d(0, 1, 0, .5)),
		
		// right side
		new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
				new Vector3d(0, .5, 0), new Vector4d(0, 1, 0, .5)),
	
		// left side
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
				new Vector3d(1, .5, 1), new Vector4d(1, 0, 0, .5)),
	
		// top
		new Quad(new Vector3d(1, .5, 0), new Vector3d(0, .5, 0),
				new Vector3d(1, .5, 1), new Vector4d(1, 0, 0, 1)),
		
		// bottom
		new Quad(new Vector3d(0, .25, 0), new Vector3d(1, .25, 0),
				new Vector3d(0, .25, 1), new Vector4d(0, 1, 0, 1)),
	};
	
	private static Quad[][][] rot = new Quad[2][4][];
	static {
		rot[0][0] = foot;
		rot[1][0] = head;
		for (int isHead = 0; isHead < 2; ++isHead) {
			for (int angle = 1; angle < 4; ++angle) {
				rot[isHead][angle] = new Quad[rot[isHead][angle-1].length];
				for (int i = 0; i < rot[isHead][angle-1].length; ++i) {
					rot[isHead][angle][i] = rot[isHead][angle-1][i].getYRotated();
				}
				
			}
		}
	}

	public static boolean intersect(Ray ray) {
		boolean hit = false;
		int isHead = (ray.getBlockData() >> 3) & 1;
		int angle = ray.getBlockData() & 3;

		ray.t = Double.POSITIVE_INFINITY;

		for (Quad quad : rot[isHead][angle]) {
			if (quad.intersect(ray)) {
				float[] color;
				if (isHead == 1) {
					if (quad == rot[isHead][angle][3])
						color = Texture.bedHeadTop.getColor(ray.v, ray.u);
					else if (quad == rot[isHead][angle][4])
						color = Texture.oakPlanks.getColor(ray.u, ray.v);
					else if (quad == rot[isHead][angle][0])
						color = Texture.bedHeadEnd.getColor(ray.u, ray.v);
					else
						color = Texture.bedHeadSide.getColor(ray.u, ray.v);
				} else {
					if (quad == rot[isHead][angle][3])
						color = Texture.bedFootTop.getColor(ray.v, ray.u);
					else if (quad == rot[isHead][angle][4])
						color = Texture.oakPlanks.getColor(ray.u, ray.v);
					else if (quad == rot[isHead][angle][0])
						color = Texture.bedFootEnd.getColor(ray.u, ray.v);
					else
						color = Texture.bedFootSide.getColor(ray.u, ray.v);
				}
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
