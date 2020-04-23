package se.llbit.chunky.chunk;

import se.llbit.chunky.block.*;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BlockPalette {
  private static final int BLOCK_PALETTE_VERSION = 4;
  public final int airId, stoneId, waterId;

  private static final Map<String, Consumer<Block>> materialProperties = new HashMap<>();

  /** Stone blocks are used for filling invisible regions in the Octree. */
  public final Block stone, water;

  private final Map<BlockSpec, Integer> blockMap;
  private final List<Block> palette;

  public BlockPalette(Map<BlockSpec, Integer> initialMap, List<Block> initialList) {
    this.blockMap = initialMap;
    this.palette = initialList;
    CompoundTag airTag = new CompoundTag();
    airTag.add("Name", new StringTag("minecraft:air"));
    CompoundTag stoneTag = new CompoundTag();
    stoneTag.add("Name", new StringTag("minecraft:stone"));
    CompoundTag waterTag = new CompoundTag();
    waterTag.add("Name", new StringTag("minecraft:water"));
    airId = put(airTag);
    stoneId = put(stoneTag);
    waterId = put(waterTag);
    stone = get(stoneId);
    water = get(waterId);
  }

  public BlockPalette() {
    this(new HashMap<>(), new ArrayList<>());
  }

  /**
   * Adds a new block to the palette and returns the palette index.
   * @param tag NBT tag for the block.
   * @return the palette index of the block in this palette.
   */
  public int put(Tag tag) {
    return put(new BlockSpec(tag));
  }

  public int put(BlockSpec spec) {
    Integer id = blockMap.get(spec);
    if (id != null) {
      return id;
    }
    id = palette.size();
    blockMap.put(spec, id);
    Block block = spec.toBlock();
    applyMaterial(block);
    palette.add(block);
    return id;
  }

  private static void applyMaterial(Block block) {
    Consumer<Block> properties = materialProperties.get(block.name);
    if (properties != null) {
      properties.accept(block);
    }
  }

  public Block get(int id) {
    return palette.get(id);
  }

  static {
    materialProperties.put("minecraft:water", block -> {
      block.specular = 0.12f;
      block.ior = 1.333f;
      block.refractive = true;
    });
    materialProperties.put("minecraft:lava", block -> {
      block.emittance = 1.0f;
    });
    Consumer<Block> glassConfig = block -> {
      block.ior = 1.52f;
      block.refractive = true;
    };
    materialProperties.put("minecraft:glass", glassConfig);
    materialProperties.put("minecraft:glass_pane", glassConfig);
    materialProperties.put("minecraft:white_stained_glass", glassConfig);
    materialProperties.put("minecraft:orange_stained_glass", glassConfig);
    materialProperties.put("minecraft:magenta_stained_glass", glassConfig);
    materialProperties.put("minecraft:light_blue_stained_glass", glassConfig);
    materialProperties.put("minecraft:yellow_stained_glass", glassConfig);
    materialProperties.put("minecraft:lime_stained_glass", glassConfig);
    materialProperties.put("minecraft:pink_stained_glass", glassConfig);
    materialProperties.put("minecraft:gray_stained_glass", glassConfig);
    materialProperties.put("minecraft:light_gray_stained_glass", glassConfig);
    materialProperties.put("minecraft:cyan_stained_glass", glassConfig);
    materialProperties.put("minecraft:purple_stained_glass", glassConfig);
    materialProperties.put("minecraft:blue_stained_glass", glassConfig);
    materialProperties.put("minecraft:brown_stained_glass", glassConfig);
    materialProperties.put("minecraft:green_stained_glass", glassConfig);
    materialProperties.put("minecraft:red_stained_glass", glassConfig);
    materialProperties.put("minecraft:black_stained_glass", glassConfig);
    materialProperties.put("minecraft:white_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:orange_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:magenta_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:light_blue_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:yellow_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:lime_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:pink_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:gray_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:light_gray_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:cyan_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:purple_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:blue_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:brown_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:green_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:red_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:black_stained_glass_pane", glassConfig);
    materialProperties.put("minecraft:gold_block", block -> {
      block.specular = 0.04f;
    });
    materialProperties.put("minecraft:diamond_block", block -> {
      block.specular = 0.04f;
    });
    materialProperties.put("minecraft:iron_block", block -> {
      block.specular = 0.04f;
    });
    materialProperties.put("minecraft:redstone_torch", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:torch", block -> {
      block.emittance = 50.0f;
    });
    materialProperties.put("minecraft:wall_torch", block -> {
      block.emittance = 50.0f;
    });
    materialProperties.put("minecraft:fire", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:ice", block -> {
      block.ior = 1.31f;
      block.refractive = true;
    });
    materialProperties.put("minecraft:frosted_ice", block -> {
      block.ior = 1.31f;
      block.refractive = true;
    });
    materialProperties.put("minecraft:glowstone", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:portal", block -> { // MC <1.13
      block.emittance = 0.4f;
    });
    materialProperties.put("minecraft:nether_portal", block -> { // MC >=1.13
      block.emittance = 0.4f;
    });
    materialProperties.put("minecraft:jack_o_lantern", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:beacon", block -> {
      block.emittance = 1.0f;
      block.ior = 1.52f;
    });
    materialProperties.put("minecraft:redstone_lamp", block -> {
      if (block instanceof RedstoneLamp && ((RedstoneLamp) block).isLit) {
        block.emittance = 1.0f;
      }
    });
    materialProperties.put("minecraft:emerald_block", block -> {
      block.specular = 0.04f;
    });
    materialProperties.put("minecraft:sea_lantern", block -> {
      block.emittance = 0.5f;
    });
    materialProperties.put("minecraft:magma", block -> {
      block.emittance = 0.6f;
    });
    materialProperties.put("minecraft:end_rod", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:kelp", block -> {
      block.waterlogged = true;
    });
    materialProperties.put("minecraft:kelp_plant", block -> {
      block.waterlogged = true;
    });
    materialProperties.put("minecraft:seagrass", block -> {
      block.waterlogged = true;
    });
    materialProperties.put("minecraft:tall_seagrass", block -> {
      block.waterlogged = true;
    });
    materialProperties.put("minecraft:campfire", block -> {
      if (block instanceof Campfire && ((Campfire)block).isLit) {
        block.emittance = 1.0f;
      }
    });
    materialProperties.put("minecraft:lantern", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:shroomlight", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:soul_fire_lantern", block -> {
      block.emittance = 0.6f;
    });
    materialProperties.put("minecraft:soul_fire_torch", block -> {
      block.emittance = 35.0f;
    });
    materialProperties.put("minecraft:soul_fire_wall_torch", block -> {
      block.emittance = 35.0f;
    });
    materialProperties.put("minecraft:soul_fire", block -> {
      block.emittance = 0.6f;
    });
    materialProperties.put("minecraft:crying_obsidian", block -> {
      block.emittance = 0.6f;
    });
    materialProperties.put("minecraft:respawn_anchor", block -> {
      if (block instanceof RespawnAnchor) {
        int charges = ((RespawnAnchor)block).charges;
        if (charges > 0) {
          block.emittance = 1.0f / 15 * (charges * 4 - 2);
        }
      }
    });
  }

  /**
   * Writes the block specifications to file.
   */
  public void write(DataOutputStream out) throws IOException {
    out.writeInt(BLOCK_PALETTE_VERSION);
    BlockSpec[] specs = new BlockSpec[blockMap.size()];
    for (Map.Entry<BlockSpec, Integer> entry : blockMap.entrySet()) {
      specs[entry.getValue()] = entry.getKey();
    }
    out.writeInt(specs.length);
    for (BlockSpec spec : specs) {
      spec.serialize(out);
    }
  }

  public static BlockPalette read(DataInputStream in) throws IOException {
    int version = in.readInt();
    if (version != BLOCK_PALETTE_VERSION) {
      throw new IOException("Incompatible block palette format.");
    }
    int numBlocks = in.readInt();
    Map<BlockSpec, Integer> blockMap = new HashMap<>(numBlocks);
    List<Block> blocks = new ArrayList<>(numBlocks);
    for (int i = 0; i < numBlocks; ++i) {
      BlockSpec spec = BlockSpec.deserialize(in);
      blockMap.put(spec, i);
      Block block = spec.toBlock();
      applyMaterial(block);
      blocks.add(block);
    }
    return new BlockPalette(blockMap, blocks);
  }
}
