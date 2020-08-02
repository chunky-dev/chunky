package se.llbit.chunky.block;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;
import se.llbit.util.NbtUtil;
import se.llbit.util.NotNull;

public class BlockSpec {

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
   *
   * @param blockProviders
   */
  public Block toBlock(BlockProviderRegistry blockProviders) {
    String name = tag.get("Name").stringValue("unknown:unknown");
    for (BlockProvider provider : blockProviders.getBlockProviders()) {
      Block block = provider.getBlockByTag(name, tag, blockProviders);
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
