package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassMagenta extends MinecraftBlock {
  public StainedGlassMagenta() {
    super("magenta_stained_glass", Texture.magentaGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
