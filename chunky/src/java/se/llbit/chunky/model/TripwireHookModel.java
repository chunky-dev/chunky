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
import se.llbit.math.AABB;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class TripwireHookModel {
	private static final AABB boxNorth = new AABB(
			6/16., 10/16., 1/16., 9/16., 0, 2/16.);

	private static final AABB[] box = new AABB[4];

	private static final Quad[] armQuads = {
		// north
		new Quad(new Vector3d(9/16., 7/16., 0), new Vector3d(7/16., 7/16., 0),
				new Vector3d(9/16., 9/16., 0), new Vector4d(9/16., 7/16., 0, 2/16.)),

		// south
		new Quad(new Vector3d(7/16., 7/16., .5), new Vector3d(9/16., 7/16., .5),
				new Vector3d(7/16., 9/16., .5), new Vector4d(7/16., 9/16., 0, 2/16.)),

		// west
		new Quad(new Vector3d(7/16., 9/16., 0), new Vector3d(7/16., 7/16., 0),
				new Vector3d(7/16., 9/16., .5), new Vector4d(7/16., 9/16., 0, 7/16.)),

		// east
		new Quad(new Vector3d(9/16., 9/16., .5), new Vector3d(9/16., 7/16., .5),
				new Vector3d(9/16., 9/16., 0), new Vector4d(9/16., 7/16., 7/16., 0)),

		// top
		new Quad(new Vector3d(9/16., 9/16., 0), new Vector3d(7/16., 9/16., 0),
				new Vector3d(9/16., 9/16., .5), new Vector4d(7/16., 9/16., 0, 7/16.)),

		// bottom
		new Quad(new Vector3d(7/16., 7/16., 0), new Vector3d(9/16., 7/16., 0),
				new Vector3d(7/16., 7/16., .5), new Vector4d(7/16., 9/16., 0, 7/16.)),
	};

	private static final Quad[] hookQuads = {
		// north
		new Quad(new Vector3d(9.5/16., 7.75/16., 6.5/16.), new Vector3d(6.5/16., 7.75/16., 6.5/16.),
				new Vector3d(9.5/16., 8.25/16., 6.5/16.), new Vector4d(11/16., 5/16., 11/16., 13/16.)),

		// south
		new Quad(new Vector3d(6.5/16., 7.75/16., 9.5/16.), new Vector3d(9.5/16., 7.75/16., 9.5/16.),
				new Vector3d(6.5/16., 8.25/16., 9.5/16.), new Vector4d(5/16., 11/16., 7/16., 9/16.)),

		// west
		new Quad(new Vector3d(6.5/16., 8.25/16., 6.5/16.), new Vector3d(6.5/16., 7.75/16., 6.5/16.),
				new Vector3d(6.5/16., 8.25/16., 9.5/16.), new Vector4d(5/16., 7/16., 7/16., 13/16.)),

		// east
		new Quad(new Vector3d(9.5/16., 8.25/16., 9.5/16.), new Vector3d(9.5/16., 7.75/16., 9.5/16.),
				new Vector3d(9.5/16., 8.25/16., 6.5/16.), new Vector4d(11/16., 9/16., 13/16., 7/16.)),

		// top
		new Quad(new Vector3d(9.5/16., 8.25/16., 6.5/16.), new Vector3d(6.5/16., 8.25/16., 6.5/16.),
				new Vector3d(9.5/16., 8.25/16., 9.5/16.), new Vector4d(5/16., 11/16., 7/16., 13/16.)),

		// bottom
		new Quad(new Vector3d(6.5/16., 7.75/16., 6.5/16.), new Vector3d(9.5/16., 7.75/16., 6.5/16.),
				new Vector3d(6.5/16., 7.75/16., 9.5/16.), new Vector4d(5/16., 11/16., 7/16., 13/16.)),
	};

	private static final Quad[][][] arm = new Quad[4][4][];
	private static final Quad[][][] hook = new Quad[4][4][];

	static {
		box[0] = boxNorth;
		box[1] = box[0].getYRotated();
		box[2] = box[1].getYRotated();
		box[3] = box[2].getYRotated();

		// unarmed
		arm[0][0] = Model.translate(Model.rotateX(armQuads, -Math.PI/4), 0, 3.3/16., -1.3/16.);
		hook[0][0] = Model.translate(Model.rotateX(hookQuads, Math.PI/4), 0, 2.3/16., -.6/16);

		// armed
		arm[0][1] = Model.translate(Model.rotateX(armQuads, Math.PI/25), 0, -2.5/16., 0);
		hook[0][1] = Model.translate(Model.rotateX(hookQuads, Math.PI/16), 0, -2.3/16., 1/16.);

		// tripped
		arm[0][2] = Model.translate(Model.rotateX(armQuads, Math.PI/8), 0, -5/16., 0);
		hook[0][2] = Model.translate(hookQuads, 0, -4.1/16., 1/16.);

		arm[0][3] = arm[0][2];
		hook[0][3] = hook[0][2];

		for (int i = 1; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				arm[i][j] = Model.rotateY(arm[i-1][j]);
				hook[i][j] = Model.rotateY(hook[i-1][j]);
			}
		}
	}

	public static boolean intersect(Ray ray) {
		int data = ray.getBlockData();
		int direction = data & 3;
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		if (box[direction].intersect(ray)) {
			ray.t = ray.tNear;
			Texture.oakPlanks.getColor(ray);
			ray.color.w = 1;
			hit = true;
		}

		for (Quad quad: arm[direction][data>>2]) {
			if (quad.intersect(ray)) {
				float[] color = Texture.tripwireHook.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.t = ray.tNear;
					ray.n.set(quad.n);
					hit = true;
				}
			}
		}
		for (Quad quad: hook[direction][data>>2]) {
			if (quad.intersect(ray)) {
				float[] color = Texture.tripwireHook.getColor(ray.u, ray.v);
				if (color[3] > Ray.EPSILON) {
					ray.color.set(color);
					ray.t = ray.tNear;
					ray.n.set(quad.n);
					hit = true;
				}
			}
		}
		if (hit) {
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d);
		}
		return hit;
	}
}

