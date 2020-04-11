package se.llbit.chunky.block;

import se.llbit.chunky.model.GlassPaneModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.Ray;

public class GlassPane extends MinecraftBlockTranslucent {
  private final String description;
  private final Texture topTexture;
  private final int connections;

  public GlassPane(String name, Texture side, Texture top,
      boolean north, boolean south, boolean east, boolean west) {
    super(name, side);
    topTexture = top;
    localIntersect = true;
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
    this.connections = connections;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return GlassPaneModel.intersect(ray, texture, topTexture, connections);
  }

  @Override public String description() {
    return description;
  }
}
