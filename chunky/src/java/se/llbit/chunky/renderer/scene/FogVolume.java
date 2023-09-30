package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public abstract class FogVolume {
  public Vector3 color;
  public double density;
  public abstract boolean intersect(Ray ray, Scene scene, Random random);
  public void setRandomNormal(Ray ray, Random random) {
    Vector3 a1 = new Vector3();
    a1.cross(ray.d, new Vector3(0, 1, 0));
    a1.normalize();
    Vector3 a2 = new Vector3();
    a2.cross(ray.d, a1);
    // get random point on unit disk
    double x1 = random.nextDouble();
    double x2 = random.nextDouble();
    double r = FastMath.sqrt(x1);
    double theta = 2 * Math.PI * x2;
    double t1 = r * FastMath.cos(theta);
    double t2 = r * FastMath.sin(theta);
    a1.scale(t1);
    a1.scaleAdd(t2, a2);
    a1.scaleAdd(-Math.sqrt(1 - a1.lengthSquared()), ray.d);
    ray.setNormal(a1);
  }

  public void setDensity(double value) {
    this.density = value;
  }

  public void setColor(Vector3 value) {
    this.color = new Vector3(value);
  }
}
