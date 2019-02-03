package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Podzol extends MinecraftBlock {
  public static final Podzol INSTANCE = new Podzol();

  private static final Texture[] texture = {
      Texture.podzolSide, Texture.podzolSide, Texture.podzolSide, Texture.podzolSide,
      Texture.podzolTop, Texture.podzolSide,
  };

  private Podzol() {
    super("podzol", Texture.podzolSide);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }

}
