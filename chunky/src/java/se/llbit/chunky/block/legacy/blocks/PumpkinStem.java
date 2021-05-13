package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;

public class PumpkinStem extends UnfinalizedLegacyBlock {

  private static final BlockFace[] sides = new BlockFace[]{
    BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH
  };

  public PumpkinStem(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // pumpkin stem points to adjacent pumpkin or carved pumpkin (but not jack-o-lantern)
    for (BlockFace side : sides) {
      if (hasName(state.getMaterial(side), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
        state.replaceCurrentBlock(
            LegacyBlocks
                .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing",
                    side.getName()));
        return;
      }
    }

    // otherwise just unwrap the block
    state.replaceCurrentBlock(tag);
  }
}
