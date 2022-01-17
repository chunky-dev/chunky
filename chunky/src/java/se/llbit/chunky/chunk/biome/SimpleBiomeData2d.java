package se.llbit.chunky.chunk.biome;

import java.util.Arrays;

import static se.llbit.chunky.world.Chunk.*;

/**
 * Implementation of a 16x16 biome representation
 */
public class SimpleBiomeData2d implements BiomeData {
  public static final int BIOMES_ARRAY_SIZE = X_MAX * Z_MAX;

  private final int[] biomes = new int[BIOMES_ARRAY_SIZE];

  @Override
  public int getBiome(int chunkLocalX, int chunkLocalY, int chunkLocalZ) {
    return biomes[chunkXZIndex(chunkLocalX, chunkLocalZ)];
  }

  @Override
  public void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome) {
    biomes[chunkXZIndex(chunkLocalX, chunkLocalZ)] = biome;
  }

  @Override
  public void clear() {
    Arrays.fill(biomes, 0);
  }
}
