package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.LogModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Log extends MinecraftBlock implements ModelBlock {
  private final LogModel model;
  private final String description;

  public Log(String name, Texture side, Texture top, String axis) {
    super(name, side);
    this.description = "axis=" + axis;
    this.model = new LogModel(axis, side, top);
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
