package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;

public class MelonStem extends UnfinalizedLegacyBlock {

  public MelonStem(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // melon stem points to adjacent melon
    if (state.getMaterial(-1, 0, 0).name.equals("minecraft:melon")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "west"));
      return;
    }
    if (state.getMaterial(1, 0, 0).name.equals("minecraft:melon")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "east"));
      return;
    }
    if (state.getMaterial(0, 0, -1).name.equals("minecraft:melon")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "north"));
      return;
    }
    if (state.getMaterial(0, 0, 1).name.equals("minecraft:melon")) {
      state.replaceCurrentBlock(
          LegacyBlocks
              .stringTag(LegacyBlocks.createTag("attached_pumpkin_stem"), "facing", "south"));
      return;
    }

    // otherwise just unwrap the block
    state.replaceCurrentBlock(tag);
  }
}
