package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Sand extends MinecraftBlock {
  public Sand() {
    super("sand", Texture.sand);
    localIntersect = true;
  }
}
