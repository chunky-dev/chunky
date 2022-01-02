package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class TintedGlass extends MinecraftBlockTranslucent {

  public TintedGlass() {
    super("tinted_glass", Texture.tintedGlass);
    ior = 1.52f;
  }
}
