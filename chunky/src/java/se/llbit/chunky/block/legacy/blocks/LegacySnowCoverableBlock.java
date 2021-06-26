package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.Snow;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacySnowCoverableBlock extends UnfinalizedLegacyBlock {

  public LegacySnowCoverableBlock(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // grass block, podzol and mycelium are snow-covered if the block above is snow or snow block
    // (aside: Chunky 1.x only handles this correctly for grass blocks)
    if (id == 2 || (id == 3 && data == 2) || id == 110) {
      if (state.getY() < state.getYMax() - 1) {
        Material above = state.getMaterial(0, 1, 0);
        if (above instanceof Snow || above.name.equals("minecraft:snow_block")) {
          state.replaceCurrentBlock(LegacyBlocks
              .boolTag(LegacyBlocks.createTag(block.name), "snowy", true));
          return;
        }
      }
    }

    // otherwise just unwrap the block
    state.replaceCurrentBlock(tag);
  }
}
