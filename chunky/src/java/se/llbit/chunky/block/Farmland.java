package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Farmland extends MinecraftBlock {
  private static final Texture[] moist = {
      Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt,
      Texture.farmlandWet, Texture.dirt,
  };
  private static final Texture[] dry = {
      Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt,
      Texture.farmlandDry, Texture.dirt,
  };
  private final int moisture;

  public Farmland(int moisture) {
    super("farmland", Texture.farmlandWet);
    this.moisture = moisture;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, moisture >= 7 ? moist : dry);
  }

  @Override public String description() {
    return "moisture=" + moisture;
  }
}
