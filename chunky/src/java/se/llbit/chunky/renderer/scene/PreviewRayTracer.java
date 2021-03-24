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

import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.MinecraftBlock;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PreviewRayTracer implements RayTracer {

  /**
   * Do a quick preview ray tracing for the current ray.
   */
  @Override public void trace(Scene scene, WorkerState state) {
    Ray ray = state.ray;
    if (scene.isInWater(ray)) {
      ray.setCurrentMaterial(Water.INSTANCE);
    } else {
      ray.setCurrentMaterial(Air.INSTANCE);
    }
    while (true) {
      if (!nextIntersection(scene, ray)) {
        if (mapIntersection(scene, ray)) {
          break;
        }
        break;
      } else if (ray.getCurrentMaterial() != Air.INSTANCE && ray.color.w > 0) {
        break;
      } else {
        ray.o.scaleAdd(Ray.OFFSET, ray.d);
      }
    }

    if (ray.getCurrentMaterial() == Air.INSTANCE) {
      scene.sky.getSkySpecularColor(ray);
    } else {
      scene.sun.flatShading(ray);
    }
  }

  /**
   * Calculate sky occlusion.
   * @return occlusion value
   */
  public static double skyOcclusion(Scene scene, WorkerState state) {
    Ray ray = state.ray;
    double occlusion = 1.0;
    while (true) {
      if (!nextIntersection(scene, ray)) {
        break;
      } else {
        occlusion *= (1 - ray.color.w);
        if (occlusion == 0) {
          return 1; // occlusion can't become > 0 anymore
        }
        ray.o.scaleAdd(Ray.OFFSET, ray.d);
      }
    }
    return 1 - occlusion;
  }

  /**
   * Find next ray intersection.
   * @return Next intersection
   */
  public static boolean nextIntersection(Scene scene, Ray ray) {
    ray.setPrevMaterial(ray.getCurrentMaterial(), ray.getCurrentData());
    ray.t = Double.POSITIVE_INFINITY;
    boolean hit = false;
    if (scene.sky().cloudsEnabled()) {
      hit = scene.sky().cloudIntersection(scene, ray);
    }
    if (scene.isWaterPlaneEnabled()) {
      hit = waterIntersection(scene, ray) || hit;
    }
    if (scene.intersect(ray)) {
      // Octree tracer handles updating distance.
      return true;
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
      scene.updateOpacity(ray);
      return true;
    } else {
      ray.setCurrentMaterial(Air.INSTANCE);
      return false;
    }
  }

  private static boolean waterIntersection(Scene scene, Ray ray) {
    if (ray.d.y < 0) {
      double t = (scene.getWaterPlaneHeight() - ray.o.y - scene.origin.y) / ray.d.y;
      if (t > 0 && t < ray.t) {
        ray.t = t;
        Water.INSTANCE.getColor(ray);
        ray.n.set(0, 1, 0);
        ray.setCurrentMaterial(Water.OCEAN_WATER);
        return true;
      }
    }
    if (ray.d.y > 0) {
      double t = (scene.getWaterPlaneHeight() - ray.o.y - scene.origin.y) / ray.d.y;
      if (t > 0 && t < ray.t) {
        ray.t = t;
        Water.INSTANCE.getColor(ray);
        ray.n.set(0, -1, 0);
        ray.setCurrentMaterial(Air.INSTANCE);
        return true;
      }
    }
    return false;
  }

  private static boolean mapIntersection(Scene scene, Ray ray) {
    if (ray.d.y < 0) {
      double t = (scene.waterHeight - .125 - ray.o.y - scene.origin.y) / ray.d.y;
      if (t > 0 && t < ray.t) {
        Vector3 vec = new Vector3();
        vec.scaleAdd(t + Ray.OFFSET, ray.d, ray.o);
        if (!scene.isInsideOctree(vec)) {
          ray.t = t;
          ray.o.set(vec);
          double xm = (ray.o.x % 16.0 + 16.0) % 16.0;
          double zm = (ray.o.z % 16.0 + 16.0) % 16.0;
          if (xm > 0.6 && zm > 0.6) {
            ray.color.set(0.8, 0.8, 0.8, 1);
          } else {
            ray.color.set(0.25, 0.25, 0.25, 1);
          }
          ray.setCurrentMaterial(MinecraftBlock.STONE);
          ray.n.set(0, 1, 0);
          return true;
        }
      }
    }
    return false;
  }
}
