package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Sand extends MinecraftBlock {
  public static final Sand INSTANCE = new Sand();

  private Sand() {
    super("sand", Texture.sand);
    localIntersect = true;
  }
}
