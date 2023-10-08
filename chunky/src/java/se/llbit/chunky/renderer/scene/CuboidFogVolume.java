package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.json.JsonObject;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class CuboidFogVolume extends FogVolume {
  private static final double DEFAULT_XMIN = -10;
  private static final double DEFAULT_XMAX = 10;
  private static final double DEFAULT_YMIN = 100;
  private static final double DEFAULT_YMAX = 120;
  private static final double DEFAULT_ZMIN = -10;
  private static final double DEFAULT_ZMAX = 10;
  private AABB aabb;

  @Override
  public boolean intersect(Ray ray, Scene scene, Random random) {
    double distance;
    double fogPenetrated = -FastMath.log(1 - random.nextDouble());
    double fogDistance = fogPenetrated / density;
    AABB aabbTranslated = aabb.getTranslated(-scene.origin.x, -scene.origin.y, -scene.origin.z);
    if (!aabbTranslated.inside(ray.o)) {
      Ray test = new Ray(ray);
      if (aabbTranslated.hitTest(test)) {
        distance = test.tNext;
        distance += fogDistance;
      } else {
        return false;
      }
    } else {
      distance = fogDistance;
    }

    if (distance >= ray.t) {
      return false;
    }

    Vector3 o = new Vector3(ray.o);
    o.scaleAdd(distance, ray.d);
    if (aabbTranslated.inside(o)) {
      ray.t = distance;
      // pick a random normal vector based on a spherical particle.
      // This is done only to prevent fog from looking ugly in the render preview and
      // should have no effect on the path-traced render.
      setRandomNormal(ray, random);
      setRayMaterialAndColor(ray);
      ray.specular = false;
      return true;
    } else {
      return false;
    }
  }

  public void setBounds(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
    setBounds(new AABB(xmin, xmax, ymin, ymax, zmin, zmax));
  }

  private void setBounds(AABB aabb) {
    this.aabb = aabb;
  }

  public AABB getBounds() {
    return new AABB(this.aabb.xmin, this.aabb.xmax, this.aabb.ymin, this.aabb.ymax, this.aabb.zmin, this.aabb.zmax);
  }

  public CuboidFogVolume(Vector3 color, double density, double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
    this(color, density, new AABB(xmin, xmax, ymin, ymax, zmin, zmax));
  }

  public CuboidFogVolume(Vector3 color, double density, AABB aabb) {
    this.type = FogVolumeType.CUBOID;
    this.aabb = aabb;
    this.color = new Vector3(color);
    this.density = density;
  }

  public CuboidFogVolume(Vector3 color, double density) {
    this(color, density, new AABB(DEFAULT_XMIN, DEFAULT_XMAX, DEFAULT_YMIN, DEFAULT_YMAX, DEFAULT_ZMIN, DEFAULT_ZMAX));
  }

  @Override
  public JsonObject volumeSpecificPropertiesToJson() {
    JsonObject properties = new JsonObject();
    JsonObject pos1 = new JsonObject();
    pos1.add("x", aabb.xmin);
    pos1.add("y", aabb.ymin);
    pos1.add("z", aabb.zmin);
    properties.add("pos1", pos1);
    JsonObject pos2 = new JsonObject();
    pos2.add("x", aabb.xmax);
    pos2.add("y", aabb.ymax);
    pos2.add("z", aabb.zmax);
    properties.add("pos2", pos2);
    return properties;
  }

  @Override
  public void importVolumeSpecificProperties(JsonObject json) {
    JsonObject pos1 = json.get("pos1").object();
    double x1 = pos1.get("x").doubleValue(aabb.xmin);
    double y1 = pos1.get("y").doubleValue(aabb.ymin);
    double z1 = pos1.get("z").doubleValue(aabb.zmin);
    JsonObject pos2 = json.get("pos2").object();
    double x2 = pos2.get("x").doubleValue(aabb.xmax);
    double y2 = pos2.get("y").doubleValue(aabb.ymax);
    double z2 = pos2.get("z").doubleValue(aabb.zmax);
    aabb = new AABB(x1, x2, y1, y2, z1, z2);
  }
}
