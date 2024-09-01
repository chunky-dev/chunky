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

import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.renderer.scene.sky.Sun;
import se.llbit.chunky.resources.texture.BitmapTexture;
import se.llbit.chunky.resources.texturepack.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Utility methods to load Minecraft texture packs.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturePackLoader {
  public static final Map<String, TextureLoader> ALL_TEXTURES = new HashMap<>();

  static {
    ALL_TEXTURES.put("normal chest", new ConditionalTextures(
        "assets/minecraft/textures/entity/chest/normal_left.png",
        new ChestTexture("assets/minecraft/textures/entity/chest/normal", ChestTexture.Layout.NEW_LAYOUT,
            Texture.chestLock, Texture.chestTop, Texture.chestBottom, Texture.chestLeft,
            Texture.chestRight, Texture.chestFront, Texture.chestBack), // MC 1.15
        new AlternateTextures(
            new ChestTexture("assets/minecraft/textures/entity/chest/normal",
                Texture.chestLock, Texture.chestTop, Texture.chestBottom, Texture.chestLeft,
                Texture.chestRight, Texture.chestFront, Texture.chestBack), // MC 1.6
            new ChestTexture("item/chest", Texture.chestLock, Texture.chestTop, Texture.chestBottom,
                Texture.chestLeft, Texture.chestRight, Texture.chestFront, Texture.chestBack))));
    ALL_TEXTURES.put("ender chest", new ConditionalTextures(
        "assets/minecraft/textures/entity/chest/normal_left.png",
        new ChestTexture("assets/minecraft/textures/entity/chest/ender", ChestTexture.Layout.NEW_LAYOUT,
            Texture.enderChestLock, Texture.enderChestTop, Texture.enderChestBottom,
            Texture.enderChestLeft, Texture.enderChestRight, Texture.enderChestFront,
            Texture.enderChestBack), // MC 1.15
        new AlternateTextures(
            new ChestTexture("assets/minecraft/textures/entity/chest/ender", // MC 1.6
                Texture.enderChestLock, Texture.enderChestTop, Texture.enderChestBottom,
                Texture.enderChestLeft, Texture.enderChestRight, Texture.enderChestFront,
                Texture.enderChestBack),
            new ChestTexture("item/enderchest", Texture.enderChestLock, Texture.enderChestTop,
                Texture.enderChestBottom, Texture.enderChestLeft, Texture.enderChestRight,
                Texture.enderChestFront, Texture.enderChestBack))));
    ALL_TEXTURES.put("normal double chest", new AlternateTextures(
        new AllTextures( // MC 1.15
            new SplitLargeChestTexture("assets/minecraft/textures/entity/chest/normal_left", SplitLargeChestTexture.Part.LEFT,
                Texture.largeChestTopLeft, Texture.largeChestBottomLeft, Texture.largeChestLeft, null,
                Texture.largeChestFrontLeft, Texture.largeChestBackLeft),
            new SplitLargeChestTexture("assets/minecraft/textures/entity/chest/normal_right", SplitLargeChestTexture.Part.RIGHT,
                Texture.largeChestTopRight, Texture.largeChestBottomRight, null, Texture.largeChestRight,
                Texture.largeChestFrontRight, Texture.largeChestBackRight)),
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
    ALL_TEXTURES.put("trapped chest", new ConditionalTextures(
        "assets/minecraft/textures/entity/chest/trapped_left.png",
        new ChestTexture("assets/minecraft/textures/entity/chest/trapped", ChestTexture.Layout.NEW_LAYOUT,
            Texture.trappedChestLock, Texture.trappedChestTop, Texture.trappedChestBottom,
            Texture.trappedChestLeft, Texture.trappedChestRight, Texture.trappedChestFront,
            Texture.trappedChestBack), // MC 1.15
        new ChestTexture("assets/minecraft/textures/entity/chest/trapped", // MC 1.6
            Texture.trappedChestLock, Texture.trappedChestTop, Texture.trappedChestBottom,
            Texture.trappedChestLeft, Texture.trappedChestRight, Texture.trappedChestFront,
            Texture.trappedChestBack)));
    ALL_TEXTURES.put("trapped double chest", new AlternateTextures(
        new AllTextures( // MC 1.15
            new SplitLargeChestTexture("assets/minecraft/textures/entity/chest/trapped_left", SplitLargeChestTexture.Part.LEFT,
                Texture.largeTrappedChestTopLeft, Texture.largeTrappedChestBottomLeft, Texture.largeTrappedChestLeft, null,
                Texture.largeTrappedChestFrontLeft, Texture.largeTrappedChestBackLeft),
            new SplitLargeChestTexture("assets/minecraft/textures/entity/chest/trapped_right", SplitLargeChestTexture.Part.RIGHT,
                Texture.largeTrappedChestTopRight, Texture.largeTrappedChestBottomRight, null, Texture.largeTrappedChestRight,
                Texture.largeTrappedChestFrontRight, Texture.largeTrappedChestBackRight)),
        new LargeChestTexture("assets/minecraft/textures/entity/chest/trapped_double", // MC 1.6
            Texture.largeTrappedChestLeft, Texture.largeTrappedChestRight,
            Texture.largeTrappedChestTopLeft, Texture.largeTrappedChestTopRight,
            Texture.largeTrappedChestFrontLeft, Texture.largeTrappedChestFrontRight,
            Texture.largeTrappedChestBottomLeft, Texture.largeTrappedChestBottomRight,
            Texture.largeTrappedChestBackLeft, Texture.largeTrappedChestBackRight)));
    ALL_TEXTURES.put("sun", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/environment/sun", Sun.texture),// MC 1.6
        new SimpleTexture("environment/sun", Sun.texture),// MC 1.5
        new SimpleTexture("terrain/sun", Sun.texture)));
    ALL_TEXTURES.put("clouds", new AlternateTextures(
        new CloudsTexture("assets/minecraft/textures/environment/clouds"),
        // MC 1.6
        new CloudsTexture("environment/clouds")));
    ALL_TEXTURES.put("grass color map", new AlternateTextures(
        new GrassColorTexture("assets/minecraft/textures/colormap/grass"),
        // MC 1.6
        new GrassColorTexture("misc/grasscolor")));
    ALL_TEXTURES.put("foliage color map", new AlternateTextures(
        new FoliageColorTexture("assets/minecraft/textures/colormap/foliage"),
        // MC 1.6
        new FoliageColorTexture("misc/foliagecolor")));

    ALL_TEXTURES.put("grass_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_top", Texture.grassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_top", Texture.grassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_top", Texture.grassTop),
        new SimpleTexture("textures/blocks/grass_top", Texture.grassTop),
        new IndexedTexture(0x00, Texture.grassTop)));
    ALL_TEXTURES.put("stone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/stone", Texture.stone),
        new SimpleTexture("assets/minecraft/textures/blocks/stone", Texture.stone),
        new SimpleTexture("textures/blocks/stone", Texture.stone),
        new IndexedTexture(0x01, Texture.stone)));
    ALL_TEXTURES.put("dirt", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dirt", Texture.dirt),
        new SimpleTexture("assets/minecraft/textures/blocks/dirt", Texture.dirt),
        new SimpleTexture("textures/blocks/dirt", Texture.dirt),
        new IndexedTexture(0x02, Texture.dirt)));
    ALL_TEXTURES.put("grass_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_side",
            Texture.grassSideSaturated),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_side",
            Texture.grassSideSaturated),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_side",
            Texture.grassSideSaturated),
        new SimpleTexture("textures/blocks/grass_side", Texture.grassSideSaturated),
        new IndexedTexture(0x03, Texture.grassSideSaturated)));
    ALL_TEXTURES.put("oak planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_planks", Texture.oakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_planks", Texture.oakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_oak", Texture.oakPlanks),
        new SimpleTexture("textures/blocks/wood", Texture.oakPlanks),
        new IndexedTexture(0x04, Texture.oakPlanks)));
    ALL_TEXTURES.put("stone slab side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/smooth_stone_slab_side", Texture.smoothStoneSlabSide), // MC 1.14+
        new SimpleTexture("assets/minecraft/textures/block/stone_slab_side", Texture.smoothStoneSlabSide),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_slab_side", Texture.smoothStoneSlabSide),
        new SimpleTexture("textures/blocks/stoneslab_side", Texture.smoothStoneSlabSide),
        new IndexedTexture(0x05, Texture.smoothStoneSlabSide)));
    ALL_TEXTURES.put("stone slab top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/smooth_stone", Texture.smoothStone), // MC 1.14+
        new SimpleTexture("assets/minecraft/textures/block/stone_slab_top", Texture.smoothStone),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_slab_top", Texture.smoothStone),
        new SimpleTexture("textures/blocks/stoneslab_top", Texture.smoothStone),
        new IndexedTexture(0x06, Texture.smoothStone)));
    ALL_TEXTURES.put("brick", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bricks", Texture.brick),
        new SimpleTexture("assets/minecraft/textures/blocks/brick", Texture.brick),
        new SimpleTexture("textures/blocks/brick", Texture.brick),
        new IndexedTexture(0x07, Texture.brick)));
    ALL_TEXTURES.put("tnt side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tnt_side", Texture.tntSide),
        new SimpleTexture("assets/minecraft/textures/blocks/tnt_side", Texture.tntSide),
        new SimpleTexture("textures/blocks/tnt_side", Texture.tntSide),
        new IndexedTexture(0x08, Texture.tntSide)));
    ALL_TEXTURES.put("tnt top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tnt_top", Texture.tntTop),
        new SimpleTexture("assets/minecraft/textures/blocks/tnt_top", Texture.tntTop),
        new SimpleTexture("textures/blocks/tnt_top", Texture.tntTop),
        new IndexedTexture(0x09, Texture.tntTop)));
    ALL_TEXTURES.put("tnt bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tnt_bottom", Texture.tntBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/tnt_bottom", Texture.tntBottom),
        new SimpleTexture("textures/blocks/tnt_bottom", Texture.tntBottom),
        new IndexedTexture(0x0A, Texture.tntBottom)));
    ALL_TEXTURES.put("cobweb", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cobweb", Texture.cobweb),
        new SimpleTexture("assets/minecraft/textures/blocks/cobweb", Texture.cobweb),
        new SimpleTexture("assets/minecraft/textures/blocks/web", Texture.cobweb),
        new SimpleTexture("textures/blocks/web", Texture.cobweb),
        new IndexedTexture(0x0B, Texture.cobweb)));
    ALL_TEXTURES.put("rose", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/poppy", Texture.poppy),
        new SimpleTexture("assets/minecraft/textures/blocks/poppy", Texture.poppy),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_rose", Texture.poppy),
        new SimpleTexture("textures/blocks/rose", Texture.poppy),
        new IndexedTexture(0x0C, Texture.poppy)));
    ALL_TEXTURES.put("dandelion", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dandelion", Texture.dandelion),
        new SimpleTexture("assets/minecraft/textures/blocks/dandelion", Texture.dandelion),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_dandelion", Texture.dandelion),
        new SimpleTexture("textures/blocks/flower", Texture.dandelion),
        new IndexedTexture(0x0D, Texture.dandelion)));
    ALL_TEXTURES.put("nether portal", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_portal", Texture.portal),
        new SimpleTexture("assets/minecraft/textures/blocks/portal", Texture.portal),
        new SimpleTexture("textures/blocks/portal", Texture.portal),
        new IndexedTexture(0x0E, Texture.portal)));
    ALL_TEXTURES.put("oak_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_sapling", Texture.oakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_sapling", Texture.oakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_oak", Texture.oakSapling),
        new SimpleTexture("textures/blocks/sapling", Texture.oakSapling),
        new IndexedTexture(0x0F, Texture.oakSapling)));

    ALL_TEXTURES.put("cobblestone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cobblestone", Texture.cobblestone),
        new SimpleTexture("assets/minecraft/textures/blocks/cobblestone", Texture.cobblestone),
        new SimpleTexture("textures/blocks/stonebrick", Texture.cobblestone),
        new IndexedTexture(0x10, Texture.cobblestone)));
    ALL_TEXTURES.put("bedrock", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bedrock", Texture.bedrock),
        new SimpleTexture("assets/minecraft/textures/blocks/bedrock", Texture.bedrock),
        new SimpleTexture("textures/blocks/bedrock", Texture.bedrock),
        new IndexedTexture(0x11, Texture.bedrock)));
    ALL_TEXTURES.put("sand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sand", Texture.sand),
        new SimpleTexture("assets/minecraft/textures/blocks/sand", Texture.sand),
        new SimpleTexture("textures/blocks/sand", Texture.sand),
        new IndexedTexture(0x12, Texture.sand)));
    ALL_TEXTURES.put("gravel", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gravel", Texture.gravel),
        new SimpleTexture("assets/minecraft/textures/blocks/gravel", Texture.gravel),
        new SimpleTexture("textures/blocks/gravel", Texture.gravel),
        new IndexedTexture(0x13, Texture.gravel)));
    ALL_TEXTURES.put("oak log side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_log", Texture.oakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_log", Texture.oakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_oak", Texture.oakWood),
        new SimpleTexture("textures/blocks/tree_side", Texture.oakWood),
        new IndexedTexture(0x14, Texture.oakWood)));
    ALL_TEXTURES.put("oak log top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_log_top", Texture.oakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_log_top", Texture.oakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_oak_top", Texture.oakWoodTop),
        new SimpleTexture("textures/blocks/tree_top", Texture.oakWoodTop),
        new IndexedTexture(0x15, Texture.oakWoodTop)));
    ALL_TEXTURES.put("iron block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_block", Texture.ironBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_block", Texture.ironBlock),
        new SimpleTexture("textures/blocks/blockIron", Texture.ironBlock),
        new IndexedTexture(0x16, Texture.ironBlock)));
    ALL_TEXTURES.put("gold block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gold_block", Texture.goldBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/gold_block", Texture.goldBlock),
        new SimpleTexture("textures/blocks/blockGold", Texture.goldBlock),
        new IndexedTexture(0x17, Texture.goldBlock)));
    ALL_TEXTURES.put("diamond block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/diamond_block", Texture.diamondBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/diamond_block", Texture.diamondBlock),
        new SimpleTexture("textures/blocks/blockDiamond", Texture.diamondBlock),
        new IndexedTexture(0x18, Texture.diamondBlock)));
    ALL_TEXTURES.put("emerald block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/emerald_block", Texture.emeraldBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/emerald_block", Texture.emeraldBlock),
        new SimpleTexture("textures/blocks/blockEmerald", Texture.emeraldBlock),
        new IndexedTexture(0x19, Texture.emeraldBlock)));
    ALL_TEXTURES.put("redstone block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_block", Texture.redstoneBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_block", Texture.redstoneBlock),
        new SimpleTexture("textures/blocks/blockRedstone", Texture.redstoneBlock),
        new IndexedTexture(0x1A, Texture.redstoneBlock)));
    ALL_TEXTURES.put("red_mushroom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_mushroom", Texture.redMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/red_mushroom", Texture.redMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_red", Texture.redMushroom),
        new SimpleTexture("textures/blocks/mushroom_red", Texture.redMushroom),
        new IndexedTexture(0x1C, Texture.redMushroom)));
    ALL_TEXTURES.put("brown_mushroom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_mushroom", Texture.brownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_mushroom", Texture.brownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_brown", Texture.brownMushroom),
        new SimpleTexture("textures/blocks/mushroom_brown", Texture.brownMushroom),
        new IndexedTexture(0x1D, Texture.brownMushroom)));
    ALL_TEXTURES.put("jungle_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_sapling", Texture.jungleSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_sapling", Texture.jungleSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_jungle", Texture.jungleSapling),
        new SimpleTexture("textures/blocks/sapling_jungle", Texture.jungleSapling),
        new IndexedTexture(0x1E, Texture.jungleSapling)));

    ALL_TEXTURES.put("gold ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gold_ore", Texture.goldOre),
        new SimpleTexture("assets/minecraft/textures/blocks/gold_ore", Texture.goldOre),
        new SimpleTexture("textures/blocks/oreGold", Texture.goldOre),
        new IndexedTexture(0x20, Texture.goldOre)));
    ALL_TEXTURES.put("iron ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_ore", Texture.ironOre),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_ore", Texture.ironOre),
        new SimpleTexture("textures/blocks/oreIron", Texture.ironOre),
        new IndexedTexture(0x21, Texture.ironOre)));
    ALL_TEXTURES.put("coal ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/coal_ore", Texture.coalOre),
        new SimpleTexture("assets/minecraft/textures/blocks/coal_ore", Texture.coalOre),
        new SimpleTexture("textures/blocks/oreCoal", Texture.coalOre),
        new IndexedTexture(0x22, Texture.coalOre)));
    ALL_TEXTURES.put("bookshelf", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bookshelf", Texture.bookshelf),
        new SimpleTexture("assets/minecraft/textures/blocks/bookshelf", Texture.bookshelf),
        new SimpleTexture("textures/blocks/bookshelf", Texture.bookshelf),
        new IndexedTexture(0x23, Texture.bookshelf)));
    ALL_TEXTURES.put("mossy cobblestone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mossy_cobblestone", Texture.mossStone),
        new SimpleTexture("assets/minecraft/textures/blocks/mossy_cobblestone", Texture.mossStone),
        new SimpleTexture("assets/minecraft/textures/blocks/cobblestone_mossy", Texture.mossStone),
        new SimpleTexture("textures/blocks/stoneMoss", Texture.mossStone),
        new IndexedTexture(0x24, Texture.mossStone)));
    ALL_TEXTURES.put("obsidian", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/obsidian", Texture.obsidian),
        new SimpleTexture("assets/minecraft/textures/blocks/obsidian", Texture.obsidian),
        new SimpleTexture("textures/blocks/obsidian", Texture.obsidian),
        new IndexedTexture(0x25, Texture.obsidian)));
    ALL_TEXTURES.put("grass_side_overlay", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_side_overlay",
            Texture.grassSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_side_overlay",
            Texture.grassSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_side_overlay", Texture.grassSide),
        new SimpleTexture("textures/blocks/grass_side_overlay", Texture.grassSide),
        new IndexedTexture(0x26, Texture.grassSide)));
    ALL_TEXTURES.put("tallgrass", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/short_grass", Texture.tallGrass), // since 1.20.3-pre2
        new SimpleTexture("assets/minecraft/textures/block/grass", Texture.tallGrass),
        new SimpleTexture("assets/minecraft/textures/blocks/grass", Texture.tallGrass),
        new SimpleTexture("assets/minecraft/textures/blocks/tallgrass", Texture.tallGrass),
        new SimpleTexture("textures/blocks/tallgrass", Texture.tallGrass),
        new IndexedTexture(0x27, Texture.tallGrass)));
    ALL_TEXTURES.put("beacon", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beacon", Texture.beacon),
        new SimpleTexture("assets/minecraft/textures/blocks/beacon", Texture.beacon),
        new SimpleTexture("textures/blocks/beacon", Texture.beacon),
        new IndexedTexture(0x29, Texture.beacon)));
    ALL_TEXTURES.put("beacon_beam", new SimpleTexture("assets/minecraft/textures/entity/beacon_beam", Texture.beaconBeam));
    ALL_TEXTURES.put("crafting_table_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/crafting_table_top",
            Texture.workbenchTop),
        new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_top",
            Texture.workbenchTop),
        new SimpleTexture("textures/blocks/workbench_top", Texture.workbenchTop),
        new IndexedTexture(0x2B, Texture.workbenchTop)));
    ALL_TEXTURES.put("furnace_front_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_front",
            Texture.furnaceUnlitFront),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_front",
            Texture.furnaceUnlitFront),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_front_off",
            Texture.furnaceUnlitFront),
        new SimpleTexture("textures/blocks/furnace_front", Texture.furnaceUnlitFront),
        new IndexedTexture(0x2C, Texture.furnaceUnlitFront)));
    ALL_TEXTURES.put("furnace_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_side", Texture.furnaceSide),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_side", Texture.furnaceSide),
        new SimpleTexture("textures/blocks/furnace_side", Texture.furnaceSide),
        new IndexedTexture(0x2D, Texture.furnaceSide)));
    ALL_TEXTURES.put("dispenser_front_horizontal", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dispenser_front",
            Texture.dispenserFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dispenser_front",
            Texture.dispenserFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dispenser_front_horizontal",
            Texture.dispenserFront),
        new SimpleTexture("textures/blocks/dispenser_front", Texture.dispenserFront),
        new IndexedTexture(0x2E, Texture.dispenserFront)));
    ALL_TEXTURES.put("dispenser_front_vertical", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dispenser_front_vertical",
            Texture.dispenserFrontVertical),
        new SimpleTexture("assets/minecraft/textures/blocks/dispenser_front_vertical",
            Texture.dispenserFrontVertical)));

    ALL_TEXTURES.put("sponge", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sponge", Texture.sponge),
        new SimpleTexture("assets/minecraft/textures/blocks/sponge", Texture.sponge),
        new SimpleTexture("textures/blocks/sponge", Texture.sponge),
        new IndexedTexture(0x30, Texture.sponge)));
    ALL_TEXTURES.put("glass", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/glass", Texture.glass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass", Texture.glass),
        new SimpleTexture("textures/blocks/glass", Texture.glass),
        new IndexedTexture(0x31, Texture.glass)));
    ALL_TEXTURES.put("diamond_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/diamond_ore", Texture.diamondOre),
        new SimpleTexture("assets/minecraft/textures/blocks/diamond_ore", Texture.diamondOre),
        new SimpleTexture("textures/blocks/oreDiamond", Texture.diamondOre),
        new IndexedTexture(0x32, Texture.diamondOre)));
    ALL_TEXTURES.put("redstone_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_ore", Texture.redstoneOre),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_ore", Texture.redstoneOre),
        new SimpleTexture("textures/blocks/oreRedstone", Texture.redstoneOre),
        new IndexedTexture(0x33, Texture.redstoneOre)));
    ALL_TEXTURES.put("oak_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_leaves", Texture.oakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_leaves", Texture.oakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_oak", Texture.oakLeaves),
        new SimpleTexture("textures/blocks/leaves", Texture.oakLeaves),
        new IndexedTexture(0x34, Texture.oakLeaves)));
    ALL_TEXTURES.put("stone_brick", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/stone_bricks", Texture.stoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_bricks", Texture.stoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick", Texture.stoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth", Texture.stoneBrick),
        new IndexedTexture(0x36, Texture.stoneBrick)));
    ALL_TEXTURES.put("dead_bush", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dead_bush", Texture.deadBush),
        new SimpleTexture("assets/minecraft/textures/blocks/dead_bush", Texture.deadBush),
        new SimpleTexture("assets/minecraft/textures/blocks/deadbush", Texture.deadBush),
        new SimpleTexture("textures/blocks/deadbush", Texture.deadBush),
        new IndexedTexture(0x37, Texture.deadBush)));
    ALL_TEXTURES.put("fern", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/fern", Texture.fern),
        new SimpleTexture("assets/minecraft/textures/blocks/fern", Texture.fern),
        new SimpleTexture("textures/blocks/fern", Texture.fern),
        new IndexedTexture(0x38, Texture.fern)));
    ALL_TEXTURES.put("crafting_table_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/crafting_table_side",
            Texture.workbenchSide),
        new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_side",
            Texture.workbenchSide),
        new SimpleTexture("textures/blocks/workbench_side", Texture.workbenchSide),
        new IndexedTexture(0x3B, Texture.workbenchSide)));
    ALL_TEXTURES.put("crafting_table_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/crafting_table_front",
            Texture.workbenchFront),
        new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_front",
            Texture.workbenchFront),
        new SimpleTexture("textures/blocks/workbench_front", Texture.workbenchFront),
        new IndexedTexture(0x3C, Texture.workbenchFront)));
    ALL_TEXTURES.put("furnace_front_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_front_on",
            Texture.furnaceLitFront),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_front_on",
            Texture.furnaceLitFront),
        new SimpleTexture("textures/blocks/furnace_front_lit", Texture.furnaceLitFront),
        new IndexedTexture(0x3D, Texture.furnaceLitFront)));
    ALL_TEXTURES.put("furnace_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/furnace_top", Texture.furnaceTop),
        new SimpleTexture("assets/minecraft/textures/blocks/furnace_top", Texture.furnaceTop),
        new SimpleTexture("textures/blocks/furnace_top", Texture.furnaceTop),
        new IndexedTexture(0x3E, Texture.furnaceTop)));
    ALL_TEXTURES.put("spruce_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_sapling", Texture.spruceSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_sapling", Texture.spruceSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_spruce", Texture.spruceSapling),
        new SimpleTexture("textures/blocks/sapling_spruce", Texture.spruceSapling),
        new IndexedTexture(0x3F, Texture.spruceSapling)));

    ALL_TEXTURES.put("white_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_wool", Texture.whiteWool),
        new SimpleTexture("assets/minecraft/textures/blocks/white_wool", Texture.whiteWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_white", Texture.whiteWool),
        new SimpleTexture("textures/blocks/cloth_0", Texture.whiteWool),
        new IndexedTexture(0x40, Texture.whiteWool)));
    ALL_TEXTURES.put("mob spawner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spawner", Texture.monsterSpawner),
        new SimpleTexture("assets/minecraft/textures/blocks/mob_spawner", Texture.monsterSpawner),
        new SimpleTexture("textures/blocks/mobSpawner", Texture.monsterSpawner),
        new IndexedTexture(0x41, Texture.monsterSpawner)));
    ALL_TEXTURES.put("snow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/snow", Texture.snowBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/snow", Texture.snowBlock),
        new SimpleTexture("textures/blocks/snow", Texture.snowBlock),
        new IndexedTexture(0x42, Texture.snowBlock)));
    ALL_TEXTURES.put("ice", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/ice", Texture.ice),
        new SimpleTexture("assets/minecraft/textures/blocks/ice", Texture.ice),
        new SimpleTexture("textures/blocks/ice", Texture.ice),
        new IndexedTexture(0x43, Texture.ice)));
    ALL_TEXTURES.put("grass_block_snow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/grass_block_snow", Texture.snowSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_block_snow", Texture.snowSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_side_snowed", Texture.snowSide),
        new SimpleTexture("textures/blocks/snow_side", Texture.snowSide),
        new IndexedTexture(0x44, Texture.snowSide)));
    ALL_TEXTURES.put("cactus_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cactus_top", Texture.cactusTop),
        new SimpleTexture("assets/minecraft/textures/blocks/cactus_top", Texture.cactusTop),
        new SimpleTexture("textures/blocks/cactus_top", Texture.cactusTop),
        new IndexedTexture(0x45, Texture.cactusTop)));
    ALL_TEXTURES.put("cactus_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cactus_side", Texture.cactusSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cactus_side", Texture.cactusSide),
        new SimpleTexture("textures/blocks/cactus_side", Texture.cactusSide),
        new IndexedTexture(0x46, Texture.cactusSide)));
    ALL_TEXTURES.put("cactus_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cactus_bottom", Texture.cactusBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/cactus_bottom", Texture.cactusBottom),
        new SimpleTexture("textures/blocks/cactus_bottom", Texture.cactusBottom),
        new IndexedTexture(0x47, Texture.cactusBottom)));
    ALL_TEXTURES.put("clay", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/clay", Texture.clay),
        new SimpleTexture("assets/minecraft/textures/blocks/clay", Texture.clay),
        new SimpleTexture("textures/blocks/clay", Texture.clay),
        new IndexedTexture(0x48, Texture.clay)));
    ALL_TEXTURES.put("sugar_cane", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sugar_cane", Texture.sugarCane),
        new SimpleTexture("assets/minecraft/textures/blocks/sugar_cane", Texture.sugarCane),
        new SimpleTexture("assets/minecraft/textures/blocks/reeds", Texture.sugarCane),
        new SimpleTexture("textures/blocks/reeds", Texture.sugarCane),
        new IndexedTexture(0x49, Texture.sugarCane)));
    ALL_TEXTURES.put("note_block", new AlternateTextures(
      new SimpleTexture("assets/minecraft/textures/block/note_block", Texture.noteBlock),
      new SimpleTexture("assets/minecraft/textures/blocks/note_block", Texture.noteBlock),
      new SimpleTexture("assets/minecraft/textures/blocks/noteblock", Texture.noteBlock),
      new SimpleTexture("textures/blocks/musicBlock", Texture.noteBlock),
      new IndexedTexture(0x4A, Texture.noteBlock)));
    ALL_TEXTURES.put("jukebox_side", new AlternateTextures(
      new SimpleTexture("assets/minecraft/textures/block/jukebox_side", Texture.jukeboxSide),
      new SimpleTexture("assets/minecraft/textures/block/note_block", Texture.jukeboxSide),
      new SimpleTexture("assets/minecraft/textures/blocks/note_block", Texture.jukeboxSide),
      new SimpleTexture("assets/minecraft/textures/blocks/noteblock", Texture.jukeboxSide),
      new SimpleTexture("textures/blocks/musicBlock", Texture.jukeboxSide),
      new IndexedTexture(0x4A, Texture.jukeboxSide)));
    ALL_TEXTURES.put("jukebox_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jukebox_top", Texture.jukeboxTop),
        new SimpleTexture("assets/minecraft/textures/blocks/jukebox_top", Texture.jukeboxTop),
        new SimpleTexture("textures/blocks/jukebox_top", Texture.jukeboxTop),
        new IndexedTexture(0x4B, Texture.jukeboxTop)));
    ALL_TEXTURES.put("lily_pad", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lily_pad", Texture.lilyPad),
        new SimpleTexture("assets/minecraft/textures/blocks/lily_pad", Texture.lilyPad),
        new SimpleTexture("assets/minecraft/textures/blocks/waterlily", Texture.lilyPad),
        new SimpleTexture("textures/blocks/waterlily", Texture.lilyPad),
        new IndexedTexture(0x4C, Texture.lilyPad)));
    ALL_TEXTURES.put("mycelium_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mycelium_side", Texture.myceliumSide),
        new SimpleTexture("assets/minecraft/textures/blocks/mycelium_side", Texture.myceliumSide),
        new SimpleTexture("textures/blocks/mycel_side", Texture.myceliumSide),
        new IndexedTexture(0x4D, Texture.myceliumSide)));
    ALL_TEXTURES.put("mycelium_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mycelium_top", Texture.myceliumTop),
        new SimpleTexture("assets/minecraft/textures/blocks/mycelium_top", Texture.myceliumTop),
        new SimpleTexture("textures/blocks/mycel_top", Texture.myceliumTop),
        new IndexedTexture(0x4E, Texture.myceliumTop)));
    ALL_TEXTURES.put("birch_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_sapling", Texture.birchSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_sapling", Texture.birchSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_birch", Texture.birchSapling),
        new SimpleTexture("textures/blocks/sapling_birch", Texture.birchSapling),
        new IndexedTexture(0x4F, Texture.birchSapling)));

    ALL_TEXTURES.put("torch", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/torch", Texture.torch),
        new SimpleTexture("assets/minecraft/textures/blocks/torch", Texture.torch),
        new SimpleTexture("assets/minecraft/textures/blocks/torch_on", Texture.torch),
        new SimpleTexture("textures/blocks/torch", Texture.torch),
        new IndexedTexture(0x50, Texture.torch)));
    ALL_TEXTURES.put("oak_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_door_top",
            Texture.oakDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_door_upper",
            Texture.oakDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_wood_upper",
            Texture.oakDoorTop),
        new SimpleTexture("textures/blocks/doorWood_upper", Texture.oakDoorTop),
        new IndexedTexture(0x51, Texture.oakDoorTop)));
    ALL_TEXTURES.put("iron_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_door_top", Texture.ironDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_door_upper", Texture.ironDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_iron_upper", Texture.ironDoorTop),
        new SimpleTexture("textures/blocks/doorIron_upper", Texture.ironDoorTop),
        new IndexedTexture(0x52, Texture.ironDoorTop)));
    ALL_TEXTURES.put("ladder", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/ladder", Texture.ladder),
        new SimpleTexture("assets/minecraft/textures/blocks/ladder", Texture.ladder),
        new SimpleTexture("textures/blocks/ladder", Texture.ladder),
        new IndexedTexture(0x53, Texture.ladder)));
    ALL_TEXTURES.put("trapdoor", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_trapdoor", Texture.trapdoor),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_trapdoor", Texture.trapdoor),
        new SimpleTexture("assets/minecraft/textures/blocks/trapdoor", Texture.trapdoor),
        new SimpleTexture("textures/blocks/trapdoor", Texture.trapdoor),
        new IndexedTexture(0x54, Texture.trapdoor)));
    ALL_TEXTURES.put("iron bars", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_bars", Texture.ironBars),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_bars", Texture.ironBars),
        new SimpleTexture("textures/blocks/fenceIron", Texture.ironBars),
        new IndexedTexture(0x55, Texture.ironBars)));
    ALL_TEXTURES.put("farmland_wet", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/farmland_moist", Texture.farmlandWet),
        new SimpleTexture("assets/minecraft/textures/blocks/farmland_wet", Texture.farmlandWet),
        new SimpleTexture("textures/blocks/farmland_wet", Texture.farmlandWet),
        new IndexedTexture(0x56, Texture.farmlandWet)));
    ALL_TEXTURES.put("farmland_dry", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/farmland", Texture.farmlandDry),
        new SimpleTexture("assets/minecraft/textures/blocks/farmland", Texture.farmlandDry),
        new SimpleTexture("assets/minecraft/textures/blocks/farmland_dry", Texture.farmlandDry),
        new SimpleTexture("textures/blocks/farmland_dry", Texture.farmlandDry),
        new IndexedTexture(0x57, Texture.farmlandDry)));
    ALL_TEXTURES.put("wheat_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage0", Texture.crops0),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage0", Texture.crops0),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_0", Texture.crops0),
        new SimpleTexture("textures/blocks/crops_0", Texture.crops0),
        new IndexedTexture(0x58, Texture.crops0)));
    ALL_TEXTURES.put("wheat_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage1", Texture.crops1),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage1", Texture.crops1),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_1", Texture.crops1),
        new SimpleTexture("textures/blocks/crops_1", Texture.crops1),
        new IndexedTexture(0x59, Texture.crops1)));
    ALL_TEXTURES.put("wheat_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage2", Texture.crops2),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage2", Texture.crops2),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_2", Texture.crops2),
        new SimpleTexture("textures/blocks/crops_2", Texture.crops2),
        new IndexedTexture(0x5A, Texture.crops2)));
    ALL_TEXTURES.put("wheat_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage3", Texture.crops3),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage3", Texture.crops3),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_3", Texture.crops3),
        new SimpleTexture("textures/blocks/crops_3", Texture.crops3),
        new IndexedTexture(0x5B, Texture.crops3)));
    ALL_TEXTURES.put("wheat_stage_4", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage4", Texture.crops4),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage4", Texture.crops4),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_4", Texture.crops4),
        new SimpleTexture("textures/blocks/crops_4", Texture.crops4),
        new IndexedTexture(0x5C, Texture.crops4)));
    ALL_TEXTURES.put("wheat_stage_5", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage5", Texture.crops5),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage5", Texture.crops5),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_5", Texture.crops5),
        new SimpleTexture("textures/blocks/crops_5", Texture.crops5),
        new IndexedTexture(0x5D, Texture.crops5)));
    ALL_TEXTURES.put("wheat_stage_6", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage6", Texture.crops6),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage6", Texture.crops6),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_6", Texture.crops6),
        new SimpleTexture("textures/blocks/crops_6", Texture.crops6),
        new IndexedTexture(0x5E, Texture.crops6)));
    ALL_TEXTURES.put("wheat_stage_7", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wheat_stage7", Texture.crops7),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage7", Texture.crops7),
        new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_7", Texture.crops7),
        new SimpleTexture("textures/blocks/crops_7", Texture.crops7),
        new IndexedTexture(0x5F, Texture.crops7)));

    ALL_TEXTURES.put("lever", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lever", Texture.lever),
        new SimpleTexture("assets/minecraft/textures/blocks/lever", Texture.lever),
        new SimpleTexture("textures/blocks/lever", Texture.lever),
        new IndexedTexture(0x60, Texture.lever)));
    ALL_TEXTURES.put("oak_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oak_door_bottom",
            Texture.oakDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/oak_door_lower",
            Texture.oakDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_wood_lower",
            Texture.oakDoorBottom),
        new SimpleTexture("textures/blocks/doorWood_lower", Texture.oakDoorBottom),
        new IndexedTexture(0x61, Texture.oakDoorBottom)));
    ALL_TEXTURES.put("iron_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_door_bottom",
            Texture.ironDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_door_lower",
            Texture.ironDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_iron_lower",
            Texture.ironDoorBottom),
        new SimpleTexture("textures/blocks/doorIron_lower", Texture.ironDoorBottom),
        new IndexedTexture(0x62, Texture.ironDoorBottom)));
    ALL_TEXTURES.put("redstone_torch_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_torch",
            Texture.redstoneTorchOn),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch",
            Texture.redstoneTorchOn),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch_on",
            Texture.redstoneTorchOn),
        new SimpleTexture("textures/blocks/redtorch_lit", Texture.redstoneTorchOn),
        new IndexedTexture(0x63, Texture.redstoneTorchOn)));
    ALL_TEXTURES.put("stonebrick_mossy", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mossy_stone_bricks",
            Texture.mossyStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/mossy_stone_bricks",
            Texture.mossyStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_mossy",
            Texture.mossyStoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth_mossy", Texture.mossyStoneBrick),
        new IndexedTexture(0x64, Texture.mossyStoneBrick)));
    ALL_TEXTURES.put("stonebrick_cracked", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cracked_stone_bricks",
            Texture.crackedStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/cracked_stone_bricks",
            Texture.crackedStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_cracked",
            Texture.crackedStoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth_cracked", Texture.crackedStoneBrick),
        new IndexedTexture(0x65, Texture.crackedStoneBrick)));
    ALL_TEXTURES.put("pumpkin_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pumpkin_top", Texture.pumpkinTop),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_top", Texture.pumpkinTop),
        new SimpleTexture("textures/blocks/pumpkin_top", Texture.pumpkinTop),
        new IndexedTexture(0x66, Texture.pumpkinTop)));
    ALL_TEXTURES.put("netherrack", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/netherrack", Texture.netherrack),
        new SimpleTexture("assets/minecraft/textures/blocks/netherrack", Texture.netherrack),
        new SimpleTexture("textures/blocks/hellrock", Texture.netherrack),
        new IndexedTexture(0x67, Texture.netherrack)));
    ALL_TEXTURES.put("soul_sand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/soul_sand", Texture.soulsand),
        new SimpleTexture("assets/minecraft/textures/blocks/soul_sand", Texture.soulsand),
        new SimpleTexture("textures/blocks/hellsand", Texture.soulsand),
        new IndexedTexture(0x68, Texture.soulsand)));
    ALL_TEXTURES.put("glowstone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/glowstone", Texture.glowstone),
        new SimpleTexture("assets/minecraft/textures/blocks/glowstone", Texture.glowstone),
        new SimpleTexture("textures/blocks/lightgem", Texture.glowstone),
        new IndexedTexture(0x69, Texture.glowstone)));
    ALL_TEXTURES.put("piston_top_sticky", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_top_sticky",
            Texture.pistonTopSticky),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_top_sticky",
            Texture.pistonTopSticky),
        new SimpleTexture("textures/blocks/piston_top_sticky", Texture.pistonTopSticky),
        new IndexedTexture(0x6A, Texture.pistonTopSticky)));
    ALL_TEXTURES.put("piston_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_top", Texture.pistonTop),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_top", Texture.pistonTop),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_top_normal", Texture.pistonTop),
        new SimpleTexture("textures/blocks/piston_top", Texture.pistonTop),
        new IndexedTexture(0x6B, Texture.pistonTop)));
    ALL_TEXTURES.put("piston_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_side", Texture.pistonSide),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_side", Texture.pistonSide),
        new SimpleTexture("textures/blocks/piston_side", Texture.pistonSide),
        new IndexedTexture(0x6C, Texture.pistonSide)));
    ALL_TEXTURES.put("piston_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_bottom", Texture.pistonBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_bottom", Texture.pistonBottom),
        new SimpleTexture("textures/blocks/piston_bottom", Texture.pistonBottom),
        new IndexedTexture(0x6D, Texture.pistonBottom)));
    ALL_TEXTURES.put("piston_inner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/piston_inner", Texture.pistonInnerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/piston_inner", Texture.pistonInnerTop),
        new SimpleTexture("textures/blocks/piston_inner_top", Texture.pistonInnerTop),
        new IndexedTexture(0x6E, Texture.pistonInnerTop)));
    // TODO pumpkin stem variants
    ALL_TEXTURES.put("melon_stem", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/melon_stem",
            Texture.stemStraight),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_stem",
            Texture.stemStraight),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_stem_disconnected",
            Texture.stemStraight),
        new SimpleTexture("textures/blocks/stem_straight", Texture.stemStraight),
        new IndexedTexture(0x6F, Texture.stemStraight)));

    ALL_TEXTURES.put("rail_corner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rail_corner",
            Texture.railsCurved),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_corner",
            Texture.railsCurved),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_normal_turned",
            Texture.railsCurved),
        new SimpleTexture("textures/blocks/rail_turn", Texture.railsCurved),
        new IndexedTexture(0x70, Texture.railsCurved)));
    ALL_TEXTURES.put("black_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_wool", Texture.blackWool),
        new SimpleTexture("assets/minecraft/textures/blocks/black_wool", Texture.blackWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_black", Texture.blackWool),
        new SimpleTexture("textures/blocks/cloth_15", Texture.blackWool),
        new IndexedTexture(0x71, Texture.blackWool)));
    ALL_TEXTURES.put("gray_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_wool", Texture.grayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_wool", Texture.grayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_gray", Texture.grayWool),
        new SimpleTexture("textures/blocks/cloth_7", Texture.grayWool),
        new IndexedTexture(0x72, Texture.grayWool)));
    ALL_TEXTURES.put("redstone_torch_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_torch_off",
            Texture.redstoneTorchOff),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch_off",
            Texture.redstoneTorchOff),
        new SimpleTexture("textures/blocks/redtorch", Texture.redstoneTorchOff),
        new IndexedTexture(0x73, Texture.redstoneTorchOff)));
    ALL_TEXTURES.put("spruce_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_log", Texture.spruceWood),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_log", Texture.spruceWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_spruce", Texture.spruceWood),
        new SimpleTexture("textures/blocks/tree_spruce", Texture.spruceWood),
        new IndexedTexture(0x74, Texture.spruceWood)));
    ALL_TEXTURES.put("birch_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_log", Texture.birchWood),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_log", Texture.birchWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_birch", Texture.birchWood),
        new SimpleTexture("textures/blocks/tree_birch", Texture.birchWood),
        new IndexedTexture(0x75, Texture.birchWood)));
    ALL_TEXTURES.put("pumpkin_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pumpkin_side", Texture.pumpkinSide),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_side", Texture.pumpkinSide),
        new SimpleTexture("textures/blocks/pumpkin_side", Texture.pumpkinSide),
        new IndexedTexture(0x76, Texture.pumpkinSide)));
    ALL_TEXTURES.put("pumpkin_face", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carved_pumpkin",
            Texture.pumpkinFront),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face",
            Texture.pumpkinFront),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face_off",
            Texture.pumpkinFront),
        new SimpleTexture("textures/blocks/pumpkin_face", Texture.pumpkinFront),
        new IndexedTexture(0x77, Texture.pumpkinFront)));
    ALL_TEXTURES.put("pumpkin_face_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jack_o_lantern",
            Texture.jackolanternFront),
        new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face_on",
            Texture.jackolanternFront),
        new SimpleTexture("textures/blocks/pumpkin_jack", Texture.jackolanternFront),
        new IndexedTexture(0x78, Texture.jackolanternFront)));
    ALL_TEXTURES.put("cake_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_top", Texture.cakeTop),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_top", Texture.cakeTop),
        new SimpleTexture("textures/blocks/cake_top", Texture.cakeTop),
        new IndexedTexture(0x79, Texture.cakeTop)));
    ALL_TEXTURES.put("cake_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_side", Texture.cakeSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_side", Texture.cakeSide),
        new SimpleTexture("textures/blocks/cake_side", Texture.cakeSide),
        new IndexedTexture(0x7A, Texture.cakeSide)));
    ALL_TEXTURES.put("cake_inner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_inner", Texture.cakeInside),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_inner", Texture.cakeInside),
        new SimpleTexture("textures/blocks/cake_inner", Texture.cakeInside),
        new IndexedTexture(0x7B, Texture.cakeInside)));
    ALL_TEXTURES.put("cake_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cake_bottom", Texture.cakeBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/cake_bottom", Texture.cakeBottom),
        new SimpleTexture("textures/blocks/cake_bottom", Texture.cakeBottom),
        new IndexedTexture(0x7C, Texture.cakeBottom)));
    ALL_TEXTURES.put("red_mushroom_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_mushroom_block",
            Texture.hugeRedMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/red_mushroom_block",
            Texture.hugeRedMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_red",
            Texture.hugeRedMushroom),
        new SimpleTexture("textures/blocks/mushroom_skin_red", Texture.hugeRedMushroom),
        new IndexedTexture(0x7D, Texture.hugeRedMushroom)));
    ALL_TEXTURES.put("brown_mushroom_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_mushroom_block",
            Texture.hugeBrownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_mushroom_block",
            Texture.hugeBrownMushroom),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_brown",
            Texture.hugeBrownMushroom),
        new SimpleTexture("textures/blocks/mushroom_skin_brown", Texture.hugeBrownMushroom),
        new IndexedTexture(0x7E, Texture.hugeBrownMushroom)));
    ALL_TEXTURES.put("melon_stem_connected", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/attached_melon_stem",
            Texture.stemBent),
        new SimpleTexture("assets/minecraft/textures/blocks/attached_melon_stem",
            Texture.stemBent),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_stem_connected",
            Texture.stemBent),
        new SimpleTexture("textures/blocks/stem_bent", Texture.stemBent),
        new IndexedTexture(0x7F, Texture.stemBent)));

    ALL_TEXTURES.put("rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rail", Texture.rails),
        new SimpleTexture("assets/minecraft/textures/blocks/rail", Texture.rails),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_normal", Texture.rails),
        new SimpleTexture("textures/blocks/rail", Texture.rails),
        new IndexedTexture(0x80, Texture.rails)));
    ALL_TEXTURES.put("red_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_wool", Texture.redWool),
        new SimpleTexture("assets/minecraft/textures/blocks/red_wool", Texture.redWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_red", Texture.redWool),
        new SimpleTexture("textures/blocks/cloth_14", Texture.redWool),
        new IndexedTexture(0x81, Texture.redWool)));
    ALL_TEXTURES.put("pink_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_wool", Texture.pinkWool),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_wool", Texture.pinkWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_pink", Texture.pinkWool),
        new SimpleTexture("textures/blocks/cloth_6", Texture.pinkWool),
        new IndexedTexture(0x82, Texture.pinkWool)));
    ALL_TEXTURES.put("repeater_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeater",
            Texture.redstoneRepeaterOff),
        new SimpleTexture("assets/minecraft/textures/blocks/repeater",
            Texture.redstoneRepeaterOff),
        new SimpleTexture("assets/minecraft/textures/blocks/repeater_off",
            Texture.redstoneRepeaterOff),
        new SimpleTexture("textures/blocks/repeater", Texture.redstoneRepeaterOff),
        new IndexedTexture(0x83, Texture.redstoneRepeaterOff)));
    ALL_TEXTURES.put("spruce_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_leaves", Texture.spruceLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_leaves", Texture.spruceLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_spruce", Texture.spruceLeaves),
        new SimpleTexture("textures/blocks/leaves_spruce", Texture.spruceLeaves),
        new IndexedTexture(0x84, Texture.spruceLeaves)));
    ALL_TEXTURES.put("melon_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/melon_side", Texture.melonSide),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_side", Texture.melonSide),
        new SimpleTexture("textures/blocks/melon_side", Texture.melonSide),
        new IndexedTexture(0x88, Texture.melonSide)));
    ALL_TEXTURES.put("melon_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/melon_top", Texture.melonTop),
        new SimpleTexture("assets/minecraft/textures/blocks/melon_top", Texture.melonTop),
        new SimpleTexture("textures/blocks/melon_top", Texture.melonTop),
        new IndexedTexture(0x89, Texture.melonTop)));
    ALL_TEXTURES.put("cauldron_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_top", Texture.cauldronTop),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_top", Texture.cauldronTop),
        new SimpleTexture("textures/blocks/cauldron_top", Texture.cauldronTop),
        new IndexedTexture(0x8A, Texture.cauldronTop)));
    ALL_TEXTURES.put("cauldron_inner", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_inner",
            Texture.cauldronInside),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_inner",
            Texture.cauldronInside),
        new SimpleTexture("textures/blocks/cauldron_inner", Texture.cauldronInside),
        new IndexedTexture(0x8B, Texture.cauldronInside)));
    ALL_TEXTURES.put("mushroom_stem", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mushroom_stem",
            Texture.mushroomStem),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_stem",
            Texture.mushroomStem),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_stem",
            Texture.mushroomStem),
        new SimpleTexture("textures/blocks/mushroom_skin_stem", Texture.mushroomStem),
        new IndexedTexture(0x8D, Texture.mushroomStem)));
    ALL_TEXTURES.put("mushroom_block_inside", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/mushroom_block_inside",
            Texture.mushroomPores),
        new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_inside",
            Texture.mushroomPores),
        new SimpleTexture("textures/blocks/mushroom_inside", Texture.mushroomPores),
        new IndexedTexture(0x8E, Texture.mushroomPores)));
    ALL_TEXTURES.put("vine", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/vine", Texture.vines),
        new SimpleTexture("assets/minecraft/textures/blocks/vine", Texture.vines),
        new SimpleTexture("textures/blocks/vine", Texture.vines),
        new IndexedTexture(0x8F, Texture.vines)));

    ALL_TEXTURES.put("lapis_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lapis_block", Texture.lapisBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/lapis_block", Texture.lapisBlock),
        new SimpleTexture("textures/blocks/blockLapis", Texture.lapisBlock),
        new IndexedTexture(0x90, Texture.lapisBlock)));
    ALL_TEXTURES.put("green_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_wool", Texture.greenWool),
        new SimpleTexture("assets/minecraft/textures/blocks/green_wool", Texture.greenWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_green", Texture.greenWool),
        new SimpleTexture("textures/blocks/cloth_13", Texture.greenWool),
        new IndexedTexture(0x91, Texture.greenWool)));
    ALL_TEXTURES.put("lime_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_wool", Texture.limeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_wool", Texture.limeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_lime", Texture.limeWool),
        new SimpleTexture("textures/blocks/cloth_5", Texture.limeWool),
        new IndexedTexture(0x92, Texture.limeWool)));
    ALL_TEXTURES.put("repeater_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeater_on",
            Texture.redstoneRepeaterOn),
        new SimpleTexture("assets/minecraft/textures/blocks/repeater_on",
            Texture.redstoneRepeaterOn),
        new SimpleTexture("textures/blocks/repeater_lit", Texture.redstoneRepeaterOn),
        new IndexedTexture(0x93, Texture.redstoneRepeaterOn)));
    ALL_TEXTURES.put("glass_pane_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/glass_pane_top", Texture.glassPaneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top", Texture.glassPaneTop),
        new SimpleTexture("textures/blocks/thinglass_top", Texture.glassPaneTop),
        new IndexedTexture(0x94, Texture.glassPaneTop)));
    ALL_TEXTURES.put("jungle_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_log", Texture.jungleWood),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_log", Texture.jungleWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_jungle", Texture.jungleWood),
        new SimpleTexture("textures/blocks/tree_jungle", Texture.jungleWood),
        new IndexedTexture(0x99, Texture.jungleWood)));
    ALL_TEXTURES.put("cauldron_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_side", Texture.cauldronSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_side", Texture.cauldronSide),
        new SimpleTexture("textures/blocks/cauldron_side", Texture.cauldronSide),
        new IndexedTexture(0x9A, Texture.cauldronSide)));
    ALL_TEXTURES.put("cauldron_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cauldron_bottom",
            Texture.cauldronBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/cauldron_bottom",
            Texture.cauldronBottom),
        new SimpleTexture("textures/blocks/cauldron_bottom", Texture.cauldronBottom),
        new IndexedTexture(0x9B, Texture.cauldronBottom)));
    ALL_TEXTURES.put("brewing_stand_base", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brewing_stand_base",
            Texture.brewingStandBase),
        new SimpleTexture("assets/minecraft/textures/blocks/brewing_stand_base",
            Texture.brewingStandBase),
        new SimpleTexture("textures/blocks/brewingStand_base", Texture.brewingStandBase),
        new IndexedTexture(0x9C, Texture.brewingStandBase)));
    ALL_TEXTURES.put("brewing_stand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brewing_stand",
            Texture.brewingStandSide),
        new SimpleTexture("assets/minecraft/textures/blocks/brewing_stand",
            Texture.brewingStandSide),
        new SimpleTexture("textures/blocks/brewingStand", Texture.brewingStandSide),
        new IndexedTexture(0x9D, Texture.brewingStandSide)));
    ALL_TEXTURES.put("endframe_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_portal_frame_top",
            Texture.endPortalFrameTop),
        new SimpleTexture("assets/minecraft/textures/blocks/end_portal_frame_top",
            Texture.endPortalFrameTop),
        new SimpleTexture("assets/minecraft/textures/blocks/endframe_top",
            Texture.endPortalFrameTop),
        new SimpleTexture("textures/blocks/endframe_top", Texture.endPortalFrameTop),
        new IndexedTexture(0x9E, Texture.endPortalFrameTop)));
    ALL_TEXTURES.put("endframe_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_portal_frame_side",
            Texture.endPortalFrameSide),
        new SimpleTexture("assets/minecraft/textures/blocks/end_portal_frame_side",
            Texture.endPortalFrameSide),
        new SimpleTexture("assets/minecraft/textures/blocks/endframe_side",
            Texture.endPortalFrameSide),
        new SimpleTexture("textures/blocks/endframe_side", Texture.endPortalFrameSide),
        new IndexedTexture(0x9F, Texture.endPortalFrameSide)));

    ALL_TEXTURES.put("lapis_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lapis_ore", Texture.lapisOre),
        new SimpleTexture("assets/minecraft/textures/blocks/lapis_ore", Texture.lapisOre),
        new SimpleTexture("textures/blocks/oreLapis", Texture.lapisOre),
        new IndexedTexture(0xA0, Texture.lapisOre)));
    ALL_TEXTURES.put("brown_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_wool", Texture.brownWool),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_wool", Texture.brownWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_brown", Texture.brownWool),
        new SimpleTexture("textures/blocks/cloth_12", Texture.brownWool),
        new IndexedTexture(0xA1, Texture.brownWool)));
    ALL_TEXTURES.put("yellow_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_wool", Texture.yellowWool),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_wool", Texture.yellowWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_yellow",
            Texture.yellowWool),
        new SimpleTexture("textures/blocks/cloth_4", Texture.yellowWool),
        new IndexedTexture(0xA2, Texture.yellowWool)));
    ALL_TEXTURES.put("powered_rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/powered_rail", Texture.poweredRailOff),
        new SimpleTexture("assets/minecraft/textures/blocks/powered_rail", Texture.poweredRailOff),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_golden", Texture.poweredRailOff),
        new SimpleTexture("textures/blocks/goldenRail", Texture.poweredRailOff),
        new IndexedTexture(0xA3, Texture.poweredRailOff)));
    ALL_TEXTURES.put("enchanting_table_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/enchanting_table_top",
            Texture.enchantmentTableTop),
        new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_top",
            Texture.enchantmentTableTop),
        new SimpleTexture("textures/blocks/enchantment_top", Texture.enchantmentTableTop),
        new IndexedTexture(0xA6, Texture.enchantmentTableTop)));
    ALL_TEXTURES.put("dragon_egg", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dragon_egg", Texture.dragonEgg),
        new SimpleTexture("assets/minecraft/textures/blocks/dragon_egg", Texture.dragonEgg),
        new SimpleTexture("textures/blocks/dragonEgg", Texture.dragonEgg),
        new IndexedTexture(0xA7, Texture.dragonEgg)));
    ALL_TEXTURES.put("cocoa_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cocoa_stage2",
            Texture.cocoaPlantLarge),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage2",
            Texture.cocoaPlantLarge),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_2",
            Texture.cocoaPlantLarge),
        new SimpleTexture("textures/blocks/cocoa_2", Texture.cocoaPlantLarge),
        new IndexedTexture(0xA8, Texture.cocoaPlantLarge)));
    ALL_TEXTURES.put("cocoa_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cocoa_stage1",
            Texture.cocoaPlantMedium),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage1",
            Texture.cocoaPlantMedium),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_1",
            Texture.cocoaPlantMedium),
        new SimpleTexture("textures/blocks/cocoa_1", Texture.cocoaPlantMedium),
        new IndexedTexture(0xA9, Texture.cocoaPlantMedium)));
    ALL_TEXTURES.put("cocoa_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cocoa_stage0",
            Texture.cocoaPlantSmall),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage0",
            Texture.cocoaPlantSmall),
        new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_0",
            Texture.cocoaPlantSmall),
        new SimpleTexture("textures/blocks/cocoa_0", Texture.cocoaPlantSmall),
        new IndexedTexture(0xAA, Texture.cocoaPlantSmall)));
    ALL_TEXTURES.put("emerald_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/emerald_ore", Texture.emeraldOre),
        new SimpleTexture("assets/minecraft/textures/blocks/emerald_ore", Texture.emeraldOre),
        new SimpleTexture("textures/blocks/oreEmerald", Texture.emeraldOre),
        new IndexedTexture(0xAB, Texture.emeraldOre)));
    ALL_TEXTURES.put("trip_wire_hook", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tripwire_hook",
            Texture.tripwireHook),
        new SimpleTexture("assets/minecraft/textures/blocks/trip_wire_hook",
            Texture.tripwireHook),
        new SimpleTexture("assets/minecraft/textures/blocks/trip_wire_source",
            Texture.tripwireHook),
        new SimpleTexture("textures/blocks/tripWireSource", Texture.tripwireHook),
        new IndexedTexture(0xAC, Texture.tripwireHook)));
    ALL_TEXTURES.put("trip_wire", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tripwire", Texture.tripwire),
        new SimpleTexture("assets/minecraft/textures/blocks/trip_wire", Texture.tripwire),
        new SimpleTexture("textures/blocks/tripWire", Texture.tripwire),
        new IndexedTexture(0xAD, Texture.tripwire)));
    ALL_TEXTURES.put("endframe_eye", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_portal_frame_eye",
            Texture.eyeOfTheEnder),
        new SimpleTexture("assets/minecraft/textures/blocks/end_portal_frame_eye",
            Texture.eyeOfTheEnder),
        new SimpleTexture("assets/minecraft/textures/blocks/endframe_eye", Texture.eyeOfTheEnder),
        new SimpleTexture("textures/blocks/endframe_eye", Texture.eyeOfTheEnder),
        new IndexedTexture(0xAE, Texture.eyeOfTheEnder)));
    ALL_TEXTURES.put("end_stone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_stone", Texture.endStone),
        new SimpleTexture("assets/minecraft/textures/blocks/end_stone", Texture.endStone),
        new SimpleTexture("textures/blocks/whiteStone", Texture.endStone),
        new IndexedTexture(0xAF, Texture.endStone)));

    ALL_TEXTURES.put("sandstone_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sandstone_top", Texture.sandstoneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_top", Texture.sandstoneTop),
        new SimpleTexture("textures/blocks/sandstone_top", Texture.sandstoneTop),
        new IndexedTexture(0xB0, Texture.sandstoneTop)));
    ALL_TEXTURES.put("blue_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_wool", Texture.blueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_wool", Texture.blueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_blue", Texture.blueWool),
        new SimpleTexture("textures/blocks/cloth_11", Texture.blueWool),
        new IndexedTexture(0xB1, Texture.blueWool)));
    ALL_TEXTURES.put("light_blue_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_wool",
            Texture.lightBlueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_wool",
            Texture.lightBlueWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_light_blue",
            Texture.lightBlueWool),
        new SimpleTexture("textures/blocks/cloth_3", Texture.lightBlueWool),
        new IndexedTexture(0xB2, Texture.lightBlueWool)));
    ALL_TEXTURES.put("powered_rail_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/powered_rail_on",
            Texture.poweredRailOn),
        new SimpleTexture("assets/minecraft/textures/blocks/powered_rail_on",
            Texture.poweredRailOn),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_golden_powered",
            Texture.poweredRailOn),
        new SimpleTexture("textures/blocks/goldenRail_powered", Texture.poweredRailOn),
        new IndexedTexture(0xB3, Texture.poweredRailOn)));
    ALL_TEXTURES.put("enchanting_table_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/enchanting_table_side",
            Texture.enchantmentTableSide),
        new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_side",
            Texture.enchantmentTableSide),
        new SimpleTexture("textures/blocks/enchantment_side", Texture.enchantmentTableSide),
        new IndexedTexture(0xB6, Texture.enchantmentTableSide)));
    ALL_TEXTURES.put("enchanting_table_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/enchanting_table_bottom",
            Texture.enchantmentTableBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_bottom",
            Texture.enchantmentTableBottom),
        new SimpleTexture("textures/blocks/enchantment_bottom", Texture.enchantmentTableBottom),
        new IndexedTexture(0xB7, Texture.enchantmentTableBottom)));

    // Command block textures were changed in Minecraft 1.9.
    ALL_TEXTURES.put("command_block_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_back",
            Texture.commandBlockBack),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_back",
            Texture.commandBlockBack)));
    ALL_TEXTURES.put("command_block_conditional", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_conditional",
            Texture.commandBlockConditional),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_conditional",
            Texture.commandBlockConditional)));
    ALL_TEXTURES.put("command_block_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_front",
            Texture.commandBlockFront),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_front",
            Texture.commandBlockFront)));
    ALL_TEXTURES.put("command_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/command_block_side",
            Texture.commandBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/command_block_side",
            Texture.commandBlockSide)));

    ALL_TEXTURES.put("repeating_command_block_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_back",
            Texture.repeatingCommandBlockBack),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_back",
            Texture.repeatingCommandBlockBack)));
    ALL_TEXTURES.put("repeating_command_block_conditional", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_conditional",
            Texture.repeatingCommandBlockConditional),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_conditional",
            Texture.repeatingCommandBlockConditional)));
    ALL_TEXTURES.put("repeating_command_block_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_front",
            Texture.repeatingCommandBlockFront),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_front",
            Texture.repeatingCommandBlockFront)));
    ALL_TEXTURES.put("repeating_command_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/repeating_command_block_side",
            Texture.repeatingCommandBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/repeating_command_block_side",
            Texture.repeatingCommandBlockSide)));

    ALL_TEXTURES.put("chain_command_block_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_back",
            Texture.chainCommandBlockBack),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_back",
            Texture.chainCommandBlockBack)));
    ALL_TEXTURES.put("chain_command_block_conditional", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_conditional",
            Texture.chainCommandBlockConditional),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_conditional",
            Texture.chainCommandBlockConditional)));
    ALL_TEXTURES.put("chain_command_block_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_front",
            Texture.chainCommandBlockFront),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_front",
            Texture.chainCommandBlockFront)));
    ALL_TEXTURES.put("chain_command_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chain_command_block_side",
            Texture.chainCommandBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/chain_command_block_side",
            Texture.chainCommandBlockSide)));

    ALL_TEXTURES.put("flower_pot", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/flower_pot", Texture.flowerPot),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_pot", Texture.flowerPot),
        new SimpleTexture("textures/blocks/flowerPot", Texture.flowerPot),
        new IndexedTexture(0xBA, Texture.flowerPot)));
    ALL_TEXTURES.put("quartz_ore", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_quartz_ore", Texture.netherQuartzOre),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_ore", Texture.netherQuartzOre),
        new SimpleTexture("textures/blocks/netherquartz", Texture.netherQuartzOre),
        new IndexedTexture(0xBF, Texture.netherQuartzOre)));

    ALL_TEXTURES.put("sandstone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sandstone",
            Texture.sandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone",
            Texture.sandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_normal",
            Texture.sandstoneSide),
        new SimpleTexture("textures/blocks/sandstone_side", Texture.sandstoneSide),
        new IndexedTexture(0xC0, Texture.sandstoneSide)));
    ALL_TEXTURES.put("purple_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_wool", Texture.purpleWool),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_wool", Texture.purpleWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_purple",
            Texture.purpleWool),
        new SimpleTexture("textures/blocks/cloth_10", Texture.purpleWool),
        new IndexedTexture(0xC1, Texture.purpleWool)));
    ALL_TEXTURES.put("magenta_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_wool", Texture.magentaWool),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_wool", Texture.magentaWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_magenta",
            Texture.magentaWool),
        new SimpleTexture("textures/blocks/cloth_2", Texture.magentaWool),
        new IndexedTexture(0xC2, Texture.magentaWool)));
    ALL_TEXTURES.put("detector_rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/detector_rail", Texture.detectorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/detector_rail", Texture.detectorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_detector", Texture.detectorRail),
        new SimpleTexture("textures/blocks/detectorRail", Texture.detectorRail),
        new IndexedTexture(0xC3, Texture.detectorRail)));
    // Since 1.5:
    ALL_TEXTURES.put("detector_rail_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/detector_rail_on", Texture.detectorRailOn),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_detector_powered", Texture.detectorRailOn)));
    ALL_TEXTURES.put("jungle_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_leaves",
            Texture.jungleTreeLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_leaves",
            Texture.jungleTreeLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_jungle",
            Texture.jungleTreeLeaves),
        new SimpleTexture("textures/blocks/leaves_jungle", Texture.jungleTreeLeaves),
        new IndexedTexture(0xC4, Texture.jungleTreeLeaves)));
    ALL_TEXTURES.put("spruce_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_planks", Texture.sprucePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_planks", Texture.sprucePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_spruce", Texture.sprucePlanks),
        new SimpleTexture("textures/blocks/wood_spruce", Texture.sprucePlanks),
        new IndexedTexture(0xC6, Texture.sprucePlanks)));
    ALL_TEXTURES.put("jungle_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_planks",
            Texture.jungleTreePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_planks",
            Texture.jungleTreePlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_jungle",
            Texture.jungleTreePlanks),
        new SimpleTexture("textures/blocks/wood_jungle", Texture.jungleTreePlanks),
        new IndexedTexture(0xC7, Texture.jungleTreePlanks)));
    ALL_TEXTURES.put("carrots_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage0", Texture.carrots0),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage0", Texture.carrots0),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_0", Texture.carrots0),
        new SimpleTexture("textures/blocks/carrots_0", Texture.carrots0),
        new IndexedTexture(0xC8, Texture.carrots0)));
    ALL_TEXTURES.put("potatoes_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage0", Texture.potatoes0),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage0", Texture.potatoes0),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_0", Texture.potatoes0),
        new SimpleTexture("textures/blocks/potatoes_0", Texture.potatoes0),
        new IndexedTexture(0xC8, Texture.potatoes0)));
    ALL_TEXTURES.put("carrots_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage1", Texture.carrots1),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage1", Texture.carrots1),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_1", Texture.carrots1),
        new SimpleTexture("textures/blocks/carrots_1", Texture.carrots1),
        new IndexedTexture(0xC9, Texture.carrots1)));
    ALL_TEXTURES.put("potatoes_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage1", Texture.potatoes1),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage1", Texture.potatoes1),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_1", Texture.potatoes1),
        new SimpleTexture("textures/blocks/potatoes_1", Texture.potatoes1),
        new IndexedTexture(0xC9, Texture.potatoes1)));
    ALL_TEXTURES.put("carrots_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage2", Texture.carrots2),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage2", Texture.carrots2),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_2", Texture.carrots2),
        new SimpleTexture("textures/blocks/carrots_2", Texture.carrots2),
        new IndexedTexture(0xCA, Texture.carrots2)));
    ALL_TEXTURES.put("potatoes_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage2", Texture.potatoes2),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage2", Texture.potatoes2),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_2", Texture.potatoes2),
        new SimpleTexture("textures/blocks/potatoes_2", Texture.potatoes2),
        new IndexedTexture(0xCA, Texture.potatoes2)));
    ALL_TEXTURES.put("carrots_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/carrots_stage3", Texture.carrots3),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage3", Texture.carrots3),
        new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_3", Texture.carrots3),
        new SimpleTexture("textures/blocks/carrots_3", Texture.carrots3),
        new IndexedTexture(0xCB, Texture.carrots3)));
    ALL_TEXTURES.put("potatoes_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/potatoes_stage3", Texture.potatoes3),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage3", Texture.potatoes3),
        new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_3", Texture.potatoes3),
        new SimpleTexture("textures/blocks/potatoes_3", Texture.potatoes3),
        new IndexedTexture(0xCC, Texture.potatoes3)));
    ALL_TEXTURES.put("water_still", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/water_still", Texture.water),
        new SimpleTexture("assets/minecraft/textures/blocks/water_still", Texture.water),
        new SimpleTexture("textures/blocks/water", Texture.water),
        new IndexedTexture(0xCD, Texture.water)));

    ALL_TEXTURES.put("sandstone_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sandstone_bottom",
            Texture.sandstoneBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_bottom",
            Texture.sandstoneBottom),
        new SimpleTexture("textures/blocks/sandstone_bottom", Texture.sandstoneBottom),
        new IndexedTexture(0xD0, Texture.sandstoneBottom)));
    ALL_TEXTURES.put("cyan_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_wool", Texture.cyanWool),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_wool", Texture.cyanWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_cyan", Texture.cyanWool),
        new SimpleTexture("textures/blocks/cloth_9", Texture.cyanWool),
        new IndexedTexture(0xD1, Texture.cyanWool)));
    ALL_TEXTURES.put("orange_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_wool", Texture.orangeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_wool", Texture.orangeWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_orange",
            Texture.orangeWool),
        new SimpleTexture("textures/blocks/cloth_1", Texture.orangeWool),
        new IndexedTexture(0xD2, Texture.orangeWool)));
    ALL_TEXTURES.put("redstone_lamp_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_lamp", Texture.redstoneLampOff),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_lamp_off",
            Texture.redstoneLampOff),
        new SimpleTexture("textures/blocks/redstoneLight", Texture.redstoneLampOff),
        new IndexedTexture(0xD3, Texture.redstoneLampOff)));
    ALL_TEXTURES.put("redstone_lamp_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/redstone_lamp_on",
            Texture.redstoneLampOn),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_lamp_on",
            Texture.redstoneLampOn),
        new SimpleTexture("textures/blocks/redstoneLight_lit", Texture.redstoneLampOn),
        new IndexedTexture(0xD4, Texture.redstoneLampOn)));
    ALL_TEXTURES.put("stonebrick_carved", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_stone_bricks",
            Texture.circleStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_stone_bricks",
            Texture.circleStoneBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_carved",
            Texture.circleStoneBrick),
        new SimpleTexture("textures/blocks/stonebricksmooth_carved", Texture.circleStoneBrick),
        new IndexedTexture(0xD5, Texture.circleStoneBrick)));
    ALL_TEXTURES.put("birch_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_planks", Texture.birchPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_planks", Texture.birchPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_birch", Texture.birchPlanks),
        new SimpleTexture("textures/blocks/wood_birch", Texture.birchPlanks),
        new IndexedTexture(0xD6, Texture.birchPlanks)));
    ALL_TEXTURES.put("anvil_base", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/anvil", Texture.anvilSide),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil", Texture.anvilSide),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_base", Texture.anvilSide),
        new SimpleTexture("textures/blocks/anvil_base", Texture.anvilSide),
        new IndexedTexture(0xD7, Texture.anvilSide)));
    ALL_TEXTURES.put("anvil_top_damaged_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chipped_anvil_top",
            Texture.anvilTopDamaged1),
        new SimpleTexture("assets/minecraft/textures/blocks/chipped_anvil_top",
            Texture.anvilTopDamaged1),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_top_damaged_1",
            Texture.anvilTopDamaged1),
        new SimpleTexture("textures/blocks/anvil_top_damaged_1", Texture.anvilTopDamaged1),
        new IndexedTexture(0xD8, Texture.anvilTopDamaged1)));

    ALL_TEXTURES.put("nether_brick", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_bricks", Texture.netherBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_brick", Texture.netherBrick),
        new SimpleTexture("textures/blocks/netherBrick", Texture.netherBrick),
        new IndexedTexture(0xE0, Texture.netherBrick)));
    ALL_TEXTURES.put("silver_wool", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_wool", Texture.lightGrayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_wool", Texture.lightGrayWool),
        new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_silver",
            Texture.lightGrayWool),
        new SimpleTexture("textures/blocks/cloth_8", Texture.lightGrayWool),
        new IndexedTexture(0xE1, Texture.lightGrayWool)));
    ALL_TEXTURES.put("nether_wart_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_stage0",
            Texture.netherWart0),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage0",
            Texture.netherWart0),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_0",
            Texture.netherWart0),
        new SimpleTexture("textures/blocks/netherStalk_0", Texture.netherWart0),
        new IndexedTexture(0xE2, Texture.netherWart0)));
    ALL_TEXTURES.put("nether_wart_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_stage1",
            Texture.netherWart1),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage1",
            Texture.netherWart1),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_1",
            Texture.netherWart1),
        new SimpleTexture("textures/blocks/netherStalk_1", Texture.netherWart1),
        new IndexedTexture(0xE3, Texture.netherWart1)));
    ALL_TEXTURES.put("nether_wart_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_stage2",
            Texture.netherWart2),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage2",
            Texture.netherWart2),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_2",
            Texture.netherWart2),
        new SimpleTexture("textures/blocks/netherStalk_2", Texture.netherWart2),
        new IndexedTexture(0xE4, Texture.netherWart2)));
    ALL_TEXTURES.put("sandstone_carved", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_sandstone",
            Texture.sandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_sandstone",
            Texture.sandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_carved",
            Texture.sandstoneDecorated),
        new SimpleTexture("textures/blocks/sandstone_carved", Texture.sandstoneDecorated),
        new IndexedTexture(0xE5, Texture.sandstoneDecorated)));
    ALL_TEXTURES.put("sandstone_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cut_sandstone",
            Texture.sandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/cut_sandstone",
            Texture.sandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/sandstone_smooth",
            Texture.sandstoneCut),
        new SimpleTexture("textures/blocks/sandstone_smooth", Texture.sandstoneCut),
        new IndexedTexture(0xE6, Texture.sandstoneCut)));
    ALL_TEXTURES.put("anvil_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/anvil_top", Texture.anvilTop),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_top", Texture.anvilTop),
        new SimpleTexture("assets/minecraft/textures/blocks/anvil_top_damaged_0", Texture.anvilTop),
        new SimpleTexture("textures/blocks/anvil_top", Texture.anvilTop),
        new IndexedTexture(0xE7, Texture.anvilTop)));
    ALL_TEXTURES.put("anvil_top_damaged_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/damaged_anvil_top",
            Texture.anvilTopDamaged2),
        new SimpleTexture("assets/minecraft/textures/blocks/damaged_anvil_top",
            Texture.anvilTopDamaged2),
        new SimpleTexture("textures/blocks/anvil_top_damaged_2", Texture.anvilTopDamaged2),
        new IndexedTexture(0xE8, Texture.anvilTopDamaged2)));
    ALL_TEXTURES.put("lava_still", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lava_still", Texture.lava),
        new SimpleTexture("assets/minecraft/textures/blocks/lava_still", Texture.lava),
        new SimpleTexture("textures/blocks/lava", Texture.lava),
        new IndexedTexture(0xED, Texture.lava)));

    // MC 1.5
    ALL_TEXTURES.put("quartz_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_block_side", Texture.quartzSide),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_side", Texture.quartzSide),
        new SimpleTexture("textures/blocks/quartzblock_side", Texture.quartzSide)));
    ALL_TEXTURES.put("quartz_block_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_block_top", Texture.quartzTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_top", Texture.quartzTop),
        new SimpleTexture("textures/blocks/quartzblock_top", Texture.quartzTop)));
    ALL_TEXTURES.put("quartz_block_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_block_bottom",
            Texture.quartzBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_bottom",
            Texture.quartzBottom),
        new SimpleTexture("textures/blocks/quartzblock_bottom", Texture.quartzBottom)));
    ALL_TEXTURES.put("quartz_block_chiseled", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_quartz_block",
            Texture.quartzChiseled),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_quartz_block",
            Texture.quartzChiseled),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_chiseled",
            Texture.quartzChiseled),
        new SimpleTexture("textures/blocks/quartzblock_chiseled", Texture.quartzChiseled)));
    ALL_TEXTURES.put("quartz_block_chiseled_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_quartz_block_top",
            Texture.quartzChiseledTop),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_quartz_block_top",
            Texture.quartzChiseledTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_chiseled_top",
            Texture.quartzChiseledTop),
        new SimpleTexture("textures/blocks/quartzblock_chiseled_top", Texture.quartzChiseledTop)));
    ALL_TEXTURES.put("quartz_pillar", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_pillar",
            Texture.quartzPillar),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_pillar",
            Texture.quartzPillar),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_lines",
            Texture.quartzPillar),
        new SimpleTexture("textures/blocks/quartzblock_lines", Texture.quartzPillar)));
    ALL_TEXTURES.put("quartz_pillar_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/quartz_pillar_top",
            Texture.quartzPillarTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_pillar_top",
            Texture.quartzPillarTop),
        new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_lines_top",
            Texture.quartzPillarTop),
        new SimpleTexture("textures/blocks/quartzblock_lines_top", Texture.quartzPillarTop)));
    ALL_TEXTURES.put("dropper_front_horizontal", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dropper_front",
            Texture.dropperFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dropper_front",
            Texture.dropperFront),
        new SimpleTexture("assets/minecraft/textures/blocks/dropper_front_horizontal",
            Texture.dropperFront),
        new SimpleTexture("textures/blocks/dropper_front", Texture.dropperFront)));
    ALL_TEXTURES.put("dropper_front_vertical", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dropper_front_vertical",
            Texture.dropperFrontVertical),
        new SimpleTexture("assets/minecraft/textures/blocks/dropper_front_vertical",
            Texture.dropperFrontVertical)));
    ALL_TEXTURES.put("activator_rail", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/activator_rail", Texture.activatorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/activator_rail", Texture.activatorRail),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_activator", Texture.activatorRail),
        new SimpleTexture("textures/blocks/activatorRail", Texture.activatorRail)));
    ALL_TEXTURES.put("activator_rail_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/activator_rail_on",
            Texture.activatorRailPowered),
        new SimpleTexture("assets/minecraft/textures/blocks/activator_rail_on",
            Texture.activatorRailPowered),
        new SimpleTexture("assets/minecraft/textures/blocks/rail_activator_powered",
            Texture.activatorRailPowered),
        new SimpleTexture("textures/blocks/activatorRail_powered", Texture.activatorRailPowered)));
    ALL_TEXTURES.put("daylight_detector_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/daylight_detector_top",
            Texture.daylightDetectorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_top",
            Texture.daylightDetectorTop),
        new SimpleTexture("textures/blocks/daylightDetector_top", Texture.daylightDetectorTop)));
    ALL_TEXTURES.put("daylight_detector_inverted_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/daylight_detector_inverted_top",
            Texture.daylightDetectorInvertedTop),
        new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_inverted_top",
            Texture.daylightDetectorInvertedTop)));
    ALL_TEXTURES.put("daylight_detector_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/daylight_detector_side",
            Texture.daylightDetectorSide),
        new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_side",
            Texture.daylightDetectorSide),
        new SimpleTexture("textures/blocks/daylightDetector_side", Texture.daylightDetectorSide)));
    ALL_TEXTURES.put("comparator_off", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/comparator", Texture.comparatorOff),
        new SimpleTexture("assets/minecraft/textures/blocks/comparator", Texture.comparatorOff),
        new SimpleTexture("assets/minecraft/textures/blocks/comparator_off", Texture.comparatorOff),
        new SimpleTexture("textures/blocks/comparator", Texture.comparatorOff)));
    ALL_TEXTURES.put("comparator_on", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/comparator_on", Texture.comparatorOn),
        new SimpleTexture("assets/minecraft/textures/blocks/comparator_on", Texture.comparatorOn),
        new SimpleTexture("textures/blocks/comparator_lit", Texture.comparatorOn)));
    ALL_TEXTURES.put("hopper_outside", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hopper_outside", Texture.hopperOutside),
        new SimpleTexture("assets/minecraft/textures/blocks/hopper_outside", Texture.hopperOutside),
        new SimpleTexture("textures/blocks/hopper", Texture.hopperOutside)));
    ALL_TEXTURES.put("hopper_inside", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hopper_inside", Texture.hopperInside),
        new SimpleTexture("assets/minecraft/textures/blocks/hopper_inside", Texture.hopperInside),
        new SimpleTexture("textures/blocks/hopper_inside", Texture.hopperInside)));
    ALL_TEXTURES.put("hopper_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hopper_top", Texture.hopperTop),
        new SimpleTexture("assets/minecraft/textures/blocks/hopper_top", Texture.hopperTop),
        new SimpleTexture("textures/blocks/hopper_top", Texture.hopperTop)));

    // MC 1.6
    ALL_TEXTURES.put("hay_block_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hay_block_side", Texture.hayBlockSide),
        new SimpleTexture("assets/minecraft/textures/blocks/hay_block_side", Texture.hayBlockSide)));
    ALL_TEXTURES.put("hay_block_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/hay_block_top", Texture.hayBlockTop),
        new SimpleTexture("assets/minecraft/textures/blocks/hay_block_top", Texture.hayBlockTop)));
    ALL_TEXTURES.put("terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/terracotta", Texture.hardenedClay),
        new SimpleTexture("assets/minecraft/textures/blocks/terracotta", Texture.hardenedClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay", Texture.hardenedClay)));
    ALL_TEXTURES.put("coal_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/coal_block", Texture.coalBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/coal_block", Texture.coalBlock)));
    ALL_TEXTURES.put("black_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_terracotta",
            Texture.blackClay),
        new SimpleTexture("assets/minecraft/textures/blocks/black_terracotta",
            Texture.blackClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_black",
            Texture.blackClay)));
    ALL_TEXTURES.put("blue_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_terracotta",
            Texture.blueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_terracotta",
            Texture.blueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_blue",
            Texture.blueClay)));
    ALL_TEXTURES.put("brown_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_terracotta",
            Texture.brownClay),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_terracotta",
            Texture.brownClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_brown",
            Texture.brownClay)));
    ALL_TEXTURES.put("cyan_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_terracotta",
            Texture.cyanClay),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_terracotta",
            Texture.cyanClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_cyan",
            Texture.cyanClay)));
    ALL_TEXTURES.put("gray_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_terracotta",
            Texture.grayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_terracotta",
            Texture.grayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_gray",
            Texture.grayClay)));
    ALL_TEXTURES.put("green_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_terracotta",
            Texture.greenClay),
        new SimpleTexture("assets/minecraft/textures/blocks/green_terracotta",
            Texture.greenClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_green",
            Texture.greenClay)));
    ALL_TEXTURES.put("light_blue_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_terracotta",
            Texture.lightBlueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_terracotta",
            Texture.lightBlueClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_light_blue",
            Texture.lightBlueClay)));
    ALL_TEXTURES.put("lime_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_terracotta",
            Texture.limeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_terracotta",
            Texture.limeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_lime",
            Texture.limeClay)));
    ALL_TEXTURES.put("magenta_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_terracotta",
            Texture.magentaClay),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_terracotta",
            Texture.magentaClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_magenta",
            Texture.magentaClay)));
    ALL_TEXTURES.put("orange_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_terracotta",
            Texture.orangeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_terracotta",
            Texture.orangeClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_orange",
            Texture.orangeClay)));
    ALL_TEXTURES.put("pink_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_terracotta",
            Texture.pinkClay),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_terracotta",
            Texture.pinkClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_pink",
            Texture.pinkClay)));
    ALL_TEXTURES.put("purple_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_terracotta",
            Texture.purpleClay),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_terracotta",
            Texture.purpleClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_purple",
            Texture.purpleClay)));
    ALL_TEXTURES.put("red_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_terracotta",
            Texture.redClay),
        new SimpleTexture("assets/minecraft/textures/blocks/red_terracotta",
            Texture.redClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_red",
            Texture.redClay)));
    ALL_TEXTURES.put("silver_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_terracotta",
            Texture.lightGrayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_terracotta",
            Texture.lightGrayClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_silver",
            Texture.lightGrayClay)));
    ALL_TEXTURES.put("white_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_terracotta",
            Texture.whiteClay),
        new SimpleTexture("assets/minecraft/textures/blocks/white_terracotta",
            Texture.whiteClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_white",
            Texture.whiteClay)));
    ALL_TEXTURES.put("yellow_terracotta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_terracotta",
            Texture.yellowClay),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_terracotta",
            Texture.yellowClay),
        new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_yellow",
            Texture.yellowClay)));

    // Birch Leaf [MC ?]
    ALL_TEXTURES.put("birch_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_leaves", Texture.birchLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_leaves", Texture.birchLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_birch", Texture.birchLeaves),
        new IndexedTexture(0xC4, Texture.birchLeaves)));

    // [MC 1.7.2] Stained glass blocks
    ALL_TEXTURES.put("glass_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_stained_glass",
            Texture.blackGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/black_stained_glass",
            Texture.blackGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_black", Texture.blackGlass)));
    ALL_TEXTURES.put("glass_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_stained_glass", Texture.blueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_stained_glass", Texture.blueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_blue", Texture.blueGlass)));
    ALL_TEXTURES.put("glass_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_stained_glass",
            Texture.brownGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_stained_glass",
            Texture.brownGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_brown", Texture.brownGlass)));
    ALL_TEXTURES.put("glass_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_stained_glass", Texture.cyanGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_stained_glass", Texture.cyanGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_cyan", Texture.cyanGlass)));
    ALL_TEXTURES.put("glass_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_stained_glass", Texture.grayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_stained_glass", Texture.grayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_gray", Texture.grayGlass)));
    ALL_TEXTURES.put("glass_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_stained_glass",
            Texture.greenGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/green_stained_glass",
            Texture.greenGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_green", Texture.greenGlass)));
    ALL_TEXTURES.put("glass_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_stained_glass",
            Texture.lightBlueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_stained_glass",
            Texture.lightBlueGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_light_blue",
            Texture.lightBlueGlass)));
    ALL_TEXTURES.put("glass_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_stained_glass", Texture.limeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_stained_glass", Texture.limeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_lime", Texture.limeGlass)));
    ALL_TEXTURES.put("glass_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_stained_glass",
            Texture.magentaGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_stained_glass",
            Texture.magentaGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_magenta", Texture.magentaGlass)));
    ALL_TEXTURES.put("glass_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_stained_glass",
            Texture.orangeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_stained_glass",
            Texture.orangeGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_orange", Texture.orangeGlass)));
    ALL_TEXTURES.put("glass_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_stained_glass", Texture.pinkGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_stained_glass", Texture.pinkGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pink", Texture.pinkGlass)));
    ALL_TEXTURES.put("glass_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_stained_glass",
            Texture.purpleGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_stained_glass",
            Texture.purpleGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_purple", Texture.purpleGlass)));
    ALL_TEXTURES.put("glass_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_stained_glass", Texture.redGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/red_stained_glass", Texture.redGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_red", Texture.redGlass)));
    ALL_TEXTURES.put("glass_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_stained_glass",
            Texture.lightGrayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_stained_glass",
            Texture.lightGrayGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_silver", Texture.lightGrayGlass)));
    ALL_TEXTURES.put("glass_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_stained_glass",
            Texture.whiteGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/white_stained_glass",
            Texture.whiteGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_white", Texture.whiteGlass)));
    ALL_TEXTURES.put("glass_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_stained_glass",
            Texture.yellowGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_stained_glass",
            Texture.yellowGlass),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_yellow", Texture.yellowGlass)));

    // [MC 1.7.2] Stained glass panes
    ALL_TEXTURES.put("glass_pane_top_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_stained_glass_pane_top",
            Texture.blackGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/black_stained_glass_pane_top",
            Texture.blackGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_black",
            Texture.blackGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_stained_glass_pane_top",
            Texture.blueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_stained_glass_pane_top",
            Texture.blueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_blue",
            Texture.blueGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_stained_glass_pane_top",
            Texture.brownGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_stained_glass_pane_top",
            Texture.brownGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_brown",
            Texture.brownGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_stained_glass_pane_top",
            Texture.cyanGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_stained_glass_pane_top",
            Texture.cyanGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_cyan",
            Texture.cyanGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_stained_glass_pane_top",
            Texture.grayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_stained_glass_pane_top",
            Texture.grayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_gray",
            Texture.grayGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_stained_glass_pane_top",
            Texture.greenGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/green_stained_glass_pane_top",
            Texture.greenGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_green",
            Texture.greenGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_stained_glass_pane_top",
            Texture.lightBlueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_stained_glass_pane_top",
            Texture.lightBlueGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_light_blue",
            Texture.lightBlueGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_stained_glass_pane_top",
            Texture.limeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_stained_glass_pane_top",
            Texture.limeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_lime",
            Texture.limeGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_stained_glass_pane_top",
            Texture.magentaGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_stained_glass_pane_top",
            Texture.magentaGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_magenta",
            Texture.magentaGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_stained_glass_pane_top",
            Texture.orangeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_stained_glass_pane_top",
            Texture.orangeGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_orange",
            Texture.orangeGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_stained_glass_pane_top",
            Texture.pinkGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_stained_glass_pane_top",
            Texture.pinkGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_pink",
            Texture.pinkGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_stained_glass_pane_top",
            Texture.purpleGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_stained_glass_pane_top",
            Texture.purpleGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_purple",
            Texture.purpleGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_stained_glass_pane_top",
            Texture.redGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/red_stained_glass_pane_top",
            Texture.redGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_red",
            Texture.redGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_stained_glass_pane_top",
            Texture.lightGrayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_stained_glass_pane_top",
            Texture.lightGrayGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_silver",
            Texture.lightGrayGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_stained_glass_pane_top",
            Texture.whiteGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/white_stained_glass_pane_top",
            Texture.whiteGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_white",
            Texture.whiteGlassPaneSide)));
    ALL_TEXTURES.put("glass_pane_top_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_stained_glass_pane_top",
            Texture.yellowGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_stained_glass_pane_top",
            Texture.yellowGlassPaneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_yellow",
            Texture.yellowGlassPaneSide)));

    // [MC 1.7.2] Top/bottom log textures
    ALL_TEXTURES.put("spruce_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_log_top", Texture.spruceWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_log_top", Texture.spruceWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_spruce_top", Texture.spruceWoodTop),
        new IndexedTexture(0x15, Texture.spruceWoodTop)));
    ALL_TEXTURES.put("birch_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_log_top", Texture.birchWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_log_top", Texture.birchWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_birch_top", Texture.birchWoodTop),
        new IndexedTexture(0x15, Texture.spruceWoodTop)));
    ALL_TEXTURES.put("jungle_top_log_", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_log_top", Texture.jungleTreeTop),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_log_top", Texture.jungleTreeTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_jungle_top", Texture.jungleTreeTop),
        new IndexedTexture(0x15, Texture.jungleTreeTop)));

    // [MC 1.7.2] Podzol
    ALL_TEXTURES.put("podzol_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/podzol_side", Texture.podzolSide),
        new SimpleTexture("assets/minecraft/textures/blocks/podzol_side", Texture.podzolSide),
        new SimpleTexture("assets/minecraft/textures/blocks/dirt_podzol_side", Texture.podzolSide)));
    ALL_TEXTURES.put("podzol_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/podzol_top", Texture.podzolTop),
        new SimpleTexture("assets/minecraft/textures/blocks/podzol_top", Texture.podzolTop),
        new SimpleTexture("assets/minecraft/textures/blocks/dirt_podzol_top", Texture.podzolTop)));

    // [MC 1.7.2] Acacia, Dark Oak
    ALL_TEXTURES.put("acacia_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_log", Texture.acaciaWood),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_log", Texture.acaciaWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_acacia", Texture.acaciaWood)));
    ALL_TEXTURES.put("acacia_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_log_top",
            Texture.acaciaWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_log_top",
            Texture.acaciaWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_acacia_top",
            Texture.acaciaWoodTop)));
    ALL_TEXTURES.put("acacia_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_leaves", Texture.acaciaLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_leaves", Texture.acaciaLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_acacia", Texture.acaciaLeaves)));
    ALL_TEXTURES.put("acacia_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_sapling",
            Texture.acaciaSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_sapling",
            Texture.acaciaSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_acacia",
            Texture.acaciaSapling)));
    ALL_TEXTURES.put("acacia_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_planks", Texture.acaciaPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_planks", Texture.acaciaPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_acacia", Texture.acaciaPlanks)));

    ALL_TEXTURES.put("dark_oak_log", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_log", Texture.darkOakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_log", Texture.darkOakWood),
        new SimpleTexture("assets/minecraft/textures/blocks/log_big_oak", Texture.darkOakWood)));
    ALL_TEXTURES.put("dark_oak_log_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_log_top",
            Texture.darkOakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_log_top",
            Texture.darkOakWoodTop),
        new SimpleTexture("assets/minecraft/textures/blocks/log_big_oak_top",
            Texture.darkOakWoodTop)));
    ALL_TEXTURES.put("dark_oak_leaves", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_leaves",
            Texture.darkOakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_leaves",
            Texture.darkOakLeaves),
        new SimpleTexture("assets/minecraft/textures/blocks/leaves_big_oak",
            Texture.darkOakLeaves)));
    ALL_TEXTURES.put("dark_oak_sapling", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_sapling",
            Texture.darkOakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_sapling",
            Texture.darkOakSapling),
        new SimpleTexture("assets/minecraft/textures/blocks/sapling_roofed_oak",
            Texture.darkOakSapling)));
    ALL_TEXTURES.put("dark_oak_planks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_planks",
            Texture.darkOakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_planks",
            Texture.darkOakPlanks),
        new SimpleTexture("assets/minecraft/textures/blocks/planks_big_oak",
            Texture.darkOakPlanks)));

    // [MC 1.7.2] Packed Ice
    ALL_TEXTURES.put("packed_ice", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/packed_ice", Texture.packedIce),
        new SimpleTexture("assets/minecraft/textures/blocks/packed_ice", Texture.packedIce),
        new SimpleTexture("assets/minecraft/textures/blocks/ice_packed", Texture.packedIce)));

    // [MC 1.7.2] Red Sand
    ALL_TEXTURES.put("red_sand", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sand", Texture.redSand),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sand", Texture.redSand)));

    // [MC 1.7.2] Flowers
    ALL_TEXTURES.put("allium", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/allium", Texture.allium),
        new SimpleTexture("assets/minecraft/textures/blocks/allium", Texture.allium),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_allium", Texture.allium)));
    ALL_TEXTURES.put("blue_orchid", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_orchid",
            Texture.blueOrchid),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_orchid",
            Texture.blueOrchid),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_blue_orchid",
            Texture.blueOrchid)));
    ALL_TEXTURES.put("houstonia", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/azure_bluet",
            Texture.azureBluet),
        new SimpleTexture("assets/minecraft/textures/blocks/azure_bluet",
            Texture.azureBluet),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_houstonia",
            Texture.azureBluet)));
    ALL_TEXTURES.put("oxeye_daisy",new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/oxeye_daisy",
            Texture.oxeyeDaisy),
        new SimpleTexture("assets/minecraft/textures/blocks/oxeye_daisy",
            Texture.oxeyeDaisy),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_oxeye_daisy",
            Texture.oxeyeDaisy)));
    ALL_TEXTURES.put("red_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_tulip",
            Texture.redTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/red_tulip",
            Texture.redTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_red",
            Texture.redTulip)));
    ALL_TEXTURES.put("orange_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_tulip",
            Texture.orangeTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_tulip",
            Texture.orangeTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_orange",
            Texture.orangeTulip)));
    ALL_TEXTURES.put("white_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_tulip",
            Texture.whiteTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/white_tulip",
            Texture.whiteTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_white",
            Texture.whiteTulip)));
    ALL_TEXTURES.put("pink_tulip", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_tulip",
            Texture.pinkTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_tulip",
            Texture.pinkTulip),
        new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_pink",
            Texture.pinkTulip)));

    ALL_TEXTURES.put("large_fern_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/large_fern_bottom",
            Texture.largeFernBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/large_fern_bottom",
            Texture.largeFernBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_fern_bottom",
            Texture.largeFernBottom)));
    ALL_TEXTURES.put("large_fern_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/large_fern_top",
            Texture.largeFernTop),
        new SimpleTexture("assets/minecraft/textures/blocks/large_fern_top",
            Texture.largeFernTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_fern_top",
            Texture.largeFernTop)));

    ALL_TEXTURES.put("tall_grass_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tall_grass_bottom",
            Texture.doubleTallGrassBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/tall_grass_bottom",
            Texture.doubleTallGrassBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_grass_bottom",
            Texture.doubleTallGrassBottom)));
    ALL_TEXTURES.put("tall_grass_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/tall_grass_top",
            Texture.doubleTallGrassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/tall_grass_top",
            Texture.doubleTallGrassTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_grass_top",
            Texture.doubleTallGrassTop)));

    ALL_TEXTURES.put("peony_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/peony_bottom",
            Texture.peonyBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/peony_bottom",
            Texture.peonyBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_paeonia_bottom",
            Texture.peonyBottom)));
    ALL_TEXTURES.put("peony_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/peony_top",
            Texture.peonyTop),
        new SimpleTexture("assets/minecraft/textures/blocks/peony_top",
            Texture.peonyTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_paeonia_top",
            Texture.peonyTop)));

    ALL_TEXTURES.put("rose_bush_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rose_bush_bottom",
            Texture.roseBushBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/rose_bush_bottom",
            Texture.roseBushBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_rose_bottom",
            Texture.roseBushBottom)));
    ALL_TEXTURES.put("rose_bush_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/rose_bush_top",
            Texture.roseBushTop),
        new SimpleTexture("assets/minecraft/textures/blocks/rose_bush_top",
            Texture.roseBushTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_rose_top",
            Texture.roseBushTop)));

    ALL_TEXTURES.put("sunflower_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_bottom",
            Texture.sunflowerBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_bottom",
            Texture.sunflowerBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_bottom",
            Texture.sunflowerBottom)));
    ALL_TEXTURES.put("sunflower_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_top",
            Texture.sunflowerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_top",
            Texture.sunflowerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_top",
            Texture.sunflowerTop)));
    ALL_TEXTURES.put("sunflower_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_front",
            Texture.sunflowerFront),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_front",
            Texture.sunflowerFront),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_front",
            Texture.sunflowerFront)));
    ALL_TEXTURES.put("sunflower_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sunflower_back",
            Texture.sunflowerBack),
        new SimpleTexture("assets/minecraft/textures/blocks/sunflower_back",
            Texture.sunflowerBack),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_back",
            Texture.sunflowerBack)));

    ALL_TEXTURES.put("lilac_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lilac_bottom",
            Texture.lilacBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/lilac_bottom",
            Texture.lilacBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_syringa_bottom",
            Texture.lilacBottom)));
    ALL_TEXTURES.put("lilac_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lilac_top",
            Texture.lilacTop),
        new SimpleTexture("assets/minecraft/textures/blocks/lilac_top",
            Texture.lilacTop),
        new SimpleTexture("assets/minecraft/textures/blocks/double_plant_syringa_top",
            Texture.lilacTop)));

    // [MC 1.8] New Blocks
    ALL_TEXTURES.put("diorite", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/diorite", Texture.diorite),
        new SimpleTexture("assets/minecraft/textures/blocks/diorite", Texture.diorite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_diorite", Texture.diorite)));
    ALL_TEXTURES.put("diorite_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/polished_diorite",
            Texture.smoothDiorite),
        new SimpleTexture("assets/minecraft/textures/blocks/polished_diorite",
            Texture.smoothDiorite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_diorite_smooth",
            Texture.smoothDiorite)));
    ALL_TEXTURES.put("granite", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/granite", Texture.granite),
        new SimpleTexture("assets/minecraft/textures/blocks/granite", Texture.granite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_granite", Texture.granite)));
    ALL_TEXTURES.put("granite_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/polished_granite",
            Texture.smoothGranite),
        new SimpleTexture("assets/minecraft/textures/blocks/polished_granite",
            Texture.smoothGranite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_granite_smooth",
            Texture.smoothGranite)));
    ALL_TEXTURES.put("andesite", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/andesite", Texture.andesite),
        new SimpleTexture("assets/minecraft/textures/blocks/andesite", Texture.andesite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_andesite", Texture.andesite)));
    ALL_TEXTURES.put("andesite_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/polished_andesite",
            Texture.smoothAndesite),
        new SimpleTexture("assets/minecraft/textures/blocks/polished_andesite",
            Texture.smoothAndesite),
        new SimpleTexture("assets/minecraft/textures/blocks/stone_andesite_smooth",
            Texture.smoothAndesite)));
    ALL_TEXTURES.put("coarse_dirt", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/coarse_dirt", Texture.coarseDirt),
        new SimpleTexture("assets/minecraft/textures/blocks/coarse_dirt", Texture.coarseDirt)));
    ALL_TEXTURES.put("prismarine", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/prismarine",
            Texture.prismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine",
            Texture.prismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine_rough",
            Texture.prismarine)));
    ALL_TEXTURES.put("prismarine_bricks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/prismarine_bricks",
            Texture.prismarineBricks),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine_bricks",
            Texture.prismarineBricks)));
    ALL_TEXTURES.put("dark_prismarine", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_prismarine",
            Texture.darkPrismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_prismarine",
            Texture.darkPrismarine),
        new SimpleTexture("assets/minecraft/textures/blocks/prismarine_dark",
            Texture.darkPrismarine)));
    ALL_TEXTURES.put("sea_lantern", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/sea_lantern", Texture.seaLantern),
        new SimpleTexture("assets/minecraft/textures/blocks/sea_lantern", Texture.seaLantern)));
    ALL_TEXTURES.put("sponge_wet", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/wet_sponge", Texture.wetSponge),
        new SimpleTexture("assets/minecraft/textures/blocks/wet_sponge", Texture.wetSponge),
        new SimpleTexture("assets/minecraft/textures/blocks/sponge_wet", Texture.wetSponge)));
    ALL_TEXTURES.put("iron_trapdoor", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/iron_trapdoor", Texture.ironTrapdoor),
        new SimpleTexture("assets/minecraft/textures/blocks/iron_trapdoor", Texture.ironTrapdoor)));
    ALL_TEXTURES.put("slime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/slime_block", Texture.slime),
        new SimpleTexture("assets/minecraft/textures/blocks/slime_block", Texture.slime),
        new SimpleTexture("assets/minecraft/textures/blocks/slime", Texture.slime)));
    ALL_TEXTURES.put("red_sandstone_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sandstone_top",
            Texture.redSandstoneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_top",
            Texture.redSandstoneTop)));
    ALL_TEXTURES.put("red_sandstone_bottom", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sandstone_bottom",
            Texture.redSandstoneBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_bottom",
            Texture.redSandstoneBottom)));
    ALL_TEXTURES.put("red_sandstone", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_sandstone",
            Texture.redSandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone",
            Texture.redSandstoneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_normal",
            Texture.redSandstoneSide)));
    ALL_TEXTURES.put("red_sandstone_carved", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chiseled_red_sandstone",
            Texture.redSandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/chiseled_red_sandstone",
            Texture.redSandstoneDecorated),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_carved",
            Texture.redSandstoneDecorated)));
    ALL_TEXTURES.put("red_sandstone_smooth", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cut_red_sandstone",
            Texture.redSandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/cut_red_sandstone",
            Texture.redSandstoneCut),
        new SimpleTexture("assets/minecraft/textures/blocks/red_sandstone_smooth",
            Texture.redSandstoneCut)));

    ALL_TEXTURES.put("spruce_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_door_top",
            Texture.spruceDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_door_upper",
            Texture.spruceDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_spruce_upper",
            Texture.spruceDoorTop)));
    ALL_TEXTURES.put("spruce_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/spruce_door_bottom",
            Texture.spruceDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/spruce_door_lower",
            Texture.spruceDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_spruce_lower",
            Texture.spruceDoorBottom)));

    ALL_TEXTURES.put("birch_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_door_top",
            Texture.birchDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_door_upper",
            Texture.birchDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_birch_upper",
            Texture.birchDoorTop)));
    ALL_TEXTURES.put("birch_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/birch_door_bottom",
            Texture.birchDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/birch_door_lower",
            Texture.birchDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_birch_lower",
            Texture.birchDoorBottom)));

    ALL_TEXTURES.put("jungle_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_door_top",
            Texture.jungleDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_door_upper",
            Texture.jungleDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_jungle_upper",
            Texture.jungleDoorTop)));
    ALL_TEXTURES.put("jungle_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/jungle_door_bottom",
            Texture.jungleDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/jungle_door_lower",
            Texture.jungleDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_jungle_lower",
            Texture.jungleDoorBottom)));

    ALL_TEXTURES.put("acacia_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_door_top",
            Texture.acaciaDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_door_upper",
            Texture.acaciaDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_acacia_upper",
            Texture.acaciaDoorTop)));
    ALL_TEXTURES.put("acacia_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/acacia_door_bottom",
            Texture.acaciaDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/acacia_door_lower",
            Texture.acaciaDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_acacia_lower",
            Texture.acaciaDoorBottom)));

    ALL_TEXTURES.put("dark_oak_door_upper", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_door_top",
            Texture.darkOakDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_door_upper",
            Texture.darkOakDoorTop),
        new SimpleTexture("assets/minecraft/textures/blocks/door_dark_oak_upper",
            Texture.darkOakDoorTop)));
    ALL_TEXTURES.put("dark_oak_door_lower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_door_bottom",
            Texture.darkOakDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/dark_oak_door_lower",
            Texture.darkOakDoorBottom),
        new SimpleTexture("assets/minecraft/textures/blocks/door_dark_oak_lower",
            Texture.darkOakDoorBottom)));

    // Minecraft 1.9 blocks.
    ALL_TEXTURES.put("grass_path_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dirt_path_side", Texture.grassPathSide), // 20w45a
        new SimpleTexture("assets/minecraft/textures/block/grass_path_side", Texture.grassPathSide),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_path_side", Texture.grassPathSide)));
    ALL_TEXTURES.put("grass_path_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/dirt_path_top", Texture.grassPathTop), // 20w45a
        new SimpleTexture("assets/minecraft/textures/block/grass_path_top", Texture.grassPathTop),
        new SimpleTexture("assets/minecraft/textures/blocks/grass_path_top", Texture.grassPathTop)));
    ALL_TEXTURES.put("end_bricks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_stone_bricks", Texture.endBricks),
        new SimpleTexture("assets/minecraft/textures/blocks/end_stone_bricks", Texture.endBricks),
        new SimpleTexture("assets/minecraft/textures/blocks/end_bricks", Texture.endBricks)));
    ALL_TEXTURES.put("purpur_block", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purpur_block", Texture.purpurBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/purpur_block", Texture.purpurBlock)));
    ALL_TEXTURES.put("purpur_pillar", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purpur_pillar",
            Texture.purpurPillarSide),
        new SimpleTexture("assets/minecraft/textures/blocks/purpur_pillar",
            Texture.purpurPillarSide)));
    ALL_TEXTURES.put("purpur_pillar_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purpur_pillar_top",
            Texture.purpurPillarTop),
        new SimpleTexture("assets/minecraft/textures/blocks/purpur_pillar_top",
            Texture.purpurPillarTop)));
    ALL_TEXTURES.put("chorus_flower", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chorus_flower", Texture.chorusFlower),
        new SimpleTexture("assets/minecraft/textures/blocks/chorus_flower", Texture.chorusFlower)));
    ALL_TEXTURES.put("chorus_flower_dead", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chorus_flower_dead",
            Texture.chorusFlowerDead),
        new SimpleTexture("assets/minecraft/textures/blocks/chorus_flower_dead",
            Texture.chorusFlowerDead)));
    ALL_TEXTURES.put("chorus_plant", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/chorus_plant", Texture.chorusPlant),
        new SimpleTexture("assets/minecraft/textures/blocks/chorus_plant", Texture.chorusPlant)));
    ALL_TEXTURES.put("end_rod", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/end_rod", Texture.endRod),
        new SimpleTexture("assets/minecraft/textures/blocks/end_rod", Texture.endRod)));

    ALL_TEXTURES.put("frosted_ice_0",
        new SimpleTexture("assets/minecraft/textures/block/frosted_ice_0", Texture.frostedIce0));
    ALL_TEXTURES.put("frosted_ice_1",
        new SimpleTexture("assets/minecraft/textures/block/frosted_ice_1", Texture.frostedIce1));
    ALL_TEXTURES.put("frosted_ice_2",
        new SimpleTexture("assets/minecraft/textures/block/frosted_ice_2", Texture.frostedIce2));
    ALL_TEXTURES.put("frosted_ice_3",
        new SimpleTexture("assets/minecraft/textures/block/frosted_ice_3", Texture.frostedIce3));

    ALL_TEXTURES.put("fire_layer_0", new AlternateTextures(
        new AnimatedTextureLoader("assets/minecraft/textures/block/fire_0", Texture.fireLayer0),
        new AnimatedTextureLoader("assets/minecraft/textures/blocks/fire_layer_0",
            Texture.fireLayer0)));
    ALL_TEXTURES.put("fire_layer_1", new AlternateTextures(
        new AnimatedTextureLoader("assets/minecraft/textures/block/fire_1", Texture.fireLayer1),
        new AnimatedTextureLoader("assets/minecraft/textures/blocks/fire_layer_1",
            Texture.fireLayer1)));

    ALL_TEXTURES.put("paintings_zetterstrand", new AlternateTextures(
        new PaintingTextureAdapter(),
        new AllTextures(
            new PaintingTexture("assets/minecraft/textures/painting/alban", Texture.paintingAlban),
            new PaintingTexture("assets/minecraft/textures/painting/aztec2", Texture.paintingAztec2),
            new PaintingTexture("assets/minecraft/textures/painting/aztec", Texture.paintingAztec),
            new PaintingTexture("assets/minecraft/textures/painting/back", Texture.paintingBack),
            new PaintingTexture("assets/minecraft/textures/painting/bomb", Texture.paintingBomb),
            new PaintingTexture("assets/minecraft/textures/painting/burning_skull", Texture.paintingBurningSkull),
            new PaintingTexture("assets/minecraft/textures/painting/bust", Texture.paintingBust),
            new PaintingTexture("assets/minecraft/textures/painting/courbet", Texture.paintingCourbet),
            new PaintingTexture("assets/minecraft/textures/painting/creebet", Texture.paintingCreebet),
            new PaintingTexture("assets/minecraft/textures/painting/donkey_kong", Texture.paintingDonkeyKong),
            new PaintingTexture("assets/minecraft/textures/painting/fighters", Texture.paintingFighters),
            new PaintingTexture("assets/minecraft/textures/painting/graham", Texture.paintingGraham),
            new PaintingTexture("assets/minecraft/textures/painting/kebab", Texture.paintingKebab),
            new PaintingTexture("assets/minecraft/textures/painting/match", Texture.paintingMatch),
            new PaintingTexture("assets/minecraft/textures/painting/pigscene", Texture.paintingPigscene),
            new PaintingTexture("assets/minecraft/textures/painting/plant", Texture.paintingPlant),
            new PaintingTexture("assets/minecraft/textures/painting/pointer", Texture.paintingPointer),
            new PaintingTexture("assets/minecraft/textures/painting/pool", Texture.paintingPool),
            new PaintingTexture("assets/minecraft/textures/painting/sea", Texture.paintingSea),
            new PaintingTexture("assets/minecraft/textures/painting/skeleton", Texture.paintingSkeleton),
            new PaintingTexture("assets/minecraft/textures/painting/skull_and_roses", Texture.paintingSkullAndRoses),
            new PaintingTexture("assets/minecraft/textures/painting/stage", Texture.paintingStage),
            new PaintingTexture("assets/minecraft/textures/painting/sunset", Texture.paintingSunset),
            new PaintingTexture("assets/minecraft/textures/painting/void", Texture.paintingVoid),
            new PaintingTexture("assets/minecraft/textures/painting/wanderer", Texture.paintingWanderer),
            new PaintingTexture("assets/minecraft/textures/painting/wasteland", Texture.paintingWasteland),
            new PaintingTexture("assets/minecraft/textures/painting/wither", Texture.paintingWither),
            new PaintingBackTexture("assets/minecraft/textures/painting/back", Texture.paintingBack)
        )));
    ALL_TEXTURES.put("font_default", new AlternateTextures(
        new JsonFontTextureLoader("assets/minecraft/font/default.json"), // MC 1.13
        new AsciiFontTextureLoader("assets/minecraft/textures/font/ascii"))); // MC 1.6

    ALL_TEXTURES.put("alex",
        new PlayerTextureLoader("assets/minecraft/textures/entity/alex", Texture.alex, PlayerModel.ALEX));
    ALL_TEXTURES.put("steve",
        new PlayerTextureLoader("assets/minecraft/textures/entity/steve", Texture.steve, PlayerModel.STEVE));
    ALL_TEXTURES.put("creeper",
        new EntityTextureLoader("assets/minecraft/textures/entity/creeper/creeper",
            Texture.creeper));
    ALL_TEXTURES.put("zombie",
        new EntityTextureLoader("assets/minecraft/textures/entity/zombie/zombie", Texture.zombie));
    ALL_TEXTURES.put("skeleton",
        new EntityTextureLoader("assets/minecraft/textures/entity/skeleton/skeleton",
            Texture.skeleton));
    ALL_TEXTURES.put("wither",
        new EntityTextureLoader("assets/minecraft/textures/entity/wither/wither", Texture.wither));
    ALL_TEXTURES.put("dragon",
        new EntityTextureLoader("assets/minecraft/textures/entity/enderdragon/dragon", Texture.dragon));
    ALL_TEXTURES.put("book",
        new EntityTextureLoader("assets/minecraft/textures/entity/enchanting_table_book", Texture.book));

    // Minecraft 1.10 blocks.
    ALL_TEXTURES.put("boneSide", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bone_block_side", Texture.boneSide),
        new SimpleTexture("assets/minecraft/textures/blocks/bone_block_side", Texture.boneSide)));
    ALL_TEXTURES.put("boneTop", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/bone_block_top", Texture.boneTop),
        new SimpleTexture("assets/minecraft/textures/blocks/bone_block_top", Texture.boneTop)));
    ALL_TEXTURES.put("magma", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magma", Texture.magma),
        new SimpleTexture("assets/minecraft/textures/blocks/magma", Texture.magma)));
    ALL_TEXTURES.put("netherWartBlock", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/nether_wart_block",
            Texture.netherWartBlock),
        new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_block",
            Texture.netherWartBlock)));
    ALL_TEXTURES.put("red_nether_bricks", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_nether_bricks",
            Texture.redNetherBrick),
        new SimpleTexture("assets/minecraft/textures/blocks/red_nether_brick",
            Texture.redNetherBrick)));

    // [1.11] Shulker boxes.
    ALL_TEXTURES.put("shulker",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker",
            Texture.shulker));
    ALL_TEXTURES.put("shulkerBlack",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_black",
            Texture.shulkerBlack));
    ALL_TEXTURES.put("shulkerBlue",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_blue",
            Texture.shulkerBlue));
    ALL_TEXTURES.put("shulkerBrown",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_brown",
            Texture.shulkerBrown));
    ALL_TEXTURES.put("shulkerCyan",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_cyan",
            Texture.shulkerCyan));
    ALL_TEXTURES.put("shulkerGray",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_gray",
            Texture.shulkerGray));
    ALL_TEXTURES.put("shulkerGreen",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_green",
            Texture.shulkerGreen));
    ALL_TEXTURES.put("shulkerLightBlue",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_light_blue",
            Texture.shulkerLightBlue));
    ALL_TEXTURES.put("shulkerLime",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_lime",
            Texture.shulkerLime));
    ALL_TEXTURES.put("shulkerMagenta",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_magenta",
            Texture.shulkerMagenta));
    ALL_TEXTURES.put("shulkerOrange",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_orange",
            Texture.shulkerOrange));
    ALL_TEXTURES.put("shulkerPink",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_pink",
            Texture.shulkerPink));
    ALL_TEXTURES.put("shulkerPurple",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_purple",
            Texture.shulkerPurple));
    ALL_TEXTURES.put("shulkerRed",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_red",
            Texture.shulkerRed));
    ALL_TEXTURES.put("shulkerSilver", new AlternateTextures(
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_light_gray",
            Texture.shulkerSilver),
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_silver",
            Texture.shulkerSilver)));
    ALL_TEXTURES.put("shulkerWhite",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_white",
            Texture.shulkerWhite));
    ALL_TEXTURES.put("shulkerYellow",
        new ShulkerTextureLoader(
            "assets/minecraft/textures/entity/shulker/shulker_yellow",
            Texture.shulkerYellow));

    // [1.11] Observer block.
    ALL_TEXTURES.put("observer_back", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_back",
            Texture.observerBack),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_back",
            Texture.observerBack)));
    ALL_TEXTURES.put("observer_back_on",
        new SimpleTexture("assets/minecraft/textures/block/observer_back_on",
            Texture.observerBackOn));
    ALL_TEXTURES.put("observer_front", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_front",
            Texture.observerFront),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_front",
            Texture.observerFront)));
    ALL_TEXTURES.put("observer_side", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_side",
            Texture.observerSide),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_side",
            Texture.observerSide)));
    ALL_TEXTURES.put("observer_top", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/observer_top",
            Texture.observerTop),
        new SimpleTexture("assets/minecraft/textures/blocks/observer_top",
            Texture.observerTop)));

    // Redstone textures were redone and renamed in Minecraft 1.9.
    // The redstone cross texture is now created by combining redstone_dust_dot
    // and redstone_dust_line0, and redstone_dust_line1.
    // See https://github.com/llbit/chunky/issues/359

    ALL_TEXTURES.put("redstone_dust_cross", new AlternateTextures(
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
    ALL_TEXTURES.put("redstone_dust_line", new AlternateTextures(
        new RotatedTextureLoader("assets/minecraft/textures/block/redstone_dust_line0",
            Texture.redstoneWire),
        new RotatedTextureLoader("assets/minecraft/textures/blocks/redstone_dust_line0",
            Texture.redstoneWire),
        new SimpleTexture("assets/minecraft/textures/blocks/redstone_dust_line",
            Texture.redstoneWire),
        new SimpleTexture("textures/blocks/redstoneDust_line", Texture.redstoneWire),
        new IndexedTexture(0xA5, Texture.redstoneWire)));

    // Minecraft 1.12: Glazed Terracotta:
    ALL_TEXTURES.put("glazed_terracotta_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_glazed_terracotta",
            Texture.terracottaBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/black_glazed_terracotta",
            Texture.terracottaBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_black",
            Texture.terracottaBlack)));
    ALL_TEXTURES.put("glazed_terracotta_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_glazed_terracotta",
            Texture.terracottaBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_glazed_terracotta",
            Texture.terracottaBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_blue",
            Texture.terracottaBlue)));
    ALL_TEXTURES.put("glazed_terracotta_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_glazed_terracotta",
            Texture.terracottaBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_glazed_terracotta",
            Texture.terracottaBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_brown",
            Texture.terracottaBrown)));
    ALL_TEXTURES.put("glazed_terracotta_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_glazed_terracotta",
            Texture.terracottaCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_glazed_terracotta",
            Texture.terracottaCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_cyan",
            Texture.terracottaCyan)));
    ALL_TEXTURES.put("glazed_terracotta_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_glazed_terracotta",
            Texture.terracottaGray),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_glazed_terracotta",
            Texture.terracottaGray),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_gray",
            Texture.terracottaGray)));
    ALL_TEXTURES.put("glazed_terracotta_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_glazed_terracotta",
            Texture.terracottaGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/green_glazed_terracotta",
            Texture.terracottaGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_green",
            Texture.terracottaGreen)));
    ALL_TEXTURES.put("glazed_terracotta_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_glazed_terracotta",
            Texture.terracottaLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_glazed_terracotta",
            Texture.terracottaLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_light_blue",
            Texture.terracottaLightBlue)));
    ALL_TEXTURES.put("glazed_terracotta_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_glazed_terracotta",
            Texture.terracottaLime),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_glazed_terracotta",
            Texture.terracottaLime),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_lime",
            Texture.terracottaLime)));
    ALL_TEXTURES.put("glazed_terracotta_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_glazed_terracotta",
            Texture.terracottaMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_glazed_terracotta",
            Texture.terracottaMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_magenta",
            Texture.terracottaMagenta)));
    ALL_TEXTURES.put("glazed_terracotta_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_glazed_terracotta",
            Texture.terracottaOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_glazed_terracotta",
            Texture.terracottaOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_orange",
            Texture.terracottaOrange)));
    ALL_TEXTURES.put("glazed_terracotta_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_glazed_terracotta",
            Texture.terracottaPink),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_glazed_terracotta",
            Texture.terracottaPink),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_pink",
            Texture.terracottaPink)));
    ALL_TEXTURES.put("glazed_terracotta_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_glazed_terracotta",
            Texture.terracottaPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_glazed_terracotta",
            Texture.terracottaPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_purple",
            Texture.terracottaPurple)));
    ALL_TEXTURES.put("glazed_terracotta_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_glazed_terracotta",
            Texture.terracottaRed),
        new SimpleTexture("assets/minecraft/textures/blocks/red_glazed_terracotta",
            Texture.terracottaRed),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_red",
            Texture.terracottaRed)));
    ALL_TEXTURES.put("glazed_terracotta_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_glazed_terracotta",
            Texture.terracottaSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_glazed_terracotta",
            Texture.terracottaSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_silver",
            Texture.terracottaSilver)));
    ALL_TEXTURES.put("glazed_terracotta_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_glazed_terracotta",
            Texture.terracottaWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/white_glazed_terracotta",
            Texture.terracottaWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_white",
            Texture.terracottaWhite)));
    ALL_TEXTURES.put("glazed_terracotta_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_glazed_terracotta",
            Texture.terracottaYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_glazed_terracotta",
            Texture.terracottaYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/glazed_terracotta_yellow",
            Texture.terracottaYellow)));

    // Minecraft 1.12: Concrete:
    ALL_TEXTURES.put("concrete_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_concrete",
            Texture.concreteBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/black_concrete",
            Texture.concreteBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_black",
            Texture.concreteBlack)));
    ALL_TEXTURES.put("concrete_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_concrete",
            Texture.concreteBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_concrete",
            Texture.concreteBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_blue",
            Texture.concreteBlue)));
    ALL_TEXTURES.put("concrete_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_concrete",
            Texture.concreteBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_concrete",
            Texture.concreteBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_brown",
            Texture.concreteBrown)));
    ALL_TEXTURES.put("concrete_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_concrete",
            Texture.concreteCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_concrete",
            Texture.concreteCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_cyan",
            Texture.concreteCyan)));
    ALL_TEXTURES.put("concrete_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_concrete",
            Texture.concreteGray),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_concrete",
            Texture.concreteGray),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_gray",
            Texture.concreteGray)));
    ALL_TEXTURES.put("concrete_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_concrete",
            Texture.concreteGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/green_concrete",
            Texture.concreteGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_green",
            Texture.concreteGreen)));
    ALL_TEXTURES.put("concrete_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_concrete",
            Texture.concreteLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_concrete",
            Texture.concreteLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_light_blue",
            Texture.concreteLightBlue)));
    ALL_TEXTURES.put("concrete_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_concrete",
            Texture.concreteLime),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_concrete",
            Texture.concreteLime),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_lime",
            Texture.concreteLime)));
    ALL_TEXTURES.put("concrete_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_concrete",
            Texture.concreteMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_concrete",
            Texture.concreteMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_magenta",
            Texture.concreteMagenta)));
    ALL_TEXTURES.put("concrete_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_concrete",
            Texture.concreteOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_concrete",
            Texture.concreteOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_orange",
            Texture.concreteOrange)));
    ALL_TEXTURES.put("concrete_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_concrete",
            Texture.concretePink),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_concrete",
            Texture.concretePink),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_pink",
            Texture.concretePink)));
    ALL_TEXTURES.put("concrete_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_concrete",
            Texture.concretePurple),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_concrete",
            Texture.concretePurple),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_purple",
            Texture.concretePurple)));
    ALL_TEXTURES.put("concrete_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_concrete",
            Texture.concreteRed),
        new SimpleTexture("assets/minecraft/textures/blocks/red_concrete",
            Texture.concreteRed),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_red",
            Texture.concreteRed)));
    ALL_TEXTURES.put("concrete_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_concrete",
            Texture.concreteSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_concrete",
            Texture.concreteSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_silver",
            Texture.concreteSilver)));
    ALL_TEXTURES.put("concrete_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_concrete",
            Texture.concreteWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/white_concrete",
            Texture.concreteWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_white",
            Texture.concreteWhite)));
    ALL_TEXTURES.put("concrete_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_concrete",
            Texture.concreteYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_concrete",
            Texture.concreteYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_yellow",
            Texture.concreteYellow)));

    // Concrete powder:
    ALL_TEXTURES.put("concrete_powder_black", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/black_concrete_powder",
            Texture.concretePowderBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/black_concrete_powder",
            Texture.concretePowderBlack),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_black",
            Texture.concretePowderBlack)));
    ALL_TEXTURES.put("concrete_powder_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/blue_concrete_powder",
            Texture.concretePowderBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/blue_concrete_powder",
            Texture.concretePowderBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_blue",
            Texture.concretePowderBlue)));
    ALL_TEXTURES.put("concrete_powder_brown", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/brown_concrete_powder",
            Texture.concretePowderBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/brown_concrete_powder",
            Texture.concretePowderBrown),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_brown",
            Texture.concretePowderBrown)));
    ALL_TEXTURES.put("concrete_powder_cyan", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/cyan_concrete_powder",
            Texture.concretePowderCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/cyan_concrete_powder",
            Texture.concretePowderCyan),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_cyan",
            Texture.concretePowderCyan)));
    ALL_TEXTURES.put("concrete_powder_gray", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/gray_concrete_powder",
            Texture.concretePowderGray),
        new SimpleTexture("assets/minecraft/textures/blocks/gray_concrete_powder",
            Texture.concretePowderGray),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_gray",
            Texture.concretePowderGray)));
    ALL_TEXTURES.put("concrete_powder_green", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/green_concrete_powder",
            Texture.concretePowderGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/green_concrete_powder",
            Texture.concretePowderGreen),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_green",
            Texture.concretePowderGreen)));
    ALL_TEXTURES.put("concrete_powder_light_blue", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_blue_concrete_powder",
            Texture.concretePowderLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/light_blue_concrete_powder",
            Texture.concretePowderLightBlue),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_light_blue",
            Texture.concretePowderLightBlue)));
    ALL_TEXTURES.put("concrete_powder_lime", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/lime_concrete_powder",
            Texture.concretePowderLime),
        new SimpleTexture("assets/minecraft/textures/blocks/lime_concrete_powder",
            Texture.concretePowderLime),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_lime",
            Texture.concretePowderLime)));
    ALL_TEXTURES.put("concrete_powder_magenta", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/magenta_concrete_powder",
            Texture.concretePowderMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/magenta_concrete_powder",
            Texture.concretePowderMagenta),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_magenta",
            Texture.concretePowderMagenta)));
    ALL_TEXTURES.put("concrete_powder_orange", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/orange_concrete_powder",
            Texture.concretePowderOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/orange_concrete_powder",
            Texture.concretePowderOrange),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_orange",
            Texture.concretePowderOrange)));
    ALL_TEXTURES.put("concrete_powder_pink", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/pink_concrete_powder",
            Texture.concretePowderPink),
        new SimpleTexture("assets/minecraft/textures/blocks/pink_concrete_powder",
            Texture.concretePowderPink),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_pink",
            Texture.concretePowderPink)));
    ALL_TEXTURES.put("concrete_powder_purple", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/purple_concrete_powder",
            Texture.concretePowderPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/purple_concrete_powder",
            Texture.concretePowderPurple),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_purple",
            Texture.concretePowderPurple)));
    ALL_TEXTURES.put("concrete_powder_red", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/red_concrete_powder",
            Texture.concretePowderRed),
        new SimpleTexture("assets/minecraft/textures/blocks/red_concrete_powder",
            Texture.concretePowderRed),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_red",
            Texture.concretePowderRed)));
    ALL_TEXTURES.put("concrete_powder_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/light_gray_concrete_powder",
            Texture.concretePowderSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/light_gray_concrete_powder",
            Texture.concretePowderSilver),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_silver",
            Texture.concretePowderSilver)));
    ALL_TEXTURES.put("concrete_powder_white", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/white_concrete_powder",
            Texture.concretePowderWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/white_concrete_powder",
            Texture.concretePowderWhite),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_white",
            Texture.concretePowderWhite)));
    ALL_TEXTURES.put("concrete_powder_yellow", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/yellow_concrete_powder",
            Texture.concretePowderYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/yellow_concrete_powder",
            Texture.concretePowderYellow),
        new SimpleTexture("assets/minecraft/textures/blocks/concrete_powder_yellow",
            Texture.concretePowderYellow)));

    // [1.12] Beetroots:
    ALL_TEXTURES.put("beetroots_stage_0", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage0",
            Texture.beets0),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage0",
            Texture.beets0),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_0",
            Texture.beets0)));
    ALL_TEXTURES.put("beetroots_stage_1", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage1",
            Texture.beets1),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage1",
            Texture.beets1),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_1",
            Texture.beets1)));
    ALL_TEXTURES.put("beetroots_stage_2", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage2",
            Texture.beets2),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage2",
            Texture.beets2),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_2",
            Texture.beets2)));
    ALL_TEXTURES.put("beetroots_stage_3", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/beetroots_stage3",
            Texture.beets3),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage3",
            Texture.beets3),
        new SimpleTexture("assets/minecraft/textures/blocks/beetroots_stage_3",
            Texture.beets3)));

    ALL_TEXTURES.put("bed_white",
        new SimpleTexture("assets/minecraft/textures/entity/bed/white", Texture.bedWhite));
    ALL_TEXTURES.put("bed_orange",
        new SimpleTexture("assets/minecraft/textures/entity/bed/orange", Texture.bedOrange));
    ALL_TEXTURES.put("bed_magenta",
        new SimpleTexture("assets/minecraft/textures/entity/bed/magenta", Texture.bedMagenta));
    ALL_TEXTURES.put("bed_light_blue",
        new SimpleTexture("assets/minecraft/textures/entity/bed/light_blue", Texture.bedLightBlue));
    ALL_TEXTURES.put("bed_yellow",
        new SimpleTexture("assets/minecraft/textures/entity/bed/yellow", Texture.bedYellow));
    ALL_TEXTURES.put("bed_lime",
        new SimpleTexture("assets/minecraft/textures/entity/bed/lime", Texture.bedLime));
    ALL_TEXTURES.put("bed_pink",
        new SimpleTexture("assets/minecraft/textures/entity/bed/pink", Texture.bedPink));
    ALL_TEXTURES.put("bed_gray",
        new SimpleTexture("assets/minecraft/textures/entity/bed/gray", Texture.bedGray));
    ALL_TEXTURES.put("bed_silver", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/entity/bed/light_gray", Texture.bedSilver),
        new SimpleTexture("assets/minecraft/textures/entity/bed/silver", Texture.bedSilver)));
    ALL_TEXTURES.put("bed_cyan",
        new SimpleTexture("assets/minecraft/textures/entity/bed/cyan", Texture.bedCyan));
    ALL_TEXTURES.put("bed_purple",
        new SimpleTexture("assets/minecraft/textures/entity/bed/purple", Texture.bedPurple));
    ALL_TEXTURES.put("bed_blue",
        new SimpleTexture("assets/minecraft/textures/entity/bed/blue", Texture.bedBlue));
    ALL_TEXTURES.put("bed_brown",
        new SimpleTexture("assets/minecraft/textures/entity/bed/brown", Texture.bedBrown));
    ALL_TEXTURES.put("bed_green",
        new SimpleTexture("assets/minecraft/textures/entity/bed/green", Texture.bedGreen));
    ALL_TEXTURES.put("bed_red",
        new AlternateTextures(
            new SimpleTexture("assets/minecraft/textures/entity/bed/red", Texture.bedRed),
            new BedTextureAdapter()
        ));
    ALL_TEXTURES.put("bed_black",
        new SimpleTexture("assets/minecraft/textures/entity/bed/black", Texture.bedBlack));

    ALL_TEXTURES.put("banner_base",
        new SimpleTexture("assets/minecraft/textures/entity/banner_base", Texture.bannerBase));

    ALL_TEXTURES.put("armor_stand",
        new SimpleTexture("assets/minecraft/textures/entity/armorstand/wood", Texture.armorStand));

    // Minecraft 1.13
    ALL_TEXTURES.put("stripped_oak_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_oak_log",
            Texture.strippedOakLog));
    ALL_TEXTURES.put("stripped_oak_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_oak_log_top",
            Texture.strippedOakLogTop));

    ALL_TEXTURES.put("stripped_spruce_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_spruce_log",
            Texture.strippedSpruceLog));
    ALL_TEXTURES.put("stripped_spruce_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_spruce_log_top",
            Texture.strippedSpruceLogTop));

    ALL_TEXTURES.put("stripped_birch_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_birch_log",
            Texture.strippedBirchLog));
    ALL_TEXTURES.put("stripped_birch_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_birch_log_top",
            Texture.strippedBirchLogTop));

    ALL_TEXTURES.put("stripped_jungle_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_jungle_log",
            Texture.strippedJungleLog));
    ALL_TEXTURES.put("stripped_jungle_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_jungle_log_top",
            Texture.strippedJungleLogTop));

    ALL_TEXTURES.put("stripped_acacia_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_acacia_log",
            Texture.strippedAcaciaLog));
    ALL_TEXTURES.put("stripped_acacia_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_acacia_log_top",
            Texture.strippedAcaciaLogTop));

    ALL_TEXTURES.put("stripped_dark_oak_log",
        new SimpleTexture("assets/minecraft/textures/block/stripped_dark_oak_log",
            Texture.strippedDarkOakLog));
    ALL_TEXTURES.put("stripped_dark_oak_log_top",
        new SimpleTexture("assets/minecraft/textures/block/stripped_dark_oak_log_top",
            Texture.strippedDarkOakLogTop));

    ALL_TEXTURES.put("birch_trapdoor",
        new SimpleTexture("assets/minecraft/textures/block/birch_trapdoor", Texture.birchTrapdoor));
    ALL_TEXTURES.put("spruce_trapdoor",
        new SimpleTexture("assets/minecraft/textures/block/spruce_trapdoor", Texture.spruceTrapdoor));
    ALL_TEXTURES.put("jungle_trapdoor",
        new SimpleTexture("assets/minecraft/textures/block/jungle_trapdoor", Texture.jungleTrapdoor));
    ALL_TEXTURES.put("acacia_trapdoor",
        new SimpleTexture("assets/minecraft/textures/block/acacia_trapdoor", Texture.acaciaTrapdoor));
    ALL_TEXTURES.put("dark_oak_trapdoor",
        new SimpleTexture("assets/minecraft/textures/block/dark_oak_trapdoor", Texture.darkOakTrapdoor));

    ALL_TEXTURES.put("kelp",
        new SimpleTexture("assets/minecraft/textures/block/kelp", Texture.kelp));
    ALL_TEXTURES.put("kelp_plant",
        new SimpleTexture("assets/minecraft/textures/block/kelp_plant", Texture.kelpPlant));

    ALL_TEXTURES.put("seagrass",
        new SimpleTexture("assets/minecraft/textures/block/seagrass", Texture.seagrass));
    ALL_TEXTURES.put("tall_seagrass_top",
        new SimpleTexture("assets/minecraft/textures/block/tall_seagrass_top", Texture.tallSeagrassTop));
    ALL_TEXTURES.put("tall_seagrass_bottom",
        new SimpleTexture("assets/minecraft/textures/block/tall_seagrass_bottom", Texture.tallSeagrassBottom));

    ALL_TEXTURES.put("dried_kelp_side",
        new SimpleTexture("assets/minecraft/textures/block/dried_kelp_side",
            Texture.driedKelpSide));
    ALL_TEXTURES.put("dried_kelp_top",
        new SimpleTexture("assets/minecraft/textures/block/dried_kelp_top",
            Texture.driedKelpTop));
    ALL_TEXTURES.put("dried_kelp_bottom",
        new SimpleTexture("assets/minecraft/textures/block/dried_kelp_bottom",
            Texture.driedKelpBottom));

    ALL_TEXTURES.put("tube_coral",
        new SimpleTexture("assets/minecraft/textures/block/tube_coral",
            Texture.tubeCoral));
    ALL_TEXTURES.put("brain_coral",
        new SimpleTexture("assets/minecraft/textures/block/brain_coral",
            Texture.brainCoral));
    ALL_TEXTURES.put("bubble_coral",
        new SimpleTexture("assets/minecraft/textures/block/bubble_coral",
            Texture.bubbleCoral));
    ALL_TEXTURES.put("fire_coral",
        new SimpleTexture("assets/minecraft/textures/block/fire_coral",
            Texture.fireCoral));
    ALL_TEXTURES.put("horn_coral",
        new SimpleTexture("assets/minecraft/textures/block/horn_coral",
            Texture.hornCoral));

    ALL_TEXTURES.put("tube_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/tube_coral_block",
            Texture.tubeCoralBlock));
    ALL_TEXTURES.put("brain_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/brain_coral_block",
            Texture.brainCoralBlock));
    ALL_TEXTURES.put("bubble_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/bubble_coral_block",
            Texture.bubbleCoralBlock));
    ALL_TEXTURES.put("fire_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/fire_coral_block",
            Texture.fireCoralBlock));
    ALL_TEXTURES.put("horn_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/horn_coral_block",
            Texture.hornCoralBlock));

    ALL_TEXTURES.put("dead_tube_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/dead_tube_coral_block",
            Texture.deadTubeCoralBlock));
    ALL_TEXTURES.put("dead_brain_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/dead_brain_coral_block",
            Texture.deadBrainCoralBlock));
    ALL_TEXTURES.put("dead_bubble_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/dead_bubble_coral_block",
            Texture.deadBubbleCoralBlock));
    ALL_TEXTURES.put("dead_fire_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/dead_fire_coral_block",
            Texture.deadFireCoralBlock));
    ALL_TEXTURES.put("dead_horn_coral_block",
        new SimpleTexture("assets/minecraft/textures/block/dead_horn_coral_block",
            Texture.deadHornCoralBlock));

    ALL_TEXTURES.put("tube_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/tube_coral_fan",
            Texture.tubeCoralFan));
    ALL_TEXTURES.put("brain_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/brain_coral_fan",
            Texture.brainCoralFan));
    ALL_TEXTURES.put("bubble_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/bubble_coral_fan",
            Texture.bubbleCoralFan));
    ALL_TEXTURES.put("fire_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/fire_coral_fan",
            Texture.fireCoralFan));
    ALL_TEXTURES.put("horn_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/horn_coral_fan",
            Texture.hornCoralFan));

    ALL_TEXTURES.put("dead_tube_coral",
        new SimpleTexture("assets/minecraft/textures/block/dead_tube_coral",
            Texture.deadTubeCoral));
    ALL_TEXTURES.put("dead_brain_coral",
        new SimpleTexture("assets/minecraft/textures/block/dead_brain_coral",
            Texture.deadBrainCoral));
    ALL_TEXTURES.put("dead_bubble_coral",
        new SimpleTexture("assets/minecraft/textures/block/dead_bubble_coral",
            Texture.deadBubbleCoral));
    ALL_TEXTURES.put("dead_fire_coral",
        new SimpleTexture("assets/minecraft/textures/block/dead_fire_coral",
            Texture.deadFireCoral));
    ALL_TEXTURES.put("dead_horn_coral",
        new SimpleTexture("assets/minecraft/textures/block/dead_horn_coral",
            Texture.deadHornCoral));

    ALL_TEXTURES.put("dead_tube_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/dead_tube_coral_fan",
            Texture.deadTubeCoralFan));
    ALL_TEXTURES.put("dead_brain_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/dead_brain_coral_fan",
            Texture.deadBrainCoralFan));
    ALL_TEXTURES.put("dead_bubble_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/dead_bubble_coral_fan",
            Texture.deadBubbleCoralFan));
    ALL_TEXTURES.put("dead_fire_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/dead_fire_coral_fan",
            Texture.deadFireCoralFan));
    ALL_TEXTURES.put("dead_horn_coral_fan",
        new SimpleTexture("assets/minecraft/textures/block/dead_horn_coral_fan",
            Texture.deadHornCoralFan));

    ALL_TEXTURES.put("turtle_egg",
        new SimpleTexture("assets/minecraft/textures/block/turtle_egg",
            Texture.turtleEgg));
    ALL_TEXTURES.put("turtle_egg_slightly_cracked",
        new SimpleTexture("assets/minecraft/textures/block/turtle_egg_slightly_cracked",
            Texture.turtleEggSlightlyCracked));
    ALL_TEXTURES.put("turtle_egg_very_cracked",
        new SimpleTexture("assets/minecraft/textures/block/turtle_egg_very_cracked",
            Texture.turtleEggVeryCracked));

    ALL_TEXTURES.put("blue_ice",
        new SimpleTexture("assets/minecraft/textures/block/blue_ice",
            Texture.blueIce));
    ALL_TEXTURES.put("sea_pickle",
        new SimpleTexture("assets/minecraft/textures/block/sea_pickle",
            Texture.seaPickle));
    ALL_TEXTURES.put("conduit",
        new SimpleTexture("assets/minecraft/textures/block/conduit",
            Texture.conduit));
    ALL_TEXTURES.put("structure_block",
        new SimpleTexture("assets/minecraft/textures/block/structure_block",
            Texture.structureBlock));
    ALL_TEXTURES.put("structure_block_corner",
        new SimpleTexture("assets/minecraft/textures/block/structure_block_corner",
            Texture.structureBlockCorner));
    ALL_TEXTURES.put("structure_block_data",
        new SimpleTexture("assets/minecraft/textures/block/structure_block_data",
            Texture.structureBlockData));
    ALL_TEXTURES.put("structure_block_load",
        new SimpleTexture("assets/minecraft/textures/block/structure_block_load",
            Texture.structureBlockLoad));
    ALL_TEXTURES.put("structure_block_save",
        new SimpleTexture("assets/minecraft/textures/block/structure_block_save",
            Texture.structureBlockSave));

    // Minecraft 1.14
    ALL_TEXTURES.put("barrel_top",
        new SimpleTexture("assets/minecraft/textures/block/barrel_top",
            Texture.barrelTop));
    ALL_TEXTURES.put("barrel_top_open",
        new SimpleTexture("assets/minecraft/textures/block/barrel_top_open",
            Texture.barrelOpen));
    ALL_TEXTURES.put("barrel_side",
        new SimpleTexture("assets/minecraft/textures/block/barrel_side",
            Texture.barrelSide));
    ALL_TEXTURES.put("barrel_bottom",
        new SimpleTexture("assets/minecraft/textures/block/barrel_bottom",
            Texture.barrelBottom));

    ALL_TEXTURES.put("loom_bottom",
        new SimpleTexture("assets/minecraft/textures/block/loom_bottom",
            Texture.loomBottom));
    ALL_TEXTURES.put("loom_front",
        new SimpleTexture("assets/minecraft/textures/block/loom_front",
            Texture.loomFront));
    ALL_TEXTURES.put("loom_side",
        new SimpleTexture("assets/minecraft/textures/block/loom_side",
            Texture.loomSide));
    ALL_TEXTURES.put("loom_top",
        new SimpleTexture("assets/minecraft/textures/block/loom_top",
            Texture.loomTop));

    ALL_TEXTURES.put("sign_acacia",
        new SimpleTexture("assets/minecraft/textures/entity/signs/acacia", Texture.acaciaSignPost));
    ALL_TEXTURES.put("sign_birch",
        new SimpleTexture("assets/minecraft/textures/entity/signs/birch", Texture.birchSignPost));
    ALL_TEXTURES.put("sign_dark_oak",
        new SimpleTexture("assets/minecraft/textures/entity/signs/dark_oak", Texture.darkOakSignPost));
    ALL_TEXTURES.put("sign_jungle",
        new SimpleTexture("assets/minecraft/textures/entity/signs/jungle", Texture.jungleSignPost));
    ALL_TEXTURES.put("sign_oak", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/entity/signs/oak", Texture.oakSignPost),// MC 1.14
        new SimpleTexture("assets/minecraft/textures/entity/sign", Texture.oakSignPost),// MC 1.6
        new SimpleTexture("item/sign", Texture.oakSignPost)));
    ALL_TEXTURES.put("sign_spruce",
        new SimpleTexture("assets/minecraft/textures/entity/signs/spruce", Texture.spruceSignPost));

    ALL_TEXTURES.put("cartography_table_side1",
        new SimpleTexture("assets/minecraft/textures/block/cartography_table_side1",
            Texture.cartographyTableSide1));
    ALL_TEXTURES.put("cartography_table_side2",
        new SimpleTexture("assets/minecraft/textures/block/cartography_table_side2",
            Texture.cartographyTableSide2));
    ALL_TEXTURES.put("cartography_table_side3",
        new SimpleTexture("assets/minecraft/textures/block/cartography_table_side3",
            Texture.cartographyTableSide3));
    ALL_TEXTURES.put("cartography_table_top",
        new SimpleTexture("assets/minecraft/textures/block/cartography_table_top",
            Texture.cartographyTableTop));

    ALL_TEXTURES.put("fletching_table_front",
        new SimpleTexture("assets/minecraft/textures/block/fletching_table_front",
            Texture.fletchingTableFront));
    ALL_TEXTURES.put("fletching_table_top",
        new SimpleTexture("assets/minecraft/textures/block/fletching_table_top",
            Texture.fletchingTableTop));
    ALL_TEXTURES.put("fletching_table_side",
        new SimpleTexture("assets/minecraft/textures/block/fletching_table_side",
            Texture.fletchingTableSide));

    ALL_TEXTURES.put("smithing_table_front",
        new SimpleTexture("assets/minecraft/textures/block/smithing_table_front",
            Texture.smithingTableFront));
    ALL_TEXTURES.put("smithing_table_top",
        new SimpleTexture("assets/minecraft/textures/block/smithing_table_top",
            Texture.smithingTableTop));
    ALL_TEXTURES.put("smithing_table_side",
        new SimpleTexture("assets/minecraft/textures/block/smithing_table_side",
            Texture.smithingTableSide));
    ALL_TEXTURES.put("smithing_table_bottom",
        new SimpleTexture("assets/minecraft/textures/block/smithing_table_bottom",
            Texture.smithingTableBottom));

    ALL_TEXTURES.put("blast_furnace_top",
        new SimpleTexture("assets/minecraft/textures/block/blast_furnace_top",
            Texture.blastFurnaceTop));
    ALL_TEXTURES.put("blast_furnace_side",
        new SimpleTexture("assets/minecraft/textures/block/blast_furnace_side",
            Texture.blastFurnaceSide));
    ALL_TEXTURES.put("blast_furnace_front",
        new SimpleTexture("assets/minecraft/textures/block/blast_furnace_front",
            Texture.blastFurnaceFront));
    ALL_TEXTURES.put("blast_furnace_front_on",
        new SimpleTexture("assets/minecraft/textures/block/blast_furnace_front_on",
            Texture.blastFurnaceFrontOn));

    ALL_TEXTURES.put("smoker_top",
        new SimpleTexture("assets/minecraft/textures/block/smoker_top",
            Texture.smokerTop));
    ALL_TEXTURES.put("smoker_bottom",
        new SimpleTexture("assets/minecraft/textures/block/smoker_bottom",
            Texture.smokerBottom));
    ALL_TEXTURES.put("smoker_side",
        new SimpleTexture("assets/minecraft/textures/block/smoker_side",
            Texture.smokerSide));
    ALL_TEXTURES.put("smoker_front",
        new SimpleTexture("assets/minecraft/textures/block/smoker_front",
            Texture.smokerFront));
    ALL_TEXTURES.put("smoker_front_on",
        new SimpleTexture("assets/minecraft/textures/block/smoker_front_on",
            Texture.smokerFrontOn));

    addSimpleTexture("assets/minecraft/textures/block/sweet_berry_bush_stage0", Texture.sweetBerryBushStage0);
    addSimpleTexture("assets/minecraft/textures/block/sweet_berry_bush_stage1", Texture.sweetBerryBushStage1);
    addSimpleTexture("assets/minecraft/textures/block/sweet_berry_bush_stage2", Texture.sweetBerryBushStage2);
    addSimpleTexture("assets/minecraft/textures/block/sweet_berry_bush_stage3", Texture.sweetBerryBushStage3);

    addSimpleTexture("assets/minecraft/textures/block/cornflower", Texture.cornflower);
    addSimpleTexture("assets/minecraft/textures/block/lily_of_the_valley", Texture.lilyOfTheValley);
    addSimpleTexture("assets/minecraft/textures/block/wither_rose", Texture.witherRose);
    addSimpleTexture("assets/minecraft/textures/block/bamboo_stage0", Texture.bambooSapling);

    addSimpleTexture("assets/minecraft/textures/block/lectern_base", Texture.lecternBase);
    addSimpleTexture("assets/minecraft/textures/block/lectern_front", Texture.lecternFront);
    addSimpleTexture("assets/minecraft/textures/block/lectern_sides", Texture.lecternSides);
    addSimpleTexture("assets/minecraft/textures/block/lectern_top", Texture.lecternTop);

    addSimpleTexture("assets/minecraft/textures/block/composter_top", Texture.composterTop);
    addSimpleTexture("assets/minecraft/textures/block/composter_bottom", Texture.composterBottom);
    addSimpleTexture("assets/minecraft/textures/block/composter_side", Texture.composterSide);
    addSimpleTexture("assets/minecraft/textures/block/composter_compost", Texture.composterCompost);
    addSimpleTexture("assets/minecraft/textures/block/composter_ready", Texture.composterReady);

    addSimpleTexture("assets/minecraft/textures/block/bamboo_stalk", Texture.bambooStalk);
    addSimpleTexture("assets/minecraft/textures/block/bamboo_small_leaves", Texture.bambooSmallLeaves);
    addSimpleTexture("assets/minecraft/textures/block/bamboo_large_leaves", Texture.bambooLargeLeaves);
    addSimpleTexture("assets/minecraft/textures/block/bamboo_singleleaf", Texture.bambooSingleLeaf);

    addSimpleTexture("assets/minecraft/textures/block/stonecutter_bottom", Texture.stonecutterBottom);
    addSimpleTexture("assets/minecraft/textures/block/stonecutter_top", Texture.stonecutterTop);
    addSimpleTexture("assets/minecraft/textures/block/stonecutter_side", Texture.stonecutterSide);
    addSimpleTexture("assets/minecraft/textures/block/stonecutter_saw", Texture.stonecutterSaw);

    addSimpleTexture("assets/minecraft/textures/block/grindstone_pivot", Texture.grindstonePivot);
    addSimpleTexture("assets/minecraft/textures/block/grindstone_round", Texture.grindstoneRound);
    addSimpleTexture("assets/minecraft/textures/block/grindstone_side", Texture.grindstoneSide);

    addSimpleTexture("assets/minecraft/textures/block/campfire_log", Texture.campfireLog);
    addSimpleTexture("assets/minecraft/textures/block/campfire_log_lit", Texture.campfireLogLit);
    addSimpleTexture("assets/minecraft/textures/block/campfire_fire", Texture.campfireFire);

    addSimpleTexture("assets/minecraft/textures/block/lantern", Texture.lantern);

    addSimpleTexture("assets/minecraft/textures/entity/bell/bell_body", Texture.bellBody);

    addSimpleTexture("assets/minecraft/textures/block/scaffolding_top", Texture.scaffoldingTop);
    addSimpleTexture("assets/minecraft/textures/block/scaffolding_side", Texture.scaffoldingSide);
    addSimpleTexture("assets/minecraft/textures/block/scaffolding_bottom", Texture.scaffoldingBottom);

    addSimpleTexture("assets/minecraft/textures/block/jigsaw_top", Texture.jigsawTop);
    addSimpleTexture("assets/minecraft/textures/block/jigsaw_side", Texture.jigsawSide);
    addSimpleTexture("assets/minecraft/textures/block/jigsaw_bottom", Texture.jigsawBottom);

    // Minecraft 1.15
    addSimpleTexture("assets/minecraft/textures/block/honey_block_top", Texture.honeyBlockTop);
    addSimpleTexture("assets/minecraft/textures/block/honey_block_side", Texture.honeyBlockSide);
    addSimpleTexture("assets/minecraft/textures/block/honey_block_bottom", Texture.honeyBlockBottom);

    addSimpleTexture("assets/minecraft/textures/block/beehive_end", Texture.beehiveEnd);
    addSimpleTexture("assets/minecraft/textures/block/beehive_side", Texture.beehiveSide);
    addSimpleTexture("assets/minecraft/textures/block/beehive_front", Texture.beehiveFront);
    addSimpleTexture("assets/minecraft/textures/block/beehive_front_honey", Texture.beehiveFrontHoney);

    addSimpleTexture("assets/minecraft/textures/block/bee_nest_top", Texture.beeNestTop);
    addSimpleTexture("assets/minecraft/textures/block/bee_nest_bottom", Texture.beeNestBottom);
    addSimpleTexture("assets/minecraft/textures/block/bee_nest_side", Texture.beeNestSide);
    addSimpleTexture("assets/minecraft/textures/block/bee_nest_front", Texture.beeNestFront);
    addSimpleTexture("assets/minecraft/textures/block/bee_nest_front_honey", Texture.beeNestFrontHoney);

    addSimpleTexture("assets/minecraft/textures/block/honeycomb_block", Texture.honeycombBlock);

    // Minecraft 1.16
    addSimpleTexture("assets/minecraft/textures/block/soul_soil", Texture.soulSoil);
    addSimpleTexture("assets/minecraft/textures/block/crimson_nylium", Texture.crimsonNylium);
    addSimpleTexture("assets/minecraft/textures/block/crimson_nylium_side", Texture.crimsonNyliumSide);
    addSimpleTexture("assets/minecraft/textures/block/warped_nylium", Texture.warpedNylium);
    addSimpleTexture("assets/minecraft/textures/block/warped_nylium_side", Texture.warpedNyliumSide);
    addSimpleTexture("assets/minecraft/textures/block/nether_gold_ore", Texture.netherGoldOre);
    addSimpleTexture("assets/minecraft/textures/block/target_top", Texture.targetTop);
    addSimpleTexture("assets/minecraft/textures/block/target_side", Texture.targetSide);
    addSimpleTexture("assets/minecraft/textures/block/netherite_block", Texture.netheriteBlock);
    addSimpleTexture("assets/minecraft/textures/block/shroomlight", Texture.shroomlight);
    addSimpleTexture("assets/minecraft/textures/block/warped_wart_block", Texture.warpedWartBlock);
    addSimpleTexture("assets/minecraft/textures/block/basalt_top", Texture.basaltTop);
    addSimpleTexture("assets/minecraft/textures/block/basalt_side", Texture.basaltSide);
    addSimpleTexture("assets/minecraft/textures/block/polished_basalt_top", Texture.polishedBasaltTop);
    addSimpleTexture("assets/minecraft/textures/block/polished_basalt_side", Texture.polishedBasaltSide);
    addSimpleTexture("assets/minecraft/textures/block/ancient_debris_top", Texture.ancientDebrisTop);
    addSimpleTexture("assets/minecraft/textures/block/ancient_debris_side", Texture.ancientDebrisSide);
    addSimpleTexture("assets/minecraft/textures/block/warped_fungus", Texture.warpedFungus);
    addSimpleTexture("assets/minecraft/textures/block/crimson_fungus", Texture.crimsonFungus);
    addSimpleTexture("assets/minecraft/textures/block/nether_sprouts", Texture.netherSprouts);
    addSimpleTexture("assets/minecraft/textures/block/warped_roots", Texture.warpedRoots);
    addSimpleTexture("assets/minecraft/textures/block/crimson_roots", Texture.crimsonRoots);
    addSimpleTexture("assets/minecraft/textures/block/warped_roots_pot", Texture.warpedRootsPot);
    addSimpleTexture("assets/minecraft/textures/block/crimson_roots_pot", Texture.crimsonRootsPot);
    addSimpleTexture("assets/minecraft/textures/block/crying_obsidian", Texture.cryingObsidian);
    addSimpleTexture("assets/minecraft/textures/block/warped_stem", Texture.warpedStem);
    addSimpleTexture("assets/minecraft/textures/block/warped_stem_top", Texture.warpedStemTop);
    addSimpleTexture("assets/minecraft/textures/block/stripped_warped_stem", Texture.strippedWarpedStem);
    addSimpleTexture("assets/minecraft/textures/block/stripped_warped_stem_top", Texture.strippedWarpedStemTop);
    addSimpleTexture("assets/minecraft/textures/block/crimson_stem", Texture.crimsonStem);
    addSimpleTexture("assets/minecraft/textures/block/crimson_stem_top", Texture.crimsonStemTop);
    addSimpleTexture("assets/minecraft/textures/block/stripped_crimson_stem", Texture.strippedCrimsonStem);
    addSimpleTexture("assets/minecraft/textures/block/stripped_crimson_stem_top", Texture.strippedCrimsonStemTop);
    ALL_TEXTURES.put("soul_lantern", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/soul_fire_lantern", Texture.soulFireLantern), // MC 20w06a-20w16a
        new SimpleTexture("assets/minecraft/textures/block/soul_lantern", Texture.soulFireLantern) // MC >= 20w17a
    ));
    addSimpleTexture("assets/minecraft/textures/block/twisting_vines", Texture.twistingVines);
    addSimpleTexture("assets/minecraft/textures/block/twisting_vines_plant", Texture.twistingVinesPlant);
    addSimpleTexture("assets/minecraft/textures/block/weeping_vines", Texture.weepingVines);
    addSimpleTexture("assets/minecraft/textures/block/weeping_vines_plant", Texture.weepingVinesPlant);
    ALL_TEXTURES.put("soul_torch", new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/block/soul_fire_torch", Texture.soulFireTorch), // MC 20w06a-20w16a
        new SimpleTexture("assets/minecraft/textures/block/soul_torch", Texture.soulFireTorch) // MC >= 20w17a
    ));
    addSimpleTexture("assets/minecraft/textures/block/respawn_anchor_top", Texture.respawnAnchorTop);
    addSimpleTexture("assets/minecraft/textures/block/respawn_anchor_bottom", Texture.respawnAnchorBottom);
    addSimpleTexture("assets/minecraft/textures/block/respawn_anchor_side0", Texture.respawnAnchorSide0);
    addSimpleTexture("assets/minecraft/textures/block/respawn_anchor_side1", Texture.respawnAnchorSide1);
    addSimpleTexture("assets/minecraft/textures/block/respawn_anchor_side2", Texture.respawnAnchorSide2);
    addSimpleTexture("assets/minecraft/textures/block/respawn_anchor_side3", Texture.respawnAnchorSide3);
    addSimpleTexture("assets/minecraft/textures/block/respawn_anchor_side4", Texture.respawnAnchorSide4);
    addSimpleTexture("assets/minecraft/textures/entity/signs/crimson", Texture.crimsonSignPost);
    addSimpleTexture("assets/minecraft/textures/entity/signs/warped", Texture.warpedSignPost);
    addSimpleTexture("assets/minecraft/textures/block/crimson_planks", Texture.crimsonPlanks);
    addSimpleTexture("assets/minecraft/textures/block/warped_planks", Texture.warpedPlanks);
    addSimpleTexture("assets/minecraft/textures/block/crimson_door_top", Texture.crimsonDoorTop);
    addSimpleTexture("assets/minecraft/textures/block/crimson_door_bottom", Texture.crimsonDoorBottom);
    addSimpleTexture("assets/minecraft/textures/block/warped_door_top", Texture.warpedDoorTop);
    addSimpleTexture("assets/minecraft/textures/block/warped_door_bottom", Texture.warpedDoorBottom);
    addSimpleTexture("assets/minecraft/textures/block/crimson_trapdoor", Texture.crimsonTrapdoor);
    addSimpleTexture("assets/minecraft/textures/block/warped_trapdoor", Texture.warpedTrapdoor);
    addSimpleTexture("assets/minecraft/textures/block/lodestone_side", Texture.lodestoneSide);
    addSimpleTexture("assets/minecraft/textures/block/lodestone_top", Texture.lodestoneTop);
    ALL_TEXTURES.put("soul_fire_layer_0", new AnimatedTextureLoader("assets/minecraft/textures/block/soul_fire_0", Texture.soulFireLayer0));
    ALL_TEXTURES.put("soul_fire_layer_1", new AnimatedTextureLoader("assets/minecraft/textures/block/soul_fire_1", Texture.soulFireLayer1));
    addSimpleTexture("assets/minecraft/textures/block/blackstone", Texture.blackstone);
    addSimpleTexture("assets/minecraft/textures/block/blackstone_top", Texture.blackstoneTop);
    addSimpleTexture("assets/minecraft/textures/block/chiseled_nether_bricks", Texture.chiseledNetherBricks);
    addSimpleTexture("assets/minecraft/textures/block/cracked_nether_bricks", Texture.crackedNetherBricks);
    addSimpleTexture("assets/minecraft/textures/block/gilded_blackstone", Texture.gildedBlackstone);
    addSimpleTexture("assets/minecraft/textures/block/soul_campfire_log_lit", Texture.soulCampfireLogLit);
    addSimpleTexture("assets/minecraft/textures/block/soul_campfire_fire", Texture.soulCampfireFire);
    addSimpleTexture("assets/minecraft/textures/block/polished_blackstone", Texture.polishedBlackstone);
    addSimpleTexture("assets/minecraft/textures/block/chiseled_polished_blackstone", Texture.chiseledPolishedBlackstone);
    addSimpleTexture("assets/minecraft/textures/block/polished_blackstone_bricks", Texture.polishedBlackstoneBricks);
    addSimpleTexture("assets/minecraft/textures/block/cracked_polished_blackstone_bricks", Texture.crackedPolishedBlackstoneBricks);
    addSimpleTexture("assets/minecraft/textures/block/quartz_bricks", Texture.quartzBricks);
    addSimpleTexture("assets/minecraft/textures/block/chain", Texture.chain);
    addSimpleTexture("assets/minecraft/textures/block/jigsaw_lock", Texture.jigsawLock);

    // Minecraft 1.17
    addSimpleTexture("assets/minecraft/textures/block/candle", Texture.candle);
    addSimpleTexture("assets/minecraft/textures/block/white_candle", Texture.whiteCandle);
    addSimpleTexture("assets/minecraft/textures/block/orange_candle", Texture.orangeCandle);
    addSimpleTexture("assets/minecraft/textures/block/magenta_candle", Texture.magentaCandle);
    addSimpleTexture("assets/minecraft/textures/block/light_blue_candle", Texture.lightBlueCandle);
    addSimpleTexture("assets/minecraft/textures/block/yellow_candle", Texture.yellowCandle);
    addSimpleTexture("assets/minecraft/textures/block/lime_candle", Texture.limeCandle);
    addSimpleTexture("assets/minecraft/textures/block/pink_candle", Texture.pinkCandle);
    addSimpleTexture("assets/minecraft/textures/block/gray_candle", Texture.grayCandle);
    addSimpleTexture("assets/minecraft/textures/block/light_gray_candle", Texture.lightGrayCandle);
    addSimpleTexture("assets/minecraft/textures/block/cyan_candle", Texture.cyanCandle);
    addSimpleTexture("assets/minecraft/textures/block/purple_candle", Texture.purpleCandle);
    addSimpleTexture("assets/minecraft/textures/block/blue_candle", Texture.blueCandle);
    addSimpleTexture("assets/minecraft/textures/block/brown_candle", Texture.brownCandle);
    addSimpleTexture("assets/minecraft/textures/block/green_candle", Texture.greenCandle);
    addSimpleTexture("assets/minecraft/textures/block/red_candle", Texture.redCandle);
    addSimpleTexture("assets/minecraft/textures/block/black_candle", Texture.blackCandle);
    addSimpleTexture("assets/minecraft/textures/block/candle_lit", Texture.candleLit);
    addSimpleTexture("assets/minecraft/textures/block/white_candle_lit", Texture.whiteCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/orange_candle_lit", Texture.orangeCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/magenta_candle_lit", Texture.magentaCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/light_blue_candle_lit", Texture.lightBlueCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/yellow_candle_lit", Texture.yellowCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/lime_candle_lit", Texture.limeCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/pink_candle_lit", Texture.pinkCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/gray_candle_lit", Texture.grayCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/light_gray_candle_lit", Texture.lightGrayCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/cyan_candle_lit", Texture.cyanCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/purple_candle_lit", Texture.purpleCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/blue_candle_lit", Texture.blueCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/brown_candle_lit", Texture.brownCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/green_candle_lit", Texture.greenCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/red_candle_lit", Texture.redCandleLit);
    addSimpleTexture("assets/minecraft/textures/block/black_candle_lit", Texture.blackCandleLit);
    addSimpleTexture("assets/minecraft/textures/particle/flame", Texture.flameParticle);
    addSimpleTexture("assets/minecraft/textures/block/copper_ore", Texture.copperOre);
    addSimpleTexture("assets/minecraft/textures/block/calcite", Texture.calcite);
    addSimpleTexture("assets/minecraft/textures/block/tuff", Texture.tuff);
    addSimpleTexture("assets/minecraft/textures/block/amethyst_block", Texture.amethyst);
    addSimpleTexture("assets/minecraft/textures/block/budding_amethyst", Texture.buddingAmethyst);
    addSimpleTexture("assets/minecraft/textures/block/copper_block", Texture.copperBlock);
    addSimpleTexture("assets/minecraft/textures/block/exposed_copper", Texture.exposedCopper);
    addSimpleTexture("assets/minecraft/textures/block/weathered_copper", Texture.weatheredCopper);
    addSimpleTexture("assets/minecraft/textures/block/oxidized_copper", Texture.oxidizedCopper);
    addSimpleTexture("assets/minecraft/textures/block/cut_copper", Texture.cutCopper);
    addSimpleTexture("assets/minecraft/textures/block/exposed_cut_copper", Texture.exposedCutCopper);
    addSimpleTexture("assets/minecraft/textures/block/weathered_cut_copper", Texture.weatheredCutCopper);
    addSimpleTexture("assets/minecraft/textures/block/oxidized_cut_copper", Texture.oxidizedCutCopper);
    addSimpleTexture("assets/minecraft/textures/block/lightning_rod", Texture.lightningRod);
    addSimpleTexture("assets/minecraft/textures/block/small_amethyst_bud", Texture.smallAmethystBud);
    addSimpleTexture("assets/minecraft/textures/block/medium_amethyst_bud", Texture.mediumAmethystBud);
    addSimpleTexture("assets/minecraft/textures/block/large_amethyst_bud", Texture.largeAmethystBud);
    addSimpleTexture("assets/minecraft/textures/block/amethyst_cluster", Texture.amethystCluster);
    addSimpleTexture("assets/minecraft/textures/block/tinted_glass", Texture.tintedGlass);
    addSimpleTexture("assets/minecraft/textures/block/powder_snow", Texture.powderSnow);
    addSimpleTexture("assets/minecraft/textures/block/dripstone_block", Texture.dripstoneBlock);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_down_base", Texture.pointedDripstoneDownBase);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_down_frustum", Texture.pointedDripstoneDownFrustum);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_down_middle", Texture.pointedDripstoneDownMiddle);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_down_tip", Texture.pointedDripstoneDownTip);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_down_tip_merge", Texture.pointedDripstoneDownTipMerge);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_up_base", Texture.pointedDripstoneUpBase);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_up_frustum", Texture.pointedDripstoneUpFrustum);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_up_middle", Texture.pointedDripstoneUpMiddle);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_up_tip", Texture.pointedDripstoneUpTip);
    addSimpleTexture("assets/minecraft/textures/block/pointed_dripstone_up_tip_merge", Texture.pointedDripstoneUpTipMerge);
    addSimpleTexture("assets/minecraft/textures/block/sculk_sensor_bottom", Texture.sculkSensorBottom);
    addSimpleTexture("assets/minecraft/textures/block/sculk_sensor_side", Texture.sculkSensorSide);
    addSimpleTexture("assets/minecraft/textures/block/sculk_sensor_tendril_active", Texture.sculkSensorTendrilActive);
    addSimpleTexture("assets/minecraft/textures/block/sculk_sensor_tendril_inactive", Texture.sculkSensorTendrilInactive);
    addSimpleTexture("assets/minecraft/textures/block/sculk_sensor_top", Texture.sculkSensorTop);
    addSimpleTexture("assets/minecraft/textures/block/glow_lichen", Texture.glowLichen);
    addSimpleTexture("assets/minecraft/textures/block/azalea_plant", Texture.azaleaPlant);
    addSimpleTexture("assets/minecraft/textures/block/azalea_top", Texture.azaleaTop);
    addSimpleTexture("assets/minecraft/textures/block/azalea_side", Texture.azaleaSide);
    addSimpleTexture("assets/minecraft/textures/block/flowering_azalea_top", Texture.floweringAzaleaTop);
    addSimpleTexture("assets/minecraft/textures/block/flowering_azalea_side", Texture.floweringAzaleaSide);
    addSimpleTexture("assets/minecraft/textures/block/azalea_leaves", Texture.azaleaLeaves);
    addSimpleTexture("assets/minecraft/textures/block/flowering_azalea_leaves", Texture.floweringAzaleaLeaves);
    addSimpleTexture("assets/minecraft/textures/block/moss_block", Texture.mossBlock);
    addSimpleTexture("assets/minecraft/textures/block/cave_vines_plant", Texture.caveVinesPlant);
    addSimpleTexture("assets/minecraft/textures/block/cave_vines", Texture.caveVines);
    addSimpleTexture("assets/minecraft/textures/block/cave_vines_plant_lit", Texture.caveVinesPlantLit);
    addSimpleTexture("assets/minecraft/textures/block/cave_vines_lit", Texture.caveVinesLit);
    addSimpleTexture("assets/minecraft/textures/block/hanging_roots", Texture.hangingRoots);
    addSimpleTexture("assets/minecraft/textures/block/rooted_dirt", Texture.rootedDirt);
    addSimpleTexture("assets/minecraft/textures/block/big_dripleaf_stem", Texture.bigDripleafStem);
    addSimpleTexture("assets/minecraft/textures/block/big_dripleaf_top", Texture.bigDripleafTop);
    addSimpleTexture("assets/minecraft/textures/block/big_dripleaf_side", Texture.bigDripleafSide);
    addSimpleTexture("assets/minecraft/textures/block/big_dripleaf_tip", Texture.bigDripleafTip);
    addSimpleTexture("assets/minecraft/textures/block/small_dripleaf_top", Texture.smallDripleafTop);
    addSimpleTexture("assets/minecraft/textures/block/small_dripleaf_side", Texture.smallDripleafSide);
    addSimpleTexture("assets/minecraft/textures/block/small_dripleaf_stem_top", Texture.smallDripleafStemTop);
    addSimpleTexture("assets/minecraft/textures/block/small_dripleaf_stem_bottom", Texture.smallDripleafStemBottom);
    addSimpleTexture("assets/minecraft/textures/block/spore_blossom", Texture.sporeBlossom);
    addSimpleTexture("assets/minecraft/textures/block/spore_blossom_base", Texture.sporeBlossomBase);
    addSimpleTexture("assets/minecraft/textures/block/deepslate", Texture.deepslate);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_top", Texture.deepslateTop);
    addSimpleTexture("assets/minecraft/textures/block/polished_deepslate", Texture.polishedDeepslate);
    addSimpleTexture("assets/minecraft/textures/block/chiseled_deepslate", Texture.chiseledDeepslate);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_bricks", Texture.deepslateBricks);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_tiles", Texture.deepslateTiles);
    addSimpleTexture("assets/minecraft/textures/block/smooth_basalt", Texture.smoothBasalt);
    addSimpleTexture("assets/minecraft/textures/block/cobbled_deepslate", Texture.cobbledDeepslate);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_gold_ore", Texture.deepslateGoldOre);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_iron_ore", Texture.deepslateIronOre);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_diamond_ore", Texture.deepslateDiamondOre);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_lapis_ore", Texture.deepslateLapisOre);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_redstone_ore", Texture.deepslateRedstoneOre);
    addSimpleTexture("assets/minecraft/textures/block/cracked_deepslate_bricks", Texture.crackedDeepslateBricks);
    addSimpleTexture("assets/minecraft/textures/block/cracked_deepslate_tiles", Texture.crackedDeepslateTiles);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_coal_ore", Texture.deepslateCoalOre);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_copper_ore", Texture.deepslateCopperOre);
    addSimpleTexture("assets/minecraft/textures/block/deepslate_emerald_ore", Texture.deepslateEmeraldOre);
    addSimpleTexture("assets/minecraft/textures/block/lightning_rod_on", Texture.lightningRodOn);
    addSimpleTexture("assets/minecraft/textures/item/light", Texture.light);
    addSimpleTexture("assets/minecraft/textures/block/raw_copper_block", Texture.rawCopperBlock);
    addSimpleTexture("assets/minecraft/textures/block/raw_gold_block", Texture.rawGoldBlock);
    addSimpleTexture("assets/minecraft/textures/block/raw_iron_block", Texture.rawIronBlock);
    addSimpleTexture("assets/minecraft/textures/block/potted_azalea_bush_top", Texture.pottedAzaleaBushTop);
    addSimpleTexture("assets/minecraft/textures/block/potted_azalea_bush_side", Texture.pottedAzaleaBushSide);
    addSimpleTexture("assets/minecraft/textures/block/potted_azalea_bush_plant", Texture.pottedAzaleaBushPlant);
    addSimpleTexture("assets/minecraft/textures/block/potted_flowering_azalea_bush_top", Texture.pottedFloweringAzaleaBushTop);
    addSimpleTexture("assets/minecraft/textures/block/potted_flowering_azalea_bush_side", Texture.pottedFloweringAzaleaBushSide);

    for (Field field : Texture.class.getFields()) {
      if (Texture.class.isAssignableFrom(field.getType())) {
        addTextureLoader(field);
      }
    }
  }

  private static void addTextureLoader(Field textureField) {
    TexturePath path = textureField.getAnnotation(TexturePath.class);
    if (path != null) {
      try {
        addSimpleTexture(path.value(), (BitmapTexture) textureField.get(BitmapTexture.class));
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Could not get texture field for " + path.value(), e);
      }
    }
  }

  private static void addSimpleTexture(String file, BitmapTexture texture) {
    addSimpleTexture(file, file, texture);
  }

  private static void addSimpleTexture(String name, String file, BitmapTexture texture) {
    ALL_TEXTURES.put(name, new SimpleTexture(file, texture));
  }
}
