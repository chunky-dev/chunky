package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StainedGlassLightBlue extends MinecraftBlock {
  public StainedGlassLightBlue() {
    super("light_blue_stained_glass", Texture.lightBlueGlass);
    localIntersect = true;
    ior = 1.52f;
  }

  /*@Override public boolean isSameMaterial(Material other) {
    return other.isGlass();
  }*/
}
