package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.SmallDripleafModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SmallDripleaf extends MinecraftBlockTranslucent implements ModelBlock {
  private final SmallDripleafModel model;
  private final String description;

  public SmallDripleaf(String facing, String half) {
    super("small_dripleaf", Texture.smallDripleafTop);
    this.description = "facing=" + facing + ", half=" + half;
    this.model = new SmallDripleafModel(facing, half);
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
