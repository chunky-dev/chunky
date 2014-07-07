/* Copyright (c) 2012-2013 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.texturepack.AlternateTextures;
import se.llbit.chunky.resources.texturepack.ChestTexture;
import se.llbit.chunky.resources.texturepack.CloudsTexture;
import se.llbit.chunky.resources.texturepack.FoliageColorTexture;
import se.llbit.chunky.resources.texturepack.GrassColorTexture;
import se.llbit.chunky.resources.texturepack.IndexedTexture;
import se.llbit.chunky.resources.texturepack.LargeChestTexture;
import se.llbit.chunky.resources.texturepack.SignTexture;
import se.llbit.chunky.resources.texturepack.SimpleTexture;
import se.llbit.chunky.resources.texturepack.SunTexture;
import se.llbit.chunky.resources.texturepack.TextureRef;

/**
 * Utility methods to load Minecraft texture packs.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturePackLoader {

	@SuppressWarnings("serial")
	public static final class TextureLoadingError extends Exception {
	}

	private static final Logger logger =
			Logger.getLogger(TexturePackLoader.class);

	private static Map<String, TextureRef> allTextures =
		new HashMap<String, TextureRef>();

	static {
		allTextures.put("normal chest", new AlternateTextures(
				new ChestTexture("assets/minecraft/textures/entity/chest/normal", Texture.chestLock, // MC 1.6
					Texture.chestTop, Texture.chestBottom, Texture.chestLeft,
					Texture.chestRight, Texture.chestFront, Texture.chestBack),
				new ChestTexture("item/chest", Texture.chestLock,
					Texture.chestTop, Texture.chestBottom, Texture.chestLeft,
					Texture.chestRight, Texture.chestFront, Texture.chestBack)));
		allTextures.put("ender chest", new AlternateTextures(
				new ChestTexture("assets/minecraft/textures/entity/chest/ender", // MC 1.6
					Texture.enderChestLock, Texture.enderChestTop,
					Texture.enderChestBottom, Texture.enderChestLeft,
					Texture.enderChestRight, Texture.enderChestFront,
					Texture.enderChestBack),
				new ChestTexture("item/enderchest",
					Texture.enderChestLock, Texture.enderChestTop,
					Texture.enderChestBottom, Texture.enderChestLeft,
					Texture.enderChestRight, Texture.enderChestFront,
					Texture.enderChestBack)));
		allTextures.put("normal double chest", new AlternateTextures(
				new LargeChestTexture("assets/minecraft/textures/entity/chest/normal_double", // MC 1.6
					Texture.largeChestLeft, Texture.largeChestRight,
					Texture.largeChestTopLeft, Texture.largeChestTopRight,
					Texture.largeChestFrontLeft, Texture.largeChestFrontRight,
					Texture.largeChestBottomLeft, Texture.largeChestBottomRight,
					Texture.largeChestBackLeft, Texture.largeChestBackRight),
				new LargeChestTexture("item/largechest",
					Texture.largeChestLeft, Texture.largeChestRight,
					Texture.largeChestTopLeft, Texture.largeChestTopRight,
					Texture.largeChestFrontLeft, Texture.largeChestFrontRight,
					Texture.largeChestBottomLeft, Texture.largeChestBottomRight,
					Texture.largeChestBackLeft, Texture.largeChestBackRight)));
		allTextures.put("trapped chest", new AlternateTextures(
				new ChestTexture("assets/minecraft/textures/entity/chest/trapped", // MC 1.6
					Texture.trappedChestLock, Texture.trappedChestTop,
					Texture.trappedChestBottom, Texture.trappedChestLeft,
					Texture.trappedChestRight, Texture.trappedChestFront,
					Texture.trappedChestBack)));
		allTextures.put("trapped double chest", new AlternateTextures(
				new LargeChestTexture("assets/minecraft/textures/entity/chest/trapped_double", // MC 1.6
					Texture.largeTrappedChestLeft, Texture.largeTrappedChestRight,
					Texture.largeTrappedChestTopLeft, Texture.largeTrappedChestTopRight,
					Texture.largeTrappedChestFrontLeft, Texture.largeTrappedChestFrontRight,
					Texture.largeTrappedChestBottomLeft, Texture.largeTrappedChestBottomRight,
					Texture.largeTrappedChestBackLeft, Texture.largeTrappedChestBackRight)));
		allTextures.put("sun", new AlternateTextures(
				new SunTexture("assets/minecraft/textures/environment/sun"),// MC 1.6
				new SunTexture("environment/sun"),// MC 1.5
				new SunTexture("terrain/sun")));
		allTextures.put("sign", new AlternateTextures(
				new SignTexture("assets/minecraft/textures/entity/sign"),// MC 1.6
				new SignTexture("item/sign")));
		allTextures.put("clouds", new AlternateTextures(
				new CloudsTexture("assets/minecraft/textures/environment/clouds"),// MC 1.6
				new CloudsTexture("environment/clouds")));
		allTextures.put("grass color map", new AlternateTextures(
				new GrassColorTexture("assets/minecraft/textures/colormap/grass"),// MC 1.6
				new GrassColorTexture("misc/grasscolor")));
		allTextures.put("foliage color map", new AlternateTextures(
				new FoliageColorTexture("assets/minecraft/textures/colormap/foliage"),// MC 1.6
				new FoliageColorTexture("misc/foliagecolor")));

		allTextures.put("grass top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/grass_top", Texture.grassTop),
				new SimpleTexture("textures/blocks/grass_top", Texture.grassTop),
				new IndexedTexture(0x00, Texture.grassTop)));
		allTextures.put("stone", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/stone", Texture.stone),
				new SimpleTexture("textures/blocks/stone", Texture.stone),
				new IndexedTexture(0x01, Texture.stone)));
		allTextures.put("dirt", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/dirt", Texture.dirt),
				new SimpleTexture("textures/blocks/dirt", Texture.dirt),
				new IndexedTexture(0x02, Texture.dirt)));
		allTextures.put("grass side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/grass_side", Texture.grassSideSaturated),
				new SimpleTexture("textures/blocks/grass_side", Texture.grassSideSaturated),
				new IndexedTexture(0x03, Texture.grassSideSaturated)));
		allTextures.put("oak planks", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/planks_oak", Texture.oakPlanks),
				new SimpleTexture("textures/blocks/wood", Texture.oakPlanks),
				new IndexedTexture(0x04, Texture.oakPlanks)));
		allTextures.put("stone slab side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/stone_slab_side", Texture.slabSide),
				new SimpleTexture("textures/blocks/stoneslab_side", Texture.slabSide),
				new IndexedTexture(0x05, Texture.slabSide)));
		allTextures.put("stone slab top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/stone_slab_top", Texture.slabTop),
				new SimpleTexture("textures/blocks/stoneslab_top", Texture.slabTop),
				new IndexedTexture(0x06, Texture.slabTop)));
		allTextures.put("brick", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/brick", Texture.brick),
				new SimpleTexture("textures/blocks/brick", Texture.brick),
				new IndexedTexture(0x07, Texture.brick)));
		allTextures.put("tnt side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/tnt_side", Texture.tntSide),
				new SimpleTexture("textures/blocks/tnt_side", Texture.tntSide),
				new IndexedTexture(0x08, Texture.tntSide)));
		allTextures.put("tnt top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/tnt_top", Texture.tntTop),
				new SimpleTexture("textures/blocks/tnt_top", Texture.tntTop),
				new IndexedTexture(0x09, Texture.tntTop)));
		allTextures.put("tnt bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/tnt_bottom", Texture.tntBottom),
				new SimpleTexture("textures/blocks/tnt_bottom", Texture.tntBottom),
				new IndexedTexture(0x0A, Texture.tntBottom)));
		allTextures.put("cobweb", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/web", Texture.cobweb),
				new SimpleTexture("textures/blocks/web", Texture.cobweb),
				new IndexedTexture(0x0B, Texture.cobweb)));
		allTextures.put("rose", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/flower_rose", Texture.poppy),
				new SimpleTexture("textures/blocks/rose", Texture.poppy),
				new IndexedTexture(0x0C, Texture.poppy)));
		allTextures.put("dandelion", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/flower_dandelion", Texture.dandelion),
				new SimpleTexture("textures/blocks/flower", Texture.dandelion),
				new IndexedTexture(0x0D, Texture.dandelion)));
		allTextures.put("nether portal", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/portal", Texture.portal),
				new SimpleTexture("textures/blocks/portal", Texture.portal),
				new IndexedTexture(0x0E, Texture.portal)));
		allTextures.put("oak sapling", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sapling_oak", Texture.oakSapling),
				new SimpleTexture("textures/blocks/sapling", Texture.oakSapling),
				new IndexedTexture(0x0F, Texture.oakSapling)));

		allTextures.put("cobblestone", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cobblestone", Texture.cobblestone),
				new SimpleTexture("textures/blocks/stonebrick", Texture.cobblestone),
				new IndexedTexture(0x10, Texture.cobblestone)));
		allTextures.put("bedrock", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bedrock", Texture.bedrock),
				new SimpleTexture("textures/blocks/bedrock", Texture.bedrock),
				new IndexedTexture(0x11, Texture.bedrock)));
		allTextures.put("sand", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sand", Texture.sand),
				new SimpleTexture("textures/blocks/sand", Texture.sand),
				new IndexedTexture(0x12, Texture.sand)));
		allTextures.put("gravel", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/gravel", Texture.gravel),
				new SimpleTexture("textures/blocks/gravel", Texture.gravel),
				new IndexedTexture(0x13, Texture.gravel)));
		allTextures.put("oak log side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_oak", Texture.oakWood),
				new SimpleTexture("textures/blocks/tree_side", Texture.oakWood),
				new IndexedTexture(0x14, Texture.oakWood)));
		allTextures.put("oak log top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_oak_top", Texture.oakWoodTop),
				new SimpleTexture("textures/blocks/tree_top", Texture.oakWoodTop),
				new IndexedTexture(0x15, Texture.oakWoodTop)));
		allTextures.put("iron block", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/iron_block", Texture.ironBlock),
				new SimpleTexture("textures/blocks/blockIron", Texture.ironBlock),
				new IndexedTexture(0x16, Texture.ironBlock)));
		allTextures.put("gold block", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/gold_block", Texture.goldBlock),
				new SimpleTexture("textures/blocks/blockGold", Texture.goldBlock),
				new IndexedTexture(0x17, Texture.goldBlock)));
		allTextures.put("diamond block", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/diamond_block", Texture.diamondBlock),
				new SimpleTexture("textures/blocks/blockDiamond", Texture.diamondBlock),
				new IndexedTexture(0x18, Texture.diamondBlock)));
		allTextures.put("emerald block", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/emerald_block", Texture.emeraldBlock),
				new SimpleTexture("textures/blocks/blockEmerald", Texture.emeraldBlock),
				new IndexedTexture(0x19, Texture.emeraldBlock)));
		allTextures.put("redstone block", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_block", Texture.redstoneBlock),
				new SimpleTexture("textures/blocks/blockRedstone", Texture.redstoneBlock),
				new IndexedTexture(0x1A, Texture.redstoneBlock)));
		allTextures.put("red mushroom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mushroom_red", Texture.redMushroom),
				new SimpleTexture("textures/blocks/mushroom_red", Texture.redMushroom),
				new IndexedTexture(0x1C, Texture.redMushroom)));
		allTextures.put("brown mushroom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mushroom_brown", Texture.brownMushroom),
				new SimpleTexture("textures/blocks/mushroom_brown", Texture.brownMushroom),
				new IndexedTexture(0x1D, Texture.brownMushroom)));
		allTextures.put("jungle sapling", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sapling_jungle", Texture.jungleSapling),
				new SimpleTexture("textures/blocks/sapling_jungle", Texture.jungleSapling),
				new IndexedTexture(0x1E, Texture.jungleSapling)));

		allTextures.put("gold ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/gold_ore", Texture.goldOre),
				new SimpleTexture("textures/blocks/oreGold", Texture.goldOre),
				new IndexedTexture(0x20, Texture.goldOre)));
		allTextures.put("iron ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/iron_ore", Texture.ironOre),
				new SimpleTexture("textures/blocks/oreIron", Texture.ironOre),
				new IndexedTexture(0x21, Texture.ironOre)));
		allTextures.put("coal ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/coal_ore", Texture.coalOre),
				new SimpleTexture("textures/blocks/oreCoal", Texture.coalOre),
				new IndexedTexture(0x22, Texture.coalOre)));
		allTextures.put("bookshelf", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bookshelf", Texture.bookshelf),
				new SimpleTexture("textures/blocks/bookshelf", Texture.bookshelf),
				new IndexedTexture(0x23, Texture.bookshelf)));
		allTextures.put("mossy cobblestone", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cobblestone_mossy", Texture.mossStone),
				new SimpleTexture("textures/blocks/stoneMoss", Texture.mossStone),
				new IndexedTexture(0x24, Texture.mossStone)));
		allTextures.put("obsidian", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/obsidian", Texture.obsidian),
				new SimpleTexture("textures/blocks/obsidian", Texture.obsidian),
				new IndexedTexture(0x25, Texture.obsidian)));
		allTextures.put("grass_side_overlay", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/grass_side_overlay", Texture.grassSide),
				new SimpleTexture("textures/blocks/grass_side_overlay", Texture.grassSide),
				new IndexedTexture(0x26, Texture.grassSide)));
		allTextures.put("tallgrass", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/tallgrass", Texture.tallGrass),
				new SimpleTexture("textures/blocks/tallgrass", Texture.tallGrass),
				new IndexedTexture(0x27, Texture.tallGrass)));
		allTextures.put("beacon", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/beacon", Texture.beacon),
				new SimpleTexture("textures/blocks/beacon", Texture.beacon),
				new IndexedTexture(0x29, Texture.beacon)));
		allTextures.put("crafting_table_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_top", Texture.workbenchTop),
				new SimpleTexture("textures/blocks/workbench_top", Texture.workbenchTop),
				new IndexedTexture(0x2B, Texture.workbenchTop)));
		allTextures.put("furnace_front_off", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/furnace_front_off", Texture.furnaceUnlitFront),
				new SimpleTexture("textures/blocks/furnace_front", Texture.furnaceUnlitFront),
				new IndexedTexture(0x2C, Texture.furnaceUnlitFront)));
		allTextures.put("furnace_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/furnace_side", Texture.furnaceSide),
				new SimpleTexture("textures/blocks/furnace_side", Texture.furnaceSide),
				new IndexedTexture(0x2D, Texture.furnaceSide)));
		allTextures.put("dispenser_front_horizontal", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/dispenser_front_horizontal", Texture.dispenserFront),
				new SimpleTexture("textures/blocks/dispenser_front", Texture.dispenserFront),
				new IndexedTexture(0x2E, Texture.dispenserFront)));

		allTextures.put("sponge", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sponge", Texture.sponge),
				new SimpleTexture("textures/blocks/sponge", Texture.sponge),
				new IndexedTexture(0x30, Texture.sponge)));
		allTextures.put("glass", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/glass", Texture.glass),
				new SimpleTexture("textures/blocks/glass", Texture.glass),
				new IndexedTexture(0x31, Texture.glass)));
		allTextures.put("diamond ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/diamond_ore", Texture.diamondOre),
				new SimpleTexture("textures/blocks/oreDiamond", Texture.diamondOre),
				new IndexedTexture(0x32, Texture.diamondOre)));
		allTextures.put("redstone ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_ore", Texture.redstoneOre),
				new SimpleTexture("textures/blocks/oreRedstone", Texture.redstoneOre),
				new IndexedTexture(0x33, Texture.redstoneOre)));
		allTextures.put("oak leaves", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/leaves_oak", Texture.oakLeaves),
				new SimpleTexture("textures/blocks/leaves", Texture.oakLeaves),
				new IndexedTexture(0x34, Texture.oakLeaves)));
		allTextures.put("stone brick", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/stonebrick", Texture.stoneBrick),
				new SimpleTexture("textures/blocks/stonebricksmooth", Texture.stoneBrick),
				new IndexedTexture(0x36, Texture.stoneBrick)));
		allTextures.put("deadbush", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/deadbush", Texture.deadBush),
				new SimpleTexture("textures/blocks/deadbush", Texture.deadBush),
				new IndexedTexture(0x37, Texture.deadBush)));
		allTextures.put("fern", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/fern", Texture.fern),
				new SimpleTexture("textures/blocks/fern", Texture.fern),
				new IndexedTexture(0x38, Texture.fern)));
		allTextures.put("crafting_table_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_side", Texture.workbenchSide),
				new SimpleTexture("textures/blocks/workbench_side", Texture.workbenchSide),
				new IndexedTexture(0x3B, Texture.workbenchSide)));
		allTextures.put("crafting_table_front", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/crafting_table_front", Texture.workbenchFront),
				new SimpleTexture("textures/blocks/workbench_front", Texture.workbenchFront),
				new IndexedTexture(0x3C, Texture.workbenchFront)));
		allTextures.put("furnace_front_on", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/furnace_front_on", Texture.furnaceLitFront),
				new SimpleTexture("textures/blocks/furnace_front_lit", Texture.furnaceLitFront),
				new IndexedTexture(0x3D, Texture.furnaceLitFront)));
		allTextures.put("furnace_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/furnace_top", Texture.furnaceTop),
				new SimpleTexture("textures/blocks/furnace_top", Texture.furnaceTop),
				new IndexedTexture(0x3E, Texture.furnaceTop)));
		allTextures.put("sapling_spruce", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sapling_spruce", Texture.spruceSapling),
				new SimpleTexture("textures/blocks/sapling_spruce", Texture.spruceSapling),
				new IndexedTexture(0x3F, Texture.spruceSapling)));

		allTextures.put("white wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_white", Texture.whiteWool),
				new SimpleTexture("textures/blocks/cloth_0", Texture.whiteWool),
				new IndexedTexture(0x40, Texture.whiteWool)));
		allTextures.put("mob spawner", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mob_spawner", Texture.monsterSpawner),
				new SimpleTexture("textures/blocks/mobSpawner", Texture.monsterSpawner),
				new IndexedTexture(0x41, Texture.monsterSpawner)));
		allTextures.put("snow", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/snow", Texture.snowBlock),
				new SimpleTexture("textures/blocks/snow", Texture.snowBlock),
				new IndexedTexture(0x42, Texture.snowBlock)));
		allTextures.put("ice", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/ice", Texture.ice),
				new SimpleTexture("textures/blocks/ice", Texture.ice),
				new IndexedTexture(0x43, Texture.ice)));
		allTextures.put("snowy grass block side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/grass_side_snowed", Texture.snowSide),
				new SimpleTexture("textures/blocks/snow_side", Texture.snowSide),
				new IndexedTexture(0x44, Texture.snowSide)));
		allTextures.put("cactus_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cactus_top", Texture.cactusTop),
				new SimpleTexture("textures/blocks/cactus_top", Texture.cactusTop),
				new IndexedTexture(0x45, Texture.cactusTop)));
		allTextures.put("cactus_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cactus_side", Texture.cactusSide),
				new SimpleTexture("textures/blocks/cactus_side", Texture.cactusSide),
				new IndexedTexture(0x46, Texture.cactusSide)));
		allTextures.put("cactus_bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cactus_bottom", Texture.cactusBottom),
				new SimpleTexture("textures/blocks/cactus_bottom", Texture.cactusBottom),
				new IndexedTexture(0x47, Texture.cactusBottom)));
		allTextures.put("clay", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/clay", Texture.clay),
				new SimpleTexture("textures/blocks/clay", Texture.clay),
				new IndexedTexture(0x48, Texture.clay)));
		allTextures.put("reeds", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/reeds", Texture.sugarCane),
				new SimpleTexture("textures/blocks/reeds", Texture.sugarCane),
				new IndexedTexture(0x49, Texture.sugarCane)));
		allTextures.put("noteblock", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/noteblock", Texture.jukeboxSide),
				new SimpleTexture("textures/blocks/musicBlock", Texture.jukeboxSide),
				new IndexedTexture(0x4A, Texture.jukeboxSide)));
		allTextures.put("jukebox_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/jukebox_top", Texture.jukeboxTop),
				new SimpleTexture("textures/blocks/jukebox_top", Texture.jukeboxTop),
				new IndexedTexture(0x4B, Texture.jukeboxTop)));
		allTextures.put("waterlily", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/waterlily", Texture.lilyPad),
				new SimpleTexture("textures/blocks/waterlily", Texture.lilyPad),
				new IndexedTexture(0x4C, Texture.lilyPad)));
		allTextures.put("mycelium side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mycelium_side", Texture.myceliumSide),
				new SimpleTexture("textures/blocks/mycel_side", Texture.myceliumSide),
				new IndexedTexture(0x4D, Texture.myceliumSide)));
		allTextures.put("mycelium top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mycelium_top", Texture.myceliumTop),
				new SimpleTexture("textures/blocks/mycel_top", Texture.myceliumTop),
				new IndexedTexture(0x4E, Texture.myceliumTop)));
		allTextures.put("sapling_birch", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sapling_birch", Texture.birchSapling),
				new SimpleTexture("textures/blocks/sapling_birch", Texture.birchSapling),
				new IndexedTexture(0x4F, Texture.birchSapling)));

		allTextures.put("torch", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/torch_on", Texture.torch),
				new SimpleTexture("textures/blocks/torch", Texture.torch),
				new IndexedTexture(0x50, Texture.torch)));
		allTextures.put("door_wood_upper", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/door_wood_upper", Texture.woodenDoorTop),
				new SimpleTexture("textures/blocks/doorWood_upper", Texture.woodenDoorTop),
				new IndexedTexture(0x51, Texture.woodenDoorTop)));
		allTextures.put("door_iron_upper", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/door_iron_upper", Texture.ironDoorTop),
				new SimpleTexture("textures/blocks/doorIron_upper", Texture.ironDoorTop),
				new IndexedTexture(0x52, Texture.ironDoorTop)));
		allTextures.put("ladder", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/ladder", Texture.ladder),
				new SimpleTexture("textures/blocks/ladder", Texture.ladder),
				new IndexedTexture(0x53, Texture.ladder)));
		allTextures.put("trapdoor", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/trapdoor", Texture.trapdoor),
				new SimpleTexture("textures/blocks/trapdoor", Texture.trapdoor),
				new IndexedTexture(0x54, Texture.trapdoor)));
		allTextures.put("iron bars", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/iron_bars", Texture.ironBars),
				new SimpleTexture("textures/blocks/fenceIron", Texture.ironBars),
				new IndexedTexture(0x55, Texture.ironBars)));
		allTextures.put("farmland_wet", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/farmland_wet", Texture.farmlandWet),
				new SimpleTexture("textures/blocks/farmland_wet", Texture.farmlandWet),
				new IndexedTexture(0x56, Texture.farmlandWet)));
		allTextures.put("farmland_dry", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/farmland_dry", Texture.farmlandDry),
				new SimpleTexture("textures/blocks/farmland_dry", Texture.farmlandDry),
				new IndexedTexture(0x57, Texture.farmlandDry)));
		allTextures.put("wheat_stage_0", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_0", Texture.crops0),
				new SimpleTexture("textures/blocks/crops_0", Texture.crops0),
				new IndexedTexture(0x58, Texture.crops0)));
		allTextures.put("wheat_stage_1", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_1", Texture.crops1),
				new SimpleTexture("textures/blocks/crops_1", Texture.crops1),
				new IndexedTexture(0x59, Texture.crops1)));
		allTextures.put("wheat_stage_2", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_2", Texture.crops2),
				new SimpleTexture("textures/blocks/crops_2", Texture.crops2),
				new IndexedTexture(0x5A, Texture.crops2)));
		allTextures.put("wheat_stage_3", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_3", Texture.crops3),
				new SimpleTexture("textures/blocks/crops_3", Texture.crops3),
				new IndexedTexture(0x5B, Texture.crops3)));
		allTextures.put("wheat_stage_4", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_4", Texture.crops4),
				new SimpleTexture("textures/blocks/crops_4", Texture.crops4),
				new IndexedTexture(0x5C, Texture.crops4)));
		allTextures.put("wheat_stage_5", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_5", Texture.crops5),
				new SimpleTexture("textures/blocks/crops_5", Texture.crops5),
				new IndexedTexture(0x5D, Texture.crops5)));
		allTextures.put("wheat_stage_6", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_6", Texture.crops6),
				new SimpleTexture("textures/blocks/crops_6", Texture.crops6),
				new IndexedTexture(0x5E, Texture.crops6)));
		allTextures.put("wheat_stage_7", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wheat_stage_7", Texture.crops7),
				new SimpleTexture("textures/blocks/crops_7", Texture.crops7),
				new IndexedTexture(0x5F, Texture.crops7)));

		allTextures.put("lever", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/lever", Texture.lever),
				new SimpleTexture("textures/blocks/lever", Texture.lever),
				new IndexedTexture(0x60, Texture.lever)));
		allTextures.put("door_wood_lower", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/door_wood_lower", Texture.woodenDoorBottom),
				new SimpleTexture("textures/blocks/doorWood_lower", Texture.woodenDoorBottom),
				new IndexedTexture(0x61, Texture.woodenDoorBottom)));
		allTextures.put("door_iron_lower", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/door_iron_lower", Texture.ironDoorBottom),
				new SimpleTexture("textures/blocks/doorIron_lower", Texture.ironDoorBottom),
				new IndexedTexture(0x62, Texture.ironDoorBottom)));
		allTextures.put("redstone_torch_on", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch_on", Texture.redstoneTorchOn),
				new SimpleTexture("textures/blocks/redtorch_lit", Texture.redstoneTorchOn),
				new IndexedTexture(0x63, Texture.redstoneTorchOn)));
		allTextures.put("stonebrick_mossy", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_mossy", Texture.mossyStoneBrick),
				new SimpleTexture("textures/blocks/stonebricksmooth_mossy", Texture.mossyStoneBrick),
				new IndexedTexture(0x64, Texture.mossyStoneBrick)));
		allTextures.put("stonebrick_cracked", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_cracked", Texture.crackedStoneBrick),
				new SimpleTexture("textures/blocks/stonebricksmooth_cracked", Texture.crackedStoneBrick),
				new IndexedTexture(0x65, Texture.crackedStoneBrick)));
		allTextures.put("pumpkin_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_top", Texture.pumpkinTop),
				new SimpleTexture("textures/blocks/pumpkin_top", Texture.pumpkinTop),
				new IndexedTexture(0x66, Texture.pumpkinTop)));
		allTextures.put("netherrack", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/netherrack", Texture.netherrack),
				new SimpleTexture("textures/blocks/hellrock", Texture.netherrack),
				new IndexedTexture(0x67, Texture.netherrack)));
		allTextures.put("soul_sand", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/soul_sand", Texture.soulsand),
				new SimpleTexture("textures/blocks/hellsand", Texture.soulsand),
				new IndexedTexture(0x68, Texture.soulsand)));
		allTextures.put("glowstone", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/glowstone", Texture.glowstone),
				new SimpleTexture("textures/blocks/lightgem", Texture.glowstone),
				new IndexedTexture(0x69, Texture.glowstone)));
		allTextures.put("piston_top_sticky", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/piston_top_sticky", Texture.pistonTopSticky),
				new SimpleTexture("textures/blocks/piston_top_sticky", Texture.pistonTopSticky),
				new IndexedTexture(0x6A, Texture.pistonTopSticky)));
		allTextures.put("piston_top_normal", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/piston_top_normal", Texture.pistonTop),
				new SimpleTexture("textures/blocks/piston_top", Texture.pistonTop),
				new IndexedTexture(0x6B, Texture.pistonTop)));
		allTextures.put("piston_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/piston_side", Texture.pistonSide),
				new SimpleTexture("textures/blocks/piston_side", Texture.pistonSide),
				new IndexedTexture(0x6C, Texture.pistonSide)));
		allTextures.put("piston_bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/piston_bottom", Texture.pistonBottom),
				new SimpleTexture("textures/blocks/piston_bottom", Texture.pistonBottom),
				new IndexedTexture(0x6D, Texture.pistonBottom)));
		allTextures.put("piston_inner", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/piston_inner", Texture.pistonInnerTop),
				new SimpleTexture("textures/blocks/piston_inner_top", Texture.pistonInnerTop),
				new IndexedTexture(0x6E, Texture.pistonInnerTop)));
		// TODO pumpkin stem variants
		allTextures.put("melon_stem_disconnected", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/melon_stem_disconnected", Texture.stemStraight),
				new SimpleTexture("textures/blocks/stem_straight", Texture.stemStraight),
				new IndexedTexture(0x6F, Texture.stemStraight)));

		allTextures.put("rail_normal_turned", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/rail_normal_turned", Texture.railsCurved),
				new SimpleTexture("textures/blocks/rail_turn", Texture.railsCurved),
				new IndexedTexture(0x70, Texture.railsCurved)));
		allTextures.put("black wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_black", Texture.blackWool),
				new SimpleTexture("textures/blocks/cloth_15", Texture.blackWool),
				new IndexedTexture(0x71, Texture.blackWool)));
		allTextures.put("gray wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_gray", Texture.grayWool),
				new SimpleTexture("textures/blocks/cloth_7", Texture.grayWool),
				new IndexedTexture(0x72, Texture.grayWool)));
		allTextures.put("redstone_torch_off", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_torch_off", Texture.redstoneTorchOff),
				new SimpleTexture("textures/blocks/redtorch", Texture.redstoneTorchOff),
				new IndexedTexture(0x73, Texture.redstoneTorchOff)));
		allTextures.put("log_spruce", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_spruce", Texture.spruceWood),
				new SimpleTexture("textures/blocks/tree_spruce", Texture.spruceWood),
				new IndexedTexture(0x74, Texture.spruceWood)));
		allTextures.put("log_birch", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_birch", Texture.birchWood),
				new SimpleTexture("textures/blocks/tree_birch", Texture.birchWood),
				new IndexedTexture(0x75, Texture.birchWood)));
		allTextures.put("pumpkin_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_side", Texture.pumpkinSide),
				new SimpleTexture("textures/blocks/pumpkin_side", Texture.pumpkinSide),
				new IndexedTexture(0x76, Texture.pumpkinSide)));
		allTextures.put("pumpkin_face_off", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face_off", Texture.pumpkinFront),
				new SimpleTexture("textures/blocks/pumpkin_face", Texture.pumpkinFront),
				new IndexedTexture(0x77, Texture.pumpkinFront)));
		allTextures.put("pumpkin_face_on", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/pumpkin_face_on", Texture.jackolanternFront),
				new SimpleTexture("textures/blocks/pumpkin_jack", Texture.jackolanternFront),
				new IndexedTexture(0x78, Texture.jackolanternFront)));
		allTextures.put("cake_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cake_top", Texture.cakeTop),
				new SimpleTexture("textures/blocks/cake_top", Texture.cakeTop),
				new IndexedTexture(0x79, Texture.cakeTop)));
		allTextures.put("cake_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cake_side", Texture.cakeSide),
				new SimpleTexture("textures/blocks/cake_side", Texture.cakeSide),
				new IndexedTexture(0x7A, Texture.cakeSide)));
		allTextures.put("cake_inner", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cake_inner", Texture.cakeInside),
				new SimpleTexture("textures/blocks/cake_inner", Texture.cakeInside),
				new IndexedTexture(0x7B, Texture.cakeInside)));
		allTextures.put("cake_bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cake_bottom", Texture.cakeBottom),
				new SimpleTexture("textures/blocks/cake_bottom", Texture.cakeBottom),
				new IndexedTexture(0x7C, Texture.cakeBottom)));
		allTextures.put("mushroom_block_skin_red", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_red", Texture.hugeRedMushroom),
				new SimpleTexture("textures/blocks/mushroom_skin_red", Texture.hugeRedMushroom),
				new IndexedTexture(0x7D, Texture.hugeRedMushroom)));
		allTextures.put("mushroom_block_skin_brown", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_brown", Texture.hugeBrownMushroom),
				new SimpleTexture("textures/blocks/mushroom_skin_brown", Texture.hugeBrownMushroom),
				new IndexedTexture(0x7E, Texture.hugeBrownMushroom)));
		allTextures.put("melon_stem_connected", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/melon_stem_connected", Texture.stemBent),
				new SimpleTexture("textures/blocks/stem_bent", Texture.stemBent),
				new IndexedTexture(0x7F, Texture.stemBent)));

		allTextures.put("rail_normal", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/rail_normal", Texture.rails),
				new SimpleTexture("textures/blocks/rail", Texture.rails),
				new IndexedTexture(0x80, Texture.rails)));
		allTextures.put("red wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_red", Texture.redWool),
				new SimpleTexture("textures/blocks/cloth_14", Texture.redWool),
				new IndexedTexture(0x81, Texture.redWool)));
		allTextures.put("pink wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_pink", Texture.pinkWool),
				new SimpleTexture("textures/blocks/cloth_6", Texture.pinkWool),
				new IndexedTexture(0x82, Texture.pinkWool)));
		allTextures.put("repeater_off", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/repeater_off", Texture.redstoneRepeaterOff),
				new SimpleTexture("textures/blocks/repeater", Texture.redstoneRepeaterOff),
				new IndexedTexture(0x83, Texture.redstoneRepeaterOff)));
		allTextures.put("leaves_spruce", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/leaves_spruce", Texture.spruceLeaves),
				new SimpleTexture("textures/blocks/leaves_spruce", Texture.spruceLeaves),
				new IndexedTexture(0x84, Texture.spruceLeaves)));
		allTextures.put("bed_feet_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bed_feet_top", Texture.bedFootTop),
				new SimpleTexture("textures/blocks/bed_feet_top", Texture.bedFootTop),
				new IndexedTexture(0x86, Texture.bedFootTop)));
		allTextures.put("bed_head_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bed_head_top", Texture.bedHeadTop),
				new SimpleTexture("textures/blocks/bed_head_top", Texture.bedHeadTop),
				new IndexedTexture(0x87, Texture.bedHeadTop)));
		allTextures.put("melon_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/melon_side", Texture.melonSide),
				new SimpleTexture("textures/blocks/melon_side", Texture.melonSide),
				new IndexedTexture(0x88, Texture.melonSide)));
		allTextures.put("melon_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/melon_top", Texture.melonTop),
				new SimpleTexture("textures/blocks/melon_top", Texture.melonTop),
				new IndexedTexture(0x89, Texture.melonTop)));
		allTextures.put("cauldron_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cauldron_top", Texture.cauldronTop),
				new SimpleTexture("textures/blocks/cauldron_top", Texture.cauldronTop),
				new IndexedTexture(0x8A, Texture.cauldronTop)));
		allTextures.put("cauldron_inner", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cauldron_inner", Texture.cauldronInside),
				new SimpleTexture("textures/blocks/cauldron_inner", Texture.cauldronInside),
				new IndexedTexture(0x8B, Texture.cauldronInside)));
		allTextures.put("mushroom_block_skin_stem", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_skin_stem", Texture.mushroomStem),
				new SimpleTexture("textures/blocks/mushroom_skin_stem", Texture.mushroomStem),
				new IndexedTexture(0x8D, Texture.mushroomStem)));
		allTextures.put("mushroom_block_inside", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/mushroom_block_inside", Texture.mushroomPores),
				new SimpleTexture("textures/blocks/mushroom_inside", Texture.mushroomPores),
				new IndexedTexture(0x8E, Texture.mushroomPores)));
		allTextures.put("vine", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/vine", Texture.vines),
				new SimpleTexture("textures/blocks/vine", Texture.vines),
				new IndexedTexture(0x8F, Texture.vines)));

		allTextures.put("lapis_block", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/lapis_block", Texture.lapislazuliBlock),
				new SimpleTexture("textures/blocks/blockLapis", Texture.lapislazuliBlock),
				new IndexedTexture(0x90, Texture.lapislazuliBlock)));
		allTextures.put("green wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_green", Texture.greenWool),
				new SimpleTexture("textures/blocks/cloth_13", Texture.greenWool),
				new IndexedTexture(0x91, Texture.greenWool)));
		allTextures.put("lime wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_lime", Texture.limeWool),
				new SimpleTexture("textures/blocks/cloth_5", Texture.limeWool),
				new IndexedTexture(0x92, Texture.limeWool)));
		allTextures.put("repeater_on", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/repeater_on", Texture.redstoneRepeaterOn),
				new SimpleTexture("textures/blocks/repeater_lit", Texture.redstoneRepeaterOn),
				new IndexedTexture(0x93, Texture.redstoneRepeaterOn)));
		allTextures.put("glass_pane_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top", Texture.glassPaneSide),
				new SimpleTexture("textures/blocks/thinglass_top", Texture.glassPaneSide),
				new IndexedTexture(0x94, Texture.glassPaneSide)));
		allTextures.put("bed_feet_end", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bed_feet_end", Texture.bedFootEnd),
				new SimpleTexture("textures/blocks/bed_feet_end", Texture.bedFootEnd),
				new IndexedTexture(0x95, Texture.bedFootEnd)));
		allTextures.put("bed_feet_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bed_feet_side", Texture.bedFootSide),
				new SimpleTexture("textures/blocks/bed_feet_side", Texture.bedFootSide),
				new IndexedTexture(0x96, Texture.bedFootSide)));
		allTextures.put("bed_head_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bed_head_side", Texture.bedHeadSide),
				new SimpleTexture("textures/blocks/bed_head_side", Texture.bedHeadSide),
				new IndexedTexture(0x97, Texture.bedHeadSide)));
		allTextures.put("bed_head_end", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/bed_head_end", Texture.bedHeadEnd),
				new SimpleTexture("textures/blocks/bed_head_end", Texture.bedHeadEnd),
				new IndexedTexture(0x98, Texture.bedHeadEnd)));
		allTextures.put("log_jungle", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_jungle", Texture.jungleWood),
				new SimpleTexture("textures/blocks/tree_jungle", Texture.jungleWood),
				new IndexedTexture(0x99, Texture.jungleWood)));
		allTextures.put("cauldron_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cauldron_side", Texture.cauldronSide),
				new SimpleTexture("textures/blocks/cauldron_side", Texture.cauldronSide),
				new IndexedTexture(0x9A, Texture.cauldronSide)));
		allTextures.put("cauldron_bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cauldron_bottom", Texture.cauldronBottom),
				new SimpleTexture("textures/blocks/cauldron_bottom", Texture.cauldronBottom),
				new IndexedTexture(0x9B, Texture.cauldronBottom)));
		allTextures.put("brewing_stand_base", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/brewing_stand_base", Texture.brewingStandBase),
				new SimpleTexture("textures/blocks/brewingStand_base", Texture.brewingStandBase),
				new IndexedTexture(0x9C, Texture.brewingStandBase)));
		allTextures.put("brewing_stand", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/brewing_stand", Texture.brewingStandSide),
				new SimpleTexture("textures/blocks/brewingStand", Texture.brewingStandSide),
				new IndexedTexture(0x9D, Texture.brewingStandSide)));
		allTextures.put("endframe_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/endframe_top", Texture.endPortalFrameTop),
				new SimpleTexture("textures/blocks/endframe_top", Texture.endPortalFrameTop),
				new IndexedTexture(0x9E, Texture.endPortalFrameTop)));
		allTextures.put("endframe_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/endframe_side", Texture.endPortalFrameSide),
				new SimpleTexture("textures/blocks/endframe_side", Texture.endPortalFrameSide),
				new IndexedTexture(0x9F, Texture.endPortalFrameSide)));

		allTextures.put("lapis_ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/lapis_ore", Texture.lapislazuliOre),
				new SimpleTexture("textures/blocks/oreLapis", Texture.lapislazuliOre),
				new IndexedTexture(0xA0, Texture.lapislazuliOre)));
		allTextures.put("brown wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_brown", Texture.brownWool),
				new SimpleTexture("textures/blocks/cloth_12", Texture.brownWool),
				new IndexedTexture(0xA1, Texture.brownWool)));
		allTextures.put("yellow wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_yellow", Texture.yellowWool),
				new SimpleTexture("textures/blocks/cloth_4", Texture.yellowWool),
				new IndexedTexture(0xA2, Texture.yellowWool)));
		allTextures.put("rail_golden", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/rail_golden", Texture.poweredRailOff),
				new SimpleTexture("textures/blocks/goldenRail", Texture.poweredRailOff),
				new IndexedTexture(0xA3, Texture.poweredRailOff)));
		allTextures.put("redstone_dust_cross", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_dust_cross", Texture.redstoneWireCross),
				new SimpleTexture("textures/blocks/redstoneDust_cross", Texture.redstoneWireCross),
				new IndexedTexture(0xA4, Texture.redstoneWireCross)));
		allTextures.put("redstone_dust_line", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_dust_line", Texture.redstoneWire),
				new SimpleTexture("textures/blocks/redstoneDust_line", Texture.redstoneWire),
				new IndexedTexture(0xA5, Texture.redstoneWire)));
		allTextures.put("enchanting_table_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_top", Texture.enchantmentTableTop),
				new SimpleTexture("textures/blocks/enchantment_top", Texture.enchantmentTableTop),
				new IndexedTexture(0xA6, Texture.enchantmentTableTop)));
		allTextures.put("dragon_egg", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/dragon_egg", Texture.dragonEgg),
				new SimpleTexture("textures/blocks/dragonEgg", Texture.dragonEgg),
				new IndexedTexture(0xA7, Texture.dragonEgg)));
		allTextures.put("cocoa_stage_2", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_2", Texture.cocoaPlantLarge),
				new SimpleTexture("textures/blocks/cocoa_2", Texture.cocoaPlantLarge),
				new IndexedTexture(0xA8, Texture.cocoaPlantLarge)));
		allTextures.put("cocoa_stage_1", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_1", Texture.cocoaPlantMedium),
				new SimpleTexture("textures/blocks/cocoa_1", Texture.cocoaPlantMedium),
				new IndexedTexture(0xA9, Texture.cocoaPlantMedium)));
		allTextures.put("cocoa_stage_0", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/cocoa_stage_0", Texture.cocoaPlantSmall),
				new SimpleTexture("textures/blocks/cocoa_0", Texture.cocoaPlantSmall),
				new IndexedTexture(0xAA, Texture.cocoaPlantSmall)));
		allTextures.put("emerald_ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/emerald_ore", Texture.emeraldOre),
				new SimpleTexture("textures/blocks/oreEmerald", Texture.emeraldOre),
				new IndexedTexture(0xAB, Texture.emeraldOre)));
		allTextures.put("trip_wire_source", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/trip_wire_source", Texture.tripwireHook),
				new SimpleTexture("textures/blocks/tripWireSource", Texture.tripwireHook),
				new IndexedTexture(0xAC, Texture.tripwireHook)));
		allTextures.put("trip_wire", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/trip_wire", Texture.tripwire),
				new SimpleTexture("textures/blocks/tripWire", Texture.tripwire),
				new IndexedTexture(0xAD, Texture.tripwire)));
		allTextures.put("endframe_eye", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/endframe_eye", Texture.eyeOfTheEnder),
				new SimpleTexture("textures/blocks/endframe_eye", Texture.eyeOfTheEnder),
				new IndexedTexture(0xAE, Texture.eyeOfTheEnder)));
		allTextures.put("end_stone", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/end_stone", Texture.endStone),
				new SimpleTexture("textures/blocks/whiteStone", Texture.endStone),
				new IndexedTexture(0xAF, Texture.endStone)));

		allTextures.put("sandstone_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sandstone_top", Texture.sandstoneTop),
				new SimpleTexture("textures/blocks/sandstone_top", Texture.sandstoneTop),
				new IndexedTexture(0xB0, Texture.sandstoneTop)));
		allTextures.put("blue wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_blue", Texture.blueWool),
				new SimpleTexture("textures/blocks/cloth_11", Texture.blueWool),
				new IndexedTexture(0xB1, Texture.blueWool)));
		allTextures.put("light blue wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_light_blue", Texture.lightBlueWool),
				new SimpleTexture("textures/blocks/cloth_3", Texture.lightBlueWool),
				new IndexedTexture(0xB2, Texture.lightBlueWool)));
		allTextures.put("rail_golden_powered", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/rail_golden_powered", Texture.poweredRailOn),
				new SimpleTexture("textures/blocks/goldenRail_powered", Texture.poweredRailOn),
				new IndexedTexture(0xB3, Texture.poweredRailOn)));
		allTextures.put("enchanting_table_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_side", Texture.enchantmentTableSide),
				new SimpleTexture("textures/blocks/enchantment_side", Texture.enchantmentTableSide),
				new IndexedTexture(0xB6, Texture.enchantmentTableSide)));
		allTextures.put("enchanting_table_bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/enchanting_table_bottom", Texture.enchantmentTableBottom),
				new SimpleTexture("textures/blocks/enchantment_bottom", Texture.enchantmentTableBottom),
				new IndexedTexture(0xB7, Texture.enchantmentTableBottom)));
		allTextures.put("command_block", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/command_block", Texture.commandBlock),
				new SimpleTexture("textures/blocks/commandBlock", Texture.commandBlock),
				new IndexedTexture(0xB8, Texture.commandBlock)));
		allTextures.put("flower_pot", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/flower_pot", Texture.flowerPot),
				new SimpleTexture("textures/blocks/flowerPot", Texture.flowerPot),
				new IndexedTexture(0xBA, Texture.flowerPot)));
		allTextures.put("quartz_ore", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_ore", Texture.netherQuartzOre),
				new SimpleTexture("textures/blocks/netherquartz", Texture.netherQuartzOre),
				new IndexedTexture(0xBF, Texture.netherQuartzOre)));

		allTextures.put("sandstone_normal", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sandstone_normal", Texture.sandstoneSide),
				new SimpleTexture("textures/blocks/sandstone_side", Texture.sandstoneSide),
				new IndexedTexture(0xC0, Texture.sandstoneSide)));
		allTextures.put("purple wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_purple", Texture.purpleWool),
				new SimpleTexture("textures/blocks/cloth_10", Texture.purpleWool),
				new IndexedTexture(0xC1, Texture.purpleWool)));
		allTextures.put("magenta wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_magenta", Texture.magentaWool),
				new SimpleTexture("textures/blocks/cloth_2", Texture.magentaWool),
				new IndexedTexture(0xC2, Texture.magentaWool)));
		allTextures.put("rail_detector", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/rail_detector", Texture.detectorRail),
				new SimpleTexture("textures/blocks/detectorRail", Texture.detectorRail),
				new IndexedTexture(0xC3, Texture.detectorRail)));
		allTextures.put("leaves_jungle", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/leaves_jungle", Texture.jungleTreeLeaves),
				new SimpleTexture("textures/blocks/leaves_jungle", Texture.jungleTreeLeaves),
				new IndexedTexture(0xC4, Texture.jungleTreeLeaves)));
		allTextures.put("planks_spruce", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/planks_spruce", Texture.sprucePlanks),
				new SimpleTexture("textures/blocks/wood_spruce", Texture.sprucePlanks),
				new IndexedTexture(0xC6, Texture.sprucePlanks)));
		allTextures.put("planks_jungle", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/planks_jungle", Texture.jungleTreePlanks),
				new SimpleTexture("textures/blocks/wood_jungle", Texture.jungleTreePlanks),
				new IndexedTexture(0xC7, Texture.jungleTreePlanks)));
		allTextures.put("carrots_stage_0", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_0", Texture.carrots0),
				new SimpleTexture("textures/blocks/carrots_0", Texture.carrots0),
				new IndexedTexture(0xC8, Texture.carrots0)));
		allTextures.put("potatoes_stage_0", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_0", Texture.potatoes0),
				new SimpleTexture("textures/blocks/potatoes_0", Texture.potatoes0),
				new IndexedTexture(0xC8, Texture.potatoes0)));
		allTextures.put("carrots_stage_1", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_1", Texture.carrots1),
				new SimpleTexture("textures/blocks/carrots_1", Texture.carrots1),
				new IndexedTexture(0xC9, Texture.carrots1)));
		allTextures.put("potatoes_stage_1", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_1", Texture.potatoes1),
				new SimpleTexture("textures/blocks/potatoes_1", Texture.potatoes1),
				new IndexedTexture(0xC9, Texture.potatoes1)));
		allTextures.put("carrots_stage_2", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_2", Texture.carrots2),
				new SimpleTexture("textures/blocks/carrots_2", Texture.carrots2),
				new IndexedTexture(0xCA, Texture.carrots2)));
		allTextures.put("potatoes_stage_2", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_2", Texture.potatoes2),
				new SimpleTexture("textures/blocks/potatoes_2", Texture.potatoes2),
				new IndexedTexture(0xCA, Texture.potatoes2)));
		allTextures.put("carrots_stage_3", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/carrots_stage_3", Texture.carrots3),
				new SimpleTexture("textures/blocks/carrots_3", Texture.carrots3),
				new IndexedTexture(0xCB, Texture.carrots3)));
		allTextures.put("potatoes_stage_3", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/potatoes_stage_3", Texture.potatoes3),
				new SimpleTexture("textures/blocks/potatoes_3", Texture.potatoes3),
				new IndexedTexture(0xCC, Texture.potatoes3)));
		allTextures.put("water_still", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/water_still", Texture.water),
				new SimpleTexture("textures/blocks/water", Texture.water),
				new IndexedTexture(0xCD, Texture.water)));

		allTextures.put("sandstone_bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sandstone_bottom", Texture.sandstoneBottom),
				new SimpleTexture("textures/blocks/sandstone_bottom", Texture.sandstoneBottom),
				new IndexedTexture(0xD0, Texture.sandstoneBottom)));
		allTextures.put("cyan wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_cyan", Texture.cyanWool),
				new SimpleTexture("textures/blocks/cloth_9", Texture.cyanWool),
				new IndexedTexture(0xD1, Texture.cyanWool)));
		allTextures.put("orange wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_orange", Texture.orangeWool),
				new SimpleTexture("textures/blocks/cloth_1", Texture.orangeWool),
				new IndexedTexture(0xD2, Texture.orangeWool)));
		allTextures.put("redstone_lamp_off", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_lamp_off", Texture.redstoneLampOff),
				new SimpleTexture("textures/blocks/redstoneLight", Texture.redstoneLampOff),
				new IndexedTexture(0xD3, Texture.redstoneLampOff)));
		allTextures.put("redstone_lamp_on", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/redstone_lamp_on", Texture.redstoneLampOn),
				new SimpleTexture("textures/blocks/redstoneLight_lit", Texture.redstoneLampOn),
				new IndexedTexture(0xD4, Texture.redstoneLampOn)));
		allTextures.put("stonebrick_carved", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/stonebrick_carved", Texture.circleStoneBrick),
				new SimpleTexture("textures/blocks/stonebricksmooth_carved", Texture.circleStoneBrick),
				new IndexedTexture(0xD5, Texture.circleStoneBrick)));
		allTextures.put("planks_birch", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/planks_birch", Texture.birchPlanks),
				new SimpleTexture("textures/blocks/wood_birch", Texture.birchPlanks),
				new IndexedTexture(0xD6, Texture.birchPlanks)));
		allTextures.put("anvil_base", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/anvil_base", Texture.anvilSide),
				new SimpleTexture("textures/blocks/anvil_base", Texture.anvilSide),
				new IndexedTexture(0xD7, Texture.anvilSide)));
		allTextures.put("anvil_top_damaged_1", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/anvil_top_damaged_1", Texture.anvilTopDamaged1),
				new SimpleTexture("textures/blocks/anvil_top_damaged_1", Texture.anvilTopDamaged1),
				new IndexedTexture(0xD8, Texture.anvilTopDamaged1)));

		allTextures.put("nether_brick", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/nether_brick", Texture.netherBrick),
				new SimpleTexture("textures/blocks/netherBrick", Texture.netherBrick),
				new IndexedTexture(0xE0, Texture.netherBrick)));
		allTextures.put("light gray wool", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/wool_colored_silver", Texture.lightGrayWool),
				new SimpleTexture("textures/blocks/cloth_8", Texture.lightGrayWool),
				new IndexedTexture(0xE1, Texture.lightGrayWool)));
		allTextures.put("nether_wart_stage_0", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_0", Texture.netherWart0),
				new SimpleTexture("textures/blocks/netherStalk_0", Texture.netherWart0),
				new IndexedTexture(0xE2, Texture.netherWart0)));
		allTextures.put("nether_wart_stage_1", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_1", Texture.netherWart1),
				new SimpleTexture("textures/blocks/netherStalk_1", Texture.netherWart1),
				new IndexedTexture(0xE3, Texture.netherWart1)));
		allTextures.put("nether_wart_stage_2", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/nether_wart_stage_2", Texture.netherWart2),
				new SimpleTexture("textures/blocks/netherStalk_2", Texture.netherWart2),
				new IndexedTexture(0xE4, Texture.netherWart2)));
		allTextures.put("sandstone_carved", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sandstone_carved", Texture.sandstoneDecorated),
				new SimpleTexture("textures/blocks/sandstone_carved", Texture.sandstoneDecorated),
				new IndexedTexture(0xE5, Texture.sandstoneDecorated)));
		allTextures.put("sandstone_smooth", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/sandstone_smooth", Texture.sandstoneSmooth),
				new SimpleTexture("textures/blocks/sandstone_smooth", Texture.sandstoneSmooth),
				new IndexedTexture(0xE6, Texture.sandstoneSmooth)));
		allTextures.put("anvil_top_damaged_0", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/anvil_top_damaged_0", Texture.anvilTop),
				new SimpleTexture("textures/blocks/anvil_top", Texture.anvilTop),
				new IndexedTexture(0xE7, Texture.anvilTop)));
		allTextures.put("anvil_top_damaged_2", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/anvil_top_damaged_2", Texture.anvilTopDamaged2),
				new SimpleTexture("textures/blocks/anvil_top_damaged_2", Texture.anvilTopDamaged2),
				new IndexedTexture(0xE8, Texture.anvilTopDamaged2)));
		allTextures.put("lava_still", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/lava_still", Texture.lava),
				new SimpleTexture("textures/blocks/lava", Texture.lava),
				new IndexedTexture(0xED, Texture.lava)));

		// MC 1.5
		allTextures.put("quartz_block_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_side", Texture.quartzSide),
				new SimpleTexture("textures/blocks/quartzblock_side", Texture.quartzSide)));
		allTextures.put("quartz_block_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_top", Texture.quartzTop),
				new SimpleTexture("textures/blocks/quartzblock_top", Texture.quartzTop)));
		allTextures.put("quartz_block_bottom", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_bottom", Texture.quartzBottom),
				new SimpleTexture("textures/blocks/quartzblock_bottom", Texture.quartzBottom)));
		allTextures.put("quartz_block_chiseled", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_chiseled", Texture.quartzChiseled),
				new SimpleTexture("textures/blocks/quartzblock_chiseled", Texture.quartzChiseled)));
		allTextures.put("quartz_block_chiseled_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_chiseled_top", Texture.quartzChiseledTop),
				new SimpleTexture("textures/blocks/quartzblock_chiseled_top", Texture.quartzChiseledTop)));
		allTextures.put("quartz_block_lines", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_lines", Texture.quartzPillar),
				new SimpleTexture("textures/blocks/quartzblock_lines", Texture.quartzPillar)));
		allTextures.put("quartz_block_lines_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/quartz_block_lines_top", Texture.quartzPillarTop),
				new SimpleTexture("textures/blocks/quartzblock_lines_top", Texture.quartzPillarTop)));
		allTextures.put("dropper_front_horizontal", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/dropper_front_horizontal", Texture.dropperFront),
				new SimpleTexture("textures/blocks/dropper_front", Texture.dropperFront)));
		// TODO vertical dropper
		allTextures.put("rail_activator", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/rail_activator", Texture.activatorRail),
				new SimpleTexture("textures/blocks/activatorRail", Texture.activatorRail)));
		allTextures.put("rail_activator_powered", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/rail_activator_powered", Texture.activatorRailPowered),
				new SimpleTexture("textures/blocks/activatorRail_powered", Texture.activatorRailPowered)));
		allTextures.put("daylight_detector_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_top", Texture.daylightDetectorTop),
				new SimpleTexture("textures/blocks/daylightDetector_top", Texture.daylightDetectorTop)));
		allTextures.put("daylight_detector_side", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/daylight_detector_side", Texture.daylightDetectorSide),
				new SimpleTexture("textures/blocks/daylightDetector_side", Texture.daylightDetectorSide)));
		allTextures.put("comparator_off", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/comparator_off", Texture.comparatorOff),
				new SimpleTexture("textures/blocks/comparator", Texture.comparatorOff)));
		allTextures.put("comparator_on", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/comparator_on", Texture.comparatorOn),
				new SimpleTexture("textures/blocks/comparator_lit", Texture.comparatorOn)));
		allTextures.put("hopper_outside", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/hopper_outside", Texture.hopperOutside),
				new SimpleTexture("textures/blocks/hopper", Texture.hopperOutside)));
		allTextures.put("hopper_inside", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/hopper_inside", Texture.hopperInside),
				new SimpleTexture("textures/blocks/hopper_inside", Texture.hopperInside)));
		// TODO hopper top

		// MC 1.6
		allTextures.put("hay_block_side",
				new SimpleTexture("assets/minecraft/textures/blocks/hay_block_side", Texture.hayBlockSide));
		allTextures.put("hay_block_top",
				new SimpleTexture("assets/minecraft/textures/blocks/hay_block_top", Texture.hayBlockTop));
		allTextures.put("hardened_clay",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay", Texture.hardenedClay));
		allTextures.put("coal_block",
				new SimpleTexture("assets/minecraft/textures/blocks/coal_block", Texture.coalBlock));
		allTextures.put("hardened_clay_stained_black",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_black", Texture.blackClay));
		allTextures.put("hardened_clay_stained_blue",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_blue", Texture.blueClay));
		allTextures.put("hardened_clay_stained_brown",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_brown", Texture.brownClay));
		allTextures.put("hardened_clay_stained_cyan",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_cyan", Texture.cyanClay));
		allTextures.put("hardened_clay_stained_gray",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_gray", Texture.grayClay));
		allTextures.put("hardened_clay_stained_green",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_green", Texture.greenClay));
		allTextures.put("hardened_clay_stained_light_blue",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_light_blue", Texture.lightBlueClay));
		allTextures.put("hardened_clay_stained_lime",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_lime", Texture.limeClay));
		allTextures.put("hardened_clay_stained_magenta",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_magenta", Texture.magentaClay));
		allTextures.put("hardened_clay_stained_orange",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_orange", Texture.orangeClay));
		allTextures.put("hardened_clay_stained_pink",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_pink", Texture.pinkClay));
		allTextures.put("hardened_clay_stained_purple",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_purple", Texture.purpleClay));
		allTextures.put("hardened_clay_stained_red",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_red", Texture.redClay));
		allTextures.put("hardened_clay_stained_silver",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_silver", Texture.lightGrayClay));
		allTextures.put("hardened_clay_stained_white",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_white", Texture.whiteClay));
		allTextures.put("hardened_clay_stained_yellow",
				new SimpleTexture("assets/minecraft/textures/blocks/hardened_clay_stained_yellow", Texture.yellowClay));

		// Birch Leaf [MC ?]
		allTextures.put("leaves_birch", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/leaves_birch", Texture.birchLeaves),
				new IndexedTexture(0xC4, Texture.birchLeaves)));

		// [MC 1.7.2] Stained glass blocks
		allTextures.put("glass_black",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_black", Texture.blackGlass));
		allTextures.put("glass_blue",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_blue", Texture.blueGlass));
		allTextures.put("glass_brown",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_brown", Texture.brownGlass));
		allTextures.put("glass_cyan",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_cyan", Texture.cyanGlass));
		allTextures.put("glass_gray",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_gray", Texture.grayGlass));
		allTextures.put("glass_green",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_green", Texture.greenGlass));
		allTextures.put("glass_light_blue",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_light_blue", Texture.lightBlueGlass));
		allTextures.put("glass_lime",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_lime", Texture.limeGlass));
		allTextures.put("glass_magenta",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_magenta", Texture.magentaGlass));
		allTextures.put("glass_orange",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_orange", Texture.orangeGlass));
		allTextures.put("glass_pink",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pink", Texture.pinkGlass));
		allTextures.put("glass_purple",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_purple", Texture.purpleGlass));
		allTextures.put("glass_red",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_red", Texture.redGlass));
		allTextures.put("glass_silver",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_silver", Texture.lightGrayGlass));
		allTextures.put("glass_white",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_white", Texture.whiteGlass));
		allTextures.put("glass_yellow",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_yellow", Texture.yellowGlass));

		// [MC 1.7.2] Stained glass panes
		allTextures.put("glass_pane_top_black",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_black", Texture.blackGlassPaneSide));
		allTextures.put("glass_pane_top_blue",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_blue", Texture.blueGlassPaneSide));
		allTextures.put("glass_pane_top_brown",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_brown", Texture.brownGlassPaneSide));
		allTextures.put("glass_pane_top_cyan",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_cyan", Texture.cyanGlassPaneSide));
		allTextures.put("glass_pane_top_gray",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_gray", Texture.grayGlassPaneSide));
		allTextures.put("glass_pane_top_green",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_green", Texture.greenGlassPaneSide));
		allTextures.put("glass_pane_top_light_blue",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_light_blue", Texture.lightBlueGlassPaneSide));
		allTextures.put("glass_pane_top_lime",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_lime", Texture.limeGlassPaneSide));
		allTextures.put("glass_pane_top_magenta",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_magenta", Texture.magentaGlassPaneSide));
		allTextures.put("glass_pane_top_orange",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_orange", Texture.orangeGlassPaneSide));
		allTextures.put("glass_pane_top_pink",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_pink", Texture.pinkGlassPaneSide));
		allTextures.put("glass_pane_top_purple",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_purple", Texture.purpleGlassPaneSide));
		allTextures.put("glass_pane_top_red",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_red", Texture.redGlassPaneSide));
		allTextures.put("glass_pane_top_silver",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_silver", Texture.lightGrayGlassPaneSide));
		allTextures.put("glass_pane_top_white",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_white", Texture.whiteGlassPaneSide));
		allTextures.put("glass_pane_top_yellow",
				new SimpleTexture("assets/minecraft/textures/blocks/glass_pane_top_yellow", Texture.yellowGlassPaneSide));

		// [MC 1.7.2] Top/bottom log textures
		allTextures.put("log_spruce_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_spruce_top", Texture.spruceWoodTop),
				new IndexedTexture(0x15, Texture.spruceWoodTop)));
		allTextures.put("log_birch_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_birch_top", Texture.birchWoodTop),
				new IndexedTexture(0x15, Texture.spruceWoodTop)));
		allTextures.put("log_jungle_top", new AlternateTextures(
				new SimpleTexture("assets/minecraft/textures/blocks/log_jungle_top", Texture.jungleTreeTop),
				new IndexedTexture(0x15, Texture.jungleTreeTop)));

		// [MC 1.7.2] Podzol
		allTextures.put("dirt_podzol_side",
				new SimpleTexture("assets/minecraft/textures/blocks/dirt_podzol_side", Texture.podzolSide));
		allTextures.put("dirt_podzol_top",
				new SimpleTexture("assets/minecraft/textures/blocks/dirt_podzol_top", Texture.podzolTop));

		// [MC 1.7.2] Acacia, Dark Oak
		allTextures.put("log_acacia",
				new SimpleTexture("assets/minecraft/textures/blocks/log_acacia", Texture.acaciaWood));
		allTextures.put("log_big_oak",
				new SimpleTexture("assets/minecraft/textures/blocks/log_big_oak", Texture.darkOakWood));
		allTextures.put("log_acacia_top",
				new SimpleTexture("assets/minecraft/textures/blocks/log_acacia_top", Texture.acaciaWoodTop));
		allTextures.put("log_big_oak_top",
				new SimpleTexture("assets/minecraft/textures/blocks/log_big_oak_top", Texture.darkOakWoodTop));
		allTextures.put("leaves_acacia",
				new SimpleTexture("assets/minecraft/textures/blocks/leaves_acacia", Texture.acaciaLeaves));
		allTextures.put("leaves_big_oak",
				new SimpleTexture("assets/minecraft/textures/blocks/leaves_big_oak", Texture.darkOakLeaves));
		allTextures.put("sapling_acacia",
				new SimpleTexture("assets/minecraft/textures/blocks/sapling_acacia", Texture.acaciaSapling));
		allTextures.put("sapling_roofed_oak",
				new SimpleTexture("assets/minecraft/textures/blocks/sapling_roofed_oak", Texture.darkOakSapling));
		allTextures.put("planks_acacia",
				new SimpleTexture("assets/minecraft/textures/blocks/planks_acacia", Texture.acaciaPlanks));
		allTextures.put("planks_big_oak",
				new SimpleTexture("assets/minecraft/textures/blocks/planks_big_oak", Texture.darkOakPlanks));

		// [MC 1.7.2] Packed Ice
		allTextures.put("ice_packed",
				new SimpleTexture("assets/minecraft/textures/blocks/ice_packed", Texture.packedIce));

		// [MC 1.7.2] Red Sand
		allTextures.put("red_sand",
				new SimpleTexture("assets/minecraft/textures/blocks/red_sand", Texture.redSand));

		// [MC 1.7.2] Flowers
		allTextures.put("flower_allium",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_allium", Texture.allium));
		allTextures.put("flower_blue_orchid",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_blue_orchid", Texture.blueOrchid));
		allTextures.put("flower_houstonia",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_houstonia", Texture.azureBluet));
		allTextures.put("flower_oxeye_daisy",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_oxeye_daisy", Texture.oxeyeDaisy));
		allTextures.put("flower_tulip_red",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_red", Texture.redTulip));
		allTextures.put("flower_tulip_orange",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_orange", Texture.orangeTulip));
		allTextures.put("flower_tulip_white",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_white", Texture.whiteTulip));
		allTextures.put("flower_tulip_pink",
				new SimpleTexture("assets/minecraft/textures/blocks/flower_tulip_pink", Texture.pinkTulip));
		allTextures.put("double_plant_fern_bottom",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_fern_bottom", Texture.largeFernBottom));
		allTextures.put("double_plant_fern_top",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_fern_top", Texture.largeFernTop));
		allTextures.put("double_plant_grass_bottom",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_grass_bottom", Texture.doubleTallGrassBottom));
		allTextures.put("double_plant_grass_top",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_grass_top", Texture.doubleTallGrassTop));
		allTextures.put("double_plant_paeonia_bottom",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_paeonia_bottom", Texture.peonyBottom));
		allTextures.put("double_plant_paeonia_top",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_paeonia_top", Texture.peonyTop));
		allTextures.put("double_plant_rose_bottom",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_rose_bottom", Texture.roseBushBottom));
		allTextures.put("double_plant_rose_top",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_rose_top", Texture.roseBushTop));
		allTextures.put("double_plant_sunflower_bottom",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_bottom", Texture.sunflowerBottom));
		allTextures.put("double_plant_sunflower_top",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_top", Texture.sunflowerTop));
		allTextures.put("double_plant_sunflower_front",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_front", Texture.sunflowerFront));
		allTextures.put("double_plant_sunflower_back",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_sunflower_back", Texture.sunflowerBack));
		allTextures.put("double_plant_syringa_bottom",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_syringa_bottom", Texture.lilacBottom));
		allTextures.put("double_plant_syringa_top",
				new SimpleTexture("assets/minecraft/textures/blocks/double_plant_syringa_top", Texture.lilacTop));

		// [MC 1.8] New Stone Blocks
		allTextures.put("stone_diorite",
				new SimpleTexture("assets/minecraft/textures/blocks/stone_diorite", Texture.diorite));
		allTextures.put("stone_diorite_smooth",
				new SimpleTexture("assets/minecraft/textures/blocks/stone_diorite_smooth", Texture.smoothDiorite));
		allTextures.put("stone_granite",
				new SimpleTexture("assets/minecraft/textures/blocks/stone_granite", Texture.granite));
		allTextures.put("stone_granite_smooth",
				new SimpleTexture("assets/minecraft/textures/blocks/stone_granite_smooth", Texture.smoothGranite));
		allTextures.put("stone_andesite",
				new SimpleTexture("assets/minecraft/textures/blocks/stone_andesite", Texture.andesite));
		allTextures.put("stone_andesite_smooth",
				new SimpleTexture("assets/minecraft/textures/blocks/stone_andesite_smooth", Texture.smoothAndesite));

		// [14w07a] Iron Trapdoor
		allTextures.put("iron_trapdoor",
				new SimpleTexture("assets/minecraft/textures/blocks/iron_trapdoor", Texture.ironTrapdoor));

		// coarse dirt
		allTextures.put("coarse_dirt",
				new SimpleTexture("assets/minecraft/textures/blocks/coarse_dirt", Texture.coarseDirt));
	}

	/**
	 * Attempt to load the specified texture pack.
	 * If some textures files are not found they will be loaded from
	 * the default texture pack.
	 * @param tpFile
	 * @param rememberTP Decides if this texture pack should be saved as the
	 * last used texture pack
	 */
	public static void loadTexturePack(File tpFile, boolean rememberTP) throws TextureLoadingError {
		if (tpFile == null || !tpFile.isFile()) {
			throw new TextureLoadingError();
		}
		logger.info("Loading textures from " + tpFile.getAbsolutePath());
		loadTexturePack(tpFile, allTextures.keySet(), rememberTP);
	}

	private static void loadTexturePack(File tpFile,
			Collection<String> toLoad, boolean rememberTP) {

		File defaultTP = MinecraftFinder.getMinecraftJar();
		boolean isDefault = tpFile.equals(defaultTP);
		String tpName = isDefault ? "default texture pack"
				: "texture pack";
		tpName += " (" + tpFile.getAbsolutePath() + ")";

		Set<String> notLoaded = new HashSet<String>(toLoad);

		ZipFile texturePack = null;
		try {
			texturePack = new ZipFile(tpFile);
			for (String id: toLoad) {
				TextureRef tex = allTextures.get(id);
				if (tex.load(texturePack)) {
					notLoaded.remove(id);
				}
			}

			// fall back on terrain.png
			loadTerrainTextures(texturePack, notLoaded);

			if (rememberTP) {
				PersistentSettings.setLastTexturePack(tpFile.getAbsolutePath());
			}
		} catch (IOException e) {
			logger.warn("Failed to open " + tpName + ": " + e.getMessage());
		} finally {
			if (texturePack != null) {
				try {
					texturePack.close();
				} catch (IOException e) {
				}
			}
		}

		if (!notLoaded.isEmpty()) {
			StringBuffer msg = new StringBuffer();
			msg.append("Failed to load textures from " + tpName + ":\n");
			Iterator<String> iter = notLoaded.iterator();
			for (int count = 0; iter.hasNext() && count < 10; ++count) {
				msg.append("\t");
				msg.append(iter.next());
				msg.append("\n");
			}
			if (notLoaded.size() > 10) {
				msg.append("\t... plus " + (notLoaded.size()-10) + " more");
			}
			logger.info(msg.toString());

			if (!isDefault) {
				// fall back on default TP
				loadTexturePack(defaultTP, notLoaded, false);
			}
		}
	}

	private static void loadTerrainTextures(ZipFile texturePack,
			Set<String> notLoaded) {

		// will mutate notLoaded below
		Collection<String> toLoad = new LinkedList<String>(notLoaded);

		try {
			InputStream in = texturePack.getInputStream(new ZipEntry("terrain.png"));
			if (in != null) {
				BufferedImage spritemap = ImageIO.read(in);
				BufferedImage[] texture = getTerrainTextures(spritemap);

				for (String id: toLoad) {
					TextureRef tex = allTextures.get(id);
					if (notLoaded.contains(tex) && tex.loadFromTerrain(texture)) {
						notLoaded.remove(tex);
					}
				}
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Load a 16x16 spritemap.
	 * @param spritemap
	 * @return A bufferedImage containing the spritemap
	 * @throws IOException if the image dimensions are incorrect
	 */
	private static BufferedImage[] getTerrainTextures(BufferedImage spritemap)
			throws IOException {

		if (spritemap.getWidth() != spritemap.getHeight() ||
				spritemap.getWidth() % 16 != 0) {
			throw new IOException("Error: terrain.png file must have equal width and height, divisible by 16!");
		}

		int imgW = spritemap.getWidth();
		int spriteW = imgW / 16;
		BufferedImage[] tex = new BufferedImage[256];

		for (int i = 0; i < 256; ++i)
			tex[i] = new BufferedImage(spriteW, spriteW,
					BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < imgW; ++y) {
			int sy = y / spriteW;
			for (int x = 0; x < imgW; ++x) {
				int sx = x / spriteW;
				BufferedImage texture = tex[sx + sy * 16];
				texture.setRGB(x % spriteW, y % spriteW, spritemap.getRGB(x, y));
			}
		}
		return tex;
	}
}
