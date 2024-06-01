package se.llbit.chunky.block;

public enum BlockFace {
  /**
   * Facing north, i.e. towards negative Z.
   */
  NORTH("north"),
  /**
   * Facing east, i.e. towards positive X.
   */
  EAST("east"),
  /**
   * Facing south, i.e. towards positive Z.
   */
  SOUTH("south", NORTH),
  /**
   * Facing west, i.e. towards negative X.
   */
  WEST("west", EAST),
  /**
   * Facing up, i.e. towards positive Y.
   */
  UP("up"),
  /**
   * Facing down, i.e. towards negative Y.
   */
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
