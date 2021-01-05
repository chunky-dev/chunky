package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Furnace extends TopBottomOrientedTexturedBlock {

  private final boolean lit;
  private final String description;

  public Furnace(String facing, boolean lit) {
    super("furnace", facing, lit ? Texture.furnaceLitFront : Texture.furnaceUnlitFront,
        Texture.furnaceSide, Texture.furnaceTop);
    this.description = String.format("facing=%s, lit=%s", facing, lit);
    this.lit = lit;
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public String description() {
    return description;
  }
}
