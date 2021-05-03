package se.llbit.chunky.block;

import se.llbit.chunky.model.SmallDripleafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SmallDripleaf extends MinecraftBlockTranslucent {

  private final String facing;
  private final String half;

  public SmallDripleaf(String facing, String half) {
    super("small_dripleaf", Texture.smallDripleafTop);
    this.facing = facing;
    this.half = half;
    localIntersect = true;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return SmallDripleafModel.intersect(ray, facing, half);
  }

  @Override
  public String description() {
    return "facing=" + facing + ", half=" + half;
  }
}
