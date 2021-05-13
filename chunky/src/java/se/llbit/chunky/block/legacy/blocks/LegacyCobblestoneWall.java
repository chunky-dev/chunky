package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyCobblestoneWall extends UnfinalizedLegacyBlock {

  public LegacyCobblestoneWall(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    boolean north = isStoneWallConnector(state.getMaterial(0, 0, -1), BlockFace.NORTH);
    boolean south = isStoneWallConnector(state.getMaterial(0, 0, 1), BlockFace.SOUTH);
    boolean east = isStoneWallConnector(state.getMaterial(1, 0, 0), BlockFace.EAST);
    boolean west = isStoneWallConnector(state.getMaterial(-1, 0, 0), BlockFace.WEST);
    boolean up = false;

    if (!(north && south && !east && !west) && !(!north && !south && east && west)) {
      // corner or start and end
      up = true;
    } else if (state.getY() < state.getYMax() - 1) {
      // check if connected to a block above
      Material above = state.getMaterial(0, 1, 0);
      up = isStoneWallTopConnector(above);
    }

    state.replaceCurrentBlock(createTag(north, south, east, west, up));
  }

  private static boolean isStoneWallConnector(Material block, BlockFace direction) {
    String name = LegacyBlockUtils.getName(block);
    if (name.equals("cobblestone_wall") || name.equals("mossy_cobblestone_wall")) {
      return true;
    }
    if (name.endsWith("_fence")
        || name.equals("glass") || name.endsWith("_stained_glass")
        || name.equals("iron_bars")
        || name.equals("glass_pane") || name.endsWith("stained_glass_pane")
        || name.endsWith("_leaves")) { // in 1.9 stone walls do connect to leaves
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

  private static boolean isStoneWallTopConnector(Material block) {
    String name = LegacyBlockUtils.getName(block);
    return block.solid || name.equals("water") || name.equals("lava") || name.equals("torch")
        || name.endsWith("_fence") || name.equals("chest") || name.equals("redstone_torch")
        || name.equals("trapped_chest") || name.equals("ender_chest")
        || name.equals("dragon_egg") || name.equals("hopper") || name.equals("end_rod");
  }

  private CompoundTag createTag(boolean north, boolean south, boolean east, boolean west,
      boolean up) {
    CompoundTag tag = LegacyBlocks.createTag(block.name);
    LegacyBlocks.boolTag(tag, "north", north);
    LegacyBlocks.boolTag(tag, "south", south);
    LegacyBlocks.boolTag(tag, "east", east);
    LegacyBlocks.boolTag(tag, "west", west);
    LegacyBlocks.boolTag(tag, "up", up);
    return tag;
  }
}
