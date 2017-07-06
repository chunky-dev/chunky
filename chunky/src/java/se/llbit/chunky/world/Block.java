/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.PersistentSettings;
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
import se.llbit.chunky.model.ChorusFlowerModel;
import se.llbit.chunky.model.ChorusPlantModel;
import se.llbit.chunky.model.CocoaPlantModel;
import se.llbit.chunky.model.ComparatorModel;
import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.model.DaylightSensorModel;
import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.model.DoorModel;
import se.llbit.chunky.model.DragonEggModel;
import se.llbit.chunky.model.EnchantmentTableModel;
import se.llbit.chunky.model.EndPortalFrameModel;
import se.llbit.chunky.model.EndPortalModel;
import se.llbit.chunky.model.EndRodModel;
import se.llbit.chunky.model.FarmlandModel;
import se.llbit.chunky.model.FenceGateModel;
import se.llbit.chunky.model.FenceModel;
import se.llbit.chunky.model.FireModel;
import se.llbit.chunky.model.FlowerPotModel;
import se.llbit.chunky.model.FurnaceModel;
import se.llbit.chunky.model.GlassPaneModel;
import se.llbit.chunky.model.GrassModel;
import se.llbit.chunky.model.GrassPathModel;
import se.llbit.chunky.model.HopperModel;
import se.llbit.chunky.model.IronBarsModel;
import se.llbit.chunky.model.LadderModel;
import se.llbit.chunky.model.LargeFlowerModel;
import se.llbit.chunky.model.LavaModel;
import se.llbit.chunky.model.LeafModel;
import se.llbit.chunky.model.LeverModel;
import se.llbit.chunky.model.LilyPadModel;
import se.llbit.chunky.model.MelonStemModel;
import se.llbit.chunky.model.ObserverModel;
import se.llbit.chunky.model.PistonExtensionModel;
import se.llbit.chunky.model.PistonModel;
import se.llbit.chunky.model.PressurePlateModel;
import se.llbit.chunky.model.PumpkinModel;
import se.llbit.chunky.model.QuartzModel;
import se.llbit.chunky.model.RailModel;
import se.llbit.chunky.model.RedstoneRepeaterModel;
import se.llbit.chunky.model.RedstoneWireModel;
import se.llbit.chunky.model.SaplingModel;
import se.llbit.chunky.model.SlabModel;
import se.llbit.chunky.model.SnowModel;
import se.llbit.chunky.model.SpriteModel;
import se.llbit.chunky.model.StairModel;
import se.llbit.chunky.model.StoneWallModel;
import se.llbit.chunky.model.TallGrassModel;
import se.llbit.chunky.model.TerracottaModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.model.TorchModel;
import se.llbit.chunky.model.TrapdoorModel;
import se.llbit.chunky.model.TripwireHookModel;
import se.llbit.chunky.model.TripwireModel;
import se.llbit.chunky.model.VineModel;
import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.model.WoodModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.math.Ray;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Block objects define the properties for different types of Minecraft blocks.
 *
 * <p>This class also determines how blocks are rendered.
 *
 * <p>Block ID reference: http://minecraft.gamepedia.com/Data_values/Block_IDs
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class Block extends Material {
  private static final boolean UNKNOWN_INVISIBLE = !PersistentSettings.drawUnknownBlocks();

  public static final int AIR_ID = 0x00;

  // The air block is public because it is never supposed to change.
  // All other blocks are private to avoid direct references to the default blocks.
  public static final Block AIR = new Block(AIR_ID, "Air", Texture.air);
  public static final int STONE_ID = 0x01;
  public static final Block STONE = new Block(STONE_ID, "block:stone", Texture.stone) {
    final Texture[] texture =
        {Texture.stone, Texture.granite, Texture.smoothGranite, Texture.diorite,
            Texture.smoothDiorite, Texture.andesite, Texture.smoothAndesite,};
    final String[] stoneKind =
        {"stone", "granite", "smoothGranite", "diorite", "smoothDiorite", "andesite",
            "smoothAndesite",};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 7]);
    }

    @Override public String description(int data) {
      return stoneKind[data % 7];
    }
  };
  public static final int GRASS_ID = 0x02;
  public static final Block GRASS = new Block(GRASS_ID, "block:grass", Texture.grassTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return GrassModel.intersect(ray, scene);
    }
  };
  public static final int DIRT_ID = 0x03;
  public static final Block DIRT = new Block(DIRT_ID, "block:dirt", Texture.dirt) {
    final Texture[][] textures =
        {{Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt,},
            {Texture.coarseDirt, Texture.coarseDirt, Texture.coarseDirt, Texture.coarseDirt,
                Texture.coarseDirt, Texture.coarseDirt,},
            {Texture.podzolSide, Texture.podzolSide, Texture.podzolSide, Texture.podzolSide,
                Texture.podzolTop, Texture.podzolSide,},};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, textures[ray.getBlockData() % 3]);
    }

    final String[] kind = {"regular", "coarse", "podzol"};

    @Override public String description(int data) {
      return kind[data % 3];
    }
  };
  public static final int COBBLESTONE_ID = 0x04;
  public static final Block COBBLESTONE = new Block(COBBLESTONE_ID, "block:cobblestone", Texture.cobblestone);
  public static final int WOODENPLANKS_ID = 0x05;
  public static final Block WOODENPLANKS = new Block(WOODENPLANKS_ID, "block:planks", Texture.oakPlanks) {
    final Texture[] texture =
        {Texture.oakPlanks, Texture.sprucePlanks, Texture.birchPlanks, Texture.jungleTreePlanks,
            Texture.acaciaPlanks, Texture.darkOakPlanks, Texture.acaciaPlanks,
            Texture.darkOakPlanks};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, getTexture(ray.getBlockData()));
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 7];
    }
  };
  public static final int SAPLING_ID = 0x06;
  public static final Block SAPLING = new Block(SAPLING_ID, "block:sapling", Texture.oakSapling) {
    final Texture[] texture =
        {Texture.oakSapling, Texture.spruceSapling, Texture.birchSapling, Texture.jungleSapling,
            Texture.acaciaSapling, Texture.darkOakSapling, Texture.acaciaSapling,
            Texture.darkOakSapling};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return SaplingModel.intersect(ray, getTexture(ray.getBlockData()));
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 7];
    }
  };
  public static final int BEDROCK_ID = 0x07;
  public static final Block BEDROCK = new Block(BEDROCK_ID, "block:bedrock", Texture.bedrock);
  public static final int WATER_ID = 0x08;
  public static final Block WATER = new Block(WATER_ID, "block:flowing_water", Texture.water) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return WaterModel.intersect(ray);
    }

    @Override public void getColor(Ray ray) {
      Texture.water.getAvgColorLinear(ray.color);
    }

    @Override public boolean isWater() {
      return true;
    }
  };
  public static final int STATIONARYWATER_ID = 0x09;
  public static final Block STATIONARYWATER = new Block(STATIONARYWATER_ID, "block:water", Texture.water) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return WaterModel.intersect(ray);
    }

    @Override public boolean isWater() {
      return true;
    }
  };
  public static final int LAVA_ID = 0x0A;
  public static final Block LAVA = new Block(LAVA_ID, "block:flowing_lava", Texture.lava) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LavaModel.intersect(ray);
    }
  };
  public static final int STATIONARYLAVA_ID = 0x0B;
  public static final Block STATIONARYLAVA = new Block(STATIONARYLAVA_ID, "block:lava", Texture.lava) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LavaModel.intersect(ray);
    }
  };
  public static final int SAND_ID = 0x0C;
  public static final Block SAND = new Block(SAND_ID, "block:sand", Texture.sand) {
    final Texture[] texture = {Texture.sand, Texture.redSand};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, getTexture(ray.getBlockData()));
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 1];
    }
  };
  public static final int GRAVEL_ID = 0x0D;
  public static final Block GRAVEL = new Block(GRAVEL_ID, "block:gravel", Texture.gravel);
  public static final int GOLDORE_ID = 0x0E;
  public static final Block GOLDORE = new Block(GOLDORE_ID, "block:gold_ore", Texture.goldOre);
  public static final int IRONORE_ID = 0x0F;
  public static final Block IRONORE = new Block(IRONORE_ID, "block:iron_ore", Texture.ironOre);
  public static final int COALORE_ID = 0x10;
  public static final Block COALORE = new Block(COALORE_ID, "block:coal_ore", Texture.coalOre);
  public static final int WOOD_ID = 0x11;
  public static final Block WOOD = new Block(WOOD_ID, "block:log2", Texture.oakWood) {
    final Texture[][] texture =
        {{Texture.oakWood, Texture.oakWoodTop}, {Texture.spruceWood, Texture.spruceWoodTop},
            {Texture.birchWood, Texture.birchWoodTop}, {Texture.jungleWood, Texture.jungleTreeTop}};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return WoodModel.intersect(ray, texture[ray.getBlockData() & 3]);
    }

    final String[] woodType = {"oak", "spruce", "birch", "jungle",};

    @Override public String description(int data) {
      return woodType[data & 3];
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 3][1];
    }
  };
  public static final int LEAVES_ID = 0x12;
  public static final Block LEAVES = new Block(LEAVES_ID, "block:leaves", Texture.oakLeaves) {
    final Texture[] texture =
        {Texture.oakLeaves, Texture.spruceLeaves, Texture.birchLeaves, Texture.jungleTreeLeaves};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return LeafModel.intersect(ray, scene, getTexture(ray.getBlockData()));

    }

    final String[] woodType = {"oak", "spruce", "birch", "jungle",};

    @Override public String description(int data) {
      return woodType[data & 3];
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 3];
    }
  };
  public static final int SPONGE_ID = 0x13;
  public static final Block SPONGE = new Block(SPONGE_ID, "block:sponge", Texture.sponge) {
    final Texture[] texture = {Texture.sponge, Texture.wetSponge,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 2]);
    }

    final String[] kind = {"dry", "wet"};

    @Override public String description(int data) {
      return kind[data % 2];
    }
  };
  public static final int GLASS_ID = 0x14;
  public static final Block GLASS = new Block(GLASS_ID, "block:glass", Texture.glass) {
    @Override public boolean isSameMaterial(Material other) {
      return other == this || other == STAINED_GLASS;
    }
  };
  public static final int LAPIS_ORE_ID = 0x15;
  public static final Block LAPIS_ORE =
      new Block(LAPIS_ORE_ID, "block:lapis_ore", Texture.lapisOre);
  public static final int LAPIS_BLOCK_ID = 0x16;
  public static final Block LAPIS_BLOCK =
      new Block(LAPIS_BLOCK_ID, "block:lapis_block", Texture.lapisBlock);
  public static final int DISPENSER_ID = 0x17;
  public static final Block DISPENSER = new Block(DISPENSER_ID, "block:dispenser", Texture.dispenserFront) {
    final Texture[][] texture = {
        // Facing down.
        {Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
            Texture.furnaceTop, Texture.dispenserFrontVertical},
        // Facing up.
        {Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
            Texture.dispenserFrontVertical, Texture.furnaceTop},
        // Facing north.
        {Texture.dispenserFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,},
        // Facing south.
        {Texture.furnaceSide, Texture.dispenserFront, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,},
        // Facing east.
        {Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide, Texture.dispenserFront,
            Texture.furnaceTop, Texture.furnaceTop,},
        // Facing west.
        {Texture.furnaceSide, Texture.furnaceSide, Texture.dispenserFront, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,},
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 6]);
    }
  };
  public static final int SANDSTONE_ID = 0x18;
  public static final Block SANDSTONE = new Block(SANDSTONE_ID, "block:sandstone", Texture.sandstoneSide) {
    final Texture[][] texture = {
        // normal
        {Texture.sandstoneSide, Texture.sandstoneSide, Texture.sandstoneSide,
            Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom,},

        // decorated
        {Texture.sandstoneDecorated, Texture.sandstoneDecorated, Texture.sandstoneDecorated,
            Texture.sandstoneDecorated, Texture.sandstoneTop, Texture.sandstoneBottom,},

        // smooth
        {Texture.sandstoneSmooth, Texture.sandstoneSmooth, Texture.sandstoneSmooth,
            Texture.sandstoneSmooth, Texture.sandstoneTop, Texture.sandstoneBottom,},};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 3]);
    }
  };
  public static final int NOTEBLOCK_ID = 0x19;
  public static final Block NOTEBLOCK = new Block(NOTEBLOCK_ID, "block:noteblock", Icon.noteBlock) {
    @Override public Texture getTexture(int blockData) {
      return Texture.jukeboxSide;
    }
  };
  public static final int BED_ID = 0x1A;
  public static final Block BED = new Block(BED_ID, "block:bed", Icon.bed) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return BedModel.intersect(ray);
    }
  };
  public static final int POWEREDRAIL_ID = 0x1B;
  public static final Block POWEREDRAIL = new Block(POWEREDRAIL_ID, "block:golden_rail", Texture.poweredRailOn) {
    final Texture[] texture = {Texture.poweredRailOff, Texture.poweredRailOn};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return RailModel
          .intersect(ray, texture[ray.getBlockData() >>> 3], (ray.getBlockData() & 7) % 6);
    }
  };
  public static final int DETECTORRAIL_ID = 0x1C;
  public static final Block DETECTORRAIL = new Block(DETECTORRAIL_ID, "block:detector_rail", Texture.detectorRail) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RailModel.intersect(ray, Texture.detectorRail, (ray.getBlockData() & 7) % 6);
    }
  };
  public static final int STICKYPISTON_ID = 0x1D;
  public static final Block STICKYPISTON = new Block(STICKYPISTON_ID, "block:sticky_piston", Texture.pistonTopSticky) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PistonModel.intersect(ray, 1);
    }
  };
  public static final int COBWEB_ID = 0x1E;
  public static final Block COBWEB = new Block(COBWEB_ID, "block:web", Texture.cobweb) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.cobweb);
    }
  };
  public static final int TALLGRASS_ID = 0x1F;
  public static final Block TALLGRASS = new Block(TALLGRASS_ID, "block:tallgrass", Texture.tallGrass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TallGrassModel.intersect(ray, scene);
    }
  };
  public static final int DEADBUSH_ID = 0x20;
  public static final Block DEADBUSH = new Block(DEADBUSH_ID, "block:deadbush", Texture.deadBush) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.deadBush);
    }
  };
  public static final int PISTON_ID = 0x21;
  public static final Block PISTON = new Block(PISTON_ID, "block:piston", Texture.pistonTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PistonModel.intersect(ray, 0);
    }
  };
  public static final int PISTON_HEAD_ID = 0x22;
  public static final Block PISTON_HEAD = new Block(PISTON_HEAD_ID,
      "block:piston_head", Texture.pistonTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PistonExtensionModel.intersect(ray);
    }
  };
  public static final int WOOL_ID = 0x23;
  public static final Block WOOL = new Block(WOOL_ID, "block:wool", Texture.lightGrayWool) {
    @Override public Texture getTexture(int blockData) {
      return Texture.wool[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final int PISTON_EXTENSION_ID = 0x24;
  public static final Block PISTON_EXTENSION = new Block(PISTON_EXTENSION_ID,
      "block:piston_extension", Texture.unknown);
  public static final int DANDELION_ID = 0x25;
  public static final Block DANDELION = new Block(DANDELION_ID, "block:yellow_flower", Texture.dandelion) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.dandelion);
    }
  };
  public static final int FLOWER_ID = 0x26;
  public static final Block FLOWER = new Block(FLOWER_ID, "block:red_flower", Texture.poppy) {
    final Texture[] textures =
        {Texture.poppy, Texture.blueOrchid, Texture.allium, Texture.azureBluet, Texture.redTulip,
            Texture.orangeTulip, Texture.whiteTulip, Texture.pinkTulip, Texture.oxeyeDaisy};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, textures[ray.getBlockData() % 9]);
    }

    final String[] flowerType =
        {"Poppy", "Blue Orchid", "Allium", "Azure Bluet", "Red Tulip", "Orange Tulip",
            "White Tulip", "Pink Tulip", "Oxeye Daisy"};

    @Override public String description(int data) {
      return flowerType[data % 8];
    }
  };
  public static final int BROWNMUSHROOM_ID = 0x27;
  public static final Block BROWNMUSHROOM = new Block(BROWNMUSHROOM_ID, "block:brown_mushroom", Texture.brownMushroom) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.brownMushroom);
    }
  };
  public static final int REDMUSHROOM_ID = 0x28;
  public static final Block REDMUSHROOM = new Block(REDMUSHROOM_ID, "block:red_mushroom", Texture.redMushroom) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.redMushroom);
    }
  };
  public static final int GOLDBLOCK_ID = 0x29;
  public static final Block GOLDBLOCK = new Block(GOLDBLOCK_ID, "block:gold_block", Texture.goldBlock);
  public static final int IRONBLOCK_ID = 0x2A;
  public static final Block IRONBLOCK = new Block(IRONBLOCK_ID, "block:iron_block", Texture.ironBlock);
  public static final int DOUBLESLAB_ID = 0x2B;
  public static final Block DOUBLESLAB = new Block(DOUBLESLAB_ID, "block:double_stone_slab", Texture.slabTop) {
    final Texture[] sandstone =
        {Texture.sandstoneSide, Texture.sandstoneSide, Texture.sandstoneSide,
            Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneTop,};

    final Texture[] stone =
        {Texture.slabSide, Texture.slabSide, Texture.slabSide, Texture.slabSide,
            Texture.slabTop, Texture.slabTop,};

    final Texture[] quartz =
        {Texture.quartzSide, Texture.quartzSide, Texture.quartzSide, Texture.quartzSide,
            Texture.quartzTop, Texture.quartzBottom,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      switch (ray.getBlockData()) {
        default:
        case 0:
          return TexturedBlockModel.intersect(ray, stone);
        case 1:
          return TexturedBlockModel.intersect(ray, sandstone);
        case 2:
        case 10:
          return TexturedBlockModel.intersect(ray, Texture.oakPlanks);
        case 3:
        case 11:
          return TexturedBlockModel.intersect(ray, Texture.cobblestone);
        case 4:
        case 12:
          return TexturedBlockModel.intersect(ray, Texture.brick);
        case 5:
        case 13:
          return TexturedBlockModel.intersect(ray, Texture.stoneBrick);
        case 6:
        case 14:
          return TexturedBlockModel.intersect(ray, Texture.netherBrick);
        case 7:
        case 15:
          return TexturedBlockModel.intersect(ray, quartz);
        case 8:
          return TexturedBlockModel.intersect(ray, Texture.slabTop);
        case 9:
          return TexturedBlockModel.intersect(ray, Texture.sandstoneTop);
      }
    }
  };
  public static final int SLAB_ID = 0x2C;
  public static final Block SLAB = new Block(SLAB_ID, "block:stone_slab", Texture.slabTop) {
    final Texture[][] texture =
        {{Texture.slabSide, Texture.slabTop}, {Texture.sandstoneSide, Texture.sandstoneTop},
            {Texture.oakPlanks, Texture.oakPlanks}, {Texture.cobblestone, Texture.cobblestone},
            {Texture.brick, Texture.brick}, {Texture.stoneBrick, Texture.stoneBrick},
            {Texture.netherBrick, Texture.netherBrick}, {Texture.quartzSide, Texture.quartzTop},};
    final String[] slabKind =
        {"stone", "sandstone", "wood", "cobble", "brick", "stone brick", "nether brick", "quartz",};

    @Override public boolean intersect(Ray ray, Scene scene) {
      Texture[] textures = texture[ray.getBlockData() & 7];
      return SlabModel.intersect(ray, textures[0], textures[1]);
    }

    @Override public String description(int data) {
      return slabKind[data & 7];
    }
  };
  public static final int BRICKS_ID = 0x2D;
  public static final Block BRICKS = new Block(BRICKS_ID, "block:brick_block", Texture.brick);
  public static final int TNT_ID = 0x2E;
  public static final Block TNT = new Block(TNT_ID, "block:tnt", Texture.tntSide) {
    final Texture[] texture =
        {Texture.tntSide, Texture.tntSide, Texture.tntSide, Texture.tntSide, Texture.tntTop,
            Texture.tntBottom,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int BOOKSHELF_ID = 0x2F;
  public static final Block BOOKSHELF = new Block(BOOKSHELF_ID, "block:bookshelf", Texture.bookshelf) {
    final Texture[] texture =
        {Texture.bookshelf, Texture.bookshelf, Texture.bookshelf, Texture.bookshelf,
            Texture.oakPlanks, Texture.oakPlanks,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int MOSSSTONE_ID = 0x30;
  public static final Block MOSSSTONE = new Block(MOSSSTONE_ID, "block:mossy_cobblestone", Texture.mossStone);
  public static final int OBSIDIAN_ID = 0x31;
  public static final Block OBSIDIAN = new Block(OBSIDIAN_ID, "block:obsidian", Texture.obsidian);
  public static final int TORCH_ID = 0x32;
  public static final Block TORCH = new Block(TORCH_ID, "block:torch", Texture.torch) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TorchModel.intersect(ray, Texture.torch);
    }

    final String[] direction = {"", "east", "west", "south", "north", "on floor"};

    @Override public String description(int data) {
      return direction[data % 6];
    }
  };
  public static final int FIRE_ID = 0x33;
  public static final Block FIRE = new Block(FIRE_ID, "block:fire", Texture.fire) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FireModel.intersect(ray);
    }
  };
  public static final int MONSTERSPAWNER_ID = 0x34;
  public static final Block MONSTERSPAWNER = new Block(MONSTERSPAWNER_ID, "block:mob_spawner", Texture.monsterSpawner);
  public static final int OAKWOODSTAIRS_ID = 0x35;
  public static final Block OAKWOODSTAIRS = new Block(OAKWOODSTAIRS_ID, "block:oak_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.oakPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.oakPlanks;
    }
  };
  public static final int CHEST_ID = 0x36;
  public static final Block CHEST = new Block(CHEST_ID, "block:chest", Texture.chestFront) {
    final Texture[][] texture = {
        // single
        {Texture.chestFront, Texture.chestBack, Texture.chestLeft, Texture.chestRight,
            Texture.chestTop, Texture.chestBottom, Texture.chestLock, Texture.chestLock,
            Texture.chestLock, Texture.chestLock, Texture.chestLock,},

        // left
        {Texture.largeChestFrontLeft, Texture.largeChestBackLeft, Texture.largeChestLeft,
            Texture.largeChestTopLeft, Texture.largeChestBottomLeft, Texture.chestLock,
            Texture.chestLock, Texture.chestLock, Texture.chestLock,},

        // right
        {Texture.largeChestFrontRight, Texture.largeChestBackRight, Texture.largeChestRight,
            Texture.largeChestTopRight, Texture.largeChestBottomRight, Texture.chestLock,
            Texture.chestLock, Texture.chestLock, Texture.chestLock,}};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return ChestModel.intersect(ray, texture[(ray.getCurrentData() >> 16) % 3]);
    }
  };
  public static final int REDSTONEWIRE_ID = 0x37;
  public static final Block REDSTONEWIRE = new Block(REDSTONEWIRE_ID, "block:redstone_wire", Texture.redstoneWireCross) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RedstoneWireModel.intersect(ray);
    }

    @Override public String description(int data) {
      return "power: " + data;
    }
  };
  public static final int DIAMONDORE_ID = 0x38;
  public static final Block DIAMONDORE = new Block(DIAMONDORE_ID, "block:diamond_ore", Texture.diamondOre);
  public static final int DIAMONDBLOCK_ID = 0x39;
  public static final Block DIAMONDBLOCK = new Block(DIAMONDBLOCK_ID, "block:diamond_block", Texture.diamondBlock);
  public static final int WORKBENCH_ID = 0x3A;
  public static final Block WORKBENCH = new Block(WORKBENCH_ID, "block:crafting_table", Texture.workbenchFront) {
    final Texture[] texture =
        {Texture.workbenchFront, Texture.workbenchSide, Texture.workbenchSide,
            Texture.workbenchFront, Texture.workbenchTop, Texture.oakPlanks,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int CROPS_ID = 0x3B;
  public static final Block CROPS = new Block(CROPS_ID, "block:wheat", Texture.crops7) {
    final Texture[] texture =
        {Texture.crops0, Texture.crops1, Texture.crops2, Texture.crops3, Texture.crops4,
            Texture.crops5, Texture.crops6, Texture.crops7};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
    }
  };
  public static final int SOIL_ID = 0x3C;
  public static final Block SOIL = new Block(SOIL_ID, "block:farmland", Texture.farmlandWet) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FarmlandModel.intersect(ray);
    }
  };
  public static final int FURNACEUNLIT_ID = 0x3D;
  public static final Block FURNACEUNLIT = new Block(FURNACEUNLIT_ID, "block:furnace", Texture.furnaceUnlitFront) {
    final Texture[] texture =
        {Texture.furnaceUnlitFront, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceSide, Texture.furnaceTop, Texture.furnaceTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return FurnaceModel.intersect(ray, texture);
    }
  };
  public static final int FURNACELIT_ID = 0x3E;
  public static final Block FURNACELIT = new Block(FURNACELIT_ID, "block:lit_furnace", Texture.furnaceLitFront) {
    final Texture[] texture =
        {Texture.furnaceLitFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return FurnaceModel.intersect(ray, texture);
    }
  };
  public static final int SIGNPOST_ID = 0x3F;
  public static final Block SIGNPOST = new Block(SIGNPOST_ID, "block:standing_sign", Icon.signPost);
  public static final int WOODENDOOR_ID = 0x40;
  public static final Block WOODENDOOR = new Block(WOODENDOOR_ID, "block:wooden_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.woodenDoorBottom, Texture.woodenDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int LADDER_ID = 0x41;
  public static final Block LADDER = new Block(LADDER_ID, "block:ladder", Texture.ladder) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LadderModel.intersect(ray);
    }
  };
  public static final int MINECARTTRACKS_ID = 0x42;
  public static final Block MINECARTTRACKS = new Block(MINECARTTRACKS_ID, "block:rail", Texture.rails) {
    final Texture[] texture =
        {Texture.rails, Texture.rails, Texture.rails, Texture.rails, Texture.rails,
            Texture.rails, Texture.railsCurved, Texture.railsCurved, Texture.railsCurved,
            Texture.railsCurved};

    @Override public boolean intersect(Ray ray, Scene scene) {
      int type = ray.getBlockData() % 10;
      return RailModel.intersect(ray, texture[type], type);
    }
  };
  public static final int STONESTAIRS_ID = 0x43;
  public static final Block STONESTAIRS = new Block(STONESTAIRS_ID, "block:stone_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.cobblestone);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.stone;
    }
  };
  public static final int WALLSIGN_ID = 0x44;
  public static final Block WALLSIGN = new Block(WALLSIGN_ID, "block:wall_sign", Icon.wallSign);
  public static final int LEVER_ID = 0x45;
  public static final Block LEVER = new Block(LEVER_ID, "block:lever", Texture.lever) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LeverModel.intersect(ray);
    }
  };
  public static final int STONEPRESSUREPLATE_ID = 0x46;
  public static final Block STONEPRESSUREPLATE = new Block(STONEPRESSUREPLATE_ID, "block:stone_pressure_plate", Icon.stonePressurePlate) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.stone);
    }
  };
  public static final int IRONDOOR_ID = 0x47;
  public static final Block IRONDOOR = new Block(IRONDOOR_ID, "block:iron_door", Icon.ironDoor) {
    final Texture[] texture = {Texture.ironDoorBottom, Texture.ironDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int WOODENPRESSUREPLATE_ID = 0x48;
  public static final Block WOODENPRESSUREPLATE = new Block(WOODENPRESSUREPLATE_ID, "block:wooden_pressure_plate", Icon.woodenPressurePlate) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.oakPlanks);
    }
  };
  public static final int REDSTONEORE_ID = 0x49;
  public static final Block REDSTONEORE = new Block(REDSTONEORE_ID, "block:redstone_ore", Texture.redstoneOre);
  public static final int GLOWINGREDSTONEORE_ID = 0x4A;
  public static final Block GLOWINGREDSTONEORE = new Block(GLOWINGREDSTONEORE_ID, "block:lit_redstone_ore", Texture.redstoneOre);
  public static final int REDSTONETORCHOFF_ID = 0x4B;
  public static final Block REDSTONETORCHOFF = new Block(REDSTONETORCHOFF_ID, "block:unlit_redstone_torch", Texture.redstoneTorchOff) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TorchModel.intersect(ray, Texture.redstoneTorchOff);
    }

    final String[] direction = {"", "east", "west", "south", "north", "on floor"};

    @Override public String description(int data) {
      return direction[data % 6];
    }
  };
  public static final int REDSTONETORCHON_ID = 0x4C;
  public static final Block REDSTONETORCHON = new Block(REDSTONETORCHON_ID, "block:redstone_torch", Texture.redstoneTorchOn) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TorchModel.intersect(ray, Texture.redstoneTorchOn);
    }

    final String[] direction = {"", "east", "west", "south", "north", "on floor"};

    @Override public String description(int data) {
      return direction[data % 6];
    }
  };
  public static final int STONEBUTTON_ID = 0x4D;
  public static final Block STONEBUTTON = new Block(STONEBUTTON_ID, "block:stone_button", Icon.stoneButton) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ButtonModel.intersect(ray, Texture.stone);
    }
  };
  public static final int SNOW_ID = 0x4E;
  public static final Block SNOW = new Block(SNOW_ID, "block:snow_layer", Texture.snowBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SnowModel.intersect(ray);
    }
  };
  public static final int ICE_ID = 0x4F;
  public static final Block ICE = new Block(ICE_ID, "block:ice", Texture.ice);
  public static final int SNOWBLOCK_ID = 0x50;
  public static final Block SNOWBLOCK = new Block(SNOWBLOCK_ID, "block:snow", Texture.snowBlock);
  public static final int CACTUS_ID = 0x51;
  public static final Block CACTUS = new Block(CACTUS_ID, "block:cactus", Texture.cactusSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CactusModel.intersect(ray);
    }
  };
  public static final int CLAY_ID = 0x52;
  public static final Block CLAY = new Block(CLAY_ID, "block:clay", Texture.clay);
  public static final int SUGARCANE_ID = 0x53;
  public static final Block SUGARCANE = new Block(SUGARCANE_ID, "block:reeds", Texture.sugarCane) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.sugarCane);
    }
  };
  public static final int JUKEBOX_ID = 0x54;
  public static final Block JUKEBOX = new Block(JUKEBOX_ID, "block:jukebox", Texture.jukeboxSide) {
    final Texture[] texture =
        {Texture.jukeboxSide, Texture.jukeboxSide, Texture.jukeboxSide, Texture.jukeboxSide,
            Texture.jukeboxTop, Texture.jukeboxSide,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int FENCE_ID = 0x55;
  public static final Block FENCE = new Block(FENCE_ID, "block:fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.oakPlanks);
    }

    @Override protected boolean isFence() {
      return true;
    }
  };
  public static final int PUMPKIN_ID = 0x56;
  public static final Block PUMPKIN = new Block(PUMPKIN_ID, "block:pumpkin", Texture.pumpkinSide) {
    final Texture[] texture =
        {Texture.pumpkinFront, Texture.pumpkinSide, Texture.pumpkinSide, Texture.pumpkinSide,
            Texture.pumpkinTop, Texture.pumpkinTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return PumpkinModel.intersect(ray, texture);
    }
  };
  public static final int NETHERRACK_ID = 0x57;
  public static final Block NETHERRACK =
      new Block(NETHERRACK_ID, "block:netherrack", Texture.netherrack);
  public static final int SOULSAND_ID = 0x58;
  public static final Block SOULSAND = new Block(SOULSAND_ID, "block:soul_sand", Texture.soulsand);
  public static final int GLOWSTONE_ID = 0x59;
  public static final Block GLOWSTONE = new Block(GLOWSTONE_ID, "block:glowstone", Texture.glowstone);
  public static final int PORTAL_ID = 0x5A;
  public static final Block PORTAL = new Block(PORTAL_ID, "block:portal", Texture.portal);
  public static final int JACKOLANTERN_ID = 0x5B;
  public static final Block JACKOLANTERN = new Block(JACKOLANTERN_ID, "block:lit_pumpkin", Texture.jackolanternFront) {
    final Texture[] texture =
        {Texture.jackolanternFront, Texture.pumpkinSide, Texture.pumpkinSide,
            Texture.pumpkinSide, Texture.pumpkinTop, Texture.pumpkinTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return PumpkinModel.intersect(ray, texture);
    }
  };
  public static final int CAKE_ID = 0x5C;
  public static final Block CAKE = new Block(CAKE_ID, "block:cake", Icon.cake) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CakeModel.intersect(ray);
    }
  };
  public static final int REDSTONEREPEATEROFF_ID = 0x5D;
  public static final Block REDSTONEREPEATEROFF = new Block(REDSTONEREPEATEROFF_ID, "block:unpowered_repeater", Texture.redstoneRepeaterOff) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RedstoneRepeaterModel.intersect(ray, 0);
    }
  };
  public static final int REDSTONEREPEATERON_ID = 0x5E;
  public static final Block REDSTONEREPEATERON = new Block(REDSTONEREPEATERON_ID, "block:powered_repeater", Texture.redstoneRepeaterOn) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RedstoneRepeaterModel.intersect(ray, 1);
    }
  };
  public static final int STAINED_GLASS_ID = 0x5F;
  public static final Block STAINED_GLASS = new Block(STAINED_GLASS_ID, "block:stained_glass", Texture.glass) {
    @Override public Texture getTexture(int blockData) {
      return Texture.stainedGlass[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }

    @Override public boolean isSameMaterial(Material other) {
      return other == this || other == GLASS;
    }
  };
  public static final int TRAPDOOR_ID = 0x60;
  public static final Block TRAPDOOR = new Block(TRAPDOOR_ID, "block:trapdoor", Texture.trapdoor) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TrapdoorModel.intersect(ray, Texture.trapdoor);
    }
  };
  public static final int HIDDENSILVERFISH_ID = 0x61;
  public static final Block HIDDENSILVERFISH = new Block(HIDDENSILVERFISH_ID, "block:monster_egg", Texture.stone) {
    final Texture[] texture = {Texture.stone, Texture.cobblestone, Texture.stoneBrick};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % texture.length]);
    }
  };
  public static final int STONEBRICKS_ID = 0x62;
  public static final Block STONEBRICKS = new Block(STONEBRICKS_ID, "block:stonebrick", Texture.stoneBrick) {
    final Texture[] texture =
        {Texture.stoneBrick, Texture.mossyStoneBrick, Texture.crackedStoneBrick,
            Texture.circleStoneBrick};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[(ray.getCurrentData() >> 8) & 3]);
    }
  };
  public static final int HUGEBROWNMUSHROOM_ID = 0x63;
  public static final Block HUGEBROWNMUSHROOM = new Block(HUGEBROWNMUSHROOM_ID, "block:brown_mushroom_block", Texture.hugeBrownMushroom) {
    final Texture[][] texture = {
        // 0 = fleshy
        {Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores,
            Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores},

        // 1 = cap on top, west, north
        {Texture.hugeBrownMushroom, Texture.mushroomPores, Texture.mushroomPores,
            Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 2 = cap on top, north
        {Texture.hugeBrownMushroom, Texture.mushroomPores, Texture.mushroomPores,
            Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 3 = cap on top, east, north
        {Texture.hugeBrownMushroom, Texture.mushroomPores, Texture.hugeBrownMushroom,
            Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 4 = cap on top, west
        {Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores,
            Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 5 = cap on top
        {Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores,
            Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 6 = cap on top, east
        {Texture.mushroomPores, Texture.mushroomPores, Texture.hugeBrownMushroom,
            Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 7 = cap on top, west, south
        {Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores,
            Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 8 = cap on top, south
        {Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores,
            Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 9 = cap on top, east, south
        {Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom,
            Texture.mushroomPores, Texture.hugeBrownMushroom, Texture.mushroomPores},

        // 10 = stem
        {Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem,
            Texture.mushroomPores, Texture.mushroomPores},

        // 11 = undefined (cap on all sides)
        {Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom,
            Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom},

        // 12 = undefined (cap on all sides)
        {Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom,
            Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom},

        // 13 = undefined (cap on all sides)
        {Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom,
            Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom},

        // 14 = cap on all sides
        {Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom,
            Texture.hugeBrownMushroom, Texture.hugeBrownMushroom, Texture.hugeBrownMushroom},

        // 15 = stem on all sides
        {Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem,
            Texture.mushroomStem, Texture.mushroomStem}};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData()]);
    }
  };
  public static final int HUGEREDMUSHROOM_ID = 0x64;
  public static final Block HUGEREDMUSHROOM = new Block(HUGEREDMUSHROOM_ID, "block:red_mushroom_block", Texture.hugeRedMushroom) {
    final Texture[][] texture = {
        // 0 = fleshy
        {Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores,
            Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores},

        // 1 = cap on top, west, north
        {Texture.hugeRedMushroom, Texture.mushroomPores, Texture.mushroomPores,
            Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 2 = cap on top, north
        {Texture.hugeRedMushroom, Texture.mushroomPores, Texture.mushroomPores,
            Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 3 = cap on top, east, north
        {Texture.hugeRedMushroom, Texture.mushroomPores, Texture.hugeRedMushroom,
            Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 4 = cap on top, west
        {Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores,
            Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 5 = cap on top
        {Texture.mushroomPores, Texture.mushroomPores, Texture.mushroomPores,
            Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 6 = cap on top, east
        {Texture.mushroomPores, Texture.mushroomPores, Texture.hugeRedMushroom,
            Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 7 = cap on top, west, south
        {Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores,
            Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 8 = cap on top, south
        {Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores,
            Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 9 = cap on top, east, south
        {Texture.mushroomPores, Texture.hugeRedMushroom, Texture.hugeRedMushroom,
            Texture.mushroomPores, Texture.hugeRedMushroom, Texture.mushroomPores},

        // 10 = stem
        {Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem,
            Texture.mushroomPores, Texture.mushroomPores},

        // 11 = undefined (cap on all sides)
        {Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom,
            Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom},

        // 12 = undefined (cap on all sides)
        {Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom,
            Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom},

        // 13 = undefined (cap on all sides)
        {Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom,
            Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom},

        // 14 = cap on all sides
        {Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom,
            Texture.hugeRedMushroom, Texture.hugeRedMushroom, Texture.hugeRedMushroom},

        // 15 = stem on all sides
        {Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem, Texture.mushroomStem,
            Texture.mushroomStem, Texture.mushroomStem}};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 11]);
    }
  };
  public static final int IRONBARS_ID = 0x65;
  public static final Block IRONBARS = new Block(IRONBARS_ID, "block:iron_bars", Texture.ironBars) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return IronBarsModel.intersect(ray);
    }
  };
  public static final int GLASSPANE_ID = 0x66;
  public static final Block GLASSPANE = new Block(GLASSPANE_ID, "block:glass_pane", Texture.glass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return GlassPaneModel.intersect(ray, Texture.glass, Texture.glassPaneTop);
    }
  };
  public static final int MELON_ID = 0x67;
  public static final Block MELON = new Block(MELON_ID, "block:melon_block", Texture.melonSide) {
    final Texture texture[] =
        {Texture.melonSide, Texture.melonSide, Texture.melonSide, Texture.melonSide,
            Texture.melonTop, Texture.melonTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int PUMPKINSTEM_ID = 0x68;
  public static final Block PUMPKINSTEM = new Block(PUMPKINSTEM_ID, "block:pumpkin_stem", Texture.stemStraight) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return MelonStemModel.intersect(ray);
    }
  };
  public static final int MELONSTEM_ID = 0x69;
  public static final Block MELONSTEM = new Block(MELONSTEM_ID, "block:melon_stem", Texture.stemStraight) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return MelonStemModel.intersect(ray);
    }
  };
  public static final int VINES_ID = 0x6A;
  public static final Block VINES = new Block(VINES_ID, "block:vine", Texture.vines) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return VineModel.intersect(ray, scene);
    }
  };
  public static final int FENCEGATE_ID = 0x6B;
  public static final Block FENCEGATE = new Block(FENCEGATE_ID, "block:fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.oakPlanks);
    }

    @Override protected boolean isFenceGate() {
      return true;
    }
  };
  public static final int BRICKSTAIRS_ID = 0x6C;
  public static final Block BRICKSTAIRS = new Block(BRICKSTAIRS_ID, "block:brick_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.brick);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.brick;
    }
  };
  public static final int STONEBRICKSTAIRS_ID = 0x6D;
  public static final Block STONEBRICKSTAIRS = new Block(STONEBRICKSTAIRS_ID, "block:stone_brick_stairs", Icon.stoneBrickStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.stoneBrick);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.stoneBrick;
    }
  };
  public static final int MYCELIUM_ID = 0x6E;
  public static final Block MYCELIUM = new Block(MYCELIUM_ID, "block:mycelium", Texture.myceliumSide) {
    final Texture texture[] =
        {Texture.myceliumSide, Texture.myceliumSide, Texture.myceliumSide, Texture.myceliumSide,
            Texture.myceliumTop, Texture.dirt};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.myceliumTop;
    }
  };
  public static final int LILY_PAD_ID = 0x6F;
  public static final Block LILY_PAD = new Block(LILY_PAD_ID, "block:waterlily", Texture.lilyPad) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LilyPadModel.intersect(ray);
    }

    @Override public void getColor(Ray ray) {
      LilyPadModel.getColor(ray);
    }
  };
  public static final int NETHERBRICK_ID = 0x70;
  public static final Block NETHERBRICK = new Block(NETHERBRICK_ID, "block:nether_brick", Texture.netherBrick);
  public static final int NETHERBRICKFENCE_ID = 0x71;
  public static final Block NETHERBRICKFENCE = new Block(NETHERBRICKFENCE_ID, "block:nether_brick_fence", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.netherBrick);
    }

    @Override protected boolean isFence() {
      // Note: isFence() returns false since nether brick fence does not connect to normal fence.
      return false;
    }
  };
  public static final int NETHERBRICKSTAIRS_ID = 0x72;
  public static final Block NETHERBRICKSTAIRS = new Block(NETHERBRICKSTAIRS_ID, "block:nether_brick_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.netherBrick);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.netherBrick;
    }
  };
  public static final int NETHERWART_ID = 0x73;
  public static final Block NETHERWART = new Block(NETHERWART_ID, "block:nether_wart_block", Texture.netherWart2) {
    final Texture[] texture =
        {Texture.netherWart0, Texture.netherWart1, Texture.netherWart1, Texture.netherWart2};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() & 3]);
    }
  };
  public static final int ENCHNATMENTTABLE_ID = 0x74;
  public static final Block ENCHNATMENTTABLE = new Block(ENCHNATMENTTABLE_ID, "block:enchanting_table", Texture.enchantmentTableSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return EnchantmentTableModel.intersect(ray);
    }
  };
  public static final int BREWINGSTAND_ID = 0x75;
  public static final Block BREWINGSTAND = new Block(BREWINGSTAND_ID, "block:brewing_stand", Texture.brewingStandSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return BrewingStandModel.intersect(ray);
    }
  };
  public static final int CAULDRON_ID = 0x76;
  public static final Block CAULDRON = new Block(CAULDRON_ID, "block:cauldron", Texture.cauldronSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CauldronModel.intersect(ray);
    }
  };
  public static final int ENDPORTAL_ID = 0x77;
  public static final Block ENDPORTAL = new Block(ENDPORTAL_ID, "block:end_portal", Texture.endPortal) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return EndPortalModel.intersect(ray);
    }
  };
  public static final int ENDPORTALFRAME_ID = 0x78;
  public static final Block ENDPORTALFRAME = new Block(ENDPORTALFRAME_ID, "block:end_portal_frame", Texture.endPortalFrameTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return EndPortalFrameModel.intersect(ray);
    }
  };
  public static final int ENDSTONE_ID = 0x79;
  public static final Block ENDSTONE = new Block(ENDSTONE_ID, "block:end_stone", Texture.endStone);
  public static final int DRAGONEGG_ID = 0x7A;
  public static final Block DRAGONEGG = new Block(DRAGONEGG_ID, "block:dragon_egg", Texture.dragonEgg) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return DragonEggModel.intersect(ray);
    }
  };
  public static final int REDSTONELAMPOFF_ID = 0x7B;
  public static final Block REDSTONELAMPOFF = new Block(REDSTONELAMPOFF_ID, "block:redstone_lamp", Texture.redstoneLampOff);
  public static final int REDSTONELAMPON_ID = 0x7C;
  public static final Block REDSTONELAMPON = new Block(REDSTONELAMPON_ID, "block:lit_redstone_lamp", Texture.redstoneLampOn);
  public static final int DOUBLEWOODENSLAB_ID = 0x7D;
  public static final Block DOUBLEWOODENSLAB = new Block(DOUBLEWOODENSLAB_ID, "block:double_wooden_slab", Texture.oakPlanks) {
    final Texture[] texture =
        {Texture.oakPlanks, Texture.sprucePlanks, Texture.birchPlanks, Texture.jungleTreePlanks,
            Texture.acaciaPlanks, Texture.darkOakPlanks,};
    final String[] woodKind = {"oak", "spruce", "birch", "jungle", "acacia", "dark oak",};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 6]);
    }

    @Override public String description(int data) {
      return woodKind[data % 6];
    }
  };
  public static final int SINGLEWOODENSLAB_ID = 0x7E;
  public static final Block SINGLEWOODENSLAB = new Block(SINGLEWOODENSLAB_ID, "block:wooden_slab", Texture.oakPlanks) {
    final Texture[] texture =
        {Texture.oakPlanks, Texture.sprucePlanks, Texture.birchPlanks, Texture.jungleTreePlanks,
            Texture.acaciaPlanks, Texture.darkOakPlanks,};
    final String[] woodKind = {"oak", "spruce", "birch", "jungle", "acacia", "dark oak",};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return SlabModel.intersect(ray, texture[(ray.getBlockData() & 7) % 6]);
    }

    @Override public String description(int data) {
      return woodKind[(data & 7) % 6];
    }
  };
  public static final int COCOAPLANT_ID = 0x7F;
  public static final Block COCOAPLANT = new Block(COCOAPLANT_ID, "block:cocoa", Texture.cocoaPlantLarge) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CocoaPlantModel.intersect(ray);
    }

    final String[] cocoaSize = {"small", "medium", "large"};

    @Override public String description(int data) {
      return cocoaSize[(data & 15) >> 2];
    }
  };
  public static final int SANDSTONESTAIRS_ID = 0x80;
  public static final Block SANDSTONESTAIRS = new Block(SANDSTONESTAIRS_ID, "block:sandstone_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel
          .intersect(ray, Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.sandstoneSide;
    }
  };
  public static final int EMERALDORE_ID = 0x81;
  public static final Block EMERALDORE = new Block(EMERALDORE_ID, "block:emerald_ore", Texture.emeraldOre);
  public static final int ENDERCHEST_ID = 0x82;
  public static final Block ENDERCHEST = new Block(ENDERCHEST_ID, "block:ender_chest", Texture.unknown) {
    final Texture[] texture =
        {Texture.enderChestFront, Texture.enderChestBack, Texture.enderChestLeft,
            Texture.enderChestRight, Texture.enderChestTop, Texture.enderChestBottom,
            Texture.enderChestLock, Texture.enderChestLock, Texture.enderChestLock,
            Texture.enderChestLock, Texture.enderChestLock,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return ChestModel.intersect(ray, texture);
    }
  };
  public static final int TRIPWIREHOOK_ID = 0x83;
  public static final Block TRIPWIREHOOK = new Block(TRIPWIREHOOK_ID, "block:tripwire_hook", Texture.tripwireHook) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TripwireHookModel.intersect(ray);
    }
  };
  public static final int TRIPWIRE_ID = 0x84;
  public static final Block TRIPWIRE = new Block(TRIPWIRE_ID, "block:tripwire", Texture.tripwire) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TripwireModel.intersection(ray);
    }
  };
  public static final int EMERALDBLOCK_ID = 0x85;
  public static final Block EMERALDBLOCK = new Block(EMERALDBLOCK_ID, "block:emerald_block", Texture.emeraldBlock);
  public static final int SPRUCEWOODSTAIRS_ID = 0x86;
  public static final Block SPRUCEWOODSTAIRS = new Block(SPRUCEWOODSTAIRS_ID, "block:spruce_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.sprucePlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.sprucePlanks;
    }
  };
  public static final int BIRCHWOODSTAIRS_ID = 0x87;
  public static final Block BIRCHWOODSTAIRS = new Block(BIRCHWOODSTAIRS_ID, "block:birch_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.birchPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.birchPlanks;
    }
  };
  public static final int JUNGLEWOODSTAIRS_ID = 0x88;
  public static final Block JUNGLEWOODSTAIRS = new Block(JUNGLEWOODSTAIRS_ID, "block:jungle_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.jungleTreePlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.jungleTreePlanks;
    }
  };
  public static final int COMMAND_BLOCK_ID = 0x89;
  public static final Block COMMAND_BLOCK = new Block(COMMAND_BLOCK_ID, "block:command_block", Texture.commandBlockBack) {
    final Texture[][] texture = {
        {
            Texture.commandBlockBack,
            Texture.commandBlockFront,
            Texture.commandBlockSide
        },
        {
            Texture.commandBlockBack,
            Texture.commandBlockFront,
            Texture.commandBlockConditional
        },
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, texture[ray.getBlockData() >> 3]);
    }
  };
  public static final int BEACON_ID = 0x8A;
  public static final Block BEACON = new Block(BEACON_ID, "block:beacon", Texture.glass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return BeaconModel.intersect(ray);
    }
  };
  public static final int STONEWALL_ID = 0x8B;
  public static final Block STONEWALL = new Block(STONEWALL_ID, "block:cobblestone_wall", Texture.unknown) {
    final Texture[] texture = {Texture.cobblestone, Texture.mossStone};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return StoneWallModel.intersect(ray, texture[ray.getBlockData() & 1]);
    }
  };
  public static final int FLOWERPOT_ID = 0x8C;
  public static final Block FLOWERPOT = new Block(FLOWERPOT_ID, "block:flower_pot", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FlowerPotModel.intersect(ray, scene);
    }
  };
  public static final int CARROTS_ID = 0x8D;
  public static final Block CARROTS = new Block(CARROTS_ID, "block:carrots", Texture.carrots3) {
    final Texture[] texture =
        {Texture.carrots0, Texture.carrots0, Texture.carrots1, Texture.carrots1, Texture.carrots2,
            Texture.carrots2, Texture.carrots2, Texture.carrots3};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
    }
  };
  public static final int POTATOES_ID = 0x8E;
  public static final Block POTATOES = new Block(POTATOES_ID, "block:potatoes", Texture.potatoes3) {
    final Texture[] texture =
        {Texture.potatoes0, Texture.potatoes0, Texture.potatoes1, Texture.potatoes1,
            Texture.potatoes2, Texture.potatoes2, Texture.potatoes2, Texture.potatoes3};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
    }
  };
  public static final int WOODENBUTTON_ID = 0x8F;
  public static final Block WOODENBUTTON = new Block(WOODENBUTTON_ID, "block:wooden_button", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ButtonModel.intersect(ray, Texture.oakPlanks);
    }
  };
  public static final int HEAD_ID = 0x90;
  public static final Block HEAD = new Block(HEAD_ID, "block:skull", Texture.unknown);
  public static final int ANVIL_ID = 0x91;
  public static final Block ANVIL = new Block(ANVIL_ID, "block:anvil", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return AnvilModel.intersect(ray);
    }
  };
  public static final int TRAPPEDCHEST_ID = 0x92;
  public static final Block TRAPPEDCHEST = new Block(TRAPPEDCHEST_ID, "block:trapped_chest", Texture.chestFront) {
    final Texture[][] texture = {
        // single
        {Texture.trappedChestFront, Texture.trappedChestBack, Texture.trappedChestLeft,
            Texture.trappedChestRight, Texture.trappedChestTop, Texture.trappedChestBottom,
            Texture.trappedChestLock, Texture.trappedChestLock, Texture.trappedChestLock,
            Texture.trappedChestLock, Texture.trappedChestLock,},

        // left
        {Texture.largeTrappedChestFrontLeft, Texture.largeTrappedChestBackLeft,
            Texture.largeTrappedChestLeft, Texture.largeTrappedChestTopLeft,
            Texture.largeTrappedChestBottomLeft, Texture.trappedChestLock,
            Texture.trappedChestLock, Texture.trappedChestLock, Texture.chestLock,},

        // right
        {Texture.largeTrappedChestFrontRight, Texture.largeTrappedChestBackRight,
            Texture.largeTrappedChestRight, Texture.largeTrappedChestTopRight,
            Texture.largeTrappedChestBottomRight, Texture.trappedChestLock,
            Texture.trappedChestLock, Texture.trappedChestLock, Texture.trappedChestLock,}};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return ChestModel.intersect(ray, texture[(ray.getCurrentData() >> 16) % 3]);
    }
  };
  public static final int WEIGHTEDPRESSUREPLATELIGHT_ID = 0x93;
  public static final Block WEIGHTEDPRESSUREPLATELIGHT = new Block(WEIGHTEDPRESSUREPLATELIGHT_ID, "block:light_weighted_pressure_plate", Texture.goldBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.goldBlock);
    }
  };
  public static final int WEIGHTEDPRESSUREPLATEHEAVY_ID = 0x94;
  public static final Block WEIGHTEDPRESSUREPLATEHEAVY = new Block(WEIGHTEDPRESSUREPLATEHEAVY_ID, "block:heavy_weighted_pressure_plate", Texture.ironBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.ironBlock);
    }
  };
  public static final int COMPARATOR_ID = 0x95;
  public static final Block COMPARATOR = new Block(COMPARATOR_ID,
      "block:unpowered_comparator", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ComparatorModel.intersect(ray, 1 & (ray.getBlockData() >> 3));
    }
  };
  public static final int COMPARATOR_POWERED_ID = 0x96;
  public static final Block COMPARATOR_POWERED = new Block(COMPARATOR_POWERED_ID,
      "block:powered_comparator", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ComparatorModel.intersect(ray, 1);
    }
  };
  public static final int DAYLIGHTSENSOR_ID = 0x97;
  public static final Block DAYLIGHTSENSOR = new Block(DAYLIGHTSENSOR_ID,
      "block:daylight_detector", Texture.daylightDetectorTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return DaylightSensorModel.intersect(ray, Texture.daylightDetectorTop);
    }
  };
  public static final int REDSTONEBLOCK_ID = 0x98;
  public static final Block REDSTONEBLOCK = new Block(REDSTONEBLOCK_ID, "block:redstone_block", Texture.redstoneBlock);
  public static final int NETHERQUARTZORE_ID = 0x99;
  public static final Block NETHERQUARTZORE = new Block(NETHERQUARTZORE_ID, "block:quartz_ore", Texture.netherQuartzOre);
  public static final int HOPPER_ID = 0x9A;
  public static final Block HOPPER = new Block(HOPPER_ID, "block:hopper", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return HopperModel.intersect(ray);
    }
  };
  public static final int QUARTZ_ID = 0x9B;
  public static final Block QUARTZ = new Block(QUARTZ_ID, "block:quartz_block", Texture.quartzSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return QuartzModel.intersect(ray);
    }
  };
  public static final int QUARTZSTAIRS_ID = 0x9C;
  public static final Block QUARTZSTAIRS = new Block(QUARTZSTAIRS_ID, "block:quartz_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel
          .intersect(ray, Texture.quartzSide, Texture.quartzTop, Texture.quartzBottom);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.quartzSide;
    }
  };
  public static final int ACTIVATORRAIL_ID = 0x9D;
  public static final Block ACTIVATORRAIL = new Block(ACTIVATORRAIL_ID, "block:activator_rail", Texture.unknown) {
    final Texture[] texture = {Texture.activatorRail, Texture.activatorRailPowered};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return RailModel
          .intersect(ray, texture[ray.getBlockData() >>> 3], (ray.getBlockData() & 7) % 6);
    }
  };
  public static final int DROPPER_ID = 0x9E;
  public static final Block DROPPER = new Block(DROPPER_ID, "block:dropper", Texture.dropperFront) {
    final Texture[][] texture = {
        // Facing down.
        {Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
            Texture.furnaceTop, Texture.dropperFrontVertical},
        // Facing up.
        {Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop, Texture.furnaceTop,
            Texture.dropperFrontVertical, Texture.furnaceTop},
        // Facing north.
        {Texture.dropperFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,},
        // Facing south.
        {Texture.furnaceSide, Texture.dropperFront, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,},
        // Facing east.
        {Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide, Texture.dropperFront,
            Texture.furnaceTop, Texture.furnaceTop,},
        // Facing west.
        {Texture.furnaceSide, Texture.furnaceSide, Texture.dropperFront, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,},
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 6]);
    }
  };
  public static final int STAINED_CLAY_ID = 0x9F;
  public static final Block STAINED_CLAY = new Block(STAINED_CLAY_ID, "block:stained_hardened_clay", Texture.clay) {
    @Override public Texture getTexture(int blockData) {
      return Texture.stainedClay[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final int STAINED_GLASSPANE_ID = 0xA0;
  public static final Block STAINED_GLASSPANE = new Block(STAINED_GLASSPANE_ID, "block:stained_glass_pane", Texture.glass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      int data = ray.getBlockData();
      return GlassPaneModel
          .intersect(ray, Texture.stainedGlass[data], Texture.stainedGlassPaneSide[data]);
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final int LEAVES2_ID = 0xA1;
  public static final Block LEAVES2 = new Block(LEAVES2_ID, "block:leaves", Texture.oakLeaves) {
    final Texture[] texture =
        {Texture.acaciaLeaves, Texture.darkOakLeaves, Texture.acaciaLeaves, Texture.darkOakLeaves};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return LeafModel.intersect(ray, scene, getTexture(ray.getBlockData()));
    }

    final String[] woodType = {"acacia", "dark oak", "unknown", "unknown"};

    @Override public String description(int data) {
      return woodType[data & 3];
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 3];
    }
  };
  public static final int WOOD2_ID = 0xA2;
  public static final Block WOOD2 = new Block(WOOD2_ID, "block:log2", Texture.oakWood) {
    final Texture[][] texture =
        {{Texture.acaciaWood, Texture.acaciaWoodTop}, {Texture.darkOakWood, Texture.darkOakWoodTop},
            {Texture.acaciaWood, Texture.acaciaWoodTop},
            {Texture.darkOakWood, Texture.darkOakWoodTop}};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return WoodModel.intersect(ray, texture[ray.getBlockData() & 3]);
    }

    final String[] woodType = {"acacia", "dark oak", "unknown", "unknown"};

    @Override public String description(int data) {
      return woodType[data & 3];
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 3][1];
    }
  };
  public static final int ACACIASTAIRS_ID = 0xA3;
  public static final Block ACACIASTAIRS = new Block(ACACIASTAIRS_ID, "block:acacia_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.acaciaPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.acaciaPlanks;
    }
  };
  public static final int DARKOAKSTAIRS_ID = 0xA4;
  public static final Block DARKOAKSTAIRS = new Block(DARKOAKSTAIRS_ID, "block:dark_oak_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.darkOakPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.darkOakPlanks;
    }
  };
  public static final int SLIMEBLOCK_ID = 0xA5;
  public static final Block SLIMEBLOCK = new Block(SLIMEBLOCK_ID, "block:slime", Texture.slime);
  public static final int BARRIER_ID = 0xA6;
  public static final Block BARRIER = new Block(BARRIER_ID, "block:barrier", Texture.unknown);
  public static final int IRON_TRAPDOOR_ID = 0xA7;
  public static final Block IRON_TRAPDOOR = new Block(IRON_TRAPDOOR_ID, "block:iron_trapdoor", Texture.ironTrapdoor) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TrapdoorModel.intersect(ray, Texture.ironTrapdoor);
    }
  };
  public static final int PRISMARINE_ID = 0xA8;
  public static final Block PRISMARINE = new Block(PRISMARINE_ID, "block:prismarine", Texture.prismarine) {
    final Texture[][] textures =
        {{Texture.prismarine, Texture.prismarine, Texture.prismarine, Texture.prismarine,
            Texture.prismarine, Texture.prismarine,},
            {Texture.prismarineBricks, Texture.prismarineBricks, Texture.prismarineBricks,
                Texture.prismarineBricks, Texture.prismarineBricks, Texture.prismarineBricks,},
            {Texture.darkPrismarine, Texture.darkPrismarine, Texture.darkPrismarine,
                Texture.darkPrismarine, Texture.darkPrismarine, Texture.darkPrismarine,},};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, textures[ray.getBlockData() % 3]);
    }

    final String[] kind = {"rough", "bricks", "dark"};

    @Override public String description(int data) {
      return kind[data % 3];
    }
  };
  public static final int SEALANTERN_ID = 0xA9;
  public static final Block SEALANTERN = new Block(SEALANTERN_ID, "block:sea_lantern", Texture.seaLantern);
  public static final int HAY_BLOCK_ID = 0xAA;
  public static final Block HAY_BLOCK = new Block(HAY_BLOCK_ID, "block:hay_block", Texture.hayBlockSide) {
    final Texture[] texture = {Texture.hayBlockSide, Texture.hayBlockTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return WoodModel.intersect(ray, texture);
    }
  };
  public static final int CARPET_ID = 0xAB;
  public static final Block CARPET = new Block(CARPET_ID, "block:carpet", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CarpetModel.intersect(ray, Texture.wool[ray.getBlockData()]);
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final int HARDENED_CLAY_ID = 0xAC;
  public static final Block HARDENED_CLAY = new Block(HARDENED_CLAY_ID, "block:hardened_clay", Texture.hardenedClay) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, Texture.hardenedClay);
    }
  };
  public static final int BLOCK_OF_COAL_ID = 0xAD;
  public static final Block COAL_BLOCK = new Block(BLOCK_OF_COAL_ID, "block:coal_block", Texture.coalBlock);
  public static final int PACKED_ICE_ID = 0xAE;
  public static final Block PACKED_ICE = new Block(PACKED_ICE_ID, "block:packed_ice", Texture.packedIce);
  public static final int LARGE_FLOWER_ID = 0xAF;
  public static final Block LARGE_FLOWER = new Block(LARGE_FLOWER_ID, "block:double_plant", Texture.dandelion) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LargeFlowerModel.intersect(ray, scene);
    }
  };
  public static final int STANDING_BANNER_ID = 0xB0;
  public static final Block STANDING_BANNER = new Block(STANDING_BANNER_ID,
      "block:standing_banner", Texture.unknown);
  public static final int WALL_BANNER_ID = 0xB1;
  public static final Block WALL_BANNER = new Block(WALL_BANNER_ID, "block:wall_banner", Texture.unknown);
  public static final Block INVERTED_DAYLIGHTSENSOR = new Block(0xB2,
      "block:daylight_detector_inverted", Texture.daylightDetectorTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return DaylightSensorModel.intersect(ray, Texture.daylightDetectorInvertedTop);
    }
  };
  public static final int REDSANDSTONE_ID = 0xB3;
  public static final Block REDSANDSTONE = new Block(REDSANDSTONE_ID, "block:red_standstone", Texture.redSandstoneSide) {
    final Texture[][] texture = {
        // normal
        {Texture.redSandstoneSide, Texture.redSandstoneSide, Texture.redSandstoneSide,
            Texture.redSandstoneSide, Texture.redSandstoneTop, Texture.redSandstoneBottom,},

        // decorated
        {Texture.redSandstoneDecorated, Texture.redSandstoneDecorated,
            Texture.redSandstoneDecorated, Texture.redSandstoneDecorated,
            Texture.redSandstoneTop, Texture.redSandstoneBottom,},

        // smooth
        {Texture.redSandstoneSmooth, Texture.redSandstoneSmooth, Texture.redSandstoneSmooth,
            Texture.redSandstoneSmooth, Texture.redSandstoneTop, Texture.redSandstoneBottom,},};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 3]);
    }
  };
  public static final int REDSANDSTONESTAIRS_ID = 0xB4;
  public static final Block REDSANDSTONESTAIRS = new Block(REDSANDSTONESTAIRS_ID, "block:red_standstone_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.redSandstoneSide, Texture.redSandstoneTop,
          Texture.redSandstoneBottom);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.redSandstoneSide;
    }
  };
  public static final int DOUBLESLAB2_ID = 0xB5;
  public static final Block DOUBLESLAB2 = new Block(DOUBLESLAB2_ID, "block:double_stone_slab2", Texture.redSandstoneTop) {
    final Texture[] textures =
        {Texture.redSandstoneSide, Texture.redSandstoneSide, Texture.redSandstoneSide,
            Texture.redSandstoneSide, Texture.redSandstoneTop, Texture.redSandstoneTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, textures);
    }
  };
  public static final int SLAB2_ID = 0xB6;
  public static final Block SLAB2 = new Block(SLAB2_ID, "block:stone_slab2", Texture.redSandstoneTop) {
    final Texture[] textures = {Texture.redSandstoneSide, Texture.redSandstoneTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return SlabModel.intersect(ray, textures[0], textures[1]);
    }
  };
  public static final int SPRUCEFENCEGATE_ID = 0xB7;
  public static final Block SPRUCEFENCEGATE = new Block(SPRUCEFENCEGATE_ID, "block:spruce_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.sprucePlanks);
    }

    @Override protected boolean isFence() {
      return true;
    }

    @Override protected boolean isFenceGate() {
      return true;
    }
  };
  public static final int BIRCHFENCEGATE_ID = 0xB8;
  public static final Block BIRCHFENCEGATE = new Block(BIRCHFENCEGATE_ID, "block:birch_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.birchPlanks);
    }

    @Override protected boolean isFenceGate() {
      return true;
    }
  };
  public static final int JUNGLEFENCEGATE_ID = 0xB9;
  public static final Block JUNGLEFENCEGATE = new Block(JUNGLEFENCEGATE_ID, "block:jungle_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.jungleTreePlanks);
    }

    @Override protected boolean isFenceGate() {
      return true;
    }
  };
  public static final int DARKOAKFENCEGATE_ID = 0xBA;
  public static final Block DARKOAKFENCEGATE = new Block(DARKOAKFENCEGATE_ID, "block:dark_oak_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.darkOakPlanks);
    }

    @Override protected boolean isFenceGate() {
      return true;
    }
  };
  public static final int ACACIAFENCEGATE_ID = 0xBB;
  public static final Block ACACIAFENCEGATE = new Block(ACACIAFENCEGATE_ID, "block:acacia_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.acaciaPlanks);
    }

    @Override protected boolean isFenceGate() {
      return true;
    }
  };
  public static final int SPRUCEFENCE_ID = 0xBC;
  public static final Block SPRUCEFENCE = new Block(SPRUCEFENCE_ID, "block:spruce_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.sprucePlanks);
    }

    @Override protected boolean isFence() {
      return true;
    }
  };
  public static final int BIRCHFENCE_ID = 0xBD;
  public static final Block BIRCHFENCE = new Block(BIRCHFENCE_ID, "block:birch_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.birchPlanks);
    }

    @Override protected boolean isFence() {
      return true;
    }
  };
  public static final int JUNGLEFENCE_ID = 0xBE;
  public static final Block JUNGLEFENCE = new Block(JUNGLEFENCE_ID, "block:jungle_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.jungleTreePlanks);
    }

    @Override protected boolean isFence() {
      return true;
    }
  };
  public static final int DARKOAKFENCE_ID = 0xBF;
  public static final Block DARKOAKFENCE =
      new Block(DARKOAKFENCE_ID, "block:dark_oak_fence", Icon.fence) {
        @Override public boolean intersect(Ray ray, Scene scene) {
          return FenceModel.intersect(ray, Texture.darkOakPlanks);
        }

        @Override protected boolean isFence() {
          return true;
        }
      };
  public static final int ACACIAFENCE_ID = 0xC0;
  public static final Block ACACIAFENCE = new Block(ACACIAFENCE_ID, "block:acacia_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.acaciaPlanks);
    }

    @Override protected boolean isFence() {
      return true;
    }
  };
  public static final int SPRUCEDOOR_ID = 0xC1;
  public static final Block SPRUCEDOOR = new Block(SPRUCEDOOR_ID, "block:spruce_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.spruceDoorBottom, Texture.spruceDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int BIRCHDOOR_ID = 0xC2;
  public static final Block BIRCHDOOR = new Block(BIRCHDOOR_ID, "block:birch_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.birchDoorBottom, Texture.birchDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int JUNGLEDOOR_ID = 0xC3;
  public static final Block JUNGLEDOOR = new Block(JUNGLEDOOR_ID, "block:jungle_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.jungleDoorBottom, Texture.jungleDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int ACACIADOOR_ID = 0xC4;
  public static final Block ACACIADOOR = new Block(ACACIADOOR_ID, "block:acacia_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.acaciaDoorBottom, Texture.acaciaDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int DARKOAKDOOR_ID = 0xC5;
  public static final Block DARKOAKDOOR = new Block(DARKOAKDOOR_ID, "block:dark_oak_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.darkOakDoorBottom, Texture.darkOakDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int ENDROD_ID = 0xC6;
  public static final Block ENDROD = new Block(ENDROD_ID, "block:end_rod", Texture.endRod) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return EndRodModel.intersect(ray);
    }

    final String[] direction = {
        "down", "up", "north", "south", "west", "east"
    };

    @Override
    public String description(int data) {
      return direction[data % 6];
    }
  };
  public static final int CHORUSPLANT_ID = 0xC7;
  public static final Block CHORUSPLANT =
      new Block(CHORUSPLANT_ID, "block:chorus_plant", Texture.chorusPlant) {
        @Override public boolean intersect(Ray ray, Scene scene) {
          return ChorusPlantModel.intersect(ray);
        }
      };
  public static final int CHORUSFLOWER_ID = 0xC8;
  public static final Block CHORUSFLOWER =
      new Block(CHORUSFLOWER_ID, "block:chorus_flower", Texture.chorusFlower) {
        @Override public boolean intersect(Ray ray, Scene scene) {
          return ChorusFlowerModel.intersect(ray);
        }

        @Override public Texture getTexture(int blockData) {
          return blockData < 5 ? Texture.chorusFlower : Texture.chorusFlowerDead;
        }
      };
  public static final int PURPURBLOCK_ID = 0xC9;
  public static final Block PURPURBLOCK = new Block(PURPURBLOCK_ID, "block:purpur_block", Texture.purpurBlock);
  public static final int PURPURPILLAR_ID = 0xCA;
  public static final Block PURPURPILLAR = new Block(PURPURPILLAR_ID, "block:purpur_pillar", Texture.purpurPillarSide) {
    final Texture[] texture =
        {Texture.purpurPillarSide, Texture.purpurPillarSide, Texture.purpurPillarSide,
            Texture.purpurPillarSide, Texture.purpurPillarTop, Texture.purpurPillarTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 7]);
    }
  };
  public static final int PURPURSTAIRS_ID = 0xCB;
  public static final Block PURPURSTAIRS = new Block(PURPURSTAIRS_ID, "block:purpur_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.purpurBlock);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.purpurBlock;
    }
  };
  public static final int PURPURDOUBLESLAB_ID = 0xCC;
  public static final Block PURPURDOUBLESLAB = new Block(PURPURDOUBLESLAB_ID, "block:purpur_double_slab", Texture.purpurBlock);
  public static final int PURPURSLAB_ID = 0xCD;
  public static final Block PURPURSLAB = new Block(PURPURSLAB_ID, "block:purpur_slab", Texture.purpurBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SlabModel.intersect(ray, Texture.purpurBlock);
    }
  };
  public static final int ENDBRICKS_ID = 0xCE;
  public static final Block ENDBRICKS = new Block(ENDBRICKS_ID, "block:end_bricks", Texture.endBricks);
  public static final Block BEETROOTS = new Block(0xCF, "block:beetroots", Texture.unknown);
  public static final int GRASSPATH_ID = 0xD0;
  public static final Block GRASSPATH = new Block(GRASSPATH_ID, "block:grass_path", Texture.grassPathTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return GrassPathModel.intersect(ray);
    }
  };
  public static final Block END_GATEWAY = new Block(0xD1, "block:end_gateway", Texture.unknown);
  public static final int REPEATING_COMMAND_BLOCK_ID = 0xD2;
  public static final Block REPEATING_COMMAND_BLOCK = new Block(REPEATING_COMMAND_BLOCK_ID,
      "block:repeating_command_block", Texture.repeatingCommandBlockBack) {
    final Texture[][] texture = {
        {
            Texture.repeatingCommandBlockBack,
            Texture.repeatingCommandBlockFront,
            Texture.repeatingCommandBlockSide
        },
        {
            Texture.repeatingCommandBlockBack,
            Texture.repeatingCommandBlockFront,
            Texture.repeatingCommandBlockConditional
        }
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, texture[ray.getBlockData() >> 3]);
    }
  };
  public static final int CHAIN_COMMAND_BLOCK_ID = 0xD3;
  public static final Block CHAIN_COMMAND_BLOCK = new Block(CHAIN_COMMAND_BLOCK_ID,
      "block:chain_command_block", Texture.chainCommandBlockBack) {
    final Texture[][] texture = {
        {
            Texture.chainCommandBlockBack,
            Texture.chainCommandBlockFront,
            Texture.chainCommandBlockSide
        },
        {
            Texture.chainCommandBlockBack,
            Texture.chainCommandBlockFront,
            Texture.chainCommandBlockConditional
        }
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, texture[ray.getBlockData() >> 3]);
    }
  };
  // TODO: render frosted ice cracks.
  public static final Block FROSTEDICE = new Block(0xD4, "block:frosted_ice", Texture.ice);
  public static final Block MAGMA = new Block(0xD5, "block:magma", Texture.magma);
  public static final Block NETHER_WART_BLOCK = new Block(0xD6, "block:nether_wart_block",
      Texture.netherWartBlock);
  public static final Block RED_NETHER_BRICK = new Block(0xD7, "block:red_nether_brick",
      Texture.redNetherBrick);
  public static final Block BONE = new Block(0xD8, "block:bone_block", Texture.boneSide) {
    final Texture[] texture = {Texture.boneSide, Texture.boneTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return WoodModel.intersect(ray, texture);
    }
  };
  private static final Block STRUCTURE_VOID = new Block(0xD9, "Structure Void", Texture.unknown);
  public static final Block OBSERVER = new Block(0xDA, "block:observer", Texture.unknown) {
    final Texture[] texture = {
        Texture.observerBack, Texture.observerFront,
        Texture.observerSide, Texture.observerTop
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return ObserverModel.intersect(ray, texture);
    }
  };
  public static final Block SHULKERBOX_WHITE = new Block(0xDB, "block:white_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerWhite.bottom,
        Texture.shulkerWhite.top,
        Texture.shulkerWhite.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_ORANGE = new Block(0xDC, "block:orange_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerOrange.bottom,
        Texture.shulkerOrange.top,
        Texture.shulkerOrange.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_MAGENTA = new Block(0xDD, "block:magenta_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerMagenta.bottom,
        Texture.shulkerMagenta.top,
        Texture.shulkerMagenta.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_LIGHTBLUE = new Block(0xDE, "block:ligth_blue_shuler_box",
      Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerLightBlue.bottom,
        Texture.shulkerLightBlue.top,
        Texture.shulkerLightBlue.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_YELLOW = new Block(0xDF, "block:yellow_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerYellow.bottom,
        Texture.shulkerYellow.top,
        Texture.shulkerYellow.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_LIME = new Block(0xE0, "block:lime_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerLime.bottom,
        Texture.shulkerLime.top,
        Texture.shulkerLime.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_PINK = new Block(0xE1, "block:pink_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerPink.bottom,
        Texture.shulkerPink.top,
        Texture.shulkerPink.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_GRAY = new Block(0xE2, "block:gray_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerGray.bottom,
        Texture.shulkerGray.top,
        Texture.shulkerGray.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_SILVER = new Block(0xE3, "block:light_gray_shuler_box",
      Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerSilver.bottom,
        Texture.shulkerSilver.top,
        Texture.shulkerSilver.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_CYAN = new Block(0xE4, "block:cyan_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerCyan.bottom,
        Texture.shulkerCyan.top,
        Texture.shulkerCyan.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_PURPLE = new Block(0xE5, "block:purple_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerPurple.bottom,
        Texture.shulkerPurple.top,
        Texture.shulkerPurple.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_BLUE = new Block(0xE6, "block:blue_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerBlue.bottom,
        Texture.shulkerBlue.top,
        Texture.shulkerBlue.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_BROWN = new Block(0xE7, "block:brown_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerBrown.bottom,
        Texture.shulkerBrown.top,
        Texture.shulkerBrown.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_GREEN = new Block(0xE8, "block:green_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerGreen.bottom,
        Texture.shulkerGreen.top,
        Texture.shulkerGreen.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_RED = new Block(0xE9, "block:red_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerRed.bottom,
        Texture.shulkerRed.top,
        Texture.shulkerRed.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block SHULKERBOX_BLACK = new Block(0xEA, "block:black_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerBlack.bottom,
        Texture.shulkerBlack.top,
        Texture.shulkerBlack.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final Block WHITE_TERRACOTTA = new Block(0xEB, "block:white_glazed_terracotta",
      Texture.terracottaWhite) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaWhite);
    }
  };
  public static final Block ORANGE_TERRACOTTA = new Block(0xEC, "block:orange_glazed_terracotta",
      Texture.terracottaOrange) {
    {
      isOpaque = true;
      isSolid = true;
      localIntersect = true;
    }

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaOrange);
    }
  };
  public static final Block MAGENTA_TERRACOTTA = new Block(0xED, "block:magenta_glazed_terracotta",
      Texture.terracottaMagenta) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaMagenta);
    }
  };
  public static final Block LIGHT_BLUE_TERRACOTTA = new Block(0xEE, "block:light_blue_glazed_terracotta",
      Texture.terracottaLightBlue) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaLightBlue);
    }
  };
  public static final Block YELLOW_TERRACOTTA = new Block(0xEF, "block:yellow_glazed_terracotta",
      Texture.terracottaYellow) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaYellow);
    }
  };
  public static final Block LIME_TERRACOTTA = new Block(0xF0, "block:lime_glazed_terracotta",
      Texture.terracottaLime) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaLime);
    }
  };
  public static final Block PINK_TERRACOTTA = new Block(0xF1, "block:pink_glazed_terracotta", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaWhite);
    }
  };
  public static final Block GRAY_TERRACOTTA = new Block(0xF2, "block:gray_glazed_terracotta", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaWhite);
    }
  };
  public static final Block SILVER_TERRACOTTA = new Block(0xF3, "block:light_gray_glazed_terracotta",
      Texture.terracottaSilver) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaSilver);
    }
  };
  public static final Block CYAN_TERRACOTTA = new Block(0xF4, "block:cyan_glazed_terracotta",
      Texture.terracottaCyan) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaCyan);
    }
  };
  public static final Block PURPLE_TERRACOTTA = new Block(0xF5, "block:purple_glazed_terracotta",
      Texture.terracottaPurple) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaPurple);
    }
  };
  public static final Block BLUE_TERRACOTTA = new Block(0xF6, "block:blue_glazed_terracotta",
      Texture.terracottaBlue) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaBlue);
    }
  };
  public static final Block BROWN_TERRACOTTA = new Block(0xF7, "block:brown_glazed_terracotta",
      Texture.terracottaBrown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaBrown);
    }
  };
  public static final Block GREEN_TERRACOTTA = new Block(0xF8, "block:green_glazed_terracotta",
      Texture.terracottaGreen) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaGreen);
    }
  };
  public static final Block RED_TERRACOTTA = new Block(0xF9, "block:red_glazed_terracotta",
      Texture.terracottaRed) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaRed);
    }
  };
  public static final Block BLACK_TERRACOTTA = new Block(0xFA, "block:black_glazed_terracotta",
      Texture.terracottaBlack) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaBlack);
    }
  };
  public static final Block CONCRETE = new Block(0xFB, "block:concrete", Texture.unknown) {
    @Override public Texture getTexture(int blockData) {
      return Texture.concrete[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final Block CONCRETE_POWDER = new Block(0xFC, "block:concrete_powder", Texture.unknown) {
    @Override public Texture getTexture(int blockData) {
      return Texture.concretePowder[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  private static final Block UNKNOWN0xFD = new Block(0xFD, "Unknown Block 0xFD", Texture.unknown);
  private static final Block UNKNOWN0xFE = new Block(0xFE, "Unknown Block 0xFE", Texture.unknown);
  public static final Block STRUCTURE_BLOCK = new Block(0xFF, "block:structure_block", Texture.unknown);

  public static final Block[] blocks = {
      AIR, STONE, GRASS, DIRT, COBBLESTONE, WOODENPLANKS, SAPLING, BEDROCK, WATER, STATIONARYWATER,
      LAVA, STATIONARYLAVA, SAND, GRAVEL, GOLDORE, IRONORE, COALORE, WOOD, LEAVES, SPONGE,
      GLASS, LAPIS_ORE, LAPIS_BLOCK, DISPENSER, SANDSTONE, NOTEBLOCK, BED,
      POWEREDRAIL, DETECTORRAIL, STICKYPISTON, COBWEB, TALLGRASS, DEADBUSH, PISTON, PISTON_HEAD, WOOL,
      PISTON_EXTENSION, DANDELION, FLOWER, BROWNMUSHROOM, REDMUSHROOM,
      GOLDBLOCK, IRONBLOCK, DOUBLESLAB, SLAB, BRICKS, TNT, BOOKSHELF, MOSSSTONE, OBSIDIAN,
      TORCH, FIRE, MONSTERSPAWNER, OAKWOODSTAIRS, CHEST, REDSTONEWIRE, DIAMONDORE, DIAMONDBLOCK,
      WORKBENCH, CROPS, SOIL, FURNACEUNLIT, FURNACELIT, SIGNPOST, WOODENDOOR, LADDER,
      MINECARTTRACKS, STONESTAIRS, WALLSIGN, LEVER, STONEPRESSUREPLATE, IRONDOOR,
      WOODENPRESSUREPLATE, REDSTONEORE, GLOWINGREDSTONEORE, REDSTONETORCHOFF, REDSTONETORCHON,
      STONEBUTTON, SNOW, ICE, SNOWBLOCK, CACTUS, CLAY, SUGARCANE, JUKEBOX, FENCE, PUMPKIN,
      NETHERRACK, SOULSAND, GLOWSTONE, PORTAL, JACKOLANTERN, CAKE, REDSTONEREPEATEROFF,
      REDSTONEREPEATERON, STAINED_GLASS, TRAPDOOR, HIDDENSILVERFISH, STONEBRICKS,
      HUGEBROWNMUSHROOM, HUGEREDMUSHROOM, IRONBARS, GLASSPANE, MELON, PUMPKINSTEM, MELONSTEM,
      VINES, FENCEGATE, BRICKSTAIRS, STONEBRICKSTAIRS, MYCELIUM, LILY_PAD, NETHERBRICK,
      NETHERBRICKFENCE, NETHERBRICKSTAIRS, NETHERWART, ENCHNATMENTTABLE, BREWINGSTAND, CAULDRON,
      ENDPORTAL, ENDPORTALFRAME, ENDSTONE, DRAGONEGG, REDSTONELAMPOFF, REDSTONELAMPON,
      DOUBLEWOODENSLAB, SINGLEWOODENSLAB, COCOAPLANT, SANDSTONESTAIRS, EMERALDORE, ENDERCHEST,
      TRIPWIREHOOK, TRIPWIRE, EMERALDBLOCK, SPRUCEWOODSTAIRS, BIRCHWOODSTAIRS, JUNGLEWOODSTAIRS,
      COMMAND_BLOCK, BEACON, STONEWALL, FLOWERPOT, CARROTS, POTATOES, WOODENBUTTON, HEAD, ANVIL,
      TRAPPEDCHEST, WEIGHTEDPRESSUREPLATELIGHT, WEIGHTEDPRESSUREPLATEHEAVY, COMPARATOR,
      COMPARATOR_POWERED, DAYLIGHTSENSOR, REDSTONEBLOCK, NETHERQUARTZORE, HOPPER, QUARTZ,
      QUARTZSTAIRS, ACTIVATORRAIL, DROPPER, STAINED_CLAY, STAINED_GLASSPANE, LEAVES2, WOOD2,
      ACACIASTAIRS, DARKOAKSTAIRS, SLIMEBLOCK, BARRIER, IRON_TRAPDOOR, PRISMARINE, SEALANTERN,
      HAY_BLOCK, CARPET, HARDENED_CLAY, COAL_BLOCK, PACKED_ICE, LARGE_FLOWER, STANDING_BANNER,
      WALL_BANNER, INVERTED_DAYLIGHTSENSOR, REDSANDSTONE, REDSANDSTONESTAIRS, DOUBLESLAB2, SLAB2,
      SPRUCEFENCEGATE, BIRCHFENCEGATE, JUNGLEFENCEGATE, DARKOAKFENCEGATE, ACACIAFENCEGATE,
      SPRUCEFENCE, BIRCHFENCE, JUNGLEFENCE, DARKOAKFENCE, ACACIAFENCE, SPRUCEDOOR, BIRCHDOOR,
      JUNGLEDOOR, ACACIADOOR, DARKOAKDOOR, ENDROD, CHORUSPLANT, CHORUSFLOWER, PURPURBLOCK,
      PURPURPILLAR, PURPURSTAIRS, PURPURDOUBLESLAB, PURPURSLAB, ENDBRICKS, BEETROOTS,
      GRASSPATH, END_GATEWAY, REPEATING_COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, FROSTEDICE, MAGMA,
      NETHER_WART_BLOCK, RED_NETHER_BRICK, BONE, STRUCTURE_VOID, OBSERVER, SHULKERBOX_WHITE,
      SHULKERBOX_ORANGE, SHULKERBOX_MAGENTA, SHULKERBOX_LIGHTBLUE, SHULKERBOX_YELLOW,
      SHULKERBOX_LIME, SHULKERBOX_PINK, SHULKERBOX_GRAY, SHULKERBOX_SILVER, SHULKERBOX_CYAN,
      SHULKERBOX_PURPLE, SHULKERBOX_BLUE, SHULKERBOX_BROWN, SHULKERBOX_GREEN, SHULKERBOX_RED,
      SHULKERBOX_BLACK, WHITE_TERRACOTTA, ORANGE_TERRACOTTA, MAGENTA_TERRACOTTA,
      LIGHT_BLUE_TERRACOTTA, YELLOW_TERRACOTTA, LIME_TERRACOTTA, PINK_TERRACOTTA, GRAY_TERRACOTTA,
      SILVER_TERRACOTTA, CYAN_TERRACOTTA, PURPLE_TERRACOTTA, BLUE_TERRACOTTA, BROWN_TERRACOTTA,
      GREEN_TERRACOTTA, RED_TERRACOTTA, BLACK_TERRACOTTA, CONCRETE, CONCRETE_POWDER, UNKNOWN0xFD,
      UNKNOWN0xFE, STRUCTURE_BLOCK
  };

  public static final Map<String, Block> idMap = new HashMap<>();
  public static final Map<String, Collection<Block>> collections = new LinkedHashMap<>();

  static {
    collections.put("all:blocks", Arrays.asList(blocks));
    collections.put("all:water", Arrays.asList(Block.WATER, Block.STATIONARYWATER));
    collections.put("all:lava", Arrays.asList(Block.LAVA, Block.STATIONARYLAVA));

    idMap.put("block:stone", Block.STONE);
    idMap.put("block:grass", Block.GRASS);
    idMap.put("block:dirt", Block.DIRT);
    idMap.put("block:cobblestone", Block.COBBLESTONE);
    idMap.put("block:planks", Block.WOODENPLANKS);
    idMap.put("block:sapling", Block.SAPLING);
    idMap.put("block:bedrock", Block.BEDROCK);
    idMap.put("block:flowing_water", Block.WATER);
    idMap.put("block:water", Block.STATIONARYWATER);
    idMap.put("block:flowing_lava", Block.LAVA);
    idMap.put("block:lava", Block.STATIONARYLAVA);
    idMap.put("block:sand", Block.SAND);
    idMap.put("block:gravel", Block.GRAVEL);
    idMap.put("block:gold_ore", Block.GOLDORE);
    idMap.put("block:iron_ore", Block.IRONORE);

    idMap.put("block:coal_ore", Block.COALORE);
    idMap.put("block:log", Block.WOOD);
    idMap.put("block:leaves", Block.LEAVES);
    idMap.put("block:sponge", Block.SPONGE);
    idMap.put("block:glass", Block.GLASS);
    idMap.put("block:lapis_ore", Block.LAPIS_ORE);
    idMap.put("block:lapis_block", Block.LAPIS_BLOCK);
    idMap.put("block:dispenser", Block.DISPENSER);
    idMap.put("block:sandstone", Block.SANDSTONE);
    idMap.put("block:noteblock", Block.NOTEBLOCK);
    idMap.put("block:bed", Block.BED);
    idMap.put("block:golden_rail", Block.POWEREDRAIL);
    idMap.put("block:detector_rail", Block.DETECTORRAIL);
    idMap.put("block:sticky_piston", Block.STICKYPISTON);
    idMap.put("block:web", Block.COBWEB);
    idMap.put("block:tallgrass", Block.TALLGRASS);

    idMap.put("block:deadbush", Block.DEADBUSH);
    idMap.put("block:piston", Block.PISTON);
    idMap.put("block:piston_head", Block.PISTON_HEAD);
    idMap.put("block:wool", Block.WOOL);
    idMap.put("block:piston_extension", Block.PISTON_EXTENSION);
    idMap.put("block:yellow_flower", Block.DANDELION);
    idMap.put("block:red_flower", Block.FLOWER);
    idMap.put("block:brown_mushroom", Block.BROWNMUSHROOM);
    idMap.put("block:red_mushroom", Block.REDMUSHROOM);
    idMap.put("block:gold_block", Block.GOLDBLOCK);
    idMap.put("block:iron_block", Block.IRONBLOCK);
    idMap.put("block:double_stone_slab", Block.DOUBLESLAB);
    idMap.put("block:stone_slab", Block.SLAB);
    idMap.put("block:brick_block", Block.BRICKS);
    idMap.put("block:tnt", Block.TNT);
    idMap.put("block:bookshelf", Block.BOOKSHELF);

    idMap.put("block:mossy_cobblestone", Block.MOSSSTONE);
    idMap.put("block:obsidian", Block.OBSIDIAN);
    idMap.put("block:torch", Block.TORCH);
    idMap.put("block:fire", Block.FIRE);
    idMap.put("block:mob_spawner", Block.MONSTERSPAWNER);
    idMap.put("block:oak_stairs", Block.OAKWOODSTAIRS);
    idMap.put("block:chest", Block.CHEST);
    idMap.put("block:redstone_wire", Block.REDSTONEWIRE);
    idMap.put("block:diamond_ore", Block.DIAMONDORE);
    idMap.put("block:diamond_block", Block.DIAMONDBLOCK);
    idMap.put("block:crafting_table", Block.WORKBENCH);
    idMap.put("block:wheat", Block.CROPS);
    idMap.put("block:farmland", Block.SOIL);
    idMap.put("block:furnace", Block.FURNACEUNLIT);
    idMap.put("block:lit_furnace", Block.FURNACELIT);
    idMap.put("block:standing_sign", Block.SIGNPOST);

    idMap.put("block:wooden_door", Block.WOODENDOOR);
    idMap.put("block:ladder", Block.LADDER);
    idMap.put("block:rail", Block.MINECARTTRACKS);
    idMap.put("block:stone_stairs", Block.STONESTAIRS);
    idMap.put("block:wall_sign", Block.WALLSIGN);
    idMap.put("block:lever", Block.LEVER);
    idMap.put("block:stone_pressure_plate", Block.STONEPRESSUREPLATE);
    idMap.put("block:iron_door", Block.IRONDOOR);
    idMap.put("block:wooden_pressure_plate", Block.WOODENPRESSUREPLATE);
    idMap.put("block:redstone_ore", Block.REDSTONEORE);
    idMap.put("block:lit_redstone_ore", Block.GLOWINGREDSTONEORE);
    idMap.put("block:unlit_redstone_torch", Block.REDSTONETORCHOFF);
    idMap.put("block:redstone_torch", Block.REDSTONETORCHON);
    idMap.put("block:stone_button", Block.STONEBUTTON);
    idMap.put("block:snow_layer", Block.SNOW);
    idMap.put("block:ice", Block.ICE);

    idMap.put("block:snow", Block.SNOWBLOCK);
    idMap.put("block:cactus", Block.CACTUS);
    idMap.put("block:clay", Block.CLAY);
    idMap.put("block:reeds", Block.SUGARCANE);
    idMap.put("block:jukebox", Block.JUKEBOX);
    idMap.put("block:fence", Block.FENCE);
    idMap.put("block:pumpkin", Block.PUMPKIN);
    idMap.put("block:netherrack", Block.NETHERRACK);
    idMap.put("block:soul_sand", Block.SOULSAND);
    idMap.put("block:glowstone", Block.GLOWSTONE);
    idMap.put("block:portal", Block.PORTAL);
    idMap.put("block:lit_pumpkin", Block.JACKOLANTERN);
    idMap.put("block:cake", Block.CAKE);
    idMap.put("block:unpowered_repeater", Block.REDSTONEREPEATEROFF);
    idMap.put("block:powered_repeater", Block.REDSTONEREPEATERON);
    idMap.put("block:stained_glass", Block.STAINED_GLASS);

    idMap.put("block:trapdoor", Block.TRAPDOOR);
    idMap.put("block:monster_egg", Block.HIDDENSILVERFISH);
    idMap.put("block:stonebrick", Block.STONEBRICKS);
    idMap.put("block:brown_mushroom_block", Block.HUGEBROWNMUSHROOM);
    idMap.put("block:red_mushroom_block", Block.HUGEREDMUSHROOM);
    idMap.put("block:iron_bars", Block.IRONBARS);
    idMap.put("block:glass_pane", Block.GLASSPANE);
    idMap.put("block:melon_block", Block.MELON);
    idMap.put("block:pumpkin_stem", Block.PUMPKINSTEM);
    idMap.put("block:melon_stem", Block.MELONSTEM);
    idMap.put("block:vine", Block.VINES);
    idMap.put("block:fence_gate", Block.FENCEGATE);
    idMap.put("block:brick_stairs", Block.BRICKSTAIRS);
    idMap.put("block:stone_brick_stairs", Block.STONEBRICKSTAIRS);
    idMap.put("block:mycelium", Block.MYCELIUM);
    idMap.put("block:waterlily", Block.LILY_PAD);

    idMap.put("block:nether_brick", Block.NETHERBRICK);
    idMap.put("block:nether_brick_fence", Block.NETHERBRICKFENCE);
    idMap.put("block:nether_brick_stairs", Block.NETHERBRICKSTAIRS);
    idMap.put("block:nether_wart", Block.NETHERWART);
    idMap.put("block:enchanting_table", Block.ENCHNATMENTTABLE);
    idMap.put("block:brewing_stand", Block.BREWINGSTAND);
    idMap.put("block:cauldron", Block.CAULDRON);
    idMap.put("block:end_portal", Block.ENDPORTAL);
    idMap.put("block:end_portal_frame", Block.ENDPORTALFRAME);
    idMap.put("block:end_stone", Block.ENDSTONE);
    idMap.put("block:dragon_egg", Block.DRAGONEGG);
    idMap.put("block:redstone_lamp", Block.REDSTONELAMPOFF);
    idMap.put("block:lit_redstone_lamp", Block.REDSTONELAMPON);
    idMap.put("block:double_wooden_slab", Block.DOUBLEWOODENSLAB);
    idMap.put("block:wooden_slab", Block.SINGLEWOODENSLAB);
    idMap.put("block:cocoa", Block.COCOAPLANT);

    idMap.put("block:sandstone_stairs", Block.SANDSTONESTAIRS);
    idMap.put("block:emerald_ore", Block.EMERALDORE);
    idMap.put("block:ender_chest", Block.ENDERCHEST);
    idMap.put("block:tripwire_hook", Block.TRIPWIREHOOK);
    idMap.put("block:tripwire", Block.TRIPWIRE);
    idMap.put("block:emerald_block", Block.EMERALDBLOCK);
    idMap.put("block:spruce_stairs", Block.SPRUCEWOODSTAIRS);
    idMap.put("block:birch_stairs", Block.BIRCHWOODSTAIRS);
    idMap.put("block:jungle_stairs", Block.JUNGLEWOODSTAIRS);
    idMap.put("block:command_block", Block.COMMAND_BLOCK);
    idMap.put("block:beacon", Block.BEACON);
    idMap.put("block:cobblestone_wall", Block.STONEWALL);
    idMap.put("block:flower_pot", Block.FLOWERPOT);
    idMap.put("block:carrots", Block.CARROTS);
    idMap.put("block:potatoes", Block.POTATOES);
    idMap.put("block:wooden_button", Block.WOODENBUTTON);

    idMap.put("block:skull", Block.HEAD);
    idMap.put("block:anvil", Block.ANVIL);
    idMap.put("block:trapped_chest", Block.TRAPPEDCHEST);
    idMap.put("block:light_weighted_pressure_plate", Block.WEIGHTEDPRESSUREPLATELIGHT);
    idMap.put("block:heavy_weighted_pressure_plate", Block.WEIGHTEDPRESSUREPLATEHEAVY);
    idMap.put("block:unpowered_comparator", Block.COMPARATOR);
    idMap.put("block:powered_comparator", Block.COMPARATOR_POWERED);
    idMap.put("block:daylight_detector", Block.DAYLIGHTSENSOR);
    idMap.put("block:redstone_block", Block.REDSTONEBLOCK);
    idMap.put("block:quartz_ore", Block.NETHERQUARTZORE);
    idMap.put("block:hopper", Block.HOPPER);
    idMap.put("block:quartz_block", Block.QUARTZ);
    idMap.put("block:quartz_stairs", Block.QUARTZSTAIRS);
    idMap.put("block:activator_rail", Block.ACTIVATORRAIL);
    idMap.put("block:dropper", Block.DROPPER);
    idMap.put("block:stained_hardened_clay", Block.STAINED_CLAY);

    idMap.put("block:stained_glass_pane", Block.STAINED_GLASSPANE);
    idMap.put("block:leaves2", Block.LEAVES2);
    idMap.put("block:log2", Block.WOOD2);
    idMap.put("block:acacia_stairs", Block.ACACIASTAIRS);
    idMap.put("block:dark_oak_stairs", Block.DARKOAKSTAIRS);
    idMap.put("block:slime", Block.SLIMEBLOCK);
    idMap.put("block:barrier", Block.BARRIER);
    idMap.put("block:iron_trapdoor", Block.IRON_TRAPDOOR);
    idMap.put("block:prismarine", Block.PRISMARINE);
    idMap.put("block:sea_lantern", Block.SEALANTERN);
    idMap.put("block:hay_block", Block.HAY_BLOCK);
    idMap.put("block:carpet", Block.CARPET);
    idMap.put("block:hardened_clay", Block.HARDENED_CLAY);
    idMap.put("block:coal_block", Block.COAL_BLOCK);
    idMap.put("block:packed_ice", Block.PACKED_ICE);
    idMap.put("block:double_plant", Block.LARGE_FLOWER);

    idMap.put("block:standing_banner", Block.STANDING_BANNER);
    idMap.put("block:wall_banner", Block.WALL_BANNER);
    idMap.put("block:daylight_detector_inverted", Block.INVERTED_DAYLIGHTSENSOR);
    idMap.put("block:red_standstone", Block.REDSANDSTONE);
    idMap.put("block:red_standstone_stairs", Block.REDSANDSTONESTAIRS);
    idMap.put("block:double_stone_slab2", Block.DOUBLESLAB2);
    idMap.put("block:stone_slab2", Block.SLAB2);
    idMap.put("block:spruce_fence_gate", Block.SPRUCEFENCEGATE);
    idMap.put("block:birch_fence_gate", Block.BIRCHFENCEGATE);
    idMap.put("block:jungle_fence_gate", Block.JUNGLEFENCEGATE);
    idMap.put("block:dark_oak_fence_gate", Block.DARKOAKFENCEGATE);
    idMap.put("block:acacia_fence_gate", Block.ACACIAFENCEGATE);
    idMap.put("block:spruce_fence", Block.SPRUCEFENCE);
    idMap.put("block:birch_fence", Block.BIRCHFENCE);
    idMap.put("block:jungle_fence", Block.JUNGLEFENCE);
    idMap.put("block:dark_oak_fence", Block.DARKOAKFENCE);

    idMap.put("block:acacia_fence", Block.ACACIAFENCE);
    idMap.put("block:spruce_door", Block.SPRUCEDOOR);
    idMap.put("block:birch_door", Block.BIRCHDOOR);
    idMap.put("block:jungle_door", Block.JUNGLEDOOR);
    idMap.put("block:acacia_door", Block.ACACIADOOR);
    idMap.put("block:dark_oak_door", Block.DARKOAKDOOR);
    idMap.put("block:end_rod", Block.ENDROD);
    idMap.put("block:chorus_plant", Block.CHORUSPLANT);
    idMap.put("block:chorus_flower", Block.CHORUSFLOWER);
    idMap.put("block:purpur_block", Block.PURPURBLOCK);
    idMap.put("block:purpur_pillar", Block.PURPURPILLAR);
    idMap.put("block:purpur_stairs", Block.PURPURSTAIRS);
    idMap.put("block:purpur_double_slab", Block.PURPURDOUBLESLAB);
    idMap.put("block:purpur_slab", Block.PURPURSLAB);
    idMap.put("block:end_bricks", Block.ENDBRICKS);
    idMap.put("block:beetroots", Block.BEETROOTS);

    idMap.put("block:grass_path", Block.GRASSPATH);
    idMap.put("block:end_gateway", Block.END_GATEWAY);
    idMap.put("block:repeating_command_block", Block.REPEATING_COMMAND_BLOCK);
    idMap.put("block:chain_command_block", Block.CHAIN_COMMAND_BLOCK);
    idMap.put("block:frosted_ice", Block.FROSTEDICE);
    idMap.put("block:magma", Block.MAGMA);
    idMap.put("block:nether_wart_block", Block.NETHER_WART_BLOCK);
    idMap.put("block:red_nether_brick", Block.RED_NETHER_BRICK);
    idMap.put("block:bone_block", Block.BONE);
    idMap.put("block:observer", Block.OBSERVER);
    idMap.put("block:white_shuler_box", Block.SHULKERBOX_WHITE);
    idMap.put("block:orange_shuler_box", Block.SHULKERBOX_ORANGE);
    idMap.put("block:magenta_shuler_box", Block.SHULKERBOX_MAGENTA);
    idMap.put("block:ligth_blue_shuler_box", Block.SHULKERBOX_LIGHTBLUE);
    idMap.put("block:yellow_shuler_box", Block.SHULKERBOX_YELLOW);

    idMap.put("block:lime_shuler_box", Block.SHULKERBOX_LIME);
    idMap.put("block:pink_shuler_box", Block.SHULKERBOX_PINK);
    idMap.put("block:gray_shuler_box", Block.SHULKERBOX_GRAY);
    idMap.put("block:light_gray_shuler_box", Block.SHULKERBOX_SILVER);
    idMap.put("block:cyan_shuler_box", Block.SHULKERBOX_CYAN);
    idMap.put("block:purple_shuler_box", Block.SHULKERBOX_PURPLE);
    idMap.put("block:blue_shuler_box", Block.SHULKERBOX_BLUE);
    idMap.put("block:brown_shuler_box", Block.SHULKERBOX_BROWN);
    idMap.put("block:green_shuler_box", Block.SHULKERBOX_GREEN);
    idMap.put("block:red_shuler_box", Block.SHULKERBOX_RED);
    idMap.put("block:black_shuler_box", Block.SHULKERBOX_BLACK);
    idMap.put("block:white_glazed_terracotta", Block.WHITE_TERRACOTTA);
    idMap.put("block:orange_glazed_terracotta", Block.ORANGE_TERRACOTTA);
    idMap.put("block:magenta_glazed_terracotta", Block.MAGENTA_TERRACOTTA);
    idMap.put("block:light_blue_glazed_terracotta", Block.LIGHT_BLUE_TERRACOTTA);
    idMap.put("block:yellow_glazed_terracotta", Block.YELLOW_TERRACOTTA);

    idMap.put("block:lime_glazed_terracotta", Block.LIME_TERRACOTTA);
    idMap.put("block:pink_glazed_terracotta", Block.PINK_TERRACOTTA);
    idMap.put("block:gray_glazed_terracotta", Block.GRAY_TERRACOTTA);
    idMap.put("block:light_gray_glazed_terracotta", Block.SILVER_TERRACOTTA);
    idMap.put("block:cyan_glazed_terracotta", Block.CYAN_TERRACOTTA);
    idMap.put("block:purple_glazed_terracotta", Block.PURPLE_TERRACOTTA);
    idMap.put("block:blue_glazed_terracotta", Block.BLUE_TERRACOTTA);
    idMap.put("block:brown_glazed_terracotta", Block.BROWN_TERRACOTTA);
    idMap.put("block:green_glazed_terracotta", Block.GREEN_TERRACOTTA);
    idMap.put("block:red_glazed_terracotta", Block.RED_TERRACOTTA);
    idMap.put("block:black_glazed_terracotta", Block.BLACK_TERRACOTTA);
    idMap.put("block:concrete", Block.CONCRETE);
    idMap.put("block:concrete_powder", Block.CONCRETE_POWDER);
    idMap.put("block:structure_block", Block.STRUCTURE_BLOCK);
  }

  /** Minecraft block ID. */
  public final int id;

  /** The block types that can connect to redstone wire. */
  private static final Set<Block> redstoneConnectors = new HashSet<>();

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
    redstoneConnectors.add(COMPARATOR);
    redstoneConnectors.add(COMPARATOR_POWERED);
    redstoneConnectors.add(DAYLIGHTSENSOR);
  }

  /**
   * @param id the Minecraft block ID
   * @param name the Minecraft name for this block
   * @param texture default texture
   */
  public Block(int id, String name, Texture texture) {
    super(name, texture);
    this.id = id;
  }

  /**
   * Updates the material properties for all blocks to the default values.
   *
   * <p>This must be called at least once before loading a scene
   * because block visibility affects octree construction.
   */
  public static void loadDefaultMaterialProperties() {
    for (Block block : blocks) {
      block.isOpaque = false;
      block.isSolid = true;
      block.localIntersect = false;
      block.isInvisible = false;
      block.emittance = 0;
      block.specular = 0;
      block.ior = 1.000293f;
    }

    AIR.isOpaque = false;
    AIR.isInvisible = true;
    STONE.isOpaque = true;
    STONE.localIntersect = true;
    GRASS.isOpaque = true;
    GRASS.localIntersect = true;
    DIRT.isOpaque = true;
    DIRT.localIntersect = true;
    COBBLESTONE.isOpaque = true;
    WOODENPLANKS.isOpaque = true;
    WOODENPLANKS.localIntersect = true;
    SAPLING.isOpaque = false;
    SAPLING.localIntersect = true;
    WATER.isOpaque = false;
    WATER.localIntersect = true;
    WATER.specular = 0.12f;
    WATER.ior = 1.333f;
    STATIONARYWATER.isOpaque = false;
    STATIONARYWATER.localIntersect = true;
    STATIONARYWATER.specular = 0.12f;
    STATIONARYWATER.ior = 1.333f;
    LAVA.isOpaque = false;
    LAVA.localIntersect = true;
    LAVA.emittance = 1.0f;
    STATIONARYLAVA.isOpaque = false;
    STATIONARYLAVA.localIntersect = true;
    STATIONARYLAVA.emittance = 1.0f;
    SAND.isOpaque = true;
    SAND.localIntersect = true;
    GRAVEL.isOpaque = true;
    GOLDORE.isOpaque = true;
    IRONORE.isOpaque = true;
    COALORE.isOpaque = true;
    WOOD.isOpaque = true;
    WOOD.localIntersect = true;
    LEAVES.localIntersect = true;
    SPONGE.isOpaque = true;
    SPONGE.localIntersect = true;
    GLASS.ior = 1.52f;
    LAPIS_ORE.isOpaque = true;
    LAPIS_BLOCK.isOpaque = true;
    DISPENSER.isOpaque = true;
    DISPENSER.localIntersect = true;
    SANDSTONE.isOpaque = true;
    SANDSTONE.localIntersect = true;
    NOTEBLOCK.isOpaque = true;
    BED.isOpaque = false;
    BED.localIntersect = true;
    POWEREDRAIL.isOpaque = false;
    POWEREDRAIL.localIntersect = true;
    DETECTORRAIL.isOpaque = false;
    DETECTORRAIL.localIntersect = true;
    STICKYPISTON.isOpaque = false;
    STICKYPISTON.localIntersect = true;
    COBWEB.isOpaque = false;
    COBWEB.localIntersect = true;
    TALLGRASS.isOpaque = false;
    TALLGRASS.localIntersect = true;
    DEADBUSH.isOpaque = false;
    DEADBUSH.localIntersect = true;
    PISTON.isOpaque = false;
    PISTON.localIntersect = true;
    PISTON_HEAD.isOpaque = false;
    PISTON_HEAD.localIntersect = true;
    WOOL.isOpaque = true;
    PISTON_EXTENSION.isOpaque = false;
    PISTON_EXTENSION.isInvisible = true;
    DANDELION.isOpaque = false;
    DANDELION.localIntersect = true;
    FLOWER.isOpaque = false;
    FLOWER.localIntersect = true;
    BROWNMUSHROOM.isOpaque = false;
    BROWNMUSHROOM.localIntersect = true;
    REDMUSHROOM.isOpaque = false;
    REDMUSHROOM.localIntersect = true;
    GOLDBLOCK.isOpaque = true;
    GOLDBLOCK.specular = 0.04f;
    IRONBLOCK.isOpaque = true;
    IRONBLOCK.specular = 0.04f;
    DOUBLESLAB.isOpaque = true;
    DOUBLESLAB.localIntersect = true;
    SLAB.isOpaque = false;
    SLAB.localIntersect = true;
    BRICKS.isOpaque = true;
    TNT.isOpaque = true;
    TNT.localIntersect = true;
    BOOKSHELF.isOpaque = true;
    BOOKSHELF.localIntersect = true;
    MOSSSTONE.isOpaque = true;
    OBSIDIAN.isOpaque = true;
    TORCH.isOpaque = false;
    TORCH.localIntersect = true;
    TORCH.emittance = 50.0f;
    FIRE.isOpaque = false;
    FIRE.localIntersect = true;
    FIRE.emittance = 1.0f;
    OAKWOODSTAIRS.isOpaque = false;
    OAKWOODSTAIRS.localIntersect = true;
    CHEST.isOpaque = false;
    CHEST.localIntersect = true;
    REDSTONEWIRE.isOpaque = false;
    REDSTONEWIRE.localIntersect = true;
    DIAMONDORE.isOpaque = true;
    DIAMONDBLOCK.isOpaque = true;
    DIAMONDBLOCK.specular = 0.04f;
    WORKBENCH.isOpaque = true;
    WORKBENCH.localIntersect = true;
    CROPS.isOpaque = false;
    CROPS.localIntersect = true;
    SOIL.isOpaque = true;
    SOIL.localIntersect = true;
    FURNACEUNLIT.isOpaque = true;
    FURNACEUNLIT.localIntersect = true;
    FURNACELIT.isOpaque = true;
    FURNACELIT.localIntersect = true;
    // SIGNPOST is rendered as an entity rather than as a voxel.
    SIGNPOST.isOpaque = false;
    SIGNPOST.isInvisible = true;
    WOODENDOOR.isOpaque = false;
    WOODENDOOR.localIntersect = true;
    LADDER.isOpaque = false;
    LADDER.localIntersect = true;
    MINECARTTRACKS.isOpaque = false;
    MINECARTTRACKS.localIntersect = true;
    STONESTAIRS.isOpaque = false;
    STONESTAIRS.localIntersect = true;
    // WALLSIGN is rendered as an entity rather than as a voxel.
    WALLSIGN.isOpaque = false;
    WALLSIGN.isInvisible = true;
    LEVER.isOpaque = false;
    LEVER.localIntersect = true;
    STONEPRESSUREPLATE.isOpaque = false;
    STONEPRESSUREPLATE.localIntersect = true;
    IRONDOOR.isOpaque = false;
    IRONDOOR.localIntersect = true;
    WOODENPRESSUREPLATE.isOpaque = false;
    WOODENPRESSUREPLATE.localIntersect = true;
    REDSTONEORE.isOpaque = true;
    GLOWINGREDSTONEORE.isOpaque = true;
    REDSTONETORCHOFF.isOpaque = false;
    REDSTONETORCHOFF.localIntersect = true;
    REDSTONETORCHON.isOpaque = false;
    REDSTONETORCHON.localIntersect = true;
    REDSTONETORCHON.emittance = 1.0f;
    STONEBUTTON.isOpaque = false;
    STONEBUTTON.localIntersect = true;
    SNOW.isOpaque = false;
    SNOW.localIntersect = true;
    ICE.ior = 1.31f;
    SNOWBLOCK.isOpaque = true;
    CACTUS.isOpaque = false;
    CACTUS.localIntersect = true;
    CLAY.isOpaque = true;
    SUGARCANE.isOpaque = false;
    SUGARCANE.localIntersect = true;
    JUKEBOX.isOpaque = true;
    JUKEBOX.localIntersect = true;
    FENCE.isOpaque = false;
    FENCE.localIntersect = true;
    PUMPKIN.isOpaque = true;
    PUMPKIN.localIntersect = true;
    NETHERRACK.isOpaque = true;
    SOULSAND.isOpaque = true;
    GLOWSTONE.isOpaque = true;
    GLOWSTONE.emittance = 1.0f;
    PORTAL.isOpaque = false;
    PORTAL.emittance = 0.4f;
    JACKOLANTERN.isOpaque = true;
    JACKOLANTERN.localIntersect = true;
    JACKOLANTERN.emittance = 1.0f;
    CAKE.isOpaque = false;
    CAKE.localIntersect = true;
    REDSTONEREPEATEROFF.isOpaque = false;
    REDSTONEREPEATEROFF.localIntersect = true;
    REDSTONEREPEATERON.isOpaque = false;
    REDSTONEREPEATERON.localIntersect = true;
    STAINED_GLASS.ior = 1.52f;
    TRAPDOOR.isOpaque = false;
    TRAPDOOR.localIntersect = true;
    HIDDENSILVERFISH.isOpaque = true;
    HIDDENSILVERFISH.localIntersect = true;
    STONEBRICKS.isOpaque = true;
    STONEBRICKS.localIntersect = true;
    HUGEBROWNMUSHROOM.isOpaque = true;
    HUGEBROWNMUSHROOM.localIntersect = true;
    HUGEREDMUSHROOM.isOpaque = true;
    HUGEREDMUSHROOM.localIntersect = true;
    IRONBARS.isOpaque = false;
    IRONBARS.localIntersect = true;
    GLASSPANE.isOpaque = false;
    GLASSPANE.localIntersect = true;
    GLASSPANE.ior = 1.52f;
    MELON.isOpaque = true;
    MELON.localIntersect = true;
    PUMPKINSTEM.isOpaque = false;
    PUMPKINSTEM.localIntersect = true;
    MELONSTEM.isOpaque = false;
    MELONSTEM.localIntersect = true;
    VINES.isOpaque = false;
    VINES.localIntersect = true;
    FENCEGATE.isOpaque = false;
    FENCEGATE.localIntersect = true;
    BRICKSTAIRS.isOpaque = false;
    BRICKSTAIRS.localIntersect = true;
    STONEBRICKSTAIRS.isOpaque = false;
    STONEBRICKSTAIRS.localIntersect = true;
    MYCELIUM.isOpaque = true;
    MYCELIUM.localIntersect = true;
    LILY_PAD.isOpaque = false;
    LILY_PAD.localIntersect = true;
    LILY_PAD.isInvisible = true;
    NETHERBRICK.isOpaque = true;
    NETHERBRICKFENCE.isOpaque = false;
    NETHERBRICKFENCE.localIntersect = true;
    NETHERBRICKSTAIRS.isOpaque = false;
    NETHERBRICKSTAIRS.localIntersect = true;
    NETHERWART.isOpaque = false;
    NETHERWART.localIntersect = true;
    ENCHNATMENTTABLE.isOpaque = false;
    ENCHNATMENTTABLE.localIntersect = true;
    BREWINGSTAND.isOpaque = false;
    BREWINGSTAND.localIntersect = true;
    CAULDRON.isOpaque = false;
    CAULDRON.localIntersect = true;
    ENDPORTAL.isOpaque = false;
    ENDPORTAL.localIntersect = true;
    ENDPORTALFRAME.isOpaque = false;
    ENDPORTALFRAME.localIntersect = true;
    ENDSTONE.isOpaque = true;
    ENDSTONE.localIntersect = true;
    DRAGONEGG.isOpaque = false;
    DRAGONEGG.localIntersect = true;
    REDSTONELAMPOFF.isOpaque = true;
    REDSTONELAMPON.isOpaque = true;
    REDSTONELAMPON.emittance = 1.0f;
    DOUBLEWOODENSLAB.isOpaque = true;
    DOUBLEWOODENSLAB.localIntersect = true;
    SINGLEWOODENSLAB.isOpaque = false;
    SINGLEWOODENSLAB.localIntersect = true;
    COCOAPLANT.isOpaque = false;
    COCOAPLANT.localIntersect = true;
    SANDSTONESTAIRS.isOpaque = false;
    SANDSTONESTAIRS.localIntersect = true;
    EMERALDORE.isOpaque = true;
    ENDERCHEST.isOpaque = false;
    ENDERCHEST.localIntersect = true;
    TRIPWIREHOOK.isOpaque = false;
    TRIPWIREHOOK.localIntersect = true;
    TRIPWIRE.isOpaque = false;
    TRIPWIRE.localIntersect = true;
    EMERALDBLOCK.isOpaque = true;
    EMERALDBLOCK.specular = 0.04f;
    SPRUCEWOODSTAIRS.isOpaque = false;
    SPRUCEWOODSTAIRS.localIntersect = true;
    BIRCHWOODSTAIRS.isOpaque = false;
    BIRCHWOODSTAIRS.localIntersect = true;
    JUNGLEWOODSTAIRS.isOpaque = false;
    JUNGLEWOODSTAIRS.localIntersect = true;
    COMMAND_BLOCK.isOpaque = true;
    COMMAND_BLOCK.localIntersect = true;
    BEACON.localIntersect = true;
    BEACON.emittance = 1.0f;
    BEACON.ior = 1.52f;
    STONEWALL.isOpaque = false;
    STONEWALL.localIntersect = true;
    FLOWERPOT.isOpaque = false;
    FLOWERPOT.localIntersect = true;
    CARROTS.isOpaque = false;
    CARROTS.localIntersect = true;
    POTATOES.isOpaque = false;
    POTATOES.localIntersect = true;
    WOODENBUTTON.isOpaque = false;
    WOODENBUTTON.localIntersect = true;
    HEAD.isOpaque = false;
    HEAD.isInvisible = true;
    ANVIL.isOpaque = false;
    ANVIL.localIntersect = true;
    TRAPPEDCHEST.isOpaque = false;
    TRAPPEDCHEST.localIntersect = true;
    WEIGHTEDPRESSUREPLATELIGHT.isOpaque = false;
    WEIGHTEDPRESSUREPLATELIGHT.localIntersect = true;
    WEIGHTEDPRESSUREPLATEHEAVY.isOpaque = false;
    WEIGHTEDPRESSUREPLATEHEAVY.localIntersect = true;
    COMPARATOR.isOpaque = false;
    COMPARATOR.localIntersect = true;
    COMPARATOR_POWERED.isOpaque = false;
    COMPARATOR_POWERED.localIntersect = true;
    DAYLIGHTSENSOR.isOpaque = false;
    DAYLIGHTSENSOR.localIntersect = true;
    REDSTONEBLOCK.isOpaque = true;
    NETHERQUARTZORE.isOpaque = true;
    HOPPER.isOpaque = false;
    HOPPER.localIntersect = true;
    QUARTZ.isOpaque = true;
    QUARTZ.localIntersect = true;
    QUARTZSTAIRS.isOpaque = false;
    QUARTZSTAIRS.localIntersect = true;
    ACTIVATORRAIL.isOpaque = false;
    ACTIVATORRAIL.localIntersect = true;
    DROPPER.isOpaque = true;
    DROPPER.localIntersect = true;
    STAINED_CLAY.isOpaque = true;
    STAINED_GLASSPANE.isOpaque = false;
    STAINED_GLASSPANE.localIntersect = true;
    STAINED_GLASSPANE.ior = 1.52f;
    LEAVES2.localIntersect = true;
    WOOD2.isOpaque = true;
    WOOD2.localIntersect = true;
    ACACIASTAIRS.isOpaque = false;
    ACACIASTAIRS.localIntersect = true;
    DARKOAKSTAIRS.isOpaque = false;
    DARKOAKSTAIRS.localIntersect = true;
    BARRIER.isOpaque = false;
    BARRIER.isInvisible = true;
    IRON_TRAPDOOR.isOpaque = false;
    IRON_TRAPDOOR.localIntersect = true;
    PRISMARINE.isOpaque = true;
    PRISMARINE.localIntersect = true;
    SEALANTERN.isOpaque = true;
    SEALANTERN.emittance = 0.5f;
    HAY_BLOCK.isOpaque = true;
    HAY_BLOCK.localIntersect = true;
    CARPET.isOpaque = false;
    CARPET.localIntersect = true;
    HARDENED_CLAY.isOpaque = true;
    COAL_BLOCK.isOpaque = true;
    PACKED_ICE.isOpaque = true;
    LARGE_FLOWER.isOpaque = false;
    LARGE_FLOWER.localIntersect = true;
    STANDING_BANNER.isOpaque = false;
    STANDING_BANNER.isInvisible = UNKNOWN_INVISIBLE; // TODO: render this.
    WALL_BANNER.isOpaque = false;
    WALL_BANNER.isInvisible = UNKNOWN_INVISIBLE; // TODO: render this.
    INVERTED_DAYLIGHTSENSOR.isOpaque = false;
    INVERTED_DAYLIGHTSENSOR.localIntersect = true;
    REDSANDSTONE.isOpaque = true;
    REDSANDSTONE.localIntersect = true;
    REDSANDSTONESTAIRS.isOpaque = false;
    REDSANDSTONESTAIRS.localIntersect = true;
    DOUBLESLAB2.isOpaque = true;
    DOUBLESLAB2.localIntersect = true;
    SLAB2.isOpaque = false;
    SLAB2.localIntersect = true;
    SPRUCEFENCEGATE.isOpaque = false;
    SPRUCEFENCEGATE.localIntersect = true;
    BIRCHFENCEGATE.isOpaque = false;
    BIRCHFENCEGATE.localIntersect = true;
    JUNGLEFENCEGATE.isOpaque = false;
    JUNGLEFENCEGATE.localIntersect = true;
    DARKOAKFENCEGATE.isOpaque = false;
    DARKOAKFENCEGATE.localIntersect = true;
    ACACIAFENCEGATE.isOpaque = false;
    ACACIAFENCEGATE.localIntersect = true;
    SPRUCEFENCE.isOpaque = false;
    SPRUCEFENCE.localIntersect = true;
    BIRCHFENCE.isOpaque = false;
    BIRCHFENCE.localIntersect = true;
    JUNGLEFENCE.isOpaque = false;
    JUNGLEFENCE.localIntersect = true;
    DARKOAKFENCE.localIntersect = true;
    ACACIAFENCE.isOpaque = false;
    ACACIAFENCE.localIntersect = true;
    SPRUCEDOOR.isOpaque = false;
    SPRUCEDOOR.localIntersect = true;
    BIRCHDOOR.isOpaque = false;
    BIRCHDOOR.localIntersect = true;
    JUNGLEDOOR.isOpaque = false;
    JUNGLEDOOR.localIntersect = true;
    ACACIADOOR.isOpaque = false;
    ACACIADOOR.localIntersect = true;
    DARKOAKDOOR.isOpaque = false;
    DARKOAKDOOR.localIntersect = true;
    ENDROD.isOpaque = false;
    ENDROD.localIntersect = true;
    ENDROD.emittance = 1.0f;
    CHORUSPLANT.isOpaque = false;
    CHORUSPLANT.localIntersect = true;
    CHORUSFLOWER.isOpaque = false;
    CHORUSFLOWER.localIntersect = true;
    PURPURBLOCK.isOpaque = true;
    PURPURPILLAR.isOpaque = true;
    PURPURPILLAR.localIntersect = true;
    PURPURSTAIRS.isOpaque = false;
    PURPURSTAIRS.localIntersect = true;
    PURPURDOUBLESLAB.isOpaque = true;
    PURPURSLAB.isOpaque = false;
    PURPURSLAB.localIntersect = true;
    ENDBRICKS.isOpaque = true;
    BEETROOTS.isOpaque = false;
    BEETROOTS.isInvisible = UNKNOWN_INVISIBLE; // TODO: render this.
    GRASSPATH.isOpaque = false;
    GRASSPATH.localIntersect = true;
    END_GATEWAY.isOpaque = false;
    END_GATEWAY.isInvisible = UNKNOWN_INVISIBLE; // TODO: render this.
    REPEATING_COMMAND_BLOCK.isOpaque = true;
    REPEATING_COMMAND_BLOCK.localIntersect = true;
    CHAIN_COMMAND_BLOCK.isOpaque = true;
    CHAIN_COMMAND_BLOCK.localIntersect = true;
    FROSTEDICE.ior = 1.31f;
    MAGMA.isOpaque = true;
    MAGMA.emittance = 0.6f;  // Not as bright as lava (1.0).
    NETHER_WART_BLOCK.isOpaque = true;
    RED_NETHER_BRICK.isOpaque = true;
    BONE.isOpaque = true;
    BONE.localIntersect = true;
    OBSERVER.isOpaque = true;
    OBSERVER.localIntersect = true;
    SHULKERBOX_WHITE.isOpaque = true;
    SHULKERBOX_WHITE.localIntersect = true;
    SHULKERBOX_ORANGE.isOpaque = true;
    SHULKERBOX_ORANGE.localIntersect = true;
    SHULKERBOX_MAGENTA.isOpaque = true;
    SHULKERBOX_MAGENTA.localIntersect = true;
    SHULKERBOX_LIGHTBLUE.isOpaque = true;
    SHULKERBOX_LIGHTBLUE.localIntersect = true;
    SHULKERBOX_YELLOW.isOpaque = true;
    SHULKERBOX_YELLOW.localIntersect = true;
    SHULKERBOX_LIME.isOpaque = true;
    SHULKERBOX_LIME.localIntersect = true;
    SHULKERBOX_PINK.isOpaque = true;
    SHULKERBOX_PINK.localIntersect = true;
    SHULKERBOX_GRAY.isOpaque = true;
    SHULKERBOX_GRAY.localIntersect = true;
    SHULKERBOX_SILVER.isOpaque = true;
    SHULKERBOX_SILVER.localIntersect = true;
    SHULKERBOX_CYAN.isOpaque = true;
    SHULKERBOX_CYAN.localIntersect = true;
    SHULKERBOX_PURPLE.isOpaque = true;
    SHULKERBOX_PURPLE.localIntersect = true;
    SHULKERBOX_BLUE.isOpaque = true;
    SHULKERBOX_BLUE.localIntersect = true;
    SHULKERBOX_BROWN.isOpaque = true;
    SHULKERBOX_BROWN.localIntersect = true;
    SHULKERBOX_GREEN.isOpaque = true;
    SHULKERBOX_GREEN.localIntersect = true;
    SHULKERBOX_RED.isOpaque = true;
    SHULKERBOX_RED.localIntersect = true;
    SHULKERBOX_BLACK.isOpaque = true;
    SHULKERBOX_BLACK.localIntersect = true;
    WHITE_TERRACOTTA.isOpaque = true;
    WHITE_TERRACOTTA.localIntersect = true;
    ORANGE_TERRACOTTA.isOpaque = true;
    ORANGE_TERRACOTTA.localIntersect = true;
    MAGENTA_TERRACOTTA.isOpaque = true;
    MAGENTA_TERRACOTTA.localIntersect = true;
    LIGHT_BLUE_TERRACOTTA.isOpaque = true;
    LIGHT_BLUE_TERRACOTTA.localIntersect = true;
    YELLOW_TERRACOTTA.isOpaque = true;
    YELLOW_TERRACOTTA.localIntersect = true;
    LIME_TERRACOTTA.isOpaque = true;
    LIME_TERRACOTTA.localIntersect = true;
    PINK_TERRACOTTA.isOpaque = true;
    PINK_TERRACOTTA.localIntersect = true;
    GRAY_TERRACOTTA.isOpaque = true;
    GRAY_TERRACOTTA.localIntersect = true;
    SILVER_TERRACOTTA.isOpaque = true;
    SILVER_TERRACOTTA.localIntersect = true;
    CYAN_TERRACOTTA.isOpaque = true;
    CYAN_TERRACOTTA.localIntersect = true;
    PURPLE_TERRACOTTA.isOpaque = true;
    PURPLE_TERRACOTTA.localIntersect = true;
    BLUE_TERRACOTTA.isOpaque = true;
    BLUE_TERRACOTTA.localIntersect = true;
    BROWN_TERRACOTTA.isOpaque = true;
    BROWN_TERRACOTTA.localIntersect = true;
    GREEN_TERRACOTTA.isOpaque = true;
    GREEN_TERRACOTTA.localIntersect = true;
    RED_TERRACOTTA.isOpaque = true;
    RED_TERRACOTTA.localIntersect = true;
    BLACK_TERRACOTTA.isOpaque = true;
    BLACK_TERRACOTTA.localIntersect = true;
    CONCRETE.isOpaque = true;
    CONCRETE_POWDER.isOpaque = true;
    UNKNOWN0xFD.isInvisible = UNKNOWN_INVISIBLE;
    UNKNOWN0xFE.isInvisible = UNKNOWN_INVISIBLE;
    STRUCTURE_BLOCK.isOpaque = false;
    STRUCTURE_BLOCK.isInvisible = UNKNOWN_INVISIBLE; // TODO: render this.
  }

  /** The in-game name of this block. */
  public String getBlockName() {
    return name;
  }

  @Override public String toString() {
    return getBlockName();
  }

  public boolean intersect(Ray ray, Scene scene) {
    return TexturedBlockModel.intersect(ray, texture);
  }

  public Texture getIcon() {
    return texture;
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

  public boolean isChorusPlant() {
    return this == CHORUSPLANT || this == CHORUSFLOWER;
  }

  public boolean isRedstoneWireConnector() {
    return redstoneConnectors.contains(this);
  }

  public boolean isCave() {
    return !isSolid && !this.isWater();
  }

  public boolean isStair() {
    return this == OAKWOODSTAIRS || this == STONESTAIRS || this == BRICKSTAIRS
        || this == STONEBRICKSTAIRS || this == NETHERBRICKSTAIRS || this == SANDSTONESTAIRS
        || this == SPRUCEWOODSTAIRS || this == BIRCHWOODSTAIRS || this == JUNGLEWOODSTAIRS
        || this == QUARTZSTAIRS || this == ACACIASTAIRS || this == DARKOAKSTAIRS
        || this == REDSANDSTONESTAIRS;
  }

  public boolean isGroundBlock() {
    return id != Block.AIR_ID &&
        id != Block.LEAVES_ID &&
        id != Block.LEAVES2_ID &&
        id != Block.WOOD_ID &&
        id != Block.WOOD2_ID;
  }

  public static Block get(int id) {
    return blocks[0xFF & id];
  }

  /**
   * Changes the internal block representation for a given block ID.
   *
   * <p>The Block class is responsible for rendering Minecraft blocks.
   * Changing the block instance for a given block ID can be used to
   * change the rendering of that block, or to change rendering
   * parameters such as emittance and texture.
   *
   * @param id ID of the block to change.
   * @param newBlock new block representation.
   */
  public static void set(int id, Block newBlock) {
    if (id < 0 || id > 0xFF) {
      throw new IllegalArgumentException("Block id out of range.");
    }
    if (newBlock == null) {
      throw new IllegalArgumentException("Block can not be null.");
    }
    blocks[id] = newBlock;
  }

  private static final String[] woolColor = {
      "white", "orange", "magenta", "light blue", "yellow", "lime", "pink", "gray", "light gray",
      "cyan", "purple", "blue", "brown", "green", "red", "black"
  };

  private static final String[] bits = {
      "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010",
      "1011", "1100", "1101", "1110", "1111"
  };

  /**
   * @param data block data
   * @return debug info for this block
   */
  public String description(int data) {
    return bits[data & 15];
  }

  public boolean isSameMaterial(Material other) {
    return other == this;
  }

  @Override public JsonValue toJson() {
    return new JsonString("block:" + id);
  }
}
