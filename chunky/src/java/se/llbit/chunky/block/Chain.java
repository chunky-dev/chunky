package se.llbit.chunky.block;

import se.llbit.chunky.model.ChainModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Chain extends MinecraftBlockTranslucent {

  private final String axis;

  public Chain(String name, Texture texture, String axis) {
    super(name, texture);
    localIntersect = true;
    this.axis = axis;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return ChainModel.intersect(ray, axis);
  }
}
