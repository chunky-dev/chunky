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

import se.llbit.chunky.block.Water;
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
   *
   * @param octree Octree to finalize
   * @param origin Origin of the octree
   * @param cp     Position of the chunk to finalize
   */
  public static void finalizeChunk(Octree octree, Vector3i origin, ChunkPosition cp) {
    for (int cy = 0 - origin.y; cy < Chunk.Y_MAX - origin.y; ++cy) {
      for (int cz = 0; cz < 16; ++cz) {
        int z = cz + cp.z * 16 - origin.z;
        for (int cx = 0; cx < 16; ++cx) {
          int x = cx + cp.x * 16 - origin.x;
          Material mat = octree.getMaterial(x, cy, z);

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

          if (mat instanceof Water) {
            Material above = octree.getMaterial(x, cy + 1, z);
            if (!above.isWater()) {
              Water water = (Water) mat;

              int level0 = 8 - water.level;
              int corner0 = level0;
              int corner1 = level0;
              int corner2 = level0;
              int corner3 = level0;

              Octree.Node node = octree.get(x - 1, cy, z);
              Material corner = octree.palette.get(node.type);
              int fullBlock;
              int level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner3 += level;
              corner0 += level;

              node = octree.get(x - 1, cy, z + 1);
              corner = octree.palette.get(node.type);
              level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner0 += level;

              node = octree.get(x, cy, z + 1);
              corner = octree.palette.get(node.type);
              level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner0 += level;
              corner1 += level;

              node = octree.get(x + 1, cy, z + 1);
              corner = octree.palette.get(node.type);
              level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner1 += level;

              node = octree.get(x + 1, cy, z);
              corner = octree.palette.get(node.type);
              level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner1 += level;
              corner2 += level;

              node = octree.get(x + 1, cy, z - 1);
              corner = octree.palette.get(node.type);
              level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner2 += level;

              node = octree.get(x, cy, z - 1);
              corner = octree.palette.get(node.type);
              level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner2 += level;
              corner3 += level;

              node = octree.get(x - 1, cy, z - 1);
              corner = octree.palette.get(node.type);
              level = level0;
              if (corner.isWater()) {
                fullBlock = (node.getData() >> Water.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * ((Water) corner).level;
              } else if (!corner.solid) {
                level = 0;
              }
              corner3 += level;

              corner0 = Math.min(7, 8 - (corner0 / 4));
              corner1 = Math.min(7, 8 - (corner1 / 4));
              corner2 = Math.min(7, 8 - (corner2 / 4));
              corner3 = Math.min(7, 8 - (corner3 / 4));
              node = octree.get(x, cy, z);
              Octree.Node replaced = new Octree.DataNode(
                  node.type,
                  (corner0 << Water.CORNER_0)
                  | (corner1 << Water.CORNER_1)
                  | (corner2 << Water.CORNER_2)
                  | (corner3 << Water.CORNER_3));
              octree.set(replaced, x, cy, z);
            }
          }
        }
      }
    }
  }
}

