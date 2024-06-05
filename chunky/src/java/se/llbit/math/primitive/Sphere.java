package se.llbit.math.primitive;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.entity.BVHMaterial;
import se.llbit.chunky.world.Material;
import se.llbit.math.AABB;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;

public class Sphere implements Primitive {
  private final double radius;
  private final Vector3 center;

  public BVHMaterial material;

  public Sphere(Vector3 center, double radius, BVHMaterial material) {
    this.center = new Vector3(center);
    this.radius = radius;
    this.material = material;
  }

  public boolean isInside(Vector3 point) {
    double distance = this.center.rSub(point).length();
    return distance < this.radius;
  }

  public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord) {
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
    intersectionRecord.distance = t0;
    Vector3 pHit = ray.o.rScaleAdd(t0, ray.d);
    Vector3 nHit = pHit.rSub(center).normalized();
    intersectionRecord.setNormal(nHit);
    if (ray.d.dot(nHit) > 0) {
      intersectionRecord.material = Air.INSTANCE;
    } else {
      intersectionRecord.material = material;
    }

    material.getColor(intersectionRecord);

    return true;

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
