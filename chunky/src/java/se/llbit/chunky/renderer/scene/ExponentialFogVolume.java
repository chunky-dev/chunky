package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.json.JsonObject;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class ExponentialFogVolume extends FogVolume {
  public static final double DEFAULT_SCALE_HEIGHT = 20;
  public static final double DEFAULT_Y_OFFSET = 0;
  private double scaleHeight;
  private double yOffset;
  public ExponentialFogVolume(Vector3 color, double density, double scaleHeight, double yOffset) {
    this.type = FogVolumeType.EXPONENTIAL;
    this.color = new Vector3(color);
    this.density = density;
    this.scaleHeight = scaleHeight;
    this.yOffset = yOffset;
  }

  public ExponentialFogVolume(Vector3 color, double density) {
    this(color, density, DEFAULT_SCALE_HEIGHT, DEFAULT_Y_OFFSET);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene, Random random) {
    // Amount of fog the ray should pass through before being scattered
    // Sampled from an exponential distribution
    double fogPenetrated = -FastMath.log(1 - random.nextDouble());
    double expHeightDiff = fogPenetrated * ray.d.y / (scaleHeight * density);
    double expYfHs = FastMath.exp(-(ray.o.y + scene.origin.y - yOffset) / scaleHeight) - expHeightDiff;
    if(expYfHs <= 0) {
      // The ray does not encounter enough fog to be scattered - no intersection.
      return false;
    }
    double yf = -FastMath.log(expYfHs) * scaleHeight + yOffset;
    double dist = (yf - (ray.o.y + scene.origin.y)) / ray.d.y;
    if(dist >= ray.t) {
      // The ray would have encountered enough fog to be scattered, but something is in the way.
      return false;
    }
    ray.t = dist;
    // pick a random normal vector based on a spherical particle.
    // This is done only to prevent fog from looking ugly in the render preview and
    // should have no effect on the path-traced render.
    setRandomNormal(ray, random);
    setRayMaterialAndColor(ray);
    ray.specular = false;
    return true;
  }

  public void setScaleHeight(double s) {
    scaleHeight = s;
  }

  public double getScaleHeight() {
    return scaleHeight;
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
    properties.add("scaleHeight", scaleHeight);
    properties.add("yOffset", yOffset);
    return properties;
  }

  @Override
  public void importVolumeSpecificProperties(JsonObject json) {
    scaleHeight = json.get("scaleHeight").doubleValue(scaleHeight);
    yOffset = json.get("yOffset").doubleValue(yOffset);
  }
}
