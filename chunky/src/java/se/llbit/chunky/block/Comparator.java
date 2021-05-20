package se.llbit.chunky.block;

import se.llbit.chunky.model.ComparatorModel;
import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: render locked repeaters.
public class Comparator extends MinecraftBlockTranslucent {
  private final int facing, powered, mode;
  private final String description;

  public Comparator(String facing, String mode, boolean powered) {
    super("comparator", Texture.redstoneRepeaterOn);
    this.description = String.format("facing=%s, mode=%s, powered=%s",
        facing, mode, powered);
    localIntersect = true;
    this.powered = powered ? 1 : 0;
    this.mode = mode.equals("compare") ? 0 : 1;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 0;
        break;
      case "west":
        this.facing = 1;
        break;
      case "east":
        this.facing = 3;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return ComparatorModel.intersect(ray, facing, mode, powered);
  }

  @Override public String description() {
    return description;
  }
}
