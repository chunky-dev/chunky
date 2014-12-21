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
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * Melon stem grows in eight steps.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class MelonStemModel {

	private static final Quad[][] growth = new Quad[8][2];
	private static final Quad[] ripe = {
		new DoubleSidedQuad(new Vector3d(0, 0, .5), new Vector3d(1, 0, .5),
				new Vector3d(0, 1, .5), new Vector4d(0, 1, 0, 1)),
		new DoubleSidedQuad(new Vector3d(0, 0, .5), new Vector3d(1, 0, .5),
				new Vector3d(0, 1, .5), new Vector4d(1, 0, 0, 1)),
		new DoubleSidedQuad(new Vector3d(.5, 0, 0), new Vector3d(.5, 0, 1),
				new Vector3d(.5, 1, 0), new Vector4d(0, 1, 0, 1)),
		new DoubleSidedQuad(new Vector3d(.5, 0, 0), new Vector3d(.5, 0, 1),
				new Vector3d(.5, 1, 0), new Vector4d(1, 0, 0, 1)),
	};
	private static final double[][] stemColor = {
		{ 0, 0xE2 / 255., 0x10 / 255. },
		{ 0, 0xE2 / 255., 0x10 / 255. },
		{ 0, 0xE2 / 255., 0x10 / 255. },
		{ 0, 0xCC / 255., 0x06 / 255. },
		{ 0x5F / 255., 0xC8 / 255., 0x03 / 255. },
		{ 0x65 / 255., 0xC2 / 255., 0x06 / 255. },
		{ 0xA0 / 255., 0xB8 / 255., 0 },
		{ 0xBF / 255., 0xB6 / 255., 0 },
	};

	static {
		for (int height = 0; height < 8; ++height) {
			growth[height][0] = new DoubleSidedQuad(
					new Vector3d(0, 0, 0), new Vector3d(1, 0, 1),
					new Vector3d(0, (height+1)/8., 0),
					new Vector4d(0, 1, (7-height)/8., 1));
			growth[height][1] = new DoubleSidedQuad(
					new Vector3d(1, 0, 0), new Vector3d(0, 0, 1),
					new Vector3d(1, (height+1)/8., 0),
					new Vector4d(0, 1, (7-height)/8., 1));
		}
	}

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray) {
		boolean hit = false;
		int connected = (ray.currentMaterial >> 16) % 5;
		ray.t = Double.POSITIVE_INFINITY;
		if (connected == 0) {
			int height = ray.getBlockData() & 7;
			for (Quad quad : growth[height]) {
				if (quad.intersect(ray)) {
					float[] color = Texture.stemStraight.getColor(ray.u, ray.v);
					if (color[3] > Ray.EPSILON) {
						ray.color.set(color);
						ray.color.x *= stemColor[height][0];
						ray.color.y *= stemColor[height][1];
						ray.color.z *= stemColor[height][2];
						ray.t = ray.tNext;
						ray.n.set(quad.n);
						ray.n.scale(-QuickMath.signum(ray.d.dot(quad.n)));
						hit = true;
					}
				}
			}
		} else {
			for (Quad quad : growth[3]) {
				if (quad.intersect(ray)) {
					float[] color = Texture.stemStraight.getColor(ray.u, ray.v);
					if (color[3] > Ray.EPSILON) {
						ray.color.set(color);
						ray.color.x *= stemColor[7][0];
						ray.color.y *= stemColor[7][1];
						ray.color.z *= stemColor[7][2];
						ray.t = ray.tNext;
						ray.n.set(quad.n);
						ray.n.scale(-QuickMath.signum(ray.d.dot(quad.n)));
						hit = true;
					}
				}
			}
			Quad quad = ripe[connected-1];
			if (quad.intersect(ray)) {
				float[] color = Texture.stemBent.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.color.x *= stemColor[7][0];
					ray.color.y *= stemColor[7][1];
					ray.color.z *= stemColor[7][2];
					ray.t = ray.tNext;
					ray.n.set(quad.n);
					ray.n.scale(-QuickMath.signum(ray.d.dot(quad.n)));
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
