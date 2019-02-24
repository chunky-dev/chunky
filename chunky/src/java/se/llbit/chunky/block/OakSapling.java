package se.llbit.chunky.block;

import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class OakSapling extends MinecraftBlock {
  public OakSapling() {
    super("oak_sapling", Texture.oakSapling);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SaplingModel.intersect(ray, texture);
  }
}
