package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockProviderRegistry;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

public class LegacyBed extends UnfinalizedLegacyBlock {

  public LegacyBed(String name, CompoundTag tag,
      BlockProviderRegistry blockProviders) {
    super(name, tag, blockProviders);
  }

  @Override
  public boolean isModifiedByBlockEntity() {
    return true;
  }

  @Override
  public Tag getNewTagWithBlockEntity(Tag blockTag, CompoundTag entityTag) {
    int color = entityTag.get("color").intValue(1);
    CompoundTag tag = LegacyBlocks.createTag(getColor(color) + "_bed");
    LegacyBlocks.stringTag(tag, "part", this.tag.get("Properties").get("part").stringValue("head"));
    LegacyBlocks
        .stringTag(tag, "facing", this.tag.get("Properties").get("facing").stringValue("south"));
    return tag;
  }

  private static String getColor(int color) {
    if (color < 0 || color > 15) {
      return "red";
    }
    return new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
        "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"}[color];
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // bed without a block entity, just unwrap to red bed
    state.replaceCurrentBlock(tag);
  }
}
