/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.world.BlockData;
import se.llbit.math.AABB;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * Hopper block
 * @author Jesper Öqvist <jesper@llbit.se>
 *
 */
public class HopperModel {
	private static final AABB[] boxes = new AABB[] {
		// east
		new AABB(14/16., 1, 10/16., 1, 0, 1),
		// west
		new AABB(0, 2/16., 10/16., 1, 0, 1),
		// north
		new AABB(2/16., 14/16., 10/16., 1, 0, 2/16.),
		// south
		new AABB(2/16., 14/16., 10/16., 1, 14/16., 1),
		// center
		new AABB(4/16., 12/16., 4/16., 10/16., 4/16., 12/16.),
	};

	private static final AABB[] pipe = new AABB[] {
		// bottom
		new AABB(6/16., 10/16., 0, 4/16., 6/16., 10/16.),
		// bottom
		new AABB(6/16., 10/16., 0, 4/16., 6/16., 10/16.),
		// facing north
		new AABB(6/16., 10/16., 4/16., 8/16., 0, 4/16.),
		// facing south
		new AABB(6/16., 10/16., 4/16., 8/16., 12/16., 1),
		// facing west
		new AABB(0/16., 4/16., 4/16., 8/16., 6/16., 10/16.),
		// facing east
		new AABB(12/16., 1, 4/16., 8/16., 6/16., 10/16.),
		// bottom
		new AABB(6/16., 10/16., 0, 4/16., 6/16., 10/16.),
		// bottom
		new AABB(6/16., 10/16., 0, 4/16., 6/16., 10/16.),
	};

	private static final Quad bottom = new DoubleSidedQuad(
			new Vector3d(2/16., 10/16., 2/16.), new Vector3d(14/16., 10/16., 2/16.),
			new Vector3d(2/16., 10/16., 14/16.), new Vector4d(2/16., 14/16., 2/16., 14/16.));

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray) {
		boolean hit = false;
		ray.t = Double.POSITIVE_INFINITY;
		for (int i = 0; i < boxes.length; ++i) {
			if (boxes[i].intersect(ray)) {
				if (ray.n.y > 0)
					Texture.hopperInside.getColor(ray);
				else
					Texture.hopper.getColor(ray);
				ray.color.w = 1;
				ray.t = ray.tNear;
				hit = true;
			}
		}
		int dir = 7 & (ray.currentMaterial >> BlockData.BLOCK_DATA_OFFSET);
		if (pipe[dir].intersect(ray)) {
			if (ray.n.y > 0)
				Texture.hopperInside.getColor(ray);
			else
				Texture.hopper.getColor(ray);
			ray.color.w = 1;
			ray.t = ray.tNear;
			hit = true;
		}
		if (bottom.intersect(ray)) {
			ray.n.set(bottom.n);
			ray.n.scale(-Math.signum(ray.d.dot(bottom.n)));
			if (ray.n.y > 0)
				Texture.hopperInside.getColor(ray);
			else
				Texture.hopper.getColor(ray);
			ray.t = ray.tNear;
			hit = true;
		}
		if (hit) {
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
		}
		return hit;
	}
}
