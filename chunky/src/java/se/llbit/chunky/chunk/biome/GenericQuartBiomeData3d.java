package se.llbit.chunky.chunk.biome;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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

/**
 * Implementation of a 3D biome grid where the smallest size of a biome is 4x4x4 blocks
 * Supports any Y values
 *
 * Minecraft versions >= 21w39a
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

    return sectionData[getQuartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)];
  }

  @Override
  public void setBiomeAt(int chunkLocalX, int chunkLocalY, int chunkLocalZ, int biome) {
    if(biome == 0)
      return;

    int sectionY = chunkLocalY >> 4;
    int[] sectionData = sections.computeIfAbsent(sectionY, _sectionY -> new int[4*4*4]);
    sectionData[getQuartIdx(chunkLocalX, chunkLocalY, chunkLocalZ)] = biome;
  }

  public static int getQuartIdx(int localQuartX, int localQuartY, int localQuartZ) {
    localQuartX = (localQuartX & (X_MAX - 1)) >> 2;
    localQuartY = (localQuartY & (SECTION_Y_MAX - 1)) >> 2;
    localQuartZ = (localQuartZ & (Z_MAX - 1)) >> 2;
    return localQuartX + 4 * (localQuartY + localQuartZ * 4);
  }

  @Override
  public void clear() {
    sections.clear();
  }

  public static void loadBiomeDataPost21w39a(Tag chunkData, GenericQuartBiomeData3d biomeData, BiomePalette biomePalette, int yMin, int yMax) {
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
                Log.infof("Unknown biome %s, will be rendered like minecraft:ocean", ((StringTag) item).getData());
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
                Log.infof("Unknown biome %s, will be rendered like minecraft:ocean", ((StringTag) localBiomePalette.get(0)).getData());
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
