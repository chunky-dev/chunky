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

import se.llbit.chunky.block.minecraft.Lava;
import se.llbit.chunky.block.minecraft.Water;
import se.llbit.chunky.chunk.BlockPalette;
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
  public static void finalizeChunk(Octree worldTree, Octree waterTree, BlockPalette palette,
      Set<ChunkPosition> loadedChunks, Vector3i origin, ChunkPosition cp, int yMin, int yMax) {
    for (int cy = yMin; cy < yMax; ++cy) {
      for (int cz = 0; cz < 16; ++cz) {
        int z = cz + cp.z * 16 - origin.z;
        for (int cx = 0; cx < 16; ++cx) {
          int x = cx + cp.x * 16 - origin.x;
          // process blocks that are at the edge of the chunk, the other should have be taken care of during the loading
          if (cy == yMin || cy == yMax - 1 || cz == 0 || cz == 15 || cx == 0 || cx == 15) {
            hideBlocks(worldTree, palette, x, cy, z, yMin, yMax, origin);
            processBlock(worldTree, waterTree, palette, loadedChunks, x, cy, z, origin);
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
          worldTree.getMaterial(x - 1, y, z, palette).opaque
              && worldTree.getMaterial(x + 1, y, z, palette).opaque
              && worldTree.getMaterial(x, y, z - 1, palette).opaque
              && worldTree.getMaterial(x, y, z + 1, palette).opaque
              && worldTree.getMaterial(x, y - 1, z, palette).opaque
              && worldTree.getMaterial(x, y + 1, z, palette).opaque;
      if (isHidden) {
        worldTree.set(BlockPalette.ANY_ID, x, y, z);
      }
    }
  }

  private static void processBlock(Octree worldTree, Octree waterTree, BlockPalette palette,
      Set<ChunkPosition> loadedChunks, int x, int cy, int z, Vector3i origin) {
    int y = cy - origin.y;
    Material mat = worldTree.getMaterial(x, y, z, palette);
    Material wmat = waterTree.getMaterial(x, y, z, palette);

    if (wmat instanceof Water) {
      Material above = waterTree.getMaterial(x, y + 1, z, palette);
      Material aboveBlock = worldTree.getMaterial(x, y + 1, z, palette);
      int level0 = 8 - ((Water) wmat).level;
      if (!above.isWaterFilled() && !aboveBlock.solid) {
        int corner0 = level0;
        int corner1 = level0;
        int corner2 = level0;
        int corner3 = level0;

        int level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x - 1, y, z, level0);
        corner3 += level;
        corner0 += level;

        level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x - 1, y, z + 1, level0);
        corner0 += level;

        level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x, y, z + 1, level0);
        corner0 += level;
        corner1 += level;

        level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x + 1, y, z + 1, level0);
        corner1 += level;

        level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x + 1, y, z, level0);
        corner1 += level;
        corner2 += level;

        level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x + 1, y, z - 1, level0);
        corner2 += level;

        level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x, y, z - 1, level0);
        corner2 += level;
        corner3 += level;

        level = waterLevelAt(worldTree, waterTree, palette, loadedChunks, x - 1, y, z - 1, level0);
        corner3 += level;

        corner0 = Math.min(7, 8 - (corner0 / 4));
        corner1 = Math.min(7, 8 - (corner1 / 4));
        corner2 = Math.min(7, 8 - (corner2 / 4));
        corner3 = Math.min(7, 8 - (corner3 / 4));

        waterTree.set(palette.getWaterId(((Water) wmat).level, (corner0 << Water.CORNER_0)
            | (corner1 << Water.CORNER_1)
            | (corner2 << Water.CORNER_2)
            | (corner3 << Water.CORNER_3)), x, y, z);
      } else if (above.isWaterFilled()) {
        waterTree.set(palette.getWaterId(0, 1 << Water.FULL_BLOCK), x, y, z);
      }
    } else if (mat instanceof Lava) {
      Material above = worldTree.getMaterial(x, y + 1, z, palette);
      if (!(above instanceof Lava)) {
        Lava lava = (Lava) mat;

        int level0 = 8 - lava.level;
        int corner0 = level0;
        int corner1 = level0;
        int corner2 = level0;
        int corner3 = level0;

        int level = lavaLevelAt(worldTree, palette, loadedChunks, x - 1, y, z, level0);
        corner3 += level;
        corner0 += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x - 1, y, z + 1, level0);
        corner0 += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x, y, z + 1, level0);
        corner0 += level;
        corner1 += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x + 1, y, z + 1, level0);
        corner1 += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x + 1, y, z, level0);
        corner1 += level;
        corner2 += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x + 1, y, z - 1, level0);
        corner2 += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x, y, z - 1, level0);
        corner2 += level;
        corner3 += level;

        level = lavaLevelAt(worldTree, palette, loadedChunks, x - 1, y, z - 1, level0);
        corner3 += level;

        corner0 = Math.min(7, 8 - (corner0 / 4));
        corner1 = Math.min(7, 8 - (corner1 / 4));
        corner2 = Math.min(7, 8 - (corner2 / 4));
        corner3 = Math.min(7, 8 - (corner3 / 4));
        worldTree.set(palette.getLavaId(
            lava.level,
            (corner0 << Water.CORNER_0)
                | (corner1 << Water.CORNER_1)
                | (corner2 << Water.CORNER_2)
                | (corner3 << Water.CORNER_3)
        ), x, y, z);
      }
    }
  }

  private static int waterLevelAt(Octree worldTree, Octree waterTree, BlockPalette palette,
      Set<ChunkPosition> loadedChunks, int x, int cy, int z, int baseLevel) {
    // If the position isn't in a loaded chunk, return the baseLevel to make the edge-of-world water flat
    if (!loadedChunks.contains(new ChunkPosition(x >> 4, z >> 4))) {
      return baseLevel;
    }

    Material corner = waterTree.getMaterial(x, cy, z, palette);
    if (corner instanceof Water) {
      Material above = waterTree.getMaterial(x, cy + 1, z, palette);
      boolean isFullBlock = above.isWaterFilled();
      return isFullBlock ? 8 : 8 - ((Water) corner).level;
    } else if (corner.waterlogged) {
      return 8;
    } else if (!worldTree.getMaterial(x, cy, z, palette).solid) {
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

    Material corner = octree.getMaterial(x, cy, z, palette);
    if (corner instanceof Lava) {
      Material above = octree.getMaterial(x, cy + 1, z, palette);
      boolean isFullBlock = above instanceof Lava;
      return isFullBlock ? 8 : 8 - ((Lava) corner).level;
    } else if (!corner.solid) {
      return 0;
    }
    return baseLevel;
  }
}

