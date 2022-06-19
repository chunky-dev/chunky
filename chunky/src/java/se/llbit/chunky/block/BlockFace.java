package se.llbit.chunky.block;

public enum BlockFace {
  SELF("self", 0, 0, 0),
  /**
   * Facing north, i.e. towards negative Z.
   */
  NORTH("north", 0, 0, -1),
  /**
   * Facing north east, i.e. towards positive X, negative Z.
   */
  NORTH_EAST("north_east", 1, 0, -1),
  /**
   * Facing east, i.e. towards positive X.
   */
  EAST("east", 1, 0, 0),
  /**
   * Facing south east, i.e. towards positive X, positive Z.
   */
  SOUTH_EAST("south_east", 1, 0, 1),
  /**
   * Facing south, i.e. towards positive Z.
   */
  SOUTH("south", NORTH, 0, 0, 1),
  /**
   * Facing south west, i.e. towards negative X, positive Z.
   */
  SOUTH_WEST("south_west", NORTH_EAST, -1, 0, 1),
  /**
   * Facing west, i.e. towards negative X.
   */
  WEST("west", EAST, -1, 0, 0),
  /**
   * Facing north west, i.e. towards negative X, negative Z.
   */
  NORTH_WEST("north_west", SOUTH_EAST, -1, 0, -1),
  /**
   * Facing up, i.e. towards positive Y.
   */
  UP("up", 0, 1, 0),
  /**
   * Facing down, i.e. towards negative Y.
   */
  DOWN("down", UP, 0, -1, 0);

  private final String name;
  private BlockFace oppositeFace;
  public final int rx;
  public final int ry;
  public final int rz;

  BlockFace(String name, int rx, int ry, int rz) {
    this.name = name;
    this.rx = rx;
    this.ry = ry;
    this.rz = rz;
    this.oppositeFace = this; // to make the opposite of SELF be SELF
  }

  BlockFace(String name, BlockFace oppositeFace, int rx, int ry, int rz) {
    this(name, rx, ry, rz);
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
      case "north_east":
        return NORTH_EAST;
      case "east":
        return EAST;
      case "south_east":
        return SOUTH_EAST;
      case "south":
        return SOUTH;
      case "south_west":
        return SOUTH_WEST;
      case "west":
        return WEST;
      case "north_west":
        return NORTH_WEST;
      case "up":
        return UP;
      case "down":
        return DOWN;
      case "self":
        return SELF;
      default:
        throw new IllegalArgumentException("Unknown BlockFace name: " + name);
    }
  }
}
