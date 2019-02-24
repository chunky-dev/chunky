package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class ChiseledStandstone extends MinecraftBlock {
  private static final Texture[] texture = {
      Texture.sandstoneDecorated, Texture.sandstoneDecorated, Texture.sandstoneDecorated,
      Texture.sandstoneDecorated, Texture.sandstoneTop, Texture.sandstoneBottom,
  };

  public ChiseledStandstone() {
    super("chiseled_sandstone", Texture.sandstoneDecorated);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }
}
