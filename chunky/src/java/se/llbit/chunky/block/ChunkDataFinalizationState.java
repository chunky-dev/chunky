package se.llbit.chunky.block;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.world.Material;

public abstract class ChunkDataFinalizationState extends FinalizationState {
  private  ChunkData chunkData;
  private final int yMin;
  private final int yMax;
  protected int x;
  protected int y;
  protected int z;

  public ChunkDataFinalizationState(ChunkData chunkData, BlockPalette palette, int yMin, int yMax) {
    super(palette);
    this.chunkData = chunkData;
    this.yMin = yMin;
    this.yMax = yMax;
  }

  public void setChunkData(ChunkData chunkData) {
    this.chunkData = chunkData;
  }

  @Override
  public Material getMaterial() {
    return getPalette().get(chunkData.getBlockAt(x, y, z));
  }

  @Override
  public Material getMaterial(int rx, int ry, int rz) {
    return getPalette().get(chunkData.getBlockAt(x + rx, y + ry, z + rz));
  }

  @Override
  public Material getWaterMaterial() {
    Material material = getMaterial();
    if (material.isWater()) {
      return material;
    }
    return getPalette().get(getPalette().airId);
  }

  @Override
  public Material getWaterMaterial(int rx, int ry, int rz) {
    Material material = getMaterial(rx, ry, rz);
    if (material.isWater()) {
      return material;
    }
    return getPalette().get(getPalette().airId);
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
  }
}
