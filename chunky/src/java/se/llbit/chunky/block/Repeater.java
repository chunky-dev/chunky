package se.llbit.chunky.block;

import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.resources.Texture;

// TODO: render locked repeaters.
public class Repeater extends AbstractModelBlock {

  private final String description;

  public Repeater(int delay, String facingString, boolean powered, boolean locked) {
    super("repeater", Texture.redstoneRepeaterOn);
    this.description = String.format("delay=%d, facing=%s, powered=%s, locked=%s",
        delay, facingString, powered, locked);
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
    this.model = new RedstoneRepeaterModel(3 & (delay - 1), facing, powered ? 1 : 0,
        locked ? 1 : 0);
  }

  @Override
  public String description() {
    return description;
  }
}
