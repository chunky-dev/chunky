package se.llbit.chunky.block;

import se.llbit.chunky.model.TerracottaModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class GlazedTerracotta extends MinecraftBlock {
  private final int facing;
  private final String description;

  public GlazedTerracotta(String name, Texture texture, String facing) {
    super(name, texture);
    this.description = "facing=" + facing;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "east":
        this.facing = 3;
        break;
      case "south":
        this.facing = 0;
        break;
      case "west":
        this.facing = 1;
        break;
    }
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return TerracottaModel.intersect(ray, texture, facing);
  }

  @Override public String description() {
    return description;
  }
}
