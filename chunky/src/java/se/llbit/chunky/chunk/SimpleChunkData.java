package se.llbit.chunky.chunk;

import se.llbit.chunky.chunk.biome.BiomeData;
import se.llbit.chunky.world.Chunk;
import se.llbit.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static se.llbit.chunky.world.Chunk.*;

/**
 * A simple Chunk Data implementation
 * Blocks from 0-255
 * 2d Biomes only
 */
public class SimpleChunkData implements ChunkData {
  /** These final fields are never written to, and are instead used for quickly setting existing blocks/biomes arrays
   to all zeros, in {@link SimpleChunkData#clear()} */
  private static final int[] emptyBlocks = new int[X_MAX * Y_MAX * Z_MAX];

  private final int[] blocks;
  private BiomeData biomeData;
  private final Collection<CompoundTag> tileEntities;
  private final Collection<CompoundTag> entities;
  private boolean isEmpty;

  public SimpleChunkData() {
    blocks = new int[X_MAX * Y_MAX * Z_MAX];
    tileEntities = new ArrayList<>();
    entities = new ArrayList<>();
    isEmpty = true;
  }

  @Override public int minY() {
    return 0;
  }

  @Override public int maxY() {
    return 256;
  }

  @Override public int getBlockAt(int x, int y, int z) {
    if(y < 0 || y > 255) {
      return 0;
    }
    return blocks[chunkIndex(x & (X_MAX - 1), y, z & (Z_MAX - 1))];
  }

  @Override public void setBlockAt(int x, int y, int z, int block) {
    if(y < 0 || y > 255) {
      return;
    }
    isEmpty = false;
    if(block == 0)
      return;
    blocks[chunkIndex(x & (X_MAX - 1), y, z & (Z_MAX - 1))] = block;
  }

  @Override public boolean isBlockOnEdge(int x, int y, int z) {
    return x <= 0 || x >= 15 || z <= 0 || z >= 15 || y <= 0 || y >= 255;
  }

  @Override public Collection<CompoundTag> getTileEntities() {
    return tileEntities;
  }

  @Override
  public void addTileEntity(CompoundTag tileEntity) {
    tileEntities.add(tileEntity);
    isEmpty = false;
  }

  @Override public Collection<CompoundTag> getEntities() {
    return entities;
  }

  @Override
  public void addEntity(CompoundTag entity) {
    entities.add(entity);
    isEmpty = false;
  }

  @Override public int getBiomeAt(int x, int y, int z) {
    return biomeData.getBiome(x, y, z);
  }

  @Override public void setBiomeAt(int x, int y, int z, int biome) {
    biomeData.setBiomeAt(x, y, z, biome);
  }

  @Override public void clear() {
    //Quickly set all values to zero. Explanation here: https://github.com/chunky-dev/chunky/pull/866#issuecomment-808490741
    System.arraycopy(emptyBlocks, 0, blocks, 0, emptyBlocks.length);
    this.biomeData.clear();
    tileEntities.clear();
    entities.clear();
    isEmpty = true;
  }

  @Override
  public BiomeData getBiomeData() {
    return biomeData;
  }

  @Override
  public void setBiomeData(BiomeData biomeData) {
    this.biomeData = biomeData;
  }

  @Override
  public boolean isEmpty() {
    return isEmpty;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SimpleChunkData that = (SimpleChunkData) o;
    return Arrays.equals(blocks, that.blocks) && Objects.equals(biomeData, that.biomeData) && Objects.equals(tileEntities, that.tileEntities) && Objects.equals(entities, that.entities);
  }

  @Override public int hashCode() {
    int result = Objects.hash(tileEntities, entities);
    result = 31 * result + Arrays.hashCode(blocks);
    result = 31 * result + biomeData.hashCode();
    return result;
  }
}
