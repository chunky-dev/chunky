package se.llbit.chunky.block;

import se.llbit.chunky.model.BigDripleafStemModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class BigDripleafStem extends MinecraftBlockTranslucent {

  private final String facing;

  public BigDripleafStem(String facing) {
    super("big_dripleaf_stem", Texture.bigDripleafStem);
    this.facing = facing;
    localIntersect = true;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return BigDripleafStemModel.intersect(ray, facing);
  }

  @Override
  public String description() {
    return "facing=" + facing;
  }
}
