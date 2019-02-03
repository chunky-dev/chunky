package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class GoldOre extends MinecraftBlock {
  public static final GoldOre INSTANCE = new GoldOre();

  private GoldOre() {
    super("gold_ore", Texture.goldOre);
  }
}
