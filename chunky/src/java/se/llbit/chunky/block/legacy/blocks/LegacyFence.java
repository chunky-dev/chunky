package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
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
      boolean north = isNetherBrickFenceConnector(state.getMaterial(0, 0, -1), BlockFace.NORTH);
      boolean south = isNetherBrickFenceConnector(state.getMaterial(0, 0, 1), BlockFace.SOUTH);
      boolean east = isNetherBrickFenceConnector(state.getMaterial(1, 0, 0), BlockFace.EAST);
      boolean west = isNetherBrickFenceConnector(state.getMaterial(-1, 0, 0), BlockFace.WEST);

      if (north || south || east || west) {
        state.replaceCurrentBlock(createTag(north, south, east, west));
      } else {
        // not connected, just unwrap
        state.replaceCurrentBlock(tag);
      }
    } else {
      // wood fence
      boolean north = isFenceConnector(state.getMaterial(0, 0, -1), BlockFace.NORTH);
      boolean south = isFenceConnector(state.getMaterial(0, 0, 1), BlockFace.SOUTH);
      boolean east = isFenceConnector(state.getMaterial(1, 0, 0), BlockFace.EAST);
      boolean west = isFenceConnector(state.getMaterial(-1, 0, 0), BlockFace.WEST);

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

  private boolean isFenceConnector(Material block, BlockFace direction) {
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
    BlockFace adjacentStairsFacing = LegacyBlockUtils.getStairsFacing(block);
    if (adjacentStairsFacing != null) {
      return adjacentStairsFacing.getOppositeFace() == direction;
    }

    // fence gate connection
    BlockFace adjacentFenceGateFacing = LegacyBlockUtils.getFenceGateFacing(block);
    if (adjacentFenceGateFacing != null) {
      switch (direction) {
        case NORTH:
        case SOUTH:
          return adjacentFenceGateFacing == BlockFace.EAST
              || adjacentFenceGateFacing == BlockFace.WEST;
        case EAST:
        case WEST:
          return adjacentFenceGateFacing == BlockFace.NORTH
              || adjacentFenceGateFacing == BlockFace.SOUTH;
      }
    }

    return block.solid || block.name.endsWith("_fence");
  }

  private boolean isNetherBrickFenceConnector(Material block, BlockFace direction) {
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
    BlockFace adjacentStairsFacing = LegacyBlockUtils.getStairsFacing(block);
    if (adjacentStairsFacing != null) {
      return adjacentStairsFacing.getOppositeFace() == direction;
    }

    // fence gate connection
    BlockFace adjacentFenceGateFacing = LegacyBlockUtils.getFenceGateFacing(block);
    if (adjacentFenceGateFacing != null) {
      switch (direction) {
        case NORTH:
        case SOUTH:
          return adjacentFenceGateFacing == BlockFace.EAST
              || adjacentFenceGateFacing == BlockFace.WEST;
        case EAST:
        case WEST:
          return adjacentFenceGateFacing == BlockFace.NORTH
              || adjacentFenceGateFacing == BlockFace.SOUTH;
      }
    }

    return block.solid;
  }
}
