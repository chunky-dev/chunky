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
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Triangle;

/**
 * A lava block. The height the top lava block is slightly lower
 * than a regular block.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LavaModel {
	private static AABB fullBlock = new AABB(0, 1, 0, 1, 0, 1);

	@SuppressWarnings("javadoc")
	public static boolean intersect(Ray ray) {
		ray.t = Double.POSITIVE_INFINITY;
		int data = ray.currentMaterial;
		int isFull = (data >> WaterModel.FULL_BLOCK) & 1;

		if (isFull != 0) {
			if (fullBlock.intersect(ray)) {
				Texture.lava.getColor(ray);
				ray.distance += ray.tNear;
				ray.x.scaleAdd(ray.tNear, ray.d);
				return true;
			}
			return false;
		}

		int c0 = (0xF & (data >> 16)) % 8;
		int c1 = (0xF & (data >> 20)) % 8;
		int c2 = (0xF & (data >> 24)) % 8;
		int c3 = (0xF & (data >> 28)) % 8;
		Triangle triangle = WaterModel.t012[c0][c1][c2];
		boolean hit = false;
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			hit = true;
		}
		triangle = WaterModel.t230[c2][c3][c0];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			ray.u = 1-ray.u;
			ray.v = 1-ray.v;
			hit = true;
		}
		triangle = WaterModel.westt[c0][c3];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double y = ray.t * ray.d.y + ray.x.y;
			double z = ray.t * ray.d.z + ray.x.z;
			y -= QuickMath.floor(y);
			z -= QuickMath.floor(z);
			ray.u = z;
			ray.v = y;
			hit = true;
		}
		triangle = WaterModel.westb[c0];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double y = ray.t * ray.d.y + ray.x.y;
			double z = ray.t * ray.d.z + ray.x.z;
			y -= QuickMath.floor(y);
			z -= QuickMath.floor(z);
			ray.u = z;
			ray.v = y;
			hit = true;
		}
		triangle = WaterModel.eastt[c1][c2];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double y = ray.t * ray.d.y + ray.x.y;
			double z = ray.t * ray.d.z + ray.x.z;
			y -= QuickMath.floor(y);
			z -= QuickMath.floor(z);
			ray.u = z;
			ray.v = y;
			hit = true;
		}
		triangle = WaterModel.eastb[c1];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double y = ray.t * ray.d.y + ray.x.y;
			double z = ray.t * ray.d.z + ray.x.z;
			y -= QuickMath.floor(y);
			z -= QuickMath.floor(z);
			ray.u = z;
			ray.v = y;
			hit = true;
		}
		triangle = WaterModel.southt[c0][c1];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double x = ray.t * ray.d.x + ray.x.x;
			double y = ray.t * ray.d.y + ray.x.y;
			x -= QuickMath.floor(x);
			y -= QuickMath.floor(y);
			ray.u = x;
			ray.v = y;
			hit = true;
		}
		triangle = WaterModel.southb[c1];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double x = ray.t * ray.d.x + ray.x.x;
			double y = ray.t * ray.d.y + ray.x.y;
			x -= QuickMath.floor(x);
			y -= QuickMath.floor(y);
			ray.u = x;
			ray.v = y;
			hit = true;
		}
		triangle = WaterModel.northt[c2][c3];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double x = ray.t * ray.d.x + ray.x.x;
			double y = ray.t * ray.d.y + ray.x.y;
			x -= QuickMath.floor(x);
			y -= QuickMath.floor(y);
			ray.u = 1-x;
			ray.v = y;
			hit = true;
		}
		triangle = WaterModel.northb[c2];
		if (triangle.intersect(ray)) {
			ray.n.set(triangle.n);
			ray.n.scale(QuickMath.signum(-ray.d.dot(triangle.n)));
			ray.t = ray.tNear;
			double x = ray.t * ray.d.x + ray.x.x;
			double y = ray.t * ray.d.y + ray.x.y;
			x -= QuickMath.floor(x);
			y -= QuickMath.floor(y);
			ray.u = 1-x;
			ray.v = y;
			hit = true;
		}
		if (hit) {
			Texture.lava.getColor(ray);
			ray.color.w = 1;
			ray.distance += ray.tNear;
			ray.x.scaleAdd(ray.tNear, ray.d);
			return true;
		}
		return false;
	}
}
