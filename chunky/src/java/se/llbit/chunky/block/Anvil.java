package se.llbit.chunky.block;

import se.llbit.chunky.model.AnvilModel;
import se.llbit.chunky.resources.Texture;

public class Anvil extends AbstractModelBlock {

  private final String description;

  public Anvil(String name, String facing, int damage) {
    super(name, Texture.anvilSide);
    int facing1;
    switch (facing) {
      default:
      case "north":
      case "south":
        facing1 = 0;
        break;
      case "east":
      case "west":
        facing1 = 1;
        break;
    }
    this.model = new AnvilModel(facing1, damage);

    this.description = "damage=" + damage;
  }

  @Override
  public String description() {
    return description;
  }
}
