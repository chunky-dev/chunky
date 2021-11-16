package se.llbit.chunky.block;

import se.llbit.chunky.model.LeverModel;
import se.llbit.chunky.resources.Texture;

public class Lever extends AbstractModelBlock {

  private final String description;

  public Lever(String face, String facing, boolean powered) {
    super("lever", Texture.lever);
    this.description = String.format("face=%s, facing=%s, powered=%s",
        face, facing, powered);
    int activated = powered ? 1 : 0;
    int position;
    switch (face) {
      case "ceiling":
        switch (facing) {
          default:
          case "north":
            position = 7;
            break;
          case "south":
            activated ^= 1;
            position = 7;
            break;
          case "west":
            position = 0;
            break;
          case "east":
            activated ^= 1;
            position = 0;
            break;
        }
        break;
      case "wall":
        switch (facing) {
          default:
          case "north":
            position = 4;
            break;
          case "south":
            position = 3;
            break;
          case "west":
            position = 2;
            break;
          case "east":
            position = 1;
            break;
        }
        break;
      default:
      case "floor":
        switch (facing) {
          default:
          case "north":
            position = 5;
            break;
          case "south":
            activated ^= 1;
            position = 5;
            break;
          case "west":
            position = 6;
            break;
          case "east":
            activated ^= 1;
            position = 6;
            break;
        }
        break;
    }
    this.model = new LeverModel(position, activated);
  }

  @Override
  public String description() {
    return description;
  }
}
