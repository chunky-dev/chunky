package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassBrown extends MinecraftBlock {
  public StainedGlassBrown() {
    super("brown_stained_glass", Texture.brownGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
