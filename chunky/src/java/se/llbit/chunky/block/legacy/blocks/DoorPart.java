package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.Door;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class DoorPart extends UnfinalizedLegacyBlock {

  public DoorPart(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    if (isTopPart(data)) {
      // top part
      if (state.getY() > state.getYMin()) {
        Material bottomPart = state.getMaterial(0, -1, 0);
        if (bottomPart instanceof Door) {
          // finalization is from bottom to top, so the bottom part is already finalized
          Door doorBelow = (Door) bottomPart;
          state.replaceCurrentBlock(
              createTag("upper", doorBelow.getFacing(), doorBelow.getHinge(), doorBelow.isOpen()));
          return;
        }
      }
    } else {
      // bottom part
      if (state.getY() < state.getYMax() - 1) {
        Material topPart = state.getMaterial(0, 1, 0);
        if (topPart instanceof DoorPart) {
          // finalization is from bottom to top, so the top part is not finalized yet
          DoorPart doorAbove = (DoorPart) topPart;
          state.replaceCurrentBlock(
              createTag("lower", getFacing(data), getHinge(doorAbove.data),
                  isOpen(data)));
          return;
        }
      }
    }

    // otherwise unwrap (shouldn't happen)
    state.replaceCurrentBlock(tag);
  }

  private CompoundTag createTag(String half, String facing, String hinge, boolean open) {
    CompoundTag tag = LegacyBlocks.createTag(block.name);
    LegacyBlocks.stringTag(tag, "half", half);
    LegacyBlocks.stringTag(tag, "facing", facing);
    LegacyBlocks.stringTag(tag, "hinge", hinge);
    LegacyBlocks.boolTag(tag, "open", open);
    return tag;
  }

  private boolean isTopPart(int data) {
    return (data & 0b1000) != 0;
  }

  private boolean isOpen(int bottomData) {
    return (bottomData & 0b0100) != 0;
  }

  private static String getFacing(int bottomData) {
    return new String[]{"east", "south", "west", "north"}[(bottomData & 0b011) % 4];
  }

  private static String getHinge(int topData) {
    return ((topData & 0b0001) != 0) ? "right": "left";//"left" : "right";
  }
}
