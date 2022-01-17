package se.llbit.chunky.chunk.biome;

import java.util.Arrays;

/**
 * Implementation of a 4*64*4 biome representation
 */
public class SimpleQuartBiomeData3d implements BiomeData {
  public static final int BIOME_DATA3D_HEIGHT = 256 / QUART;
  public static final int BIOMES_ARRAY_SIZE = QUART * QUART * BIOME_DATA3D_HEIGHT;

  /** internally varies first z -> x -> y */
  private final int[] biomes = new int[BIOMES_ARRAY_SIZE];

  @Override
  public int getBiome(int chunkLocalX, int chunkLocalY, int chunkLocalZ) {
    if(chunkLocalY < 0 || chunkLocalY > 255) {
      return 0;
    }

    return biomes[quartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)];
  }

  @Override
  public void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome) {
    if(chunkLocalY < 0 || chunkLocalY > 255) {
      return;
    }

    biomes[quartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)] = biome;
  }

  private static int quartIdx(int x, int y, int z) {
    x = (x >> 2) & QUART_BITS;
    z = (z >> 2) & QUART_BITS;
    y = (y >> 2);
    return z + QUART * (x + QUART * y);
  }

  @Override
  public void clear() {
    Arrays.fill(this.biomes, 0);
  }
}
