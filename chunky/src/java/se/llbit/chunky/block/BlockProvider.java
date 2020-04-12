package se.llbit.chunky.block;

import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

public interface BlockProvider {
  Block getBlockByTag(String name, Tag tag);

  static String facing(Tag tag, String defaultValue) {
    return tag.get("Properties").get("facing").stringValue(defaultValue);
  }

  static String facing(Tag tag) {
    return facing(tag, "north");
  }

  static int stringToInt(Tag tag, int defaultValue) {
    if (!(tag instanceof StringTag)) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(tag.stringValue());
    } catch (NumberFormatException ignored) {
      return defaultValue;
    }
  }

  static String blockName(Tag tag) {
    String name = tag.get("Name").stringValue();
    if (name.startsWith("minecraft:")) {
      name = name.substring(10); // Remove "minecraft:" prefix.
    }
    return name;
  }
}
