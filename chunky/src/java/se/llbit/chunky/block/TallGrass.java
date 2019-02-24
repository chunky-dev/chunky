package se.llbit.chunky.block;

import se.llbit.chunky.model.TallGrassModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TallGrass extends MinecraftBlock {
  public TallGrass(String half) {
    super("tall_grass",
        half.equals("upper")
            ? Texture.doubleTallGrassTop
            : Texture.doubleTallGrassBottom);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TallGrassModel.intersect(ray, scene, texture);
  }
}
