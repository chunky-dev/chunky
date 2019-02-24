package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassGreen extends MinecraftBlock {
  public StainedGlassGreen() {
    super("green_stained_glass", Texture.greenGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
