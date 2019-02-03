package se.llbit.chunky.chunk;

import se.llbit.chunky.block.Block;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPalette {
  public final int airId, stoneId;

  /** Stone blocks are used for filling invisible regions in the Octree. */
  public final Block stone;

  private final Map<BlockSpec, Integer> blockMap = new HashMap<>();
  private final List<Block> palette = new ArrayList<>();

  public BlockPalette() {
    CompoundTag airTag = new CompoundTag();
    airTag.add("Name", new StringTag("minecraft:air"));
    CompoundTag stoneTag = new CompoundTag();
    stoneTag.add("Name", new StringTag("minecraft:stone"));
    airId = put(airTag);
    stoneId = put(stoneTag);
    stone = get(stoneId);
  }

  /**
   * Adds a new block to the palette and returns the palette index.
   * @param tag NBT tag for the block.
   * @return the palette index of the block in this palette.
   */
  public int put(Tag tag) {
    return put(new TagBlockSpec(tag));
  }

  public int put(BlockSpec spec) {
    Integer id = blockMap.get(spec);
    if (id != null) {
      return id;
    }
    id = palette.size();
    blockMap.put(spec, id);
    palette.add(spec.toBlock());
    return id;
  }

  public Block get(int id) {
    return palette.get(id);
  }
}
