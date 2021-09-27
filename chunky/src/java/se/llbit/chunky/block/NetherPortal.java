package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class NetherPortal extends MinecraftBlockTranslucent {
  private static final Quad[] quad = {
      // North-south.
      new DoubleSidedQuad(
          new Vector3(16 / 16.0, 0, 6 / 16.0),
          new Vector3(0, 0, 6 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),

      // East-west.
      new DoubleSidedQuad(
          new Vector3(10 / 16.0, 0, 16 / 16.0),
          new Vector3(10 / 16.0, 0, 0),
          new Vector3(10 / 16.0, 16 / 16.0, 16 / 16.0),
          new Vector4(0, 16 / 16.0, 0, 16 / 16.0)),
  };

  private final int axis;
  private final String description;

  public NetherPortal(String axis) {
    super("nether_portal", Texture.portal);
    localIntersect = true;
    this.description = "axis=" + axis;
    switch (axis) {
      default:
      case "x":
        this.axis = 0;
        break;
      case "z":
        this.axis = 1;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;
    if (!quad[axis].intersect(ray)) {
      return false;
    }
    texture.getColor(ray);
    ray.setNormal(quad[axis].n);
    ray.t = ray.tNext;
    ray.distance += ray.t;
    ray.o.scaleAdd(ray.t, ray.d);
    return true;
  }

  @Override public String description() {
    return description;
  }
}
