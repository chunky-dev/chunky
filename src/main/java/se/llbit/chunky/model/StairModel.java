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
import se.llbit.math.AABB;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class StairModel {
	private static AABB[][][] corners = {
		{
			{
				// s-e
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0.5, 1, 0.5, 1, 0.5, 1),
			},
			{
				// s-w
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 0.5, 0.5, 1, 0.5, 1),
			},
			{
				// n-e
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0.5, 1, 0.5, 1, 0, 0.5),
			},
			{
				// n-w
				new AABB(0, 1, 0, 0.5, 0, 1),
				new AABB(0, 0.5, 0.5, 1, 0, 0.5),
			},
		},
		{
			{
				// s-e
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0.5, 1, 0, 0.5, 0.5, 1),
			},
			{
				// s-w
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 0.5, 0, 0.5, 0.5, 1),
			},
			{
				// n-e
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0.5, 1, 0, 0.5, 0, 0.5),
			},
			{
				// n-w
				new AABB(0, 1, 0.5, 1, 0, 1),
				new AABB(0, 0.5, 0, 0.5, 0, 0.5),
			},
		},
	};
	// ascending south
	private static Quad[] quads = {
		// lower front
		new Quad(new Vector3d(0, .5, 0), new Vector3d(1, .5, 0),
					new Vector3d(0, 0, 0), new Vector4d(0, 1, .5, 0)),

		// upper front ?
		new Quad(new Vector3d(0, 1, .5), new Vector3d(1, 1, .5),
				new Vector3d(0, .5, .5), new Vector4d(0, 1, 1, .5)),

		// lower top ?
		new Quad(new Vector3d(0, .5, .5), new Vector3d(1, .5, .5),
				new Vector3d(0, .5, 0), new Vector4d(0, 1, .5, 0)),

		// upper top ?
		new Quad(new Vector3d(0, 1, 1), new Vector3d(1, 1, 1),
				new Vector3d(0, 1, .5), new Vector4d(0, 1, 1, .5)),

		// lower left side ?
		new Quad(new Vector3d(0, 0, 0), new Vector3d(0, 0, 1),
				new Vector3d(0, .5, 0), new Vector4d(0, 1, 0, .5)),

		// upper left side ?
		new Quad(new Vector3d(0, .5, .5), new Vector3d(0, .5, 1),
				new Vector3d(0, 1, .5), new Vector4d(.5, 1, .5, 1)),

		// lower right side ?
		new Quad(new Vector3d(1, .5, 0), new Vector3d(1, .5, 1),
				new Vector3d(1, 0, 0), new Vector4d(1, 0, .5, 0)),

		// upper right side ?
		new Quad(new Vector3d(1, 1, .5), new Vector3d(1, 1, 1),
				new Vector3d(1, .5, .5), new Vector4d(.5, 1, 1, .5)),

		new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
				new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),

		new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
				new Vector3d(0, 0, 1), new Vector4d(0, 1, 0, 1)),
	};
		
	private static Quad[][][] rot = new Quad[2][4][];
	
	static {
		rot[0][0] = quads;
		rot[1][0] = new Quad[quads.length];
		for (int i = 0; i < quads.length; ++i) {
			rot[1][0][i] = rot[0][0][i].getFlipped();
		}
		for (int flipped = 0; flipped <= 1; ++flipped) {
			for (int angle = 1; angle < 4; ++angle) {
				rot[flipped][angle] = new Quad[quads.length];
				for (int i = 0; i < quads.length; ++i) {
					rot[flipped][angle][i] =
							rot[flipped][angle-1][i].getYRotated();
				}
			}
		}
	}
	
	public static boolean intersect(Ray ray, Texture texture) {
		boolean hit = false;
		Quad[] rotated = null;
		int flipped = (ray.getBlockData() & 4) >> 2;
		int corner = 7 & (ray.currentMaterial >> BlockData.CORNER_OFFSET);
		
		ray.t = Double.POSITIVE_INFINITY;
		
		if (corner != 0) {
			for (AABB box : corners[flipped][3 & corner]) {
				if (box.intersect(ray)) {
					texture.getColor(ray);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		} else {
			switch (ray.getBlockData() & 3) {
			case 0:
				rotated = rot[flipped][3];
				break;
			case 1:
				rotated = rot[flipped][1];
				break;
			case 2:
				rotated = rot[flipped][0];
				break;
			case 3:
				rotated = rot[flipped][2];
				break;
			}
	
			for (Quad quad : rotated) {
				if (quad.intersect(ray)) {
					texture.getColor(ray);
					ray.n.set(quad.n);
					ray.t = ray.tNear;
					hit = true;
				}
			}
		}
		
		if (hit) {
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
		}
		return hit;
	}
	
	private static final int[] index = { 3, 1, 0, 2 };
	
	public static boolean intersect(Ray ray, Texture texture,
			Texture textureTop, Texture textureBottom) {
		
		boolean hit = false;
		int flipped = (ray.getBlockData() & 4) >> 2;
		int corner = (ray.currentMaterial & 7) >> BlockData.CORNER_OFFSET;
		Quad[] rotated = rot[flipped][index[ray.getBlockData() & 3]];
		
		if (corner != 0)
			return false;

		ray.t = Double.POSITIVE_INFINITY;
		for (Quad quad : rotated) {
			if (quad.intersect(ray)) {
				if (quad.n.y > 0)
					textureTop.getColor(ray);
				else if (quad.n.y < 0)
					textureBottom.getColor(ray);
				else
					texture.getColor(ray);
				ray.n.set(quad.n);
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
