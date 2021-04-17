package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: render locked repeaters.
public class Repeater extends MinecraftBlockTranslucent implements ModelBlock {
  private final RedstoneRepeaterModel model;
  private final String description;

  public Repeater(int delay, String facingString, boolean powered, boolean locked) {
    super("repeater", Texture.redstoneRepeaterOn);
    this.description = String.format("delay=%d, facing=%s, powered=%s, locked=%s",
        delay, facingString, powered, locked);
    localIntersect = true;
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
    this.model = new RedstoneRepeaterModel(3 & (delay - 1), facing, powered ? 1 : 0, locked ? 1 : 0);
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
