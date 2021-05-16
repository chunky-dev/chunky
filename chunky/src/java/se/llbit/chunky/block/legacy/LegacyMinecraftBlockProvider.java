package se.llbit.chunky.block.legacy;

import java.util.Collection;
import java.util.Collections;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockProvider;
import se.llbit.chunky.block.legacy.blocks.*;
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
        return new LegacySnowCoverableBlock(name, ctag);
      case "vine":
        return new LegacyVine(name, ctag);
      case "pumpkin_stem":
        return new LegacyPumpkinStem(name, ctag);
      case "melon_stem":
        return new LegacyMelonStem(name, ctag);
      case "chest":
      case "trapped_chest":
        return new LegacyChest(name, ctag);
      case "oak_door":
      case "iron_door":
      case "spruce_door":
      case "birch_door":
      case "jungle_door":
      case "acacia_door":
      case "dark_oak_door":
        return new LegacyDoorPart(name, ctag);
      case "oak_stairs":
      case "cobblestone_stairs":
      case "brick_stairs":
      case "stone_brick_stairs":
      case "nether_brick_stairs":
      case "sandstone_stairs":
      case "spruce_stairs":
      case "birch_stairs":
      case "jungle_stairs":
      case "quartz_stairs":
      case "acacia_stairs":
      case "dark_oak_stairs":
      case "red_sandstone_stairs":
      case "purpur_stairs":
        return new LegacyStairs(name, ctag);
      case "iron_bars":
        return new LegacyIronBars(name, ctag);
      case "redstone_wire":
        return new LegacyRedstoneWire(name, ctag);
      case "oak_fence_gate":
      case "spruce_fence_gate":
      case "birch_fence_gate":
      case "jungle_fence_gate":
      case "dark_oak_fence_gate":
      case "acacia_fence_gate":
        return new LegacyFenceGate(name, ctag);
      case "chorus_plant":
        return new LegacyChorusPlant(name, ctag);
      case "sunflower":
      case "lilac":
      case "tall_grass":
      case "large_fern":
      case "rose_bush":
      case "peony":
        return new LegacyLargeFlower(name, ctag);
      case "glass_pane":
      case "white_stained_glass_pane":
      case "orange_stained_glass_pane":
      case "magenta_stained_glass_pane":
      case "light_blue_stained_glass_pane":
      case "yellow_stained_glass_pane":
      case "lime_stained_glass_pane":
      case "pink_stained_glass_pane":
      case "gray_stained_glass_pane":
      case "light_gray_stained_glass_pane":
      case "cyan_stained_glass_pane":
      case "purple_stained_glass_pane":
      case "blue_stained_glass_pane":
      case "brown_stained_glass_pane":
      case "green_stained_glass_pane":
      case "red_stained_glass_pane":
      case "black_stained_glass_pane":
        return new LegacyGlassPane(name, ctag);
      case "red_bed":
        return new LegacyBed(name, ctag);
      case "oak_fence":
      case "nether_brick_fence":
      case "spruce_fence":
      case "birch_fence":
      case "jungle_fence":
      case "dark_oak_fence":
      case "acacia_fence":
        return new LegacyFence(name, ctag);
      case "flower_pot":
        return new LegacyFlowerPot(name, ctag);
      case "cobblestone_wall":
      case "mossy_cobblestone_wall":
        return new LegacyCobblestoneWall(name, ctag);
      case "skull":
        return new LegacySkull(name, ctag);
      case "banner":
        return new LegacyBanner(name, ctag);
      case "wall_banner":
        return new LegacyWallBanner(name, ctag);
      case "tripwire":
        return new LegacyTripwire(name, ctag);
      case "nether_portal":
        return new LegacyNetherPortal(name, ctag);
      case "fire":
        return new LegacyFire(name, ctag);
    }
    Log.warn("Unsupported legacy block: " + name);
    return null;
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    return Collections.emptyList();
  }
}
