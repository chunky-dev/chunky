package se.llbit.chunky.block;

import se.llbit.chunky.model.PistonExtensionModel;
import se.llbit.chunky.resources.Texture;

public class PistonHead extends AbstractModelBlock {

  private final String description;

  public PistonHead(String name, boolean sticky, String facing) {
    super(name, Texture.pistonSide);
    this.description = String.format("sticky=%s, facing=%s", sticky, facing);
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
    this.model = new PistonExtensionModel(sticky, orientation);
  }

  @Override
  public String description() {
    return description;
  }
}
