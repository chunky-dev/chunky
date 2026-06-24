package se.llbit.chunky.world.bedrock;

import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.RegionPosition;
import se.llbit.chunky.world.region.Region;

import java.util.Iterator;

/**
 * Bedrock doesn't have regions, this class redirects region calls to the chunk/dimension as is appropriate
 */
public class VirtualBedrockRegion implements Region {
  private final RegionPosition position;
  private final BedrockDimension dimension;

  public VirtualBedrockRegion(RegionPosition pos, BedrockDimension dimension) {
    this.position = pos;
    this.dimension = dimension;
  }

  @Override
  public Chunk getChunk(int x, int z) {
    return this.dimension.getChunk(new ChunkPosition(x, z));
  }

  @Override
  public void parse(int minY, int maxY) { }

  @Override
  public RegionPosition getPosition() {
    return this.position;
  }

  @Override
  public boolean hasChanged() {
    return false; // Not supported by Bedrock implementation
  }

  @Override
  public boolean chunkChangedSince(ChunkPosition chunkPos, int timestamp) {
    return false;
  }

  @Override public Iterator<Chunk> iterator() {
    return new Iterator<>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < Region.CHUNKS_X * Region.CHUNKS_Z; // virtual bedrock regions are the same size as java ones (dictated by the map view)
      }

      @Override
      public Chunk next() {
        int localX = index & 0x1f;
        int localZ = index >> 5;
        index++;
        return dimension.getChunk(position.asChunkPosition(localX, localZ));
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
