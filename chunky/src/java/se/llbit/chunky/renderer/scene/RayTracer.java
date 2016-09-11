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

import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Block;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RayTracer {

  /**
   * Do a quick preview ray tracing for the current ray.
   */
  public static void quickTrace(Scene scene, WorkerState state) {
    Ray ray = state.ray;
    if (scene.isInWater(ray)) {
      ray.setCurrentMat(Block.WATER, 0);
    } else {
      ray.setCurrentMat(Block.AIR, 0);
    }
    while (true) {
      if (!nextIntersection(scene, ray)) {
        if (mapIntersection(scene, ray)) {
          break;
        }
        break;
      } else if (ray.getCurrentMaterial() != Block.AIR && ray.color.w > 0) {
        break;
      } else {
        ray.o.scaleAdd(Ray.OFFSET, ray.d);
      }
    }

    if (ray.getCurrentMaterial() == Block.AIR) {
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

    ray.setPrevMat(ray.getCurrentMaterial(), ray.getCurrentData());
    ray.t = Double.POSITIVE_INFINITY;
    boolean hit = false;
    if (scene.sky().cloudsEnabled()) {
      hit = scene.sky().cloudIntersection(scene, ray);
    }
    if (scene.waterHeight > 0) {
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
      ray.setCurrentMat(Block.AIR, 0);
      return false;
    }
  }

  private static boolean waterIntersection(Scene scene, Ray ray) {
    if (ray.d.y < 0) {
      double t = (scene.waterHeight - .125 - ray.o.y - scene.origin.y) / ray.d.y;
      if (t > 0 && t < ray.t) {
        Vector3 vec = new Vector3();
        vec.scaleAdd(t + Ray.OFFSET, ray.d, ray.o);
        if (!scene.isInsideOctree(vec)) {
          ray.t = t;
          Block.WATER.getColor(ray);
          ray.n.set(0, 1, 0);
          ray.setCurrentMat(Block.WATER, 0);
          return true;
        }
      }
    }
    if (ray.d.y > 0) {
      double t = (scene.waterHeight - .125 - ray.o.y - scene.origin.y) / ray.d.y;
      if (t > 0 && t < ray.t) {
        Vector3 vec = new Vector3();
        vec.scaleAdd(t + Ray.OFFSET, ray.d, ray.o);
        if (!scene.isInsideOctree(vec)) {
          ray.t = t;
          Block.WATER.getColor(ray);
          ray.n.set(0, -1, 0);
          ray.setCurrentMat(Block.AIR, 0);
          return true;
        }
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
          ray.setCurrentMat(Block.STONE, 0);
          ray.n.set(0, 1, 0);
          return true;
        }
      }
    }
    return false;
  }
}
