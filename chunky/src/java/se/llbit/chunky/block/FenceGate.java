package se.llbit.chunky.block;

import se.llbit.chunky.model.FenceGateModel;
import se.llbit.chunky.resources.Texture;

public class FenceGate extends AbstractModelBlock {

  private final String description;
  private final BlockFace facing;

  public FenceGate(String name, Texture texture, String facingString, boolean inWall,
      boolean open) {
    super(name, texture);
    this.description = String.format("facing=%s, in_wall=%s, open=%s",
        facingString, inWall, open);
    solid = false;

    facing = BlockFace.fromName(facingString);
    int facingVal;
    switch (facing) {
      default:
      case NORTH:
        facingVal = 2;
        break;
      case SOUTH:
        facingVal = 0;
        break;
      case WEST:
        facingVal = 1;
        break;
      case EAST:
        facingVal = 3;
        break;
    }
    this.model = new FenceGateModel(texture, facingVal, inWall ? 1 : 0, open ? 1 : 0);
  }

  @Override
  public String description() {
    return description;
  }

  public BlockFace getFacing() {
    return facing;
  }
}
