package se.llbit.chunky.block;

import se.llbit.chunky.model.LeafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SpruceLeaves extends MinecraftBlock {
  public SpruceLeaves() {
    super("birch_leaves", Texture.birchLeaves);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return LeafModel.intersect(ray, scene, texture);
  }
}
