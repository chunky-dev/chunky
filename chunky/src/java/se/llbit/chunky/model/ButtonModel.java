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
public class ButtonModel {
	protected static Quad[] attachedSouth = {
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

	private static Quad[][] variant = new Quad[8][];

	static {
		// data bits: (part of block attached to)
		// 000 => top
		// 001 => west
		// 010 => east
		// 011 => north
		// 100 => south
		// 101 => up
		// 110 => undefined
		// 111 => undefined
		// last updated for MC 1.8
		variant[4] = attachedSouth;
		variant[1] = Model.rotateY(attachedSouth);
		variant[3] = Model.rotateY(variant[1]);
		variant[2] = Model.rotateY(variant[3]);
		variant[0] = Model.rotateNegX(attachedSouth);
		variant[5] = Model.rotateX(attachedSouth);
		variant[6] = attachedSouth;
		variant[7] = attachedSouth;
	}

	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		Quad[] rotated = variant[ray.getBlockData() & 7];
		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : rotated) {
			if (quad.intersect(ray)) {
				texture.getColor(ray);
				ray.n.set(quad.n);
				ray.t = ray.tNext;
				hit = true;
			}
		}
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.o.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
