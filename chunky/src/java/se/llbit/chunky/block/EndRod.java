package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.EndRodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EndRod extends MinecraftBlock implements ModelBlock {
  private final EndRodModel model;
  private final String description;

  public EndRod(String facingString) {
    super("end_rod", Texture.endRod);
    this.description = "facing=" + facingString;
    localIntersect = true;
    opaque = false;
    int facing;
    switch (facingString) {
      case "down":
        facing = 0;
        break;
      default:
      case "up":
        facing = 1;
        break;
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 3;
        break;
      case "west":
        facing = 4;
        break;
      case "east":
        facing = 5;
        break;
    }
    model = new EndRodModel(facing);
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
