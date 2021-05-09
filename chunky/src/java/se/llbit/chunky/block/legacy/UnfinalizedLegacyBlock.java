package se.llbit.chunky.block.legacy;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockSpec;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.world.Material;
import se.llbit.log.Log;
import se.llbit.math.Ray;
import se.llbit.nbt.CompoundTag;

/**
 * A wrapper for legacy blocks (before the flattening in Minecraft 1.13) that have properties that
 * depend on surrounding blocks (e.g. doors, snow covered grass or fences).
 * <p>
 * The finalization logic of these blocks is handled by the {@link LegacyBlocksFinalizer}, which
 * then replaces these blocks with new blocks that have all properties set.
 */
public abstract class UnfinalizedLegacyBlock extends Block {

  /**
   * The wrapped block.
   */
  protected final Block block;

  /**
   * The ID of the wrapped block.
   */
  protected final int id;

  /**
   * The data value of the wrapped block.
   */
  protected final int data;

  /**
   * The tag of the wrapped block.
   */
  protected final CompoundTag tag;

  public UnfinalizedLegacyBlock(String name, CompoundTag tag) {
    this(name, new BlockSpec(tag.get("Block")).toBlock(), tag);
    solid = block.solid;
    opaque = block.opaque;
    localIntersect = true;
  }

  private UnfinalizedLegacyBlock(String name, Block block, CompoundTag tag) {
    super(block.name, block.texture);
    this.block = block;
    this.tag = (CompoundTag) tag.get("Block");
    id = tag.get("Id").intValue(0);
    data = tag.get("Data").intValue(0);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    Log.info("Intersecting a UnfinalizedLegacyBlock (" + block.name
        + "), which is supposed to be replaced");
    return block.intersect(ray, scene);
  }

  /**
   * Get the incomplete block. This block only has the properties that can be determined by its ID
   * and data value. Anything that requires knowledge of the surrounding blocks is missing.
   * <p>
   * This is used for the map view where connections don't matter when e.g. determining the block
   * color of fences.
   *
   * @return Incomplete block
   */
  public Block getIncompleteBlock() {
    return block;
  }

  /**
   * Replace the current block in the given finalization state by the finalized version of this
   * block. The state is used for context, i.e. surrounding blocks.
   *
   * @param state Current finalization state
   */
  public abstract void finalizeBlock(FinalizationState state);

  protected static boolean hasName(Material material, String... names) {
    for (String name : names) {
      if (material.name.equals(name)) {
        return true;
      }
    }
    return false;
  }
}
