package se.llbit.chunky.block;

import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class JungleSapling extends MinecraftBlock {
  public JungleSapling() {
    super("jungle_sapling", Texture.jungleSapling);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SaplingModel.intersect(ray, texture);
  }
}
