package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassRed extends MinecraftBlock {
  public StainedGlassRed() {
    super("red_stained_glass", Texture.redGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
