package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.world.material.ParticleFogMaterial;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class LayerFogVolume extends FogVolume {
  public double layerHeight;
  public double scaleOffset;
  public LayerFogVolume(Vector3 color, double density, double layerHeight, double scaleOffset) {
    this.color = color;
    this.density = density;
    this.layerHeight = layerHeight;
    this.scaleOffset = scaleOffset;
  }
  public boolean intersect(Ray ray, Scene scene, Random random) {
    // Amount of fog the ray should pass through before being scattered
    // Sampled from an exponential distribution
    double fogPenetrated = -Math.log(1 - random.nextDouble());
    double atanHeightDiff = fogPenetrated * ray.d.y / (layerHeight * density);
    double atanYfHs = Math.atan((ray.o.y + scene.origin.y - scaleOffset) / layerHeight) + atanHeightDiff;
    if(Math.PI/2 - Math.abs(atanYfHs) <= 0) {
      // The ray does not encounter enough fog to be scattered - no intersection.
      return false;
    }
    double yf = Math.tan(atanYfHs) * layerHeight + scaleOffset;
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
