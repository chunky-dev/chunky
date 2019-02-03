package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Andesite extends MinecraftBlock {
  public static final Andesite INSTANCE = new Andesite();

  private Andesite() {
    super("andesite", Texture.andesite);
  }
}
