package se.llbit.chunky.chunk;

import se.llbit.nbt.CompoundTag;

import java.util.Collection;

public interface ChunkData {
  int getBlockAt(int x, int y, int z);

  void setBlockAt(int x, int y, int z, int block);

  boolean isBlockOnEdge(int x, int y, int z);

  Collection<CompoundTag> getTileEntities();

  Collection<CompoundTag> getEntities();

  byte getBiomeAt(int x, int y, int z);

  void setBiomeAt(int x, int y, int z, byte biome);

  void clear();
}
