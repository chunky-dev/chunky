package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.BlockFace;
import se.llbit.chunky.block.FinalizationState;
import se.llbit.chunky.block.legacy.LegacyBlockUtils;
import se.llbit.chunky.block.legacy.LegacyBlocks;
import se.llbit.chunky.block.legacy.UnfinalizedLegacyBlock;
import se.llbit.chunky.world.Material;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

/**
 * A 1.6.4 or older nether portal block.
 * <p>
 * Before 1.7, nether portals determined their orientation by looking at the surrounding blocks.
 */
public class LegacyNetherPortal extends UnfinalizedLegacyBlock {

  public LegacyNetherPortal(String name, CompoundTag tag) {
    super(name, tag);
  }

  @Override
  public void finalizeBlock(FinalizationState state) {
    if (isNetherPortalConnector(state.getMaterial(BlockFace.NORTH))) {
      state.replaceCurrentBlock(createTag("z"));
    } else if (isNetherPortalConnector(state.getMaterial(BlockFace.EAST))) {
      state.replaceCurrentBlock(createTag("x"));
    } else if (isNetherPortalConnector(state.getMaterial(BlockFace.SOUTH))) {
      state.replaceCurrentBlock(createTag("z"));
    } else if (isNetherPortalConnector(state.getMaterial(BlockFace.WEST))) {
      state.replaceCurrentBlock(createTag("x"));
    } else {
      // not connected, just unwrap
      state.replaceCurrentBlock(tag);
    }
  }

  private static Tag createTag(String axis) {
    return LegacyBlocks.stringTag(LegacyBlocks.createTag("nether_portal"), "axis", axis);
  }

  private static boolean isNetherPortalConnector(Material material) {
    String name = LegacyBlockUtils.getName(material);
    return name.equals("nether_portal") || name.equals("obsidian");
  }
}
