package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassBlue extends MinecraftBlock {
  public StainedGlassBlue() {
    super("blue_stained_glass", Texture.blueGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
