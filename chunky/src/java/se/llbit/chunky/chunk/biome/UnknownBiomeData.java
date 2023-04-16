package se.llbit.chunky.chunk.biome;

import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.biome.Biomes;

public final class UnknownBiomeData implements BiomeData {
  /**
   * Singleton instance
   */
  public static final BiomeData INSTANCE = new UnknownBiomeData(0);

  private final int unknownPaletteId;

  private UnknownBiomeData(int unknownPaletteId) {
    this.unknownPaletteId = unknownPaletteId;
  }

  public static BiomeData instanceFor(BiomePalette biomePalette) {
    if(biomePalette.size() == 0) {
      biomePalette.put(Biomes.unknown);
      return UnknownBiomeData.INSTANCE;
    } else {
      return new UnknownBiomeData(biomePalette.put(Biomes.unknown));
    }
  }

  @Override
  public int getBiome(int chunkLocalX, int chunkLocalY, int chunkLocalZ) {
    return unknownPaletteId;
  }

  @Override
  public void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome) { }

  @Override
  public void clear() { }
}
