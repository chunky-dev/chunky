package se.llbit.chunky.block;

import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class ShulkerBox extends MinecraftBlockTranslucent {
  private final int facing;
  private final Texture[] texture;
  private final String description;

  public ShulkerBox(String name, Texture side, Texture top, Texture bottom, String facing) {
    super(name, side);
    this.texture = new Texture[] { bottom, top, side };
    this.description = "facing=" + facing;
    localIntersect = true;
    switch (facing) {
      default:
      case "up":
        this.facing = 1;
        break;
      case "down":
        this.facing = 0;
        break;
      case "north":
        this.facing = 2;
        break;
      case "east":
        this.facing = 5;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return DirectionalBlockModel.intersect(ray, texture, facing);
  }

  @Override public String description() {
    return description;
  }
}
