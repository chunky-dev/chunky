package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class SolidNonOpaqueBlock extends MinecraftBlock {
  public SolidNonOpaqueBlock(String name, Texture texture) {
    super(name, texture);
    solid = true;
    opaque = false;
  }
}
