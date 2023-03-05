/* Copyright (c) 2013-2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2013-2022 Chunky Contributors
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
import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.SunSamplingStrategy;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Material;
import se.llbit.math.*;

import java.util.List;
import java.util.Random;

/**
 * Static methods for path tracing.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PathTracer implements RayTracer {

  /**
   * Path trace the ray.
   */
  @Override public void trace(Scene scene, WorkerState state) {
    Ray ray = state.ray;
    if (scene.isInWater(ray)) {
      ray.setCurrentMaterial(Water.INSTANCE);
    } else {
      ray.setCurrentMaterial(Air.INSTANCE);
    }
    pathTrace(scene, ray, state, 1, true);
  }

  /**
   * Path trace the ray in this scene.
   *
   * @param firstReflection {@code true} if the ray has not yet hit the first
   * diffuse or specular reflection
   */
  public static boolean pathTrace(Scene scene, Ray ray, WorkerState state, int addEmitted,
                                  boolean firstReflection) {

    boolean hit = false;
    Random random = state.random;
    Vector3 ox = new Vector3(ray.o);
    Vector3 od = new Vector3(ray.d);
    double airDistance = 0;

    while (true) {

      if (!PreviewRayTracer.nextIntersection(scene, ray)) {
        if (ray.getPrevMaterial().isWater()) {
          ray.color.set(0, 0, 0, 1);
          hit = true;
        } else if (ray.depth == 0) {
          // Direct sky hit.
          if (!scene.transparentSky()) {
            scene.sky.getSkyColorInterpolated(ray);
            addSkyFog(scene, ray, state, ox, od);
            hit = true;
          }
        } else if (ray.specular) {
          // Indirect sky hit - specular color.
          scene.sky.getSkyColor(ray, true);
          addSkyFog(scene, ray, state, ox, od);
          hit = true;
        } else {
          // Indirect sky hit - diffuse color.
          scene.sky.getSkyColorDiffuseSun(ray, scene.getSunSamplingStrategy().isDiffuseSun());
          // Skip sky fog - likely not noticeable in diffuse reflection.
          hit = true;
        }
        break;
      }

      Material currentMat = ray.getCurrentMaterial();
      Material prevMat = ray.getPrevMaterial();

      if (!scene.stillWater && ray.getNormal().y != 0 &&
        ((currentMat.isWater() && prevMat == Air.INSTANCE)
          || (currentMat == Air.INSTANCE && prevMat.isWater()))) {
        scene.getWaterShading().doWaterShading(ray, scene.getAnimationTime());
        if (currentMat == Air.INSTANCE) {
          ray.invertNormal();
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
      if(ray.depth + 1 >= scene.rayDepth) {
        break;
      }
      Vector4 cumulativeColor = new Vector4(0, 0, 0, 0);
      Vector3 cumulativeEmittance = new Vector3(0, 0, 0);
      Ray next = new Ray();
      float pMetal = currentMat.metalness;
      int count = firstReflection ? scene.getBranchCount() : 1;
      for (int i = 0; i < count; i++) {
        boolean doMetal = pMetal > Ray.EPSILON && random.nextFloat() < pMetal;
        if (doMetal || (pSpecular > Ray.EPSILON && random.nextFloat() < pSpecular)) {
          hit |= doSpecularReflection(ray, next, cumulativeColor, cumulativeEmittance, doMetal, random, state, scene);
        } else if(random.nextFloat() < pDiffuse) {
          hit |= doDiffuseReflection(ray, next, currentMat, cumulativeColor, cumulativeEmittance, addEmitted, random, state, scene);
        } else if (n1 != n2) {
          hit |= doRefraction(ray, next, currentMat, prevMat, cumulativeColor, cumulativeEmittance, n1, n2, pDiffuse, random, state, scene);
        } else {
          hit |= doTransmission(ray, next, cumulativeColor, cumulativeEmittance, pDiffuse, state, scene);
        }
      }
      ray.color.set(cumulativeColor);
      ray.emittance.set(cumulativeEmittance);

      if (hit && prevMat.isWater()) {
        // Render water fog effect.
        if(scene.waterVisibility == 0) {
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

    // This is a simplistic fog model which gives greater artistic freedom but
    // less realism. The user can select fog color and density; in a more
    // realistic model color would depend on viewing angle and sun color/position.
    if (airDistance > 0 && scene.fog.fogEnabled()) {

      // Pick point between ray origin and intersected object.
      // The chosen point is used to test if the sun is lighting the
      // fog between the camera and the first diffuse ray target.
      // The sun contribution will be proportional to the amount of
      // sunlit fog areas in the ray path, thus giving an approximation
      // of the sun inscatter leading to effects like god rays.
      // The way the sun contribution point is chosen is not
      // entirely correct because the original ray may have
      // travelled through glass or other materials between air gaps.
      // However, the results are probably close enough to not be distracting,
      // so this seems like a reasonable approximation.
      Ray atmos = new Ray();
      double offset = scene.fog.sampleGroundScatterOffset(ray, ox, random);
      atmos.o.scaleAdd(offset, od, ox);
      scene.sun.getRandomSunDirection(atmos, random);
      atmos.setCurrentMaterial(Air.INSTANCE);

      // Check sun visibility at random point to determine inscatter brightness.
      getDirectLightAttenuation(scene, atmos, state);
      scene.fog.addGroundFog(ray, ox, airDistance, state.attenuation, offset);
    }

    return hit;
  }

  private static boolean doSpecularReflection(Ray ray, Ray next, Vector4 cumulativeColor, Vector3 cumulativeEmittance, boolean doMetal, Random random, WorkerState state, Scene scene) {
    boolean hit = false;
    next.specularReflection(ray, random);
    if (pathTrace(scene, next, state, 1, false)) {
      cumulativeEmittance.x += ray.color.x * next.emittance.x;
      cumulativeEmittance.y += ray.color.y * next.emittance.y;
      cumulativeEmittance.z += ray.color.z * next.emittance.z;

      if (doMetal) {
        // use the albedo color as specular color
        cumulativeColor.x += ray.color.x * next.color.x;
        cumulativeColor.y += ray.color.y * next.color.y;
        cumulativeColor.z += ray.color.z * next.color.z;
      } else {
        cumulativeColor.x += next.color.x;
        cumulativeColor.y += next.color.y;
        cumulativeColor.z += next.color.z;
      }
      hit = true;
    }
    return hit;
  }

  private static boolean doDiffuseReflection(Ray ray, Ray next, Material currentMat, Vector4 cumulativeColor, Vector3 cumulativeEmittance, int addEmitted, Random random, WorkerState state, Scene scene) {
    boolean hit = false;
    float emittance = 0;
    Vector4 indirectEmitterColor = new Vector4(0, 0, 0, 0);

    if (scene.emittersEnabled && (!scene.isPreventNormalEmitterWithSampling() || scene.getEmitterSamplingStrategy() == EmitterSamplingStrategy.NONE || ray.depth == 0) && currentMat.emittance > Ray.EPSILON) {

      emittance = addEmitted;
      cumulativeEmittance.x = ray.color.x * ray.color.x *
        currentMat.emittance * scene.emitterIntensity;
      cumulativeEmittance.y = ray.color.y * ray.color.y *
        currentMat.emittance * scene.emitterIntensity;
      cumulativeEmittance.z = ray.color.z * ray.color.z *
        currentMat.emittance * scene.emitterIntensity;

      hit = true;
    } else if (scene.emittersEnabled && scene.emitterSamplingStrategy != EmitterSamplingStrategy.NONE && scene.getEmitterGrid() != null) {
      // Sample emitter
      switch (scene.emitterSamplingStrategy) {
        case ONE:
        case ONE_BLOCK: {
          Grid.EmitterPosition pos = scene.getEmitterGrid().sampleEmitterPosition((int) ray.o.x, (int) ray.o.y, (int) ray.o.z, random);
          if (pos != null) {
            indirectEmitterColor.scaleAdd(Math.PI, sampleEmitter(scene, ray, pos, random));
          }
          break;
        }
        case ALL: {
          List<Grid.EmitterPosition> positions = scene.getEmitterGrid().getEmitterPositions((int) ray.o.x, (int) ray.o.y, (int) ray.o.z);
          double sampleScaler = Math.PI / positions.size();
          for (Grid.EmitterPosition pos : positions) {
            indirectEmitterColor.scaleAdd(sampleScaler, sampleEmitter(scene, ray, pos, random));
          }
          break;
        }
      }
    }

    if (scene.getSunSamplingStrategy().doSunSampling()) {
      next.set(ray);
      scene.sun.getRandomSunDirection(next, random);

      double directLightR = 0;
      double directLightG = 0;
      double directLightB = 0;

      boolean frontLight = next.d.dot(ray.getNormal()) > 0;

      if (frontLight || (currentMat.subSurfaceScattering
        && random.nextFloat() < Scene.fSubSurface)) {

        if (!frontLight) {
          next.o.scaleAdd(-Ray.OFFSET, ray.getNormal());
        }

        next.setCurrentMaterial(next.getPrevMaterial(), next.getPrevData());

        getDirectLightAttenuation(scene, next, state);

        Vector4 attenuation = state.attenuation;
        if (attenuation.w > 0) {
          double mult = QuickMath.abs(next.d.dot(ray.getNormal())) * (scene.getSunSamplingStrategy().isSunLuminosity() ? scene.sun().getLuminosityPdf() : 1);
          directLightR = attenuation.x * attenuation.w * mult;
          directLightG = attenuation.y * attenuation.w * mult;
          directLightB = attenuation.z * attenuation.w * mult;
          hit = true;
        }
      }

      next.diffuseReflection(ray, random);
      hit = pathTrace(scene, next, state, 0, false) || hit;
      if (hit) {
        cumulativeColor.x += ray.color.x * (emittance + directLightR * scene.sun.emittance.x + (
          next.color.x + next.emittance.x) + (indirectEmitterColor.x));
        cumulativeColor.y += ray.color.y * (emittance + directLightG * scene.sun.emittance.y + (
          next.color.y + next.emittance.y) + (indirectEmitterColor.y));
        cumulativeColor.z += ray.color.z * (emittance + directLightB * scene.sun.emittance.z + (
          next.color.z + next.emittance.z) + (indirectEmitterColor.z));
      } else if (indirectEmitterColor.x > Ray.EPSILON || indirectEmitterColor.y > Ray.EPSILON || indirectEmitterColor.z > Ray.EPSILON) {
        hit = true;
        cumulativeColor.x += ray.color.x * indirectEmitterColor.x;
        cumulativeColor.y += ray.color.y * indirectEmitterColor.y;
        cumulativeColor.z += ray.color.z * indirectEmitterColor.z;
      }

    } else {
      next.diffuseReflection(ray, random);

      hit = pathTrace(scene, next, state, 0, false) || hit;
      if (hit) {
        cumulativeColor.x += ray.color.x * (emittance + (next.color.x + next.emittance.x) + (indirectEmitterColor.x));
        cumulativeColor.y += ray.color.y * (emittance + (next.color.y + next.emittance.y) + (indirectEmitterColor.y));
        cumulativeColor.z += ray.color.z * (emittance + (next.color.z + next.emittance.z) + (indirectEmitterColor.z));
      } else if (indirectEmitterColor.x > Ray.EPSILON || indirectEmitterColor.y > Ray.EPSILON || indirectEmitterColor.z > Ray.EPSILON) {
        hit = true;
        cumulativeColor.x += ray.color.x * indirectEmitterColor.x;
        cumulativeColor.y += ray.color.y * indirectEmitterColor.y;
        cumulativeColor.z += ray.color.z * indirectEmitterColor.z;
      }
    }
    return hit;
  }

  private static boolean doRefraction(Ray ray, Ray next, Material currentMat, Material prevMat, Vector4 cumulativeColor, Vector3 cumulativeEmittance, float n1, float n2, double pDiffuse, Random random, WorkerState state, Scene scene) {
    boolean hit = false;
    // TODO: make this decision dependent on the material properties:
    boolean doRefraction = currentMat.refractive || prevMat.refractive;

    float n1n2 = n1 / n2;
    double cosTheta = -ray.getNormal().dot(ray.d);
    double radicand = 1 - n1n2 * n1n2 * (1 - cosTheta * cosTheta);
    if (doRefraction && radicand < Ray.EPSILON) {
      // Total internal reflection.
      next.specularReflection(ray, random);
      if (pathTrace(scene, next, state, 1, false)) {
        cumulativeEmittance.x += ray.color.x * next.emittance.x;
        cumulativeEmittance.y += ray.color.y * next.emittance.y;
        cumulativeEmittance.z += ray.color.z * next.emittance.z;

        cumulativeColor.x += next.color.x;
        cumulativeColor.y += next.color.y;
        cumulativeColor.z += next.color.z;
        hit = true;
      }
    } else {
      next.set(ray);

      // Calculate angle-dependent reflectance using
      // Fresnel equation approximation:
      // R(cosineAngle) = R0 + (1 - R0) * (1 - cos(cosineAngle))^5
      float a = (n1n2 - 1);
      float b = (n1n2 + 1);
      double R0 = a * a / (b * b);
      double c = 1 - cosTheta;
      double Rtheta = R0 + (1 - R0) * c * c * c * c * c;

      if (random.nextFloat() < Rtheta) {
        next.specularReflection(ray, random);
        if (pathTrace(scene, next, state, 1, false)) {
          cumulativeEmittance.x += ray.color.x * next.emittance.x;
          cumulativeEmittance.y += ray.color.y * next.emittance.y;
          cumulativeEmittance.z += ray.color.z * next.emittance.z;

          cumulativeColor.x += next.color.x;
          cumulativeColor.y += next.color.y;
          cumulativeColor.z += next.color.z;
          hit = true;
        }
      } else {
        if (doRefraction) {

          double t2 = FastMath.sqrt(radicand);
          Vector3 n = ray.getNormal();
          if (cosTheta > 0) {
            next.d.x = n1n2 * ray.d.x + (n1n2 * cosTheta - t2) * n.x;
            next.d.y = n1n2 * ray.d.y + (n1n2 * cosTheta - t2) * n.y;
            next.d.z = n1n2 * ray.d.z + (n1n2 * cosTheta - t2) * n.z;
          } else {
            next.d.x = n1n2 * ray.d.x - (-n1n2 * cosTheta - t2) * n.x;
            next.d.y = n1n2 * ray.d.y - (-n1n2 * cosTheta - t2) * n.y;
            next.d.z = n1n2 * ray.d.z - (-n1n2 * cosTheta - t2) * n.z;
          }

          next.d.normalize();

          // See Ray.specularReflection for information on why this is needed
          // This is the same thing but for refraction instead of reflection
          // so this time we want the signs of the dot product to be the same
          if (QuickMath.signum(next.getGeometryNormal().dot(next.d)) != QuickMath.signum(next.getGeometryNormal().dot(ray.d))) {
            double factor = QuickMath.signum(next.getGeometryNormal().dot(ray.d)) * -Ray.EPSILON - next.d.dot(next.getGeometryNormal());
            next.d.scaleAdd(factor, next.getGeometryNormal());
            next.d.normalize();
          }

          next.o.scaleAdd(Ray.OFFSET, next.d);
        }

        if (pathTrace(scene, next, state, 1, false)) {
          double rtemp, gtemp, btemp;
          rtemp = ray.color.x * pDiffuse + (1 - pDiffuse);
          gtemp = ray.color.y * pDiffuse + (1 - pDiffuse);
          btemp = ray.color.z * pDiffuse + (1 - pDiffuse);

          cumulativeEmittance.x += rtemp * next.emittance.x;
          cumulativeEmittance.y += gtemp * next.emittance.y;
          cumulativeEmittance.z += btemp * next.emittance.z;

          cumulativeColor.x += rtemp * next.color.x;
          cumulativeColor.y += gtemp * next.color.y;
          cumulativeColor.z += btemp * next.color.z;
          hit = true;
        }
      }
    }
    return hit;
  }

  private static boolean doTransmission(Ray ray, Ray next, Vector4 cumulativeColor, Vector3 cumulativeEmittance, double pDiffuse, WorkerState state, Scene scene) {
    boolean hit = false;
    next.set(ray);
    next.o.scaleAdd(Ray.OFFSET, next.d);

    if (pathTrace(scene, next, state, 1, false)) {
      double rtemp, gtemp, btemp;
      rtemp = ray.color.x * pDiffuse + (1 - pDiffuse);
      gtemp = ray.color.y * pDiffuse + (1 - pDiffuse);
      btemp = ray.color.z * pDiffuse + (1 - pDiffuse);

      cumulativeEmittance.x += rtemp * next.emittance.x;
      cumulativeEmittance.y += gtemp * next.emittance.y;
      cumulativeEmittance.z += btemp * next.emittance.z;

      cumulativeColor.x += rtemp * next.color.x;
      cumulativeColor.y += gtemp * next.color.y;
      cumulativeColor.z += btemp * next.color.z;
      hit = true;
    }
    return hit;
  }
  private static void addSkyFog(Scene scene, Ray ray, WorkerState state, Vector3 ox, Vector3 od) {
    if (scene.fog.mode == FogMode.UNIFORM) {
      scene.fog.addSkyFog(ray, null);
    } else if (scene.fog.mode == FogMode.LAYERED) {
      Ray atmos = new Ray();
      double offset = scene.fog.sampleSkyScatterOffset(scene, ray, state.random);
      atmos.o.scaleAdd(offset, od, ox);
      scene.sun.getRandomSunDirection(atmos, state.random);
      atmos.setCurrentMaterial(Air.INSTANCE);
      getDirectLightAttenuation(scene, atmos, state);
      scene.fog.addSkyFog(ray, state.attenuation);
    }
  }

  private static void sampleEmitterFace(Scene scene, Ray ray, Grid.EmitterPosition pos, int face, Vector4 result, double scaler, Random random) {
    Ray emitterRay = new Ray(ray);

    pos.sampleFace(face, emitterRay.d, random);
    emitterRay.d.sub(emitterRay.o);

    if (emitterRay.d.dot(ray.getNormal()) > 0) {
      double distance = emitterRay.d.length();
      emitterRay.d.scale(1 / distance);

      emitterRay.o.scaleAdd(Ray.OFFSET, emitterRay.d);
      emitterRay.distance += Ray.OFFSET;
      PreviewRayTracer.nextIntersection(scene, emitterRay);
      if (Math.abs(emitterRay.distance - distance) < Ray.OFFSET) {
        double e = Math.abs(emitterRay.d.dot(emitterRay.getNormal()));
        e /= Math.max(distance * distance, 1);
        e *= pos.block.surfaceArea(face);
        e *= emitterRay.getCurrentMaterial().emittance;
        e *= scene.emitterIntensity;
        e *= scaler;

        result.scaleAdd(e, emitterRay.color);
      }
    }
  }

  /**
   * Cast a shadow ray from the intersection point (given by ray) to the emitter
   * at position pos. Returns the contribution of this emitter (0 if the emitter is occluded)
   * @param scene The scene being rendered
   * @param ray The ray that generated the intersection
   * @param pos The position of the emitter to sample
   * @param random RNG
   * @return The contribution of the emitter
   */
  private static Vector4 sampleEmitter(Scene scene, Ray ray, Grid.EmitterPosition pos, Random random) {
    Vector4 result = new Vector4();
    result.set(0, 0, 0, 1);

    switch (scene.getEmitterSamplingStrategy()) {
      default:
      case ONE:
        sampleEmitterFace(scene, ray, pos, random.nextInt(pos.block.faceCount()), result, 1, random);
        break;
      case ONE_BLOCK:
      case ALL:
        double scaler = 1.0 / pos.block.faceCount();
        for (int i = 0; i < pos.block.faceCount(); i++) {
          sampleEmitterFace(scene, ray, pos, i, result, scaler, random);
        }
        break;
    }

    return result;
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
      if (!PreviewRayTracer.nextIntersection(scene, ray)) {
        break;
      }
      double mult = 1 - ray.color.w;
      attenuation.x *= ray.color.x * ray.color.w + mult;
      attenuation.y *= ray.color.y * ray.color.w + mult;
      attenuation.z *= ray.color.z * ray.color.w + mult;
      attenuation.w *= mult;
      if (ray.getPrevMaterial().isWater()) {
        if(scene.waterVisibility == 0) {
          attenuation.w = 0;
        } else {
          double a = ray.distance / scene.waterVisibility;
          attenuation.w *= Math.exp(-a);
        }
      }
      if (scene.getSunSamplingStrategy().isStrictDirectLight() && ray.getPrevMaterial().ior != ray.getCurrentMaterial().ior) {
        attenuation.w = 0;
      }
    }
  }

}
