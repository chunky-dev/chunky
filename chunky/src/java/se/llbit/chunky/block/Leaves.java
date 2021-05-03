package se.llbit.chunky.block;

import se.llbit.chunky.model.LeafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;

public class Leaves extends MinecraftBlock {
  private float[] tint;

  public Leaves(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    opaque = false;
  }

  public Leaves(String name, Texture texture, int tint) {
    this(name, texture);
    this.tint = new float[3];
    ColorUtil.getRGBComponents(tint, this.tint);
    ColorUtil.toLinear(this.tint);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    if (tint != null) {
      return LeafModel.intersect(ray, texture, tint);
    }
    return LeafModel.intersect(ray, scene, texture);
  }
}
