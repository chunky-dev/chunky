package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.BlockProviderRegistry;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;

public class LegacyMelonStem extends UnfinalizedLegacyBlock {

  private static final BlockFace[] sides = new BlockFace[]{
      BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH
  };

  public LegacyMelonStem(String name, CompoundTag tag,
      BlockProviderRegistry blockProviders) {
    super(name, tag, blockProviders);
  }


  @Override
  public void finalizeBlock(FinalizationState state) {
    // melon stem points to adjacent melon
    for (BlockFace side : sides) {
      if (state.getMaterial(side).name.equals("minecraft:melon")) {
        state.replaceCurrentBlock(
            LegacyBlocks.stringTag(
                LegacyBlocks.createTag("attached_melon_stem"), "facing", side.getName()
            ));
        return;
      }
    }

    // otherwise just unwrap the block
    state.replaceCurrentBlock(tag);
  }
}
