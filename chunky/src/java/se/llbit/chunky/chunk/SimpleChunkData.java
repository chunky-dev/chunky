package se.llbit.chunky.chunk;

import se.llbit.chunky.world.Chunk;
import se.llbit.nbt.CompoundTag;
import se.llbit.util.NotNull;

import java.util.ArrayList;
import java.util.Collection;

import static se.llbit.chunky.world.Chunk.*;

public class SimpleChunkData implements ChunkData {

  private int[] blocks;
  public byte[] biomes;
  public Collection<CompoundTag> tileEntities;
  public Collection<CompoundTag> entities;

  public SimpleChunkData() {
    blocks = new int[X_MAX * Y_MAX * Z_MAX];
    biomes = new byte[X_MAX * Z_MAX];
  }

  @Override
  public int minY() {
    return 0;
  }

  @Override
  public int maxY() {
    return 256;
  }

  @Override
  public int getBlockAt(int x, int y, int z) {
    if(y < 0 || y > 255) {
      return 0;
    }
    return blocks[chunkIndex(x & (X_MAX - 1), y, z & (Z_MAX - 1))];
  }

  @Override
  public void setBlockAt(int x, int y, int z, int block) {
    if(block == 0)
      return;
    if(y < 0 || y > 255) {
      return;
    }
    blocks[chunkIndex(x & (X_MAX - 1), y, z & (Z_MAX - 1))] = block;
  }

  @Override
  public boolean isBlockOnEdge(int x, int y, int z) {
    return false;
  }

  @NotNull @Override
  public Collection<CompoundTag> getTileEntities() {
    if(tileEntities == null) {
      tileEntities = new ArrayList<>();
    }
    return tileEntities;
  }

  @NotNull @Override
  public Collection<CompoundTag> getEntities() {
    if(entities == null) {
      entities = new ArrayList<>();
    }
    return entities;
  }

  @Override
  public byte getBiomeAt(int x, int y, int z) {
    return biomes[chunkXZIndex(x, z)];
  }

  @Override
  public void setBiomeAt(int x, int y, int z, byte biome) {
    biomes[chunkXZIndex(x, z)] = biome;
  }

  @Override
  public void clear() {
    blocks = new int[X_MAX * Y_MAX * Z_MAX];
    biomes = new byte[X_MAX * Chunk.Z_MAX];
    tileEntities.clear();
    entities.clear();
  }
}
