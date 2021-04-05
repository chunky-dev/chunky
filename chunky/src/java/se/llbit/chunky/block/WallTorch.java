package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * A torch attached to a wall.
 */
public class WallTorch extends MinecraftBlock implements ModelBlock {
  private final String description;
  private final int facing;
  private final BlockModel model;

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
    model = new TorchModel(texture, this.facing);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
