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

import static se.llbit.chunky.model.Model.rotateY;
import static se.llbit.chunky.model.Model.rotateZ;
import static se.llbit.chunky.model.Model.translate;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * A lever switch.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LeverModel {
	private static Quad[] base = {
		// front
		new Quad(new Vector3d(.75, 0, .3125), new Vector3d(.25, 0, .3125),
				new Vector3d(.75, .1875, .3125), new Vector4d(.75, .25, 0, .1875)),

		// back
		new Quad(new Vector3d(.25, 0, .6875), new Vector3d(.75, 0, .6875),
				new Vector3d(.25, .1875, .6875), new Vector4d(.25, .75, 0, .1875)),

		// right
		new Quad(new Vector3d(.25, 0, .3125), new Vector3d(.25, 0, .6875),
				new Vector3d(.25, .1875, .3125), new Vector4d(.3125, .6875, 0, .1875)),

		// left
		new Quad(new Vector3d(.75, 0, .6875), new Vector3d(.75, 0, .3125),
				new Vector3d(.75, .1875, .6875), new Vector4d(.6875, .3125, 0, .1875)),

		// top
		new Quad(new Vector3d(.75, .1875, .3125), new Vector3d(.25, .1875, .3125),
				new Vector3d(.75, .1875, .6875), new Vector4d(.75, 0, .3125, .6875)),

		// bottom
		/*new Quad(new Vector3d(.25, 0, .3125), new Vector3d(.75, 0, .3125),
				new Vector3d(.25, 0, .6875), new Vector4d(.25, .75, .3125, .6875)),*/

	};

	private static Quad[] lever = {
		new Quad(new Vector3d(.5625, 0, .4375), new Vector3d(.4375, 0, .4375),
				new Vector3d(.5625, .625, .4375), new Vector4d(.5625, .4375, 0, .625)),

		new Quad(new Vector3d(.4375, 0, .5625), new Vector3d(.5625, 0, .5625),
				new Vector3d(.4375, .625, .5625), new Vector4d(.4375, .5625, 0, .625)),

		new Quad(new Vector3d(.4375, 0, .4375), new Vector3d(.4375, 0, .5625),
				new Vector3d(.4375, .625, .4375), new Vector4d(.4375, .5625, 0, .625)),

		new Quad(new Vector3d(.5625, 0, .5625), new Vector3d(.5625, 0, .4375),
				new Vector3d(.5625, .625, .5625), new Vector4d(.5625, .4375, 0, .625)),

		// top
		new Quad(new Vector3d(.4375, .625, .5625), new Vector3d(.5625, .625, .5625),
				new Vector3d(.4375, .625, .4375), new Vector4d(.4375, .5625, .5, .625)),
	};

	private static Quad[][][] baseRotated = new Quad[8][2][];
	private static Quad[][][] leverRotated = new Quad[8][2][];

	static {

		Quad[] groundEW = base;
		Quad[] groundNS = rotateY(groundEW);
		Quad[] wallWest = rotateZ(groundEW);
		Quad[] wallNorth = rotateY(wallWest);
		Quad[] wallEast = rotateY(wallNorth);
		Quad[] wallSouth = rotateY(wallEast);
		Quad[] ceilingEW = rotateZ(wallWest);
		Quad[] ceilingNS = rotateY(ceilingEW);

		baseRotated[0][0] = ceilingEW;
		baseRotated[0][1] = ceilingEW;
		baseRotated[1][0] = wallEast;
		baseRotated[1][1] = wallEast;
		baseRotated[2][0] = wallWest;
		baseRotated[2][1] = wallWest;
		baseRotated[3][0] = wallSouth;
		baseRotated[3][1] = wallSouth;
		baseRotated[4][0] = wallNorth;
		baseRotated[4][1] = wallNorth;
		baseRotated[5][0] = groundNS;
		baseRotated[5][1] = groundNS;
		baseRotated[6][0] = groundEW;
		baseRotated[6][1] = groundEW;
		baseRotated[7][0] = ceilingNS;
		baseRotated[7][1] = ceilingNS;

		Quad[] leverEWOff = translate(rotateZ(lever, -Math.PI/4), .35, 0, 0);
		Quad[] leverEWOn = translate(rotateZ(lever, Math.PI/4), -.35, 0, 0);
		Quad[] leverNSOn = rotateY(leverEWOn);
		Quad[] leverNSOff = rotateY(leverEWOff);

		Quad[] leverWallWestOff = rotateZ(leverEWOff);
		Quad[] leverWallNorthOff = rotateY(leverWallWestOff);
		Quad[] leverWallEastOff = rotateY(leverWallNorthOff);
		Quad[] leverWallSouthOff = rotateY(leverWallEastOff);

		Quad[] leverWallWestOn = rotateZ(leverEWOn);
		Quad[] leverWallNorthOn = rotateY(leverWallWestOn);
		Quad[] leverWallEastOn = rotateY(leverWallNorthOn);
		Quad[] leverWallSouthOn = rotateY(leverWallEastOn);

		Quad[] leverCeilingEWOff = rotateZ(leverWallWestOn);
		Quad[] leverCeilingEWOn = rotateZ(leverWallWestOff);

		Quad[] leverCeilingNSOff = rotateY(leverCeilingEWOff);
		Quad[] leverCeilingNSOn = rotateY(leverCeilingEWOn);

		leverRotated[0][0] = leverCeilingEWOff;
		leverRotated[0][1] = leverCeilingEWOn;
		leverRotated[1][0] = leverWallEastOff;
		leverRotated[1][1] = leverWallEastOn;
		leverRotated[2][0] = leverWallWestOff;
		leverRotated[2][1] = leverWallWestOn;
		leverRotated[3][0] = leverWallSouthOff;
		leverRotated[3][1] = leverWallSouthOn;
		leverRotated[4][0] = leverWallNorthOff;
		leverRotated[4][1] = leverWallNorthOn;
		leverRotated[5][0] = leverNSOff;
		leverRotated[5][1] = leverNSOn;
		leverRotated[6][0] = leverEWOff;
		leverRotated[6][1] = leverEWOn;
		leverRotated[7][0] = leverCeilingNSOff;
		leverRotated[7][1] = leverCeilingNSOn;
	}

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;

		int metadata = ray.getBlockData();
		int activated = (metadata >> 3) & 1;
		int direction = (metadata & 7);

		for (Quad quad : baseRotated[direction][activated]) {
			if (quad.intersect(ray)) {
				Texture.cobblestone.getColor(ray);
				ray.n.set(quad.n);
				ray.t = ray.tNext;
				hit = true;
			}
		}

		for (Quad quad : leverRotated[direction][activated]) {
			if (quad.intersect(ray)) {
				Texture.lever.getColor(ray);
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
