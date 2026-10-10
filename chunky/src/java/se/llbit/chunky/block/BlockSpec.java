package se.llbit.chunky.block;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import se.llbit.chunky.block.minecraft.UnknownBlock;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.SpecificTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;
import se.llbit.util.NbtUtil;
import se.llbit.util.annotation.NotNull;

public class BlockSpec {

  public static final List<BlockProvider> blockProviders = new LinkedList<>();

  private final Tag tag;

  public BlockSpec(@NotNull Tag tag) {
    this.tag = tag;
  }

  public static BlockSpec deserialize(DataInputStream in) throws IOException {
    Tag tag = CompoundTag.read(in);
    if (tag.isError()) {
      throw new IOException("Error while reading block palette: " + tag.error());
    }
    return new BlockSpec(tag);
  }

  public void serialize(DataOutputStream out) throws IOException {
    NbtUtil.safeSerialize(out, tag);
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
    } else if (tag instanceof CompoundTag compoundTag) {
      Tag idTag = tag.get("Name");
      if (idTag.isError()) {
        idTag = tag.get("id");
        if (idTag.isError()) {
          idTag = tag.get("");
          compoundTag.add("Properties", (SpecificTag) tag.get("properties"));
        }
      }
      name = idTag.stringValue("unknown:unknown");
    } else {
      name = "unknown:unknown";
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
    if (tag.get("Properties").get("waterlogged").stringValue("").equals("true")) {
      return block.applyWaterlogging();
    }
    return block;
  }

  public Tag getTag() {
    return tag;
  }
}
