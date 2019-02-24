package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassLightGray extends MinecraftBlock {
  public StainedGlassLightGray() {
    super("light_gray_stained_glass", Texture.lightGrayGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
