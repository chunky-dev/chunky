package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassLime extends MinecraftBlock {
  public StainedGlassLime() {
    super("lime_stained_glass", Texture.limeGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
