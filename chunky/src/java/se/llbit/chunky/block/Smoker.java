package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Smoker extends TopBottomOrientedTexturedBlock {

  private final boolean isLit;
  private final String description;

  public Smoker(String facing, boolean lit) {
    super(
            "smoker",
            facing,
            lit ? Texture.smokerFrontOn : Texture.smokerFront,
            Texture.smokerSide,
            Texture.smokerTop,
            Texture.smokerBottom);
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
