package se.llbit.chunky.block;

import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * A torch attached to a wall.
 */
public class WallTorch extends MinecraftBlock {
  private final String description;
  private final int facing;

  public WallTorch(String name, Texture texture, String facing) {
    super(name, texture);
    description = "facing=" + facing;
    localIntersect = true;
    opaque = false;
    solid = false;
    switch (facing) {
      default:
      case "north":
        this.facing = 4;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 2;
        break;
      case "east":
        this.facing = 1;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TorchModel.intersect(ray, texture, facing);
  }

  @Override public String description() {
    return description;
  }
}
