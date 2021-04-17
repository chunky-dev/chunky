package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ChainModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Chain extends MinecraftBlockTranslucent implements ModelBlock {

  private final ChainModel model;

  public Chain(String name, Texture texture, String axis) {
    super(name, texture);
    localIntersect = true;
    model = new ChainModel(axis);
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
