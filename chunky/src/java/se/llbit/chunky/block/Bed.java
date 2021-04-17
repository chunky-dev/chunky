package se.llbit.chunky.block;

import se.llbit.chunky.model.BedModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Bed extends MinecraftBlock implements ModelBlock{
  private final BedModel model;
  private final String description;

  public Bed(String name, Texture texture, String part, String facing) {
    super(name, texture);
    this.description = String.format("part=%s, facing=%s", part, facing);
    boolean head = part.equals("head");
    int direction;
    switch (facing) {
      default:
      case "north":
        direction = 2;
        break;
      case "east":
        direction = 3;
        break;
      case "south":
        direction = 0;
        break;
      case "west":
        direction = 1;
        break;
    }
    model = new BedModel(head, direction, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
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
