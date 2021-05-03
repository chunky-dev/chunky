package se.llbit.chunky.block;

import se.llbit.chunky.model.BigDripleafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class BigDripleaf extends MinecraftBlockTranslucent {

  private final String facing;
  private final String tilt;

  public BigDripleaf(String facing, String tilt) {
    super("big_dripleaf", Texture.bigDripleafTop);
    this.facing = facing;
    this.tilt = tilt;
    localIntersect = true;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return BigDripleafModel.intersect(ray, facing, tilt);
  }

  @Override
  public String description() {
    return "facing=" + facing + ", tilt=" + tilt;
  }
}
