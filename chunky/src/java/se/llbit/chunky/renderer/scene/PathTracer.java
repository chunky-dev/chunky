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

import java.util.Random;

import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Material;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;
import se.llbit.math.Vector4d;

/**
 * Static methods for path tracing
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PathTracer {

	/**
	 * Path trace the ray
	 * @param scene
	 * @param state
	 */
	public static final void pathTrace(Scene scene, WorkerState state) {
		Ray ray = state.ray;
		if (scene.isInWater(ray)) {
			ray.setCurrentMat(Block.WATER, 0);
		} else {
			ray.setCurrentMat(Block.AIR, 0);
		}
		pathTrace(scene, ray, state, 1, true);
	}

	/**
	 * Path trace the ray in this scene
	 * @param scene
	 * @param state
	 * @param addEmitted
	 * @param first
	 */
	public static final boolean pathTrace(Scene scene, Ray ray, WorkerState state,
			int addEmitted, boolean first) {

		boolean hit = false;
		Random random = state.random;
		Vector3d ox = new Vector3d(ray.o);
		Vector3d od = new Vector3d(ray.d);
		double s = 0;

		while (true) {

			if (!RayTracer.nextIntersection(scene, ray, state)) {
				if (ray.getPrevMaterial() == Block.WATER) {
					ray.color.set(0,0,0,1);
					hit = true;
				} else if (ray.depth == 0) {
					// direct sky hit
					if (!scene.transparentSky()) {
						scene.sky.getSkyColorInterpolated(ray);
						hit = true;
					}
				} else if (ray.specular) {
					// sky color
					scene.sky.getSkySpecularColor(ray);
					hit = true;
				} else {
					scene.sky.getSkyColor(ray);
					hit = true;
				}
				break;
			}

			double pSpecular = 0;

			Material currentBlock = ray.getCurrentMaterial();
			Material prevBlock = ray.getPrevMaterial();

			if (!scene.stillWater && ray.n.y != 0 &&
					((currentBlock == Block.WATER && prevBlock == Block.AIR) ||
					(currentBlock == Block.AIR && prevBlock == Block.WATER))) {

				WaterModel.doWaterDisplacement(ray);

				if (currentBlock == Block.AIR) {
					ray.n.y = -ray.n.y;
				}
			}

			if (currentBlock.isShiny) {
				if (currentBlock == Block.WATER) {
					pSpecular = Scene.WATER_SPECULAR;
				} else {
					pSpecular = Scene.SPECULAR_COEFF;
				}
			}

			double pDiffuse = ray.color.w;

			float n1 = prevBlock.ior;
			float n2 = currentBlock.ior;

			if (pDiffuse + pSpecular < Ray.EPSILON && n1 == n2)
				continue;

			if (first) {
				s = ray.distance;
				first = false;
			}

			if (currentBlock.isShiny &&
					random.nextDouble() < pSpecular) {

				Ray reflected = new Ray();
				reflected.specularReflection(ray);

				if (!scene.kill(reflected, random)) {
					if (pathTrace(scene, reflected, state, 1, false)) {
						ray.color.x *= reflected.color.x;
						ray.color.y *= reflected.color.y;
						ray.color.z *= reflected.color.z;
						hit = true;
					}
				}

			} else {

				if (random.nextDouble() < pDiffuse) {

					Ray reflected = new Ray();
					reflected.set(ray);
					if (!scene.kill(reflected, random)) {

						double emittance = 0;

						if (scene.emittersEnabled && currentBlock.isEmitter) {

							emittance = addEmitted;
							ray.emittance.x = ray.color.x * ray.color.x *
									currentBlock.emittance * scene.emitterIntensity;
							ray.emittance.y = ray.color.y * ray.color.y *
									currentBlock.emittance * scene.emitterIntensity;
							ray.emittance.z = ray.color.z * ray.color.z *
									currentBlock.emittance * scene.emitterIntensity;
							hit = true;
						}

						if (scene.sunEnabled) {
							scene.sun.getRandomSunDirection(reflected, random);

							double directLightR = 0;
							double directLightG = 0;
							double directLightB = 0;

							boolean frontLight = reflected.d.dot(ray.n) > 0;

							if (frontLight || (currentBlock.subSurfaceScattering &&
									random.nextDouble() < Scene.fSubSurface)) {

								if (!frontLight) {
									reflected.o.scaleAdd(-Ray.OFFSET, ray.n);
								}

								reflected.setCurrentMat(reflected.getPrevMaterial(), reflected.getPrevData());

								getDirectLightAttenuation(scene, reflected, state);

								Vector4d attenuation = state.attenuation;
								if (attenuation.w > 0) {
									double mult = QuickMath.abs(reflected.d.dot(ray.n));
									directLightR = attenuation.x*attenuation.w * mult;
									directLightG = attenuation.y*attenuation.w * mult;
									directLightB = attenuation.z*attenuation.w * mult;
									hit = true;
								}
							}

							reflected.diffuseReflection(ray, random);
							hit = pathTrace(scene, reflected, state, 0, false) || hit;
							if (hit) {
								ray.color.x = ray.color.x
									* (emittance + directLightR * scene.sun.emittance.x
										+ (reflected.color.x + reflected.emittance.x));
								ray.color.y = ray.color.y
									* (emittance + directLightG * scene.sun.emittance.y
										+ (reflected.color.y + reflected.emittance.y));
								ray.color.z = ray.color.z
									* (emittance + directLightB * scene.sun.emittance.z
										+ (reflected.color.z + reflected.emittance.z));
							}

						} else {
							reflected.diffuseReflection(ray, random);

							hit = pathTrace(scene, reflected, state, 0, false) || hit;
							if (hit) {
								ray.color.x = ray.color.x
									* (emittance + (reflected.color.x + reflected.emittance.x));
								ray.color.y = ray.color.y
									* (emittance + (reflected.color.y + reflected.emittance.y));
								ray.color.z = ray.color.z
									* (emittance + (reflected.color.z + reflected.emittance.z));
							}
						}
					}
				} else if (n1 != n2) {

					boolean doRefraction =
							currentBlock == Block.WATER ||
							prevBlock == Block.WATER ||
							currentBlock == Block.ICE ||
							prevBlock == Block.ICE;

					// refraction
					float n1n2 = n1 / n2;
					double cosTheta = - ray.n.dot(ray.d);
					double radicand = 1 - n1n2*n1n2 * (1 - cosTheta*cosTheta);
					if (doRefraction && radicand < Ray.EPSILON) {
						// total internal reflection
						Ray reflected = new Ray();
						reflected.specularReflection(ray);
						if (!scene.kill(reflected, random)) {
							if (pathTrace(scene, reflected, state, 1, false)) {

								ray.color.x = reflected.color.x;
								ray.color.y = reflected.color.y;
								ray.color.z = reflected.color.z;
								hit = true;
							}
						}
					} else {
						Ray refracted = new Ray();
						refracted.set(ray);
						if (!scene.kill(refracted, random)) {

							// Calculate angle-dependent reflectance using
							// Fresnel equation approximation
							// R(theta) = R0 + (1 - R0) * (1 - cos(theta))^5
							float a = (n1n2 - 1);
							float b = (n1n2 + 1);
							double R0 = a*a/(b*b);
							double c = 1 - cosTheta;
							double Rtheta = R0 + (1-R0) * c*c*c*c*c;

							if (random.nextDouble() < Rtheta) {
								Ray reflected = new Ray();
								reflected.specularReflection(ray);
								if (pathTrace(scene, reflected, state, 1, false)) {
									ray.color.x = reflected.color.x;
									ray.color.y = reflected.color.y;
									ray.color.z = reflected.color.z;
									hit = true;
								}
							} else {
								if (doRefraction) {

									double t2 = FastMath.sqrt(radicand);
									if (cosTheta > 0) {
										refracted.d.x = n1n2*ray.d.x + (n1n2*cosTheta - t2)*ray.n.x;
										refracted.d.y = n1n2*ray.d.y + (n1n2*cosTheta - t2)*ray.n.y;
										refracted.d.z = n1n2*ray.d.z + (n1n2*cosTheta - t2)*ray.n.z;
									} else {
										refracted.d.x = n1n2*ray.d.x - (-n1n2*cosTheta - t2)*ray.n.x;
										refracted.d.y = n1n2*ray.d.y - (-n1n2*cosTheta - t2)*ray.n.y;
										refracted.d.z = n1n2*ray.d.z - (-n1n2*cosTheta - t2)*ray.n.z;
									}

									refracted.d.normalize();

									refracted.o.scaleAdd(Ray.OFFSET, refracted.d);
								}

								if (pathTrace(scene, refracted, state, 1, false)) {
									ray.color.x = ray.color.x * pDiffuse + (1-pDiffuse);
									ray.color.y = ray.color.y * pDiffuse + (1-pDiffuse);
									ray.color.z = ray.color.z * pDiffuse + (1-pDiffuse);
									ray.color.x *= refracted.color.x;
									ray.color.y *= refracted.color.y;
									ray.color.z *= refracted.color.z;
									hit = true;
								}
							}
						}
					}

				} else {

					Ray transmitted = new Ray();
					transmitted.set(ray);
					transmitted.o.scaleAdd(Ray.OFFSET, transmitted.d);

					if (pathTrace(scene, transmitted, state, 1, false)) {
						ray.color.x = ray.color.x * pDiffuse + (1-pDiffuse);
						ray.color.y = ray.color.y * pDiffuse + (1-pDiffuse);
						ray.color.z = ray.color.z * pDiffuse + (1-pDiffuse);
						ray.color.x *= transmitted.color.x;
						ray.color.y *= transmitted.color.y;
						ray.color.z *= transmitted.color.z;
						hit = true;
					}
				}
			}

			if (hit && prevBlock == Block.WATER) {
				// do water fog
				double a = ray.distance / scene.waterVisibility;
				double attenuation = 1 - QuickMath.min(1, a*a);
				ray.color.scale(attenuation);
				/*ray.color.x *= attenuation;
				ray.color.y *= attenuation;
				ray.color.z *= attenuation;
				float[] wc = Texture.water.getAvgColorLinear();
				ray.color.x += (1-attenuation) * wc[0];
				ray.color.y += (1-attenuation) * wc[1];
				ray.color.z += (1-attenuation) * wc[2];
				ray.color.w = attenuation;*/
			}

			break;
		}
		if (!hit) {
			ray.color.set(0, 0, 0, 1);
			if (first) {
				s = ray.distance;
			}
		}

		if (s > 0) {

			if (scene.atmosphereEnabled) {
				double Fex = scene.sun.extinction(s);
				ray.color.x *= Fex;
				ray.color.y *= Fex;
				ray.color.z *= Fex;

				if (!scene.volumetricFogEnabled) {
					double Fin = scene.sun.inscatter(Fex, scene.sun.theta(ray.d));

					ray.color.x += Fin * scene.sun.emittance.x * scene.sun.getIntensity();
					ray.color.y += Fin * scene.sun.emittance.y * scene.sun.getIntensity();
					ray.color.z += Fin * scene.sun.emittance.z * scene.sun.getIntensity();
				}
			}

			if (scene.volumetricFogEnabled) {
				s = (s - Ray.OFFSET) * random.nextDouble();

				Ray reflected = new Ray();
				reflected.o.scaleAdd(s, od, ox);
				scene.sun.getRandomSunDirection(reflected, random);
				reflected.setCurrentMat(Block.AIR, 0);

				getDirectLightAttenuation(scene, reflected, state);
				Vector4d attenuation = state.attenuation;

				double Fex = scene.sun.extinction(s);
				double Fin = scene.sun.inscatter(Fex, scene.sun.theta(ray.d));

				ray.color.x += 50 * attenuation.x*attenuation.w * Fin * scene.sun.emittance.x * scene.sun.getIntensity();
				ray.color.y += 50 * attenuation.y*attenuation.w * Fin * scene.sun.emittance.y * scene.sun.getIntensity();
				ray.color.z += 50 * attenuation.z*attenuation.w * Fin * scene.sun.emittance.z * scene.sun.getIntensity();
			}
		}

		return hit;
	}

	/**
	 * Calculate direct lighting attenuation
	 * @param scene
	 * @param ray
	 * @param state
	 */
	public static final void getDirectLightAttenuation(Scene scene, Ray ray,
			WorkerState state) {

		Vector4d attenuation = state.attenuation;
		attenuation.x = 1;
		attenuation.y = 1;
		attenuation.z = 1;
		attenuation.w = 1;
		while (attenuation.w > 0) {
			ray.o.scaleAdd(Ray.OFFSET, ray.d);
			if (!RayTracer.nextIntersection(scene, ray, state))
				break;
			double mult = 1 - ray.color.w;
			attenuation.x *= ray.color.x * ray.color.w + mult;
			attenuation.y *= ray.color.y * ray.color.w + mult;
			attenuation.z *= ray.color.z * ray.color.w + mult;
			attenuation.w *= mult;
			if (ray.getPrevMaterial() == Block.WATER) {
				double a = ray.distance / scene.waterVisibility;
				attenuation.w *= 1 - QuickMath.min(1, a*a);
			}
		}
	}

}
