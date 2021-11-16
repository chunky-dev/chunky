package se.llbit.chunky.block;

import se.llbit.chunky.model.WallModel;
import se.llbit.chunky.resources.Texture;

public class Wall extends AbstractModelBlock {

  private final String description;

  public Wall(String name, Texture texture, String north, String south, String east, String west,
      boolean up) {
    super(name, texture);
    this.description = String
        .format("north=%s, south=%s, east=%s, west=%s, up=%s", north, south, east, west, up);
    this.model = new WallModel(texture,
        new int[]{getConnection(north), getConnection(east), getConnection(south),
            getConnection(west)}, up);
  }

  @Override
  public String description() {
    return description;
  }

  private static int getConnection(String state) {
    switch (state) {
      case "true": // < 20w06a
      case "low": // >= 20w06a
        return 1;
      case "tall": // >= 20w06a
        return 2;
      default:
        return 0;
    }
  }
}
