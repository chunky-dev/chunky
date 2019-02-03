package se.llbit.chunky.chunk;

import se.llbit.chunky.block.Block;

/**
 * Builder for blocks.
 */
public interface BlockSpec {
  Block toBlock();
}
