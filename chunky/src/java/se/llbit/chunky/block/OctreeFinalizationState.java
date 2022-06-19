package se.llbit.chunky.block;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;
import se.llbit.math.Vector3i;

public class OctreeFinalizationState extends FinalizationState {

  private final Octree worldTree;
  private final Octree waterTree;
  private final int yMin;
  private final int yMax;
  private final Vector3i origin;
  private int x;
  private int y;
  private int z;
  private int ox;
  private int oy;
  private int oz;

  public OctreeFinalizationState(Octree worldTree, Octree waterTree,
                                 BlockPalette palette, int yMin, int yMax, Vector3i origin) {
    super(palette);
    this.worldTree = worldTree;
    this.waterTree = waterTree;
    this.yMin = yMin;
    this.yMax = yMax;
    this.origin = origin;
  }

  @Override
  public Material getMaterial() {
    return worldTree.getMaterial(ox, oy, oz, getPalette());
  }

  @Override
  public Material getMaterial(int rx, int ry, int rz) {
    return worldTree
      .getMaterial(ox + rx, oy + ry, oz + rz, getPalette());
  }

  @Override
  public Material getWaterMaterial() {
    return waterTree.getMaterial(ox, oy, oz, getPalette());
  }

  @Override
  public Material getWaterMaterial(int rx, int ry, int rz) {
    return waterTree
      .getMaterial(ox + rx, oy + ry, oz + rz, getPalette());
  }

  @Override
  public void replaceCurrentBlock(int newPaletteId) {
    worldTree.set(newPaletteId, ox, oy, oz);
  }

  @Override
  public void replaceCurrentWaterBlock(int newPaletteId) {
    waterTree.set(newPaletteId, ox, oy, oz);
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
    return yMin;
  }

  @Override
  public int getYMax() {
    return yMax;
  }

  public void setPosition(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    ox = x - origin.x;
    oy = y - origin.y;
    oz = z - origin.z;
  }
}
