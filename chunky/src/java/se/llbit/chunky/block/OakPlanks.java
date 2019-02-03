package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class OakPlanks extends MinecraftBlock {
  public static final OakPlanks INSTANCE = new OakPlanks();

  private OakPlanks() {
    super("oak_planks", Texture.oakPlanks);
  }
}
