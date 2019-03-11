package se.llbit.chunky.block;

import se.llbit.chunky.model.FenceModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Fence extends MinecraftBlockTranslucent {
  private final String description;
  private final int connections;

  public Fence(String name, Texture texture,
      boolean north, boolean south, boolean east, boolean west) {
    super(name, texture);
    localIntersect = true;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    int connections = 0;
    if (north) {
      connections |= 1;
    }
    if (south) {
      connections |= 2;
    }
    if (east) {
      connections |= 4;
    }
    if (west) {
      connections |= 8;
    }
    this.connections = connections;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return FenceModel.intersect(ray, texture, connections);
  }

  @Override public String description() {
    return description;
  }
}
