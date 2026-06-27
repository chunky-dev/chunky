package se.llbit.chunky.chunk.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import static se.llbit.chunky.world.Chunk.*;
import static se.llbit.chunky.world.Chunk.SECTION_Y_MAX;

/**
 * Implementation of a 3D biome grid where every block has a biome
 * Supports any Y values
 *
 * Minecraft versions: Bedrock & CubicChunks
 */
public class GenericBiomeData3d implements BiomeData {
    private final Int2ObjectOpenHashMap<int[]> sections = new Int2ObjectOpenHashMap<>();

    @Override
    public int getBiome(int chunkLocalX, int chunkLocalY, int chunkLocalZ) {
      int sectionY = chunkLocalY >> 4;
      int[] sectionData = sections.get(sectionY);

      if(sectionData == null) {
        return 0;
      }

      return sectionData[getIdx(chunkLocalX, chunkLocalY, chunkLocalZ)];
    }

    @Override
    public void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome) {
      if(biome == 0)
        return;

      int sectionY = chunkLocalY >> 4;
      int[] sectionData = sections.computeIfAbsent(sectionY, _ -> new int[X_MAX * SECTION_Y_MAX * Z_MAX]);
      sectionData[getIdx(chunkLocalX, chunkLocalY, chunkLocalZ)] = biome;
    }

    public static int getIdx(int localX, int localY, int localZ) {
      return (localX & 0xf) + SECTION_Y_MAX * ((localY & 0xf) + (localZ & 0xf) * X_MAX);
    }

    @Override
    public void clear() {
      sections.clear();
    }
}
