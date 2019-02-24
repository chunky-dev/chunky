package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassGray extends MinecraftBlock {
  public StainedGlassGray() {
    super("gray_stained_glass", Texture.grayGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
