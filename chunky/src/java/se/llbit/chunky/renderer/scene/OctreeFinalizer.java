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

import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.BlockData;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
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
          int type = octree.get(x, cy, z);
          Block block = Block.get(type);

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
          }

          int fullBlock;
          int data;
          int level0;
          int level;
          int corner0;
          int corner1;
          int corner2;
          int corner3;
          int connections;
          int dir;
          int bd;
          int bd_alt;
          int tex;

          int otherId;
          Block other;
          Block other_alt;
          Block above;
          Block west;
          Block east;
          Block north;
          Block south;
          switch (block.id) {
            case Block.LARGE_FLOWER_ID:
              data = type >> BlockData.OFFSET;
              if ((data & 8) != 0) {
                // Get flower kind from block beneath.
                int kind = (octree.get(x, cy - 1, z) >> BlockData.OFFSET) & 7;
                type = (type & ~(15 << BlockData.OFFSET)) | ((8 | kind) << BlockData.OFFSET);
                octree.set(type, x, cy, z);
              }
              break;
            case Block.WATER_ID:
              fullBlock = (type >> WaterModel.FULL_BLOCK) & 1;
              if (fullBlock != 0)
                break;

              level0 = 8 - (0xF & (type >> 8));
              corner0 = level0;
              corner1 = level0;
              corner2 = level0;
              corner3 = level0;

              data = octree.get(x - 1, cy, z);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner3 += level;
              corner0 += level;

              data = octree.get(x - 1, cy, z + 1);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner0 += level;

              data = octree.get(x, cy, z + 1);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner0 += level;
              corner1 += level;

              data = octree.get(x + 1, cy, z + 1);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner1 += level;

              data = octree.get(x + 1, cy, z);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner1 += level;
              corner2 += level;

              data = octree.get(x + 1, cy, z - 1);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner2 += level;

              data = octree.get(x, cy, z - 1);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner2 += level;
              corner3 += level;

              data = octree.get(x - 1, cy, z - 1);
              level = level0;
              if ((data & 0xFF) == Block.WATER_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner3 += level;

              corner0 = Math.min(7, 8 - (corner0 / 4));
              corner1 = Math.min(7, 8 - (corner1 / 4));
              corner2 = Math.min(7, 8 - (corner2 / 4));
              corner3 = Math.min(7, 8 - (corner3 / 4));
              type |= (corner0 << 16);
              type |= (corner1 << 20);
              type |= (corner2 << 24);
              type |= (corner3 << 28);
              octree.set(type, x, cy, z);
              break;
            case Block.LAVA_ID:
              fullBlock = (type >> WaterModel.FULL_BLOCK) & 1;
              if (fullBlock != 0)
                break;

              level0 = 8 - (0xF & (type >> 8));
              corner0 = level0;
              corner1 = level0;
              corner2 = level0;
              corner3 = level0;

              data = octree.get(x - 1, cy, z);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner3 += level;
              corner0 += level;

              data = octree.get(x - 1, cy, z + 1);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner0 += level;

              data = octree.get(x, cy, z + 1);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner0 += level;
              corner1 += level;

              data = octree.get(x + 1, cy, z + 1);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner1 += level;

              data = octree.get(x + 1, cy, z);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner1 += level;
              corner2 += level;

              data = octree.get(x + 1, cy, z - 1);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner2 += level;

              data = octree.get(x, cy, z - 1);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner2 += level;
              corner3 += level;

              data = octree.get(x - 1, cy, z - 1);
              level = level0;
              if ((data & 0xFF) == Block.LAVA_ID) {
                fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
                level = 8 - (1 - fullBlock) * (7 & (data >> 8));
              } else if (!Block.get(data).solid) {
                level = 0;
              }
              corner3 += level;

              corner0 = Math.min(7, 8 - (corner0 / 4));
              corner1 = Math.min(7, 8 - (corner1 / 4));
              corner2 = Math.min(7, 8 - (corner2 / 4));
              corner3 = Math.min(7, 8 - (corner3 / 4));
              type |= (corner0 << 16);
              type |= (corner1 << 20);
              type |= (corner2 << 24);
              type |= (corner3 << 28);
              octree.set(type, x, cy, z);
              break;
            case Block.TRIPWIRE_ID:
              otherId = 0xFF & octree.get(x - 1, cy, z);
              if (otherId == Block.TRIPWIRE_ID || otherId == Block.TRIPWIREHOOK_ID) {
                type |= 1 << 12;
              } else {
                otherId = 0xFF & octree.get(x + 1, cy, z);
                if (otherId == Block.TRIPWIRE_ID || otherId == Block.TRIPWIREHOOK_ID) {
                  type |= 1 << 12;
                }
              }
              octree.set(type, x, cy, z);
              break;
            case Block.REDSTONEWIRE_ID:
              above = Block.get(octree.get(x, cy + 1, z));
              west = Block.get(octree.get(x - 1, cy, z));
              east = Block.get(octree.get(x + 1, cy, z));
              north = Block.get(octree.get(x, cy, z - 1));
              south = Block.get(octree.get(x, cy, z + 1));

              if (above == Block.AIR) {
                int westAbove = 0xFF & octree.get(x - 1, cy + 1, z);
                if (west.solid && westAbove == Block.REDSTONEWIRE_ID) {
                  // Wire on west block side.
                  type |= 1 << BlockData.RSW_WEST_CONNECTION;
                  type |= 1 << BlockData.RSW_WEST_SIDE;
                }
                int eastAbove = 0xFF & octree.get(x + 1, cy + 1, z);
                if (east.solid && eastAbove == Block.REDSTONEWIRE_ID) {
                  // Wire on east block side.
                  type |= 1 << BlockData.RSW_EAST_CONNECTION;
                  type |= 1 << BlockData.RSW_EAST_SIDE;
                }
                int northAbove = 0xFF & octree.get(x, cy + 1, z - 1);
                if (north.solid && northAbove == Block.REDSTONEWIRE_ID) {
                  // Wire on north block side.
                  type |= 1 << BlockData.RSW_NORTH_CONNECTION;
                  type |= 1 << BlockData.RSW_NORTH_SIDE;
                }
                int southAbove = 0xFF & octree.get(x, cy + 1, z + 1);
                if (south.solid && southAbove == Block.REDSTONEWIRE_ID) {
                  // Wire on south block side.
                  type |= 1 << BlockData.RSW_SOUTH_CONNECTION;
                  type |= 1 << BlockData.RSW_SOUTH_SIDE;
                }
              }

              if (west.isRedstoneWireConnector()) {
                type |= 1 << BlockData.RSW_WEST_CONNECTION;
              } else if (west == Block.AIR) {
                int westBelow = 0xFF & octree.get(x - 1, cy - 1, z);
                if (westBelow == Block.REDSTONEWIRE_ID) {
                  type |= 1 << BlockData.RSW_WEST_CONNECTION;
                }
              }

              if (east.isRedstoneWireConnector()) {
                type |= 1 << BlockData.RSW_EAST_CONNECTION;
              } else if (east == Block.AIR) {
                int eastBelow = 0xFF & octree.get(x + 1, cy - 1, z);
                if (eastBelow == Block.REDSTONEWIRE_ID) {
                  type |= 1 << BlockData.RSW_EAST_CONNECTION;
                }
              }

              if (north.isRedstoneWireConnector()) {
                type |= 1 << BlockData.RSW_NORTH_CONNECTION;
              } else if (north == Block.AIR) {
                int northBelow = 0xFF & octree.get(x, cy - 1, z - 1);
                if (northBelow == Block.REDSTONEWIRE_ID) {
                  type |= 1 << BlockData.RSW_NORTH_CONNECTION;
                }
              }

              if (south.isRedstoneWireConnector()) {
                type |= 1 << BlockData.RSW_SOUTH_CONNECTION;
              } else if (south == Block.AIR) {
                int southBelow = 0xFF & octree.get(x, cy - 1, z + 1);
                if (southBelow == Block.REDSTONEWIRE_ID) {
                  type |= 1 << BlockData.RSW_SOUTH_CONNECTION;
                }
              }

              octree.set(type, x, cy, z);
              break;
            case Block.MELONSTEM_ID:
              if ((0xFF & octree.get(x - 1, cy, z)) == Block.MELON_ID) {
                type |= 1 << 16;
              } else if ((0xFF & octree.get(x + 1, cy, z)) == Block.MELON_ID) {
                type |= 2 << 16;
              } else if ((0xFF & octree.get(x, cy, z - 1)) == Block.MELON_ID) {
                type |= 3 << 16;
              } else if ((0xFF & octree.get(x, cy, z + 1)) == Block.MELON_ID) {
                type |= 4 << 16;
              }
              octree.set(type, x, cy, z);
              break;
            case Block.PUMPKINSTEM_ID:
              if ((0xFF & octree.get(x - 1, cy, z)) == Block.PUMPKIN_ID) {
                type |= 1 << 16;
              } else if ((0xFF & octree.get(x + 1, cy, z)) == Block.PUMPKIN_ID) {
                type |= 2 << 16;
              } else if ((0xFF & octree.get(x, cy, z - 1)) == Block.PUMPKIN_ID) {
                type |= 3 << 16;
              } else if ((0xFF & octree.get(x, cy, z + 1)) == Block.PUMPKIN_ID) {
                type |= 4 << 16;
              }
              octree.set(type, x, cy, z);
              break;
            case Block.TRAPPEDCHEST_ID:
              dir = type >> 8;
              tex = 0;
              if (dir < 4) {
                if ((0xFF & octree.get(x - 1, cy, z)) == Block.TRAPPEDCHEST_ID) {
                  tex = 1 + (dir - 1) % 2;
                } else if ((0xFF & octree.get(x + 1, cy, z)) == Block.TRAPPEDCHEST_ID) {
                  tex = 1 + dir % 2;
                }
              } else {
                if ((0xFF & octree.get(x, cy, z - 1)) == Block.TRAPPEDCHEST_ID) {
                  tex = 1 + dir % 2;
                } else if ((0xFF & octree.get(x, cy, z + 1)) == Block.TRAPPEDCHEST_ID) {
                  tex = 1 + (dir - 1) % 2;
                }
              }
              type |= tex << 16;
              octree.set(type, x, cy, z);
              break;
            case Block.CHEST_ID:
              dir = type >> 8;
              tex = 0;
              if (dir < 4) {
                if ((0xFF & octree.get(x - 1, cy, z)) == Block.CHEST_ID) {
                  tex = 1 + (dir - 1) % 2;
                } else if ((0xFF & octree.get(x + 1, cy, z)) == Block.CHEST_ID) {
                  tex = 1 + dir % 2;
                }
              } else {
                if ((0xFF & octree.get(x, cy, z - 1)) == Block.CHEST_ID) {
                  tex = 1 + dir % 2;
                } else if ((0xFF & octree.get(x, cy, z + 1)) == Block.CHEST_ID) {
                  tex = 1 + (dir - 1) % 2;
                }
              }
              type |= tex << 16;
              octree.set(type, x, cy, z);
              break;
            case Block.IRONBARS_ID:
              data = octree.get(x, cy, z - 1);
              other = Block.get(data);
              if (other.isIronBarsConnector(data >> BlockData.OFFSET, BlockData.NORTH)) {
                type |= BlockData.CONNECTED_NORTH << BlockData.GLASS_PANE_OFFSET;
              }
              data = octree.get(x, cy, z + 1);
              other = Block.get(data);
              if (other.isIronBarsConnector(data >> BlockData.OFFSET, BlockData.SOUTH)) {
                type |= BlockData.CONNECTED_SOUTH << BlockData.GLASS_PANE_OFFSET;
              }
              data = octree.get(x + 1, cy, z);
              other = Block.get(data);
              if (other.isIronBarsConnector(data >> BlockData.OFFSET, BlockData.EAST)) {
                type |= BlockData.CONNECTED_EAST << BlockData.GLASS_PANE_OFFSET;
              }
              data = octree.get(x - 1, cy, z);
              other = Block.get(data);
              if (other.isIronBarsConnector(data >> BlockData.OFFSET, BlockData.WEST)) {
                type |= BlockData.CONNECTED_WEST << BlockData.GLASS_PANE_OFFSET;
              }
              octree.set(type, x, cy, z);
              break;
            case Block.GLASSPANE_ID:
            case Block.STAINED_GLASSPANE_ID:
              data = octree.get(x, cy, z - 1);
              other = Block.get(data);
              if (other.isGlassPaneConnector(data >> BlockData.OFFSET, BlockData.NORTH)) {
                type |= BlockData.CONNECTED_NORTH << BlockData.GLASS_PANE_OFFSET;
              }
              data = octree.get(x, cy, z + 1);
              other = Block.get(data);
              if (other.isGlassPaneConnector(data >> BlockData.OFFSET, BlockData.SOUTH)) {
                type |= BlockData.CONNECTED_SOUTH << BlockData.GLASS_PANE_OFFSET;
              }
              data = octree.get(x + 1, cy, z);
              other = Block.get(data);
              if (other.isGlassPaneConnector(data >> BlockData.OFFSET, BlockData.EAST)) {
                type |= BlockData.CONNECTED_EAST << BlockData.GLASS_PANE_OFFSET;
              }
              data = octree.get(x - 1, cy, z);
              other = Block.get(data);
              if (other.isGlassPaneConnector(data >> BlockData.OFFSET, BlockData.WEST)) {
                type |= BlockData.CONNECTED_WEST << BlockData.GLASS_PANE_OFFSET;
              }
              octree.set(type, x, cy, z);
              break;
            case Block.STONEWALL_ID:
              connections = 0;
              data = octree.get(x, cy, z - 1);
              other = Block.get(data);
              if (other.isStoneWallConnector(data >> BlockData.OFFSET, BlockData.NORTH)) {
                connections |= BlockData.CONNECTED_NORTH;
              }
              data = octree.get(x, cy, z + 1);
              other = Block.get(data);
              if (other.isStoneWallConnector(data >> BlockData.OFFSET, BlockData.SOUTH)) {
                connections |= BlockData.CONNECTED_SOUTH;
              }
              data = octree.get(x + 1, cy, z);
              other = Block.get(data);
              if (other.isStoneWallConnector(data >> BlockData.OFFSET, BlockData.EAST)) {
                connections |= BlockData.CONNECTED_EAST;
              }
              data = octree.get(x - 1, cy, z);
              other = Block.get(data);
              if (other.isStoneWallConnector(data >> BlockData.OFFSET, BlockData.WEST)) {
                connections |= BlockData.CONNECTED_WEST;
              }
              type |= connections << BlockData.STONEWALL_CONN;
              if (connections != 3 && connections != 12) {
                type |= 1 << BlockData.STONEWALL_CORNER;
              } else if (cy + 1 < Chunk.Y_MAX) {
                otherId = (0xFF & octree.get(x, cy + 1, z));
                if (otherId == Block.TORCH_ID || otherId == Block.REDSTONETORCHON_ID
                    || otherId == Block.REDSTONETORCHOFF_ID) {
                  type |= 1 << BlockData.STONEWALL_CORNER;
                }
              }
              octree.set(type, x, cy, z);
              break;
            case Block.FENCE_ID:
            case Block.SPRUCEFENCE_ID:
            case Block.BIRCHFENCE_ID:
            case Block.JUNGLEFENCE_ID:
            case Block.DARKOAKFENCE_ID:
            case Block.ACACIAFENCE_ID:
              data = octree.get(x, cy, z - 1);
              other = Block.get(data);
              if (other.isFenceConnector(data >> BlockData.OFFSET, BlockData.NORTH)) {
                type |= BlockData.CONNECTED_NORTH << BlockData.OFFSET;
              }
              data = octree.get(x, cy, z + 1);
              other = Block.get(data);
              if (other.isFenceConnector(data >> BlockData.OFFSET, BlockData.SOUTH)) {
                type |= BlockData.CONNECTED_SOUTH << BlockData.OFFSET;
              }
              data = octree.get(x + 1, cy, z);
              other = Block.get(data);
              if (other.isFenceConnector(data >> BlockData.OFFSET, BlockData.EAST)) {
                type |= BlockData.CONNECTED_EAST << BlockData.OFFSET;
              }
              data = octree.get(x - 1, cy, z);
              other = Block.get(data);
              if (other.isFenceConnector(data >> BlockData.OFFSET, BlockData.WEST)) {
                type |= BlockData.CONNECTED_WEST << BlockData.OFFSET;
              }
              octree.set(type, x, cy, z);
              break;
            case Block.NETHERBRICKFENCE_ID:
              data = octree.get(x, cy, z - 1);
              other = Block.get(data);
              if (other.isNetherBrickFenceConnector(data >> BlockData.OFFSET, BlockData.NORTH)) {
                type |= BlockData.CONNECTED_NORTH << BlockData.OFFSET;
              }
              data = octree.get(x, cy, z + 1);
              other = Block.get(data);
              if (other.isNetherBrickFenceConnector(data >> BlockData.OFFSET, BlockData.SOUTH)) {
                type |= BlockData.CONNECTED_SOUTH << BlockData.OFFSET;
              }
              data = octree.get(x + 1, cy, z);
              other = Block.get(data);
              if (other.isNetherBrickFenceConnector(data >> BlockData.OFFSET, BlockData.EAST)) {
                type |= BlockData.CONNECTED_EAST << BlockData.OFFSET;
              }
              data = octree.get(x - 1, cy, z);
              other = Block.get(data);
              if (other.isNetherBrickFenceConnector(data >> BlockData.OFFSET, BlockData.WEST)) {
                type |= BlockData.CONNECTED_WEST << BlockData.OFFSET;
              }
              octree.set(type, x, cy, z);
              break;
            case Block.FENCEGATE_ID:
            case Block.SPRUCEFENCEGATE_ID:
            case Block.BIRCHFENCEGATE_ID:
            case Block.JUNGLEFENCEGATE_ID:
            case Block.DARKOAKFENCEGATE_ID:
            case Block.ACACIAFENCEGATE_ID:
              dir = 3 & (type >> BlockData.OFFSET);
              if (dir == 0 || dir == 2) {
                // facing north or south
                int westId = (0xFF & octree.get(x - 1, cy, z));
                int eastId = (0xFF & octree.get(x + 1, cy, z));
                if (westId == Block.STONEWALL_ID && eastId == Block.STONEWALL_ID) {
                  type |= 1 << BlockData.FENCEGATE_LOW;
                  octree.set(type, x, cy, z);
                }
              } else {
                // facing east or west
                int northId = (0xFF & octree.get(x, cy, z - 1));
                int southId = (0xFF & octree.get(x, cy, z + 1));
                if (northId == Block.STONEWALL_ID && southId == Block.STONEWALL_ID) {
                  type |= 1 << BlockData.FENCEGATE_LOW;
                  octree.set(type, x, cy, z);
                }
              }
              break;
            case Block.OAKWOODSTAIRS_ID:
            case Block.STONESTAIRS_ID:
            case Block.BRICKSTAIRS_ID:
            case Block.STONEBRICKSTAIRS_ID:
            case Block.NETHERBRICKSTAIRS_ID:
            case Block.SANDSTONESTAIRS_ID:
            case Block.SPRUCEWOODSTAIRS_ID:
            case Block.BIRCHWOODSTAIRS_ID:
            case Block.JUNGLEWOODSTAIRS_ID:
            case Block.QUARTZSTAIRS_ID:
            case Block.ACACIASTAIRS_ID:
            case Block.DARKOAKSTAIRS_ID:
              // all corner notation (s-e, n-w, etc) is indicating gradient
              // check if this is a corner stair block
              int stairdata = type >> BlockData.OFFSET;
              int rotation = 3 & stairdata;
              int upsidedown = type & BlockData.UPSIDE_DOWN_STAIR;
              switch (rotation) {
                case 0:
                  // ascending east
                  bd = octree.get(x + 1, cy, z);// behind
                  other = Block.get(bd);
                  bd_alt = octree.get(x - 1, cy, z);// in front of
                  other_alt = Block.get(bd_alt);
                  if (other.isStair() && (bd & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd >> BlockData.OFFSET)) {
                      case 2:
                        // if stair behind ascends south we have outer s-e corner
                        // unless stair to the left has same orientation
                        if (!sameStair(octree, type, x, cy, z - 1)) {
                          type |= BlockData.SOUTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 3:
                        // if stair behind ascends north we have n-e corner
                        // unless stair to the right has same orientation
                        if (!sameStair(octree, type, x, cy, z + 1)) {
                          type |= BlockData.NORTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  } else if (other_alt.isStair()
                      && (bd_alt & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd_alt >> BlockData.OFFSET)) {
                      case 2:
                        // if stair in front ascends south we have inner s-e corner
                        // unless stair to the right has same orientation
                        if (!sameStair(octree, type, x, cy, z + 1)) {
                          type |= BlockData.INNER_SOUTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 3:
                        // if stair in front ascends north we have inner n-e corner
                        // unless stair to the left has same orientation
                        if (!sameStair(octree, type, x, cy, z - 1)) {
                          type |= BlockData.INNER_NORTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  }
                  break;
                case 1:
                  // ascending west
                  bd = octree.get(x - 1, cy, z);// behind
                  other = Block.get(bd);
                  bd_alt = octree.get(x + 1, cy, z);// in front of
                  other_alt = Block.get(bd_alt);
                  if (other.isStair() && (bd & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd >> BlockData.OFFSET)) {
                      case 2:
                        // if stair behind ascends south we have outer s-w corner
                        // unless stair to the right has same orientation
                        if (!sameStair(octree, type, x, cy, z - 1)) {
                          type |= BlockData.SOUTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 3:
                        // if stair behind ascends north we have outer n-w corner
                        // unless stair to the left has same orientation
                        if (!sameStair(octree, type, x, cy, z + 1)) {
                          type |= BlockData.NORTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  } else if (other_alt.isStair()
                      && (bd_alt & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd_alt >> BlockData.OFFSET)) {
                      case 2:
                        // if stair in front ascends south we have inner s-w corner
                        // unless stair to the left has same orientation
                        if (!sameStair(octree, type, x, cy, z + 1)) {
                          type |= BlockData.INNER_SOUTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 3:
                        // if stair in front ascends north we have inner n-w corner
                        // unless stair to the right has same orientation
                        if (!sameStair(octree, type, x, cy, z - 1)) {
                          type |= BlockData.INNER_NORTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  }
                  break;
                case 2:
                  // ascending south
                  bd = octree.get(x, cy, z + 1);// behind
                  other = Block.get(bd);
                  bd_alt = octree.get(x, cy, z - 1);// in front of
                  other_alt = Block.get(bd_alt);
                  if (other.isStair() && (bd & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd >> BlockData.OFFSET)) {
                      case 0:
                        // if stair behind ascends east we have outer s-e corner
                        if (!sameStair(octree, type, x - 1, cy, z)) {
                          type |= BlockData.SOUTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 1:
                        // if stair behind ascends west we have outer s-w corner
                        if (!sameStair(octree, type, x + 1, cy, z)) {
                          type |= BlockData.SOUTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  } else if (other_alt.isStair()
                      && (bd_alt & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd_alt >> BlockData.OFFSET)) {
                      case 0:
                        // if stair in front ascends east we have inner s-e corner
                        if (!sameStair(octree, type, x + 1, cy, z)) {
                          type |= BlockData.INNER_SOUTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 1:
                        // if stair in front ascends west we have inner s-w corner
                        if (!sameStair(octree, type, x - 1, cy, z)) {
                          type |= BlockData.INNER_SOUTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  }
                  break;
                case 3:
                  // Ascending north.
                  bd = octree.get(x, cy, z - 1); // Behind.
                  other = Block.get(bd);
                  bd_alt = octree.get(x, cy, z + 1); // In front of.
                  other_alt = Block.get(bd_alt);
                  if (other.isStair() && (bd & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd >> BlockData.OFFSET)) {
                      case 0:
                        // If stair behind ascends east we have outer n-e corner.
                        if (!sameStair(octree, type, x - 1, cy, z)) {
                          type |= BlockData.NORTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 1:
                        // If stair behind ascends west we have outer n-w corner.
                        if (!sameStair(octree, type, x + 1, cy, z)) {
                          type |= BlockData.NORTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  } else if (other_alt.isStair()
                      && (bd_alt & BlockData.UPSIDE_DOWN_STAIR) == upsidedown) {
                    switch (3 & (bd_alt >> BlockData.OFFSET)) {
                      case 0:
                        // If stair in front ascends east we have inner n-e corner.
                        if (!sameStair(octree, type, x + 1, cy, z)) {
                          type |= BlockData.INNER_NORTH_EAST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                      case 1:
                        // If stair in front ascends west we have inner n-w corner.
                        if (!sameStair(octree, type, x - 1, cy, z)) {
                          type |= BlockData.INNER_NORTH_WEST << BlockData.CORNER_OFFSET;
                          octree.set(type, x, cy, z);
                        }
                        break;
                    }
                  }
                  break;
              }
              break;
            case Block.CHORUSPLANT_ID:
              other = Block.get(octree.get(x, cy, z - 1));
              if (other.isChorusPlant()) {
                type |= BlockData.CONNECTED_NORTH << BlockData.OFFSET;
              }
              other = Block.get(octree.get(x, cy, z + 1));
              if (other.isChorusPlant()) {
                type |= BlockData.CONNECTED_SOUTH << BlockData.OFFSET;
              }
              other = Block.get(octree.get(x + 1, cy, z));
              if (other.isChorusPlant()) {
                type |= BlockData.CONNECTED_EAST << BlockData.OFFSET;
              }
              other = Block.get(octree.get(x - 1, cy, z));
              if (other.isChorusPlant()) {
                type |= BlockData.CONNECTED_WEST << BlockData.OFFSET;
              }
              other = Block.get(octree.get(x, cy + 1, z));
              if (other.isChorusPlant()) {
                type |= BlockData.CONNECTED_ABOVE << BlockData.OFFSET;
              }
              other = Block.get(octree.get(x, cy - 1, z));
              if (other.isChorusPlant() || other.id == Block.ENDSTONE_ID) {
                type |= BlockData.CONNECTED_BELOW << BlockData.OFFSET;
              }
              octree.set(type, x, cy, z);
              break;
            default:
              break;
          }
        }
      }
    }
  }

  /**
   * Check if this stair type is the same as the other stair block.
   */
  private static boolean sameStair(Octree octree, int type, int x, int y, int z) {
    int id = octree.get(x, y, z);
    return Block.get(id).isStair() && (type & (7 << 8)) == (id & (7 << 8));
  }

}

