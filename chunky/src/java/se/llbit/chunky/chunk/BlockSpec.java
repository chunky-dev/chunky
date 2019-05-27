package se.llbit.chunky.chunk;

import se.llbit.chunky.block.Block;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.NamedTag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Builder for blocks.
 */
public interface BlockSpec {
  static BlockSpec deserialize(DataInputStream in) throws IOException {
    int type = in.read();
    switch (type) {
      case 0:
        return new TagBlockSpec(CompoundTag.read(in));
      default:
        throw new IOException("Unrecognized block spec type: " + type);
    }
  }

  Block toBlock();

  void serialize(DataOutputStream out) throws IOException;
}
