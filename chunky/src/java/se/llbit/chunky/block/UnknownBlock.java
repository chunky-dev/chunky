package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class UnknownBlock extends SpriteBlock {
  public static final UnknownBlock UNKNOWN = new UnknownBlock("?");

  public UnknownBlock(String name) {
    super(name, Texture.unknown);
  }
}
