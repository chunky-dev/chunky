package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Barrel extends OrientedTexturedBlock {

  private final String description;

  public Barrel(String facing, boolean open) {
    super("barrel", facing, Texture.barrelSide, open ? Texture.barrelOpen : Texture.barrelTop,
        Texture.barrelBottom);
    this.description = "facing=" + facing + ", open=" + open;
  }

  @Override
  public String description() {
    return description;
  }
}
