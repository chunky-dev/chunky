package se.llbit.chunky.block;

import se.llbit.chunky.model.GlowLichenModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;

public class GlowLichen extends AbstractModelBlock {

  private final String description;

  public GlowLichen(boolean north, boolean south, boolean east, boolean west, boolean up,
      boolean down) {
    super("glow_lichen", Texture.glowLichen);
    this.description = String.format("north=%s, south=%s, east=%s, west=%s, up=%s, down=%s",
        north, south, east, west, up, down);
    solid = false;

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
    if (up) {
      connections |= BlockData.CONNECTED_ABOVE;
    }
    if (down) {
      connections |= BlockData.CONNECTED_BELOW;
    }
    this.model = new GlowLichenModel(connections);
  }

  @Override
  public String description() {
    return description;
  }
}
