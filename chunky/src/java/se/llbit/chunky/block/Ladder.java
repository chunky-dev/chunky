package se.llbit.chunky.block;

import se.llbit.chunky.model.LadderModel;
import se.llbit.chunky.resources.Texture;

public class Ladder extends AbstractModelBlock {

  private final String description;

  public Ladder(String facingString) {
    super("ladder", Texture.ladder);
    this.description = "facing=" + facingString;

    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 3;
        break;
      case "west":
        facing = 0;
        break;
      case "east":
        facing = 1;
        break;
    }
    model = new LadderModel(facing);
  }

  @Override
  public String description() {
    return description;
  }
}
