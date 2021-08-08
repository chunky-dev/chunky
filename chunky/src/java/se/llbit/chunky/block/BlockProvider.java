package se.llbit.chunky.block;

import se.llbit.nbt.IntTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.util.Collection;

public interface BlockProvider {
  Block getBlockByTag(String name, Tag tag);

  /**
   *
   * @return A collection of block IDs that this provider provides.
   */
  Collection<String> getSupportedBlocks();

  static String facing(Tag tag, String defaultValue) {
    return tag.get("Properties").get("facing").stringValue(defaultValue);
  }

  static String facing(Tag tag) {
    return facing(tag, "north");
  }

  /**
   * Get the integer value of the given tag (either a {@link StringTag} or an {@link IntTag}.
   * @param tag String or int tag
   * @param defaultValue Default value if the tag doesn't exist or the value is not an integer
   * @return Integer value of the given tag or the default value
   */
  static int stringToInt(Tag tag, int defaultValue) {
    if (!(tag instanceof StringTag)) {
      return tag.intValue(defaultValue);
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
