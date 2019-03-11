package se.llbit.chunky.block;

import se.llbit.chunky.model.EndRodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EndRod extends MinecraftBlock {
  private final int facing;
  private final String description;

  public EndRod(String facing) {
    super("end_rod", Texture.dispenserFront);
    this.description = "facing=" + facing;
    localIntersect = true;
    opaque = false;
    switch (facing) {
      case "down":
        this.facing = 0;
        break;
      default:
      case "up":
        this.facing = 1;
        break;
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
      case "east":
        this.facing = 5;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return EndRodModel.intersect(ray, facing);
  }

  @Override public String description() {
    return description;
  }
}
