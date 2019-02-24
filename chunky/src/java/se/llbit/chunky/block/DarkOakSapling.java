package se.llbit.chunky.block;

import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class DarkOakSapling extends MinecraftBlock {
  public DarkOakSapling() {
    super("dark_oak_sapling", Texture.darkOakSapling);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SaplingModel.intersect(ray, texture);
  }
}
