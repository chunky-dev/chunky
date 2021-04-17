package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.StonecutterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Stonecutter extends MinecraftBlockTranslucent implements ModelBlock {
  private final StonecutterModel model;
  private final String facing;

  public Stonecutter(String facing) {
    super("stonecutter", Texture.stonecutterSide);
    localIntersect = true;
    this.facing = facing;
    this.model = new StonecutterModel(facing);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "facing=" + facing;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
