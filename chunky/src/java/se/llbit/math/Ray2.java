package se.llbit.math;

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.world.Material;

public class Ray2 {
  public Point3 o;
  public Vector3 d;
  private Material currentMedium;

  public Ray2() {
    o = new Point3();
    d = new Vector3();
    currentMedium = Air.INSTANCE;
  }

  public Ray2(Ray2 ray) {
    o.set(ray.o);
    d.set(ray.d);
    currentMedium = ray.currentMedium;
  }

  public Material getCurrentMedium() {
    return currentMedium;
  }

  public void setCurrentMedium(Material material) {
    this.currentMedium = material;
  }
}
