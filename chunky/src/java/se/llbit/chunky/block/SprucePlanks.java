package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class SprucePlanks extends MinecraftBlock {
  public static final SprucePlanks INSTANCE = new SprucePlanks();

  private SprucePlanks() {
    super("spruce_planks", Texture.sprucePlanks);
  }
}
