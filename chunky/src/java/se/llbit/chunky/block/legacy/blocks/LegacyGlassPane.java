package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyGlassPane extends UnfinalizedLegacyBlock {

  public LegacyGlassPane(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    boolean north = isGlassPaneConnector(state.getMaterial(0, 0, -1), "north");
    boolean south = isGlassPaneConnector(state.getMaterial(0, 0, 1), "south");
    boolean east = isGlassPaneConnector(state.getMaterial(1, 0, 0), "east");
    boolean west = isGlassPaneConnector(state.getMaterial(-1, 0, 0), "west");

    if (north || south || east || west) {
      state.replaceCurrentBlock(createTag(north, south, east, west));
    } else {
      // not connected, just unwrap
      state.replaceCurrentBlock(tag);
    }
  }

  private boolean isGlassPaneConnector(Material block, String direction) {
    String name = LegacyBlockUtils.getName(block);
    if (name.equals("cobblestone_wall") || name.equals("mossy_cobblestone_wall") || name
        .endsWith("_fence") || name
        .endsWith("_leaves")) { // in 1.9 glass panes do connect to leaves
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

    return block.solid || block.name.endsWith("glass_pane");
  }

  private CompoundTag createTag(boolean north, boolean south, boolean east, boolean west) {
    CompoundTag tag = LegacyBlocks.createTag(block.name);
    LegacyBlocks.boolTag(tag, "north", north);
    LegacyBlocks.boolTag(tag, "south", south);
    LegacyBlocks.boolTag(tag, "east", east);
    LegacyBlocks.boolTag(tag, "west", west);
    return tag;
  }
}
