package se.llbit.chunky.block;

import se.llbit.chunky.block.minecraft.Comparator;
import se.llbit.chunky.block.minecraft.Observer;
import se.llbit.chunky.block.minecraft.Tripwire;
import se.llbit.chunky.block.minecraft.*;
import se.llbit.chunky.entity.BannerDesign;
import se.llbit.chunky.entity.SkullEntity;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.model.minecraft.FlowerPotModel;
import se.llbit.chunky.resources.ShulkerTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.Tag;

import java.util.*;
import java.util.function.BiFunction;

public class MinecraftBlockProvider implements BlockProvider {

  private static final String[] blockList = new String[]{
    "minecraft:acacia_button",
    "minecraft:acacia_door",
    "minecraft:acacia_fence_gate",
    "minecraft:acacia_fence",
    "minecraft:acacia_leaves",
    "minecraft:acacia_log",
    "minecraft:acacia_planks",
    "minecraft:acacia_pressure_plate",
    "minecraft:acacia_sapling",
    "minecraft:acacia_sign",
    "minecraft:acacia_slab",
    "minecraft:acacia_stairs",
    "minecraft:acacia_trapdoor",
    "minecraft:acacia_wall_sign",
    "minecraft:acacia_wood",
    "minecraft:activator_rail",
    "minecraft:allium",
    "minecraft:amethyst_block",
    "minecraft:amethyst_cluster",
    "minecraft:ancient_debris",
    "minecraft:andesite",
    "minecraft:andesite_slab",
    "minecraft:andesite_stairs",
    "minecraft:andesite_wall",
    "minecraft:anvil",
    "minecraft:attached_melon_stem",
    "minecraft:attached_pumpkin_stem",
    "minecraft:azalea",
    "minecraft:azalea_leaves",
    "minecraft:flowering_azalea_leaves",
    "minecraft:azure_bluet",
    "minecraft:bamboo",
    "minecraft:bamboo_sapling",
    "minecraft:barrel",
    "minecraft:basalt",
    "minecraft:beacon",
    "minecraft:bedrock",
    "minecraft:beehive",
    "minecraft:bee_nest",
    "minecraft:beetroots",
    "minecraft:bell",
    "minecraft:big_dripleaf",
    "minecraft:big_dripleaf_stem",
    "minecraft:birch_button",
    "minecraft:birch_door",
    "minecraft:birch_fence_gate",
    "minecraft:birch_fence",
    "minecraft:birch_leaves",
    "minecraft:birch_log",
    "minecraft:birch_planks",
    "minecraft:birch_pressure_plate",
    "minecraft:birch_sapling",
    "minecraft:birch_sign",
    "minecraft:birch_slab",
    "minecraft:birch_stairs",
    "minecraft:birch_trapdoor",
    "minecraft:birch_wall_sign",
    "minecraft:birch_wood",
    "minecraft:black_banner",
    "minecraft:black_bed",
    "minecraft:black_candle_cake",
    "minecraft:black_candle",
    "minecraft:black_carpet",
    "minecraft:black_concrete",
    "minecraft:black_concrete_powder",
    "minecraft:black_glazed_terracotta",
    "minecraft:black_shulker_box",
    "minecraft:black_stained_glass",
    "minecraft:black_stained_glass_pane",
    "minecraft:blackstone",
    "minecraft:blackstone_slab",
    "minecraft:blackstone_stairs",
    "minecraft:blackstone_wall",
    "minecraft:black_terracotta",
    "minecraft:black_wall_banner",
    "minecraft:black_wool",
    "minecraft:blast_furnace",
    "minecraft:blue_banner",
    "minecraft:blue_bed",
    "minecraft:blue_candle_cake",
    "minecraft:blue_candle",
    "minecraft:blue_carpet",
    "minecraft:blue_concrete",
    "minecraft:blue_concrete_powder",
    "minecraft:blue_glazed_terracotta",
    "minecraft:blue_ice",
    "minecraft:blue_orchid",
    "minecraft:blue_shulker_box",
    "minecraft:blue_stained_glass",
    "minecraft:blue_stained_glass_pane",
    "minecraft:blue_terracotta",
    "minecraft:blue_wall_banner",
    "minecraft:blue_wool",
    "minecraft:bone_block",
    "minecraft:bookshelf",
    "minecraft:brain_coral_block",
    "minecraft:brain_coral_fan",
    "minecraft:brain_coral",
    "minecraft:brain_coral_wall_fan",
    "minecraft:brewing_stand",
    "minecraft:bricks",
    "minecraft:brick_slab",
    "minecraft:brick_stairs",
    "minecraft:brick_wall",
    "minecraft:brown_banner",
    "minecraft:brown_bed",
    "minecraft:brown_candle_cake",
    "minecraft:brown_candle",
    "minecraft:brown_carpet",
    "minecraft:brown_concrete",
    "minecraft:brown_concrete_powder",
    "minecraft:brown_glazed_terracotta",
    "minecraft:brown_mushroom_block",
    "minecraft:brown_mushroom",
    "minecraft:brown_shulker_box",
    "minecraft:brown_stained_glass",
    "minecraft:brown_stained_glass_pane",
    "minecraft:brown_terracotta",
    "minecraft:brown_wall_banner",
    "minecraft:brown_wool",
    "minecraft:bubble_column",
    "minecraft:bubble_coral_block",
    "minecraft:bubble_coral_fan",
    "minecraft:bubble_coral",
    "minecraft:bubble_coral_wall_fan",
    "minecraft:budding_amethyst",
    "minecraft:cactus",
    "minecraft:cake",
    "minecraft:calcite",
    "minecraft:campfire",
    "minecraft:candle_cake",
    "minecraft:candle",
    "minecraft:carrots",
    "minecraft:cartography_table",
    "minecraft:carved_pumpkin",
    "minecraft:cauldron",
    "minecraft:cave_vines_plant",
    "minecraft:cave_vines",
    "minecraft:chain_command_block",
    "minecraft:chain",
    "minecraft:chest",
    "minecraft:chipped_anvil",
    "minecraft:chiseled_deepslate",
    "minecraft:chiseled_nether_bricks",
    "minecraft:chiseled_polished_blackstone",
    "minecraft:chiseled_quartz_block",
    "minecraft:chiseled_red_sandstone",
    "minecraft:chiseled_sandstone",
    "minecraft:chiseled_stone_bricks",
    "minecraft:chorus_flower",
    "minecraft:chorus_plant",
    "minecraft:clay",
    "minecraft:coal_block",
    "minecraft:coal_ore",
    "minecraft:coarse_dirt",
    "minecraft:cobbled_deepslate",
    "minecraft:cobbled_deepslate_wall",
    "minecraft:cobbled_deepslate_slab",
    "minecraft:cobbled_deepslate_stairs",
    "minecraft:cobblestone",
    "minecraft:cobblestone_slab",
    "minecraft:cobblestone_stairs",
    "minecraft:cobblestone_wall",
    "minecraft:cobweb",
    "minecraft:cocoa",
    "minecraft:command_block",
    "minecraft:comparator",
    "minecraft:composter",
    "minecraft:conduit",
    "minecraft:copper_block",
    "minecraft:copper_ore",
    "minecraft:cornflower",
    "minecraft:cracked_deepslate_bricks",
    "minecraft:cracked_deepslate_tiles",
    "minecraft:cracked_nether_bricks",
    "minecraft:cracked_polished_blackstone_bricks",
    "minecraft:cracked_stone_bricks",
    "minecraft:crafting_table",
    "minecraft:creeper_head",
    "minecraft:creeper_wall_head",
    "minecraft:crimson_button",
    "minecraft:crimson_door",
    "minecraft:crimson_fence_gate",
    "minecraft:crimson_fence",
    "minecraft:crimson_fungus",
    "minecraft:crimson_hyphae",
    "minecraft:crimson_nylium",
    "minecraft:crimson_planks",
    "minecraft:crimson_pressure_plate",
    "minecraft:crimson_roots",
    "minecraft:crimson_sign",
    "minecraft:crimson_slab",
    "minecraft:crimson_stairs",
    "minecraft:crimson_stem",
    "minecraft:crimson_trapdoor",
    "minecraft:crimson_wall_sign",
    "minecraft:crying_obsidian",
    "minecraft:cut_copper",
    "minecraft:cut_copper_slab",
    "minecraft:cut_copper_stairs",
    "minecraft:cut_red_sandstone",
    "minecraft:cut_red_sandstone_slab",
    "minecraft:cut_sandstone",
    "minecraft:cut_sandstone_slab",
    "minecraft:cyan_banner",
    "minecraft:cyan_bed",
    "minecraft:cyan_candle_cake",
    "minecraft:cyan_candle",
    "minecraft:cyan_carpet",
    "minecraft:cyan_concrete",
    "minecraft:cyan_concrete_powder",
    "minecraft:cyan_glazed_terracotta",
    "minecraft:cyan_shulker_box",
    "minecraft:cyan_stained_glass",
    "minecraft:cyan_stained_glass_pane",
    "minecraft:cyan_terracotta",
    "minecraft:cyan_wall_banner",
    "minecraft:cyan_wool",
    "minecraft:damaged_anvil",
    "minecraft:dandelion",
    "minecraft:dark_oak_button",
    "minecraft:dark_oak_door",
    "minecraft:dark_oak_fence_gate",
    "minecraft:dark_oak_fence",
    "minecraft:dark_oak_leaves",
    "minecraft:dark_oak_log",
    "minecraft:dark_oak_planks",
    "minecraft:dark_oak_pressure_plate",
    "minecraft:dark_oak_sapling",
    "minecraft:dark_oak_sign",
    "minecraft:dark_oak_slab",
    "minecraft:dark_oak_stairs",
    "minecraft:dark_oak_trapdoor",
    "minecraft:dark_oak_wall_sign",
    "minecraft:dark_oak_wood",
    "minecraft:dark_prismarine",
    "minecraft:dark_prismarine_slab",
    "minecraft:dark_prismarine_stairs",
    "minecraft:daylight_detector",
    "minecraft:dead_brain_coral_block",
    "minecraft:dead_brain_coral_fan",
    "minecraft:dead_brain_coral",
    "minecraft:dead_brain_coral_wall_fan",
    "minecraft:dead_bubble_coral_block",
    "minecraft:dead_bubble_coral_fan",
    "minecraft:dead_bubble_coral",
    "minecraft:dead_bubble_coral_wall_fan",
    "minecraft:dead_bush",
    "minecraft:dead_fire_coral_block",
    "minecraft:dead_fire_coral_fan",
    "minecraft:dead_fire_coral",
    "minecraft:dead_fire_coral_wall_fan",
    "minecraft:dead_horn_coral_block",
    "minecraft:dead_horn_coral_fan",
    "minecraft:dead_horn_coral",
    "minecraft:dead_horn_coral_wall_fan",
    "minecraft:dead_tube_coral_block",
    "minecraft:dead_tube_coral_fan",
    "minecraft:dead_tube_coral",
    "minecraft:dead_tube_coral_wall_fan",
    "minecraft:deepslate",
    "minecraft:deepslate_bricks",
    "minecraft:deepslate_brick_wall",
    "minecraft:deepslate_brick_slab",
    "minecraft:deepslate_brick_stairs",
    "minecraft:deepslate_tiles",
    "minecraft:deepslate_tile_wall",
    "minecraft:deepslate_tile_slab",
    "minecraft:deepslate_tile_stairs",
    "minecraft:deepslate_gold_ore",
    "minecraft:deepslate_iron_ore",
    "minecraft:deepslate_diamond_ore",
    "minecraft:deepslate_lapis_ore",
    "minecraft:deepslate_redstone_ore",
    "minecraft:deepslate_coal_ore",
    "minecraft:deepslate_copper_ore",
    "minecraft:deepslate_emerald_ore",
    "minecraft:detector_rail",
    "minecraft:diamond_block",
    "minecraft:diamond_ore",
    "minecraft:diorite",
    "minecraft:diorite_slab",
    "minecraft:diorite_stairs",
    "minecraft:diorite_wall",
    "minecraft:dirt",
    "minecraft:dirt_path",
    "minecraft:dispenser",
    "minecraft:dragon_egg",
    "minecraft:dragon_head",
    "minecraft:dragon_wall_head",
    "minecraft:dried_kelp_block",
    "minecraft:dripstone_block",
    "minecraft:dropper",
    "minecraft:emerald_block",
    "minecraft:emerald_ore",
    "minecraft:enchanting_table",
    "minecraft:ender_chest",
    "minecraft:end_gateway",
    "minecraft:end_portal_frame",
    "minecraft:end_portal",
    "minecraft:end_rod",
    "minecraft:end_stone_bricks",
    "minecraft:end_stone_brick_slab",
    "minecraft:end_stone_brick_stairs",
    "minecraft:end_stone_brick_wall",
    "minecraft:end_stone",
    "minecraft:farmland",
    "minecraft:fern",
    "minecraft:fire_coral_block",
    "minecraft:fire_coral_fan",
    "minecraft:fire_coral",
    "minecraft:fire_coral_wall_fan",
    "minecraft:fire",
    "minecraft:fletching_table",
    "minecraft:flowering_azalea",
    "minecraft:flower_pot",
    "minecraft:frosted_ice",
    "minecraft:furnace",
    "minecraft:gilded_blackstone",
    "minecraft:glass",
    "minecraft:glass_pane",
    "minecraft:glowstone",
    "minecraft:glow_lichen",
    "minecraft:gold_block",
    "minecraft:gold_ore",
    "minecraft:granite",
    "minecraft:granite_slab",
    "minecraft:granite_stairs",
    "minecraft:granite_wall",
    "minecraft:grass_block",
    "minecraft:grass", // pre 1.20.3-pre2
    "minecraft:short_grass", // since 1.20.3-pre2
    "minecraft:grass_path", // pre 20w45a
    "minecraft:gravel",
    "minecraft:gray_banner",
    "minecraft:gray_bed",
    "minecraft:gray_candle_cake",
    "minecraft:gray_candle",
    "minecraft:gray_carpet",
    "minecraft:gray_concrete",
    "minecraft:gray_concrete_powder",
    "minecraft:gray_glazed_terracotta",
    "minecraft:gray_shulker_box",
    "minecraft:gray_stained_glass",
    "minecraft:gray_stained_glass_pane",
    "minecraft:gray_terracotta",
    "minecraft:gray_wall_banner",
    "minecraft:gray_wool",
    "minecraft:green_banner",
    "minecraft:green_bed",
    "minecraft:green_candle_cake",
    "minecraft:green_candle",
    "minecraft:green_carpet",
    "minecraft:green_concrete",
    "minecraft:green_concrete_powder",
    "minecraft:green_glazed_terracotta",
    "minecraft:green_shulker_box",
    "minecraft:green_stained_glass",
    "minecraft:green_stained_glass_pane",
    "minecraft:green_terracotta",
    "minecraft:green_wall_banner",
    "minecraft:green_wool",
    "minecraft:grindstone",
    "minecraft:hanging_roots",
    "minecraft:hay_block",
    "minecraft:heavy_weighted_pressure_plate",
    "minecraft:honey_block",
    "minecraft:honeycomb_block",
    "minecraft:hopper",
    "minecraft:horn_coral_block",
    "minecraft:horn_coral_fan",
    "minecraft:horn_coral",
    "minecraft:horn_coral_wall_fan",
    "minecraft:ice",
    "minecraft:infested_chiseled_stone_bricks",
    "minecraft:infested_cobblestone",
    "minecraft:infested_cracked_stone_bricks",
    "minecraft:infested_deepslate",
    "minecraft:infested_mossy_stone_bricks",
    "minecraft:infested_stone_bricks",
    "minecraft:iron_bars",
    "minecraft:iron_block",
    "minecraft:iron_door",
    "minecraft:iron_ore",
    "minecraft:iron_trapdoor",
    "minecraft:item_frame",
    "minecraft:jack_o_lantern",
    "minecraft:jigsaw",
    "minecraft:jukebox",
    "minecraft:jungle_button",
    "minecraft:jungle_door",
    "minecraft:jungle_fence_gate",
    "minecraft:jungle_fence",
    "minecraft:jungle_leaves",
    "minecraft:jungle_log",
    "minecraft:jungle_planks",
    "minecraft:jungle_pressure_plate",
    "minecraft:jungle_sapling",
    "minecraft:jungle_sign",
    "minecraft:jungle_slab",
    "minecraft:jungle_stairs",
    "minecraft:jungle_trapdoor",
    "minecraft:jungle_wall_sign",
    "minecraft:jungle_wood",
    "minecraft:kelp",
    "minecraft:kelp_plant",
    "minecraft:ladder",
    "minecraft:lantern",
    "minecraft:lapis_block",
    "minecraft:lapis_ore",
    "minecraft:large_amethyst_bud",
    "minecraft:large_fern",
    "minecraft:lava_cauldron",
    "minecraft:lava",
    "minecraft:lectern",
    "minecraft:lever",
    "minecraft:light",
    "minecraft:light_blue_banner",
    "minecraft:light_blue_bed",
    "minecraft:light_blue_candle_cake",
    "minecraft:light_blue_candle",
    "minecraft:light_blue_carpet",
    "minecraft:light_blue_concrete",
    "minecraft:light_blue_concrete_powder",
    "minecraft:light_blue_glazed_terracotta",
    "minecraft:light_blue_shulker_box",
    "minecraft:light_blue_stained_glass",
    "minecraft:light_blue_stained_glass_pane",
    "minecraft:light_blue_terracotta",
    "minecraft:light_blue_wall_banner",
    "minecraft:light_blue_wool",
    "minecraft:light_gray_banner",
    "minecraft:light_gray_bed",
    "minecraft:light_gray_candle_cake",
    "minecraft:light_gray_candle",
    "minecraft:light_gray_carpet",
    "minecraft:light_gray_concrete",
    "minecraft:light_gray_concrete_powder",
    "minecraft:light_gray_glazed_terracotta",
    "minecraft:light_gray_shulker_box",
    "minecraft:light_gray_stained_glass",
    "minecraft:light_gray_stained_glass_pane",
    "minecraft:light_gray_terracotta",
    "minecraft:light_gray_wall_banner",
    "minecraft:light_gray_wool",
    "minecraft:exposed_copper",
    "minecraft:exposed_cut_copper",
    "minecraft:exposed_cut_copper_slab",
    "minecraft:exposed_cut_copper_stairs",
    "minecraft:lightning_rod",
    "minecraft:light_weighted_pressure_plate",
    "minecraft:lilac",
    "minecraft:lily_of_the_valley",
    "minecraft:lily_pad",
    "minecraft:lime_banner",
    "minecraft:lime_bed",
    "minecraft:lime_candle_cake",
    "minecraft:lime_candle",
    "minecraft:lime_carpet",
    "minecraft:lime_concrete",
    "minecraft:lime_concrete_powder",
    "minecraft:lime_glazed_terracotta",
    "minecraft:lime_shulker_box",
    "minecraft:lime_stained_glass",
    "minecraft:lime_stained_glass_pane",
    "minecraft:lime_terracotta",
    "minecraft:lime_wall_banner",
    "minecraft:lime_wool",
    "minecraft:lodestone",
    "minecraft:loom",
    "minecraft:magenta_banner",
    "minecraft:magenta_bed",
    "minecraft:magenta_candle_cake",
    "minecraft:magenta_candle",
    "minecraft:magenta_carpet",
    "minecraft:magenta_concrete",
    "minecraft:magenta_concrete_powder",
    "minecraft:magenta_glazed_terracotta",
    "minecraft:magenta_shulker_box",
    "minecraft:magenta_stained_glass",
    "minecraft:magenta_stained_glass_pane",
    "minecraft:magenta_terracotta",
    "minecraft:magenta_wall_banner",
    "minecraft:magenta_wool",
    "minecraft:magma_block",
    "minecraft:medium_amethyst_bud",
    "minecraft:melon",
    "minecraft:melon_stem",
    "minecraft:mossy_cobblestone",
    "minecraft:mossy_cobblestone_slab",
    "minecraft:mossy_cobblestone_stairs",
    "minecraft:mossy_cobblestone_wall",
    "minecraft:mossy_stone_bricks",
    "minecraft:mossy_stone_brick_slab",
    "minecraft:mossy_stone_brick_stairs",
    "minecraft:mossy_stone_brick_wall",
    "minecraft:moss_block",
    "minecraft:moss_carpet",
    "minecraft:moving_piston",
    "minecraft:mushroom_stem",
    "minecraft:mycelium",
    "minecraft:nether_brick_fence",
    "minecraft:nether_bricks",
    "minecraft:nether_brick_slab",
    "minecraft:nether_brick_stairs",
    "minecraft:nether_brick_wall",
    "minecraft:nether_gold_ore",
    "minecraft:netherite_block",
    "minecraft:nether_portal",
    "minecraft:nether_quartz_ore",
    "minecraft:netherrack",
    "minecraft:nether_sprouts",
    "minecraft:nether_wart_block",
    "minecraft:nether_wart",
    "minecraft:note_block",
    "minecraft:oak_button",
    "minecraft:oak_door",
    "minecraft:oak_fence_gate",
    "minecraft:oak_fence",
    "minecraft:oak_leaves",
    "minecraft:oak_log",
    "minecraft:oak_planks",
    "minecraft:oak_pressure_plate",
    "minecraft:oak_sapling",
    "minecraft:oak_sign",
    "minecraft:oak_slab",
    "minecraft:oak_stairs",
    "minecraft:oak_trapdoor",
    "minecraft:oak_wall_sign",
    "minecraft:oak_wood",
    "minecraft:observer",
    "minecraft:obsidian",
    "minecraft:orange_banner",
    "minecraft:orange_bed",
    "minecraft:orange_candle_cake",
    "minecraft:orange_candle",
    "minecraft:orange_carpet",
    "minecraft:orange_concrete",
    "minecraft:orange_concrete_powder",
    "minecraft:orange_glazed_terracotta",
    "minecraft:orange_shulker_box",
    "minecraft:orange_stained_glass",
    "minecraft:orange_stained_glass_pane",
    "minecraft:orange_terracotta",
    "minecraft:orange_tulip",
    "minecraft:orange_wall_banner",
    "minecraft:orange_wool",
    "minecraft:oxeye_daisy",
    "minecraft:packed_ice",
    "minecraft:peony",
    "minecraft:petrified_oak_slab",
    "minecraft:pink_banner",
    "minecraft:pink_bed",
    "minecraft:pink_candle_cake",
    "minecraft:pink_candle",
    "minecraft:pink_carpet",
    "minecraft:pink_concrete",
    "minecraft:pink_concrete_powder",
    "minecraft:pink_glazed_terracotta",
    "minecraft:pink_shulker_box",
    "minecraft:pink_stained_glass",
    "minecraft:pink_stained_glass_pane",
    "minecraft:pink_terracotta",
    "minecraft:pink_tulip",
    "minecraft:pink_wall_banner",
    "minecraft:pink_wool",
    "minecraft:piston_head",
    "minecraft:piston",
    "minecraft:player_head",
    "minecraft:player_wall_head",
    "minecraft:podzol",
    "minecraft:pointed_dripstone",
    "minecraft:polished_andesite",
    "minecraft:polished_andesite_slab",
    "minecraft:polished_andesite_stairs",
    "minecraft:polished_basalt",
    "minecraft:polished_blackstone_bricks",
    "minecraft:polished_blackstone_brick_slab",
    "minecraft:polished_blackstone_brick_stairs",
    "minecraft:polished_blackstone_brick_wall",
    "minecraft:polished_blackstone_button",
    "minecraft:polished_blackstone",
    "minecraft:polished_blackstone_pressure_plate",
    "minecraft:polished_blackstone_slab",
    "minecraft:polished_blackstone_stairs",
    "minecraft:polished_blackstone_wall",
    "minecraft:polished_diorite",
    "minecraft:polished_diorite_slab",
    "minecraft:polished_diorite_stairs",
    "minecraft:polished_granite",
    "minecraft:polished_granite_slab",
    "minecraft:polished_granite_stairs",
    "minecraft:polished_deepslate",
    "minecraft:polished_deepslate_wall",
    "minecraft:polished_deepslate_slab",
    "minecraft:polished_deepslate_stairs",
    "minecraft:poppy",
    "minecraft:potatoes",
    "minecraft:potted_acacia_sapling",
    "minecraft:potted_allium",
    "minecraft:potted_azure_bluet",
    "minecraft:potted_bamboo",
    "minecraft:potted_birch_sapling",
    "minecraft:potted_blue_orchid",
    "minecraft:potted_brown_mushroom",
    "minecraft:potted_cactus",
    "minecraft:potted_cornflower",
    "minecraft:potted_crimson_fungus",
    "minecraft:potted_crimson_roots",
    "minecraft:potted_dandelion",
    "minecraft:potted_dark_oak_sapling",
    "minecraft:potted_dead_bush",
    "minecraft:potted_fern",
    "minecraft:potted_jungle_sapling",
    "minecraft:potted_lily_of_the_valley",
    "minecraft:potted_oak_sapling",
    "minecraft:potted_orange_tulip",
    "minecraft:potted_oxeye_daisy",
    "minecraft:potted_pink_tulip",
    "minecraft:potted_poppy",
    "minecraft:potted_red_mushroom",
    "minecraft:potted_red_tulip",
    "minecraft:potted_spruce_sapling",
    "minecraft:potted_warped_fungus",
    "minecraft:potted_warped_roots",
    "minecraft:potted_white_tulip",
    "minecraft:potted_wither_rose",
    "minecraft:potted_warped_fungus",
    "minecraft:potted_crimson_fungus",
    "minecraft:potted_crimson_roots",
    "minecraft:potted_warped_roots",
    "minecraft:potted_azalea_bush",
    "minecraft:potted_flowering_azalea_bush",
    "minecraft:powder_snow",
    "minecraft:powder_snow_cauldron",
    "minecraft:powered_rail",
    "minecraft:prismarine_bricks",
    "minecraft:prismarine_brick_slab",
    "minecraft:prismarine_brick_stairs",
    "minecraft:prismarine",
    "minecraft:prismarine_slab",
    "minecraft:prismarine_stairs",
    "minecraft:prismarine_wall",
    "minecraft:pumpkin",
    "minecraft:pumpkin_stem",
    "minecraft:purple_banner",
    "minecraft:purple_bed",
    "minecraft:purple_candle_cake",
    "minecraft:purple_candle",
    "minecraft:purple_carpet",
    "minecraft:purple_concrete",
    "minecraft:purple_concrete_powder",
    "minecraft:purple_glazed_terracotta",
    "minecraft:purple_shulker_box",
    "minecraft:purple_stained_glass",
    "minecraft:purple_stained_glass_pane",
    "minecraft:purple_terracotta",
    "minecraft:purple_wall_banner",
    "minecraft:purple_wool",
    "minecraft:purpur_block",
    "minecraft:purpur_pillar",
    "minecraft:purpur_slab",
    "minecraft:purpur_stairs",
    "minecraft:quartz_block",
    "minecraft:quartz_bricks",
    "minecraft:quartz_pillar",
    "minecraft:quartz_slab",
    "minecraft:quartz_stairs",
    "minecraft:rail",
    "minecraft:raw_copper_block",
    "minecraft:raw_gold_block",
    "minecraft:raw_iron_block",
    "minecraft:red_banner",
    "minecraft:red_bed",
    "minecraft:red_candle_cake",
    "minecraft:red_candle",
    "minecraft:red_carpet",
    "minecraft:red_concrete",
    "minecraft:red_concrete_powder",
    "minecraft:red_glazed_terracotta",
    "minecraft:red_mushroom_block",
    "minecraft:red_mushroom",
    "minecraft:red_nether_bricks",
    "minecraft:red_nether_brick_slab",
    "minecraft:red_nether_brick_stairs",
    "minecraft:red_nether_brick_wall",
    "minecraft:red_sand",
    "minecraft:red_sandstone",
    "minecraft:red_sandstone_slab",
    "minecraft:red_sandstone_stairs",
    "minecraft:red_sandstone_wall",
    "minecraft:red_shulker_box",
    "minecraft:red_stained_glass",
    "minecraft:red_stained_glass_pane",
    "minecraft:redstone_block",
    "minecraft:redstone_lamp",
    "minecraft:redstone_ore",
    "minecraft:redstone_torch",
    "minecraft:redstone_wall_torch",
    "minecraft:redstone_wire",
    "minecraft:red_terracotta",
    "minecraft:red_tulip",
    "minecraft:red_wall_banner",
    "minecraft:red_wool",
    "minecraft:repeater",
    "minecraft:repeating_command_block",
    "minecraft:respawn_anchor",
    "minecraft:rooted_dirt",
    "minecraft:rose_bush",
    "minecraft:sand",
    "minecraft:sandstone",
    "minecraft:sandstone_slab",
    "minecraft:sandstone_stairs",
    "minecraft:sandstone_wall",
    "minecraft:scaffolding",
    "minecraft:sculk_sensor",
    "minecraft:seagrass",
    "minecraft:sea_lantern",
    "minecraft:sea_pickle",
    "minecraft:weathered_copper",
    "minecraft:weathered_cut_copper",
    "minecraft:weathered_cut_copper_slab",
    "minecraft:weathered_cut_copper_stairs",
    "minecraft:shroomlight",
    "minecraft:shulker_box",
    "minecraft:skeleton_skull",
    "minecraft:skeleton_wall_skull",
    "minecraft:slime_block",
    "minecraft:small_amethyst_bud",
    "minecraft:small_dripleaf",
    "minecraft:smithing_table",
    "minecraft:smoker",
    "minecraft:smooth_basalt",
    "minecraft:smooth_quartz",
    "minecraft:smooth_quartz_slab",
    "minecraft:smooth_quartz_stairs",
    "minecraft:smooth_red_sandstone",
    "minecraft:smooth_red_sandstone_slab",
    "minecraft:smooth_red_sandstone_stairs",
    "minecraft:smooth_sandstone",
    "minecraft:smooth_sandstone_slab",
    "minecraft:smooth_sandstone_stairs",
    "minecraft:smooth_stone",
    "minecraft:smooth_stone_slab",
    "minecraft:snow_block",
    "minecraft:snow",
    "minecraft:soul_campfire",
    "minecraft:soul_fire",
    "minecraft:soul_lantern",
    "minecraft:soul_sand",
    "minecraft:soul_soil",
    "minecraft:soul_torch",
    "minecraft:soul_wall_torch",
    "minecraft:spawner",
    "minecraft:sponge",
    "minecraft:spore_blossom",
    "minecraft:spruce_button",
    "minecraft:spruce_door",
    "minecraft:spruce_fence_gate",
    "minecraft:spruce_fence",
    "minecraft:spruce_leaves",
    "minecraft:spruce_log",
    "minecraft:spruce_planks",
    "minecraft:spruce_pressure_plate",
    "minecraft:spruce_sapling",
    "minecraft:spruce_sign",
    "minecraft:spruce_slab",
    "minecraft:spruce_stairs",
    "minecraft:spruce_trapdoor",
    "minecraft:spruce_wall_sign",
    "minecraft:spruce_wood",
    "minecraft:sticky_piston",
    "minecraft:stone_bricks",
    "minecraft:stone_brick_slab",
    "minecraft:stone_brick_stairs",
    "minecraft:stone_brick_wall",
    "minecraft:stone_button",
    "minecraft:stonecutter",
    "minecraft:stone_pressure_plate",
    "minecraft:stone_slab",
    "minecraft:stone_stairs",
    "minecraft:stripped_acacia_log",
    "minecraft:stripped_acacia_wood",
    "minecraft:stripped_birch_log",
    "minecraft:stripped_birch_wood",
    "minecraft:stripped_crimson_hyphae",
    "minecraft:stripped_crimson_stem",
    "minecraft:stripped_dark_oak_log",
    "minecraft:stripped_dark_oak_wood",
    "minecraft:stripped_jungle_log",
    "minecraft:stripped_jungle_wood",
    "minecraft:stripped_oak_log",
    "minecraft:stripped_oak_wood",
    "minecraft:stripped_spruce_log",
    "minecraft:stripped_spruce_wood",
    "minecraft:stripped_warped_hyphae",
    "minecraft:stripped_warped_stem",
    "minecraft:structure_block",
    "minecraft:sugar_cane",
    "minecraft:sunflower",
    "minecraft:sweet_berry_bush",
    "minecraft:tall_grass",
    "minecraft:tall_seagrass",
    "minecraft:target",
    "minecraft:terracotta",
    "minecraft:tinted_glass",
    "minecraft:tnt",
    "minecraft:torch",
    "minecraft:trapped_chest",
    "minecraft:tripwire_hook",
    "minecraft:tripwire",
    "minecraft:tube_coral_block",
    "minecraft:tube_coral_fan",
    "minecraft:tube_coral",
    "minecraft:tube_coral_wall_fan",
    "minecraft:tuff",
    "minecraft:turtle_egg",
    "minecraft:twisting_vines",
    "minecraft:twisting_vines_plant",
    "minecraft:vine",
    "minecraft:wall_torch",
    "minecraft:warped_button",
    "minecraft:warped_door",
    "minecraft:warped_fence_gate",
    "minecraft:warped_fence",
    "minecraft:warped_fungus",
    "minecraft:warped_hyphae",
    "minecraft:warped_nylium",
    "minecraft:warped_planks",
    "minecraft:warped_pressure_plate",
    "minecraft:warped_roots",
    "minecraft:warped_sign",
    "minecraft:warped_slab",
    "minecraft:warped_stairs",
    "minecraft:warped_stem",
    "minecraft:warped_trapdoor",
    "minecraft:warped_wall_sign",
    "minecraft:warped_wart_block",
    "minecraft:water_cauldron",
    "minecraft:water",
    "minecraft:waxed_copper_block",
    "minecraft:waxed_cut_copper",
    "minecraft:waxed_cut_copper_slab",
    "minecraft:waxed_cut_copper_stairs",
    "minecraft:waxed_exposed_copper",
    "minecraft:waxed_exposed_cut_copper",
    "minecraft:waxed_exposed_cut_copper_slab",
    "minecraft:waxed_exposed_cut_copper_stairs",
    "minecraft:waxed_weathered_copper",
    "minecraft:waxed_weathered_cut_copper",
    "minecraft:waxed_weathered_cut_copper_slab",
    "minecraft:waxed_weathered_cut_copper_stairs",
    "minecraft:oxidized_copper",
    "minecraft:oxidized_cut_copper",
    "minecraft:oxidized_cut_copper_slab",
    "minecraft:oxidized_cut_copper_stairs",
    "minecraft:waxed_oxidized_copper",
    "minecraft:waxed_oxidized_cut_copper",
    "minecraft:waxed_oxidized_cut_copper_slab",
    "minecraft:waxed_oxidized_cut_copper_stairs",
    "minecraft:weeping_vines",
    "minecraft:weeping_vines_plant",
    "minecraft:wet_sponge",
    "minecraft:wheat",
    "minecraft:white_banner",
    "minecraft:white_bed",
    "minecraft:white_candle_cake",
    "minecraft:white_candle",
    "minecraft:white_carpet",
    "minecraft:white_concrete",
    "minecraft:white_concrete_powder",
    "minecraft:white_glazed_terracotta",
    "minecraft:white_shulker_box",
    "minecraft:white_stained_glass",
    "minecraft:white_stained_glass_pane",
    "minecraft:white_terracotta",
    "minecraft:white_tulip",
    "minecraft:white_wall_banner",
    "minecraft:white_wool",
    "minecraft:wither_rose",
    "minecraft:wither_skeleton_skull",
    "minecraft:wither_skeleton_wall_skull",
    "minecraft:yellow_banner",
    "minecraft:yellow_bed",
    "minecraft:yellow_candle_cake",
    "minecraft:yellow_candle",
    "minecraft:yellow_carpet",
    "minecraft:yellow_concrete",
    "minecraft:yellow_concrete_powder",
    "minecraft:yellow_glazed_terracotta",
    "minecraft:yellow_shulker_box",
    "minecraft:yellow_stained_glass",
    "minecraft:yellow_stained_glass_pane",
    "minecraft:yellow_terracotta",
    "minecraft:yellow_wall_banner",
    "minecraft:yellow_wool",
    "minecraft:zombie_head",
    "minecraft:zombie_wall_head",
  }; // Stop! Do not add more blocks here, use the static initializer and the addBlock(s) methods instead.

  private static final Map<String, BiFunction<String, Tag, Block>> blocks = new HashMap<>();

  private static void addBlock(String name, BiFunction<String, Tag, Block> factory) {
    blocks.put("minecraft:" + name, factory);
  }

  private static void addBlocks(BiFunction<String, Tag, Block> factory, String... names) {
    for (String name : names) {
      addBlock(name, factory);
    }
  }

  private static void addBlock(String name, Texture texture) {
    addBlock(name, (n, tag) -> new MinecraftBlock(n, texture));
  }

  private static void addBlocks(Texture texture, String... names) {
    addBlocks((name, tag) -> new MinecraftBlock(name, texture), names);
  }

  static {
    addBlocks((name, tag) -> Air.INSTANCE, "air", "cave_air", "void_air", "structure_void");
    addBlock("barrier", (name, tag) -> tag.get("Properties").get("waterlogged").stringValue("").equals("true") ? Water.INSTANCE : Air.INSTANCE);
    addBlocks(Texture.stone, "infested_stone", "stone");

    // 1.19
    addBlock("mud", Texture.mud);
    addBlock("mud_bricks", Texture.mudBricks);
    addBlock("mud_brick_slab", (name, tag) -> slab(tag, Texture.mudBricks));
    addBlock("mud_brick_stairs", (name, tag) -> stairs(tag, Texture.mudBricks));
    addBlock("mud_brick_wall", (name, tag) -> wall(tag, Texture.mudBricks));
    addBlock("packed_mud", Texture.packedMud);
    addBlock("muddy_mangrove_roots", (name, tag) -> log(tag, Texture.muddyMangroveRootsSide, Texture.muddyMangroveRootsTop));
    addBlock("reinforced_deepslate", (name, tag) -> new TexturedBlock(name, Texture.reinforcedDeepslateSide, Texture.reinforcedDeepslateTop, Texture.reinforcedDeepslateBottom));
    addBlock("frogspawn", (name, tag) -> new Frogspawn());
    addBlock("ochre_froglight", (name, tag) -> nonSolid(log(tag, Texture.ochreFroglightSide, Texture.ochreFroglightTop)));
    addBlock("verdant_froglight", (name, tag) -> nonSolid(log(tag, Texture.verdantFroglightSide, Texture.verdantFroglightTop)));
    addBlock("pearlescent_froglight", (name, tag) -> nonSolid(log(tag, Texture.pearlescentFroglightSide, Texture.pearlescentFroglightTop)));
    addBlock("mangrove_button", (name, tag) -> button(tag, Texture.mangrovePlanks));
    addBlock("mangrove_door", (name, tag) -> door(tag, Texture.mangroveDoorTop, Texture.mangroveDoorBottom));
    addBlock("mangrove_fence", (name, tag) -> fence(tag, Texture.mangrovePlanks));
    addBlock("mangrove_fence_gate", (name, tag) -> fenceGate(tag, Texture.mangrovePlanks));
    addBlock("mangrove_leaves", (name, tag) -> new Leaves(name, Texture.mangroveLeaves));
    addBlock("mangrove_log", (name, tag) -> log(tag, Texture.mangroveLog, Texture.mangroveLogTop));
    addBlock("stripped_mangrove_log", (name, tag) -> log(tag, Texture.strippedMangroveLog, Texture.strippedMangroveLogTop));
    addBlock("mangrove_planks", Texture.mangrovePlanks);
    addBlock("mangrove_pressure_plate", (name, tag) -> new PressurePlate(name, Texture.mangrovePlanks));
    addBlock("mangrove_sign", (name, tag) -> sign(tag, "mangrove"));
    addBlock("mangrove_wall_sign", (name, tag) -> wallSign(tag, "mangrove"));
    addBlock("mangrove_slab", (name, tag) -> slab(tag, Texture.mangrovePlanks));
    addBlock("mangrove_stairs", (name, tag) -> stairs(tag, Texture.mangrovePlanks));
    addBlock("mangrove_trapdoor", (name, tag) -> orientableTrapdoor(tag, Texture.mangroveTrapdoor));
    addBlock("mangrove_wood", (name, tag) -> log(tag, Texture.mangroveLog, Texture.mangroveLog));
    addBlock("stripped_mangrove_wood", (name, tag) -> log(tag, Texture.strippedMangroveLog, Texture.strippedMangroveLog));
    addBlock("mangrove_roots", (name, tag) -> new MangroveRoots());
    addBlock("mangrove_propagule", (name, tag) -> new MangrovePropagule(
      BlockProvider.stringToInt(tag.get("Properties").get("age"), 0),
      tag.get("Properties").get("hanging").stringValue("false").equals("true")));
    addBlock("potted_mangrove_propagule", (name, tag) -> new FlowerPot(name, FlowerPotModel.Kind.MANGROVE_PROPAGULE));
    addBlock("sculk_catalyst", (name, tag) -> new SculkCatalyst(tag.get("Properties").get("bloom").stringValue("false").equals("true")));
    addBlock("sculk", Texture.sculk);
    addBlock("sculk_shrieker", (name, tag) -> new SculkShrieker(tag.get("Properties").get("can_summon").stringValue("false").equals("true")));
    addBlock("sculk_vein", (name, tag) -> new SculkVein(
      tag.get("Properties").get("north").stringValue("false").equals("true"),
      tag.get("Properties").get("south").stringValue("false").equals("true"),
      tag.get("Properties").get("east").stringValue("false").equals("true"),
      tag.get("Properties").get("west").stringValue("false").equals("true"),
      tag.get("Properties").get("up").stringValue("false").equals("true"),
      tag.get("Properties").get("down").stringValue("false").equals("true")));

    //1.20
    addBlock("bamboo_button", (name, tag) -> button(tag, Texture.bambooPlanks));
    addBlock("bamboo_door", (name, tag) -> door(tag, Texture.bambooDoorTop, Texture.bambooDoorBottom));
    addBlock("bamboo_fence", (name, tag) -> fence(tag, Texture.bambooPlanks));
    addBlock("bamboo_fence_gate", (name, tag) -> fenceGate(tag, Texture.bambooPlanks));
    addBlock("bamboo_block", (name, tag) -> log(tag, Texture.bambooBlock, Texture.bambooBlockTop));
    addBlock("stripped_bamboo_block", (name, tag) -> log(tag, Texture.strippedBambooBlock, Texture.strippedBambooBlockTop));
    addBlock("bamboo_planks", Texture.bambooPlanks);
    addBlock("bamboo_mosaic", Texture.bambooMosaic);
    addBlock("bamboo_pressure_plate", (name, tag) -> new PressurePlate(name, Texture.bambooPlanks));
    addBlock("bamboo_sign", (name, tag) -> sign(tag, "bamboo"));
    addBlock("bamboo_wall_sign", (name, tag) -> wallSign(tag, "bamboo"));
    addBlock("bamboo_slab", (name, tag) -> slab(tag, Texture.bambooPlanks));
    addBlock("bamboo_stairs", (name, tag) -> stairs(tag, Texture.bambooPlanks));
    addBlock("bamboo_mosaic_slab", (name, tag) -> slab(tag, Texture.bambooMosaic));
    addBlock("bamboo_mosaic_stairs", (name, tag) -> stairs(tag, Texture.bambooMosaic));
    addBlock("bamboo_trapdoor", (name, tag) -> orientableTrapdoor(tag, Texture.bambooTrapdoor));
    addBlock("cherry_button", (name, tag) -> button(tag, Texture.cherryPlanks));
    addBlock("cherry_door", (name, tag) -> door(tag, Texture.cherryDoorTop, Texture.cherryDoorBottom));
    addBlock("cherry_fence", (name, tag) -> fence(tag, Texture.cherryPlanks));
    addBlock("cherry_fence_gate", (name, tag) -> fenceGate(tag, Texture.cherryPlanks));
    addBlock("cherry_leaves", (name, tag) -> new UntintedLeaves(name, Texture.cherryLeaves));
    addBlock("cherry_log", (name, tag) -> log(tag, Texture.cherryLog, Texture.cherryLogTop));
    addBlock("stripped_cherry_log", (name, tag) -> log(tag, Texture.strippedCherryLog, Texture.strippedCherryLogTop));
    addBlock("cherry_planks", Texture.cherryPlanks);
    addBlock("cherry_pressure_plate", (name, tag) -> new PressurePlate(name, Texture.cherryPlanks));
    addBlock("cherry_sign", (name, tag) -> sign(tag, "cherry"));
    addBlock("cherry_wall_sign", (name, tag) -> wallSign(tag, "cherry"));
    addBlock("cherry_slab", (name, tag) -> slab(tag, Texture.cherryPlanks));
    addBlock("cherry_stairs", (name, tag) -> stairs(tag, Texture.cherryPlanks));
    addBlock("cherry_trapdoor", (name, tag) -> orientableTrapdoor(tag, Texture.cherryTrapdoor));
    addBlock("cherry_wood", (name, tag) -> log(tag, Texture.cherryLog, Texture.cherryLog));
    addBlock("stripped_cherry_wood", (name, tag) -> log(tag, Texture.strippedCherryLog, Texture.strippedCherryLog));
    addBlock("cherry_sapling", (name, tag) -> new SpriteBlock(name, Texture.cherrySapling));
    addBlock("potted_cherry_sapling", (name, tag) -> new FlowerPot(name, FlowerPotModel.Kind.CHERRY_SAPLING));
    addBlock("torchflower", (name, tag) -> new SpriteBlock(name, Texture.torchflower));
    addBlock("torchflower_crop", (name, tag) -> new TorchflowerCrop(BlockProvider.stringToInt(tag.get("Properties").get("age"), 2)));
    addBlock("potted_torchflower", (name, tag) -> new FlowerPot(name, FlowerPotModel.Kind.TORCHFLOWER));
    addBlock("suspicious_sand", (name, tag) -> suspiciousSand(tag));
    addBlock("suspicious_gravel", (name, tag) -> suspiciousGravel(tag));
    addBlock("chiseled_bookshelf", (name, tag) -> new ChiseledBookshelf(
      BlockProvider.facing(tag),
      tag.get("Properties").get("slot_0_occupied").stringValue("false").equals("true"),
      tag.get("Properties").get("slot_1_occupied").stringValue("false").equals("true"),
      tag.get("Properties").get("slot_2_occupied").stringValue("false").equals("true"),
      tag.get("Properties").get("slot_3_occupied").stringValue("false").equals("true"),
      tag.get("Properties").get("slot_4_occupied").stringValue("false").equals("true"),
      tag.get("Properties").get("slot_5_occupied").stringValue("false").equals("true")));
    addBlock("decorated_pot", (name, tag) -> decoratedPot(tag));
    addBlock("sniffer_egg", (name, tag) -> new SnifferEgg(name, BlockProvider.stringToInt(tag.get("Properties").get("hatch"), 0)));
    addBlock("pink_petals", (name, tag) -> new PinkPetals(name, BlockProvider.stringToInt(tag.get("Properties").get("flower_amount"), 1), BlockProvider.facing(tag)));
    addBlock("pitcher_plant", (name, tag) -> new PitcherPlant(name, tag.get("Properties").get("half").stringValue("lower")));
    addBlock("pitcher_crop", (name, tag) -> {
      int age = BlockProvider.stringToInt(tag.get("Properties").get("age"), 0);
      String half = tag.get("Properties").get("half").stringValue("lower");
      if (half.equals("upper") && age < 2) {
        return Air.INSTANCE;
      }
      return new PitcherCrop(age, half);
    });
    addBlock("calibrated_sculk_sensor", (name, tag) -> new CalibratedSculkSensor(
      tag.get("Properties").get("sculk_sensor_phase").stringValue("cooldown"),
      tag.get("Properties").get("facing").stringValue("north")));
    addBlock("oak_hanging_sign", (name, tag) -> hangingSign(tag, "oak"));
    addBlock("oak_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "oak"));
    addBlock("spruce_hanging_sign", (name, tag) -> hangingSign(tag, "spruce"));
    addBlock("spruce_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "spruce"));
    addBlock("birch_hanging_sign", (name, tag) -> hangingSign(tag, "birch"));
    addBlock("birch_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "birch"));
    addBlock("jungle_hanging_sign", (name, tag) -> hangingSign(tag, "jungle"));
    addBlock("jungle_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "jungle"));
    addBlock("acacia_hanging_sign", (name, tag) -> hangingSign(tag, "acacia"));
    addBlock("acacia_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "acacia"));
    addBlock("dark_oak_hanging_sign", (name, tag) -> hangingSign(tag, "dark_oak"));
    addBlock("dark_oak_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "dark_oak"));
    addBlock("crimson_hanging_sign", (name, tag) -> hangingSign(tag, "crimson"));
    addBlock("crimson_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "crimson"));
    addBlock("warped_hanging_sign", (name, tag) -> hangingSign(tag, "warped"));
    addBlock("warped_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "warped"));
    addBlock("mangrove_hanging_sign", (name, tag) -> hangingSign(tag, "mangrove"));
    addBlock("mangrove_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "mangrove"));
    addBlock("bamboo_hanging_sign", (name, tag) -> hangingSign(tag, "bamboo"));
    addBlock("bamboo_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "bamboo"));
    addBlock("cherry_hanging_sign", (name, tag) -> hangingSign(tag, "cherry"));
    addBlock("cherry_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "cherry"));

    //1.21
    addBlock("tuff_slab", (name, tag) -> slab(tag, Texture.tuff));
    addBlock("tuff_stairs", (name, tag) -> stairs(tag, Texture.tuff));
    addBlock("tuff_wall", (name, tag) -> wall(tag, Texture.tuff));
    addBlock("polished_tuff", Texture.polishedTuff);
    addBlock("polished_tuff_slab", (name, tag) -> slab(tag, Texture.polishedTuff));
    addBlock("polished_tuff_stairs", (name, tag) -> stairs(tag, Texture.polishedTuff));
    addBlock("polished_tuff_wall", (name, tag) -> wall(tag, Texture.polishedTuff));
    addBlock("tuff_bricks", Texture.tuffBricks);
    addBlock("tuff_brick_slab", (name, tag) -> slab(tag, Texture.tuffBricks));
    addBlock("tuff_brick_stairs", (name, tag) -> stairs(tag, Texture.tuffBricks));
    addBlock("tuff_brick_wall", (name, tag) -> wall(tag, Texture.tuffBricks));
    addBlock("chiseled_tuff", Texture.chiseledTuff);
    addBlock("chiseled_tuff_bricks", Texture.chiseledTuffBricks);
    for (String s : new String[]{"", "waxed_"}) {
      addBlock(s + "chiseled_copper", Texture.chiseledCopper);
      addBlock(s + "copper_grate", (name, tag) -> new SolidNonOpaqueBlock(name, Texture.copperGrate));
      addBlock(s + "copper_bulb", (name, tag) -> new CopperBulb(name, tag.get("Properties").get("lit").stringValue().equals("true"), tag.get("Properties").get("powered").stringValue().equals("true"),
        Texture.copperBulbLitPowered, Texture.copperBulbLit, Texture.copperBulbPowered, Texture.copperBulb));
      addBlock(s + "copper_door", (name, tag) -> door(tag, Texture.copperDoorTop, Texture.copperDoorBottom));
      addBlock(s + "copper_trapdoor", (name, tag) -> trapdoor(tag, Texture.copperTrapdoor));
      addBlock(s + "exposed_chiseled_copper", Texture.exposedChiseledCopper);
      addBlock(s + "exposed_copper_grate", (name, tag) -> new SolidNonOpaqueBlock(name, Texture.exposedCopperGrate));
      addBlock(s + "exposed_copper_bulb", (name, tag) -> new CopperBulb(name, tag.get("Properties").get("lit").stringValue().equals("true"), tag.get("Properties").get("powered").stringValue().equals("true"),
        Texture.exposedCopperBulbLitPowered, Texture.exposedCopperBulbLit, Texture.exposedCopperBulbPowered, Texture.exposedCopperBulb));
      addBlock(s + "exposed_copper_door", (name, tag) -> door(tag, Texture.exposedCopperDoorTop, Texture.exposedCopperDoorBottom));
      addBlock(s + "exposed_copper_trapdoor", (name, tag) -> trapdoor(tag, Texture.exposedCopperTrapdoor));
      addBlock(s + "weathered_chiseled_copper", Texture.weatheredChiseledCopper);
      addBlock(s + "weathered_copper_grate", (name, tag) -> new SolidNonOpaqueBlock(name, Texture.weatheredCopperGrate));
      addBlock(s + "weathered_copper_bulb", (name, tag) -> new CopperBulb(name, tag.get("Properties").get("lit").stringValue().equals("true"), tag.get("Properties").get("powered").stringValue().equals("true"),
        Texture.weatheredCopperBulbLitPowered, Texture.weatheredCopperBulbLit, Texture.weatheredCopperBulbPowered, Texture.weatheredCopperBulb));
      addBlock(s + "weathered_copper_door", (name, tag) -> door(tag, Texture.weatheredCopperDoorTop, Texture.weatheredCopperDoorBottom));
      addBlock(s + "weathered_copper_trapdoor", (name, tag) -> trapdoor(tag, Texture.weatheredCopperTrapdoor));
      addBlock(s + "oxidized_chiseled_copper", Texture.oxidizedChiseledCopper);
      addBlock(s + "oxidized_copper_grate", (name, tag) -> new SolidNonOpaqueBlock(name, Texture.oxidizedCopperGrate));
      addBlock(s + "oxidized_copper_bulb", (name, tag) -> new CopperBulb(name, tag.get("Properties").get("lit").stringValue().equals("true"), tag.get("Properties").get("powered").stringValue().equals("true"),
        Texture.oxidizedCopperBulbLitPowered, Texture.oxidizedCopperBulbLit, Texture.oxidizedCopperBulbPowered, Texture.oxidizedCopperBulb));
      addBlock(s + "oxidized_copper_door", (name, tag) -> door(tag, Texture.oxidizedCopperDoorTop, Texture.oxidizedCopperDoorBottom));
      addBlock(s + "oxidized_copper_trapdoor", (name, tag) -> trapdoor(tag, Texture.oxidizedCopperTrapdoor));
    }
    addBlock("crafter", (name, tag) -> new Crafter(name, tag.get("Properties").get("orientation").stringValue(), tag.get("Properties").get("crafting").stringValue().equals("true"), tag.get("Properties").get("triggered").stringValue().equals("true"),
      Texture.crafterNorth, Texture.crafterNorthCrafting, Texture.crafterEast, Texture.crafterEastCrafting, Texture.crafterEastTriggered,
      Texture.crafterSouth, Texture.crafterSouthTriggered, Texture.crafterWest, Texture.crafterWestCrafting, Texture.crafterWestTriggered,
      Texture.crafterTop, Texture.crafterTopCrafting, Texture.crafterTopTriggered, Texture.crafterBottom));
    addBlock("vault", (name, tag) -> new Vault(tag.get("Properties").get("facing").stringValue("north"), tag.get("Properties").get("ominous").stringValue().equals("true"), tag.get("Properties").get("vault_state").stringValue("active")));
    addBlock("heavy_core", (name, tag) -> new HeavyCore());
    addBlock("trial_spawner", (name, tag) -> new TrialSpawner(tag.get("Properties").get("ominous").stringValue().equals("true"), tag.get("Properties").get("trial_spawner_state").stringValue("active")));

    //1.21.4 (Winter Drop)
    addBlock("pale_moss_block", Texture.paleMossBlock);
    addBlock("pale_oak_leaves", (name, tag) -> new UntintedLeaves(name, Texture.paleOakLeaves));
    addBlock("pale_oak_log", (name, tag) -> log(tag, Texture.paleOakLog, Texture.paleOakLogTop));
    addBlock("pale_hanging_moss", (name, tag) -> new SpriteBlock(name, tag.get("Properties").get("tip").stringValue().equals("true") ? Texture.paleHangingMossTip : Texture.paleHangingMoss));
    addBlock("pale_oak_button", (name, tag) -> button(tag, Texture.paleOakPlanks));
    addBlock("pale_oak_planks", Texture.paleOakPlanks);
    addBlock("pale_oak_slab", (name, tag) -> slab(tag, Texture.paleOakPlanks));
    addBlock("pale_oak_stairs", (name, tag) -> stairs(tag, Texture.paleOakPlanks));
    addBlock("pale_oak_pressure_plate", (name, tag) -> new PressurePlate(name, Texture.sprucePlanks));
    addBlock("pale_oak_fence", (name, tag) -> fence(tag, Texture.paleOakPlanks));
    addBlock("pale_oak_fence_gate", (name, tag) -> fenceGate(tag, Texture.paleOakPlanks));
    addBlock("pale_oak_trapdoor", (name, tag) -> orientableTrapdoor(tag, Texture.paleOakTrapdoor));
    addBlock("pale_oak_door", (name, tag) -> door(tag, Texture.paleOakDoorTop, Texture.paleOakDoorBottom));
    addBlock("stripped_pale_oak_log", (name, tag) -> log(tag, Texture.strippedPaleOakLog, Texture.strippedPaleOakLogTop));
    addBlock("stripped_pale_oak_wood", (name, tag) -> log(tag, Texture.strippedPaleOakLog, Texture.strippedPaleOakLog));
    addBlock("pale_oak_wood", (name, tag) -> log(tag, Texture.paleOakLog, Texture.paleOakLog));
    addBlock("pale_oak_sapling", (name, tag) -> new SpriteBlock(name, Texture.paleOakSapling));
    addBlock("potted_pale_oak_sapling", (name, tag) -> new FlowerPot(name, FlowerPotModel.Kind.PALE_OAK_SAPLING));
    addBlock("pale_oak_sign", (name, tag) -> sign(tag, "pale_oak"));
    addBlock("pale_oak_wall_sign", (name, tag) -> wallSign(tag, "pale_oak"));
    addBlock("pale_oak_hanging_sign", (name, tag) -> hangingSign(tag, "pale_oak"));
    addBlock("pale_oak_wall_hanging_sign", (name, tag) -> wallHangingSign(tag, "pale_oak"));
    addBlock("pale_moss_carpet", (name, tag) -> new PaleMossCarpet(name,
      tag.get("Properties").get("bottom").stringValue("false").equals("true"),
      tag.get("Properties").get("north").stringValue("none"),
      tag.get("Properties").get("east").stringValue("none"),
      tag.get("Properties").get("south").stringValue("none"),
      tag.get("Properties").get("west").stringValue("none")));
    addBlock("creaking_heart", (name, tag) -> new CreakingHeart(name,
      tag.get("Properties").get("axis").stringValue("y"),
      tag.get("Properties").get("creaking_heart_state").stringValue(
        // pre 25w02a compatibility (active => awake, not active => uprooted)
        tag.get("Properties").get("active").stringValue("false").equals("true") ? "awake" : "uprooted"
      )));
    addBlock("chiseled_resin_bricks", Texture.chiseledResinBricks);
    addBlock("closed_eyeblossom", (name, tag) -> new SpriteBlock(name, Texture.closedEyeblossom));
    addBlock("open_eyeblossom", (name, tag) -> new OpenEyeblossom());
    addBlock("potted_closed_eyeblossom", (name, tag) -> new FlowerPot(name, FlowerPotModel.Kind.CLOSED_EYEBLOSSOM));
    addBlock("potted_open_eyeblossom", (name, tag) -> new FlowerPot(name, FlowerPotModel.Kind.OPEN_EYEBLOSSOM));
    addBlock("resin_block", Texture.resinBlock);
    addBlock("resin_bricks", Texture.resinBricks);
    addBlock("resin_brick_stairs", (name, tag) -> stairs(tag, Texture.resinBricks));
    addBlock("resin_brick_slab", (name, tag) -> slab(tag, Texture.resinBricks));
    addBlock("resin_brick_walls", (name, tag) -> wall(tag, Texture.resinBricks));
    addBlock("resin_clump", (name, tag) -> new ResinClump(
      tag.get("Properties").get("north").stringValue("false").equals("true"),
      tag.get("Properties").get("south").stringValue("false").equals("true"),
      tag.get("Properties").get("east").stringValue("false").equals("true"),
      tag.get("Properties").get("west").stringValue("false").equals("true"),
      tag.get("Properties").get("up").stringValue("false").equals("true"),
      tag.get("Properties").get("down").stringValue("false").equals("true")));

    // 1.21.5 (Spring to Life drop)
    addBlock("leaf_litter", (name, tag) -> new LeafLitter(
      BlockProvider.facing(tag, "north"),
      BlockProvider.stringToInt(tag.get("Properties").get("segment_amount"), 1)
    ));
    addBlock("wildflowers", (name, tag) -> new Wildflowers(name, BlockProvider.stringToInt(tag.get("Properties").get("flower_amount"), 1), BlockProvider.facing(tag)));
    addBlock("bush", (name, tag) -> new TintedSpriteBlock(name, Texture.bush, Tint.BIOME_GRASS));
    addBlock("firefly_bush", (name, tag) -> new FireflyBush());
    addBlock("cactus_flower", (name, tag) -> new SpriteBlock(name, Texture.cactusFlower));
    addBlock("short_dry_grass", (name, tag) -> new TintedSpriteBlock(name, Texture.shortDryGrass, Tint.BIOME_DRY_FOLIAGE));
    addBlock("tall_dry_grass", (name, tag) -> new TintedSpriteBlock(name, Texture.tallDryGrass, Tint.BIOME_DRY_FOLIAGE));
    addBlock("dried_ghast", (name, tag) -> new DriedGhast(BlockProvider.facing(tag), BlockProvider.stringToInt(tag.get("Properties").get("hydration"), 0)));
  }

  @Override
  public Collection<String> getSupportedBlocks() {
    List<String> names = new ArrayList<>(blockList.length + blocks.size());
    names.addAll(Arrays.asList(blockList));
    names.addAll(blocks.keySet());
    return names;
  }

  @Override
  public Block getBlockByTag(String namespacedName, Tag tag) {
    // drop the minecraft: prefix
    String[] split = namespacedName.split(":", 2); // split into maximum 2 parts

    if (split.length != 2 || !split[0].equals("minecraft")) {
      return null;
    }
    String name = split[1];
    switch (name) {
      case "granite":
        return new MinecraftBlock(name, Texture.granite);
      case "polished_granite":
        return new MinecraftBlock(name, Texture.smoothGranite);
      case "diorite":
        return new MinecraftBlock(name, Texture.diorite);
      case "polished_diorite":
        return new MinecraftBlock(name, Texture.smoothDiorite);
      case "andesite":
        return new MinecraftBlock(name, Texture.andesite);
      case "polished_andesite":
        return new MinecraftBlock(name, Texture.smoothAndesite);
      case "grass_block":
        return snowCovered(tag, new GrassBlock());
      case "dirt":
        return new MinecraftBlock(name, Texture.dirt);
      case "coarse_dirt":
        return new MinecraftBlock(name, Texture.coarseDirt);
      case "podzol":
        return snowCovered(
          tag, new TexturedBlock(name, Texture.podzolSide, Texture.podzolTop, Texture.dirt));
      case "infested_cobblestone":
      case "cobblestone":
        return new MinecraftBlock(name, Texture.cobblestone);
      case "oak_planks":
        return new MinecraftBlock(name, Texture.oakPlanks);
      case "spruce_planks":
        return new MinecraftBlock(name, Texture.sprucePlanks);
      case "birch_planks":
        return new MinecraftBlock(name, Texture.birchPlanks);
      case "jungle_planks":
        return new MinecraftBlock(name, Texture.jungleTreePlanks);
      case "acacia_planks":
        return new MinecraftBlock(name, Texture.acaciaPlanks);
      case "dark_oak_planks":
        return new MinecraftBlock(name, Texture.darkOakPlanks);
      case "oak_sapling":
        return new SpriteBlock(name, Texture.oakSapling);
      case "spruce_sapling":
        return new SpriteBlock(name, Texture.spruceSapling);
      case "birch_sapling":
        return new SpriteBlock(name, Texture.birchSapling);
      case "jungle_sapling":
        return new SpriteBlock(name, Texture.jungleSapling);
      case "acacia_sapling":
        return new SpriteBlock(name, Texture.acaciaSapling);
      case "dark_oak_sapling":
        return new SpriteBlock(name, Texture.darkOakSapling);
      case "water":
        return new Water(BlockProvider.stringToInt(tag.get("Properties").get("level"), 0));
      case "water$chunky":
        return new Water(tag.get("level").intValue(), tag.get("data").intValue());
      case "bubble_column":
        return new Water(0); // TODO: render bubbles!
      case "lava":
        return new Lava(BlockProvider.stringToInt(tag.get("Properties").get("level"), 0));
      case "lava$chunky":
        return new Lava(tag.get("level").intValue(), tag.get("data").intValue());
      case "bedrock":
        return new MinecraftBlock(name, Texture.bedrock);
      case "sand":
        return new MinecraftBlock(name, Texture.sand);
      case "red_sand":
        return new MinecraftBlock(name, Texture.redSand);
      case "gravel":
        return new MinecraftBlock(name, Texture.gravel);
      case "gold_ore":
        return new MinecraftBlock(name, Texture.goldOre);
      case "iron_ore":
        return new MinecraftBlock(name, Texture.ironOre);
      case "coal_ore":
        return new MinecraftBlock(name, Texture.coalOre);
      case "oak_log":
        return log(tag, Texture.oakWood, Texture.oakWoodTop);
      case "spruce_log":
        return log(tag, Texture.spruceWood, Texture.spruceWoodTop);
      case "birch_log":
        return log(tag, Texture.birchWood, Texture.birchWoodTop);
      case "jungle_log":
        return log(tag, Texture.jungleWood, Texture.jungleTreeTop);
      case "acacia_log":
        return log(tag, Texture.acaciaWood, Texture.acaciaWoodTop);
      case "dark_oak_log":
        return log(tag, Texture.darkOakWood, Texture.darkOakWoodTop);
      case "stripped_oak_log":
        return log(tag, Texture.strippedOakLog, Texture.strippedOakLogTop);
      case "stripped_spruce_log":
        return log(tag, Texture.strippedSpruceLog, Texture.strippedSpruceLogTop);
      case "stripped_birch_log":
        return log(tag, Texture.strippedBirchLog, Texture.strippedBirchLogTop);
      case "stripped_jungle_log":
        return log(tag, Texture.strippedJungleLog, Texture.strippedJungleLogTop);
      case "stripped_acacia_log":
        return log(tag, Texture.strippedAcaciaLog, Texture.strippedAcaciaLogTop);
      case "stripped_dark_oak_log":
        return log(tag, Texture.strippedDarkOakLog, Texture.strippedDarkOakLogTop);
      case "stripped_oak_wood":
        return log(tag, Texture.strippedOakLog, Texture.strippedOakLog);
      case "stripped_spruce_wood":
        return log(tag, Texture.strippedSpruceLog, Texture.strippedSpruceLog);
      case "stripped_birch_wood":
        return log(tag, Texture.strippedBirchLog, Texture.strippedBirchLog);
      case "stripped_jungle_wood":
        return log(tag, Texture.strippedJungleLog, Texture.strippedJungleLog);
      case "stripped_acacia_wood":
        return log(tag, Texture.strippedAcaciaLog, Texture.strippedAcaciaLog);
      case "stripped_dark_oak_wood":
        return log(tag, Texture.strippedDarkOakLog, Texture.strippedDarkOakLog);
      case "oak_wood":
        return new MinecraftBlock(name, Texture.oakWood);
      case "spruce_wood":
        return new MinecraftBlock(name, Texture.spruceWood);
      case "birch_wood":
        return new MinecraftBlock(name, Texture.birchWood);
      case "jungle_wood":
        return new MinecraftBlock(name, Texture.jungleWood);
      case "acacia_wood":
        return new MinecraftBlock(name, Texture.acaciaWood);
      case "dark_oak_wood":
        return new MinecraftBlock(name, Texture.darkOakWood);
      case "oak_leaves":
        return new Leaves(name, Texture.oakLeaves);
      case "spruce_leaves":
        return new Leaves(name, Texture.spruceLeaves, 0x619961);
      case "birch_leaves":
        return new Leaves(name, Texture.birchLeaves, 0x80a755);
      case "jungle_leaves":
        return new Leaves(name, Texture.jungleTreeLeaves);
      case "acacia_leaves":
        return new Leaves(name, Texture.acaciaLeaves);
      case "dark_oak_leaves":
        return new Leaves(name, Texture.darkOakLeaves);
      case "sponge":
        return new MinecraftBlock(name, Texture.sponge);
      case "wet_sponge":
        return new MinecraftBlock(name, Texture.wetSponge);
      case "glass":
        return new Glass(name, Texture.glass);
      case "lapis_ore":
        return new MinecraftBlock(name, Texture.lapisOre);
      case "lapis_block":
        return new MinecraftBlock(name, Texture.lapisBlock);
      case "dispenser":
        return new Dispenser(BlockProvider.facing(tag));
      case "sandstone":
        return new TexturedBlock(
          name, Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom);
      case "chiseled_sandstone":
        return new TexturedBlock(
          name, Texture.sandstoneDecorated, Texture.sandstoneTop, Texture.sandstoneBottom);
      case "cut_sandstone":
        return new TexturedBlock(
          name, Texture.sandstoneCut, Texture.sandstoneTop, Texture.sandstoneBottom);
      case "note_block":
        return new MinecraftBlock(name, Texture.noteBlock);
      case "powered_rail":
        return poweredRail(tag);
      case "detector_rail": {
        Tag properties = tag.get("Properties");
        String powered = properties.get("powered").stringValue("false");
        Texture straightTrack =
          powered.equals("true") ? Texture.detectorRailOn : Texture.detectorRail;
        return rail(tag, straightTrack);
      }
      case "sticky_piston":
        return piston(tag, true);
      case "cobweb":
        return new SpriteBlock(name, Texture.cobweb);
      case "grass":
      case "short_grass": // since 1.20.3-pre2
        return new TintedSpriteBlock(name, Texture.tallGrass, Tint.BIOME_GRASS);
      case "fern":
        return new TintedSpriteBlock(name, Texture.fern, Tint.BIOME_GRASS);
      case "dead_bush":
        return new SpriteBlock(name, Texture.deadBush);
      case "seagrass":
        return new SpriteBlock(name, Texture.seagrass);
      case "tall_seagrass": {
        String half = tag.get("Properties").get("half").stringValue("lower");
        return new SpriteBlock(
          name, half.equals("lower") ? Texture.tallSeagrassBottom : Texture.tallSeagrassTop);
      }
      case "sea_pickle":
        return seaPickle(tag);
      case "piston":
        return piston(tag, false);
      case "piston_head":
        return pistonHead(tag);
      case "moving_piston":
        // Invisible.
        return Air.INSTANCE;
      case "white_wool":
        return new MinecraftBlock(name, Texture.whiteWool);
      case "orange_wool":
        return new MinecraftBlock(name, Texture.orangeWool);
      case "magenta_wool":
        return new MinecraftBlock(name, Texture.magentaWool);
      case "light_blue_wool":
        return new MinecraftBlock(name, Texture.lightBlueWool);
      case "yellow_wool":
        return new MinecraftBlock(name, Texture.yellowWool);
      case "lime_wool":
        return new MinecraftBlock(name, Texture.limeWool);
      case "pink_wool":
        return new MinecraftBlock(name, Texture.pinkWool);
      case "gray_wool":
        return new MinecraftBlock(name, Texture.grayWool);
      case "light_gray_wool":
        return new MinecraftBlock(name, Texture.lightGrayWool);
      case "cyan_wool":
        return new MinecraftBlock(name, Texture.cyanWool);
      case "purple_wool":
        return new MinecraftBlock(name, Texture.purpleWool);
      case "blue_wool":
        return new MinecraftBlock(name, Texture.blueWool);
      case "brown_wool":
        return new MinecraftBlock(name, Texture.brownWool);
      case "green_wool":
        return new MinecraftBlock(name, Texture.greenWool);
      case "red_wool":
        return new MinecraftBlock(name, Texture.redWool);
      case "black_wool":
        return new MinecraftBlock(name, Texture.blackWool);
      case "dandelion":
        return new SpriteBlock(name, Texture.dandelion);
      case "poppy":
        return new SpriteBlock(name, Texture.poppy);
      case "blue_orchid":
        return new SpriteBlock(name, Texture.blueOrchid);
      case "allium":
        return new SpriteBlock(name, Texture.allium);
      case "azure_bluet":
        return new SpriteBlock(name, Texture.azureBluet);
      case "red_tulip":
        return new SpriteBlock(name, Texture.redTulip);
      case "orange_tulip":
        return new SpriteBlock(name, Texture.orangeTulip);
      case "white_tulip":
        return new SpriteBlock(name, Texture.whiteTulip);
      case "pink_tulip":
        return new SpriteBlock(name, Texture.pinkTulip);
      case "oxeye_daisy":
        return new SpriteBlock(name, Texture.oxeyeDaisy);
      case "cornflower":
        return new SpriteBlock(name, Texture.cornflower);
      case "lily_of_the_valley":
        return new SpriteBlock(name, Texture.lilyOfTheValley);
      case "wither_rose":
        return new SpriteBlock(name, Texture.witherRose);
      case "brown_mushroom":
        return new SpriteBlock(name, Texture.brownMushroom);
      case "red_mushroom":
        return new SpriteBlock(name, Texture.redMushroom);
      case "gold_block":
        return new MinecraftBlock(name, Texture.goldBlock);
      case "iron_block":
        return new MinecraftBlock(name, Texture.ironBlock);
      case "oak_slab":
        return slab(tag, Texture.oakPlanks);
      case "spruce_slab":
        return slab(tag, Texture.sprucePlanks);
      case "birch_slab":
        return slab(tag, Texture.birchPlanks);
      case "jungle_slab":
        return slab(tag, Texture.jungleTreePlanks);
      case "acacia_slab":
        return slab(tag, Texture.acaciaPlanks);
      case "dark_oak_slab":
        return slab(tag, Texture.darkOakPlanks);
      case "stone_slab":
        return slab(tag, Texture.stone);
      case "smooth_stone_slab": // introduced in 1.14, previously called stone_slab (and the
        // stone_slab above didn't exist)
        return slab(tag, Texture.smoothStoneSlabSide, Texture.smoothStone);
      case "sandstone_slab":
        return slab(tag, Texture.sandstoneSide, Texture.sandstoneTop);
      case "petrified_oak_slab":
        return slab(tag, Texture.oakPlanks);
      case "cobblestone_slab":
        return slab(tag, Texture.cobblestone);
      case "brick_slab":
        return slab(tag, Texture.brick);
      case "stone_brick_slab":
        return slab(tag, Texture.stoneBrick);
      case "nether_brick_slab":
        return slab(tag, Texture.netherBrick);
      case "quartz_slab":
        return slab(tag, Texture.quartzSide, Texture.quartzTop);
      case "red_sandstone_slab":
        return slab(tag, Texture.redSandstoneSide, Texture.redSandstoneTop);
      case "purpur_slab":
        return slab(tag, Texture.purpurBlock);
      case "prismarine_slab":
        return slab(tag, Texture.prismarine);
      case "prismarine_brick_slab":
        return slab(tag, Texture.prismarineBricks);
      case "dark_prismarine_slab":
        return slab(tag, Texture.darkPrismarine);
      case "smooth_quartz":
        return new MinecraftBlock(name, Texture.quartzBottom);
      case "smooth_red_sandstone":
        return new MinecraftBlock(name, Texture.redSandstoneTop);
      case "smooth_sandstone":
        return new MinecraftBlock(name, Texture.sandstoneTop);
      case "smooth_stone":
        return new MinecraftBlock(name, Texture.smoothStone);
      case "bricks":
        return new MinecraftBlock(name, Texture.brick);
      case "tnt":
        return new TexturedBlock(name, Texture.tntSide, Texture.tntTop, Texture.tntBottom);
      case "bookshelf":
        return new TexturedBlock(name, Texture.bookshelf, Texture.oakPlanks);
      case "mossy_cobblestone":
        return new MinecraftBlock(name, Texture.mossStone);
      case "obsidian":
        return new MinecraftBlock(name, Texture.obsidian);
      case "torch":
        return new Torch(name, Texture.torch);
      case "wall_torch":
        return new WallTorch(name, Texture.torch, BlockProvider.facing(tag));
      case "end_rod":
        return endRod(tag);
      case "chorus_plant": {
        Tag properties = tag.get("Properties");
        String north = properties.get("north").stringValue("false");
        String south = properties.get("south").stringValue("false");
        String east = properties.get("east").stringValue("false");
        String west = properties.get("west").stringValue("false");
        String up = properties.get("up").stringValue("false");
        String down = properties.get("down").stringValue("false");
        return new ChorusPlant(
          north.equals("true"),
          south.equals("true"),
          east.equals("true"),
          west.equals("true"),
          up.equals("true"),
          down.equals("true"));
      }
      case "chorus_flower":
        return new ChorusFlower(BlockProvider.stringToInt(tag.get("Properties").get("age"), 0));
      case "purpur_block":
        return new MinecraftBlock(name, Texture.purpurBlock);
      case "purpur_pillar":
        return log(tag, Texture.purpurPillarSide, Texture.purpurPillarTop);
      case "purpur_stairs":
        return stairs(tag, Texture.purpurBlock);
      case "oak_stairs":
        return stairs(tag, Texture.oakPlanks);
      case "spruce_stairs":
        return stairs(tag, Texture.sprucePlanks);
      case "birch_stairs":
        return stairs(tag, Texture.birchPlanks);
      case "jungle_stairs":
        return stairs(tag, Texture.jungleTreePlanks);
      case "acacia_stairs":
        return stairs(tag, Texture.acaciaPlanks);
      case "dark_oak_stairs":
        return stairs(tag, Texture.darkOakPlanks);
      case "chest":
        return chest(tag, false);
      case "diamond_ore":
        return new MinecraftBlock(name, Texture.diamondOre);
      case "diamond_block":
        return new MinecraftBlock(name, Texture.diamondBlock);
      case "crafting_table":
        return new TexturedBlock(
          name,
          Texture.workbenchFront,
          Texture.workbenchSide,
          Texture.workbenchSide,
          Texture.workbenchFront,
          Texture.workbenchTop,
          Texture.oakPlanks);
      case "farmland":
        return new Farmland(BlockProvider.stringToInt(tag.get("Properties").get("moisture"), 0));
      case "furnace":
        return furnace(tag);
      case "ladder":
        return new Ladder(BlockProvider.facing(tag));
      case "rail":
        return rail(tag, Texture.rails);
      case "cobblestone_stairs":
        return stairs(tag, Texture.cobblestone);
      case "lever":
        return lever(tag);
      case "stone_pressure_plate":
        return new PressurePlate(name, Texture.stone);
      case "oak_pressure_plate":
        return new PressurePlate(name, Texture.oakPlanks);
      case "spruce_pressure_plate":
        return new PressurePlate(name, Texture.sprucePlanks);
      case "birch_pressure_plate":
        return new PressurePlate(name, Texture.birchPlanks);
      case "jungle_pressure_plate":
        return new PressurePlate(name, Texture.jungleTreePlanks);
      case "acacia_pressure_plate":
        return new PressurePlate(name, Texture.acaciaPlanks);
      case "dark_oak_pressure_plate":
        return new PressurePlate(name, Texture.darkOakPlanks);
      case "redstone_ore":
        return new MinecraftBlock(name, Texture.redstoneOre);
      case "redstone_torch":
        return new RedstoneTorch(isLit(tag));
      case "redstone_wall_torch":
        return new RedstoneWallTorch(BlockProvider.facing(tag), isLit(tag));
      case "stone_button":
        return button(tag, Texture.stone);
      case "snow":
        return new Snow(Math.max(1,
          Math.min(8, BlockProvider.stringToInt(tag.get("Properties").get("layers"), 1))));
      case "ice":
        return new MinecraftBlockTranslucent(name, Texture.ice);
      case "snow_block":
        return new MinecraftBlock(name, Texture.snowBlock);
      case "cactus":
        return new Cactus();
      case "clay":
        return new MinecraftBlock(name, Texture.clay);
      case "jukebox":
        return new TexturedBlock(
          name, Texture.jukeboxSide, Texture.jukeboxTop, Texture.jukeboxSide);
      case "oak_fence":
        return fence(tag, Texture.oakPlanks);
      case "spruce_fence":
        return fence(tag, Texture.sprucePlanks);
      case "birch_fence":
        return fence(tag, Texture.birchPlanks);
      case "jungle_fence":
        return fence(tag, Texture.jungleTreePlanks);
      case "acacia_fence":
        return fence(tag, Texture.acaciaPlanks);
      case "dark_oak_fence":
        return fence(tag, Texture.darkOakPlanks);
      case "pumpkin":
        return nonSolid(new TexturedBlock(name, Texture.pumpkinSide, Texture.pumpkinTop));
      case "carved_pumpkin":
        return nonSolid(new TopBottomOrientedTexturedBlock(
          name,
          BlockProvider.facing(tag),
          Texture.pumpkinFront,
          Texture.pumpkinSide,
          Texture.pumpkinTop));
      case "netherrack":
        return new MinecraftBlock(name, Texture.netherrack);
      case "soul_sand":
        return new MinecraftBlock(name, Texture.soulsand);
      case "glowstone":
        return nonSolid(new MinecraftBlock(name, Texture.glowstone));
      case "jack_o_lantern":
        return nonSolid(new TopBottomOrientedTexturedBlock(
          name,
          BlockProvider.facing(tag),
          Texture.jackolanternFront,
          Texture.pumpkinSide,
          Texture.pumpkinTop));
      case "oak_trapdoor":
        return trapdoor(tag, Texture.trapdoor);
      case "spruce_trapdoor":
        return orientableTrapdoor(tag, Texture.spruceTrapdoor);
      case "birch_trapdoor":
        return orientableTrapdoor(tag, Texture.birchTrapdoor);
      case "jungle_trapdoor":
        return orientableTrapdoor(tag, Texture.jungleTrapdoor);
      case "acacia_trapdoor":
        return orientableTrapdoor(tag, Texture.acaciaTrapdoor);
      case "dark_oak_trapdoor":
        return trapdoor(tag, Texture.darkOakTrapdoor);
      case "infested_stone_bricks":
      case "stone_bricks":
        return new MinecraftBlock(name, Texture.stoneBrick);
      case "infested_mossy_stone_bricks":
      case "mossy_stone_bricks":
        return new MinecraftBlock(name, Texture.mossyStoneBrick);
      case "infested_cracked_stone_bricks":
      case "cracked_stone_bricks":
        return new MinecraftBlock(name, Texture.crackedStoneBrick);
      case "infested_chiseled_stone_bricks":
      case "chiseled_stone_bricks":
        return new MinecraftBlock(name, Texture.circleStoneBrick);
      case "nether_bricks":
        return new MinecraftBlock(name, Texture.netherBrick);
      case "brown_mushroom_block":
        return hugeMushroom(tag, Texture.hugeBrownMushroom);
      case "red_mushroom_block":
        return hugeMushroom(tag, Texture.hugeRedMushroom);
      case "mushroom_stem":
        return hugeMushroom(tag, Texture.mushroomStem);
      case "iron_bars":
        return ironBars(tag);
      case "glass_pane":
        return glassPane(tag, Texture.glass, Texture.glassPaneTop);
      case "melon":
        return nonSolid(new TexturedBlock(name, Texture.melonSide, Texture.melonTop));
      case "vine":
        return vine(tag);
      case "oak_fence_gate":
        return fenceGate(tag, Texture.oakPlanks);
      case "spruce_fence_gate":
        return fenceGate(tag, Texture.sprucePlanks);
      case "birch_fence_gate":
        return fenceGate(tag, Texture.birchPlanks);
      case "jungle_fence_gate":
        return fenceGate(tag, Texture.jungleTreePlanks);
      case "acacia_fence_gate":
        return fenceGate(tag, Texture.acaciaPlanks);
      case "dark_oak_fence_gate":
        return fenceGate(tag, Texture.darkOakPlanks);
      case "brick_stairs":
        return stairs(tag, Texture.brick);
      case "stone_brick_stairs":
        return stairs(tag, Texture.stoneBrick);
      case "mycelium":
        return snowCovered(
          tag, new TexturedBlock(name, Texture.myceliumSide, Texture.myceliumTop, Texture.dirt));
      case "lily_pad":
        return new LilyPad();
      case "nether_brick_fence":
        return fence(tag, Texture.netherBrick);
      case "nether_brick_stairs":
        return stairs(tag, Texture.netherBrick);
      case "enchanting_table":
        return new EnchantingTable();
      case "end_portal_frame": {
        String eye = tag.get("Properties").get("eye").stringValue("false");
        String facing = BlockProvider.facing(tag);
        return new EndPortalFrame(eye.equals("true"), facing);
      }
      case "end_stone":
        return new MinecraftBlock(name, Texture.endStone);
      case "end_stone_bricks":
        return new MinecraftBlock(name, Texture.endBricks);
      case "redstone_lamp": {
        String lit = tag.get("Properties").get("lit").stringValue("false");
        return new RedstoneLamp(lit.equals("true"));
      }
      case "cocoa":
        return cocoa(tag);
      case "sandstone_stairs":
        return stairs(tag, Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom);
      case "emerald_ore":
        return new MinecraftBlock(name, Texture.emeraldOre);
      case "ender_chest": {
        String facing = BlockProvider.facing(tag);
        return new EnderChest(facing);
      }
      case "tripwire_hook":
        return tripwireHook(tag);
      case "tripwire":
        return tripwire(tag);
      case "emerald_block":
        return new MinecraftBlock(name, Texture.emeraldBlock);
      case "beacon":
        return new Beacon();
      case "cobblestone_wall":
        return wall(tag, Texture.cobblestone);
      case "mossy_cobblestone_wall":
        return wall(tag, Texture.mossStone);
      case "oak_button":
        return button(tag, Texture.oakPlanks);
      case "spruce_button":
        return button(tag, Texture.sprucePlanks);
      case "birch_button":
        return button(tag, Texture.birchPlanks);
      case "jungle_button":
        return button(tag, Texture.jungleTreePlanks);
      case "acacia_button":
        return button(tag, Texture.acaciaPlanks);
      case "dark_oak_button":
        return button(tag, Texture.darkOakPlanks);
      case "anvil":
        return anvil(tag, 0);
      case "chipped_anvil":
        return anvil(tag, 1);
      case "damaged_anvil":
        return anvil(tag, 2);
      case "trapped_chest":
        return chest(tag, true);
      case "light_weighted_pressure_plate":
        return new PressurePlate(name, Texture.goldBlock);
      case "heavy_weighted_pressure_plate":
        return new PressurePlate(name, Texture.ironBlock);
      case "daylight_detector": {
        String inverted = tag.get("Properties").get("inverted").stringValue("false");
        return new DaylightDetector(inverted.equals("true"));
      }
      case "redstone_block":
        return new MinecraftBlock(name, Texture.redstoneBlock);
      case "nether_quartz_ore":
        return new MinecraftBlock(name, Texture.netherQuartzOre);
      case "hopper":
        return new Hopper(BlockProvider.facing(tag, "down"));
      case "chiseled_quartz_block":
        return new TexturedBlock(name, Texture.quartzChiseled, Texture.quartzChiseledTop);
      case "quartz_block":
        return new MinecraftBlock(name, Texture.quartzSide);
      case "quartz_pillar":
        return log(tag, Texture.quartzPillar, Texture.quartzPillarTop);
      case "quartz_stairs":
        return stairs(tag, Texture.quartzSide, Texture.quartzTop, Texture.quartzBottom);
      case "activator_rail": {
        Tag properties = tag.get("Properties");
        String powered = properties.get("powered").stringValue("false");
        Texture straightTrack =
          powered.equals("true") ? Texture.activatorRailPowered : Texture.activatorRail;
        return rail(tag, straightTrack);
      }
      case "dropper":
        return new Dropper(BlockProvider.facing(tag, "south"));
      case "white_terracotta":
        return new MinecraftBlock(name, Texture.whiteClay);
      case "orange_terracotta":
        return new MinecraftBlock(name, Texture.orangeClay);
      case "magenta_terracotta":
        return new MinecraftBlock(name, Texture.magentaClay);
      case "light_blue_terracotta":
        return new MinecraftBlock(name, Texture.lightBlueClay);
      case "yellow_terracotta":
        return new MinecraftBlock(name, Texture.yellowClay);
      case "lime_terracotta":
        return new MinecraftBlock(name, Texture.limeClay);
      case "pink_terracotta":
        return new MinecraftBlock(name, Texture.pinkClay);
      case "gray_terracotta":
        return new MinecraftBlock(name, Texture.grayClay);
      case "light_gray_terracotta":
        return new MinecraftBlock(name, Texture.lightGrayClay);
      case "cyan_terracotta":
        return new MinecraftBlock(name, Texture.cyanClay);
      case "purple_terracotta":
        return new MinecraftBlock(name, Texture.purpleClay);
      case "blue_terracotta":
        return new MinecraftBlock(name, Texture.blueClay);
      case "brown_terracotta":
        return new MinecraftBlock(name, Texture.brownClay);
      case "green_terracotta":
        return new MinecraftBlock(name, Texture.greenClay);
      case "red_terracotta":
        return new MinecraftBlock(name, Texture.redClay);
      case "black_terracotta":
        return new MinecraftBlock(name, Texture.blackClay);
      case "iron_trapdoor":
        return trapdoor(tag, Texture.ironTrapdoor);
      case "hay_block":
        return log(tag, Texture.hayBlockSide, Texture.hayBlockTop);
      case "white_carpet":
        return new Carpet(name, Texture.whiteWool);
      case "orange_carpet":
        return new Carpet(name, Texture.orangeWool);
      case "magenta_carpet":
        return new Carpet(name, Texture.magentaWool);
      case "light_blue_carpet":
        return new Carpet(name, Texture.lightBlueWool);
      case "yellow_carpet":
        return new Carpet(name, Texture.yellowWool);
      case "lime_carpet":
        return new Carpet(name, Texture.limeWool);
      case "pink_carpet":
        return new Carpet(name, Texture.pinkWool);
      case "gray_carpet":
        return new Carpet(name, Texture.grayWool);
      case "light_gray_carpet":
        return new Carpet(name, Texture.lightGrayWool);
      case "cyan_carpet":
        return new Carpet(name, Texture.cyanWool);
      case "purple_carpet":
        return new Carpet(name, Texture.purpleWool);
      case "blue_carpet":
        return new Carpet(name, Texture.blueWool);
      case "brown_carpet":
        return new Carpet(name, Texture.brownWool);
      case "green_carpet":
        return new Carpet(name, Texture.greenWool);
      case "red_carpet":
        return new Carpet(name, Texture.redWool);
      case "black_carpet":
        return new Carpet(name, Texture.blackWool);
      case "terracotta":
        return new MinecraftBlock(name, Texture.hardenedClay);
      case "coal_block":
        return new MinecraftBlock(name, Texture.coalBlock);
      case "packed_ice":
        return new MinecraftBlock(name, Texture.packedIce);
      case "slime_block":
        return new Slime();
      case "grass_path": // renamed to dirt_path in 20w45a
      case "dirt_path":
        return new GrassPath();
      case "sunflower": {
        String half = tag.get("Properties").get("half").stringValue("lower");
        return new Sunflower(half);
      }
      case "lilac":
        return largeFlower(tag, Texture.lilacTop, Texture.lilacBottom);
      case "rose_bush":
        return largeFlower(tag, Texture.roseBushTop, Texture.roseBushBottom);
      case "peony":
        return largeFlower(tag, Texture.peonyTop, Texture.peonyBottom);
      case "tall_grass": {
        String half = tag.get("Properties").get("half").stringValue("lower");
        return new TallGrass(half);
      }
      case "large_fern": {
        String half = tag.get("Properties").get("half").stringValue("lower");
        return new LargeFern(half);
      }
      case "white_stained_glass":
        return new Glass(name, Texture.whiteGlass);
      case "orange_stained_glass":
        return new Glass(name, Texture.orangeGlass);
      case "magenta_stained_glass":
        return new Glass(name, Texture.magentaGlass);
      case "light_blue_stained_glass":
        return new Glass(name, Texture.lightBlueGlass);
      case "yellow_stained_glass":
        return new Glass(name, Texture.yellowGlass);
      case "lime_stained_glass":
        return new Glass(name, Texture.limeGlass);
      case "pink_stained_glass":
        return new Glass(name, Texture.pinkGlass);
      case "gray_stained_glass":
        return new Glass(name, Texture.grayGlass);
      case "light_gray_stained_glass":
        return new Glass(name, Texture.lightGrayGlass);
      case "cyan_stained_glass":
        return new Glass(name, Texture.cyanGlass);
      case "purple_stained_glass":
        return new Glass(name, Texture.purpleGlass);
      case "blue_stained_glass":
        return new Glass(name, Texture.blueGlass);
      case "brown_stained_glass":
        return new Glass(name, Texture.brownGlass);
      case "green_stained_glass":
        return new Glass(name, Texture.greenGlass);
      case "red_stained_glass":
        return new Glass(name, Texture.redGlass);
      case "black_stained_glass":
        return new Glass(name, Texture.blackGlass);
      case "white_stained_glass_pane":
        return glassPane(tag, Texture.whiteGlass, Texture.whiteGlassPaneSide);
      case "orange_stained_glass_pane":
        return glassPane(tag, Texture.orangeGlass, Texture.orangeGlassPaneSide);
      case "magenta_stained_glass_pane":
        return glassPane(tag, Texture.magentaGlass, Texture.magentaGlassPaneSide);
      case "light_blue_stained_glass_pane":
        return glassPane(tag, Texture.lightBlueGlass, Texture.lightBlueGlassPaneSide);
      case "yellow_stained_glass_pane":
        return glassPane(tag, Texture.yellowGlass, Texture.yellowGlassPaneSide);
      case "lime_stained_glass_pane":
        return glassPane(tag, Texture.limeGlass, Texture.limeGlassPaneSide);
      case "pink_stained_glass_pane":
        return glassPane(tag, Texture.pinkGlass, Texture.pinkGlassPaneSide);
      case "gray_stained_glass_pane":
        return glassPane(tag, Texture.grayGlass, Texture.grayGlassPaneSide);
      case "light_gray_stained_glass_pane":
        return glassPane(tag, Texture.lightGrayGlass, Texture.lightGrayGlassPaneSide);
      case "cyan_stained_glass_pane":
        return glassPane(tag, Texture.cyanGlass, Texture.cyanGlassPaneSide);
      case "purple_stained_glass_pane":
        return glassPane(tag, Texture.purpleGlass, Texture.purpleGlassPaneSide);
      case "blue_stained_glass_pane":
        return glassPane(tag, Texture.blueGlass, Texture.blueGlassPaneSide);
      case "brown_stained_glass_pane":
        return glassPane(tag, Texture.brownGlass, Texture.brownGlassPaneSide);
      case "green_stained_glass_pane":
        return glassPane(tag, Texture.greenGlass, Texture.greenGlassPaneSide);
      case "red_stained_glass_pane":
        return glassPane(tag, Texture.redGlass, Texture.redGlassPaneSide);
      case "black_stained_glass_pane":
        return glassPane(tag, Texture.blackGlass, Texture.blackGlassPaneSide);
      case "prismarine":
        return new MinecraftBlock(name, Texture.prismarine);
      case "prismarine_bricks":
        return new MinecraftBlock(name, Texture.prismarineBricks);
      case "dark_prismarine":
        return new MinecraftBlock(name, Texture.darkPrismarine);
      case "prismarine_stairs":
        return stairs(tag, Texture.prismarine);
      case "prismarine_brick_stairs":
        return stairs(tag, Texture.prismarineBricks);
      case "dark_prismarine_stairs":
        return stairs(tag, Texture.darkPrismarine);
      case "sea_lantern":
        return nonSolid(new MinecraftBlock(name, Texture.seaLantern));
      case "red_sandstone":
        return new TexturedBlock(
          name, Texture.redSandstoneSide, Texture.redSandstoneTop, Texture.redSandstoneBottom);
      case "chiseled_red_sandstone":
        return new TexturedBlock(
          name,
          Texture.redSandstoneDecorated,
          Texture.redSandstoneTop,
          Texture.redSandstoneBottom);
      case "cut_red_sandstone":
        return new TexturedBlock(
          name, Texture.redSandstoneCut, Texture.redSandstoneTop, Texture.redSandstoneBottom);
      case "red_sandstone_stairs":
        return stairs(
          tag, Texture.redSandstoneSide, Texture.redSandstoneTop, Texture.redSandstoneBottom);
      case "magma_block": {
        Block block = new MinecraftBlock(name, Texture.magma);
        block.emittance = 0.6f;
        return block;
      }
      case "nether_wart_block":
        return new MinecraftBlock(name, Texture.netherWartBlock);
      case "red_nether_bricks":
        return new MinecraftBlock(name, Texture.redNetherBrick);
      case "bone_block":
        return log(tag, Texture.boneSide, Texture.boneTop);
      case "observer": {
        Tag properties = tag.get("Properties");
        String facing = BlockProvider.facing(tag, "south");
        String powered = properties.get("powered").stringValue("false");
        return new Observer(facing, powered.equals("true"));
      }
      case "shulker_box":
        return shulkerBox(tag, Texture.shulker);
      case "white_shulker_box":
        return shulkerBox(tag, Texture.shulkerWhite);
      case "orange_shulker_box":
        return shulkerBox(tag, Texture.shulkerOrange);
      case "magenta_shulker_box":
        return shulkerBox(tag, Texture.shulkerMagenta);
      case "light_blue_shulker_box":
        return shulkerBox(tag, Texture.shulkerLightBlue);
      case "yellow_shulker_box":
        return shulkerBox(tag, Texture.shulkerYellow);
      case "lime_shulker_box":
        return shulkerBox(tag, Texture.shulkerLime);
      case "pink_shulker_box":
        return shulkerBox(tag, Texture.shulkerPink);
      case "gray_shulker_box":
        return shulkerBox(tag, Texture.shulkerGray);
      case "light_gray_shulker_box":
        return shulkerBox(tag, Texture.shulkerSilver);
      case "cyan_shulker_box":
        return shulkerBox(tag, Texture.shulkerCyan);
      case "purple_shulker_box":
        return shulkerBox(tag, Texture.shulkerPurple);
      case "blue_shulker_box":
        return shulkerBox(tag, Texture.shulkerBlue);
      case "brown_shulker_box":
        return shulkerBox(tag, Texture.shulkerBrown);
      case "green_shulker_box":
        return shulkerBox(tag, Texture.shulkerGreen);
      case "red_shulker_box":
        return shulkerBox(tag, Texture.shulkerRed);
      case "black_shulker_box":
        return shulkerBox(tag, Texture.shulkerBlack);
      case "white_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaWhite);
      case "orange_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaOrange);
      case "magenta_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaMagenta);
      case "light_blue_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaLightBlue);
      case "yellow_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaYellow);
      case "lime_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaLime);
      case "pink_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaPink);
      case "gray_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaGray);
      case "light_gray_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaSilver);
      case "cyan_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaCyan);
      case "purple_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaPurple);
      case "blue_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaBlue);
      case "brown_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaBrown);
      case "green_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaGreen);
      case "red_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaRed);
      case "black_glazed_terracotta":
        return glazedTerracotta(tag, Texture.terracottaBlack);
      case "white_concrete":
        return new MinecraftBlock(name, Texture.concreteWhite);
      case "orange_concrete":
        return new MinecraftBlock(name, Texture.concreteOrange);
      case "magenta_concrete":
        return new MinecraftBlock(name, Texture.concreteMagenta);
      case "light_blue_concrete":
        return new MinecraftBlock(name, Texture.concreteLightBlue);
      case "yellow_concrete":
        return new MinecraftBlock(name, Texture.concreteYellow);
      case "lime_concrete":
        return new MinecraftBlock(name, Texture.concreteLime);
      case "pink_concrete":
        return new MinecraftBlock(name, Texture.concretePink);
      case "gray_concrete":
        return new MinecraftBlock(name, Texture.concreteGray);
      case "light_gray_concrete":
        return new MinecraftBlock(name, Texture.concreteSilver);
      case "cyan_concrete":
        return new MinecraftBlock(name, Texture.concreteCyan);
      case "purple_concrete":
        return new MinecraftBlock(name, Texture.concretePurple);
      case "blue_concrete":
        return new MinecraftBlock(name, Texture.concreteBlue);
      case "brown_concrete":
        return new MinecraftBlock(name, Texture.concreteBrown);
      case "green_concrete":
        return new MinecraftBlock(name, Texture.concreteGreen);
      case "red_concrete":
        return new MinecraftBlock(name, Texture.concreteRed);
      case "black_concrete":
        return new MinecraftBlock(name, Texture.concreteBlack);
      case "white_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderWhite);
      case "orange_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderOrange);
      case "magenta_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderMagenta);
      case "light_blue_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderLightBlue);
      case "yellow_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderYellow);
      case "lime_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderLime);
      case "pink_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderPink);
      case "gray_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderGray);
      case "light_gray_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderSilver);
      case "cyan_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderCyan);
      case "purple_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderPurple);
      case "blue_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderBlue);
      case "brown_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderBrown);
      case "green_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderGreen);
      case "red_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderRed);
      case "black_concrete_powder":
        return new MinecraftBlock(name, Texture.concretePowderBlack);
      case "turtle_egg":
        return turtleEgg(tag);
      case "dead_tube_coral_block":
        return new MinecraftBlock(name, Texture.deadTubeCoralBlock);
      case "dead_brain_coral_block":
        return new MinecraftBlock(name, Texture.deadBrainCoralBlock);
      case "dead_bubble_coral_block":
        return new MinecraftBlock(name, Texture.deadBubbleCoralBlock);
      case "dead_fire_coral_block":
        return new MinecraftBlock(name, Texture.deadFireCoralBlock);
      case "dead_horn_coral_block":
        return new MinecraftBlock(name, Texture.deadHornCoralBlock);
      case "tube_coral_block":
        return new MinecraftBlock(name, Texture.tubeCoralBlock);
      case "brain_coral_block":
        return new MinecraftBlock(name, Texture.brainCoralBlock);
      case "bubble_coral_block":
        return new MinecraftBlock(name, Texture.bubbleCoralBlock);
      case "fire_coral_block":
        return new MinecraftBlock(name, Texture.fireCoralBlock);
      case "horn_coral_block":
        return new MinecraftBlock(name, Texture.hornCoralBlock);
      case "tube_coral":
        return new SpriteBlock(name, Texture.tubeCoral);
      case "brain_coral":
        return new SpriteBlock(name, Texture.hornCoral);
      case "bubble_coral":
        return new SpriteBlock(name, Texture.bubbleCoral);
      case "fire_coral":
        return new SpriteBlock(name, Texture.fireCoral);
      case "horn_coral":
        return new SpriteBlock(name, Texture.hornCoral);
      case "dead_tube_coral":
        return new SpriteBlock(name, Texture.deadTubeCoral);
      case "dead_brain_coral":
        return new SpriteBlock(name, Texture.deadBrainCoral);
      case "dead_bubble_coral":
        return new SpriteBlock(name, Texture.deadBubbleCoral);
      case "dead_fire_coral":
        return new SpriteBlock(name, Texture.deadFireCoral);
      case "dead_horn_coral":
        return new SpriteBlock(name, Texture.deadHornCoral);
      case "tube_coral_fan":
        return new CoralFan(name, "tube");
      case "tube_coral_wall_fan":
        return new WallCoralFan(name, "tube", BlockProvider.facing(tag));
      case "brain_coral_fan":
        return new CoralFan(name, "brain");
      case "brain_coral_wall_fan":
        return new WallCoralFan(name, "brain", BlockProvider.facing(tag));
      case "bubble_coral_fan":
        return new CoralFan(name, "bubble");
      case "bubble_coral_wall_fan":
        return new WallCoralFan(name, "bubble", BlockProvider.facing(tag));
      case "fire_coral_fan":
        return new CoralFan(name, "fire");
      case "fire_coral_wall_fan":
        return new WallCoralFan(name, "fire", BlockProvider.facing(tag));
      case "horn_coral_fan":
        return new CoralFan(name, "horn");
      case "horn_coral_wall_fan":
        return new WallCoralFan(name, "horn", BlockProvider.facing(tag));
      case "dead_tube_coral_fan":
        return new CoralFan(name, "dead_tube");
      case "dead_tube_coral_wall_fan":
        return new WallCoralFan(name, "dead_tube", BlockProvider.facing(tag));
      case "dead_brain_coral_fan":
        return new CoralFan(name, "dead_brain");
      case "dead_brain_coral_wall_fan":
        return new WallCoralFan(name, "dead_brain", BlockProvider.facing(tag));
      case "dead_bubble_coral_fan":
        return new CoralFan(name, "dead_bubble");
      case "dead_bubble_coral_wall_fan":
        return new WallCoralFan(name, "dead_bubble", BlockProvider.facing(tag));
      case "dead_fire_coral_fan":
        return new CoralFan(name, "dead_fire");
      case "dead_fire_coral_wall_fan":
        return new WallCoralFan(name, "dead_fire", BlockProvider.facing(tag));
      case "dead_horn_coral_fan":
        return new CoralFan(name, "dead_horn");
      case "dead_horn_coral_wall_fan":
        return new WallCoralFan(name, "dead_horn", BlockProvider.facing(tag));
      case "blue_ice":
        return new MinecraftBlock(name, Texture.blueIce);
      case "conduit":
        return new Conduit();
      case "polished_granite_stairs":
        return stairs(tag, Texture.smoothGranite);
      case "smooth_red_sandstone_stairs":
        return stairs(tag, Texture.redSandstoneTop);
      case "mossy_stone_brick_stairs":
        return stairs(tag, Texture.mossyStoneBrick);
      case "polished_diorite_stairs":
        return stairs(tag, Texture.smoothDiorite);
      case "mossy_cobblestone_stairs":
        return stairs(tag, Texture.mossStone);
      case "end_stone_brick_stairs":
        return stairs(tag, Texture.endBricks);
      case "stone_stairs":
        return stairs(tag, Texture.stone);
      case "smooth_sandstone_stairs":
        return stairs(tag, Texture.sandstoneTop);
      case "smooth_quartz_stairs":
        return stairs(tag, Texture.quartzBottom);
      case "granite_stairs":
        return stairs(tag, Texture.granite);
      case "andesite_stairs":
        return stairs(tag, Texture.andesite);
      case "red_nether_brick_stairs":
        return stairs(tag, Texture.redNetherBrick);
      case "polished_andesite_stairs":
        return stairs(tag, Texture.smoothAndesite);
      case "diorite_stairs":
        return stairs(tag, Texture.diorite);
      case "polished_granite_slab":
        return slab(tag, Texture.smoothGranite);
      case "smooth_red_sandstone_slab":
        return slab(tag, Texture.redSandstoneTop);
      case "mossy_stone_brick_slab":
        return slab(tag, Texture.mossyStoneBrick);
      case "polished_diorite_slab":
        return slab(tag, Texture.smoothDiorite);
      case "mossy_cobblestone_slab":
        return slab(tag, Texture.mossStone);
      case "end_stone_brick_slab":
        return slab(tag, Texture.endBricks);
      case "smooth_sandstone_slab":
        return slab(tag, Texture.sandstoneTop);
      case "smooth_quartz_slab":
        return slab(tag, Texture.quartzBottom, Texture.quartzBottom);
      case "granite_slab":
        return slab(tag, Texture.granite);
      case "andesite_slab":
        return slab(tag, Texture.andesite);
      case "red_nether_brick_slab":
        return slab(tag, Texture.redNetherBrick);
      case "polished_andesite_slab":
        return slab(tag, Texture.smoothAndesite);
      case "diorite_slab":
        return slab(tag, Texture.diorite);
      case "brick_wall":
        return wall(tag, Texture.brick);
      case "prismarine_wall":
        return wall(tag, Texture.prismarine);
      case "red_sandstone_wall":
        return wall(tag, Texture.redSandstoneSide);
      case "mossy_stone_brick_wall":
        return wall(tag, Texture.mossyStoneBrick);
      case "granite_wall":
        return wall(tag, Texture.granite);
      case "stone_brick_wall":
        return wall(tag, Texture.stoneBrick);
      case "nether_brick_wall":
        return wall(tag, Texture.netherBrick);
      case "andesite_wall":
        return wall(tag, Texture.andesite);
      case "red_nether_brick_wall":
        return wall(tag, Texture.redNetherBrick);
      case "sandstone_wall":
        return wall(tag, Texture.sandstoneSide);
      case "end_stone_brick_wall":
        return wall(tag, Texture.endBricks);
      case "diorite_wall":
        return wall(tag, Texture.diorite);
      case "scaffolding":
        return new Scaffolding(
          tag.get("Properties").get("bottom").stringValue("false").equals("true"));
      case "oak_door":
        return door(tag, Texture.oakDoorTop, Texture.oakDoorBottom);
      case "iron_door":
        return door(tag, Texture.ironDoorTop, Texture.ironDoorBottom);
      case "spruce_door":
        return door(tag, Texture.spruceDoorTop, Texture.spruceDoorBottom);
      case "birch_door":
        return door(tag, Texture.birchDoorTop, Texture.birchDoorBottom);
      case "jungle_door":
        return door(tag, Texture.jungleDoorTop, Texture.jungleDoorBottom);
      case "acacia_door":
        return door(tag, Texture.acaciaDoorTop, Texture.acaciaDoorBottom);
      case "dark_oak_door":
        return door(tag, Texture.darkOakDoorTop, Texture.darkOakDoorBottom);
      case "repeater":
        return repeater(tag);
      case "comparator":
        return comparator(tag);
      case "composter":
        return composter(tag);
      case "fire":
        return new Fire();
      case "wheat":
        return new Wheat(BlockProvider.stringToInt(tag.get("Properties").get("age"), 7));
      case "sign":
      case "oak_sign":
        return sign(tag, "oak");
      case "wall_sign":
      case "oak_wall_sign":
        return wallSign(tag, "oak");
      case "spruce_sign":
        return sign(tag, "spruce");
      case "spruce_wall_sign":
        return wallSign(tag, "spruce");
      case "birch_sign":
        return sign(tag, "birch");
      case "birch_wall_sign":
        return wallSign(tag, "birch");
      case "jungle_sign":
        return sign(tag, "jungle");
      case "jungle_wall_sign":
        return wallSign(tag, "jungle");
      case "acacia_sign":
        return sign(tag, "acacia");
      case "acacia_wall_sign":
        return wallSign(tag, "acacia");
      case "dark_oak_sign":
        return sign(tag, "dark_oak");
      case "dark_oak_wall_sign":
        return wallSign(tag, "dark_oak");
      case "redstone_wire":
        return redstoneWire(tag);
      case "sugar_cane":
        return new TintedSpriteBlock(name, Texture.sugarCane, Tint.BIOME_GRASS); // tinted since 1.7.2 (13w36a)
      case "kelp":
        return new SpriteBlock(name, Texture.kelp);
      case "kelp_plant":
        return new SpriteBlock(name, Texture.kelpPlant);
      case "dried_kelp_block":
        return new TexturedBlock(
          name, Texture.driedKelpSide, Texture.driedKelpTop, Texture.driedKelpBottom);
      case "bamboo":
        return bamboo(tag);
      case "bamboo_sapling":
        return new SpriteBlock(name, Texture.bambooSapling);
      case "cake":
        return new Cake(BlockProvider.stringToInt(tag.get("Properties").get("bites"), 0));
      case "white_bed":
        return bed(tag, Texture.bedWhite);
      case "orange_bed":
        return bed(tag, Texture.bedOrange);
      case "magenta_bed":
        return bed(tag, Texture.bedMagenta);
      case "light_blue_bed":
        return bed(tag, Texture.bedLightBlue);
      case "yellow_bed":
        return bed(tag, Texture.bedYellow);
      case "lime_bed":
        return bed(tag, Texture.bedLime);
      case "pink_bed":
        return bed(tag, Texture.bedPink);
      case "gray_bed":
        return bed(tag, Texture.bedGray);
      case "light_gray_bed":
        return bed(tag, Texture.bedSilver);
      case "cyan_bed":
        return bed(tag, Texture.bedCyan);
      case "purple_bed":
        return bed(tag, Texture.bedPurple);
      case "blue_bed":
        return bed(tag, Texture.bedBlue);
      case "brown_bed":
        return bed(tag, Texture.bedBrown);
      case "green_bed":
        return bed(tag, Texture.bedGreen);
      case "red_bed":
        return bed(tag, Texture.bedRed);
      case "black_bed":
        return bed(tag, Texture.bedBlack);
      case "pumpkin_stem":
      case "melon_stem":
        return new Stem(name, BlockProvider.stringToInt(tag.get("Properties").get("age"), 7));
      case "attached_pumpkin_stem":
      case "attached_melon_stem":
        return new AttachedStem(name, BlockProvider.facing(tag));
      case "nether_wart":
        return new NetherWart(BlockProvider.stringToInt(tag.get("Properties").get("age"), 3));
      case "brewing_stand": {
        Tag properties = tag.get("Properties");
        String bottle0 = properties.get("has_bottle_0").stringValue("false");
        String bottle1 = properties.get("has_bottle_1").stringValue("false");
        String bottle2 = properties.get("has_bottle_2").stringValue("false");
        return new BrewingStand(
          bottle0.equals("true"), bottle1.equals("true"), bottle2.equals("true"));
      }
      case "cauldron":
      case "water_cauldron":
        return new Cauldron(name, BlockProvider.stringToInt(tag.get("Properties").get("level"), 3));
      case "flower_pot":
        return new FlowerPot(name, FlowerPotModel.Kind.NONE);
      case "potted_poppy":
        return new FlowerPot(name, FlowerPotModel.Kind.POPPY);
      case "potted_dandelion":
        return new FlowerPot(name, FlowerPotModel.Kind.DANDELION);
      case "potted_oak_sapling":
        return new FlowerPot(name, FlowerPotModel.Kind.OAK_SAPLING);
      case "potted_spruce_sapling":
        return new FlowerPot(name, FlowerPotModel.Kind.SPRUCE_SAPLING);
      case "potted_birch_sapling":
        return new FlowerPot(name, FlowerPotModel.Kind.BIRCH_SAPLING);
      case "potted_jungle_sapling":
        return new FlowerPot(name, FlowerPotModel.Kind.JUNGLE_SAPLING);
      case "potted_red_mushroom":
        return new FlowerPot(name, FlowerPotModel.Kind.RED_MUSHROOM);
      case "potted_brown_mushroom":
        return new FlowerPot(name, FlowerPotModel.Kind.BROWN_MUSHROOM);
      case "potted_cactus":
        return new FlowerPot(name, FlowerPotModel.Kind.CACTUS);
      case "potted_dead_bush":
        return new FlowerPot(name, FlowerPotModel.Kind.DEAD_BUSH);
      case "potted_fern":
        return new FlowerPot(name, FlowerPotModel.Kind.FERN);
      case "potted_acacia_sapling":
        return new FlowerPot(name, FlowerPotModel.Kind.ACACIA_SAPLING);
      case "potted_dark_oak_sapling":
        return new FlowerPot(name, FlowerPotModel.Kind.DARK_OAK_SAPLING);
      case "potted_blue_orchid":
        return new FlowerPot(name, FlowerPotModel.Kind.BLUE_ORCHID);
      case "potted_allium":
        return new FlowerPot(name, FlowerPotModel.Kind.ALLIUM);
      case "potted_azure_bluet":
        return new FlowerPot(name, FlowerPotModel.Kind.AZURE_BLUET);
      case "potted_red_tulip":
        return new FlowerPot(name, FlowerPotModel.Kind.RED_TULIP);
      case "potted_orange_tulip":
        return new FlowerPot(name, FlowerPotModel.Kind.ORANGE_TULIP);
      case "potted_white_tulip":
        return new FlowerPot(name, FlowerPotModel.Kind.WHITE_TULIP);
      case "potted_pink_tulip":
        return new FlowerPot(name, FlowerPotModel.Kind.PINK_TULIP);
      case "potted_oxeye_daisy":
        return new FlowerPot(name, FlowerPotModel.Kind.OXEYE_DAISY);
      case "potted_bamboo":
        return new FlowerPot(name, FlowerPotModel.Kind.BAMBOO);
      case "potted_cornflower":
        return new FlowerPot(name, FlowerPotModel.Kind.CORNFLOWER);
      case "potted_lily_of_the_valley":
        return new FlowerPot(name, FlowerPotModel.Kind.LILY_OF_THE_VALLEY);
      case "potted_wither_rose":
        return new FlowerPot(name, FlowerPotModel.Kind.WITHER_ROSE);
      case "potted_warped_fungus":
        return new FlowerPot(name, FlowerPotModel.Kind.WARPED_FUNGUS);
      case "potted_crimson_fungus":
        return new FlowerPot(name, FlowerPotModel.Kind.CRIMSON_FUNGUS);
      case "potted_crimson_roots":
        return new FlowerPot(name, FlowerPotModel.Kind.CRIMSON_ROOTS);
      case "potted_warped_roots":
        return new FlowerPot(name, FlowerPotModel.Kind.WARPED_ROOTS);
      case "potted_azalea_bush":
        return new FlowerPot(name, FlowerPotModel.Kind.AZALEA_BUSH);
      case "potted_flowering_azalea_bush":
        return new FlowerPot(name, FlowerPotModel.Kind.FLOWERING_AZALEA_BUSH);
      case "carrots":
        return new Carrots(BlockProvider.stringToInt(tag.get("Properties").get("age"), 7));
      case "potatoes":
        return new Potatoes(BlockProvider.stringToInt(tag.get("Properties").get("age"), 7));
      case "skeleton_skull":
        return skull(tag, Texture.skeleton, SkullEntity.Kind.SKELETON);
      case "skeleton_wall_skull":
        return wallSkull(tag, Texture.skeleton, SkullEntity.Kind.SKELETON);
      case "wither_skeleton_skull":
        return skull(tag, Texture.wither, SkullEntity.Kind.WITHER_SKELETON);
      case "wither_skeleton_wall_skull":
        return wallSkull(tag, Texture.wither, SkullEntity.Kind.WITHER_SKELETON);
      case "zombie_head":
        return skull(tag, Texture.zombie, SkullEntity.Kind.ZOMBIE);
      case "zombie_wall_head":
        return wallSkull(tag, Texture.zombie, SkullEntity.Kind.ZOMBIE);
      case "player_head":
        return skull(tag, Texture.steve, SkullEntity.Kind.PLAYER);
      case "player_wall_head":
        return wallSkull(tag, Texture.steve, SkullEntity.Kind.PLAYER);
      case "creeper_head":
        return skull(tag, Texture.creeper, SkullEntity.Kind.CREEPER);
      case "creeper_wall_head":
        return wallSkull(tag, Texture.creeper, SkullEntity.Kind.CREEPER);
      case "dragon_egg":
        return new DragonEgg();
      case "dragon_head":
        return skull(tag, Texture.dragon, SkullEntity.Kind.DRAGON);
      case "dragon_wall_head":
        return wallSkull(tag, Texture.dragon, SkullEntity.Kind.DRAGON);
      case "white_banner":
        return banner(tag, Texture.whiteWool, BannerDesign.Color.WHITE);
      case "orange_banner":
        return banner(tag, Texture.orangeWool, BannerDesign.Color.ORANGE);
      case "magenta_banner":
        return banner(tag, Texture.magentaWool, BannerDesign.Color.MAGENTA);
      case "light_blue_banner":
        return banner(tag, Texture.lightBlueWool, BannerDesign.Color.LIGHT_BLUE);
      case "yellow_banner":
        return banner(tag, Texture.yellowWool, BannerDesign.Color.YELLOW);
      case "lime_banner":
        return banner(tag, Texture.limeWool, BannerDesign.Color.LIME);
      case "pink_banner":
        return banner(tag, Texture.pinkWool, BannerDesign.Color.PINK);
      case "gray_banner":
        return banner(tag, Texture.grayWool, BannerDesign.Color.GRAY);
      case "light_gray_banner":
        return banner(tag, Texture.lightGrayWool, BannerDesign.Color.SILVER);
      case "cyan_banner":
        return banner(tag, Texture.cyanWool, BannerDesign.Color.CYAN);
      case "purple_banner":
        return banner(tag, Texture.purpleWool, BannerDesign.Color.PURPLE);
      case "blue_banner":
        return banner(tag, Texture.blueWool, BannerDesign.Color.BLUE);
      case "brown_banner":
        return banner(tag, Texture.brownWool, BannerDesign.Color.BROWN);
      case "green_banner":
        return banner(tag, Texture.greenWool, BannerDesign.Color.GREEN);
      case "red_banner":
        return banner(tag, Texture.redWool, BannerDesign.Color.RED);
      case "black_banner":
        return banner(tag, Texture.blackWool, BannerDesign.Color.BLACK);
      case "white_wall_banner":
        return wallBanner(tag, Texture.whiteWool, BannerDesign.Color.WHITE);
      case "orange_wall_banner":
        return wallBanner(tag, Texture.orangeWool, BannerDesign.Color.ORANGE);
      case "magenta_wall_banner":
        return wallBanner(tag, Texture.magentaWool, BannerDesign.Color.MAGENTA);
      case "light_blue_wall_banner":
        return wallBanner(tag, Texture.lightBlueWool, BannerDesign.Color.LIGHT_BLUE);
      case "yellow_wall_banner":
        return wallBanner(tag, Texture.yellowWool, BannerDesign.Color.YELLOW);
      case "lime_wall_banner":
        return wallBanner(tag, Texture.limeWool, BannerDesign.Color.LIME);
      case "pink_wall_banner":
        return wallBanner(tag, Texture.pinkWool, BannerDesign.Color.PINK);
      case "gray_wall_banner":
        return wallBanner(tag, Texture.grayWool, BannerDesign.Color.GRAY);
      case "light_gray_wall_banner":
        return wallBanner(tag, Texture.lightGrayWool, BannerDesign.Color.SILVER);
      case "cyan_wall_banner":
        return wallBanner(tag, Texture.cyanWool, BannerDesign.Color.CYAN);
      case "purple_wall_banner":
        return wallBanner(tag, Texture.purpleWool, BannerDesign.Color.PURPLE);
      case "blue_wall_banner":
        return wallBanner(tag, Texture.blueWool, BannerDesign.Color.BLUE);
      case "brown_wall_banner":
        return wallBanner(tag, Texture.brownWool, BannerDesign.Color.BROWN);
      case "green_wall_banner":
        return wallBanner(tag, Texture.greenWool, BannerDesign.Color.GREEN);
      case "red_wall_banner":
        return wallBanner(tag, Texture.redWool, BannerDesign.Color.RED);
      case "black_wall_banner":
        return wallBanner(tag, Texture.blackWool, BannerDesign.Color.BLACK);
      case "beetroots":
        return new Beetroots(BlockProvider.stringToInt(tag.get("Properties").get("age"), 3));
      case "loom":
        return new TopBottomOrientedTexturedBlock(
          name,
          BlockProvider.facing(tag),
          Texture.loomFront,
          Texture.loomSide,
          Texture.loomTop,
          Texture.loomBottom);
      case "barrel":
        return new Barrel(tag.get("Properties").get("facing").stringValue(), tag.get("Properties").get("open").stringValue());
      case "smoker":
        return smoker(tag);
      case "blast_furnace":
        return blastFurnace(tag);
      case "cartography_table":
        return new TexturedBlock(
          name,
          Texture.cartographyTableSide3,
          Texture.cartographyTableSide1,
          Texture.cartographyTableSide3,
          Texture.cartographyTableSide2,
          Texture.cartographyTableTop,
          Texture.darkOakPlanks);
      case "fletching_table":
        return new TexturedBlock(
          name,
          Texture.fletchingTableFront,
          Texture.fletchingTableFront,
          Texture.fletchingTableSide,
          Texture.fletchingTableSide,
          Texture.fletchingTableTop,
          Texture.birchPlanks);
      case "grindstone":
        return new Grindstone(
          tag.get("Properties").get("face").stringValue("floor"), BlockProvider.facing(tag));
      case "lectern":
        return new Lectern(
          BlockProvider.facing(tag),
          tag.get("Properties").get("has_book").stringValue("false").equals("true"));
      case "smithing_table":
        return new TexturedBlock(
          name,
          Texture.smithingTableFront,
          Texture.smithingTableFront,
          Texture.smithingTableSide,
          Texture.smithingTableSide,
          Texture.smithingTableTop,
          Texture.smithingTableBottom);
      case "stonecutter":
        return new Stonecutter(BlockProvider.facing(tag));
      case "bell":
        return new Bell(
          BlockProvider.facing(tag),
          tag.get("Properties").get("attachment").stringValue("floor"));
      case "lantern":
        return new Lantern(
          "lantern",
          Texture.lantern,
          tag.get("Properties").get("hanging").stringValue("false").equals("true"));
      case "sweet_berry_bush":
        return new SweetBerryBush(BlockProvider.stringToInt(tag.get("Properties").get("age"), 3));
      case "campfire":
        return new Campfire(
          "campfire",
          se.llbit.chunky.entity.Campfire.Kind.CAMPFIRE,
          BlockProvider.facing(tag),
          isLit(tag));
      case "cut_sandstone_slab":
        return slab(tag, Texture.sandstoneCut, Texture.sandstoneTop);
      case "cut_red_sandstone_slab":
        return slab(tag, Texture.redSandstoneCut, Texture.redSandstoneTop);
      case "frosted_ice":
        return new FrostedIce(BlockProvider.stringToInt(tag.get("Properties").get("age"), 3));
      case "honey_block":
        return new Honey();
      case "bee_nest":
        return beeNest(tag);
      case "beehive":
        return beehive(tag);
      case "honeycomb_block":
        return new MinecraftBlock("honeycomb_block", Texture.honeycombBlock);
      case "spawner":
        return solid(new MinecraftBlockTranslucent(name, Texture.monsterSpawner));
      case "nether_portal": {
        String axis = tag.get("Properties").get("axis").stringValue("x");
        return new NetherPortal(axis);
      }
      case "end_portal":
        return new EndPortal();
      case "end_gateway":
        return new MinecraftBlock(name, Texture.black);
      case "command_block": {
        Tag properties = tag.get("Properties");
        String conditional = properties.get("conditional").stringValue("false");
        return new CommandBlock(BlockProvider.facing(tag, "south"), conditional.equals("true"));
      }
      case "chain_command_block": {
        Tag properties = tag.get("Properties");
        String facing = BlockProvider.facing(tag, "south");
        String conditional = properties.get("conditional").stringValue("false");
        return new ChainCommandBlock(facing, conditional.equals("true"));
      }
      case "repeating_command_block": {
        Tag properties = tag.get("Properties");
        String facing = BlockProvider.facing(tag, "south");
        String conditional = properties.get("conditional").stringValue("false");
        return new RepeatingCommandBlock(facing, conditional.equals("true"));
      }
      case "structure_block":
        return structureBlock(tag);
      case "jigsaw": {
        // as of 20w13a (1.16), the jigsaw block supports 12 orientations saved in the orientation tag
        Tag orientation = tag.get("Properties").get("orientation");
        if (orientation.isError()) {
          return new JigsawBlock("jigsaw", BlockProvider.facing(tag, "up"));
        } else {
          return new JigsawBlock("jigsaw", orientation.stringValue("north_up"));
        }
      }
      case "soul_soil":
        return new MinecraftBlock("soul_soil", Texture.soulSoil);
      case "crimson_nylium":
        return new TexturedBlock(
          "crimson_nylium", Texture.crimsonNyliumSide, Texture.crimsonNylium, Texture.netherrack);
      case "warped_nylium":
        return new TexturedBlock(
          "warped_nylium", Texture.warpedNyliumSide, Texture.warpedNylium, Texture.netherrack);
      case "nether_gold_ore":
        return new MinecraftBlock("nether_gold_ore", Texture.netherGoldOre);
      case "target":
        return new TexturedBlock("target", Texture.targetSide, Texture.targetTop);
      case "netherite_block":
        return new MinecraftBlock("netherite_block", Texture.netheriteBlock);
      case "shroomlight":
        return new MinecraftBlock("shroomlight", Texture.shroomlight);
      case "warped_wart_block":
        return new MinecraftBlock("warped_wart_block", Texture.warpedWartBlock);
      case "basalt":
        return log(tag, Texture.basaltSide, Texture.basaltTop);
      case "polished_basalt":
        return log(tag, Texture.polishedBasaltSide, Texture.polishedBasaltTop);
      case "ancient_debris":
        return new TexturedBlock(
          "ancient_debris", Texture.ancientDebrisSide, Texture.ancientDebrisTop);
      case "warped_fungus":
        return new SpriteBlock("warped_fungus", Texture.warpedFungus);
      case "crimson_fungus":
        return new SpriteBlock("crimson_fungus", Texture.crimsonFungus);
      case "nether_sprouts":
        return new SpriteBlock("nether_sprouts", Texture.netherSprouts);
      case "warped_roots":
        return new SpriteBlock("warped_roots", Texture.warpedRoots);
      case "crimson_roots":
        return new SpriteBlock("crimson_roots", Texture.crimsonRoots);
      case "crying_obsidian":
        return new MinecraftBlock("crying_obsidian", Texture.cryingObsidian);
      case "warped_hyphae":
        return log(tag, Texture.warpedStem, Texture.warpedStem);
      case "stripped_warped_hyphae":
        return log(tag, Texture.strippedWarpedStem, Texture.strippedWarpedStem);
      case "warped_stem":
        return log(tag, Texture.warpedStem, Texture.warpedStemTop);
      case "stripped_warped_stem":
        return log(tag, Texture.strippedWarpedStem, Texture.strippedWarpedStemTop);
      case "crimson_hyphae":
        return log(tag, Texture.crimsonStem, Texture.crimsonStem);
      case "stripped_crimson_hyphae":
        return log(tag, Texture.strippedCrimsonStem, Texture.strippedCrimsonStem);
      case "crimson_stem":
        return log(tag, Texture.crimsonStem, Texture.crimsonStemTop);
      case "stripped_crimson_stem":
        return log(tag, Texture.strippedCrimsonStem, Texture.strippedCrimsonStemTop);
      case "soul_fire_lantern": // 20w06a - 20w16a
      case "soul_lantern": // since 20w17a
        return new Lantern(
          name,
          Texture.soulFireLantern,
          tag.get("Properties").get("hanging").stringValue("false").equals("true"));
      case "twisting_vines":
        return new SpriteBlock("twisting_vines", Texture.twistingVines);
      case "twisting_vines_plant":
        return new SpriteBlock("twisting_vines_plant", Texture.twistingVinesPlant);
      case "weeping_vines":
        return new SpriteBlock("weeping_vines", Texture.weepingVines);
      case "weeping_vines_plant":
        return new SpriteBlock("weeping_vines_plant", Texture.weepingVinesPlant);
      case "soul_fire_torch": // 20w06a - 20w16a
      case "soul_torch": // since 20w17a
        return new Torch(name, Texture.soulFireTorch);
      case "soul_fire_wall_torch": // 20w06a - 20w16a
      case "soul_wall_torch": // since 20w17a
        return new WallTorch(name, Texture.soulFireTorch, BlockProvider.facing(tag));
      case "respawn_anchor":
        return new RespawnAnchor(
          BlockProvider.stringToInt(tag.get("Properties").get("charges"), 0));
      case "crimson_sign":
        return sign(tag, "crimson");
      case "crimson_wall_sign":
        return wallSign(tag, "crimson");
      case "warped_sign":
        return sign(tag, "warped");
      case "warped_wall_sign":
        return wallSign(tag, "warped");
      case "crimson_planks":
        return new MinecraftBlock(name, Texture.crimsonPlanks);
      case "warped_planks":
        return new MinecraftBlock(name, Texture.warpedPlanks);
      case "crimson_pressure_plate":
        return new PressurePlate(name, Texture.crimsonPlanks);
      case "warped_pressure_plate":
        return new PressurePlate(name, Texture.warpedPlanks);
      case "crimson_slab":
        return slab(tag, Texture.crimsonPlanks);
      case "warped_slab":
        return slab(tag, Texture.warpedPlanks);
      case "crimson_stairs":
        return stairs(tag, Texture.crimsonPlanks);
      case "warped_stairs":
        return stairs(tag, Texture.warpedPlanks);
      case "crimson_fence":
        return fence(tag, Texture.crimsonPlanks);
      case "warped_fence":
        return fence(tag, Texture.warpedPlanks);
      case "crimson_fence_gate":
        return fenceGate(tag, Texture.crimsonPlanks);
      case "warped_fence_gate":
        return fenceGate(tag, Texture.warpedPlanks);
      case "crimson_button":
        return button(tag, Texture.crimsonPlanks);
      case "warped_button":
        return button(tag, Texture.warpedPlanks);
      case "crimson_door":
        return door(tag, Texture.crimsonDoorTop, Texture.crimsonDoorBottom);
      case "warped_door":
        return door(tag, Texture.warpedDoorTop, Texture.warpedDoorBottom);
      case "crimson_trapdoor":
        return orientableTrapdoor(tag, Texture.crimsonTrapdoor);
      case "warped_trapdoor":
        return orientableTrapdoor(tag, Texture.warpedTrapdoor);
      case "soul_fire":
        return new SoulFire();
      case "lodestone":
        return new TexturedBlock("lodestone", Texture.lodestoneSide, Texture.lodestoneTop);
      case "blackstone":
        return new TexturedBlock("blackstone", Texture.blackstone, Texture.blackstoneTop);
      case "blackstone_slab":
        return slab(tag, Texture.blackstone, Texture.blackstoneTop);
      case "blackstone_stairs":
        return stairs(tag, Texture.blackstone, Texture.blackstoneTop, Texture.blackstoneTop);
      case "blackstone_wall":
        return wall(tag, Texture.blackstone);
      case "chiseled_nether_bricks":
        return new MinecraftBlock("chiseled_nether_bricks", Texture.chiseledNetherBricks);
      case "cracked_nether_bricks":
        return new MinecraftBlock("cracked_nether_bricks", Texture.crackedNetherBricks);
      case "gilded_blackstone":
        return new MinecraftBlock("gilded_blackstone", Texture.gildedBlackstone);
      case "soul_campfire":
        return new Campfire(
          "soul_campfire",
          se.llbit.chunky.entity.Campfire.Kind.SOUL_CAMPFIRE,
          BlockProvider.facing(tag),
          isLit(tag));
      case "polished_blackstone":
        return new MinecraftBlock("polished_blackstone", Texture.polishedBlackstone);
      case "polished_blackstone_slab":
        return slab(tag, Texture.polishedBlackstone);
      case "polished_blackstone_stairs":
        return stairs(tag, Texture.polishedBlackstone);
      case "polished_blackstone_wall":
        return wall(tag, Texture.polishedBlackstone);
      case "chiseled_polished_blackstone":
        return new MinecraftBlock(
          "chiseled_polished_blackstone", Texture.chiseledPolishedBlackstone);
      case "polished_blackstone_bricks":
        return new MinecraftBlock("polished_blackstone_bricks", Texture.polishedBlackstoneBricks);
      case "polished_blackstone_brick_slab":
        return slab(tag, Texture.polishedBlackstoneBricks);
      case "polished_blackstone_brick_stairs":
        return stairs(tag, Texture.polishedBlackstoneBricks);
      case "polished_blackstone_brick_wall":
        return wall(tag, Texture.polishedBlackstoneBricks);
      case "cracked_polished_blackstone_bricks":
        return new MinecraftBlock(
          "cracked_polished_blackstone_bricks", Texture.crackedPolishedBlackstoneBricks);
      case "polished_blackstone_button":
        return button(tag, Texture.polishedBlackstone);
      case "polished_blackstone_pressure_plate":
        return new PressurePlate(name, Texture.polishedBlackstone);
      case "quartz_bricks":
        return new MinecraftBlock(name, Texture.quartzBricks);
      case "chain":
        return chain(tag, "chain", Texture.chain);
      case "candle_cake":
        return candleCake(tag, Texture.candle, Texture.candleLit);
      case "white_candle_cake":
        return candleCake(tag, Texture.whiteCandle, Texture.whiteCandleLit);
      case "orange_candle_cake":
        return candleCake(tag, Texture.orangeCandle, Texture.orangeCandleLit);
      case "magenta_candle_cake":
        return candleCake(tag, Texture.magentaCandle, Texture.magentaCandleLit);
      case "light_blue_candle_cake":
        return candleCake(tag, Texture.lightBlueCandle, Texture.lightBlueCandleLit);
      case "yellow_candle_cake":
        return candleCake(tag, Texture.yellowCandle, Texture.yellowCandleLit);
      case "lime_candle_cake":
        return candleCake(tag, Texture.limeCandle, Texture.limeCandleLit);
      case "pink_candle_cake":
        return candleCake(tag, Texture.pinkCandle, Texture.pinkCandleLit);
      case "gray_candle_cake":
        return candleCake(tag, Texture.grayCandle, Texture.grayCandleLit);
      case "light_gray_candle_cake":
        return candleCake(tag, Texture.lightGrayCandle, Texture.lightGrayCandleLit);
      case "cyan_candle_cake":
        return candleCake(tag, Texture.cyanCandle, Texture.cyanCandleLit);
      case "purple_candle_cake":
        return candleCake(tag, Texture.purpleCandle, Texture.purpleCandleLit);
      case "blue_candle_cake":
        return candleCake(tag, Texture.blueCandle, Texture.blueCandleLit);
      case "brown_candle_cake":
        return candleCake(tag, Texture.brownCandle, Texture.brownCandleLit);
      case "green_candle_cake":
        return candleCake(tag, Texture.greenCandle, Texture.greenCandleLit);
      case "red_candle_cake":
        return candleCake(tag, Texture.redCandle, Texture.redCandleLit);
      case "black_candle_cake":
        return candleCake(tag, Texture.blackCandle, Texture.blackCandleLit);
      case "candle":
        return candle(tag, Texture.candle, Texture.candleLit);
      case "white_candle":
        return candle(tag, Texture.whiteCandle, Texture.whiteCandleLit);
      case "orange_candle":
        return candle(tag, Texture.orangeCandle, Texture.orangeCandleLit);
      case "magenta_candle":
        return candle(tag, Texture.magentaCandle, Texture.magentaCandleLit);
      case "light_blue_candle":
        return candle(tag, Texture.lightBlueCandle, Texture.lightBlueCandleLit);
      case "yellow_candle":
        return candle(tag, Texture.yellowCandle, Texture.yellowCandleLit);
      case "lime_candle":
        return candle(tag, Texture.limeCandle, Texture.limeCandleLit);
      case "pink_candle":
        return candle(tag, Texture.pinkCandle, Texture.pinkCandleLit);
      case "gray_candle":
        return candle(tag, Texture.grayCandle, Texture.grayCandleLit);
      case "light_gray_candle":
        return candle(tag, Texture.lightGrayCandle, Texture.lightGrayCandleLit);
      case "cyan_candle":
        return candle(tag, Texture.cyanCandle, Texture.cyanCandleLit);
      case "purple_candle":
        return candle(tag, Texture.purpleCandle, Texture.purpleCandleLit);
      case "blue_candle":
        return candle(tag, Texture.blueCandle, Texture.blueCandleLit);
      case "brown_candle":
        return candle(tag, Texture.brownCandle, Texture.brownCandleLit);
      case "green_candle":
        return candle(tag, Texture.greenCandle, Texture.greenCandleLit);
      case "red_candle":
        return candle(tag, Texture.redCandle, Texture.redCandleLit);
      case "black_candle":
        return candle(tag, Texture.blackCandle, Texture.blackCandleLit);
      case "copper_ore":
        return new MinecraftBlock("copper_ore", Texture.copperOre);
      case "calcite":
        return new MinecraftBlock("calcite", Texture.calcite);
      case "tuff":
        return new MinecraftBlock("tuff", Texture.tuff);
      case "amethyst_block":
        return new MinecraftBlock("amethyst_block", Texture.amethyst);
      case "budding_amethyst":
        return new MinecraftBlock("budding_amethyst", Texture.buddingAmethyst);
      case "copper_block":
      case "waxed_copper_block":
        return new MinecraftBlock(name, Texture.copperBlock);
      case "exposed_copper":
      case "waxed_exposed_copper":
        return new MinecraftBlock(name, Texture.exposedCopper);
      case "weathered_copper":
      case "waxed_weathered_copper":
        return new MinecraftBlock(name, Texture.weatheredCopper);
      case "oxidized_copper":
      case "waxed_oxidized_copper":
        return new MinecraftBlock(name, Texture.oxidizedCopper);
      case "cut_copper":
      case "waxed_cut_copper":
        return new MinecraftBlock(name, Texture.cutCopper);
      case "exposed_cut_copper":
      case "waxed_exposed_cut_copper":
        return new MinecraftBlock(name, Texture.exposedCutCopper);
      case "weathered_cut_copper":
      case "waxed_weathered_cut_copper":
        return new MinecraftBlock(name, Texture.weatheredCutCopper);
      case "oxidized_cut_copper":
      case "waxed_oxidized_cut_copper":
        return new MinecraftBlock(name, Texture.oxidizedCutCopper);
      case "cut_copper_stairs":
      case "waxed_cut_copper_stairs":
        return stairs(tag, Texture.cutCopper);
      case "exposed_cut_copper_stairs":
      case "waxed_exposed_cut_copper_stairs":
        return stairs(tag, Texture.exposedCutCopper);
      case "weathered_cut_copper_stairs":
      case "waxed_weathered_cut_copper_stairs":
        return stairs(tag, Texture.weatheredCutCopper);
      case "oxidized_cut_copper_stairs":
      case "waxed_oxidized_cut_copper_stairs":
        return stairs(tag, Texture.oxidizedCutCopper);
      case "cut_copper_slab":
      case "waxed_cut_copper_slab":
        return slab(tag, Texture.cutCopper);
      case "exposed_cut_copper_slab":
      case "waxed_exposed_cut_copper_slab":
        return slab(tag, Texture.exposedCutCopper);
      case "weathered_cut_copper_slab":
      case "waxed_weathered_cut_copper_slab":
        return slab(tag, Texture.weatheredCutCopper);
      case "oxidized_cut_copper_slab":
      case "waxed_oxidized_cut_copper_slab":
        return slab(tag, Texture.oxidizedCutCopper);
      case "lava_cauldron":
        return new LavaCauldron();
      case "lightning_rod":
        return new LightningRod(BlockProvider.facing(tag, "up"), tag.get("Properties").get("powered").stringValue("false").equals("true"));
      case "small_amethyst_bud":
        return new SpriteBlock(name, Texture.smallAmethystBud, BlockProvider.facing(tag, "up"));
      case "medium_amethyst_bud":
        return new SpriteBlock(name, Texture.mediumAmethystBud, BlockProvider.facing(tag, "up"));
      case "large_amethyst_bud":
        return new SpriteBlock(name, Texture.largeAmethystBud, BlockProvider.facing(tag, "up"));
      case "amethyst_cluster":
        return new SpriteBlock(name, Texture.amethystCluster, BlockProvider.facing(tag, "up"));
      case "tinted_glass":
        return new TintedGlass();
      case "powder_snow":
        return new MinecraftBlock(name, Texture.powderSnow);
      case "powder_snow_cauldron":
        return new PowderSnowCauldron(
          BlockProvider.stringToInt(tag.get("Properties").get("level"), 3));
      case "dripstone_block":
        return new MinecraftBlock(name, Texture.dripstoneBlock);
      case "pointed_dripstone":
        return new PointedDripstone(
          tag.get("Properties").get("thickness").stringValue("tip"),
          tag.get("Properties").get("vertical_direction").stringValue("up"),
          tag.get("Properties").get("waterlogged").stringValue("").equals("true"));
      case "sculk_sensor":
        return new SculkSensor(tag.get("Properties").get("sculk_sensor_phase").stringValue("cooldown"));
      case "glow_lichen":
        return new GlowLichen(
          tag.get("Properties").get("north").stringValue("false").equals("true"),
          tag.get("Properties").get("south").stringValue("false").equals("true"),
          tag.get("Properties").get("east").stringValue("false").equals("true"),
          tag.get("Properties").get("west").stringValue("false").equals("true"),
          tag.get("Properties").get("up").stringValue("false").equals("true"),
          tag.get("Properties").get("down").stringValue("false").equals("true")
        );
      case "azalea":
        return new Azalea(name, Texture.azaleaTop, Texture.azaleaSide);
      case "flowering_azalea":
        return new Azalea(name, Texture.floweringAzaleaTop, Texture.floweringAzaleaSide);
      case "azalea_leaves":
        return new UntintedLeaves(name, Texture.azaleaLeaves);
      case "flowering_azalea_leaves":
        return new UntintedLeaves(name, Texture.floweringAzaleaLeaves);
      case "moss_block":
        return new MinecraftBlock(name, Texture.mossBlock);
      case "moss_carpet":
        return new Carpet(name, Texture.mossBlock);
      case "cave_vines_plant":
        return new CaveVines(name, tag.get("Properties").get("berries").stringValue("false").equals("true"), true);
      case "cave_vines":
        return new CaveVines(name, tag.get("Properties").get("berries").stringValue("false").equals("true"), false);
      case "hanging_roots":
        return new SpriteBlock(name, Texture.hangingRoots);
      case "rooted_dirt":
        return new MinecraftBlock(name, Texture.rootedDirt);
      case "big_dripleaf_stem":
        return new BigDripleafStem(BlockProvider.facing(tag, "north"));
      case "big_dripleaf":
        return new BigDripleaf(BlockProvider.facing(tag, "north"), tag.get("Properties").get("tilt").stringValue("none"));
      case "small_dripleaf":
        return new SmallDripleaf(BlockProvider.facing(tag, "north"), tag.get("Properties").get("half").stringValue("lower"));
      case "spore_blossom":
        return new SporeBlossom();
      case "deepslate":
      case "infested_deepslate":
        return log(tag, Texture.deepslate, Texture.deepslateTop);
      case "deepslate_gold_ore":
        return new MinecraftBlock(name, Texture.deepslateGoldOre);
      case "deepslate_iron_ore":
        return new MinecraftBlock(name, Texture.deepslateIronOre);
      case "deepslate_diamond_ore":
        return new MinecraftBlock(name, Texture.deepslateDiamondOre);
      case "deepslate_lapis_ore":
        return new MinecraftBlock(name, Texture.deepslateLapisOre);
      case "deepslate_redstone_ore":
        return new MinecraftBlock(name, Texture.deepslateRedstoneOre);
      case "cobbled_deepslate":
        return new MinecraftBlock(name, Texture.cobbledDeepslate);
      case "cobbled_deepslate_wall":
        return wall(tag, Texture.cobbledDeepslate);
      case "cobbled_deepslate_slab":
        return slab(tag, Texture.cobbledDeepslate);
      case "cobbled_deepslate_stairs":
        return stairs(tag, Texture.cobbledDeepslate);
      case "polished_deepslate":
        return new MinecraftBlock(name, Texture.polishedDeepslate);
      case "polished_deepslate_wall":
        return wall(tag, Texture.polishedDeepslate);
      case "polished_deepslate_slab":
        return slab(tag, Texture.polishedDeepslate);
      case "polished_deepslate_stairs":
        return stairs(tag, Texture.polishedDeepslate);
      case "deepslate_bricks":
        return new MinecraftBlock(name, Texture.deepslateBricks);
      case "deepslate_brick_wall":
        return wall(tag, Texture.deepslateBricks);
      case "deepslate_brick_slab":
        return slab(tag, Texture.deepslateBricks);
      case "deepslate_brick_stairs":
        return stairs(tag, Texture.deepslateBricks);
      case "deepslate_tiles":
        return new MinecraftBlock(name, Texture.deepslateTiles);
      case "deepslate_tile_wall":
        return wall(tag, Texture.deepslateTiles);
      case "deepslate_tile_slab":
        return slab(tag, Texture.deepslateTiles);
      case "deepslate_tile_stairs":
        return stairs(tag, Texture.deepslateTiles);
      case "chiseled_deepslate":
        return new MinecraftBlock(name, Texture.chiseledDeepslate);
      case "smooth_basalt":
        return new MinecraftBlock(name, Texture.smoothBasalt);
      case "cracked_deepslate_bricks":
        return new MinecraftBlock(name, Texture.crackedDeepslateBricks);
      case "cracked_deepslate_tiles":
        return new MinecraftBlock(name, Texture.crackedDeepslateTiles);
      case "deepslate_coal_ore":
        return new MinecraftBlock(name, Texture.deepslateCoalOre);
      case "deepslate_copper_ore":
        return new MinecraftBlock(name, Texture.deepslateCopperOre);
      case "deepslate_emerald_ore":
        return new MinecraftBlock(name, Texture.deepslateEmeraldOre);
      case "light":
        return new LightBlock(name, BlockProvider.stringToInt(tag.get("Properties").get("level"), 15));
      case "raw_copper_block":
        return new MinecraftBlock(name, Texture.rawCopperBlock);
      case "raw_gold_block":
        return new MinecraftBlock(name, Texture.rawGoldBlock);
      case "raw_iron_block":
        return new MinecraftBlock(name, Texture.rawIronBlock);
      case "piglin_head":
        return skull(tag, Texture.piglin, SkullEntity.Kind.PIGLIN);
      case "piglin_wall_head":
        return wallSkull(tag, Texture.piglin, SkullEntity.Kind.PIGLIN);
      default:
        BiFunction<String, Tag, Block> factory = blocks.get(namespacedName);
        if (factory != null) {
          return factory.apply(name, tag);
        }
        return null;
    }
  }

  public static Block largeFlower(Tag tag, Texture top, Texture bottom) {
    String name = BlockProvider.blockName(tag);
    String half = tag.get("Properties").get("half").stringValue("lower");
    return new SpriteBlock(name, half.equals("upper") ? top : bottom);
  }

  public static Block log(Tag tag, Texture side, Texture top) {
    String name = BlockProvider.blockName(tag);
    String axis = tag.get("Properties").get("axis").stringValue("y");
    return new Log(name, side, top, axis);
  }

  public static Block slab(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    String type = tag.get("Properties").get("type").stringValue("bottom");
    return new Slab(name, texture, type);
  }

  public static Block slab(Tag tag, Texture sideTexture, Texture topTexture) {
    String name = BlockProvider.blockName(tag);
    String type = tag.get("Properties").get("type").stringValue("bottom");
    return new Slab(name, sideTexture, topTexture, type);
  }

  public static Block stairs(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String half = properties.get("half").stringValue("bottom");
    String shape = properties.get("shape").stringValue("straight");
    String facing = BlockProvider.facing(tag, "south");
    return new Stairs(name, texture, half, shape, facing);
  }

  public static Block stairs(Tag tag, Texture side, Texture top, Texture bottom) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String half = properties.get("half").stringValue("bottom");
    String shape = properties.get("shape").stringValue("straight");
    String facing = BlockProvider.facing(tag, "south");
    return new Stairs(name, side, top, bottom, half, shape, facing);
  }

  private static Block glazedTerracotta(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag, "south");
    return new GlazedTerracotta(name, texture, facing);
  }

  private static Block bed(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    String part = tag.get("Properties").get("part").stringValue("head");
    String facing = BlockProvider.facing(tag, "south");
    return new Bed(name, texture, part, facing);
  }

  private static Block hugeMushroom(Tag tag, Texture skin) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String east = properties.get("east").stringValue("true");
    String west = properties.get("west").stringValue("true");
    String north = properties.get("north").stringValue("true");
    String south = properties.get("south").stringValue("true");
    String up = properties.get("up").stringValue("true");
    String down = properties.get("down").stringValue("true");
    return new TexturedBlock(
      name,
      north.equals("true") ? skin : Texture.mushroomPores,
      south.equals("true") ? skin : Texture.mushroomPores,
      east.equals("true") ? skin : Texture.mushroomPores,
      west.equals("true") ? skin : Texture.mushroomPores,
      up.equals("true") ? skin : Texture.mushroomPores,
      down.equals("true") ? skin : Texture.mushroomPores);
  }

  private static Block snowCovered(Tag tag, Block block) {
    String snowy = tag.get("Properties").get("snowy").stringValue("false");
    if (snowy.equals("true")) {
      block = new TexturedBlock(block.name, Texture.snowSide, Texture.snowBlock, Texture.dirt);
    }
    return block;
  }

  private static Block piston(Tag tag, boolean isSticky) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String extended = properties.get("extended").stringValue("false");
    String facing = BlockProvider.facing(tag);
    return new Piston(name, isSticky, extended.equals("true"), facing);
  }

  private static Block pistonHead(Tag tag) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    String type = properties.get("type").stringValue("normal");
    return new PistonHead(name, type.equals("sticky"), facing);
  }

  private static Block rail(Tag tag, Texture straightTrack) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String shape = properties.get("shape").stringValue("north-south");
    return new Rail(name, straightTrack, shape);
  }

  private static Block poweredRail(Tag tag) {
    Tag properties = tag.get("Properties");
    String powered = properties.get("powered").stringValue("false");
    Texture straightTrack = powered.equals("true") ? Texture.poweredRailOn : Texture.poweredRailOff;
    return rail(tag, straightTrack);
  }

  private static boolean isLit(Tag tag) {
    return tag.get("Properties").get("lit").stringValue("false").equals("true");
  }

  private static boolean isLit(Tag tag, boolean defaultValue) {
    return tag.get("Properties").get("lit").stringValue(Boolean.toString(defaultValue))
      .equals("true");
  }

  private static Block redstoneWire(Tag tag) {
    Tag properties = tag.get("Properties");
    String north = properties.get("north").stringValue("none");
    String south = properties.get("south").stringValue("none");
    String east = properties.get("east").stringValue("none");
    String west = properties.get("west").stringValue("none");
    int power = BlockProvider.stringToInt(properties.get("power"), 0);
    return new RedstoneWire(power, north, south, east, west);
  }

  private static Block chest(Tag tag, boolean trapped) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag, "north");
    String type = properties.get("type").stringValue("single");
    return new Chest(name, type, facing, trapped);
  }

  private static Block chain(Tag tag, String name, Texture texture) {
    String axis = tag.get("Properties").get("axis").stringValue("y");
    return new Chain(name, texture, axis);
  }

  private static Block endRod(Tag tag) {
    String facing = BlockProvider.facing(tag, "up");
    return new EndRod(facing);
  }

  private static Block furnace(Tag tag) {
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    String lit = properties.get("lit").stringValue("false");
    return new Furnace(facing, lit.equals("true"));
  }

  private static Block smoker(Tag tag) {
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    String lit = properties.get("lit").stringValue("false");
    return new Smoker(
      facing,
      lit.equals("true"));
  }

  private static Block blastFurnace(Tag tag) {
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    String lit = properties.get("lit").stringValue("false");
    return new BlastFurnace(
      facing,
      lit.equals("true"));
  }

  private static Block composter(Tag tag) {
    Tag properties = tag.get("Properties");
    int level = BlockProvider.stringToInt(properties.get("level"), 0);
    return new Composter(level);
  }

  private static Block bamboo(Tag tag) {
    Tag properties = tag.get("Properties");
    int age = BlockProvider.stringToInt(properties.get("age"), 0);
    String leaves = properties.get("leaves").stringValue("none");
    return new Bamboo(age, leaves);
  }

  private static Block beeNest(Tag tag) {
    Tag properties = tag.get("Properties");
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag);
    int honeyLevel = BlockProvider.stringToInt(properties.get("honey_level"), 0);
    return new TopBottomOrientedTexturedBlock(
      name,
      facing,
      honeyLevel == 5 ? Texture.beeNestFrontHoney : Texture.beeNestFront,
      Texture.beeNestSide,
      Texture.beeNestTop,
      Texture.beeNestBottom);
  }

  private static Block beehive(Tag tag) {
    Tag properties = tag.get("Properties");
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag);
    int honeyLevel = BlockProvider.stringToInt(properties.get("honey_level"), 0);
    return new TopBottomOrientedTexturedBlock(
      name,
      facing,
      honeyLevel == 5 ? Texture.beehiveFrontHoney : Texture.beehiveFront,
      Texture.beehiveSide,
      Texture.beehiveEnd,
      Texture.beehiveEnd);
  }

  private static Block door(Tag tag, Texture upper, Texture lower) {
    Tag properties = tag.get("Properties");
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag);
    String half = properties.get("half").stringValue("upper");
    String hinge = properties.get("hinge").stringValue("right");
    String open = properties.get("open").stringValue("false");
    return new Door(
      name, half.equals("upper") ? upper : lower, facing, half, hinge, open.equals("true"));
  }

  private static Block shulkerBox(Tag tag, ShulkerTexture texture) {
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag, "up");
    return new ShulkerBox(name, texture.side, texture.top, texture.bottom, facing);
  }

  private static Block sign(Tag tag, String material) {
    String name = BlockProvider.blockName(tag);
    int rotation = BlockProvider.stringToInt(tag.get("Properties").get("rotation"), 0);
    return new Sign(name, material, rotation);
  }

  private static Block hangingSign(Tag tag, String material) {
    String name = BlockProvider.blockName(tag);
    int rotation = BlockProvider.stringToInt(tag.get("Properties").get("rotation"), 0);
    boolean attached = tag.get("Properties").get("attached").stringValue("false").equals("true");
    return new HangingSign(name, material, rotation, attached);
  }

  private static Block wallHangingSign(Tag tag, String material) {
    return new WallHangingSign(BlockProvider.blockName(tag), material, BlockProvider.facing(tag));
  }

  private static Block banner(Tag tag, Texture texture, BannerDesign.Color color) {
    String name = BlockProvider.blockName(tag);
    int rotation = BlockProvider.stringToInt(tag.get("Properties").get("rotation"), 0);
    return new Banner(name, texture, rotation, color);
  }

  private static Block wallBanner(Tag tag, Texture texture, BannerDesign.Color color) {
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag);
    return new WallBanner(name, texture, facing, color);
  }

  private static Block wallSign(Tag tag, String material) {
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag);
    return new WallSign(name, material, facing);
  }

  private static Block lever(Tag tag) {
    Tag properties = tag.get("Properties");
    String face = properties.get("face").stringValue("floor");
    String facing = BlockProvider.facing(tag);
    String powered = properties.get("powered").stringValue("false");
    return new Lever(face, facing, powered.equals("true"));
  }

  private static Block button(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String face = properties.get("face").stringValue("floor");
    String facing = BlockProvider.facing(tag);
    String powered = properties.get("powered").stringValue("false");
    return new Button(name, texture, face, facing, powered.equals("true"));
  }

  private static Block repeater(Tag tag) {
    Tag properties = tag.get("Properties");
    int delay = BlockProvider.stringToInt(properties.get("delay"), 1);
    String facing = BlockProvider.facing(tag);
    String powered = properties.get("powered").stringValue("false");
    String locked = properties.get("locked").stringValue("false");
    return new Repeater(delay, facing, powered.equals("true"), locked.equals("true"));
  }

  private static Block comparator(Tag tag) {
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    String powered = properties.get("powered").stringValue("false");
    String mode = properties.get("mode").stringValue("compare");
    return new Comparator(facing, mode, powered.equals("true"));
  }

  private static Block fence(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String north = properties.get("north").stringValue("false");
    String south = properties.get("south").stringValue("false");
    String east = properties.get("east").stringValue("false");
    String west = properties.get("west").stringValue("false");
    return new Fence(
      name,
      texture,
      north.equals("true"),
      south.equals("true"),
      east.equals("true"),
      west.equals("true"));
  }

  private static Block fenceGate(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    String in_wall = properties.get("in_wall").stringValue("false");
    String open = properties.get("open").stringValue("false");
    return new FenceGate(name, texture, facing, in_wall.equals("true"), open.equals("true"));
  }

  private Block glassPane(Tag tag, Texture side, Texture top) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String north = properties.get("north").stringValue("false");
    String south = properties.get("south").stringValue("false");
    String east = properties.get("east").stringValue("false");
    String west = properties.get("west").stringValue("false");
    return new GlassPane(
      name,
      side,
      top,
      north.equals("true"),
      south.equals("true"),
      east.equals("true"),
      west.equals("true"));
  }

  private Block ironBars(Tag tag) {
    Tag properties = tag.get("Properties");
    String north = properties.get("north").stringValue("false");
    String south = properties.get("south").stringValue("false");
    String east = properties.get("east").stringValue("false");
    String west = properties.get("west").stringValue("false");
    return new IronBars(
      north.equals("true"), south.equals("true"), east.equals("true"), west.equals("true"));
  }

  private static Block trapdoor(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String half = properties.get("half").stringValue("bottom");
    String facing = BlockProvider.facing(tag);
    String open = properties.get("open").stringValue("false");
    return new Trapdoor(name, texture, half, facing, open.equals("true"));
  }

  private static Block orientableTrapdoor(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String half = properties.get("half").stringValue("bottom");
    String facing = BlockProvider.facing(tag);
    String open = properties.get("open").stringValue("false");
    return new OrientableTrapdoor(name, texture, half, facing, open.equals("true"));
  }

  private Block vine(Tag tag) {
    Tag properties = tag.get("Properties");
    String north = properties.get("north").stringValue("false");
    String south = properties.get("south").stringValue("false");
    String east = properties.get("east").stringValue("false");
    String west = properties.get("west").stringValue("false");
    String up = properties.get("up").stringValue("false");
    return new Vine(
      north.equals("true"),
      south.equals("true"),
      east.equals("true"),
      west.equals("true"),
      up.equals("true"));
  }

  private Block tripwire(Tag tag) {
    Tag properties = tag.get("Properties");
    String north = properties.get("north").stringValue("false");
    String south = properties.get("south").stringValue("false");
    String east = properties.get("east").stringValue("false");
    String west = properties.get("west").stringValue("false");
    return new Tripwire(
      north.equals("true"), south.equals("true"), east.equals("true"), west.equals("true"));
  }

  private Block tripwireHook(Tag tag) {
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    String attached = properties.get("attached").stringValue("false");
    String powered = properties.get("powered").stringValue("false");
    return new TripwireHook(facing, attached.equals("true"), powered.equals("true"));
  }

  private Block cocoa(Tag tag) {
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    int age = BlockProvider.stringToInt(properties.get("age"), 2);
    return new Cocoa(facing, age);
  }

  private static Block wall(Tag tag, Texture texture) {
    String name = BlockProvider.blockName(tag);
    Tag properties = tag.get("Properties");
    String north = properties.get("north").stringValue("false");
    String south = properties.get("south").stringValue("false");
    String east = properties.get("east").stringValue("false");
    String west = properties.get("west").stringValue("false");
    String up = properties.get("up").stringValue("false");
    return new Wall(name, texture, north, south, east, west, up.equals("true"));
  }

  private Block skull(Tag tag, Texture texture, SkullEntity.Kind type) {
    String name = BlockProvider.blockName(tag);
    int rotation = BlockProvider.stringToInt(tag.get("Properties").get("rotation"), 0);
    return new Head(name, texture, type, rotation);
  }

  private Block wallSkull(Tag tag, Texture texture, SkullEntity.Kind type) {
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag);
    return new WallHead(name, texture, type, facing);
  }

  private Block anvil(Tag tag, int damage) {
    String name = BlockProvider.blockName(tag);
    String facing = BlockProvider.facing(tag);
    return new Anvil(name, facing, damage);
  }

  private static Block turtleEgg(Tag tag) {
    Tag properties = tag.get("Properties");
    int eggs = BlockProvider.stringToInt(properties.get("eggs"), 1);
    int hatch = BlockProvider.stringToInt(properties.get("hatch"), 0);
    return new TurtleEgg(eggs, hatch);
  }

  private static Block seaPickle(Tag tag) {
    Tag properties = tag.get("Properties");
    int pickles = BlockProvider.stringToInt(properties.get("pickles"), 1);
    return new SeaPickle(pickles, properties.get("waterlogged").stringValue("").equals("true"));
  }

  private static Block structureBlock(Tag tag) {
    Tag properties = tag.get("Properties");
    String mode = properties.get("mode").stringValue("");
    Texture texture = switch (mode) {
      case "corner" -> Texture.structureBlockCorner;
      case "data" -> Texture.structureBlockData;
      case "load" -> Texture.structureBlockLoad;
      case "save" -> Texture.structureBlockSave;
      default -> Texture.structureBlock;
    };
    return new MinecraftBlock("structure_block", texture);
  }

  private static Block candle(Tag tag, Texture candleTexture, Texture candleTextureLit) {
    Tag properties = tag.get("Properties");
    return new Candle(BlockProvider.blockName(tag), candleTexture, candleTextureLit,
      BlockProvider.stringToInt(properties.get("candles"), 1),
      properties.get("lit").stringValue("false").equals("true"));
  }

  private static Block candleCake(Tag tag, Texture candleTexture, Texture candleTextureLit) {
    Tag properties = tag.get("Properties");
    return new CakeWithCandle(BlockProvider.blockName(tag), candleTexture, candleTextureLit,
      properties.get("lit").stringValue("false").equals("true"));
  }

  private static Block suspiciousSand(Tag tag) {
    Tag properties = tag.get("Properties");
    String dusted = properties.get("dusted").stringValue("0");
    return switch (dusted) {
      case "1" -> new MinecraftBlock("suspicious_sand", Texture.suspiciousSandStage1);
      case "2" -> new MinecraftBlock("suspicious_sand", Texture.suspiciousSandStage2);
      case "3" -> new MinecraftBlock("suspicious_sand", Texture.suspiciousSandStage3);
      default -> new MinecraftBlock("suspicious_sand", Texture.suspiciousSandStage0);
    };
  }

  private static Block suspiciousGravel(Tag tag) {
    Tag properties = tag.get("Properties");
    String dusted = properties.get("dusted").stringValue("0");
    return switch (dusted) {
      case "1" -> new MinecraftBlock("suspicious_gravel", Texture.suspiciousGravelStage1);
      case "2" -> new MinecraftBlock("suspicious_gravel", Texture.suspiciousGravelStage2);
      case "3" -> new MinecraftBlock("suspicious_gravel", Texture.suspiciousGravelStage3);
      default -> new MinecraftBlock("suspicious_gravel", Texture.suspiciousGravelStage0);
    };
  }

  private static Block decoratedPot(Tag tag) {
    Tag properties = tag.get("Properties");
    String facing = BlockProvider.facing(tag);
    boolean waterlogged = properties.get("waterlogged").stringValue("").equals("true");
    String[] sherds = new String[4];
    ListTag sherdTags = properties.get("blockEntity#sherds").asList();
    for (int i = 0; i < sherdTags.size() && i < 4; i++) {
      String sherd = sherdTags.get(i).stringValue();
      if (!sherd.equals("minecraft:brick")) sherds[i] = sherd;
    }
    return new DecoratedPot(
      facing,
      waterlogged,
      sherds
    );
  }

  private static Block nonSolid(Block block) {
    block.solid = false;
    return block;
  }

  private static Block solid(Block block) {
    block.solid = true;
    return block;
  }
}
