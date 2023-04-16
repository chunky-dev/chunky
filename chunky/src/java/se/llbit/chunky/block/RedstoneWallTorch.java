package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class RedstoneWallTorch extends WallTorch {
  private final boolean lit;

  public RedstoneWallTorch(String facing, boolean lit) {
    super("redstone_wall_torch", lit ? Texture.redstoneTorchOn : Texture.redstoneTorchOff, facing);
    this.lit = lit;
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public String description() {
    return "facing=" + facing + ", lit=" + lit;
  }
}
