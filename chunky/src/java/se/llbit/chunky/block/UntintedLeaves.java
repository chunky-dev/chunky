package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class UntintedLeaves extends Block {

  public UntintedLeaves(String name, Texture texture) {
    super(name, texture);
    solid = false;
    opaque = false;
  }
}
