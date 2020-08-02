package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.BlockProviderRegistry;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;

public class LegacyPumpkinStem extends UnfinalizedLegacyBlock {

  private static final BlockFace[] sides = new BlockFace[]{
      BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH
  };

  public LegacyPumpkinStem(String name, CompoundTag tag, BlockProviderRegistry blockProviders) {
    super(name, tag, blockProviders);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // pumpkin stem points to adjacent pumpkin or carved pumpkin (but not jack-o-lantern)
    for (BlockFace side : sides) {
      if (state.getMaterial(side).name.endsWith("pumpkin")) {
        state.replaceCurrentBlock(
            LegacyBlocks.stringTag(
                LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", side.getName()
            ));
        return;
      }
    }

    // otherwise just unwrap the block
    state.replaceCurrentBlock(tag);
  }
}
