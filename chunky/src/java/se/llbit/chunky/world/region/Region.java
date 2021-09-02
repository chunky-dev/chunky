package se.llbit.chunky.world.region;

import com.sun.istack.internal.Nullable;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.nbt.Tag;
import se.llbit.util.Mutable;

import java.util.Map;
import java.util.Set;

public interface Region extends Iterable<Chunk> {
  /**
   * @return Chunk at (x, z)
   */
  Chunk getChunk(int x, int z);
  default Chunk getChunk(ChunkPosition pos) {
    return getChunk(pos.x & 31, pos.z & 31);
  }

  /**
   * Delete a chunk.
   */
  default void deleteChunk(ChunkPosition chunkPos) {
    throw new UnsupportedOperationException("Not implemented by region implementation");
  }

  /**
   * Parse the region file to discover chunks.
   * @param minY the minimum requested Y to be loaded. This does NOT need to be respected by the implementation
   * @param maxY the maximum requested Y to be loaded. This does NOT need to be respected by the implementation
   */
  void parse(int minY, int maxY);

  /**
   * @return <code>true</code> if this is an empty or non-existent region
   */
  default boolean isEmpty() {
    return false;
  }

  ChunkPosition getPosition();

  boolean hasChanged();

  boolean chunkChangedSince(ChunkPosition chunkPos, int timestamp);

  @Nullable
  default Map<String, Tag> getChunkTags(ChunkPosition position, Set<String> request, Mutable<Integer> dataTimestamp) {
    return null;
  }
}
