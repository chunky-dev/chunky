package se.llbit.chunky.world;

import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.EmptyChunkData;
import se.llbit.chunky.map.IconLayer;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.chunky.world.biome.ArrayBiomePalette;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.chunky.world.region.ImposterCubicRegion;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.Tag;
import se.llbit.util.Mutable;
import se.llbit.util.annotation.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static se.llbit.chunky.world.JavaChunk.SECTION_BYTES;
import static se.llbit.chunky.world.JavaChunk.SECTION_HALF_NIBBLES;

/**
 * An implementation of a cube wrapper for pre flattening cubic chunks (1.10, 1.11, 1.12)
 *
 * Represents an infinitely tall column of 16x16x16 Cubes
 */
public class ImposterCubicChunk extends Chunk {
  private final CubicDimension dimension;

  public ImposterCubicChunk(ChunkPosition pos, Dimension dimension) {
    super(pos, dimension);
    assert dimension instanceof CubicDimension;
    this.dimension = (CubicDimension) dimension;

    version = ChunkVersion.PRE_FLATTENING;
  }

  private Map<Integer, Map<String, Tag>> getCubeTags(Set<String> request) {
    Mutable<Integer> timestamp = new Mutable<>(dataTimestamp);
    ImposterCubicRegion region = (ImposterCubicRegion) dimension.getRegion(position.getRegionPosition());
    Map<Integer, Map<String, Tag>> cubeTagsInColumn = region.getCubeTagsInColumn(position, request, timestamp);
    dataTimestamp = timestamp.get();
    return cubeTagsInColumn;
  }

  /**
   * Parse the chunk from the region file and render the current
   * layer, surface and cave maps.
   * @return whether the input chunkdata was modified
   */
  @Override
  public synchronized boolean loadChunk(@NotNull Mutable<ChunkData> mutableChunkData, int yMin, int yMax) {
    if (!shouldReloadChunk()) {
      return false;
    }

    Set<String> request = new HashSet<>();
    request.add(JavaChunk.DATAVERSION);
    request.add(JavaChunk.LEVEL_SECTIONS);
    request.add(JavaChunk.LEVEL_BIOMES);
    Map<Integer, Map<String, Tag>> data = getCubeTags(request);
    // TODO: improve error handling here.
    if (data == null) {
      return false;
    }

    surfaceTimestamp = dataTimestamp;
    mutableChunkData.set(this.dimension.createChunkData(mutableChunkData.get(), Integer.MIN_VALUE, Integer.MAX_VALUE));
    ChunkData chunkData = mutableChunkData.get();
    loadSurfaceCubic(data, chunkData, yMin, yMax);
    biomes = IconLayer.UNKNOWN;

    biomesTimestamp = dataTimestamp;
    // TODO: add biomes support once we have 3d biomes support
//    if (surface == IconLayer.MC_1_12) {
//      biomes = IconLayer.MC_1_12;
//    } else {
//      loadBiomes(data, chunkData);
//    }
    dimension.chunkUpdated(position);
    return true;
  }

  private void loadSurfaceCubic(Map<Integer, Map<String, Tag>> data, ChunkData chunkData, int yMin, int yMax) {
    if (data == null) {
      surface = IconLayer.CORRUPT;
      return;
    }

    Heightmap heightmap = dimension.getHeightmap();
    BlockPalette palette = new BlockPalette();
    BiomePalette biomePalette = new ArrayBiomePalette();
    biomePalette.put(Biomes.biomesPrePalette[0]); //We don't currently support cubic chunks biomes, and so default to ocean

    for (Map.Entry<Integer, Map<String, Tag>> entry : data.entrySet()) {
      Integer yPos = entry.getKey();
      Map<String, Tag> cubeData = entry.getValue();

      Tag sections = cubeData.get(JavaChunk.LEVEL_SECTIONS);
      if (sections.isList()) {
//        extractBiomeData(cubeData.get(LEVEL_BIOMES), chunkData);
        if (version == ChunkVersion.PRE_FLATTENING || version == ChunkVersion.POST_FLATTENING) {
          loadBlockDataCubic(yPos, cubeData, chunkData, palette, yMin, yMax);
          queueTopography();
        }
      }
    }

    int[] heightmapData = extractHeightmapDataCubic(null, chunkData);
    updateHeightmap(heightmap, position, chunkData, heightmapData, palette, yMax);
    surface = new SurfaceLayer(JavaWorld.VANILLA_DIMENSION_ID_TO_IDX.get(dimension.id()), chunkData, palette, biomePalette, yMin, yMax, heightmapData);
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
    Tag sections = cubeData.get(JavaChunk.LEVEL_SECTIONS);
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

  @Override
  public synchronized void getChunkData(@NotNull Mutable<ChunkData> reuseChunkData, BlockPalette palette, BiomePalette biomePalette, int minY, int maxY) {
    Set<String> request = new HashSet<>();
    request.add(JavaChunk.DATAVERSION);
    request.add(JavaChunk.LEVEL_SECTIONS);
    request.add(JavaChunk.LEVEL_BIOMES);
    request.add(JavaChunk.LEVEL_ENTITIES);
    request.add(JavaChunk.LEVEL_TILEENTITIES);
    Map<Integer, Map<String, Tag>> data = getCubeTags(request);
    if(reuseChunkData.get() == null || reuseChunkData.get() instanceof EmptyChunkData) {
      reuseChunkData.set(dimension.createChunkData(reuseChunkData.get(), Integer.MIN_VALUE, Integer.MAX_VALUE));
    } else {
      reuseChunkData.get().clear();
    }
    // TODO: improve error handling here.
    if (data == null) {
      return;
    }

    ChunkData chunkData = reuseChunkData.get();
    for (Map.Entry<Integer, Map<String, Tag>> entry : data.entrySet()) {
      Integer yPos = entry.getKey();
      Map<String, Tag> cubeData = entry.getValue();

      Tag sections = cubeData.get(JavaChunk.LEVEL_SECTIONS);
      Tag biomesTag = cubeData.get(JavaChunk.LEVEL_BIOMES);
      Tag entitiesTag = cubeData.get(JavaChunk.LEVEL_ENTITIES);
      Tag tileEntitiesTag = cubeData.get(JavaChunk.LEVEL_TILEENTITIES);
      // TODO: add biomes support once we have 3d biomes support
//      if (biomesTag.isByteArray(X_MAX * Z_MAX) || biomesTag.isIntArray(X_MAX * Z_MAX)) {
//        extractBiomeData(biomesTag, reuseChunkData);
//      }

      biomePalette.put(Biomes.biomesPrePalette[0]); //We don't currently support cubic chunks biomes, and so default to ocean

      if (sections.isList()) {
        loadBlockDataCubic(yPos, cubeData, chunkData, palette, minY, maxY);
      }

      if (entitiesTag.isList()) {
        for (SpecificTag tag : (ListTag) entitiesTag) {
          if (tag.isCompoundTag())
            chunkData.addEntity((CompoundTag) tag);
        }
      }

      if (tileEntitiesTag.isList()) {
        for (SpecificTag tag : (ListTag) tileEntitiesTag) {
          if (tag.isCompoundTag())
            chunkData.addTileEntity((CompoundTag) tag);
        }
      }
    }
  }
}
