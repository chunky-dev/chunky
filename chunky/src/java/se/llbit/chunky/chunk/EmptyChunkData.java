package se.llbit.chunky.chunk;

import se.llbit.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Collection;

public class EmptyChunkData implements ChunkData {
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
    return new ArrayList<>();
  }

  @Override
  public void addTileEntity(CompoundTag tileEntity) { }

  @Override
  public Collection<CompoundTag> getEntities() {
    return new ArrayList<>();
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
  public void clear() { }
}
