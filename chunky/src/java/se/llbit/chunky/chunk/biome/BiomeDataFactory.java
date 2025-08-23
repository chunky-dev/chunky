package se.llbit.chunky.chunk.biome;

import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.world.JavaChunk;
import se.llbit.chunky.world.biome.Biome;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;
import se.llbit.util.BitBuffer;

import static se.llbit.chunky.world.Chunk.*;
import static se.llbit.util.NbtUtil.getTagFromNames;

public class BiomeDataFactory {
  //TODO: Ideally we would have registered factory impls with an isValidFor(Tag chunkTag), but this messy if chain works for now
  public static void loadBiomeData(ChunkData chunkData, Tag chunkTag, BiomePalette biomePalette, int yMin, int yMax) {
    BiomeData biomeData = chunkData.getBiomeData();
    Tag biomeTagsPre21w39a = chunkTag.get(JavaChunk.LEVEL_BIOMES);
    if (!biomeTagsPre21w39a.isError()) { // pre21w39a tag exists
      if (biomeTagsPre21w39a.isByteArray(X_MAX * Z_MAX)) {
        if (!(biomeData instanceof BiomeData2d)) {
          biomeData = new BiomeData2d();
        }
        BiomeData2d.loadBiomeDataByteArray(chunkTag, (BiomeData2d) biomeData, biomePalette);
        chunkData.setBiomeData(biomeData);
        return;
      } else if (biomeTagsPre21w39a.isIntArray(QuartBiomeData3d.BIOMES_ARRAY_SIZE)) {
        if (!(biomeData instanceof QuartBiomeData3d)) {
          biomeData = new QuartBiomeData3d();
        }
        QuartBiomeData3d.loadBiomeData3dSimple(chunkTag, (QuartBiomeData3d) biomeData, biomePalette);
        chunkData.setBiomeData(biomeData);
        return;
      } else if (biomeTagsPre21w39a.isIntArray(BiomeData2d.BIOMES_ARRAY_SIZE)) {
        if (!(biomeData instanceof BiomeData2d)) {
          biomeData = new BiomeData2d();
        }
        BiomeData2d.loadBiomeDataIntArray(chunkTag, (BiomeData2d) biomeData, biomePalette);
        chunkData.setBiomeData(biomeData);
        return;
      }
    } else if (!chunkTag.get(JavaChunk.SECTIONS_POST_21W39A).isError()) { // in 21w39a biome tags moved into the sections array
      if(!(biomeData instanceof GenericQuartBiomeData3d)) {
        biomeData = new GenericQuartBiomeData3d();
      }
      GenericQuartBiomeData3d.loadBiomeDataPost21w39a(chunkTag, (GenericQuartBiomeData3d) biomeData, biomePalette, yMin, yMax);
      chunkData.setBiomeData(biomeData);
      return;
    }
    biomeData = UnknownBiomeData.instanceFor(biomePalette);
    chunkData.setBiomeData(biomeData);
  }
}
