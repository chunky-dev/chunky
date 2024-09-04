package se.llbit.math.primitive;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;
import se.llbit.math.*;

public class Sphere implements Primitive {
  private final double radius;
  private final Vector3 center;

  public Material material;

  public Sphere(Vector3 center, double radius, Material material) {
    this.center = new Vector3(center);
    this.radius = radius;
    this.material = material;
  }

  public boolean isInside(Vector3 point) {
    double distance = this.center.rSub(point).length();
    return distance < this.radius;
  }

  public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    double t0;
    double t1;

    double radiusSquared = radius * radius;

    Vector3 l = center.rSub(ray.o);
    double tca = ray.d.dot(l);
    if (tca < 0.0 && l.length() > radius) {
      return false;
    }

    double d2 = l.dot(l) - tca * tca;
    if (d2 > radiusSquared) {
      return false;
    }
    double thc = FastMath.sqrt(radiusSquared - d2);
    t0 = tca - thc;
    t1 = tca + thc;

    if (t0 > t1) {
      double tmp = t0;
      t0 = t1;
      t1 = tmp;
    }

    if (t0 < 0) {
      t0 = t1;
      if (t0 < 0) {
        return false;
      }
    }
    if (t0 > Constants.EPSILON && t0 < intersectionRecord.distance) {
      intersectionRecord.distance = t0;
      Vector3 nHit = ray.o.rScaleAdd(t0, ray.d);
      nHit.sub(center);
      nHit.normalize();
      intersectionRecord.setNormal(nHit);
      if (ray.d.dot(nHit) > 0) {
        intersectionRecord.material = Air.INSTANCE;
        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
        intersectionRecord.color.set(1, 1, 1, 0);
      } else {
        intersectionRecord.material = material;
        material.getColor(intersectionRecord);
      }

      return true;
    }

    return false;
  }

  @Override
  public AABB bounds() {
    return new AABB(
      center.x - radius,
      center.x + radius,
      center.y - radius,
      center.y + radius,
      center.z - radius,
      center.z + radius
    );
  }
}
