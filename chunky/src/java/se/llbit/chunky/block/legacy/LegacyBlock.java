package se.llbit.chunky.block.legacy;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockSpec;
import se.llbit.chunky.resources.Texture;
import se.llbit.nbt.Tag;

public class LegacyBlock extends Block {

  final Block block;
  final int data;

  public LegacyBlock(String name, Tag tag) {
    super(name, Texture.unknown);
    block = new BlockSpec(tag.get("Block")).toBlock();
    data = tag.get("Data").intValue(0);

    solid = block.solid;
    opaque = block.opaque;
  }
}
