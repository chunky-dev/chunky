package se.llbit.chunky.block;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.NotNull;

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
    tag.write(out);
  }

  @Override
  public int hashCode() {
    return tag.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof BlockSpec) && ((BlockSpec) obj).tag.equals(tag);
  }

  /** Converts NBT block data to Chunky block object. */
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
}
