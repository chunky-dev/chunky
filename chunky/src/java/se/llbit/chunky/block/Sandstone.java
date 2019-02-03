package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Sandstone extends MinecraftBlock {
  public static final Sandstone INSTANCE = new Sandstone();

  private static final Texture[] texture = {
      Texture.sandstoneSide, Texture.sandstoneSide, Texture.sandstoneSide,
      Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom,
  };

  private Sandstone() {
    super("sandstone", Texture.sandstoneSide);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }
}
