package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ChestModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EnderChest extends MinecraftBlock implements ModelBlock {

  private final ChestModel model;

  private final String description;

  public EnderChest(String facingString) {
    super("chest", Texture.chestFront);
    this.description = "facing=" + facingString;
    localIntersect = true;
    opaque = false;
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
    model = new ChestModel(0, facing, false, true);
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
