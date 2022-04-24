package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class RedstoneTorch extends Torch {
  private final boolean lit;

  public RedstoneTorch(boolean lit) {
    super("redstone_torch", lit ? Texture.redstoneTorchOn : Texture.redstoneTorchOff);
    this.lit = lit;
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public String description() {
    return "lit=" + lit;
  }
}
