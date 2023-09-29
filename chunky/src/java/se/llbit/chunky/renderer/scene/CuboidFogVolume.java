package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.world.material.ParticleFogMaterial;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class CuboidFogVolume extends FogVolume {
  public AABB bounds;
  public CuboidFogVolume(Vector3 color, double density, AABB bounds) {
    this.color = color;
    this.density = density;
    this.bounds = bounds;
  }

  @Override
  public void setRandomNormal(Ray ray, Random random) {
    super.setRandomNormal(ray, random);
  }

  public boolean intersect(Ray ray, Scene scene, Random random) {
    // Amount of fog the ray should pass through before being scattered
    // Sampled from an exponential distribution
    double fogPenetrated = -Math.log(1 - random.nextDouble());
    double fogDistance = fogPenetrated / density;
    AABB boundsTranslated = bounds.getTranslated(-scene.origin.x, -scene.origin.y, -scene.origin.z);
    double dist;
    if(boundsTranslated.inside(ray.o)) {
      boundsTranslated.intersectFromInside(ray);
      if(fogDistance > ray.tNext) {
        // The ray makes it out of the box without hitting fog
        return false;
      } else {
        dist = fogDistance;
      }
    } else {
      // Outside the box
      if(!boundsTranslated.quickIntersect(ray)) {
        // The ray misses the box
        return false;
      }
      Ray throughBox = new Ray(ray);
      throughBox.o.scaleAdd(ray.tNext + Ray.OFFSET, ray.d);
      boundsTranslated.intersectFromInside(throughBox);
      if(fogDistance > throughBox.tNext) {
        // The ray makes it through the box without hitting fog
        return false;
      } else {
        dist = ray.tNext + fogDistance;
      }
    }
    if(dist >= ray.t) {
      // The ray would have encountered enough fog to be scattered, but something is in the way.
      return false;
    }
    ray.t = dist;
    // pick a random normal vector based on a spherical particle
    setRandomNormal(ray, random);
    ray.setCurrentMaterial(ParticleFogMaterial.INSTANCE);
    ray.color.set(color.x, color.y, color.z, 1);
    return true;
  }
}
