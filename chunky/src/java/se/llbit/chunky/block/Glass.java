package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;

public class Glass extends MinecraftBlockTranslucent {
  public Glass(String name, Texture texture) {
    super(name, texture);
    ior = 1.52f;
  }

  @Override
  public boolean isSameMaterial(Material other) {
    return other instanceof Glass && other.name.equals(this.name); // same name means same color
  }
}
