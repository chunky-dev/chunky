package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ComparatorModel;
import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: render locked repeaters.
public class Comparator extends MinecraftBlockTranslucent implements ModelBlock {
  private final String description;
  private final ComparatorModel model;

  public Comparator(String facingString, String modeString, boolean powered) {
    super("repeater", Texture.redstoneRepeaterOn);
    this.description = String.format("facing=%s, mode=%s, powered=%s",
        facingString, modeString, powered);
    localIntersect = true;
    int mode = modeString.equals("compare") ? 0 : 1;
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 0;
        break;
      case "west":
        facing = 1;
        break;
      case "east":
        facing = 3;
        break;
    }

    this.model = new ComparatorModel(facing, mode, powered ? 1 : 0);
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
