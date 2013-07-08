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

/**
 * A textured block.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturedBlockModel {
	private static final AABB block = new AABB(0, 1, 0, 1, 0, 1);

	/**
	 * Find intersection between ray and block
	 * @param ray
	 * @param texture 0 = north, 1 = south, 2 = east, 3 = west,
	 * 4 = top, 5 = bottom
	 * @return <code>true</code> if the ray intersected the block
	 */
	public static boolean intersect(Ray ray, Texture[] texture) {
		ray.t = Double.POSITIVE_INFINITY;
		if (block.intersect(ray)) {
			float[] color;
			if (ray.n.z < 0)
				color = texture[0].getColor(ray.u, ray.v);
			else if (ray.n.z > 0)
				color = texture[1].getColor(ray.u, ray.v);
			else if (ray.n.x > 0)
				color = texture[2].getColor(ray.u, ray.v);
			else if (ray.n.x < 0)
				color = texture[3].getColor(ray.u, ray.v);
			else if (ray.n.y > 0)
				color = texture[4].getColor(ray.u, ray.v);
			else
				color = texture[5].getColor(ray.u, ray.v);

			if (color[3] > Ray.EPSILON) {
				ray.color.set(color);
				ray.distance += ray.tNear;
				ray.x.scaleAdd(ray.tNear, ray.d, ray.x);
				return true;
			}
		}
		return false;
	}

	/**
	 * Find intersection between ray and block.
	 * @param ray
	 * @param texture Texture array
	 * @param index An index array used to index the texture array
	 * @return <code>true</code> if the ray intersected the block
	 */
	public static boolean intersect(Ray ray, Texture[] texture, int[] index) {
		ray.t = Double.POSITIVE_INFINITY;
		if (block.intersect(ray)) {
			float[] color;
			if (ray.n.z < 0)
				color = texture[index[0]].getColor(ray.u, ray.v);
			else if (ray.n.z > 0)
				color = texture[index[1]].getColor(ray.u, ray.v);
			else if (ray.n.x > 0)
				color = texture[index[2]].getColor(ray.u, ray.v);
			else if (ray.n.x < 0)
				color = texture[index[3]].getColor(ray.u, ray.v);
			else if (ray.n.y > 0)
				color = texture[index[4]].getColor(ray.u, ray.v);
			else
				color = texture[index[5]].getColor(ray.u, ray.v);

			if (color[3] > Ray.EPSILON) {
				ray.color.set(color);
				ray.distance += ray.tNear;
				ray.x.scaleAdd(ray.tNear, ray.d, ray.x);
				return true;
			}
		}
		return false;
	}
	/**
	 * Find intersection between ray and block
	 * @param ray
	 * @param texture Block texture
	 * @return <code>true</code> if the ray intersected the block
	 */
	public static boolean intersect(Ray ray, Texture texture) {
		ray.t = Double.POSITIVE_INFINITY;
		if (block.intersect(ray)) {
			float[] color = texture.getColor(ray.u, ray.v);
			if (color[3] > Ray.EPSILON) {
				ray.color.set(color);
				ray.distance += ray.tNear;
				ray.x.scaleAdd(ray.tNear, ray.d, ray.x);
				return true;
			}
		}
		return false;
	}

	/**
	 * Find the color of the object at the intersection point
	 * @param ray
	 */
	public static void getIntersectionColor(Ray ray) {

		if (ray.currentMaterial == 0) {
			ray.color.x = 1;
			ray.color.y = 1;
			ray.color.z = 1;
			ray.color.w = 0;
			return;
		}

		calcUVCoords(ray);

		ray.getCurrentBlock().getTexture().getColor(ray);
	}

	/**
 	 * Calculate the UV coordinates for the ray on the intersected block.
 	 * @param ray
 	 */
	private static void calcUVCoords(Ray ray) {
		int bx = (int) QuickMath.floor(ray.x.x);
		int by = (int) QuickMath.floor(ray.x.y);
		int bz = (int) QuickMath.floor(ray.x.z);
		if (ray.n.y != 0) {
			ray.u = ray.x.x - bx;
			ray.v = ray.x.z - bz;
		} else if (ray.n.x != 0) {
			ray.u = ray.x.z - bz;
			ray.v = ray.x.y - by;
		} else {
			ray.u = ray.x.x - bx;
			ray.v = ray.x.y - by;
		}
		if (ray.n.x > 0 || ray.n.z < 0)
			ray.u = 1-ray.u;
	}

}
