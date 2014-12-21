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

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class GrassModel {
	protected static Quad[] quads = {
		// front
		new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
				new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, 1)),

		// back
		new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
				new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),

		// right
		new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
				new Vector3d(0, 1, 0), new Vector4d(0, 1, 0, 1)),

		// left
		new Quad(new Vector3d(1, 0, 1), new Vector3d(1, 0, 0),
				new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),

		// top
		new Quad(new Vector3d(1, 1, 0), new Vector3d(0, 1, 0),
				new Vector3d(1, 1, 1), new Vector4d(1, 0, 0, 1)),

		// bottom
		new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
				new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),

	};

	public static boolean intersect(Ray ray, Scene scene) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : quads) {
			if (quad.intersect(ray)) {
				if (quad.n.y == -1) {
					// bottom texture
					Texture.dirt.getColor(ray);
					ray.n.set(quad.n);
					ray.t = ray.tNext;
					hit = true;
					continue;
				} else if (quad.n.y == 0 &&
						(ray.getCurrentData() & (1<<8)) != 0) {

					// snow side texture
					Texture.snowSide.getColor(ray);
					ray.n.set(quad.n);
					ray.t = ray.tNext;
					hit = true;
					continue;
				}
				float[] color;
				if (quad.n.y > 0) {
					color = Texture.grassTop.getColor(ray.u, ray.v);
				} else {
					color = Texture.grassSide.getColor(ray.u, ray.v);
				}
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					float[] biomeColor = ray.getBiomeGrassColor(scene);
					ray.color.x *= biomeColor[0];
					ray.color.y *= biomeColor[1];
					ray.color.z *= biomeColor[2];
					ray.n.set(quad.n);
					ray.t = ray.tNext;
					hit = true;
				} else {
					Texture.grassSideSaturated.getColor(ray);
					ray.color.w = 1;
					ray.n.set(quad.n);
					ray.t = ray.tNext;
					hit = true;
				}
			}
		}
		if (hit) {
			ray.distance += ray.t;
			ray.o.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
