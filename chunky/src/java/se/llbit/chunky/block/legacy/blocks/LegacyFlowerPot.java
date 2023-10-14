package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

public class LegacyFlowerPot extends UnfinalizedLegacyBlock {

  public LegacyFlowerPot(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    // empty flower pot, just unwrap
    state.replaceCurrentBlock(tag);
  }

  @Override
  public boolean isModifiedByBlockEntity() {
    // from 1.7.2 on, the plant contained in the pot is stored as a tile entity
    return data == 0;
  }

  @Override
  public Tag getNewTagWithBlockEntity(Tag blockTag, CompoundTag entityTag) {
    switch (entityTag.get("Item").stringValue("")) {
      case "minecraft:red_flower":
        switch (entityTag.get("Data").intValue(0)) {
          default:
          case 0:
            return LegacyBlocks.createTag("potted_poppy");
          case 1:
            return LegacyBlocks.createTag("potted_blue_orchid");
          case 2:
            return LegacyBlocks.createTag("potted_allium");
          case 3:
            return LegacyBlocks.createTag("potted_azure_bluet");
          case 4:
            return LegacyBlocks.createTag("potted_red_tulip");
          case 5:
            return LegacyBlocks.createTag("potted_orange_tulip");
          case 6:
            return LegacyBlocks.createTag("potted_white_tulip");
          case 7:
            return LegacyBlocks.createTag("potted_pink_tulip");
          case 8:
            return LegacyBlocks.createTag("potted_oxeye_daisy");
          case 9:
            return LegacyBlocks.createTag("potted_cornflower");
        }
      case "minecraft:yellow_flower":
        return LegacyBlocks.createTag("potted_dandelion");
      case "minecraft:cactus":
        return LegacyBlocks.createTag("potted_cactus");
      case "minecraft:sapling":
        switch (entityTag.get("Data").intValue(0)) {
          default:
          case 0:
            return LegacyBlocks.createTag("potted_oak_sapling");
          case 1:
            return LegacyBlocks.createTag("potted_spruce_sapling");
          case 2:
            return LegacyBlocks.createTag("potted_birch_sapling");
          case 3:
            return LegacyBlocks.createTag("potted_jungle_sapling");
          case 4:
            return LegacyBlocks.createTag("potted_acacia_sapling");
          case 5:
            return LegacyBlocks.createTag("potted_dark_oak_sapling");
        }
      case "minecraft:brown_mushroom":
        return LegacyBlocks.createTag("potted_brown_mushroom");
      case "minecraft:red_mushroom":
        return LegacyBlocks.createTag("potted_red_mushroom");
      case "minecraft:deadbush":
        return LegacyBlocks.createTag("potted_dead_bush");
      case "minecraft:tallgrass":
        return LegacyBlocks.createTag("potted_fern");
    }

    return tag; // keep empty
  }

  @Override
  public boolean isBiomeDependant() {
    return true; // (in reality it is only used for fern)
  }
}
