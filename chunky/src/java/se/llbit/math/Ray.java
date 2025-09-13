package se.llbit.math;

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.world.Material;

/**
 * The ray representation used for ray tracing.
 */
public class Ray {
  public static final int DIFFUSE = 1;
  public static final int SPECULAR = 1 << 1;
  public static final int INDIRECT = 1 << 2;

  public Vector3 o = new Vector3();
  public Vector3 d = new Vector3();
  private Material currentMedium = Air.INSTANCE;
  public int flags = 0;

  public void setDiffuse(boolean value) {
    this.flags = value ? this.flags | DIFFUSE : this.flags & ~DIFFUSE;
  }

  public void setSpecular(boolean value) {
    this.flags = value ? this.flags | SPECULAR : this.flags & ~SPECULAR;
  }

  public void setIndirect(boolean value) {
    this.flags = value ? this.flags | INDIRECT : this.flags & ~INDIRECT;
  }

  public boolean isDiffuse() {
    return (this.flags & DIFFUSE) != 0;
  }

  public boolean isSpecular() {
    return (this.flags & SPECULAR) != 0;
  }

  public boolean isIndirect() {
    return (this.flags & INDIRECT) != 0;
  }

  public Ray() {}

  public Ray(Ray ray) {
    o.set(ray.o);
    d.set(ray.d);
    currentMedium = ray.currentMedium;
    flags = ray.flags;
  }

  public Ray(Vector3 origin, Vector3 direction) {
    o.set(origin);
    d.set(direction);
  }

  public void set(Ray other) {
    o.set(other.o);
    d.set(other.d);
    currentMedium = other.currentMedium;
    flags = other.flags;
  }

  public void reset() {
    o.set(0);
    d.set(0);
    currentMedium = Air.INSTANCE;
    flags = 0;
  }

  public void clearReflectionFlags() {
    flags &= ~DIFFUSE & ~SPECULAR;
  }

  public Material getCurrentMedium() {
    return currentMedium;
  }

  public void setCurrentMedium(Material material) {
    this.currentMedium = material;
  }
}
