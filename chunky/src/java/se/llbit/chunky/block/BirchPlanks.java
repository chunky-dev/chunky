package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class BirchPlanks extends MinecraftBlock {
  public static final BirchPlanks INSTANCE = new BirchPlanks();

  private BirchPlanks() {
    super("birch_planks", Texture.birchPlanks);
  }
}
