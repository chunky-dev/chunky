package se.llbit.chunky.block;

public enum BlockFace {
  NORTH("north"),
  EAST("east"),
  SOUTH("south", NORTH),
  WEST("west", EAST),
  UP("up"),
  DOWN("down", UP);

  private final String name;
  private BlockFace oppositeFace;

  BlockFace(String name) {
    this.name = name;
  }

  BlockFace(String name, BlockFace oppositeFace) {
    this.name = name;
    this.oppositeFace = oppositeFace;
    oppositeFace.oppositeFace = this;
  }

  public String getName() {
    return name;
  }

  public BlockFace getOppositeFace() {
    return oppositeFace;
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
