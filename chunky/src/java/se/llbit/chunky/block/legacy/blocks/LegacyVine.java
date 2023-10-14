package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyVine extends UnfinalizedLegacyBlock {

  public LegacyVine(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // vines have an "up" part if the block above is solid
    if (state.getY() < state.getYMax() - 1) {
      Material above = state.getMaterial(0, 1, 0);
      if (above.solid) {
        state.replaceCurrentBlock(
            LegacyBlocks.vineTag(LegacyBlocks.createTag("vine"), data, true));
        return;
      }
    }

    // otherwise just unwrap the block
    state.replaceCurrentBlock(tag);
  }

  @Override
  public boolean isBiomeDependant() {
    return true;
  }
}
