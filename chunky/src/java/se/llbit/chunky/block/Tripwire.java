package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TripwireModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

import static se.llbit.chunky.world.BlockData.CONNECTED_EAST;
import static se.llbit.chunky.world.BlockData.CONNECTED_NORTH;
import static se.llbit.chunky.world.BlockData.CONNECTED_SOUTH;
import static se.llbit.chunky.world.BlockData.CONNECTED_WEST;

public class Tripwire extends MinecraftBlockTranslucent implements ModelBlock {
  private final TripwireModel model;
  private final String description;

  public Tripwire(boolean north, boolean south, boolean east, boolean west) {
    super("tripwire", Texture.tripwire);
    localIntersect = true;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    int connections = 0;
    if (north) {
      connections |= CONNECTED_NORTH;
    }
    if (south) {
      connections |= CONNECTED_SOUTH;
    }
    if (east) {
      connections |= CONNECTED_EAST;
    }
    if (west) {
      connections |= CONNECTED_WEST;
    }
    this.model = new TripwireModel(connections);
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
