package se.llbit.chunky.block;

import se.llbit.nbt.*;

public class LegacyBlocks {

  public static Tag getTag(int offset, byte[] blocks, byte[] blockData) {
    int id = blocks[offset] & 0xFF;
    int data = (blockData[offset / 2] << (offset % 2 == 0 ? 0 : 4)) & 0xFF;

    CompoundTag tag = new CompoundTag();
    switch (id) {
      case 0:   return nameTag(tag, "air");
      case 2:   return nameTag(tag, "grass_block");
      case 4:   return nameTag(tag, "cobblestone");
      case 7:   return nameTag(tag, "bedrock");
      case 8:
      case 9:   return nameTag(tag, "water");
      case 10:
      case 11:  return nameTag(tag, "lava");
      case 13:  return nameTag(tag, "gravel");
      case 14:  return nameTag(tag, "gold_ore");
      case 15:  return nameTag(tag, "iron_ore");
      case 16:  return nameTag(tag, "coal_ore");
      case 20:  return nameTag(tag, "glass");
      case 21:  return nameTag(tag, "lapis_ore");
      case 22:  return nameTag(tag, "lapis_block");
      case 23:  return facingTag(nameTag(tag, "dispenser"), data);
      case 25:  return nameTag(tag, "note_block");
      case 26:  return nameTag(tag, "red_bed");
      case 27:  return nameTag(tag, "powered_rail");
      case 28:  return nameTag(tag, "detector_rail");
      case 29:  return nameTag(tag, "sticky_piston");
      case 30:  return nameTag(tag, "cobweb");
      case 32:  return nameTag(tag, "dead_bush");
      case 33:  return nameTag(tag, "piston");
      case 34:  return nameTag(tag, "piston_head");
      case 36:  //TODO: none?
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
      case 50:  return nameTag(tag, "torch");
      case 51:  return nameTag(tag, "fire");
      case 52:  return nameTag(tag, "spawner");
      case 53:  return nameTag(tag, "oak_stairs");
      case 54:  return nameTag(tag, "chest");
      case 55:  return nameTag(tag, "redstone_wire");
      case 56:  return nameTag(tag, "diamond_ore");
      case 57:  return nameTag(tag, "diamond_block");
      case 58:  return nameTag(tag, "crafting_table");
      case 59:  return nameTag(tag, "wheat");
      case 60:  return nameTag(tag, "farmland");
      case 61:  return litTag(facingTag(nameTag(tag, "furnace"), data), false);
      case 62:  return litTag(facingTag(nameTag(tag, "furnace"), data), true);
      case 63:  return nameTag(tag, "oak_sign");
      case 64:  return nameTag(tag, "oak_door");
      case 65:  return nameTag(tag, "ladder");
      case 66:  return nameTag(tag, "rail");
      case 67:  return nameTag(tag, "cobblestone_stairs");
      case 68:  return nameTag(tag, "oak_wall_sign");
      case 69:  return nameTag(tag, "lever");
      case 70:  return nameTag(tag, "stone_pressure_plate");
      case 71:  return nameTag(tag, "iron_door");
      case 72:  return nameTag(tag, "oak_door");
      case 73:  return litTag(nameTag(tag, "redstone_ore"), false);
      case 74:  return litTag(nameTag(tag, "redstone_ore"), true);
      case 75:  return redstoneTorchTag(tag, data, false);
      case 76:  return redstoneTorchTag(tag, data, true);
      case 77:  return nameTag(tag, "stone_button");
      case 78:  return nameTag(tag, "snow");
      case 79:  return nameTag(tag, "ice");
      case 80:  return nameTag(tag, "snow_block");
      case 81:  return nameTag(tag, "cactus");
      case 82:  return nameTag(tag, "clay");
      case 83:  return nameTag(tag, "sugar_cane");
      case 84:  return nameTag(tag, "jukebox");
      case 85:  return nameTag(tag, "oak_fence");
      case 86:  return facing4Tag(nameTag(tag, "carved_pumpkin"), data);
      case 87:  return nameTag(tag, "netherrack");
      case 88:  return nameTag(tag, "soul_sand");
      case 89:  return nameTag(tag, "glowstone");
      case 90:  return customTag(nameTag(tag, "nether_portal"), "axis", new StringTag(data == 2 ? "x" : "y"));
      case 91:  return facing4Tag(nameTag(tag, "jack_o_lantern"), data);
      case 92:  return customTag(nameTag(tag, "cake"), "bites", new IntTag(data % 6));
      case 93:  return repeaterTag(nameTag(tag, "repeater"), data, false);
      case 94:  return repeaterTag(nameTag(tag, "repeater"), data, true);
      case 95:  //TODO: stained glass
      case 96:  return trapdoorTag(nameTag(tag, "oak_trapdoor"), data);
      case 99:  return nameTag(tag, "brown_mushroom_block");  //TODO state
      case 100: return nameTag(tag, "red_mushroom_block");  //TODO state
      case 101: return nameTag(tag, "iron_bars"); //TODO state
      case 102: return nameTag(tag, "glass_pane");  //TODO state
      case 103: return nameTag(tag, "melon");
      case 104: return nameTag(tag, "pumpkin_stem");  //TODO state
      case 105: return nameTag(tag, "melon_stem");  //TODO state
      case 106: return nameTag(tag, "vine"); //TODO state
      case 107: return nameTag(tag, "oak_fence_gate"); //TODO state
      case 108: return nameTag(tag, "brick_stairs");  //TODO state
      case 109: return nameTag(tag, "stone_brick_stairs");  //TODO state
      case 110: return nameTag(tag, "mycelium");
      case 111: return nameTag(tag, "lily_pad");  //TODO state
      case 112: return nameTag(tag, "nether_bricks");
      case 113: return nameTag(tag, "nether_brick_fence"); //TODO state
      case 114: return nameTag(tag, "nether_brick_stairs"); //TODO state
      case 115: return nameTag(tag, "nether_wart"); //TODO state
      case 116: return nameTag(tag, "enchanting_table");
      case 117: return nameTag(tag, "brewing_stand");
      case 118: return nameTag(tag, "cauldron");  //TODO state
      case 119: return nameTag(tag, "end_portal");
      case 120: return nameTag(tag, "end_portal_frame");
      case 121: return nameTag(tag, "end_stone");
      case 122: return nameTag(tag, "dragon_egg");
      case 123: return nameTag(tag, "redstone_lamp");
      case 124: return nameTag(tag, "redstone_lamp"); //TODO state
      case 125: //TODO double slab
      case 126: //TODO single slab
      case 127: return nameTag(tag, "cocoa"); //TODO state
      case 128: return nameTag(tag, "sandstone_stairs"); //TODO state
      case 129: return nameTag(tag, "emerald_ore");
      case 130: return nameTag(tag, "ender_chest");
      case 131: return nameTag(tag, "tripwire_hook");
      case 132: return nameTag(tag, "tripwire");
      case 133: return nameTag(tag, "emerald_block");
      case 134: return nameTag(tag, "spruce_stairs"); //TODO state
      case 135: return nameTag(tag, "birch_stairs"); //TODO state
      case 136: return nameTag(tag, "jungle_stairs"); //TODO state
      case 137: return nameTag(tag, "command_block"); //TODO state
      case 138: return nameTag(tag, "beacon");
      case 139: //TODO cobble wall
      case 140: return nameTag(tag, "flower_pot"); //TODO state
      case 141: return nameTag(tag, "carrots"); //TODO state
      case 142: return nameTag(tag, "potatoes"); //TODO state
      case 143: return nameTag(tag, "oak_button");  //TODO state
      case 144: //TODO skull
      case 145: return nameTag(tag, "anvil"); //TODO orientation
      case 146: return nameTag(tag, "trapped_chest"); //TODO state
      case 147: return nameTag(tag, "light_weighted_pressure_plate");
      case 148: return nameTag(tag, "heavy_weighted_pressure_plate");
      case 149: return nameTag(tag, "comparator"); //TODO state
      case 150: return nameTag(tag, "comparator"); //TODO state
      case 151: return nameTag(tag, "daylight_detector");
      case 152: return nameTag(tag, "redstone_block");
      case 153: return nameTag(tag, "nether_quartz_ore");
      case 154: return nameTag(tag, "hopper");  //TODO state
      case 155: //TODO quartz block
      case 156: return nameTag(tag, "quartz_stairs");
      case 157: return nameTag(tag, "activator_rail");
      case 158: return nameTag(tag, "dropper");
      case 159: //TODO hardened clay
      case 160: //TODO stained glass
      case 161: //TODO leaves
      case 162: //TODO logs
      case 163: return nameTag(tag, "acacia_stairs");
      case 164: return nameTag(tag, "dark_oak_stairs");
      case 165: return nameTag(tag, "slime_block");
      case 166: return nameTag(tag, "barrier");
      case 167: return trapdoorTag(nameTag(tag, "iron_trapdoor"), data);
      case 168: //TODO prismarine
      case 169: return nameTag(tag, "sea_lantern");
      case 170: return nameTag(tag, "hay_block");
      case 171: //TODO carpet
      case 172: return nameTag(tag, "terracotta");
      case 173: return nameTag(tag, "coal_block");
      case 174: return nameTag(tag, "packed_ice");
      case 175: //TODO double plant
      case 176: //TODO banner
      case 177: //TODO banner
      case 178: return nameTag(tag, "daylight_detector"); //TODO state
      case 179: //TODO red sandstone
      case 180: return nameTag(tag, "red_sandstone_stairs"); //TODO stairs
      case 181: return nameTag(tag, "red_sandstone_slab");  //TODO double slab
      case 182: return nameTag(tag, "red_sandstone_slab");
      case 183: return nameTag(tag, "spruce_fence_gate"); // TODO state V
      case 184: return nameTag(tag, "birch_fence_gate");
      case 185: return nameTag(tag, "jungle_fence_gate");
      case 186: return nameTag(tag, "dark_oak_fence_gate");
      case 187: return nameTag(tag, "acacia_fence_gate");
      case 188: return nameTag(tag, "spruce_fence");
      case 189: return nameTag(tag, "birch_fence");
      case 190: return nameTag(tag, "jungle_fence");
      case 191: return nameTag(tag, "dark_oak_fence");
      case 192: return nameTag(tag, "acacia_fence");
      case 193: return nameTag(tag, "spruce_door");
      case 194: return nameTag(tag, "birch_door");
      case 195: return nameTag(tag, "jungle_door");
      case 196: return nameTag(tag, "acacia_door");
      case 197: return nameTag(tag, "dark_oak_door");
      case 198: return nameTag(tag, "end_rod");
      case 199: return nameTag(tag, "chorus_plant");
      case 200: return nameTag(tag, "chorus_flower"); //TODO end state ^
      case 201: return nameTag(tag, "purpur_block");
      case 202: return nameTag(tag, "purpur_pillar"); //TODO state
      case 203: return nameTag(tag, "purpur_stairs"); //TODO state
      case 204: return nameTag(tag, "purpur_slab"); //TODO state
      case 205: return nameTag(tag, "purpur_slab");
      case 206: return nameTag(tag, "end_stone_bricks");
      case 207: return nameTag(tag, "beetroots");
      case 208: return nameTag(tag, "grass_path");
      case 209: return nameTag(tag, "end_gateway");
      case 210: return nameTag(tag, "repeating_command_block"); //TODO state
      case 211: return nameTag(tag, "chain_command_block"); //TODO state
      case 212: return nameTag(tag, "frosted_ice"); //TODO state
      case 213: return nameTag(tag, "magma_block");
      case 214: return nameTag(tag, "nether_wart_block");
      case 215: return nameTag(tag, "red_nether_bricks");
      case 216: return nameTag(tag, "bone_block"); //TODO state
      case 217: return nameTag(tag, "structure_void");
      case 218: return nameTag(tag, "observer");  //TODO state
      case 219: return nameTag(tag, "white_shulker_box"); //TODO state
      case 221: return nameTag(tag, "orange_shulker_box"); //TODO state
      case 222: return nameTag(tag, "magenta_shulker_box"); //TODO state
      case 223: return nameTag(tag, "light_blue_shulker_box"); //TODO state
      case 224: return nameTag(tag, "yellow_shulker_box"); //TODO state
      case 225: return nameTag(tag, "lime_shulker_box"); //TODO state
      case 226: return nameTag(tag, "pink_shulker_box"); //TODO state
      case 227: return nameTag(tag, "gray_shulker_box"); //TODO state
      case 228: return nameTag(tag, "cyan_shulker_box"); //TODO state
      case 229: return nameTag(tag, "purple_shulker_box"); //TODO state
      case 230: return nameTag(tag, "blue_shulker_box"); //TODO state
      case 231: return nameTag(tag, "brown_shulker_box"); //TODO state
      case 232: return nameTag(tag, "green_shulker_box"); //TODO state
      case 233: return nameTag(tag, "red_shulker_box"); //TODO state
      case 234: return nameTag(tag, "black_shulker_box"); //TODO state
      case 235: return nameTag(tag, "white_glazed_terracotta"); //TODO state
      case 236: return nameTag(tag, "orange_glazed_terracotta"); //TODO state
      case 237: return nameTag(tag, "magenta_glazed_terracotta"); //TODO state
      case 238: return nameTag(tag, "light_blue_glazed_terracotta"); //TODO state
      case 239: return nameTag(tag, "yellow_glazed_terracotta"); //TODO state
      case 240: return nameTag(tag, "lime_glazed_terracotta"); //TODO state
      case 241: return nameTag(tag, "pink_glazed_terracotta"); //TODO state
      case 242: return nameTag(tag, "gray_glazed_terracotta"); //TODO state
      case 243: return nameTag(tag, "light_gray_glazed_terracotta"); //TODO state
      case 244: return nameTag(tag, "cyan_glazed_terracotta"); //TODO state
      case 245: return nameTag(tag, "purple_glazed_terracotta"); //TODO state
      case 246: return nameTag(tag, "blue_glazed_terracotta"); //TODO state
      case 247: return nameTag(tag, "brown_glazed_terracotta"); //TODO state
      case 248: return nameTag(tag, "green_glazed_terracotta"); //TODO state
      case 249: return nameTag(tag, "red_glazed_terracotta"); //TODO state
      case 250: return nameTag(tag, "black_glazed_terracotta"); //TODO state
      case 251: //TODO concrete
      case 252: //TODO concrete powder
      case 253: //TODO empty?
      case 254: //TODO empty?
      case 255: return nameTag(tag, "structure_block"); //TODO state

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
          case 2: return nameTag(tag, "podzol");
        }
      case 5:
        switch (data) {
          default:
          case 0: return nameTag(tag, "oak_wood");
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
        switch (data) {
          default:
          case 0: return nameTag(tag, "oak_log");
          case 1: return nameTag(tag, "spruce_log");
          case 2: return nameTag(tag, "birch_log");
          case 3: return nameTag(tag, "jungle_log");
        }
      case 18:
        switch (data) {
          default:
          case 0: return nameTag(tag, "oak_leaves");
          case 1: return nameTag(tag, "spruce_leaves");
          case 2: return nameTag(tag, "brich_leaves");
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
        boolean which = (data & 0x8) != 0;
        switch (data) {
          default:
          case 0: return slabTag(nameTag(tag, "smooth_stone_slab"), both, which);
          case 1: return slabTag(nameTag(tag, "sandstone_slab"), both, which);
          case 2: return slabTag(nameTag(tag, "oak_slab"), both, which);
          case 3: return slabTag(nameTag(tag, "cobblestone_slab"), both, which);
          case 4: return slabTag(nameTag(tag, "brick_slab"), both, which);
          case 5: return slabTag(nameTag(tag, "stone_brick_slab"), both, which);
          case 6: return slabTag(nameTag(tag, "nether_brick_slab"), both, which);
          case 7: return slabTag(nameTag(tag, "quartz_slab"), both, which);
        }
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
    }
    return nameTag(tag, "unknown");
  }

  private static CompoundTag nameTag(CompoundTag tag, String name) {
    tag.add("Name", new StringTag("minecraft:" + name));
    return tag;
  }

  private static CompoundTag facingTag(CompoundTag tag, int direction) {
    return customTag(tag, "facing", new StringTag((new String[] {"down", "up", "north", "south", "east", "west"})[direction % 6]));
  }

  private static CompoundTag facing4Tag(CompoundTag tag, int data) {
    return customTag(tag, "facing", new StringTag((new String[] {"south", "west", "north", "east"})[data % 4]));
  }

  private static CompoundTag customTag(CompoundTag tag, String name, SpecificTag newTag) {
    tag.add(name, newTag);
    return tag;
  }

  private static CompoundTag slabTag(CompoundTag tag, boolean both, boolean which) {
    return customTag(tag, "type", new StringTag(both ? "double" : (which ? "top" : "bottom")));
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
    boolean open = (data & 4) != 0;
    int state = open ? (data & 3) + 2 : data >> 3;
    switch (state) {
      case 0: return customTag(tag, "half", new StringTag("bottom"));
      case 1: return customTag(tag, "half", new StringTag("top"));
      default: return customTag(customTag(tag, "open", new StringTag("true")),
          "facing", new StringTag((new String[] {"north", "south", "west", "east"})[state-2]));
    }
  }
}
