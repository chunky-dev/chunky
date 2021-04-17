package se.llbit.chunky.block;

import se.llbit.chunky.model.TerracottaModel;
import se.llbit.chunky.resources.Texture;

public class GlazedTerracotta extends AbstractModelBlock {

  private final String description;

  public GlazedTerracotta(String name, Texture texture, String facingString) {
    super(name, texture);
    this.description = "facing=" + facingString;
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2;
        break;
      case "east":
        facing = 3;
        break;
      case "south":
        facing = 0;
        break;
      case "west":
        facing = 1;
        break;
    }
    this.model = new TerracottaModel(texture, facing);
    opaque = true;
  }

  @Override
  public String description() {
    return description;
  }
}
