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

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Material;
import se.llbit.math.*;

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
    Ray2 ray = state.ray;
    ray.setCurrentMedium(scene.getWorldMaterial(ray));
    pathTrace(scene, ray, state);
  }

  /**
   * Path trace the ray in this scene.
   *
   * @param firstReflection {@code true} if the ray has not yet hit the first
   * diffuse or specular reflection
   */
  public static void pathTrace(Scene scene, Ray2 ray, WorkerState state) {
    Random random = state.random;
//    Vector3 ox = new Vector3(ray.o);
//    Vector3 od = new Vector3(ray.d);

    Vector4 cumulativeColor = state.color;
    Vector3 throughput = new Vector3(1, 1, 1);

    int rayDepth = 0;
    while (rayDepth < scene.rayDepth) {

      IntersectionRecord intersectionRecord = new IntersectionRecord();

      if (scene.intersect(ray, intersectionRecord)) {

        double emittance = intersectionRecord.material.emittance;
        ray.o.scaleAdd(intersectionRecord.distance, ray.d);
        if (!intersectionRecord.material.scatter(ray, intersectionRecord, random)) {
          //rayDepth--;
          emittance = 0;
        }

        throughput.x *= intersectionRecord.color.x;
        throughput.y *= intersectionRecord.color.y;
        throughput.z *= intersectionRecord.color.z;

        cumulativeColor.x += intersectionRecord.color.x * emittance * scene.emitterIntensity * throughput.x;
        cumulativeColor.y += intersectionRecord.color.y * emittance * scene.emitterIntensity * throughput.y;
        cumulativeColor.z += intersectionRecord.color.z * emittance * scene.emitterIntensity * throughput.z;

      } else {
        scene.sky.getSkyColor(ray, intersectionRecord);

        throughput.x *= intersectionRecord.color.x;
        throughput.y *= intersectionRecord.color.y;
        throughput.z *= intersectionRecord.color.z;

        cumulativeColor.x += throughput.x;
        cumulativeColor.y += throughput.y;
        cumulativeColor.z += throughput.z;

        break;

      }
      rayDepth++;
    }
  }
}
