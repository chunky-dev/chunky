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
 * Piston
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PistonModel {
	protected static Quad[][] retracted = {
		// down
		{},

		// up
		{},

		// facing north
		{
			// north
			new Quad(new Vector3d(1, 0, 0), new Vector3d(0, 0, 0),
					new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, 1)),

			// south
			new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),

			// west
			new Quad(new Vector3d(0, 1, 0), new Vector3d(0, 0, 0),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, 1, 0)),

			// east
			new Quad(new Vector3d(1, 1, 1), new Vector3d(1, 0, 1),
					new Vector3d(1, 1, 0), new Vector4d(1, 0, 0, 1)),

			// top
			new Quad(new Vector3d(1, 1, 0), new Vector3d(0, 1, 0),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, 1, 0)),

			// bottom
			new Quad(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, 1, 0)),
		},

		// facing south
		{},

		//facing west
		{},

		// facing east
		{},
	};

	protected static Quad[][] extended = {
		// down
		{},

		// up
		{},

		// facing north
		{
			// north
			new Quad(new Vector3d(1, 0, .25), new Vector3d(0, 0, .25),
					new Vector3d(1, 1, .25), new Vector4d(1, 0, 0, 1)),

			// south
			new Quad(new Vector3d(0, 0, 1), new Vector3d(1, 0, 1),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, 0, 1)),

			// west
			new Quad(new Vector3d(0, 1, .25), new Vector3d(0, 0, .25),
					new Vector3d(0, 1, 1), new Vector4d(0, 1, .75, 0)),

			// east
			new Quad(new Vector3d(1, 1, 1), new Vector3d(1, 0, 1),
					new Vector3d(1, 1, .25), new Vector4d(1, 0, 0, .75)),

			// top
			new Quad(new Vector3d(1, 1, .25), new Vector3d(0, 1, .25),
					new Vector3d(1, 1, 1), new Vector4d(1, 0, .75, 0)),

			// bottom
			new Quad(new Vector3d(0, 0, .25), new Vector3d(1, 0, .25),
					new Vector3d(0, 0, 1), new Vector4d(0, 1, .75, 0)),

			// extension west
			new Quad(new Vector3d(.375, .375, 0), new Vector3d(.375, .375, .25),
					new Vector3d(.375, .625, 0), new Vector4d(.25, 0, .75, 1)),

			// extension east
			new Quad(new Vector3d(.625, .375, .25), new Vector3d(.625, .375, 0),
					new Vector3d(.625, .625, .25), new Vector4d(0, .25, .75, 1)),

			// extension top
			new Quad(new Vector3d(.375, .625, 0), new Vector3d(.375, .625, .25),
					new Vector3d(.625, .625, 0), new Vector4d(.25, 0, .75, 1)),

			// extension bottom
			new Quad(new Vector3d(.375, .375, .25), new Vector3d(.375, .375, 0),
					new Vector3d(.625, .375, .25), new Vector4d(0, .25, .75, 1)),
		},

		// facing south
		{},

		//facing west
		{},

		// facing east
		{},
	};

	static {
		extended[0] = Model.rotateNegX(extended[2]);
		extended[1] = Model.rotateX(extended[2]);
		extended[5] = Model.rotateY(extended[2]);
		extended[3] = Model.rotateY(extended[5]);
		extended[4] = Model.rotateY(extended[3]);
		retracted[0] = Model.rotateNegX(retracted[2]);
		retracted[1] = Model.rotateX(retracted[2]);
		retracted[5] = Model.rotateY(retracted[2]);
		retracted[3] = Model.rotateY(retracted[5]);
		retracted[4] = Model.rotateY(retracted[3]);
	}

	static final Texture[][][] texture = {
		{
			{
				Texture.pistonTop,
				Texture.pistonBottom,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
			},

			{
				Texture.pistonTopSticky,
				Texture.pistonBottom,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
			},
		},

		{
			{
				Texture.pistonInnerTop,
				Texture.pistonBottom,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
			},

			{
				Texture.pistonInnerTop,
				Texture.pistonBottom,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
				Texture.pistonSide,
			},
		},
	};

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray, int isSticky) {
		boolean hit = false;
		int isExtended = ray.getBlockData() >> 3;
		Quad[] rot = isExtended == 1 ?
				extended[(ray.getBlockData() & 7) % 6] :
				retracted[(ray.getBlockData() & 7) % 6];
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < rot.length; ++i) {
			Quad side = rot[i];
			if (side.intersect(ray)) {
				texture[isExtended][isSticky][i].getColor(ray);
				ray.n.set(side.n);
				ray.t = ray.tNear;
				hit = true;
			}
		}
		if (hit) {
			ray.color.w = 1;
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}
