package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.LadderModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Ladder extends MinecraftBlockTranslucent implements ModelBlock {
  private final LadderModel model;
  private final String description;

  public Ladder(String facingString) {
    super("ladder", Texture.ladder);
    this.description = "facing=" + facingString;
    localIntersect = true;

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
        facing = 0;
        break;
      case "east":
        facing = 1;
        break;
    }
    model = new LadderModel(facing);
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
