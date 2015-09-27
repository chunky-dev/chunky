/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class IronBarsModel {

	private static Quad[] core = {
		new DoubleSidedQuad(new Vector3d(.5, 1, 7/16.), new Vector3d(.5, 1, 9/16.),
				new Vector3d(.5, 0, 7/16.), new Vector4d(7/16., 9/16., 1, 0)),

		new DoubleSidedQuad(new Vector3d(7/16., 1, .5), new Vector3d(9/16., 1, .5),
				new Vector3d(7/16., 0, .5), new Vector4d(7/16., 9/16., 1, 0)),
	};


	private static Quad[] coreTop = {
		// Top face.
		new DoubleSidedQuad(new Vector3d(9/16., 1, 7/16.), new Vector3d(7/16., 1, 7/16.),
				new Vector3d(9/16., 1, 9/16.), new Vector4d(9/16., 7/16., 7/16., 9/16.)),

		// Bottom face.
		new DoubleSidedQuad(new Vector3d(7/16., 0, 7/16.), new Vector3d(9/16., 0, 7/16.),
				new Vector3d(7/16., 0, 9/16.), new Vector4d(7/16., 9/16., 7/16., 9/16.)),
	};

	private static Quad[][] connector = {
		// Front side.
		{
			// Center face.
			new DoubleSidedQuad(new Vector3d(.5, 1, .5), new Vector3d(.5, 1, 0),
					new Vector3d(.5, 0, .5), new Vector4d(.5, 0, 1, 0)),

			// Top face.
			new DoubleSidedQuad(new Vector3d(9/16., 1, 0), new Vector3d(7/16., 1, 0),
					new Vector3d(9/16., 1, 7/16.), new Vector4d(9/16., 7/16., 0, 7/16.)),

			// Bottom face.
			new DoubleSidedQuad(new Vector3d(7/16., 0, 0), new Vector3d(9/16., 0, 0),
					new Vector3d(7/16., 0, 7/16.), new Vector4d(7/16., 9/16., 0, 7/16.)),

		},
		// Back side.
		{
			// Center face.
			new DoubleSidedQuad(new Vector3d(.5, 1, 1), new Vector3d(.5, 1, .5),
					new Vector3d(.5, 0, 1), new Vector4d(1, .5, 1, 0)),

			// Top face.
			new DoubleSidedQuad(new Vector3d(9/16., 1, 9/16.), new Vector3d(7/16., 1, 9/16.),
					new Vector3d(9/16., 1, 1), new Vector4d(9/16., 7/16., 9/16., 1)),

			// Bottom face.
			new DoubleSidedQuad(new Vector3d(7/16., 0, 9/16.), new Vector3d(9/16., 0, 9/16.),
					new Vector3d(7/16., 0, 1), new Vector4d(7/16., 9/16., 9/16., 1)),
		},
	};

	private static Quad[][] panes = new Quad[4][];

	static {
		panes[0] = connector[0];
		panes[1] = connector[1];
		for (int j = 2; j < 4; ++j) {
			panes[j] = Model.rotateY(connector[j-2]);
		}
	}

	public static boolean intersect(Ray ray) {
		int metadata = 0xF & (ray.getCurrentData() >> BlockData.GLASS_PANE_OFFSET);
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		if (metadata == 0) {
			for (Quad quad : core) {
				if (quad.intersect(ray)) {
					Texture.ironBars.getColor(ray);
					if (ray.color.w > 0) {
						ray.n.set(quad.n);
						ray.n.scale(QuickMath.signum(-ray.d.dot(quad.n)));
						ray.t = ray.tNext;
						hit = true;
					}
				}
			}
		}
		for (Quad quad : coreTop) {
			if (quad.intersect(ray)) {
				Texture.ironBars.getColor(ray);
				if (ray.color.w > 0) {
					ray.n.set(quad.n);
					ray.n.scale(QuickMath.signum(-ray.d.dot(quad.n)));
					ray.t = ray.tNext;
					hit = true;
				}
			}
		}
		for (int i = 0; i < 4; ++i) {
			if ((metadata & (1 << i)) != 0) {
				for (int j = 0; j < panes[i].length; ++j) {
					Quad quad = panes[i][j];
					if (quad.intersect(ray)) {
						Texture.ironBars.getColor(ray);
						if (ray.color.w > 0) {
							ray.n.set(quad.n);
							ray.n.scale(QuickMath.signum(-ray.d.dot(quad.n)));
							ray.t = ray.tNext;
							hit = true;
						}
					}
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
