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

import se.llbit.chunky.block.Lava;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;
import se.llbit.math.Vector3i;

/**
 * Processes the Octree after it has been loaded and updates block states for
 * blocks that depend on neighbor blocks. Octree finalization is be done after
 * all chunks have been loaded because before then we can't reliably test for
 * neighbor blocks.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class OctreeFinalizer {
  /**
   * Finalize a chunk in the octree.
   * @param worldTree Octree to finalize
   * @param origin Origin of the octree
   * @param cp     Position of the chunk to finalize
   */
  public static void finalizeChunk(Octree worldTree, Octree waterTree, BlockPalette palette,
      Vector3i origin, ChunkPosition cp) {
    for (int cy = 0 - origin.y; cy < Chunk.Y_MAX - origin.y; ++cy) {
      for (int cz = 0; cz < 16; ++cz) {
        int z = cz + cp.z * 16 - origin.z;
        for (int cx = 0; cx < 16; ++cx) {
          int x = cx + cp.x * 16 - origin.x;
          processBlock(worldTree, waterTree, palette, x, cy, z);
        }
      }
    }
  }

  private static void processBlock(Octree worldTree, Octree waterTree, BlockPalette palette, int x,
      int cy, int z) {
    Material mat = worldTree.getMaterial(x, cy, z, palette);
    Material wmat = waterTree.getMaterial(x, cy, z, palette);

    // TODO: duplicated code? Check Scene.loadChunks()...
    /*
    // Set non-visible blocks to be stone, in order to merge large patches.
    if ((cx == 0 || cx == 15 || cz == 0 || cz == 15) && cy > -origin.y
        && cy < Chunk.Y_MAX - origin.y - 1 && type != Block.STONE_ID && block.opaque) {
      if (Block.get(octree.get(x - 1, cy, z)).opaque && Block
          .get(octree.get(x + 1, cy, z)).opaque && Block
          .get(octree.get(x, cy - 1, z)).opaque && Block
          .get(octree.get(x, cy + 1, z)).opaque && Block
          .get(octree.get(x, cy, z - 1)).opaque && Block
          .get(octree.get(x, cy, z + 1)).opaque) {
        octree.set(Block.STONE_ID, x, cy, z);
        continue;
      }
    }*/

    if (wmat instanceof Water) {
      Material above = waterTree.getMaterial(x, cy + 1, z, palette);
      int level0 = 8 - ((Water) wmat).level;
      if (!above.isWater()) {
        int corner0 = level0;
        int corner1 = level0;
        int corner2 = level0;
        int corner3 = level0;

        int level = waterLevelAt(worldTree, waterTree, palette, x - 1, cy, z, level0);
        corner3 += level;
        corner0 += level;

        level = waterLevelAt(worldTree, waterTree, palette, x - 1, cy, z + 1, level0);
        corner0 += level;

        level = waterLevelAt(worldTree, waterTree, palette, x, cy, z + 1, level0);
        corner0 += level;
        corner1 += level;

        level = waterLevelAt(worldTree, waterTree, palette, x + 1, cy, z + 1, level0);
        corner1 += level;

        level = waterLevelAt(worldTree, waterTree, palette, x + 1, cy, z, level0);
        corner1 += level;
        corner2 += level;

        level = waterLevelAt(worldTree, waterTree, palette, x + 1, cy, z - 1, level0);
        corner2 += level;

        level = waterLevelAt(worldTree, waterTree, palette, x, cy, z - 1, level0);
        corner2 += level;
        corner3 += level;

        level = waterLevelAt(worldTree, waterTree, palette, x - 1, cy, z - 1, level0);
        corner3 += level;

        corner0 = Math.min(7, 8 - (corner0 / 4));
        corner1 = Math.min(7, 8 - (corner1 / 4));
        corner2 = Math.min(7, 8 - (corner2 / 4));
        corner3 = Math.min(7, 8 - (corner3 / 4));
        Octree.Node node = waterTree.get(x, cy, z);
        node = new Octree.DataNode(
            node.type,
            (corner0 << Water.CORNER_0)
                | (corner1 << Water.CORNER_1)
                | (corner2 << Water.CORNER_2)
                | (corner3 << Water.CORNER_3));
        waterTree.set(node, x, cy, z);
      }
    } else if (mat instanceof Lava) {
      Material above = worldTree.getMaterial(x, cy + 1, z, palette);
      if (!(above instanceof Lava)) {
        Lava lava = (Lava) mat;

        int level0 = 8 - lava.level;
        int corner0 = level0;
        int corner1 = level0;
        int corner2 = level0;
        int corner3 = level0;

        int level = lavaLevelAt(worldTree, palette, x - 1, cy, z, level0);
        corner3 += level;
        corner0 += level;

        level = lavaLevelAt(worldTree, palette, x - 1, cy, z + 1, level0);
        corner0 += level;

        level = lavaLevelAt(worldTree, palette, x, cy, z + 1, level0);
        corner0 += level;
        corner1 += level;

        level = lavaLevelAt(worldTree, palette, x + 1, cy, z + 1, level0);
        corner1 += level;

        level = lavaLevelAt(worldTree, palette, x + 1, cy, z, level0);
        corner1 += level;
        corner2 += level;

        level = lavaLevelAt(worldTree, palette, x + 1, cy, z - 1, level0);
        corner2 += level;

        level = lavaLevelAt(worldTree, palette, x, cy, z - 1, level0);
        corner2 += level;
        corner3 += level;

        level = lavaLevelAt(worldTree, palette, x - 1, cy, z - 1, level0);
        corner3 += level;

        corner0 = Math.min(7, 8 - (corner0 / 4));
        corner1 = Math.min(7, 8 - (corner1 / 4));
        corner2 = Math.min(7, 8 - (corner2 / 4));
        corner3 = Math.min(7, 8 - (corner3 / 4));
        Octree.Node node = worldTree.get(x, cy, z);
        Octree.Node replaced = new Octree.DataNode(
            node.type,
            (corner0 << Water.CORNER_0)
                | (corner1 << Water.CORNER_1)
                | (corner2 << Water.CORNER_2)
                | (corner3 << Water.CORNER_3));
        worldTree.set(replaced, x, cy, z);
      }
    }
  }

  private static int waterLevelAt(Octree worldTree, Octree waterTree,
      BlockPalette palette, int x, int cy, int z, int baseLevel) {
    Octree.Node node = waterTree.get(x, cy, z);
    Material corner = palette.get(node.type);
    if (corner instanceof Water) {
      int fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
      return 8 - (1 - fullBlock) * ((Water) corner).level;
    } else if (corner.waterlogged) {
      return 8;
    } else if (!worldTree.getMaterial(x, cy, z, palette).solid) {
      return 0;
    }
    return baseLevel;
  }

  private static int lavaLevelAt(Octree octree, BlockPalette palette,
      int x, int cy, int z, int baseLevel) {
    Octree.Node node = octree.get(x, cy, z);
    Material corner = palette.get(node.type);
    if (corner instanceof Lava) {
      int fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
      return 8 - (1 - fullBlock) * ((Lava) corner).level;
    } else if (!corner.solid) {
      return 0;
    }
    return baseLevel;
  }
}

