package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.PistonModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Piston extends MinecraftBlock implements ModelBlock {
  private final PistonModel model;
  private final String description;

  public Piston(String name, boolean sticky, boolean extended, String facing) {
    super(name, Texture.pistonSide);
    this.description = String.format("sticky=%s, extended=%s, facing=%s", sticky, extended, facing);
    localIntersect = true;
    opaque = !extended;
    solid = false;
    int orientation;
    switch (facing) {
      case "down":
        orientation = 0;
        break;
      case "up":
        orientation = 1;
        break;
      default:
      case "north":
        orientation = 2;
        break;
      case "south":
        orientation = 3;
        break;
      case "west":
        orientation = 4;
        break;
      case "east":
        orientation = 5;
        break;
    }
    this.model = new PistonModel(sticky, extended, orientation);
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
