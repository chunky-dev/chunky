package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.VineModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.Ray;

public class Vine extends MinecraftBlockTranslucent implements ModelBlock {
  private final VineModel model;
  private final String description;

  public Vine(boolean north, boolean south, boolean east, boolean west,
      boolean up) {
    super("vine", Texture.vines);
    localIntersect = true;
    solid = false;
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
    if (up) {
      connections |= BlockData.CONNECTED_ABOVE;
    }
    if (connections == 0) {
      // If no side is true, render on south side.
      connections = BlockData.CONNECTED_SOUTH;
    }
    this.model = new VineModel(connections);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
