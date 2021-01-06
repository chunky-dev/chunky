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

import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.Lava;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.map.AbstractLayer;
import se.llbit.chunky.map.BiomeLayer;
import se.llbit.chunky.map.IconLayer;
import se.llbit.chunky.map.MapTile;
import se.llbit.chunky.map.SurfaceLayer;
import se.llbit.math.QuickMath;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ErrorTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.Tag;
import se.llbit.util.BitBuffer;
import se.llbit.util.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

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
  private Map<String, Tag> getChunkData(Set<String> request) {
    Region region = world.getRegion(position.getRegionPosition());
    ChunkDataSource data = region.getChunkData(position);
    dataTimestamp = data.timestamp;
    if (data.inputStream != null) {
      try (DataInputStream in = data.inputStream) {
        Map<String, Tag> result = NamedTag.quickParse(in, request);
        for (String key : request) {
          if (!result.containsKey(key)) {
            result.put(key, new ErrorTag(""));
          }
        }
        return result;
      } catch (IOException e) {
        // Ignored.
      }
    }
    return null;
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
   */
  public synchronized void loadChunk() {
    if (!shouldReloadChunk()) {
      return;
    }

    Set<String> request = new HashSet<>();
    request.add(Chunk.DATAVERSION);
    request.add(Chunk.LEVEL_SECTIONS);
    request.add(Chunk.LEVEL_BIOMES);
    request.add(Chunk.LEVEL_HEIGHTMAP);
    Map<String, Tag> data = getChunkData(request);
    // TODO: improve error handling here.
    if (data == null) {
      return;
    }
    ChunkData chunkData = new GenericChunkData();

    surfaceTimestamp = dataTimestamp;
    version = chunkVersion(data);
    loadSurface(data, chunkData);
    biomesTimestamp = dataTimestamp;
    if (surface == IconLayer.MC_1_12) {
      biomes = IconLayer.MC_1_12;
    } else {
      loadBiomes(data, chunkData);
    }
    world.chunkUpdated(position);
  }

  private void loadSurface(Map<String, Tag> data, ChunkData chunkData) {
    if (data == null) {
      surface = IconLayer.CORRUPT;
      return;
    }

    Heightmap heightmap = world.heightmap();
    Tag sections = data.get(LEVEL_SECTIONS);
    if (sections.isList()) {
      extractBiomeData(data.get(LEVEL_BIOMES), chunkData);
      if (version.equals("1.13")) {
        BlockPalette palette = new BlockPalette();
        loadBlockData(data, chunkData, palette);
        int[] heightmapData = extractHeightmapData(data, chunkData);
        updateHeightmap(heightmap, position, chunkData, heightmapData, palette);
        surface = new SurfaceLayer(world.currentDimension(), chunkData, palette);
        queueTopography();
      } else if (version.equals("1.12")) {
        surface = IconLayer.MC_1_12;
      }
    } else {
      surface = IconLayer.CORRUPT;
    }
  }

  private void loadBiomes(Map<String, Tag> data, ChunkData chunkData) {
    if (data == null) {
      biomes = IconLayer.CORRUPT;
    } else {
      byte[] biomeData = new byte[X_MAX * Z_MAX];
      extractBiomeData(data.get(LEVEL_BIOMES), chunkData);
      biomes = new BiomeLayer(biomeData);
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
        fallback[i] = chunkData.maxY();
      }
      return fallback;
    }
  }

  /** Detect Minecraft version that generated the chunk. */
  private static String chunkVersion(@NotNull Map<String, Tag> data) {
    Tag sections = data.get(LEVEL_SECTIONS);
    String version = "?";
    if (sections.isList()) {
      version = "1.13";
      for (SpecificTag section : sections.asList()) {
        if (!section.get("Palette").isList()) {
          if (!version.equals("?") && section.get("Blocks").isByteArray(SECTION_BYTES)) {
            version = "1.12";
          }
        }
      }
    }
    return version;
  }

  private static void loadBlockData(@NotNull Map<String, Tag> data, @NotNull ChunkData chunkData,
      BlockPalette blockPalette) {
    Tag sections = data.get(LEVEL_SECTIONS);
    if (sections.isList()) {
      for (SpecificTag section : sections.asList()) {
        Tag yTag = section.get("Y");
        int sectionY = yTag.byteValue() & 0xFF;
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
          //Log.error(">>> WIP <<< Old chunk format temp disabled.");
          /*Tag blocksTag = section.get("Blocks");
          if (blocksTag.isByteArray(SECTION_BYTES)) {
            System.arraycopy(blocksTag.byteArray(), 0, blocks, SECTION_BYTES * yOffset,
                SECTION_BYTES);
          }
          Tag dataTag = section.get("Data");
          if (dataTag.isByteArray(SECTION_HALF_NIBBLES)) {
            System.arraycopy(dataTag.byteArray(), 0, blockData, SECTION_HALF_NIBBLES * sectionY,
                SECTION_HALF_NIBBLES);
          }*/
        }
      }
    }
  }

  /**
   * Load heightmap information from a chunk heightmap array
   * and insert into a quadtree.
   */
  public static void updateHeightmap(Heightmap heightmap, ChunkPosition pos, ChunkData chunkData,
      int[] chunkHeightmap, BlockPalette palette) {
    for (int x = 0; x < 16; ++x) {
      for (int z = 0; z < 16; ++z) {
        int y = chunkHeightmap[z * 16 + x];
        y = Math.max(1, y - 1);
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
   * @param chunkData to be reused, if null one is created
   * @param palette
   */
  public synchronized ChunkData getChunkData(ChunkData chunkData, BlockPalette palette) {
    Set<String> request = new HashSet<>();
    request.add(DATAVERSION);
    request.add(LEVEL_SECTIONS);
    request.add(LEVEL_BIOMES);
    request.add(LEVEL_ENTITIES);
    request.add(LEVEL_TILEENTITIES);
    Map<String, Tag> data = getChunkData(request);
    if(chunkData == null) {
      chunkData = new GenericChunkData();
    } else {
      chunkData.clear();
    }
    // TODO: improve error handling here.
    if (data == null) {
      return chunkData;
    }
    Tag sections = data.get(LEVEL_SECTIONS);
    Tag biomesTag = data.get(LEVEL_BIOMES);
    Tag entitiesTag = data.get(LEVEL_ENTITIES);
    Tag tileEntitiesTag = data.get(LEVEL_TILEENTITIES);
    if (biomesTag.isByteArray(X_MAX * Z_MAX) || biomesTag.isIntArray(X_MAX * Z_MAX)) {
      extractBiomeData(biomesTag, chunkData);
    }

    if (sections.isList()) {
      loadBlockData(data, chunkData, palette);

      if (entitiesTag.isList()) {
        Collection<CompoundTag> entities = chunkData.getEntities();
        for (SpecificTag tag : (ListTag) entitiesTag) {
          if (tag.isCompoundTag())
            entities.add((CompoundTag) tag);
        }
      }

      if (tileEntitiesTag.isList()) {
        Collection<CompoundTag> tileEntities = chunkData.getTileEntities();
        for (SpecificTag tag : (ListTag) tileEntitiesTag) {
          if (tag.isCompoundTag())
            tileEntities.add((CompoundTag) tag);
        }
      }
    }
    return chunkData;
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
