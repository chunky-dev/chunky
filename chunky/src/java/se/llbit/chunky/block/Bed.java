package se.llbit.chunky.block;

import se.llbit.chunky.model.BedModel;
import se.llbit.chunky.resources.Texture;

public class Bed extends AbstractModelBlock {

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
    solid = false;
  }

  @Override
  public String description() {
    return description;
  }
}
