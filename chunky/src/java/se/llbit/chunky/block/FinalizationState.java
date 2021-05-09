package se.llbit.chunky.block;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.Tag;

public abstract class FinalizationState {

  private final BlockPalette palette;

  public FinalizationState(BlockPalette palette) {
    this.palette = palette;
  }

  public abstract Material getMaterial();

  public abstract Material getMaterial(int rx, int ry, int rz);

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
