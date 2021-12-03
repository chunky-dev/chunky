package se.llbit.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ErrorTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.Tag;

public class NbtUtil {

  /**
   * Write the given NBT tag into the given outputstream but skip any invalid tags instead of
   * throwing errors. This can be removed once https://github.com/llbit/jo-nbt/pull/2 is merged.
   *
   * @param out output stream
   * @param tag NBT tag
   * @throws IOException if writing to the stream fails
   */
  public static void safeSerialize(DataOutputStream out, Tag tag) throws IOException {
    if (tag instanceof ErrorTag) {
      // ignore invalid tag
    } else if (tag instanceof CompoundTag) {
      for (NamedTag item : (CompoundTag) tag) {
        safeSerialize(out, item);
      }
      out.writeByte(Tag.TAG_END);
    } else if (tag instanceof ListTag) {
      out.writeByte(((ListTag) tag).getType());
      List<SpecificTag> validItems = new ArrayList<>();
      for (SpecificTag item : (ListTag) tag) {
        if (!(item instanceof ErrorTag)) {
          validItems.add(item);
        }
      }
      out.writeInt(validItems.size());
      for (SpecificTag item : validItems) {
        safeSerialize(out, item);
      }
    } else if (tag instanceof NamedTag) {
      if (((NamedTag) tag).getTag() instanceof ErrorTag) {
        // ignore invalid named tag
        return;
      }
      ((NamedTag) tag).getTag().writeType(out);
      out.writeUTF(((NamedTag) tag).name());
      safeSerialize(out, ((NamedTag) tag).getTag());
    } else {
      tag.write(out);
    }
  }

  /**
   * Gets the first Non-Error tag from within the tag parameter
   *
   * This function:
   * Tag t = GetTagFromNames(tag, "name1", "name2", "name3");
   *
   * is equivalent to this example:
   * Tag t = tag.get("name1");
   * if(t.isError()) {
   *   t = tag.get("name2");
   * }
   * if(t.isError()) {
   *   t = tag.get("name3");
   * }
   *
   * @param tag The tag to extract data from
   * @param tagNames List of names in priority order
   * @return First tag found
   */
  public static Tag getTagFromNames(@NotNull Tag tag, @NotNull String... tagNames) {
    Tag outTag = null;
    for (String tagName : tagNames) {
      outTag = tag.get(tagName);
      if (!outTag.isError()) {
        return outTag;
      }
    }
    return outTag;
  }

  /**
   * Creates a compound tag from a map
   */
  public static CompoundTag tagFromMap(@NotNull Map<String, Tag> map) {
    CompoundTag compoundTag = new CompoundTag();
    for (Map.Entry<String, Tag> entry : map.entrySet()) {
      String name = entry.getKey();
      Tag tag = entry.getValue();
      compoundTag.add(new NamedTag(name, (SpecificTag) tag));
    }
    return compoundTag;
  }
}
