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
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * Minecart rails.
 * @author Jesper Öqvist <jesper@llbit.se>
 *
 */
public class RailModel {
	private static Quad[][] rails = {
		// flat north/south
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),
		},

		// flat east/west
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
					new Vector3d(1, 0, 0), new Vector4d(0, 1, 0, 1)),
		},

		// ascending east
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
					new Vector3d(1, 1, 0), new Vector4d(0, 1, 0, 1)),
		},

		// ascending west
		{
			new DoubleSidedQuad(new Vector3d(0, 1, 0), new Vector3d(0, 1, 1),
					new Vector3d(1, 0, 0), new Vector4d(0, 1, 0, 1)),
		},

		// ascending north
		{
			new DoubleSidedQuad(new Vector3d(0, 1, 0), new Vector3d(1, 1, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),
		},

		// ascending south
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),
		},

		// nw corner
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 1, 0)),
		},

		// ne corner
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(1, 0, 1, 0)),
		},

		// se corner
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(1, 0, 0, 1)),
		},

		// sw corner
		{
			new DoubleSidedQuad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),
		},

	};

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray, Texture texture, int type) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : rails[type]) {
			if (quad.intersect(ray)) {
				float[] color = texture.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.t = ray.tNear;
					ray.n.set(quad.n);
					ray.n.scale(-Math.signum(ray.d.dot(quad.n)));
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
