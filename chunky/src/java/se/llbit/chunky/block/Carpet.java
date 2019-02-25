package se.llbit.chunky.block;

import se.llbit.chunky.model.CarpetModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.math.Ray;

public class Carpet extends MinecraftBlock {
  public Carpet(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CarpetModel.intersect(ray, texture);
  }
}
