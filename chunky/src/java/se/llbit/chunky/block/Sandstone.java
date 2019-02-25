package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Sandstone extends MinecraftBlock {
  private static final Texture[] texture = {
      Texture.sandstoneSide, Texture.sandstoneSide, Texture.sandstoneSide,
      Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom,
  };

  public Sandstone() {
    super("sandstone", texture[0]);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }
}
