package se.llbit.chunky.block;

import se.llbit.nbt.IntTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.util.Collection;

public interface BlockProvider {
  Block getBlockByTag(String name, Tag tag);

  /**
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
   *
   * @param tag          String or int tag
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

  /**
   * Get the boolean value of the given {@link StringTag}.
   *
   * @param tag String tag
   * @return True if the tag is a string tag with the value "true", false otherwise
   */
  static boolean stringToBoolean(Tag tag) {
    return tag.stringValue("false").equals("true");
  }

  static String blockName(Tag tag) {
    // See BlockSpec#toBlock for why this is needed.
    String name;
    if (tag instanceof StringTag) {
      name = tag.stringValue();
    } else {
      Tag idTag = tag.get("Name");
      if (idTag.isError()) {
        idTag = tag.get("id");
        if (idTag.isError()) {
          idTag = tag.get("");
        }
      }
      name = idTag.stringValue();
    }
    if (name.startsWith("minecraft:")) {
      name = name.substring(10); // Remove "minecraft:" prefix.
    }
    return name;
  }
}
