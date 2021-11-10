package se.llbit.chunky.block;

import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.resources.Texture;

public class ShulkerBox extends AbstractModelBlock {

  private final String description;

  public ShulkerBox(String name, Texture side, Texture top, Texture bottom, String facing) {
    super(name, side);
    this.description = "facing=" + facing;
    this.model = new DirectionalBlockModel(facing, top, bottom, side);
    opaque = true;
  }

  @Override
  public String description() {
    return description;
  }
}
