package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class JungleStairs extends Stairs {
  public JungleStairs(String half, String shape, String facing) {
    super("jungle_stairs", Texture.jungleTreePlanks, half, shape, facing);
  }
}
