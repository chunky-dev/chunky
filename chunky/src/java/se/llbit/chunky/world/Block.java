/* Copyright (c) 2010-2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import java.util.HashSet;
import java.util.Set;

import se.llbit.chunky.model.AnvilModel;
import se.llbit.chunky.model.BeaconModel;
import se.llbit.chunky.model.BedModel;
import se.llbit.chunky.model.BrewingStandModel;
import se.llbit.chunky.model.ButtonModel;
import se.llbit.chunky.model.CactusModel;
import se.llbit.chunky.model.CakeModel;
import se.llbit.chunky.model.CarpetModel;
import se.llbit.chunky.model.CauldronModel;
import se.llbit.chunky.model.ChestModel;
import se.llbit.chunky.model.CocoaPlantModel;
import se.llbit.chunky.model.ComparatorModel;
import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.model.DaylightSensorModel;
import se.llbit.chunky.model.DoorModel;
import se.llbit.chunky.model.DragonEggModel;
import se.llbit.chunky.model.EnchantmentTableModel;
import se.llbit.chunky.model.EndPortalFrameModel;
import se.llbit.chunky.model.EndPortalModel;
import se.llbit.chunky.model.FarmlandModel;
import se.llbit.chunky.model.FenceGateModel;
import se.llbit.chunky.model.FenceModel;
import se.llbit.chunky.model.FlowerPotModel;
import se.llbit.chunky.model.FurnaceModel;
import se.llbit.chunky.model.GlassPaneModel;
import se.llbit.chunky.model.GrassModel;
import se.llbit.chunky.model.HeadModel;
import se.llbit.chunky.model.HopperModel;
import se.llbit.chunky.model.LadderModel;
import se.llbit.chunky.model.LargeFlowerModel;
import se.llbit.chunky.model.LavaModel;
import se.llbit.chunky.model.LeafModel;
import se.llbit.chunky.model.LeverModel;
import se.llbit.chunky.model.MelonStemModel;
import se.llbit.chunky.model.PancakeModel;
import se.llbit.chunky.model.PistonExtensionModel;
import se.llbit.chunky.model.PistonModel;
import se.llbit.chunky.model.PressurePlateModel;
import se.llbit.chunky.model.PumpkinModel;
import se.llbit.chunky.model.QuartzModel;
import se.llbit.chunky.model.RailModel;
import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.model.RedstoneWireModel;
import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.model.SignPostModel;
import se.llbit.chunky.model.SlabModel;
import se.llbit.chunky.model.SnowModel;
import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.model.StairModel;
import se.llbit.chunky.model.StoneWallModel;
import se.llbit.chunky.model.TallGrassModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.model.TrapdoorModel;
import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.model.TripwireModel;
import se.llbit.chunky.model.VineModel;
import se.llbit.chunky.model.WallSignModel;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.model.WoodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
@SuppressWarnings("javadoc")
public class Block {
	private static final boolean UNKNOWN_INVISIBLE = false;

	public static final int AIR_ID = 0x00;
	public static final Block AIR = new Block(AIR_ID, "Air", Texture.air) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final int STONE_ID = 0x01;
	public static final Block STONE = new Block(STONE_ID, "Stone", Texture.stone) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.stone,
			Texture.granite, Texture.smoothGranite,
			Texture.diorite, Texture.smoothDiorite,
			Texture.andesite, Texture.smoothAndesite,
		};
		final String[] stoneKind = {
			"stone",
			"granite", "smoothGranite",
			"diorite", "smoothDiorite",
			"andesite", "smoothAndesite",
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex[ray.getBlockData()%7]);
		}
		@Override
		public String description(int data) {
			return stoneKind[data%7];
		};
	};
	public static final int GRASS_ID = 0x02;
	public static final Block GRASS = new Block(GRASS_ID, "Grass", Texture.grassTop) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return GrassModel.intersect(ray, scene);
		}
	};
	public static final int DIRT_ID = 0x03;
	public static final Block DIRT = new Block(DIRT_ID, "Dirt", Texture.dirt) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}

		final Texture[][] textures = {
			{
				Texture.dirt,
				Texture.dirt,
				Texture.dirt,
				Texture.dirt,
				Texture.dirt,
				Texture.dirt,
			},
			{
				Texture.coarseDirt,
				Texture.coarseDirt,
				Texture.coarseDirt,
				Texture.coarseDirt,
				Texture.coarseDirt,
				Texture.coarseDirt,
			},
			{
				Texture.podzolSide,
				Texture.podzolSide,
				Texture.podzolSide,
				Texture.podzolSide,
				Texture.podzolTop,
				Texture.podzolSide,
			},
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					textures[ray.getBlockData() % 3]);
		}
		final String[] kind = {
			"regular", "coarse", "podzol"
		};
		@Override
		public String description(int data) {
			return kind[data%3];
		}
	};
	public static final int COBBLESTONE_ID = 0x04;
	public static final Block COBBLESTONE = new Block(COBBLESTONE_ID, "Cobblestone", Texture.cobblestone) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int WOODENPLANKS_ID = 0x05;
	public static final Block WOODENPLANKS = new Block(WOODENPLANKS_ID, "Wooden Planks", Texture.oakPlanks) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.oakPlanks, Texture.sprucePlanks, Texture.birchPlanks,
			Texture.jungleTreePlanks, Texture.acaciaPlanks,
			Texture.darkOakPlanks, Texture.acaciaPlanks, Texture.darkOakPlanks
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, getTexture(ray.getBlockData()));
		}
		@Override
		public Texture getTexture(int blockData) {
			return texture[blockData & 7];
		}
	};
	public static final int SAPLING_ID = 0x06;
	public static final Block SAPLING = new Block(SAPLING_ID, "Sapling", Texture.oakSapling) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.oakSapling, Texture.spruceSapling, Texture.birchSapling,
			Texture.jungleSapling, Texture.acaciaSapling, Texture.darkOakSapling,
			Texture.acaciaSapling, Texture.darkOakSapling
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SaplingModel.intersect(ray, getTexture(ray.getBlockData()));
		}
		@Override
		public Texture getTexture(int blockData) {
			return texture[blockData & 7];
		}
	};
	public static final int BEDROCK_ID = 0x07;
	public static final Block BEDROCK = new Block(BEDROCK_ID, "Bedrock", Texture.bedrock);
	public static final int WATER_ID = 0x08;
	public static final Block WATER = new Block(WATER_ID, "Water", Texture.water) {
		{
			isOpaque = false;
			isSolid = false;
			isShiny = true;
			localIntersect = true;
			ior = 1.333f;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return WaterModel.intersect(ray);
		}
	};
	public static final int STATIONARYWATER_ID = 0x09;
	public static final Block STATIONARYWATER = new Block(STATIONARYWATER_ID, "Stationary Water", Texture.water) {
		{
			isOpaque = false;
			isSolid = false;
			isShiny = true;
			localIntersect = true;
			ior = 1.333f;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return WaterModel.intersect(ray);
		}
	};
	public static final int LAVA_ID = 0x0A;
	public static final Block LAVA = new Block(LAVA_ID, "Lava", Texture.lava) {
		{
			isOpaque = false;
			isSolid = false;
			isEmitter = true;
			emittance = 1.0;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LavaModel.intersect(ray);
		}
	};
	public static final int STATIONARYLAVA_ID = 0x0B;
	public static final Block STATIONARYLAVA = new Block(STATIONARYLAVA_ID, "Stationary Lava", Texture.lava) {
		{
			isOpaque = false;
			isSolid = false;
			isEmitter = true;
			emittance = 1.0;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LavaModel.intersect(ray);
		}
	};
	public static final int SAND_ID = 0x0C;
	public static final Block SAND = new Block(SAND_ID, "Sand", Texture.sand) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] texture = { Texture.sand, Texture.redSand };
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, getTexture(ray.getBlockData()));
		}
		@Override
		public Texture getTexture(int blockData) {
			return texture[blockData&1];
		}
	};
	public static final int GRAVEL_ID = 0x0D;
	public static final Block GRAVEL = new Block(GRAVEL_ID, "Gravel", Texture.gravel) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int GOLDORE_ID = 0x0E;
	public static final Block GOLDORE = new Block(GOLDORE_ID, "Gold Ore", Texture.goldOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int IRONORE_ID = 0x0F;
	public static final Block IRONORE = new Block(IRONORE_ID, "Iron Ore", Texture.ironOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int COALORE_ID = 0x10;
	public static final Block COALORE = new Block(COALORE_ID, "Coal Ore", Texture.coalOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int WOOD_ID = 0x11;
	public static final Block WOOD = new Block(WOOD_ID, "Wood", Texture.oakWood) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] texture = {
			{ Texture.oakWood, Texture.oakWoodTop },
			{ Texture.spruceWood, Texture.spruceWoodTop },
			{ Texture.birchWood, Texture.birchWoodTop },
			{ Texture.jungleWood, Texture.jungleTreeTop }
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return WoodModel.intersect(ray, texture[ray.getBlockData()&3]);
		}
		final String[] woodType = {
			"oak", "spruce", "birch", "jungle",
		};
		@Override
		public String description(int data) {
			return woodType[data&3];
		}
		@Override
		public Texture getTexture(int blockData) {
			return texture[blockData&3][1];
		}
	};
	public static final int LEAVES_ID = 0x12;
	public static final Block LEAVES = new Block(LEAVES_ID, "Leaves", Texture.oakLeaves) {
		{
			isOpaque = false;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.oakLeaves, Texture.spruceLeaves, Texture.birchLeaves,
			Texture.jungleTreeLeaves
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LeafModel.intersect(ray, scene, getTexture(ray.getBlockData()));

		}
		final String[] woodType = {
			"oak", "spruce", "birch", "jungle",
		};
		@Override
		public String description(int data) {
			return woodType[data&3];
		}
		@Override
		public Texture getTexture(int blockData) {
			return texture[blockData & 3];
		}
	};
	public static final int SPONGE_ID = 0x13;
	public static final Block SPONGE = new Block(SPONGE_ID, "Sponge", Texture.sponge) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] textures = {
			{
				Texture.sponge,
				Texture.sponge,
				Texture.sponge,
				Texture.sponge,
				Texture.sponge,
				Texture.sponge,
			},
			{
				Texture.wetSponge,
				Texture.wetSponge,
				Texture.wetSponge,
				Texture.wetSponge,
				Texture.wetSponge,
				Texture.wetSponge,
			},
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					textures[ray.getBlockData() % 2]);
		}
		final String[] kind = {
			"dry", "wet"
		};
		@Override
		public String description(int data) {
			return kind[data%2];
		}
	};
	public static final int GLASS_ID = 0x14;
	public static final Block GLASS = new Block(GLASS_ID, "Glass", Texture.glass) {
		{
			isOpaque = false;
			isSolid = true;
			ior = 1.520f;
		}
		@Override
		public boolean isSameMaterial(Block other) {
			return other == this || other == STAINED_GLASS;
		}
	};
	public static final int LAPISLAZULIORE_ID = 0x15;
	public static final Block LAPISLAZULIORE = new Block(LAPISLAZULIORE_ID, "Lapis Lazuli Ore", Texture.lapislazuliOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int LAPISLAZULIBLOCK_ID = 0x16;
	public static final Block LAPISLAZULIBLOCK = new Block(LAPISLAZULIBLOCK_ID, "Lapis Lazuli Block", Texture.lapislazuliBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int DISPENSER_ID = 0x17;
	public static final Block DISPENSER = new Block(DISPENSER_ID, "Dispenser", Texture.dispenserFront) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
				Texture.dispenserFront,
				Texture.furnaceSide,
				Texture.furnaceSide,
				Texture.furnaceSide,
				Texture.furnaceTop,
				Texture.furnaceTop,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FurnaceModel.intersect(ray, tex);
		}
	};
	public static final int SANDSTONE_ID = 0x18;
	public static final Block SANDSTONE = new Block(SANDSTONE_ID, "Sandstone", Texture.sandstoneSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] tex = {
			// normal
			{
				Texture.sandstoneSide,
				Texture.sandstoneSide,
				Texture.sandstoneSide,
				Texture.sandstoneSide,
				Texture.sandstoneTop,
				Texture.sandstoneBottom,
			},

			// decorated
			{
				Texture.sandstoneDecorated,
				Texture.sandstoneDecorated,
				Texture.sandstoneDecorated,
				Texture.sandstoneDecorated,
				Texture.sandstoneTop,
				Texture.sandstoneBottom,
			},

			// smooth
			{
				Texture.sandstoneSmooth,
				Texture.sandstoneSmooth,
				Texture.sandstoneSmooth,
				Texture.sandstoneSmooth,
				Texture.sandstoneTop,
				Texture.sandstoneBottom,
			},
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					tex[ray.getBlockData() % 3]);
		}
	};
	public static final int NOTEBLOCK_ID = 0x19;
	public static final Block NOTEBLOCK = new Block(NOTEBLOCK_ID, "Note Block", Icon.noteBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.jukeboxSide;
		}
	};
	public static final int BED_ID = 0x1A;
	public static final Block BED = new Block(BED_ID, "Bed", Icon.bed) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return BedModel.intersect(ray);
		}
	};
	public static final int POWEREDRAIL_ID = 0x1B;
	public static final Block POWEREDRAIL = new Block(POWEREDRAIL_ID, "Powered Rail", Texture.poweredRailOn) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.poweredRailOff, Texture.poweredRailOn
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RailModel.intersect(ray,
					texture[ray.getBlockData() >>> 3],
					(ray.getBlockData() & 7) % 6);
		}
	};
	public static final int DETECTORRAIL_ID = 0x1C;
	public static final Block DETECTORRAIL = new Block(DETECTORRAIL_ID, "Detector Rail", Texture.detectorRail) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RailModel.intersect(ray, Texture.detectorRail,
					(ray.getBlockData() & 7) % 6);
		}
	};
	public static final int STICKYPISTON_ID = 0x1D;
	public static final Block STICKYPISTON = new Block(STICKYPISTON_ID, "Sticky Piston", Texture.pistonTopSticky) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PistonModel.intersect(ray, 1);
		}
	};
	public static final int COBWEB_ID = 0x1E;
	public static final Block COBWEB = new Block(COBWEB_ID, "Cobweb", Texture.cobweb) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.cobweb);
		}
	};
	public static final int TALLGRASS_ID = 0x1F;
	public static final Block TALLGRASS = new Block(TALLGRASS_ID, "Tall Grass", Texture.tallGrass) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			subSurfaceScattering = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TallGrassModel.intersect(ray, scene);
		}
	};
	public static final int DEADBUSH_ID = 0x20;
	public static final Block DEADBUSH = new Block(DEADBUSH_ID, "Dead Bush", Texture.deadBush) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.deadBush);
		}
	};
	public static final int PISTON_ID = 0x21;
	public static final Block PISTON = new Block(PISTON_ID, "Piston", Texture.pistonTop) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PistonModel.intersect(ray, 0);
		}
	};
	public static final int PISTONEXTENSION_ID = 0x22;
	public static final Block PISTONEXTENSION = new Block(PISTONEXTENSION_ID, "Piston Extension", Texture.pistonTop) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PistonExtensionModel.intersect(ray);
		}
	};
	public static final int WOOL_ID = 0x23;
	public static final Block WOOL = new Block(WOOL_ID, "Wool", Texture.lightGrayWool) {
		{
			isOpaque = true;
			isSolid = true;
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.wool[blockData];
		}
		@Override
		public String description(int data) {
			return woolColor[data&15];
		}
	};

	public static final int MOVEDBYPISTON_ID = 0x24;
	public static final Block MOVEDBYPISTON = new Block(MOVEDBYPISTON_ID, "Block moved by Piston", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final int DANDELION_ID = 0x25;
	public static final Block DANDELION = new Block(DANDELION_ID, "Dandelion", Texture.dandelion) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			subSurfaceScattering = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.dandelion);
		}
	};
	public static final int FLOWER_ID = 0x26;
	public static final Block FLOWER = new Block(FLOWER_ID, "Flower", Texture.poppy) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			subSurfaceScattering = true;
		}
		final Texture[] textures = {
			Texture.poppy,
			Texture.blueOrchid,
			Texture.allium,
			Texture.azureBluet,
			Texture.redTulip,
			Texture.orangeTulip,
			Texture.whiteTulip,
			Texture.pinkTulip,
			Texture.oxeyeDaisy
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, textures[ray.getBlockData()%9]);
		}
		final String[] flowerType = {
			"Poppy", "Blue Orchid", "Allium", "Azure Bluet",
			"Red Tulip", "Orange Tulip", "White Tulip", "Pink Tulip",
			"Oxeye Daisy"
		};
		@Override
		public String description(int data) {
			return flowerType[data%8];
		}
	};
	public static final int BROWNMUSHROOM_ID = 0x27;
	public static final Block BROWNMUSHROOM = new Block(BROWNMUSHROOM_ID, "Brown Mushroom", Texture.brownMushroom) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.brownMushroom);
		}
	};
	public static final int REDMUSHROOM_ID = 0x28;
	public static final Block REDMUSHROOM = new Block(REDMUSHROOM_ID, "Red Mushroom", Texture.redMushroom) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.redMushroom);
		}
	};
	public static final int GOLDBLOCK_ID = 0x29;
	public static final Block GOLDBLOCK = new Block(GOLDBLOCK_ID, "Gold Block", Texture.goldBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final int IRONBLOCK_ID = 0x2A;
	public static final Block IRONBLOCK = new Block(IRONBLOCK_ID, "Iron Block", Texture.ironBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final int DOUBLESLAB_ID = 0x2B;
	public static final Block DOUBLESLAB = new Block(DOUBLESLAB_ID, "Double Stone Slab", Texture.slabTop) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}

		final Texture[] sandstone = {
			Texture.sandstoneSide,
			Texture.sandstoneSide,
			Texture.sandstoneSide,
			Texture.sandstoneSide,
			Texture.sandstoneTop,
			Texture.sandstoneTop,
		};

		final Texture[] wood = {
			Texture.oakPlanks,
			Texture.oakPlanks,
			Texture.oakPlanks,
			Texture.oakPlanks,
			Texture.oakPlanks,
			Texture.oakPlanks,
		};

		final Texture[] cobble = {
			Texture.cobblestone,
			Texture.cobblestone,
			Texture.cobblestone,
			Texture.cobblestone,
			Texture.cobblestone,
			Texture.cobblestone,
		};

		final Texture[] brick = {
			Texture.brick,
			Texture.brick,
			Texture.brick,
			Texture.brick,
			Texture.brick,
			Texture.brick,
		};

		final Texture[] stoneBrick = {
			Texture.stoneBrick,
			Texture.stoneBrick,
			Texture.stoneBrick,
			Texture.stoneBrick,
			Texture.stoneBrick,
			Texture.stoneBrick,
		};

		final Texture[] netherBrick = {
			Texture.netherBrick,
			Texture.netherBrick,
			Texture.netherBrick,
			Texture.netherBrick,
			Texture.netherBrick,
			Texture.netherBrick,
		};

		final Texture[] stone = {
			Texture.slabSide,
			Texture.slabSide,
			Texture.slabSide,
			Texture.slabSide,
			Texture.slabTop,
			Texture.slabTop,
		};

		final Texture[] quartz = {
			Texture.quartzSide,
			Texture.quartzSide,
			Texture.quartzSide,
			Texture.quartzSide,
			Texture.quartzTop,
			Texture.quartzBottom,
		};

		final Texture[] smoothStone = {
			Texture.slabTop,
			Texture.slabTop,
			Texture.slabTop,
			Texture.slabTop,
			Texture.slabTop,
			Texture.slabTop,
		};

		final Texture[] smoothSandstone = {
			Texture.sandstoneTop,
			Texture.sandstoneTop,
			Texture.sandstoneTop,
			Texture.sandstoneTop,
			Texture.sandstoneTop,
			Texture.sandstoneTop,
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			switch (ray.getBlockData()) {
			default: case 0:
				return TexturedBlockModel.intersect(ray, stone);
			case 1:
				return TexturedBlockModel.intersect(ray, sandstone);
			case 2: case 10:
				return TexturedBlockModel.intersect(ray, wood);
			case 3: case 11:
				return TexturedBlockModel.intersect(ray, cobble);
			case 4: case 12:
				return TexturedBlockModel.intersect(ray, brick);
			case 5: case 13:
				return TexturedBlockModel.intersect(ray, stoneBrick);
			case 6: case 14:
				return TexturedBlockModel.intersect(ray, netherBrick);
			case 7: case 15:
				return TexturedBlockModel.intersect(ray, quartz);
			case 8:
				return TexturedBlockModel.intersect(ray, smoothStone);
			case 9:
				return TexturedBlockModel.intersect(ray, smoothSandstone);
			}
		}
	};
	public static final int SLAB_ID = 0x2C;
	public static final Block SLAB = new Block(SLAB_ID, "Stone Slab", Texture.slabTop) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[][] tex = {
			{ Texture.slabSide, Texture.slabTop },
			{ Texture.sandstoneSide, Texture.sandstoneTop },
			{ Texture.oakPlanks, Texture.oakPlanks },
			{ Texture.cobblestone, Texture.cobblestone },
			{ Texture.brick, Texture.brick },
			{ Texture.stoneBrick, Texture.stoneBrick },
			{ Texture.netherBrick, Texture.netherBrick },
			{ Texture.quartzSide, Texture.quartzTop },
		};
		final String[] slabKind = {
			"stone",
			"sandstone",
			"wood",
			"cobble",
			"brick",
			"stone brick",
			"nether brick",
			"quartz",
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			Texture[] textures = tex[ray.getBlockData()&7];
			return SlabModel.intersect(ray, textures[0], textures[1]);
		}
		@Override
		public String description(int data) {
			return slabKind[data&7];
		};
	};
	public static final int BRICKS_ID = 0x2D;
	public static final Block BRICKS = new Block(BRICKS_ID, "Bricks", Texture.brick) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int TNT_ID = 0x2E;
	public static final Block TNT = new Block(TNT_ID, "TNT", Texture.tntSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.tntSide,
			Texture.tntSide,
			Texture.tntSide,
			Texture.tntSide,
			Texture.tntTop,
			Texture.tntBottom,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex);
		}
	};
	public static final int BOOKSHELF_ID = 0x2F;
	public static final Block BOOKSHELF = new Block(BOOKSHELF_ID, "Bookshelf", Texture.bookshelf) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.bookshelf,
			Texture.bookshelf,
			Texture.bookshelf,
			Texture.bookshelf,
			Texture.oakPlanks,
			Texture.oakPlanks,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex);
		}
	};
	public static final int MOSSSTONE_ID = 0x30;
	public static final Block MOSSSTONE = new Block(MOSSSTONE_ID, "Moss Stone", Texture.mossStone) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int OBSIDIAN_ID = 0x31;
	public static final Block OBSIDIAN = new Block(OBSIDIAN_ID, "Obsidian", Texture.obsidian) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int TORCH_ID = 0x32;
	public static final Block TORCH = new Block(TORCH_ID, "Torch", Texture.torch) {
		{
			isOpaque = false;
			isSolid = false;
			isEmitter = true;
			emittance = 50.0;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TorchModel.intersect(ray, Texture.torch);
		}
		final String[] direction = {
			"", "east", "west", "south", "north", "on floor"
		};
		@Override
		public String description(int data) {
			return direction[data%6];
		};
	};
	public static final int FIRE_ID = 0x33;
	public static final Block FIRE = new Block(FIRE_ID, "Fire", Texture.fire) {
		{
			isOpaque = false;
			isSolid = false;
			isEmitter = true;
			emittance = 1.0;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.fire);
		}
	};
	public static final int MONSTERSPAWNER_ID = 0x34;
	public static final Block MONSTERSPAWNER = new Block(MONSTERSPAWNER_ID, "Monster Spawner", Texture.monsterSpawner) {
		{
			isOpaque = false;
			isSolid = true;
		}
	};
	public static final int OAKWOODSTAIRS_ID = 0x35;
	public static final Block OAKWOODSTAIRS = new Block(OAKWOODSTAIRS_ID, "Wooden Stairs", Icon.woodenStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.oakPlanks);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.oakPlanks;
		}
	};
	public static final int CHEST_ID = 0x36;
	public static final Block CHEST = new Block(CHEST_ID, "Chest", Texture.chestFront) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[][] tex = {
			// single
			{
				Texture.chestFront,
				Texture.chestBack,
				Texture.chestLeft,
				Texture.chestRight,
				Texture.chestTop,
				Texture.chestBottom,
				Texture.chestLock,
				Texture.chestLock,
				Texture.chestLock,
				Texture.chestLock,
				Texture.chestLock,
			},

			// left
			{
				Texture.largeChestFrontLeft,
				Texture.largeChestBackLeft,
				Texture.largeChestLeft,
				Texture.largeChestTopLeft,
				Texture.largeChestBottomLeft,
				Texture.chestLock,
				Texture.chestLock,
				Texture.chestLock,
				Texture.chestLock,
			},

			// right
			{
				Texture.largeChestFrontRight,
				Texture.largeChestBackRight,
				Texture.largeChestRight,
				Texture.largeChestTopRight,
				Texture.largeChestBottomRight,
				Texture.chestLock,
				Texture.chestLock,
				Texture.chestLock,
				Texture.chestLock,
			}
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ChestModel.intersect(ray, tex[(ray.currentMaterial >> 16) % 3]);
		}
	};

	public static final int REDSTONEWIRE_ID = 0x37;
	public static final Block REDSTONEWIRE = new Block(REDSTONEWIRE_ID, "Redstone Wire", Texture.redstoneWireCross) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RedstoneWireModel.intersect(ray);
		}
		@Override
		public String description(int data) {
			return "power: " + data;
		};
	};
	public static final int DIAMONDORE_ID = 0x38;
	public static final Block DIAMONDORE = new Block(DIAMONDORE_ID, "Diamond Ore", Texture.diamondOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int DIAMONDBLOCK_ID = 0x39;
	public static final Block DIAMONDBLOCK = new Block(DIAMONDBLOCK_ID, "Diamond Block", Texture.diamondBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final int WORKBENCH_ID = 0x3A;
	public static final Block WORKBENCH = new Block(WORKBENCH_ID, "Workbench", Texture.workbenchFront) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.workbenchFront,
			Texture.workbenchSide,
			Texture.workbenchSide,
			Texture.workbenchFront,
			Texture.workbenchTop,
			Texture.oakPlanks,
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex);
		}
	};
	public static final int CROPS_ID = 0x3B;
	public static final Block CROPS = new Block(CROPS_ID, "Wheat", Texture.crops7) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.crops0, Texture.crops1, Texture.crops2, Texture.crops3,
			Texture.crops4, Texture.crops5, Texture.crops6, Texture.crops7
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
		}
	};
	public static final int SOIL_ID = 0x3C;
	public static final Block SOIL = new Block(SOIL_ID, "Soil", Texture.farmlandWet) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FarmlandModel.intersect(ray);
		}
	};
	public static final int FURNACEUNLIT_ID = 0x3D;
	public static final Block FURNACEUNLIT = new Block(FURNACEUNLIT_ID, "Furnace", Texture.furnaceUnlitFront) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
				Texture.furnaceUnlitFront,
				Texture.furnaceSide,
				Texture.furnaceSide,
				Texture.furnaceSide,
				Texture.furnaceTop,
				Texture.furnaceTop,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FurnaceModel.intersect(ray, tex);
		}
	};
	public static final int FURNACELIT_ID = 0x3E;
	public static final Block FURNACELIT = new Block(FURNACELIT_ID, "Burning Furnace", Texture.furnaceLitFront) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
				Texture.furnaceLitFront,
				Texture.furnaceSide,
				Texture.furnaceSide,
				Texture.furnaceSide,
				Texture.furnaceTop,
				Texture.furnaceTop,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FurnaceModel.intersect(ray, tex);
		}
	};
	public static final int SIGNPOST_ID = 0x3F;
	public static final Block SIGNPOST = new Block(SIGNPOST_ID, "Sign Post", Icon.signPost) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SignPostModel.intersect(ray);
		}
	};

	public static final int WOODENDOOR_ID = 0x40;
	public static final Block WOODENDOOR = new Block(WOODENDOOR_ID, "Wooden Door", Icon.woodenDoor) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.woodenDoorBottom, Texture.woodenDoorTop
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
		}
	};
	public static final int LADDER_ID = 0x41;
	public static final Block LADDER = new Block(LADDER_ID, "Ladder", Texture.ladder) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LadderModel.intersect(ray);
		}
	};
	public static final int MINECARTTRACKS_ID = 0x42;
	public static final Block MINECARTTRACKS = new Block(MINECARTTRACKS_ID, "Minecart Tracks", Texture.rails) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.rails, Texture.rails, Texture.rails, Texture.rails,
			Texture.rails, Texture.rails, Texture.railsCurved,
			Texture.railsCurved, Texture.railsCurved, Texture.railsCurved
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			int type = ray.getBlockData() % 10;
			return RailModel.intersect(ray, texture[type], type);
		}
	};
	public static final int STONESTAIRS_ID = 0x43;
	public static final Block STONESTAIRS = new Block(STONESTAIRS_ID, "Cobblestone Stairs", Icon.stoneStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.cobblestone);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.stone;
		}
	};
	public static final int WALLSIGN_ID = 0x44;
	public static final Block WALLSIGN = new Block(WALLSIGN_ID, "Wall Sign", Icon.wallSign) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return WallSignModel.intersect(ray);
		}
	};
	public static final int LEVER_ID = 0x45;
	public static final Block LEVER = new Block(LEVER_ID, "Lever", Texture.lever) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LeverModel.intersect(ray);
		}
	};
	public static final int STONEPRESSUREPLATE_ID = 0x46;
	public static final Block STONEPRESSUREPLATE = new Block(STONEPRESSUREPLATE_ID, "Stone Pressure Plate", Icon.stonePressurePlate) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PressurePlateModel.intersect(ray, Texture.stone);
		}
	};

	public static final int IRONDOOR_ID = 0x47;
	public static final Block IRONDOOR = new Block(IRONDOOR_ID, "Iron Door", Icon.ironDoor) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.ironDoorBottom, Texture.ironDoorTop
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
		}
	};
	public static final int WOODENPRESSUREPLATE_ID = 0x48;
	public static final Block WOODENPRESSUREPLATE = new Block(WOODENPRESSUREPLATE_ID, "Wooden Pressure Plate", Icon.woodenPressurePlate) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PressurePlateModel.intersect(ray, Texture.oakPlanks);
		}
	};
	public static final int REDSTONEORE_ID = 0x49;
	public static final Block REDSTONEORE = new Block(REDSTONEORE_ID, "Redstone Ore", Texture.redstoneOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int GLOWINGREDSTONEORE_ID = 0x4A;
	public static final Block GLOWINGREDSTONEORE = new Block(GLOWINGREDSTONEORE_ID, "Glowing Redstone Ore", Texture.redstoneOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int REDSTONETORCHOFF_ID = 0x4B;
	public static final Block REDSTONETORCHOFF = new Block(REDSTONETORCHOFF_ID, "Redstone Torch (off)", Texture.redstoneTorchOff) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TorchModel.intersect(ray, Texture.redstoneTorchOff);
		}
		final String[] direction = {
			"", "east", "west", "south", "north", "on floor"
		};
		@Override
		public String description(int data) {
			return direction[data%6];
		};
	};
	public static final int REDSTONETORCHON_ID = 0x4C;
	public static final Block REDSTONETORCHON = new Block(REDSTONETORCHON_ID, "Redstone Torch (on)", Texture.redstoneTorchOn) {
		{
			isOpaque = false;
			isSolid = false;
			isEmitter = true;
			emittance = 1.0;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TorchModel.intersect(ray, Texture.redstoneTorchOn);
		}
		final String[] direction = {
			"", "east", "west", "south", "north", "on floor"
		};
		@Override
		public String description(int data) {
			return direction[data%6];
		};
	};
	public static final int STONEBUTTON_ID = 0x4D;
	public static final Block STONEBUTTON = new Block(STONEBUTTON_ID, "Stone Button", Icon.stoneButton) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ButtonModel.intersect(ray, Texture.stone);
		}
	};
	public static final int SNOW_ID = 0x4E;
	public static final Block SNOW = new Block(SNOW_ID, "Snow", Texture.snowBlock) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SnowModel.intersect(ray);
		}
	};
	public static final int ICE_ID = 0x4F;
	public static final Block ICE = new Block(ICE_ID, "Ice", Texture.ice) {
		{
			isOpaque = false;
			isSolid = true;
			ior = 1.31f;
		}
	};
	public static final int SNOWBLOCK_ID = 0x50;
	public static final Block SNOWBLOCK = new Block(SNOWBLOCK_ID, "Snow Block", Texture.snowBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int CACTUS_ID = 0x51;
	public static final Block CACTUS = new Block(CACTUS_ID, "Cactus", Texture.cactusSide) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CactusModel.intersect(ray);
		}
	};
	public static final int CLAY_ID = 0x52;
	public static final Block CLAY = new Block(CLAY_ID, "Clay", Texture.clay) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int SUGARCANE_ID = 0x53;
	public static final Block SUGARCANE = new Block(SUGARCANE_ID, "Sugar Cane", Texture.sugarCane) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.sugarCane);
		}
	};
	public static final int JUKEBOX_ID = 0x54;
	public static final Block JUKEBOX = new Block(JUKEBOX_ID, "Jukebox", Texture.jukeboxSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.jukeboxSide,
			Texture.jukeboxSide,
			Texture.jukeboxSide,
			Texture.jukeboxSide,
			Texture.jukeboxTop,
			Texture.jukeboxSide,
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex);
		}
	};
	public static final int FENCE_ID = 0x55;
	public static final Block FENCE = new Block(FENCE_ID, "Fence", Icon.fence) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceModel.intersect(ray, Texture.oakPlanks);
		}
		@Override
		protected boolean isFence() {
			return true;
		}
	};
	public static final int PUMPKIN_ID = 0x56;
	public static final Block PUMPKIN = new Block(PUMPKIN_ID, "Pumpkin", Texture.pumpkinSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
				Texture.pumpkinFront,
				Texture.pumpkinSide,
				Texture.pumpkinSide,
				Texture.pumpkinSide,
				Texture.pumpkinTop,
				Texture.pumpkinTop,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PumpkinModel.intersect(ray, tex);
		}
	};
	public static final int NETHERRACK_ID = 0x57;
	public static final Block NETHERRACK = new Block(NETHERRACK_ID, "Netherrack", Texture.netherrack) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int SOULSAND_ID = 0x58;
	public static final Block SOULSAND = new Block(SOULSAND_ID, "Soul Sand", Texture.soulsand) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int GLOWSTONE_ID = 0x59;
	public static final Block GLOWSTONE = new Block(GLOWSTONE_ID, "Glowstone", Texture.glowstone) {
		{
			isOpaque = true;
			isSolid = true;
			isEmitter = true;
			emittance = 1.0;
		}
	};
	public static final int PORTAL_ID = 0x5A;
	public static final Block PORTAL = new Block(PORTAL_ID, "Portal", Texture.portal) {
		{
			isOpaque = false;
			isSolid = false;
		}
	};
	public static final int JACKOLANTERN_ID = 0x5B;
	public static final Block JACKOLANTERN = new Block(JACKOLANTERN_ID, "Jack-O-Lantern", Texture.jackolanternFront) {
		{
			isOpaque = true;
			isSolid = true;
			isEmitter = true;
			emittance = 1.0;
			localIntersect = true;
		}
		final Texture[] tex = {
				Texture.jackolanternFront,
				Texture.pumpkinSide,
				Texture.pumpkinSide,
				Texture.pumpkinSide,
				Texture.pumpkinTop,
				Texture.pumpkinTop,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PumpkinModel.intersect(ray, tex);
		}
	};
	public static final int CAKE_ID = 0x5C;
	public static final Block CAKE = new Block(CAKE_ID, "Cake Block", Icon.cake) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CakeModel.intersect(ray);
		}
	};
	public static final int REDSTONEREPEATEROFF_ID = 0x5D;
	public static final Block REDSTONEREPEATEROFF = new Block(REDSTONEREPEATEROFF_ID, "Redstone Repeater (off)", Texture.redstoneRepeaterOff) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RedstoneRepeaterModel.intersect(ray, 0);
		}
	};
	public static final int REDSTONEREPEATERON_ID = 0x5E;
	public static final Block REDSTONEREPEATERON = new Block(REDSTONEREPEATERON_ID, "Redstone Repeater (on)", Texture.redstoneRepeaterOn) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RedstoneRepeaterModel.intersect(ray, 1);
		}
	};
	public static final int STAINED_GLASS_ID = 0x5F;
	public static final Block STAINED_GLASS = new Block(STAINED_GLASS_ID, "Stained Glass", Texture.glass) {
		{
			isOpaque = false;
			isSolid = true;
			ior = 1.520f;
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.stainedGlass[blockData];
		}
		@Override
		public String description(int data) {
			return woolColor[data&15];
		}
		@Override
		public boolean isSameMaterial(Block other) {
			return other == this || other == GLASS;
		}
	};
	public static final int TRAPDOOR_ID = 0x60;
	public static final Block TRAPDOOR = new Block(TRAPDOOR_ID, "Trapdoor", Texture.trapdoor) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TrapdoorModel.intersect(ray, Texture.trapdoor);
		}
	};
	public static final int HIDDENSILVERFISH_ID = 0x61;
	public static final Block HIDDENSILVERFISH = new Block(HIDDENSILVERFISH_ID, "Hidden Silverfish", Texture.stone) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.stone,
			Texture.cobblestone,
			Texture.stoneBrick
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					tex[ray.getBlockData() % tex.length]);
		}
	};
	public static final int STONEBRICKS_ID = 0x62;
	public static final Block STONEBRICKS = new Block(STONEBRICKS_ID, "Stone Bricks", Texture.stoneBrick) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.stoneBrick, Texture.mossyStoneBrick,
			Texture.crackedStoneBrick, Texture.circleStoneBrick
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, texture[(ray.currentMaterial>>8)&3]);
		}
	};
	public static final int HUGEBROWNMUSHROOM_ID = 0x63;
	public static final Block HUGEBROWNMUSHROOM = new Block(HUGEBROWNMUSHROOM_ID, "Huge Brown Mushroom", Texture.hugeBrownMushroom) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] tex =  {
				// fleshy
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores
				},

				// cap on top, west, north
				{
					Texture.hugeBrownMushroom, Texture.mushroomPores,
					Texture.mushroomPores, Texture.hugeBrownMushroom,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top, north
				{
					Texture.hugeBrownMushroom, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top, east, north
				{
					Texture.hugeBrownMushroom, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top, west
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.hugeBrownMushroom,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top, east
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top, west, south
				{
					Texture.mushroomPores, Texture.hugeBrownMushroom,
					Texture.mushroomPores, Texture.hugeBrownMushroom,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top, south
				{
					Texture.mushroomPores, Texture.hugeBrownMushroom,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// cap on top, east, south
				{
					Texture.mushroomPores, Texture.hugeBrownMushroom,
					Texture.hugeBrownMushroom, Texture.mushroomPores,
					Texture.hugeBrownMushroom, Texture.mushroomPores
				},

				// stem
				{
					Texture.mushroomStem, Texture.mushroomStem,
					Texture.mushroomStem, Texture.mushroomStem,
					Texture.mushroomPores, Texture.mushroomPores
				},
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex[ray.getBlockData() % 11]);
		}
	};
	public static final int HUGEREDMUSHROOM_ID = 0x64;
	public static final Block HUGEREDMUSHROOM = new Block(HUGEREDMUSHROOM_ID, "Huge Red Mushroom", Texture.hugeRedMushroom) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] tex =  {
				// fleshy
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores
				},

				// cap on top, west, north
				{
					Texture.hugeRedMushroom, Texture.mushroomPores,
					Texture.mushroomPores, Texture.hugeRedMushroom,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top, north
				{
					Texture.hugeRedMushroom, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top, east, north
				{
					Texture.hugeRedMushroom, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top, west
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.hugeRedMushroom,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top, east
				{
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top, west, south
				{
					Texture.mushroomPores, Texture.hugeRedMushroom,
					Texture.mushroomPores, Texture.hugeRedMushroom,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top, south
				{
					Texture.mushroomPores, Texture.hugeRedMushroom,
					Texture.mushroomPores, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// cap on top, east, south
				{
					Texture.mushroomPores, Texture.hugeRedMushroom,
					Texture.hugeRedMushroom, Texture.mushroomPores,
					Texture.hugeRedMushroom, Texture.mushroomPores
				},

				// stem
				{
					Texture.mushroomStem, Texture.mushroomStem,
					Texture.mushroomStem, Texture.mushroomStem,
					Texture.mushroomPores, Texture.mushroomPores
				},
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex[ray.getBlockData() % 11]);
		}
	};
	public static final int IRONBARS_ID = 0x65;
	public static final Block IRONBARS = new Block(IRONBARS_ID, "Iron Bars", Texture.ironBars) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return GlassPaneModel.intersect(ray, Texture.ironBars, Texture.ironBars);
		}
	};
	public static final int GLASSPANE_ID = 0x66;
	public static final Block GLASSPANE = new Block(GLASSPANE_ID, "Glass Pane", Texture.glass) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			ior = 1.520f;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return GlassPaneModel.intersect(ray, Texture.glass, Texture.glassPaneSide);
		}
	};
	public static final int MELON_ID = 0x67;
	public static final Block MELON = new Block(MELON_ID, "Melon", Texture.melonSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture tex[] = {
				Texture.melonSide, Texture.melonSide,
				Texture.melonSide, Texture.melonSide,
				Texture.melonTop, Texture.melonTop
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex);
		}
	};
	public static final int PUMPKINSTEM_ID = 0x68;
	public static final Block PUMPKINSTEM = new Block(PUMPKINSTEM_ID, "Pumpkin Stem", Texture.stemStraight) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return MelonStemModel.intersect(ray);
		}
	};
	public static final int MELONSTEM_ID = 0x69;
	public static final Block MELONSTEM = new Block(MELONSTEM_ID, "Melon Stem", Texture.stemStraight) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return MelonStemModel.intersect(ray);
		}
	};

	public static final int VINES_ID = 0x6A;
	public static final Block VINES = new Block(VINES_ID, "Vines", Texture.vines) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return VineModel.intersect(ray, scene);
		}
	};
	public static final int FENCEGATE_ID = 0x6B;
	public static final Block FENCEGATE = new Block(FENCEGATE_ID, "Fence Gate", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceGateModel.intersect(ray, Texture.oakPlanks);
		}
		@Override
		protected boolean isFenceGate() {
			return true;
		}
	};
	public static final int BRICKSTAIRS_ID = 0x6C;
	public static final Block BRICKSTAIRS = new Block(BRICKSTAIRS_ID, "Brick Stairs", Icon.stoneStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.brick);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.brick;
		}
	};
	public static final int STONEBRICKSTAIRS_ID = 0x6D;
	public static final Block STONEBRICKSTAIRS = new Block(STONEBRICKSTAIRS_ID, "Stone Brick Stairs", Icon.stoneBrickStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.stoneBrick);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.stoneBrick;
		}
	};
	public static final int MYCELIUM_ID = 0x6E;
	public static final Block MYCELIUM = new Block(MYCELIUM_ID, "Mycelium", Texture.myceliumSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture tex[] = {
				Texture.myceliumSide, Texture.myceliumSide,
				Texture.myceliumSide, Texture.myceliumSide,
				Texture.myceliumTop, Texture.dirt
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.myceliumTop;
		}
	};
	public static final int LILY_PAD_ID = 0x6F;
	public static final Block LILY_PAD = new Block(LILY_PAD_ID, "Lily Pad", Texture.lilyPad) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			isInvisible = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PancakeModel.intersect(ray, Texture.lilyPad);
		}
	};
	public static final int NETHERBRICK_ID = 0x70;
	public static final Block NETHERBRICK = new Block(NETHERBRICK_ID, "Nether Brick", Texture.netherBrick) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int NETHERBRICKFENCE_ID = 0x71;
	public static final Block NETHERBRICKFENCE = new Block(NETHERBRICKFENCE_ID, "Nether Brick Fence", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceModel.intersect(ray, Texture.netherBrick);
		}
		// isFence should return false since nether brick fence does not connect to normal fence
	};
	public static final int NETHERBRICKSTAIRS_ID = 0x72;
	public static final Block NETHERBRICKSTAIRS = new Block(NETHERBRICKSTAIRS_ID, "Nether Brick Stairs", Icon.stoneStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.netherBrick);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.netherBrick;
		}
	};
	public static final int NETHERWART_ID = 0x73;
	public static final Block NETHERWART = new Block(NETHERWART_ID, "Nether Wart", Texture.netherWart2) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.netherWart0, Texture.netherWart1, Texture.netherWart1,
			Texture.netherWart2
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray, texture[ray.getBlockData() & 3]);
		}
	};
	public static final int ENCHNATMENTTABLE_ID = 0x74;
	public static final Block ENCHNATMENTTABLE = new Block(ENCHNATMENTTABLE_ID, "Enchantment Table", Texture.enchantmentTableSide) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return EnchantmentTableModel.intersect(ray);
		}
	};
	public static final int BREWINGSTAND_ID = 0x75;
	public static final Block BREWINGSTAND = new Block(BREWINGSTAND_ID, "Brewing Stand", Texture.brewingStandSide) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return BrewingStandModel.intersect(ray);
		}
	};
	public static final int CAULDRON_ID = 0x76;
	public static final Block CAULDRON = new Block(CAULDRON_ID, "Cauldron", Texture.cauldronSide) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CauldronModel.intersect(ray);
		}
	};
	public static final int ENDPORTAL_ID = 0x77;
	public static final Block ENDPORTAL = new Block(ENDPORTAL_ID, "End Portal", Texture.endPortal) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return EndPortalModel.intersect(ray);
		}
	};
	public static final int ENDPORTALFRAME_ID = 0x78;
	public static final Block ENDPORTALFRAME = new Block(ENDPORTALFRAME_ID, "End Portal Frame", Texture.endPortalFrameTop) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return EndPortalFrameModel.intersect(ray);
		}
	};
	public static final int ENDSTONE_ID = 0x79;
	public static final Block ENDSTONE = new Block(ENDSTONE_ID, "End Stone", Texture.endStone) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
	};
	public static final int DRAGONEGG_ID = 0x7A;
	public static final Block DRAGONEGG = new Block(DRAGONEGG_ID, "Dragon Egg", Texture.dragonEgg) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return DragonEggModel.intersect(ray);
		}
	};
	public static final int REDSTONELAMPOFF_ID = 0x7B;
	public static final Block REDSTONELAMPOFF = new Block(REDSTONELAMPOFF_ID, "Redstone Lamp (off)", Texture.redstoneLampOff) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int REDSTONELAMPON_ID = 0x7C;
	public static final Block REDSTONELAMPON = new Block(REDSTONELAMPON_ID, "Redstone Lamp (on)", Texture.redstoneLampOn) {
		{
			isOpaque = true;
			isSolid = true;
			isEmitter = true;
			emittance = 1.0;
		}
	};
	public static final int DOUBLEWOODENSLAB_ID = 0x7D;
	public static final Block DOUBLEWOODENSLAB = new Block(DOUBLEWOODENSLAB_ID, "Double Wooden Slab", Texture.oakPlanks) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.oakPlanks,
			Texture.sprucePlanks,
			Texture.birchPlanks,
			Texture.jungleTreePlanks,
			Texture.acaciaPlanks,
			Texture.darkOakPlanks,
		};
		final String[] woodKind = {
			"oak",
			"spruce",
			"birch",
			"jungle",
			"acacia",
			"dark oak",
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, tex[ray.getBlockData() % 6]);
		}
		@Override
		public String description(int data) {
			return woodKind[data % 6];
		}
	};
	public static final int SINGLEWOODENSLAB_ID = 0x7E;
	public static final Block SINGLEWOODENSLAB = new Block(SINGLEWOODENSLAB_ID, "Single Wooden Slab", Texture.oakPlanks) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.oakPlanks,
			Texture.sprucePlanks,
			Texture.birchPlanks,
			Texture.jungleTreePlanks,
			Texture.acaciaPlanks,
			Texture.darkOakPlanks,
		};
		final String[] woodKind = {
			"oak",
			"spruce",
			"birch",
			"jungle",
			"acacia",
			"dark oak",
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SlabModel.intersect(ray, tex[(ray.getBlockData()&7) % 6]);
		}
		@Override
		public String description(int data) {
			return woodKind[(data&7) % 6];
		}
	};
	public static final int COCOAPLANT_ID = 0x7F;
	public static final Block COCOAPLANT = new Block(COCOAPLANT_ID, "Cocoa Plant", Texture.cocoaPlantLarge) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CocoaPlantModel.intersect(ray);
		}
		final String[] cocoaSize = {
			"small", "medium", "large"
		};
		@Override
		public String description(int data) {
			return cocoaSize[(data&15)>>2];
		}
	};
	public static final int SANDSTONESTAIRS_ID = 0x80;
	public static final Block SANDSTONESTAIRS = new Block(SANDSTONESTAIRS_ID, "Sandstone Stairs", Icon.stoneStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.sandstoneSide,
					Texture.sandstoneTop, Texture.sandstoneBottom);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.sandstoneSide;
		}
	};
	public static final int EMERALDORE_ID = 0x81;
	public static final Block EMERALDORE = new Block(EMERALDORE_ID, "Emerald Ore", Texture.emeraldOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int ENDERCHEST_ID = 0x82;
	public static final Block ENDERCHEST = new Block(ENDERCHEST_ID, "Ender Chest", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] tex = {
				Texture.enderChestFront,
				Texture.enderChestBack,
				Texture.enderChestLeft,
				Texture.enderChestRight,
				Texture.enderChestTop,
				Texture.enderChestBottom,
				Texture.enderChestLock,
				Texture.enderChestLock,
				Texture.enderChestLock,
				Texture.enderChestLock,
				Texture.enderChestLock,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ChestModel.intersect(ray, tex);
		}
	};
	public static final int TRIPWIREHOOK_ID = 0x83;
	public static final Block TRIPWIREHOOK = new Block(TRIPWIREHOOK_ID, "Tripwire Hook", Texture.tripwireHook) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TripwireHookModel.intersect(ray);
		}
	};
	public static final int TRIPWIRE_ID = 0x84;
	public static final Block TRIPWIRE = new Block(TRIPWIRE_ID, "Tripwire", Texture.tripwire) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TripwireModel.intersection(ray);
		}
	};
	public static final int EMERALDBLOCK_ID = 0x85;
	public static final Block EMERALDBLOCK = new Block(EMERALDBLOCK_ID, "Emerald Block", Texture.emeraldBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final int SPRUCEWOODSTAIRS_ID = 0x86;
	public static final Block SPRUCEWOODSTAIRS = new Block(SPRUCEWOODSTAIRS_ID, "Spruce Wood Stairs", Icon.woodenStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.sprucePlanks);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.sprucePlanks;
		}
	};
	public static final int BIRCHWOODSTAIRS_ID = 0x87;
	public static final Block BIRCHWOODSTAIRS = new Block(BIRCHWOODSTAIRS_ID, "Birch Wood Stairs", Icon.woodenStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.birchPlanks);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.birchPlanks;
		}
	};
	public static final int JUNGLEWOODSTAIRS_ID = 0x88;
	public static final Block JUNGLEWOODSTAIRS = new Block(JUNGLEWOODSTAIRS_ID, "Jungle Wood Stairs", Icon.woodenStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.jungleTreePlanks);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.jungleTreePlanks;
		}
	};
	public static final int COMMANDBLOCK_ID = 0x89;
	public static final Block COMMANDBLOCK = new Block(COMMANDBLOCK_ID, "Command Block", Texture.commandBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int BEACON_ID = 0x8A;
	public static final Block BEACON = new Block(BEACON_ID, "Beacon", Texture.glass) {
		{
			isOpaque = false;
			isSolid = true;
			ior = 1.520f;
			localIntersect = true;
			isEmitter = true;
			emittance = 1.0;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return BeaconModel.intersect(ray);
		}
	};
	public static final int STONEWALL_ID = 0x8B;
	public static final Block STONEWALL = new Block(STONEWALL_ID, "Cobblestone Wall", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] tex = {
				Texture.cobblestone,
				Texture.mossStone
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StoneWallModel.intersect(ray, tex[ray.getBlockData() & 1]);
		}
	};
	public static final int FLOWERPOT_ID = 0x8C;
	public static final Block FLOWERPOT = new Block(FLOWERPOT_ID, "Flower Pot", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FlowerPotModel.intersect(ray, scene);
		}
	};
	public static final int CARROTS_ID = 0x8D;
	public static final Block CARROTS = new Block(CARROTS_ID, "Carrots", Texture.carrots3) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.carrots0, Texture.carrots0, Texture.carrots1,
			Texture.carrots1, Texture.carrots2, Texture.carrots2,
			Texture.carrots2, Texture.carrots3
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
		}
	};
	public static final int POTATOES_ID = 0x8E;
	public static final Block POTATOES = new Block(POTATOES_ID, "Potatoes", Texture.potatoes3) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.potatoes0, Texture.potatoes0, Texture.potatoes1,
			Texture.potatoes1, Texture.potatoes2, Texture.potatoes2,
			Texture.potatoes2, Texture.potatoes3
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
		}
	};
	public static final int WOODENBUTTON_ID = 0x8F;
	public static final Block WOODENBUTTON = new Block(WOODENBUTTON_ID, "Wooden Button", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ButtonModel.intersect(ray, Texture.oakPlanks);
		}
	};
	public static final int HEAD_ID = 0x90;
	public static final Block HEAD = new Block(HEAD_ID, "Head", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			isInvisible = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return HeadModel.intersect(ray, Texture.oakPlanks);
		}
	};
	public static final int ANVIL_ID = 0x91;
	public static final Block ANVIL = new Block(ANVIL_ID, "Anvil", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return AnvilModel.intersect(ray);
		};
	};
	public static final int TRAPPEDCHEST_ID = 0x92;
	public static final Block TRAPPEDCHEST = new Block(TRAPPEDCHEST_ID, "Trapped Chest", Texture.chestFront) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[][] tex = {
			// single
			{
				Texture.trappedChestFront,
				Texture.trappedChestBack,
				Texture.trappedChestLeft,
				Texture.trappedChestRight,
				Texture.trappedChestTop,
				Texture.trappedChestBottom,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
			},

			// left
			{
				Texture.largeTrappedChestFrontLeft,
				Texture.largeTrappedChestBackLeft,
				Texture.largeTrappedChestLeft,
				Texture.largeTrappedChestTopLeft,
				Texture.largeTrappedChestBottomLeft,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
				Texture.chestLock,
			},

			// right
			{
				Texture.largeTrappedChestFrontRight,
				Texture.largeTrappedChestBackRight,
				Texture.largeTrappedChestRight,
				Texture.largeTrappedChestTopRight,
				Texture.largeTrappedChestBottomRight,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
				Texture.trappedChestLock,
			}
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ChestModel.intersect(ray, tex[(ray.currentMaterial >> 16) % 3]);
		}
	};
	public static final int WEIGHTEDPRESSUREPLATELIGHT_ID = 0x93;
	public static final Block WEIGHTEDPRESSUREPLATELIGHT = new Block(WEIGHTEDPRESSUREPLATELIGHT_ID, "Weighted Pressure Plate (Light)", Texture.goldBlock) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PressurePlateModel.intersect(ray, Texture.goldBlock);
		}
	};
	public static final int WEIGHTEDPRESSUREPLATEHEAVY_ID = 0x94;
	public static final Block WEIGHTEDPRESSUREPLATEHEAVY = new Block(WEIGHTEDPRESSUREPLATEHEAVY_ID, "Weighted Pressure Plate (Heavy)", Texture.ironBlock) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return PressurePlateModel.intersect(ray, Texture.ironBlock);
		}
	};
	public static final int REDSTONECOMPARATOR_ID = 0x95;
	public static final Block REDSTONECOMPARATOR = new Block(REDSTONECOMPARATOR_ID, "Redstone Comparator (inactive)", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ComparatorModel.intersect(ray, 0);
		}
	};
	public static final int REDSTONECOMPARATORLIT_ID = 0x96;
	public static final Block REDSTONECOMPARATORLIT = new Block(REDSTONECOMPARATORLIT_ID, "Redstone Comparator (active)", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ComparatorModel.intersect(ray, 1);
		}
	};
	public static final int DAYLIGHTSENSOR_ID = 0x97;
	public static final Block DAYLIGHTSENSOR = new Block(DAYLIGHTSENSOR_ID, "Daylight Sensor", Texture.daylightDetectorTop) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return DaylightSensorModel.intersect(ray);
		}
	};
	public static final int REDSTONEBLOCK_ID = 0x98;
	public static final Block REDSTONEBLOCK = new Block(REDSTONEBLOCK_ID, "Block of Redstone", Texture.redstoneBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int NETHERQUARTZORE_ID = 0x99;
	public static final Block NETHERQUARTZORE = new Block(NETHERQUARTZORE_ID, "Nether Quartz Ore", Texture.netherQuartzOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int HOPPER_ID = 0x9A;
	public static final Block HOPPER = new Block(HOPPER_ID, "Hopper", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return HopperModel.intersect(ray);
		}
	};
	public static final int QUARTZ_ID = 0x9B;
	public static final Block QUARTZ = new Block(QUARTZ_ID, "Block of Quartz", Texture.quartzSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return QuartzModel.intersect(ray);
		}
	};
	public static final int QUARTZSTAIRS_ID = 0x9C;
	public static final Block QUARTZSTAIRS = new Block(QUARTZSTAIRS_ID, "Quartz Stairs", Icon.stoneStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.quartzSide,
					Texture.quartzTop, Texture.quartzBottom);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.quartzSide;
		}
	};
	public static final int ACTIVATORRAIL_ID = 0x9D;
	public static final Block ACTIVATORRAIL = new Block(ACTIVATORRAIL_ID, "Activator Rail", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.activatorRail, Texture.activatorRailPowered
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RailModel.intersect(ray,
					texture[ray.getBlockData() >>> 3],
					(ray.getBlockData() & 7) % 6);
		}
	};
	public static final int DROPPER_ID = 0x9E;
	public static final Block DROPPER = new Block(DROPPER_ID, "Dropper", Texture.dropperFront) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] tex = {
			Texture.dropperFront,
			Texture.furnaceSide,
			Texture.furnaceSide,
			Texture.furnaceSide,
			Texture.furnaceTop,
			Texture.furnaceTop,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FurnaceModel.intersect(ray, tex);
		}
	};
	public static final int STAINED_CLAY_ID = 0x9F;
	public static final Block STAINED_CLAY = new Block(STAINED_CLAY_ID, "Stained Clay", Texture.clay) {
		{
			isOpaque = true;
			isSolid = true;
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.stainedClay[blockData];
		}
		@Override
		public String description(int data) {
			return woolColor[data&15];
		}
	};
	public static final int STAINED_GLASSPANE_ID = 0xA0;
	public static final Block STAINED_GLASSPANE = new Block(STAINED_GLASSPANE_ID, "Stained Glass Pane", Texture.glass) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			ior = 1.520f;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			int data = ray.getBlockData();
			return GlassPaneModel.intersect(ray,
					Texture.stainedGlass[data],
					Texture.stainedGlassPaneSide[data]);
		}
		@Override
		public String description(int data) {
			return woolColor[data&15];
		}
	};
	public static final int LEAVES2_ID = 0xA1;
	public static final Block LEAVES2 = new Block(LEAVES2_ID, "Leaves", Texture.oakLeaves) {
		{
			isOpaque = false;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.acaciaLeaves, Texture.darkOakLeaves, Texture.acaciaLeaves,
			Texture.darkOakLeaves
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LeafModel.intersect(ray, scene, getTexture(ray.getBlockData()));
		}
		final String[] woodType = {
			"acacia", "dark oak", "unknown", "unknown"
		};
		@Override
		public String description(int data) {
			return woodType[data&3];
		}
		@Override
		public Texture getTexture(int blockData) {
			return texture[blockData & 3];
		}
	};
	public static final int WOOD2_ID = 0xA2;
	public static final Block WOOD2 = new Block(WOOD2_ID, "Wood", Texture.oakWood) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] texture = {
			{ Texture.acaciaWood, Texture.acaciaWoodTop },
			{ Texture.darkOakWood, Texture.darkOakWoodTop },
			{ Texture.acaciaWood, Texture.acaciaWoodTop },
			{ Texture.darkOakWood, Texture.darkOakWoodTop }
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return WoodModel.intersect(ray, texture[ray.getBlockData()&3]);
		}
		final String[] woodType = {
			"acacia", "dark oak", "unknown", "unknown"
		};
		@Override
		public String description(int data) {
			return woodType[data&3];
		}
		@Override
		public Texture getTexture(int blockData) {
			return texture[blockData&3][1];
		}
	};
	public static final int ACACIASTAIRS_ID = 0xA3;
	public static final Block ACACIASTAIRS = new Block(ACACIASTAIRS_ID, "Acacia Stairs", Icon.woodenStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.acaciaPlanks);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.acaciaPlanks;
		}
	};
	public static final int DARKOAKSTAIRS_ID = 0xA4;
	public static final Block DARKOAKSTAIRS = new Block(DARKOAKSTAIRS_ID, "Dark Oak Stairs", Icon.woodenStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.darkOakPlanks);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.darkOakPlanks;
		}
	};
	public static final int SLIMEBLOCK_ID = 0xA5;
	public static final Block SLIMEBLOCK = new Block(SLIMEBLOCK_ID, "Slime Block", Texture.slime) {
		{
			isOpaque = false;
			isSolid = true;
			localIntersect = false;
		}
	};
	public static final int BARRIER_ID = 0xA6;
	public static final Block BARRIER = new Block(BARRIER_ID, "Barrier", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final int IRON_TRAPDOOR_ID = 0xA7;
	public static final Block IRON_TRAPDOOR = new Block(IRON_TRAPDOOR_ID, "Iron Trapdoor", Texture.ironTrapdoor) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TrapdoorModel.intersect(ray, Texture.ironTrapdoor);
		}
	};
	public static final int PRISMARINE_ID = 0xA8;
	public static final Block PRISMARINE = new Block(PRISMARINE_ID, "Prismarine", Texture.prismarine) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] textures = {
			{
				Texture.prismarine,
				Texture.prismarine,
				Texture.prismarine,
				Texture.prismarine,
				Texture.prismarine,
				Texture.prismarine,
			},
			{
				Texture.prismarineBricks,
				Texture.prismarineBricks,
				Texture.prismarineBricks,
				Texture.prismarineBricks,
				Texture.prismarineBricks,
				Texture.prismarineBricks,
			},
			{
				Texture.darkPrismarine,
				Texture.darkPrismarine,
				Texture.darkPrismarine,
				Texture.darkPrismarine,
				Texture.darkPrismarine,
				Texture.darkPrismarine,
			},
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					textures[ray.getBlockData() % 3]);
		}
		final String[] kind = {
			"rough", "bricks", "dark"
		};
		@Override
		public String description(int data) {
			return kind[data%3];
		}
	};
	public static final int SEALANTERN_ID = 0xA9;
	public static final Block SEALANTERN = new Block(SEALANTERN_ID, "Sea Lantern", Texture.seaLantern) {
		{
			isOpaque = true;
			isSolid = true;
			isEmitter = true;
			emittance = 0.5;
		}
	};
	public static final int HAY_BLOCK_ID = 0xAA;
	public static final Block HAY_BLOCK = new Block(HAY_BLOCK_ID, "Hay Block", Texture.hayBlockSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[] texture = {
			Texture.hayBlockSide,
			Texture.hayBlockTop,
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return WoodModel.intersect(ray, texture);
		}
	};
	public static final int CARPET_ID = 0xAB;
	public static final Block CARPET = new Block(CARPET_ID, "Carpet", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CarpetModel.intersect(ray,
					Texture.wool[ray.getBlockData()]);
		}
		@Override
		public String description(int data) {
			return woolColor[data&15];
		}
	};
	public static final int HARDENED_CLAY_ID = 0xAC;
	public static final Block HARDENED_CLAY = new Block(HARDENED_CLAY_ID, "Hardened Clay", Texture.hardenedClay) {
		{
			isOpaque = true;
			isSolid = true;
			isInvisible = false;
		}

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, Texture.hardenedClay);
		}
	};
	public static final int BLOCK_OF_COAL_ID = 0xAD;
	public static final Block BLOCK_OF_COAL = new Block(BLOCK_OF_COAL_ID, "Block of Coal", Texture.coalBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int PACKED_ICE_ID = 0xAE;
	public static final Block PACKED_ICE = new Block(PACKED_ICE_ID, "Unknown Block 0xAE", Texture.packedIce) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int LARGE_FLOWER_ID = 0xAF;
	public static final Block LARGE_FLOWER = new Block(LARGE_FLOWER_ID, "Large Flower", Texture.dandelion) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			subSurfaceScattering = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LargeFlowerModel.intersect(ray, scene);
		}
	};
	public static final int UNKNOWN0xB0_ID = 0xB0;
	public static final Block UNKNOWN0xB0 = new Block(UNKNOWN0xB0_ID, "Unknown Block 0xB0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final int UNKNOWN0xB1_ID = 0xB1;
	public static final Block UNKNOWN0xB1 = new Block(UNKNOWN0xB1_ID, "Unknown Block 0xB1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final int UNKNOWN0xB2_ID = 0xB2;
	public static final Block UNKNOWN0xB2 = new Block(UNKNOWN0xB2_ID, "Unknown Block 0xB2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final int REDSANDSTONE_ID = 0xB3;
	public static final Block REDSANDSTONE = new Block(REDSANDSTONE_ID, "Red Sandstone", Texture.redSandstoneSide) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		final Texture[][] tex = {
			// normal
			{
				Texture.redSandstoneSide,
				Texture.redSandstoneSide,
				Texture.redSandstoneSide,
				Texture.redSandstoneSide,
				Texture.redSandstoneTop,
				Texture.redSandstoneBottom,
			},

			// decorated
			{
				Texture.redSandstoneDecorated,
				Texture.redSandstoneDecorated,
				Texture.redSandstoneDecorated,
				Texture.redSandstoneDecorated,
				Texture.redSandstoneTop,
				Texture.redSandstoneBottom,
			},

			// smooth
			{
				Texture.redSandstoneSmooth,
				Texture.redSandstoneSmooth,
				Texture.redSandstoneSmooth,
				Texture.redSandstoneSmooth,
				Texture.redSandstoneTop,
				Texture.redSandstoneBottom,
			},
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					tex[ray.getBlockData() % 3]);
		}
	};
	public static final int REDSANDSTONESTAIRS_ID = 0xB4;
	public static final Block REDSANDSTONESTAIRS = new Block(REDSANDSTONESTAIRS_ID, "Red Sandstone Stairs", Icon.stoneStairs) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.redSandstoneSide,
					Texture.redSandstoneTop, Texture.redSandstoneBottom);
		}
		@Override
		public Texture getTexture(int blockData) {
			return Texture.redSandstoneSide;
		}
	};
	public static final int DOUBLESLAB2_ID = 0xB5;
	public static final Block DOUBLESLAB2 = new Block(DOUBLESLAB2_ID, "Double Slab2", Texture.redSandstoneTop) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}

		final Texture[] textures = {
			Texture.redSandstoneSide,
			Texture.redSandstoneSide,
			Texture.redSandstoneSide,
			Texture.redSandstoneSide,
			Texture.redSandstoneTop,
			Texture.redSandstoneTop,
		};

		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray, textures);
		}
	};
	public static final int SLAB2_ID = 0xB6;
	public static final Block SLAB2 = new Block(SLAB2_ID, "Slab2", Texture.redSandstoneTop) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] textures = {
			Texture.redSandstoneSide, Texture.redSandstoneTop
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SlabModel.intersect(ray, textures[0], textures[1]);
		}
	};
	public static final int SPRUCEFENCEGATE_ID = 0xB7;
	public static final Block SPRUCEFENCEGATE = new Block(SPRUCEFENCEGATE_ID, "Spruce Fence Gate", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceGateModel.intersect(ray, Texture.sprucePlanks);
		}
		@Override
		protected boolean isFence() {
			return true;
		}
		@Override
		protected boolean isFenceGate() {
			return true;
		}
	};
	public static final int BIRCHFENCEGATE_ID = 0xB8;
	public static final Block BIRCHFENCEGATE = new Block(BIRCHFENCEGATE_ID, "Birch Fence Gate", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceGateModel.intersect(ray, Texture.birchPlanks);
		}
		@Override
		protected boolean isFenceGate() {
			return true;
		}
	};
	public static final int JUNGLEFENCEGATE_ID = 0xB9;
	public static final Block JUNGLEFENCEGATE = new Block(JUNGLEFENCEGATE_ID, "Jungle Fence Gate", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceGateModel.intersect(ray, Texture.jungleTreePlanks);
		}
		@Override
		protected boolean isFenceGate() {
			return true;
		}
	};
	public static final int DARKOAKFENCEGATE_ID = 0xBA;
	public static final Block DARKOAKFENCEGATE = new Block(DARKOAKFENCEGATE_ID, "Dark Oak Fence Gate", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceGateModel.intersect(ray, Texture.darkOakPlanks);
		}
		@Override
		protected boolean isFenceGate() {
			return true;
		}
	};
	public static final int ACACIAFENCEGATE_ID = 0xBB;
	public static final Block ACACIAFENCEGATE = new Block(ACACIAFENCEGATE_ID, "Acacia Fence Gate", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceGateModel.intersect(ray, Texture.acaciaPlanks);
		}
		@Override
		protected boolean isFenceGate() {
			return true;
		}
	};
	public static final int SPRUCEFENCE_ID = 0xBC;
	public static final Block SPRUCEFENCE = new Block(SPRUCEFENCE_ID, "Spruce Fence", Icon.fence) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceModel.intersect(ray, Texture.sprucePlanks);
		}
		@Override
		protected boolean isFence() {
			return true;
		}
	};
	public static final int BIRCHFENCE_ID = 0xBD;
	public static final Block BIRCHFENCE = new Block(BIRCHFENCE_ID, "Birch Fence", Icon.fence) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceModel.intersect(ray, Texture.birchPlanks);
		}
		@Override
		protected boolean isFence() {
			return true;
		}
	};
	public static final int JUNGLEFENCE_ID = 0xBE;
	public static final Block JUNGLEFENCE = new Block(JUNGLEFENCE_ID, "Jungle Fence", Icon.fence) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceModel.intersect(ray, Texture.jungleTreePlanks);
		}
		@Override
		protected boolean isFence() {
			return true;
		}
	};
	public static final int DARKOAKFENCE_ID = 0xBF;
	public static final Block DARKOAKFENCE = new Block(DARKOAKFENCE_ID, "Dark Oak Fence", Icon.fence) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceModel.intersect(ray, Texture.darkOakPlanks);
		}
		@Override
		protected boolean isFence() {
			return true;
		}
	};
	public static final int ACACIAFENCE_ID = 0xC0;
	public static final Block ACACIAFENCE = new Block(ACACIAFENCE_ID, "Acacia Fence", Icon.fence) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return FenceModel.intersect(ray, Texture.acaciaPlanks);
		}
		@Override
		protected boolean isFence() {
			return true;
		}
	};
	public static final Block UNKNOWN0xC1 = new Block(0xC1, "Unknown Block 0xC1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC2 = new Block(0xC2, "Unknown Block 0xC2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC3 = new Block(0xC3, "Unknown Block 0xC3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC4 = new Block(0xC4, "Unknown Block 0xC4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC5 = new Block(0xC5, "Unknown Block 0xC5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC6 = new Block(0xC6, "Unknown Block 0xC6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC7 = new Block(0xC7, "Unknown Block 0xC7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC8 = new Block(0xC8, "Unknown Block 0xC8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xC9 = new Block(0xC9, "Unknown Block 0xC9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xCA = new Block(0xCA, "Unknown Block 0xCA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xCB = new Block(0xCB, "Unknown Block 0xCB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xCC = new Block(0xCC, "Unknown Block 0xCC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xCD = new Block(0xCD, "Unknown Block 0xCD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xCE = new Block(0xCE, "Unknown Block 0xCE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xCF = new Block(0xCF, "Unknown Block 0xCF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD0 = new Block(0xD0, "Unknown Block 0xD0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD1 = new Block(0xD1, "Unknown Block 0xD1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD2 = new Block(0xD2, "Unknown Block 0xD2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD3 = new Block(0xD3, "Unknown Block 0xD3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD4 = new Block(0xD4, "Unknown Block 0xD4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD5 = new Block(0xD5, "Unknown Block 0xD5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD6 = new Block(0xD6, "Unknown Block 0xD6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD7 = new Block(0xD7, "Unknown Block 0xD7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD8 = new Block(0xD8, "Unknown Block 0xD8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xD9 = new Block(0xD9, "Unknown Block 0xD9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xDA = new Block(0xDA, "Unknown Block 0xDA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xDB = new Block(0xDB, "Unknown Block 0xDB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xDC = new Block(0xDC, "Unknown Block 0xDC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xDD = new Block(0xDD, "Unknown Block 0xDD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xDE = new Block(0xDE, "Unknown Block 0xDE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xDF = new Block(0xDF, "Unknown Block 0xDF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE0 = new Block(0xE0, "Unknown Block 0xE0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE1 = new Block(0xE1, "Unknown Block 0xE1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE2 = new Block(0xE2, "Unknown Block 0xE2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE3 = new Block(0xE3, "Unknown Block 0xE3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE4 = new Block(0xE4, "Unknown Block 0xE4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE5 = new Block(0xE5, "Unknown Block 0xE5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE6 = new Block(0xE6, "Unknown Block 0xE6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE7 = new Block(0xE7, "Unknown Block 0xE7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE8 = new Block(0xE8, "Unknown Block 0xE8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xE9 = new Block(0xE9, "Unknown Block 0xE9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xEA = new Block(0xEA, "Unknown Block 0xEA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xEB = new Block(0xEB, "Unknown Block 0xEB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xEC = new Block(0xEC, "Unknown Block 0xEC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xED = new Block(0xED, "Unknown Block 0xED", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xEE = new Block(0xEE, "Unknown Block 0xEE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xEF = new Block(0xEF, "Unknown Block 0xEF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF0 = new Block(0xF0, "Unknown Block 0xF0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF1 = new Block(0xF1, "Unknown Block 0xF1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF2 = new Block(0xF2, "Unknown Block 0xF2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF3 = new Block(0xF3, "Unknown Block 0xF3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF4 = new Block(0xF4, "Unknown Block 0xF4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF5 = new Block(0xF5, "Unknown Block 0xF5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF6 = new Block(0xF6, "Unknown Block 0xF6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF7 = new Block(0xF7, "Unknown Block 0xF7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF8 = new Block(0xF8, "Unknown Block 0xF8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xF9 = new Block(0xF9, "Unknown Block 0xF9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xFA = new Block(0xFA, "Unknown Block 0xFA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xFB = new Block(0xFB, "Unknown Block 0xFB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xFC = new Block(0xFC, "Unknown Block 0xFC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xFD = new Block(0xFD, "Unknown Block 0xFD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xFE = new Block(0xFE, "Unknown Block 0xFE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};
	public static final Block UNKNOWN0xFF = new Block(0xFF, "Unknown Block 0xFF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = UNKNOWN_INVISIBLE;
		}
	};

	public static final Block[] values = {
		AIR, STONE, GRASS, DIRT,
		COBBLESTONE, WOODENPLANKS, SAPLING, BEDROCK,
		WATER, STATIONARYWATER, LAVA, STATIONARYLAVA,
		SAND, GRAVEL, GOLDORE, IRONORE,
		COALORE, WOOD, LEAVES, SPONGE,
		GLASS, LAPISLAZULIORE, LAPISLAZULIBLOCK, DISPENSER,
		SANDSTONE, NOTEBLOCK, BED, POWEREDRAIL,
		DETECTORRAIL, STICKYPISTON, COBWEB, TALLGRASS,
		DEADBUSH, PISTON, PISTONEXTENSION, WOOL,
		MOVEDBYPISTON, DANDELION, FLOWER, BROWNMUSHROOM,
		REDMUSHROOM, GOLDBLOCK, IRONBLOCK, DOUBLESLAB,
		SLAB, BRICKS, TNT, BOOKSHELF,
		MOSSSTONE, OBSIDIAN, TORCH, FIRE,
		MONSTERSPAWNER, OAKWOODSTAIRS, CHEST, REDSTONEWIRE,
		DIAMONDORE, DIAMONDBLOCK, WORKBENCH, CROPS,
		SOIL, FURNACEUNLIT, FURNACELIT, SIGNPOST,
		WOODENDOOR, LADDER, MINECARTTRACKS, STONESTAIRS,
		WALLSIGN, LEVER, STONEPRESSUREPLATE, IRONDOOR,
		WOODENPRESSUREPLATE, REDSTONEORE, GLOWINGREDSTONEORE, REDSTONETORCHOFF,
		REDSTONETORCHON, STONEBUTTON, SNOW, ICE,
		SNOWBLOCK, CACTUS, CLAY, SUGARCANE,
		JUKEBOX, FENCE, PUMPKIN, NETHERRACK,
		SOULSAND, GLOWSTONE, PORTAL, JACKOLANTERN,
		CAKE, REDSTONEREPEATEROFF, REDSTONEREPEATERON, STAINED_GLASS,
		TRAPDOOR, HIDDENSILVERFISH, STONEBRICKS, HUGEBROWNMUSHROOM,
		HUGEREDMUSHROOM, IRONBARS, GLASSPANE, MELON,
		PUMPKINSTEM, MELONSTEM, VINES, FENCEGATE,
		BRICKSTAIRS, STONEBRICKSTAIRS, MYCELIUM, LILY_PAD,
		NETHERBRICK, NETHERBRICKFENCE, NETHERBRICKSTAIRS, NETHERWART,
		ENCHNATMENTTABLE, BREWINGSTAND, CAULDRON, ENDPORTAL,
		ENDPORTALFRAME, ENDSTONE, DRAGONEGG, REDSTONELAMPOFF,
		REDSTONELAMPON, DOUBLEWOODENSLAB, SINGLEWOODENSLAB, COCOAPLANT,
		SANDSTONESTAIRS, EMERALDORE, ENDERCHEST, TRIPWIREHOOK,
		TRIPWIRE, EMERALDBLOCK, SPRUCEWOODSTAIRS, BIRCHWOODSTAIRS,
		JUNGLEWOODSTAIRS, COMMANDBLOCK, BEACON, STONEWALL,
		FLOWERPOT, CARROTS, POTATOES, WOODENBUTTON,
		HEAD, ANVIL, TRAPPEDCHEST, WEIGHTEDPRESSUREPLATELIGHT,
		WEIGHTEDPRESSUREPLATEHEAVY, REDSTONECOMPARATOR, REDSTONECOMPARATORLIT, DAYLIGHTSENSOR,
		REDSTONEBLOCK, NETHERQUARTZORE, HOPPER, QUARTZ,
		QUARTZSTAIRS, ACTIVATORRAIL, DROPPER, STAINED_CLAY,
		STAINED_GLASSPANE, LEAVES2, WOOD2, ACACIASTAIRS,
		DARKOAKSTAIRS, SLIMEBLOCK, BARRIER, IRON_TRAPDOOR,
		PRISMARINE, SEALANTERN, HAY_BLOCK, CARPET,
		HARDENED_CLAY, BLOCK_OF_COAL, PACKED_ICE, LARGE_FLOWER,
		UNKNOWN0xB0, UNKNOWN0xB1, UNKNOWN0xB2, REDSANDSTONE,
		REDSANDSTONESTAIRS, DOUBLESLAB2, SLAB2, SPRUCEFENCEGATE,
		BIRCHFENCEGATE, JUNGLEFENCEGATE, DARKOAKFENCEGATE, ACACIAFENCEGATE,
		SPRUCEFENCE, BIRCHFENCE, JUNGLEFENCE, DARKOAKFENCE,
		ACACIAFENCE, UNKNOWN0xC1, UNKNOWN0xC2, UNKNOWN0xC3,
		UNKNOWN0xC4, UNKNOWN0xC5, UNKNOWN0xC6, UNKNOWN0xC7,
		UNKNOWN0xC8, UNKNOWN0xC9, UNKNOWN0xCA, UNKNOWN0xCB,
		UNKNOWN0xCC, UNKNOWN0xCD, UNKNOWN0xCE, UNKNOWN0xCF,
		UNKNOWN0xD0, UNKNOWN0xD1, UNKNOWN0xD2, UNKNOWN0xD3,
		UNKNOWN0xD4, UNKNOWN0xD5, UNKNOWN0xD6, UNKNOWN0xD7,
		UNKNOWN0xD8, UNKNOWN0xD9, UNKNOWN0xDA, UNKNOWN0xDB,
		UNKNOWN0xDC, UNKNOWN0xDD, UNKNOWN0xDE, UNKNOWN0xDF,
		UNKNOWN0xE0, UNKNOWN0xE1, UNKNOWN0xE2, UNKNOWN0xE3,
		UNKNOWN0xE4, UNKNOWN0xE5, UNKNOWN0xE6, UNKNOWN0xE7,
		UNKNOWN0xE8, UNKNOWN0xE9, UNKNOWN0xEA, UNKNOWN0xEB,
		UNKNOWN0xEC, UNKNOWN0xED, UNKNOWN0xEE, UNKNOWN0xEF,
		UNKNOWN0xF0, UNKNOWN0xF1, UNKNOWN0xF2, UNKNOWN0xF3,
		UNKNOWN0xF4, UNKNOWN0xF5, UNKNOWN0xF6, UNKNOWN0xF7,
		UNKNOWN0xF8, UNKNOWN0xF9, UNKNOWN0xFA, UNKNOWN0xFB,
		UNKNOWN0xFC, UNKNOWN0xFD, UNKNOWN0xFE, UNKNOWN0xFF,
	};

	/**
	 * Block ID
	 */
	public final int id;

	/**
	 * Block name
	 */
	private final String name;

	/**
	 * Index of refraction.
	 * Default value is equal to the IoR for air.
	 */
	public float ior = 1.000293f;

	/**
	 * True if there is a specific local intersection model
	 * for this block
	 */
	public boolean localIntersect = false;

	/**
	 * A block is opaque if it occupies an entire voxel
	 * and no light can pass through it.
	 *
	 * @return {@code true} if the block is solid
	 */
	public boolean isOpaque = false;

	/**
	 * A block is solid if the block occupies an entire voxel.
	 */
	public boolean isSolid = true;

	/**
	 * A block is shiny if it has a specular reflection.
	 */
	public boolean isShiny = false;

	/**
	 * Invisible blocks are not added to the voxel octree, and thus
	 * they are not rendered. This is only used for special blocks
	 * that either have been replaced by specialized rendering,
	 * such as the lily pad, or are not implemented.
	 */
	public boolean isInvisible = false;

	/**
	 * Emitter blocks emit light.
	 */
	public boolean isEmitter = false;

	public double emittance = 0.0;

	/**
	 * Subsurface scattering property.
	 */
	public boolean subSurfaceScattering = false;

	private final Texture texture;

	private static final Set<Block> redstoneConnectors = new HashSet<Block>();
	static {
		redstoneConnectors.add(REDSTONEWIRE);
		redstoneConnectors.add(REDSTONETORCHOFF);
		redstoneConnectors.add(REDSTONETORCHON);
		redstoneConnectors.add(REDSTONEREPEATEROFF);
		redstoneConnectors.add(REDSTONEREPEATERON);
		redstoneConnectors.add(LEVER);
		redstoneConnectors.add(STONEBUTTON);
		redstoneConnectors.add(TRIPWIREHOOK);
		redstoneConnectors.add(TRIPWIREHOOK);
		redstoneConnectors.add(REDSTONECOMPARATOR);
		redstoneConnectors.add(REDSTONECOMPARATORLIT);
		redstoneConnectors.add(DAYLIGHTSENSOR);
	}

	Block(int id, String name, Texture texture) {
		this.id = id;
		this.name = name;
		this.texture = texture;
	}

	/**
	 * The name of the block.
	 *
	 * @return
	 */
	public String getBlockName() {
		return name;
	}

	@Override
	public String toString() {
		return getBlockName();
	}

	public boolean intersect(Ray ray, Scene scene) {
		return TexturedBlockModel.intersect(ray, texture);
	}

	public Texture getIcon() {
		return texture;
	}

	/**
	 * Retrieves the texture dependent on the block data
	 * @param blockData [0,16]
	 * @return the selected texture
	 */
	public Texture getTexture(int blockData) {
		return texture;
	}

	public boolean isWater() {
		return this == WATER || this == STATIONARYWATER;
	}

	public boolean isLava() {
		return this == LAVA || this == STATIONARYLAVA;
	}

	public boolean isNetherBrickFenceConnector() {
		return isSolid || this == NETHERBRICKFENCE || isFenceGate();
	}

	public boolean isFenceConnector() {
		return isSolid || isFence() || isFenceGate();
	}

	protected boolean isFence() {
		return false;
	}

	protected boolean isFenceGate() {
		return false;
	}

	public boolean isStoneWallConnector() {
		return isSolid || this == FENCEGATE || this == STONEWALL;
	}

	public boolean isGlassPaneConnector() {
		return isSolid || this == GLASSPANE || this == STAINED_GLASSPANE;
	}

	public boolean isIronBarsConnector() {
		return isSolid || this == IRONBARS;
	}

	public boolean isRedstoneWireConnector() {
		return redstoneConnectors.contains(this);
	}

	public boolean isCave() {
		return !isSolid && !this.isWater();
	}

	public boolean isStair() {
		return this == OAKWOODSTAIRS ||
				this == STONESTAIRS ||
				this == BRICKSTAIRS ||
				this == STONEBRICKSTAIRS ||
				this == NETHERBRICKSTAIRS ||
				this == SANDSTONESTAIRS ||
				this == SPRUCEWOODSTAIRS ||
				this == BIRCHWOODSTAIRS ||
				this == JUNGLEWOODSTAIRS ||
				this == QUARTZSTAIRS ||
				this == ACACIASTAIRS ||
				this == DARKOAKSTAIRS ||
				this == REDSANDSTONESTAIRS;
	}

	public boolean isGroundBlock() {
		return id != Block.AIR_ID &&
				id != Block.LEAVES_ID &&
				id != Block.LEAVES2_ID &&
				id != Block.WOOD_ID &&
				id != Block.WOOD2_ID;
	}

	public static Block get(int id) {
		return values[0xFF & id];
	}

	private static final String[] woolColor = {
		"white", "orange", "magenta", "light blue", "yellow", "lime", "pink",
		"gray", "light gray", "cyan", "purple", "blue", "brown", "green",
		"red", "black"
	};

	private static final String[] bits = {
		"0000", "0001", "0010", "0011",
		"0100", "0101", "0110", "0111",
		"1000", "1001", "1010", "1011",
		"1100", "1101", "1110", "1111",
	};

	/**
	 * @param data block data
	 * @return debug info for this block
	 */
	public String description(int data) {
		return bits[data&15];
	}

	public boolean isSameMaterial(Block other) {
		return other == this;
	}
}
