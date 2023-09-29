package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.world.material.ParticleFogMaterial;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class CuboidFogVolume extends FogVolume {

  private AABB aabb;

  @Override
  public boolean intersect(Ray ray, Scene scene, Random random) {
    double distance;
    double fogPenetrated = -Math.log(1 - random.nextDouble());
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

    if (distance > ray.t) {
      return false;
    }

    Vector3 o = new Vector3(ray.o);
    o.scaleAdd(distance, ray.d);
    if (aabbTranslated.inside(o)) {
      ray.t = distance;
      setRandomNormal(ray, random);
      ray.setCurrentMaterial(ParticleFogMaterial.INSTANCE);
      ray.color.set(color.x, color.y, color.z, 1);
      return true;
    } else {
      return false;
    }
  }

  public void setBounds(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
    this.aabb = new AABB(xmin, xmax, ymin, ymax, zmin, zmax);
  }

  public void setBounds(AABB aabb) {
    this.aabb = aabb;
  }

  public CuboidFogVolume(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax, Vector3 color, double density) {
    this.aabb = new AABB(xmin, xmax, ymin, ymax, zmin, zmax);
    this.color = color;
    this.density = density;
  }

  public CuboidFogVolume(AABB aabb, Vector3 color, double density) {
    this.aabb = aabb;
    this.color = color;
    this.density = density;
  }
}
