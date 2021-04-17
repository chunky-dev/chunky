package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CarpetModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Carpet extends MinecraftBlock implements ModelBlock {
  private final CarpetModel model;

  public Carpet(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
    this.model = new CarpetModel(texture);
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
