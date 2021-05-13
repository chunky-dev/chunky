package se.llbit.chunky.block;

public enum BlockFace {
  NORTH("north"),
  EAST("east"),
  SOUTH("south"),
  WEST("west"),
  UP("up"),
  DOWN("down");

  private final String name;

  BlockFace(String name) {
    this.name = name;
  }

  public BlockFace getOppositeFace() {
    switch (this) {
      case NORTH:
        return SOUTH;
      case EAST:
        return WEST;
      case SOUTH:
        return NORTH;
      case WEST:
        return EAST;
      case UP:
        return DOWN;
      case DOWN:
        return UP;
      default:
        throw new IllegalStateException("Unexpected BlockFace: " + this.name);
    }
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  public static BlockFace fromName(String name) {
    switch (name) {
      case "north":
        return NORTH;
      case "east":
        return EAST;
      case "south":
        return SOUTH;
      case "west":
        return WEST;
      case "up":
        return UP;
      case "down":
        return DOWN;
      default:
        throw new IllegalArgumentException("Unknown BlockFace name: " + name);
    }
  }
}
