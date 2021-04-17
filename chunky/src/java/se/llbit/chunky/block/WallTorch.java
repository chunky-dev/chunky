package se.llbit.chunky.block;

import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.resources.Texture;

/**
 * A torch attached to a wall.
 */
public class WallTorch extends AbstractModelBlock {

  private final String description;

  public WallTorch(String name, Texture texture, String facing) {
    super(name, texture);
    description = "facing=" + facing;
    solid = false;
    int facingInt;
    switch (facing) {
      default:
      case "north":
        facingInt = 4;
        break;
      case "south":
        facingInt = 3;
        break;
      case "west":
        facingInt = 2;
        break;
      case "east":
        facingInt = 1;
        break;
    }
    model = new TorchModel(texture, facingInt);
  }

  @Override
  public String description() {
    return description;
  }
}
