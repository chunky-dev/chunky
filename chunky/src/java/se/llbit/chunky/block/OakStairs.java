package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class OakStairs extends Stairs {
  public OakStairs(String half, String shape, String facing) {
    super("oak_stairs", Texture.oakPlanks, half, shape, facing);
  }
}
