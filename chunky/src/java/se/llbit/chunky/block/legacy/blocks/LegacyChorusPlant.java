package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockProviderRegistry;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyChorusPlant extends UnfinalizedLegacyBlock {

  public LegacyChorusPlant(String name, CompoundTag tag,
      BlockProviderRegistry blockProviders) {
    super(name, tag, blockProviders);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    CompoundTag newTag = LegacyBlocks.createTag(this.name);

    LegacyBlocks.boolTag(newTag, "up", isConnect(state.getMaterial(0, 1, 0)));
    LegacyBlocks.boolTag(newTag, "down", isConnectDown(state.getMaterial(0, -1, 0)));
    LegacyBlocks.boolTag(newTag, "east", isConnect(state.getMaterial(1, 0, 0)));
    LegacyBlocks.boolTag(newTag, "west", isConnect(state.getMaterial(-1, 0, 0)));
    LegacyBlocks.boolTag(newTag, "north", isConnect(state.getMaterial(0, 0, -1)));
    LegacyBlocks.boolTag(newTag, "south", isConnect(state.getMaterial(0, 0, 1)));

    state.replaceCurrentBlock(newTag);
  }

  private static boolean isConnect(Material block) {
    String name = LegacyBlockUtils.getName(block);
    return name.equals("chorus_plant") || name.equals("chorus_flower");
  }

  private static boolean isConnectDown(Material block) {
    String name = LegacyBlockUtils.getName(block);
    return name.equals("chorus_plant") || name.equals("chorus_flower") || name.equals("end_stone");
  }
}
