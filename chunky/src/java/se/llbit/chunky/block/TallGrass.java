package se.llbit.chunky.block;

import se.llbit.chunky.model.TallGrassModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TallGrass extends MinecraftBlock {
  public static final TallGrass UPPER = new TallGrass(Texture.doubleTallGrassTop);
  public static final TallGrass LOWER = new TallGrass(Texture.doubleTallGrassBottom);

  private TallGrass(Texture texture) {
    super("tall_grass", texture);
    localIntersect = true;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TallGrassModel.intersect(ray, scene, texture);
  }
}
