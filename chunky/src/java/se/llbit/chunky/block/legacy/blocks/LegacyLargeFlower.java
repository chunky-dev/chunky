package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyLargeFlower extends UnfinalizedLegacyBlock {

  public LegacyLargeFlower(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    CompoundTag newTag;

    if ((this.data&8) != 0) {
      Material bottom = state.getMaterial(0, -1, 0);

      if (bottom instanceof LegacyLargeFlower) {
        newTag = LegacyBlocks.createTag(LegacyBlockUtils.getName(bottom));
      } else {
        String name = LegacyBlockUtils.getName(bottom);
        switch (name) {
          case "sunflower":
          case "lilac":
          case "tall_grass":
          case "large_fern":
          case "rose_bush":
          case "peony":
            newTag = LegacyBlocks.createTag(name);
            break;
          default:
            newTag = LegacyBlocks.createTag("unknown"); // Invalid state
        }
      }
      LegacyBlocks.stringTag(newTag, "half", "upper");
    } else {
      newTag = LegacyBlocks.createTag(LegacyBlockUtils.getName(this.block));
      LegacyBlocks.stringTag(newTag, "half", "lower");
    }

    state.replaceCurrentBlock(newTag);
  }
}
