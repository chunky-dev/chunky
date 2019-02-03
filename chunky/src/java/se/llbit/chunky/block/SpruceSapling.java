package se.llbit.chunky.block;

import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SpruceSapling extends MinecraftBlock {
  public static final SpruceSapling INSTANCE = new SpruceSapling();

  private SpruceSapling() {
    super("spruce_sapling", Texture.spruceSapling);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SaplingModel.intersect(ray, texture);
  }
}
