package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassCyan extends MinecraftBlock {
  public StainedGlassCyan() {
    super("cyan_stained_glass", Texture.cyanGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
