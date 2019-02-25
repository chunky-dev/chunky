package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class SpruceStairs extends Stairs {
  public SpruceStairs(String half, String shape, String facing) {
    super("spruce_stairs", Texture.sprucePlanks, half, shape, facing);
  }
}
