package se.llbit.chunky.block;

import se.llbit.chunky.model.HopperModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Hopper extends MinecraftBlock {
  private final String description;
  private final int facing;

  public Hopper(String facing) {
    super("hopper", Texture.hopperInside);
    this.description = "facing=" + facing;
    int direction;
    switch (facing) {
      case "down":
        direction = 0;
        break;
      default:
      case "north":
        direction = 2;
        break;
      case "east":
        direction = 5;
        break;
      case "south":
        direction = 3;
        break;
      case "west":
        direction = 4;
        break;
    }
    this.facing = direction;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return HopperModel.intersect(ray, facing);
  }

  @Override public String description() {
    return description;
  }
}
