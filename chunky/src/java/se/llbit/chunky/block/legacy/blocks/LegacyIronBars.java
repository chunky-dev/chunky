package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyIronBars extends UnfinalizedLegacyBlock {

  public LegacyIronBars(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    boolean north = isIronBarConnector(state.getMaterial(0, 0, -1), BlockFace.NORTH);
    boolean south = isIronBarConnector(state.getMaterial(0, 0, 1), BlockFace.SOUTH);
    boolean east = isIronBarConnector(state.getMaterial(1, 0, 0), BlockFace.EAST);
    boolean west = isIronBarConnector(state.getMaterial(-1, 0, 0), BlockFace.WEST);

    CompoundTag newTag = LegacyBlocks.createTag("iron_bars");
    LegacyBlocks.boolTag(newTag, "east", east);
    LegacyBlocks.boolTag(newTag, "west", west);
    LegacyBlocks.boolTag(newTag, "north", north);
    LegacyBlocks.boolTag(newTag, "south", south);

    state.replaceCurrentBlock(newTag);
  }

  private static boolean isIronBarConnector(Material block, BlockFace direction) {
    String name = LegacyBlockUtils.getName(block);
    if (name.equals("cobblestone_wall") || name.equals("mossy_cobblestone_wall") || name
        .endsWith("_fence") || name
        .endsWith("_leaves")) { // in 1.9 iron bars do connect to leaves
      return false;
    }

    // 1.12 (?) stairs connection logic (only connect to the high side)
    BlockFace adjacentStairsFacing = LegacyBlockUtils.getStairsFacing(block);
    if (adjacentStairsFacing != null) {
      return adjacentStairsFacing.getOppositeFace() == direction;
    }

    return block.solid || name.equals("iron_bars");
  }
}
