package se.llbit.chunky.block;

import se.llbit.chunky.model.CocoaPlantModel;
import se.llbit.chunky.resources.Texture;

public class Cocoa extends AbstractModelBlock {

  private final String description;

  public Cocoa(String facingString, int age) {
    super("cocoa", Texture.cocoaPlantLarge);
    description = String.format("facing=%s, age=%d", facingString, age);
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 0;
        break;
      case "west":
        facing = 1;
        break;
      case "east":
        facing = 3;
        break;
    }
    model = new CocoaPlantModel(facing, age);
  }

  @Override
  public String description() {
    return description;
  }
}
