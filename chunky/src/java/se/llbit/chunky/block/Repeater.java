package se.llbit.chunky.block;

import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

// TODO: render locked repeaters.
public class Repeater extends MinecraftBlockTranslucent {
  private final int facing, powered, delay, locked;
  private final String description;

  public Repeater(int delay, String facing, boolean powered, boolean locked) {
    super("repeater", Texture.redstoneRepeaterOn);
    this.description = String.format("delay=%d, facing=%s, powered=%s, locked=%s",
        delay, facing, powered, locked);
    localIntersect = true;
    this.delay = 3 & (delay - 1);
    this.powered = powered ? 1 : 0;
    this.locked = locked ? 1 : 0;
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

  public int getFacing() {
    return facing;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return RedstoneRepeaterModel.intersect(ray, delay, facing, powered, locked);
  }

  @Override public String description() {
    return description;
  }
}
