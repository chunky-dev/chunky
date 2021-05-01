package se.llbit.chunky.chunk;

import java.util.Collection;
import java.util.Collections;
import se.llbit.nbt.CompoundTag;

public class EmptyChunkData implements ChunkData {

  /**
   * Singleton instance.
   */
  public static final EmptyChunkData INSTANCE = new EmptyChunkData();

  private EmptyChunkData(){}

  @Override
  public int minY() {
    return 0;
  }

  @Override
  public int maxY() {
    return 0;
  }

  @Override
  public int getBlockAt(int x, int y, int z) {
    return 0;
  }

  @Override
  public void setBlockAt(int x, int y, int z, int block) { }

  @Override
  public boolean isBlockOnEdge(int x, int y, int z) {
    return false;
  }

  @Override
  public Collection<CompoundTag> getTileEntities() {
    return Collections.emptyList();
  }

  @Override
  public void addTileEntity(CompoundTag tileEntity) { }

  @Override
  public Collection<CompoundTag> getEntities() {
    return Collections.emptyList();
  }

  @Override
  public void addEntity(CompoundTag entity) { }

  @Override
  public byte getBiomeAt(int x, int y, int z) {
    return 0;
  }

  @Override
  public void setBiomeAt(int x, int y, int z, byte biome) { }

  @Override
  public void clear() {
    throw new IllegalStateException("EmptyChunkData may not be re-used, this is a bug. Please report it!");
  }

  @Override
  public boolean isEmpty() {
    return true;
  }
}
