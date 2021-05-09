package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;

public class PumpkinStem extends UnfinalizedLegacyBlock {

  public PumpkinStem(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // pumpkin stem points to adjacent pumpkin or carved pumpkin (but not jack-o-lantern)
    if (hasName(state.getMaterial(-1, 0, 0), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "west"));
      return;
    }
    if (hasName(state.getMaterial(1, 0, 0), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "east"));
      return;
    }
    if (hasName(state.getMaterial(0, 0, -1), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "north"));
      return;
    }
    if (hasName(state.getMaterial(0, 0, 1), "minecraft:pumpkin", "minecraft:carved_pumpkin")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "south"));
      return;
    }

    // otherwise just unwrap the block
    state.replaceCurrentBlock(tag);
  }
}
