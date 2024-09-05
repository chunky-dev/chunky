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

import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Material;
import se.llbit.math.*;

import java.util.Random;

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
  public static void pathTrace(Scene scene, WorkerState state) {
    final Random random = state.random;
    final Ray2 ray = state.ray;

    final Vector4 cumulativeColor = state.color;
    final Vector3 throughput = state.throughput;
    final IntersectionRecord intersectionRecord = state.intersectionRecord;
    final Vector3 emittance = state.emittance;
    final Vector4 sunColor = state.sampleColor;

    for (int i = scene.rayDepth; i > 0; i--) {
      intersectionRecord.reset();
      emittance.set(0, 0, 0);
      sunColor.set(0, 0, 0, 0);

      if (scene.intersect(ray, intersectionRecord, random)) {
        if (intersectionRecord.color.w < Constants.EPSILON) {
          intersectionRecord.color.set(1, 1, 1, 0);
        }

        ray.o.scaleAdd(intersectionRecord.distance, ray.d);

        ray.getCurrentMedium().absorption(throughput, intersectionRecord.distance);

        ray.clearReflectionFlags();
        if ((intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0) {
          emittance.set(intersectionRecord.material.volumeEmittance);
          intersectionRecord.material.volumeScatter(ray, random);
        } else {
          if (intersectionRecord.material.scatter(ray, intersectionRecord, emittance, random)) {
            ray.setCurrentMedium(intersectionRecord.material);
          }
        }

        doSunSampling(scene, state, i);

        throughput.x *= intersectionRecord.color.x;
        throughput.y *= intersectionRecord.color.y;
        throughput.z *= intersectionRecord.color.z;

        cumulativeColor.x += (intersectionRecord.color.x * emittance.x * scene.emitterIntensity + sunColor.x) * throughput.x;
        cumulativeColor.y += (intersectionRecord.color.y * emittance.y * scene.emitterIntensity + sunColor.y) * throughput.y;
        cumulativeColor.z += (intersectionRecord.color.z * emittance.z * scene.emitterIntensity + sunColor.z) * throughput.z;

      } else {
        scene.sky.intersect(ray, intersectionRecord);

        throughput.x *= intersectionRecord.color.x;
        throughput.y *= intersectionRecord.color.y;
        throughput.z *= intersectionRecord.color.z;

        cumulativeColor.x += throughput.x;
        cumulativeColor.y += throughput.y;
        cumulativeColor.z += throughput.z;
        break;
      }
    }
  }

  private static void doSunSampling(Scene scene, WorkerState state, int rayDepth) {
    if (!scene.sunSamplingStrategy.doSunSampling()) {
      return;
    }
    if ((state.ray.flags & Ray2.DIFFUSE) == 0 && (state.intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) == 0) {
      return;
    }

    state.sampleRay.set(state.ray);
    state.sampleRecord.reset();
    scene.sun.getRandomSunDirection(state.sampleRay, state.random);
    switch (scene.sunSamplingStrategy) {
      case SAMPLE_ONLY:
      case MIX:
        if (!scene.intersect(state.sampleRay, state.sampleRecord, state.random)) {
          scene.sky.getSkyColor(state.sampleRay, state.sampleRecord);
          state.sampleColor.set(state.sampleRecord.color);
          double scaleFactor;
          if ((state.intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0) {
            scaleFactor = Material.phaseHG(state.ray.d.rScale(-1).dot(state.sampleRay.d), state.intersectionRecord.material.volumeAnisotropy);
          } else {
            scaleFactor = QuickMath.abs(state.sampleRay.d.dot(state.intersectionRecord.shadeN));
          }
          scaleFactor *= scene.sun.radius * scene.sun.radius;
          state.sampleColor.scale(scaleFactor);
        }
        break;
      case SAMPLE_THROUGH_OPACITY:
        state.attenuation.set(1);
        for (int i = 0; i < rayDepth; i++) {
          if (!scene.intersect(state.sampleRay, state.sampleRecord, state.random)) {
            scene.sky.getSkyColor(state.sampleRay, state.sampleRecord);
            state.sampleColor.set(state.sampleRecord.color);
            double scaleFactor;
            if ((state.intersectionRecord.flags & IntersectionRecord.VOLUME_INTERSECT) != 0) {
              scaleFactor = Material.phaseHG(state.ray.d.rScale(-1).dot(state.sampleRay.d), state.intersectionRecord.material.volumeAnisotropy);
            } else {
              scaleFactor = QuickMath.abs(state.sampleRay.d.dot(state.intersectionRecord.shadeN));
            }
            scaleFactor *= scene.sun.radius * scene.sun.radius;
            state.sampleColor.scale(scaleFactor);
            state.sampleColor.x *= state.attenuation.x;
            state.sampleColor.y *= state.attenuation.y;
            state.sampleColor.z *= state.attenuation.z;
            break;
          }
          if (1 - state.sampleRecord.material.specular < Constants.EPSILON) {
            break;
          }
          state.attenuation.scale(1 - state.sampleRecord.material.specular);
          double mult = 1 - state.sampleRecord.color.w * state.sampleRecord.material.alpha;
          if (mult < Constants.EPSILON) {
            break;
          }
          state.attenuation.scale(mult);
          state.attenuation.x *= 1 - state.sampleRecord.material.transmissionMetalness * (1 - state.sampleRecord.color.x);
          state.attenuation.y *= 1 - state.sampleRecord.material.transmissionMetalness * (1 - state.sampleRecord.color.y);
          state.attenuation.z *= 1 - state.sampleRecord.material.transmissionMetalness * (1 - state.sampleRecord.color.z);

          state.attenuation.x *= state.sampleRecord.material.transmissionSpecularColor.x;
          state.attenuation.y *= state.sampleRecord.material.transmissionSpecularColor.y;
          state.attenuation.z *= state.sampleRecord.material.transmissionSpecularColor.z;

          state.sampleRay.getCurrentMedium().absorption(state.attenuation, state.sampleRecord.distance);

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
}
