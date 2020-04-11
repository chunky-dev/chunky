package se.llbit.chunky.block;

import se.llbit.chunky.model.BedModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Bed extends MinecraftBlock {
  private final int head, facing;
  private final String description;

  public Bed(String name, Texture texture, String part, String facing) {
    super(name, texture);
    this.description = String.format("part=%s, facing=%s", part, facing);
    this.head = part.equals("head") ? 1 : 0;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "east":
        this.facing = 3;
        break;
      case "south":
        this.facing = 0;
        break;
      case "west":
        this.facing = 1;
        break;
    }
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return BedModel.intersect(ray, texture, head, facing);
  }

  @Override public String description() {
    return description;
  }
}
