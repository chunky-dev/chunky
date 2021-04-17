package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.resources.Texture;

public class TexturedBlock extends AbstractModelBlock {

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
    opaque = true;
  }
}
