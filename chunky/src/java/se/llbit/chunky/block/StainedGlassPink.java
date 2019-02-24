package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassPink extends MinecraftBlock {
  public StainedGlassPink() {
    super("pink_stained_glass", Texture.pinkGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
