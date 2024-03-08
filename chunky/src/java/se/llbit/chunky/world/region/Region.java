package se.llbit.chunky.world.region;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.RegionPosition;

public interface Region extends Iterable<Chunk> {
  /**
   * @return Chunk at (x, z)
   */
  @PluginApi
  Chunk getChunk(int x, int z);
  @PluginApi
  default Chunk getChunk(ChunkPosition pos) {
    return getChunk(pos.x & 0b11111, pos.z & 0b11111);
  }

  /**
   * Delete a chunk.
   */
  @PluginApi
  default void deleteChunk(ChunkPosition chunkPos) {
    throw new UnsupportedOperationException("Not implemented by region implementation");
  }

  /**
   * Parse the region file to discover chunks.
   * @param minY the minimum requested block Y to be loaded. This does NOT need to be respected by the implementation
   * @param maxY the maximum requested block Y to be loaded. This does NOT need to be respected by the implementation
   */
  void parse(int minY, int maxY);

  /**
   * @return <code>true</code> if this is an empty or non-existent region
   */
  default boolean isEmpty() {
    return false;
  }

  RegionPosition getPosition();

  boolean hasChanged();

  boolean chunkChangedSince(ChunkPosition chunkPos, int timestamp);
}
