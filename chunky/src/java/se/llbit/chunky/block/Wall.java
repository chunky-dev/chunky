package se.llbit.chunky.block;

import se.llbit.chunky.model.StoneWallModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.Ray;

public class Wall extends MinecraftBlockTranslucent {
  private final String description;
  private final int connections, up;

  public Wall(String name, Texture texture,
      boolean north, boolean south, boolean east, boolean west, boolean up) {
    super(name, texture);
    localIntersect = true;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s, up=%s",
        north, south, east, west, up);
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
    this.up = up ? 1 : 0;
    this.connections = connections;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return StoneWallModel.intersect(ray, texture, connections, up);
  }

  @Override public String description() {
    return description;
  }
}
