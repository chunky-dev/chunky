package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TexturedBlock extends MinecraftBlock {
  private final Texture[] texture;

  public TexturedBlock(String name, Texture side, Texture topBottom) {
    this(name, side, side, side, side, topBottom, topBottom);
  }

  public TexturedBlock(String name, Texture side, Texture top, Texture bottom) {
    this(name, side, side, side, side, top, bottom);
  }

  public TexturedBlock(String name,
      Texture north, Texture south, Texture west, Texture east,
      Texture top, Texture bottom) {
    super(name, north);
    texture = new Texture[] { north, south, west, east, top, bottom };
    localIntersect = true;
    opaque = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }
}
