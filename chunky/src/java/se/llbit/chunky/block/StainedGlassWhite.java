package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassWhite extends MinecraftBlock {
  public StainedGlassWhite() {
    super("white_stained_glass", Texture.whiteGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
