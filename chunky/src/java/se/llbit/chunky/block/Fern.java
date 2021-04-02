package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TallGrassModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Fern extends MinecraftBlockTranslucent implements ModelBlock {

  private final TallGrassModel model;

  public Fern() {
    super("fern", Texture.fern);
    localIntersect = true;
    solid = false;
    model = new TallGrassModel(texture);
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
