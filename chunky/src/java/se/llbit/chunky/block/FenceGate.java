package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.FenceGateModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class FenceGate extends MinecraftBlockTranslucent implements ModelBlock {
  private final FenceGateModel model;
  private final String description;

  public FenceGate(String name, Texture texture, String facingString, boolean inWall, boolean open) {
    super(name, texture);
    this.description = String.format("facing=%s, in_wall=%s, open=%s",
        facingString, inWall, open);
    solid = false;
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
    this.model = new FenceGateModel(texture, facing, inWall ? 1 : 0,open ? 1 : 0);
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
