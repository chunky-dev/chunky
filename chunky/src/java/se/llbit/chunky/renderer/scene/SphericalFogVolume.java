package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.world.material.ParticleFogMaterial;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class SphericalFogVolume extends FogVolume {
  private Vector3 center;
  private double radius;

  @Override
  public boolean intersect(Ray ray, Scene scene, Random random) {
    double distance;
    double fogPenetrated = -Math.log(1 - random.nextDouble());
    double fogDistance = fogPenetrated / density;
    Vector3 o = new Vector3(ray.o);
    Vector3 transformedCenter = new Vector3(center.x + -scene.origin.x, center.y + -scene.origin.y, center.z + -scene.origin.z);
    Vector3 centerFromRayOrigin = new Vector3(transformedCenter);
    centerFromRayOrigin.sub(o);
    double rayDistanceFromCenter = centerFromRayOrigin.length();
    if (rayDistanceFromCenter > radius) {
      o.scaleAdd(rayDistanceFromCenter, ray.d);
      o.sub(transformedCenter);
      double distanceOfPointClosestToCenter = o.length();
      if (distanceOfPointClosestToCenter <= radius) {
        distance = distanceOfPointClosestToCenter;
        distance -= FastMath.sqrt(FastMath.pow(radius, 2) - FastMath.pow(distanceOfPointClosestToCenter, 2));
        distance += fogDistance;
      } else {
        return false;
      }
    } else {
      distance = fogDistance;
    }
    if (distance > ray.t) {
      return false;
    }
    if (distance <= radius) {
      ray.t = distance;
      setRandomNormal(ray, random);
      ray.setCurrentMaterial(ParticleFogMaterial.INSTANCE);
      ray.color.set(color.x, color.y, color.z, 1);
      return true;
    } else {
      return false;
    }
  }

  public Vector3 getCenter() {
    return this.center;
  }

  public void setCenter(Vector3 value) {
    this.center = new Vector3(value);
  }

  public double getRadius() {
    return this.radius;
  }

  public void setRadius(double value) {
    this.radius = value;
  }

  public SphericalFogVolume(Vector3 center, double radius, Vector3 color, double density) {
    this.center = center;
    this.radius = radius;
    this.color = color;
    this.density = density;
  }
}
