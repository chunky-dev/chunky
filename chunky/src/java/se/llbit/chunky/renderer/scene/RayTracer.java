/* Copyright (c) 2013-2014 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Block;
import se.llbit.math.Ray;
import se.llbit.math.Ray.RayPool;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RayTracer {

	/**
	 * @param scene
	 * @param state
	 */
	public static void quickTrace(Scene scene, WorkerState state) {
		Ray ray = state.ray;
		while (true) {
			if (!nextIntersection(scene, ray, state)) {
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
	 * @param state
	 * @return occlusion value
	 */
	public static double skyOcclusion(Scene scene, WorkerState state) {
		Ray ray = state.ray;
		double occlusion = 1.0;
		while (true) {
			if (!nextIntersection(scene, ray, state)) {
				if (scene.waterHeight > 0 &&
						ray.d.y < 0 && ray.x.y > scene.waterHeight-.125) {

					ray.t = (scene.waterHeight-.125-ray.x.y) / ray.d.y;
					ray.distance += ray.t;
					ray.x.scaleAdd(ray.t, ray.d, ray.x);
					ray.currentMaterial = Block.WATER.id;
					ray.prevMaterial = 0;
					WaterModel.intersect(ray);
					occlusion *= (1 - ray.color.w);
				}
				break;
			} else {
				occlusion *= (1 - ray.color.w);
				ray.x.scaleAdd(Ray.OFFSET, ray.d, ray.x);
			}
		}
		return 1-occlusion;
	}

	/**
	 * @param scene
	 * @param state
	 * @return Next intersection
	 */
	public static boolean nextIntersection(Scene scene, Ray ray, WorkerState state) {

		if (scene.sky().cloudsEnabled()) {
			Ray oct = state.rayPool.get(ray);
			if  (scene.sky().cloudIntersection(scene, ray, state.random)) {
				if (nextWorldIntersection(scene, oct, state.rayPool) &&
						oct.distance <= ray.distance) {
					ray.distance = oct.distance;
					ray.d.set(oct.d);
					ray.x.set(oct.x);
					ray.n.set(oct.n);
					ray.color.set(oct.color);
					ray.prevMaterial = oct.prevMaterial;
					ray.currentMaterial = oct.currentMaterial;
				} else {
					ray.prevMaterial = ray.currentMaterial;
					ray.currentMaterial = Block.GRASS_ID;
					ray.x.scaleAdd(ray.tNear + Ray.EPSILON, ray.d);
				}
				state.rayPool.dispose(oct);
				return true;
			}
		}
		return nextWorldIntersection(scene, ray, state.rayPool);
	}

	private static boolean nextWorldIntersection(Scene scene, Ray ray,
			RayPool rayPool) {

		boolean hit = false;
		Ray oct = rayPool.get(ray);
		if (scene.intersect(oct)) {
			ray.distance = oct.distance;
			ray.x.set(oct.x);
			ray.n.set(oct.n);
			ray.color.set(oct.color);
			ray.prevMaterial = oct.prevMaterial;
			ray.currentMaterial = oct.currentMaterial;
			hit = true;
		} else if (scene.waterHeight > 0 &&
				ray.d.y < 0 && ray.x.y > scene.waterHeight-.125) {

			// infinite water intersection
			ray.t = (scene.waterHeight-.125-ray.x.y) / ray.d.y;
			ray.distance += ray.t;
			ray.x.scaleAdd(ray.t, ray.d, ray.x);
			ray.currentMaterial = Block.WATER_ID;
			ray.prevMaterial = 0;
			WaterModel.intersect(ray);

			hit = true;

		} else {
			ray.currentMaterial = Block.AIR_ID;
		}
		rayPool.dispose(oct);
		return hit;
	}

}
