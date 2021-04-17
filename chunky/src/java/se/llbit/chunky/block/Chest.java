package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ChestModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Chest extends MinecraftBlock implements ModelBlock {
  private final ChestModel model;

  private final String description;

  public Chest(String name, String typeString, String facingString, boolean trapped) {
    super(name, trapped ? Texture.trappedChestFront : Texture.chestFront);
    this.description = String.format("type=%s, facing=%s", typeString, facingString);
    localIntersect = true;
    opaque = false;
    int type;
    switch (typeString) {
      default:
      case "single":
        type = 0;
        break;
      case "left":
        type = 1;
        break;
      case "right":
        type = 2;
        break;
    }
    int facing;
    switch (facingString) {
      default:
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
    model = new ChestModel(type, facing, trapped, false);
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
