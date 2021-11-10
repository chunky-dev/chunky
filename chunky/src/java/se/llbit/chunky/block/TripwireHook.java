package se.llbit.chunky.block;

import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.resources.Texture;

public class TripwireHook extends AbstractModelBlock {

  private final String description;
  private final BlockFace facing;

  public TripwireHook(String facingString, boolean attached, boolean powered) {
    super("tripwire_hook", Texture.tripwire);
    this.description = String
        .format("facing=%s,attached=%s,powered=%s", facingString, attached, powered);
    facing = BlockFace.fromName(facingString);
    int facingVal;
    switch (facing) {
      default:
      case NORTH:
        facingVal = 0;
        break;
      case SOUTH:
        facingVal = 2;
        break;
      case WEST:
        facingVal = 3;
        break;
      case EAST:
        facingVal = 1;
        break;
    }
    this.model = new TripwireHookModel(facingVal, attached, powered);
  }

  @Override
  public String description() {
    return description;
  }

  public BlockFace getFacing() {
      return facing;
  }
}
