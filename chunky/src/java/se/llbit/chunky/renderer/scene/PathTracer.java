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

import java.util.List;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.Void;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.renderer.scene.fog.FogMode;
import se.llbit.chunky.world.Material;
import se.llbit.math.*;

import java.util.Random;
import se.llbit.math.Grid.EmitterPosition;

/**
 * Static methods for path tracing.
 *
 * @author Jesper Öqvist <jesper@llbit.se>, Peregrine05, and Chunky Contributors
 */
public class PathTracer implements RayTracer {

  /**
   * Path trace the ray.
   */
  @Override public void trace(Scene scene, WorkerState state) {
    Ray2 ray = state.ray;
    ray.flags = Ray2.SPECULAR;

    ray.setCurrentMedium(scene.getWorldMaterial(ray));
    pathTrace(scene, state);
  }

  /**
   * Path trace the ray in this scene.
   */
  private static void pathTrace(Scene scene, WorkerState state) {
    final Random random = state.random;
    final Ray2 ray = state.ray;

    final Vector4 cumulativeColor = state.color;
    final Vector3 throughput = state.throughput;
    final IntersectionRecord intersectionRecord = state.intersectionRecord;
    final Vector3 emittance = state.emittance;
    final Vector4 sampleColor = state.sampleColor;

    final Vector3 ox = new Vector3(ray.o);
    final Vector3 od = new Vector3(ray.d);

    for (int i = scene.rayDepth; i > 0; i--) {
      intersectionRecord.reset();
      emittance.set(0, 0, 0);
      sampleColor.set(0, 0, 0, 0);

      ox.set(ray.o);
      od.set(ray.d);

      if (scene.intersect(ray, intersectionRecord, random)) {
        if (intersectionRecord.color.w < Constants.EPSILON) {
          intersectionRecord.color.set(1, 1, 1, 0);
        }

        ray.o.scaleAdd(intersectionRecord.distance, ray.d);

        ray.getCurrentMedium().absorption(throughput, intersectionRecord.distance);

        int prevFlags = ray.flags; // saving this for emitter sampling

        ray.clearReflectionFlags();
        if ((intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0) {
          emittance.set(intersectionRecord.material.volumeEmittance);
          intersectionRecord.material.volumeScatter(ray, random);
        } else {
          if (intersectionRecord.material.scatter(ray, intersectionRecord, scene, emittance, random)) {
            ray.setCurrentMedium(intersectionRecord.material);
          }
        }

        // Sun sampling

        if (scene.sunSamplingStrategy.doSunSampling() && ((state.ray.flags & Ray2.DIFFUSE) != 0 || (state.intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0)) {
          state.sampleRay.set(ray);
          scene.sun.getRandomSunDirection(state.sampleRay.d, state.random);

          scene.sky.getSkyColor(state.sampleRay, state.sampleRecord, true);
          state.sampleColor.set(state.sampleRecord.color);

          transmittance(scene, state, i);

          double scaleFactor;
          if ((intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0) {
            scaleFactor = Material.phaseHG(ray.d.rScale(-1).dot(state.sampleRay.d), intersectionRecord.material.volumeAnisotropy);
          } else {
            scaleFactor = QuickMath.abs(state.sampleRay.d.dot(intersectionRecord.shadeN));
          }
          scaleFactor *= scene.sun.radius * scene.sun.radius;
          state.sampleColor.scale(scaleFactor);
          state.sampleColor.x *= state.attenuation.x;
          state.sampleColor.y *= state.attenuation.y;
          state.sampleColor.z *= state.attenuation.z;
        }

        // --------

        // This is a simplistic fog model which gives greater artistic freedom but
        // less realism. The user can select fog color and density; in a more
        // realistic model color would depend on viewing angle and sun color/position.
        if (intersectionRecord.distance > 0 && scene.fog.isFogEnabled() && (ray.getCurrentMedium() == Air.INSTANCE || ray.getCurrentMedium() == Void.INSTANCE)) {

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
          Ray2 atmos = state.sampleRay;
          double offset = scene.fog.sampleGroundScatterOffset(ray, intersectionRecord.distance, ox, od, random);
          atmos.o.scaleAdd(offset, od, ox);
          scene.sun.getRandomSunDirection(atmos.d, random);

          // Check sun visibility at random point to determine inscatter brightness.
          transmittance(scene, state, i);
          Vector4 fogColor = new Vector4(0);
          scene.fog.addGroundFog(ray, fogColor, intersectionRecord.color, emittance, ox, od, intersectionRecord.distance, state.attenuation, offset);

          cumulativeColor.x += fogColor.x * throughput.x;
          cumulativeColor.y += fogColor.y * throughput.y;
          cumulativeColor.z += fogColor.z * throughput.z;
        }

        // --------
        // Emitter sampling

        if (scene.emitterSamplingStrategy != EmitterSamplingStrategy.NONE
            && scene.getEmitterGrid() != null
            && ((state.ray.flags & Ray2.DIFFUSE) != 0
                || (state.intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0)) {
          switch (scene.emitterSamplingStrategy) {
            case ONE:
            case ONE_BLOCK: {
              Grid.EmitterPosition pos = scene.getEmitterGrid().sampleEmitterPosition((int) ray.o.x, (int) ray.o.y, (int) ray.o.z, random);
              if (pos != null) {
                sampleColor.scaleAdd(FastMath.PI, sampleEmitter(scene, ray, intersectionRecord, pos, random));

                if (scene.isPreventNormalEmitterWithSampling() && (prevFlags & Ray2.INDIRECT) != 0) {
                  emittance.set(0);
                }
              }
              break;
            }
            case ALL: {
              List<EmitterPosition> positions = scene.getEmitterGrid()
                  .getEmitterPositions((int) ray.o.x, (int) ray.o.y, (int) ray.o.z);
              double sampleScaler = FastMath.PI / positions.size();
              for (Grid.EmitterPosition pos : positions) {
                sampleColor.scaleAdd(sampleScaler, sampleEmitter(scene, ray, intersectionRecord, pos, random));

                if (scene.isPreventNormalEmitterWithSampling() && (prevFlags & Ray2.INDIRECT) != 0) {
                  emittance.set(0);
                }
              }
              break;
            }
          }
        }

        // Light emitted by object should not be affected by the tinting of reflected light.
        // Thus, emittance is added to cumulativeColor before the object's color is applied to
        // the throughput.

        cumulativeColor.x += emittance.x * scene.emitterIntensity * throughput.x;
        cumulativeColor.y += emittance.y * scene.emitterIntensity * throughput.y;
        cumulativeColor.z += emittance.z * scene.emitterIntensity * throughput.z;

        throughput.x *= intersectionRecord.color.x;
        throughput.y *= intersectionRecord.color.y;
        throughput.z *= intersectionRecord.color.z;

        cumulativeColor.x += sampleColor.x * throughput.x;
        cumulativeColor.y += sampleColor.y * throughput.y;
        cumulativeColor.z += sampleColor.z * throughput.z;

      } else {
        scene.sky.intersect(ray, intersectionRecord);

        addSkyFog(scene, state, ox, od, i);

        cumulativeColor.x += throughput.x * intersectionRecord.color.x;
        cumulativeColor.y += throughput.y * intersectionRecord.color.y;
        cumulativeColor.z += throughput.z * intersectionRecord.color.z;
        break;
      }
    }
  }

  private static void transmittance(Scene scene, WorkerState state, int rayDepth) {
    state.sampleRecord.reset();
    state.attenuation.set(0);
    switch (scene.sunSamplingStrategy) {
      case SAMPLE_ONLY:
      case MIX:
        if (!scene.intersect(state.sampleRay, state.sampleRecord, state.random)) {
          state.attenuation.set(1);
        }
        break;
      case SAMPLE_THROUGH_OPACITY:
        state.attenuation.set(1);
        for (int i = 0; i < rayDepth; i++) {
          if (!scene.intersect(state.sampleRay, state.sampleRecord, state.random)) {
            break;
          }
          double mult = 1 - state.sampleRecord.color.w * state.sampleRecord.material.alpha;
          if (mult < Constants.EPSILON) {
            state.attenuation.set(0);
            break;
          }
          state.attenuation.scale(mult);
          state.attenuation.x *= 1 - state.sampleRecord.material.transmissionMetalness * (1 - state.sampleRecord.color.x);
          state.attenuation.y *= 1 - state.sampleRecord.material.transmissionMetalness * (1 - state.sampleRecord.color.y);
          state.attenuation.z *= 1 - state.sampleRecord.material.transmissionMetalness * (1 - state.sampleRecord.color.z);

          state.attenuation.x *= state.sampleRecord.material.transmissionSpecularColor.x;
          state.attenuation.y *= state.sampleRecord.material.transmissionSpecularColor.y;
          state.attenuation.z *= state.sampleRecord.material.transmissionSpecularColor.z;

          state.sampleRay.getCurrentMedium().absorption(state.attenuation.toVec3(), state.sampleRecord.distance);

          state.sampleRay.o.scaleAdd(state.sampleRecord.distance, state.sampleRay.d);
          if ((state.sampleRecord.flags & IntersectionRecord.NO_MEDIUM_CHANGE) == 0) {
            state.sampleRay.setCurrentMedium(state.sampleRecord.material);
          }
          state.sampleRay.o.scaleAdd(-Constants.OFFSET, state.sampleRecord.n);
          state.sampleRecord.reset();
        }
        break;
    }
  }

  private static void addSkyFog(Scene scene, WorkerState state, Vector3 ox, Vector3 od, int rayDepth) {
    if (scene.fog.getFogMode() == FogMode.UNIFORM) {
      scene.fog.addSkyFog(state.ray, state.intersectionRecord, null);
    } else if (scene.fog.getFogMode() == FogMode.LAYERED) {
      Ray2 atmos = state.sampleRay;
      double offset = scene.fog.sampleSkyScatterOffset(scene, state.ray, state.random);
      atmos.o.scaleAdd(offset, od, ox);
      scene.sun.getRandomSunDirection(atmos.d, state.random);
      transmittance(scene, state, rayDepth);
      scene.fog.addSkyFog(state.ray, state.intersectionRecord, state.attenuation);
    }
  }

  private static void sampleEmitterFace(Scene scene, Ray2 ray, IntersectionRecord intersectionRecord, Grid.EmitterPosition pos, int face, Vector4 result, double scaler, Random random) {
    Ray2 emitterRay = new Ray2(ray);

    pos.sampleFace(face, emitterRay.d, random);
    emitterRay.d.sub(emitterRay.o);

    if (emitterRay.d.dot(intersectionRecord.n) > 0) {
      double distance = emitterRay.d.length();
      emitterRay.d.scale(1 / distance);

      emitterRay.o.scaleAdd(Constants.OFFSET, emitterRay.d);
      IntersectionRecord emitterIntersection = new IntersectionRecord();
      scene.intersect(emitterRay, emitterIntersection, random);
      if (FastMath.abs(emitterIntersection.distance + Constants.OFFSET - distance) < Constants.OFFSET) {
        double e;
        if ((intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0) {
          e = Material.phaseHG(ray.d.rScale(-1).dot(emitterRay.d), intersectionRecord.material.volumeAnisotropy);
        } else {
          e = FastMath.abs(emitterRay.d.dot(emitterIntersection.n));
        }
        e /= FastMath.max(distance * distance, 1);
        e *= pos.block.surfaceArea(face);
        e *= emitterIntersection.material.emittance;
        e *= scene.emitterIntensity;
        e *= scaler;

        Vector3 emittance = new Vector3();
        Material.tintColor(emitterIntersection.color, 1, emitterIntersection.material.emittanceColor, random);
        emitterIntersection.material.doEmitterMapping(emittance, emitterIntersection.color, scene);
        result.x += emittance.x * e;
        result.y += emittance.y * e;
        result.z += emittance.z * e;
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
  private static Vector4 sampleEmitter(Scene scene, Ray2 ray, IntersectionRecord intersectionRecord, Grid.EmitterPosition pos, Random random) {
    Vector4 result = new Vector4();
    result.set(0, 0, 0, 1);

    switch (scene.getEmitterSamplingStrategy()) {
      case ONE_BLOCK:
      case ALL:
        double scaler = 1.0 / pos.block.faceCount();
        for (int i = 0; i < pos.block.faceCount(); i++) {
          sampleEmitterFace(scene, ray, intersectionRecord, pos, i, result, scaler, random);
        }
        break;
      case ONE:
      default:
        sampleEmitterFace(scene, ray, intersectionRecord, pos, random.nextInt(pos.block.faceCount()), result, 1, random);
        break;
    }

    return result;
  }
}
