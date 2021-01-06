package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Dropper extends Dispenser {

  public Dropper(String facing) {
    super("dropper", facing, Texture.dropperFront, Texture.dropperFrontVertical,
        Texture.furnaceSide, Texture.furnaceTop);
  }
}
