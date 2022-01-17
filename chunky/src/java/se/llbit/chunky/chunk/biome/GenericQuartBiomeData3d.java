package se.llbit.chunky.chunk.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import static se.llbit.chunky.world.Chunk.*;
import static se.llbit.chunky.world.Chunk.Z_MAX;

/**
 * Implementation of a
 */
public class GenericQuartBiomeData3d implements BiomeData {
  private final Int2ObjectOpenHashMap<int[]> sections = new Int2ObjectOpenHashMap<>();

  @Override
  public int getBiome(int chunkLocalX, int chunkLocalY, int chunkLocalZ) {
    int sectionY = chunkLocalY >> 4;
    int[] sectionData = sections.get(sectionY);

    if(sectionData == null) {
      return 0;
    }

    return sectionData[quartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)];
  }

  @Override
  public void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome) {
    if(biome == 0)
      return;

    int sectionY = chunkLocalY >> 4;
    int[] sectionData = sections.computeIfAbsent(sectionY, _sectionY -> new int[4*4*4]);
    sectionData[quartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)] = biome;
  }

  public static int quartIdx(int localQuartX, int localQuartY, int localQuartZ) {
    localQuartX = (localQuartX & (X_MAX - 1)) >> 2;
    localQuartY = (localQuartY & (SECTION_Y_MAX - 1)) >> 2;
    localQuartZ = (localQuartZ & (Z_MAX - 1)) >> 2;
    return localQuartX + 4 * (localQuartY + localQuartZ * 4);
  }

  @Override
  public void clear() {
    sections.clear();
  }
}
