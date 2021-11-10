package se.llbit.chunky.block;

import se.llbit.chunky.model.TurtleEggModel;
import se.llbit.chunky.resources.Texture;

public class TurtleEgg extends AbstractModelBlock {

  private final String description;

  public TurtleEgg(int eggs, int hatch) {
    super("turtle_egg", Texture.turtleEgg);
    this.description = String.format("eggs=%d, hatch=%d", eggs, hatch);
    this.model = new TurtleEggModel(eggs, hatch);
  }

  @Override
  public String description() {
    return description;
  }
}
