package se.llbit.chunky.chunk;

import se.llbit.chunky.chunk.biome.BiomeData;
import se.llbit.nbt.CompoundTag;

import java.util.Collection;

/**
 * Interface designed to allow for any chunk data structure behind it
 *
 * Implementations expected to handle x/z values from 0-15
 * Implementations expected to gracefully handle ANY y values, though it does not need to support all values
 *
 * If a given y coordinate is outside of supported bounds, the implementation should return 0
 */
public interface ChunkData {
  /**
   * Minimum INCLUSIVE block Y in this chunk
   * @return Can return a set value or adapt to blocks added
   */
  int minY();

  /**
   * Maximum EXCLUSIVE block Y in this chunk
   * @return Can return a set value or adapt to blocks added
   */
  int maxY();

  /**
   * @param x X position of the requested block
   * @param y Y position of the requested block
   * @param z Z position of the requested block
   * @return int ID of the block at the requested location
   */
  int getBlockAt(int x, int y, int z);

  /**
   * @param x X position of the block to be set
   * @param y Y position of the block to be set
   * @param z Z position of the block to be set
   * @param block int ID of the block to be set
   */
  void setBlockAt(int x, int y, int z, int block);

  /**
   * @param x X position of the requested block
   * @param y Y position of the requested block
   * @param z Z position of the requested block
   * @return Whether the block is on the edge of the chunk
   */
  boolean isBlockOnEdge(int x, int y, int z);

  /**
   * @return Collection of the tile entities in the chunk
   */
  Collection<CompoundTag> getTileEntities();

  /**
   * Adds a tile entity to the chunk
   * @param tileEntity the tile entity to be added
   */
  void addTileEntity(CompoundTag tileEntity);

  /**
   * @return Collection of the entities in the chunk
   */
  Collection<CompoundTag> getEntities();

  /**
   * Adds an entity to the chunk
   * @param entity the entity to be added
   */
  void addEntity(CompoundTag entity);

  /**
   * Reset the internal data to the initial state
   */
  void clear();

  /**
   * @return <code>true</code> if this is an empty (non-existing) chunk
   */
  boolean isEmpty();

  BiomeData getBiomeData();

  void setBiomeData(BiomeData biomeData);
}
