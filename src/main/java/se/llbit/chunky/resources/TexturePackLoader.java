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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import se.llbit.chunky.main.Chunky;
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
import se.llbit.util.ProgramProperties;

/**
 * Utility methods to load Minecraft texture packs.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturePackLoader {

	private static final Logger logger =
			Logger.getLogger(TexturePackLoader.class);

	private static Map<String, TextureRef> allTextures =
		new HashMap<String, TextureRef>();

	static {
		allTextures.put("normal chest",
				new ChestTexture("item/chest", Texture.chestLock,
					Texture.chestTop, Texture.chestBottom, Texture.chestLeft,
					Texture.chestRight, Texture.chestFront, Texture.chestBack));
		allTextures.put("ender chest",
				new ChestTexture("item/enderchest",
					Texture.enderChestLock, Texture.enderChestTop,
					Texture.enderChestBottom, Texture.enderChestLeft,
					Texture.enderChestRight, Texture.enderChestFront,
					Texture.enderChestBack));
		allTextures.put("normal double chest",
				new LargeChestTexture("item/largechest"));
		allTextures.put("sun",
				new AlternateTextures(
					new SunTexture("environment/sun"),
					new SunTexture("terrain/sun")));
		allTextures.put("sign", new SignTexture("item/sign"));
		allTextures.put("clouds", new CloudsTexture("environment/clouds"));
		allTextures.put("grass color map", new GrassColorTexture("misc/grasscolor"));
		allTextures.put("foliage color map", new FoliageColorTexture("misc/foliagecolor"));

		allTextures.put("grass top", new AlternateTextures(
				new SimpleTexture("textures/blocks/grass_top", Texture.grassTop),
				new IndexedTexture(0x00, Texture.grassTop)));
		allTextures.put("stone", new AlternateTextures(
				new SimpleTexture("textures/blocks/stone", Texture.stone),
				new IndexedTexture(0x01, Texture.stone)));
		allTextures.put("dirt", new AlternateTextures(
				new SimpleTexture("textures/blocks/dirt", Texture.dirt),
				new IndexedTexture(0x02, Texture.dirt)));
		allTextures.put("grass side", new AlternateTextures(
				new SimpleTexture("textures/blocks/grass_side", Texture.grassSideSaturated),
				new IndexedTexture(0x03, Texture.grassSideSaturated)));
		allTextures.put("oak planks", new AlternateTextures(
				new SimpleTexture("textures/blocks/wood", Texture.oakPlanks),
				new IndexedTexture(0x04, Texture.oakPlanks)));
		allTextures.put("stone slab side", new AlternateTextures(
				new SimpleTexture("textures/blocks/stoneslab_side", Texture.slabSide),
				new IndexedTexture(0x05, Texture.slabSide)));
		allTextures.put("stone slab top", new AlternateTextures(
				new SimpleTexture("textures/blocks/stoneslab_top", Texture.slabTop),
				new IndexedTexture(0x06, Texture.slabTop)));
		allTextures.put("brick", new AlternateTextures(
				new SimpleTexture("textures/blocks/brick", Texture.brick),
				new IndexedTexture(0x07, Texture.brick)));
		allTextures.put("tnt side", new AlternateTextures(
				new SimpleTexture("textures/blocks/tnt_side", Texture.tntSide),
				new IndexedTexture(0x08, Texture.tntSide)));
		allTextures.put("tnt top", new AlternateTextures(
				new SimpleTexture("textures/blocks/tnt_top", Texture.tntTop),
				new IndexedTexture(0x09, Texture.tntTop)));
		allTextures.put("tnt bottom", new AlternateTextures(
				new SimpleTexture("textures/blocks/tnt_bottom", Texture.tntBottom),
				new IndexedTexture(0x0A, Texture.tntBottom)));
		allTextures.put("cobweb", new AlternateTextures(
				new SimpleTexture("textures/blocks/web", Texture.cobweb),
				new IndexedTexture(0x0B, Texture.cobweb)));
		allTextures.put("rose", new AlternateTextures(
				new SimpleTexture("textures/blocks/rose", Texture.redRose),
				new IndexedTexture(0x0C, Texture.redRose)));
		allTextures.put("dandelion", new AlternateTextures(
				new SimpleTexture("textures/blocks/flower", Texture.yellowFlower),
				new IndexedTexture(0x0D, Texture.yellowFlower)));
		allTextures.put("nether portal", new AlternateTextures(
				new SimpleTexture("textures/blocks/portal", Texture.portal),
				new IndexedTexture(0x0E, Texture.portal)));
		allTextures.put("oak sapling", new AlternateTextures(
				new SimpleTexture("textures/blocks/sapling", Texture.oakSapling),
				new IndexedTexture(0x0F, Texture.oakSapling)));

		allTextures.put("stone brick", new AlternateTextures(
				new SimpleTexture("textures/blocks/stonebrick", Texture.cobblestone),
				new IndexedTexture(0x10, Texture.cobblestone)));
		allTextures.put("bedrock", new AlternateTextures(
				new SimpleTexture("textures/blocks/bedrock", Texture.bedrock),
				new IndexedTexture(0x11, Texture.bedrock)));
		allTextures.put("sand", new AlternateTextures(
				new SimpleTexture("textures/blocks/sand", Texture.sand),
				new IndexedTexture(0x12, Texture.sand)));
		allTextures.put("gravel", new AlternateTextures(
				new SimpleTexture("textures/blocks/gravel", Texture.gravel),
				new IndexedTexture(0x13, Texture.gravel)));
		allTextures.put("tree bark", new AlternateTextures(
				new SimpleTexture("textures/blocks/tree_side", Texture.oakWood),
				new IndexedTexture(0x14, Texture.oakWood)));
		allTextures.put("tree log", new AlternateTextures(
				new SimpleTexture("textures/blocks/tree_top", Texture.woodTop),
				new IndexedTexture(0x15, Texture.woodTop)));
		allTextures.put("iron block", new AlternateTextures(
				new SimpleTexture("textures/blocks/blockIron", Texture.ironBlock),
				new IndexedTexture(0x16, Texture.ironBlock)));
		allTextures.put("blockGold", new AlternateTextures(
				new SimpleTexture("textures/blocks/blockGold", Texture.goldBlock),
				new IndexedTexture(0x17, Texture.goldBlock)));
		allTextures.put("blockDiamond", new AlternateTextures(
				new SimpleTexture("textures/blocks/blockDiamond", Texture.diamondBlock),
				new IndexedTexture(0x18, Texture.diamondBlock)));
		allTextures.put("blockEmerald", new AlternateTextures(
				new SimpleTexture("textures/blocks/blockEmerald", Texture.emeraldBlock),
				new IndexedTexture(0x19, Texture.emeraldBlock)));
		allTextures.put("blockRedstone", new AlternateTextures(
				new SimpleTexture("textures/blocks/blockRedstone", Texture.redstoneBlock),
				new IndexedTexture(0x1A, Texture.redstoneBlock)));
		allTextures.put("mushroom_red", new AlternateTextures(
				new SimpleTexture("textures/blocks/mushroom_red", Texture.redMushroom),
				new IndexedTexture(0x1C, Texture.redMushroom)));
		allTextures.put("mushroom_brown", new AlternateTextures(
				new SimpleTexture("textures/blocks/mushroom_brown", Texture.brownMushroom),
				new IndexedTexture(0x1D, Texture.brownMushroom)));
		allTextures.put("sapling_jungle", new AlternateTextures(
				new SimpleTexture("textures/blocks/sapling_jungle", Texture.jungleTreeSapling),
				new IndexedTexture(0x1E, Texture.jungleTreeSapling)));

		allTextures.put("oreGold", new AlternateTextures(
				new SimpleTexture("textures/blocks/oreGold", Texture.goldOre),
				new IndexedTexture(0x20, Texture.goldOre)));
		allTextures.put("oreIron", new AlternateTextures(
				new SimpleTexture("textures/blocks/oreIron", Texture.ironOre),
				new IndexedTexture(0x21, Texture.ironOre)));
		allTextures.put("oreCoal", new AlternateTextures(
				new SimpleTexture("textures/blocks/oreCoal", Texture.coalOre),
				new IndexedTexture(0x22, Texture.coalOre)));
		allTextures.put("bookshelf", new AlternateTextures(
				new SimpleTexture("textures/blocks/bookshelf", Texture.bookshelf),
				new IndexedTexture(0x23, Texture.bookshelf)));
		allTextures.put("stoneMoss", new AlternateTextures(
				new SimpleTexture("textures/blocks/stoneMoss", Texture.mossStone),
				new IndexedTexture(0x24, Texture.mossStone)));
		allTextures.put("obsidian", new AlternateTextures(
				new SimpleTexture("textures/blocks/obsidian", Texture.obsidian),
				new IndexedTexture(0x25, Texture.obsidian)));
		allTextures.put("grass_side_overlay", new AlternateTextures(
				new SimpleTexture("textures/blocks/grass_side_overlay", Texture.grassSide),
				new IndexedTexture(0x26, Texture.grassSide)));
		allTextures.put("tallgrass", new AlternateTextures(
				new SimpleTexture("textures/blocks/tallgrass", Texture.tallGrass),
				new IndexedTexture(0x27, Texture.tallGrass)));
		allTextures.put("beacon", new AlternateTextures(
				new SimpleTexture("textures/blocks/beacon", Texture.beacon),
				new IndexedTexture(0x29, Texture.beacon)));
		allTextures.put("workbench_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/workbench_top", Texture.workbenchTop),
				new IndexedTexture(0x2B, Texture.workbenchTop)));
		allTextures.put("furnace_front", new AlternateTextures(
				new SimpleTexture("textures/blocks/furnace_front", Texture.furnaceUnlitFront),
				new IndexedTexture(0x2C, Texture.furnaceUnlitFront)));
		allTextures.put("furnace_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/furnace_side", Texture.furnaceSide),
				new IndexedTexture(0x2D, Texture.furnaceSide)));
		allTextures.put("dispenser_front", new AlternateTextures(
				new SimpleTexture("textures/blocks/dispenser_front", Texture.dispenserFront),
				new IndexedTexture(0x2E, Texture.dispenserFront)));

		allTextures.put("sponge", new AlternateTextures(
				new SimpleTexture("textures/blocks/sponge", Texture.sponge),
				new IndexedTexture(0x30, Texture.sponge)));
		allTextures.put("glass", new AlternateTextures(
				new SimpleTexture("textures/blocks/glass", Texture.glass),
				new IndexedTexture(0x31, Texture.glass)));
		allTextures.put("oreDiamond", new AlternateTextures(
				new SimpleTexture("textures/blocks/oreDiamond", Texture.diamondOre),
				new IndexedTexture(0x32, Texture.diamondOre)));
		allTextures.put("oreRedstone", new AlternateTextures(
				new SimpleTexture("textures/blocks/oreRedstone", Texture.redstoneOre),
				new IndexedTexture(0x33, Texture.redstoneOre)));
		allTextures.put("leaves", new AlternateTextures(
				new SimpleTexture("textures/blocks/leaves", Texture.oakLeaves),
				new IndexedTexture(0x34, Texture.oakLeaves)));
		allTextures.put("stonebricksmooth", new AlternateTextures(
				new SimpleTexture("textures/blocks/stonebricksmooth", Texture.stoneBrick),
				new IndexedTexture(0x36, Texture.stoneBrick)));
		allTextures.put("deadbush", new AlternateTextures(
				new SimpleTexture("textures/blocks/deadbush", Texture.deadBush),
				new IndexedTexture(0x37, Texture.deadBush)));
		allTextures.put("fern", new AlternateTextures(
				new SimpleTexture("textures/blocks/fern", Texture.fern),
				new IndexedTexture(0x38, Texture.fern)));
		allTextures.put("workbench_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/workbench_side", Texture.workbenchSide),
				new IndexedTexture(0x3B, Texture.workbenchSide)));
		allTextures.put("workbench_front", new AlternateTextures(
				new SimpleTexture("textures/blocks/workbench_front", Texture.workbenchFront),
				new IndexedTexture(0x3C, Texture.workbenchFront)));
		allTextures.put("furnace_front_lit", new AlternateTextures(
				new SimpleTexture("textures/blocks/furnace_front_lit", Texture.furnaceLitFront),
				new IndexedTexture(0x3D, Texture.furnaceLitFront)));
		allTextures.put("furnace_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/furnace_top", Texture.furnaceTop),
				new IndexedTexture(0x3E, Texture.furnaceTop)));
		allTextures.put("sapling_spruce", new AlternateTextures(
				new SimpleTexture("textures/blocks/sapling_spruce", Texture.spruceSapling),
				new IndexedTexture(0x3F, Texture.spruceSapling)));

		allTextures.put("cloth_0", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_0", Texture.whiteWool),
				new IndexedTexture(0x40, Texture.whiteWool)));
		allTextures.put("mobSpawner", new AlternateTextures(
				new SimpleTexture("textures/blocks/mobSpawner", Texture.monsterSpawner),
				new IndexedTexture(0x41, Texture.monsterSpawner)));
		allTextures.put("snow", new AlternateTextures(
				new SimpleTexture("textures/blocks/snow", Texture.snowBlock),
				new IndexedTexture(0x42, Texture.snowBlock)));
		allTextures.put("ice", new AlternateTextures(
				new SimpleTexture("textures/blocks/ice", Texture.ice),
				new IndexedTexture(0x43, Texture.ice)));
		allTextures.put("snow_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/snow_side", Texture.snowSide),
				new IndexedTexture(0x44, Texture.snowSide)));
		allTextures.put("cactus_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/cactus_top", Texture.cactusTop),
				new IndexedTexture(0x45, Texture.cactusTop)));
		allTextures.put("cactus_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/cactus_side", Texture.cactusSide),
				new IndexedTexture(0x46, Texture.cactusSide)));
		allTextures.put("cactus_bottom", new AlternateTextures(
				new SimpleTexture("textures/blocks/cactus_bottom", Texture.cactusBottom),
				new IndexedTexture(0x47, Texture.cactusBottom)));
		allTextures.put("clay", new AlternateTextures(
				new SimpleTexture("textures/blocks/clay", Texture.clay),
				new IndexedTexture(0x48, Texture.clay)));
		allTextures.put("reeds", new AlternateTextures(
				new SimpleTexture("textures/blocks/reeds", Texture.sugarCane),
				new IndexedTexture(0x49, Texture.sugarCane)));
		allTextures.put("musicBlock", new AlternateTextures(
				new SimpleTexture("textures/blocks/musicBlock", Texture.jukeboxSide),
				new IndexedTexture(0x4A, Texture.jukeboxSide)));
		allTextures.put("jukebox_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/jukebox_top", Texture.jukeboxTop),
				new IndexedTexture(0x4B, Texture.jukeboxTop)));
		allTextures.put("waterlily", new AlternateTextures(
				new SimpleTexture("textures/blocks/waterlily", Texture.lilyPad),
				new IndexedTexture(0x4C, Texture.lilyPad)));
		allTextures.put("mycel_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/mycel_side", Texture.myceliumSide),
				new IndexedTexture(0x4D, Texture.myceliumSide)));
		allTextures.put("mycel_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/mycel_top", Texture.myceliumTop),
				new IndexedTexture(0x4E, Texture.myceliumTop)));
		allTextures.put("sapling_birch", new AlternateTextures(
				new SimpleTexture("textures/blocks/sapling_birch", Texture.birchSapling),
				new IndexedTexture(0x4F, Texture.birchSapling)));

		allTextures.put("torch", new AlternateTextures(
				new SimpleTexture("textures/blocks/torch", Texture.torch),
				new IndexedTexture(0x50, Texture.torch)));
		allTextures.put("doorWood_upper", new AlternateTextures(
				new SimpleTexture("textures/blocks/doorWood_upper", Texture.woodenDoorTop),
				new IndexedTexture(0x51, Texture.woodenDoorTop)));
		allTextures.put("doorIron_upper", new AlternateTextures(
				new SimpleTexture("textures/blocks/doorIron_upper", Texture.ironDoorTop),
				new IndexedTexture(0x52, Texture.ironDoorTop)));
		allTextures.put("ladder", new AlternateTextures(
				new SimpleTexture("textures/blocks/ladder", Texture.ladder),
				new IndexedTexture(0x53, Texture.ladder)));
		allTextures.put("trapdoor", new AlternateTextures(
				new SimpleTexture("textures/blocks/trapdoor", Texture.trapdoor),
				new IndexedTexture(0x54, Texture.trapdoor)));
		allTextures.put("fenceIron", new AlternateTextures(
				new SimpleTexture("textures/blocks/fenceIron", Texture.ironBars),
				new IndexedTexture(0x55, Texture.ironBars)));
		allTextures.put("farmland_wet", new AlternateTextures(
				new SimpleTexture("textures/blocks/farmland_wet", Texture.farmlandWet),
				new IndexedTexture(0x56, Texture.farmlandWet)));
		allTextures.put("farmland_dry", new AlternateTextures(
				new SimpleTexture("textures/blocks/farmland_dry", Texture.farmlandDry),
				new IndexedTexture(0x57, Texture.farmlandDry)));
		allTextures.put("crops_0", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_0", Texture.crops0),
				new IndexedTexture(0x58, Texture.crops0)));
		allTextures.put("crops_1", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_1", Texture.crops1),
				new IndexedTexture(0x59, Texture.crops1)));
		allTextures.put("crops_2", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_2", Texture.crops2),
				new IndexedTexture(0x5A, Texture.crops2)));
		allTextures.put("crops_3", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_3", Texture.crops3),
				new IndexedTexture(0x5B, Texture.crops3)));
		allTextures.put("crops_4", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_4", Texture.crops4),
				new IndexedTexture(0x5C, Texture.crops4)));
		allTextures.put("crops_5", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_5", Texture.crops5),
				new IndexedTexture(0x5D, Texture.crops5)));
		allTextures.put("crops_6", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_6", Texture.crops6),
				new IndexedTexture(0x5E, Texture.crops6)));
		allTextures.put("crops_7", new AlternateTextures(
				new SimpleTexture("textures/blocks/crops_7", Texture.crops7),
				new IndexedTexture(0x5F, Texture.crops7)));

		allTextures.put("lever", new AlternateTextures(
				new SimpleTexture("textures/blocks/lever", Texture.lever),
				new IndexedTexture(0x60, Texture.lever)));
		allTextures.put("doorWood_lower", new AlternateTextures(
				new SimpleTexture("textures/blocks/doorWood_lower", Texture.woodenDoorBottom),
				new IndexedTexture(0x61, Texture.woodenDoorBottom)));
		allTextures.put("doorIron_lower", new AlternateTextures(
				new SimpleTexture("textures/blocks/doorIron_lower", Texture.ironDoorBottom),
				new IndexedTexture(0x62, Texture.ironDoorBottom)));
		allTextures.put("redtorch_lit", new AlternateTextures(
				new SimpleTexture("textures/blocks/redtorch_lit", Texture.redstoneTorchOn),
				new IndexedTexture(0x63, Texture.redstoneTorchOn)));
		allTextures.put("stonebricksmooth_mossy", new AlternateTextures(
				new SimpleTexture("textures/blocks/stonebricksmooth_mossy", Texture.mossyStoneBrick),
				new IndexedTexture(0x64, Texture.mossyStoneBrick)));
		allTextures.put("stonebricksmooth_cracked", new AlternateTextures(
				new SimpleTexture("textures/blocks/stonebricksmooth_cracked", Texture.crackedStoneBrick),
				new IndexedTexture(0x65, Texture.crackedStoneBrick)));
		allTextures.put("pumpkin_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/pumpkin_top", Texture.pumpkinTop),
				new IndexedTexture(0x66, Texture.pumpkinTop)));
		allTextures.put("hellrock", new AlternateTextures(
				new SimpleTexture("textures/blocks/hellrock", Texture.netherrack),
				new IndexedTexture(0x67, Texture.netherrack)));
		allTextures.put("hellsand", new AlternateTextures(
				new SimpleTexture("textures/blocks/hellsand", Texture.soulsand),
				new IndexedTexture(0x68, Texture.soulsand)));
		allTextures.put("lightgem", new AlternateTextures(
				new SimpleTexture("textures/blocks/lightgem", Texture.glowstone),
				new IndexedTexture(0x69, Texture.glowstone)));
		allTextures.put("piston_top_sticky", new AlternateTextures(
				new SimpleTexture("textures/blocks/piston_top_sticky", Texture.pistonTopSticky),
				new IndexedTexture(0x6A, Texture.pistonTopSticky)));
		allTextures.put("piston_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/piston_top", Texture.pistonTop),
				new IndexedTexture(0x6B, Texture.pistonTop)));
		allTextures.put("piston_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/piston_side", Texture.pistonSide),
				new IndexedTexture(0x6C, Texture.pistonSide)));
		allTextures.put("piston_bottom", new AlternateTextures(
				new SimpleTexture("textures/blocks/piston_bottom", Texture.pistonBottom),
				new IndexedTexture(0x6D, Texture.pistonBottom)));
		allTextures.put("piston_inner_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/piston_inner_top", Texture.pistonInnerTop),
				new IndexedTexture(0x6E, Texture.pistonInnerTop)));
		allTextures.put("stem_straight", new AlternateTextures(
				new SimpleTexture("textures/blocks/stem_straight", Texture.stemStraight),
				new IndexedTexture(0x6F, Texture.stemStraight)));

		allTextures.put("rail_turn", new AlternateTextures(
				new SimpleTexture("textures/blocks/rail_turn", Texture.railsCurved),
				new IndexedTexture(0x70, Texture.railsCurved)));
		allTextures.put("cloth_15", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_15", Texture.blackWool),
				new IndexedTexture(0x71, Texture.blackWool)));
		allTextures.put("cloth_7", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_7", Texture.grayWool),
				new IndexedTexture(0x72, Texture.grayWool)));
		allTextures.put("redtorch", new AlternateTextures(
				new SimpleTexture("textures/blocks/redtorch", Texture.redstoneTorchOff),
				new IndexedTexture(0x73, Texture.redstoneTorchOff)));
		allTextures.put("tree_spruce", new AlternateTextures(
				new SimpleTexture("textures/blocks/tree_spruce", Texture.spruceWood),
				new IndexedTexture(0x74, Texture.spruceWood)));
		allTextures.put("tree_birch", new AlternateTextures(
				new SimpleTexture("textures/blocks/tree_birch", Texture.birchWood),
				new IndexedTexture(0x75, Texture.birchWood)));
		allTextures.put("pumpkin_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/pumpkin_side", Texture.pumpkinSide),
				new IndexedTexture(0x76, Texture.pumpkinSide)));
		allTextures.put("pumpkin_face", new AlternateTextures(
				new SimpleTexture("textures/blocks/pumpkin_face", Texture.pumpkinFront),
				new IndexedTexture(0x77, Texture.pumpkinFront)));
		allTextures.put("pumpkin_jack", new AlternateTextures(
				new SimpleTexture("textures/blocks/pumpkin_jack", Texture.jackolanternFront),
				new IndexedTexture(0x78, Texture.jackolanternFront)));
		allTextures.put("cake_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/cake_top", Texture.cakeTop),
				new IndexedTexture(0x79, Texture.cakeTop)));
		allTextures.put("cake_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/cake_side", Texture.cakeSide),
				new IndexedTexture(0x7A, Texture.cakeSide)));
		allTextures.put("cake_inner", new AlternateTextures(
				new SimpleTexture("textures/blocks/cake_inner", Texture.cakeInside),
				new IndexedTexture(0x7B, Texture.cakeInside)));
		allTextures.put("cake_bottom", new AlternateTextures(
				new SimpleTexture("textures/blocks/cake_bottom", Texture.cakeBottom),
				new IndexedTexture(0x7C, Texture.cakeBottom)));
		allTextures.put("mushroom_skin_red", new AlternateTextures(
				new SimpleTexture("textures/blocks/mushroom_skin_red", Texture.hugeRedMushroom),
				new IndexedTexture(0x7D, Texture.hugeRedMushroom)));
		allTextures.put("mushroom_skin_brown", new AlternateTextures(
				new SimpleTexture("textures/blocks/mushroom_skin_brown", Texture.hugeBrownMushroom),
				new IndexedTexture(0x7E, Texture.hugeBrownMushroom)));
		allTextures.put("stem_bent", new AlternateTextures(
				new SimpleTexture("textures/blocks/stem_bent", Texture.stemBent),
				new IndexedTexture(0x7F, Texture.stemBent)));

		allTextures.put("rail", new AlternateTextures(
				new SimpleTexture("textures/blocks/rail", Texture.rails),
				new IndexedTexture(0x80, Texture.rails)));
		allTextures.put("cloth_14", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_14", Texture.redWool),
				new IndexedTexture(0x81, Texture.redWool)));
		allTextures.put("cloth_6", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_6", Texture.pinkWool),
				new IndexedTexture(0x82, Texture.pinkWool)));
		allTextures.put("repeater", new AlternateTextures(
				new SimpleTexture("textures/blocks/repeater", Texture.redstoneRepeaterOff),
				new IndexedTexture(0x83, Texture.redstoneRepeaterOff)));
		allTextures.put("leaves_spruce", new AlternateTextures(
				new SimpleTexture("textures/blocks/leaves_spruce", Texture.spruceLeaves),
				new IndexedTexture(0x84, Texture.spruceLeaves)));
		allTextures.put("bed_feet_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/bed_feet_top", Texture.bedFootTop),
				new IndexedTexture(0x86, Texture.bedFootTop)));
		allTextures.put("bed_head_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/bed_head_top", Texture.bedHeadTop),
				new IndexedTexture(0x87, Texture.bedHeadTop)));
		allTextures.put("melon_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/melon_side", Texture.melonSide),
				new IndexedTexture(0x88, Texture.melonSide)));
		allTextures.put("melon_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/melon_top", Texture.melonTop),
				new IndexedTexture(0x89, Texture.melonTop)));
		allTextures.put("cauldron_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/cauldron_top", Texture.cauldronTop),
				new IndexedTexture(0x8A, Texture.cauldronTop)));
		allTextures.put("cauldron_inner", new AlternateTextures(
				new SimpleTexture("textures/blocks/cauldron_inner", Texture.cauldronInside),
				new IndexedTexture(0x8B, Texture.cauldronInside)));
		allTextures.put("mushroom_skin_stem", new AlternateTextures(
				new SimpleTexture("textures/blocks/mushroom_skin_stem", Texture.mushroomStem),
				new IndexedTexture(0x8D, Texture.mushroomStem)));
		allTextures.put("mushroom_inside", new AlternateTextures(
				new SimpleTexture("textures/blocks/mushroom_inside", Texture.mushroomPores),
				new IndexedTexture(0x8E, Texture.mushroomPores)));
		allTextures.put("vine", new AlternateTextures(
				new SimpleTexture("textures/blocks/vine", Texture.vines),
				new IndexedTexture(0x8F, Texture.vines)));

		allTextures.put("blockLapis", new AlternateTextures(
				new SimpleTexture("textures/blocks/blockLapis", Texture.lapislazuliBlock),
				new IndexedTexture(0x90, Texture.lapislazuliBlock)));
		allTextures.put("cloth_13", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_13", Texture.greenWool),
				new IndexedTexture(0x91, Texture.greenWool)));
		allTextures.put("cloth_5", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_5", Texture.limeWool),
				new IndexedTexture(0x92, Texture.limeWool)));
		allTextures.put("repeater_lit", new AlternateTextures(
				new SimpleTexture("textures/blocks/repeater_lit", Texture.redstoneRepeaterOn),
				new IndexedTexture(0x93, Texture.redstoneRepeaterOn)));
		allTextures.put("thinglass_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/thinglass_top", Texture.glassPaneSide),
				new IndexedTexture(0x94, Texture.glassPaneSide)));
		allTextures.put("bed_feet_end", new AlternateTextures(
				new SimpleTexture("textures/blocks/bed_feet_end", Texture.bedFootEnd),
				new IndexedTexture(0x95, Texture.bedFootEnd)));
		allTextures.put("bed_feet_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/bed_feet_side", Texture.bedFootSide),
				new IndexedTexture(0x96, Texture.bedFootSide)));
		allTextures.put("bed_head_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/bed_head_side", Texture.bedHeadSide),
				new IndexedTexture(0x97, Texture.bedHeadSide)));
		allTextures.put("bed_head_end", new AlternateTextures(
				new SimpleTexture("textures/blocks/bed_head_end", Texture.bedHeadEnd),
				new IndexedTexture(0x98, Texture.bedHeadEnd)));
		allTextures.put("tree_jungle", new AlternateTextures(
				new SimpleTexture("textures/blocks/tree_jungle", Texture.jungleTreeWood),
				new IndexedTexture(0x99, Texture.jungleTreeWood)));
		allTextures.put("cauldron_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/cauldron_side", Texture.cauldronSide),
				new IndexedTexture(0x9A, Texture.cauldronSide)));
		allTextures.put("cauldron_bottom", new AlternateTextures(
				new SimpleTexture("textures/blocks/cauldron_bottom", Texture.cauldronBottom),
				new IndexedTexture(0x9B, Texture.cauldronBottom)));
		allTextures.put("brewingStand_base", new AlternateTextures(
				new SimpleTexture("textures/blocks/brewingStand_base", Texture.brewingStandBase),
				new IndexedTexture(0x9C, Texture.brewingStandBase)));
		allTextures.put("brewingStand", new AlternateTextures(
				new SimpleTexture("textures/blocks/brewingStand", Texture.brewingStandSide),
				new IndexedTexture(0x9D, Texture.brewingStandSide)));
		allTextures.put("endframe_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/endframe_top", Texture.endPortalFrameTop),
				new IndexedTexture(0x9E, Texture.endPortalFrameTop)));
		allTextures.put("endframe_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/endframe_side", Texture.endPortalFrameSide),
				new IndexedTexture(0x9F, Texture.endPortalFrameSide)));

		allTextures.put("oreLapis", new AlternateTextures(
				new SimpleTexture("textures/blocks/oreLapis", Texture.lapislazuliOre),
				new IndexedTexture(0xA0, Texture.lapislazuliOre)));
		allTextures.put("cloth_12", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_12", Texture.brownWool),
				new IndexedTexture(0xA1, Texture.brownWool)));
		allTextures.put("cloth_4", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_4", Texture.yellowWool),
				new IndexedTexture(0xA2, Texture.yellowWool)));
		allTextures.put("goldenRail", new AlternateTextures(
				new SimpleTexture("textures/blocks/goldenRail", Texture.poweredRailOff),
				new IndexedTexture(0xA3, Texture.poweredRailOff)));
		allTextures.put("redstoneDust_cross", new AlternateTextures(
				new SimpleTexture("textures/blocks/redstoneDust_cross", Texture.redstoneWireCross),
				new IndexedTexture(0xA4, Texture.redstoneWireCross)));
		allTextures.put("redstoneDust_line", new AlternateTextures(
				new SimpleTexture("textures/blocks/redstoneDust_line", Texture.redstoneWire),
				new IndexedTexture(0xA5, Texture.redstoneWire)));
		allTextures.put("enchantment_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/enchantment_top", Texture.enchantmentTableTop),
				new IndexedTexture(0xA6, Texture.enchantmentTableTop)));
		allTextures.put("dragonEgg", new AlternateTextures(
				new SimpleTexture("textures/blocks/dragonEgg", Texture.dragonEgg),
				new IndexedTexture(0xA7, Texture.dragonEgg)));
		allTextures.put("cocoa_2", new AlternateTextures(
				new SimpleTexture("textures/blocks/cocoa_2", Texture.cocoaPlantLarge),
				new IndexedTexture(0xA8, Texture.cocoaPlantLarge)));
		allTextures.put("cocoa_1", new AlternateTextures(
				new SimpleTexture("textures/blocks/cocoa_1", Texture.cocoaPlantMedium),
				new IndexedTexture(0xA9, Texture.cocoaPlantMedium)));
		allTextures.put("cocoa_0", new AlternateTextures(
				new SimpleTexture("textures/blocks/cocoa_0", Texture.cocoaPlantSmall),
				new IndexedTexture(0xAA, Texture.cocoaPlantSmall)));
		allTextures.put("oreEmerald", new AlternateTextures(
				new SimpleTexture("textures/blocks/oreEmerald", Texture.emeraldOre),
				new IndexedTexture(0xAB, Texture.emeraldOre)));
		allTextures.put("tripWireSource", new AlternateTextures(
				new SimpleTexture("textures/blocks/tripWireSource", Texture.tripwireHook),
				new IndexedTexture(0xAC, Texture.tripwireHook)));
		allTextures.put("tripWire", new AlternateTextures(
				new SimpleTexture("textures/blocks/tripWire", Texture.tripwire),
				new IndexedTexture(0xAD, Texture.tripwire)));
		allTextures.put("endframe_eye", new AlternateTextures(
				new SimpleTexture("textures/blocks/endframe_eye", Texture.eyeOfTheEnder),
				new IndexedTexture(0xAE, Texture.eyeOfTheEnder)));
		allTextures.put("whiteStone", new AlternateTextures(
				new SimpleTexture("textures/blocks/whiteStone", Texture.endStone),
				new IndexedTexture(0xAF, Texture.endStone)));

		allTextures.put("sandstone_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/sandstone_top", Texture.sandstoneTop),
				new IndexedTexture(0xB0, Texture.sandstoneTop)));
		allTextures.put("cloth_11", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_11", Texture.blueWool),
				new IndexedTexture(0xB1, Texture.blueWool)));
		allTextures.put("cloth_3", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_3", Texture.lightBlueWool),
				new IndexedTexture(0xB2, Texture.lightBlueWool)));
		allTextures.put("goldenRail_powered", new AlternateTextures(
				new SimpleTexture("textures/blocks/goldenRail_powered", Texture.poweredRailOn),
				new IndexedTexture(0xB3, Texture.poweredRailOn)));
		allTextures.put("enchantment_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/enchantment_side", Texture.enchantmentTableSide),
				new IndexedTexture(0xB6, Texture.enchantmentTableSide)));
		allTextures.put("enchantment_bottom", new AlternateTextures(
				new SimpleTexture("textures/blocks/enchantment_bottom", Texture.enchantmentTableBottom),
				new IndexedTexture(0xB7, Texture.enchantmentTableBottom)));
		allTextures.put("commandBlock", new AlternateTextures(
				new SimpleTexture("textures/blocks/commandBlock", Texture.commandBlock),
				new IndexedTexture(0xB8, Texture.commandBlock)));
		allTextures.put("flowerPot", new AlternateTextures(
				new SimpleTexture("textures/blocks/flowerPot", Texture.flowerPot),
				new IndexedTexture(0xBA, Texture.flowerPot)));
		allTextures.put("netherquartz", new AlternateTextures(
				new SimpleTexture("textures/blocks/netherquartz", Texture.netherQuartzOre),
				new IndexedTexture(0xBF, Texture.netherQuartzOre)));

		allTextures.put("sandstone_side", new AlternateTextures(
				new SimpleTexture("textures/blocks/sandstone_side", Texture.sandstoneSide),
				new IndexedTexture(0xC0, Texture.sandstoneSide)));
		allTextures.put("cloth_10", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_10", Texture.purpleWool),
				new IndexedTexture(0xC1, Texture.purpleWool)));
		allTextures.put("cloth_2", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_2", Texture.magentaWool),
				new IndexedTexture(0xC2, Texture.magentaWool)));
		allTextures.put("detectorRail", new AlternateTextures(
				new SimpleTexture("textures/blocks/detectorRail", Texture.detectorRail),
				new IndexedTexture(0xC3, Texture.detectorRail)));
		allTextures.put("leaves_jungle", new AlternateTextures(
				new SimpleTexture("textures/blocks/leaves_jungle", Texture.jungleTreeLeaves),
				new IndexedTexture(0xC4, Texture.jungleTreeLeaves)));
		allTextures.put("wood_spruce", new AlternateTextures(
				new SimpleTexture("textures/blocks/wood_spruce", Texture.sprucePlanks),
				new IndexedTexture(0xC6, Texture.sprucePlanks)));
		allTextures.put("wood_jungle", new AlternateTextures(
				new SimpleTexture("textures/blocks/wood_jungle", Texture.jungleTreePlanks),
				new IndexedTexture(0xC7, Texture.jungleTreePlanks)));
		allTextures.put("carrots_0", new AlternateTextures(
				new SimpleTexture("textures/blocks/carrots_0", Texture.carrots0),
				new IndexedTexture(0xC8, Texture.carrots0)));
		allTextures.put("potatoes_0", new AlternateTextures(
				new SimpleTexture("textures/blocks/potatoes_0", Texture.potatoes0),
				new IndexedTexture(0xC8, Texture.potatoes0)));
		allTextures.put("carrots_1", new AlternateTextures(
				new SimpleTexture("textures/blocks/carrots_1", Texture.carrots1),
				new IndexedTexture(0xC9, Texture.carrots1)));
		allTextures.put("potatoes_1", new AlternateTextures(
				new SimpleTexture("textures/blocks/potatoes_1", Texture.potatoes1),
				new IndexedTexture(0xC9, Texture.potatoes1)));
		allTextures.put("carrots_2", new AlternateTextures(
				new SimpleTexture("textures/blocks/carrots_2", Texture.carrots2),
				new IndexedTexture(0xCA, Texture.carrots2)));
		allTextures.put("potatoes_2", new AlternateTextures(
				new SimpleTexture("textures/blocks/potatoes_2", Texture.potatoes2),
				new IndexedTexture(0xCA, Texture.potatoes2)));
		allTextures.put("carrots_3", new AlternateTextures(
				new SimpleTexture("textures/blocks/carrots_3", Texture.carrots3),
				new IndexedTexture(0xCB, Texture.carrots3)));
		allTextures.put("potatoes_3", new AlternateTextures(
				new SimpleTexture("textures/blocks/potatoes_3", Texture.potatoes3),
				new IndexedTexture(0xCC, Texture.potatoes3)));
		allTextures.put("water", new AlternateTextures(
				new SimpleTexture("textures/blocks/water", Texture.water),
				new IndexedTexture(0xCD, Texture.water)));

		allTextures.put("sandstone_bottom", new AlternateTextures(
				new SimpleTexture("textures/blocks/sandstone_bottom", Texture.sandstoneBottom),
				new IndexedTexture(0xD0, Texture.sandstoneBottom)));
		allTextures.put("cloth_9", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_9", Texture.cyanWool),
				new IndexedTexture(0xD1, Texture.cyanWool)));
		allTextures.put("cloth_1", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_1", Texture.orangeWool),
				new IndexedTexture(0xD2, Texture.orangeWool)));
		allTextures.put("redstoneLight", new AlternateTextures(
				new SimpleTexture("textures/blocks/redstoneLight", Texture.redstoneLampOff),
				new IndexedTexture(0xD3, Texture.redstoneLampOff)));
		allTextures.put("redstoneLight_lit", new AlternateTextures(
				new SimpleTexture("textures/blocks/redstoneLight_lit", Texture.redstoneLampOn),
				new IndexedTexture(0xD4, Texture.redstoneLampOn)));
		allTextures.put("stonebricksmooth_carved", new AlternateTextures(
				new SimpleTexture("textures/blocks/stonebricksmooth_carved", Texture.circleStoneBrick),
				new IndexedTexture(0xD5, Texture.circleStoneBrick)));
		allTextures.put("wood_birch", new AlternateTextures(
				new SimpleTexture("textures/blocks/wood_birch", Texture.birchPlanks),
				new IndexedTexture(0xD6, Texture.birchPlanks)));
		allTextures.put("anvil_base", new AlternateTextures(
				new SimpleTexture("textures/blocks/anvil_base", Texture.anvilSide),
				new IndexedTexture(0xD7, Texture.anvilSide)));
		allTextures.put("anvil_top_damaged_1", new AlternateTextures(
				new SimpleTexture("textures/blocks/anvil_top_damaged_1", Texture.anvilTopDamaged1),
				new IndexedTexture(0xD8, Texture.anvilTopDamaged1)));

		allTextures.put("netherBrick", new AlternateTextures(
				new SimpleTexture("textures/blocks/netherBrick", Texture.netherBrick),
				new IndexedTexture(0xE0, Texture.netherBrick)));
		allTextures.put("cloth_8", new AlternateTextures(
				new SimpleTexture("textures/blocks/cloth_8", Texture.lightGrayWool),
				new IndexedTexture(0xE1, Texture.lightGrayWool)));
		allTextures.put("netherStalk_0", new AlternateTextures(
				new SimpleTexture("textures/blocks/netherStalk_0", Texture.netherWart0),
				new IndexedTexture(0xE2, Texture.netherWart0)));
		allTextures.put("netherStalk_1", new AlternateTextures(
				new SimpleTexture("textures/blocks/netherStalk_1", Texture.netherWart1),
				new IndexedTexture(0xE3, Texture.netherWart1)));
		allTextures.put("netherStalk_2", new AlternateTextures(
				new SimpleTexture("textures/blocks/netherStalk_2", Texture.netherWart2),
				new IndexedTexture(0xE4, Texture.netherWart2)));
		allTextures.put("sandstone_carved", new AlternateTextures(
				new SimpleTexture("textures/blocks/sandstone_carved", Texture.sandstoneDecorated),
				new IndexedTexture(0xE5, Texture.sandstoneDecorated)));
		allTextures.put("sandstone_smooth", new AlternateTextures(
				new SimpleTexture("textures/blocks/sandstone_smooth", Texture.sandstoneSmooth),
				new IndexedTexture(0xE6, Texture.sandstoneSmooth)));
		allTextures.put("anvil_top", new AlternateTextures(
				new SimpleTexture("textures/blocks/anvil_top", Texture.anvilTop),
				new IndexedTexture(0xE7, Texture.anvilTop)));
		allTextures.put("anvil_top_damaged_2", new AlternateTextures(
				new SimpleTexture("textures/blocks/anvil_top_damaged_2", Texture.anvilTopDamaged2),
				new IndexedTexture(0xE8, Texture.anvilTopDamaged2)));
		allTextures.put("lava", new AlternateTextures(
				new SimpleTexture("textures/blocks/lava", Texture.lava),
				new IndexedTexture(0xED, Texture.lava)));

		allTextures.put("quartzblock_side",
				new SimpleTexture("textures/blocks/quartzblock_side", Texture.quartzSide));
		allTextures.put("quartzblock_top",
				new SimpleTexture("textures/blocks/quartzblock_top", Texture.quartzTop));
		allTextures.put("quartzblock_bottom",
				new SimpleTexture("textures/blocks/quartzblock_bottom", Texture.quartzBottom));
		allTextures.put("quartzblock_chiseled",
				new SimpleTexture("textures/blocks/quartzblock_chiseled", Texture.quartzChiseled));
		allTextures.put("quartzblock_chiseled_top",
				new SimpleTexture("textures/blocks/quartzblock_chiseled_top", Texture.quartzChiseledTop));
		allTextures.put("quartzblock_lines",
				new SimpleTexture("textures/blocks/quartzblock_lines", Texture.quartzPillar));
		allTextures.put("quartzblock_lines_top",
				new SimpleTexture("textures/blocks/quartzblock_lines_top", Texture.quartzPillarTop));
		allTextures.put("dropper_front",
				new SimpleTexture("textures/blocks/dropper_front", Texture.dropperFront));
		allTextures.put("activatorRail",
				new SimpleTexture("textures/blocks/activatorRail", Texture.activatorRail));
		allTextures.put("activatorRail_powered",
				new SimpleTexture("textures/blocks/activatorRail_powered", Texture.activatorRailPowered));
		allTextures.put("daylightDetector_top",
				new SimpleTexture("textures/blocks/daylightDetector_top", Texture.daylightDetectorTop));
		allTextures.put("daylightDetector_side",
				new SimpleTexture("textures/blocks/daylightDetector_side", Texture.daylightDetectorSide));
		allTextures.put("comparator",
				new SimpleTexture("textures/blocks/comparator", Texture.comparator));
		allTextures.put("comparator_lit",
				new SimpleTexture("textures/blocks/comparator_lit", Texture.comparatorLit));
		allTextures.put("hopper",
				new SimpleTexture("textures/blocks/hopper", Texture.hopper));
		allTextures.put("hopper_inside",
				new SimpleTexture("textures/blocks/hopper_inside", Texture.hopperInside));
	}

	/**
	 * Attempt to load the specified texture pack.
	 * If some textures files are not found they will be loaded from
	 * the default texture pack.
	 * @param tpFile
	 * @param rememberTP Decides if this texture pack should be saved as the
	 * last used texture pack
	 */
	public static void loadTexturePack(File tpFile, boolean rememberTP) {
		loadTexturePack(tpFile, allTextures.keySet(), rememberTP);
	}

	private static void loadTexturePack(File tpFile,
			Collection<String> toLoad, boolean rememberTP) {

		File defaultTP = Chunky.getMinecraftJar();
		boolean isDefault = tpFile.equals(defaultTP);
		String tpName = isDefault ? "default texture pack"
				: "texture pack " + tpFile.getName();

		Set<String> notLoaded = new HashSet<String>(toLoad);

		ZipFile texturePack = null;
		try {
			texturePack = new ZipFile(tpFile);
			for (String id: toLoad) {
				TextureRef tex = allTextures.get(id);
				if (tex.load(texturePack)) {
					notLoaded.remove(tex);
				}
			}

			// fall back on terrain.png
			loadTerrainTextures(texturePack, notLoaded);

			if (rememberTP) {
				ProgramProperties.setProperty("lastTexturePack",
						tpFile.getAbsolutePath());
			}
		} catch (IOException e) {
			logger.warn("Failed to open " + tpName);
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
			for (String id: notLoaded) {
				msg.append(id);
				msg.append("\n");
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
