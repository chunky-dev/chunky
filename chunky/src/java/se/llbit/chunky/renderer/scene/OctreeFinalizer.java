/* Copyright (c) 2013-2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.minecraft.Lava;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.model.minecraft.WaterModel;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;
import se.llbit.math.Vector3i;

import java.util.Set;

/**
 * Processes the Octree after it has been loaded and updates block states for blocks that depend on
 * neighbor blocks. Octree finalization is be done after all chunks have been loaded because before
 * then we can't reliably test for neighbor blocks.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class OctreeFinalizer {

  /**
   * Finalize a chunk in the octree.
   *
   * @param worldTree Octree to finalize
   * @param origin    Origin of the octree
   * @param cp        Position of the chunk to finalize
   */
  public static void finalizeChunk(Octree worldTree, BlockPalette palette,
      Set<ChunkPosition> loadedChunks, Vector3i origin, ChunkPosition cp, int yMin, int yMax) {
    for (int cy = yMin; cy < yMax; ++cy) {
      for (int cz = 0; cz < 16; ++cz) {
        int z = cz + cp.z * 16 - origin.z;
        for (int cx = 0; cx < 16; ++cx) {
          int x = cx + cp.x * 16 - origin.x;
          // process blocks that are at the edge of the chunk, the other should have be taken care of during the loading
          if (cy == yMin || cy == yMax - 1 || cz == 0 || cz == 15 || cx == 0 || cx == 15) {
            hideBlocks(worldTree, palette, x, cy, z, yMin, yMax, origin);
            processBlock(worldTree, palette, loadedChunks, x, cy, z, origin);
          }
        }
      }
    }
  }

  private static void hideBlocks(Octree worldTree, BlockPalette palette, int x,
      int cy, int z, int yMin, int yMax, Vector3i origin) {
    // Set non-visible blocks to be any block, in order to merge large patches.
    int y = cy - origin.y;
    if (cy > yMin && cy < yMax - 1) {
      boolean isHidden =
          worldTree.getBlock(x - 1, y, z, palette).opaque
              && worldTree.getBlock(x + 1, y, z, palette).opaque
              && worldTree.getBlock(x, y, z - 1, palette).opaque
              && worldTree.getBlock(x, y, z + 1, palette).opaque
              && worldTree.getBlock(x, y - 1, z, palette).opaque
              && worldTree.getBlock(x, y + 1, z, palette).opaque;
      if (isHidden) {
        worldTree.set(BlockPalette.ANY_ID, x, y, z);
      }
    }
  }

  private static void processBlock(Octree worldTree, BlockPalette palette,
      Set<ChunkPosition> loadedChunks, int x, int cy, int z, Vector3i origin) {
    int y = cy - origin.y;
    Block mat = worldTree.getBlock(x, y, z, palette);

    if (mat instanceof Water) {
      Block above = worldTree.getBlock(x, y + 1, z, palette);
      int level0 = 8 - ((Water) mat).level;
      if (!above.isWaterFilled()) {
        int cornerSW = level0;
        int cornerSE = level0;
        int cornerNE = level0;
        int cornerNW = level0;

        // west
        int level = waterLevelAt(worldTree, palette, loadedChunks, x - 1, y, z, level0);
        cornerNW += level;
        cornerSW += level;

        // south west
        level = waterLevelAt(worldTree, palette, loadedChunks, x - 1, y, z + 1, level0);
        cornerSW += level;

        // south
        level = waterLevelAt(worldTree, palette, loadedChunks, x, y, z + 1, level0);
        cornerSW += level;
        cornerSE += level;

        // south east
        level = waterLevelAt(worldTree, palette, loadedChunks, x + 1, y, z + 1, level0);
        cornerSE += level;

        // east
        level = waterLevelAt(worldTree, palette, loadedChunks, x + 1, y, z, level0);
        cornerSE += level;
        cornerNE += level;

        // north east
        level = waterLevelAt(worldTree, palette, loadedChunks, x + 1, y, z - 1, level0);
        cornerNE += level;

        // north
        level = waterLevelAt(worldTree, palette, loadedChunks, x, y, z - 1, level0);
        cornerNE += level;
        cornerNW += level;

        // north west
        level = waterLevelAt(worldTree, palette, loadedChunks, x - 1, y, z - 1, level0);
        cornerNW += level;

        cornerSW = Math.min(7, 8 - (cornerSW / 4));
        cornerSE = Math.min(7, 8 - (cornerSE / 4));
        cornerNE = Math.min(7, 8 - (cornerNE / 4));
        cornerNW = Math.min(7, 8 - (cornerNW / 4));

        worldTree.set(palette.getWaterId(((Water) mat).level, (cornerSW << WaterModel.CORNER_SW)
            | (cornerSE << WaterModel.CORNER_SE)
            | (cornerNE << WaterModel.CORNER_NE)
            | (cornerNW << WaterModel.CORNER_NW)), x, y, z);
      } else if (above.isWaterFilled()) {
        worldTree.set(palette.getWaterId(0, 1 << Water.FULL_BLOCK_DATA), x, y, z);
      }
    } else if (mat instanceof Lava) {
      Block above = worldTree.getBlock(x, y + 1, z, palette);
      if (!(above instanceof Lava)) {
        Lava lava = (Lava) mat;

        int level0 = 8 - lava.level;
        int cornerSW = level0;
        int cornerSE = level0;
        int cornerNE = level0;
        int cornerNW = level0;

        int level = lavaLevelAt(worldTree, palette, loadedChunks, x - 1, y, z, level0);
        cornerNW += level;
        cornerSW += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x - 1, y, z + 1, level0);
        cornerSW += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x, y, z + 1, level0);
        cornerSW += level;
        cornerSE += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x + 1, y, z + 1, level0);
        cornerSE += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x + 1, y, z, level0);
        cornerSE += level;
        cornerNE += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x + 1, y, z - 1, level0);
        cornerNE += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x, y, z - 1, level0);
        cornerNE += level;
        cornerNW += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x - 1, y, z - 1, level0);
        cornerNW += level;

        cornerSW = Math.min(7, 8 - (cornerSW / 4));
        cornerSE = Math.min(7, 8 - (cornerSE / 4));
        cornerNE = Math.min(7, 8 - (cornerNE / 4));
        cornerNW = Math.min(7, 8 - (cornerNW / 4));
        worldTree.set(palette.getLavaId(
            lava.level,
            (cornerSW << WaterModel.CORNER_SW)
                | (cornerSE << WaterModel.CORNER_SE)
                | (cornerNE << WaterModel.CORNER_NE)
                | (cornerNW << WaterModel.CORNER_NW)
        ), x, y, z);
      }
    }
  }

  private static int waterLevelAt(Octree worldTree, BlockPalette palette,
      Set<ChunkPosition> loadedChunks, int x, int cy, int z, int baseLevel) {
    // If the position isn't in a loaded chunk, return the baseLevel to make the edge-of-world water flat
    if (!loadedChunks.contains(new ChunkPosition(x >> 4, z >> 4))) {
      return baseLevel;
    }

    Block corner = worldTree.getBlock(x, cy, z, palette);
    if (corner.isWater()) {
      Block above = worldTree.getBlock(x, cy + 1, z, palette);
      boolean isFullBlock = above.isWaterFilled();
      return isFullBlock ? 8 : 8 - ((Water) corner).level;
    } else if (corner.isWaterlogged()) {
      return 8;
    } else if (!worldTree.getBlock(x, cy, z, palette).solid) {
      return 0;
    }
    return baseLevel;
  }

  private static int lavaLevelAt(Octree octree, BlockPalette palette,
      Set<ChunkPosition> loadedChunks, int x, int cy, int z, int baseLevel) {
    // If the position isn't in a loaded chunk, return the baseLevel to make the edge-of-world water flat
    if (!loadedChunks.contains(new ChunkPosition(x >> 4, z >> 4))) {
      return baseLevel;
    }

    Block corner = octree.getBlock(x, cy, z, palette);
    if (corner instanceof Lava) {
      Block above = octree.getBlock(x, cy + 1, z, palette);
      boolean isFullBlock = above instanceof Lava;
      return isFullBlock ? 8 : 8 - ((Lava) corner).level;
    } else if (!corner.solid) {
      return 0;
    }
    return baseLevel;
  }
}

