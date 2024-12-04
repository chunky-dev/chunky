package se.llbit.chunky.chunk.biome;

import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.nbt.Tag;

import java.util.Arrays;

import static se.llbit.chunky.world.JavaChunk.LEVEL_BIOMES;

/**
 * Implementation of a 3D biome grid where the smallest size of a biome is 4x4x4 blocks
 * Supports Y values 0-255 (inclusive)
 *
 * Minecraft versions >= 1.15 */
public class QuartBiomeData3d implements BiomeData {
  public static final int BIOME_DATA3D_HEIGHT = 256 / QUART;
  public static final int BIOMES_ARRAY_SIZE = QUART * QUART * BIOME_DATA3D_HEIGHT;

  /** internally varies first z -> x -> y */
  private final int[] biomes = new int[BIOMES_ARRAY_SIZE];

  @Override
  public int getBiome(int chunkLocalX, int chunkLocalY, int chunkLocalZ) {
    if(chunkLocalY < 0 || chunkLocalY > 255) {
      return 0;
    }

    return biomes[getQuartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)];
  }

  @Override
  public void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome) {
    if(chunkLocalY < 0 || chunkLocalY > 255) {
      return;
    }

    biomes[getQuartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)] = biome;
  }

  /**
   * Params x, y, z, are chunk local coordinates
   *
   * @return Index for position into the {@link QuartBiomeData3d#biomes} array
   */
  private static int getQuartIdx(int x, int y, int z) {
    x = (x >> 2) & QUART_BITS;
    z = (z >> 2) & QUART_BITS;
    y = (y >> 2);
    return z + QUART * (x + QUART * y);
  }

  @Override
  public void clear() {
    Arrays.fill(this.biomes, 0);
  }

  public static void loadBiomeData3dSimple(Tag chunkData, QuartBiomeData3d biomeData, BiomePalette biomePalette) {
    // Since Minecraft 1.15, biome IDs are stored in an int vector with 1024 entries.
    // Each entry stores the biome for a 4x4x4 cube, sorted by X, Z, Y. For now, we use the biome at y=0.
    int[] data = chunkData.get(LEVEL_BIOMES).intArray();
    for (int x = 0; x < 4; x++) {
      for (int z = 0; z < 4; z++) {
        for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 4; j++) {
            biomeData.setBiomeAt((x * 4 + i), 0, z * 4 + j, (byte) biomePalette.put(Biomes.biomesPrePalette[data[z * 4 + x] & 0xff]));
          }
        }
      }
    }
  }
}
