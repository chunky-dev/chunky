package se.llbit.chunky.block;

import se.llbit.chunky.model.PistonModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Piston extends MinecraftBlock {
  private final int isSticky;
  private final int isExtended;
  private final String description;
  private final int facing;

  public Piston(String name, boolean sticky, boolean extended, String facing) {
    super(name, Texture.pistonSide);
    this.description = String.format("sticky=%s, extended=%s, facing=%s", sticky, extended, facing);
    this.isSticky = sticky ? 1 : 0;
    this.isExtended = extended ? 1 : 0;
    localIntersect = true;
    opaque = false;
    solid = false;
    switch (facing) {
      case "down":
        this.facing = 0;
        break;
      case "up":
        this.facing = 1;
        break;
      default:
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
    return PistonModel.intersect(ray, isSticky, isExtended, facing);
  }

  @Override public String description() {
    return description;
  }
}
