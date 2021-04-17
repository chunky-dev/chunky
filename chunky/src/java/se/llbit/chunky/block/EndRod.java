package se.llbit.chunky.block;

import se.llbit.chunky.model.EndRodModel;
import se.llbit.chunky.resources.Texture;

public class EndRod extends AbstractModelBlock {

  private final String description;

  public EndRod(String facingString) {
    super("end_rod", Texture.endRod);
    this.description = "facing=" + facingString;
    int facing;
    switch (facingString) {
      case "down":
        facing = 0;
        break;
      default:
      case "up":
        facing = 1;
        break;
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 3;
        break;
      case "west":
        facing = 4;
        break;
      case "east":
        facing = 5;
        break;
    }
    model = new EndRodModel(facing);
  }

  @Override
  public String description() {
    return description;
  }
}
