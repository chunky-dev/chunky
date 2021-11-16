package se.llbit.chunky.block;

import se.llbit.chunky.model.StonecutterModel;
import se.llbit.chunky.resources.Texture;

public class Stonecutter extends AbstractModelBlock {

  private final String facing;

  public Stonecutter(String facing) {
    super("stonecutter", Texture.stonecutterSide);
    this.facing = facing;
    this.model = new StonecutterModel(facing);
  }

  @Override
  public String description() {
    return "facing=" + facing;
  }
}
