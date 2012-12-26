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

import static se.llbit.chunky.model.Model.*;

@SuppressWarnings("javadoc")
public class TorchModel {
	private static Quad[] faces = {
		new Quad(new Vector3d(.75, 0, .4375), new Vector3d(.25, 0, .4375),
				new Vector3d(.75, 1, .4375), new Vector4d(.75, .25, 0, 1)),

		new Quad(new Vector3d(.25, 0, .5625), new Vector3d(.75, 0, .5625),
				new Vector3d(.25, 1, .5625), new Vector4d(.25, .75, 0, 1)),
				
		new Quad(new Vector3d(.4375, 0, .25), new Vector3d(.4375, 0, .75),
				new Vector3d(.4375, 1, .25), new Vector4d(.25, .75, 0, 1)),

		new Quad(new Vector3d(.5625, 0, .75), new Vector3d(.5625, 0, .25),
				new Vector3d(.5625, 1, .75), new Vector4d(.75, .25, 0, 1)),
				
		// top
		new Quad(new Vector3d(.4375, .625, .5625), new Vector3d(.5625, .625, .5625),
				new Vector3d(.4375, .625, .4375), new Vector4d(.4375, .5625, .5, .625)),
	};
	
	private static Quad[][] rot = new Quad[6][];
	
	static {
		rot[0] = new Quad[0];

		// on ground
		rot[5] = faces;
		
		// pointing east
		rot[1] = translate(rotateZ(faces, -Math.PI/4), -.1, 0, 0);
		
		// pointing west
		rot[2] = translate(rotateZ(faces, Math.PI/4), .1, 0, 0);
		
		// pointing south
		rot[3] = translate(rotateX(faces, Math.PI/4), 0, 0, -.1);
		
		// pointing north
		rot[4] = translate(rotateX(faces, -Math.PI/4), 0, 0, .1);
	}

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : rot[ray.getBlockData() % 6]) {
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
