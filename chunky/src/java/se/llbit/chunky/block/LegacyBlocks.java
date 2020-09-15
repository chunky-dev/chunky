package se.llbit.chunky.block;

import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

public class LegacyBlocks {

  private static final String[] nameMap = new String[]{
      "air", "stone", "grass_block", "dirt",
      "cobblestone", "oak_planks", "oak_sapling", "bedrock",
      "water", "water", "lava", "lava",
      "sand", "gravel", "gold_ore", "iron_ore",
      "coal_ore", "oak_wood", "oak_leaves", "sponge",
      "glass", "lapis_ore", "lapis_block", "dispenser",
      "sandstone", "note_block", "red_bed", "powered_rail",
      "detector_rail", "sticky_piston", "cobweb", "tall_grass",
      "dead_bush", "piston", "piston_head", "white_wool",
      "air","dandelion","poppy","brown_mushroom",
      "red_mushroom", "gold_block","iron_block","smooth_stone",
      "smooth_stone_slab"
  };

  public static Tag getTag(int offset, byte[] blocks, byte[] blockData) {
    CompoundTag tag = new CompoundTag();
    int id = blocks[offset] & 0xff;
    if (id >= nameMap.length) {
      id = 0x0;
    }
    tag.add("Name", new StringTag("minecraft:" + nameMap[id]));
    return tag;
  }
}
