package se.llbit.chunky.block.legacy;

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;
import se.llbit.math.Vector3i;

/**
 * Finalizer for pre-flattening (< 1.13) blocks.
 */
public class LegacyBlocksFinalizer {

  /**
   * Finalize a chunk in the octree.
   *
   * @param worldTree Octree to finalize
   * @param origin    Origin of the octree
   * @param cp        Position of the chunk to finalize
   */
  public static void finalizeChunk(Octree worldTree, Octree waterTree, BlockPalette palette,
      Vector3i origin, ChunkPosition cp, int yMin, int yMax) {
    for (int cy = yMin; cy < yMax; ++cy) {
      for (int cz = 0; cz < 16; ++cz) {
        int z = cz + cp.z * 16 - origin.z;
        for (int cx = 0; cx < 16; ++cx) {
          int x = cx + cp.x * 16 - origin.x;
          // TODO as in 1.13+ finalization we could finalize non-edge blocks during chunk loading
          processBlock(worldTree, waterTree, palette, x, cy, z, origin);
        }
      }
    }
  }

  private static void processBlock(Octree worldTree, Octree waterTree, BlockPalette palette, int x,
      int cy, int z, Vector3i origin) {
    int y = cy - origin.y;
    Material mat = worldTree.getMaterial(x, y, z, palette);
    if (mat instanceof LegacyBlock) {
      int data = ((LegacyBlock) mat).data;
      if ((data & 8) != 0) {
        // top part of the door
        if(cy > 0) {

        }
      } else {
        // bottom part of the door
      }
    }
  }
}
