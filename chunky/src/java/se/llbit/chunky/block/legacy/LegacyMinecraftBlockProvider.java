package se.llbit.chunky.block.legacy;

import java.util.Collection;
import java.util.Collections;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockProvider;
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
      // case "oak_door":
      //   return new Door(name, ctag);
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
    }
    Log.warn("Unsupported legacy block: " + name);
    return null;
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    return Collections.emptyList();
  }
}
