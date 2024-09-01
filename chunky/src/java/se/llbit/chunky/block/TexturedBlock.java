package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.resources.texture.AbstractTexture;

public class TexturedBlock extends AbstractModelBlock {

  public TexturedBlock(String name, AbstractTexture side, AbstractTexture topBottom) {
    this(name, side, side, side, side, topBottom, topBottom);
  }

  public TexturedBlock(String name, AbstractTexture side, AbstractTexture top, AbstractTexture bottom) {
    this(name, side, side, side, side, top, bottom);
  }

  public TexturedBlock(String name,
                       AbstractTexture north, AbstractTexture south, AbstractTexture east, AbstractTexture west,
                       AbstractTexture top, AbstractTexture bottom) {
    super(name, north);
    this.model = new TexturedBlockModel(north, east, south, west, top, bottom);
    opaque = true;
    solid = true;
  }
}
