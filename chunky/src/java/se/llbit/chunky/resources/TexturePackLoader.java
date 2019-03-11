/* Copyright (c) 2012-2019 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.Sun;
import se.llbit.chunky.resources.texturepack.AlternateTextures;
import se.llbit.chunky.resources.texturepack.AnimatedTextureLoader;
import se.llbit.chunky.resources.texturepack.BedTextureAdapter;
import se.llbit.chunky.resources.texturepack.ChestTexture;
import se.llbit.chunky.resources.texturepack.CloudsTexture;
import se.llbit.chunky.resources.texturepack.EntityTextureLoader;
import se.llbit.chunky.resources.texturepack.FoliageColorTexture;
import se.llbit.chunky.resources.texturepack.FontTexture;
import se.llbit.chunky.resources.texturepack.GrassColorTexture;
import se.llbit.chunky.resources.texturepack.IndexedTexture;
import se.llbit.chunky.resources.texturepack.LargeChestTexture;
import se.llbit.chunky.resources.texturepack.LayeredTextureLoader;
import se.llbit.chunky.resources.texturepack.RotatedTextureLoader;
import se.llbit.chunky.resources.texturepack.ShulkerTextureLoader;
import se.llbit.chunky.resources.texturepack.SimpleTexture;
import se.llbit.chunky.resources.texturepack.TextureLoader;
import se.llbit.chunky.resources.texturepack.ThinArmEntityTextureLoader;
import se.llbit.log.Log;
import se.llbit.resources.ImageLoader;
import se.llbit.util.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility methods to load Minecraft texture packs.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturePackLoader {
  private static Map<String, TextureLoader> allTextures = new HashMap<>();

  static {
    allTextures.put("normal chest", new AlternateTextures(
        new ChestTexture("assets/minecraft/textures/entity/chest/normal", Texture.chestLock,
            // MC 1.6
            Texture.chestTop, Texture.chestBottom, Texture.chestLeft, Texture.chestRight,
            Texture.chestFront, Texture.chestBack),
        new ChestTexture("item/chest", Texture.chestLock, Texture.chestTop, Texture.chestBottom,
            Texture.chestLeft, Texture.chestRight, Texture.chestFront, Texture.chestBack)));
    allTextures.put("ender chest", new AlternateTextures(
        new ChestTexture("assets/minecraft/textures/entity/chest/ender", // MC 1.6
            Texture.enderChestLock, Texture.enderChestTop, Texture.enderChestBottom,
            Texture.enderChestLeft, Texture.enderChestRight, Texture.enderChestFront,
            Texture.enderChestBack),
        new ChestTexture("item/enderchest", Texture.enderChestLock, Texture.enderChestTop,
            Texture.enderChestBottom, Texture.enderChestLeft, Texture.enderChestRight,
            Texture.enderChestFront, Texture.enderChestBack)));
    allTextures.put("normal double chest", new AlternateTextures(
        new LargeChestTexture("assets/minecraft/textures/entity/chest/normal_double", // MC 1.6
            Texture.largeChestLeft, Texture.largeChestRight, Texture.largeChestTopLeft,
            Texture.largeChestTopRight, Texture.largeChestFrontLeft, Texture.largeChestFrontRight,
            Texture.largeChestBottomLeft, Texture.largeChestBottomRight, Texture.largeChestBackLeft,
            Texture.largeChestBackRight),
        new LargeChestTexture("item/largechest", Texture.largeChestLeft, Texture.largeChestRight,
            Texture.largeChestTopLeft, Texture.largeChestTopRight, Texture.largeChestFrontLeft,
            Texture.largeChestFrontRight, Texture.largeChestBottomLeft,
            Texture.largeChestBottomRight, Texture.largeChestBackLeft,
            Texture.largeChestBackRight)));
    allTextures.put("trapped chest",
        new ChestTexture("assets/minecraft/textures/entity/chest/trapped", // MC 1.6
            Texture.trappedChestLock, Texture.trappedChestTop, Texture.trappedChestBottom,
            Texture.trappedChestLeft, Texture.trappedChestRight, Texture.trappedChestFront,
            Texture.trappedChestBack));
    allTextures.put("trapped double chest",
        new LargeChestTexture("assets/minecraft/textures/entity/chest/trapped_double", // MC 1.6
            Texture.largeTrappedChestLeft, Texture.largeTrappedChestRight,
            Texture.largeTrappedChestTopLeft, Texture.largeTrappedChestTopRight,
            Texture.largeTrappedChestFrontLeft, Texture.largeTrappedChestFrontRight,
            Texture.largeTrappedChestBottomLeft, Texture.largeTrappedChestBottomRight,
            Texture.largeTrappedChestBackLeft, Texture.largeTrappedChestBackRight));
    allTextures.put("sun", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/environment/sun", Sun.texture),// MC 1.6
        new SimpleTexture("environment/sun", Sun.texture),// MC 1.5
        new SimpleTexture("terrain/sun", Sun.texture)));
    allTextures.put("sign", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/entity/sign", Texture.signPost),// MC 1.6
        new SimpleTexture("item/sign", Texture.signPost)));
    allTextures.put("clouds", new AlternateTextures(
        new CloudsTexture("assets/minecraft/textures/environment/clouds"),
        // MC 1.6
        new CloudsTexture("environment/clouds")));
    allTextures.put("grass color map", new AlternateTextures(
        new GrassColorTexture("assets/minecraft/textures/colormap/grass"),
        // MC 1.6
        new GrassColorTexture("misc/grasscolor")));
    allTextures.put("foliage color map", new AlternateTextures(
        new FoliageColorTexture("assets/minecraft/textures/colormap/foliage"),
        // MC 1.6
        new FoliageColorTexture("misc/foliagecolor")));

    allTextures.put("grass_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_top", Texture.grassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_top", Texture.grassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_top", Texture.grassTop),
        new SimpleTexture("textures/blocks/grass_top", Texture.grassTop),
        new IndexedTexture(0x00, Texture.grassTop)));
    allTextures.put("stone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/stone", Texture.stone),
        new SimpleTexture("assets/minecraft/textures/blocks/stone", Texture.stone),
        new SimpleTexture("textures/blocks/stone", Texture.stone),
        new IndexedTexture(0x01, Texture.stone)));
    allTextures.put("dirt", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dirt", Texture.dirt),
        new SimpleTexture("assets/minecraft/textures/blocks/dirt", Texture.dirt),
        new SimpleTexture("textures/blocks/dirt", Texture.dirt),
        new IndexedTexture(0x02, Texture.dirt)));
    allTextures.put("grass_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_side",
            Texture.grassSideSaturated),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_side",
            Texture.grassSideSaturated),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_side",
            Texture.grassSideSaturated),
        new SimpleTexture("textures/blocks/grass_side", Texture.grassSideSaturated),
        new IndexedTexture(0x03, Texture.grassSideSaturated)));
    allTextures.put("oak planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_planks", Texture.oakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_planks", Texture.oakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_oak", Texture.oakPlanks),
        new SimpleTexture("textures/blocks/wood", Texture.oakPlanks),
        new IndexedTexture(0x04, Texture.oakPlanks)));
    allTextures.put("stone slab side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/stone_slab_side", Texture.slabSide),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_slab_side", Texture.slabSide),
        new SimpleTexture("textures/blocks/stoneslab_side", Texture.slabSide),
        new IndexedTexture(0x05, Texture.slabSide)));
    allTextures.put("stone slab top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/stone_slab_top", Texture.slabTop),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_slab_top", Texture.slabTop),
        new SimpleTexture("textures/blocks/stoneslab_top", Texture.slabTop),
        new IndexedTexture(0x06, Texture.slabTop)));
    allTextures.put("brick", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bricks", Texture.brick),
        new SimpleTexture("assets/minecraft/textures/blocks/brick", Texture.brick),
        new SimpleTexture("textures/blocks/brick", Texture.brick),
        new IndexedTexture(0x07, Texture.brick)));
    allTextures.put("tnt side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tnt_side", Texture.tntSide),
        new SimpleTexture("assets/minecraft/textures/blocks/tnt_side", Texture.tntSide),
        new SimpleTexture("textures/blocks/tnt_side", Texture.tntSide),
        new IndexedTexture(0x08, Texture.tntSide)));
    allTextures.put("tnt top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tnt_top", Texture.tntTop),
        new SimpleTexture("assets/minecraft/textures/blocks/tnt_top", Texture.tntTop),
        new SimpleTexture("textures/blocks/tnt_top", Texture.tntTop),
        new IndexedTexture(0x09, Texture.tntTop)));
    allTextures.put("tnt bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tnt_bottom", Texture.tntBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/tnt_bottom", Texture.tntBottom),
        new SimpleTexture("textures/blocks/tnt_bottom", Texture.tntBottom),
        new IndexedTexture(0x0A, Texture.tntBottom)));
    allTextures.put("cobweb", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cobweb", Texture.cobweb),
        new SimpleTexture("assets/minecraft/textures/blocks/cobweb", Texture.cobweb),
        new SimpleTexture("assets/minecraft/textures/blocks/web", Texture.cobweb),
        new SimpleTexture("textures/blocks/web", Texture.cobweb),
        new IndexedTexture(0x0B, Texture.cobweb)));
    allTextures.put("rose", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/poppy", Texture.poppy),
        new SimpleTexture("assets/minecraft/textures/blocks/poppy", Texture.poppy),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_rose", Texture.poppy),
        new SimpleTexture("textures/blocks/rose", Texture.poppy),
        new IndexedTexture(0x0C, Texture.poppy)));
    allTextures.put("dandelion", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dandelion", Texture.dandelion),
        new SimpleTexture("assets/minecraft/textures/blocks/dandelion", Texture.dandelion),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_dandelion", Texture.dandelion),
        new SimpleTexture("textures/blocks/flower", Texture.dandelion),
        new IndexedTexture(0x0D, Texture.dandelion)));
    allTextures.put("nether portal", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_portal", Texture.portal),
        new SimpleTexture("assets/minecraft/textures/blocks/portal", Texture.portal),
        new SimpleTexture("textures/blocks/portal", Texture.portal),
        new IndexedTexture(0x0E, Texture.portal)));
    allTextures.put("oak_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_sapling", Texture.oakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_sapling", Texture.oakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_oak", Texture.oakSapling),
        new SimpleTexture("textures/blocks/sapling", Texture.oakSapling),
        new IndexedTexture(0x0F, Texture.oakSapling)));

    allTextures.put("cobblestone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cobblestone", Texture.cobblestone),
        new SimpleTexture("assets/minecraft/textures/blocks/cobblestone", Texture.cobblestone),
        new SimpleTexture("textures/blocks/stonebrick", Texture.cobblestone),
        new IndexedTexture(0x10, Texture.cobblestone)));
    allTextures.put("bedrock", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bedrock", Texture.bedrock),
        new SimpleTexture("assets/minecraft/textures/blocks/bedrock", Texture.bedrock),
        new SimpleTexture("textures/blocks/bedrock", Texture.bedrock),
        new IndexedTexture(0x11, Texture.bedrock)));
    allTextures.put("sand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sand", Texture.sand),
        new SimpleTexture("assets/minecraft/textures/blocks/sand", Texture.sand),
        new SimpleTexture("textures/blocks/sand", Texture.sand),
        new IndexedTexture(0x12, Texture.sand)));
    allTextures.put("gravel", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gravel", Texture.gravel),
        new SimpleTexture("assets/minecraft/textures/blocks/gravel", Texture.gravel),
        new SimpleTexture("textures/blocks/gravel", Texture.gravel),
        new IndexedTexture(0x13, Texture.gravel)));
    allTextures.put("oak log side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_log", Texture.oakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_log", Texture.oakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_oak", Texture.oakWood),
        new SimpleTexture("textures/blocks/tree_side", Texture.oakWood),
        new IndexedTexture(0x14, Texture.oakWood)));
    allTextures.put("oak log top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_log_top", Texture.oakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_log_top", Texture.oakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_oak_top", Texture.oakWoodTop),
        new SimpleTexture("textures/blocks/tree_top", Texture.oakWoodTop),
        new IndexedTexture(0x15, Texture.oakWoodTop)));
    allTextures.put("iron block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_block", Texture.ironBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_block", Texture.ironBlock),
        new SimpleTexture("textures/blocks/blockIron", Texture.ironBlock),
        new IndexedTexture(0x16, Texture.ironBlock)));
    allTextures.put("gold block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gold_block", Texture.goldBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/gold_block", Texture.goldBlock),
        new SimpleTexture("textures/blocks/blockGold", Texture.goldBlock),
        new IndexedTexture(0x17, Texture.goldBlock)));
    allTextures.put("diamond block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/diamond_block", Texture.diamondBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/diamond_block", Texture.diamondBlock),
        new SimpleTexture("textures/blocks/blockDiamond", Texture.diamondBlock),
        new IndexedTexture(0x18, Texture.diamondBlock)));
    allTextures.put("emerald block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/emerald_block", Texture.emeraldBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/emerald_block", Texture.emeraldBlock),
        new SimpleTexture("textures/blocks/blockEmerald", Texture.emeraldBlock),
        new IndexedTexture(0x19, Texture.emeraldBlock)));
    allTextures.put("redstone block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_block", Texture.redstoneBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_block", Texture.redstoneBlock),
        new SimpleTexture("textures/blocks/blockRedstone", Texture.redstoneBlock),
        new IndexedTexture(0x1A, Texture.redstoneBlock)));
    allTextures.put("red_mushroom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_mushroom", Texture.redMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/red_mushroom", Texture.redMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_red", Texture.redMushroom),
        new SimpleTexture("textures/blocks/mushroom_red", Texture.redMushroom),
        new IndexedTexture(0x1C, Texture.redMushroom)));
    allTextures.put("brown_mushroom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_mushroom", Texture.brownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_mushroom", Texture.brownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_brown", Texture.brownMushroom),
        new SimpleTexture("textures/blocks/mushroom_brown", Texture.brownMushroom),
        new IndexedTexture(0x1D, Texture.brownMushroom)));
    allTextures.put("jungle_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_sapling", Texture.jungleSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_sapling", Texture.jungleSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_jungle", Texture.jungleSapling),
        new SimpleTexture("textures/blocks/sapling_jungle", Texture.jungleSapling),
        new IndexedTexture(0x1E, Texture.jungleSapling)));

    allTextures.put("gold ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gold_ore", Texture.goldOre),
        new SimpleTexture("assets/minecraft/textures/blocks/gold_ore", Texture.goldOre),
        new SimpleTexture("textures/blocks/oreGold", Texture.goldOre),
        new IndexedTexture(0x20, Texture.goldOre)));
    allTextures.put("iron ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_ore", Texture.ironOre),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_ore", Texture.ironOre),
        new SimpleTexture("textures/blocks/oreIron", Texture.ironOre),
        new IndexedTexture(0x21, Texture.ironOre)));
    allTextures.put("coal ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/coal_ore", Texture.coalOre),
        new SimpleTexture("assets/minecraft/textures/blocks/coal_ore", Texture.coalOre),
        new SimpleTexture("textures/blocks/oreCoal", Texture.coalOre),
        new IndexedTexture(0x22, Texture.coalOre)));
    allTextures.put("bookshelf", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bookshelf", Texture.bookshelf),
        new SimpleTexture("assets/minecraft/textures/blocks/bookshelf", Texture.bookshelf),
        new SimpleTexture("textures/blocks/bookshelf", Texture.bookshelf),
        new IndexedTexture(0x23, Texture.bookshelf)));
    allTextures.put("mossy cobblestone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mossy_cobblestone", Texture.mossStone),
        new SimpleTexture("assets/minecraft/textures/blocks/mossy_cobblestone", Texture.mossStone),
        new SimpleTexture("assets/minecraft/textures/blocks/cobblestone_mossy", Texture.mossStone),
        new SimpleTexture("textures/blocks/stoneMoss", Texture.mossStone),
        new IndexedTexture(0x24, Texture.mossStone)));
    allTextures.put("obsidian", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/obsidian", Texture.obsidian),
        new SimpleTexture("assets/minecraft/textures/blocks/obsidian", Texture.obsidian),
        new SimpleTexture("textures/blocks/obsidian", Texture.obsidian),
        new IndexedTexture(0x25, Texture.obsidian)));
    allTextures.put("grass_side_overlay", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_side_overlay",
            Texture.grassSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_side_overlay",
            Texture.grassSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_side_overlay", Texture.grassSide),
        new SimpleTexture("textures/blocks/grass_side_overlay", Texture.grassSide),
        new IndexedTexture(0x26, Texture.grassSide)));
    allTextures.put("tallgrass", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass", Texture.tallGrass),
        new SimpleTexture("assets/minecraft/textures/blocks/grass", Texture.tallGrass),
        new SimpleTexture("assets/minecraft/textures/blocks/tallgrass", Texture.tallGrass),
        new SimpleTexture("textures/blocks/tallgrass", Texture.tallGrass),
        new IndexedTexture(0x27, Texture.tallGrass)));
    allTextures.put("beacon", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beacon", Texture.beacon),
        new SimpleTexture("assets/minecraft/textures/blocks/beacon", Texture.beacon),
        new SimpleTexture("textures/blocks/beacon", Texture.beacon),
        new IndexedTexture(0x29, Texture.beacon)));
    allTextures.put("crafting_table_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/crafting_table_top",
            Texture.workbenchTop),
        new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_top",
            Texture.workbenchTop),
        new SimpleTexture("textures/blocks/workbench_top", Texture.workbenchTop),
        new IndexedTexture(0x2B, Texture.workbenchTop)));
    allTextures.put("furnace_front_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_front",
            Texture.furnaceUnlitFront),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_front",
            Texture.furnaceUnlitFront),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_front_off",
            Texture.furnaceUnlitFront),
        new SimpleTexture("textures/blocks/furnace_front", Texture.furnaceUnlitFront),
        new IndexedTexture(0x2C, Texture.furnaceUnlitFront)));
    allTextures.put("furnace_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_side", Texture.furnaceSide),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_side", Texture.furnaceSide),
        new SimpleTexture("textures/blocks/furnace_side", Texture.furnaceSide),
        new IndexedTexture(0x2D, Texture.furnaceSide)));
    allTextures.put("dispenser_front_horizontal", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dispenser_front",
            Texture.dispenserFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dispenser_front",
            Texture.dispenserFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dispenser_front_horizontal",
            Texture.dispenserFront),
        new SimpleTexture("textures/blocks/dispenser_front", Texture.dispenserFront),
        new IndexedTexture(0x2E, Texture.dispenserFront)));
    allTextures.put("dispenser_front_vertical", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dispenser_front_vertical",
            Texture.dispenserFrontVertical),
        new SimpleTexture("assets/minecraft/textures/blocks/dispenser_front_vertical",
            Texture.dispenserFrontVertical)));

    allTextures.put("sponge", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sponge", Texture.sponge),
        new SimpleTexture("assets/minecraft/textures/blocks/sponge", Texture.sponge),
        new SimpleTexture("textures/blocks/sponge", Texture.sponge),
        new IndexedTexture(0x30, Texture.sponge)));
    allTextures.put("glass", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/glass", Texture.glass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass", Texture.glass),
        new SimpleTexture("textures/blocks/glass", Texture.glass),
        new IndexedTexture(0x31, Texture.glass)));
    allTextures.put("diamond_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/diamond_ore", Texture.diamondOre),
        new SimpleTexture("assets/minecraft/textures/blocks/diamond_ore", Texture.diamondOre),
        new SimpleTexture("textures/blocks/oreDiamond", Texture.diamondOre),
        new IndexedTexture(0x32, Texture.diamondOre)));
    allTextures.put("redstone_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_ore", Texture.redstoneOre),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_ore", Texture.redstoneOre),
        new SimpleTexture("textures/blocks/oreRedstone", Texture.redstoneOre),
        new IndexedTexture(0x33, Texture.redstoneOre)));
    allTextures.put("oak_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_leaves", Texture.oakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_leaves", Texture.oakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_oak", Texture.oakLeaves),
        new SimpleTexture("textures/blocks/leaves", Texture.oakLeaves),
        new IndexedTexture(0x34, Texture.oakLeaves)));
    allTextures.put("stone_brick", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/stone_bricks", Texture.stoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_bricks", Texture.stoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick", Texture.stoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth", Texture.stoneBrick),
        new IndexedTexture(0x36, Texture.stoneBrick)));
    allTextures.put("dead_bush", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dead_bush", Texture.deadBush),
        new SimpleTexture("assets/minecraft/textures/blocks/dead_bush", Texture.deadBush),
        new SimpleTexture("assets/minecraft/textures/blocks/deadbush", Texture.deadBush),
        new SimpleTexture("textures/blocks/deadbush", Texture.deadBush),
        new IndexedTexture(0x37, Texture.deadBush)));
    allTextures.put("fern", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/fern", Texture.fern),
        new SimpleTexture("assets/minecraft/textures/blocks/fern", Texture.fern),
        new SimpleTexture("textures/blocks/fern", Texture.fern),
        new IndexedTexture(0x38, Texture.fern)));
    allTextures.put("crafting_table_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/crafting_table_side",
            Texture.workbenchSide),
        new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_side",
            Texture.workbenchSide),
        new SimpleTexture("textures/blocks/workbench_side", Texture.workbenchSide),
        new IndexedTexture(0x3B, Texture.workbenchSide)));
    allTextures.put("crafting_table_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/crafting_table_front",
            Texture.workbenchFront),
        new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_front",
            Texture.workbenchFront),
        new SimpleTexture("textures/blocks/workbench_front", Texture.workbenchFront),
        new IndexedTexture(0x3C, Texture.workbenchFront)));
    allTextures.put("furnace_front_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_front_on",
            Texture.furnaceLitFront),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_front_on",
            Texture.furnaceLitFront),
        new SimpleTexture("textures/blocks/furnace_front_lit", Texture.furnaceLitFront),
        new IndexedTexture(0x3D, Texture.furnaceLitFront)));
    allTextures.put("furnace_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_top", Texture.furnaceTop),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_top", Texture.furnaceTop),
        new SimpleTexture("textures/blocks/furnace_top", Texture.furnaceTop),
        new IndexedTexture(0x3E, Texture.furnaceTop)));
    allTextures.put("spruce_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_sapling", Texture.spruceSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_sapling", Texture.spruceSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_spruce", Texture.spruceSapling),
        new SimpleTexture("textures/blocks/sapling_spruce", Texture.spruceSapling),
        new IndexedTexture(0x3F, Texture.spruceSapling)));

    allTextures.put("white_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_wool", Texture.whiteWool),
        new SimpleTexture("assets/minecraft/textures/blocks/white_wool", Texture.whiteWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_white", Texture.whiteWool),
        new SimpleTexture("textures/blocks/cloth_0", Texture.whiteWool),
        new IndexedTexture(0x40, Texture.whiteWool)));
    allTextures.put("mob spawner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spawner", Texture.monsterSpawner),
        new SimpleTexture("assets/minecraft/textures/blocks/mob_spawner", Texture.monsterSpawner),
        new SimpleTexture("textures/blocks/mobSpawner", Texture.monsterSpawner),
        new IndexedTexture(0x41, Texture.monsterSpawner)));
    allTextures.put("snow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/snow", Texture.snowBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/snow", Texture.snowBlock),
        new SimpleTexture("textures/blocks/snow", Texture.snowBlock),
        new IndexedTexture(0x42, Texture.snowBlock)));
    allTextures.put("ice", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/ice", Texture.ice),
        new SimpleTexture("assets/minecraft/textures/blocks/ice", Texture.ice),
        new SimpleTexture("textures/blocks/ice", Texture.ice),
        new IndexedTexture(0x43, Texture.ice)));
    allTextures.put("grass_block_snow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_snow", Texture.snowSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_snow", Texture.snowSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_side_snowed", Texture.snowSide),
        new SimpleTexture("textures/blocks/snow_side", Texture.snowSide),
        new IndexedTexture(0x44, Texture.snowSide)));
    allTextures.put("cactus_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cactus_top", Texture.cactusTop),
        new SimpleTexture("assets/minecraft/textures/blocks/cactus_top", Texture.cactusTop),
        new SimpleTexture("textures/blocks/cactus_top", Texture.cactusTop),
        new IndexedTexture(0x45, Texture.cactusTop)));
    allTextures.put("cactus_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cactus_side", Texture.cactusSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cactus_side", Texture.cactusSide),
        new SimpleTexture("textures/blocks/cactus_side", Texture.cactusSide),
        new IndexedTexture(0x46, Texture.cactusSide)));
    allTextures.put("cactus_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cactus_bottom", Texture.cactusBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/cactus_bottom", Texture.cactusBottom),
        new SimpleTexture("textures/blocks/cactus_bottom", Texture.cactusBottom),
        new IndexedTexture(0x47, Texture.cactusBottom)));
    allTextures.put("clay", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/clay", Texture.clay),
        new SimpleTexture("assets/minecraft/textures/blocks/clay", Texture.clay),
        new SimpleTexture("textures/blocks/clay", Texture.clay),
        new IndexedTexture(0x48, Texture.clay)));
    allTextures.put("sugar_cane", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sugar_cane", Texture.sugarCane),
        new SimpleTexture("assets/minecraft/textures/blocks/sugar_cane", Texture.sugarCane),
        new SimpleTexture("assets/minecraft/textures/blocks/reeds", Texture.sugarCane),
        new SimpleTexture("textures/blocks/reeds", Texture.sugarCane),
        new IndexedTexture(0x49, Texture.sugarCane)));
    allTextures.put("note_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/note_block", Texture.jukeboxSide),
        new SimpleTexture("assets/minecraft/textures/blocks/note_block", Texture.jukeboxSide),
        new SimpleTexture("assets/minecraft/textures/blocks/noteblock", Texture.jukeboxSide),
        new SimpleTexture("textures/blocks/musicBlock", Texture.jukeboxSide),
        new IndexedTexture(0x4A, Texture.jukeboxSide)));
    allTextures.put("jukebox_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jukebox_top", Texture.jukeboxTop),
        new SimpleTexture("assets/minecraft/textures/blocks/jukebox_top", Texture.jukeboxTop),
        new SimpleTexture("textures/blocks/jukebox_top", Texture.jukeboxTop),
        new IndexedTexture(0x4B, Texture.jukeboxTop)));
    allTextures.put("lily_pad", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lily_pad", Texture.lilyPad),
        new SimpleTexture("assets/minecraft/textures/blocks/lily_pad", Texture.lilyPad),
        new SimpleTexture("assets/minecraft/textures/blocks/waterlily", Texture.lilyPad),
        new SimpleTexture("textures/blocks/waterlily", Texture.lilyPad),
        new IndexedTexture(0x4C, Texture.lilyPad)));
    allTextures.put("mycelium_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mycelium_side", Texture.myceliumSide),
        new SimpleTexture("assets/minecraft/textures/blocks/mycelium_side", Texture.myceliumSide),
        new SimpleTexture("textures/blocks/mycel_side", Texture.myceliumSide),
        new IndexedTexture(0x4D, Texture.myceliumSide)));
    allTextures.put("mycelium_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mycelium_top", Texture.myceliumTop),
        new SimpleTexture("assets/minecraft/textures/blocks/mycelium_top", Texture.myceliumTop),
        new SimpleTexture("textures/blocks/mycel_top", Texture.myceliumTop),
        new IndexedTexture(0x4E, Texture.myceliumTop)));
    allTextures.put("birch_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_sapling", Texture.birchSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_sapling", Texture.birchSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_birch", Texture.birchSapling),
        new SimpleTexture("textures/blocks/sapling_birch", Texture.birchSapling),
        new IndexedTexture(0x4F, Texture.birchSapling)));

    allTextures.put("torch", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/torch", Texture.torch),
        new SimpleTexture("assets/minecraft/textures/blocks/torch", Texture.torch),
        new SimpleTexture("assets/minecraft/textures/blocks/torch_on", Texture.torch),
        new SimpleTexture("textures/blocks/torch", Texture.torch),
        new IndexedTexture(0x50, Texture.torch)));
    allTextures.put("oak_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_door_top",
            Texture.woodenDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_door_upper",
            Texture.woodenDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_wood_upper",
            Texture.woodenDoorTop),
        new SimpleTexture("textures/blocks/doorWood_upper", Texture.woodenDoorTop),
        new IndexedTexture(0x51, Texture.woodenDoorTop)));
    allTextures.put("iron_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_door_top", Texture.ironDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_door_upper", Texture.ironDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_iron_upper", Texture.ironDoorTop),
        new SimpleTexture("textures/blocks/doorIron_upper", Texture.ironDoorTop),
        new IndexedTexture(0x52, Texture.ironDoorTop)));
    allTextures.put("ladder", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/ladder", Texture.ladder),
        new SimpleTexture("assets/minecraft/textures/blocks/ladder", Texture.ladder),
        new SimpleTexture("textures/blocks/ladder", Texture.ladder),
        new IndexedTexture(0x53, Texture.ladder)));
    allTextures.put("trapdoor", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_trapdoor", Texture.trapdoor),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_trapdoor", Texture.trapdoor),
        new SimpleTexture("assets/minecraft/textures/blocks/trapdoor", Texture.trapdoor),
        new SimpleTexture("textures/blocks/trapdoor", Texture.trapdoor),
        new IndexedTexture(0x54, Texture.trapdoor)));
    allTextures.put("iron bars", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_bars", Texture.ironBars),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_bars", Texture.ironBars),
        new SimpleTexture("textures/blocks/fenceIron", Texture.ironBars),
        new IndexedTexture(0x55, Texture.ironBars)));
    allTextures.put("farmland_wet", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/farmland_moist", Texture.farmlandWet),
        new SimpleTexture("assets/minecraft/textures/blocks/farmland_wet", Texture.farmlandWet),
        new SimpleTexture("textures/blocks/farmland_wet", Texture.farmlandWet),
        new IndexedTexture(0x56, Texture.farmlandWet)));
    allTextures.put("farmland_dry", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/farmland", Texture.farmlandDry),
        new SimpleTexture("assets/minecraft/textures/blocks/farmland", Texture.farmlandDry),
        new SimpleTexture("assets/minecraft/textures/blocks/farmland_dry", Texture.farmlandDry),
        new SimpleTexture("textures/blocks/farmland_dry", Texture.farmlandDry),
        new IndexedTexture(0x57, Texture.farmlandDry)));
    allTextures.put("wheat_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage0", Texture.crops0),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage0", Texture.crops0),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_0", Texture.crops0),
        new SimpleTexture("textures/blocks/crops_0", Texture.crops0),
        new IndexedTexture(0x58, Texture.crops0)));
    allTextures.put("wheat_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage1", Texture.crops1),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage1", Texture.crops1),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_1", Texture.crops1),
        new SimpleTexture("textures/blocks/crops_1", Texture.crops1),
        new IndexedTexture(0x59, Texture.crops1)));
    allTextures.put("wheat_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage2", Texture.crops2),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage2", Texture.crops2),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_2", Texture.crops2),
        new SimpleTexture("textures/blocks/crops_2", Texture.crops2),
        new IndexedTexture(0x5A, Texture.crops2)));
    allTextures.put("wheat_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage3", Texture.crops3),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage3", Texture.crops3),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_3", Texture.crops3),
        new SimpleTexture("textures/blocks/crops_3", Texture.crops3),
        new IndexedTexture(0x5B, Texture.crops3)));
    allTextures.put("wheat_stage_4", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage4", Texture.crops4),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage4", Texture.crops4),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_4", Texture.crops4),
        new SimpleTexture("textures/blocks/crops_4", Texture.crops4),
        new IndexedTexture(0x5C, Texture.crops4)));
    allTextures.put("wheat_stage_5", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage5", Texture.crops5),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage5", Texture.crops5),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_5", Texture.crops5),
        new SimpleTexture("textures/blocks/crops_5", Texture.crops5),
        new IndexedTexture(0x5D, Texture.crops5)));
    allTextures.put("wheat_stage_6", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage6", Texture.crops6),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage6", Texture.crops6),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_6", Texture.crops6),
        new SimpleTexture("textures/blocks/crops_6", Texture.crops6),
        new IndexedTexture(0x5E, Texture.crops6)));
    allTextures.put("wheat_stage_7", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage7", Texture.crops7),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage7", Texture.crops7),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_7", Texture.crops7),
        new SimpleTexture("textures/blocks/crops_7", Texture.crops7),
        new IndexedTexture(0x5F, Texture.crops7)));

    allTextures.put("lever", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lever", Texture.lever),
        new SimpleTexture("assets/minecraft/textures/blocks/lever", Texture.lever),
        new SimpleTexture("textures/blocks/lever", Texture.lever),
        new IndexedTexture(0x60, Texture.lever)));
    allTextures.put("oak_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_door_bottom",
            Texture.woodenDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_door_lower",
            Texture.woodenDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_wood_lower",
            Texture.woodenDoorBottom),
        new SimpleTexture("textures/blocks/doorWood_lower", Texture.woodenDoorBottom),
        new IndexedTexture(0x61, Texture.woodenDoorBottom)));
    allTextures.put("iron_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_door_bottom",
            Texture.ironDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_door_lower",
            Texture.ironDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_iron_lower",
            Texture.ironDoorBottom),
        new SimpleTexture("textures/blocks/doorIron_lower", Texture.ironDoorBottom),
        new IndexedTexture(0x62, Texture.ironDoorBottom)));
    allTextures.put("redstone_torch_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_torch",
            Texture.redstoneTorchOn),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch",
            Texture.redstoneTorchOn),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch_on",
            Texture.redstoneTorchOn),
        new SimpleTexture("textures/blocks/redtorch_lit", Texture.redstoneTorchOn),
        new IndexedTexture(0x63, Texture.redstoneTorchOn)));
    allTextures.put("stonebrick_mossy", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mossy_stone_bricks",
            Texture.mossyStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/mossy_stone_bricks",
            Texture.mossyStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_mossy",
            Texture.mossyStoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth_mossy", Texture.mossyStoneBrick),
        new IndexedTexture(0x64, Texture.mossyStoneBrick)));
    allTextures.put("stonebrick_cracked", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cracked_stone_bricks",
            Texture.crackedStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/cracked_stone_bricks",
            Texture.crackedStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_cracked",
            Texture.crackedStoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth_cracked", Texture.crackedStoneBrick),
        new IndexedTexture(0x65, Texture.crackedStoneBrick)));
    allTextures.put("pumpkin_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pumpkin_top", Texture.pumpkinTop),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_top", Texture.pumpkinTop),
        new SimpleTexture("textures/blocks/pumpkin_top", Texture.pumpkinTop),
        new IndexedTexture(0x66, Texture.pumpkinTop)));
    allTextures.put("netherrack", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/netherrack", Texture.netherrack),
        new SimpleTexture("assets/minecraft/textures/blocks/netherrack", Texture.netherrack),
        new SimpleTexture("textures/blocks/hellrock", Texture.netherrack),
        new IndexedTexture(0x67, Texture.netherrack)));
    allTextures.put("soul_sand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/soul_sand", Texture.soulsand),
        new SimpleTexture("assets/minecraft/textures/blocks/soul_sand", Texture.soulsand),
        new SimpleTexture("textures/blocks/hellsand", Texture.soulsand),
        new IndexedTexture(0x68, Texture.soulsand)));
    allTextures.put("glowstone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/glowstone", Texture.glowstone),
        new SimpleTexture("assets/minecraft/textures/blocks/glowstone", Texture.glowstone),
        new SimpleTexture("textures/blocks/lightgem", Texture.glowstone),
        new IndexedTexture(0x69, Texture.glowstone)));
    allTextures.put("piston_top_sticky", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_top_sticky",
            Texture.pistonTopSticky),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_top_sticky",
            Texture.pistonTopSticky),
        new SimpleTexture("textures/blocks/piston_top_sticky", Texture.pistonTopSticky),
        new IndexedTexture(0x6A, Texture.pistonTopSticky)));
    allTextures.put("piston_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_top", Texture.pistonTop),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_top", Texture.pistonTop),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_top_normal", Texture.pistonTop),
        new SimpleTexture("textures/blocks/piston_top", Texture.pistonTop),
        new IndexedTexture(0x6B, Texture.pistonTop)));
    allTextures.put("piston_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_side", Texture.pistonSide),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_side", Texture.pistonSide),
        new SimpleTexture("textures/blocks/piston_side", Texture.pistonSide),
        new IndexedTexture(0x6C, Texture.pistonSide)));
    allTextures.put("piston_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_bottom", Texture.pistonBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_bottom", Texture.pistonBottom),
        new SimpleTexture("textures/blocks/piston_bottom", Texture.pistonBottom),
        new IndexedTexture(0x6D, Texture.pistonBottom)));
    allTextures.put("piston_inner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_inner", Texture.pistonInnerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_inner", Texture.pistonInnerTop),
        new SimpleTexture("textures/blocks/piston_inner_top", Texture.pistonInnerTop),
        new IndexedTexture(0x6E, Texture.pistonInnerTop)));
    // TODO pumpkin stem variants
    allTextures.put("melon_stem", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/melon_stem",
            Texture.stemStraight),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_stem",
            Texture.stemStraight),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_stem_disconnected",
            Texture.stemStraight),
        new SimpleTexture("textures/blocks/stem_straight", Texture.stemStraight),
        new IndexedTexture(0x6F, Texture.stemStraight)));

    allTextures.put("rail_corner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rail_corner",
            Texture.railsCurved),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_corner",
            Texture.railsCurved),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_normal_turned",
            Texture.railsCurved),
        new SimpleTexture("textures/blocks/rail_turn", Texture.railsCurved),
        new IndexedTexture(0x70, Texture.railsCurved)));
    allTextures.put("black_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_wool", Texture.blackWool),
        new SimpleTexture("assets/minecraft/textures/blocks/black_wool", Texture.blackWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_black", Texture.blackWool),
        new SimpleTexture("textures/blocks/cloth_15", Texture.blackWool),
        new IndexedTexture(0x71, Texture.blackWool)));
    allTextures.put("gray_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_wool", Texture.grayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_wool", Texture.grayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_gray", Texture.grayWool),
        new SimpleTexture("textures/blocks/cloth_7", Texture.grayWool),
        new IndexedTexture(0x72, Texture.grayWool)));
    allTextures.put("redstone_torch_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_torch_off",
            Texture.redstoneTorchOff),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch_off",
            Texture.redstoneTorchOff),
        new SimpleTexture("textures/blocks/redtorch", Texture.redstoneTorchOff),
        new IndexedTexture(0x73, Texture.redstoneTorchOff)));
    allTextures.put("spruce_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_log", Texture.spruceWood),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_log", Texture.spruceWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_spruce", Texture.spruceWood),
        new SimpleTexture("textures/blocks/tree_spruce", Texture.spruceWood),
        new IndexedTexture(0x74, Texture.spruceWood)));
    allTextures.put("birch_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_log", Texture.birchWood),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_log", Texture.birchWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_birch", Texture.birchWood),
        new SimpleTexture("textures/blocks/tree_birch", Texture.birchWood),
        new IndexedTexture(0x75, Texture.birchWood)));
    allTextures.put("pumpkin_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pumpkin_side", Texture.pumpkinSide),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_side", Texture.pumpkinSide),
        new SimpleTexture("textures/blocks/pumpkin_side", Texture.pumpkinSide),
        new IndexedTexture(0x76, Texture.pumpkinSide)));
    allTextures.put("pumpkin_face", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carved_pumpkin",
            Texture.pumpkinFront),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face",
            Texture.pumpkinFront),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face_off",
            Texture.pumpkinFront),
        new SimpleTexture("textures/blocks/pumpkin_face", Texture.pumpkinFront),
        new IndexedTexture(0x77, Texture.pumpkinFront)));
    allTextures.put("pumpkin_face_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jack_o_lantern",
            Texture.jackolanternFront),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face_on",
            Texture.jackolanternFront),
        new SimpleTexture("textures/blocks/pumpkin_jack", Texture.jackolanternFront),
        new IndexedTexture(0x78, Texture.jackolanternFront)));
    allTextures.put("cake_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_top", Texture.cakeTop),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_top", Texture.cakeTop),
        new SimpleTexture("textures/blocks/cake_top", Texture.cakeTop),
        new IndexedTexture(0x79, Texture.cakeTop)));
    allTextures.put("cake_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_side", Texture.cakeSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_side", Texture.cakeSide),
        new SimpleTexture("textures/blocks/cake_side", Texture.cakeSide),
        new IndexedTexture(0x7A, Texture.cakeSide)));
    allTextures.put("cake_inner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_inner", Texture.cakeInside),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_inner", Texture.cakeInside),
        new SimpleTexture("textures/blocks/cake_inner", Texture.cakeInside),
        new IndexedTexture(0x7B, Texture.cakeInside)));
    allTextures.put("cake_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_bottom", Texture.cakeBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_bottom", Texture.cakeBottom),
        new SimpleTexture("textures/blocks/cake_bottom", Texture.cakeBottom),
        new IndexedTexture(0x7C, Texture.cakeBottom)));
    allTextures.put("red_mushroom_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_mushroom_block",
            Texture.hugeRedMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/red_mushroom_block",
            Texture.hugeRedMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_red",
            Texture.hugeRedMushroom),
        new SimpleTexture("textures/blocks/mushroom_skin_red", Texture.hugeRedMushroom),
        new IndexedTexture(0x7D, Texture.hugeRedMushroom)));
    allTextures.put("brown_mushroom_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_mushroom_block",
            Texture.hugeBrownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_mushroom_block",
            Texture.hugeBrownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_brown",
            Texture.hugeBrownMushroom),
        new SimpleTexture("textures/blocks/mushroom_skin_brown", Texture.hugeBrownMushroom),
        new IndexedTexture(0x7E, Texture.hugeBrownMushroom)));
    allTextures.put("melon_stem_connected", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/attached_melon_stem",
            Texture.stemBent),
        new SimpleTexture("assets/minecraft/textures/blocks/attached_melon_stem",
            Texture.stemBent),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_stem_connected",
            Texture.stemBent),
        new SimpleTexture("textures/blocks/stem_bent", Texture.stemBent),
        new IndexedTexture(0x7F, Texture.stemBent)));

    allTextures.put("rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rail", Texture.rails),
        new SimpleTexture("assets/minecraft/textures/blocks/rail", Texture.rails),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_normal", Texture.rails),
        new SimpleTexture("textures/blocks/rail", Texture.rails),
        new IndexedTexture(0x80, Texture.rails)));
    allTextures.put("red_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_wool", Texture.redWool),
        new SimpleTexture("assets/minecraft/textures/blocks/red_wool", Texture.redWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_red", Texture.redWool),
        new SimpleTexture("textures/blocks/cloth_14", Texture.redWool),
        new IndexedTexture(0x81, Texture.redWool)));
    allTextures.put("pink_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_wool", Texture.pinkWool),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_wool", Texture.pinkWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_pink", Texture.pinkWool),
        new SimpleTexture("textures/blocks/cloth_6", Texture.pinkWool),
        new IndexedTexture(0x82, Texture.pinkWool)));
    allTextures.put("repeater_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeater",
            Texture.redstoneRepeaterOff),
        new SimpleTexture("assets/minecraft/textures/blocks/repeater",
            Texture.redstoneRepeaterOff),
        new SimpleTexture("assets/minecraft/textures/blocks/repeater_off",
            Texture.redstoneRepeaterOff),
        new SimpleTexture("textures/blocks/repeater", Texture.redstoneRepeaterOff),
        new IndexedTexture(0x83, Texture.redstoneRepeaterOff)));
    allTextures.put("spruce_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_leaves", Texture.spruceLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_leaves", Texture.spruceLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_spruce", Texture.spruceLeaves),
        new SimpleTexture("textures/blocks/leaves_spruce", Texture.spruceLeaves),
        new IndexedTexture(0x84, Texture.spruceLeaves)));
    allTextures.put("melon_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/melon_side", Texture.melonSide),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_side", Texture.melonSide),
        new SimpleTexture("textures/blocks/melon_side", Texture.melonSide),
        new IndexedTexture(0x88, Texture.melonSide)));
    allTextures.put("melon_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/melon_top", Texture.melonTop),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_top", Texture.melonTop),
        new SimpleTexture("textures/blocks/melon_top", Texture.melonTop),
        new IndexedTexture(0x89, Texture.melonTop)));
    allTextures.put("cauldron_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_top", Texture.cauldronTop),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_top", Texture.cauldronTop),
        new SimpleTexture("textures/blocks/cauldron_top", Texture.cauldronTop),
        new IndexedTexture(0x8A, Texture.cauldronTop)));
    allTextures.put("cauldron_inner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_inner",
            Texture.cauldronInside),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_inner",
            Texture.cauldronInside),
        new SimpleTexture("textures/blocks/cauldron_inner", Texture.cauldronInside),
        new IndexedTexture(0x8B, Texture.cauldronInside)));
    allTextures.put("mushroom_stem", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mushroom_stem",
            Texture.mushroomStem),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_stem",
            Texture.mushroomStem),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_stem",
            Texture.mushroomStem),
        new SimpleTexture("textures/blocks/mushroom_skin_stem", Texture.mushroomStem),
        new IndexedTexture(0x8D, Texture.mushroomStem)));
    allTextures.put("mushroom_block_inside", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mushroom_block_inside",
            Texture.mushroomPores),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_inside",
            Texture.mushroomPores),
        new SimpleTexture("textures/blocks/mushroom_inside", Texture.mushroomPores),
        new IndexedTexture(0x8E, Texture.mushroomPores)));
    allTextures.put("vine", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/vine", Texture.vines),
        new SimpleTexture("assets/minecraft/textures/blocks/vine", Texture.vines),
        new SimpleTexture("textures/blocks/vine", Texture.vines),
        new IndexedTexture(0x8F, Texture.vines)));

    allTextures.put("lapis_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lapis_block", Texture.lapisBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/lapis_block", Texture.lapisBlock),
        new SimpleTexture("textures/blocks/blockLapis", Texture.lapisBlock),
        new IndexedTexture(0x90, Texture.lapisBlock)));
    allTextures.put("green_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_wool", Texture.greenWool),
        new SimpleTexture("assets/minecraft/textures/blocks/green_wool", Texture.greenWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_green", Texture.greenWool),
        new SimpleTexture("textures/blocks/cloth_13", Texture.greenWool),
        new IndexedTexture(0x91, Texture.greenWool)));
    allTextures.put("lime_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_wool", Texture.limeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_wool", Texture.limeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_lime", Texture.limeWool),
        new SimpleTexture("textures/blocks/cloth_5", Texture.limeWool),
        new IndexedTexture(0x92, Texture.limeWool)));
    allTextures.put("repeater_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeater_on",
            Texture.redstoneRepeaterOn),
        new SimpleTexture("assets/minecraft/textures/blocks/repeater_on",
            Texture.redstoneRepeaterOn),
        new SimpleTexture("textures/blocks/repeater_lit", Texture.redstoneRepeaterOn),
        new IndexedTexture(0x93, Texture.redstoneRepeaterOn)));
    allTextures.put("glass_pane_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/glass_pane_top", Texture.glassPaneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top", Texture.glassPaneTop),
        new SimpleTexture("textures/blocks/thinglass_top", Texture.glassPaneTop),
        new IndexedTexture(0x94, Texture.glassPaneTop)));
    allTextures.put("jungle_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_log", Texture.jungleWood),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_log", Texture.jungleWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_jungle", Texture.jungleWood),
        new SimpleTexture("textures/blocks/tree_jungle", Texture.jungleWood),
        new IndexedTexture(0x99, Texture.jungleWood)));
    allTextures.put("cauldron_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_side", Texture.cauldronSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_side", Texture.cauldronSide),
        new SimpleTexture("textures/blocks/cauldron_side", Texture.cauldronSide),
        new IndexedTexture(0x9A, Texture.cauldronSide)));
    allTextures.put("cauldron_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_bottom",
            Texture.cauldronBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_bottom",
            Texture.cauldronBottom),
        new SimpleTexture("textures/blocks/cauldron_bottom", Texture.cauldronBottom),
        new IndexedTexture(0x9B, Texture.cauldronBottom)));
    allTextures.put("brewing_stand_base", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brewing_stand_base",
            Texture.brewingStandBase),
        new SimpleTexture("assets/minecraft/textures/blocks/brewing_stand_base",
            Texture.brewingStandBase),
        new SimpleTexture("textures/blocks/brewingStand_base", Texture.brewingStandBase),
        new IndexedTexture(0x9C, Texture.brewingStandBase)));
    allTextures.put("brewing_stand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brewing_stand",
            Texture.brewingStandSide),
        new SimpleTexture("assets/minecraft/textures/blocks/brewing_stand",
            Texture.brewingStandSide),
        new SimpleTexture("textures/blocks/brewingStand", Texture.brewingStandSide),
        new IndexedTexture(0x9D, Texture.brewingStandSide)));
    allTextures.put("endframe_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_portal_frame_top",
            Texture.endPortalFrameTop),
        new SimpleTexture("assets/minecraft/textures/blocks/end_portal_frame_top",
            Texture.endPortalFrameTop),
        new SimpleTexture("assets/minecraft/textures/blocks/endframe_top",
            Texture.endPortalFrameTop),
        new SimpleTexture("textures/blocks/endframe_top", Texture.endPortalFrameTop),
        new IndexedTexture(0x9E, Texture.endPortalFrameTop)));
    allTextures.put("endframe_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_portal_frame_side",
            Texture.endPortalFrameSide),
        new SimpleTexture("assets/minecraft/textures/blocks/end_portal_frame_side",
            Texture.endPortalFrameSide),
        new SimpleTexture("assets/minecraft/textures/blocks/endframe_side",
            Texture.endPortalFrameSide),
        new SimpleTexture("textures/blocks/endframe_side", Texture.endPortalFrameSide),
        new IndexedTexture(0x9F, Texture.endPortalFrameSide)));

    allTextures.put("lapis_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lapis_ore", Texture.lapisOre),
        new SimpleTexture("assets/minecraft/textures/blocks/lapis_ore", Texture.lapisOre),
        new SimpleTexture("textures/blocks/oreLapis", Texture.lapisOre),
        new IndexedTexture(0xA0, Texture.lapisOre)));
    allTextures.put("brown_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_wool", Texture.brownWool),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_wool", Texture.brownWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_brown", Texture.brownWool),
        new SimpleTexture("textures/blocks/cloth_12", Texture.brownWool),
        new IndexedTexture(0xA1, Texture.brownWool)));
    allTextures.put("yellow_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_wool", Texture.yellowWool),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_wool", Texture.yellowWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_yellow",
            Texture.yellowWool),
        new SimpleTexture("textures/blocks/cloth_4", Texture.yellowWool),
        new IndexedTexture(0xA2, Texture.yellowWool)));
    allTextures.put("powered_rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/powered_rail", Texture.poweredRailOff),
        new SimpleTexture("assets/minecraft/textures/blocks/powered_rail", Texture.poweredRailOff),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_golden", Texture.poweredRailOff),
        new SimpleTexture("textures/blocks/goldenRail", Texture.poweredRailOff),
        new IndexedTexture(0xA3, Texture.poweredRailOff)));
    allTextures.put("enchanting_table_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/enchanting_table_top",
            Texture.enchantmentTableTop),
        new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_top",
            Texture.enchantmentTableTop),
        new SimpleTexture("textures/blocks/enchantment_top", Texture.enchantmentTableTop),
        new IndexedTexture(0xA6, Texture.enchantmentTableTop)));
    allTextures.put("dragon_egg", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dragon_egg", Texture.dragonEgg),
        new SimpleTexture("assets/minecraft/textures/blocks/dragon_egg", Texture.dragonEgg),
        new SimpleTexture("textures/blocks/dragonEgg", Texture.dragonEgg),
        new IndexedTexture(0xA7, Texture.dragonEgg)));
    allTextures.put("cocoa_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cocoa_stage2",
            Texture.cocoaPlantLarge),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage2",
            Texture.cocoaPlantLarge),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_2",
            Texture.cocoaPlantLarge),
        new SimpleTexture("textures/blocks/cocoa_2", Texture.cocoaPlantLarge),
        new IndexedTexture(0xA8, Texture.cocoaPlantLarge)));
    allTextures.put("cocoa_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cocoa_stage1",
            Texture.cocoaPlantMedium),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage1",
            Texture.cocoaPlantMedium),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_1",
            Texture.cocoaPlantMedium),
        new SimpleTexture("textures/blocks/cocoa_1", Texture.cocoaPlantMedium),
        new IndexedTexture(0xA9, Texture.cocoaPlantMedium)));
    allTextures.put("cocoa_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cocoa_stage0",
            Texture.cocoaPlantSmall),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage0",
            Texture.cocoaPlantSmall),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_0",
            Texture.cocoaPlantSmall),
        new SimpleTexture("textures/blocks/cocoa_0", Texture.cocoaPlantSmall),
        new IndexedTexture(0xAA, Texture.cocoaPlantSmall)));
    allTextures.put("emerald_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/emerald_ore", Texture.emeraldOre),
        new SimpleTexture("assets/minecraft/textures/blocks/emerald_ore", Texture.emeraldOre),
        new SimpleTexture("textures/blocks/oreEmerald", Texture.emeraldOre),
        new IndexedTexture(0xAB, Texture.emeraldOre)));
    allTextures.put("trip_wire_hook", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tripwire_hook",
            Texture.tripwireHook),
        new SimpleTexture("assets/minecraft/textures/blocks/trip_wire_hook",
            Texture.tripwireHook),
        new SimpleTexture("assets/minecraft/textures/blocks/trip_wire_source",
            Texture.tripwireHook),
        new SimpleTexture("textures/blocks/tripWireSource", Texture.tripwireHook),
        new IndexedTexture(0xAC, Texture.tripwireHook)));
    allTextures.put("trip_wire", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tripwire", Texture.tripwire),
        new SimpleTexture("assets/minecraft/textures/blocks/trip_wire", Texture.tripwire),
        new SimpleTexture("textures/blocks/tripWire", Texture.tripwire),
        new IndexedTexture(0xAD, Texture.tripwire)));
    allTextures.put("endframe_eye", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_portal_frame_eye",
            Texture.eyeOfTheEnder),
        new SimpleTexture("assets/minecraft/textures/blocks/end_portal_frame_eye",
            Texture.eyeOfTheEnder),
        new SimpleTexture("assets/minecraft/textures/blocks/endframe_eye", Texture.eyeOfTheEnder),
        new SimpleTexture("textures/blocks/endframe_eye", Texture.eyeOfTheEnder),
        new IndexedTexture(0xAE, Texture.eyeOfTheEnder)));
    allTextures.put("end_stone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_stone", Texture.endStone),
        new SimpleTexture("assets/minecraft/textures/blocks/end_stone", Texture.endStone),
        new SimpleTexture("textures/blocks/whiteStone", Texture.endStone),
        new IndexedTexture(0xAF, Texture.endStone)));

    allTextures.put("sandstone_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sandstone_top", Texture.sandstoneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_top", Texture.sandstoneTop),
        new SimpleTexture("textures/blocks/sandstone_top", Texture.sandstoneTop),
        new IndexedTexture(0xB0, Texture.sandstoneTop)));
    allTextures.put("blue_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_wool", Texture.blueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_wool", Texture.blueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_blue", Texture.blueWool),
        new SimpleTexture("textures/blocks/cloth_11", Texture.blueWool),
        new IndexedTexture(0xB1, Texture.blueWool)));
    allTextures.put("light_blue_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_wool",
            Texture.lightBlueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_wool",
            Texture.lightBlueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_light_blue",
            Texture.lightBlueWool),
        new SimpleTexture("textures/blocks/cloth_3", Texture.lightBlueWool),
        new IndexedTexture(0xB2, Texture.lightBlueWool)));
    allTextures.put("powered_rail_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/powered_rail_on",
            Texture.poweredRailOn),
        new SimpleTexture("assets/minecraft/textures/blocks/powered_rail_on",
            Texture.poweredRailOn),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_golden_powered",
            Texture.poweredRailOn),
        new SimpleTexture("textures/blocks/goldenRail_powered", Texture.poweredRailOn),
        new IndexedTexture(0xB3, Texture.poweredRailOn)));
    allTextures.put("enchanting_table_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/enchanting_table_side",
            Texture.enchantmentTableSide),
        new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_side",
            Texture.enchantmentTableSide),
        new SimpleTexture("textures/blocks/enchantment_side", Texture.enchantmentTableSide),
        new IndexedTexture(0xB6, Texture.enchantmentTableSide)));
    allTextures.put("enchanting_table_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/enchanting_table_bottom",
            Texture.enchantmentTableBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_bottom",
            Texture.enchantmentTableBottom),
        new SimpleTexture("textures/blocks/enchantment_bottom", Texture.enchantmentTableBottom),
        new IndexedTexture(0xB7, Texture.enchantmentTableBottom)));

    // Command block textures were changed in Minecraft 1.9.
    allTextures.put("command_block_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_back",
            Texture.commandBlockBack),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_back",
            Texture.commandBlockBack)));
    allTextures.put("command_block_conditional", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_conditional",
            Texture.commandBlockConditional),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_conditional",
            Texture.commandBlockConditional)));
    allTextures.put("command_block_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_front",
            Texture.commandBlockFront),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_front",
            Texture.commandBlockFront)));
    allTextures.put("command_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_side",
            Texture.commandBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_side",
            Texture.commandBlockSide)));

    allTextures.put("repeating_command_block_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_back",
            Texture.repeatingCommandBlockBack),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_back",
            Texture.repeatingCommandBlockBack)));
    allTextures.put("repeating_command_block_conditional", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_conditional",
            Texture.repeatingCommandBlockConditional),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_conditional",
            Texture.repeatingCommandBlockConditional)));
    allTextures.put("repeating_command_block_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_front",
            Texture.repeatingCommandBlockFront),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_front",
            Texture.repeatingCommandBlockFront)));
    allTextures.put("repeating_command_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_side",
            Texture.repeatingCommandBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_side",
            Texture.repeatingCommandBlockSide)));

    allTextures.put("chain_command_block_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_back",
            Texture.chainCommandBlockBack),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_back",
            Texture.chainCommandBlockBack)));
    allTextures.put("chain_command_block_conditional", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_conditional",
            Texture.chainCommandBlockConditional),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_conditional",
            Texture.chainCommandBlockConditional)));
    allTextures.put("chain_command_block_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_front",
            Texture.chainCommandBlockFront),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_front",
            Texture.chainCommandBlockFront)));
    allTextures.put("chain_command_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_side",
            Texture.chainCommandBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_side",
            Texture.chainCommandBlockSide)));

    allTextures.put("flower_pot", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/flower_pot", Texture.flowerPot),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_pot", Texture.flowerPot),
        new SimpleTexture("textures/blocks/flowerPot", Texture.flowerPot),
        new IndexedTexture(0xBA, Texture.flowerPot)));
    allTextures.put("quartz_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_quartz_ore", Texture.netherQuartzOre),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_ore", Texture.netherQuartzOre),
        new SimpleTexture("textures/blocks/netherquartz", Texture.netherQuartzOre),
        new IndexedTexture(0xBF, Texture.netherQuartzOre)));

    allTextures.put("sandstone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sandstone",
            Texture.sandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone",
            Texture.sandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_normal",
            Texture.sandstoneSide),
        new SimpleTexture("textures/blocks/sandstone_side", Texture.sandstoneSide),
        new IndexedTexture(0xC0, Texture.sandstoneSide)));
    allTextures.put("purple_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_wool", Texture.purpleWool),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_wool", Texture.purpleWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_purple",
            Texture.purpleWool),
        new SimpleTexture("textures/blocks/cloth_10", Texture.purpleWool),
        new IndexedTexture(0xC1, Texture.purpleWool)));
    allTextures.put("magenta_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_wool", Texture.magentaWool),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_wool", Texture.magentaWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_magenta",
            Texture.magentaWool),
        new SimpleTexture("textures/blocks/cloth_2", Texture.magentaWool),
        new IndexedTexture(0xC2, Texture.magentaWool)));
    allTextures.put("detector_rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/detector_rail", Texture.detectorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/detector_rail", Texture.detectorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_detector", Texture.detectorRail),
        new SimpleTexture("textures/blocks/detectorRail", Texture.detectorRail),
        new IndexedTexture(0xC3, Texture.detectorRail)));
    // Since 1.5:
    allTextures.put("detector_rail_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/detector_rail_on", Texture.detectorRailOn),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_detector_powered", Texture.detectorRailOn)));
    allTextures.put("jungle_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_leaves",
            Texture.jungleTreeLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_leaves",
            Texture.jungleTreeLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_jungle",
            Texture.jungleTreeLeaves),
        new SimpleTexture("textures/blocks/leaves_jungle", Texture.jungleTreeLeaves),
        new IndexedTexture(0xC4, Texture.jungleTreeLeaves)));
    allTextures.put("spruce_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_planks", Texture.sprucePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_planks", Texture.sprucePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_spruce", Texture.sprucePlanks),
        new SimpleTexture("textures/blocks/wood_spruce", Texture.sprucePlanks),
        new IndexedTexture(0xC6, Texture.sprucePlanks)));
    allTextures.put("jungle_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_planks",
            Texture.jungleTreePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_planks",
            Texture.jungleTreePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_jungle",
            Texture.jungleTreePlanks),
        new SimpleTexture("textures/blocks/wood_jungle", Texture.jungleTreePlanks),
        new IndexedTexture(0xC7, Texture.jungleTreePlanks)));
    allTextures.put("carrots_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage0", Texture.carrots0),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage0", Texture.carrots0),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_0", Texture.carrots0),
        new SimpleTexture("textures/blocks/carrots_0", Texture.carrots0),
        new IndexedTexture(0xC8, Texture.carrots0)));
    allTextures.put("potatoes_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage0", Texture.potatoes0),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage0", Texture.potatoes0),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_0", Texture.potatoes0),
        new SimpleTexture("textures/blocks/potatoes_0", Texture.potatoes0),
        new IndexedTexture(0xC8, Texture.potatoes0)));
    allTextures.put("carrots_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage1", Texture.carrots1),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage1", Texture.carrots1),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_1", Texture.carrots1),
        new SimpleTexture("textures/blocks/carrots_1", Texture.carrots1),
        new IndexedTexture(0xC9, Texture.carrots1)));
    allTextures.put("potatoes_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage1", Texture.potatoes1),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage1", Texture.potatoes1),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_1", Texture.potatoes1),
        new SimpleTexture("textures/blocks/potatoes_1", Texture.potatoes1),
        new IndexedTexture(0xC9, Texture.potatoes1)));
    allTextures.put("carrots_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage2", Texture.carrots2),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage2", Texture.carrots2),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_2", Texture.carrots2),
        new SimpleTexture("textures/blocks/carrots_2", Texture.carrots2),
        new IndexedTexture(0xCA, Texture.carrots2)));
    allTextures.put("potatoes_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage2", Texture.potatoes2),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage2", Texture.potatoes2),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_2", Texture.potatoes2),
        new SimpleTexture("textures/blocks/potatoes_2", Texture.potatoes2),
        new IndexedTexture(0xCA, Texture.potatoes2)));
    allTextures.put("carrots_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage3", Texture.carrots3),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage3", Texture.carrots3),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_3", Texture.carrots3),
        new SimpleTexture("textures/blocks/carrots_3", Texture.carrots3),
        new IndexedTexture(0xCB, Texture.carrots3)));
    allTextures.put("potatoes_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage3", Texture.potatoes3),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage3", Texture.potatoes3),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_3", Texture.potatoes3),
        new SimpleTexture("textures/blocks/potatoes_3", Texture.potatoes3),
        new IndexedTexture(0xCC, Texture.potatoes3)));
    allTextures.put("water_still", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/water_still", Texture.water),
        new SimpleTexture("assets/minecraft/textures/blocks/water_still", Texture.water),
        new SimpleTexture("textures/blocks/water", Texture.water),
        new IndexedTexture(0xCD, Texture.water)));

    allTextures.put("sandstone_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sandstone_bottom",
            Texture.sandstoneBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_bottom",
            Texture.sandstoneBottom),
        new SimpleTexture("textures/blocks/sandstone_bottom", Texture.sandstoneBottom),
        new IndexedTexture(0xD0, Texture.sandstoneBottom)));
    allTextures.put("cyan_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_wool", Texture.cyanWool),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_wool", Texture.cyanWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_cyan", Texture.cyanWool),
        new SimpleTexture("textures/blocks/cloth_9", Texture.cyanWool),
        new IndexedTexture(0xD1, Texture.cyanWool)));
    allTextures.put("orange_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_wool", Texture.orangeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_wool", Texture.orangeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_orange",
            Texture.orangeWool),
        new SimpleTexture("textures/blocks/cloth_1", Texture.orangeWool),
        new IndexedTexture(0xD2, Texture.orangeWool)));
    allTextures.put("redstone_lamp_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_lamp", Texture.redstoneLampOff),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_lamp_off",
            Texture.redstoneLampOff),
        new SimpleTexture("textures/blocks/redstoneLight", Texture.redstoneLampOff),
        new IndexedTexture(0xD3, Texture.redstoneLampOff)));
    allTextures.put("redstone_lamp_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_lamp_on",
            Texture.redstoneLampOn),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_lamp_on",
            Texture.redstoneLampOn),
        new SimpleTexture("textures/blocks/redstoneLight_lit", Texture.redstoneLampOn),
        new IndexedTexture(0xD4, Texture.redstoneLampOn)));
    allTextures.put("stonebrick_carved", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_stone_bricks",
            Texture.circleStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_stone_bricks",
            Texture.circleStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_carved",
            Texture.circleStoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth_carved", Texture.circleStoneBrick),
        new IndexedTexture(0xD5, Texture.circleStoneBrick)));
    allTextures.put("birch_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_planks", Texture.birchPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_planks", Texture.birchPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_birch", Texture.birchPlanks),
        new SimpleTexture("textures/blocks/wood_birch", Texture.birchPlanks),
        new IndexedTexture(0xD6, Texture.birchPlanks)));
    allTextures.put("anvil_base", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/anvil", Texture.anvilSide),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil", Texture.anvilSide),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_base", Texture.anvilSide),
        new SimpleTexture("textures/blocks/anvil_base", Texture.anvilSide),
        new IndexedTexture(0xD7, Texture.anvilSide)));
    allTextures.put("anvil_top_damaged_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chipped_anvil_top",
            Texture.anvilTopDamaged1),
        new SimpleTexture("assets/minecraft/textures/blocks/chipped_anvil_top",
            Texture.anvilTopDamaged1),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_top_damaged_1",
            Texture.anvilTopDamaged1),
        new SimpleTexture("textures/blocks/anvil_top_damaged_1", Texture.anvilTopDamaged1),
        new IndexedTexture(0xD8, Texture.anvilTopDamaged1)));

    allTextures.put("nether_brick", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_bricks", Texture.netherBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_brick", Texture.netherBrick),
        new SimpleTexture("textures/blocks/netherBrick", Texture.netherBrick),
        new IndexedTexture(0xE0, Texture.netherBrick)));
    allTextures.put("silver_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_wool", Texture.lightGrayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_wool", Texture.lightGrayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_silver",
            Texture.lightGrayWool),
        new SimpleTexture("textures/blocks/cloth_8", Texture.lightGrayWool),
        new IndexedTexture(0xE1, Texture.lightGrayWool)));
    allTextures.put("nether_wart_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_stage0",
            Texture.netherWart0),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage0",
            Texture.netherWart0),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_0",
            Texture.netherWart0),
        new SimpleTexture("textures/blocks/netherStalk_0", Texture.netherWart0),
        new IndexedTexture(0xE2, Texture.netherWart0)));
    allTextures.put("nether_wart_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_stage1",
            Texture.netherWart1),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage1",
            Texture.netherWart1),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_1",
            Texture.netherWart1),
        new SimpleTexture("textures/blocks/netherStalk_1", Texture.netherWart1),
        new IndexedTexture(0xE3, Texture.netherWart1)));
    allTextures.put("nether_wart_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_stage2",
            Texture.netherWart2),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage2",
            Texture.netherWart2),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_2",
            Texture.netherWart2),
        new SimpleTexture("textures/blocks/netherStalk_2", Texture.netherWart2),
        new IndexedTexture(0xE4, Texture.netherWart2)));
    allTextures.put("sandstone_carved", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_sandstone",
            Texture.sandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_sandstone",
            Texture.sandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_carved",
            Texture.sandstoneDecorated),
        new SimpleTexture("textures/blocks/sandstone_carved", Texture.sandstoneDecorated),
        new IndexedTexture(0xE5, Texture.sandstoneDecorated)));
    allTextures.put("sandstone_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cut_sandstone",
            Texture.sandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/cut_sandstone",
            Texture.sandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_smooth",
            Texture.sandstoneCut),
        new SimpleTexture("textures/blocks/sandstone_smooth", Texture.sandstoneCut),
        new IndexedTexture(0xE6, Texture.sandstoneCut)));
    allTextures.put("anvil_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/anvil_top", Texture.anvilTop),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_top", Texture.anvilTop),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_top_damaged_0", Texture.anvilTop),
        new SimpleTexture("textures/blocks/anvil_top", Texture.anvilTop),
        new IndexedTexture(0xE7, Texture.anvilTop)));
    allTextures.put("anvil_top_damaged_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/damaged_anvil_top",
            Texture.anvilTopDamaged2),
        new SimpleTexture("assets/minecraft/textures/blocks/damaged_anvil_top",
            Texture.anvilTopDamaged2),
        new SimpleTexture("textures/blocks/anvil_top_damaged_2", Texture.anvilTopDamaged2),
        new IndexedTexture(0xE8, Texture.anvilTopDamaged2)));
    allTextures.put("lava_still", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lava_still", Texture.lava),
        new SimpleTexture("assets/minecraft/textures/blocks/lava_still", Texture.lava),
        new SimpleTexture("textures/blocks/lava", Texture.lava),
        new IndexedTexture(0xED, Texture.lava)));

    // MC 1.5
    allTextures.put("quartz_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_block_side", Texture.quartzSide),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_side", Texture.quartzSide),
        new SimpleTexture("textures/blocks/quartzblock_side", Texture.quartzSide)));
    allTextures.put("quartz_block_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_block_top", Texture.quartzTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_top", Texture.quartzTop),
        new SimpleTexture("textures/blocks/quartzblock_top", Texture.quartzTop)));
    allTextures.put("quartz_block_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_block_bottom",
            Texture.quartzBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_bottom",
            Texture.quartzBottom),
        new SimpleTexture("textures/blocks/quartzblock_bottom", Texture.quartzBottom)));
    allTextures.put("quartz_block_chiseled", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_quartz_block",
            Texture.quartzChiseled),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_quartz_block",
            Texture.quartzChiseled),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_chiseled",
            Texture.quartzChiseled),
        new SimpleTexture("textures/blocks/quartzblock_chiseled", Texture.quartzChiseled)));
    allTextures.put("quartz_block_chiseled_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_quartz_block_top",
            Texture.quartzChiseledTop),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_quartz_block_top",
            Texture.quartzChiseledTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_chiseled_top",
            Texture.quartzChiseledTop),
        new SimpleTexture("textures/blocks/quartzblock_chiseled_top", Texture.quartzChiseledTop)));
    allTextures.put("quartz_pillar", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_pillar",
            Texture.quartzPillar),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_pillar",
            Texture.quartzPillar),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_lines",
            Texture.quartzPillar),
        new SimpleTexture("textures/blocks/quartzblock_lines", Texture.quartzPillar)));
    allTextures.put("quartz_pillar_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_pillar_top",
            Texture.quartzPillarTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_pillar_top",
            Texture.quartzPillarTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_lines_top",
            Texture.quartzPillarTop),
        new SimpleTexture("textures/blocks/quartzblock_lines_top", Texture.quartzPillarTop)));
    allTextures.put("dropper_front_horizontal", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dropper_front",
            Texture.dropperFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dropper_front",
            Texture.dropperFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dropper_front_horizontal",
            Texture.dropperFront),
        new SimpleTexture("textures/blocks/dropper_front", Texture.dropperFront)));
    allTextures.put("dropper_front_vertical", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dropper_front_vertical",
            Texture.dropperFrontVertical),
        new SimpleTexture("assets/minecraft/textures/blocks/dropper_front_vertical",
            Texture.dropperFrontVertical)));
    allTextures.put("activator_rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/activator_rail", Texture.activatorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/activator_rail", Texture.activatorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_activator", Texture.activatorRail),
        new SimpleTexture("textures/blocks/activatorRail", Texture.activatorRail)));
    allTextures.put("activator_rail_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/activator_rail_on",
            Texture.activatorRailPowered),
        new SimpleTexture("assets/minecraft/textures/blocks/activator_rail_on",
            Texture.activatorRailPowered),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_activator_powered",
            Texture.activatorRailPowered),
        new SimpleTexture("textures/blocks/activatorRail_powered", Texture.activatorRailPowered)));
    allTextures.put("daylight_detector_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/daylight_detector_top",
            Texture.daylightDetectorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_top",
            Texture.daylightDetectorTop),
        new SimpleTexture("textures/blocks/daylightDetector_top", Texture.daylightDetectorTop)));
    allTextures.put("daylight_detector_inverted_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/daylight_detector_inverted_top",
            Texture.daylightDetectorInvertedTop),
        new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_inverted_top",
            Texture.daylightDetectorInvertedTop)));
    allTextures.put("daylight_detector_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/daylight_detector_side",
            Texture.daylightDetectorSide),
        new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_side",
            Texture.daylightDetectorSide),
        new SimpleTexture("textures/blocks/daylightDetector_side", Texture.daylightDetectorSide)));
    allTextures.put("comparator_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/comparator", Texture.comparatorOff),
        new SimpleTexture("assets/minecraft/textures/blocks/comparator", Texture.comparatorOff),
        new SimpleTexture("assets/minecraft/textures/blocks/comparator_off", Texture.comparatorOff),
        new SimpleTexture("textures/blocks/comparator", Texture.comparatorOff)));
    allTextures.put("comparator_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/comparator_on", Texture.comparatorOn),
        new SimpleTexture("assets/minecraft/textures/blocks/comparator_on", Texture.comparatorOn),
        new SimpleTexture("textures/blocks/comparator_lit", Texture.comparatorOn)));
    allTextures.put("hopper_outside", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hopper_outside", Texture.hopperOutside),
        new SimpleTexture("assets/minecraft/textures/blocks/hopper_outside", Texture.hopperOutside),
        new SimpleTexture("textures/blocks/hopper", Texture.hopperOutside)));
    allTextures.put("hopper_inside", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hopper_inside", Texture.hopperInside),
        new SimpleTexture("assets/minecraft/textures/blocks/hopper_inside", Texture.hopperInside),
        new SimpleTexture("textures/blocks/hopper_inside", Texture.hopperInside)));
    // TODO hopper top

    // MC 1.6
    allTextures.put("hay_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hay_block_side", Texture.hayBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/hay_block_side", Texture.hayBlockSide)));
    allTextures.put("hay_block_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hay_block_top", Texture.hayBlockTop),
        new SimpleTexture("assets/minecraft/textures/blocks/hay_block_top", Texture.hayBlockTop)));
    allTextures.put("terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/terracotta", Texture.hardenedClay),
        new SimpleTexture("assets/minecraft/textures/blocks/terracotta", Texture.hardenedClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay", Texture.hardenedClay)));
    allTextures.put("coal_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/coal_block", Texture.coalBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/coal_block", Texture.coalBlock)));
    allTextures.put("black_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_terracotta",
            Texture.blackClay),
        new SimpleTexture("assets/minecraft/textures/blocks/black_terracotta",
            Texture.blackClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_black",
            Texture.blackClay)));
    allTextures.put("blue_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_terracotta",
            Texture.blueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_terracotta",
            Texture.blueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_blue",
            Texture.blueClay)));
    allTextures.put("brown_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_terracotta",
            Texture.brownClay),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_terracotta",
            Texture.brownClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_brown",
            Texture.brownClay)));
    allTextures.put("cyan_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_terracotta",
            Texture.cyanClay),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_terracotta",
            Texture.cyanClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_cyan",
            Texture.cyanClay)));
    allTextures.put("gray_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_terracotta",
            Texture.grayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_terracotta",
            Texture.grayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_gray",
            Texture.grayClay)));
    allTextures.put("green_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_terracotta",
            Texture.greenClay),
        new SimpleTexture("assets/minecraft/textures/blocks/green_terracotta",
            Texture.greenClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_green",
            Texture.greenClay)));
    allTextures.put("light_blue_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_terracotta",
            Texture.lightBlueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_terracotta",
            Texture.lightBlueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_light_blue",
            Texture.lightBlueClay)));
    allTextures.put("lime_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_terracotta",
            Texture.limeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_terracotta",
            Texture.limeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_lime",
            Texture.limeClay)));
    allTextures.put("magenta_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_terracotta",
            Texture.magentaClay),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_terracotta",
            Texture.magentaClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_magenta",
            Texture.magentaClay)));
    allTextures.put("orange_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_terracotta",
            Texture.orangeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_terracotta",
            Texture.orangeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_orange",
            Texture.orangeClay)));
    allTextures.put("pink_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_terracotta",
            Texture.pinkClay),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_terracotta",
            Texture.pinkClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_pink",
            Texture.pinkClay)));
    allTextures.put("purple_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_terracotta",
            Texture.purpleClay),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_terracotta",
            Texture.purpleClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_purple",
            Texture.purpleClay)));
    allTextures.put("red_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_terracotta",
            Texture.redClay),
        new SimpleTexture("assets/minecraft/textures/blocks/red_terracotta",
            Texture.redClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_red",
            Texture.redClay)));
    allTextures.put("silver_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_terracotta",
            Texture.lightGrayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_terracotta",
            Texture.lightGrayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_silver",
            Texture.lightGrayClay)));
    allTextures.put("white_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_terracotta",
            Texture.whiteClay),
        new SimpleTexture("assets/minecraft/textures/blocks/white_terracotta",
            Texture.whiteClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_white",
            Texture.whiteClay)));
    allTextures.put("yellow_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_terracotta",
            Texture.yellowClay),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_terracotta",
            Texture.yellowClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_yellow",
            Texture.yellowClay)));

    // Birch Leaf [MC ?]
    allTextures.put("birch_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_leaves", Texture.birchLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_leaves", Texture.birchLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_birch", Texture.birchLeaves),
        new IndexedTexture(0xC4, Texture.birchLeaves)));

    // [MC 1.7.2] Stained glass blocks
    allTextures.put("glass_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_stained_glass",
            Texture.blackGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/black_stained_glass",
            Texture.blackGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_black", Texture.blackGlass)));
    allTextures.put("glass_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_stained_glass", Texture.blueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_stained_glass", Texture.blueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_blue", Texture.blueGlass)));
    allTextures.put("glass_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_stained_glass",
            Texture.brownGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_stained_glass",
            Texture.brownGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_brown", Texture.brownGlass)));
    allTextures.put("glass_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_stained_glass", Texture.cyanGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_stained_glass", Texture.cyanGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_cyan", Texture.cyanGlass)));
    allTextures.put("glass_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_stained_glass", Texture.grayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_stained_glass", Texture.grayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_gray", Texture.grayGlass)));
    allTextures.put("glass_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_stained_glass",
            Texture.greenGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/green_stained_glass",
            Texture.greenGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_green", Texture.greenGlass)));
    allTextures.put("glass_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_stained_glass",
            Texture.lightBlueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_stained_glass",
            Texture.lightBlueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_light_blue",
            Texture.lightBlueGlass)));
    allTextures.put("glass_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_stained_glass", Texture.limeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_stained_glass", Texture.limeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_lime", Texture.limeGlass)));
    allTextures.put("glass_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_stained_glass",
            Texture.magentaGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_stained_glass",
            Texture.magentaGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_magenta", Texture.magentaGlass)));
    allTextures.put("glass_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_stained_glass",
            Texture.orangeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_stained_glass",
            Texture.orangeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_orange", Texture.orangeGlass)));
    allTextures.put("glass_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_stained_glass", Texture.pinkGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_stained_glass", Texture.pinkGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pink", Texture.pinkGlass)));
    allTextures.put("glass_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_stained_glass",
            Texture.purpleGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_stained_glass",
            Texture.purpleGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_purple", Texture.purpleGlass)));
    allTextures.put("glass_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_stained_glass", Texture.redGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/red_stained_glass", Texture.redGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_red", Texture.redGlass)));
    allTextures.put("glass_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_stained_glass",
            Texture.lightGrayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_stained_glass",
            Texture.lightGrayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_silver", Texture.lightGrayGlass)));
    allTextures.put("glass_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_stained_glass",
            Texture.whiteGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/white_stained_glass",
            Texture.whiteGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_white", Texture.whiteGlass)));
    allTextures.put("glass_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_stained_glass",
            Texture.yellowGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_stained_glass",
            Texture.yellowGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_yellow", Texture.yellowGlass)));

    // [MC 1.7.2] Stained glass panes
    allTextures.put("glass_pane_top_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_stained_glass_pane_top",
            Texture.blackGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/black_stained_glass_pane_top",
            Texture.blackGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_black",
            Texture.blackGlassPaneSide)));
    allTextures.put("glass_pane_top_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_stained_glass_pane_top",
            Texture.blueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_stained_glass_pane_top",
            Texture.blueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_blue",
            Texture.blueGlassPaneSide)));
    allTextures.put("glass_pane_top_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_stained_glass_pane_top",
            Texture.brownGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_stained_glass_pane_top",
            Texture.brownGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_brown",
            Texture.brownGlassPaneSide)));
    allTextures.put("glass_pane_top_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_stained_glass_pane_top",
            Texture.cyanGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_stained_glass_pane_top",
            Texture.cyanGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_cyan",
            Texture.cyanGlassPaneSide)));
    allTextures.put("glass_pane_top_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_stained_glass_pane_top",
            Texture.grayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_stained_glass_pane_top",
            Texture.grayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_gray",
            Texture.grayGlassPaneSide)));
    allTextures.put("glass_pane_top_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_stained_glass_pane_top",
            Texture.greenGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/green_stained_glass_pane_top",
            Texture.greenGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_green",
            Texture.greenGlassPaneSide)));
    allTextures.put("glass_pane_top_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_stained_glass_pane_top",
            Texture.lightBlueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_stained_glass_pane_top",
            Texture.lightBlueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_light_blue",
            Texture.lightBlueGlassPaneSide)));
    allTextures.put("glass_pane_top_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_stained_glass_pane_top",
            Texture.limeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_stained_glass_pane_top",
            Texture.limeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_lime",
            Texture.limeGlassPaneSide)));
    allTextures.put("glass_pane_top_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_stained_glass_pane_top",
            Texture.magentaGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_stained_glass_pane_top",
            Texture.magentaGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_magenta",
            Texture.magentaGlassPaneSide)));
    allTextures.put("glass_pane_top_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_stained_glass_pane_top",
            Texture.orangeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_stained_glass_pane_top",
            Texture.orangeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_orange",
            Texture.orangeGlassPaneSide)));
    allTextures.put("glass_pane_top_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_stained_glass_pane_top",
            Texture.pinkGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_stained_glass_pane_top",
            Texture.pinkGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_pink",
            Texture.pinkGlassPaneSide)));
    allTextures.put("glass_pane_top_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_stained_glass_pane_top",
            Texture.purpleGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_stained_glass_pane_top",
            Texture.purpleGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_purple",
            Texture.purpleGlassPaneSide)));
    allTextures.put("glass_pane_top_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_stained_glass_pane_top",
            Texture.redGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/red_stained_glass_pane_top",
            Texture.redGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_red",
            Texture.redGlassPaneSide)));
    allTextures.put("glass_pane_top_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_stained_glass_pane_top",
            Texture.lightGrayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_stained_glass_pane_top",
            Texture.lightGrayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_silver",
            Texture.lightGrayGlassPaneSide)));
    allTextures.put("glass_pane_top_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_stained_glass_pane_top",
            Texture.whiteGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/white_stained_glass_pane_top",
            Texture.whiteGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_white",
            Texture.whiteGlassPaneSide)));
    allTextures.put("glass_pane_top_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_stained_glass_pane_top",
            Texture.yellowGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_stained_glass_pane_top",
            Texture.yellowGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_yellow",
            Texture.yellowGlassPaneSide)));

    // [MC 1.7.2] Top/bottom log textures
    allTextures.put("spruce_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_log_top", Texture.spruceWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_log_top", Texture.spruceWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_spruce_top", Texture.spruceWoodTop),
        new IndexedTexture(0x15, Texture.spruceWoodTop)));
    allTextures.put("birch_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_log_top", Texture.birchWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_log_top", Texture.birchWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_birch_top", Texture.birchWoodTop),
        new IndexedTexture(0x15, Texture.spruceWoodTop)));
    allTextures.put("jungle_top_log_", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_log_top", Texture.jungleTreeTop),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_log_top", Texture.jungleTreeTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_jungle_top", Texture.jungleTreeTop),
        new IndexedTexture(0x15, Texture.jungleTreeTop)));

    // [MC 1.7.2] Podzol
    allTextures.put("podzol_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/podzol_side", Texture.podzolSide),
        new SimpleTexture("assets/minecraft/textures/blocks/podzol_side", Texture.podzolSide),
        new SimpleTexture("assets/minecraft/textures/blocks/dirt_podzol_side", Texture.podzolSide)));
    allTextures.put("podzol_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/podzol_top", Texture.podzolTop),
        new SimpleTexture("assets/minecraft/textures/blocks/podzol_top", Texture.podzolTop),
        new SimpleTexture("assets/minecraft/textures/blocks/dirt_podzol_top", Texture.podzolTop)));

    // [MC 1.7.2] Acacia, Dark Oak
    allTextures.put("acacia_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_log", Texture.acaciaWood),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_log", Texture.acaciaWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_acacia", Texture.acaciaWood)));
    allTextures.put("acacia_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_log_top",
            Texture.acaciaWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_log_top",
            Texture.acaciaWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_acacia_top",
            Texture.acaciaWoodTop)));
    allTextures.put("acacia_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_leaves", Texture.acaciaLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_leaves", Texture.acaciaLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_acacia", Texture.acaciaLeaves)));
    allTextures.put("acacia_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_sapling",
            Texture.acaciaSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_sapling",
            Texture.acaciaSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_acacia",
            Texture.acaciaSapling)));
    allTextures.put("acacia_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_planks", Texture.acaciaPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_planks", Texture.acaciaPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_acacia", Texture.acaciaPlanks)));

    allTextures.put("dark_oak_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_log", Texture.darkOakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_log", Texture.darkOakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_big_oak", Texture.darkOakWood)));
    allTextures.put("dark_oak_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_log_top",
            Texture.darkOakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_log_top",
            Texture.darkOakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_big_oak_top",
            Texture.darkOakWoodTop)));
    allTextures.put("dark_oak_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_leaves",
            Texture.darkOakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_leaves",
            Texture.darkOakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_big_oak",
            Texture.darkOakLeaves)));
    allTextures.put("dark_oak_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_sapling",
            Texture.darkOakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_sapling",
            Texture.darkOakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_roofed_oak",
            Texture.darkOakSapling)));
    allTextures.put("dark_oak_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_planks",
            Texture.darkOakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_planks",
            Texture.darkOakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_big_oak",
            Texture.darkOakPlanks)));

    // [MC 1.7.2] Packed Ice
    allTextures.put("packed_ice", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/packed_ice", Texture.packedIce),
        new SimpleTexture("assets/minecraft/textures/blocks/packed_ice", Texture.packedIce),
        new SimpleTexture("assets/minecraft/textures/blocks/ice_packed", Texture.packedIce)));

    // [MC 1.7.2] Red Sand
    allTextures.put("red_sand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sand", Texture.redSand),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sand", Texture.redSand)));

    // [MC 1.7.2] Flowers
    allTextures.put("allium", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/allium", Texture.allium),
        new SimpleTexture("assets/minecraft/textures/blocks/allium", Texture.allium),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_allium", Texture.allium)));
    allTextures.put("blue_orchid", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_orchid",
            Texture.blueOrchid),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_orchid",
            Texture.blueOrchid),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_blue_orchid",
            Texture.blueOrchid)));
    allTextures.put("houstonia", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/azure_bluet",
            Texture.azureBluet),
        new SimpleTexture("assets/minecraft/textures/blocks/azure_bluet",
            Texture.azureBluet),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_houstonia",
            Texture.azureBluet)));
    allTextures.put("oxeye_daisy",new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oxeye_daisy",
            Texture.oxeyeDaisy),
        new SimpleTexture("assets/minecraft/textures/blocks/oxeye_daisy",
            Texture.oxeyeDaisy),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_oxeye_daisy",
            Texture.oxeyeDaisy)));
    allTextures.put("red_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_tulip",
            Texture.redTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/red_tulip",
            Texture.redTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_red",
            Texture.redTulip)));
    allTextures.put("orange_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_tulip",
            Texture.orangeTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_tulip",
            Texture.orangeTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_orange",
            Texture.orangeTulip)));
    allTextures.put("white_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_tulip",
            Texture.whiteTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/white_tulip",
            Texture.whiteTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_white",
            Texture.whiteTulip)));
    allTextures.put("pink_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_tulip",
            Texture.pinkTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_tulip",
            Texture.pinkTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_pink",
            Texture.pinkTulip)));

    allTextures.put("large_fern_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/large_fern_bottom",
            Texture.largeFernBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/large_fern_bottom",
            Texture.largeFernBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_fern_bottom",
            Texture.largeFernBottom)));
    allTextures.put("large_fern_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/large_fern_top",
            Texture.largeFernTop),
        new SimpleTexture("assets/minecraft/textures/blocks/large_fern_top",
            Texture.largeFernTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_fern_top",
            Texture.largeFernTop)));

    allTextures.put("tall_grass_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tall_grass_bottom",
            Texture.doubleTallGrassBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/tall_grass_bottom",
            Texture.doubleTallGrassBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_grass_bottom",
            Texture.doubleTallGrassBottom)));
    allTextures.put("tall_grass_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tall_grass_top",
            Texture.doubleTallGrassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/tall_grass_top",
            Texture.doubleTallGrassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_grass_top",
            Texture.doubleTallGrassTop)));

    allTextures.put("peony_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/peony_bottom",
            Texture.peonyBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/peony_bottom",
            Texture.peonyBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_paeonia_bottom",
            Texture.peonyBottom)));
    allTextures.put("peony_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/peony_top",
            Texture.peonyTop),
        new SimpleTexture("assets/minecraft/textures/blocks/peony_top",
            Texture.peonyTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_paeonia_top",
            Texture.peonyTop)));

    allTextures.put("rose_bush_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rose_bush_bottom",
            Texture.roseBushBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/rose_bush_bottom",
            Texture.roseBushBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_rose_bottom",
            Texture.roseBushBottom)));
    allTextures.put("rose_bush_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rose_bush_top",
            Texture.roseBushTop),
        new SimpleTexture("assets/minecraft/textures/blocks/rose_bush_top",
            Texture.roseBushTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_rose_top",
            Texture.roseBushTop)));

    allTextures.put("sunflower_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_bottom",
            Texture.sunflowerBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_bottom",
            Texture.sunflowerBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_bottom",
            Texture.sunflowerBottom)));
    allTextures.put("sunflower_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_top",
            Texture.sunflowerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_top",
            Texture.sunflowerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_top",
            Texture.sunflowerTop)));
    allTextures.put("sunflower_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_front",
            Texture.sunflowerFront),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_front",
            Texture.sunflowerFront),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_front",
            Texture.sunflowerFront)));
    allTextures.put("sunflower_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_back",
            Texture.sunflowerBack),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_back",
            Texture.sunflowerBack),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_back",
            Texture.sunflowerBack)));

    allTextures.put("lilac_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lilac_bottom",
            Texture.lilacBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/lilac_bottom",
            Texture.lilacBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_syringa_bottom",
            Texture.lilacBottom)));
    allTextures.put("lilac_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lilac_top",
            Texture.lilacTop),
        new SimpleTexture("assets/minecraft/textures/blocks/lilac_top",
            Texture.lilacTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_syringa_top",
            Texture.lilacTop)));

    // [MC 1.8] New Blocks
    allTextures.put("diorite", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/diorite", Texture.diorite),
        new SimpleTexture("assets/minecraft/textures/blocks/diorite", Texture.diorite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_diorite", Texture.diorite)));
    allTextures.put("diorite_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/polished_diorite",
            Texture.smoothDiorite),
        new SimpleTexture("assets/minecraft/textures/blocks/polished_diorite",
            Texture.smoothDiorite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_diorite_smooth",
            Texture.smoothDiorite)));
    allTextures.put("granite", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/granite", Texture.granite),
        new SimpleTexture("assets/minecraft/textures/blocks/granite", Texture.granite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_granite", Texture.granite)));
    allTextures.put("granite_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/polished_granite",
            Texture.smoothGranite),
        new SimpleTexture("assets/minecraft/textures/blocks/polished_granite",
            Texture.smoothGranite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_granite_smooth",
            Texture.smoothGranite)));
    allTextures.put("andesite", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/andesite", Texture.andesite),
        new SimpleTexture("assets/minecraft/textures/blocks/andesite", Texture.andesite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_andesite", Texture.andesite)));
    allTextures.put("andesite_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/polished_andesite",
            Texture.smoothAndesite),
        new SimpleTexture("assets/minecraft/textures/blocks/polished_andesite",
            Texture.smoothAndesite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_andesite_smooth",
            Texture.smoothAndesite)));
    allTextures.put("coarse_dirt", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/coarse_dirt", Texture.coarseDirt),
        new SimpleTexture("assets/minecraft/textures/blocks/coarse_dirt", Texture.coarseDirt)));
    allTextures.put("prismarine", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/prismarine",
            Texture.prismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine",
            Texture.prismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine_rough",
            Texture.prismarine)));
    allTextures.put("prismarine_bricks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/prismarine_bricks",
            Texture.prismarineBricks),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine_bricks",
            Texture.prismarineBricks)));
    allTextures.put("dark_prismarine", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_prismarine",
            Texture.darkPrismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_prismarine",
            Texture.darkPrismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine_dark",
            Texture.darkPrismarine)));
    allTextures.put("sea_lantern", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sea_lantern", Texture.seaLantern),
        new SimpleTexture("assets/minecraft/textures/blocks/sea_lantern", Texture.seaLantern)));
    allTextures.put("sponge_wet", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wet_sponge", Texture.wetSponge),
        new SimpleTexture("assets/minecraft/textures/blocks/wet_sponge", Texture.wetSponge),
        new SimpleTexture("assets/minecraft/textures/blocks/sponge_wet", Texture.wetSponge)));
    allTextures.put("iron_trapdoor", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_trapdoor", Texture.ironTrapdoor),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_trapdoor", Texture.ironTrapdoor)));
    allTextures.put("slime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/slime_block", Texture.slime),
        new SimpleTexture("assets/minecraft/textures/blocks/slime_block", Texture.slime),
        new SimpleTexture("assets/minecraft/textures/blocks/slime", Texture.slime)));
    allTextures.put("red_sandstone_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sandstone_top",
            Texture.redSandstoneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_top",
            Texture.redSandstoneTop)));
    allTextures.put("red_sandstone_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sandstone_bottom",
            Texture.redSandstoneBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_bottom",
            Texture.redSandstoneBottom)));
    allTextures.put("red_sandstone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sandstone",
            Texture.redSandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone",
            Texture.redSandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_normal",
            Texture.redSandstoneSide)));
    allTextures.put("red_sandstone_carved", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_red_sandstone",
            Texture.redSandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_red_sandstone",
            Texture.redSandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_carved",
            Texture.redSandstoneDecorated)));
    allTextures.put("red_sandstone_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cut_red_sandstone",
            Texture.redSandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/cut_red_sandstone",
            Texture.redSandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_smooth",
            Texture.redSandstoneCut)));

    allTextures.put("spruce_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_door_top",
            Texture.spruceDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_door_upper",
            Texture.spruceDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_spruce_upper",
            Texture.spruceDoorTop)));
    allTextures.put("spruce_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_door_bottom",
            Texture.spruceDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_door_lower",
            Texture.spruceDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_spruce_lower",
            Texture.spruceDoorBottom)));

    allTextures.put("birch_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_door_top",
            Texture.birchDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_door_upper",
            Texture.birchDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_birch_upper",
            Texture.birchDoorTop)));
    allTextures.put("birch_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_door_bottom",
            Texture.birchDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_door_lower",
            Texture.birchDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_birch_lower",
            Texture.birchDoorBottom)));

    allTextures.put("jungle_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_door_top",
            Texture.jungleDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_door_upper",
            Texture.jungleDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_jungle_upper",
            Texture.jungleDoorTop)));
    allTextures.put("jungle_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_door_bottom",
            Texture.jungleDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_door_lower",
            Texture.jungleDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_jungle_lower",
            Texture.jungleDoorBottom)));

    allTextures.put("acacia_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_door_top",
            Texture.acaciaDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_door_upper",
            Texture.acaciaDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_acacia_upper",
            Texture.acaciaDoorTop)));
    allTextures.put("acacia_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_door_bottom",
            Texture.acaciaDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_door_lower",
            Texture.acaciaDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_acacia_lower",
            Texture.acaciaDoorBottom)));

    allTextures.put("dark_oak_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_door_top",
            Texture.darkOakDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_door_upper",
            Texture.darkOakDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_dark_oak_upper",
            Texture.darkOakDoorTop)));
    allTextures.put("dark_oak_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_door_bottom",
            Texture.darkOakDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_door_lower",
            Texture.darkOakDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_dark_oak_lower",
            Texture.darkOakDoorBottom)));

    // Minecraft 1.9 blocks.
    allTextures.put("grass_path_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_path_side",
            Texture.grassPathSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_path_side",
            Texture.grassPathSide)));
    allTextures.put("grass_path_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_path_top", Texture.grassPathTop),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_path_top", Texture.grassPathTop)));
    allTextures.put("end_bricks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_stone_bricks", Texture.endBricks),
        new SimpleTexture("assets/minecraft/textures/blocks/end_stone_bricks", Texture.endBricks),
        new SimpleTexture("assets/minecraft/textures/blocks/end_bricks", Texture.endBricks)));
    allTextures.put("purpur_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purpur_block", Texture.purpurBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/purpur_block", Texture.purpurBlock)));
    allTextures.put("purpur_pillar", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purpur_pillar",
            Texture.purpurPillarSide),
        new SimpleTexture("assets/minecraft/textures/blocks/purpur_pillar",
            Texture.purpurPillarSide)));
    allTextures.put("purpur_pillar_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purpur_pillar_top",
            Texture.purpurPillarTop),
        new SimpleTexture("assets/minecraft/textures/blocks/purpur_pillar_top",
            Texture.purpurPillarTop)));
    allTextures.put("chorus_flower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chorus_flower", Texture.chorusFlower),
        new SimpleTexture("assets/minecraft/textures/blocks/chorus_flower", Texture.chorusFlower)));
    allTextures.put("chorus_flower_dead", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chorus_flower_dead",
            Texture.chorusFlowerDead),
        new SimpleTexture("assets/minecraft/textures/blocks/chorus_flower_dead",
            Texture.chorusFlowerDead)));
    allTextures.put("chorus_plant", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chorus_plant", Texture.chorusPlant),
        new SimpleTexture("assets/minecraft/textures/blocks/chorus_plant", Texture.chorusPlant)));
    allTextures.put("end_rod", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_rod", Texture.endRod),
        new SimpleTexture("assets/minecraft/textures/blocks/end_rod", Texture.endRod)));

    allTextures.put("fire_layer_0", new AlternateTextures(
        new AnimatedTextureLoader("assets/minecraft/textures/block/fire_0", Texture.fireLayer0),
        new AnimatedTextureLoader("assets/minecraft/textures/blocks/fire_layer_0",
            Texture.fireLayer0)));
    allTextures.put("fire_layer_1", new AlternateTextures(
        new AnimatedTextureLoader("assets/minecraft/textures/block/fire_1", Texture.fireLayer1),
        new AnimatedTextureLoader("assets/minecraft/textures/blocks/fire_layer_1",
            Texture.fireLayer1)));

    allTextures.put("paintings_zetterstrand",
        new SimpleTexture("assets/minecraft/textures/painting/paintings_kristoffer_zetterstrand",
            Texture.paintings));
    allTextures.put("font_ascii", new FontTexture("assets/minecraft/textures/font/ascii"));

    allTextures.put("alex",
        new ThinArmEntityTextureLoader("assets/minecraft/textures/entity/alex", Texture.alex));
    allTextures.put("steve",
        new EntityTextureLoader("assets/minecraft/textures/entity/steve", Texture.steve));
    allTextures.put("creeper",
        new EntityTextureLoader("assets/minecraft/textures/entity/creeper/creeper",
            Texture.creeper));
    allTextures.put("zombie",
        new EntityTextureLoader("assets/minecraft/textures/entity/zombie/zombie", Texture.zombie));
    allTextures.put("skeleton",
        new EntityTextureLoader("assets/minecraft/textures/entity/skeleton/skeleton",
            Texture.skeleton));
    allTextures.put("wither",
        new EntityTextureLoader("assets/minecraft/textures/entity/wither/wither", Texture.wither));

    // Minecraft 1.10 blocks.
    allTextures.put("boneSide", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bone_block_side", Texture.boneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/bone_block_side", Texture.boneSide)));
    allTextures.put("boneTop", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bone_block_top", Texture.boneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/bone_block_top", Texture.boneTop)));
    allTextures.put("magma", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magma", Texture.magma),
        new SimpleTexture("assets/minecraft/textures/blocks/magma", Texture.magma)));
    allTextures.put("netherWartBlock", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_block",
            Texture.netherWartBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_block",
            Texture.netherWartBlock)));
    allTextures.put("red_nether_bricks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_nether_bricks",
            Texture.redNetherBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/red_nether_brick",
            Texture.redNetherBrick)));

    // [1.11] Shulker boxes.
    allTextures.put("shulkerBlack",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_black",
            Texture.shulkerBlack));
    allTextures.put("shulkerBlue",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_blue",
            Texture.shulkerBlue));
    allTextures.put("shulkerBrown",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_brown",
            Texture.shulkerBrown));
    allTextures.put("shulkerCyan",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_cyan",
            Texture.shulkerCyan));
    allTextures.put("shulkerGray",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_gray",
            Texture.shulkerGray));
    allTextures.put("shulkerGreen",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_green",
            Texture.shulkerGreen));
    allTextures.put("shulkerLightBlue",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_light_blue",
            Texture.shulkerLightBlue));
    allTextures.put("shulkerLime",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_lime",
            Texture.shulkerLime));
    allTextures.put("shulkerMagenta",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_magenta",
            Texture.shulkerMagenta));
    allTextures.put("shulkerOrange",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_orange",
            Texture.shulkerOrange));
    allTextures.put("shulkerPink",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_pink",
            Texture.shulkerPink));
    allTextures.put("shulkerPurple",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_purple",
            Texture.shulkerPurple));
    allTextures.put("shulkerRed",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_red",
            Texture.shulkerRed));
    allTextures.put("shulkerSilver", new AlternateTextures(
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_light_gray",
            Texture.shulkerSilver),
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_silver",
            Texture.shulkerSilver)));
    allTextures.put("shulkerWhite",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_white",
            Texture.shulkerWhite));
    allTextures.put("shulkerYellow",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_yellow",
            Texture.shulkerYellow));

    // [1.11] Observer block.
    allTextures.put("observer_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_back",
            Texture.observerBack),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_back",
            Texture.observerBack)));
    allTextures.put("observer_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_front",
            Texture.observerFront),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_front",
            Texture.observerFront)));
    allTextures.put("observer_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_side",
            Texture.observerSide),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_side",
            Texture.observerSide)));
    allTextures.put("observer_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_top",
            Texture.observerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_top",
            Texture.observerTop)));

    // Redstone textures were redone and renamed in Minecraft 1.9.
    // The redstone cross texture is now created by combining redstone_dust_dot
    // and redstone_dust_line0, and redstone_dust_line1.
    // See https://github.com/llbit/chunky/issues/359

    allTextures.put("redstone_dust_cross", new AlternateTextures(
        new LayeredTextureLoader(
            "assets/minecraft/textures/block/redstone_dust_dot",
            Texture.redstoneWireCross,
            new LayeredTextureLoader(
                "assets/minecraft/textures/block/redstone_dust_line0",
                Texture.redstoneWireCross,
                new RotatedTextureLoader(
                    "assets/minecraft/textures/block/redstone_dust_line1",
                    Texture.redstoneWireCross))),
        new LayeredTextureLoader(
            "assets/minecraft/textures/blocks/redstone_dust_dot",
            Texture.redstoneWireCross,
            new LayeredTextureLoader(
                "assets/minecraft/textures/blocks/redstone_dust_line0",
                Texture.redstoneWireCross,
                new RotatedTextureLoader(
                    "assets/minecraft/textures/blocks/redstone_dust_line1",
                    Texture.redstoneWireCross))),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_dust_cross",
            Texture.redstoneWireCross),
        new SimpleTexture("textures/blocks/redstoneDust_cross", Texture.redstoneWireCross),
        new IndexedTexture(0xA4, Texture.redstoneWireCross)));
    allTextures.put("redstone_dust_line", new AlternateTextures(
        new RotatedTextureLoader("assets/minecraft/textures/block/redstone_dust_line0",
            Texture.redstoneWire),
        new RotatedTextureLoader("assets/minecraft/textures/blocks/redstone_dust_line0",
            Texture.redstoneWire),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_dust_line",
            Texture.redstoneWire),
        new SimpleTexture("textures/blocks/redstoneDust_line", Texture.redstoneWire),
        new IndexedTexture(0xA5, Texture.redstoneWire)));

    // Minecraft 1.12: Glazed Terracotta:
    allTextures.put("glazed_terracotta_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_glazed_terracotta",
            Texture.terracottaBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/black_glazed_terracotta",
            Texture.terracottaBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_black",
            Texture.terracottaBlack)));
    allTextures.put("glazed_terracotta_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_glazed_terracotta",
            Texture.terracottaBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_glazed_terracotta",
            Texture.terracottaBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_blue",
            Texture.terracottaBlue)));
    allTextures.put("glazed_terracotta_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_glazed_terracotta",
            Texture.terracottaBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_glazed_terracotta",
            Texture.terracottaBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_brown",
            Texture.terracottaBrown)));
    allTextures.put("glazed_terracotta_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_glazed_terracotta",
            Texture.terracottaCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_glazed_terracotta",
            Texture.terracottaCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_cyan",
            Texture.terracottaCyan)));
    allTextures.put("glazed_terracotta_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_glazed_terracotta",
            Texture.terracottaGray),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_glazed_terracotta",
            Texture.terracottaGray),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_gray",
            Texture.terracottaGray)));
    allTextures.put("glazed_terracotta_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_glazed_terracotta",
            Texture.terracottaGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/green_glazed_terracotta",
            Texture.terracottaGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_green",
            Texture.terracottaGreen)));
    allTextures.put("glazed_terracotta_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_glazed_terracotta",
            Texture.terracottaLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_glazed_terracotta",
            Texture.terracottaLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_light_blue",
            Texture.terracottaLightBlue)));
    allTextures.put("glazed_terracotta_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_glazed_terracotta",
            Texture.terracottaLime),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_glazed_terracotta",
            Texture.terracottaLime),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_lime",
            Texture.terracottaLime)));
    allTextures.put("glazed_terracotta_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_glazed_terracotta",
            Texture.terracottaMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_glazed_terracotta",
            Texture.terracottaMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_magenta",
            Texture.terracottaMagenta)));
    allTextures.put("glazed_terracotta_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_glazed_terracotta",
            Texture.terracottaOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_glazed_terracotta",
            Texture.terracottaOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_orange",
            Texture.terracottaOrange)));
    allTextures.put("glazed_terracotta_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_glazed_terracotta",
            Texture.terracottaPink),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_glazed_terracotta",
            Texture.terracottaPink),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_pink",
            Texture.terracottaPink)));
    allTextures.put("glazed_terracotta_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_glazed_terracotta",
            Texture.terracottaPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_glazed_terracotta",
            Texture.terracottaPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_purple",
            Texture.terracottaPurple)));
    allTextures.put("glazed_terracotta_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_glazed_terracotta",
            Texture.terracottaRed),
        new SimpleTexture("assets/minecraft/textures/blocks/red_glazed_terracotta",
            Texture.terracottaRed),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_red",
            Texture.terracottaRed)));
    allTextures.put("glazed_terracotta_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_glazed_terracotta",
            Texture.terracottaSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_glazed_terracotta",
            Texture.terracottaSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_silver",
            Texture.terracottaSilver)));
    allTextures.put("glazed_terracotta_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_glazed_terracotta",
            Texture.terracottaWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/white_glazed_terracotta",
            Texture.terracottaWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_white",
            Texture.terracottaWhite)));
    allTextures.put("glazed_terracotta_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_glazed_terracotta",
            Texture.terracottaYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_glazed_terracotta",
            Texture.terracottaYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_yellow",
            Texture.terracottaYellow)));

    // Minecraft 1.12: Concrete:
    allTextures.put("concrete_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_concrete",
            Texture.concreteBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/black_concrete",
            Texture.concreteBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_black",
            Texture.concreteBlack)));
    allTextures.put("concrete_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_concrete",
            Texture.concreteBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_concrete",
            Texture.concreteBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_blue",
            Texture.concreteBlue)));
    allTextures.put("concrete_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_concrete",
            Texture.concreteBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_concrete",
            Texture.concreteBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_brown",
            Texture.concreteBrown)));
    allTextures.put("concrete_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_concrete",
            Texture.concreteCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_concrete",
            Texture.concreteCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_cyan",
            Texture.concreteCyan)));
    allTextures.put("concrete_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_concrete",
            Texture.concreteGray),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_concrete",
            Texture.concreteGray),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_gray",
            Texture.concreteGray)));
    allTextures.put("concrete_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_concrete",
            Texture.concreteGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/green_concrete",
            Texture.concreteGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_green",
            Texture.concreteGreen)));
    allTextures.put("concrete_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_concrete",
            Texture.concreteLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_concrete",
            Texture.concreteLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_light_blue",
            Texture.concreteLightBlue)));
    allTextures.put("concrete_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_concrete",
            Texture.concreteLime),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_concrete",
            Texture.concreteLime),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_lime",
            Texture.concreteLime)));
    allTextures.put("concrete_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_concrete",
            Texture.concreteMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_concrete",
            Texture.concreteMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_magenta",
            Texture.concreteMagenta)));
    allTextures.put("concrete_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_concrete",
            Texture.concreteOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_concrete",
            Texture.concreteOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_orange",
            Texture.concreteOrange)));
    allTextures.put("concrete_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_concrete",
            Texture.concretePink),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_concrete",
            Texture.concretePink),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_pink",
            Texture.concretePink)));
    allTextures.put("concrete_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_concrete",
            Texture.concretePurple),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_concrete",
            Texture.concretePurple),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_purple",
            Texture.concretePurple)));
    allTextures.put("concrete_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_concrete",
            Texture.concreteRed),
        new SimpleTexture("assets/minecraft/textures/blocks/red_concrete",
            Texture.concreteRed),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_red",
            Texture.concreteRed)));
    allTextures.put("concrete_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_concrete",
            Texture.concreteSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_concrete",
            Texture.concreteSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_silver",
            Texture.concreteSilver)));
    allTextures.put("concrete_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_concrete",
            Texture.concreteWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/white_concrete",
            Texture.concreteWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_white",
            Texture.concreteWhite)));
    allTextures.put("concrete_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_concrete",
            Texture.concreteYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_concrete",
            Texture.concreteYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_yellow",
            Texture.concreteYellow)));

    // Concrete powder:
    allTextures.put("concrete_powder_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_concrete_powder",
            Texture.concretePowderBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/black_concrete_powder",
            Texture.concretePowderBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_black",
            Texture.concretePowderBlack)));
    allTextures.put("concrete_powder_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_concrete_powder",
            Texture.concretePowderBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_concrete_powder",
            Texture.concretePowderBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_blue",
            Texture.concretePowderBlue)));
    allTextures.put("concrete_powder_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_concrete_powder",
            Texture.concretePowderBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_concrete_powder",
            Texture.concretePowderBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_brown",
            Texture.concretePowderBrown)));
    allTextures.put("concrete_powder_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_concrete_powder",
            Texture.concretePowderCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_concrete_powder",
            Texture.concretePowderCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_cyan",
            Texture.concretePowderCyan)));
    allTextures.put("concrete_powder_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_concrete_powder",
            Texture.concretePowderGray),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_concrete_powder",
            Texture.concretePowderGray),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_gray",
            Texture.concretePowderGray)));
    allTextures.put("concrete_powder_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_concrete_powder",
            Texture.concretePowderGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/green_concrete_powder",
            Texture.concretePowderGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_green",
            Texture.concretePowderGreen)));
    allTextures.put("concrete_powder_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_concrete_powder",
            Texture.concretePowderLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_concrete_powder",
            Texture.concretePowderLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_light_blue",
            Texture.concretePowderLightBlue)));
    allTextures.put("concrete_powder_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_concrete_powder",
            Texture.concretePowderLime),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_concrete_powder",
            Texture.concretePowderLime),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_lime",
            Texture.concretePowderLime)));
    allTextures.put("concrete_powder_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_concrete_powder",
            Texture.concretePowderMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_concrete_powder",
            Texture.concretePowderMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_magenta",
            Texture.concretePowderMagenta)));
    allTextures.put("concrete_powder_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_concrete_powder",
            Texture.concretePowderOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_concrete_powder",
            Texture.concretePowderOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_orange",
            Texture.concretePowderOrange)));
    allTextures.put("concrete_powder_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_concrete_powder",
            Texture.concretePowderPink),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_concrete_powder",
            Texture.concretePowderPink),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_pink",
            Texture.concretePowderPink)));
    allTextures.put("concrete_powder_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_concrete_powder",
            Texture.concretePowderPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_concrete_powder",
            Texture.concretePowderPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_purple",
            Texture.concretePowderPurple)));
    allTextures.put("concrete_powder_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_concrete_powder",
            Texture.concretePowderRed),
        new SimpleTexture("assets/minecraft/textures/blocks/red_concrete_powder",
            Texture.concretePowderRed),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_red",
            Texture.concretePowderRed)));
    allTextures.put("concrete_powder_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_concrete_powder",
            Texture.concretePowderSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_concrete_powder",
            Texture.concretePowderSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_silver",
            Texture.concretePowderSilver)));
    allTextures.put("concrete_powder_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_concrete_powder",
            Texture.concretePowderWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/white_concrete_powder",
            Texture.concretePowderWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_white",
            Texture.concretePowderWhite)));
    allTextures.put("concrete_powder_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_concrete_powder",
            Texture.concretePowderYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_concrete_powder",
            Texture.concretePowderYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_yellow",
            Texture.concretePowderYellow)));

    // [1.12] Beetroots:
    allTextures.put("beetroots_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage0",
            Texture.beets0),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage0",
            Texture.beets0),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_0",
            Texture.beets0)));
    allTextures.put("beetroots_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage1",
            Texture.beets1),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage1",
            Texture.beets1),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_1",
            Texture.beets1)));
    allTextures.put("beetroots_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage2",
            Texture.beets2),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage2",
            Texture.beets2),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_2",
            Texture.beets2)));
    allTextures.put("beetroots_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage3",
            Texture.beets3),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage3",
            Texture.beets3),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_3",
            Texture.beets3)));

    allTextures.put("bed_white",
        new SimpleTexture("assets/minecraft/textures/entity/bed/white", Texture.bedWhite));
    allTextures.put("bed_orange",
        new SimpleTexture("assets/minecraft/textures/entity/bed/orange", Texture.bedOrange));
    allTextures.put("bed_magenta",
        new SimpleTexture("assets/minecraft/textures/entity/bed/magenta", Texture.bedMagenta));
    allTextures.put("bed_light_blue",
        new SimpleTexture("assets/minecraft/textures/entity/bed/light_blue", Texture.bedLightBlue));
    allTextures.put("bed_yellow",
        new SimpleTexture("assets/minecraft/textures/entity/bed/yellow", Texture.bedYellow));
    allTextures.put("bed_lime",
        new SimpleTexture("assets/minecraft/textures/entity/bed/lime", Texture.bedLime));
    allTextures.put("bed_pink",
        new SimpleTexture("assets/minecraft/textures/entity/bed/pink", Texture.bedPink));
    allTextures.put("bed_gray",
        new SimpleTexture("assets/minecraft/textures/entity/bed/gray", Texture.bedGray));
    allTextures.put("bed_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/entity/bed/light_gray", Texture.bedSilver),
        new SimpleTexture("assets/minecraft/textures/entity/bed/silver", Texture.bedSilver)));
    allTextures.put("bed_cyan",
        new SimpleTexture("assets/minecraft/textures/entity/bed/cyan", Texture.bedCyan));
    allTextures.put("bed_purple",
        new SimpleTexture("assets/minecraft/textures/entity/bed/purple", Texture.bedPurple));
    allTextures.put("bed_blue",
        new SimpleTexture("assets/minecraft/textures/entity/bed/blue", Texture.bedBlue));
    allTextures.put("bed_brown",
        new SimpleTexture("assets/minecraft/textures/entity/bed/brown", Texture.bedBrown));
    allTextures.put("bed_green",
        new SimpleTexture("assets/minecraft/textures/entity/bed/green", Texture.bedGreen));
    allTextures.put("bed_red",
        new AlternateTextures(
            new SimpleTexture("assets/minecraft/textures/entity/bed/red", Texture.bedRed),
            new BedTextureAdapter()
        ));
    allTextures.put("bed_black",
        new SimpleTexture("assets/minecraft/textures/entity/bed/black", Texture.bedBlack));

    allTextures.put("banner_base",
        new SimpleTexture("assets/minecraft/textures/entity/banner_base", Texture.bannerBase));

    allTextures.put("armor_stand",
        new SimpleTexture("assets/minecraft/textures/entity/armorstand/wood", Texture.armorStand));

    // Minecraft 1.13
    allTextures.put("stripped_oak_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_oak_log",
            Texture.strippedOakLog));
    allTextures.put("stripped_oak_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_oak_log_top",
            Texture.strippedOakLogTop));

    allTextures.put("stripped_spruce_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_spruce_log",
            Texture.strippedSpruceLog));
    allTextures.put("stripped_spruce_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_spruce_log_top",
            Texture.strippedSpruceLogTop));

    allTextures.put("stripped_birch_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_birch_log",
            Texture.strippedBirchLog));
    allTextures.put("stripped_birch_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_birch_log_top",
            Texture.strippedBirchLogTop));

    allTextures.put("stripped_jungle_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_jungle_log",
            Texture.strippedJungleLog));
    allTextures.put("stripped_jungle_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_jungle_log_top",
            Texture.strippedJungleLogTop));

    allTextures.put("stripped_acacia_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_acacia_log",
            Texture.strippedAcaciaLog));
    allTextures.put("stripped_acacia_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_acacia_log_top",
            Texture.strippedAcaciaLogTop));

    allTextures.put("stripped_dark_oak_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_dark_oak_log",
            Texture.strippedDarkOakLog));
    allTextures.put("stripped_dark_oak_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_dark_oak_log_top",
            Texture.strippedDarkOakLogTop));

    allTextures.put("kelp",
        new SimpleTexture("assets/minecraft/textures/block/kelp",
            Texture.kelp));
    allTextures.put("kelp_plant",
        new SimpleTexture("assets/minecraft/textures/block/kelp_plant",
            Texture.kelpPlant));
  }

  private static String[] texturePacks = { };

  private static String texturePackName(File tpFile) {
    boolean isDefault = tpFile.equals(MinecraftFinder.getMinecraftJar());
    return String.format("%s (%s)",
        isDefault ? "default resource pack" : "resource pack",
        tpFile.getAbsolutePath());
  }

  /**
   * Load a set of textures from a Minecraft resource pack.
   * The resource pack must contain an assets directory, either as
   * a top-level directory or inside a top-level directory with the
   * same name as the Zip file.
   *
   * @param tpFile resource pack file
   * @param textures textures to load
   * @return the keys for textures that could not be loaded
   */
  public static Set<Map.Entry<String, TextureLoader>> loadTextures(File tpFile,
      Collection<Map.Entry<String, TextureLoader>> textures) {
    Set<Map.Entry<String, TextureLoader>> notLoaded = new HashSet<>(textures);

    String basename = tpFile.getName().toLowerCase();
    if (basename.endsWith(".zip")) {
      basename = basename.substring(0, basename.length() - 4);
    }

    try (ZipFile texturePack = new ZipFile(tpFile)) {
      // Seach for the assets directory in the resource pack.
      // The assets directory can be inside a top-level directory with
      // the same name as the resource pack zip file.
      boolean foundAssetDirectory = false;
      String topLevel = "";
      Enumeration<? extends ZipEntry> entries = texturePack.entries();
      while (entries.hasMoreElements()) {
        String name = entries.nextElement().getName();
        if (name.startsWith("assets/")) {
          foundAssetDirectory = true;
          break;
        }
        if (name.toLowerCase().startsWith(basename) &&
            name.substring(basename.length()).startsWith("/assets/")) {
          topLevel = name.substring(0, basename.length()) + "/";
          foundAssetDirectory = true;
          break;
        }
      }
      if (!foundAssetDirectory) {
        Log.errorf("Missing assets directory in %s", texturePackName(tpFile));
      } else {
        for (Map.Entry<String, TextureLoader> texture : textures) {
          if (texture.getValue().load(texturePack, topLevel)) {
            notLoaded.remove(texture);
          }
        }

        // Fall back on the "terrain.png" texture atlas:
        notLoaded = loadTerrainTextures(texturePack, notLoaded);
      }
    } catch (IOException e) {
      Log.warnf("Failed to open %s: %s", texturePackName(tpFile), e.getMessage());
    }
    return notLoaded;
  }

  /**
   * Load textures from some resource packs.
   * @param texturePacks The paths to texture packs to be loaded, as a path list.
   * Texture packs are loaded in the order of the paths in this argument.
   * Paths are separated by the system path separator.
   * @param remember Decides if the texture packs should be saved as the
   * last used texture pack.
   */
  public static void loadTexturePacks(@NotNull String texturePacks, boolean remember) {
    String pathList = texturePacks.trim();
    String[] packs;
    if (!texturePacks.isEmpty()) {
      packs = pathList.split(File.pathSeparator);
    } else {
      packs = new String[0];
    }
    loadTexturePacks(packs, remember);
  }

  /**
   * Load textures from some resource packs.
   * @param texturePacks The paths to texture packs to be loaded.
   * Texture packs are loaded in the order of the paths in this argument.
   * @param remember Decides if the texture packs should be saved as the
   * last used texture pack.
   */
  public static void loadTexturePacks(@NotNull String[] texturePacks, boolean remember) {
    Set<Map.Entry<String, TextureLoader>> toLoad = allTextures.entrySet();
    for (String path : texturePacks) {
      if (!path.isEmpty()) {
        File file = new File(path);
        if (!file.isFile()) {
          Log.error("Could not open texture pack: " + file.getAbsolutePath());
        } else {
          Log.infof("Loading %d textures from %s", toLoad.size(), file.getAbsolutePath());
          toLoad = loadTextures(file, toLoad);
          if (toLoad.isEmpty()) {
            break;
          }
        }
      }
    }
    if (!toLoad.isEmpty()) {
      // If there are textures left to load we try to load the default textures.
      File defaultResources = MinecraftFinder.getMinecraftJar();
      if (defaultResources != null) {
        Log.infof("Loading %d textures from %s", toLoad.size(), defaultResources.getAbsolutePath());
        toLoad = loadTextures(defaultResources, toLoad);
      } else {
        Log.error("Minecraft Jar not found: falling back on placeholder textures.");
      }
    }
    if (!toLoad.isEmpty()) {
      StringBuilder message = new StringBuilder();
      message.append("Failed to load textures:");
      Iterator<Map.Entry<String, TextureLoader>> iterator = toLoad.iterator();
      for (int count = 0; iterator.hasNext() && count < 10; ++count) {
        message.append("\n\t").append(iterator.next().getKey());
      }
      if (toLoad.size() > 10) {
        message.append("\n\t... and ").append(toLoad.size() - 10).append(" more");
      }
      Log.info(message.toString());
    }
    if (remember) {
      StringBuilder paths = new StringBuilder();
      for (String path : texturePacks) {
        if (paths.length() > 0) {
          paths.append(File.pathSeparator);
        }
        paths.append(path);
      }
      PersistentSettings.setLastTexturePack(paths.toString());
    }
  }

  private static Set<Map.Entry<String, TextureLoader>> loadTerrainTextures(ZipFile texturePack,
      Set<Map.Entry<String, TextureLoader>> textures) {
    Set<Map.Entry<String, TextureLoader>> notLoaded = new HashSet<>(textures);

    try (InputStream in = texturePack.getInputStream(new ZipEntry("terrain.png"))) {
      if (in != null) {
        BitmapImage spriteMap = ImageLoader.read(in);
        BitmapImage[] terrainTextures = getTerrainTextures(spriteMap);

        for (Map.Entry<String, TextureLoader> texture : textures) {
          if (texture.getValue().loadFromTerrain(terrainTextures)) {
            notLoaded.remove(texture);
          }
        }
      }
    } catch (IOException e) {
      // Failed to load terrain textures - this is handled implicitly.
    }
    return notLoaded;
  }

  /**
   * Load a 16x16 spritemap.
   *
   * @return A bufferedImage containing the spritemap
   * @throws IOException if the image dimensions are incorrect
   */
  private static BitmapImage[] getTerrainTextures(BitmapImage spritemap) throws IOException {

    if (spritemap.width != spritemap.height || spritemap.width % 16 != 0) {
      throw new IOException(
          "Error: terrain.png file must have equal width and height, divisible by 16!");
    }

    int imgW = spritemap.width;
    int spriteW = imgW / 16;
    BitmapImage[] tex = new BitmapImage[256];

    for (int i = 0; i < 256; ++i) {
      tex[i] = new BitmapImage(spriteW, spriteW);
    }

    for (int y = 0; y < imgW; ++y) {
      int sy = y / spriteW;
      for (int x = 0; x < imgW; ++x) {
        int sx = x / spriteW;
        BitmapImage texture = tex[sx + sy * 16];
        texture.setPixel(x % spriteW, y % spriteW, spritemap.getPixel(x, y));
      }
    }
    return tex;
  }

  /**
   * Set the resource packs to be used to load textures from.
   */
  public static void setTexturePacks(@NotNull String texturePacks) {
    String pathList = texturePacks.trim();
    String[] packs;
    if (!texturePacks.isEmpty()) {
      packs = pathList.split(File.pathSeparator);
    } else {
      packs = new String[0];
    }
    TexturePackLoader.texturePacks = packs;
  }

  public static Collection<Map.Entry<String, TextureLoader>> loadTextures(
      Collection<Map.Entry<String, TextureLoader>> textures) {
    Collection<Map.Entry<String, TextureLoader>> toLoad = textures;
    for (String path : texturePacks) {
      if (!path.isEmpty()) {
        File file = new File(path);
        if (!file.isFile()) {
          Log.error("Could not open texture pack: " + file.getAbsolutePath());
        } else {
          toLoad = loadTextures(file, toLoad);
          if (toLoad.isEmpty()) {
            return Collections.emptyList();
          }
        }
      }
    }
    if (!toLoad.isEmpty()) {
      // If there are textures left to load we try to load the default textures.
      File defaultResources = MinecraftFinder.getMinecraftJar();
      if (defaultResources != null) {
        toLoad = loadTextures(defaultResources, toLoad);
      } else {
        Log.error("Minecraft Jar not found: falling back on placeholder textures.");
      }
    }
    return toLoad;
  }
}
