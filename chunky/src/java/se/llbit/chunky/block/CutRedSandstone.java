package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class CutRedSandstone extends MinecraftBlock {
  private static final Texture[] texture = {
      Texture.redSandstoneCut, Texture.redSandstoneCut, Texture.redSandstoneCut,
      Texture.redSandstoneCut, Texture.redSandstoneTop, Texture.redSandstoneBottom,
  };

  public CutRedSandstone() {
    super("cut_red_sandstone", texture[0]);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }
}
