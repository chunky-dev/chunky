package se.llbit.chunky.block;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.Tag;

public abstract class FinalizationState {

  private final BlockPalette palette;

  public FinalizationState(BlockPalette palette) {
    this.palette = palette;
  }

  /**
   * Get the material at the current position. This may or may not return materials replaced by
   * {@link #replaceCurrentBlock(int)} or {@link #replaceCurrentBlock(Tag)}.
   *
   * @return Material at the current location
   */
  public abstract Material getMaterial();

  /**
   * Get the material at the current position. If rx = ry = rz = 0, this may or may not return materials replaced by
   * {@link #replaceCurrentBlock(int)} or {@link #replaceCurrentBlock(Tag)}.
   *
   * @return Material at the current location
   */
  public abstract Material getMaterial(int rx, int ry, int rz);

  /**
   * Get the material at given direction from the current positon. If direction is SELF, this may or may not return
   * materials replaced by {@link #replaceCurrentBlock(int)} or {@link #replaceCurrentBlock(Tag)}.
   *
   * @return Material at the current location
   */
  public Material getMaterial(BlockFace direction) {
    return getMaterial(direction.rx, direction.ry, direction.rz);
  }

  /**
   * Check if the block at the current position is visible (ie. has any non-opaque blocks surrounding it).
   *
   * @return True if the block is visible, false otherwise
   */
  public boolean isCurrentBlockVisible() {
    return !(getMaterial(BlockFace.NORTH).opaque && getMaterial(BlockFace.SOUTH).opaque &&
      getMaterial(BlockFace.WEST).opaque && getMaterial(BlockFace.EAST).opaque &&
      getMaterial(BlockFace.UP).opaque && getMaterial(BlockFace.DOWN).opaque);
  }

  /**
   * Get the water material at the current position. This may or may not return materials replaced
   * by {@link #replaceCurrentWaterBlock(int)}.
   *
   * @return Water material at the current location
   */
  public abstract Material getWaterMaterial();

  /**
   * Get the water material at the current position. If rx = ry = rz = 0, this may or may not return materials
   * replaced by {@link #replaceCurrentWaterBlock(int)}.
   *
   * @return Water material at the current location
   */
  public abstract Material getWaterMaterial(int rx, int ry, int rz);

  /**
   * Get the material at given direction from the current positon. If direction is SELF, this may or may not return
   * materials replaced by {@link #replaceCurrentWaterBlock(int)}.
   *
   * @return Material at the current location
   */
  public Material getWaterMaterial(BlockFace direction) {
    return getWaterMaterial(direction.rx, direction.ry, direction.rz);
  }

  public abstract void replaceCurrentBlock(int newBlock);

  public void replaceCurrentBlock(Tag tag) {
    replaceCurrentBlock(getPalette().put(tag));
  }

  public abstract void replaceCurrentWaterBlock(int newPaletteId);

  public BlockPalette getPalette() {
    return palette;
  }

  public abstract int getX();

  public abstract int getY();

  public abstract int getZ();

  public abstract int getYMin();

  public abstract int getYMax();
}
