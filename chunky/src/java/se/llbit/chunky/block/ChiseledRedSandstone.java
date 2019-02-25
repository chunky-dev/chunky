package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class ChiseledRedSandstone extends MinecraftBlock {
  private static final Texture[] texture = {
      Texture.redSandstoneDecorated, Texture.redSandstoneDecorated,
      Texture.redSandstoneDecorated, Texture.redSandstoneDecorated,
      Texture.redSandstoneTop, Texture.redSandstoneBottom,
  };

  public ChiseledRedSandstone() {
    super("chiseled_red_sandstone", texture[0]);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }
}
