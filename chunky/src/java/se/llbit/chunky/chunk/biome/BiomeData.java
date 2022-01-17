package se.llbit.chunky.chunk.biome;

import se.llbit.chunky.world.Chunk;

/**
 * Interface designed to allow for any biome data structure behind it
 *
 * Implementations expected to handle x/z values from 0-15
 * Implementations expected to gracefully handle ANY y values, though it does not need to support all values
 *
 * If a given y coordinate is outside of supported bounds, the implementation should return 0
 */
public interface BiomeData {
  int QUART = 4;
  int QUART_BITS = 0x7;

  int SECTION_DIAMETER_IN_QUARTS = Chunk.X_MAX / QUART;

  /**
   * All coordinates are chunk local block coordinates.
   */
  int getBiome(int chunkLocalX, int chunkLocalY, int chunkLocalZ);

  /**
   * All coordinates are chunk local block coordinates.
   */
  void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome);

  /**
   * Reset the internal data to the initial state
   */
  void clear();
}
