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
import se.llbit.chunky.world.Clouds;
import se.llbit.math.Ray;
import se.llbit.math.Ray.RayPool;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RayTracer {
	private static final double CLOUD_OPACITY = 0.9;

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
	 * @return Next intersection
	 */
	public static boolean nextIntersection(Scene scene, Ray ray, WorkerState state) {

		if (scene.sky().cloudsEnabled() && cloudIntersection(scene, ray)) {
			Ray oct = state.rayPool.get(ray);
			if (nextWorldIntersection(scene, oct, state.rayPool) &&
					oct.distance <= ray.distance) {
				ray.distance = oct.distance;
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
		} else {
			//return scene.intersect(ray);
			return nextWorldIntersection(scene, ray, state.rayPool);
		}
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

	private static boolean cloudIntersection(Scene scene, Ray ray) {
		double offsetX = scene.sky().cloudXOffset();
		double offsetY = scene.sky().cloudYOffset();
		double offsetZ = scene.sky().cloudZOffset();
		double inv_size = 1/scene.sky().cloudSize();
		double cloudBot = offsetY - scene.origin.y;
		double cloudTop = offsetY - scene.origin.y + 5;
		int target = 1;
		double t_offset = 0;
		ray.tNear = Double.POSITIVE_INFINITY;
		if (ray.x.y < cloudBot || ray.x.y > cloudTop) {
			if (ray.d.y > 0) {
				t_offset = (cloudBot - ray.x.y) / ray.d.y;
			} else {
				t_offset = (cloudTop - ray.x.y) / ray.d.y;
			}
			if (t_offset < 0) {
				return false;
			}
			// ray is entering cloud
			if (inCloud((ray.d.x*t_offset + ray.x.x)*inv_size + offsetX, (ray.d.z*t_offset + ray.x.z)*inv_size + offsetZ)) {
				ray.tNear = t_offset;
				ray.distance += t_offset;
				ray.n.set(0, -Math.signum(ray.d.y), 0);
				ray.color.set(1,1,1,CLOUD_OPACITY);
				return true;
			}
		} else if (inCloud(ray.x.x*inv_size + offsetX, ray.x.z*inv_size + offsetZ)) {
			target = 0;
			return false;
		}
		double tExit = Double.MAX_VALUE;
		if (ray.d.y > 0) {
			tExit = (cloudTop - ray.x.y) / ray.d.y - t_offset;
		} else {
			tExit = (cloudBot - ray.x.y) / ray.d.y - t_offset;
		}
		double x0 = (ray.x.x + ray.d.x*t_offset)*inv_size + offsetX;
		double z0 = (ray.x.z + ray.d.z*t_offset)*inv_size + offsetZ;
		double xp = x0;
		double zp = z0;
		int ix = (int) Math.floor(xp);
		int iz = (int) Math.floor(zp);
		int xmod = (int)Math.signum(ray.d.x), zmod = (int)Math.signum(ray.d.z);
		double dx = Math.abs(ray.d.x)*inv_size;
		double dz = Math.abs(ray.d.z)*inv_size;
		double t = 0;
		int i = 0;
		int nx = 0, nz = 0;
		if (dx > dz) {
			double m = dz/dx;
			double xrem = xmod * (ix+0.5*(1+xmod) - xp);
			double zlimit = xrem*m;
			while (t < tExit) {
				double zrem = zmod * (iz+0.5*(1+zmod) - zp);
				zp = z0 + zmod * (i+1) * m;
				if (zrem < zlimit) {
					iz += zmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = i/dx + zrem/dz;
						nx = 0;
						nz = -zmod;
						break;
					}
					ix += xmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+xrem)/dx;
						nx = -xmod;
						nz = 0;
						break;
					}
				} else {
					ix += xmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+xrem)/dx;
						nx = -xmod;
						nz = 0;
						break;
					}
					if (zrem <= m) {
						iz += zmod;
						if (Clouds.getCloud(ix, iz) == target) {
							t = i/dx + zrem/dz;
							nx = 0;
							nz = -zmod;
							break;
						}
					}
				}
				t = i/dx;
				i+=1;
			}
		} else {
			double m = dx/dz;
			double zrem = zmod * (iz+0.5*(1+zmod) - zp);
			double xlimit = zrem*m;
			while (t < tExit) {
				double xrem = xmod * (ix+0.5*(1+xmod) - xp);
				xp = x0 + xmod * (i+1) * m;
				if (xrem < xlimit) {
					ix += xmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = i/dz + xrem/dx;
						nx = -xmod;
						nz = 0;
						break;
					}
					iz += zmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+zrem)/dz;
						nx = 0;
						nz = -zmod;
						break;
					}
				} else {
					iz += zmod;
					if (Clouds.getCloud(ix, iz) == target) {
						t = (i+zrem)/dz;
						nx = 0;
						nz = -zmod;
						break;
					}
					if (xrem <= m) {
						ix += xmod;
						if (Clouds.getCloud(ix, iz) == target) {
							t = i/dz + xrem/dx;
							nx = -xmod;
							nz = 0;
							break;
						}
					}
				}
				t = i/dz;
				i+=1;
			}
		}
		int ny = 0;
		if (target == 1) {
			if (t > tExit) {
				return false;
			}
		} else {
			if (t > tExit) {
				nx = 0;
				ny = (int) Math.signum(ray.d.y);
				nz = 0;
				t = tExit;
			} else {
				nx = -nx;
				nz = -nz;
			}
		}
		ray.n.set(nx, ny, nz);
		ray.tNear = t + t_offset;
		ray.distance += ray.tNear;
		ray.color.set(1, 1, 1, CLOUD_OPACITY);
		return true;
	}

	private static boolean inCloud(double x, double z) {
		return Clouds.getCloud((int)Math.floor(x), (int)Math.floor(z)) == 1;
	}


}
