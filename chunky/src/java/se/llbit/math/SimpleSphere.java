package se.llbit.math;

import org.apache.commons.math3.util.FastMath;

public class SimpleSphere {
  private final double radius;
  private final Vector3 center;

  public SimpleSphere(Vector3 center, double radius) {
    this.center = new Vector3(center);
    this.radius = radius;
  }

  public boolean isInside(Vector3 point) {
    double distance = this.center.rSub(point).length();
    return distance < this.radius;
  }

  public double intersect(Ray2 ray) {
    double t0;
    double t1;

    double radiusSquared = radius * radius;

    Vector3 l = center.rSub(ray.o);
    double tca = ray.d.dot(l);
    if (tca < 0.0 && l.length() > radius) {
      return -1;
    }

    double d2 = l.dot(l) - tca * tca;
    if (d2 > radiusSquared) {
      return -1;
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
        return -1;
      }
    }
    if (t0 > Constants.EPSILON) {
      return t0;
    }

    return -1;
  }
}
