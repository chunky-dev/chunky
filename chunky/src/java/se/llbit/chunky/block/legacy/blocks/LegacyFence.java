package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyFence extends UnfinalizedLegacyBlock {

  public LegacyFence(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    if (id == 113) {
      // nether brick fence
      boolean north = isNetherBrickFenceConnector(state.getMaterial(0, 0, -1), "north");
      boolean south = isNetherBrickFenceConnector(state.getMaterial(0, 0, 1), "south");
      boolean east = isNetherBrickFenceConnector(state.getMaterial(1, 0, 0), "east");
      boolean west = isNetherBrickFenceConnector(state.getMaterial(-1, 0, 0), "west");

      if (north || south || east || west) {
        state.replaceCurrentBlock(createTag(north, south, east, west));
      } else {
        // not connected, just unwrap
        state.replaceCurrentBlock(tag);
      }
    } else {
      // wood fence
      boolean north = isFenceConnector(state.getMaterial(0, 0, -1), "north");
      boolean south = isFenceConnector(state.getMaterial(0, 0, 1), "south");
      boolean east = isFenceConnector(state.getMaterial(1, 0, 0), "east");
      boolean west = isFenceConnector(state.getMaterial(-1, 0, 0), "west");

      if (north || south || east || west) {
        state.replaceCurrentBlock(createTag(north, south, east, west));
      } else {
        // not connected, just unwrap
        state.replaceCurrentBlock(tag);
      }
    }
  }

  private CompoundTag createTag(boolean north, boolean south, boolean east, boolean west) {
    CompoundTag tag = LegacyBlocks.createTag(name);
    LegacyBlocks.boolTag(tag, "north", north);
    LegacyBlocks.boolTag(tag, "south", south);
    LegacyBlocks.boolTag(tag, "east", east);
    LegacyBlocks.boolTag(tag, "west", west);
    return tag;
  }

  private boolean isFenceConnector(Material block, String direction) {
    String name = LegacyBlockUtils.getName(block);
    if (name.equals("cobblestone_wall") || name.equals("mossy_cobblestone_wall")
        || name.equals("glass") || name.endsWith("_stained_glass")
        || name.equals("iron_bars")
        || name.equals("glass_pane") || name.endsWith("stained_glass_pane")
        || name.equals("nether_brick_fence")
        || name.endsWith("_leaves")) { // in 1.9 glass panes do connect to leaves
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

    // fence gate connection
    String adjacentFenceGateFacing = LegacyBlockUtils.getFenceGateFacing(block);
    if (adjacentFenceGateFacing != null) {
      switch (direction) {
        case "north":
        case "south":
          return adjacentFenceGateFacing.equals("east") || adjacentFenceGateFacing.equals("west");
        case "east":
        case "west":
          return adjacentFenceGateFacing.equals("north") || adjacentFenceGateFacing.equals("south");
      }
    }

    return block.solid || block.name.endsWith("_fence");
  }

  private boolean isNetherBrickFenceConnector(Material block, String direction) {
    String name = LegacyBlockUtils.getName(block);
    if (name.equals("nether_brick_fence")) {
      return true;
    }
    if (name.equals("cobblestone_wall") || name.equals("mossy_cobblestone_wall")
        || name.endsWith("_fence")
        || name.equals("glass") || name.endsWith("_stained_glass")
        || name.equals("iron_bars")
        || name.equals("glass_pane") || name.endsWith("stained_glass_pane")
        || name.endsWith("_leaves")) { // in 1.9 glass panes do connect to leaves
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

    // fence gate connection
    String adjacentFenceGateFacing = LegacyBlockUtils.getFenceGateFacing(block);
    if (adjacentFenceGateFacing != null) {
      switch (direction) {
        case "north":
        case "south":
          return adjacentFenceGateFacing.equals("east") || adjacentFenceGateFacing.equals("west");
        case "east":
        case "west":
          return adjacentFenceGateFacing.equals("north") || adjacentFenceGateFacing.equals("south");
      }
    }

    return block.solid;
  }
}
