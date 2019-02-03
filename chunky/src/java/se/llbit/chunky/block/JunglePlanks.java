package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class JunglePlanks extends MinecraftBlock {
  public static final JunglePlanks INSTANCE = new JunglePlanks();

  private JunglePlanks() {
    super("jungle_planks", Texture.jungleTreePlanks);
  }
}
