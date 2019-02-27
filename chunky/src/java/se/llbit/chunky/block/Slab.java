package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;

public class Slab extends MinecraftBlock {
  private final static AABB[] aabb = {
      // Lower half-block.
      new AABB(0, 1, 0, .5, 0, 1),

      // Upper half-block.
      new AABB(0, 1, .5, 1, 0, 1),

      // Double slab.
      new AABB(0, 1, 0, 1, 0, 1),
  };

  private final int half;
  private final Texture sideTexture;
  private final Texture topTexture;
  private final String description;

  public Slab(String name, Texture sideTexture, Texture topTexture, String type) {
    super(name, sideTexture);
    this.description = String.format("type=%s", type);
    this.sideTexture = sideTexture;
    this.topTexture = topTexture;
    localIntersect = true;
    solid = false;
    switch (type) {
      default:
      case "top":
        half = 1;
        break;
      case "bottom":
        half = 0;
        break;
      case "double":
        half = 2;
        break;
    }
  }

  public Slab(String name, Texture texture, String type) {
    this(name, texture, texture, type);
  }

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

  @Override public String description() {
    return description;
  }
}
