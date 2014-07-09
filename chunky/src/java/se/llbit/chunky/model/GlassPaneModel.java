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
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Transform;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

@SuppressWarnings("javadoc")
public class GlassPaneModel {

	private static Quad cap =
			new Quad(new Vector3d(.5625, 1, .5), new Vector3d(.4375, 1, .5),
				new Vector3d(.4375, 0, .5), new Vector4d(.5625, .4375, 1, 0));

	private static Quad[][] connector = {
		// front
		{
			// left
			new Quad(new Vector3d(.4375, 1, .5), new Vector3d(.4375, 1, 0),
					new Vector3d(.4375, 0, .5), new Vector4d(.5, 0, 1, 0)),

			// right
			new Quad(new Vector3d(.5625, 1, 0), new Vector3d(.5625, 1, .5),
					new Vector3d(.5625, 0, 0), new Vector4d(0, .5, 1, 0)),

			// top
			new Quad(new Vector3d(.5625, 1, 0), new Vector3d(.4375, 1, 0),
					new Vector3d(.5625, 1, .5), new Vector4d(.5625, .4375, 0, .5)),

			// bottom
			new Quad(new Vector3d(.4375, 0, 0), new Vector3d(.5625, 0, 0),
					new Vector3d(.4375, 0, .5), new Vector4d(.4375, .5625, 0, .5)),

		},
		// back
		{
			// left
			new Quad(new Vector3d(.4375, 1, 1), new Vector3d(.4375, 1, .5),
					new Vector3d(.4375, 0, 1), new Vector4d(1, .5, 1, 0)),

			// right
			new Quad(new Vector3d(.5625, 1, .5), new Vector3d(.5625, 1, 1),
					new Vector3d(.5625, 0, .5), new Vector4d(.5, 1, 1, 0)),

			// top
			new Quad(new Vector3d(.5625, 1, .5), new Vector3d(.4375, 1, .5),
					new Vector3d(.5625, 1, 1), new Vector4d(.5625, .4375, .5, 1)),

			// bottom
			new Quad(new Vector3d(.4375, 0, .5), new Vector3d(.5625, 0, .5),
					new Vector3d(.4375, 0, 1), new Vector4d(.4375, .5625, .5, 1)),
		},
	};

	private static Quad[][] panes = new Quad[4][];
	private static Quad[] rotCap = new Quad[4];

	static {
		panes[0] = connector[0];
		panes[1] = connector[1];
		for (int j = 2; j < 4; ++j) {
			panes[j] = Model.rotateY(connector[j-2]);
		}
		rotCap[0] = cap;
		rotCap[2] = new Quad(rotCap[0], Transform.NONE.rotateY());
		rotCap[1] = new Quad(rotCap[2], Transform.NONE.rotateY());
		rotCap[3] = new Quad(rotCap[1], Transform.NONE.rotateY());
	}

	public static boolean intersect(Ray ray, Texture texture, Texture sideTexture) {
		int metadata = 0xF & (ray.currentMaterial >> BlockData.GLASS_PANE_OFFSET);
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < 4; ++i) {
			if (metadata == 0 || ((metadata & (1 << i)) != 0)) {
				for (int j = 0; j < panes[i].length; ++j) {
					Quad quad = panes[i][j];
					if (quad.intersect(ray)) {
						float[] color;
						if (j < 2)
							color = texture.getColor(ray.u, ray.v);
						else
							color = sideTexture.getColor(ray.u, ray.v);
						if (color[3] > Ray.EPSILON) {
							ray.color.set(color);
							ray.n.set(quad.n);
							ray.t = ray.tNear;
							hit = true;
						}
					}
				}
				if (metadata == (1 << i)) {
					if (rotCap[i].intersect(ray)) {
						sideTexture.getColor(ray);
						if (ray.color.w > 0) {
							ray.n.set(rotCap[i].n);
							ray.t = ray.tNear;
							hit = true;
						}
					}
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
