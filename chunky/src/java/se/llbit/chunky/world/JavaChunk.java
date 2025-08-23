package se.llbit.chunky.world;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.ChunkLoadingException;
import se.llbit.chunky.chunk.EmptyChunkData;
import se.llbit.chunky.chunk.biome.BiomeDataFactory;
import se.llbit.chunky.map.BiomeLayer;
import se.llbit.chunky.map.IconLayer;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.chunky.world.biome.ArrayBiomePalette;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.chunky.world.region.MCRegion;
import se.llbit.log.Log;
import se.llbit.math.QuickMath;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.Tag;
import se.llbit.util.BitBuffer;
import se.llbit.util.Mutable;
import se.llbit.util.annotation.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static se.llbit.util.NbtUtil.getTagFromNames;
import static se.llbit.util.NbtUtil.tagFromMap;

public class JavaChunk extends Chunk {
  public static final String DATAVERSION = ".DataVersion";
  public static final String LEVEL_HEIGHTMAP = ".Level.HeightMap";
  public static final String LEVEL_SECTIONS = ".Level.Sections";
  public static final String LEVEL_BIOMES = ".Level.Biomes";
  public static final String LEVEL_ENTITIES = ".Level.Entities";
  public static final String ENTITIES_POST_20W45A = ".Entities";
  public static final String LEVEL_TILEENTITIES = ".Level.TileEntities";
  public static final String BLOCK_ENTITIES_POST_21W43A = ".block_entities";

  public static final String SECTIONS_POST_21W39A = ".sections";
  public static final String BIOMES_POST_21W39A = ".biomes";

  public static final int SECTION_BYTES = X_MAX * SECTION_Y_MAX * Z_MAX;
  public static final int SECTION_HALF_NIBBLES = SECTION_BYTES / 2;
  private static final int CHUNK_BYTES = X_MAX * Y_MAX * Z_MAX;

  public static final int DATAVERSION_20W17A = 2529;
  public static final int DATAVERSION_20W45A = 2681; // entities moved into separate region files

  public JavaChunk(ChunkPosition pos, JavaDimension dimension) {
    super(pos, dimension);
  }

  /**
   * @param request fresh request set
   * @return loaded data, or null if something went wrong
   */
  private Map<String, Tag> getChunkTags(Set<String> request) throws ChunkLoadingException {
    MCRegion region = (MCRegion) ((JavaDimension) dimension).getRegion(position.getRegionPosition());
    Mutable<Integer> timestamp = new Mutable<>(dataTimestamp);
    Map<String, Tag> chunkTags = region.getChunkTags(this.position, request, timestamp);
    this.dataTimestamp = timestamp.get();
    return chunkTags;
  }

  /**
   * @param request fresh request set
   * @return loaded data, or null if something went wrong
   */
  private Map<String, Tag> getEntityTags(Set<String> request) throws ChunkLoadingException {
    MCRegion region = (MCRegion) ((JavaDimension) dimension).getRegion(position.getRegionPosition());
    return region.getEntityTags(this.position, request);
  }

  public synchronized boolean loadChunk(@NotNull Mutable<ChunkData> chunkData, int yMin, int yMax) {
    if (!shouldReloadChunk()) {
      return false;
    }

    Set<String> request = new HashSet<>();
    request.add(JavaChunk.DATAVERSION);
    request.add(JavaChunk.LEVEL_SECTIONS);
    request.add(JavaChunk.SECTIONS_POST_21W39A);
    request.add(JavaChunk.LEVEL_BIOMES);
    request.add(JavaChunk.BIOMES_POST_21W39A);
    request.add(JavaChunk.LEVEL_HEIGHTMAP);

    Map<String, Tag> dataMap;
    try {
      dataMap = getChunkTags(request);
    } catch (ChunkLoadingException e) { // we don't want to crash the map view if a chunk fails to load, so we warn the user
      Log.warn(String.format("Failed to load chunk %s", position), e);
      return false;
    }
    // TODO: improve error handling here.
    if (dataMap == null) {
      return false;
    }
    Tag data = tagFromMap(dataMap);

    surfaceTimestamp = dataTimestamp;
    version = chunkVersion(data);
    IntIntImmutablePair chunkBounds = inclusiveChunkBounds(data);
    chunkData.set(this.dimension.createChunkData(chunkData.get(), chunkBounds.leftInt(), chunkBounds.rightInt()));
    loadSurface(data, chunkData.get(), yMin, yMax);
    biomesTimestamp = dataTimestamp;

    dimension.chunkUpdated(position);
    return true;
  }

  private void loadSurface(@NotNull Tag data, ChunkData chunkData, int yMin, int yMax) {
    if (data == null) {
      surface = IconLayer.CORRUPT;
      return;
    }

    Heightmap heightmap = dimension.getHeightmap();
    Tag sections = getTagFromNames(data, LEVEL_SECTIONS, SECTIONS_POST_21W39A);
    if (sections.isList()) {
      if (version == ChunkVersion.PRE_FLATTENING || version == ChunkVersion.POST_FLATTENING) {
        BiomePalette biomePalette = new ArrayBiomePalette();
        BiomeDataFactory.loadBiomeData(chunkData, data, biomePalette, yMin, yMax);
        biomes = new BiomeLayer(chunkData, biomePalette);

        BlockPalette palette = new BlockPalette();
        palette.unsynchronize(); //only this RegionParser will use this palette
        loadBlockData(data, chunkData, palette, yMin, yMax);

        int[] heightmapData = extractHeightmapData(data, chunkData);
        updateHeightmap(heightmap, position, chunkData, heightmapData, palette, yMax);

        surface = new SurfaceLayer(JavaWorld.VANILLA_DIMENSION_ID_TO_IDX.get(dimension.getId()), chunkData, palette, biomePalette, yMin, yMax, heightmapData);
        queueTopography();
      }
    } else {
      surface = IconLayer.CORRUPT;
    }
  }

  private int[] extractHeightmapData(@NotNull Tag data, ChunkData chunkData) {
    Tag heightmapTag = data.get(LEVEL_HEIGHTMAP);
    if (heightmapTag.isIntArray(X_MAX * Z_MAX)) {
      return heightmapTag.intArray();
    } else {
      int[] fallback = new int[X_MAX * Z_MAX];
      for (int i = 0; i < fallback.length; ++i) {
        fallback[i] = chunkData.maxY();
      }
      return fallback;
    }
  }

  /** Detect Minecraft version that generated the chunk. */
  private static ChunkVersion chunkVersion(@NotNull Tag data) {
    Tag sections = getTagFromNames(data, LEVEL_SECTIONS, SECTIONS_POST_21W39A);
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        if (!section.get("Palette").isList()) {
          if (section.get("Blocks").isByteArray(SECTION_BYTES)) {
            return ChunkVersion.PRE_FLATTENING;
          }
        }
      }
      return ChunkVersion.POST_FLATTENING;
    }
    return ChunkVersion.UNKNOWN;
  }

  private static void loadBlockData(@NotNull Tag data, @NotNull ChunkData chunkData,
                                    BlockPalette blockPalette, int minY, int maxY) {

    Tag sections = getTagFromNames(data, LEVEL_SECTIONS, SECTIONS_POST_21W39A);
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        Tag yTag = section.get("Y");
        int sectionY = yTag.byteValue();
        int sectionMinBlockY = sectionY << 4;

        if(sectionY < minY >> 4 || sectionY-1 > (maxY >> 4)+1)
          continue; //skip parsing sections that are outside requested bounds

        Tag blockPaletteTag = getTagFromNames(section, "Palette", "block_states\\palette");
        if (blockPaletteTag.isList()) {
          ListTag localBlockPalette = blockPaletteTag.asList();
          // Bits per block:
          int bpb = 4;
          if (localBlockPalette.size() > 16) {
            bpb = QuickMath.log2(QuickMath.nextPow2(localBlockPalette.size()));
          }

          int dataSize = (4096 * bpb) / 64;
          Tag blockStates = getTagFromNames(section, "BlockStates", "block_states\\data");

          if (blockStates.isLongArray(dataSize)) {
            // since 20w17a, block states are aligned to 64-bit boundaries, so there are 64 % bpb
            // unused bits per block state; if so, the array is longer than the expected data size
            boolean isAligned = data.get(DATAVERSION).intValue() >= DATAVERSION_20W17A;
            if (isAligned) {
              // entries are 64-bit-padded, re-calculate the bits per block
              // this is the dataSize calculation from above reverted, we know the actual data size
              bpb = blockStates.longArray().length / 64;
            }

            int[] subpalette = new int[localBlockPalette.size()];
            int paletteIndex = 0;
            for (Tag item : localBlockPalette.asList()) {
              subpalette[paletteIndex] = blockPalette.put(item);
              paletteIndex += 1;
            }
            BitBuffer buffer = new BitBuffer(blockStates.longArray(), bpb, isAligned);
            for (int y = 0; y < SECTION_Y_MAX; y++) {
              int blockY = sectionMinBlockY + y;
              for (int z = 0; z < Z_MAX; z++) {
                for(int x = 0; x < X_MAX; x++) {
                  int b0 = buffer.read();
                  if (b0 < subpalette.length) {
                    chunkData.setBlockAt(x, blockY, z, subpalette[b0]);
                  }
                }
              }
            }
          } else {
            // Single block palette
            if (localBlockPalette.size() == 1) {
              // Check it is not air block
              int block = blockPalette.put(localBlockPalette.get(0));
              if (block != blockPalette.airId) {
                // Set the entire section
                for (int y = 0; y < SECTION_Y_MAX; y++) {
                  int blockY = sectionMinBlockY + y;
                  for (int z = 0; z < Z_MAX; z++) {
                    for(int x = 0; x < X_MAX; x++) {
                      chunkData.setBlockAt(x, blockY, z, block);
                    }
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

  public synchronized void getChunkData(@NotNull Mutable<ChunkData> reuseChunkData, BlockPalette palette, BiomePalette biomePalette, int minY, int maxY) throws ChunkLoadingException {
    Set<String> request = new HashSet<>();
    request.add(DATAVERSION);
    request.add(LEVEL_SECTIONS);
    request.add(SECTIONS_POST_21W39A);
    request.add(LEVEL_BIOMES);
    request.add(BIOMES_POST_21W39A);
    request.add(LEVEL_ENTITIES);
    request.add(LEVEL_TILEENTITIES);
    request.add(BLOCK_ENTITIES_POST_21W43A);
    Map<String, Tag> dataMap = getChunkTags(request);
    // TODO: improve error handling here.
    if (dataMap == null) {
      throw new ChunkLoadingException(String.format("Got null data for chunk %s", this.position));
    }
    Tag data = tagFromMap(dataMap);

    int dataVersion = data.get(DATAVERSION).intValue();

    IntIntImmutablePair chunkBounds = inclusiveChunkBounds(data);

    if(reuseChunkData.get() == null || reuseChunkData.get() instanceof EmptyChunkData) {
      reuseChunkData.set(dimension.createChunkData(reuseChunkData.get(), chunkBounds.leftInt(), chunkBounds.rightInt()));
    } else {
      reuseChunkData.get().clear();
    }
    ChunkData chunkData = reuseChunkData.get(); //unwrap mutable, for ease of use

    version = chunkVersion(data);
    Tag sections = getTagFromNames(data, LEVEL_SECTIONS, SECTIONS_POST_21W39A);
    Tag entitiesTag = data.get(LEVEL_ENTITIES);
    Tag tileEntitiesTag = getTagFromNames(data, LEVEL_TILEENTITIES, BLOCK_ENTITIES_POST_21W43A);

    BiomeDataFactory.loadBiomeData(chunkData, data, biomePalette, minY, maxY);
    if (sections.isList()) {
      loadBlockData(data, chunkData, palette, minY, maxY);

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

    // post 20w45A entities
    if (dataVersion >= DATAVERSION_20W45A) {
      Set<String> entitiesRequest = new HashSet<>();
      entitiesRequest.add(ENTITIES_POST_20W45A);

      Map<String, Tag> entitiesMap = getEntityTags(entitiesRequest);
      if (entitiesMap != null) {
        entitiesTag = entitiesMap.get(".Entities");
        if (entitiesTag.isList()) {
          for (SpecificTag tag : (ListTag) entitiesTag) {
            if (tag.isCompoundTag())
              chunkData.addEntity((CompoundTag) tag);
          }
        }
      }
    }
  }


  /**
   * @return The min and max blockY for a given section array
   */
  private IntIntImmutablePair inclusiveChunkBounds(Tag chunkData) {
    Tag sections = getTagFromNames(chunkData, LEVEL_SECTIONS, SECTIONS_POST_21W39A);
    int minSectionY = Integer.MAX_VALUE;
    int maxSectionY = Integer.MIN_VALUE;
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        byte sectionY = (byte) section.get("Y").byteValue();
        if (sectionY < minSectionY) {
          minSectionY = sectionY;
        }
        if (sectionY > maxSectionY) {
          maxSectionY = sectionY;
        }
      }
    }

    return new IntIntImmutablePair(minSectionY << 4, (maxSectionY << 4) + 15);
  }


  /**
   * @return The version of this chunk.
   */
  public ChunkVersion getVersion() {
    return version;
  }
}
