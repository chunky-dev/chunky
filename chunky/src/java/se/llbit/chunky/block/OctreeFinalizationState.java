package se.llbit.chunky.block;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;

public class OctreeFinalizationState extends FinalizationState {

  private final Octree worldTree;
  private final Octree waterTree;
  private int x;
  private int y;
  private int z;

  public OctreeFinalizationState(Octree worldTree, Octree waterTree,
      BlockPalette palette) {
    super(palette);
    this.worldTree = worldTree;
    this.waterTree = waterTree;
  }

  @Override
  public Material getMaterial() {
    return worldTree.getMaterial(x, y, z, getPalette());
  }

  @Override
  public Material getMaterial(int rx, int ry, int rz) {
    return worldTree
        .getMaterial(x + rx, y + ry, z + rz, getPalette());
  }

  @Override
  public void replaceCurrentBlock(int newPaletteId) {
    worldTree.set(newPaletteId, x, y, z);
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public int getZ() {
    return z;
  }

  @Override
  public int getYMin() {
    return 0; // TODO
  }

  @Override
  public int getYMax() {
    return 255; // TODO
  }

  public void setPosition(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
