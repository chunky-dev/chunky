package se.llbit.chunky.block.legacy;

import java.util.Collection;
import java.util.Collections;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockProvider;
import se.llbit.log.Log;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

public class LegacyMinecraftBlockProvider implements BlockProvider {

  private static String[] blockList = new String[]{
      "#legacy_minecraft:oak_door",
      "#legacy_minecraft:grass_block",
      "#legacy_minecraft:vine"
  };

  @Override
  public Block getBlockByTag(String name, Tag tag) {
    if (!name.startsWith("#legacy_minecraft:")) {
      return null;
    }

    switch (name.substring(18)) {
      case "oak_door":
      case "grass_block":
      case "mycelium":
      case "podzol":
      case "vine":
        return new UnfinalizedLegacyBlock(name, (CompoundTag) tag);
    }
    Log.warn("Unsupported legacy block: " + name);
    return null;
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    return Collections.emptyList();
  }
}
