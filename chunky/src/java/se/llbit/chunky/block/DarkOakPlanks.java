package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class DarkOakPlanks extends MinecraftBlock {
  public static final DarkOakPlanks INSTANCE = new DarkOakPlanks();

  private DarkOakPlanks() {
    super("dark_oak_planks", Texture.darkOakPlanks);
  }
}
