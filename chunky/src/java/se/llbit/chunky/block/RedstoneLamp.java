package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class RedstoneLamp extends MinecraftBlock {
  public final boolean isLit;

  public RedstoneLamp(boolean lit) {
    super("redstone_lamp", lit ? Texture.redstoneLampOn : Texture.redstoneLampOff);
    this.isLit = lit;
  }

  @Override public String description() {
    return "lit=" + isLit;
  }
}
