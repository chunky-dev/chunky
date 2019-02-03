package se.llbit.chunky.chunk;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.Water;

public class WaterBlockSpec implements BlockSpec {
  private static final int MAGIC = 0x455943E1;
  private final int data;
  private final boolean isFull;
  private final int level, c0, c1, c2, c3;

  public WaterBlockSpec(int level, int full, int corner0, int corner1, int corner2, int corner3) {
    data = level | ((full&1) << 4)
        | ((corner0&7) << 5)
        | ((corner1&7) << 9)
        | ((corner2&7) << 13)
        | ((corner3&7) << 17);
    this.level = level;
    this.isFull = full == 1;
    c0 = corner0;
    c1 = corner1;
    c2 = corner2;
    c3 = corner3;
  }

  @Override public int hashCode() {
    return MAGIC ^ data;
  }

  @Override public boolean equals(Object obj) {
    return (obj instanceof WaterBlockSpec)
        && ((WaterBlockSpec) obj).data == data;
  }

  @Override public Block toBlock() {
    return new Water(level, isFull, c0, c1, c2, c3);
  }
}
