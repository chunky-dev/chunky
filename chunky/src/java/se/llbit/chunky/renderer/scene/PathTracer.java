/* Copyright (c) 2013-2015 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.renderer.scene.EmitterSamplerFactory.EmitterSampler;
import se.llbit.chunky.renderer.scene.FogStrategyFactory.FogStrategy;
import se.llbit.chunky.world.Material;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;

/**
 * Basic path tracer.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PathTracer implements RayTracer {

  private final EmitterSamplerFactory emitterSamplerFactory;
  private final FogStrategyFactory fogStrategyFactory;

  public PathTracer() {
    this.emitterSamplerFactory = new EmitterSamplerFactory();
    this.fogStrategyFactory = new FogStrategyFactory();
  }

  /**
   * Path trace the ray.
   */
  @Override
  public void trace(Scene scene, WorkerState state) {
    Ray ray = state.ray;
    if (scene.isInWater(ray)) {
      ray.setCurrentMaterial(Water.INSTANCE);
    } else {
      ray.setCurrentMaterial(Air.INSTANCE);
    }
    pathTrace(scene, ray, state, 1, true,
        emitterSamplerFactory.create(scene), fogStrategyFactory.create(scene));
  }

  /**
   * Path trace the ray in this scene.
   *
   * @param firstReflection {@code true} if the ray has not yet hit the first diffuse or specular
   *                        reflection
   * @param fogStrategy
   */
  private static boolean pathTrace(Scene scene, Ray ray, WorkerState state, int addEmitted,
      boolean firstReflection, EmitterSampler emitterSampler, FogStrategy fogStrategy) {

    boolean hit = false;
    Random random = state.random;
    double airDistance = 0;

    while (true) {
      if (!RayTracers.nextIntersection(scene, ray)) {
        if (ray.getPrevMaterial().isWater()) {
          ray.color.set(0, 0, 0, 1);
          hit = true;
        } else if (ray.depth == 0) {
          // Direct sky hit.
          if (!scene.transparentSky()) {
            scene.sky.getSkyColorInterpolated(ray);
            scene.addSkyFog(ray);
            hit = true;
          }
        } else if (ray.specular) {
          // Indirect sky hit - specular color.
          scene.sky.getSkySpecularColor(ray);
          scene.addSkyFog(ray);
          hit = true;
        } else {
          // Indirect sky hit - diffuse color.
          scene.sky.getSkyColor(ray);
          // Skip sky fog - likely not noticeable in diffuse reflection.
          hit = true;
        }
        break;
      }

      Material currentMat = ray.getCurrentMaterial();
      Material prevMat = ray.getPrevMaterial();

      if (!scene.stillWater && ray.n.y != 0 &&
          ((currentMat.isWater() && prevMat == Air.INSTANCE)
              || (currentMat == Air.INSTANCE && prevMat.isWater()))) {
        WaterModel.doWaterDisplacement(ray);
        if (currentMat == Air.INSTANCE) {
          ray.n.y = -ray.n.y;
        }
      }

      float pSpecular = currentMat.specular;

      double pDiffuse = ray.color.w;

      float n1 = prevMat.ior;
      float n2 = currentMat.ior;

      if (prevMat == Air.INSTANCE || prevMat.isWater()) {
        airDistance = ray.distance;
      }

      if (pDiffuse + pSpecular < Ray.EPSILON && n1 == n2) {
        // Transmission without refraction.
        // This can happen when the ray passes through a transparent
        // material into another. It can also happen for example
        // when passing through a transparent part of an otherwise solid
        // object.
        // TODO: material color may change here.
        continue;
      }

      float pMetal = currentMat.metalness;
      boolean doMetal = pMetal > Ray.EPSILON && random.nextFloat() < pMetal;

      if (doMetal || (pSpecular > Ray.EPSILON && random.nextFloat() < pSpecular)) {
        // Specular reflection (metals only do specular reflection).

        firstReflection = false;

        if (!scene.kill(ray.depth + 1, random)) {
          Ray reflected = new Ray();
          reflected.specularReflection(ray, random);

          if (pathTrace(scene, reflected, state, 1, false, emitterSampler, fogStrategy)) {
            if (doMetal) {
              // use the albedo color as specular color
              ray.color.x *= reflected.color.x;
              ray.color.y *= reflected.color.y;
              ray.color.z *= reflected.color.z;
            } else {
              ray.color.x = reflected.color.x;
              ray.color.y = reflected.color.y;
              ray.color.z = reflected.color.z;
            }
            hit = true;
          }
        }

      } else {

        if (random.nextFloat() < pDiffuse) {
          // Diffuse reflection.

          firstReflection = false;

          if (!scene.kill(ray.depth + 1, random)) {
            Ray reflected = new Ray();

            float emittance = 0;

            Vector4 indirectEmitterColor = new Vector4(0, 0, 0, 0);

            if (scene.emittersEnabled && (!scene.isPreventNormalEmitterWithSampling()
                || scene.getEmitterSamplingStrategy() == EmitterSamplingStrategy.NONE
                || ray.depth == 0) && currentMat.emittance > Ray.EPSILON) {

              emittance = addEmitted;
              ray.emittance.x = ray.color.x * ray.color.x *
                  currentMat.emittance * scene.emitterIntensity;
              ray.emittance.y = ray.color.y * ray.color.y *
                  currentMat.emittance * scene.emitterIntensity;
              ray.emittance.z = ray.color.z * ray.color.z *
                  currentMat.emittance * scene.emitterIntensity;
              hit = true;
            } else if (scene.emittersEnabled
                && scene.emitterSamplingStrategy != EmitterSamplingStrategy.NONE
                && scene.getEmitterGrid() != null) {

              indirectEmitterColor = emitterSampler.sample(scene, ray, random);
            }

            if (scene.sunEnabled) {
              reflected.set(ray);
              scene.sun.getRandomSunDirection(reflected, random);

              double directLightR = 0;
              double directLightG = 0;
              double directLightB = 0;

              boolean frontLight = reflected.d.dot(ray.n) > 0;

              if (frontLight || (currentMat.subSurfaceScattering
                  && random.nextFloat() < Scene.fSubSurface)) {

                if (!frontLight) {
                  reflected.o.scaleAdd(-Ray.OFFSET, ray.n);
                }

                reflected.setCurrentMaterial(reflected.getPrevMaterial(), reflected.getPrevData());

                Vector4 attenuation = getDirectLightAttenuation(scene, reflected);

                if (attenuation.w > 0) {
                  double mult = QuickMath.abs(reflected.d.dot(ray.n));
                  directLightR = attenuation.x * attenuation.w * mult;
                  directLightG = attenuation.y * attenuation.w * mult;
                  directLightB = attenuation.z * attenuation.w * mult;
                  hit = true;
                }
              }

              reflected.diffuseReflection(ray, random);
              hit = pathTrace(scene, reflected, state, 0, false, emitterSampler,
                  fogStrategy) || hit;
              if (hit) {
                ray.color.x = ray.color.x * (emittance + directLightR * scene.sun.emittance.x + (
                    reflected.color.x + reflected.emittance.x) + (indirectEmitterColor.x));
                ray.color.y = ray.color.y * (emittance + directLightG * scene.sun.emittance.y + (
                    reflected.color.y + reflected.emittance.y) + (indirectEmitterColor.y));
                ray.color.z = ray.color.z * (emittance + directLightB * scene.sun.emittance.z + (
                    reflected.color.z + reflected.emittance.z) + (indirectEmitterColor.z));
              } else if (indirectEmitterColor.x > Ray.EPSILON
                  || indirectEmitterColor.y > Ray.EPSILON || indirectEmitterColor.z > Ray.EPSILON) {
                hit = true;
                ray.color.x *= indirectEmitterColor.x;
                ray.color.y *= indirectEmitterColor.y;
                ray.color.z *= indirectEmitterColor.z;
              }

            } else {
              reflected.diffuseReflection(ray, random);

              hit = pathTrace(scene, reflected, state, 0, false, emitterSampler,
                  fogStrategy) || hit;
              if (hit) {
                ray.color.x =
                    ray.color.x * (emittance + (reflected.color.x + reflected.emittance.x)
                        + (indirectEmitterColor.x));
                ray.color.y =
                    ray.color.y * (emittance + (reflected.color.y + reflected.emittance.y)
                        + (indirectEmitterColor.y));
                ray.color.z =
                    ray.color.z * (emittance + (reflected.color.z + reflected.emittance.z)
                        + (indirectEmitterColor.z));
              } else if (indirectEmitterColor.x > Ray.EPSILON
                  || indirectEmitterColor.y > Ray.EPSILON || indirectEmitterColor.z > Ray.EPSILON) {
                hit = true;
                ray.color.x *= indirectEmitterColor.x;
                ray.color.y *= indirectEmitterColor.y;
                ray.color.z *= indirectEmitterColor.z;
              }
            }
          }
        } else if (n1 != n2) {
          // Refraction.

          // TODO: make this decision dependent on the material properties:
          boolean doRefraction =
              currentMat.refractive || prevMat.refractive;

          // Refraction.
          float n1n2 = n1 / n2;
          double cosTheta = -ray.n.dot(ray.d);
          double radicand = 1 - n1n2 * n1n2 * (1 - cosTheta * cosTheta);
          if (doRefraction && radicand < Ray.EPSILON) {
            // Total internal reflection.
            if (!scene.kill(ray.depth + 1, random)) {
              Ray reflected = new Ray();
              reflected.specularReflection(ray, random);
              if (pathTrace(scene, reflected, state, 1, false, emitterSampler,
                  fogStrategy)) {

                ray.color.x = reflected.color.x;
                ray.color.y = reflected.color.y;
                ray.color.z = reflected.color.z;
                hit = true;
              }
            }
          } else {
            if (!scene.kill(ray.depth + 1, random)) {
              Ray refracted = new Ray();
              refracted.set(ray);

              // Calculate angle-dependent reflectance using
              // Fresnel equation approximation:
              // R(cosineAngle) = R0 + (1 - R0) * (1 - cos(cosineAngle))^5
              float a = (n1n2 - 1);
              float b = (n1n2 + 1);
              double R0 = a * a / (b * b);
              double c = 1 - cosTheta;
              double Rtheta = R0 + (1 - R0) * c * c * c * c * c;

              if (random.nextFloat() < Rtheta) {
                Ray reflected = new Ray();
                reflected.specularReflection(ray, random);
                if (pathTrace(scene, reflected, state, 1, false, emitterSampler,
                    fogStrategy)) {
                  ray.color.x = reflected.color.x;
                  ray.color.y = reflected.color.y;
                  ray.color.z = reflected.color.z;
                  hit = true;
                }
              } else {
                if (doRefraction) {

                  double t2 = FastMath.sqrt(radicand);
                  if (cosTheta > 0) {
                    refracted.d.x = n1n2 * ray.d.x + (n1n2 * cosTheta - t2) * ray.n.x;
                    refracted.d.y = n1n2 * ray.d.y + (n1n2 * cosTheta - t2) * ray.n.y;
                    refracted.d.z = n1n2 * ray.d.z + (n1n2 * cosTheta - t2) * ray.n.z;
                  } else {
                    refracted.d.x = n1n2 * ray.d.x - (-n1n2 * cosTheta - t2) * ray.n.x;
                    refracted.d.y = n1n2 * ray.d.y - (-n1n2 * cosTheta - t2) * ray.n.y;
                    refracted.d.z = n1n2 * ray.d.z - (-n1n2 * cosTheta - t2) * ray.n.z;
                  }

                  refracted.d.normalize();

                  refracted.o.scaleAdd(Ray.OFFSET, refracted.d);
                }

                if (pathTrace(scene, refracted, state, 1, false, emitterSampler,
                    fogStrategy)) {
                  ray.color.x = ray.color.x * pDiffuse + (1 - pDiffuse);
                  ray.color.y = ray.color.y * pDiffuse + (1 - pDiffuse);
                  ray.color.z = ray.color.z * pDiffuse + (1 - pDiffuse);
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

          if (pathTrace(scene, transmitted, state, 1, false, emitterSampler,
              fogStrategy)) {
            ray.color.x = ray.color.x * pDiffuse + (1 - pDiffuse);
            ray.color.y = ray.color.y * pDiffuse + (1 - pDiffuse);
            ray.color.z = ray.color.z * pDiffuse + (1 - pDiffuse);
            ray.color.x *= transmitted.color.x;
            ray.color.y *= transmitted.color.y;
            ray.color.z *= transmitted.color.z;
            hit = true;
          }
        }
      }

      if (hit && prevMat.isWater()) {
        // Render water fog effect.
        if (scene.waterVisibility == 0) {
          ray.color.scale(0.);
        } else {
          double a = ray.distance / scene.waterVisibility;
          double attenuation = Math.exp(-a);
          ray.color.scale(attenuation);
        }
      }

      break;
    }
    if (!hit) {
      ray.color.set(0, 0, 0, 1);
      if (firstReflection) {
        airDistance = ray.distance;
      }
    }

    fogStrategy.fog(scene, ray, random, airDistance);

    return hit;
  }

  /**
   * Calculate direct lighting attenuation.
   */
  public static Vector4 getDirectLightAttenuation(Scene scene, Ray ray) {
    Vector4 attenuation = new Vector4(1, 1, 1, 1);
    while (attenuation.w > 0) {
      ray.o.scaleAdd(Ray.OFFSET, ray.d);
      if (!RayTracers.nextIntersection(scene, ray)) {
        break;
      }
      double mult = 1 - ray.color.w;
      attenuation.x *= ray.color.x * ray.color.w + mult;
      attenuation.y *= ray.color.y * ray.color.w + mult;
      attenuation.z *= ray.color.z * ray.color.w + mult;
      attenuation.w *= mult;
      if (ray.getPrevMaterial().isWater()) {
        if (scene.waterVisibility == 0) {
          attenuation.w = 0;
        } else {
          double a = ray.distance / scene.waterVisibility;
          attenuation.w *= Math.exp(-a);
        }
      }
    }

    return attenuation;
  }
}
