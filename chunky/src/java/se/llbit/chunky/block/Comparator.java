package se.llbit.chunky.block;

import se.llbit.chunky.model.ComparatorModel;
import se.llbit.chunky.resources.Texture;

// TODO: render locked repeaters.
public class Comparator extends AbstractModelBlock {

  private final String description;

  public Comparator(String facingString, String modeString, boolean powered) {
    super("comparator", Texture.redstoneRepeaterOn);
    this.description = String.format("facing=%s, mode=%s, powered=%s",
        facingString, modeString, powered);
    int mode = modeString.equals("compare") ? 0 : 1;
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

    this.model = new ComparatorModel(facing, mode, powered ? 1 : 0);
  }

  @Override
  public String description() {
    return description;
  }
}
