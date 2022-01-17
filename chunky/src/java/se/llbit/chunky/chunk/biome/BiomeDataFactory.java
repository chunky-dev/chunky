package se.llbit.chunky.chunk.biome;

import se.llbit.chunky.chunk.ChunkData;
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
    Tag biomeTagsPre21w39a = chunkTag.get(LEVEL_BIOMES);
    if (!biomeTagsPre21w39a.isError()) { // pre21w39a tag exists
      if (biomeTagsPre21w39a.isByteArray(X_MAX * Z_MAX)) {
        if (!(biomeData instanceof SimpleBiomeData2d)) {
          biomeData = new SimpleBiomeData2d();
        }
        loadBiomeDataByteArray(chunkTag, (SimpleBiomeData2d) biomeData, biomePalette);
        chunkData.setBiomeData(biomeData);
        return;
      } else if (biomeTagsPre21w39a.isIntArray(SimpleQuartBiomeData3d.BIOMES_ARRAY_SIZE)) {
        if (!(biomeData instanceof SimpleQuartBiomeData3d)) {
          biomeData = new SimpleQuartBiomeData3d();
        }
        loadBiomeData3dSimple(chunkTag, (SimpleQuartBiomeData3d) biomeData, biomePalette);
        chunkData.setBiomeData(biomeData);
        return;
      } else if (biomeTagsPre21w39a.isIntArray(SimpleBiomeData2d.BIOMES_ARRAY_SIZE)) {
        if (!(biomeData instanceof SimpleBiomeData2d)) {
          biomeData = new SimpleBiomeData2d();
        }
        loadBiomeDataIntArray(chunkTag, (SimpleBiomeData2d) biomeData, biomePalette);
        chunkData.setBiomeData(biomeData);
        return;
      }
    } else if (!chunkTag.get(SECTIONS_POST_21W39A).isError()) { // in 21w39a biome tags moved into the sections array
      if(!(biomeData instanceof GenericQuartBiomeData3d)) {
        biomeData = new GenericQuartBiomeData3d();
      }
      loadBiomeDataPost21w39a(chunkTag, (GenericQuartBiomeData3d) biomeData, biomePalette, yMin, yMax);
      chunkData.setBiomeData(biomeData);
      return;
    }
    biomeData = UnknownBiomeData.instanceFor(biomePalette);
    chunkData.setBiomeData(biomeData);
  }

  private static void loadBiomeDataByteArray(Tag chunkData, SimpleBiomeData2d biomeData, BiomePalette biomePalette) {
    byte[] data = chunkData.get(LEVEL_BIOMES).byteArray();
    int i = 0;
    for(int z = 0; z < Z_MAX; z++) {
      for(int x = 0; x < X_MAX; x++) {
        biomeData.setBiomeAt(x, 0, z, (byte) biomePalette.put(Biomes.biomesPrePalette[data[i] & 0xff]));
        i++;
      }
    }
  }

  private static void loadBiomeDataIntArray(Tag chunkData, SimpleBiomeData2d biomeData, BiomePalette biomePalette) {
    // Since Minecraft 1.13, biome IDs are stored in an int vector with 256 entries (one for each XZ position).
    int[] data = chunkData.get(LEVEL_BIOMES).intArray();
    int i = 0;
    for(int z = 0; z < Z_MAX; z++) {
      for(int x = 0; x < X_MAX; x++) {
        biomeData.setBiomeAt(x, 0, z, (byte) biomePalette.put(Biomes.biomesPrePalette[data[i] & 0xff]));
        i++;
      }
    }
  }

  private static void loadBiomeData3dSimple(Tag chunkData, SimpleQuartBiomeData3d biomeData, BiomePalette biomePalette) {
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

  private static void loadBiomeDataPost21w39a(Tag chunkData, GenericQuartBiomeData3d biomeData, BiomePalette biomePalette, int yMin, int yMax) {
    Tag sections = chunkData.get(SECTIONS_POST_21W39A);
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        Tag yTag = section.get("Y");
        int sectionY = yTag.byteValue();
        int sectionMinBlockY = sectionY << 4;

        if (sectionY < yMin >> 4 || sectionY - 1 > (yMax >> 4) + 1)
          continue; //skip parsing sections that are outside requested bounds
        Tag biomePaletteTag = getTagFromNames(section, "biomes\\palette");
        if (biomePaletteTag.isList()) { //must be a post 21w39a section, so we load the 3d biomes
          ListTag localBiomePalette = biomePaletteTag.asList();
          int bitsPerBiome = Math.max(1, QuickMath.log2(QuickMath.nextPow2(localBiomePalette.size())));

          Tag biomes = getTagFromNames(section, "biomes\\data");

          if (biomes.isLongArray(bitsPerBiome)) {
            int[] subpalette = new int[localBiomePalette.size()];
            int paletteIndex = 0;
            for (Tag item : localBiomePalette.asList()) {
              Biome biome = Biomes.biomesByResourceLocation.get(((StringTag) item).getData());
              if (biome == null) {
//                Log.warnf("Missing biome %s! defaulting to first biome in the palette", ((StringTag) item).getData()); //TODO: re-enable once all 1.18 biomes are added
                System.out.printf("Missing biome %s! defaulting to first biome in the palette\n", ((StringTag) item).getData());
                subpalette[paletteIndex] = biomePalette.put(Biomes.unknown);
              } else {
                subpalette[paletteIndex] = biomePalette.put(biome);
              }
              paletteIndex += 1;
            }

            BitBuffer buffer = new BitBuffer(biomes.longArray(), bitsPerBiome, true);
            for (int biomeY = 0; biomeY < 4; biomeY++) {
              int quartY = sectionMinBlockY + (biomeY << 2);
              for (int biomeZ = 0; biomeZ < 4; biomeZ++) {
                int quartZ = biomeZ << 2;
                for (int biomeX = 0; biomeX < 4; biomeX++) {
                  int quartX = biomeX << 2;

                  int b0 = buffer.read();
                  assert b0 < subpalette.length;
                  for (int localY = 0; localY < 4; localY++) {
                    for (int localZ = 0; localZ < 4; localZ++) {
                      for (int localX = 0; localX < 4; localX++) {
                        biomeData.setBiomeAt(quartX + localX, quartY + localY, quartZ + localZ, subpalette[b0]);
                      }
                    }
                  }
                }
              }
            }
          } else {
            if (localBiomePalette.size() == 1) {
              // biome palette exists, but data does not, we assume that section is entirely filled with one biome
              Biome biome = Biomes.biomesByResourceLocation.get(((StringTag) localBiomePalette.get(0)).getData());
              int biomeId;
              if (biome == null) {
//                Log.warnf("Missing biome %s! defaulting to first biome in the palette", ((StringTag) localBiomePalette.get(0)).getData()); //TODO: re-enable once all 1.18 biomes are added
                System.out.printf("Missing biome %s! defaulting to first biome in the palette\n", ((StringTag) localBiomePalette.get(0)).getData());
                biomeId = biomePalette.put(Biomes.unknown);
              } else {
                biomeId = biomePalette.put(biome);
              }
              for (int y = 0; y < SECTION_Y_MAX; y++) {
                int blockY = sectionMinBlockY + y;
                for (int z = 0; z < Z_MAX; z++) {
                  for (int x = 0; x < X_MAX; x++) {
                    biomeData.setBiomeAt(x, blockY, z, biomeId);
                  }
                }
              }
            } else {
              // minecraft crashed while saving caused this? Should be extremely rare
              Log.warn("Biome palette of size != 1 had no data?!");
              assert false : "Biome palette of size != 1 had no data?!";
            }
          }
        }
      }
    }
  }
}
