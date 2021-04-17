package se.llbit.chunky.block;

import se.llbit.chunky.model.BigDripleafStemModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class BigDripleafStem extends MinecraftBlockTranslucent implements ModelBlock {

  private final String facing;
  private final BigDripleafStemModel model;

  public BigDripleafStem(String facing) {
    super("big_dripleaf_stem", Texture.bigDripleafStem);
    this.facing = facing;
    model = new BigDripleafStemModel(facing);
    localIntersect = true;
    solid = false;
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
