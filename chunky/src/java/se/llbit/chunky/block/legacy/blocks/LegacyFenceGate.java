package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;

public class LegacyFenceGate extends UnfinalizedLegacyBlock {

  public LegacyFenceGate(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    CompoundTag newTag = LegacyBlocks.createTag(this.name);
    LegacyBlocks.facing4Tag(newTag, data & 0b11);
    LegacyBlocks.boolTag(newTag, "open", (data & 4) != 0);

    if ((this.data & 3) % 2 == 0) {
      LegacyBlocks.boolTag(newTag, "in_wall",
          state.getMaterial(-1, 0, 0).name.endsWith("cobblestone_wall") ||
              state.getMaterial(1, 0, 0).name.endsWith("cobblestone_wall"));
    } else {
      LegacyBlocks.boolTag(newTag, "in_wall",
          state.getMaterial(0, 0, -1).name.endsWith("cobblestone_wall") ||
              state.getMaterial(0, 0, 1).name.endsWith("cobblestone_wall"));
    }

    state.replaceCurrentBlock(newTag);
  }

  public BlockFace getFacing() {
    return new BlockFace[]{
        BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST
    }[data & 0b11];
  }
}
