package se.llbit.chunky.block;

import se.llbit.chunky.model.BigDripleafModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class BigDripleaf extends MinecraftBlockTranslucent implements ModelBlock {

  private final String description;
  private final BigDripleafModel model;

  public BigDripleaf(String facing, String tilt) {
    super("big_dripleaf", Texture.bigDripleafTop);
    description = "facing=" + facing + ", tilt=" + tilt;
    model = new BigDripleafModel(facing, tilt);
    localIntersect = true;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
