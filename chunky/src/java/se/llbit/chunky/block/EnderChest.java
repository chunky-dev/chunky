package se.llbit.chunky.block;

import se.llbit.chunky.model.ChestModel;
import se.llbit.chunky.resources.Texture;

public class EnderChest extends AbstractModelBlock {

  private final String description;

  public EnderChest(String facingString) {
    super("chest", Texture.chestFront);
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
        facing = 4;
        break;
      case "east":
        facing = 5;
        break;
    }
    model = new ChestModel(0, facing, false, true);
  }

  @Override
  public String description() {
    return description;
  }
}
