package se.llbit.chunky.model;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;

public class LightBlockModel extends AABBModel {
  public static final AABB[] aabb = { new AABB(0.125, 0.875, 0.125, 0.875, 0.125, 0.875) };

  private final AABB[] box = aabb;

  private final Texture[][] textures = new Texture[][] {{
    Texture.light, Texture.light, Texture.light,
    Texture.light, Texture.light, Texture.light
  }};

  private final Vector4 color;

  public LightBlockModel(Vector4 color) {
    this.color = color;
  }

  @Override
  public AABB[] getBoxes() {
    return box;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    AABB[] boxes = getBoxes();
    ray.t = Double.POSITIVE_INFINITY;
    if (boxes[0].intersect(ray)) {
      ray.color.set(color);
      hit = true;
      ray.t = ray.tNext;
    }
    return hit;
  }

  @Override
  public Texture[][] getTextures() {
    return textures;
  }
}
