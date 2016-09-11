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

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Material;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Random;

/**
 * Static methods for path tracing
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PathTracer {

  private static final double EXTINCTION_FACTOR = 0.04;

  /**
   * Path trace the ray.
   */
  public static void pathTrace(Scene scene, WorkerState state) {
    Ray ray = state.ray;
    if (scene.isInWater(ray)) {
      ray.setCurrentMat(Block.WATER, 0);
    } else {
      ray.setCurrentMat(Block.AIR, 0);
    }
    pathTrace(scene, ray, state, 1, true);
  }

  /**
   * Path trace the ray in this scene.
   */
  public static boolean pathTrace(Scene scene, Ray ray, WorkerState state, int addEmitted,
      boolean first) {

    boolean hit = false;
    Random random = state.random;
    Vector3 ox = new Vector3(ray.o);
    Vector3 od = new Vector3(ray.d);
    double s = 0;

    while (true) {

      if (!RayTracer.nextIntersection(scene, ray)) {
        if (ray.getPrevMaterial() == Block.WATER) {
          ray.color.set(0, 0, 0, 1);
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

      Material currentMat = ray.getCurrentMaterial();
      Material prevMat = ray.getPrevMaterial();

      if (!scene.stillWater && ray.n.y != 0 &&
          ((currentMat == Block.WATER && prevMat == Block.AIR) || (currentMat == Block.AIR
              && prevMat == Block.WATER))) {

        WaterModel.doWaterDisplacement(ray);

        if (currentMat == Block.AIR) {
          ray.n.y = -ray.n.y;
        }
      }

      if (currentMat.isShiny) {
        if (currentMat == Block.WATER) {
          pSpecular = Scene.WATER_SPECULAR;
        } else {
          pSpecular = Scene.SPECULAR_COEFF;
        }
      }

      double pDiffuse = ray.color.w;

      float n1 = prevMat.ior;
      float n2 = currentMat.ior;

      if (pDiffuse + pSpecular < Ray.EPSILON && n1 == n2)
        continue;

      if (first) {
        s = ray.distance;
        first = false;
      }

      if (currentMat.isShiny && random.nextDouble() < pSpecular) {

        if (!scene.kill(ray.depth + 1, random)) {
          Ray reflected = new Ray();
          reflected.specularReflection(ray);

          if (pathTrace(scene, reflected, state, 1, false)) {
            ray.color.x *= reflected.color.x;
            ray.color.y *= reflected.color.y;
            ray.color.z *= reflected.color.z;
            hit = true;
          }
        }

      } else {

        if (random.nextDouble() < pDiffuse) {

          if (!scene.kill(ray.depth + 1, random)) {
            Ray reflected = new Ray();
            reflected.set(ray);

            double emittance = 0;

            if (scene.emittersEnabled && currentMat.isEmitter) {

              emittance = addEmitted;
              ray.emittance.x = ray.color.x * ray.color.x *
                  currentMat.emittance * scene.emitterIntensity;
              ray.emittance.y = ray.color.y * ray.color.y *
                  currentMat.emittance * scene.emitterIntensity;
              ray.emittance.z = ray.color.z * ray.color.z *
                  currentMat.emittance * scene.emitterIntensity;
              hit = true;
            }

            if (scene.sunEnabled) {
              scene.sun.getRandomSunDirection(reflected, random);

              double directLightR = 0;
              double directLightG = 0;
              double directLightB = 0;

              boolean frontLight = reflected.d.dot(ray.n) > 0;

              if (frontLight || (currentMat.subSurfaceScattering
                  && random.nextDouble() < Scene.fSubSurface)) {

                if (!frontLight) {
                  reflected.o.scaleAdd(-Ray.OFFSET, ray.n);
                }

                reflected.setCurrentMat(reflected.getPrevMaterial(), reflected.getPrevData());

                getDirectLightAttenuation(scene, reflected, state);

                Vector4 attenuation = state.attenuation;
                if (attenuation.w > 0) {
                  double mult = QuickMath.abs(reflected.d.dot(ray.n));
                  directLightR = attenuation.x * attenuation.w * mult;
                  directLightG = attenuation.y * attenuation.w * mult;
                  directLightB = attenuation.z * attenuation.w * mult;
                  hit = true;
                }
              }

              reflected.diffuseReflection(ray, random);
              hit = pathTrace(scene, reflected, state, 0, false) || hit;
              if (hit) {
                ray.color.x = ray.color.x * (emittance + directLightR * scene.sun.emittance.x + (
                    reflected.color.x + reflected.emittance.x));
                ray.color.y = ray.color.y * (emittance + directLightG * scene.sun.emittance.y + (
                    reflected.color.y + reflected.emittance.y));
                ray.color.z = ray.color.z * (emittance + directLightB * scene.sun.emittance.z + (
                    reflected.color.z + reflected.emittance.z));
              }

            } else {
              reflected.diffuseReflection(ray, random);

              hit = pathTrace(scene, reflected, state, 0, false) || hit;
              if (hit) {
                ray.color.x =
                    ray.color.x * (emittance + (reflected.color.x + reflected.emittance.x));
                ray.color.y =
                    ray.color.y * (emittance + (reflected.color.y + reflected.emittance.y));
                ray.color.z =
                    ray.color.z * (emittance + (reflected.color.z + reflected.emittance.z));
              }
            }
          }
        } else if (n1 != n2) {

          boolean doRefraction = currentMat == Block.WATER ||
              prevMat == Block.WATER ||
              currentMat == Block.ICE ||
              prevMat == Block.ICE;

          // refraction
          float n1n2 = n1 / n2;
          double cosTheta = -ray.n.dot(ray.d);
          double radicand = 1 - n1n2 * n1n2 * (1 - cosTheta * cosTheta);
          if (doRefraction && radicand < Ray.EPSILON) {
            // total internal reflection
            if (!scene.kill(ray.depth + 1, random)) {
              Ray reflected = new Ray();
              reflected.specularReflection(ray);
              if (pathTrace(scene, reflected, state, 1, false)) {

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
              // Fresnel equation approximation
              // R(cosineAngle) = R0 + (1 - R0) * (1 - cos(cosineAngle))^5
              float a = (n1n2 - 1);
              float b = (n1n2 + 1);
              double R0 = a * a / (b * b);
              double c = 1 - cosTheta;
              double Rtheta = R0 + (1 - R0) * c * c * c * c * c;

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

                if (pathTrace(scene, refracted, state, 1, false)) {
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

          if (pathTrace(scene, transmitted, state, 1, false)) {
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

      if (hit && prevMat == Block.WATER) {
        // do water fog
        double a = ray.distance / scene.waterVisibility;
        double attenuation = 1 - QuickMath.min(1, a * a);
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

    // This is a simplistic fog model which gives greater artistic freedom but
    // less realism. The user can select fog color and density; in a more
    // realistic model color would depend on viewing angle and sun color/position.
    if (s > 0 && scene.fogEnabled()) {
      Sun sun = scene.sun;

      // pick point between ray origin and intersected object
      Ray atmos = new Ray();
      double offset = QuickMath.clamp(s * random.nextFloat(), Ray.EPSILON, s - Ray.EPSILON);
      atmos.o.scaleAdd(offset, od, ox);
      sun.getRandomSunDirection(atmos, random);
      atmos.setCurrentMat(Block.AIR, 0);

      double fogDensity = scene.getFogDensity() * EXTINCTION_FACTOR;
      double extinction = Math.exp(-s * fogDensity);
      ray.color.scale(extinction);

      // check sun visibility at random point to determine inscatter brightness
      getDirectLightAttenuation(scene, atmos, state);
      Vector4 attenuation = state.attenuation;
      if (attenuation.w > Ray.EPSILON) {
        Vector3 fogColor = scene.getFogColor();
        double inscatter;
        if (scene.fastFog()) {
          inscatter = (1 - extinction);
        } else {
          inscatter = s * fogDensity * Math.exp(-offset * fogDensity);
        }
        ray.color.x += attenuation.x * attenuation.w * fogColor.x * inscatter;
        ray.color.y += attenuation.y * attenuation.w * fogColor.y * inscatter;
        ray.color.z += attenuation.z * attenuation.w * fogColor.z * inscatter;
      }
    }

    return hit;
  }

  /**
   * Calculate direct lighting attenuation.
   */
  public static void getDirectLightAttenuation(Scene scene, Ray ray, WorkerState state) {

    Vector4 attenuation = state.attenuation;
    attenuation.x = 1;
    attenuation.y = 1;
    attenuation.z = 1;
    attenuation.w = 1;
    while (attenuation.w > 0) {
      ray.o.scaleAdd(Ray.OFFSET, ray.d);
      if (!RayTracer.nextIntersection(scene, ray)) {
        break;
      }
      double mult = 1 - ray.color.w;
      attenuation.x *= ray.color.x * ray.color.w + mult;
      attenuation.y *= ray.color.y * ray.color.w + mult;
      attenuation.z *= ray.color.z * ray.color.w + mult;
      attenuation.w *= mult;
      if (ray.getPrevMaterial() == Block.WATER) {
        double a = ray.distance / scene.waterVisibility;
        attenuation.w *= 1 - QuickMath.min(1, a * a);
      }
    }
  }

}
