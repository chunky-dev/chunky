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
  public static final String LEVEL_HEIGHTMAPS = ".Level.Heightmaps";
  public static final String LEVEL_SECTIONS = ".Level.Sections";
  public static final String LEVEL_BIOMES = ".Level.Biomes";
  private static final String LEVEL_ENTITIES = ".Level.Entities";
  private static final String LEVEL_TILEENTITIES = ".Level.TileEntities";

  /**
   * Describes which long to get a value at index I for values per bits N
   * The MAGIC values come in triples
   * longer explanation above {@link Chunk#extractHeightmapPost20w17a}
   */
  private static final int[] MAGIC = new int[]{-1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE, 0, 5};


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
  public synchronized void loadChunk(ChunkData chunkData) {
    if (!shouldReloadChunk()) {
      return;
    }

    Set<String> request = new HashSet<>();
    request.add(Chunk.DATAVERSION);
    request.add(Chunk.LEVEL_SECTIONS);
    request.add(Chunk.LEVEL_BIOMES);
    request.add(Chunk.LEVEL_HEIGHTMAP);
    request.add(Chunk.LEVEL_HEIGHTMAPS);
    Map<String, Tag> data = getChunkData(request);
    // TODO: improve error handling here.
    if (data == null) {
      return;
    }

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
    }
    Tag heightmapsTag = data.get(LEVEL_HEIGHTMAPS);
    if(heightmapsTag.isCompoundTag()) {
      Tag world_surface = heightmapsTag.asCompound().get("WORLD_SURFACE");
      if(world_surface.isLongArray(0)) {
        if (data.get(DATAVERSION).intValue() >= DATAVERSION_20w17a) { //elements are not packed between longs
          return extractHeightmapPost20w17a(world_surface.longArray());
        } else { //elements are packed between longs
          return extractHeightmapPre20w17a(world_surface.longArray());
        }
      }
    }

    int[] fallback = new int[X_MAX * Z_MAX];
    for (int i = 0; i < fallback.length; ++i) {
      fallback[i] = chunkData.maxY()-1;
    }
    return fallback;
  }

  /**
   * This is an explanation of heightmap indexing
   * height values are stored in a long array, with exact numbers of bits to store the height range of the world
   * The 20w17a version differs to the one previous as it prevents values spanning over two longs in the array, where
   * the previous implementation allowed it. The reason for doing this is most likely choosing game performance over
   * optimum region file size
   *
   * to give an example: for an element with, 10 bits per value, therefore 6 values per long
   * MAGIC values for that: 715827882, 715827882, 0
   * to get index 45 we'd do:
   * longIdx = 45L * 715827882L + 715827882L >> Integer.SIZE >> 0
   * longVal = longArray[longIdx]
   * rightShift = (45 - longIdx * 6) * 10
   * so our value is: (int) (longVal >> rightShift & (1 << 10) - 1)
   */
  private static int[] extractHeightmapPost20w17a(long[] heightmapBitArray) {
    int[] heightmap = new int[Chunk.X_MAX * Chunk.Z_MAX];
    int elementBits = heightmapBitArray.length * Long.SIZE / (Chunk.X_MAX * Chunk.Z_MAX);
    int elementMask = (1 << elementBits) - 1;
    int valuesPerLong = Long.SIZE / elementBits;

    int magicIdx = 3 * (valuesPerLong - 1);
    long mul = ((long) MAGIC[magicIdx + 0]) & 0xffffffffL;
    long add = ((long) MAGIC[magicIdx + 1]) & 0xffffffffL;
    long shift = ((long) MAGIC[magicIdx + 2]) & 0xffffffffL;

    int index = 0;
    for (int x = 0; x < X_MAX; x++) {
      for (int z = 0; z < Z_MAX; z++) {
        int i = (int) ((long) index * mul + add >> 32 >> shift);
        long l = heightmapBitArray[i];
        int rightShift = (index - i * valuesPerLong) * elementBits;
        int v = (int) (l >> rightShift & elementMask);
        heightmap[x * X_MAX + z] = v;
        index++;
      }
    }
    return heightmap;
  }

  private static int[] extractHeightmapPre20w17a(long[] heightmapBitArray) {
    int[] heightmap = new int[Chunk.X_MAX * Chunk.Z_MAX];
    int elementBits = heightmapBitArray.length * Long.SIZE / (Chunk.X_MAX * Chunk.Z_MAX);
    int elementMask = (1 << elementBits) - 1;

    int index = 0;
    for (int x = 0; x < X_MAX; x++) {
      for (int z = 0; z < Z_MAX; z++) {
        int startBit = index * elementBits;
        int firstIdx = startBit >> 6;
        int secondIdx = (index + 1) * elementBits - 1 >> 6;
        int firstRightShift = startBit ^ firstIdx << 6;
        if (firstIdx == secondIdx) {
          heightmap[x * X_MAX + z] = (int) (heightmapBitArray[firstIdx] >>> firstRightShift & elementMask);
        } else {
          int secondLeftShift = 64 - firstRightShift;
          heightmap[x * X_MAX + z] = (int) ((heightmapBitArray[firstIdx] >>> firstRightShift | heightmapBitArray[secondIdx] << secondLeftShift) & elementMask);
        }
        index++;
      }
    }
    return heightmap;
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
   * @param reuseChunkData to be reused, if null one is created
   * @param palette
   */
  public synchronized ChunkData getChunkData(ChunkData reuseChunkData, BlockPalette palette) {
    Set<String> request = new HashSet<>();
    request.add(DATAVERSION);
    request.add(LEVEL_SECTIONS);
    request.add(LEVEL_BIOMES);
    request.add(LEVEL_ENTITIES);
    request.add(LEVEL_TILEENTITIES);
    Map<String, Tag> data = getChunkData(request);
    if(reuseChunkData == null) {
      reuseChunkData = new GenericChunkData();
    } else {
      reuseChunkData.clear();
    }
    // TODO: improve error handling here.
    if (data == null) {
      return reuseChunkData;
    }
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
