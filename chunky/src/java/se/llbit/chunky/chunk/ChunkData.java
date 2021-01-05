package se.llbit.chunky.chunk;

import se.llbit.nbt.CompoundTag;
import se.llbit.util.NotNull;

import java.util.Collection;

public interface ChunkData {
  int minY();

  int maxY();

  int getBlockAt(int x, int y, int z);

  void setBlockAt(int x, int y, int z, int block);

  boolean isBlockOnEdge(int x, int y, int z);

  @NotNull Collection<CompoundTag> getTileEntities();

  @NotNull Collection<CompoundTag> getEntities();

  byte getBiomeAt(int x, int y, int z); //TODO: int biomes for modded biome support

  void setBiomeAt(int x, int y, int z, byte biome);

  void clear();
}
