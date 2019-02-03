package se.llbit.chunky.block;

import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class BirchSapling extends MinecraftBlock {
  public static final BirchSapling INSTANCE = new BirchSapling();

  private BirchSapling() {
    super("birch_sapling", Texture.birchSapling);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SaplingModel.intersect(ray, texture);
  }
}
