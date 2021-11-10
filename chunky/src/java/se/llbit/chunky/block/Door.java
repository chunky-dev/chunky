package se.llbit.chunky.block;

import se.llbit.chunky.model.DoorModel;
import se.llbit.chunky.resources.Texture;

// TODO: hinge placement is wrong for some variants.
public class Door extends AbstractModelBlock {

  private final String description;
  private final BlockFace facing;
  private final boolean open;
  private final int mirrored;

  public Door(String name, Texture texture, String facingString, String half,
      String hinge, boolean open) {
    super(name, texture);
    this.facing = BlockFace.fromName(facingString);
    this.open = open;
    this.description = String.format("facing=%s, half=%s, hinge=%s, open=%s",
        facingString, half, hinge, open);
    mirrored = hinge.equals("left") ? 0 : 1;
    int direction;
    switch (this.facing) {
      default:
      case NORTH:
        direction = 3;
        break;
      case SOUTH:
        direction = 1;
        break;
      case WEST:
        direction = 2;
        break;
      case EAST:
        direction = 0;
        break;
    }
    int facing;
    if (open && mirrored != 0) {
      facing = (direction + 3) % 4;
    } else if (open) {
      facing = (direction + 1) % 4;
    } else {
      facing = direction;
    }

    model = new DoorModel(texture, mirrored, facing);
  }

  @Override
  public String description() {
    return description;
  }

  public BlockFace getFacing() {
    return facing;
  }

  public String getHinge() {
    return mirrored == 0 ? "left" : "right";
  }

  public boolean isOpen() {
    return open;
  }
}
