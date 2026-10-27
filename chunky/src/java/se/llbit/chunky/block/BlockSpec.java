package se.llbit.chunky.block;

import se.llbit.chunky.block.minecraft.UnknownBlock;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;
import se.llbit.util.annotation.NotNull;

import java.util.LinkedList;
import java.util.List;

public class BlockSpec {

  public static final List<BlockProvider> blockProviders = new LinkedList<>();

  private final Tag tag;

  public BlockSpec(@NotNull Tag tag) {
    this.tag = tag;
  }

  @Override
  public int hashCode() {
    return tag.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof BlockSpec) && ((BlockSpec) obj).tag.equals(tag);
  }

  /**
   * Converts NBT block data to Chunky block object.
   */
  public Block toBlock() {
    // Versions before 26.3 snapshot 7 used "Name" for the block name and "Properties" for the block state data.
    // Versions 26.3 snapshot 7 and following use a StringTag for a block with default block state information, or
    // a CompoundTag containing a NamedTag with an empty name to store the block name if the block is in a list with
    // other CompundTags. If the block has non-default block state data, then "id" is used for the block name, and
    // "properties" is used for the block state information.
    String name;
    if (tag instanceof StringTag) {
      name = tag.stringValue("unknown:unknown");
    } else {
      Tag idTag = tag.get("Name");
      if (idTag.isError()) {
        idTag = tag.get("id");
      }
      if (idTag.isError()) {
        idTag = tag.get("");
      }
      name = idTag.stringValue("unknown:unknown");
    }
    for (BlockProvider provider : blockProviders) {
      Block block = provider.getBlockByTag(name, tag);
      if (block != null) {
        return maybeWaterlogged(block);
      }
    }
    return new UnknownBlock(name, tag);
  }

  private Block maybeWaterlogged(Block block) {
    if (getPropertiesTag(tag).get("waterlogged").stringValue("").equals("true")) {
      return block.applyWaterlogging();
    }
    return block;
  }

  public Tag getTag() {
    return tag;
  }

  public static Tag getPropertiesTag(Tag tag) {
    Tag properties = tag.get("properties");
    if (properties.isError()) {
      return tag.get("Properties");
    }
    return properties;
  }
}
