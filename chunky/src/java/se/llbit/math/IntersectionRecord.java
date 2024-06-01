package se.llbit.math;

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.world.Material;

public class IntersectionRecord {
  public double distance = Double.POSITIVE_INFINITY;
  public final Vector3 n = new Vector3(0, 1, 0);
  public final Vector2 uv = new Vector2();
  public Material material = Air.INSTANCE;
  public final Vector3 shadeN = new Vector3(0, 1, 0);
  public final Vector4 color = new Vector4();

  public void reset() {
    this.distance = Double.POSITIVE_INFINITY;
    this.n.set(0, 1, 0);
    this.uv.set(0, 0);
    this.material = Air.INSTANCE;
    this.shadeN.set(0, 1, 0);
    this.color.set(0, 0, 0, 0);
  }

  public void setNormal(Vector3 normal) {
    n.set(normal);
    shadeN.set(normal);
  }

  public void setNormal(double x, double y, double z) {
    n.set(x, y, z);
    shadeN.set(x, y, z);
  }
}
