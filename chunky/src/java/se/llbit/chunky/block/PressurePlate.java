package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.PressurePlateModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class PressurePlate extends MinecraftBlockTranslucent implements ModelBlock {
  private final PressurePlateModel model;

  public PressurePlate(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    this.model = new PressurePlateModel(texture);
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
