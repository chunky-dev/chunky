package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassPurple extends MinecraftBlock {
  public StainedGlassPurple() {
    super("purple_stained_glass", Texture.purpleGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
