package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TerracottaModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class GlazedTerracotta extends MinecraftBlock implements ModelBlock {
  private final TerracottaModel model;
  private final String description;

  public GlazedTerracotta(String name, Texture texture, String facingString) {
    super(name, texture);
    this.description = "facing=" + facingString;
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2;
        break;
      case "east":
        facing = 3;
        break;
      case "south":
        facing = 0;
        break;
      case "west":
        facing = 1;
        break;
    }
    this.model = new TerracottaModel(texture, facing);
    localIntersect = true;
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
