package se.llbit.chunky.chunk;

import se.llbit.chunky.block.*;
import se.llbit.math.Octree;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.IntTag;
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

/**
 * The block palette maps every block type to a numeric ID and can get the <code>{@link Block}</code>
 * instance that corresponds to an ID. Only one instance of every block configuration will be created and then
 * re-used for all blocks of that type with the same configuration (i.e. the same block data).
 * The numerical IDs are used to efficiently store the blocks in the octree.
 *
 * This class also manages material properties.
 */
public class BlockPalette {
  private static final int BLOCK_PALETTE_VERSION = 4;
  public final int airId, stoneId, waterId;
  public static final int ANY_ID = Octree.ANY_TYPE;

  private final Map<String, Consumer<Block>> materialProperties;

  /** Stone blocks are used for filling invisible regions in the Octree. */
  public final Block stone, water;

  private final Map<BlockSpec, Integer> blockMap;
  private final List<Block> palette;

  public BlockPalette(Map<BlockSpec, Integer> initialMap, List<Block> initialList) {
    this.blockMap = initialMap;
    this.palette = initialList;
    this.materialProperties = getDefaultMaterialProperties();
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
   *
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

  public Block get(int id) {
    if(id == ANY_ID)
      return stone;
    return palette.get(id);
  }

  /**
   * Get the index for a water block with the given level and data. If it doesn't exist, it is
   * created.
   *
   * @param level Water level
   * @param data  Water data (for corner levels)
   * @return Index of the water block in this palette
   * @throws IllegalArgumentException If the level is out of range
   */
  public int getWaterId(int level, int data) {
    if (level < 0 || level > 15) {
      throw new IllegalArgumentException("Invalid water level " + level);
    }
    CompoundTag tag = new CompoundTag();
    tag.add("Name", new StringTag("minecraft:water$chunky"));
    tag.add("level", new IntTag(level));
    tag.add("data", new IntTag(data));
    BlockSpec spec = new BlockSpec(tag);
    return put(spec);
  }

  /**
   * Get the index for a lava block with the given level and data. If it doesn't exist, it is
   * created.
   *
   * @param level Lava level
   * @param data  Lava data (for corner levels)
   * @return Index of the lava block in this palette
   * @throws IllegalArgumentException If the level is out of range
   */
  public int getLavaId(int level, int data) {
    if (level < 0 || level > 15) {
      throw new IllegalArgumentException("Invalid lava level " + level);
    }
    CompoundTag tag = new CompoundTag();
    tag.add("Name", new StringTag("minecraft:lava$chunky"));
    tag.add("level", new IntTag(level));
    tag.add("data", new IntTag(data));
    BlockSpec spec = new BlockSpec(tag);
    return put(spec);
  }

  /**
   * Updates the material properties of the block and applies them.
   *
   * @param name the id of the block to be updated, e.g. "minecraft:stone"
   * @param properties function that modifies the block's properties
   */
  public void updateProperties(String name, Consumer<Block> properties) {
    materialProperties.put(name, properties);
    blockMap.forEach(
        (spec, id) -> {
          Block block = palette.get(id);
          if (block.name.equals(name)) {
            applyMaterial(block);
          }
        });
  }

  /**
   * Apply the material properties that were registered via <code>
   * {@link #updateProperties(String, Consumer)}</code> to the given block.
   *
   * @param block Block to apply the material configuration to
   */
  public void applyMaterial(Block block) {
    Consumer<Block> properties = materialProperties.get(block.name);
    if (properties != null) {
      properties.accept(block);
    }
  }

  /**
   * Apply all material properties that were registered with <code>
   * {@link #updateProperties(String, Consumer)}</code> for all blocks in this palette.
   */
  public void applyMaterials() {
    palette.forEach(this::applyMaterial);
  }

  /** @return Default material properties. */
  public static Map<String, Consumer<Block>> getDefaultMaterialProperties() {
    Map<String, Consumer<Block>> materialProperties = new HashMap<>();
    materialProperties.put(
        "minecraft:water",
        block -> {
          block.specular = 0.12f;
          block.ior = 1.333f;
          block.refractive = true;
        });
    materialProperties.put(
        "minecraft:lava",
        block -> {
          block.emittance = 1.0f;
        });
    Consumer<Block> glassConfig =
        block -> {
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
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.9);
    });
    materialProperties.put("minecraft:diamond_block", block -> {
      block.specular = 0.04f;
    });
    materialProperties.put("minecraft:iron_block", block -> {
      block.specular = 0.04f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.9);
    });
    materialProperties.put("minecraft:iron_bars", block -> {
      block.specular = 0.04f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.9);
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
    materialProperties.put("minecraft:furnace", block -> {
      if(block instanceof Furnace && ((Furnace)block).isLit()) {
        block.emittance = 1.0f;
      }
    });
    materialProperties.put("minecraft:smoker", block -> {
      if(block instanceof Smoker && ((Smoker)block).isLit()) {
        block.emittance = 1.0f;
      }
    });
    materialProperties.put("minecraft:blast_furnace", block -> {
      if(block instanceof BlastFurnace && ((BlastFurnace)block).isLit()) {
        block.emittance = 1.0f;
      }
    });
    materialProperties.put("minecraft:lantern", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:shroomlight", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:soul_fire_lantern", block -> { // MC 20w06a-20w16a
      block.emittance = 0.6f;
    });
    materialProperties.put("minecraft:soul_lantern", block -> { // MC >= 20w17a
      block.emittance = 0.6f;
    });
    materialProperties.put("minecraft:soul_fire_torch", block -> { // MC 20w06a-20w16a
      block.emittance = 35.0f;
    });
    materialProperties.put("minecraft:soul_torch", block -> { // MC >= 20w17a
      block.emittance = 35.0f;
    });
    materialProperties.put("minecraft:soul_fire_wall_torch", block -> { // MC 20w06a-20w16a
      block.emittance = 35.0f;
    });
    materialProperties.put("minecraft:soul_wall_torch", block -> { // MC >= 20w17a
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
    Consumer<Block> copperConfig = block -> {
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.75);
    };
    Consumer<Block> lightlyWeatheredCopperConfig = block -> {
      block.metalness = 0.66f;
      block.setPerceptualSmoothness(0.75);
    };
    Consumer<Block> semiWeatheredCopperConfig = block -> {
      block.metalness = 0.66f;
      block.setPerceptualSmoothness(0.75);
    };
    materialProperties.put("minecraft:copper_block", copperConfig);
    materialProperties.put("minecraft:lightly_weathered_copper_block", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:semi_weathered_copper_block", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:cut_copper", copperConfig);
    materialProperties.put("minecraft:lightly_weathered_cut_copper", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:semi_weathered_cut_copper", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:cut_copper_stairs", copperConfig);
    materialProperties.put("minecraft:lightly_weathered_cut_copper_stairs", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:semi_weathered_cut_copper_stairs", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:cut_copper_slab", copperConfig);
    materialProperties.put("minecraft:lightly_weathered_cut_copper_slab", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:semi_weathered_cut_copper_slab", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_copper", copperConfig);
    materialProperties.put("minecraft:waxed_lightly_weathered_copper", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_semi_weathered_copper", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_cut_copper", copperConfig);
    materialProperties.put("minecraft:waxed_lightly_weathered_cut_copper", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_semi_weathered_cut_copper", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_cut_copper_stairs", copperConfig);
    materialProperties.put("minecraft:waxed_lightly_weathered_cut_copper_stairs", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_semi_weathered_cut_copper_stairs", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_cut_copper_slab", copperConfig);
    materialProperties.put("minecraft:waxed_lightly_weathered_cut_copper_slab", lightlyWeatheredCopperConfig);
    materialProperties.put("minecraft:waxed_semi_weathered_cut_copper_slab", semiWeatheredCopperConfig);
    materialProperties.put("minecraft:lightning_rod", copperConfig);
    materialProperties.put("minecraft:small_amethyst_bud", block -> {
      if (block instanceof AmethystCluster && ((AmethystCluster) block).isLit()) {
        block.emittance = 1.0f / 15f;
      }
    });
    materialProperties.put("minecraft:medium_amethyst_bud", block -> {
      if (block instanceof AmethystCluster && ((AmethystCluster) block).isLit()) {
        block.emittance = 1.0f / 15f * 2;
      }
    });
    materialProperties.put("minecraft:large_amethyst_bud", block -> {
      if (block instanceof AmethystCluster && ((AmethystCluster) block).isLit()) {
        block.emittance = 1.0f / 15f * 4;
      }
    });
    materialProperties.put("minecraft:amethyst_cluster", block -> {
      if (block instanceof AmethystCluster && ((AmethystCluster) block).isLit()) {
        block.emittance = 1.0f / 15f * 5;
      }
    });
    materialProperties.put("minecraft:tinted_glass", glassConfig);
    materialProperties.put("minecraft:sculk_sensor", block -> {
      if (block instanceof SculkSensor && ((SculkSensor) block).isActive()) {
        block.emittance = 1.0f / 15f;
      }
    });
    return materialProperties;
  }

  /** Writes the block specifications to file. */
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
      blocks.add(spec.toBlock());
    }
    return new BlockPalette(blockMap, blocks);
  }
}
