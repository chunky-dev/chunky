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

      double pDiffuse = scene.fancierTranslucency ? 1 - Math.sqrt(1 - ray.color.w) : ray.color.w;

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
      Ray next = new Ray();
      float pMetal = currentMat.metalness;
      // Reusing first rays - a simplified form of "branched path tracing" (what Blender used to call it before they implemented something fancier)
      // The initial rays cast into the scene are very similar between each sample, since they are almost entirely a function of the pixel coordinates
      // Because of that, casting those initial rays on every sample is redundant and can be skipped
      // If the ray depth is high, this doesn't help much (just a few percent), but in some outdoor/low depth scenes, this can improve performance by >40%
      // The main caveat is that antialiasing is achieved by varying the starting rays at the subpixel level (see PathTracingRenderer.java)
      // Therefore, it's still necessary to have a decent amount (20 is ok, 50 is better) of distinct starting rays for each pixel
      // scene.branchCount is the number of times we use the same first ray before casting a new one
      int count = firstReflection ? scene.getCurrentBranchCount() : 1;
      for (int i = 0; i < count; i++) {
        boolean doMetal = pMetal > Ray.EPSILON && random.nextFloat() < pMetal;
        if (doMetal || (pSpecular > Ray.EPSILON && random.nextFloat() < pSpecular)) {
          hit |= doSpecularReflection(ray, next, cumulativeColor, doMetal, random, state, scene);
        } else if(random.nextFloat() < pDiffuse) {
          hit |= doDiffuseReflection(ray, next, currentMat, cumulativeColor, addEmitted, random, state, scene);
        } else if (n1 != n2) {
          hit |= doRefraction(ray, next, currentMat, prevMat, cumulativeColor, n1, n2, pDiffuse, random, state, scene);
        } else {
          hit |= doTransmission(ray, next, cumulativeColor, pDiffuse, state, scene);
        }
      }
      ray.color.set(cumulativeColor);
      ray.color.scale(1d/count);

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

  private static boolean doSpecularReflection(Ray ray, Ray next, Vector4 cumulativeColor, boolean doMetal, Random random, WorkerState state, Scene scene) {
    boolean hit = false;
    next.specularReflection(ray, random);
    if (pathTrace(scene, next, state, 1, false)) {
      ray.emittance.x = ray.color.x * next.emittance.x;
      ray.emittance.y = ray.color.y * next.emittance.y;
      ray.emittance.z = ray.color.z * next.emittance.z;

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

  private static boolean doDiffuseReflection(Ray ray, Ray next, Material currentMat, Vector4 cumulativeColor, int addEmitted, Random random, WorkerState state, Scene scene) {
    boolean hit = false;
    float emittance = 0;
    Vector4 indirectEmitterColor = new Vector4(0, 0, 0, 0);

    if (scene.emittersEnabled && (!scene.isPreventNormalEmitterWithSampling() || scene.getEmitterSamplingStrategy() == EmitterSamplingStrategy.NONE || ray.depth == 0) && currentMat.emittance > Ray.EPSILON) {

      emittance = addEmitted;
      ray.emittance.x = ray.color.x * ray.color.x *
        currentMat.emittance * scene.emitterIntensity;
      ray.emittance.y = ray.color.y * ray.color.y *
        currentMat.emittance * scene.emitterIntensity;
      ray.emittance.z = ray.color.z * ray.color.z *
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

  private static boolean doRefraction(Ray ray, Ray next, Material currentMat, Material prevMat, Vector4 cumulativeColor, float n1, float n2, double pDiffuse, Random random, WorkerState state, Scene scene) {
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
        ray.emittance.x = ray.color.x * next.emittance.x;
        ray.emittance.y = ray.color.y * next.emittance.y;
        ray.emittance.z = ray.color.z * next.emittance.z;

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
          ray.emittance.x = ray.color.x * next.emittance.x;
          ray.emittance.y = ray.color.y * next.emittance.y;
          ray.emittance.z = ray.color.z * next.emittance.z;

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
          // Calculate the color and emittance of the refracted ray
          translucentRayColor(scene, ray, next, cumulativeColor, pDiffuse);
          hit = true;
        }
      }
    }
    return hit;
  }

  private static boolean doTransmission(Ray ray, Ray next, Vector4 cumulativeColor, double pDiffuse, WorkerState state, Scene scene) {
    boolean hit = false;
    next.set(ray);
    next.o.scaleAdd(Ray.OFFSET, next.d);

    if (pathTrace(scene, next, state, 1, false)) {
      // Calculate the color and emittance of the refracted ray
      translucentRayColor(scene, ray, next, cumulativeColor, pDiffuse);
      hit = true;
    }
    return hit;
  }

  private static void translucentRayColor(Scene scene, Ray ray, Ray next, Vector4 cumulativeColor, double opacity) {
    Vector3 rgbTrans;
    if(scene.fancierTranslucency) {
      // Color-based transmission value
      double colorTrans = (ray.color.x + ray.color.y + ray.color.z) / 3;
      // Total amount of light we want to transmit (overall transparency of texture)
      double shouldTrans = 1 - opacity;
      // Amount of each color to transmit - default to overall transparency if RGB values add to 0 (e.g. regular glass)
      rgbTrans = new Vector3(shouldTrans, shouldTrans, shouldTrans);
      if (colorTrans > 0) {
        // Amount to transmit of each color is scaled so the total transmitted amount matches the texture's transparency
        rgbTrans.set(ray.color.toVec3());
        rgbTrans.scale(shouldTrans / colorTrans);
      }
      double transmissivityCap = scene.transmissivityCap;
      // Determine the color with the highest transmissivity
      double maxTrans = Math.max(rgbTrans.x, Math.max(rgbTrans.y, rgbTrans.z));
      if (maxTrans > transmissivityCap) {
        if (maxTrans == rgbTrans.x) {
          // Give excess transmission from red to green and blue
          double gTransNew = reassignTransmissivity(rgbTrans.x, rgbTrans.y, rgbTrans.z, shouldTrans, transmissivityCap);
          rgbTrans.z = reassignTransmissivity(rgbTrans.x, rgbTrans.z, rgbTrans.y, shouldTrans, transmissivityCap);
          rgbTrans.y = gTransNew;
          rgbTrans.x = transmissivityCap;
        } else if (maxTrans == rgbTrans.y) {
          // Give excess transmission from green to red and blue
          double rTransNew = reassignTransmissivity(rgbTrans.y, rgbTrans.x, rgbTrans.z, shouldTrans, transmissivityCap);
          rgbTrans.z = reassignTransmissivity(rgbTrans.y, rgbTrans.z, rgbTrans.x, shouldTrans, transmissivityCap);
          rgbTrans.x = rTransNew;
          rgbTrans.y = transmissivityCap;
        } else if (maxTrans == rgbTrans.z) {
          // Give excess transmission from blue to green and red
          double gTransNew = reassignTransmissivity(rgbTrans.z, rgbTrans.y, rgbTrans.x, shouldTrans, transmissivityCap);
          rgbTrans.x = reassignTransmissivity(rgbTrans.z, rgbTrans.x, rgbTrans.y, shouldTrans, transmissivityCap);
          rgbTrans.y = gTransNew;
          rgbTrans.z = transmissivityCap;
        }
      }
      // Don't need to check for energy gain if transmissivity cap is 1
      if (transmissivityCap > 1) {
        double currentEnergy = rgbTrans.x * next.color.x + rgbTrans.y * next.color.y + rgbTrans.z * next.color.z;
        double nextEnergy = next.color.x + next.color.y + next.color.z;
        double energyRatio = nextEnergy / currentEnergy;
        // Normalize if there is net energy gain across all channels (more likely for higher transmissivityCap combined with high-saturation light source)
        if (energyRatio < 1) {
          rgbTrans.scale(energyRatio);
        }
      }
    } else {
      // Old method (see https://github.com/chunky-dev/chunky/pull/1513)
      rgbTrans = new Vector3(1 - opacity, 1 - opacity, 1 - opacity);
      rgbTrans.scaleAdd(opacity, ray.color.toVec3());
    }
    // Scale color based on next ray
    Vector4 outputColor = new Vector4(0, 0, 0, 0);
    outputColor.multiplyEntrywise(new Vector4(rgbTrans, 1), next.color);
    cumulativeColor.add(outputColor);
    // Use emittance from next ray
    ray.emittance.multiplyEntrywise(rgbTrans, next.emittance);
  }

  private static double reassignTransmissivity(double from, double to, double other, double trans, double cap) {
    // Formula here derived algebraically from this system:
    // (cap - to_new)/(cap - other_new) = (from - to)/(from - other), (cap + to_new + other_new)/3 = trans
    return (cap*(other - 2*to + from) + (3*trans)*(to - from))/(other + to - 2*from);
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
