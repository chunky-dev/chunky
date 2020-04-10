package se.llbit.chunky.block;

import se.llbit.chunky.model.LadderModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Ladder extends MinecraftBlockTranslucent {
  private final int facing;
  private final String description;

  public Ladder(String facing) {
    super("ladder", Texture.ladder);
    this.description = "facing=" + facing;
    localIntersect = true;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 0;
        break;
      case "east":
        this.facing = 1;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return LadderModel.intersect(ray, facing);
  }

  @Override public String description() {
    return description;
  }
}
