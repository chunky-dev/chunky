package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

/**
 * Static utility methods pertaining to RayTracer instances.
 */
public final class RayTracers {

  /**
   * Private no argument constructor to prevent initialization.
   */
  private RayTracers() {
  }


  /**
   * Calculate sky occlusion.
   *
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
   * Find next ray intersection. The ray is displaced to the target position if it hits something.
   *
   * @return {@code true} if an intersection is found.
   */
  public static boolean nextIntersection(Scene scene, Ray ray) {
    ray.setPrevMaterial(ray.getCurrentMaterial(), ray.getCurrentData());
    ray.t = Double.POSITIVE_INFINITY;
    boolean hit = false;
    if (scene.sky().cloudsEnabled()) {
      hit = scene.sky().cloudIntersection(scene, ray);
    }
    if (scene.isWaterPlaneEnabled()) {
      hit = waterPlaneIntersection(scene, ray) || hit;
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

  private static boolean waterPlaneIntersection(Scene scene, Ray ray) {
    double t = (scene.getEffectiveWaterPlaneHeight() - ray.o.y - scene.origin.y) / ray.d.y;
    if (scene.getWaterPlaneChunkClip()) {
      Vector3 pos = new Vector3(ray.o);
      pos.scaleAdd(t, ray.d);
      if (scene.isChunkLoaded((int) Math.floor(pos.x), (int) Math.floor(pos.z))) {
        return false;
      }
    }
    if (ray.d.y < 0) {
      if (t > 0 && t < ray.t) {
        ray.t = t;
        Water.INSTANCE.getColor(ray);
        ray.n.set(0, 1, 0);
        ray.setCurrentMaterial(scene.getPalette().water);
        return true;
      }
    }
    if (ray.d.y > 0) {
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
}
