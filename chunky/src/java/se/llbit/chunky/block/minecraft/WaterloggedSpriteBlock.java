package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.resources.Texture;

public class WaterloggedSpriteBlock extends SpriteBlock {
  public WaterloggedSpriteBlock(String name, Texture texture) {
    super(name, texture);
    waterlogged = true;
  }
}
