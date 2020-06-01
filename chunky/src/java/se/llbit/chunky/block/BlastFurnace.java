package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class BlastFurnace extends TopBottomOrientedTexturedBlock {

  private final boolean isLit;
  private final String description;

  public BlastFurnace(String facing, boolean lit) {
    super(
            "blast_furnace",
            facing,
            lit ? Texture.blastFurnaceFrontOn : Texture.blastFurnaceFront,
            Texture.blastFurnaceSide,
            Texture.blastFurnaceTop);
    this.description = String.format("facing=%s, lit=%s", facing, lit);
    isLit = lit;
  }

  public boolean isLit() {
    return isLit;
  }

  @Override public String description() {
    return description;
  }
}
