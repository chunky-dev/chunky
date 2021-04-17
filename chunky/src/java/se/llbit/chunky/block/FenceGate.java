package se.llbit.chunky.block;

import se.llbit.chunky.model.FenceGateModel;
import se.llbit.chunky.resources.Texture;

public class FenceGate extends AbstractModelBlock {

  private final String description;

  public FenceGate(String name, Texture texture, String facingString, boolean inWall,
      boolean open) {
    super(name, texture);
    this.description = String.format("facing=%s, in_wall=%s, open=%s",
        facingString, inWall, open);
    solid = false;
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
    this.model = new FenceGateModel(texture, facing, inWall ? 1 : 0, open ? 1 : 0);
  }

  @Override
  public String description() {
    return description;
  }
}
