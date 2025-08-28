package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

/**
 * Non-opaque block.
 */
public class MinecraftBlockTranslucent extends MinecraftBlock {
  public MinecraftBlockTranslucent(String name, Texture texture) {
    super(name, texture);
    opaque = false;
    solid = false;
  }
}
