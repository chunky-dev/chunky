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

import se.llbit.chunky.block.*;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;
import se.llbit.math.Vector3i;

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
                                   Vector3i origin, ChunkPosition cp, int yMin, int yMax) {
    OctreeFinalizationState finalizerState = new OctreeFinalizationState(worldTree, waterTree, palette, yMin, yMax, origin);
    for (int cy = yMin; cy < yMax; ++cy) {
      for (int cz = 0; cz < 16; ++cz) {
        int z = cz + cp.z * 16;
        for (int cx = 0; cx < 16; ++cx) {
          int x = cx + cp.x * 16;
          // process blocks that are at the edge of the chunk, the other should have be taken care of during the loading
          if (cy == yMin || cy == yMax - 1 || cz == 0 || cz == 15 || cx == 0 || cx == 15) {
            finalizerState.setPosition(x, cy, z);
            hideBlocks(finalizerState);
            processBlock(finalizerState);
          }
        }
      }
    }
  }

  private static void hideBlocks(FinalizationState finalizationState) {
    // Set non-visible blocks to be any block, in order to merge large patches.
    if (finalizationState.getY() > finalizationState.getYMin() && finalizationState.getY() < finalizationState.getYMax() - 1
      && !finalizationState.isCurrentBlockVisible()) {
      finalizationState.replaceCurrentBlock(BlockPalette.ANY_ID);
    }
  }

  private static void processBlock(FinalizationState finalizerState) {
    Material mat = finalizerState.getMaterial();
    Material wmat = finalizerState.getWaterMaterial();

    if (wmat instanceof Water) {
      Material above = finalizerState.getWaterMaterial(0, 1, 0);
      Material aboveBlock = finalizerState.getMaterial(0, 1, 0);
      if (!above.isWaterFilled()) {
        processWater(finalizerState);
      } else {
        finalizerState.replaceCurrentWaterBlock(finalizerState.getPalette().getWaterId(0, 1 << Water.FULL_BLOCK));
      }
    } else if (mat instanceof Lava) {
      Material above = finalizerState.getMaterial(BlockFace.UP);
      if (!(above instanceof Lava)) {
        processLava(finalizerState);
      }
    }
  }

  public static void processWater(FinalizationState finalizerState) {
    Material wmat = finalizerState.getWaterMaterial();
    int level0 = 8 - ((Water) wmat).level;

    int corner0 = level0;
    int corner1 = level0;
    int corner2 = level0;
    int corner3 = level0;

    int level = waterLevelAt(finalizerState, BlockFace.WEST, level0);
    corner3 += level;
    corner0 += level;

    level = waterLevelAt(finalizerState, BlockFace.SOUTH_WEST, level0);
    corner0 += level;

    level = waterLevelAt(finalizerState, BlockFace.SOUTH, level0);
    corner0 += level;
    corner1 += level;

    level = waterLevelAt(finalizerState, BlockFace.SOUTH_EAST, level0);
    corner1 += level;

    level = waterLevelAt(finalizerState, BlockFace.EAST, level0);
    corner1 += level;
    corner2 += level;

    level = waterLevelAt(finalizerState, BlockFace.NORTH_EAST, level0);
    corner2 += level;

    level = waterLevelAt(finalizerState, BlockFace.NORTH, level0);
    corner2 += level;
    corner3 += level;

    level = waterLevelAt(finalizerState, BlockFace.NORTH_WEST, level0);
    corner3 += level;

    corner0 = Math.min(7, 8 - (corner0 / 4));
    corner1 = Math.min(7, 8 - (corner1 / 4));
    corner2 = Math.min(7, 8 - (corner2 / 4));
    corner3 = Math.min(7, 8 - (corner3 / 4));

    finalizerState.replaceCurrentWaterBlock(finalizerState.getPalette().getWaterId(((Water) wmat).level, (corner0 << Water.CORNER_0)
      | (corner1 << Water.CORNER_1)
      | (corner2 << Water.CORNER_2)
      | (corner3 << Water.CORNER_3)));

    if (finalizerState.getY() + 1 < finalizerState.getYMax()) {
      // check if the block is submerged in water
      boolean north = finalizerState.getWaterMaterial(0, 0, -1).isWaterFilled() && finalizerState.getWaterMaterial(0, 1, -1).isWaterFilled();
      boolean south = finalizerState.getWaterMaterial(0, 0, 1).isWaterFilled() && finalizerState.getWaterMaterial(0, 1, 1).isWaterFilled();
      if (north && south) {
        finalizerState.replaceCurrentWaterBlock(finalizerState.getPalette().getWaterId(0, 1 << Water.FULL_BLOCK));
      } else {
        boolean east = finalizerState.getWaterMaterial(-1, 0, 0).isWaterFilled() && finalizerState.getWaterMaterial(-1, 1, 0).isWaterFilled();
        boolean west = finalizerState.getWaterMaterial(1, 0, 0).isWaterFilled() && finalizerState.getWaterMaterial(1, 1, 0).isWaterFilled();
        if (west && east) {
          finalizerState.replaceCurrentWaterBlock(finalizerState.getPalette().getWaterId(0, 1 << Water.FULL_BLOCK));
        }
      }
    }
  }

  public static void processLava(FinalizationState finalizerState) {
    Lava lava = (Lava) finalizerState.getMaterial();

    int level0 = 8 - lava.level;
    int corner0 = level0;
    int corner1 = level0;
    int corner2 = level0;
    int corner3 = level0;

    int level = lavaLevelAt(finalizerState, BlockFace.WEST, level0);
    corner3 += level;
    corner0 += level;

    level = lavaLevelAt(finalizerState, BlockFace.SOUTH_WEST, level0);
    corner0 += level;

    level = lavaLevelAt(finalizerState, BlockFace.SOUTH, level0);
    corner0 += level;
    corner1 += level;

    level = lavaLevelAt(finalizerState, BlockFace.SOUTH_EAST, level0);
    corner1 += level;

    level = lavaLevelAt(finalizerState, BlockFace.EAST, level0);
    corner1 += level;
    corner2 += level;

    level = lavaLevelAt(finalizerState, BlockFace.NORTH_EAST, level0);
    corner2 += level;

    level = lavaLevelAt(finalizerState, BlockFace.NORTH, level0);
    corner2 += level;
    corner3 += level;

    level = lavaLevelAt(finalizerState, BlockFace.NORTH_WEST, level0);
    corner3 += level;

    corner0 = Math.min(7, 8 - (corner0 / 4));
    corner1 = Math.min(7, 8 - (corner1 / 4));
    corner2 = Math.min(7, 8 - (corner2 / 4));
    corner3 = Math.min(7, 8 - (corner3 / 4));
    finalizerState.replaceCurrentBlock(finalizerState.getPalette().getLavaId(
      lava.level,
      (corner0 << Water.CORNER_0)
        | (corner1 << Water.CORNER_1)
        | (corner2 << Water.CORNER_2)
        | (corner3 << Water.CORNER_3)
    ));
  }

  public static int waterLevelAt(FinalizationState finalizationState, BlockFace direction, int baseLevel) {
    Material corner = finalizationState.getMaterial(direction);
    if (corner.isWater()) {
      Material above = finalizationState.getMaterial(direction.rx, 1, direction.rz);
      boolean isFullBlock = above.isWaterFilled();
      return isFullBlock ? 8 : 8 - (((Water) corner).level);
    } else if (corner.waterlogged) {
      return 8;
    } else if (corner instanceof Air) {
      Material cornerWater = finalizationState.getWaterMaterial(direction);
      if (cornerWater.isWater()) {
        Material above = finalizationState.getWaterMaterial(direction.rx, 1, direction.rz);
        boolean isFullBlock = above.isWaterFilled();
        return isFullBlock ? 8 : 8 - (((Water) cornerWater).level);
      }
      return 0;
    }
    return baseLevel;
  }

  public static int lavaLevelAt(FinalizationState finalizationState, BlockFace direction, int baseLevel) {
    Material corner = finalizationState.getMaterial();
    if (corner instanceof Lava) {
      Material above = finalizationState.getMaterial(BlockFace.UP);
      boolean isFullBlock = above instanceof Lava;
      return isFullBlock ? 8 : 8 - ((Lava) corner).level;
    } else if (!corner.solid) {
      return 0;
    }
    return baseLevel;
  }
}
