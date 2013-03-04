/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.model.LadderModel;
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
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.model.TallGrassModel;
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
		}
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
	public static final Block DIRT = new Block(0x03, "Dirt", Texture.dirt) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block COBBLESTONE = new Block(0x04, "Cobblestone", Texture.cobblestone) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int WOOD_ID = 0x11;
	public static final Block WOODENPLANKS = new Block(0x05, "Wooden Planks", Texture.oakPlanks) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					Texture.woodPlank[ray.getBlockData() % 4]);
		}
	};
	public static final Block SAPLING = new Block(0x06, "Sapling", Texture.oakSapling) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SaplingModel.intersect(ray);
		}
	};
	public static final Block BEDROCK = new Block(0x07, "Bedrock", Texture.bedrock);
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
	public static final Block STATIONARYLAVA = new Block(0x0B, "Stationary Lava", Texture.lava) {
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
	public static final Block SAND = new Block(0x0C, "Sand", Texture.sand) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block GRAVEL = new Block(0x0D, "Gravel", Texture.gravel) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block GOLDORE = new Block(0x0E, "Gold Ore", Texture.goldOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int IRONBARS_ID = 0x65;
	public static final Block IRONORE = new Block(0x0F, "Iron Ore", Texture.ironOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block COALORE = new Block(0x10, "Coal Ore", Texture.coalOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block WOOD = new Block(WOOD_ID, "Wood Log", Texture.oakWood) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return WoodModel.intersect(ray);
		}
	};
	public static final int LEAVES_ID = 0x12;
	public static final Block LEAVES = new Block(LEAVES_ID, "Leaves", Texture.oakLeaves) {
		{
			isOpaque = false;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return LeafModel.intersect(ray, scene);
		}
	};
	public static final Block SPONGE = new Block(0x13, "Sponge", Texture.sponge) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final int GLASS_ID = 0x14;
	public static final Block GLASS = new Block(GLASS_ID, "Glass", Texture.glass) {
		{
			isOpaque = false;
			isSolid = true;
			ior = 1.520f;
		}
	};
	public static final Block LAPISLAZULIORE = new Block(0x15, "Lapis Lazuli Ore", Texture.lapislazuliOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block LAPISLAZULIBLOCK = new Block(0x16, "Lapis Lazuli Block", Texture.lapislazuliBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block DISPENSER = new Block(0x17, "Dispenser", Texture.dispenserFront) {
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
	public static final Block SANDSTONE = new Block(0x18, "Sandstone", Texture.sandstoneSide) {
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
	public static final Block NOTEBLOCK = new Block(0x19, "Note Block", Icon.noteBlock, Texture.jukeboxSide) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block BED = new Block(0x1A, "Bed", Icon.bed) {
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
	public static final Block POWEREDRAIL = new Block(0x1B, "Powered Rail", Texture.poweredRailOn) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RailModel.intersect(ray,
					Texture.poweredRails[ray.getBlockData() >>> 3],
					(ray.getBlockData() & 7) % 6);
		}
	};
	public static final Block DETECTORRAIL = new Block(0x1C, "Detector Rail", Texture.detectorRail) {
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
	public static final Block STICKYPISTON = new Block(0x1D, "Sticky Piston", Texture.pistonTopSticky) {
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
	public static final Block COBWEB = new Block(0x1E, "Cobweb", Texture.cobweb) {
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
	public static final Block DEADBUSH = new Block(0x20, "Dead Bush", Texture.deadBush) {
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
	public static final Block PISTON = new Block(0x21, "Piston", Texture.pistonTop) {
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
	public static final Block PISTONEXTENSION = new Block(0x22, "Piston Extension", Texture.pistonTop) {
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
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					Texture.wool[ray.getBlockData()]);
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
	public static final Block YELLOWFLOWER = new Block(0x25, "Yellow Flower", Texture.yellowFlower) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			subSurfaceScattering = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.yellowFlower);
		}
	};
	public static final Block REDROSE = new Block(0x26, "Red Rose", Texture.redRose) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
			subSurfaceScattering = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return SpriteModel.intersect(ray, Texture.redRose);
		}
	};
	public static final Block BROWNMUSHROOM = new Block(0x27, "Brown Mushroom", Texture.brownMushroom) {
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
	public static final Block REDMUSHROOM = new Block(0x28, "Red Mushroom", Texture.redMushroom) {
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
	public static final Block GOLDBLOCK = new Block(0x29, "Gold Block", Texture.goldBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final Block IRONBLOCK = new Block(0x2A, "Iron Block", Texture.ironBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final Block DOUBLESLAB = new Block(0x2B, "Double Stone Slab", Texture.slabTop) {
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
	public static final Block SLAB = new Block(0x2C, "Stone Slab", Texture.slabTop) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			switch (ray.getBlockData() & 7) {
			default: case 0:
				return SlabModel.intersect(ray, Texture.slabSide, Texture.slabTop);
			case 1:
				return SlabModel.intersect(ray, Texture.sandstoneSide, Texture.sandstoneTop);
			case 2:
				return SlabModel.intersect(ray, Texture.oakPlanks);
			case 3:
				return SlabModel.intersect(ray, Texture.cobblestone);
			case 4:
				return SlabModel.intersect(ray, Texture.brick);
			case 5:
				return SlabModel.intersect(ray, Texture.stoneBrick);
			case 6:
				return SlabModel.intersect(ray, Texture.netherBrick);
			case 7:
				return SlabModel.intersect(ray, Texture.quartzSide, Texture.quartzTop);
			}
		}
	};
	public static final Block BRICKS = new Block(0x2D, "Bricks", Texture.brick) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block TNT = new Block(0x2E, "TNT", Texture.tntSide) {
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
	public static final Block BOOKSHELF = new Block(0x2F, "Bookshelf", Texture.bookshelf) {
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
	public static final Block MOSSSTONE = new Block(0x30, "Moss Stone", Texture.mossStone) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block OBSIDIAN = new Block(0x31, "Obsidian", Texture.obsidian) {
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
	};
	public static final Block FIRE = new Block(0x33, "Fire", Texture.fire) {
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
	public static final Block MONSTERSPAWNER = new Block(0x34, "Monster Spawner", Texture.monsterSpawner) {
		{
			isOpaque = false;
			isSolid = true;
		}
	};
	public static final int OAKWOODSTAIRS_ID = 0x35;
	public static final Block OAKWOODSTAIRS = new Block(OAKWOODSTAIRS_ID, "Wooden Stairs", Icon.woodenStairs, Texture.oakPlanks) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.oakPlanks);
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
	};
	public static final Block DIAMONDORE = new Block(0x38, "Diamond Ore", Texture.diamondOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block DIAMONDBLOCK = new Block(0x39, "Diamond Block", Texture.diamondBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final Block WORKBENCH = new Block(0x3A, "Workbench", Texture.workbenchFront) {
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
	public static final Block CROPS = new Block(0x3B, "Wheat", Texture.crops7) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray,
					Texture.wheat[ray.getBlockData() % 8]);
		}
	};
	public static final Block SOIL = new Block(0x3C, "Soil", Texture.farmlandWet) {
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
	public static final Block FURNACEUNLIT = new Block(0x3D, "Furnace", Texture.furnaceUnlitFront) {
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
	public static final Block FURNACELIT = new Block(0x3E, "Burning Furnace", Texture.furnaceLitFront) {
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
	public static final Block SIGNPOST = new Block(0x3F, "Sign Post", Icon.signPost) {
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
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return DoorModel.intersect(ray,
					Texture.woodenDoor[ray.getBlockData() >>> 3]);
		}
	};
	public static final Block LADDER = new Block(0x41, "Ladder", Texture.ladder) {
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
	public static final Block MINECARTTRACKS = new Block(0x42, "Minecart Tracks", Texture.rails) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			int type = ray.getBlockData() % 10;
			return RailModel.intersect(ray, Texture.railsType[type], type);
		}
	};
	public static final int STONESTAIRS_ID = 0x43;
	public static final Block STONESTAIRS = new Block(STONESTAIRS_ID, "Cobblestone Stairs", Icon.stoneStairs, Texture.stone) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.cobblestone);
		}
	};
	public static final Block WALLSIGN = new Block(0x44, "Wall Sign", Icon.wallSign) {
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
	public static final Block LEVER = new Block(0x45, "Lever", Texture.lever) {
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
	public static final Block STONEPRESSUREPLATE = new Block(0x46, "Stone Pressure Plate", Icon.stonePressurePlate) {
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
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return DoorModel.intersect(ray,
					Texture.ironDoor[ray.getBlockData() >>> 3]);
		}
	};
	public static final Block WOODENPRESSUREPLATE = new Block(0x48, "Wooden Pressure Plate", Icon.woodenPressurePlate) {
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
	public static final Block REDSTONEORE = new Block(0x49, "Redstone Ore", Texture.redstoneOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block GLOWINGREDSTONEORE = new Block(0x4A, "Glowing Redstone Ore", Texture.redstoneOre) {
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
	public static final Block SNOW = new Block(0x4E, "Snow", Texture.snowBlock) {
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
	public static final Block SNOWBLOCK = new Block(0x50, "Snow Block", Texture.snowBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block CACTUS = new Block(0x51, "Cactus", Texture.cactusSide) {
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
	public static final Block CLAY = new Block(0x52, "Clay", Texture.clay) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block SUGARCANE = new Block(0x53, "Sugar Cane", Texture.sugarCane) {
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
	public static final Block JUKEBOX = new Block(0x54, "Jukebox", Texture.jukeboxSide) {
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
	};
	public static final int PUMPKINSTEM_ID = 0x68;
	public static final Block PUMPKIN = new Block(0x56, "Pumpkin", Texture.pumpkinSide) {
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
	public static final Block NETHERRACK = new Block(0x57, "Netherrack", Texture.netherrack) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block SOULSAND = new Block(0x58, "Soul Sand", Texture.soulsand) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block GLOWSTONE = new Block(0x59, "Glowstone", Texture.glowstone) {
		{
			isOpaque = true;
			isSolid = true;
			isEmitter = true;
			emittance = 1.0;
		}
	};
	public static final Block PORTAL = new Block(0x5A, "Portal", Texture.portal) {
		{
			isOpaque = false;
			isSolid = false;
		}
	};
	public static final Block JACKOLANTERN = new Block(0x5B, "Jack-O-Lantern", Texture.jackolanternFront) {
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
	public static final Block CAKE = new Block(0x5C, "Cake Block", Icon.cake) {
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
	public static final Block REDSTONEREPEATEROFF = new Block(0x5D, "Redstone Repeater (off)", Texture.redstoneRepeaterOff) {
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
	public static final Block REDSTONEREPEATERON = new Block(0x5E, "Redstone Repeater (on)", Texture.redstoneRepeaterOn) {
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
	public static final Block LOCKEDCHEST = new Block(0x5F, "Locked Chest", Texture.chestFront) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		final Texture[] tex = {
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
		};
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return ChestModel.intersect(ray, tex);
		}
	};
	public static final Block TRAPDOOR = new Block(0x60, "Trapdoor", Texture.trapdoor) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TrapdoorModel.intersect(ray);
		}
	};
	public static final Block HIDDENSILVERFISH = new Block(0x61, "Hidden Silverfish", Texture.stone) {
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
	public static final Block STONEBRICKS = new Block(0x62, "Stone Bricks", Texture.stoneBrick) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return TexturedBlockModel.intersect(ray,
					Texture.stoneBrickType[(ray.currentMaterial>>8)&3]);
		}
	};
	public static final Block HUGEBROWNMUSHROOM = new Block(0x63, "Huge Brown Mushroom", Texture.hugeBrownMushroom) {
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
	public static final Block HUGEREDMUSHROOM = new Block(0x64, "Huge Red Mushroom", Texture.hugeRedMushroom) {
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
	public static final Block MELON = new Block(0x67, "Melon", Texture.melonSide) {
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
			return FenceGateModel.intersect(ray);
		}
	};
	public static final int BRICKSTAIRS_ID = 0x6C;
	public static final Block BRICKSTAIRS = new Block(BRICKSTAIRS_ID, "Brick Stairs", Icon.stoneStairs, Texture.brick) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.brick);
		}
	};
	public static final int STONEBRICKSTAIRS_ID = 0x6D;
	public static final Block STONEBRICKSTAIRS = new Block(STONEBRICKSTAIRS_ID, "Stone Brick Stairs", Icon.stoneBrickStairs, Texture.stoneBrick) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.stoneBrick);
		}
	};
	public static final Block MYCELIUM = new Block(0x6E, "Mycelium", Texture.myceliumSide, Texture.myceliumTop) {
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
	public static final Block NETHERBRICK = new Block(0x70, "Nether Brick", Texture.netherBrick) {
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
	};
	public static final int NETHERBRICKSTAIRS_ID = 0x72;
	public static final Block NETHERBRICKSTAIRS = new Block(NETHERBRICKSTAIRS_ID, "Nether Brick Stairs", Icon.stoneStairs, Texture.netherBrick) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.netherBrick);
		}
	};
	public static final Block NETHERWART = new Block(0x73, "Nether Wart", Texture.netherWart2) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray,
					Texture.netherWart[ray.getBlockData() & 3]);
		}
	};
	public static final Block ENCHNATMENTTABLE = new Block(0x74, "Enchantment Table", Texture.enchantmentTableSide) {
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
	public static final Block BREWINGSTAND = new Block(0x75, "Brewing Stand", Texture.brewingStandSide) {
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
	public static final Block CAULDRON = new Block(0x76, "Cauldron", Texture.cauldronSide) {
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
	public static final Block ENDPORTAL = new Block(0x77, "End Portal", Texture.endPortal) {
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
	public static final Block ENDPORTALFRAME = new Block(0x78, "End Portal Frame", Texture.endPortalFrameTop) {
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
	public static final Block ENDSTONE = new Block(0x79, "End Stone", Texture.endStone) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
	};
	public static final Block DRAGONEGG = new Block(0x7A, "Dragon Egg", Texture.dragonEgg) {
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
	public static final Block REDSTONELAMPOFF = new Block(0x7B, "Redstone Lamp (off)", Texture.redstoneLampOff) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block REDSTONELAMPON = new Block(0x7C, "Redstone Lamp (on)", Texture.redstoneLampOn) {
		{
			isOpaque = true;
			isSolid = true;
			isEmitter = true;
			emittance = 1.0;
		}
	};
	public static final Block DOUBLEWOODENSLAB = new Block(0x7D, "Double Wooden Slab", Texture.oakPlanks) {
		{
			isOpaque = true;
			isSolid = true;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			switch (ray.getBlockData() & 7) {
			case 1:
				return SlabModel.intersect(ray, Texture.sprucePlanks, Texture.sprucePlanks);
			case 2:
				return SlabModel.intersect(ray, Texture.birchPlanks, Texture.birchPlanks);
			case 3:
				return SlabModel.intersect(ray, Texture.jungleTreePlanks, Texture.jungleTreePlanks);
			case 0:
			default:
				return SlabModel.intersect(ray, Texture.oakPlanks, Texture.oakPlanks);
			}
		}
	};
	public static final Block SINGLEWOODENSLAB = new Block(0x7E, "Single Wooden Slab", Texture.oakPlanks) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			switch (ray.getBlockData() & 7) {
			case 1:
				return SlabModel.intersect(ray, Texture.sprucePlanks, Texture.sprucePlanks);
			case 2:
				return SlabModel.intersect(ray, Texture.birchPlanks, Texture.birchPlanks);
			case 3:
				return SlabModel.intersect(ray, Texture.jungleTreePlanks, Texture.jungleTreePlanks);
			case 0:
			default:
				return SlabModel.intersect(ray, Texture.oakPlanks, Texture.oakPlanks);
			}
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
	};
	public static final int SANDSTONESTAIRS_ID = 0x80;
	public static final Block SANDSTONESTAIRS = new Block(SANDSTONESTAIRS_ID, "Sandstone Stairs", Icon.stoneStairs, Texture.sandstoneSide) {
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
	};
	public static final Block EMERALDORE = new Block(0x81, "Emerald Ore", Texture.emeraldOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block ENDERCHEST = new Block(0x82, "Ender Chest", Texture.unknown) {
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
	public static final int TRIPWIRE_ID = 0x84;
	public static final Block TRIPWIREHOOK = new Block(0x83, "Tripwire Hook", Texture.tripwireHook) {
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
	public static final Block EMERALDBLOCK = new Block(0x85, "Emerald Block", Texture.emeraldBlock) {
		{
			isOpaque = true;
			isSolid = true;
			isShiny = true;
		}
	};
	public static final int SPRUCEWOODSTAIRS_ID = 0x86;
	public static final Block SPRUCEWOODSTAIRS = new Block(SPRUCEWOODSTAIRS_ID, "Spruce Wood Stairs", Icon.woodenStairs, Texture.sprucePlanks) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.sprucePlanks);
		}
	};
	public static final int BIRCHWOODSTAIRS_ID = 0x87;
	public static final Block BIRCHWOODSTAIRS = new Block(BIRCHWOODSTAIRS_ID, "Birch Wood Stairs", Icon.woodenStairs, Texture.birchPlanks) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.birchPlanks);
		}
	};
	public static final int JUNGLEWOODSTAIRS_ID = 0x88;
	public static final Block JUNGLEWOODSTAIRS = new Block(JUNGLEWOODSTAIRS_ID, "Jungle Wood Stairs", Icon.woodenStairs, Texture.jungleTreePlanks) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return StairModel.intersect(ray, Texture.jungleTreePlanks);
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
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray,
					Texture.carrots[ray.getBlockData() % 8]);
		}
	};
	public static final int POTATOES_ID = 0x8E;
	public static final Block POTATOES = new Block(POTATOES_ID, "Potatoes", Texture.potatoes3) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return CropsModel.intersect(ray,
					Texture.potatoes[ray.getBlockData() % 8]);
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
	public static final Block TRAPPEDCHEST = new Block(0x92, "Trapped Chest", Texture.chestFront) {
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
	public static final Block WEIGHTEDPRESSUREPLATELIGHT = new Block(0x93, "Weighted Pressure Plate (Light)", Texture.goldBlock) {
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
	public static final Block WEIGHTEDPRESSUREPLATEHEAVY = new Block(0x94, "Weighted Pressure Plate (Heavy)", Texture.ironBlock) {
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
	public static final Block REDSTONECOMPARATOR = new Block(0x95, "Redstone Comparator (inactive)", Texture.unknown) {
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
	public static final Block REDSTONECOMPARATORLIT = new Block(0x96, "Redstone Comparator (active)", Texture.unknown) {
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
	public static final Block DAYLIGHTSENSOR = new Block(0x97, "Daylight Sensor", Texture.daylightDetectorTop) {
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
	public static final Block REDSTONEBLOCK = new Block(0x98, "Block of Redstone", Texture.redstoneBlock) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block NETHERQUARTZORE = new Block(0x99, "Nether Quartz Ore", Texture.netherQuartzOre) {
		{
			isOpaque = true;
			isSolid = true;
		}
	};
	public static final Block HOPPER = new Block(0x9A, "Hopper", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block QUARTZ = new Block(0x9B, "Block of Quartz", Texture.quartzSide) {
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
	public static final Block QUARTZSTAIRS = new Block(0x9C, "Quartz Stairs", Icon.stoneStairs, Texture.quartzSide) {
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
	};
	public static final Block ACTIVATORRAIL = new Block(0x9D, "Activator Rail", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			localIntersect = true;
		}
		@Override
		public boolean intersect(Ray ray, Scene scene) {
			return RailModel.intersect(ray,
					Texture.activatorRails[ray.getBlockData() >>> 3],
					(ray.getBlockData() & 7) % 6);
		}
	};
	public static final Block DROPPER = new Block(0x9E, "Dropper", Texture.dropperFront) {
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
	public static final Block UNKNOWN0x9F = new Block(0x9F, "Unknown Block 0x9F", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA0 = new Block(0xA0, "Unknown Block 0xA0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA1 = new Block(0xA1, "Unknown Block 0xA1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA2 = new Block(0xA2, "Unknown Block 0xA2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA3 = new Block(0xA3, "Unknown Block 0xA3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA4 = new Block(0xA4, "Unknown Block 0xA4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA5 = new Block(0xA5, "Unknown Block 0xA5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA6 = new Block(0xA6, "Unknown Block 0xA6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA7 = new Block(0xA7, "Unknown Block 0xA7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA8 = new Block(0xA8, "Unknown Block 0xA8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xA9 = new Block(0xA9, "Unknown Block 0xA9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xAA = new Block(0xAA, "Unknown Block 0xAA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xAB = new Block(0xAB, "Unknown Block 0xAB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xAC = new Block(0xAC, "Unknown Block 0xAC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xAD = new Block(0xAD, "Unknown Block 0xAD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xAE = new Block(0xAE, "Unknown Block 0xAE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xAF = new Block(0xAF, "Unknown Block 0xAF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB0 = new Block(0xB0, "Unknown Block 0xB0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB1 = new Block(0xB1, "Unknown Block 0xB1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB2 = new Block(0xB2, "Unknown Block 0xB2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB3 = new Block(0xB3, "Unknown Block 0xB3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB4 = new Block(0xB4, "Unknown Block 0xB4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB5 = new Block(0xB5, "Unknown Block 0xB5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB6 = new Block(0xB6, "Unknown Block 0xB6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB7 = new Block(0xB7, "Unknown Block 0xB7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB8 = new Block(0xB8, "Unknown Block 0xB8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xB9 = new Block(0xB9, "Unknown Block 0xB9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xBA = new Block(0xBA, "Unknown Block 0xBA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xBB = new Block(0xBB, "Unknown Block 0xBB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xBC = new Block(0xBC, "Unknown Block 0xBC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xBD = new Block(0xBD, "Unknown Block 0xBD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xBE = new Block(0xBE, "Unknown Block 0xBE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xBF = new Block(0xBF, "Unknown Block 0xBF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC0 = new Block(0xC0, "Unknown Block 0xC0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC1 = new Block(0xC1, "Unknown Block 0xC1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC2 = new Block(0xC2, "Unknown Block 0xC2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC3 = new Block(0xC3, "Unknown Block 0xC3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC4 = new Block(0xC4, "Unknown Block 0xC4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC5 = new Block(0xC5, "Unknown Block 0xC5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC6 = new Block(0xC6, "Unknown Block 0xC6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC7 = new Block(0xC7, "Unknown Block 0xC7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC8 = new Block(0xC8, "Unknown Block 0xC8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xC9 = new Block(0xC9, "Unknown Block 0xC9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xCA = new Block(0xCA, "Unknown Block 0xCA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xCB = new Block(0xCB, "Unknown Block 0xCB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xCC = new Block(0xCC, "Unknown Block 0xCC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xCD = new Block(0xCD, "Unknown Block 0xCD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xCE = new Block(0xCE, "Unknown Block 0xCE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xCF = new Block(0xCF, "Unknown Block 0xCF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD0 = new Block(0xD0, "Unknown Block 0xD0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD1 = new Block(0xD1, "Unknown Block 0xD1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD2 = new Block(0xD2, "Unknown Block 0xD2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD3 = new Block(0xD3, "Unknown Block 0xD3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD4 = new Block(0xD4, "Unknown Block 0xD4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD5 = new Block(0xD5, "Unknown Block 0xD5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD6 = new Block(0xD6, "Unknown Block 0xD6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD7 = new Block(0xD7, "Unknown Block 0xD7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD8 = new Block(0xD8, "Unknown Block 0xD8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xD9 = new Block(0xD9, "Unknown Block 0xD9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xDA = new Block(0xDA, "Unknown Block 0xDA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xDB = new Block(0xDB, "Unknown Block 0xDB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xDC = new Block(0xDC, "Unknown Block 0xDC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xDD = new Block(0xDD, "Unknown Block 0xDD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xDE = new Block(0xDE, "Unknown Block 0xDE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xDF = new Block(0xDF, "Unknown Block 0xDF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE0 = new Block(0xE0, "Unknown Block 0xE0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE1 = new Block(0xE1, "Unknown Block 0xE1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE2 = new Block(0xE2, "Unknown Block 0xE2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE3 = new Block(0xE3, "Unknown Block 0xE3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE4 = new Block(0xE4, "Unknown Block 0xE4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE5 = new Block(0xE5, "Unknown Block 0xE5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE6 = new Block(0xE6, "Unknown Block 0xE6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE7 = new Block(0xE7, "Unknown Block 0xE7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE8 = new Block(0xE8, "Unknown Block 0xE8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xE9 = new Block(0xE9, "Unknown Block 0xE9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xEA = new Block(0xEA, "Unknown Block 0xEA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xEB = new Block(0xEB, "Unknown Block 0xEB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xEC = new Block(0xEC, "Unknown Block 0xEC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xED = new Block(0xED, "Unknown Block 0xED", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xEE = new Block(0xEE, "Unknown Block 0xEE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xEF = new Block(0xEF, "Unknown Block 0xEF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF0 = new Block(0xF0, "Unknown Block 0xF0", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF1 = new Block(0xF1, "Unknown Block 0xF1", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF2 = new Block(0xF2, "Unknown Block 0xF2", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF3 = new Block(0xF3, "Unknown Block 0xF3", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF4 = new Block(0xF4, "Unknown Block 0xF4", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF5 = new Block(0xF5, "Unknown Block 0xF5", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF6 = new Block(0xF6, "Unknown Block 0xF6", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF7 = new Block(0xF7, "Unknown Block 0xF7", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF8 = new Block(0xF8, "Unknown Block 0xF8", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xF9 = new Block(0xF9, "Unknown Block 0xF9", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xFA = new Block(0xFA, "Unknown Block 0xFA", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xFB = new Block(0xFB, "Unknown Block 0xFB", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xFC = new Block(0xFC, "Unknown Block 0xFC", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xFD = new Block(0xFD, "Unknown Block 0xFD", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xFE = new Block(0xFE, "Unknown Block 0xFE", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
		}
	};
	public static final Block UNKNOWN0xFF = new Block(0xFF, "Unknown Block 0xFF", Texture.unknown) {
		{
			isOpaque = false;
			isSolid = false;
			isInvisible = true;
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
		MOVEDBYPISTON, YELLOWFLOWER, REDROSE, BROWNMUSHROOM,
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
		CAKE, REDSTONEREPEATEROFF, REDSTONEREPEATERON, LOCKEDCHEST,
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
		QUARTZSTAIRS, ACTIVATORRAIL, DROPPER, UNKNOWN0x9F,
		UNKNOWN0xA0, UNKNOWN0xA1, UNKNOWN0xA2, UNKNOWN0xA3,
		UNKNOWN0xA4, UNKNOWN0xA5, UNKNOWN0xA6, UNKNOWN0xA7,
		UNKNOWN0xA8, UNKNOWN0xA9, UNKNOWN0xAA, UNKNOWN0xAB,
		UNKNOWN0xAC, UNKNOWN0xAD, UNKNOWN0xAE, UNKNOWN0xAF,
		UNKNOWN0xB0, UNKNOWN0xB1, UNKNOWN0xB2, UNKNOWN0xB3,
		UNKNOWN0xB4, UNKNOWN0xB5, UNKNOWN0xB6, UNKNOWN0xB7,
		UNKNOWN0xB8, UNKNOWN0xB9, UNKNOWN0xBA, UNKNOWN0xBB,
		UNKNOWN0xBC, UNKNOWN0xBD, UNKNOWN0xBE, UNKNOWN0xBF,
		UNKNOWN0xC0, UNKNOWN0xC1, UNKNOWN0xC2, UNKNOWN0xC3,
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
	
	private Texture frontTexture;
	private Texture icon;
	
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
		this(id, name, texture, texture);
	}
	
	Block(int id, String name, Texture icon, Texture texture) {

		this.id = id;
		this.name = name;
		this.icon = icon;
		
		setTexture(texture);
	}

	/**
	 * The name of the block.
	 * 
	 * @return
	 */
	public String getBlockName() {
		return name;
	}
	
	/**
	 * Get the average color for the block texture as an
	 * RGB value.
	 * 
	 * @return average color (RGB)
	 */
	public int getAvgRGB() {
		return frontTexture.getAvgColor();
	}
	
	/**
	 * Get the average color for the block texture as an
	 * RGB value.
	 * 
	 * @return average color (RGB)
	 */
	public int getAvgTopRGB() {
		return icon.getAvgColor();
	}
	
	@Override
	public String toString() {
		return getBlockName();
	}

	public void setTexture(Texture newTexture) {
		frontTexture = newTexture;
	}
	
	public boolean intersect(Ray ray, Scene scene) {
		return TexturedBlockModel.intersect(ray, frontTexture);
	}

	public Texture getTexture() {
		return frontTexture;
	}
	
	public boolean isWater() {
		return this == WATER || this == STATIONARYWATER;
	}
	
	public boolean isLava() {
		return this == LAVA || this == STATIONARYLAVA;
	}
	
	public boolean isNetherBrickFenceConnector() {
		return isSolid || this == FENCEGATE || this == NETHERBRICKFENCE;
	}
	
	public boolean isFenceConnector() {
		return isSolid || this == FENCEGATE || this == FENCE;
	}
	
	public boolean isStoneWallConnector() {
		return isSolid || this == FENCEGATE || this == STONEWALL;
	}
	
	public boolean isGlassPaneConnector() {
		return isSolid || this == GLASSPANE;
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
		return id == OAKWOODSTAIRS_ID ||
				id == STONESTAIRS_ID ||
				id == BRICKSTAIRS_ID ||
				id == STONEBRICKSTAIRS_ID ||
				id == NETHERBRICKSTAIRS_ID ||
				id == SANDSTONESTAIRS_ID ||
				id == SPRUCEWOODSTAIRS_ID ||
				id == BIRCHWOODSTAIRS_ID ||
				id == JUNGLEWOODSTAIRS_ID;
	}

	public static Block get(int id) {
		return values[0xFF & id];
	}
}
