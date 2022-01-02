package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

/**
 * A 1.12 or older fire block.
 * <p>
 * Before 1.13, the sides of fire would depend on the surrounding blocks.
 */
public class LegacyFire extends UnfinalizedLegacyBlock {

  public LegacyFire(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    CompoundTag tag = LegacyBlocks.createTag("fire");

    boolean floorFire =
        state.getY() > state.getYMin() && isTopBurnable(state.getBlock(BlockFace.DOWN));

    // a side is burning if the adjacent block at that side is flammable
    LegacyBlocks.boolTag(tag, "north", !floorFire && isFlammable(state.getBlock(BlockFace.NORTH)));
    LegacyBlocks.boolTag(tag, "east", !floorFire && isFlammable(state.getBlock(BlockFace.EAST)));
    LegacyBlocks.boolTag(tag, "south", !floorFire && isFlammable(state.getBlock(BlockFace.SOUTH)));
    LegacyBlocks.boolTag(tag, "west", !floorFire && isFlammable(state.getBlock(BlockFace.WEST)));

    // the up side is visible if this burning on top of the bottom below and the block above is flammable
    LegacyBlocks
        .boolTag(tag, "up", !floorFire &&
            state.getY() < state.getYMax() - 1 && isFlammable(state.getBlock(BlockFace.UP)));

    state.replaceCurrentBlock(tag);
  }

  private static boolean isFlammable(Material material) {
    // flammable blocks can burn on their sides
    String name = LegacyBlockUtils.getName(material);
    return name.endsWith("_log") ||
        name.equals("coal_block") ||
        name.endsWith("_planks") ||
        name.equals("oak_slabs") || name.equals("spruce_slab") || name.equals("birch_slab") ||
        name.equals("jungle_slab") || name.equals("acacia_slab") || name.equals("dark_oak_slab") ||
        (name.endsWith("_fence") && !name.equals("nether_brick_fence")) ||
        name.endsWith("_fence_gate") ||
        name.equals("oak_stairs") || name.equals("spruce_stairs") || name.equals("birch_stairs") ||
        name.equals("jungle_stairs") || name.equals("acacia_stairs") || name.equals("dark_oak_stairs") ||
        name.equals("tnt") ||
        name.equals("vine") ||
        name.equals("bookshelf") ||
        name.endsWith("_leaves") ||
        name.endsWith("_wool") ||
        name.equals("hay_block") ||
        name.equals("sunflower") || name.equals("lilac") || name.equals("tall_grass") ||
        name.equals("large_fern") || name.equals("rose_bush") || name.equals("peony") ||
        name.equals("poppy") || name.equals("blue_orchid") || name.equals("allium") ||
        name.equals("azure_bluet") || name.endsWith("_tulip") || name.equals("oxeye_daisy") ||
        name.equals("dead_bush") ||
        name.endsWith("_carpet");
  }

  private static boolean isTopBurnable(Block material) {
    // solid blocks like dirt and stone can burn on top but not on the side (i.e. are not flammable)
    return material.solid || isFlammable(material);
  }
}
