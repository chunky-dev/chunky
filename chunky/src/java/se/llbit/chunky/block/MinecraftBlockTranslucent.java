package se.llbit.chunky.block;

import se.llbit.chunky.resources.texture.AbstractTexture;

/**
 * Non-opaque block.
 */
public class MinecraftBlockTranslucent extends MinecraftBlock {
  public MinecraftBlockTranslucent(String name, AbstractTexture texture) {
    super(name, texture);
    opaque = false;
    solid = false;
  }
}
