package se.llbit.chunky.block;

import se.llbit.chunky.model.HopperModel;
import se.llbit.chunky.resources.Texture;

public class Hopper extends AbstractModelBlock {
  private final String description;

  public Hopper(String facing) {
    super("hopper", Texture.hopperInside);
    this.description = "facing=" + facing;
    this.model = new HopperModel(facing);
  }

  @Override
  public String description() {
    return description;
  }
}
