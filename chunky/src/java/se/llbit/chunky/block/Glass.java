package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;

public class Glass extends MinecraftBlock {
  public Glass(String name, Texture texture) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = true;
    ior = 1.52f;
  }

  @Override public boolean isSameMaterial(Material other) {
    return other instanceof Glass;
  }
}
