package se.llbit.chunky.block;

import se.llbit.chunky.model.DoorModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: hinge placement is wrong for some variants.
public class Door extends MinecraftBlockTranslucent {
  private final int orientation, mirrored;
  private final String description;
  private final String facing;
  private final boolean open;

  public Door(String name, Texture texture, String facing, String half,
      String hinge, boolean open) {
    super(name, texture);
    this.facing = facing;
    this.open = open;
    this.description = String.format("facing=%s, half=%s, hinge=%s, open=%s",
        facing, half, hinge, open);
    localIntersect = true;
    this.mirrored = hinge.equals("left") ? 0 : 1;
    int direction;
    switch (facing) {
      default:
      case "north":
        direction = 3;
        break;
      case "south":
        direction = 1;
        break;
      case "west":
        direction = 2;
        break;
      case "east":
        direction = 0;
        break;
    }
    if (open && mirrored != 0) {
      this.orientation = (direction + 3) % 4;
    } else if (open) {
      this.orientation = (direction + 1) % 4;
    } else {
      this.orientation = direction;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return DoorModel.intersect(ray, texture, mirrored, orientation);
  }

  @Override public String description() {
    //return String.format("mirrored=%s, facing=%s", mirrored, facing);
    return description;
  }

  public String getFacing() {
    return facing;
  }

  public String getHinge() {
    return mirrored == 0 ? "left" : "right";
  }

  public boolean isOpen() {
    return open;
  }
}
