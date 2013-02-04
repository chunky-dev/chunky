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
package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Clouds;
import se.llbit.math.Ray;
import se.llbit.math.Ray.RayPool;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RayTracer {
	/**
	 * @param scene
	 * @param ray
	 * @param rayPool
	 */
	public static void quickTrace(Scene scene, Ray ray, RayPool rayPool) {
		
		ray.x.x -= scene.origin.x;
		ray.x.y -= scene.origin.y;
		ray.x.z -= scene.origin.z;

		while (true) {
			if (!nextIntersection(scene, ray, rayPool)) {
				if (scene.waterHeight > 0 &&
						ray.d.y < 0 && ray.x.y > scene.waterHeight-.125) {
					
					ray.t = (scene.waterHeight-.125-ray.x.y) / ray.d.y;
					ray.distance += ray.t;
					ray.x.scaleAdd(ray.t, ray.d, ray.x);
					ray.currentMaterial = Block.WATER.id;
					ray.prevMaterial = 0;
					WaterModel.intersect(ray);
				}
				break;
			} else if (ray.getCurrentBlock() == Block.WATER) {
				break;
			} else if (ray.currentMaterial != 0 && ray.color.w > 0) {
				break;
			} else {
				ray.x.scaleAdd(Ray.OFFSET, ray.d, ray.x);
			}
		}
		
		if (ray.currentMaterial == 0) {
			scene.sky.getSkySpecularColor(ray, false);
		} else {
			scene.sun.flatShading(ray);
		}
	}

	/**
	 * @param scene
	 * @param ray
	 * @param rayPool
	 * @return Next intersection
	 */
	public static boolean nextIntersection(Scene scene, Ray ray,
			RayPool rayPool) {
		
		if (scene.cloudsEnabled && cloudIntersection(scene, ray)) {
			Ray oct = rayPool.get(ray);
			if (scene.intersect(oct) &&
					oct.distance <= (ray.tNear + ray.distance)) {
				ray.distance = oct.distance;
				ray.x.set(oct.x);
				ray.n.set(oct.n);
				ray.color.set(oct.color);
				ray.prevMaterial = oct.prevMaterial;
				ray.currentMaterial = oct.currentMaterial;
			} else {
				ray.color.set(1, 1, 1, 1);
				ray.prevMaterial = ray.currentMaterial;
				ray.currentMaterial = Block.GRASS_ID;
				ray.x.scaleAdd(ray.tNear, ray.d, ray.x);
				ray.n.set(0, -Math.signum(ray.d.y), 0);
				ray.distance += ray.tNear;
			}
			rayPool.dispose(oct);
			return true;
		} else {
			return scene.intersect(ray);
		}
	}

	private static boolean cloudIntersection(Scene scene, Ray ray) {
		if (ray.d.y != 0) {
			ray.t = (scene.cloudHeight - ray.x.y) / ray.d.y;
			if (ray.t > Ray.EPSILON) {
				double u = ray.x.x + ray.d.x * ray.t;
				double v = ray.x.z + ray.d.z * ray.t;
				if (Clouds.getCloud((int) (u/128), (int) (v/128)) != 0) {
					ray.tNear = ray.t;
					return true;
				}
			}
		}
		return false;
	}
	
}
