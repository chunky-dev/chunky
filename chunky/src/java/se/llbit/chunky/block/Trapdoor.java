package se.llbit.chunky.block;

import se.llbit.chunky.model.TrapdoorModel;
import se.llbit.chunky.resources.Texture;

// TODO: fix rendering/texturing bugs.
public class Trapdoor extends AbstractModelBlock {

  private final String description;

  public Trapdoor(String name, Texture texture,
      String half, String facing, boolean open) {
    super(name, texture);
    this.description = String.format("half=%s, facing=%s, open=%s",
        half, facing, open);
    int state;
    switch (facing) {
      default:
      case "north":
        state = 0;
        break;
      case "south":
        state = 1;
        break;
      case "east":
        state = 3;
        break;
      case "west":
        state = 2;
        break;
    }
    if (open) {
      state |= 4;
    }
    if (half.equals("top")) {
      state |= 8;
    }
    this.model = new TrapdoorModel(texture, state);
  }

  @Override
  public String description() {
    return description;
  }
}
