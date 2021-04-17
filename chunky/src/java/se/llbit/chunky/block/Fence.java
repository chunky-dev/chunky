package se.llbit.chunky.block;

import se.llbit.chunky.model.FenceModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;

public class Fence extends AbstractModelBlock {

  private final String description;

  public Fence(String name, Texture texture,
      boolean north, boolean south, boolean east, boolean west) {
    super(name, texture);
    solid = false;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    int connections = 0;
    if (north) {
      connections |= BlockData.CONNECTED_NORTH;
    }
    if (south) {
      connections |= BlockData.CONNECTED_SOUTH;
    }
    if (east) {
      connections |= BlockData.CONNECTED_EAST;
    }
    if (west) {
      connections |= BlockData.CONNECTED_WEST;
    }
    this.model = new FenceModel(texture, connections);
  }

  @Override
  public String description() {
    return description;
  }
}
