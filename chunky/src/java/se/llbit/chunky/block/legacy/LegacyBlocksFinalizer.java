package se.llbit.chunky.block.legacy;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.OctreeFinalizationState;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Material;
import se.llbit.math.Octree;
import se.llbit.math.Point3i;
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
                                   Point3i origin, ChunkPosition cp, int yMin, int yMax) {
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
          Material mat = finalizerState.getMaterial();
          if (mat instanceof UnfinalizedLegacyBlock) {
            ((UnfinalizedLegacyBlock) mat).finalizeBlock(finalizerState);
          }
        }
      }
    }
  }
}
