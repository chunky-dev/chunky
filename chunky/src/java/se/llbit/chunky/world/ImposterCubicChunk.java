package se.llbit.chunky.world;

import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.EmptyChunkData;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.map.IconLayer;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.chunky.world.region.ImposterCubicRegion;
import se.llbit.math.QuickMath;
import se.llbit.nbt.*;
import se.llbit.util.BitBuffer;
import se.llbit.util.Mutable;
import se.llbit.util.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ImposterCubicChunk extends Chunk {
  private final CubicWorld world;

  public ImposterCubicChunk(ChunkPosition pos, World world) {
    super(pos, world);
    assert world instanceof CubicWorld;
    this.world = (CubicWorld) world;

    assert world.getVersionId() == 1343;
    version = "1.12";
  }

  private Map<Integer, Map<String, Tag>> getCubeTags(Set<String> request) {
    Mutable<Integer> timestamp = new Mutable<>(dataTimestamp);
    ImposterCubicRegion region = (ImposterCubicRegion) world.getRegion(position.getRegionPosition());
    Map<Integer, Map<String, Tag>> cubeTagsInColumn = region.getCubeTagsInColumn(position, request, timestamp);
    dataTimestamp = timestamp.get();
    return cubeTagsInColumn;
  }

  /**
   * Parse the chunk from the region file and render the current
   * layer, surface and cave maps.
   * @return whether the input chunkdata was modified
   */
  public synchronized boolean loadChunk(ChunkData chunkData, int yMin, int yMax) {
    if (!shouldReloadChunk()) {
      return false;
    }

    Set<String> request = new HashSet<>();
    request.add(Chunk.DATAVERSION);
    request.add(Chunk.LEVEL_SECTIONS);
    request.add(Chunk.LEVEL_BIOMES);
    Map<Integer, Map<String, Tag>> data = getCubeTags(request);
    // TODO: improve error handling here.
    if (data == null) {
      return false;
    }

    surfaceTimestamp = dataTimestamp;
    loadSurfaceCubic(data, chunkData, yMin, yMax);
    biomes = IconLayer.UNKNOWN;

    biomesTimestamp = dataTimestamp;
//    if (surface == IconLayer.MC_1_12) {
//      biomes = IconLayer.MC_1_12;
//    } else {
//      loadBiomes(data, chunkData);
//    }
    world.chunkUpdated(position);
    return true;
  }

  private void loadSurfaceCubic(Map<Integer, Map<String, Tag>> data, ChunkData chunkData, int yMin, int yMax) {
    if (data == null) {
      surface = IconLayer.CORRUPT;
      return;
    }

    Heightmap heightmap = world.heightmap();
    BlockPalette palette = new BlockPalette();

    for (Map.Entry<Integer, Map<String, Tag>> entry : data.entrySet()) {
      Integer yPos = entry.getKey();
      Map<String, Tag> cubeData = entry.getValue();

      Tag sections = cubeData.get(LEVEL_SECTIONS);
      if (sections.isList()) {
//        extractBiomeData(cubeData.get(LEVEL_BIOMES), chunkData);
        if (version.equals("1.13") || version.equals("1.12")) {
          loadBlockDataCubic(yPos, cubeData, chunkData, palette, yMin, yMax);
          surface = new SurfaceLayer(world.currentDimension(), chunkData, palette, yMin, yMax);
          queueTopography();
        }
      }
    }

    int[] heightmapData = extractHeightmapDataCubic(null, chunkData);
    updateHeightmap(heightmap, position, chunkData, heightmapData, palette, yMax);
  }

  private int[] extractHeightmapDataCubic(Map<String, Tag> cubeData, ChunkData chunkData) {
    //heightmap data is far more complex in CubicChunks, and for now we will ignore it
    int[] fallback = new int[X_MAX * Z_MAX];
    for (int i = 0; i < fallback.length; ++i) {
      fallback[i] = chunkData.maxY()-1;
    }
    return fallback;
  }

  private static final int CUBE_DIAMETER_IN_BLOCKS = 16;
  private void loadBlockDataCubic(int cubeY, Map<String, Tag> cubeData, ChunkData chunkData, BlockPalette blockPalette, int yMin, int yMax) {
    Tag sections = cubeData.get(LEVEL_SECTIONS);
    if(sections.isList()) {
      ListTag sectionTags = sections.asList();
      if(sectionTags.size() == 1) {
        for (SpecificTag section : sectionTags) {
          if (section.get("Palette").isList()) {
            // 1.13+ cube
          } else {
            Tag dataTag = section.get("Data");
            if (dataTag.isByteArray(SECTION_HALF_NIBBLES)) {
              byte[] dataArray = dataTag.byteArray();

              Tag blocksTag = section.get("Blocks");
              if (blocksTag.isByteArray(SECTION_BYTES)) {
                byte[] blockArray = blocksTag.byteArray();

                int cubeMinBlockY = (cubeY << 4);
                int offset = 0;
                for (int y = 0; y < CUBE_DIAMETER_IN_BLOCKS; y++) {
                  int blockY = cubeMinBlockY + y;
                  for (int z = 0; z < CUBE_DIAMETER_IN_BLOCKS; z++) {
                    for (int x = 0; x < CUBE_DIAMETER_IN_BLOCKS; x++) {
                      chunkData.setBlockAt(x, blockY, z, blockPalette.put(
                        LegacyBlocks.getTag(offset, blockArray, dataArray)));
                      offset += 1;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void loadBlockData0(Map<String, Tag> data, ChunkData chunkData, BlockPalette blockPalette, int minY, int maxY) {
    Tag sections = data.get(LEVEL_SECTIONS);
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        Tag yTag = section.get("Y");
        int sectionY = yTag.byteValue();
        int sectionMinBlockY = sectionY << 4;

        if (sectionY < minY >> 4 || sectionY - 1 > (maxY >> 4) + 1)
          continue; //skip parsing sections that are outside requested bounds

        if (section.get("Palette").isList()) {
          ListTag palette = section.get("Palette").asList();
          // Bits per block:
          int bpb = 4;
          if (palette.size() > 16) {
            bpb = QuickMath.log2(QuickMath.nextPow2(palette.size()));
          }

          int dataSize = (4096 * bpb) / 64;
          Tag blockStates = section.get("BlockStates");

          if (blockStates.isLongArray(dataSize)) {
            // since 20w17a, block states are aligned to 64-bit boundaries, so there are 64 % bpb
            // unused bits per block state; if so, the array is longer than the expected data size
            boolean isAligned = data.get(DATAVERSION).intValue() >= DATAVERSION_20w17a;
            if (isAligned) {
              // entries are 64-bit-padded, re-calculate the bits per block
              // this is the dataSize calculation from above reverted, we know the actual data size
              bpb = blockStates.longArray().length / 64;
            }

            int[] subpalette = new int[palette.size()];
            int paletteIndex = 0;
            for (Tag item : palette.asList()) {
              subpalette[paletteIndex] = blockPalette.put(item);
              paletteIndex += 1;
            }
            BitBuffer buffer = new BitBuffer(blockStates.longArray(), bpb, isAligned);
            for (int y = 0; y < SECTION_Y_MAX; y++) {
              int blockY = sectionMinBlockY + y;
              for (int z = 0; z < Z_MAX; z++) {
                for (int x = 0; x < X_MAX; x++) {
                  int b0 = buffer.read();
                  if (b0 < subpalette.length) {
                    chunkData.setBlockAt(x, blockY, z, subpalette[b0]);
                  }
                }
              }
            }
          }
        } else {
          int yOffset = sectionY & 0xFF;

          Tag dataTag = section.get("Data");
          byte[] blockDataBytes = new byte[(Chunk.X_MAX * Chunk.Y_MAX * Chunk.Z_MAX) / 2];
          if (dataTag.isByteArray(SECTION_HALF_NIBBLES)) {
            System.arraycopy(dataTag.byteArray(), 0, blockDataBytes, SECTION_HALF_NIBBLES * yOffset,
              SECTION_HALF_NIBBLES);
          }

          Tag blocksTag = section.get("Blocks");
          if (blocksTag.isByteArray(SECTION_BYTES)) {
            byte[] blocksBytes = new byte[Chunk.X_MAX * Chunk.Y_MAX * Chunk.Z_MAX];
            System.arraycopy(blocksTag.byteArray(), 0, blocksBytes, SECTION_BYTES * yOffset,
              SECTION_BYTES);

            int offset = SECTION_BYTES * yOffset;
            for (int y = 0; y < SECTION_Y_MAX; y++) {
              int blockY = sectionMinBlockY + y;
              for (int z = 0; z < Z_MAX; z++) {
                for (int x = 0; x < X_MAX; x++) {
                  chunkData.setBlockAt(x, blockY, z, blockPalette.put(
                    LegacyBlocks.getTag(offset, blocksBytes, blockDataBytes)));
                  offset += 1;
                }
              }
            }
          }
        }
      }
    }
  }

  public synchronized ChunkData getChunkData(ChunkData reuseChunkData, BlockPalette palette, int minY, int maxY) {
    Set<String> request = new HashSet<>();
    request.add(DATAVERSION);
    request.add(LEVEL_SECTIONS);
    request.add(LEVEL_BIOMES);
    request.add(LEVEL_ENTITIES);
    request.add(LEVEL_TILEENTITIES);
    Map<Integer, Map<String, Tag>> data = getCubeTags(request);
    if(reuseChunkData == null || reuseChunkData instanceof EmptyChunkData) {
      reuseChunkData = world.createChunkData();
    } else {
      reuseChunkData.clear();
    }
    // TODO: improve error handling here.
    if (data == null) {
      return reuseChunkData;
    }

    for (Map.Entry<Integer, Map<String, Tag>> entry : data.entrySet()) {
      Integer yPos = entry.getKey();
      Map<String, Tag> cubeData = entry.getValue();

      Tag sections = cubeData.get(LEVEL_SECTIONS);
      Tag biomesTag = cubeData.get(LEVEL_BIOMES);
      Tag entitiesTag = cubeData.get(LEVEL_ENTITIES);
      Tag tileEntitiesTag = cubeData.get(LEVEL_TILEENTITIES);
      if (biomesTag.isByteArray(X_MAX * Z_MAX) || biomesTag.isIntArray(X_MAX * Z_MAX)) {
//        extractBiomeData(biomesTag, reuseChunkData);
      }

      if (sections.isList()) {
        loadBlockDataCubic(yPos, cubeData, reuseChunkData, palette, minY, maxY);
      }

      if (entitiesTag.isList()) {
        for (SpecificTag tag : (ListTag) entitiesTag) {
          if (tag.isCompoundTag())
            reuseChunkData.addEntity((CompoundTag) tag);
        }
      }

      if (tileEntitiesTag.isList()) {
        for (SpecificTag tag : (ListTag) tileEntitiesTag) {
          if (tag.isCompoundTag())
            reuseChunkData.addTileEntity((CompoundTag) tag);
        }
      }
    }
    return reuseChunkData;
  }
}
