package se.llbit.chunky.block.legacy;

import se.llbit.chunky.block.Door;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.OctreeFinalizationState;
import se.llbit.chunky.block.Snow;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;
import se.llbit.math.Vector3i;

/**
 * Finalizer for pre-flattening (< 1.13) blocks that have properties that depend on the surrounding
 * blocks (e.g. doors, snow covered grass or fences).
 */
public class LegacyBlocksFinalizer {

  /**
   * Finalize a chunk in the octree.
   *
   * @param worldTree Octree to finalize
   * @param origin    Origin of the octree
   * @param cp        Position of the chunk to finalize
   * @param yMin      Minimum y position to finalize
   * @param yMax      Max y level to finalize (exclusive)
   */
  public static void finalizeChunk(Octree worldTree, Octree waterTree, BlockPalette palette,
      Vector3i origin, ChunkPosition cp, int yMin, int yMax) {
    OctreeFinalizationState finalizerState = new OctreeFinalizationState(worldTree, waterTree,
        palette, yMin, yMax);
    for (int cy = yMin; cy < yMax; ++cy) {
      int y = cy - origin.y;
      for (int cz = 0; cz < 16; ++cz) {
        int z = cz + cp.z * 16 - origin.z;
        for (int cx = 0; cx < 16; ++cx) {
          int x = cx + cp.x * 16 - origin.x;
          // TODO as in 1.13+ finalization we could finalize non-edge blocks during chunk loading
          finalizerState.setPosition(x, y, z);
          processBlock(finalizerState);
        }
      }
    }
  }

  private static void processBlock(FinalizationState state) {
    Material mat = state.getMaterial();
    if (mat instanceof UnfinalizedLegacyBlock) {
      UnfinalizedLegacyBlock block = (UnfinalizedLegacyBlock) mat;

      int data = block.data;
      if (block.block instanceof Door) {
        if ((data & 8) != 0) {
          // top part of the door
          // if (finalizationState.getMaterial(0, -1, 0))
          // TODO
        } else {
          // bottom part of the door
        }
      } else if (block.id == 2 || (block.id == 3 && data == 2) || block.id == 110) {
        // grass block, podzol and mycelium are snow-covered if the block above is snow or snow block
        // (aside: Chunky 1.x only handles this correctly for grass blocks)
        if (state.getY() < state.getYMax() - 1) {
          Material above = state.getMaterial(0, 1, 0);
          if (above instanceof Snow || above.name.equals("minecraft:snow_block")) {
            state.replaceCurrentBlock(LegacyBlocks
                .snowCovered(LegacyBlocks.createTag(block.block.name)));
            return;
          }
        }
      } else if (block.id == 106) {
        // vines have an "up" part if the block above is solid
        if (state.getY() < state.getYMax() - 1) {
          Material above = state.getMaterial(0, 1, 0);
          if (above.solid) {
            state.replaceCurrentBlock(
                LegacyBlocks.vineTag(LegacyBlocks.createTag("vine"), block.data, true));
            return;
          }
        }
      } else if (block.id == 104) {
        // pumpkin stem point to adjacent pumpkin or carved pumpkin (but not jack-o-lantern)
        if (hasName(state.getMaterial(-1, 0, 0), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "west"));
          return;
        }
        if (hasName(state.getMaterial(1, 0, 0), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "east"));
          return;
        }
        if (hasName(state.getMaterial(0, 0, -1), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "north"));
          return;
        }
        if (hasName(state.getMaterial(0, 0, 1), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "south"));
          return;
        }
      } else if (block.id == 105) {
        // melon stem point to adjacent melon
        if (state.getMaterial(-1, 0, 0).name.equals("minecraft:melon")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "west"));
          return;
        }
        if (state.getMaterial(1, 0, 0).name.equals("minecraft:melon")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "east"));
          return;
        }
        if (state.getMaterial(0, 0, -1).name.equals("minecraft:melon")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "north"));
          return;
        }
        if (state.getMaterial(0, 0, 1).name.equals("minecraft:melon")) {
          state.replaceCurrentBlock(
              LegacyBlocks
                  .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "south"));
          return;
        }
      }

      // otherwise just unwrap the block
      state.replaceCurrentBlock(block.tag);
    }
  }

  private static boolean hasName(Material material, String... names) {
    for (String name : names) {
      if (material.name.equals(name)) {
        return true;
      }
    }
    return false;
  }
}
