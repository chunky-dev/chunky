package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

/**
 * A simple opaque block with a single texture.
 */
public class MinecraftBlock extends Block {
  public MinecraftBlock(String name, Texture texture) {
    super("minecraft:" + name, texture);
    opaque = true;
  }
}
