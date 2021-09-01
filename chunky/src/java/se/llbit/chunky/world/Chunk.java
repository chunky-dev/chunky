/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.world;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.llbit.chunky.block.*;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.EmptyChunkData;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.map.AbstractLayer;
import se.llbit.chunky.map.BiomeLayer;
import se.llbit.chunky.map.IconLayer;
import se.llbit.chunky.map.MapTile;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.math.QuickMath;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.Tag;
import se.llbit.util.BitBuffer;
import se.llbit.util.Mutable;
import se.llbit.util.NotNull;

/**
 * This class represents a loaded or not-yet-loaded chunk in the world.
 * <p>
 * If the chunk is not yet loaded the loadedLayer field is equal to -1.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class Chunk {

  public static final String DATAVERSION = ".DataVersion";
  public static final String LEVEL_HEIGHTMAP = ".Level.HeightMap";
  public static final String LEVEL_SECTIONS = ".Level.Sections";
  public static final String LEVEL_BIOMES = ".Level.Biomes";
  private static final String LEVEL_ENTITIES = ".Level.Entities";
  private static final String LEVEL_TILEENTITIES = ".Level.TileEntities";

  /** Chunk width. */
  public static final int X_MAX = 16;

  /** Chunk height. */
  public static final int Y_MAX = 256;

  /** Chunk depth. */
  public static final int Z_MAX = 16;

  public static final int SECTION_Y_MAX = 16;
  private static final int SECTION_BYTES = X_MAX * SECTION_Y_MAX * Z_MAX;
  private static final int SECTION_HALF_NIBBLES = SECTION_BYTES / 2;
  private static final int CHUNK_BYTES = X_MAX * Y_MAX * Z_MAX;

  private static final int DATAVERSION_20w17a = 2529;

  private final ChunkPosition position;
  protected volatile AbstractLayer surface = IconLayer.UNKNOWN;
  protected volatile AbstractLayer biomes = IconLayer.UNKNOWN;

  private final World world;

  private int dataTimestamp = 0;
  private int surfaceTimestamp = 0;
  private int biomesTimestamp = 0;

  private String version;

  public Chunk(ChunkPosition pos, World world) {
    this.world = world;
    this.position = pos;
  }

  public void renderSurface(MapTile tile) {
    surface.render(tile);
  }

  public void renderBiomes(MapTile tile) {
    biomes.render(tile);
  }

  public int biomeColor() {
    return biomes.getAvgColor();
  }

  /**
   * @param request fresh request set
   * @return loaded data, or null if something went wrong
   */
  private Map<String, Tag> getChunkTags(Set<String> request) {
    MCRegion region = (MCRegion) world.getRegion(position.getRegionPosition());
    Mutable<Integer> timestamp = new Mutable<>(dataTimestamp);
    Map<String, Tag> chunkTags = region.getChunkTags(this.position, request, timestamp);
    this.dataTimestamp = timestamp.get();
    return chunkTags;
  }

  /**
   * Reset the rendered layers in this chunk.
   */
  public synchronized void reset() {
    surface = IconLayer.UNKNOWN;
  }

  /**
   * @return The position of this chunk
   */
  public ChunkPosition getPosition() {
    return position;
  }

  /**
   * Parse the chunk from the region file and render the current
   * layer, surface and cave maps.
   * @return whether the input chunkdata was modified
   */
  public synchronized boolean loadChunk(ChunkData chunkData, int yMax) {
    if (!shouldReloadChunk()) {
      return false;
    }

    Set<String> request = new HashSet<>();
    request.add(Chunk.DATAVERSION);
    request.add(Chunk.LEVEL_SECTIONS);
    request.add(Chunk.LEVEL_BIOMES);
    request.add(Chunk.LEVEL_HEIGHTMAP);
    Map<String, Tag> data = getChunkTags(request);
    // TODO: improve error handling here.
    if (data == null) {
      return false;
    }

    surfaceTimestamp = dataTimestamp;
    version = chunkVersion(data);
    loadSurface(data, chunkData, yMax);
    biomesTimestamp = dataTimestamp;
    if (surface == IconLayer.MC_1_12) {
      biomes = IconLayer.MC_1_12;
    } else {
      loadBiomes(data, chunkData);
    }
    world.chunkUpdated(position);
    return true;
  }

  private void loadSurface(Map<String, Tag> data, ChunkData chunkData, int yMax) {
    if (data == null) {
      surface = IconLayer.CORRUPT;
      return;
    }

    Heightmap heightmap = world.heightmap();
    Tag sections = data.get(LEVEL_SECTIONS);
    if (sections.isList()) {
      extractBiomeData(data.get(LEVEL_BIOMES), chunkData);
      if (version.equals("1.13") || version.equals("1.12")) {
        BlockPalette palette = new BlockPalette();
        loadBlockData(data, chunkData, palette);
        int[] heightmapData = extractHeightmapData(data, chunkData);
        updateHeightmap(heightmap, position, chunkData, heightmapData, palette, yMax);
        surface = new SurfaceLayer(world.currentDimension(), chunkData, palette, yMax);
        queueTopography();
      }
    } else {
      surface = IconLayer.CORRUPT;
    }
  }

  private void loadBiomes(Map<String, Tag> data, ChunkData chunkData) {
    if (data == null) {
      biomes = IconLayer.CORRUPT;
    } else {
      extractBiomeData(data.get(LEVEL_BIOMES), chunkData);
      biomes = new BiomeLayer(chunkData);
    }
  }

  /**
   * Extracts biome IDs from chunk data into the second argument.
   *
   * @param biomesTag the .Level.Biomes NBT tag to load data from.
   * @param output a byte array of length 16x16.
   */
  private void extractBiomeData(@NotNull Tag biomesTag, ChunkData output) {
    if (biomesTag.isByteArray(X_MAX * Z_MAX)) {
      byte[] data = biomesTag.byteArray();
      int i = 0;
      for(int z = 0; z < Z_MAX; z++) {
        for(int x = 0; x < X_MAX; x++) {
          output.setBiomeAt(x, 0, z, data[i]);
          i++;
        }
      }
    } else if (biomesTag.isIntArray(1024)) {
      // Since Minecraft 1.15, biome IDs are stored in an int vector with 1024 entries.
      // Each entry stores the biome for a 4x4x4 cube, sorted by X, Z, Y. For now, we use the biome at y=0.
      int[] data = biomesTag.intArray();
      for (int x = 0; x < 4; x++) {
        for (int z = 0; z < 4; z++) {
          for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
              output.setBiomeAt((x * 4 + i), 0, z * 4 + j, (byte) data[z * 4 + x]);
            }
          }
        }
      }
      // TODO add support for different biomes in the same XZ coordinate (i.e. for the nether)
    } else if (biomesTag.isIntArray(X_MAX * Z_MAX)) {
      // Since Minecraft 1.13, biome IDs are stored in an int vector with 256 entries (one for each XZ position).
      // TODO(llbit): do we need to use ints to store biome IDs for Minecraft 1.13+? (not yet, the highest ID is 173)
      int[] data = biomesTag.intArray();
      int i = 0;
      for(int z = 0; z < Z_MAX; z++) {
        for(int x = 0; x < X_MAX; x++) {
          output.setBiomeAt(x, 0, z, (byte) data[i]);
          i++;
        }
      }
    }
  }

  private int[] extractHeightmapData(@NotNull Map<String, Tag> data, ChunkData chunkData) {
    Tag heightmapTag = data.get(LEVEL_HEIGHTMAP);
    if (heightmapTag.isIntArray(X_MAX * Z_MAX)) {
      return heightmapTag.intArray();
    } else {
      int[] fallback = new int[X_MAX * Z_MAX];
      for (int i = 0; i < fallback.length; ++i) {
        fallback[i] = chunkData.maxY()-1;
      }
      return fallback;
    }
  }

  /** Detect Minecraft version that generated the chunk. */
  private static String chunkVersion(@NotNull Map<String, Tag> data) {
    Tag sections = data.get(LEVEL_SECTIONS);
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        if (!section.get("Palette").isList()) {
          if (section.get("Blocks").isByteArray(SECTION_BYTES)) {
            return "1.12";
          }
        }
      }
      return "1.13";
    }
    return "?";
  }

  private static void loadBlockData(@NotNull Map<String, Tag> data, @NotNull ChunkData chunkData,
      BlockPalette blockPalette) {
    Tag sections = data.get(LEVEL_SECTIONS);
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        Tag yTag = section.get("Y");
        int sectionY = yTag.byteValue();
        int sectionMinBlockY = sectionY << 4;

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
                for(int x = 0; x < X_MAX; x++) {
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

  /**
   * Load heightmap information from a chunk heightmap array
   * and insert into a quadtree.
   */
  public static void updateHeightmap(Heightmap heightmap, ChunkPosition pos, ChunkData chunkData,
      int[] chunkHeightmap, BlockPalette palette, int yMax) {
    for (int x = 0; x < 16; ++x) {
      for (int z = 0; z < 16; ++z) {
        int y = chunkHeightmap[z * 16 + x];
        y = Math.max(1, Math.min(y - 1, yMax));
        for (; y > 1; --y) {
          Block block = palette.get(chunkData.getBlockAt(x, y, z));
          if (block != Air.INSTANCE && !block.isWater())
            break;
        }
        heightmap.set(y, pos.x * 16 + x, pos.z * 16 + z);
      }
    }
  }

  private boolean shouldReloadChunk() {
    int timestamp = Integer.MAX_VALUE;
    timestamp = Math.min(timestamp, surfaceTimestamp);
    timestamp = Math.min(timestamp, biomesTimestamp);
    if (timestamp == 0) {
      return true;
    }
    Region region = world.getRegion(position.getRegionPosition());
    return region.chunkChangedSince(position, timestamp);
  }

  private void queueTopography() {
    for (int x = -1; x <= 1; ++x) {
      for (int z = -1; z <= 1; ++z) {
        ChunkPosition pos = ChunkPosition.get(position.x + x, position.z + z);
        Chunk chunk = world.getChunk(pos);
        if (!chunk.isEmpty()) {
          world.chunkTopographyUpdated(chunk);
        }
      }
    }
  }

  public static int waterLevelAt(ChunkData chunkData, BlockPalette palette, int cx, int cy, int cz,
                                 int baseLevel) {
    Material corner = palette.get(chunkData.getBlockAt(cx, cy, cz));
    if (corner.isWater()) {
      Material above = palette.get(chunkData.getBlockAt(cx, cy+1, cz));
      boolean isFullBlock = above.isWaterFilled();
      return isFullBlock ? 8 : 8 - ((Water) corner).level;
    } else if (corner.waterlogged) {
      return 8;
    } else if (!corner.solid) {
      return 0;
    }
    return baseLevel;
  }

  public static int lavaLevelAt(ChunkData chunkData, BlockPalette palette, int cx, int cy, int cz,
                                int baseLevel) {
    Material corner = palette.get(chunkData.getBlockAt(cx, cy, cz));
    if (corner instanceof Lava) {
      Material above = palette.get(chunkData.getBlockAt(cx, cy+1, cz));
      boolean isFullBlock = above instanceof Lava;
      return isFullBlock ? 8 : 8 - ((Lava) corner).level;
    } else if (!corner.solid) {
      return 0;
    }
    return baseLevel;
  }

  /**
   * @return <code>true</code> if this is an empty (non-existing) chunk
   */
  public boolean isEmpty() {
    return false;
  }

  /**
   * Render the topography of this chunk.
   */
  public synchronized void renderTopography() {
    surface.renderTopography(position, world.heightmap());
    world.chunkUpdated(position);
  }

  /**
   * Load the block data for this chunk.
   *
   * @param reuseChunkData ChunkData object to be reused, if null one is created
   * @param palette Block palette
   * @return Loaded chunk data, guaranteed to be reuseChunkData unless null or EmptyChunkData was passed
   */
  public synchronized ChunkData getChunkData(ChunkData reuseChunkData, BlockPalette palette) {
    Set<String> request = new HashSet<>();
    request.add(DATAVERSION);
    request.add(LEVEL_SECTIONS);
    request.add(LEVEL_BIOMES);
    request.add(LEVEL_ENTITIES);
    request.add(LEVEL_TILEENTITIES);
    Map<String, Tag> data = getChunkTags(request);
    if(reuseChunkData == null || reuseChunkData instanceof EmptyChunkData) {
      reuseChunkData = new GenericChunkData();
    } else {
      reuseChunkData.clear();
    }
    // TODO: improve error handling here.
    if (data == null) {
      return reuseChunkData;
    }
    version = chunkVersion(data);
    Tag sections = data.get(LEVEL_SECTIONS);
    Tag biomesTag = data.get(LEVEL_BIOMES);
    Tag entitiesTag = data.get(LEVEL_ENTITIES);
    Tag tileEntitiesTag = data.get(LEVEL_TILEENTITIES);
    if (biomesTag.isByteArray(X_MAX * Z_MAX) || biomesTag.isIntArray(X_MAX * Z_MAX)) {
      extractBiomeData(biomesTag, reuseChunkData);
    }

    if (sections.isList()) {
      loadBlockData(data, reuseChunkData, palette);

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

  /**
   * @return Integer index into a chunk YXZ array
   */
  public static int chunkIndex(int x, int y, int z) {
    return x + Chunk.X_MAX * (z + Chunk.Z_MAX * y);
  }

  /**
   * @return Integer index into a chunk XZ array
   */
  public static int chunkXZIndex(int x, int z) {
    return x + Chunk.X_MAX * z;
  }

  @Override public String toString() {
    return "Chunk: " + position.toString();
  }

  public String biomeAt(int blockX, int blockZ) {
    if (biomes instanceof BiomeLayer) {
      BiomeLayer biomeLayer = (BiomeLayer) biomes;
      return biomeLayer.biomeAt(blockX, blockZ);
    } else {
      return "unknown";
    }
  }

  /**
   * @return The version of this chunk (1.12, 1.13 or ?)
   */
  public String getVersion() {
    return version;
  }
}
