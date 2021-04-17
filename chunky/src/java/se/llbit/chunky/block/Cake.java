package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CakeModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Cake extends MinecraftBlockTranslucent implements ModelBlock {
  private final int bites;
  private final CakeModel model;

  public Cake(int bites) {
    super("cake", Texture.cakeTop);
    localIntersect = true;
    this.model = new CakeModel(bites);
    this.bites = bites;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "bites=" + bites;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
