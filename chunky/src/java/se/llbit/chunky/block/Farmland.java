package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Farmland extends MinecraftBlock implements ModelBlock {
  private final TexturedBlockModel model;
  private final int moisture;

  public Farmland(int moisture) {
    super("farmland", Texture.farmlandWet);
    this.model = new TexturedBlockModel(Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt,
        moisture >= 7 ? Texture.farmlandWet : Texture.farmlandDry, Texture.dirt);
    this.moisture = moisture;
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "moisture=" + moisture;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
