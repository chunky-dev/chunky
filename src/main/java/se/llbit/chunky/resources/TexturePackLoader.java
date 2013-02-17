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
import java.util.HashSet;
import java.util.LinkedList;
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
import se.llbit.chunky.world.Icon;
import se.llbit.util.ProgramProperties;

/**
 * Utility methods to load Minecraft texture packs.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturePackLoader {

	private static final Logger logger =
			Logger.getLogger(TexturePackLoader.class);
	
	private static Collection<TextureRef> textures =
		new LinkedList<TextureRef>();
	
	static {
		textures.add(new ChestTexture("item/chest", Texture.chestLock,
				Texture.chestTop, Texture.chestBottom, Texture.chestLeft,
				Texture.chestRight, Texture.chestFront, Texture.chestBack));
		textures.add(new ChestTexture("item/enderchest",
				Texture.enderChestLock, Texture.enderChestTop,
				Texture.enderChestBottom, Texture.enderChestLeft,
				Texture.enderChestRight, Texture.enderChestFront,
				Texture.enderChestBack));
		textures.add(new LargeChestTexture("item/largechest"));
		textures.add(new AlternateTextures(
				new SunTexture("environment/sun"),
				new SunTexture("terrain/sun")));
		textures.add(new SignTexture("item/sign"));
		textures.add(new CloudsTexture("environment/clouds"));
		textures.add(new GrassColorTexture("misc/grasscolor"));
		textures.add(new FoliageColorTexture("misc/foliagecolor"));
		
		textures.add(new IndexedTexture("textures/blocks/grass_top", Texture.grassTop, 0x00));
		textures.add(new IndexedTexture("textures/blocks/stone", Texture.stone, 0x01));
		textures.add(new IndexedTexture("textures/blocks/dirt", Texture.dirt, 0x02));
		textures.add(new IndexedTexture("textures/blocks/grass_side", Texture.grassSideSaturated, 0x03));
		textures.add(new IndexedTexture("textures/blocks/wood", Texture.oakPlanks, 0x04));
		textures.add(new IndexedTexture("textures/blocks/stoneslab_side", Texture.slabSide, 0x05));
		textures.add(new IndexedTexture("textures/blocks/stoneslab_top", Texture.slabTop, 0x06));
		textures.add(new IndexedTexture("textures/blocks/brick", Texture.brick, 0x07));
		textures.add(new IndexedTexture("textures/blocks/tnt_side", Texture.tntSide, 0x08));
		textures.add(new IndexedTexture("textures/blocks/tnt_top", Texture.tntTop, 0x09));
		textures.add(new IndexedTexture("textures/blocks/tnt_bottom", Texture.tntBottom, 0x0A));
		textures.add(new IndexedTexture("textures/blocks/web", Texture.cobweb, 0x0B));
		textures.add(new IndexedTexture("textures/blocks/rose", Texture.redRose, 0x0C));
		textures.add(new IndexedTexture("textures/blocks/flower", Texture.yellowFlower, 0x0D));
		// TODO: textures.add(new IndexedTexture("textures/blocks/portal", Texture.portal, 0x0E));
		textures.add(new IndexedTexture("textures/blocks/sapling", Texture.oakSapling, 0x0F));
		
		textures.add(new IndexedTexture("textures/blocks/stonebrick", Texture.cobblestone, 0x10));
		textures.add(new IndexedTexture("textures/blocks/bedrock", Texture.bedrock, 0x11));
		textures.add(new IndexedTexture("textures/blocks/sand", Texture.sand, 0x12));
		textures.add(new IndexedTexture("textures/blocks/gravel", Texture.gravel, 0x13));
		textures.add(new IndexedTexture("textures/blocks/tree_side", Texture.oakWood, 0x14));
		textures.add(new IndexedTexture("textures/blocks/tree_top", Texture.woodTop, 0x15));
		textures.add(new IndexedTexture("textures/blocks/blockIron", Texture.ironBlock, 0x16));
		textures.add(new IndexedTexture("textures/blocks/blockGold", Texture.goldBlock, 0x17));
		textures.add(new IndexedTexture("textures/blocks/blockDiamond", Texture.diamondBlock, 0x18));
		textures.add(new IndexedTexture("textures/blocks/blockEmerald", Texture.emeraldBlock, 0x19));
		textures.add(new IndexedTexture("textures/blocks/mushroom_red", Texture.redMushroom, 0x1C));
		textures.add(new IndexedTexture("textures/blocks/mushroom_brown", Texture.brownMushroom, 0x1D));
		textures.add(new IndexedTexture("textures/blocks/sapling_jungle", Texture.jungleTreeSapling, 0x1E));
		
		textures.add(new IndexedTexture("textures/blocks/oreGold", Texture.goldOre, 0x20));
		textures.add(new IndexedTexture("textures/blocks/oreIron", Texture.ironOre, 0x21));
		textures.add(new IndexedTexture("textures/blocks/oreCoal", Texture.coalOre, 0x22));
		textures.add(new IndexedTexture("textures/blocks/bookshelf", Texture.bookshelf, 0x23));
		textures.add(new IndexedTexture("textures/blocks/stoneMoss", Texture.mossStone, 0x24));
		textures.add(new IndexedTexture("textures/blocks/obsidian", Texture.obsidian, 0x25));
		textures.add(new IndexedTexture("textures/blocks/grass_side_overlay", Texture.grassSide, 0x26));
		textures.add(new IndexedTexture("textures/blocks/tallgrass", Texture.tallGrass, 0x27));
		textures.add(new IndexedTexture("textures/blocks/beacon", Texture.beacon, 0x29));
		textures.add(new IndexedTexture("textures/blocks/workbench_top", Texture.workbenchTop, 0x2B));
		textures.add(new IndexedTexture("textures/blocks/furnace_front", Texture.furnaceUnlitFront, 0x2C));
		textures.add(new IndexedTexture("textures/blocks/furnace_side", Texture.furnaceSide, 0x2D));
		textures.add(new IndexedTexture("textures/blocks/dispenser_front", Texture.dispenserFront, 0x2E));
		
		textures.add(new IndexedTexture("textures/blocks/sponge", Texture.sponge, 0x30));
		textures.add(new IndexedTexture("textures/blocks/glass", Texture.glass, 0x31));
		textures.add(new IndexedTexture("textures/blocks/oreDiamond", Texture.diamondOre, 0x32));
		textures.add(new IndexedTexture("textures/blocks/oreRedstone", Texture.redstoneOre, 0x33));
		textures.add(new IndexedTexture("textures/blocks/leaves", Texture.oakLeaves, 0x34));
		textures.add(new IndexedTexture("textures/blocks/stonebricksmooth", Texture.stoneBrick, 0x36));
		textures.add(new IndexedTexture("textures/blocks/deadbush", Texture.deadBush, 0x37));
		textures.add(new IndexedTexture("textures/blocks/fern", Texture.fern, 0x38));
		textures.add(new IndexedTexture("textures/blocks/workbench_side", Texture.workbenchSide, 0x3B));
		textures.add(new IndexedTexture("textures/blocks/workbench_front", Texture.workbenchFront, 0x3C));
		textures.add(new IndexedTexture("textures/blocks/furnace_front_lit", Texture.furnaceLitFront, 0x3D));
		textures.add(new IndexedTexture("textures/blocks/furnace_top", Texture.furnaceTop, 0x3E));
		textures.add(new IndexedTexture("textures/blocks/sapling_spruce", Texture.spruceSapling, 0x3F));
		
		textures.add(new IndexedTexture("textures/blocks/cloth_0", Texture.whiteWool, 0x40));
		textures.add(new IndexedTexture("textures/blocks/mobSpawner", Texture.monsterSpawner, 0x41));
		textures.add(new IndexedTexture("textures/blocks/snow", Texture.snowBlock, 0x42));
		textures.add(new IndexedTexture("textures/blocks/ice", Texture.ice, 0x43));
		textures.add(new IndexedTexture("textures/blocks/snow_side", Texture.snowSide, 0x44));
		textures.add(new IndexedTexture("textures/blocks/cactus_top", Texture.cactusTop, 0x45));
		textures.add(new IndexedTexture("textures/blocks/cactus_side", Texture.cactusSide, 0x46));
		textures.add(new IndexedTexture("textures/blocks/cactus_bottom", Texture.cactusBottom, 0x47));
		textures.add(new IndexedTexture("textures/blocks/clay", Texture.clay, 0x48));
		textures.add(new IndexedTexture("textures/blocks/reeds", Texture.sugarCane, 0x49));
		textures.add(new IndexedTexture("textures/blocks/musicBlock", Texture.jukeboxSide, 0x4A));
		textures.add(new IndexedTexture("textures/blocks/jukebox_top", Texture.jukeboxTop, 0x4B));
		textures.add(new IndexedTexture("textures/blocks/waterlily", Texture.lilyPad, 0x4C));
		textures.add(new IndexedTexture("textures/blocks/mycel_side", Texture.myceliumSide, 0x4D));
		textures.add(new IndexedTexture("textures/blocks/mycel_top", Texture.myceliumTop, 0x4E));
		textures.add(new IndexedTexture("textures/blocks/sapling_birch", Texture.birchSapling, 0x4F));
		
		textures.add(new IndexedTexture("textures/blocks/torch", Texture.torch, 0x50));
		textures.add(new IndexedTexture("textures/blocks/doorWood_upper", Texture.woodenDoorTop, 0x51));
		textures.add(new IndexedTexture("textures/blocks/doorIron_upper", Texture.ironDoorTop, 0x52));
		textures.add(new IndexedTexture("textures/blocks/ladder", Texture.ladder, 0x53));
		textures.add(new IndexedTexture("textures/blocks/trapdoor", Texture.trapdoor, 0x54));
		textures.add(new IndexedTexture("textures/blocks/fenceIron", Texture.ironBars, 0x55));
		textures.add(new IndexedTexture("textures/blocks/farmland_wet", Texture.farmlandWet, 0x56));
		textures.add(new IndexedTexture("textures/blocks/farmland_dry", Texture.farmlandDry, 0x57));
		textures.add(new IndexedTexture("textures/blocks/crops_0", Texture.crops0, 0x58));
		textures.add(new IndexedTexture("textures/blocks/crops_1", Texture.crops1, 0x59));
		textures.add(new IndexedTexture("textures/blocks/crops_2", Texture.crops2, 0x5A));
		textures.add(new IndexedTexture("textures/blocks/crops_3", Texture.crops3, 0x5B));
		textures.add(new IndexedTexture("textures/blocks/crops_4", Texture.crops4, 0x5C));
		textures.add(new IndexedTexture("textures/blocks/crops_5", Texture.crops5, 0x5D));
		textures.add(new IndexedTexture("textures/blocks/crops_6", Texture.crops6, 0x5E));
		textures.add(new IndexedTexture("textures/blocks/crops_7", Texture.crops7, 0x5F));

		textures.add(new IndexedTexture("textures/blocks/lever", Texture.lever, 0x60));
		textures.add(new IndexedTexture("textures/blocks/doorWood_lower", Texture.woodenDoorBottom, 0x61));
		textures.add(new IndexedTexture("textures/blocks/doorIron_lower", Texture.ironDoorBottom, 0x62));
		textures.add(new IndexedTexture("textures/blocks/redtorch_lit", Texture.redstoneTorchOn, 0x63));
		textures.add(new IndexedTexture("textures/blocks/stonebricksmooth_mossy", Texture.mossyStoneBrick, 0x64));
		textures.add(new IndexedTexture("textures/blocks/stonebricksmooth_cracked", Texture.crackedStoneBrick, 0x65));
		textures.add(new IndexedTexture("textures/blocks/pumpkin_top", Texture.pumpkinTop, 0x66));
		textures.add(new IndexedTexture("textures/blocks/hellrock", Texture.netherrack, 0x67));
		textures.add(new IndexedTexture("textures/blocks/hellsand", Texture.soulsand, 0x68));
		textures.add(new IndexedTexture("textures/blocks/lightgem", Texture.glowstone, 0x69));
		textures.add(new IndexedTexture("textures/blocks/piston_top_sticky", Texture.pistonTopSticky, 0x6A));
		textures.add(new IndexedTexture("textures/blocks/piston_top", Texture.pistonTop, 0x6B));
		textures.add(new IndexedTexture("textures/blocks/piston_side", Texture.pistonSide, 0x6C));
		textures.add(new IndexedTexture("textures/blocks/piston_bottom", Texture.pistonBottom, 0x6D));
		textures.add(new IndexedTexture("textures/blocks/piston_inner_top", Texture.pistonInnerTop, 0x6E));
		textures.add(new IndexedTexture("textures/blocks/stem_straight", Texture.stemStraight, 0x6F));
		
		textures.add(new IndexedTexture("textures/blocks/rail_turn", Texture.railsCurved, 0x70));
		textures.add(new IndexedTexture("textures/blocks/cloth_15", Texture.blackWool, 0x71));
		textures.add(new IndexedTexture("textures/blocks/cloth_7", Texture.grayWool, 0x72));
		textures.add(new IndexedTexture("textures/blocks/redtorch", Texture.redstoneTorchOff, 0x73));
		textures.add(new IndexedTexture("textures/blocks/tree_spruce", Texture.spruceWood, 0x74));
		textures.add(new IndexedTexture("textures/blocks/tree_birch", Texture.birchWood, 0x75));
		textures.add(new IndexedTexture("textures/blocks/pumpkin_side", Texture.pumpkinSide, 0x76));
		textures.add(new IndexedTexture("textures/blocks/pumpkin_face", Texture.pumpkinFront, 0x77));
		textures.add(new IndexedTexture("textures/blocks/pumpkin_jack", Texture.jackolanternFront, 0x78));
		textures.add(new IndexedTexture("textures/blocks/cake_top", Texture.cakeTop, 0x79));
		textures.add(new IndexedTexture("textures/blocks/cake_side", Texture.cakeSide, 0x7A));
		textures.add(new IndexedTexture("textures/blocks/cake_inner", Texture.cakeInside, 0x7B));
		textures.add(new IndexedTexture("textures/blocks/cake_bottom", Texture.cakeBottom, 0x7C));
		textures.add(new IndexedTexture("textures/blocks/mushroom_skin_red", Texture.hugeRedMushroom, 0x7D));
		textures.add(new IndexedTexture("textures/blocks/mushroom_skin_brown", Texture.hugeBrownMushroom, 0x7E));
		textures.add(new IndexedTexture("textures/blocks/stem_bent", Texture.stemBent, 0x7F));
		
		textures.add(new IndexedTexture("textures/blocks/rail", Texture.rails, 0x80));
		textures.add(new IndexedTexture("textures/blocks/cloth_14", Texture.redWool, 0x81));
		textures.add(new IndexedTexture("textures/blocks/cloth_6", Texture.pinkWool, 0x82));
		textures.add(new IndexedTexture("textures/blocks/repeater", Texture.redstoneRepeaterOff, 0x83));
		textures.add(new IndexedTexture("textures/blocks/leaves_spruce", Texture.spruceLeaves, 0x84));
		textures.add(new IndexedTexture("textures/blocks/bed_feet_top", Texture.bedFootTop, 0x86));
		textures.add(new IndexedTexture("textures/blocks/bed_head_top", Texture.bedHeadTop, 0x87));
		textures.add(new IndexedTexture("textures/blocks/melon_side", Texture.melonSide, 0x88));
		textures.add(new IndexedTexture("textures/blocks/melon_top", Texture.melonTop, 0x89));
		textures.add(new IndexedTexture("textures/blocks/cauldron_top", Texture.cauldronTop, 0x8A));
		textures.add(new IndexedTexture("textures/blocks/cauldron_inner", Texture.cauldronInside, 0x8B));
		textures.add(new IndexedTexture("textures/items/cake", Icon.cake, 0x8C));
		textures.add(new IndexedTexture("textures/blocks/mushroom_skin_stem", Texture.mushroomStem, 0x8D));
		textures.add(new IndexedTexture("textures/blocks/mushroom_inside", Texture.mushroomPores, 0x8E));
		textures.add(new IndexedTexture("textures/blocks/vine", Texture.vines, 0x8F));

		textures.add(new IndexedTexture("textures/blocks/blockLapis", Texture.lapislazuliBlock, 0x90));
		textures.add(new IndexedTexture("textures/blocks/cloth_13", Texture.greenWool, 0x91));
		textures.add(new IndexedTexture("textures/blocks/cloth_5", Texture.limeWool, 0x92));
		textures.add(new IndexedTexture("textures/blocks/repeater_lit", Texture.redstoneRepeaterOn, 0x93));
		textures.add(new IndexedTexture("textures/blocks/thinglass_top", Texture.glassPaneSide, 0x94));
		textures.add(new IndexedTexture("textures/blocks/bed_feet_end", Texture.bedFootEnd, 0x95));
		textures.add(new IndexedTexture("textures/blocks/bed_feet_side", Texture.bedFootSide, 0x96));
		textures.add(new IndexedTexture("textures/blocks/bed_head_side", Texture.bedHeadSide, 0x97));
		textures.add(new IndexedTexture("textures/blocks/bed_head_end", Texture.bedHeadEnd, 0x98));
		textures.add(new IndexedTexture("textures/blocks/tree_jungle", Texture.jungleTreeWood, 0x99));
		textures.add(new IndexedTexture("textures/blocks/cauldron_side", Texture.cauldronSide, 0x9A));
		textures.add(new IndexedTexture("textures/blocks/cauldron_bottom", Texture.cauldronBottom, 0x9B));
		textures.add(new IndexedTexture("textures/blocks/brewingStand_base", Texture.brewingStandBase, 0x9C));
		textures.add(new IndexedTexture("textures/blocks/brewingStand", Texture.brewingStandSide, 0x9D));
		textures.add(new IndexedTexture("textures/blocks/endframe_top", Texture.endPortalFrameTop, 0x9E));
		textures.add(new IndexedTexture("textures/blocks/endframe_side", Texture.endPortalFrameSide, 0x9F));

		textures.add(new IndexedTexture("textures/blocks/oreLapis", Texture.lapislazuliOre, 0xA0));
		textures.add(new IndexedTexture("textures/blocks/cloth_12", Texture.brownWool, 0xA1));
		textures.add(new IndexedTexture("textures/blocks/cloth_4", Texture.yellowWool, 0xA2));
		textures.add(new IndexedTexture("textures/blocks/goldenRail", Texture.poweredRailsOff, 0xA3));
		textures.add(new IndexedTexture("textures/blocks/redstoneDust_cross", Texture.redstoneWireCross, 0xA4));
		textures.add(new IndexedTexture("textures/blocks/redstoneDust_line", Texture.redstoneWire, 0xA5));
		textures.add(new IndexedTexture("textures/blocks/enchantment_top", Texture.enchantmentTableTop, 0xA6));
		textures.add(new IndexedTexture("textures/blocks/dragonEgg", Texture.dragonEgg, 0xA7));
		textures.add(new IndexedTexture("textures/blocks/cocoa_2", Texture.cocoaPlantLarge, 0xA8));
		textures.add(new IndexedTexture("textures/blocks/cocoa_1", Texture.cocoaPlantMedium, 0xA9));
		textures.add(new IndexedTexture("textures/blocks/cocoa_0", Texture.cocoaPlantSmall, 0xAA));
		textures.add(new IndexedTexture("textures/blocks/oreEmerald", Texture.emeraldOre, 0xAB));
		textures.add(new IndexedTexture("textures/blocks/tripWireSource", Texture.tripwireHook, 0xAC));
		textures.add(new IndexedTexture("textures/blocks/tripWire", Texture.tripwire, 0xAD));
		textures.add(new IndexedTexture("textures/blocks/endframe_eye", Texture.eyeOfTheEnder, 0xAE));
		textures.add(new IndexedTexture("textures/blocks/whiteStone", Texture.endStone, 0xAF));
		
		textures.add(new IndexedTexture("textures/blocks/sandstone_top", Texture.sandstoneTop, 0xB0));
		textures.add(new IndexedTexture("textures/blocks/cloth_11", Texture.blueWool, 0xB1));
		textures.add(new IndexedTexture("textures/blocks/cloth_3", Texture.lightBlueWool, 0xB2));
		textures.add(new IndexedTexture("textures/blocks/goldenRail_powered", Texture.poweredRailsOn, 0xB3));
		textures.add(new IndexedTexture("textures/blocks/enchantment_side", Texture.enchantmentTableSide, 0xB6));
		textures.add(new IndexedTexture("textures/blocks/enchantment_bottom", Texture.enchantmentTableBottom, 0xB7));
		textures.add(new IndexedTexture("textures/blocks/commandBlock", Texture.commandBlock, 0xB8));
		textures.add(new IndexedTexture("textures/blocks/flowerPot", Texture.flowerPot, 0xBA));
		
		textures.add(new IndexedTexture("textures/blocks/sandstone_side", Texture.sandstoneSide, 0xC0));
		textures.add(new IndexedTexture("textures/blocks/cloth_10", Texture.purpleWool, 0xC1));
		textures.add(new IndexedTexture("textures/blocks/cloth_2", Texture.magentaWool, 0xC2));
		textures.add(new IndexedTexture("textures/blocks/detectorRail", Texture.detectorRails, 0xC3));
		textures.add(new IndexedTexture("textures/blocks/leaves_jungle", Texture.jungleTreeLeaves, 0xC4));
		textures.add(new IndexedTexture("textures/blocks/wood_spruce", Texture.sprucePlanks, 0xC6));
		textures.add(new IndexedTexture("textures/blocks/wood_jungle", Texture.jungleTreePlanks, 0xC7));
		textures.add(new IndexedTexture("textures/blocks/carrots_0", Texture.carrotsPotatoes0, 0xC8));
		textures.add(new IndexedTexture("textures/blocks/carrots_1", Texture.carrotsPotatoes1, 0xC9));
		textures.add(new IndexedTexture("textures/blocks/carrots_2", Texture.carrotsPotatoes2, 0xCA));
		textures.add(new IndexedTexture("textures/blocks/carrots_3", Texture.carrotsMature, 0xCB));
		textures.add(new IndexedTexture("textures/blocks/potatoes_3", Texture.potatoesMature, 0xCC));
		// TODO: textures.add(new IndexedTexture("textures/blocks/", Texture.water, 0xCD));
		
		textures.add(new IndexedTexture("textures/blocks/sandstone_bottom", Texture.sandstoneBottom, 0xD0));
		textures.add(new IndexedTexture("textures/blocks/cloth_9", Texture.cyanWool, 0xD1));
		textures.add(new IndexedTexture("textures/blocks/cloth_1", Texture.orangeWool, 0xD2));
		textures.add(new IndexedTexture("textures/blocks/redstoneLight", Texture.redstoneLampOff, 0xD3));
		textures.add(new IndexedTexture("textures/blocks/redstoneLight_lit", Texture.redstoneLampOn, 0xD4));
		textures.add(new IndexedTexture("textures/blocks/stonebricksmooth_carved", Texture.circleStoneBrick, 0xD5));
		textures.add(new IndexedTexture("textures/blocks/wood_birch", Texture.birchPlanks, 0xD6));
		textures.add(new IndexedTexture("textures/blocks/anvil_base", Texture.anvilSide, 0xD7));
		textures.add(new IndexedTexture("textures/blocks/anvil_top_damaged_1", Texture.anvilTopDamaged1, 0xD8));
		
		textures.add(new IndexedTexture("textures/blocks/netherBrick", Texture.netherBrick, 0xE0));
		textures.add(new IndexedTexture("textures/blocks/cloth_8", Texture.lightGrayWool, 0xE1));
		textures.add(new IndexedTexture("textures/blocks/netherStalk_0", Texture.netherWart0, 0xE2));
		textures.add(new IndexedTexture("textures/blocks/netherStalk_1", Texture.netherWart1, 0xE3));
		textures.add(new IndexedTexture("textures/blocks/netherStalk_2", Texture.netherWart2, 0xE4));
		textures.add(new IndexedTexture("textures/blocks/sandstone_carved", Texture.sandstoneDecorated, 0xE5));
		textures.add(new IndexedTexture("textures/blocks/sandstone_smooth", Texture.sandstoneSmooth, 0xE6));
		textures.add(new IndexedTexture("textures/blocks/anvil_top", Texture.anvilTop, 0xE7));
		textures.add(new IndexedTexture("textures/blocks/anvil_top_damaged_2", Texture.anvilTopDamaged2, 0xE8));
		// TODO: textures.add(new IndexedTexture("textures/blocks/", Texture.lava, 0xED));
		
		textures.add(new SimpleTexture("textures/blocks/quartzblock_side", Texture.quartzSide));
		textures.add(new SimpleTexture("textures/blocks/quartzblock_top", Texture.quartzTop));
		textures.add(new SimpleTexture("textures/blocks/quartzblock_bottom", Texture.quartzBottom));
	}
	
	/**
	 * Load default texture files
	 * @param textures
	 */
	private static void loadDefaultTextures(Collection<TextureRef> toLoad) {
		File tpFile = Chunky.getMinecraftJar();
		try {
			ZipFile tpZip = new ZipFile(tpFile);
			for (TextureRef tex: textures) {
				if (!tex.load(tpZip, "texture pack " + tpFile)) {
					// TODO
				}
			}
			tpZip.close();
			
			// fall back on terrain.png
			
			ProgramProperties.setProperty("lastTexturePack", tpFile.getAbsolutePath());
		} catch (IOException e) {
			logger.warn("Failed to load default texture pack", e);
		}
	}
	
	/**
	 * Attempt to load the specified texture pack.
	 * If some textures files are not found they will be loaded from
	 * the default texture pack.
	 * @param tpFile
	 */
	public static void loadTexturePack(File tpFile) {
		
		Set<TextureRef> notLoaded = new HashSet<TextureRef>(textures);
		
		try {
			ZipFile texturePack = new ZipFile(tpFile);
			for (TextureRef tex: textures) {
				if (tex.load(texturePack, "texture pack " + tpFile)) {
					notLoaded.remove(tex);
				}
			}
			
			// fall back on terrain.png
			loadTerrainTextures(texturePack, notLoaded);
			
			texturePack.close();
			
			ProgramProperties.setProperty("lastTexturePack", tpFile.getAbsolutePath());
		} catch (IOException e) {
			logger.warn("Failed to load texture pack: " + tpFile.getPath(), e);
		}
		
		if (!notLoaded.isEmpty()) {
			loadDefaultTextures(notLoaded);
		}
	}
	
	private static void loadTerrainTextures(ZipFile texturePack,
			Set<TextureRef> notLoaded) throws IOException {
		
		InputStream in = texturePack.getInputStream(new ZipEntry("terrain.png"));
		if (in != null) {
			BufferedImage spritemap = ImageIO.read(in);
			BufferedImage[] texture = getTerrainTextures(spritemap);
			
			for (TextureRef tex: textures) {
				if (notLoaded.contains(tex) && tex.loadFromTerrain(texture)) {
					notLoaded.remove(tex);
				}
			}
		} else {
			// TODO
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
