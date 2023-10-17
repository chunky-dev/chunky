package se.llbit.chunky.world;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class VolumeMaterial extends Material {
  public VolumeMaterial(String name, Texture texture) {
    super(name, texture);
  }

  public void setRandomSphericalNormal(Ray ray, Random random) {
    // Set a random normal
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
}
