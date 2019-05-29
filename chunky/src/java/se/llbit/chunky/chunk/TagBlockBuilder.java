package se.llbit.chunky.chunk;

import se.llbit.chunky.block.Block;
import se.llbit.nbt.Tag;

/** Callback for constructing blocks from tag block specifications. */
public interface TagBlockBuilder {
  Block buildBlock(Tag tag);
}
