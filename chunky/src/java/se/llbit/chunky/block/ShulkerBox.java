package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class ShulkerBox extends MinecraftBlock implements ModelBlock {
  private final DirectionalBlockModel model;
  private final String description;

  public ShulkerBox(String name, Texture side, Texture top, Texture bottom, String facing) {
    super(name, side);
    this.description = "facing=" + facing;
    this.model = new DirectionalBlockModel(facing, top, bottom, side);
    localIntersect = true;
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
