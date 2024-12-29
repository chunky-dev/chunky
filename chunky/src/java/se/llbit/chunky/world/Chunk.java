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

import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.minecraft.Lava;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.chunk.ChunkLoadingException;
import se.llbit.chunky.map.*;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.util.Mutable;
import se.llbit.util.annotation.NotNull;

/**
 * This class represents a loaded or not-yet-loaded chunk in the world.
 * <p>
 * If the chunk is not yet loaded the loadedLayer field is equal to -1.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public abstract class Chunk {
  /** Chunk width. */
  public static final int X_MAX = 16;

  /** Chunk height. */
  public static final int Y_MAX = 256;

  /** Chunk depth. */
  public static final int Z_MAX = 16;

  public static final int SECTION_Y_MAX = 16;

  protected final ChunkPosition position;
  protected volatile AbstractLayer surface = IconLayer.UNKNOWN;
  protected volatile AbstractLayer biomes = IconLayer.UNKNOWN;

  protected final Dimension dimension;

  protected int dataTimestamp = 0;
  protected int surfaceTimestamp = 0;
  protected int biomesTimestamp = 0;

  protected ChunkVersion version = ChunkVersion.UNKNOWN;

  public Chunk(ChunkPosition pos, Dimension dimension) {
    this.dimension = dimension;
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
  public abstract boolean loadChunk(@NotNull Mutable<ChunkData> chunkData, int yMin, int yMax);

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
    surface.renderTopography(position, dimension.getHeightmap());
    dimension.chunkUpdated(position);
  }

  /**
   * Load the block data for this chunk.
   *
   * @param reuseChunkData mutable ChunkData object to be reused, if null one is created
   * @param palette Block palette
   * @param minY The requested minimum Y to be loaded into the chunkData object. The chunk implementation does NOT have to respect it
   * @param maxY The requested maximum Y to be loaded into the chunkData object. The chunk implementation does NOT have to respect it
   * @throws ChunkLoadingException If there is an issue loading the chunk, and it should be aborted
   */
  public abstract void getChunkData(@NotNull Mutable<ChunkData> reuseChunkData, BlockPalette palette, BiomePalette biomePalette, int minY, int maxY) throws ChunkLoadingException;

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

  public String biomeAt(int blockX, int blockZ) {
    if (biomes instanceof BiomeLayer) {
      BiomeLayer biomeLayer = (BiomeLayer) biomes;
      return biomeLayer.biomeAt(blockX, blockZ);
    } else {
      return "unknown";
    }
  }

  /**
   * Load heightmap information from a chunk heightmap array
   * and insert into a quadtree.
   */
  protected static void updateHeightmap(Heightmap heightmap, ChunkPosition pos, ChunkData chunkData,
                                        int[] chunkHeightmap, BlockPalette palette, int yMax) {
    for (int x = 0; x < 16; ++x) {
      for (int z = 0; z < 16; ++z) {
        int y = Math.max(chunkData.minY()+1, Math.min(chunkHeightmap[z * 16 + x] - 1, yMax));
        for (; y > chunkData.minY()+1; --y) {
          Block block = palette.get(chunkData.getBlockAt(x, y, z));
          if (block != Air.INSTANCE && !block.isWater())
            break;
        }
        heightmap.set(y, pos.x * 16 + x, pos.z * 16 + z);
      }
    }
  }

  protected boolean shouldReloadChunk() {
    int timestamp = Integer.MAX_VALUE;
    timestamp = Math.min(timestamp, surfaceTimestamp);
    timestamp = Math.min(timestamp, biomesTimestamp);
    if (timestamp == 0) {
      return true;
    }
    return dimension.chunkChangedSince(position, timestamp);
  }

  protected void queueTopography() {
    for (int x = -1; x <= 1; ++x) {
      for (int z = -1; z <= 1; ++z) {
        ChunkPosition pos = new ChunkPosition(position.x + x, position.z + z);
        Chunk chunk = dimension.getChunk(pos);
        if (!chunk.isEmpty()) {
          dimension.chunkTopographyUpdated(chunk);
        }
      }
    }
  }

  @Override public String toString() {
    return "Chunk: " + position.toString();
  }
}
