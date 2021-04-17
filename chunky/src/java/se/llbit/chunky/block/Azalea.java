package se.llbit.chunky.block;

import se.llbit.chunky.model.AzaleaModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Azalea extends MinecraftBlockTranslucent implements ModelBlock {

  private final AzaleaModel model;

  public Azalea(String name, Texture top, Texture side) {
    super(name, top);
    localIntersect = true;
    solid = false;
    model = new AzaleaModel(top, side);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
