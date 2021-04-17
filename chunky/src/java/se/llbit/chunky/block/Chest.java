package se.llbit.chunky.block;

import se.llbit.chunky.model.ChestModel;
import se.llbit.chunky.resources.Texture;

public class Chest extends AbstractModelBlock {

  private final String description;

  public Chest(String name, String typeString, String facingString, boolean trapped) {
    super(name, trapped ? Texture.trappedChestFront : Texture.chestFront);
    this.description = String.format("type=%s, facing=%s", typeString, facingString);
    int type;
    switch (typeString) {
      default:
      case "single":
        type = 0;
        break;
      case "left":
        type = 1;
        break;
      case "right":
        type = 2;
        break;
    }
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
    model = new ChestModel(type, facing, trapped, false);
  }

  @Override
  public String description() {
    return description;
  }
}
