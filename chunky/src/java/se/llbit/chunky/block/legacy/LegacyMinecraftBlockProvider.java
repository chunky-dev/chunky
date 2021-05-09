package se.llbit.chunky.block.legacy;

import java.util.Collection;
import java.util.Collections;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockProvider;
import se.llbit.chunky.block.legacy.blocks.Chest;
import se.llbit.chunky.block.legacy.blocks.DoorPart;
import se.llbit.chunky.block.legacy.blocks.MelonStem;
import se.llbit.chunky.block.legacy.blocks.PumpkinStem;
import se.llbit.chunky.block.legacy.blocks.SnowCoverableBlock;
import se.llbit.chunky.block.legacy.blocks.Vine;
import se.llbit.log.Log;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

public class LegacyMinecraftBlockProvider implements BlockProvider {

  @Override
  public Block getBlockByTag(String name, Tag tag) {
    if (!name.startsWith("#legacy_minecraft:")) {
      return null;
    }
    if (!(tag instanceof CompoundTag)) {
      Log.warn("Expected a CompoundTag for " + name);
      return null;
    }
    CompoundTag ctag = (CompoundTag) tag;

    switch (name.substring(18)) {
      case "grass_block":
      case "mycelium":
      case "podzol":
        return new SnowCoverableBlock(name, ctag);
      case "vine":
        return new Vine(name, ctag);
      case "pumpkin_stem":
        return new PumpkinStem(name, ctag);
      case "melon_stem":
        return new MelonStem(name, ctag);
      case "chest":
      case "trapped_chest":
        return new Chest(name, ctag);
      case "oak_door":
      case "iron_door":
      case "spruce_door":
      case "birch_door":
      case "jungle_door":
      case "acacia_door":
      case "dark_oak_door":
        return new DoorPart(name, ctag);
    }
    Log.warn("Unsupported legacy block: " + name);
    return null;
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    return Collections.emptyList();
  }
}
