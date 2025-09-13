/* Copyright (c) 2019-2022 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.chunk;

import se.llbit.chunky.block.*;
import se.llbit.chunky.block.minecraft.*;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.json.JsonValue;
import se.llbit.math.Octree;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.IntTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * The block palette maps every block type to a numeric ID and can get the <code>{@link Block}</code>
 * instance that corresponds to an ID. Only one instance of every block configuration will be created and then
 * re-used for all blocks of that type with the same configuration (i.e. the same block data).
 * The numerical IDs are used to efficiently store the blocks in the octree.
 * <p>
 * This class also manages material properties.
 *
 * <p>Before <code>{@link BlockPalette#unsynchronize()}</code> is called, <code>{@link BlockPalette}</code> is thread safe
 * for N writer and N reader threads. It locks on <code>{@link BlockPalette#put(BlockSpec)}</code>, and has concurrent
 * data structures to achieve this.</p>
 * <p>
 * After <code>{@link BlockPalette#unsynchronize()}</code> is called, it is only safe to be read by multiple threads concurrently.
 */
public class BlockPalette {
  private static final int BLOCK_PALETTE_VERSION = 4;
  public final int voidId, airId, stoneId, waterId;
  public static final int ANY_ID = Octree.ANY_TYPE;

  private final Map<String, Consumer<Block>> materialProperties;
  public static final Map<String, Consumer<Block>> DEFAULT_MATERIAL_PROPERTIES = getDefaultMaterialProperties();

  /**
   * Stone blocks are used for filling invisible regions in the Octree.
   */
  public final Block stone, water;

  private final Map<BlockSpec, Integer> blockMap;
  private List<Block> palette;

  private ReentrantLock lock = new ReentrantLock();

  public BlockPalette(Map<BlockSpec, Integer> initialMap, List<Block> initialList) {
    this.blockMap = initialMap;
    this.palette = initialList;
    this.materialProperties = new HashMap<>();
    CompoundTag voidTag = new CompoundTag();
    voidTag.add("Name", new StringTag("minecraft:void"));
    CompoundTag airTag = new CompoundTag();
    airTag.add("Name", new StringTag("minecraft:air"));
    CompoundTag stoneTag = new CompoundTag();
    stoneTag.add("Name", new StringTag("minecraft:stone"));
    CompoundTag waterTag = new CompoundTag();
    waterTag.add("Name", new StringTag("minecraft:water"));
    voidId = put(voidTag);
    airId = put(airTag);
    stoneId = put(stoneTag);
    waterId = put(waterTag);
    stone = get(stoneId);
    water = get(waterId);
  }

  public BlockPalette() {
    this(new ConcurrentHashMap<>(), new CopyOnWriteArrayList<>());
  }

  public BlockPalette(Map<String, JsonValue> materials) {
    this();
    materials.forEach((name, properties) -> {
      materialProperties.put(name, block -> {
        block.loadMaterialProperties(properties.asObject());
      });
    });
  }

  /**
   * This method should be called when no threads are acting on the palette anymore.
   * <p>
   * It replaces the lock with one that does nothing and switches the palette list for a non-concurrent one.
   * This is done to not limit render performance once async chunk loading is done.
   */
  public void unsynchronize() {
    palette = new ArrayList<>(palette);
    lock = new ReentrantLock() {
      @Override
      public void lock() {
      }

      @Override
      public void unlock() {
      }
    };
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

  /**
   * locks to avoid race conditions between writer threads
   */
  public int put(BlockSpec spec) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
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
    } finally {
      lock.unlock();
    }
  }

  public Block get(int id) {
    if (id == ANY_ID)
      return stone;
    return palette.get(id);
  }

  /**
   * Get the block specification by its ID in this palette.
   *
   * @param id ID of a block in this palette
   * @return Block specification or null if not found
   */
  public BlockSpec getBlockSpec(int id) {
    for (Entry<BlockSpec, Integer> entry : blockMap.entrySet()) {
      if (entry.getValue() == id) {
        return entry.getKey();
      }
    }
    return null;
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
   * @param name       the id of the block to be updated, e.g. "minecraft:stone"
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
    Consumer<Block> defaultProperties = DEFAULT_MATERIAL_PROPERTIES.get(block.name);
    if (defaultProperties != null) {
      defaultProperties.accept(block);
    }
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

  /**
   * @return Default material properties.
   */
  public static Map<String, Consumer<Block>> getDefaultMaterialProperties() {
    Map<String, Consumer<Block>> materialProperties = new HashMap<>();
    materialProperties.put(
        "minecraft:water",
        block -> {
          block.ior = 1.333f;
          block.alpha = 0;
        });
    materialProperties.put(
      "minecraft:air",
      block -> {
        block.alpha = 0;
      }
    );
    materialProperties.put(
        "minecraft:void",
        block -> {
          block.alpha = 0;
        }
    );
    materialProperties.put(
        "minecraft:lava",
        block -> {
          block.setLightLevel(15);
        });
    Consumer<Block> glassConfig =
        block -> {
          block.ior = 1.52f;
        };
    Consumer<Block> stainedGlassConfig =
      block -> {
        block.ior = 1.52f;
        block.transmissionMetalness = 0.95f;
        float[] color = block.texture.getAvgColorFlat();
        block.absorptionColor.x = color[0];
        block.absorptionColor.y = color[1];
        block.absorptionColor.z = color[2];
        block.absorption = 1.0f;
        block.volumeDensity = 0.3f;
      };
    materialProperties.put("minecraft:glass", glassConfig);
    materialProperties.put("minecraft:glass_pane", glassConfig);
    materialProperties.put("minecraft:white_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:orange_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:magenta_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:light_blue_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:yellow_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:lime_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:pink_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:gray_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:light_gray_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:cyan_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:purple_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:blue_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:brown_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:green_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:red_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:black_stained_glass", stainedGlassConfig);
    materialProperties.put("minecraft:white_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:orange_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:magenta_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:light_blue_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:yellow_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:lime_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:pink_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:gray_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:light_gray_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:cyan_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:purple_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:blue_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:brown_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:green_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:red_stained_glass_pane", stainedGlassConfig);
    materialProperties.put("minecraft:black_stained_glass_pane", stainedGlassConfig);

    // IoR for glossy surface
    materialProperties.put("minecraft:white_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:orange_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:magenta_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:light_blue_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:yellow_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:lime_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:pink_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:gray_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:light_gray_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:cyan_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:purple_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:blue_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:brown_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:green_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:red_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:black_glazed_terracotta", glassConfig);
    materialProperties.put("minecraft:gold_block", block -> {
      block.specular = 1.0f;
      block.metalness = 0.96f;
      block.setPerceptualSmoothness(0.9);
    });
    materialProperties.put("minecraft:raw_gold_block", block -> {
      block.specular = 0.8f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.5);
    });
    materialProperties.put("minecraft:diamond_block", block -> {
      block.ior = 2.418f;
    });
    Consumer<Block> ironConfig = block -> {
      block.specular = 1.0f;
      block.metalness = 0.96f;
      block.setPerceptualSmoothness(0.9);
    };
    materialProperties.put("minecraft:iron_block", ironConfig);
    materialProperties.put("minecraft:raw_iron_block", block -> {
      block.specular = 0.66f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.3);
    });
    materialProperties.put("minecraft:iron_bars", ironConfig);
    materialProperties.put("minecraft:iron_door", ironConfig);
    materialProperties.put("minecraft:iron_trapdoor", ironConfig);
    Consumer<Block> cauldronConfig = block -> {
      block.specular = 1.0f;
      block.metalness = 0.96f;
      block.setPerceptualSmoothness(0.5);
    };
    materialProperties.put("minecraft:cauldron", cauldronConfig);
    materialProperties.put("minecraft:lava_cauldron", cauldronConfig);
    materialProperties.put("minecraft:powder_snow_cauldron", cauldronConfig);
    materialProperties.put("minecraft:water_cauldron", cauldronConfig);
    materialProperties.put("minecraft:hopper", block -> {
      block.specular = 1.0f;
      block.metalness = 0.96f;
      block.setPerceptualSmoothness(0.7);
    });
    materialProperties.put("minecraft:iron_chain", ironConfig);
    Consumer<Block> redstoneTorchConfig = block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(255, 255, 210, 0.35f);
      block.addRefColorGammaCorrected(255, 185, 0, 0.25f);
      block.addRefColorGammaCorrected(221, 0, 0, 0.3f);
      if (block instanceof RedstoneTorch && ((RedstoneTorch) block).isLit()) {
        block.setLightLevel(7);
      }
    };
    materialProperties.put("minecraft:redstone_torch", redstoneTorchConfig);
    materialProperties.put("minecraft:redstone_wall_torch", redstoneTorchConfig);
    materialProperties.put("minecraft:redstone_ore", block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(254, 118, 118, 0.2f);
      block.addRefColorGammaCorrected(210, 3, 3, 0.2f);
    });
    materialProperties.put("minecraft:deepslate_redstone_ore", block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(254, 118, 118, 0.2f);
      block.addRefColorGammaCorrected(210, 3, 3, 0.35f);
    });
    Consumer<Block> torchConfig = block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(255, 255, 210, 0.35f);
      block.addRefColorGammaCorrected(255, 185, 0, 0.25f);
      block.setLightLevel(14);
    };
    materialProperties.put("minecraft:torch", torchConfig);
    materialProperties.put("minecraft:wall_torch", torchConfig);
    materialProperties.put("minecraft:fire", block -> {
      block.setLightLevel(15);
      block.emitterMappingOffset = -0.5f;
    });
    Consumer<Block> iceConfig = block -> {
      block.ior = 1.31f;
    };
    materialProperties.put("minecraft:ice", iceConfig);
    materialProperties.put("minecraft:frosted_ice", iceConfig);
    materialProperties.put("minecraft:packed_ice", iceConfig);
    materialProperties.put("minecraft:blue_ice", iceConfig);
    materialProperties.put("minecraft:glowstone", block -> {
      block.setLightLevel(15);
    });
    materialProperties.put("minecraft:portal", block -> { // MC <1.13
      block.setLightLevel(11);
    });
    materialProperties.put("minecraft:nether_portal", block -> { // MC >=1.13
      block.setLightLevel(11);
    });
    materialProperties.put("minecraft:jack_o_lantern", block -> {
      block.setLightLevel(15);
      block.emitterMappingOffset = 0.5f;
    });
    materialProperties.put("minecraft:beacon", block -> {
      block.setLightLevel(15);
      block.ior = 1.52f;
    });
    materialProperties.put("minecraft:redstone_lamp", block -> {
      if (block instanceof RedstoneLamp && ((RedstoneLamp) block).isLit()) {
        block.setLightLevel(15);
      }
    });
    materialProperties.put("minecraft:emerald_block", block -> {
      block.ior = 1.5825f;
    });
    materialProperties.put("minecraft:sea_lantern", block -> {
      block.setLightLevel(15);
    });
    materialProperties.put("minecraft:magma", block -> {
      block.setLightLevel(3);
    });
    materialProperties.put("minecraft:magma_block", block -> {
      block.setLightLevel(3);
    });
    materialProperties.put("minecraft:end_rod", block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(248, 236, 219, 0.3f);
      block.setLightLevel(14);
    });
    materialProperties.put("minecraft:kelp", block -> {
      block.waterlogged = true;
    });
    materialProperties.put("minecraft:kelp_plant", block -> {
      block.waterlogged = true;
    });
    materialProperties.put("minecraft:seagrass", block -> {
      block.waterlogged = true;
      block.subSurfaceScattering = 0.3f;
    });
    materialProperties.put("minecraft:tall_seagrass", block -> {
      block.waterlogged = true;
      block.subSurfaceScattering = 0.3f;
    });
    materialProperties.put("minecraft:sea_pickle", block -> {
      if (block instanceof SeaPickle) {
        if (((SeaPickle) block).live) {
          block.setLightLevel(3 * ((SeaPickle) block).pickles + 1);
        }
      }
    });
    materialProperties.put("minecraft:furnace", block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(255, 255, 215, 0.38f);
      block.addRefColorGammaCorrected(230, 171, 16, 0.38f);
      if(block instanceof Furnace && ((Furnace)block).isLit()) {
        block.setLightLevel(13);
      }
    });
    materialProperties.put("minecraft:smoker", block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(228, 169, 17, 0.32f);
      if(block instanceof Smoker && ((Smoker)block).isLit()) {
        block.setLightLevel(13);
      }
    });
    materialProperties.put("minecraft:blast_furnace", block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(224, 128, 46, 0.25f);
      if(block instanceof BlastFurnace && ((BlastFurnace)block).isLit()) {
        block.setLightLevel(13);
      }
    });
    materialProperties.put("minecraft:lantern", block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(254, 254, 179, 0.25f);
      block.addRefColorGammaCorrected(253, 158, 76, 0.45f);
      block.addRefColorGammaCorrected(134, 73, 42, 0.05f);
      block.setLightLevel(15);
    });
    materialProperties.put("minecraft:shroomlight", block -> {
      block.setLightLevel(15);
    });
    materialProperties.put("minecraft:soul_fire_lantern", block -> { // MC 20w06a-20w16a
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(220, 252, 255, 0.5f);
      block.addRefColorGammaCorrected(76, 198, 202, 0.3f);
      block.setLightLevel(10);
    });
    materialProperties.put("minecraft:soul_lantern", block -> { // MC >= 20w17a
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(220, 252, 255, 0.5f);
      block.addRefColorGammaCorrected(76, 198, 202, 0.3f);
      block.setLightLevel(10);
    });
    Consumer<Block> soulTorchConfig = block -> {
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(199, 252, 254, 0.45f);
      block.addRefColorGammaCorrected(35, 204, 209, 0.25f);
      block.setLightLevel(10);
    };
    materialProperties.put("minecraft:soul_fire_torch", soulTorchConfig);
    materialProperties.put("minecraft:soul_torch", soulTorchConfig);
    materialProperties.put("minecraft:soul_fire_wall_torch", soulTorchConfig);
    materialProperties.put("minecraft:soul_wall_torch", soulTorchConfig);
    materialProperties.put("minecraft:soul_fire", block -> {
      block.setLightLevel(10);
      block.emitterMappingOffset = -0.5f;
    });
    materialProperties.put("minecraft:crying_obsidian", block -> {
      block.setLightLevel(10);
      block.ior = 1.493f;
    });
    materialProperties.put("minecraft:enchanting_table", block -> {
      block.setLightLevel(7);
    });
    materialProperties.put("minecraft:respawn_anchor", block -> {
      if (block instanceof RespawnAnchor) {
        int charges = ((RespawnAnchor) block).charges;
        if (charges > 0) {
          block.setLightLevel(charges * 4 - 2);
        }
      }
    });
    Consumer<Block> copperConfig = block -> {
      block.specular = 1.0f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.75);
    };
    Consumer<Block> exposedCopperConfig = block -> {
      block.specular = 0.66f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.75);
    };
    Consumer<Block> weatheredCopperConfig = block -> {
      block.specular = 0.66f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.75);
    };
    materialProperties.put("minecraft:raw_copper_block", block -> {
      block.specular = 0.66f;
      block.metalness = 1.0f;
      block.setPerceptualSmoothness(0.5);
    });
    for (String s : new String[]{"minecraft:", "minecraft:waxed_"}) {
      materialProperties.put(s + "copper_block", copperConfig);
      materialProperties.put(s + "cut_copper", copperConfig);
      materialProperties.put(s + "cut_copper_stairs", copperConfig);
      materialProperties.put(s + "cut_copper_slab", copperConfig);
      materialProperties.put(s + "chiseled_copper", copperConfig);
      materialProperties.put(s + "copper_grate", copperConfig);
      materialProperties.put(s + "copper_door", copperConfig);
      materialProperties.put(s + "copper_trapdoor", copperConfig);
      materialProperties.put(s + "copper_chest", copperConfig);
      materialProperties.put(s + "copper_chain", copperConfig);
      materialProperties.put(s + "copper_bars", copperConfig);

      materialProperties.put(s + "exposed_copper", exposedCopperConfig);
      materialProperties.put(s + "exposed_cut_copper", exposedCopperConfig);
      materialProperties.put(s + "exposed_cut_copper_stairs", exposedCopperConfig);
      materialProperties.put(s + "exposed_cut_copper_slab", exposedCopperConfig);
      materialProperties.put(s + "exposed_chiseled_copper", exposedCopperConfig);
      materialProperties.put(s + "exposed_copper_grate", exposedCopperConfig);
      materialProperties.put(s + "exposed_copper_door", exposedCopperConfig);
      materialProperties.put(s + "exposed_copper_trapdoor", exposedCopperConfig);
      materialProperties.put(s + "exposed_copper_chest", exposedCopperConfig);
      materialProperties.put(s + "exposed_copper_chain", exposedCopperConfig);
      materialProperties.put(s + "exposed_copper_bars", exposedCopperConfig);

      materialProperties.put(s + "weathered_copper", weatheredCopperConfig);
      materialProperties.put(s + "weathered_cut_copper", weatheredCopperConfig);
      materialProperties.put(s + "weathered_cut_copper_stairs", weatheredCopperConfig);
      materialProperties.put(s + "weathered_cut_copper_slab", weatheredCopperConfig);
      materialProperties.put(s + "weathered_chiseled_copper", weatheredCopperConfig);
      materialProperties.put(s + "weathered_copper_grate", weatheredCopperConfig);
      materialProperties.put(s + "weathered_copper_door", weatheredCopperConfig);
      materialProperties.put(s + "weathered_copper_trapdoor", weatheredCopperConfig);
      materialProperties.put(s + "weathered_copper_chest", weatheredCopperConfig);
      materialProperties.put(s + "weathered_copper_chain", weatheredCopperConfig);
      materialProperties.put(s + "weathered_copper_bars", weatheredCopperConfig);

      materialProperties.put(s + "lightning_rod", block -> {
        // apply copper attributes only to non-powered lightning rods
        if (block instanceof LightningRod && !((LightningRod) block).isPowered()) {
          copperConfig.accept(block);
        }
      });
      materialProperties.put(s + "exposed_lightning_rod", block -> {
        // apply copper attributes only to non-powered lightning rods
        if (block instanceof LightningRod && !((LightningRod) block).isPowered()) {
          exposedCopperConfig.accept(block);
        }
      });
      materialProperties.put(s + "weathered_lightning_rod", block -> {
        // apply copper attributes only to non-powered lightning rods
        if (block instanceof LightningRod && !((LightningRod) block).isPowered()) {
          weatheredCopperConfig.accept(block);
        }
      });

      materialProperties.put(s + "copper_lantern", block -> {
        copperConfig.accept(block);
        block.metalness = 0.95f; // workaround for emittance not supporting metals
        block.emittance = 1.0f;
      });
      materialProperties.put(s + "exposed_copper_lantern", block -> {
        exposedCopperConfig.accept(block);
        block.emittance = 1.0f;
      });
      materialProperties.put(s + "weathered_copper_lantern", block -> {
        weatheredCopperConfig.accept(block);
        block.emittance = 1.0f;
      });
      materialProperties.put(s + "oxidized_copper_lantern", block -> {
        block.emittance = 1.0f;
      });
    }
    materialProperties.put("minecraft:small_amethyst_bud", block -> {
      block.setLightLevel(1);
      block.ior = 1.543f;
    });
    materialProperties.put("minecraft:medium_amethyst_bud", block -> {
      block.setLightLevel(2);
      block.ior = 1.543f;
    });
    materialProperties.put("minecraft:large_amethyst_bud", block -> {
      block.setLightLevel(4);
      block.ior = 1.543f;
    });
    materialProperties.put("minecraft:amethyst_cluster", block -> {
      block.setLightLevel(5);
      block.ior = 1.543f;
    });
    materialProperties.put("minecraft:budding_amethyst", block -> {
      block.ior = 1.543f;
    });
    materialProperties.put("minecraft:amethyst_block", block -> {
      block.ior = 1.543f;
    });
    materialProperties.put("minecraft:tinted_glass", glassConfig);
    materialProperties.put("minecraft:sculk_sensor", block -> {
      if (block instanceof SculkSensor && ((SculkSensor) block).isActive()) {
        block.setLightLevel(1);
      }
    });
    materialProperties.put("minecraft:calibrated_sculk_sensor", block -> {
      if (block instanceof CalibratedSculkSensor && ((CalibratedSculkSensor) block).isActive()) {
        block.setLightLevel(1);
      }
    });
    Consumer<Block> foliageConfig = block -> {
      block.subSurfaceScattering = 0.3f;
    };
    materialProperties.put("minecraft:grass", foliageConfig);
    materialProperties.put("minecraft:tall_grass", foliageConfig);
    materialProperties.put("minecraft:short_grass", foliageConfig);
    materialProperties.put("minecraft:sugar_cane", foliageConfig);
    materialProperties.put("minecraft:beetroots", foliageConfig);
    materialProperties.put("minecraft:carrots", foliageConfig);
    materialProperties.put("minecraft:potatoes", foliageConfig);
    materialProperties.put("minecraft:melon_stem", foliageConfig);
    materialProperties.put("minecraft:attached_melon_stem", foliageConfig);
    materialProperties.put("minecraft:pumpkin_stem", foliageConfig);
    materialProperties.put("minecraft:attached_pumpkin_stem", foliageConfig);
    materialProperties.put("minecraft:wheat", foliageConfig);
    materialProperties.put("minecraft:big_dripleaf", foliageConfig);
    materialProperties.put("minecraft:big_dripleaf_stem", foliageConfig);
    materialProperties.put("minecraft:small_dripleaf", foliageConfig);
    materialProperties.put("minecraft:fern", foliageConfig);
    materialProperties.put("minecraft:large_fern", foliageConfig);
    materialProperties.put("minecraft:allium", foliageConfig);
    materialProperties.put("minecraft:azure_bluet", foliageConfig);
    materialProperties.put("minecraft:blue_orchid", foliageConfig);
    materialProperties.put("minecraft:cornflower", foliageConfig);
    materialProperties.put("minecraft:dandelion", foliageConfig);
    materialProperties.put("minecraft:lily_of_the_valley", foliageConfig);
    materialProperties.put("minecraft:oxeye_daisy", foliageConfig);
    materialProperties.put("minecraft:poppy", foliageConfig);
    materialProperties.put("minecraft:torchflower", foliageConfig);
    materialProperties.put("minecraft:orange_tulip", foliageConfig);
    materialProperties.put("minecraft:pink_tulip", foliageConfig);
    materialProperties.put("minecraft:red_tulip", foliageConfig);
    materialProperties.put("minecraft:white_tulip", foliageConfig);
    materialProperties.put("minecraft:wither_rose", foliageConfig);
    materialProperties.put("minecraft:lilac", foliageConfig);
    materialProperties.put("minecraft:peony", foliageConfig);
    materialProperties.put("minecraft:pitcher_plant", foliageConfig);
    materialProperties.put("minecraft:rose_bush", foliageConfig);
    materialProperties.put("minecraft:sunflower", foliageConfig);
    materialProperties.put("minecraft:mangrove_propagule", foliageConfig);
    materialProperties.put("minecraft:pink_petals", foliageConfig);
    materialProperties.put("minecraft:oak_sapling", foliageConfig);
    materialProperties.put("minecraft:spruce_sapling", foliageConfig);
    materialProperties.put("minecraft:birch_sapling", foliageConfig);
    materialProperties.put("minecraft:jungle_sapling", foliageConfig);
    materialProperties.put("minecraft:acacia_sapling", foliageConfig);
    materialProperties.put("minecraft:dark_oak_sapling", foliageConfig);
    materialProperties.put("minecraft:cherry_sapling", foliageConfig);
    materialProperties.put("minecraft:oak_leaves", foliageConfig);
    materialProperties.put("minecraft:spruce_leaves", foliageConfig);
    materialProperties.put("minecraft:birch_leaves", foliageConfig);
    materialProperties.put("minecraft:jungle_leaves", foliageConfig);
    materialProperties.put("minecraft:acacia_acacia", foliageConfig);
    materialProperties.put("minecraft:dark_oak_leaves", foliageConfig);
    materialProperties.put("minecraft:mangrove_leaves", foliageConfig);
    materialProperties.put("minecraft:cherry_leaves", foliageConfig);
    materialProperties.put("minecraft:azalea_leaves", foliageConfig);
    materialProperties.put("minecraft:flowering_azalea_leaves", foliageConfig);
    materialProperties.put("minecraft:sweet_berry_bush", foliageConfig);
    materialProperties.put("minecraft:glow_lichen", block -> {
      block.setLightLevel(1);
      block.subSurfaceScattering = 0.3f;
    });
    Consumer<Block> caveVinesConfig = block -> {
      block.subSurfaceScattering = 0.3f;
      block.useReferenceColors = true;
      block.addRefColorGammaCorrected(241, 189, 85, 0.3f);
      block.addRefColorGammaCorrected(164, 100, 34, 0.05f);
      if (block instanceof CaveVines && ((CaveVines) block).hasBerries()) {
        block.setLightLevel(14);
      }
    };
    materialProperties.put("minecraft:cave_vines_plant", caveVinesConfig);
    materialProperties.put("minecraft:cave_vines", caveVinesConfig);
    materialProperties.put("minecraft:vines", foliageConfig);
    materialProperties.put("minecraft:light", block -> {
      if (block instanceof LightBlock) {
        block.emittance = 1.0f / 15f * 4 * ((LightBlock) block).getLevel();
      }
    });
    materialProperties.put("minecraft:ochre_froglight", block -> {
      block.setLightLevel(15);
      block.emitterMappingOffset = 1.0f;
    });
    materialProperties.put("minecraft:verdant_froglight", block -> {
      block.setLightLevel(15);
      block.emitterMappingOffset = 1.0f;
    });
    materialProperties.put("minecraft:pearlescent_froglight", block -> {
      block.setLightLevel(15);
      block.emitterMappingOffset = 1.0f;
    });
    materialProperties.put("minecraft:sculk_catalyst", block -> {
      block.setLightLevel(6);
    });
    Consumer<Block> copperBulbRedLight = block -> {
      block.addRefColorGammaCorrected(217, 35, 35, 0.05f);
      block.addRefColorGammaCorrected(176, 23, 23, 0.05f);
      block.addRefColorGammaCorrected(163, 24, 24, 0.05f);
      block.addRefColorGammaCorrected(138, 24, 24, 0.05f);
    };
    for(String s : new String[]{"minecraft:", "minecraft:waxed_"}) {
      materialProperties.put(s + "copper_bulb", block -> {
        block.useReferenceColors = true;
        block.addRefColorGammaCorrected(255, 235, 186, 0.25f);
        block.addRefColorGammaCorrected(251, 184, 96, 0.25f);
        copperBulbRedLight.accept(block);
        copperConfig.accept(block);
        if(block instanceof CopperBulb && (((CopperBulb) block).isLit() || ((CopperBulb) block).isPowered())) {
          block.setLightLevel(15);
        }
      });
      materialProperties.put(s + "exposed_copper_bulb", block -> {
        block.useReferenceColors = true;
        block.addRefColorGammaCorrected(253, 202, 138, 0.25f);
        block.addRefColorGammaCorrected(223, 139, 41, 0.2f);
        copperBulbRedLight.accept(block);
        exposedCopperConfig.accept(block);
        if(block instanceof CopperBulb && (((CopperBulb) block).isLit() || ((CopperBulb) block).isPowered())) {
          block.setLightLevel(12);
        }
      });
      materialProperties.put(s + "weathered_copper_bulb", block -> {
        block.useReferenceColors = true;
        block.addRefColorGammaCorrected(234, 184, 91, 0.25f);
        block.addRefColorGammaCorrected(224, 151, 53, 0.25f);
        copperBulbRedLight.accept(block);
        weatheredCopperConfig.accept(block);
        if(block instanceof CopperBulb && (((CopperBulb) block).isLit() || ((CopperBulb) block).isPowered())) {
          block.setLightLevel(8);
        }
      });
      materialProperties.put(s + "oxidized_copper_bulb", block -> {
        block.useReferenceColors = true;
        block.addRefColorGammaCorrected(212, 153, 67, 0.25f);
        block.addRefColorGammaCorrected(191, 113, 65, 0.25f);
        copperBulbRedLight.accept(block);
        if(block instanceof CopperBulb && (((CopperBulb) block).isLit() || ((CopperBulb) block).isPowered())) {
          block.setLightLevel(4);
        }
      });
      materialProperties.put("minecraft:calcite", block -> {
        block.ior = 1.486f;
      });
      materialProperties.put("minecraft:lapis_block", block -> {
        block.ior = 1.525f;
      });
      materialProperties.put("minecraft:obsidian", block -> {
        block.ior = 1.493f;
      });
      materialProperties.put("minecraft:quartz_block", block -> {
        block.ior = 1.594f;
      });
      materialProperties.put("minecraft:quartz_pillar", block -> {
        block.ior = 1.594f;
      });
      materialProperties.put("minecraft:chiseled_quartz_block", block -> {
        block.ior = 1.594f;
      });
      materialProperties.put("minecraft:smooth_quartz", block -> {
        block.ior = 1.594f;
      });
      materialProperties.put("minecraft:quartz_bricks", block -> {
        block.ior = 1.594f;
      });
      materialProperties.put("minecraft:honey_block", block -> {
        block.ior = 1.474f; // according to https://study.com/academy/answer/what-is-the-refractive-index-of-honey.html
        block.transmissionMetalness = 0.95f;
        float[] color = block.texture.getAvgColorFlat();
        block.absorptionColor.x = color[0];
        block.absorptionColor.y = color[1];
        block.absorptionColor.z = color[2];
        block.absorption = 1.0f;
        block.volumeDensity = 0.3f;
      });
      materialProperties.put("minecraft:slime_block", block -> {
        block.ior = 1.516f; // gelatin, according to https://study.com/academy/answer/what-is-the-refractive-index-of-gelatin.html
        block.transmissionMetalness = 0.95f;
        float[] color = block.texture.getAvgColorFlat();
        block.absorptionColor.x = color[0];
        block.absorptionColor.y = color[1];
        block.absorptionColor.z = color[2];
        block.absorption = 1.0f;
        block.volumeDensity = 0.3f;
      });
    }
    materialProperties.put("minecraft:copper_wall_torch", block -> {
      block.emittance = 1.0f;
    });
    materialProperties.put("minecraft:copper_torch", block -> {
      block.emittance = 1.0f;
    });
    return materialProperties;
  }

  @PluginApi
  public List<Block> getPalette() {
    return palette;
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
      blocks.add(spec.toBlock());
    }
    return new BlockPalette(blockMap, blocks);
  }
}
