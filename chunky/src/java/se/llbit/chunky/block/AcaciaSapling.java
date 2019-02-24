package se.llbit.chunky.block;

import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class AcaciaSapling extends MinecraftBlock {
  public AcaciaSapling() {
    super("acacia_sapling", Texture.acaciaSapling);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return SaplingModel.intersect(ray, texture);
  }
}
