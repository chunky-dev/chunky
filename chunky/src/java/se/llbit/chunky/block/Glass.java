package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Glass extends MinecraftBlock {
  public Glass() {
    super("glass", Texture.glass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
