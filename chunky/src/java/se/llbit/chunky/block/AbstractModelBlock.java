package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class AbstractModelBlock extends MinecraftBlock {
  protected BlockModel model;

  public AbstractModelBlock(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }
}
