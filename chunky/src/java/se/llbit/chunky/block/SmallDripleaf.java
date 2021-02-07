package se.llbit.chunky.block;

import se.llbit.chunky.model.SmallDripleafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SmallDripleaf extends MinecraftBlockTranslucent {

  private final String half;

  public SmallDripleaf(String half) {
    super("small_dripleaf", Texture.smallDripleafTop);
    this.half = half;
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return SmallDripleafModel.intersect(ray, half);
  }

  @Override
  public String description() {
    return "half=" + half;
  }
}
