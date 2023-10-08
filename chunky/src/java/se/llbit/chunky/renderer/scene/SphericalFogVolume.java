package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.json.JsonObject;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class SphericalFogVolume extends FogVolume {
  private static final double DEFAULT_X = 0;
  private static final double DEFAULT_Y = 100;
  private static final double DEFAULT_Z = 0;
  private static final double DEFAULT_RADIUS = 10;
  private Sphere sphere;

  @Override
  public boolean intersect(Ray ray, Scene scene, Random random) {
    double distance;
    double fogPenetrated = -Math.log(1 - random.nextDouble());
    double fogDistance = fogPenetrated / density;
    Sphere sphereTranslated = sphere.getTranslated(-scene.origin.x, -scene.origin.y, -scene.origin.z);
    if (!sphereTranslated.isInside(ray.o)) {
      Ray test = new Ray(ray);
      if (sphereTranslated.intersect(test)) {
        distance = test.t;
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
    if (sphereTranslated.isInside(o)) {
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

  public Vector3 getCenter() {
    return new Vector3(this.sphere.center);
  }

  public void setCenter(Vector3 center) {
    this.sphere = new Sphere(center, this.sphere.radius);
  }

  public void setCenterX(double value) {
    setCenter(new Vector3(value, this.sphere.center.y, this.sphere.center.z));
  }

  public void setCenterY(double value) {
    setCenter(new Vector3(this.sphere.center.x, value, this.sphere.center.z));
  }

  public void setCenterZ(double value) {
    setCenter(new Vector3(this.sphere.center.x, this.sphere.center.y, value));
  }


  public double getRadius() {
    return this.sphere.radius;
  }

  public void setRadius(double value) {
    this.sphere = new Sphere(this.sphere.center, value);
  }

  public SphericalFogVolume(Vector3 color, double density, Vector3 center, double radius) {
    type = FogVolumeType.SPHERE;
    this.sphere = new Sphere(new Vector3(center), radius);
    this.color = new Vector3(color);
    this.density = density;
  }

  public SphericalFogVolume(Vector3 color, double density) {
    this(color, density, new Vector3(DEFAULT_X, DEFAULT_Y, DEFAULT_Z), DEFAULT_RADIUS);
  }

  @Override
  public JsonObject volumeSpecificPropertiesToJson() {
    JsonObject properties = new JsonObject();
    JsonObject position = new JsonObject();
    position.add("x", this.sphere.center.x);
    position.add("y", this.sphere.center.y);
    position.add("z", this.sphere.center.z);
    properties.add("position", position);
    properties.add("radius", this.sphere.radius);
    return properties;
  }

  @Override
  public void importVolumeSpecificProperties(JsonObject json) {
    JsonObject position = json.get("position").object();
    double x = position.get("x").doubleValue(this.sphere.center.x);
    double y = position.get("y").doubleValue(this.sphere.center.y);
    double z = position.get("z").doubleValue(this.sphere.center.z);
    double radius = json.get("radius").doubleValue(this.sphere.radius);
    this.sphere = new Sphere(new Vector3(x, y, z), radius);
  }

  // FogVolume-specific sphere implementation.
  private static class Sphere {
    double radius;
    Vector3 center;

    Sphere(Vector3 center, double radius) {
      this.center = new Vector3(center);
      this.radius = radius;
    }

    boolean isInside(Vector3 point) {
      double distance = new Vector3(center.x - point.x, center.y - point.y, center.z - point.z).length();
      return distance <= radius;
    }

    boolean intersect(Ray ray) {
      Vector3 rayOrigin = new Vector3(ray.o);
      Vector3 rayDirection = new Vector3(ray.d);

      double t = rayDirection.dot(new Vector3(center.x - rayOrigin.x, center.y - rayOrigin.y, center.z - rayOrigin.z));
      if (t < 0) {
        return false;
      }

      Vector3 tPoint = new Vector3(rayOrigin);
      tPoint.scaleAdd(t, rayDirection);

      double tCenterDistance = new Vector3(center.x - tPoint.x, center.y - tPoint.y, center.z - tPoint.z).length();

      if (isInside(tPoint)) {
        double x = FastMath.sqrt(radius * radius - tCenterDistance * tCenterDistance);
        double t1 = t - x;
        double t2 = t + x;
        if (t1 >= 0) {
          ray.t = t1;
          return true;
        }
      }
      return false;
    }

    Sphere getTranslated(double x, double y, double z) {
      return new Sphere(new Vector3(center.x + x, center.y + y, center.z + z), this.radius);
    }
  }
}
