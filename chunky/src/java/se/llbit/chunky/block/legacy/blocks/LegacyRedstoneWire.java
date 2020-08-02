package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockProviderRegistry;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.Repeater;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;

public class LegacyRedstoneWire extends UnfinalizedLegacyBlock {

  public LegacyRedstoneWire(String name, CompoundTag tag,
      BlockProviderRegistry blockProviders) {
    super(name, tag, blockProviders);
  }

  @Override
  public void finalizeBlock(FinalizationState rawState) {
    FinalizationState state = new LegacyBlockUtils.FinalizationStateCache(rawState);
    String east, north, south, west;
    east = north = south = west = "none";

    if (isRedstoneConnector(state.getMaterial(-1, 0, 0), 1)) {
      west = "side";
    } else if (isAir(state.getMaterial(-1, 0, 0)) && isRedstone(state.getMaterial(-1, -1, 0))) {
      west = "side";
    }

    if (isRedstoneConnector(state.getMaterial(1, 0, 0), 1)) {
      east = "side";
    } else if (isAir(state.getMaterial(1, 0, 0)) && isRedstone(state.getMaterial(1, -1, 0))) {
      east = "side";
    }

    if (isRedstoneConnector(state.getMaterial(0, 0, -1), 0)) {
      north = "side";
    } else if (isAir(state.getMaterial(0, 0, -1)) && isRedstone(state.getMaterial(0, -1, -1))) {
      north = "side";
    }

    if (isRedstoneConnector(state.getMaterial(0, 0, 1), 0)) {
      south = "side";
    } else if (isAir(state.getMaterial(0, 0, 1)) && isRedstone(state.getMaterial(0, -1, 1))) {
      south = "side";
    }

    if (state.getMaterial(0, 1, 0).name.equals("minecraft:air")) {
      if (state.getMaterial(-1, 0, 0).solid && isRedstone(state.getMaterial(-1, 1, 0))) {
        west = "up";
      }
      if (state.getMaterial(1, 0, 0).solid && isRedstone(state.getMaterial(1, 1, 0))) {
        east = "up";
      }
      if (state.getMaterial(0, 0, -1).solid && isRedstone(state.getMaterial(0, 1, -1))) {
        north = "up";
      }
      if (state.getMaterial(0, 0, 1).solid && isRedstone(state.getMaterial(0, 1, 1))) {
        south = "up";
      }
    }

    CompoundTag tag = LegacyBlocks.createTag("redstone_wire");
    LegacyBlocks.stringTag(tag, "power", Integer.toString(data));
    LegacyBlocks.stringTag(tag, "east", east);
    LegacyBlocks.stringTag(tag, "west", west);
    LegacyBlocks.stringTag(tag, "north", north);
    LegacyBlocks.stringTag(tag, "south", south);

    state.replaceCurrentBlock(tag);
  }

  private static boolean isRedstone(Material block) {
    return LegacyBlockUtils.getName(block).equals("redstone_wire");
  }

  private static boolean isRedstoneConnector(Material block, int side) {
    String name = LegacyBlockUtils.getName(block);
    switch (name) {
      case "redstone_wire":
      case "redstone_torch":
      case "redstone_wall_torch":
      case "lever":
      case "stone_button":
      case "tripwire_hook":
      case "comparator":
      case "daylight_sensor":
        return true;

      case "repeater":
        if (block instanceof Repeater) {
          return ((Repeater) block).getFacing() % 2 == side;
        }
    }
    return false;
  }

  private static boolean isAir(Material block) {
    return LegacyBlockUtils.getName(block).equals("air");
  }
}
