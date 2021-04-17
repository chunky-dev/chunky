package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.DoorModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: hinge placement is wrong for some variants.
public class Door extends MinecraftBlockTranslucent implements ModelBlock {
  private final String description;
  private final DoorModel model;

  public Door(String name, Texture texture, String facingString, String half,
      String hinge, boolean open) {
    super(name, texture);
    this.description = String.format("facing=%s, half=%s, hinge=%s, open=%s",
        facingString, half, hinge, open);
    localIntersect = true;
    int mirrored = hinge.equals("left") ? 0 : 1;
    int direction;
    switch (facingString) {
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
    int facing;
    if (open && mirrored != 0) {
      facing = (direction + 3) % 4;
    } else if (open) {
      facing = (direction + 1) % 4;
    } else {
      facing = direction;
    }

    this.model = new DoorModel(texture, mirrored, facing);
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
