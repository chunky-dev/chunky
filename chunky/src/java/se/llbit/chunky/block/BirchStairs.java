package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class BirchStairs extends Stairs {
  public BirchStairs(String half, String shape, String facing) {
    super("birch_stairs", Texture.birchPlanks, half, shape, facing);
  }
}
