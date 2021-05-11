package se.llbit.chunky.block.legacy.blocks;

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
    boolean north = isIronBarConnector(state.getMaterial(0, 0, -1));
    boolean south = isIronBarConnector(state.getMaterial(0, 0, 1));
    boolean east = isIronBarConnector(state.getMaterial(1, 0, 0));
    boolean west = isIronBarConnector(state.getMaterial(-1, 0, 0));

    CompoundTag newTag = LegacyBlocks.createTag("iron_bars");
    LegacyBlocks.boolTag(newTag, "east", east);
    LegacyBlocks.boolTag(newTag, "west", west);
    LegacyBlocks.boolTag(newTag, "north", north);
    LegacyBlocks.boolTag(newTag, "south", south);

    state.replaceCurrentBlock(newTag);
  }

  private static boolean isIronBarConnector(Material block) {
    return block.solid || LegacyBlockUtils.getName(block).equals("iron_bars");
  }
}
