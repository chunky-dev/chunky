package se.llbit.chunky.block;

import se.llbit.chunky.model.FenceGateModel;
import se.llbit.chunky.model.FenceModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.Ray;

public class FenceGate extends MinecraftBlockTranslucent {
  private final String description;
  private final int facing, inWall, open;

  public FenceGate(String name, Texture texture,
      String facing, boolean inWall, boolean open) {
    super(name, texture);
    this.description = String.format("facing=%s, in_wall=%s, open=%s",
        facing, inWall, open);
    localIntersect = true;
    this.inWall = inWall ? 1 : 0;
    this.open = open ? 1 : 0;
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
    return FenceGateModel.intersect(ray, texture, facing, inWall, open);
  }

  @Override public String description() {
    return description;
  }
}
