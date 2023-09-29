package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.world.material.ParticleFogMaterial;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class ExponentialFogVolume extends FogVolume {
  public double scaleHeight;
  public double yOffset;
  public ExponentialFogVolume(Vector3 color, double density, double scaleHeight, double yOffset) {
    this.color = color;
    this.density = density;
    this.scaleHeight = scaleHeight;
    this.yOffset = yOffset;
  }
  public boolean intersect(Ray ray, Scene scene, Random random) {
    // Amount of fog the ray should pass through before being scattered
    // Sampled from an exponential distribution
    double fogPenetrated = -Math.log(1 - random.nextDouble());
    double expHeightDiff = fogPenetrated * ray.d.y / (scaleHeight * density);
    double expYfHs = Math.exp(-(ray.o.y + scene.origin.y - yOffset) / scaleHeight) - expHeightDiff;
    if(expYfHs <= 0) {
      // The ray does not encounter enough fog to be scattered - no intersection.
      return false;
    }
    double yf = -Math.log(expYfHs) * scaleHeight + yOffset;
    double dist = (yf - (ray.o.y + scene.origin.y)) / ray.d.y;
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
