package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;

public class LegacyChest extends UnfinalizedLegacyBlock {

  public LegacyChest(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // chests and trapped chests become double chests if there is a chest next to them
    // (checking for #legacy_ too because the chest next to this one might not be finalized yet)

    String name = block.name.substring(10);
    if (data < 4) {
      // facing east or west
      if (LegacyBlockUtils.getName(state.getMaterial(-1, 0, 0)).equals(name)) {
        state.replaceCurrentBlock(
            createTag(data, data == 2 ? "right" : "left"));
        return;
      } else if (LegacyBlockUtils.getName(state.getMaterial(1, 0, 0)).equals(name)) {
        state.replaceCurrentBlock(
            createTag(data, data == 2 ? "left" : "right"));
        return;
      }
    } else {
      // facing north or south
      if (LegacyBlockUtils.getName(state.getMaterial(0, 0, -1)).equals(name)) {
        state.replaceCurrentBlock(
            createTag(data, data == 5 ? "right" : "left"));
        return;
      } else if (LegacyBlockUtils.getName(state.getMaterial(0, 0, 1)).equals(name)) {
        state.replaceCurrentBlock(
            createTag(data, data == 5 ? "left" : "right"));
        return;
      }
    }

    // otherwise it's a single chest, so just unwrap
    state.replaceCurrentBlock(tag);
  }

  private CompoundTag createTag(int data, String type) {
    CompoundTag tag = LegacyBlocks
        .createTag(name.endsWith("trapped_chest") ? "trapped_chest" : "chest");
    LegacyBlocks.chestFurnaceLadderTag(tag, data);
    LegacyBlocks.stringTag(tag, "type", type);
    return tag;
  }
}
