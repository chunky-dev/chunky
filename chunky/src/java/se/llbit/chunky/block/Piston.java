package se.llbit.chunky.block;

import se.llbit.chunky.model.PistonModel;
import se.llbit.chunky.resources.Texture;

public class Piston extends AbstractModelBlock {

  private final String description;

  public Piston(String name, boolean sticky, boolean extended, String facing) {
    super(name, Texture.pistonSide);
    this.description = String.format("sticky=%s, extended=%s, facing=%s", sticky, extended, facing);
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
  public String description() {
    return description;
  }
}
