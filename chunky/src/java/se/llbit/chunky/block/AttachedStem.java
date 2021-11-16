package se.llbit.chunky.block;

import se.llbit.chunky.model.AttachedStemModel;
import se.llbit.chunky.resources.Texture;

/**
 * Attached melon or pumpkin stem.
 */
public class AttachedStem extends AbstractModelBlock {

  private final String description;

  public AttachedStem(String name, String facingString) {
    super(name, Texture.stemBent);
    description = "facing=" + facingString;
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2; //0;
        break;
      case "south":
        facing = 3; //1;
        break;
      case "east":
        facing = 1; //2;
        break;
      case "west":
        facing = 0; //3;
        break;
    }
    this.model = new AttachedStemModel(facing);
  }

  @Override
  public String description() {
    return description;
  }
}
