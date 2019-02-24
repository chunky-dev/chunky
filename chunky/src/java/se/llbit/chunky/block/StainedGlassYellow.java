package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassYellow extends MinecraftBlock {
  public StainedGlassYellow() {
    super("yellow_stained_glass", Texture.yellowGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
