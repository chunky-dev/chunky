package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class AzaleaLeaves extends Block {

  public AzaleaLeaves(String name, Texture texture) {
    super(name, texture);
    solid = false;
    opaque = false;
  }
}
