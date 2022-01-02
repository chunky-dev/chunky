package se.llbit.chunky.block;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.Tag;

public abstract class FinalizationState {

  private final BlockPalette palette;

  public FinalizationState(BlockPalette palette) {
    this.palette = palette;
  }

  public abstract Block getBlock();

  public abstract Block getBlock(int rx, int ry, int rz);

  public Block getBlock(BlockFace direction) {
    switch (direction) {
      case NORTH:
        return getBlock(0, 0, -1);
      case EAST:
        return getBlock(1, 0, 0);
      case SOUTH:
        return getBlock(0, 0, 1);
      case WEST:
        return getBlock(-1, 0, 0);
      case UP:
        return getBlock(0, 1, 0);
      case DOWN:
        return getBlock(0, -1, 0);
      default:
        throw new IllegalArgumentException("Invalid direction: " + direction);
    }
  }

  public abstract void replaceCurrentBlock(int newBlock);

  public void replaceCurrentBlock(Tag tag) {
    replaceCurrentBlock(getPalette().put(tag));
  }

  public BlockPalette getPalette() {
    return palette;
  }

  public abstract int getX();

  public abstract int getY();

  public abstract int getZ();

  public abstract int getYMin();

  public abstract int getYMax();
}
