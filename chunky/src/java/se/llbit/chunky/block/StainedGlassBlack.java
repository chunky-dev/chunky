package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassBlack extends MinecraftBlock {
  public StainedGlassBlack() {
    super("black_stained_glass", Texture.blackGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
