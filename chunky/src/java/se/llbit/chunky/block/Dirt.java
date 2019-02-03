package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Dirt extends MinecraftBlock {
  public static final Dirt INSTANCE = new Dirt();

  private Dirt() {
    super("dirt", Texture.dirt);
  }
}
