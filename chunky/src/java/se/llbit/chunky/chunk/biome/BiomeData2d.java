package se.llbit.chunky.chunk.biome;

import se.llbit.chunky.world.JavaChunk;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.nbt.Tag;

import java.util.Arrays;

import static se.llbit.chunky.world.Chunk.*;

/**
 * Implementation of a two-dimensional 16x16 biome representation where every x/z column can have a different biome.
 *
 * Minecraft versions < 1.15
 */
public class BiomeData2d implements BiomeData {
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

  public static void loadBiomeDataByteArray(Tag chunkData, BiomeData2d biomeData, BiomePalette biomePalette) {
    byte[] data = chunkData.get(JavaChunk.LEVEL_BIOMES).byteArray();
    int i = 0;
    for(int z = 0; z < Z_MAX; z++) {
      for(int x = 0; x < X_MAX; x++) {
        biomeData.setBiomeAt(x, 0, z, (byte) biomePalette.put(Biomes.biomesPrePalette[data[i] & 0xff]));
        i++;
      }
    }
  }

  public static void loadBiomeDataIntArray(Tag chunkData, BiomeData2d biomeData, BiomePalette biomePalette) {
    // Since Minecraft 1.13, biome IDs are stored in an int vector with 256 entries (one for each XZ position).
    int[] data = chunkData.get(JavaChunk.LEVEL_BIOMES).intArray();
    int i = 0;
    for(int z = 0; z < Z_MAX; z++) {
      for(int x = 0; x < X_MAX; x++) {
        biomeData.setBiomeAt(x, 0, z, (byte) biomePalette.put(Biomes.biomesPrePalette[data[i] & 0xff]));
        i++;
      }
    }
  }
}
