package se.llbit.chunky.block;

import se.llbit.chunky.model.AnvilModel;
import se.llbit.chunky.resources.Texture;

public class Anvil extends AbstractModelBlock {

  private final int facing;
  private final int damage;

  public Anvil(String name, String facing, int damage) {
    super(name, Texture.anvilSide);
    this.damage = damage;
    switch (facing) {
      default:
      case "north":
      case "south":
        this.facing = 0;
        break;
      case "east":
      case "west":
        this.facing = 1;
        break;
    }
    this.model = new AnvilModel(this.facing, this.damage);
  }

  @Override
  public String description() {
    return "damage=" + damage;
  }
}
