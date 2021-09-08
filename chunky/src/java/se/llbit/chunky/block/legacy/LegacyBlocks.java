package se.llbit.chunky.block.legacy;

import se.llbit.chunky.block.legacy.blocks.LegacyStairs;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

public class LegacyBlocks {

  /** statically initialised array of every possible tag */
  private static final Tag[] legacyTags = new Tag[(1 << 8) * (1 << 4)];

  static {
    for (int id = 0; id < 1 << 8; id++) { //id is 8 bits
      for (int data = 0; data < 1 << 4; data++) { //data is 4 bits
        legacyTags[id * (1 << 4) + data] = getTag(id, data);
      }
    }
  }

  public static Tag getTag(int offset, byte[] blocks, byte[] blockData) {
    int id = blocks[offset] & 0xFF;
    int data = 0xFF & blockData[offset / 2];
    data >>= (offset % 2) * 4;
    data &= 0xF;

    return legacyTags[id * (1 << 4) + data];
  }

  private static Tag getTag(int id, int data) {
    CompoundTag tag = new CompoundTag();
    switch (id) {
      case 0:   return nameTag(tag, "air");
      case 2:   return needsFinalization(nameTag(tag, "grass_block"), id, data);
      case 4:   return nameTag(tag, "cobblestone");
      case 7:   return nameTag(tag, "bedrock");
      case 8:
      case 9:   return intTag(nameTag(tag, "water"), "level", data & 0b111);
      case 10:
      case 11:  return intTag(nameTag(tag, "lava"), "level", data & 0b111);
      case 13:  return nameTag(tag, "gravel");
      case 14:  return nameTag(tag, "gold_ore");
      case 15:  return nameTag(tag, "iron_ore");
      case 16:  return nameTag(tag, "coal_ore");
      case 20:  return nameTag(tag, "glass");
      case 21:  return nameTag(tag, "lapis_ore");
      case 22:  return nameTag(tag, "lapis_block");
      case 23:  return facingTag(nameTag(tag, "dispenser"), data);
      case 25:  return nameTag(tag, "note_block");
      case 26:  return needsFinalization(stringTag(facing4Tag(nameTag(tag, "red_bed"), data&3),
          "part", (data&8) != 0 ? "head" : "foot"), id, data);
      case 27:  return utilityRailTag(nameTag(tag, "powered_rail"), data);
      case 28:  return utilityRailTag(nameTag(tag, "detector_rail"), data);
      case 29:  return pistonTag(nameTag(tag, "sticky_piston"), data);
      case 30:  return nameTag(tag, "cobweb");
      case 32:  return nameTag(tag, "dead_bush");
      case 33:  return pistonTag(nameTag(tag, "piston"), data);
      case 34:  return stringTag(facingTag(nameTag(tag, "piston_head"), data&7),
          "type", (data&8) != 0 ? "sticky" : "normal");
      // case 36: // unused
      case 37:  return nameTag(tag, "dandelion");
      case 39:  return nameTag(tag, "brown_mushroom");
      case 40:  return nameTag(tag, "red_mushroom");
      case 41:  return nameTag(tag, "gold_block");
      case 42:  return nameTag(tag, "iron_block");
      case 45:  return nameTag(tag, "bricks");
      case 46:  return nameTag(tag, "tnt");
      case 47:  return nameTag(tag, "bookshelf");
      case 48:  return nameTag(tag, "mossy_cobblestone");
      case 49:  return nameTag(tag, "obsidian");
      case 51:  return needsFinalization(nameTag(tag, "fire"), id, data);
      case 52:  return nameTag(tag, "spawner");
      case 53:  return needsFinalization(stairsTag(nameTag(tag, "oak_stairs"), data), id, data);
      case 54:  return needsFinalization(chestFurnaceLadderTag(nameTag(tag, "chest"), data), id, data);
      case 55:  return needsFinalization(intTag(nameTag(tag, "redstone_wire"), "power", data), id, data);
      case 56:  return nameTag(tag, "diamond_ore");
      case 57:  return nameTag(tag, "diamond_block");
      case 58:  return nameTag(tag, "crafting_table");
      case 59:  return intTag(nameTag(tag, "wheat"), "age", data&7);
      case 60:  return intTag(nameTag(tag, "farmland"), "moisture", data&7);
      case 61:  return litTag(facingTag(nameTag(tag, "furnace"), data), false);
      case 62:  return litTag(facingTag(nameTag(tag, "furnace"), data), true);
      case 63:  return intTag(nameTag(tag, "oak_sign"), "rotation", data);
      case 64:  return needsFinalization(nameTag(tag, "oak_door"), id, data);
      case 65:  return chestFurnaceLadderTag(nameTag(tag, "ladder"), data);
      case 67:  return needsFinalization(stairsTag(nameTag(tag, "cobblestone_stairs"), data), id, data);
      case 68:  return wallSignTag(nameTag(tag, "oak_wall_sign"), data);
      case 70:  return nameTag(tag, "stone_pressure_plate");
      case 71:  return needsFinalization(nameTag(tag, "iron_door"), id, data);
      case 72:  return nameTag(tag, "oak_pressure_plate");
      case 73:  return litTag(nameTag(tag, "redstone_ore"), false);
      case 74:  return litTag(nameTag(tag, "redstone_ore"), true);
      case 75:  return redstoneTorchTag(tag, data, false);
      case 76:  return redstoneTorchTag(tag, data, true);
      case 77:  return buttonTag(nameTag(tag, "stone_button"), data);
      case 78:  return intTag(nameTag(tag, "snow"), "layers", (data&7) + 1);
      case 79:  return nameTag(tag, "ice");
      case 80:  return nameTag(tag, "snow_block");
      case 81:  return nameTag(tag, "cactus");
      case 82:  return nameTag(tag, "clay");
      case 83:  return nameTag(tag, "sugar_cane");
      case 84:  return nameTag(tag, "jukebox");
      case 85:  return needsFinalization(nameTag(tag, "oak_fence"), id, data);
      case 86:  return facing4Tag(nameTag(tag, "carved_pumpkin"), data);
      case 87:  return nameTag(tag, "netherrack");
      case 88:  return nameTag(tag, "soul_sand");
      case 89:  return nameTag(tag, "glowstone");
      case 90:
        nameTag(tag, "nether_portal");
        switch (data & 0b11) {
          default:
          case 0: // 1.6.4 or older, orientation based on neighboring blocks
            return needsFinalization(tag, id, data);
          case 1:
            return stringTag(tag, "axis", "x");
          case 2:
            return stringTag(tag, "axis", "z");
        }
      case 91:  return facing4Tag(nameTag(tag, "jack_o_lantern"), data);
      case 92:  return intTag(nameTag(tag, "cake"), "bites", data % 7);
      case 93:  return repeaterTag(nameTag(tag, "repeater"), data, false);
      case 94:  return repeaterTag(nameTag(tag, "repeater"), data, true);
      case 96:  return trapdoorTag(nameTag(tag, "oak_trapdoor"), data);
      case 99:  return mushroomTag(tag, data, false);
      case 100: return mushroomTag(tag, data, true);
      case 101: return needsFinalization(nameTag(tag, "iron_bars"), id, data);
      case 102: return needsFinalization(nameTag(tag, "glass_pane"), id, data);
      case 103: return nameTag(tag, "melon");
      case 104: return needsFinalization(intTag(nameTag(tag, "pumpkin_stem"), "age", data&7), id, data);
      case 105: return needsFinalization(intTag(nameTag(tag, "melon_stem"), "age", data&7), id, data);
      case 106: return needsFinalization(vineTag(nameTag(tag, "vine"), data, false), id, data);
      case 107: return needsFinalization(fenceGate(nameTag(tag, "oak_fence_gate"), data), id, data);
      case 108: return needsFinalization(stairsTag(nameTag(tag, "brick_stairs"), data), id, data);
      case 109: return needsFinalization(stairsTag(nameTag(tag, "stone_brick_stairs"), data), id, data);
      case 110: return needsFinalization(nameTag(tag, "mycelium"), id, data);
      case 111: return nameTag(tag, "lily_pad");
      case 112: return nameTag(tag, "nether_bricks");
      case 113: return needsFinalization(nameTag(tag, "nether_brick_fence"), id, data);
      case 114: return needsFinalization(stairsTag(nameTag(tag, "nether_brick_stairs"), data), id, data);
      case 115: return intTag(nameTag(tag, "nether_wart"), "age", data & 0b11);
      case 116: return nameTag(tag, "enchanting_table");
      case 117: return nameTag(tag, "brewing_stand");
      case 118: return intTag(nameTag(tag, "cauldron"), "level", data&3);
      case 119: return nameTag(tag, "end_portal");
      case 120:
        nameTag(tag, "end_portal_frame");
        boolTag(tag, "eye", (data&4) != 0);
        facing4Tag(tag, data&3);
        return tag;
      case 121: return nameTag(tag, "end_stone");
      case 122: return nameTag(tag, "dragon_egg");
      case 123: return stringTag(nameTag(tag, "redstone_lamp"), "lit", "false");
      case 124: return stringTag(nameTag(tag, "redstone_lamp"), "lit", "true");
      case 127: return intTag(facing4Tag(nameTag(tag, "cocoa"), data & 0b11), "age", (data & 0b1100) >> 2);
      case 128: return needsFinalization(stairsTag(nameTag(tag, "sandstone_stairs"), data), id, data);
      case 129: return nameTag(tag, "emerald_ore");
      case 130: return chestFurnaceLadderTag(nameTag(tag, "ender_chest"), data);
      case 131:
        nameTag(tag, "tripwire_hook");
        facing4Tag(tag, data & 0b0011);
        boolTag(tag, "attached", (data & 0b0100) != 0);
        boolTag(tag, "powered", (data & 0b1000) != 0);
        return tag;
      case 132: return needsFinalization(nameTag(tag, "tripwire"), id, data);
      case 133: return nameTag(tag, "emerald_block");
      case 134: return needsFinalization(stairsTag(nameTag(tag, "spruce_stairs"), data), id, data);
      case 135: return needsFinalization(stairsTag(nameTag(tag, "birch_stairs"), data), id, data);
      case 136: return needsFinalization(stairsTag(nameTag(tag, "jungle_stairs"), data), id, data);
      case 137: return commandBlockTag(nameTag(tag, "command_block"), data);
      case 138: return nameTag(tag, "beacon");
      case 141: return intTag(nameTag(tag, "carrots"), "age", data&7);
      case 142: return intTag(nameTag(tag, "potatoes"), "age", data&7);
      case 143: return buttonTag(nameTag(tag, "oak_button"), data);
      case 144: return needsFinalization(nameTag(tag, "skull"), id, data);
      case 146: return needsFinalization(chestFurnaceLadderTag(nameTag(tag, "trapped_chest"), data), id, data);
      case 147: return nameTag(tag, "light_weighted_pressure_plate");
      case 148: return nameTag(tag, "heavy_weighted_pressure_plate");
      case 149:
        nameTag(tag, "comparator");
        facing4Tag(tag, data&3);
        stringTag(tag, "mode", (data&4) != 0 ? "subtract" : "compare");
        boolTag(tag, "powered", (data&8) != 0);
        return tag;
      case 150:
        nameTag(tag, "comparator");
        facing4Tag(tag, data&3);
        stringTag(tag, "mode", (data&4) != 0 ? "subtract" : "compare");
        boolTag(tag, "powered", true);
        return tag;
      case 151:
        nameTag(tag, "daylight_detector");
        boolTag(tag, "inverted", false);
        return tag;
      case 152: return nameTag(tag, "redstone_block");
      case 153: return nameTag(tag, "nether_quartz_ore");
      case 154: return facingTag(nameTag(tag, "hopper"), data&7);
      case 156: return needsFinalization(stairsTag(nameTag(tag, "quartz_stairs"), data), id, data);
      case 157: return utilityRailTag(nameTag(tag, "activator_rail"), data);
      case 158: return facingTag(nameTag(tag, "dropper"), data&7);
      case 163: return needsFinalization(stairsTag(nameTag(tag, "acacia_stairs"), data), id, data);
      case 164: return needsFinalization(stairsTag(nameTag(tag, "dark_oak_stairs"), data), id, data);
      case 165: return nameTag(tag, "slime_block");
      case 166: return nameTag(tag, "barrier");
      case 167: return trapdoorTag(nameTag(tag, "iron_trapdoor"), data);
      case 169: return nameTag(tag, "sea_lantern");
      case 170: return logTag(nameTag(tag, "hay_block"), data);
      case 172: return nameTag(tag, "terracotta");
      case 173: return nameTag(tag, "coal_block");
      case 174: return nameTag(tag, "packed_ice");
      case 176: return needsFinalization(nameTag(tag, "banner"), id, data);
      case 177: return needsFinalization(nameTag(tag, "wall_banner"), id, data);
      case 178:
        nameTag(tag, "daylight_detector");
        boolTag(tag, "inverted", true);
        return tag;
      case 180: return needsFinalization(stairsTag(nameTag(tag, "red_sandstone_stairs"), data), id, data);
      case 181: return slabTag(nameTag(tag, "red_sandstone_slab"), true, false);
      case 182: return slabTag(nameTag(tag, "red_sandstone_slab"), false, (data&8) != 0);
      case 183: return needsFinalization(fenceGate(nameTag(tag, "spruce_fence_gate"), data), id, data);
      case 184: return needsFinalization(fenceGate(nameTag(tag, "birch_fence_gate"), data), id, data);
      case 185: return needsFinalization(fenceGate(nameTag(tag, "jungle_fence_gate"), data), id, data);
      case 186: return needsFinalization(fenceGate(nameTag(tag, "dark_oak_fence_gate"), data), id, data);
      case 187: return needsFinalization(fenceGate(nameTag(tag, "acacia_fence_gate"), data), id, data);
      case 188: return needsFinalization(nameTag(tag, "spruce_fence"), id, data);
      case 189: return needsFinalization(nameTag(tag, "birch_fence"), id, data);
      case 190: return needsFinalization(nameTag(tag, "jungle_fence"), id, data);
      case 191: return needsFinalization(nameTag(tag, "dark_oak_fence"), id, data);
      case 192: return needsFinalization(nameTag(tag, "acacia_fence"), id, data);
      case 193: return needsFinalization(nameTag(tag, "spruce_door"), id, data);
      case 194: return needsFinalization(nameTag(tag, "birch_door"), id, data);
      case 195: return needsFinalization(nameTag(tag, "jungle_door"), id, data);
      case 196: return needsFinalization(nameTag(tag, "acacia_door"), id, data);
      case 197: return needsFinalization(nameTag(tag, "dark_oak_door"), id, data);
      case 198: return facingTag(nameTag(tag, "end_rod"), data);
      case 199: return needsFinalization(nameTag(tag, "chorus_plant"), id, data);
      case 200: return nameTag(tag, "chorus_flower");
      case 201: return nameTag(tag, "purpur_block");
      case 202: return nameTag(tag, "purpur_pillar");
      case 203: return needsFinalization(stairsTag(nameTag(tag, "purpur_stairs"), data), id, data);
      case 204: return slabTag(nameTag(tag, "purpur_slab"), true, false);
      case 205: return slabTag(nameTag(tag, "purpur_slab"), false, (data&8) != 0);
      case 206: return nameTag(tag, "end_stone_bricks");
      case 207: return intTag(nameTag(tag, "beetroots"), "age", data & 0b11);
      case 208: return nameTag(tag, "grass_path");
      case 209: return nameTag(tag, "end_gateway");
      case 210: return commandBlockTag(nameTag(tag, "repeating_command_block"), data);
      case 211: return commandBlockTag(nameTag(tag, "chain_command_block"), data);
      case 212: return intTag(nameTag(tag, "frosted_ice"), "age", data&3);
      case 213: return nameTag(tag, "magma_block");
      case 214: return nameTag(tag, "nether_wart_block");
      case 215: return nameTag(tag, "red_nether_bricks");
      case 216: return logTag(nameTag(tag, "bone_block"), data);
      case 217: return nameTag(tag, "structure_void");
      case 218:
        nameTag(tag, "observer");
        facingTag(tag, data&7);
        boolTag(tag, "powered", (data&8) != 0);
        return tag;
      case 219: return facingTag(nameTag(tag, "white_shulker_box"), data);
      case 221: return facingTag(nameTag(tag, "orange_shulker_box"), data);
      case 222: return facingTag(nameTag(tag, "magenta_shulker_box"), data);
      case 223: return facingTag(nameTag(tag, "light_blue_shulker_box"), data);
      case 224: return facingTag(nameTag(tag, "yellow_shulker_box"), data);
      case 225: return facingTag(nameTag(tag, "lime_shulker_box"), data);
      case 226: return facingTag(nameTag(tag, "pink_shulker_box"), data);
      case 227: return facingTag(nameTag(tag, "gray_shulker_box"), data);
      case 228: return facingTag(nameTag(tag, "cyan_shulker_box"), data);
      case 229: return facingTag(nameTag(tag, "purple_shulker_box"), data);
      case 230: return facingTag(nameTag(tag, "blue_shulker_box"), data);
      case 231: return facingTag(nameTag(tag, "brown_shulker_box"), data);
      case 232: return facingTag(nameTag(tag, "green_shulker_box"), data);
      case 233: return facingTag(nameTag(tag, "red_shulker_box"), data);
      case 234: return facingTag(nameTag(tag, "black_shulker_box"), data);
      case 235: return facing4Tag(nameTag(tag, "white_glazed_terracotta"), data);
      case 236: return facing4Tag(nameTag(tag, "orange_glazed_terracotta"), data);
      case 237: return facing4Tag(nameTag(tag, "magenta_glazed_terracotta"), data);
      case 238: return facing4Tag(nameTag(tag, "light_blue_glazed_terracotta"), data);
      case 239: return facing4Tag(nameTag(tag, "yellow_glazed_terracotta"), data);
      case 240: return facing4Tag(nameTag(tag, "lime_glazed_terracotta"), data);
      case 241: return facing4Tag(nameTag(tag, "pink_glazed_terracotta"), data);
      case 242: return facing4Tag(nameTag(tag, "gray_glazed_terracotta"), data);
      case 243: return facing4Tag(nameTag(tag, "light_gray_glazed_terracotta"), data);
      case 244: return facing4Tag(nameTag(tag, "cyan_glazed_terracotta"), data);
      case 245: return facing4Tag(nameTag(tag, "purple_glazed_terracotta"), data);
      case 246: return facing4Tag(nameTag(tag, "blue_glazed_terracotta"), data);
      case 247: return facing4Tag(nameTag(tag, "brown_glazed_terracotta"), data);
      case 248: return facing4Tag(nameTag(tag, "green_glazed_terracotta"), data);
      case 249: return facing4Tag(nameTag(tag, "red_glazed_terracotta"), data);
      case 250: return facing4Tag(nameTag(tag, "black_glazed_terracotta"), data);
      // case 253: // unused
      // case 254: // unused

      case 1:
        switch (data) {
          default:
          case 0: return nameTag(tag, "stone");
          case 1: return nameTag(tag, "granite");
          case 2: return nameTag(tag, "polished_granite");
          case 3: return nameTag(tag, "diorite");
          case 4: return nameTag(tag, "polished_diorite");
          case 5: return nameTag(tag, "andesite");
          case 6: return nameTag(tag, "polished_andesite");
        }
      case 3:
        switch (data) {
          default:
          case 0: return nameTag(tag, "dirt");
          case 1: return nameTag(tag, "coarse_dirt");
          case 2: return needsFinalization(nameTag(tag, "podzol"), id, data);
        }
      case 5:
        switch (data) {
          default:
          case 0: return nameTag(tag, "oak_planks");
          case 1: return nameTag(tag, "spruce_planks");
          case 2: return nameTag(tag, "birch_planks");
          case 3: return nameTag(tag, "jungle_planks");
          case 4: return nameTag(tag, "acacia_planks");
          case 5: return nameTag(tag, "dark_oak_planks");
        }
      case 6:
        switch (data) {
          default:
          case 0: return nameTag(tag, "oak_sapling");
          case 1: return nameTag(tag, "spruce_sapling");
          case 2: return nameTag(tag, "birch_sapling");
          case 3: return nameTag(tag, "jungle_sapling");
          case 4: return nameTag(tag, "acacia_sapling");
          case 5: return nameTag(tag, "dark_oak_sapling");
        }
      case 12:
        switch (data) {
          default:
          case 0: return nameTag(tag, "sand");
          case 1: return nameTag(tag, "red_sand");
        }
      case 17:
        switch (data&3) {
          default:
          case 0: return logTag(nameTag(tag, "oak_log"), data);
          case 1: return logTag(nameTag(tag, "spruce_log"), data);
          case 2: return logTag(nameTag(tag, "birch_log"), data);
          case 3: return logTag(nameTag(tag, "jungle_log"), data);
        }
      case 18:
        switch (data & 0b11) {
          default:
          case 0: return nameTag(tag, "oak_leaves");
          case 1: return nameTag(tag, "spruce_leaves");
          case 2: return nameTag(tag, "birch_leaves");
          case 3: return nameTag(tag, "jungle_leaves");
        }
      case 19:
        switch (data) {
          default:
          case 0: return nameTag(tag, "sponge");
          case 1: return nameTag(tag, "wet_sponge");
        }
      case 24:
        switch (data) {
          default:
          case 0: return nameTag(tag, "sandstone");
          case 1: return nameTag(tag, "chiseled_sandstone");
          case 2: return nameTag(tag, "smooth_sandstone");
        }
      case 31:
        switch (data) {
          case 0: return nameTag(tag, "dead_bush");
          default:
          case 1: return nameTag(tag, "grass");
          case 2: return nameTag(tag, "fern");
        }
      case 35:
        switch (data) {
          default:
          case 0:  return nameTag(tag, "white_wool");
          case 1:  return nameTag(tag, "orange_wool");
          case 2:  return nameTag(tag, "magenta_wool");
          case 3:  return nameTag(tag, "light_blue_wool");
          case 4:  return nameTag(tag, "yellow_wool");
          case 5:  return nameTag(tag, "lime_wool");
          case 6:  return nameTag(tag, "pink_wool");
          case 7:  return nameTag(tag, "gray_wool");
          case 8:  return nameTag(tag, "light_gray_wool");
          case 9:  return nameTag(tag, "cyan_wool");
          case 10: return nameTag(tag, "purple_wool");
          case 11: return nameTag(tag, "blue_wool");
          case 12: return nameTag(tag, "brown_wool");
          case 13: return nameTag(tag, "green_wool");
          case 14: return nameTag(tag, "red_wool");
          case 15: return nameTag(tag, "black_wool");
        }
      case 38:
        switch (data) {
          default:
          case 0: return nameTag(tag, "poppy");
          case 1: return nameTag(tag, "blue_orchid");
          case 2: return nameTag(tag, "allium");
          case 3: return nameTag(tag, "azure_bluet");
          case 4: return nameTag(tag, "red_tulip");
          case 5: return nameTag(tag, "orange_tulip");
          case 6: return nameTag(tag, "white_tulip");
          case 7: return nameTag(tag, "pink_tulip");
          case 8: return nameTag(tag, "oxeye_daisy");
        }
      case 43:
      case 44: {
        boolean both = id == 43;
        boolean top = (data & 0x8) != 0;
        switch (data) {
          default:
          case 0: return slabTag(nameTag(tag, "smooth_stone_slab"), both, top);
          case 1: return slabTag(nameTag(tag, "sandstone_slab"), both, top);
          case 2: return slabTag(nameTag(tag, "oak_slab"), both, top);
          case 3: return slabTag(nameTag(tag, "cobblestone_slab"), both, top);
          case 4: return slabTag(nameTag(tag, "brick_slab"), both, top);
          case 5: return slabTag(nameTag(tag, "stone_brick_slab"), both, top);
          case 6: return slabTag(nameTag(tag, "nether_brick_slab"), both, top);
          case 7: return slabTag(nameTag(tag, "quartz_slab"), both, top);
        }
      }
      case 50:
        if (data == 5) return nameTag(tag, "torch");
        nameTag(tag, "wall_torch");
        switch (data) {
          default:
          case 1: return stringTag(tag, "facing", "east");
          case 2: return stringTag(tag, "facing", "west");
          case 3: return stringTag(tag, "facing", "south");
          case 4: return stringTag(tag, "facing", "north");
        }
      case 66:
        nameTag(tag, "rail");
        switch (data) {
          default:
          case 0: return stringTag(tag, "shape", "north_south");
          case 1: return stringTag(tag, "shape", "east_west");
          case 2: return stringTag(tag, "shape", "ascending_east");
          case 3: return stringTag(tag, "shape", "ascending_west");
          case 4: return stringTag(tag, "shape", "ascending_north");
          case 5: return stringTag(tag, "shape", "ascending_south");
          case 6: return stringTag(tag, "shape", "south_east");
          case 7: return stringTag(tag, "shape", "south_west");
          case 8: return stringTag(tag, "shape", "north_west");
          case 9: return stringTag(tag, "shape", "north_east");
        }
      case 69:
        nameTag(tag, "lever");
        boolTag(tag, "powered", (data&8) != 0);
        switch (data&7) {
          default:
          case 0: return stringTag(stringTag(tag, "face", "ceiling"), "facing", "east");
          case 1: return stringTag(stringTag(tag, "face", "wall"), "facing", "east");
          case 2: return stringTag(stringTag(tag, "face", "wall"), "facing", "west");
          case 3: return stringTag(stringTag(tag, "face", "wall"), "facing", "south");
          case 4: return stringTag(stringTag(tag, "face", "wall"), "facing", "north");
          case 5: return stringTag(stringTag(tag, "face", "floor"), "facing", "south");
          case 6: return stringTag(stringTag(tag, "face", "floor"), "facing", "east");
          case 7: return stringTag(stringTag(tag, "face", "ceiling"), "facing", "south");
        }
      case 97:
        switch (data) {
          default:
          case 0: return nameTag(tag, "infested_stone");
          case 1: return nameTag(tag, "infested_cobblestone");
          case 2: return nameTag(tag, "infested_stone_bricks");
          case 3: return nameTag(tag, "infested_mossy_stone_bricks");
          case 4: return nameTag(tag, "infested_cracked_stone_bricks");
          case 5: return nameTag(tag, "infested_chiseled_stone_bricks");
        }
      case 98:
        switch (data) {
          default:
          case 0: return nameTag(tag, "stone_bricks");
          case 1: return nameTag(tag, "mossy_stone_bricks");
          case 2: return nameTag(tag, "cracked_stone_bricks");
          case 3: return nameTag(tag, "chiseled_stone_bricks");
        }
      case 95:
        switch (data) {
          default:
          case 0:  return nameTag(tag, "white_stained_glass");
          case 1:  return nameTag(tag, "orange_stained_glass");
          case 2:  return nameTag(tag, "magenta_stained_glass");
          case 3:  return nameTag(tag, "light_blue_stained_glass");
          case 4:  return nameTag(tag, "yellow_stained_glass");
          case 5:  return nameTag(tag, "lime_stained_glass");
          case 6:  return nameTag(tag, "pink_stained_glass");
          case 7:  return nameTag(tag, "gray_stained_glass");
          case 8:  return nameTag(tag, "light_gray_stained_glass");
          case 9:  return nameTag(tag, "cyan_stained_glass");
          case 10: return nameTag(tag, "purple_stained_glass");
          case 11: return nameTag(tag, "blue_stained_glass");
          case 12: return nameTag(tag, "brown_stained_glass");
          case 13: return nameTag(tag, "green_stained_glass");
          case 14: return nameTag(tag, "red_stained_glass");
          case 15: return nameTag(tag, "black_stained_glass");
        }
      case 125:
      case 126: {
        boolean both = id == 125;
        boolean top = (data & 0x8) != 0;
        switch (data) {
          default:
          case 0: return slabTag(nameTag(tag, "oak_slab"), both, top);
          case 1: return slabTag(nameTag(tag, "spruce_slab"), both, top);
          case 2: return slabTag(nameTag(tag, "birch_slab"), both, top);
          case 3: return slabTag(nameTag(tag, "jungle_slab"), both, top);
          case 4: return slabTag(nameTag(tag, "acacia_slab"), both, top);
          case 5: return slabTag(nameTag(tag, "dark_oak_slab"), both, top);
        }
      }
      case 139:
        switch (data) {
          default:
          case 0: return needsFinalization(nameTag(tag, "cobblestone_wall"), id, data);
          case 1: return needsFinalization(nameTag(tag, "mossy_cobblestone_wall"), id, data);
        }
      case 140:
        switch (data) {
          default:
          case 0:  return needsFinalization(nameTag(tag, "flower_pot"), id, data);
          // prior to mc 1.7.2:
          case 1:  return nameTag(tag, "potted_poppy");
          case 2:  return nameTag(tag, "potted_dandelion");
          case 3:  return nameTag(tag, "potted_oak_sapling");
          case 4:  return nameTag(tag, "potted_spruce_sapling");
          case 5:  return nameTag(tag, "potted_birch_sapling");
          case 6:  return nameTag(tag, "potted_jungle_sapling");
          case 7:  return nameTag(tag, "potted_red_mushroom");
          case 8:  return nameTag(tag, "potted_brown_mushroom");
          case 9:  return nameTag(tag, "potted_cactus");
          case 10: return nameTag(tag, "potted_dead_bush");
          case 11: return nameTag(tag, "potted_fern");
          case 12: return nameTag(tag, "potted_acacia_sapling");
          case 13: return nameTag(tag, "potted_dark_oak_sapling");
        }
      case 145:
        switch (data) {
          default:
          case 0:  return stringTag(nameTag(tag, "anvil"), "facing", "north");
          case 1:  return stringTag(nameTag(tag, "anvil"), "facing", "east");
          case 2:  return stringTag(nameTag(tag, "anvil"), "facing", "south");
          case 3:  return stringTag(nameTag(tag, "anvil"), "facing", "west");
          case 4:  return stringTag(nameTag(tag, "chipped_anvil"), "facing", "north");
          case 5:  return stringTag(nameTag(tag, "chipped_anvil"), "facing", "east");
          case 6:  return stringTag(nameTag(tag, "chipped_anvil"), "facing", "south");
          case 7:  return stringTag(nameTag(tag, "chipped_anvil"), "facing", "west");
          case 8:  return stringTag(nameTag(tag, "damaged_anvil"), "facing", "north");
          case 9:  return stringTag(nameTag(tag, "damaged_anvil"), "facing", "east");
          case 10: return stringTag(nameTag(tag, "damaged_anvil"), "facing", "south");
          case 11: return stringTag(nameTag(tag, "damaged_anvil"), "facing", "west");
        }
      case 155:
        switch (data) {
          default:
          case 0: return nameTag(tag, "quartz_block");
          case 1: return nameTag(tag, "chiseled_quartz_block");
          case 2: return nameTag(tag, "quartz_pillar");
        }
      case 159:
        switch (data) {
          default:
          case 0:  return nameTag(tag, "white_terracotta");
          case 1:  return nameTag(tag, "orange_terracotta");
          case 2:  return nameTag(tag, "magenta_terracotta");
          case 3:  return nameTag(tag, "light_blue_terracotta");
          case 4:  return nameTag(tag, "yellow_terracotta");
          case 5:  return nameTag(tag, "lime_terracotta");
          case 6:  return nameTag(tag, "pink_terracotta");
          case 7:  return nameTag(tag, "gray_terracotta");
          case 8:  return nameTag(tag, "light_gray_terracotta");
          case 9:  return nameTag(tag, "cyan_terracotta");
          case 10: return nameTag(tag, "purple_terracotta");
          case 11: return nameTag(tag, "blue_terracotta");
          case 12: return nameTag(tag, "brown_terracotta");
          case 13: return nameTag(tag, "green_terracotta");
          case 14: return nameTag(tag, "red_terracotta");
          case 15: return nameTag(tag, "black_terracotta");
        }
      case 160:
        switch (data) {
          default:
          case 0:  return needsFinalization(nameTag(tag, "white_stained_glass_pane"), id, data);
          case 1:  return needsFinalization(nameTag(tag, "orange_stained_glass_pane"), id, data);
          case 2:  return needsFinalization(nameTag(tag, "magenta_stained_glass_pane"), id, data);
          case 3:  return needsFinalization(nameTag(tag, "light_blue_stained_glass_pane"), id, data);
          case 4:  return needsFinalization(nameTag(tag, "yellow_stained_glass_pane"), id, data);
          case 5:  return needsFinalization(nameTag(tag, "lime_stained_glass_pane"), id, data);
          case 6:  return needsFinalization(nameTag(tag, "pink_stained_glass_pane"), id, data);
          case 7:  return needsFinalization(nameTag(tag, "gray_stained_glass_pane"), id, data);
          case 8:  return needsFinalization(nameTag(tag, "light_gray_stained_glass_pane"), id, data);
          case 9:  return needsFinalization(nameTag(tag, "cyan_stained_glass_pane"), id, data);
          case 10: return needsFinalization(nameTag(tag, "purple_stained_glass_pane"), id, data);
          case 11: return needsFinalization(nameTag(tag, "blue_stained_glass_pane"), id, data);
          case 12: return needsFinalization(nameTag(tag, "brown_stained_glass_pane"), id, data);
          case 13: return needsFinalization(nameTag(tag, "green_stained_glass_pane"), id, data);
          case 14: return needsFinalization(nameTag(tag, "red_stained_glass_pane"), id, data);
          case 15: return needsFinalization(nameTag(tag, "black_stained_glass_pane"), id, data);
        }
      case 161:
        switch (data & 0b1) {
          default:
          case 0: return nameTag(tag, "acacia_leaves");
          case 1: return nameTag(tag, "dark_oak_leaves");
        }
      case 162:
        switch (data&3) {
          default:
          case 0: return logTag(nameTag(tag, "acacia_log"), data);
          case 1: return logTag(nameTag(tag, "dark_oak_log"), data);
        }
      case 168:
        switch (data) {
          default:
          case 0: return nameTag(tag, "prismarine");
          case 1: return nameTag(tag, "prismarine_bricks");
          case 2: return nameTag(tag, "dark_prismarine");
        }
      case 171:
        switch (data) {
          default:
          case 0:  return nameTag(tag, "white_carpet");
          case 1:  return nameTag(tag, "orange_carpet");
          case 2:  return nameTag(tag, "magenta_carpet");
          case 3:  return nameTag(tag, "light_blue_carpet");
          case 4:  return nameTag(tag, "yellow_carpet");
          case 5:  return nameTag(tag, "lime_carpet");
          case 6:  return nameTag(tag, "pink_carpet");
          case 7:  return nameTag(tag, "gray_carpet");
          case 8:  return nameTag(tag, "light_gray_carpet");
          case 9:  return nameTag(tag, "cyan_carpet");
          case 10: return nameTag(tag, "purple_carpet");
          case 11: return nameTag(tag, "blue_carpet");
          case 12: return nameTag(tag, "brown_carpet");
          case 13: return nameTag(tag, "green_carpet");
          case 14: return nameTag(tag, "red_carpet");
          case 15: return nameTag(tag, "black_carpet");
        }
      case 175:
        switch (data & 0b111) {
          default: // finalization will determine the flower from the block below
          case 0: return needsFinalization(nameTag(tag, "sunflower"), id, data);
          case 1: return needsFinalization(nameTag(tag, "lilac"), id, data);
          case 2: return needsFinalization(nameTag(tag, "tall_grass"), id, data);
          case 3: return needsFinalization(nameTag(tag, "large_fern"), id, data);
          case 4: return needsFinalization(nameTag(tag, "rose_bush"), id, data);
          case 5: return needsFinalization(nameTag(tag, "peony"), id, data);
        }
      case 179:
        switch (data) {
          default:
          case 0: return nameTag(tag, "red_sandstone");
          case 1: return nameTag(tag, "chiseled_red_sandstone");
          case 2: return nameTag(tag, "smooth_red_sandstone");
        }
      case 251:
        switch (data) {
          default:
          case 0:  return nameTag(tag, "white_concrete");
          case 1:  return nameTag(tag, "orange_concrete");
          case 2:  return nameTag(tag, "magenta_concrete");
          case 3:  return nameTag(tag, "light_blue_concrete");
          case 4:  return nameTag(tag, "yellow_concrete");
          case 5:  return nameTag(tag, "lime_concrete");
          case 6:  return nameTag(tag, "pink_concrete");
          case 7:  return nameTag(tag, "gray_concrete");
          case 8:  return nameTag(tag, "light_gray_concrete");
          case 9:  return nameTag(tag, "cyan_concrete");
          case 10: return nameTag(tag, "purple_concrete");
          case 11: return nameTag(tag, "blue_concrete");
          case 12: return nameTag(tag, "brown_concrete");
          case 13: return nameTag(tag, "green_concrete");
          case 14: return nameTag(tag, "red_concrete");
          case 15: return nameTag(tag, "black_concrete");
        }
      case 252:
        switch (data) {
          default:
          case 0:  return nameTag(tag, "white_concrete_powder");
          case 1:  return nameTag(tag, "orange_concrete_powder");
          case 2:  return nameTag(tag, "magenta_concrete_powder");
          case 3:  return nameTag(tag, "light_blue_concrete_powder");
          case 4:  return nameTag(tag, "yellow_concrete_powder");
          case 5:  return nameTag(tag, "lime_concrete_powder");
          case 6:  return nameTag(tag, "pink_concrete_powder");
          case 7:  return nameTag(tag, "gray_concrete_powder");
          case 8:  return nameTag(tag, "light_gray_concrete_powder");
          case 9:  return nameTag(tag, "cyan_concrete_powder");
          case 10: return nameTag(tag, "purple_concrete_powder");
          case 11: return nameTag(tag, "blue_concrete_powder");
          case 12: return nameTag(tag, "brown_concrete_powder");
          case 13: return nameTag(tag, "green_concrete_powder");
          case 14: return nameTag(tag, "red_concrete_powder");
          case 15: return nameTag(tag, "black_concrete_powder");
        }
      case 255:
        nameTag(tag, "structure_block");
        switch (data) {
          default:
          case 0: return stringTag(tag, "mode", "data");
          case 1: return stringTag(tag, "mode", "save");
          case 2: return stringTag(tag, "mode", "load");
          case 3: return stringTag(tag, "mode", "corner");
        }
    }
    return nameTag(tag, "unknown");
  }

  private static Tag needsFinalization(CompoundTag blockTag, int id, int data) {
    CompoundTag tag = new CompoundTag();
    tag.add("Name", new StringTag("#legacy_" + blockTag.get("Name").stringValue()));
    tag.add("Id", new IntTag(id));
    tag.add("Data", new IntTag(data));
    tag.add("Block", blockTag);
    return tag;
  }

  public static CompoundTag createTag(String name) {
    return nameTag(new CompoundTag(), name);
  }

  private static CompoundTag nameTag(CompoundTag tag, String name) {
    tag.add("Name", new StringTag(name.startsWith("minecraft:") ? name: "minecraft:" + name));
    return tag;
  }

  private static CompoundTag customTag(CompoundTag tag, String name, SpecificTag newTag) {
    CompoundTag properties;
    if (tag.get("Properties").isCompoundTag()) {
      properties = tag.get("Properties").asCompound();
    } else {
      properties = new CompoundTag();
      tag.add("Properties", properties);
    }
    properties.add(name, newTag);
    return tag;
  }

  public static CompoundTag stringTag(CompoundTag tag, String name, String data) {
    return customTag(tag, name, new StringTag(data));
  }

  public static CompoundTag intTag(CompoundTag tag, String name, int data) {
    return customTag(tag, name, new IntTag(data));
  }

  public static CompoundTag boolTag(CompoundTag tag, String name, boolean data) {
    return stringTag(tag, name, data ? "true" : "false");
  }

  private static CompoundTag facingTag(CompoundTag tag, int direction) {
    return stringTag(tag, "facing", (new String[] {"down", "up", "north", "south", "west", "east"})[direction % 6]);
  }

  private static CompoundTag facing4Tag(CompoundTag tag, int data) {
    return stringTag(tag, "facing", (new String[] {"south", "west", "north", "east"})[data % 4]);
  }

  private static CompoundTag slabTag(CompoundTag tag, boolean both, boolean top) {
    return customTag(tag, "type", new StringTag(both ? "double" : (top ? "top" : "bottom")));
  }

  private static CompoundTag litTag(CompoundTag tag, boolean lit) {
    return customTag(tag, "lit", new StringTag(lit ? "true" : "false"));
  }

  private static CompoundTag redstoneTorchTag(CompoundTag tag, int data, boolean lit) {
    switch (data % 6) {
      case 1: return litTag(customTag(nameTag(tag, "redstone_wall_torch"), "facing", new StringTag("east")), lit);
      case 2: return litTag(customTag(nameTag(tag, "redstone_wall_torch"), "facing", new StringTag("west")), lit);
      case 3: return litTag(customTag(nameTag(tag, "redstone_wall_torch"), "facing", new StringTag("south")), lit);
      case 4: return litTag(customTag(nameTag(tag, "redstone_wall_torch"), "facing", new StringTag("north")), lit);
      default: return litTag(nameTag(tag, "redstone_torch"), lit);
    }
  }

  private static CompoundTag repeaterTag(CompoundTag tag, int data, boolean on) {
    int direction = data & 3;
    int delay = (data >> 2) & 3;

    tag.add("delay", new IntTag(delay));
    facing4Tag(tag, direction);
    tag.add("powered", new StringTag(on ? "true" : "false"));
    return tag;
  }

  private static CompoundTag trapdoorTag(CompoundTag tag, int data) {
    boolTag(tag, "open", (data & 0b0100) != 0);
    stringTag(tag, "half", (data & 0b1000) != 0 ? "top" : "bottom");
    switch (data & 0b0011) {
      default:
      case 0: return stringTag(tag, "facing", "north");
      case 1: return stringTag(tag, "facing", "south");
      case 2: return stringTag(tag, "facing", "west");
      case 3: return stringTag(tag, "facing", "east");
    }
  }

  private static CompoundTag fenceGate(CompoundTag tag, int data) {
    stringTag(tag, "open", (data & 0b0100) == 0 ? "false" : "true");
    return facing4Tag(tag, data & 4);
  }

  private static CompoundTag stairsTag(CompoundTag tag, int data) {
    stringTag(tag, "half", (data & 0b0100) == 0 ? "bottom" : "top");
    return stringTag(tag, "facing", LegacyStairs.getFacing(data).getName());
  }

  private static CompoundTag commandBlockTag(CompoundTag tag, int data) {
    stringTag(tag, "conditional", data >> 3 == 0 ? "false" : "true");
    return facingTag(tag, data&7);
  }

  private static CompoundTag pistonTag(CompoundTag tag, int data) {
    stringTag(tag, "extended", (data&8) != 0 ? "true" : "false");
    return facingTag(tag, data&7);
  }

  private static CompoundTag logTag(CompoundTag tag, int data) {
    switch (data >>> 2) {
      default:
      case 0: return stringTag(tag, "axis", "y");
      case 1: return stringTag(tag, "axis", "x");
      case 2: return stringTag(tag, "axis", "z");
    }
  }

  private static CompoundTag wallSignTag(CompoundTag tag, int data) {
    switch (data) {
      default:
      case 2: return stringTag(tag, "facing", "north");
      case 3: return stringTag(tag, "facing", "south");
      case 4: return stringTag(tag, "facing", "west");
      case 5: return stringTag(tag, "facing", "east");
    }
  }

  public static CompoundTag chestFurnaceLadderTag(CompoundTag tag, int data) {
    switch (data) {
      default:
      case 2: return stringTag(tag, "facing", "north");
      case 3: return stringTag(tag, "facing", "south");
      case 4: return stringTag(tag, "facing", "west");
      case 5: return stringTag(tag, "facing", "east");
    }
  }

  private static CompoundTag buttonTag(CompoundTag tag, int data) {
    stringTag(tag, "powered", (data & 0b1000) == 0 ? "false" : "true");
    switch (data & 7) {
      case 0: return stringTag(stringTag(tag, "facing", "north"), "face", "ceiling");
      case 1: return stringTag(stringTag(tag, "facing", "east"), "face", "wall");
      case 2: return stringTag(stringTag(tag, "facing", "west"), "face", "wall");
      case 3: return stringTag(stringTag(tag, "facing", "south"), "face", "wall");
      case 4: return stringTag(stringTag(tag, "facing", "north"), "face", "wall");
      default:
      case 5: return stringTag(stringTag(tag, "facing", "north"), "face", "floor");
    }
  }

  private static CompoundTag utilityRailTag(CompoundTag tag, int data) {
    stringTag(tag, "powered", (data&8) != 0 ? "true" : "false");
    switch (data&7) {
      default:
      case 0: return stringTag(tag, "shape", "north_south");
      case 1: return stringTag(tag, "shape", "east_west");
      case 2: return stringTag(tag, "shape", "ascending_east");
      case 3: return stringTag(tag, "shape", "ascending_west");
      case 4: return stringTag(tag, "shape", "ascending_north");
      case 5: return stringTag(tag, "shape", "ascending_south");
    }
  }

  public static CompoundTag vineTag(CompoundTag tag, int data, boolean forceUp) {
    boolTag(tag, "up", data == 0 || forceUp);
    boolTag(tag, "north", (data & 0b0100) != 0);
    boolTag(tag, "east", (data & 0b1000) != 0);
    boolTag(tag, "south", (data & 0b0001) != 0);
    boolTag(tag, "west", (data & 0b0010) != 0);
    return tag;
  }

  private static CompoundTag mushroomTag(CompoundTag tag, int data, boolean redColor) {
    boolean up, down, north, south, east, west;
    boolean stem = false;

    switch (data) {
      case 0:
        up = false;
        down = false;
        north = false;
        south = false;
        east = false;
        west = false;
        break;
      case 1:
        up = true;
        down = false;
        north = true;
        south = false;
        east = false;
        west = true;
        break;
      case 2:
        up = true;
        down = false;
        north = true;
        south = false;
        east = false;
        west = false;
        break;
      case 3:
        up = true;
        down = false;
        north = true;
        south = false;
        east = true;
        west = false;
        break;
      case 4:
        up = true;
        down = false;
        north = false;
        south = false;
        east = false;
        west = true;
        break;
      case 5:
        up = true;
        down = false;
        north = false;
        south = false;
        east = false;
        west = false;
        break;
      case 6:
        up = true;
        down = false;
        north = false;
        south = false;
        east = true;
        west = false;
        break;
      case 7:
        up = true;
        down = false;
        north = false;
        south = true;
        east = false;
        west = true;
        break;
      case 8:
        up = true;
        down = false;
        north = false;
        south = true;
        east = false;
        west = false;
        break;
      case 9:
        up = true;
        down = false;
        north = false;
        south = true;
        east = true;
        west = false;
        break;
      case 10:
        up = false;
        down = false;
        north = true;
        south = true;
        east = true;
        west = true;
        stem = true;
        break;
      default:
      case 11: // 11-13 are undefined
      case 12:
      case 13:
      case 14:
        up = true;
        down = true;
        north = true;
        south = true;
        east = true;
        west = true;
        break;
      case 15:
        up = true;
        down = true;
        north = true;
        south = true;
        east = true;
        west = true;
        stem = true;
        break;
    }

    nameTag(tag, stem ? "mushroom_stem" : (redColor ? "red_mushroom_block" : "brown_mushroom_block"));
    stringTag(tag, "up", up ? "true" : "false");
    stringTag(tag, "down", down ? "true" : "false");
    stringTag(tag, "north", north ? "true" : "false");
    stringTag(tag, "east", east ? "true" : "false");
    stringTag(tag, "south", south ? "true" : "false");
    stringTag(tag, "west", west ? "true" : "false");
    return tag;
  }
}
