package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class TintedGlass extends MinecraftBlock {

  public TintedGlass() {
    super("tinted_glass", Texture.tintedGlass);
    opaque = false;
    solid = true;
    ior = 1.52f;
  }
}
