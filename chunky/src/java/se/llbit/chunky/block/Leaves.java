package se.llbit.chunky.block;

import se.llbit.chunky.model.LeafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Leaves extends MinecraftBlock {
  public Leaves(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    solid = false;
    opaque = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return LeafModel.intersect(ray, scene, texture);
  }
}
