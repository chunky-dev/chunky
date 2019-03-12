package se.llbit.chunky.block;

import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class TripwireHook extends MinecraftBlockTranslucent {
  private final String description;
  private final int facing, powered;

  public TripwireHook(String facing, boolean powered) {
    super("tripwire", Texture.tripwire);
    localIntersect = true;
    this.description = String.format("facing=%s, powered=%s", facing, powered);
    this.powered = powered ? 1 : 0;
    switch (facing) {
      default:
      case "north":
        this.facing = 0;
        break;
      case "south":
        this.facing = 2; //1;
        break;
      case "west":
        this.facing = 1; //2;
        break;
      case "east":
        this.facing = 3; //3;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TripwireHookModel.intersect(ray, facing, powered);
  }

  @Override public String description() {
    return description;
  }
}
