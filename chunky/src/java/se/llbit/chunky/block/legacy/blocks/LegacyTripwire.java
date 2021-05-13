package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.Tripwire;
import se.llbit.chunky.block.TripwireHook;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

public class LegacyTripwire extends UnfinalizedLegacyBlock {

  public LegacyTripwire(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    boolean attached = (data & 0b0100) != 0;
    boolean north = isTripwireConnector(state.getMaterial(0, 0, -1), "north");
    boolean south = isTripwireConnector(state.getMaterial(0, 0, 1), "south");
    boolean east = isTripwireConnector(state.getMaterial(1, 0, 0), "east");
    boolean west = isTripwireConnector(state.getMaterial(-1, 0, 0), "west");
    state.replaceCurrentBlock(createTag(attached, north, south, east, west));
  }

  private Tag createTag(boolean attached, boolean north, boolean south, boolean east,
      boolean west) {
    CompoundTag tag = LegacyBlocks.createTag(name);
    LegacyBlocks.boolTag(tag, "attached", attached);
    LegacyBlocks.boolTag(tag, "north", north);
    LegacyBlocks.boolTag(tag, "south", south);
    LegacyBlocks.boolTag(tag, "east", east);
    LegacyBlocks.boolTag(tag, "west", west);
    return tag;
  }

  private static boolean isTripwireConnector(Material block, String direction) {
    if (block instanceof Tripwire || block instanceof LegacyTripwire) {
      return true;
    }

    if (block instanceof TripwireHook) {
      String hookFacing = ((TripwireHook) block).getFacing();
      switch (direction) {
        case "north":
          return hookFacing.equals("south");
        case "south":
          return hookFacing.equals("north");
        case "east":
          return hookFacing.equals("west");
        case "west":
          return hookFacing.equals("east");
      }
    }

    return false;
  }
}
