package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Cobblestone extends MinecraftBlock {
  public static final Cobblestone INSTANCE = new Cobblestone();

  private Cobblestone() {
    super("cobblestone", Texture.cobblestone);
  }
}
