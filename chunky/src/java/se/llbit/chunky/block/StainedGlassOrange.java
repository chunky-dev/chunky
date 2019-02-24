package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassOrange extends MinecraftBlock {
  public StainedGlassOrange() {
    super("orange_stained_glass", Texture.orangeGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
