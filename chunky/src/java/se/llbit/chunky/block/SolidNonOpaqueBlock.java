package se.llbit.chunky.block;

import se.llbit.chunky.resources.texture.AbstractTexture;

public class SolidNonOpaqueBlock extends Block {

  public SolidNonOpaqueBlock(String name, AbstractTexture texture) {
    super(name, texture);
    solid = true;
    opaque = false;
  }
}
