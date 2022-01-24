package se.llbit.chunky.block;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import se.llbit.nbt.CompoundTag;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlockSpec blockSpec = (BlockSpec) o;
    return Objects.equals(tag, blockSpec.tag);
  }

  /**
   * Converts NBT block data to Chunky block object.
   */
  public Block toBlock() {
    String name = tag.get("Name").stringValue("unknown:unknown");
    for (BlockProvider provider : blockProviders) {
      Block block = provider.getBlockByTag(name, tag);
      if (block != null) {
        return maybeWaterlogged(block);
      }
    }
    return new UnknownBlock(name);
  }

  private Block maybeWaterlogged(Block block) {
    if (tag.get("Properties").get("waterlogged").stringValue("").equals("true")) {
      block.waterlogged = true;
    }
    return block;
  }

  public Tag getTag() {
    return tag;
  }
}
