package se.llbit.math;

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.world.Material;

public class Ray2 {
  public Vector3 o = new Vector3();
  public Vector3 d = new Vector3();
  private Material currentMedium = Air.INSTANCE;

  public Ray2() {
  }

  public Ray2(Ray2 ray) {
    o.set(ray.o);
    d.set(ray.d);
    currentMedium = ray.currentMedium;
  }

  public Ray2(Vector3 origin, Vector3 direction) {
    o.set(origin);
    d.set(direction);
  }

  public Material getCurrentMedium() {
    return currentMedium;
  }

  public void setCurrentMedium(Material material) {
    this.currentMedium = material;
  }
}
