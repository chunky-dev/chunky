package se.llbit.chunky.block.legacy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockProvider;
import se.llbit.nbt.Tag;

public class LegacyMinecraftBlockProvider implements BlockProvider {

  private static String[] blockList = new String[]{
      "minecraft:oak_door"
  };

  @Override
  public Block getBlockByTag(String name, Tag tag) {
    switch (name) {
      case "minecraft:oak_door":
        return new LegacyBlock(name, tag);
    }
    return null;
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    return Collections.emptyList();
  }
}
