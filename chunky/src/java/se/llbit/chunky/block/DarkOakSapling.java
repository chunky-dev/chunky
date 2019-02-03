package se.llbit.chunky.block;

import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class DarkOakSapling extends MinecraftBlock {
  public static final DarkOakSapling INSTANCE = new DarkOakSapling();

  private DarkOakSapling() {
    super("dark_oak_sapling", Texture.darkOakSapling);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SaplingModel.intersect(ray, texture);
  }
}
