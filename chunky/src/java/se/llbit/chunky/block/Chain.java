package se.llbit.chunky.block;

import se.llbit.chunky.model.ChainModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Chain extends MinecraftBlockTranslucent {
  public Chain(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return ChainModel.intersect(ray);
  }
}
