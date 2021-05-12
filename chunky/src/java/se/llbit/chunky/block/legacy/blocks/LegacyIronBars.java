package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyIronBars extends UnfinalizedLegacyBlock {

  public LegacyIronBars(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    boolean north = isIronBarConnector(state.getMaterial(0, 0, -1), "north");
    boolean south = isIronBarConnector(state.getMaterial(0, 0, 1), "south");
    boolean east = isIronBarConnector(state.getMaterial(1, 0, 0), "east");
    boolean west = isIronBarConnector(state.getMaterial(-1, 0, 0), "west");

    CompoundTag newTag = LegacyBlocks.createTag("iron_bars");
    LegacyBlocks.boolTag(newTag, "east", east);
    LegacyBlocks.boolTag(newTag, "west", west);
    LegacyBlocks.boolTag(newTag, "north", north);
    LegacyBlocks.boolTag(newTag, "south", south);

    state.replaceCurrentBlock(newTag);
  }

  private static boolean isIronBarConnector(Material block, String direction) {
    String name = LegacyBlockUtils.getName(block);
    if (name.equals("cobblestone_wall") || name.equals("mossy_cobblestone_wall") || name
        .endsWith("_fence") || name
        .endsWith("_leaves")) { // in 1.9 iron bars do connect to leaves
      return false;
    }

    // 1.12 (?) stairs connection logic (only connect to the high side)
    String adjacentStairsFacing = LegacyBlockUtils.getStairsFacing(block);
    if (adjacentStairsFacing != null) {
      switch (direction) {
        case "north":
          return adjacentStairsFacing.equals("south");
        case "south":
          return adjacentStairsFacing.equals("north");
        case "east":
          return adjacentStairsFacing.equals("west");
        case "west":
          return adjacentStairsFacing.equals("east");
        default:
          return false;
      }
    }

    return block.solid || name.equals("iron_bars");
  }
}
