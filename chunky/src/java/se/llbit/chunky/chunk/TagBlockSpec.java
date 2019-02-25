package se.llbit.chunky.chunk;

import se.llbit.chunky.block.*;
import se.llbit.nbt.Tag;
import se.llbit.util.NotNull;

public class TagBlockSpec implements BlockSpec {
  private static final int MAGIC = 0xE6FFE636;
  public final Tag tag;

  public TagBlockSpec(@NotNull  Tag tag) {
    this.tag = tag;
  }

  @Override public int hashCode() {
    return MAGIC ^ tag.hashCode();
  }

  @Override public boolean equals(Object obj) {
    return (obj instanceof TagBlockSpec)
        && ((TagBlockSpec) obj).tag.equals(tag);
  }

  /**
   * Converts NBT block data to Chunky block object.
   */
  @Override public Block toBlock() {
    // Reference: https://minecraft.gamepedia.com/Java_Edition_data_values#Blocks
    String name = tag.get("Name").stringValue("minecraft:air");
    if (name.startsWith("minecraft:")) {
      name = name.substring(10);
      // TODO: add a new class for each block type...
      // TODO: convert all old block IDs to the new block types.
      // TODO: clean up - register block loaders in hash map instead
      switch (name) {
        case "air":
        case "cave_air":
        case "void_air":
          return Air.INSTANCE;
        case "infested_stone":
        case "stone":
          return new Stone();
        case "granite":
          return new Granite();
        case "polished_granite":
          return new PolishedGranite();
        case "diorite":
          return new Diorite();
        case "polished_diorite":
          return new PolishedDiorite();
        case "andesite":
          return new Andesite();
        case "polished_andesite":
          return new PolishedAndesite();
        case "grass_block":
          return new GrassBlock();
        case "dirt":
          return new Dirt();
        case "coarse_dirt":
          return new CoarseDirt();
        case "podzol":
          return new Podzol();
        case "infested_cobblestone":
        case "cobblestone":
          return new Cobblestone();
        case "oak_planks":
          return new OakPlanks();
        case "spruce_planks":
          return new SprucePlanks();
        case "birch_planks":
          return new BirchPlanks();
        case "jungle_planks":
          return new JunglePlanks();
        case "acacia_planks":
          return new AcaciaPlanks();
        case "dark_oak_planks":
          return new DarkOakPlanks();
        case "oak_sapling":
          return new OakSapling();
        case "spruce_sapling":
          return new SpruceSapling();
        case "birch_sapling":
          return new BirchSapling();
        case "jungle_sapling":
          return new JungleSapling();
        case "acacia_sapling":
          return new AcaciaSapling();
        case "dark_oak_sapling":
          return new DarkOakSapling();
        case "water": {
          int level = 0;
          try {
            level = Integer.parseInt(tag.get("Properties").get("level").stringValue("0"));
          } catch (NumberFormatException ignored) {
          }
          return new Water(level);
        }
        case "bubble_column":
          return new UnknownBlock(name);
        case "lava": {
          int level = 0;
          try {
            level = Integer.parseInt(tag.get("Properties").get("level").stringValue("0"));
          } catch (NumberFormatException ignored) {
          }
          return new Lava(level);
        }
        case "bedrock":
          return new Bedrock();
        case "sand":
          return new Sand();
        case "red_sand":
          return new RedSand();
        case "gravel":
          return new Gravel();
        case "gold_ore":
          return new GoldOre();
        case "iron_ore":
          return new IronOre();
        case "coal_ore":
          return new CoalOre();
        case "oak_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new OakLog(axis);
        }
        case "spruce_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new SpruceLog(axis);
        }
        case "birch_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new BirchLog(axis);
        }
        case "jungle_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new JungleLog(axis);
        }
        case "acacia_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new AcaciaLog(axis);
        }
        case "dark_oak_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new DarkOakLog(axis);
        }
        case "stripped_oak_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new StrippedOakLog(axis);
        }
        case "stripped_spruce_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new StrippedSpruceLog(axis);
        }
        case "stripped_birch_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new StrippedBirchLog(axis);
        }
        case "stripped_jungle_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new StrippedJungleLog(axis);
        }
        case "stripped_acacia_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new StrippedAcaciaLog(axis);
        }
        case "stripped_dark_oak_log": {
          String axis = tag.get("Properties").get("axis").stringValue("y");
          return new StrippedDarkOakLog(axis);
        }
        case "stripped_oak_wood":
          return new StrippedOakWood();
        case "stripped_spruce_wood":
          return new StrippedSpruceWood();
        case "stripped_birch_wood":
          return new StrippedBirchWood();
        case "stripped_jungle_wood":
          return new StrippedJungleWood();
        case "stripped_acacia_wood":
          return new StrippedAcaciaWood();
        case "stripped_dark_oak_wood":
          return new StrippedDarkOakWood();
        case "oak_wood":
          return new OakWood();
        case "spruce_wood":
          return new SpruceWood();
        case "birch_wood":
          return new BirchWood();
        case "jungle_wood":
          return new JungleWood();
        case "acacia_wood":
          return new AcaciaWood();
        case "dark_oak_wood":
          return new DarkOakWood();
        case "oak_leaves":
          return new OakLeaves();
        case "spruce_leaves":
          return new SpruceLeaves();
        case "birch_leaves":
          return new BirchLeaves();
        case "jungle_leaves":
          return new JungleLeaves();
        case "acacia_leaves":
          return new AcaciaLeaves();
        case "dark_oak_leaves":
          return new DarkOakLeaves();
        case "sponge":
          return new Sponge();
        case "wet_sponge":
          return new WetSponge();
        case "glass":
          return new Glass();
        case "lapis_ore":
          return new LapisOre();
        case "lapis_block":
          return new LapisBlock();
        case "dispenser":
          // TODO
          return new UnknownBlock(name);
        case "sandstone":
          return new Sandstone();
        case "chiseled_sandstone":
          return new ChiseledStandstone();
        case "cut_sandstone":
          return new CutSandstone();
        case "note_block":
          return new NoteBlock();
        case "powered_rail":
          // TODO
          return new UnknownBlock(name);
        case "detector_rail":
          // TODO
          return new UnknownBlock(name);
        case "sticky_piston":
          // TODO
          return new UnknownBlock(name);
        case "cobweb":
          return new Cobweb();
        case "grass":
          return new Grass();
        case "fern":
          return new Fern();
        case "dead_bush":
          return new DeadBush();
        case "seagrass":
          // TODO 1.13
          return new UnknownBlock(name);
        case "tall_seagrass":
          // TODO 1.13
          return new UnknownBlock(name);
        case "sea_pickle":
          // TODO 1.13
          return new UnknownBlock(name);
        case "piston":
          // TODO
          return new UnknownBlock(name);
        case "piston_head":
          // TODO
          return new UnknownBlock(name);
        case "moving_piston":
          // TODO
          return new UnknownBlock(name);
        case "white_wool":
          return new WoolWhite();
        case "orange_wool":
          return new WoolOrange();
        case "magenta_wool":
          return new WoolMagenta();
        case "light_blue_wool":
          return new WoolLightBlue();
        case "yellow_wool":
          return new WoolYellow();
        case "lime_wool":
          return new WoolLime();
        case "pink_wool":
          return new WoolPink();
        case "gray_wool":
          return new WoolGray();
        case "light_gray_wool":
          return new WoolLightGray();
        case "cyan_wool":
          return new WoolCyan();
        case "purple_wool":
          return new WoolPurple();
        case "blue_wool":
          return new WoolBlue();
        case "brown_wool":
          return new WoolBrown();
        case "green_wool":
          return new WoolGreen();
        case "red_wool":
          return new WoolRed();
        case "black_wool":
          return new WoolBlack();
        case "dandelion":
          return new Dandelion();
        case "poppy":
          return new Poppy();
        case "blue_orchid":
          return new BlueOrchid();
        case "allium":
          return new Allium();
        case "azure_bluet":
          return new AzureBluet();
        case "red_tulip":
          return new TulipRed();
        case "orange_tulip":
          return new TulipOrange();
        case "white_tulip":
          return new TulipWhite();
        case "pink_tulip":
          return new TulipPink();
        case "oxeye_daisy":
          return new OxeyeDaisy();
        case "cornflower":
          return new UnknownBlock(name);
        case "lily_of_the_valley":
          return new UnknownBlock(name);
        case "wither_rose":
          return new UnknownBlock(name);
        case "brown_mushroom":
          return new BrownMushroom();
        case "red_mushroom":
          return new RedMushroom();
        case "gold_block":
          return new GoldBlock();
        case "iron_block":
          return new IronBlock();
        case "oak_slab":
          // TODO
          return new UnknownBlock(name);
        case "spruce_slab":
          // TODO
          return new UnknownBlock(name);
        case "birch_slab":
          // TODO
          return new UnknownBlock(name);
        case "jungle_slab":
          // TODO
          return new UnknownBlock(name);
        case "acacia_slab":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_slab":
          // TODO
          return new UnknownBlock(name);
        case "stone_slab":
          // TODO
          return new UnknownBlock(name);
        case "smooth_stone_slab":
          // TODO
          return new UnknownBlock(name);
        case "sandstone_slab":
          // TODO
          return new UnknownBlock(name);
        case "petrified_oak_slab":
          // TODO
          return new UnknownBlock(name);
        case "cobblestone_slab":
          // TODO
          return new UnknownBlock(name);
        case "brick_slab":
          // TODO
          return new UnknownBlock(name);
        case "stone_brick_slab":
          // TODO
          return new UnknownBlock(name);
        case "nether_brick_slab":
          // TODO
          return new UnknownBlock(name);
        case "quartz_slab":
          // TODO
          return new UnknownBlock(name);
        case "red_sandstone_slab":
          // TODO
          return new UnknownBlock(name);
        case "purpur_slab":
          // TODO
          return new UnknownBlock(name);
        case "prismarine_slab":
          // TODO
          return new UnknownBlock(name);
        case "prismarine_brick_slab":
          // TODO
          return new UnknownBlock(name);
        case "dark_prismarine_slab":
          // TODO
          return new UnknownBlock(name);
        case "smooth_quartz":
          return new UnknownBlock(name);
        case "smooth_red_sandstone":
          return new SmoothRedSandstone();
        case "smooth_sandstone":
          return new SmoothSandstone();
        case "smooth_stone":
          return new UnknownBlock(name);
        case "bricks":
          return new Bricks();
        case "tnt":
          // TODO
          return new UnknownBlock(name);
        case "bookshelf":
          // TODO
          return new UnknownBlock(name);
        case "mossy_cobblestone":
          // TODO
          return new UnknownBlock(name);
        case "obsidian":
          // TODO
          return new UnknownBlock(name);
        case "torch":
          // TODO
          return new UnknownBlock(name);
        case "wall_torch":
          // TODO
          return new UnknownBlock(name);
        case "end_rod":
          // TODO
          return new UnknownBlock(name);
        case "chorus_plant":
          // TODO
          return new UnknownBlock(name);
        case "chorus_flower":
          // TODO
          return new UnknownBlock(name);
        case "purpur_block":
          // TODO
          return new UnknownBlock(name);
        case "purpur_pillar":
          // TODO
          return new UnknownBlock(name);
        case "purpur_stairs":
          // TODO
          return new UnknownBlock(name);
        case "oak_stairs": {
          Tag properties = tag.get("Properties");
          String half = properties.get("half").stringValue("bottom");
          String shape = properties.get("shape").stringValue("straight");
          String facing = properties.get("facing").stringValue("south");
          return new OakStairs(half, shape, facing);
        }
        case "spruce_stairs": {
          Tag properties = tag.get("Properties");
          String half = properties.get("half").stringValue("bottom");
          String shape = properties.get("shape").stringValue("straight");
          String facing = properties.get("facing").stringValue("south");
          return new SpruceStairs(half, shape, facing);
        }
        case "birch_stairs": {
          Tag properties = tag.get("Properties");
          String half = properties.get("half").stringValue("bottom");
          String shape = properties.get("shape").stringValue("straight");
          String facing = properties.get("facing").stringValue("south");
          return new BirchStairs(half, shape, facing);
        }
        case "jungle_stairs": {
          Tag properties = tag.get("Properties");
          String half = properties.get("half").stringValue("bottom");
          String shape = properties.get("shape").stringValue("straight");
          String facing = properties.get("facing").stringValue("south");
          return new JungleStairs(half, shape, facing);
        }
        case "acacia_stairs": {
          Tag properties = tag.get("Properties");
          String half = properties.get("half").stringValue("bottom");
          String shape = properties.get("shape").stringValue("straight");
          String facing = properties.get("facing").stringValue("south");
          return new AcaciaStairs(half, shape, facing);
        }
        case "dark_oak_stairs": {
          Tag properties = tag.get("Properties");
          String half = properties.get("half").stringValue("bottom");
          String shape = properties.get("shape").stringValue("straight");
          String facing = properties.get("facing").stringValue("south");
          return new DarkOakStairs(half, shape, facing);
        }
        case "chest":
          // TODO
          return new UnknownBlock(name);
        case "diamond_ore":
          return new DiamondOre();
        case "diamond_block":
          return new DiamondBlock();
        case "crafting_table":
          // TODO
          return new UnknownBlock(name);
        case "farmland":
          // TODO
          return new UnknownBlock(name);
        case "furnace":
          // TODO
          return new UnknownBlock(name);
        case "ladder":
          // TODO
          return new UnknownBlock(name);
        case "rail":
          // TODO
          return new UnknownBlock(name);
        case "cobblestone_stairs":
          // TODO
          return new UnknownBlock(name);
        case "lever":
          // TODO
          return new UnknownBlock(name);
        case "stone_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "oak_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "spruce_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "birch_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "jungle_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "acacia_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "redstone_ore":
          // TODO
          return new UnknownBlock(name);
        case "redstone_torch":
          // TODO
          return new UnknownBlock(name);
        case "redstone_wall_torch":
          // TODO
          return new UnknownBlock(name);
        case "stone_button":
          // TODO
          return new UnknownBlock(name);
        case "snow":
          // TODO
          return new UnknownBlock(name);
        case "ice":
          // TODO
          return new UnknownBlock(name);
        case "snow_block":
          // TODO
          return new UnknownBlock(name);
        case "cactus":
          // TODO
          return new UnknownBlock(name);
        case "clay":
          // TODO
          return new UnknownBlock(name);
        case "jukebox":
          // TODO
          return new UnknownBlock(name);
        case "oak_fence":
          // TODO
          return new UnknownBlock(name);
        case "spruce_fence":
          // TODO
          return new UnknownBlock(name);
        case "birch_fence":
          // TODO
          return new UnknownBlock(name);
        case "jungle_fence":
          // TODO
          return new UnknownBlock(name);
        case "acacia_fence":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_fence":
          // TODO
          return new UnknownBlock(name);
        case "pumpkin":
          // TODO
          return new UnknownBlock(name);
        case "carved_pumpkin":
          // TODO
          return new UnknownBlock(name);
        case "netherrack":
          // TODO
          return new UnknownBlock(name);
        case "soul_sand":
          // TODO
          return new UnknownBlock(name);
        case "glowstone":
          // TODO
          return new UnknownBlock(name);
        case "jack_o_lantern":
          // TODO
          return new UnknownBlock(name);
        case "oak_trapdoor":
          // TODO
          return new UnknownBlock(name);
        case "spruce_trapdoor":
          // TODO
          return new UnknownBlock(name);
        case "birch_trapdoor":
          // TODO
          return new UnknownBlock(name);
        case "jungle_trapdoor":
          // TODO
          return new UnknownBlock(name);
        case "acacia_trapdoor":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_trapdoor":
          // TODO
          return new UnknownBlock(name);
        case "infested_stone_bricks":
        case "stone_bricks":
          return new StoneBricks();
        case "infested_mossy_stone_bricks":
        case "mossy_stone_bricks":
          return new MossyStoneBricks();
        case "infested_cracked_stone_bricks":
        case "cracked_stone_bricks":
          return new CrackedStoneBricks();
        case "infested_chiseled_stone_bricks":
        case "chiseled_stone_bricks":
          return new ChiseledStoneBricks();
        case "nether_bricks":
          return new NetherBricks();
        case "brown_mushroom_block":
          // TODO
          return new UnknownBlock(name);
        case "red_mushroom_block":
          // TODO
          return new UnknownBlock(name);
        case "mushroom_stem":
          // TODO
          return new UnknownBlock(name);
        case "iron_bars":
          // TODO
          return new UnknownBlock(name);
        case "glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "melon":
          // TODO
          return new UnknownBlock(name);
        case "vine":
          // TODO
          return new UnknownBlock(name);
        case "oak_fence_gate":
          // TODO
          return new UnknownBlock(name);
        case "spruce_fence_gate":
          // TODO
          return new UnknownBlock(name);
        case "birch_fence_gate":
          // TODO
          return new UnknownBlock(name);
        case "jungle_fence_gate":
          // TODO
          return new UnknownBlock(name);
        case "acacia_fence_gate":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_fence_gate":
          // TODO
          return new UnknownBlock(name);
        case "brick_stairs":
          // TODO
          return new UnknownBlock(name);
        case "stone_brick_stairs":
          // TODO
          return new UnknownBlock(name);
        case "mycelium":
          // TODO
          return new UnknownBlock(name);
        case "lily_pad":
          // TODO
          return new UnknownBlock(name);
        case "nether_brick_fence":
          // TODO
          return new UnknownBlock(name);
        case "nether_brick_stairs":
          // TODO
          return new UnknownBlock(name);
        case "enchanting_table":
          // TODO
          return new UnknownBlock(name);
        case "end_portal_frame":
          // TODO
          return new UnknownBlock(name);
        case "end_stone":
          return new UnknownBlock(name);
        case "end_stone_bricks":
          return new EndStoneBricks();
        case "redstone_lamp":
          // TODO
          return new UnknownBlock(name);
        case "cocoa":
          // TODO
          return new UnknownBlock(name);
        case "sandstone_stairs":
          // TODO
          return new UnknownBlock(name);
        case "emerald_ore":
          // TODO
          return new UnknownBlock(name);
        case "ender_chest":
          // TODO
          return new UnknownBlock(name);
        case "tripwire_hook":
          // TODO
          return new UnknownBlock(name);
        case "tripwire":
          // TODO
          return new UnknownBlock(name);
        case "emerald_block":
          // TODO
          return new UnknownBlock(name);
        case "beacon":
          // TODO
          return new UnknownBlock(name);
        case "cobblestone_wall":
          // TODO
          return new UnknownBlock(name);
        case "mossy_cobblestone_wall":
          // TODO
          return new UnknownBlock(name);
        case "oak_button":
          // TODO
          return new UnknownBlock(name);
        case "spruce_button":
          // TODO
          return new UnknownBlock(name);
        case "birch_button":
          // TODO
          return new UnknownBlock(name);
        case "jungle_button":
          // TODO
          return new UnknownBlock(name);
        case "acacia_button":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_button":
          // TODO
          return new UnknownBlock(name);
        case "anvil":
          // TODO
          return new UnknownBlock(name);
        case "chipped_anvil":
          // TODO
          return new UnknownBlock(name);
        case "damaged_anvil":
          // TODO
          return new UnknownBlock(name);
        case "trapped_chest":
          // TODO
          return new UnknownBlock(name);
        case "light_weighted_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "heavy_weighted_pressure_plate":
          // TODO
          return new UnknownBlock(name);
        case "daylight_detector":
          // TODO
          return new UnknownBlock(name);
        case "redstone_block":
          // TODO
          return new UnknownBlock(name);
        case "nether_quartz_ore":
          // TODO
          return new UnknownBlock(name);
        case "hopper":
          // TODO
          return new UnknownBlock(name);
        case "chiseled_quartz_block":
          // TODO
          return new UnknownBlock(name);
        case "quartz_block":
          // TODO
          return new UnknownBlock(name);
        case "quartz_pillar":
          // TODO
          return new UnknownBlock(name);
        case "quartz_stairs":
          // TODO
          return new UnknownBlock(name);
        case "activator_rail":
          // TODO
          return new UnknownBlock(name);
        case "dropper":
          // TODO
          return new UnknownBlock(name);
        case "white_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "orange_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "magenta_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "yellow_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "lime_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "pink_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "gray_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "cyan_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "purple_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "blue_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "brown_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "green_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "red_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "black_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "iron_trapdoor":
          // TODO
          return new UnknownBlock(name);
        case "hay_block":
          // TODO
          return new UnknownBlock(name);
        case "white_carpet":
          // TODO
          return new UnknownBlock(name);
        case "orange_carpet":
          // TODO
          return new UnknownBlock(name);
        case "magenta_carpet":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_carpet":
          // TODO
          return new UnknownBlock(name);
        case "yellow_carpet":
          // TODO
          return new UnknownBlock(name);
        case "lime_carpet":
          // TODO
          return new UnknownBlock(name);
        case "pink_carpet":
          // TODO
          return new UnknownBlock(name);
        case "gray_carpet":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_carpet":
          // TODO
          return new UnknownBlock(name);
        case "cyan_carpet":
          // TODO
          return new UnknownBlock(name);
        case "purple_carpet":
          // TODO
          return new UnknownBlock(name);
        case "blue_carpet":
          // TODO
          return new UnknownBlock(name);
        case "brown_carpet":
          // TODO
          return new UnknownBlock(name);
        case "green_carpet":
          // TODO
          return new UnknownBlock(name);
        case "red_carpet":
          // TODO
          return new UnknownBlock(name);
        case "black_carpet":
          // TODO
          return new UnknownBlock(name);
        case "terracotta":
          // TODO
          return new UnknownBlock(name);
        case "coal_block":
          // TODO
          return new UnknownBlock(name);
        case "packed_ice":
          // TODO
          return new UnknownBlock(name);
        case "slime_block":
          // TODO
          return new UnknownBlock(name);
        case "grass_path":
          // TODO
          return new UnknownBlock(name);
        case "sunflower":
          // TODO
          return new UnknownBlock(name);
        case "lilac": {
          String half = tag.get("Properties").get("half").stringValue("lower");
          return new Lilac(half);
        }
        case "rose_bush": {
          String half = tag.get("Properties").get("half").stringValue("lower");
          return new RoseBush(half);
        }
        case "peony": {
          String half = tag.get("Properties").get("half").stringValue("lower");
          return new Peony(half);
        }
        case "tall_grass": {
          String half = tag.get("Properties").get("half").stringValue("lower");
          return new TallGrass(half);
        }
        case "large_fern": {
          String half = tag.get("Properties").get("half").stringValue("lower");
          return new LargeFern(half);
        }
        case "white_stained_glass":
          return new StainedGlassWhite();
        case "orange_stained_glass":
          return new StainedGlassOrange();
        case "magenta_stained_glass":
          return new StainedGlassMagenta();
        case "light_blue_stained_glass":
          return new StainedGlassLightBlue();
        case "yellow_stained_glass":
          return new StainedGlassYellow();
        case "lime_stained_glass":
          return new StainedGlassLime();
        case "pink_stained_glass":
          return new StainedGlassPink();
        case "gray_stained_glass":
          return new StainedGlassGray();
        case "light_gray_stained_glass":
          return new StainedGlassLightGray();
        case "cyan_stained_glass":
          return new StainedGlassCyan();
        case "purple_stained_glass":
          return new StainedGlassPurple();
        case "blue_stained_glass":
          return new StainedGlassBlue();
        case "brown_stained_glass":
          return new StainedGlassBrown();
        case "green_stained_glass":
          return new StainedGlassGreen();
        case "red_stained_glass":
          return new StainedGlassRed();
        case "black_stained_glass":
          return new StainedGlassBlack();
        case "white_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "orange_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "magenta_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "yellow_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "lime_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "pink_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "gray_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "cyan_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "purple_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "blue_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "brown_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "green_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "red_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "black_stained_glass_pane":
          // TODO
          return new UnknownBlock(name);
        case "prismarine":
          return new Prismarine();
        case "prismarine_bricks":
          return new PrismarineBricks();
        case "dark_prismarine":
          return new DarkPrismarine();
        case "prismarine_stairs":
          // TODO
          return new UnknownBlock(name);
        case "prismarine_brick_stairs":
          // TODO
          return new UnknownBlock(name);
        case "dark_prismarine_stairs":
          // TODO
          return new UnknownBlock(name);
        case "sea_lantern":
          // TODO
          return new UnknownBlock(name);
        case "red_sandstone":
          return new RedSandstone();
        case "chiseled_red_sandstone":
          return new ChiseledRedSandstone();
        case "cut_red_sandstone":
          return new CutRedSandstone();
        case "red_sandstone_stairs":
          // TODO
          return new UnknownBlock(name);
        case "magma_block":
          // TODO
          return new UnknownBlock(name);
        case "nether_wart_block":
          // TODO
          return new UnknownBlock(name);
        case "red_nether_bricks":
          return new RedNetherBricks();
        case "bone_block":
          // TODO
          return new UnknownBlock(name);
        case "observer":
          // TODO
          return new UnknownBlock(name);
        case "shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "white_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "orange_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "magenta_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "yellow_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "lime_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "pink_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "gray_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "cyan_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "purple_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "blue_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "brown_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "green_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "red_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "black_shulker_box":
          // TODO
          return new UnknownBlock(name);
        case "white_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "orange_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "magenta_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "yellow_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "lime_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "pink_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "gray_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "cyan_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "purple_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "blue_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "brown_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "green_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "red_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "black_glazed_terracotta":
          // TODO
          return new UnknownBlock(name);
        case "white_concrete":
          // TODO
          return new UnknownBlock(name);
        case "orange_concrete":
          // TODO
          return new UnknownBlock(name);
        case "magenta_concrete":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_concrete":
          // TODO
          return new UnknownBlock(name);
        case "yellow_concrete":
          // TODO
          return new UnknownBlock(name);
        case "lime_concrete":
          // TODO
          return new UnknownBlock(name);
        case "pink_concrete":
          // TODO
          return new UnknownBlock(name);
        case "gray_concrete":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_concrete":
          // TODO
          return new UnknownBlock(name);
        case "cyan_concrete":
          // TODO
          return new UnknownBlock(name);
        case "purple_concrete":
          // TODO
          return new UnknownBlock(name);
        case "blue_concrete":
          // TODO
          return new UnknownBlock(name);
        case "brown_concrete":
          // TODO
          return new UnknownBlock(name);
        case "green_concrete":
          // TODO
          return new UnknownBlock(name);
        case "red_concrete":
          // TODO
          return new UnknownBlock(name);
        case "black_concrete":
          // TODO
          return new UnknownBlock(name);
        case "white_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "orange_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "magenta_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "yellow_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "lime_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "pink_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "gray_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "cyan_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "purple_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "blue_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "brown_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "green_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "red_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "black_concrete_powder":
          // TODO
          return new UnknownBlock(name);
        case "turtle_egg":
          // TODO
          return new UnknownBlock(name);
        case "dead_tube_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "dead_brain_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "dead_bubble_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "dead_fire_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "dead_horn_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "tube_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "brain_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "bubble_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "fire_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "horn_coral_block":
          // TODO
          return new UnknownBlock(name);
        case "tube_coral":
          // TODO
          return new UnknownBlock(name);
        case "brain_coral":
          // TODO
          return new UnknownBlock(name);
        case "bubble_coral":
          // TODO
          return new UnknownBlock(name);
        case "fire_coral":
          // TODO
          return new UnknownBlock(name);
        case "horn_coral":
          // TODO
          return new UnknownBlock(name);
        case "dead_tube_coral":
          // TODO
          return new UnknownBlock(name);
        case "dead_brain_coral":
          // TODO
          return new UnknownBlock(name);
        case "dead_bubble_coral":
          // TODO
          return new UnknownBlock(name);
        case "dead_fire_coral":
          // TODO
          return new UnknownBlock(name);
        case "dead_horn_coral":
          // TODO
          return new UnknownBlock(name);
        case "tube_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "tube_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "brain_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "brain_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "bubble_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "bubble_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "fire_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "fire_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "horn_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "horn_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_tube_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_tube_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_brain_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_brain_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_bubble_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_bubble_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_fire_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_fire_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_horn_coral_fan":
          // TODO
          return new UnknownBlock(name);
        case "dead_horn_coral_wall_fan":
          // TODO
          return new UnknownBlock(name);
        case "blue_ice":
          // TODO
          return new UnknownBlock(name);
        case "conduit":
          // TODO
          return new UnknownBlock(name);
        case "polished_granite_stairs":
          // TODO
          return new UnknownBlock(name);
        case "smooth_red_sandstone_stairs":
          // TODO
          return new UnknownBlock(name);
        case "mossy_stone_brick_stairs":
          // TODO
          return new UnknownBlock(name);
        case "polished_diorite_stairs":
          // TODO
          return new UnknownBlock(name);
        case "mossy_cobblestone_stairs":
          // TODO
          return new UnknownBlock(name);
        case "end_stone_brick_stairs":
          // TODO
          return new UnknownBlock(name);
        case "stone_stairs":
          // TODO
          return new UnknownBlock(name);
        case "smooth_sandstone_stairs":
          // TODO
          return new UnknownBlock(name);
        case "smooth_quartz_stairs":
          // TODO
          return new UnknownBlock(name);
        case "granite_stairs":
          // TODO
          return new UnknownBlock(name);
        case "andesite_stairs":
          // TODO
          return new UnknownBlock(name);
        case "red_nether_brick_stairs":
          // TODO
          return new UnknownBlock(name);
        case "polished_andesite_stairs":
          // TODO
          return new UnknownBlock(name);
        case "diorite_stairs":
          // TODO
          return new UnknownBlock(name);
        case "polished_granite_slab":
          // TODO
          return new UnknownBlock(name);
        case "smooth_red_sandstone_slab":
          // TODO
          return new UnknownBlock(name);
        case "mossy_stone_brick_slab":
          // TODO
          return new UnknownBlock(name);
        case "polished_diorite_slab":
          // TODO
          return new UnknownBlock(name);
        case "mossy_cobblestone_slab":
          // TODO
          return new UnknownBlock(name);
        case "end_stone_brick_slab":
          // TODO
          return new UnknownBlock(name);
        case "smooth_sandstone_slab":
          // TODO
          return new UnknownBlock(name);
        case "smooth_quartz_slab":
          // TODO
          return new UnknownBlock(name);
        case "granite_slab":
          // TODO
          return new UnknownBlock(name);
        case "andesite_slab":
          // TODO
          return new UnknownBlock(name);
        case "red_nether_brick_slab":
          // TODO
          return new UnknownBlock(name);
        case "polished_andesite_slab":
          // TODO
          return new UnknownBlock(name);
        case "diorite_slab":
          // TODO
          return new UnknownBlock(name);
        case "brick_wall":
          // TODO
          return new UnknownBlock(name);
        case "prismarine_wall":
          // TODO
          return new UnknownBlock(name);
        case "red_sandstone_wall":
          // TODO
          return new UnknownBlock(name);
        case "mossy_stone_brick_wall":
          // TODO
          return new UnknownBlock(name);
        case "granite_wall":
          // TODO
          return new UnknownBlock(name);
        case "stone_brick_wall":
          // TODO
          return new UnknownBlock(name);
        case "nether_brick_wall":
          // TODO
          return new UnknownBlock(name);
        case "andesite_wall":
          // TODO
          return new UnknownBlock(name);
        case "red_nether_brick_wall":
          // TODO
          return new UnknownBlock(name);
        case "sandstone_wall":
          // TODO
          return new UnknownBlock(name);
        case "end_stone_brick_wall":
          // TODO
          return new UnknownBlock(name);
        case "diorite_wall":
          // TODO
          return new UnknownBlock(name);
        case "scaffolding":
          // TODO
          return new UnknownBlock(name);
        case "oak_door":
          // TODO
          return new UnknownBlock(name);
        case "iron_door":
          // TODO
          return new UnknownBlock(name);
        case "spruce_door":
          // TODO
          return new UnknownBlock(name);
        case "birch_door":
          // TODO
          return new UnknownBlock(name);
        case "jungle_door":
          // TODO
          return new UnknownBlock(name);
        case "acacia_door":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_door":
          // TODO
          return new UnknownBlock(name);
        case "repeater":
          // TODO
          return new UnknownBlock(name);
        case "comparator":
          // TODO
          return new UnknownBlock(name);
        case "composter":
          // TODO
          return new UnknownBlock(name);
        case "fire":
          // TODO
          return new UnknownBlock(name);
        case "wheat":
          // TODO
          return new UnknownBlock(name);
        case "sign":
          // TODO
          return new UnknownBlock(name);
        case "oak_sign":
          // TODO
          return new UnknownBlock(name);
        case "Sign":
          // TODO
          return new UnknownBlock(name);
        case "wall_sign":
          // TODO
          return new UnknownBlock(name);
        case "oak_wall_sign":
          // TODO
          return new UnknownBlock(name);
        case "spruce_sign":
          // TODO
          return new UnknownBlock(name);
        case "spruce_wall_sign":
          // TODO
          return new UnknownBlock(name);
        case "birch_sign":
          // TODO
          return new UnknownBlock(name);
        case "birch_wall_sign":
          // TODO
          return new UnknownBlock(name);
        case "jungle_sign":
          // TODO
          return new UnknownBlock(name);
        case "jungle_wall_sign":
          // TODO
          return new UnknownBlock(name);
        case "acacia_sign":
          // TODO
          return new UnknownBlock(name);
        case "acacia_wall_sign":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_sign":
          // TODO
          return new UnknownBlock(name);
        case "dark_oak_wall_sign":
          // TODO
          return new UnknownBlock(name);
        case "redstone_wire":
          return new UnknownBlock(name);
        case "sugar_cane":
          return new SugarCane();
        case "kelp":
          // TODO
          return new UnknownBlock(name);
        case "kelp_plant":
          // TODO
          return new UnknownBlock(name);
        case "dried_kelp_block":
          // TODO
          return new UnknownBlock(name);
        case "bamboo":
          // TODO
          return new UnknownBlock(name);
        case "bamboo_sapling":
          // TODO
          return new UnknownBlock(name);
        case "cake":
          // TODO
          return new UnknownBlock(name);
        case "white_bed":
          // TODO
          return new UnknownBlock(name);
        case "orange_bed":
          // TODO
          return new UnknownBlock(name);
        case "magenta_bed":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_bed":
          // TODO
          return new UnknownBlock(name);
        case "yellow_bed":
          // TODO
          return new UnknownBlock(name);
        case "lime_bed":
          // TODO
          return new UnknownBlock(name);
        case "pink_bed":
          // TODO
          return new UnknownBlock(name);
        case "gray_bed":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_bed":
          // TODO
          return new UnknownBlock(name);
        case "cyan_bed":
          // TODO
          return new UnknownBlock(name);
        case "purple_bed":
          // TODO
          return new UnknownBlock(name);
        case "blue_bed":
          // TODO
          return new UnknownBlock(name);
        case "brown_bed":
          // TODO
          return new UnknownBlock(name);
        case "green_bed":
          // TODO
          return new UnknownBlock(name);
        case "red_bed":
          // TODO
          return new UnknownBlock(name);
        case "black_bed":
          // TODO
          return new UnknownBlock(name);
        case "pumpkin_stem":
          // TODO
          return new UnknownBlock(name);
        case "attached_pumpkin_stem":
          // TODO
          return new UnknownBlock(name);
        case "melon_stem":
          // TODO
          return new UnknownBlock(name);
        case "attached_melon_stem":
          // TODO
          return new UnknownBlock(name);
        case "nether_wart":
          // TODO
          return new UnknownBlock(name);
        case "brewing_stand":
          // TODO
          return new UnknownBlock(name);
        case "cauldron":
          // TODO
          return new UnknownBlock(name);
        case "flower_pot":
          // TODO
          return new UnknownBlock(name);
        case "potted_poppy":
          // TODO
          return new UnknownBlock(name);
        case "potted_dandelion":
          // TODO
          return new UnknownBlock(name);
        case "potted_oak_sapling":
          // TODO
          return new UnknownBlock(name);
        case "potted_spruce_sapling":
          // TODO
          return new UnknownBlock(name);
        case "potted_birch_sapling":
          // TODO
          return new UnknownBlock(name);
        case "potted_jungle_sapling":
          // TODO
          return new UnknownBlock(name);
        case "potted_red_mushroom":
          // TODO
          return new UnknownBlock(name);
        case "potted_brown_mushroom":
          // TODO
          return new UnknownBlock(name);
        case "potted_cactus":
          // TODO
          return new UnknownBlock(name);
        case "potted_dead_bush":
          // TODO
          return new UnknownBlock(name);
        case "potted_fern":
          // TODO
          return new UnknownBlock(name);
        case "potted_acacia_sapling":
          // TODO
          return new UnknownBlock(name);
        case "potted_dark_oak_sapling":
          // TODO
          return new UnknownBlock(name);
        case "potted_blue_orchid":
          // TODO
          return new UnknownBlock(name);
        case "potted_allium":
          // TODO
          return new UnknownBlock(name);
        case "potted_azure_bluet":
          // TODO
          return new UnknownBlock(name);
        case "potted_red_tulip":
          // TODO
          return new UnknownBlock(name);
        case "potted_orange_tulip":
          // TODO
          return new UnknownBlock(name);
        case "potted_white_tulip":
          // TODO
          return new UnknownBlock(name);
        case "potted_pink_tulip":
          // TODO
          return new UnknownBlock(name);
        case "potted_oxeye_daisy":
          // TODO
          return new UnknownBlock(name);
        case "potted_bamboo":
          // TODO
          return new UnknownBlock(name);
        case "potted_cornflower":
          // TODO
          return new UnknownBlock(name);
        case "potted_lily_of_the_valley":
          // TODO
          return new UnknownBlock(name);
        case "potted_wither_rose":
          // TODO
          return new UnknownBlock(name);
        case "carrots":
          // TODO
          return new UnknownBlock(name);
        case "potatoes":
          // TODO
          return new UnknownBlock(name);
        case "skeleton_skull":
          // TODO
          return new UnknownBlock(name);
        case "skeleton_wall_skull":
          // TODO
          return new UnknownBlock(name);
        case "wither_skeleton_skull":
          // TODO
          return new UnknownBlock(name);
        case "wither_skeleton_wall_skull":
          // TODO
          return new UnknownBlock(name);
        case "zombie_head":
          // TODO
          return new UnknownBlock(name);
        case "zombie_wall_head":
          // TODO
          return new UnknownBlock(name);
        case "player_head":
          // TODO
          return new UnknownBlock(name);
        case "player_wall_head":
          // TODO
          return new UnknownBlock(name);
        case "creeper_head":
          // TODO
          return new UnknownBlock(name);
        case "creeper_wall_head":
          // TODO
          return new UnknownBlock(name);
        case "dragon_head":
          // TODO
          return new UnknownBlock(name);
        case "dragon_wall_head":
          // TODO
          return new UnknownBlock(name);
        case "white_banner":
          // TODO
          return new UnknownBlock(name);
        case "orange_banner":
          // TODO
          return new UnknownBlock(name);
        case "magenta_banner":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_banner":
          // TODO
          return new UnknownBlock(name);
        case "yellow_banner":
          // TODO
          return new UnknownBlock(name);
        case "lime_banner":
          // TODO
          return new UnknownBlock(name);
        case "pink_banner":
          // TODO
          return new UnknownBlock(name);
        case "gray_banner":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_banner":
          // TODO
          return new UnknownBlock(name);
        case "cyan_banner":
          // TODO
          return new UnknownBlock(name);
        case "purple_banner":
          // TODO
          return new UnknownBlock(name);
        case "blue_banner":
          // TODO
          return new UnknownBlock(name);
        case "brown_banner":
          // TODO
          return new UnknownBlock(name);
        case "green_banner":
          // TODO
          return new UnknownBlock(name);
        case "red_banner":
          // TODO
          return new UnknownBlock(name);
        case "black_banner":
          // TODO
          return new UnknownBlock(name);
        case "white_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "orange_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "magenta_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "light_blue_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "yellow_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "lime_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "pink_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "gray_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "light_gray_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "cyan_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "purple_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "blue_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "brown_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "green_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "red_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "black_wall_banner":
          // TODO
          return new UnknownBlock(name);
        case "beetroots":
          // TODO
          return new UnknownBlock(name);
        case "loom":
          // TODO
          return new UnknownBlock(name);
        case "barrel":
          // TODO
          return new UnknownBlock(name);
        case "smoker":
          // TODO
          return new UnknownBlock(name);
        case "blast_furnace":
          // TODO
          return new UnknownBlock(name);
        case "cartography_table":
          // TODO
          return new UnknownBlock(name);
        case "fleching_table":
          // TODO
          return new UnknownBlock(name);
        case "grindstone":
          // TODO
          return new UnknownBlock(name);
        case "lectern":
          // TODO
          return new UnknownBlock(name);
        case "smithing_table":
          // TODO
          return new UnknownBlock(name);
        case "stonecutter":
          // TODO
          return new UnknownBlock(name);
        case "bell":
          // TODO
          return new UnknownBlock(name);
        case "lantern":
          // TODO
          return new UnknownBlock(name);
        case "sweet_berry_bush":
          // TODO
          return new UnknownBlock(name);
        case "campfire":
          // TODO
          return new UnknownBlock(name);
        case "frosted_ice":
          // TODO
          return new UnknownBlock(name);
        case "spawner":
          // TODO
          return new UnknownBlock(name);
        case "nether_portal":
          // TODO
          return new UnknownBlock(name);
        case "end_portal":
          // TODO
          return new UnknownBlock(name);
        case "end_gateway":
          // TODO
          return new UnknownBlock(name);
        case "command_block":
          // TODO
          return new UnknownBlock(name);
        case "chain_command_block":
          // TODO
          return new UnknownBlock(name);
        case "repeating_command_block":
          // TODO
          return new UnknownBlock(name);
        case "structure_block":
          // TODO
          return new UnknownBlock(name);
        case "structure_void":
          // TODO
          return new UnknownBlock(name);
        case "jigsaw_block":
          // TODO
          return new UnknownBlock(name);
        case "barrier":
          // TODO
          return new UnknownBlock(name);
      }
    }
    return Air.INSTANCE;
  }

}
