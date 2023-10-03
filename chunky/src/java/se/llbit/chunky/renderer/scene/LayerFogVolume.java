package se.llbit.chunky.renderer.scene;

import se.llbit.json.JsonObject;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class LayerFogVolume extends FogVolume {
  public static final double DEFAULT_Y_OFFSET = 62;
  public static final double DEFAULT_BREADTH = 5;
  private double layerBreadth;
  private double yOffset;
  public LayerFogVolume(Vector3 color, double density, double layerBreadth, double yOffset) {
    this.type = FogVolumeType.LAYER;
    this.color = new Vector3(color);
    this.density = density;
    this.layerBreadth = layerBreadth;
    this.yOffset = yOffset;
  }

  public LayerFogVolume(Vector3 color, double density) {
    this(color, density, DEFAULT_BREADTH, DEFAULT_Y_OFFSET);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene, Random random) {
    // Amount of fog the ray should pass through before being scattered
    // Sampled from an exponential distribution
    double fogPenetrated = -Math.log(1 - random.nextDouble());
    double atanHeightDiff = fogPenetrated * ray.d.y / (layerBreadth * density);
    double atanYfHs = Math.atan((ray.o.y + scene.origin.y - yOffset) / layerBreadth) + atanHeightDiff;
    if(Math.PI/2 - Math.abs(atanYfHs) <= 0) {
      // The ray does not encounter enough fog to be scattered - no intersection.
      return false;
    }
    double yf = Math.tan(atanYfHs) * layerBreadth + yOffset;
    double dist = (yf - (ray.o.y + scene.origin.y)) / ray.d.y;
    if(dist >= ray.t) {
      // The ray would have encountered enough fog to be scattered, but something is in the way.
      return false;
    }
    ray.t = dist;
    // pick a random normal vector based on a spherical particle
    setRandomNormal(ray, random);
    setRayMaterialAndColor(ray);
    return true;
  }

  public void setLayerBreadth(double l) {
    layerBreadth = l;
  }

  public double getLayerBreadth() {
    return layerBreadth;
  }

  public void setYOffset(double y) {
    yOffset = y;
  }

  public double getYOffset() {
    return yOffset;
  }

  @Override
  public JsonObject volumeSpecificPropertiesToJson() {
    JsonObject properties = new JsonObject();
    properties.add("layerBreadth", layerBreadth);
    properties.add("yOffset", yOffset);
    return properties;
  }

  @Override
  public void importVolumeSpecificProperties(JsonObject json) {
    layerBreadth = json.get("layerBreadth").doubleValue(layerBreadth);
    yOffset = json.get("yOffset").doubleValue(yOffset);
  }
}
