package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

public class Slab extends MinecraftBlock {
  private final int half;
  private final Texture sideTexture;
  private final Texture topTexture;

  public Slab(String name, Texture sideTexture, Texture topTexture, String type) {
    super(String.format("%s (type=%s)", name, type), sideTexture);
    this.sideTexture = sideTexture;
    this.topTexture = topTexture;
    localIntersect = true;
    solid = false;
    half = (type.equals("top")) ? 1 : 0;
  }

  public Slab(String name, Texture texture, String type) {
    this(name, texture, texture, type);
  }

  private static AABB[] aabb = {
      // lower half-block
      new AABB(0, 1, 0, .5, 0, 1),

      // upper half-block
      new AABB(0, 1, .5, 1, 0, 1),
  };

  @Override public boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;
    if (aabb[half].intersect(ray)) {
      if (ray.n.y != 0) {
        topTexture.getColor(ray);
      } else {
        sideTexture.getColor(ray);
      }
      ray.color.w = 1;
      ray.distance += ray.tNext;
      ray.o.scaleAdd(ray.tNext, ray.d);
      return true;
    }
    return false;
  }

}
