package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class DarkOakStairs extends Stairs {
  public DarkOakStairs(String half, String shape, String facing) {
    super("dark_oak_stairs", Texture.darkOakPlanks, half, shape, facing);
  }
}
