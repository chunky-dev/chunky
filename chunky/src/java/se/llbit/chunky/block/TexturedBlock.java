package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TexturedBlock extends MinecraftBlock implements ModelBlock {
  private final TexturedBlockModel model;

  public TexturedBlock(String name, Texture side, Texture topBottom) {
    this(name, side, side, side, side, topBottom, topBottom);
  }

  public TexturedBlock(String name, Texture side, Texture top, Texture bottom) {
    this(name, side, side, side, side, top, bottom);
  }

  public TexturedBlock(String name,
      Texture north, Texture south, Texture east, Texture west,
      Texture top, Texture bottom) {
    super(name, north);
    this.model = new TexturedBlockModel(north, east, south, west, top, bottom);
    localIntersect = true;
    opaque = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public BlockModel getModel() {
    return this.model;
  }
}
