package se.llbit.chunky.block;

import se.llbit.chunky.model.BarrelModel;
import se.llbit.chunky.resources.Texture;

public class Barrel extends AbstractModelBlock {

  private final String description;

  public Barrel(String facing, String open) {
    super("barrel", Texture.barrelSide);
    this.description = "facing=" + facing + ", open=" + open;
    this.model = new BarrelModel(facing, open);
    opaque = true;
  }

  @Override
  public String description() {
    return description;
  }
}
