package se.llbit.chunky.block;

import se.llbit.chunky.model.PressurePlateModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class PressurePlate extends MinecraftBlockTranslucent {
  public PressurePlate(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return PressurePlateModel.intersect(ray, texture);
  }
}
