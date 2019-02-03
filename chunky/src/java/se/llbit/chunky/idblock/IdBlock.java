/* Copyright (c) 2010-2017 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.idblock;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.block.Block;
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
import se.llbit.chunky.world.Icon;
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.math.ColorUtil;
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
public class IdBlock extends Material {
  /** Controls if unknown blocks are rendered. Useful for debugging. */
  private static final boolean UNKNOWN_INVISIBLE = !PersistentSettings.drawUnknownBlocks();

  public static final int AIR_ID = 0x00;
  public static final IdBlock AIR = new IdBlock(AIR_ID, "minecraft:air", Texture.air);
  public static final int STONE_ID = 0x01;
  public static final IdBlock STONE = new IdBlock(STONE_ID, "minecraft:stone", Texture.stone) {
    final Texture[] texture = {
        Texture.stone, Texture.granite, Texture.smoothGranite, Texture.diorite,
        Texture.smoothDiorite, Texture.andesite, Texture.smoothAndesite,
    };
    final String[] stoneKind = {
        "stone", "granite", "smoothGranite", "diorite", "smoothDiorite", "andesite",
        "smoothAndesite"
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 7]);
    }

    @Override public String description(int data) {
      return stoneKind[data % 7];
    }
  };
  public static final int GRASS_ID = 0x02;
  public static final IdBlock GRASS = new IdBlock(GRASS_ID, "minecraft:grass", Texture.grassTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return GrassModel.intersect(ray, scene);
    }
  };
  public static final int DIRT_ID = 0x03;
  public static final IdBlock DIRT = new IdBlock(DIRT_ID, "minecraft:dirt", Texture.dirt) {
    final Texture[][] textures = {
        {
            Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt,
        },
        {
            Texture.coarseDirt, Texture.coarseDirt, Texture.coarseDirt, Texture.coarseDirt,
            Texture.coarseDirt, Texture.coarseDirt,
        },
        {
            Texture.podzolSide, Texture.podzolSide, Texture.podzolSide, Texture.podzolSide,
            Texture.podzolTop, Texture.podzolSide,
        },
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, textures[ray.getBlockData() % 3]);
    }

    final String[] kind = {"regular", "coarse", "podzol"};

    @Override public String description(int data) {
      return kind[data % 3];
    }
  };
  public static final int COBBLESTONE_ID = 0x04;
  public static final IdBlock
      COBBLESTONE = new IdBlock(COBBLESTONE_ID, "minecraft:cobblestone", Texture.cobblestone);
  public static final int WOODENPLANKS_ID = 0x05;
  public static final IdBlock
      WOODENPLANKS = new IdBlock(WOODENPLANKS_ID, "minecraft:planks", Texture.oakPlanks) {
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
  public static final IdBlock SAPLING = new IdBlock(SAPLING_ID, "minecraft:sapling", Texture.oakSapling) {
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
  public static final IdBlock BEDROCK = new IdBlock(BEDROCK_ID, "minecraft:bedrock", Texture.bedrock);
  public static final int WATER_ID = 0x08;
  public static final IdBlock WATER = new IdBlock(WATER_ID, "minecraft:flowing_water", Texture.water) {
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
  public static final IdBlock
      STATIONARYWATER = new IdBlock(STATIONARYWATER_ID, "minecraft:water", Texture.water) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return WaterModel.intersect(ray);
    }

    @Override public boolean isWater() {
      return true;
    }
  };
  public static final int LAVA_ID = 0x0A;
  public static final IdBlock LAVA = new IdBlock(LAVA_ID, "minecraft:flowing_lava", Texture.lava) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LavaModel.intersect(ray);
    }
  };
  public static final int STATIONARYLAVA_ID = 0x0B;
  public static final IdBlock
      STATIONARYLAVA = new IdBlock(STATIONARYLAVA_ID, "minecraft:lava", Texture.lava) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LavaModel.intersect(ray);
    }
  };
  public static final int SAND_ID = 0x0C;
  public static final IdBlock SAND = new IdBlock(SAND_ID, "minecraft:sand", Texture.sand) {
    final Texture[] texture = {Texture.sand, Texture.redSand};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, getTexture(ray.getBlockData()));
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 1];
    }
  };
  public static final int GRAVEL_ID = 0x0D;
  public static final IdBlock GRAVEL = new IdBlock(GRAVEL_ID, "minecraft:gravel", Texture.gravel);
  public static final int GOLDORE_ID = 0x0E;
  public static final IdBlock GOLDORE = new IdBlock(GOLDORE_ID, "minecraft:gold_ore", Texture.goldOre);
  public static final int IRONORE_ID = 0x0F;
  public static final IdBlock IRONORE = new IdBlock(IRONORE_ID, "minecraft:iron_ore", Texture.ironOre);
  public static final int COALORE_ID = 0x10;
  public static final IdBlock COALORE = new IdBlock(COALORE_ID, "minecraft:coal_ore", Texture.coalOre);
  public static final int WOOD_ID = 0x11;
  public static final IdBlock WOOD = new IdBlock(WOOD_ID, "minecraft:log2", Texture.oakWood) {
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
  public static final IdBlock LEAVES = new IdBlock(LEAVES_ID, "minecraft:leaves", Texture.oakLeaves) {
    private final float[] SPRUCE_COLOR = new float[4];
    private final float[] BIRCH_COLOR = new float[4];

    {
      // Spruce and birch colors are hard-coded.
      ColorUtil.getRGBAComponents(0x2b472b, SPRUCE_COLOR);
      ColorUtil.getRGBAComponents(0x3a4e25, BIRCH_COLOR);
    }

    final Texture[] texture = {
        Texture.oakLeaves, Texture.spruceLeaves, Texture.birchLeaves, Texture.jungleTreeLeaves
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      int data = ray.getBlockData();
      switch (data) {
        case 1:
          // Spruce leaf color is not based on biome.
          return LeafModel.intersect(ray, getTexture(data), SPRUCE_COLOR);
        case 2:
          // Birch leaf color is not based on biome.
          return LeafModel.intersect(ray, getTexture(data), BIRCH_COLOR);
        default:
          return LeafModel.intersect(ray, scene, getTexture(data));
      }
    }

    final String[] woodType = {"oak", "spruce", "birch", "jungle",};

    @Override public String description(int data) {
      return woodType[data & 3];
    }

    @Override public Texture getTexture(int blockData) {
      return texture[blockData & 3];
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isGlassPaneConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isIronBarsConnector(int data, int direction) {
      return false;
    }
  };
  public static final int SPONGE_ID = 0x13;
  public static final IdBlock SPONGE = new IdBlock(SPONGE_ID, "minecraft:sponge", Texture.sponge) {
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
  public static final IdBlock GLASS = new IdBlock(GLASS_ID, "minecraft:glass", Texture.glass) {
    @Override public boolean isSameMaterial(Material other) {
      return other == this || other == STAINED_GLASS;
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }
  };
  public static final int LAPIS_ORE_ID = 0x15;
  public static final IdBlock LAPIS_ORE =
      new IdBlock(LAPIS_ORE_ID, "minecraft:lapis_ore", Texture.lapisOre);
  public static final int LAPIS_BLOCK_ID = 0x16;
  public static final IdBlock LAPIS_BLOCK =
      new IdBlock(LAPIS_BLOCK_ID, "minecraft:lapis_block", Texture.lapisBlock);
  public static final int DISPENSER_ID = 0x17;
  public static final IdBlock DISPENSER = new IdBlock(DISPENSER_ID, "minecraft:dispenser", Texture.dispenserFront) {
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
  public static final IdBlock SANDSTONE = new IdBlock(SANDSTONE_ID, "minecraft:sandstone", Texture.sandstoneSide) {
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
  public static final IdBlock NOTEBLOCK = new IdBlock(NOTEBLOCK_ID, "minecraft:noteblock", Icon.noteBlock) {
    @Override public Texture getTexture(int blockData) {
      return Texture.jukeboxSide;
    }
  };
  public static final int BED_ID = 0x1A;
  public static final IdBlock BED = new IdBlock(BED_ID, "minecraft:bed", Icon.bed) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return BedModel.intersect(ray);
    }
  };
  public static final int POWEREDRAIL_ID = 0x1B;
  public static final IdBlock
      POWEREDRAIL = new IdBlock(POWEREDRAIL_ID, "minecraft:golden_rail", Texture.poweredRailOn) {
    final Texture[] texture = {Texture.poweredRailOff, Texture.poweredRailOn};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return RailModel
          .intersect(ray, texture[ray.getBlockData() >>> 3], (ray.getBlockData() & 7) % 6);
    }
  };
  public static final int DETECTORRAIL_ID = 0x1C;
  public static final IdBlock
      DETECTORRAIL = new IdBlock(DETECTORRAIL_ID, "minecraft:detector_rail", Texture.detectorRail) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RailModel.intersect(ray, Texture.detectorRail, (ray.getBlockData() & 7) % 6);
    }
  };
  public static final int STICKYPISTON_ID = 0x1D;
  public static final IdBlock
      STICKYPISTON = new IdBlock(STICKYPISTON_ID, "minecraft:sticky_piston", Texture.pistonTopSticky) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PistonModel.intersect(ray, 1);
    }
  };
  public static final int COBWEB_ID = 0x1E;
  public static final IdBlock COBWEB = new IdBlock(COBWEB_ID, "minecraft:web", Texture.cobweb) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.cobweb);
    }
  };
  public static final int TALLGRASS_ID = 0x1F;
  public static final IdBlock TALLGRASS = new IdBlock(TALLGRASS_ID, "minecraft:tallgrass", Texture.tallGrass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TallGrassModel.intersect(ray, scene);
    }
  };
  public static final int DEADBUSH_ID = 0x20;
  public static final IdBlock DEADBUSH = new IdBlock(DEADBUSH_ID, "minecraft:deadbush", Texture.deadBush) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.deadBush);
    }
  };
  public static final int PISTON_ID = 0x21;
  public static final IdBlock PISTON = new IdBlock(PISTON_ID, "minecraft:piston", Texture.pistonTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PistonModel.intersect(ray, 0);
    }
  };
  public static final int PISTON_HEAD_ID = 0x22;
  public static final IdBlock PISTON_HEAD = new IdBlock(PISTON_HEAD_ID,
      "minecraft:piston_head", Texture.pistonTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PistonExtensionModel.intersect(ray);
    }
  };
  public static final int WOOL_ID = 0x23;
  public static final IdBlock WOOL = new IdBlock(WOOL_ID, "minecraft:wool", Texture.lightGrayWool) {
    @Override public Texture getTexture(int blockData) {
      return Texture.wool[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final int PISTON_EXTENSION_ID = 0x24;
  public static final IdBlock PISTON_EXTENSION = new IdBlock(PISTON_EXTENSION_ID,
      "minecraft:piston_extension", Texture.unknown);
  public static final int DANDELION_ID = 0x25;
  public static final IdBlock
      DANDELION = new IdBlock(DANDELION_ID, "minecraft:yellow_flower", Texture.dandelion) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.dandelion);
    }
  };
  public static final int FLOWER_ID = 0x26;
  public static final IdBlock FLOWER = new IdBlock(FLOWER_ID, "minecraft:red_flower", Texture.poppy) {
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
  public static final IdBlock
      BROWNMUSHROOM = new IdBlock(BROWNMUSHROOM_ID, "minecraft:brown_mushroom", Texture.brownMushroom) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.brownMushroom);
    }
  };
  public static final int REDMUSHROOM_ID = 0x28;
  public static final IdBlock
      REDMUSHROOM = new IdBlock(REDMUSHROOM_ID, "minecraft:red_mushroom", Texture.redMushroom) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.redMushroom);
    }
  };
  public static final int GOLDBLOCK_ID = 0x29;
  public static final IdBlock GOLDBLOCK = new IdBlock(GOLDBLOCK_ID, "minecraft:gold_block", Texture.goldBlock);
  public static final int IRONBLOCK_ID = 0x2A;
  public static final IdBlock IRONBLOCK = new IdBlock(IRONBLOCK_ID, "minecraft:iron_block", Texture.ironBlock);
  public static final int DOUBLESLAB_ID = 0x2B;
  public static final IdBlock
      DOUBLESLAB = new IdBlock(DOUBLESLAB_ID, "minecraft:double_stone_slab", Texture.slabTop) {
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
  public static final IdBlock SLAB = new IdBlock(SLAB_ID, "minecraft:stone_slab", Texture.slabTop) {
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
  public static final IdBlock BRICKS = new IdBlock(BRICKS_ID, "minecraft:brick_block", Texture.brick);
  public static final int TNT_ID = 0x2E;
  public static final IdBlock TNT = new IdBlock(TNT_ID, "minecraft:tnt", Texture.tntSide) {
    final Texture[] texture =
        {Texture.tntSide, Texture.tntSide, Texture.tntSide, Texture.tntSide, Texture.tntTop,
            Texture.tntBottom,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int BOOKSHELF_ID = 0x2F;
  public static final IdBlock BOOKSHELF = new IdBlock(BOOKSHELF_ID, "minecraft:bookshelf", Texture.bookshelf) {
    final Texture[] texture =
        {Texture.bookshelf, Texture.bookshelf, Texture.bookshelf, Texture.bookshelf,
            Texture.oakPlanks, Texture.oakPlanks,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int MOSSSTONE_ID = 0x30;
  public static final IdBlock
      MOSSSTONE = new IdBlock(MOSSSTONE_ID, "minecraft:mossy_cobblestone", Texture.mossStone);
  public static final int OBSIDIAN_ID = 0x31;
  public static final IdBlock OBSIDIAN = new IdBlock(OBSIDIAN_ID, "minecraft:obsidian", Texture.obsidian);
  public static final int TORCH_ID = 0x32;
  public static final IdBlock TORCH = new IdBlock(TORCH_ID, "minecraft:torch", Texture.torch) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TorchModel.intersect(ray, Texture.torch);
    }

    final String[] direction = {"", "east", "west", "south", "north", "on floor"};

    @Override public String description(int data) {
      return direction[data % 6];
    }

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int FIRE_ID = 0x33;
  public static final IdBlock FIRE = new IdBlock(FIRE_ID, "minecraft:fire", Texture.fire) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FireModel.intersect(ray);
    }
  };
  public static final int MONSTERSPAWNER_ID = 0x34;
  public static final IdBlock
      MONSTERSPAWNER = new IdBlock(MONSTERSPAWNER_ID, "minecraft:mob_spawner", Texture.monsterSpawner);
  public static final int OAKWOODSTAIRS_ID = 0x35;
  public static final IdBlock
      OAKWOODSTAIRS = new Stairs(OAKWOODSTAIRS_ID, "minecraft:oak_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.oakPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.oakPlanks;
    }
  };
  public static final int CHEST_ID = 0x36;
  public static final IdBlock CHEST = new IdBlock(CHEST_ID, "minecraft:chest", Texture.chestFront) {
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

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int REDSTONEWIRE_ID = 0x37;
  public static final IdBlock
      REDSTONEWIRE = new IdBlock(REDSTONEWIRE_ID, "minecraft:redstone_wire", Texture.redstoneWireCross) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RedstoneWireModel.intersect(ray);
    }

    @Override public String description(int data) {
      return "power: " + data;
    }
  };
  public static final int DIAMONDORE_ID = 0x38;
  public static final IdBlock
      DIAMONDORE = new IdBlock(DIAMONDORE_ID, "minecraft:diamond_ore", Texture.diamondOre);
  public static final int DIAMONDBLOCK_ID = 0x39;
  public static final IdBlock
      DIAMONDBLOCK = new IdBlock(DIAMONDBLOCK_ID, "minecraft:diamond_block", Texture.diamondBlock);
  public static final int WORKBENCH_ID = 0x3A;
  public static final IdBlock
      WORKBENCH = new IdBlock(WORKBENCH_ID, "minecraft:crafting_table", Texture.workbenchFront) {
    final Texture[] texture =
        {Texture.workbenchFront, Texture.workbenchSide, Texture.workbenchSide,
            Texture.workbenchFront, Texture.workbenchTop, Texture.oakPlanks,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int CROPS_ID = 0x3B;
  public static final IdBlock CROPS = new IdBlock(CROPS_ID, "minecraft:wheat", Texture.crops7) {
    final Texture[] texture =
        {Texture.crops0, Texture.crops1, Texture.crops2, Texture.crops3, Texture.crops4,
            Texture.crops5, Texture.crops6, Texture.crops7};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
    }
  };
  public static final int SOIL_ID = 0x3C;
  public static final IdBlock SOIL = new IdBlock(SOIL_ID, "minecraft:farmland", Texture.farmlandWet) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FarmlandModel.intersect(ray);
    }
  };
  public static final int FURNACEUNLIT_ID = 0x3D;
  public static final IdBlock
      FURNACEUNLIT = new IdBlock(FURNACEUNLIT_ID, "minecraft:furnace", Texture.furnaceUnlitFront) {
    final Texture[] texture =
        {Texture.furnaceUnlitFront, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceSide, Texture.furnaceTop, Texture.furnaceTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return FurnaceModel.intersect(ray, texture);
    }
  };
  public static final int FURNACELIT_ID = 0x3E;
  public static final IdBlock
      FURNACELIT = new IdBlock(FURNACELIT_ID, "minecraft:lit_furnace", Texture.furnaceLitFront) {
    final Texture[] texture =
        {Texture.furnaceLitFront, Texture.furnaceSide, Texture.furnaceSide, Texture.furnaceSide,
            Texture.furnaceTop, Texture.furnaceTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return FurnaceModel.intersect(ray, texture);
    }
  };
  public static final int SIGNPOST_ID = 0x3F;
  public static final IdBlock SIGNPOST = new IdBlock(SIGNPOST_ID, "minecraft:standing_sign", Icon.signPost);
  public static final int WOODENDOOR_ID = 0x40;
  public static final IdBlock WOODENDOOR = new IdBlock(WOODENDOOR_ID, "minecraft:wooden_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.woodenDoorBottom, Texture.woodenDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int LADDER_ID = 0x41;
  public static final IdBlock LADDER = new IdBlock(LADDER_ID, "minecraft:ladder", Texture.ladder) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LadderModel.intersect(ray);
    }
  };
  public static final int MINECARTTRACKS_ID = 0x42;
  public static final IdBlock
      MINECARTTRACKS = new IdBlock(MINECARTTRACKS_ID, "minecraft:rail", Texture.rails) {
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
  public static final IdBlock
      STONESTAIRS = new Stairs(STONESTAIRS_ID, "minecraft:stone_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.cobblestone);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.stone;
    }
  };
  public static final int WALLSIGN_ID = 0x44;
  public static final IdBlock WALLSIGN = new IdBlock(WALLSIGN_ID, "minecraft:wall_sign", Icon.wallSign);
  public static final int LEVER_ID = 0x45;
  public static final IdBlock LEVER = new IdBlock(LEVER_ID, "minecraft:lever", Texture.lever) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LeverModel.intersect(ray);
    }
  };
  public static final int STONEPRESSUREPLATE_ID = 0x46;
  public static final IdBlock
      STONEPRESSUREPLATE = new IdBlock(STONEPRESSUREPLATE_ID, "minecraft:stone_pressure_plate", Icon.stonePressurePlate) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.stone);
    }
  };
  public static final int IRONDOOR_ID = 0x47;
  public static final IdBlock IRONDOOR = new IdBlock(IRONDOOR_ID, "minecraft:iron_door", Icon.ironDoor) {
    final Texture[] texture = {Texture.ironDoorBottom, Texture.ironDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int WOODENPRESSUREPLATE_ID = 0x48;
  public static final IdBlock
      WOODENPRESSUREPLATE = new IdBlock(WOODENPRESSUREPLATE_ID, "minecraft:wooden_pressure_plate", Icon.woodenPressurePlate) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.oakPlanks);
    }
  };
  public static final int REDSTONEORE_ID = 0x49;
  public static final IdBlock
      REDSTONEORE = new IdBlock(REDSTONEORE_ID, "minecraft:redstone_ore", Texture.redstoneOre);
  public static final int GLOWINGREDSTONEORE_ID = 0x4A;
  public static final IdBlock
      GLOWINGREDSTONEORE = new IdBlock(GLOWINGREDSTONEORE_ID, "minecraft:lit_redstone_ore", Texture.redstoneOre);
  public static final int REDSTONETORCHOFF_ID = 0x4B;
  public static final IdBlock
      REDSTONETORCHOFF = new IdBlock(REDSTONETORCHOFF_ID, "minecraft:unlit_redstone_torch", Texture.redstoneTorchOff) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TorchModel.intersect(ray, Texture.redstoneTorchOff);
    }

    final String[] direction = {"", "east", "west", "south", "north", "on floor"};

    @Override public String description(int data) {
      return direction[data % 6];
    }

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int REDSTONETORCHON_ID = 0x4C;
  public static final IdBlock
      REDSTONETORCHON = new IdBlock(REDSTONETORCHON_ID, "minecraft:redstone_torch", Texture.redstoneTorchOn) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TorchModel.intersect(ray, Texture.redstoneTorchOn);
    }

    final String[] direction = {"", "east", "west", "south", "north", "on floor"};

    @Override public String description(int data) {
      return direction[data % 6];
    }

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int STONEBUTTON_ID = 0x4D;
  public static final IdBlock
      STONEBUTTON = new IdBlock(STONEBUTTON_ID, "minecraft:stone_button", Icon.stoneButton) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ButtonModel.intersect(ray, Texture.stone);
    }
  };
  public static final int SNOW_ID = 0x4E;
  public static final IdBlock SNOW = new IdBlock(SNOW_ID, "minecraft:snow_layer", Texture.snowBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SnowModel.intersect(ray);
    }
  };
  public static final int ICE_ID = 0x4F;
  public static final IdBlock ICE = new IdBlock(ICE_ID, "minecraft:ice", Texture.ice);
  public static final int SNOWBLOCK_ID = 0x50;
  public static final IdBlock SNOWBLOCK = new IdBlock(SNOWBLOCK_ID, "minecraft:snow", Texture.snowBlock);
  public static final int CACTUS_ID = 0x51;
  public static final IdBlock CACTUS = new IdBlock(CACTUS_ID, "minecraft:cactus", Texture.cactusSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CactusModel.intersect(ray);
    }
  };
  public static final int CLAY_ID = 0x52;
  public static final IdBlock CLAY = new IdBlock(CLAY_ID, "minecraft:clay", Texture.clay);
  public static final int SUGARCANE_ID = 0x53;
  public static final IdBlock SUGARCANE = new IdBlock(SUGARCANE_ID, "minecraft:reeds", Texture.sugarCane) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SpriteModel.intersect(ray, Texture.sugarCane);
    }
  };
  public static final int JUKEBOX_ID = 0x54;
  public static final IdBlock JUKEBOX = new IdBlock(JUKEBOX_ID, "minecraft:jukebox", Texture.jukeboxSide) {
    final Texture[] texture = {
        Texture.jukeboxSide, Texture.jukeboxSide, Texture.jukeboxSide, Texture.jukeboxSide,
        Texture.jukeboxTop, Texture.jukeboxSide,
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int FENCE_ID = 0x55;
  public static final IdBlock FENCE = new Fence(FENCE_ID, "minecraft:fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.oakPlanks);
    }
  };
  public static final int PUMPKIN_ID = 0x56;
  public static final IdBlock PUMPKIN = new IdBlock(PUMPKIN_ID, "minecraft:pumpkin", Texture.pumpkinSide) {
    final Texture[] texture =
        {Texture.pumpkinFront, Texture.pumpkinSide, Texture.pumpkinSide, Texture.pumpkinSide,
            Texture.pumpkinTop, Texture.pumpkinTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return PumpkinModel.intersect(ray, texture);
    }
  };
  public static final int NETHERRACK_ID = 0x57;
  public static final IdBlock NETHERRACK =
      new IdBlock(NETHERRACK_ID, "minecraft:netherrack", Texture.netherrack);
  public static final int SOULSAND_ID = 0x58;
  public static final IdBlock SOULSAND = new IdBlock(SOULSAND_ID, "minecraft:soul_sand", Texture.soulsand);
  public static final int GLOWSTONE_ID = 0x59;
  public static final IdBlock GLOWSTONE = new IdBlock(GLOWSTONE_ID, "minecraft:glowstone", Texture.glowstone);
  public static final int PORTAL_ID = 0x5A;
  public static final IdBlock PORTAL = new IdBlock(PORTAL_ID, "minecraft:portal", Texture.portal);
  public static final int JACKOLANTERN_ID = 0x5B;
  public static final IdBlock
      JACKOLANTERN = new IdBlock(JACKOLANTERN_ID, "minecraft:lit_pumpkin", Texture.jackolanternFront) {
    final Texture[] texture =
        {Texture.jackolanternFront, Texture.pumpkinSide, Texture.pumpkinSide,
            Texture.pumpkinSide, Texture.pumpkinTop, Texture.pumpkinTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return PumpkinModel.intersect(ray, texture);
    }
  };
  public static final int CAKE_ID = 0x5C;
  public static final IdBlock CAKE = new IdBlock(CAKE_ID, "minecraft:cake", Icon.cake) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CakeModel.intersect(ray);
    }
  };
  public static final int REDSTONEREPEATEROFF_ID = 0x5D;
  public static final IdBlock
      REDSTONEREPEATEROFF = new IdBlock(REDSTONEREPEATEROFF_ID, "minecraft:unpowered_repeater", Texture.redstoneRepeaterOff) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RedstoneRepeaterModel.intersect(ray, 0);
    }
  };
  public static final int REDSTONEREPEATERON_ID = 0x5E;
  public static final IdBlock
      REDSTONEREPEATERON = new IdBlock(REDSTONEREPEATERON_ID, "minecraft:powered_repeater", Texture.redstoneRepeaterOn) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return RedstoneRepeaterModel.intersect(ray, 1);
    }
  };
  public static final int STAINED_GLASS_ID = 0x5F;
  public static final IdBlock
      STAINED_GLASS = new IdBlock(STAINED_GLASS_ID, "minecraft:stained_glass", Texture.glass) {
    @Override public Texture getTexture(int blockData) {
      return Texture.stainedGlass[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }

    @Override public boolean isSameMaterial(Material other) {
      return other == this || other == GLASS;
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }
  };
  public static final int TRAPDOOR_ID = 0x60;
  public static final IdBlock TRAPDOOR = new IdBlock(TRAPDOOR_ID, "minecraft:trapdoor", Texture.trapdoor) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TrapdoorModel.intersect(ray, Texture.trapdoor);
    }
  };
  public static final int HIDDENSILVERFISH_ID = 0x61;
  public static final IdBlock
      HIDDENSILVERFISH = new IdBlock(HIDDENSILVERFISH_ID, "minecraft:monster_egg", Texture.stone) {
    final Texture[] texture = {Texture.stone, Texture.cobblestone, Texture.stoneBrick};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % texture.length]);
    }
  };
  public static final int STONEBRICKS_ID = 0x62;
  public static final IdBlock
      STONEBRICKS = new IdBlock(STONEBRICKS_ID, "minecraft:stonebrick", Texture.stoneBrick) {
    final Texture[] texture =
        {Texture.stoneBrick, Texture.mossyStoneBrick, Texture.crackedStoneBrick,
            Texture.circleStoneBrick};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[(ray.getCurrentData() >> 8) & 3]);
    }
  };
  public static final int HUGEBROWNMUSHROOM_ID = 0x63;
  public static final IdBlock
      HUGEBROWNMUSHROOM = new IdBlock(HUGEBROWNMUSHROOM_ID, "minecraft:brown_mushroom_block", Texture.hugeBrownMushroom) {
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
  public static final IdBlock
      HUGEREDMUSHROOM = new IdBlock(HUGEREDMUSHROOM_ID, "minecraft:red_mushroom_block", Texture.hugeRedMushroom) {
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
  public static final IdBlock IRONBARS = new IdBlock(IRONBARS_ID, "minecraft:iron_bars", Texture.ironBars) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return IronBarsModel.intersect(ray);
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }
  };
  public static final int GLASSPANE_ID = 0x66;
  public static final IdBlock GLASSPANE = new IdBlock(GLASSPANE_ID, "minecraft:glass_pane", Texture.glass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return GlassPaneModel.intersect(ray, Texture.glass, Texture.glassPaneTop);
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }
  };
  public static final int MELON_ID = 0x67;
  public static final IdBlock MELON = new IdBlock(MELON_ID, "minecraft:melon_block", Texture.melonSide) {
    final Texture texture[] =
        {Texture.melonSide, Texture.melonSide, Texture.melonSide, Texture.melonSide,
            Texture.melonTop, Texture.melonTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture);
    }
  };
  public static final int PUMPKINSTEM_ID = 0x68;
  public static final IdBlock
      PUMPKINSTEM = new IdBlock(PUMPKINSTEM_ID, "minecraft:pumpkin_stem", Texture.stemStraight) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return MelonStemModel.intersect(ray);
    }
  };
  public static final int MELONSTEM_ID = 0x69;
  public static final IdBlock MELONSTEM = new IdBlock(MELONSTEM_ID, "minecraft:melon_stem", Texture.stemStraight) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return MelonStemModel.intersect(ray);
    }
  };
  public static final int VINES_ID = 0x6A;
  public static final IdBlock VINES = new IdBlock(VINES_ID, "minecraft:vine", Texture.vines) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return VineModel.intersect(ray, scene);
    }
  };
  public static final int FENCEGATE_ID = 0x6B;
  public static final IdBlock
      FENCEGATE = new FenceGate(FENCEGATE_ID, "minecraft:fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.oakPlanks);
    }
  };
  public static final int BRICKSTAIRS_ID = 0x6C;
  public static final IdBlock
      BRICKSTAIRS = new Stairs(BRICKSTAIRS_ID, "minecraft:brick_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.brick);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.brick;
    }
  };
  public static final int STONEBRICKSTAIRS_ID = 0x6D;
  public static final IdBlock
      STONEBRICKSTAIRS = new Stairs(STONEBRICKSTAIRS_ID, "minecraft:stone_brick_stairs", Icon.stoneBrickStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.stoneBrick);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.stoneBrick;
    }
  };
  public static final int MYCELIUM_ID = 0x6E;
  public static final IdBlock MYCELIUM = new IdBlock(MYCELIUM_ID, "minecraft:mycelium", Texture.myceliumSide) {
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
  public static final IdBlock LILY_PAD = new IdBlock(LILY_PAD_ID, "minecraft:waterlily", Texture.lilyPad) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LilyPadModel.intersect(ray);
    }

    @Override public void getColor(Ray ray) {
      LilyPadModel.getColor(ray);
    }
  };
  public static final int NETHERBRICK_ID = 0x70;
  public static final IdBlock
      NETHERBRICK = new IdBlock(NETHERBRICK_ID, "minecraft:nether_brick", Texture.netherBrick);
  public static final int NETHERBRICKFENCE_ID = 0x71;
  public static final IdBlock
      NETHERBRICKFENCE = new IdBlock(NETHERBRICKFENCE_ID, "minecraft:nether_brick_fence", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.netherBrick);
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isGlassPaneConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isIronBarsConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }
  };
  public static final int NETHERBRICKSTAIRS_ID = 0x72;
  public static final IdBlock
      NETHERBRICKSTAIRS = new Stairs(NETHERBRICKSTAIRS_ID, "minecraft:nether_brick_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.netherBrick);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.netherBrick;
    }
  };
  public static final int NETHERWART_ID = 0x73;
  public static final IdBlock
      NETHERWART = new IdBlock(NETHERWART_ID, "minecraft:nether_wart_block", Texture.netherWart2) {
    final Texture[] texture =
        {Texture.netherWart0, Texture.netherWart1, Texture.netherWart1, Texture.netherWart2};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() & 3]);
    }
  };
  public static final int ENCHNATMENTTABLE_ID = 0x74;
  public static final IdBlock
      ENCHNATMENTTABLE = new IdBlock(ENCHNATMENTTABLE_ID, "minecraft:enchanting_table", Texture.enchantmentTableSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return EnchantmentTableModel.intersect(ray);
    }
  };
  public static final int BREWINGSTAND_ID = 0x75;
  public static final IdBlock
      BREWINGSTAND = new IdBlock(BREWINGSTAND_ID, "minecraft:brewing_stand", Texture.brewingStandSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return BrewingStandModel.intersect(ray);
    }
  };
  public static final int CAULDRON_ID = 0x76;
  public static final IdBlock CAULDRON = new IdBlock(CAULDRON_ID, "minecraft:cauldron", Texture.cauldronSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CauldronModel.intersect(ray);
    }
  };
  public static final int ENDPORTAL_ID = 0x77;
  public static final IdBlock ENDPORTAL = new IdBlock(ENDPORTAL_ID, "minecraft:end_portal", Texture.endPortal) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return EndPortalModel.intersect(ray);
    }
  };
  public static final int ENDPORTALFRAME_ID = 0x78;
  public static final IdBlock
      ENDPORTALFRAME = new IdBlock(ENDPORTALFRAME_ID, "minecraft:end_portal_frame", Texture.endPortalFrameTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return EndPortalFrameModel.intersect(ray);
    }
  };
  public static final int ENDSTONE_ID = 0x79;
  public static final IdBlock ENDSTONE = new IdBlock(ENDSTONE_ID, "minecraft:end_stone", Texture.endStone);
  public static final int DRAGONEGG_ID = 0x7A;
  public static final IdBlock DRAGONEGG = new IdBlock(DRAGONEGG_ID, "minecraft:dragon_egg", Texture.dragonEgg) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return DragonEggModel.intersect(ray);
    }

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int REDSTONELAMPOFF_ID = 0x7B;
  public static final IdBlock
      REDSTONELAMPOFF = new IdBlock(REDSTONELAMPOFF_ID, "minecraft:redstone_lamp", Texture.redstoneLampOff);
  public static final int REDSTONELAMPON_ID = 0x7C;
  public static final IdBlock
      REDSTONELAMPON = new IdBlock(REDSTONELAMPON_ID, "minecraft:lit_redstone_lamp", Texture.redstoneLampOn);
  public static final int DOUBLEWOODENSLAB_ID = 0x7D;
  public static final IdBlock
      DOUBLEWOODENSLAB = new IdBlock(DOUBLEWOODENSLAB_ID, "minecraft:double_wooden_slab", Texture.oakPlanks) {
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
  public static final IdBlock
      SINGLEWOODENSLAB = new IdBlock(SINGLEWOODENSLAB_ID, "minecraft:wooden_slab", Texture.oakPlanks) {
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
  public static final IdBlock COCOAPLANT = new IdBlock(COCOAPLANT_ID, "minecraft:cocoa", Texture.cocoaPlantLarge) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CocoaPlantModel.intersect(ray);
    }

    final String[] cocoaSize = {"small", "medium", "large"};

    @Override public String description(int data) {
      return cocoaSize[(data & 15) >> 2];
    }
  };
  public static final int SANDSTONESTAIRS_ID = 0x80;
  public static final IdBlock
      SANDSTONESTAIRS = new Stairs(SANDSTONESTAIRS_ID, "minecraft:sandstone_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel
          .intersect(ray, Texture.sandstoneSide, Texture.sandstoneTop, Texture.sandstoneBottom);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.sandstoneSide;
    }
  };
  public static final int EMERALDORE_ID = 0x81;
  public static final IdBlock
      EMERALDORE = new IdBlock(EMERALDORE_ID, "minecraft:emerald_ore", Texture.emeraldOre);
  public static final int ENDERCHEST_ID = 0x82;
  public static final IdBlock
      ENDERCHEST = new IdBlock(ENDERCHEST_ID, "minecraft:ender_chest", Texture.unknown) {
    final Texture[] texture =
        {Texture.enderChestFront, Texture.enderChestBack, Texture.enderChestLeft,
            Texture.enderChestRight, Texture.enderChestTop, Texture.enderChestBottom,
            Texture.enderChestLock, Texture.enderChestLock, Texture.enderChestLock,
            Texture.enderChestLock, Texture.enderChestLock,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return ChestModel.intersect(ray, texture);
    }

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int TRIPWIREHOOK_ID = 0x83;
  public static final IdBlock
      TRIPWIREHOOK = new IdBlock(TRIPWIREHOOK_ID, "minecraft:tripwire_hook", Texture.tripwireHook) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TripwireHookModel.intersect(ray);
    }
  };
  public static final int TRIPWIRE_ID = 0x84;
  public static final IdBlock TRIPWIRE = new IdBlock(TRIPWIRE_ID, "minecraft:tripwire", Texture.tripwire) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TripwireModel.intersection(ray);
    }
  };
  public static final int EMERALDBLOCK_ID = 0x85;
  public static final IdBlock
      EMERALDBLOCK = new IdBlock(EMERALDBLOCK_ID, "minecraft:emerald_block", Texture.emeraldBlock);
  public static final int SPRUCEWOODSTAIRS_ID = 0x86;
  public static final IdBlock
      SPRUCEWOODSTAIRS = new Stairs(SPRUCEWOODSTAIRS_ID, "minecraft:spruce_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.sprucePlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.sprucePlanks;
    }
  };
  public static final int BIRCHWOODSTAIRS_ID = 0x87;
  public static final IdBlock
      BIRCHWOODSTAIRS = new Stairs(BIRCHWOODSTAIRS_ID, "minecraft:birch_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.birchPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.birchPlanks;
    }
  };
  public static final int JUNGLEWOODSTAIRS_ID = 0x88;
  public static final IdBlock
      JUNGLEWOODSTAIRS = new Stairs(JUNGLEWOODSTAIRS_ID, "minecraft:jungle_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.jungleTreePlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.jungleTreePlanks;
    }
  };
  public static final int COMMAND_BLOCK_ID = 0x89;
  public static final IdBlock
      COMMAND_BLOCK = new IdBlock(COMMAND_BLOCK_ID, "minecraft:command_block", Texture.commandBlockBack) {
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
  public static final IdBlock BEACON = new IdBlock(BEACON_ID, "minecraft:beacon", Texture.glass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return BeaconModel.intersect(ray);
    }
  };
  public static final int STONEWALL_ID = 0x8B;
  public static final IdBlock
      STONEWALL = new IdBlock(STONEWALL_ID, "minecraft:cobblestone_wall", Texture.unknown) {
    final Texture[] texture = { Texture.cobblestone, Texture.mossStone };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return StoneWallModel.intersect(ray, texture[ray.getBlockData() & 1]);
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isIronBarsConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isGlassPaneConnector(int data, int direction) {
      return false;
    }
  };
  public static final int FLOWERPOT_ID = 0x8C;
  public static final IdBlock FLOWERPOT = new IdBlock(FLOWERPOT_ID, "minecraft:flower_pot", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FlowerPotModel.intersect(ray, scene);
    }
  };
  public static final int CARROTS_ID = 0x8D;
  public static final IdBlock CARROTS = new IdBlock(CARROTS_ID, "minecraft:carrots", Texture.carrots3) {
    final Texture[] texture = {
        Texture.carrots0, Texture.carrots0, Texture.carrots1, Texture.carrots1, Texture.carrots2,
        Texture.carrots2, Texture.carrots2, Texture.carrots3
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
    }
  };
  public static final int POTATOES_ID = 0x8E;
  public static final IdBlock POTATOES = new IdBlock(POTATOES_ID, "minecraft:potatoes", Texture.potatoes3) {
    final Texture[] texture =
        {Texture.potatoes0, Texture.potatoes0, Texture.potatoes1, Texture.potatoes1,
            Texture.potatoes2, Texture.potatoes2, Texture.potatoes2, Texture.potatoes3};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() % 8]);
    }
  };
  public static final int WOODENBUTTON_ID = 0x8F;
  public static final IdBlock
      WOODENBUTTON = new IdBlock(WOODENBUTTON_ID, "minecraft:wooden_button", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ButtonModel.intersect(ray, Texture.oakPlanks);
    }
  };
  public static final int HEAD_ID = 0x90;
  public static final IdBlock HEAD = new IdBlock(HEAD_ID, "minecraft:skull", Texture.unknown);
  public static final int ANVIL_ID = 0x91;
  public static final IdBlock ANVIL = new IdBlock(ANVIL_ID, "minecraft:anvil", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return AnvilModel.intersect(ray);
    }
  };
  public static final int TRAPPEDCHEST_ID = 0x92;
  public static final IdBlock
      TRAPPEDCHEST = new IdBlock(TRAPPEDCHEST_ID, "minecraft:trapped_chest", Texture.chestFront) {
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

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int WEIGHTEDPRESSUREPLATELIGHT_ID = 0x93;
  public static final IdBlock WEIGHTEDPRESSUREPLATELIGHT = new IdBlock(WEIGHTEDPRESSUREPLATELIGHT_ID, "minecraft:light_weighted_pressure_plate", Texture.goldBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.goldBlock);
    }
  };
  public static final int WEIGHTEDPRESSUREPLATEHEAVY_ID = 0x94;
  public static final IdBlock WEIGHTEDPRESSUREPLATEHEAVY = new IdBlock(WEIGHTEDPRESSUREPLATEHEAVY_ID, "minecraft:heavy_weighted_pressure_plate", Texture.ironBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return PressurePlateModel.intersect(ray, Texture.ironBlock);
    }
  };
  public static final int COMPARATOR_ID = 0x95;
  public static final IdBlock COMPARATOR = new IdBlock(COMPARATOR_ID,
      "minecraft:unpowered_comparator", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ComparatorModel.intersect(ray, 1 & (ray.getBlockData() >> 3));
    }
  };
  public static final int COMPARATOR_POWERED_ID = 0x96;
  public static final IdBlock COMPARATOR_POWERED = new IdBlock(COMPARATOR_POWERED_ID,
      "minecraft:powered_comparator", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return ComparatorModel.intersect(ray, 1);
    }
  };
  public static final int DAYLIGHTSENSOR_ID = 0x97;
  public static final IdBlock DAYLIGHTSENSOR = new IdBlock(DAYLIGHTSENSOR_ID,
      "minecraft:daylight_detector", Texture.daylightDetectorTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return DaylightSensorModel.intersect(ray, Texture.daylightDetectorTop);
    }
  };
  public static final int REDSTONEBLOCK_ID = 0x98;
  public static final IdBlock
      REDSTONEBLOCK = new IdBlock(REDSTONEBLOCK_ID, "minecraft:redstone_block", Texture.redstoneBlock);
  public static final int NETHERQUARTZORE_ID = 0x99;
  public static final IdBlock
      NETHERQUARTZORE = new IdBlock(NETHERQUARTZORE_ID, "minecraft:quartz_ore", Texture.netherQuartzOre);
  public static final int HOPPER_ID = 0x9A;
  public static final IdBlock HOPPER = new IdBlock(HOPPER_ID, "minecraft:hopper", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return HopperModel.intersect(ray);
    }

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int QUARTZ_ID = 0x9B;
  public static final IdBlock QUARTZ = new IdBlock(QUARTZ_ID, "minecraft:quartz_block", Texture.quartzSide) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return QuartzModel.intersect(ray);
    }
  };
  public static final int QUARTZSTAIRS_ID = 0x9C;
  public static final IdBlock
      QUARTZSTAIRS = new Stairs(QUARTZSTAIRS_ID, "minecraft:quartz_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel
          .intersect(ray, Texture.quartzSide, Texture.quartzTop, Texture.quartzBottom);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.quartzSide;
    }
  };
  public static final int ACTIVATORRAIL_ID = 0x9D;
  public static final IdBlock
      ACTIVATORRAIL = new IdBlock(ACTIVATORRAIL_ID, "minecraft:activator_rail", Texture.unknown) {
    final Texture[] texture = {Texture.activatorRail, Texture.activatorRailPowered};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return RailModel
          .intersect(ray, texture[ray.getBlockData() >>> 3], (ray.getBlockData() & 7) % 6);
    }
  };
  public static final int DROPPER_ID = 0x9E;
  public static final IdBlock DROPPER = new IdBlock(DROPPER_ID, "minecraft:dropper", Texture.dropperFront) {
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
  public static final IdBlock
      STAINED_CLAY = new IdBlock(STAINED_CLAY_ID, "minecraft:stained_hardened_clay", Texture.clay) {
    @Override public Texture getTexture(int blockData) {
      return Texture.stainedClay[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final int STAINED_GLASSPANE_ID = 0xA0;
  public static final IdBlock
      STAINED_GLASSPANE = new IdBlock(STAINED_GLASSPANE_ID, "minecraft:stained_glass_pane", Texture.glass) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      int data = ray.getBlockData();
      return GlassPaneModel
          .intersect(ray, Texture.stainedGlass[data], Texture.stainedGlassPaneSide[data]);
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }
  };
  public static final int LEAVES2_ID = 0xA1;
  public static final IdBlock LEAVES2 = new IdBlock(LEAVES2_ID, "minecraft:leaves", Texture.oakLeaves) {
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

    @Override public boolean isFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isNetherBrickFenceConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isStoneWallConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isGlassPaneConnector(int data, int direction) {
      return false;
    }

    @Override public boolean isIronBarsConnector(int data, int direction) {
      return false;
    }
  };
  public static final int WOOD2_ID = 0xA2;
  public static final IdBlock WOOD2 = new IdBlock(WOOD2_ID, "minecraft:log2", Texture.oakWood) {
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
  public static final IdBlock
      ACACIASTAIRS = new Stairs(ACACIASTAIRS_ID, "minecraft:acacia_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.acaciaPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.acaciaPlanks;
    }
  };
  public static final int DARKOAKSTAIRS_ID = 0xA4;
  public static final IdBlock
      DARKOAKSTAIRS = new Stairs(DARKOAKSTAIRS_ID, "minecraft:dark_oak_stairs", Icon.woodenStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.darkOakPlanks);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.darkOakPlanks;
    }
  };
  public static final int SLIMEBLOCK_ID = 0xA5;
  public static final IdBlock SLIMEBLOCK = new IdBlock(SLIMEBLOCK_ID, "minecraft:slime", Texture.slime);
  public static final int BARRIER_ID = 0xA6;
  public static final IdBlock BARRIER = new IdBlock(BARRIER_ID, "minecraft:barrier", Texture.unknown);
  public static final int IRON_TRAPDOOR_ID = 0xA7;
  public static final IdBlock
      IRON_TRAPDOOR = new IdBlock(IRON_TRAPDOOR_ID, "minecraft:iron_trapdoor", Texture.ironTrapdoor) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TrapdoorModel.intersect(ray, Texture.ironTrapdoor);
    }
  };
  public static final int PRISMARINE_ID = 0xA8;
  public static final IdBlock
      PRISMARINE = new IdBlock(PRISMARINE_ID, "minecraft:prismarine", Texture.prismarine) {
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
  public static final IdBlock
      SEALANTERN = new IdBlock(SEALANTERN_ID, "minecraft:sea_lantern", Texture.seaLantern);
  public static final int HAY_BLOCK_ID = 0xAA;
  public static final IdBlock HAY_BLOCK = new IdBlock(HAY_BLOCK_ID, "minecraft:hay_block", Texture.hayBlockSide) {
    final Texture[] texture = {Texture.hayBlockSide, Texture.hayBlockTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return WoodModel.intersect(ray, texture);
    }
  };
  public static final int CARPET_ID = 0xAB;
  public static final IdBlock CARPET = new IdBlock(CARPET_ID, "minecraft:carpet", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return CarpetModel.intersect(ray, Texture.wool[ray.getBlockData()]);
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final int HARDENED_CLAY_ID = 0xAC;
  public static final IdBlock
      HARDENED_CLAY = new IdBlock(HARDENED_CLAY_ID, "minecraft:hardened_clay", Texture.hardenedClay) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, Texture.hardenedClay);
    }
  };
  public static final int BLOCK_OF_COAL_ID = 0xAD;
  public static final IdBlock
      COAL_BLOCK = new IdBlock(BLOCK_OF_COAL_ID, "minecraft:coal_block", Texture.coalBlock);
  public static final int PACKED_ICE_ID = 0xAE;
  public static final IdBlock
      PACKED_ICE = new IdBlock(PACKED_ICE_ID, "minecraft:packed_ice", Texture.packedIce);
  public static final int LARGE_FLOWER_ID = 0xAF;
  public static final IdBlock
      LARGE_FLOWER = new IdBlock(LARGE_FLOWER_ID, "minecraft:double_plant", Texture.dandelion) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return LargeFlowerModel.intersect(ray, scene);
    }
  };
  public static final int STANDING_BANNER_ID = 0xB0;
  public static final IdBlock STANDING_BANNER = new IdBlock(STANDING_BANNER_ID,
      "minecraft:standing_banner", Texture.unknown);
  public static final int WALL_BANNER_ID = 0xB1;
  public static final IdBlock
      WALL_BANNER = new IdBlock(WALL_BANNER_ID, "minecraft:wall_banner", Texture.unknown);
  public static final IdBlock INVERTED_DAYLIGHTSENSOR = new IdBlock(0xB2,
      "minecraft:daylight_detector_inverted", Texture.daylightDetectorTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return DaylightSensorModel.intersect(ray, Texture.daylightDetectorInvertedTop);
    }
  };
  public static final int REDSANDSTONE_ID = 0xB3;
  public static final IdBlock
      REDSANDSTONE = new IdBlock(REDSANDSTONE_ID, "minecraft:red_standstone", Texture.redSandstoneSide) {
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
  public static final IdBlock
      REDSANDSTONESTAIRS = new Stairs(REDSANDSTONESTAIRS_ID, "minecraft:red_standstone_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.redSandstoneSide, Texture.redSandstoneTop,
          Texture.redSandstoneBottom);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.redSandstoneSide;
    }
  };
  public static final int DOUBLESLAB2_ID = 0xB5;
  public static final IdBlock
      DOUBLESLAB2 = new IdBlock(DOUBLESLAB2_ID, "minecraft:double_stone_slab2", Texture.redSandstoneTop) {
    final Texture[] textures =
        {Texture.redSandstoneSide, Texture.redSandstoneSide, Texture.redSandstoneSide,
            Texture.redSandstoneSide, Texture.redSandstoneTop, Texture.redSandstoneTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, textures);
    }
  };
  public static final int SLAB2_ID = 0xB6;
  public static final IdBlock SLAB2 = new IdBlock(SLAB2_ID, "minecraft:stone_slab2", Texture.redSandstoneTop) {
    final Texture[] textures = {Texture.redSandstoneSide, Texture.redSandstoneTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return SlabModel.intersect(ray, textures[0], textures[1]);
    }
  };
  public static final int SPRUCEFENCEGATE_ID = 0xB7;
  public static final IdBlock
      SPRUCEFENCEGATE = new FenceGate(SPRUCEFENCEGATE_ID, "minecraft:spruce_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.sprucePlanks);
    }
  };
  public static final int BIRCHFENCEGATE_ID = 0xB8;
  public static final IdBlock
      BIRCHFENCEGATE = new FenceGate(BIRCHFENCEGATE_ID, "minecraft:birch_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.birchPlanks);
    }
  };
  public static final int JUNGLEFENCEGATE_ID = 0xB9;
  public static final IdBlock
      JUNGLEFENCEGATE = new IdBlock(JUNGLEFENCEGATE_ID, "minecraft:jungle_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.jungleTreePlanks);
    }
  };
  public static final int DARKOAKFENCEGATE_ID = 0xBA;
  public static final IdBlock
      DARKOAKFENCEGATE = new IdBlock(DARKOAKFENCEGATE_ID, "minecraft:dark_oak_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.darkOakPlanks);
    }
  };
  public static final int ACACIAFENCEGATE_ID = 0xBB;
  public static final IdBlock
      ACACIAFENCEGATE = new IdBlock(ACACIAFENCEGATE_ID, "minecraft:acacia_fence_gate", Texture.unknown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceGateModel.intersect(ray, Texture.acaciaPlanks);
    }
  };
  public static final int SPRUCEFENCE_ID = 0xBC;
  public static final IdBlock SPRUCEFENCE = new Fence(SPRUCEFENCE_ID, "minecraft:spruce_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.sprucePlanks);
    }
  };
  public static final int BIRCHFENCE_ID = 0xBD;
  public static final IdBlock BIRCHFENCE = new Fence(BIRCHFENCE_ID, "minecraft:birch_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.birchPlanks);
    }
  };
  public static final int JUNGLEFENCE_ID = 0xBE;
  public static final IdBlock JUNGLEFENCE = new Fence(JUNGLEFENCE_ID, "minecraft:jungle_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.jungleTreePlanks);
    }
  };
  public static final int DARKOAKFENCE_ID = 0xBF;
  public static final IdBlock
      DARKOAKFENCE = new Fence(DARKOAKFENCE_ID, "minecraft:dark_oak_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.darkOakPlanks);
    }
  };
  public static final int ACACIAFENCE_ID = 0xC0;
  public static final IdBlock ACACIAFENCE = new Fence(ACACIAFENCE_ID, "minecraft:acacia_fence", Icon.fence) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return FenceModel.intersect(ray, Texture.acaciaPlanks);
    }
  };
  public static final int SPRUCEDOOR_ID = 0xC1;
  public static final IdBlock SPRUCEDOOR = new IdBlock(SPRUCEDOOR_ID, "minecraft:spruce_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.spruceDoorBottom, Texture.spruceDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int BIRCHDOOR_ID = 0xC2;
  public static final IdBlock BIRCHDOOR = new IdBlock(BIRCHDOOR_ID, "minecraft:birch_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.birchDoorBottom, Texture.birchDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int JUNGLEDOOR_ID = 0xC3;
  public static final IdBlock JUNGLEDOOR = new IdBlock(JUNGLEDOOR_ID, "minecraft:jungle_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.jungleDoorBottom, Texture.jungleDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int ACACIADOOR_ID = 0xC4;
  public static final IdBlock ACACIADOOR = new IdBlock(ACACIADOOR_ID, "minecraft:acacia_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.acaciaDoorBottom, Texture.acaciaDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int DARKOAKDOOR_ID = 0xC5;
  public static final IdBlock
      DARKOAKDOOR = new IdBlock(DARKOAKDOOR_ID, "minecraft:dark_oak_door", Icon.woodenDoor) {
    final Texture[] texture = {Texture.darkOakDoorBottom, Texture.darkOakDoorTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DoorModel.intersect(ray, texture[ray.getBlockData() >>> 3]);
    }
  };
  public static final int ENDROD_ID = 0xC6;
  public static final IdBlock ENDROD = new IdBlock(ENDROD_ID, "minecraft:end_rod", Texture.endRod) {
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

    @Override public boolean isWallTopConnector() {
      return true;
    }
  };
  public static final int CHORUSPLANT_ID = 0xC7;
  public static final IdBlock CHORUSPLANT =
      new IdBlock(CHORUSPLANT_ID, "minecraft:chorus_plant", Texture.chorusPlant) {
        @Override public boolean intersect(Ray ray, Scene scene) {
          return ChorusPlantModel.intersect(ray);
        }
      };
  public static final int CHORUSFLOWER_ID = 0xC8;
  public static final IdBlock CHORUSFLOWER =
      new IdBlock(CHORUSFLOWER_ID, "minecraft:chorus_flower", Texture.chorusFlower) {
        @Override public boolean intersect(Ray ray, Scene scene) {
          return ChorusFlowerModel.intersect(ray);
        }

        @Override public Texture getTexture(int blockData) {
          return blockData < 5 ? Texture.chorusFlower : Texture.chorusFlowerDead;
        }
      };
  public static final int PURPURBLOCK_ID = 0xC9;
  public static final IdBlock
      PURPURBLOCK = new IdBlock(PURPURBLOCK_ID, "minecraft:purpur_block", Texture.purpurBlock);
  public static final int PURPURPILLAR_ID = 0xCA;
  public static final IdBlock
      PURPURPILLAR = new IdBlock(PURPURPILLAR_ID, "minecraft:purpur_pillar", Texture.purpurPillarSide) {
    final Texture[] texture =
        {Texture.purpurPillarSide, Texture.purpurPillarSide, Texture.purpurPillarSide,
            Texture.purpurPillarSide, Texture.purpurPillarTop, Texture.purpurPillarTop,};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return TexturedBlockModel.intersect(ray, texture[ray.getBlockData() % 7]);
    }
  };
  public static final int PURPURSTAIRS_ID = 0xCB;
  public static final IdBlock
      PURPURSTAIRS = new Stairs(PURPURSTAIRS_ID, "minecraft:purpur_stairs", Icon.stoneStairs) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return StairModel.intersect(ray, Texture.purpurBlock);
    }

    @Override public Texture getTexture(int blockData) {
      return Texture.purpurBlock;
    }
  };
  public static final int PURPURDOUBLESLAB_ID = 0xCC;
  public static final IdBlock
      PURPURDOUBLESLAB = new IdBlock(PURPURDOUBLESLAB_ID, "minecraft:purpur_double_slab", Texture.purpurBlock);
  public static final int PURPURSLAB_ID = 0xCD;
  public static final IdBlock
      PURPURSLAB = new IdBlock(PURPURSLAB_ID, "minecraft:purpur_slab", Texture.purpurBlock) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return SlabModel.intersect(ray, Texture.purpurBlock);
    }
  };
  public static final int ENDBRICKS_ID = 0xCE;
  public static final IdBlock ENDBRICKS = new IdBlock(ENDBRICKS_ID, "minecraft:end_bricks", Texture.endBricks);
  public static final IdBlock BEETROOTS = new IdBlock(0xCF, "minecraft:beetroots", Texture.beets3) {
    final Texture[] texture = {
        Texture.beets0, Texture.beets1, Texture.beets2, Texture.beets3
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return CropsModel.intersect(ray, texture[ray.getBlockData() % 4]);
    }
  };
  public static final int GRASSPATH_ID = 0xD0;
  public static final IdBlock GRASSPATH = new IdBlock(GRASSPATH_ID, "minecraft:grass_path", Texture.grassPathTop) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return GrassPathModel.intersect(ray);
    }
  };
  public static final IdBlock END_GATEWAY = new IdBlock(0xD1, "minecraft:end_gateway", Texture.unknown);
  public static final int REPEATING_COMMAND_BLOCK_ID = 0xD2;
  public static final IdBlock REPEATING_COMMAND_BLOCK = new IdBlock(REPEATING_COMMAND_BLOCK_ID,
      "minecraft:repeating_command_block", Texture.repeatingCommandBlockBack) {
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
  public static final IdBlock CHAIN_COMMAND_BLOCK = new IdBlock(CHAIN_COMMAND_BLOCK_ID,
      "minecraft:chain_command_block", Texture.chainCommandBlockBack) {
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
  public static final IdBlock FROSTEDICE = new IdBlock(0xD4, "minecraft:frosted_ice", Texture.ice);
  public static final IdBlock MAGMA = new IdBlock(0xD5, "minecraft:magma", Texture.magma);
  public static final IdBlock NETHER_WART_BLOCK = new IdBlock(0xD6, "minecraft:nether_wart_block",
      Texture.netherWartBlock);
  public static final IdBlock RED_NETHER_BRICK = new IdBlock(0xD7, "minecraft:red_nether_brick",
      Texture.redNetherBrick);
  public static final IdBlock BONE = new IdBlock(0xD8, "minecraft:bone_block", Texture.boneSide) {
    final Texture[] texture = {Texture.boneSide, Texture.boneTop};

    @Override public boolean intersect(Ray ray, Scene scene) {
      return WoodModel.intersect(ray, texture);
    }
  };
  private static final IdBlock STRUCTURE_VOID = new IdBlock(0xD9, "Structure Void", Texture.unknown);
  public static final IdBlock OBSERVER = new IdBlock(0xDA, "minecraft:observer", Texture.unknown) {
    final Texture[] texture = {
        Texture.observerBack, Texture.observerFront,
        Texture.observerSide, Texture.observerTop
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return ObserverModel.intersect(ray, texture);
    }
  };
  public static final IdBlock
      SHULKERBOX_WHITE = new IdBlock(0xDB, "minecraft:white_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerWhite.bottom,
        Texture.shulkerWhite.top,
        Texture.shulkerWhite.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_ORANGE = new IdBlock(0xDC, "minecraft:orange_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerOrange.bottom,
        Texture.shulkerOrange.top,
        Texture.shulkerOrange.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_MAGENTA = new IdBlock(0xDD, "minecraft:magenta_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerMagenta.bottom,
        Texture.shulkerMagenta.top,
        Texture.shulkerMagenta.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_LIGHTBLUE = new IdBlock(0xDE, "minecraft:ligth_blue_shuler_box",
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
  public static final IdBlock
      SHULKERBOX_YELLOW = new IdBlock(0xDF, "minecraft:yellow_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerYellow.bottom,
        Texture.shulkerYellow.top,
        Texture.shulkerYellow.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_LIME = new IdBlock(0xE0, "minecraft:lime_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerLime.bottom,
        Texture.shulkerLime.top,
        Texture.shulkerLime.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_PINK = new IdBlock(0xE1, "minecraft:pink_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerPink.bottom,
        Texture.shulkerPink.top,
        Texture.shulkerPink.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_GRAY = new IdBlock(0xE2, "minecraft:gray_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerGray.bottom,
        Texture.shulkerGray.top,
        Texture.shulkerGray.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_SILVER = new IdBlock(0xE3, "minecraft:light_gray_shuler_box",
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
  public static final IdBlock
      SHULKERBOX_CYAN = new IdBlock(0xE4, "minecraft:cyan_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerCyan.bottom,
        Texture.shulkerCyan.top,
        Texture.shulkerCyan.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_PURPLE = new IdBlock(0xE5, "minecraft:purple_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerPurple.bottom,
        Texture.shulkerPurple.top,
        Texture.shulkerPurple.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_BLUE = new IdBlock(0xE6, "minecraft:blue_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerBlue.bottom,
        Texture.shulkerBlue.top,
        Texture.shulkerBlue.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_BROWN = new IdBlock(0xE7, "minecraft:brown_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerBrown.bottom,
        Texture.shulkerBrown.top,
        Texture.shulkerBrown.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_GREEN = new IdBlock(0xE8, "minecraft:green_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerGreen.bottom,
        Texture.shulkerGreen.top,
        Texture.shulkerGreen.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock SHULKERBOX_RED = new IdBlock(0xE9, "minecraft:red_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerRed.bottom,
        Texture.shulkerRed.top,
        Texture.shulkerRed.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      SHULKERBOX_BLACK = new IdBlock(0xEA, "minecraft:black_shuler_box", Texture.unknown) {
    final Texture[] textures = {
        Texture.shulkerBlack.bottom,
        Texture.shulkerBlack.top,
        Texture.shulkerBlack.side
    };

    @Override public boolean intersect(Ray ray, Scene scene) {
      return DirectionalBlockModel.intersect(ray, textures);
    }
  };
  public static final IdBlock
      WHITE_TERRACOTTA = new IdBlock(0xEB, "minecraft:white_glazed_terracotta",
      Texture.terracottaWhite) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaWhite);
    }
  };
  public static final IdBlock
      ORANGE_TERRACOTTA = new IdBlock(0xEC, "minecraft:orange_glazed_terracotta",
      Texture.terracottaOrange) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaOrange);
    }
  };
  public static final IdBlock
      MAGENTA_TERRACOTTA = new IdBlock(0xED, "minecraft:magenta_glazed_terracotta",
      Texture.terracottaMagenta) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaMagenta);
    }
  };
  public static final IdBlock
      LIGHT_BLUE_TERRACOTTA = new IdBlock(0xEE, "minecraft:light_blue_glazed_terracotta",
      Texture.terracottaLightBlue) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaLightBlue);
    }
  };
  public static final IdBlock
      YELLOW_TERRACOTTA = new IdBlock(0xEF, "minecraft:yellow_glazed_terracotta",
      Texture.terracottaYellow) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaYellow);
    }
  };
  public static final IdBlock LIME_TERRACOTTA = new IdBlock(0xF0, "minecraft:lime_glazed_terracotta",
      Texture.terracottaLime) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaLime);
    }
  };
  public static final IdBlock PINK_TERRACOTTA = new IdBlock(0xF1, "minecraft:pink_glazed_terracotta",
      Texture.terracottaPink) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaPink);
    }
  };
  public static final IdBlock GRAY_TERRACOTTA = new IdBlock(0xF2, "minecraft:gray_glazed_terracotta",
      Texture.terracottaGray) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaGray);
    }
  };
  public static final IdBlock
      SILVER_TERRACOTTA = new IdBlock(0xF3, "minecraft:light_gray_glazed_terracotta",
      Texture.terracottaSilver) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaSilver);
    }
  };
  public static final IdBlock CYAN_TERRACOTTA = new IdBlock(0xF4, "minecraft:cyan_glazed_terracotta",
      Texture.terracottaCyan) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaCyan);
    }
  };
  public static final IdBlock
      PURPLE_TERRACOTTA = new IdBlock(0xF5, "minecraft:purple_glazed_terracotta",
      Texture.terracottaPurple) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaPurple);
    }
  };
  public static final IdBlock BLUE_TERRACOTTA = new IdBlock(0xF6, "minecraft:blue_glazed_terracotta",
      Texture.terracottaBlue) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaBlue);
    }
  };
  public static final IdBlock
      BROWN_TERRACOTTA = new IdBlock(0xF7, "minecraft:brown_glazed_terracotta",
      Texture.terracottaBrown) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaBrown);
    }
  };
  public static final IdBlock
      GREEN_TERRACOTTA = new IdBlock(0xF8, "minecraft:green_glazed_terracotta",
      Texture.terracottaGreen) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaGreen);
    }
  };
  public static final IdBlock RED_TERRACOTTA = new IdBlock(0xF9, "minecraft:red_glazed_terracotta",
      Texture.terracottaRed) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaRed);
    }
  };
  public static final IdBlock
      BLACK_TERRACOTTA = new IdBlock(0xFA, "minecraft:black_glazed_terracotta",
      Texture.terracottaBlack) {
    @Override public boolean intersect(Ray ray, Scene scene) {
      return TerracottaModel.intersect(ray, Texture.terracottaBlack);
    }
  };
  public static final IdBlock CONCRETE = new IdBlock(0xFB, "minecraft:concrete", Texture.unknown) {
    @Override public Texture getTexture(int blockData) {
      return Texture.concrete[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  public static final IdBlock
      CONCRETE_POWDER = new IdBlock(0xFC, "minecraft:concrete_powder", Texture.unknown) {
    @Override public Texture getTexture(int blockData) {
      return Texture.concretePowder[blockData];
    }

    @Override public String description(int data) {
      return woolColor[data & 15];
    }
  };
  private static final IdBlock UNKNOWN0xFD = new IdBlock(0xFD, "Unknown Block 0xFD", Texture.unknown);
  private static final IdBlock UNKNOWN0xFE = new IdBlock(0xFE, "Unknown Block 0xFE", Texture.unknown);
  public static final IdBlock
      STRUCTURE_BLOCK = new IdBlock(0xFF, "minecraft:structure_block", Texture.unknown);

  public static final IdBlock[] blocks = {
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

  public static final Map<String, IdBlock> idMap = new HashMap<>();
  public static final Map<String, Collection<IdBlock>> collections = new LinkedHashMap<>();

  static {
    collections.put("all:blocks", Arrays.asList(blocks));
    collections.put("all:water", Arrays.asList(IdBlock.WATER, IdBlock.STATIONARYWATER));
    collections.put("all:lava", Arrays.asList(IdBlock.LAVA, IdBlock.STATIONARYLAVA));

    for (IdBlock block : blocks) {
      if (block != UNKNOWN0xFD && block != UNKNOWN0xFE) {
        idMap.put(block.name, block);
      }
    }
  }

  /** Minecraft block ID. */
  public final int id;

  /** The block types that can connect to redstone wire. */
  private static final Set<IdBlock> redstoneConnectors = new HashSet<>();

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
  public IdBlock(int id, String name, Texture texture) {
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
    for (IdBlock block : blocks) {
      block.opaque = false;
      block.solid = true;
      block.localIntersect = false;
      block.invisible = false;
      block.emittance = 0;
      block.specular = 0;
      block.ior = 1.000293f;
    }

    AIR.solid = false;
    AIR.invisible = true;
    STONE.opaque = true;
    STONE.localIntersect = true;
    GRASS.opaque = true;
    GRASS.localIntersect = true;
    DIRT.opaque = true;
    DIRT.localIntersect = true;
    COBBLESTONE.opaque = true;
    WOODENPLANKS.opaque = true;
    WOODENPLANKS.localIntersect = true;
    SAPLING.solid = false;
    SAPLING.localIntersect = true;
    WATER.solid = false;
    WATER.localIntersect = true;
    WATER.specular = 0.12f;
    WATER.ior = 1.333f;
    STATIONARYWATER.solid = false;
    STATIONARYWATER.localIntersect = true;
    STATIONARYWATER.specular = 0.12f;
    STATIONARYWATER.ior = 1.333f;
    LAVA.solid = false;
    LAVA.localIntersect = true;
    LAVA.emittance = 1.0f;
    STATIONARYLAVA.solid = false;
    STATIONARYLAVA.localIntersect = true;
    STATIONARYLAVA.emittance = 1.0f;
    SAND.opaque = true;
    SAND.localIntersect = true;
    GRAVEL.opaque = true;
    GOLDORE.opaque = true;
    IRONORE.opaque = true;
    COALORE.opaque = true;
    WOOD.opaque = true;
    WOOD.localIntersect = true;
    LEAVES.localIntersect = true;
    SPONGE.opaque = true;
    SPONGE.localIntersect = true;
    GLASS.ior = 1.52f;
    LAPIS_ORE.opaque = true;
    LAPIS_BLOCK.opaque = true;
    DISPENSER.opaque = true;
    DISPENSER.localIntersect = true;
    SANDSTONE.opaque = true;
    SANDSTONE.localIntersect = true;
    NOTEBLOCK.opaque = true;
    BED.solid = false;
    BED.localIntersect = true;
    POWEREDRAIL.solid = false;
    POWEREDRAIL.localIntersect = true;
    DETECTORRAIL.solid = false;
    DETECTORRAIL.localIntersect = true;
    STICKYPISTON.solid = false;
    STICKYPISTON.localIntersect = true;
    COBWEB.solid = false;
    COBWEB.localIntersect = true;
    TALLGRASS.solid = false;
    TALLGRASS.localIntersect = true;
    DEADBUSH.solid = false;
    DEADBUSH.localIntersect = true;
    PISTON.solid = false;
    PISTON.localIntersect = true;
    PISTON_HEAD.solid = false;
    PISTON_HEAD.localIntersect = true;
    WOOL.opaque = true;
    PISTON_EXTENSION.solid = false;
    PISTON_EXTENSION.invisible = true;
    DANDELION.solid = false;
    DANDELION.localIntersect = true;
    FLOWER.solid = false;
    FLOWER.localIntersect = true;
    BROWNMUSHROOM.solid = false;
    BROWNMUSHROOM.localIntersect = true;
    REDMUSHROOM.solid = false;
    REDMUSHROOM.localIntersect = true;
    GOLDBLOCK.opaque = true;
    GOLDBLOCK.specular = 0.04f;
    IRONBLOCK.opaque = true;
    IRONBLOCK.specular = 0.04f;
    DOUBLESLAB.opaque = true;
    DOUBLESLAB.localIntersect = true;
    SLAB.solid = false;
    SLAB.localIntersect = true;
    BRICKS.opaque = true;
    TNT.opaque = true;
    TNT.localIntersect = true;
    BOOKSHELF.opaque = true;
    BOOKSHELF.localIntersect = true;
    MOSSSTONE.opaque = true;
    OBSIDIAN.opaque = true;
    TORCH.solid = false;
    TORCH.localIntersect = true;
    TORCH.emittance = 50.0f;
    FIRE.solid = false;
    FIRE.localIntersect = true;
    FIRE.emittance = 1.0f;
    OAKWOODSTAIRS.localIntersect = true;
    CHEST.solid = false;
    CHEST.localIntersect = true;
    REDSTONEWIRE.solid = false;
    REDSTONEWIRE.localIntersect = true;
    DIAMONDORE.opaque = true;
    DIAMONDBLOCK.opaque = true;
    DIAMONDBLOCK.specular = 0.04f;
    WORKBENCH.opaque = true;
    WORKBENCH.localIntersect = true;
    CROPS.solid = false;
    CROPS.localIntersect = true;
    SOIL.opaque = true;
    SOIL.localIntersect = true;
    FURNACEUNLIT.opaque = true;
    FURNACEUNLIT.localIntersect = true;
    FURNACELIT.opaque = true;
    FURNACELIT.localIntersect = true;
    SIGNPOST.solid = false;
    SIGNPOST.invisible = true;
    WOODENDOOR.solid = false;
    WOODENDOOR.localIntersect = true;
    LADDER.solid = false;
    LADDER.localIntersect = true;
    MINECARTTRACKS.solid = false;
    MINECARTTRACKS.localIntersect = true;
    STONESTAIRS.localIntersect = true;
    WALLSIGN.solid = false;
    WALLSIGN.invisible = true;
    LEVER.solid = false;
    LEVER.localIntersect = true;
    STONEPRESSUREPLATE.solid = false;
    STONEPRESSUREPLATE.localIntersect = true;
    IRONDOOR.solid = false;
    IRONDOOR.localIntersect = true;
    WOODENPRESSUREPLATE.solid = false;
    WOODENPRESSUREPLATE.localIntersect = true;
    REDSTONEORE.opaque = true;
    GLOWINGREDSTONEORE.opaque = true;
    REDSTONETORCHOFF.solid = false;
    REDSTONETORCHOFF.localIntersect = true;
    REDSTONETORCHON.solid = false;
    REDSTONETORCHON.localIntersect = true;
    REDSTONETORCHON.emittance = 1.0f;
    STONEBUTTON.solid = false;
    STONEBUTTON.localIntersect = true;
    SNOW.solid = false;
    SNOW.localIntersect = true;
    ICE.ior = 1.31f;
    SNOWBLOCK.opaque = true;
    CACTUS.solid = false;
    CACTUS.localIntersect = true;
    CLAY.opaque = true;
    SUGARCANE.solid = false;
    SUGARCANE.localIntersect = true;
    JUKEBOX.opaque = true;
    JUKEBOX.localIntersect = true;
    FENCE.localIntersect = true;
    PUMPKIN.solid = false;
    PUMPKIN.opaque = true;
    PUMPKIN.localIntersect = true;
    NETHERRACK.opaque = true;
    SOULSAND.opaque = true;
    GLOWSTONE.opaque = true;
    GLOWSTONE.emittance = 1.0f;
    PORTAL.solid = false;
    PORTAL.emittance = 0.4f;
    JACKOLANTERN.solid = false;
    JACKOLANTERN.opaque = true;
    JACKOLANTERN.localIntersect = true;
    JACKOLANTERN.emittance = 1.0f;
    CAKE.solid = false;
    CAKE.localIntersect = true;
    REDSTONEREPEATEROFF.solid = false;
    REDSTONEREPEATEROFF.localIntersect = true;
    REDSTONEREPEATERON.solid = false;
    REDSTONEREPEATERON.localIntersect = true;
    STAINED_GLASS.ior = 1.52f;
    TRAPDOOR.solid = false;
    TRAPDOOR.localIntersect = true;
    HIDDENSILVERFISH.opaque = true;
    HIDDENSILVERFISH.localIntersect = true;
    STONEBRICKS.opaque = true;
    STONEBRICKS.localIntersect = true;
    HUGEBROWNMUSHROOM.opaque = true;
    HUGEBROWNMUSHROOM.localIntersect = true;
    HUGEREDMUSHROOM.opaque = true;
    HUGEREDMUSHROOM.localIntersect = true;
    IRONBARS.localIntersect = true;
    GLASSPANE.localIntersect = true;
    GLASSPANE.ior = 1.52f;
    MELON.solid = false;
    MELON.opaque = true;
    MELON.localIntersect = true;
    PUMPKINSTEM.solid = false;
    PUMPKINSTEM.localIntersect = true;
    MELONSTEM.solid = false;
    MELONSTEM.localIntersect = true;
    VINES.solid = false;
    VINES.localIntersect = true;
    FENCEGATE.solid = false;
    FENCEGATE.localIntersect = true;
    BRICKSTAIRS.localIntersect = true;
    STONEBRICKSTAIRS.localIntersect = true;
    MYCELIUM.opaque = true;
    MYCELIUM.localIntersect = true;
    LILY_PAD.solid = false;
    LILY_PAD.invisible = true;
    LILY_PAD.localIntersect = true;
    NETHERBRICK.opaque = true;
    NETHERBRICKFENCE.localIntersect = true;
    NETHERBRICKSTAIRS.localIntersect = true;
    NETHERWART.solid = false;
    NETHERWART.localIntersect = true;
    ENCHNATMENTTABLE.solid = false;
    ENCHNATMENTTABLE.localIntersect = true;
    BREWINGSTAND.solid = false;
    BREWINGSTAND.localIntersect = true;
    CAULDRON.solid = false;
    CAULDRON.localIntersect = true;
    ENDPORTAL.solid = false;
    ENDPORTAL.localIntersect = true;
    ENDPORTALFRAME.solid = false;
    ENDPORTALFRAME.localIntersect = true;
    ENDSTONE.opaque = true;
    ENDSTONE.localIntersect = true;
    DRAGONEGG.solid = false;
    DRAGONEGG.localIntersect = true;
    REDSTONELAMPOFF.opaque = true;
    REDSTONELAMPON.opaque = true;
    REDSTONELAMPON.emittance = 1.0f;
    DOUBLEWOODENSLAB.opaque = true;
    DOUBLEWOODENSLAB.localIntersect = true;
    SINGLEWOODENSLAB.solid = false;
    SINGLEWOODENSLAB.localIntersect = true;
    COCOAPLANT.solid = false;
    COCOAPLANT.localIntersect = true;
    SANDSTONESTAIRS.localIntersect = true;
    EMERALDORE.opaque = true;
    ENDERCHEST.solid = false;
    ENDERCHEST.localIntersect = true;
    TRIPWIREHOOK.solid = false;
    TRIPWIREHOOK.localIntersect = true;
    TRIPWIRE.solid = false;
    TRIPWIRE.localIntersect = true;
    EMERALDBLOCK.opaque = true;
    EMERALDBLOCK.specular = 0.04f;
    SPRUCEWOODSTAIRS.localIntersect = true;
    BIRCHWOODSTAIRS.localIntersect = true;
    JUNGLEWOODSTAIRS.localIntersect = true;
    COMMAND_BLOCK.opaque = true;
    COMMAND_BLOCK.localIntersect = true;
    BEACON.localIntersect = true;
    BEACON.emittance = 1.0f;
    BEACON.ior = 1.52f;
    STONEWALL.localIntersect = true;
    FLOWERPOT.solid = false;
    FLOWERPOT.localIntersect = true;
    CARROTS.solid = false;
    CARROTS.localIntersect = true;
    POTATOES.solid = false;
    POTATOES.localIntersect = true;
    WOODENBUTTON.solid = false;
    WOODENBUTTON.localIntersect = true;
    HEAD.solid = false;
    HEAD.invisible = true;
    ANVIL.solid = false;
    ANVIL.localIntersect = true;
    TRAPPEDCHEST.solid = false;
    TRAPPEDCHEST.localIntersect = true;
    WEIGHTEDPRESSUREPLATELIGHT.solid = false;
    WEIGHTEDPRESSUREPLATELIGHT.localIntersect = true;
    WEIGHTEDPRESSUREPLATEHEAVY.solid = false;
    WEIGHTEDPRESSUREPLATEHEAVY.localIntersect = true;
    COMPARATOR.solid = false;
    COMPARATOR.localIntersect = true;
    COMPARATOR_POWERED.solid = false;
    COMPARATOR_POWERED.localIntersect = true;
    DAYLIGHTSENSOR.solid = false;
    DAYLIGHTSENSOR.localIntersect = true;
    REDSTONEBLOCK.opaque = true;
    NETHERQUARTZORE.opaque = true;
    HOPPER.solid = false;
    HOPPER.localIntersect = true;
    QUARTZ.opaque = true;
    QUARTZ.localIntersect = true;
    QUARTZSTAIRS.localIntersect = true;
    ACTIVATORRAIL.solid = false;
    ACTIVATORRAIL.localIntersect = true;
    DROPPER.opaque = true;
    DROPPER.localIntersect = true;
    STAINED_CLAY.opaque = true;
    STAINED_GLASSPANE.localIntersect = true;
    STAINED_GLASSPANE.ior = 1.52f;
    LEAVES2.localIntersect = true;
    WOOD2.opaque = true;
    WOOD2.localIntersect = true;
    ACACIASTAIRS.localIntersect = true;
    DARKOAKSTAIRS.localIntersect = true;
    BARRIER.solid = false;
    BARRIER.invisible = true;
    IRON_TRAPDOOR.solid = false;
    IRON_TRAPDOOR.localIntersect = true;
    PRISMARINE.opaque = true;
    PRISMARINE.localIntersect = true;
    SEALANTERN.opaque = true;
    SEALANTERN.emittance = 0.5f;
    HAY_BLOCK.opaque = true;
    HAY_BLOCK.localIntersect = true;
    CARPET.solid = false;
    CARPET.localIntersect = true;
    HARDENED_CLAY.opaque = true;
    COAL_BLOCK.opaque = true;
    PACKED_ICE.opaque = true;
    LARGE_FLOWER.solid = false;
    LARGE_FLOWER.localIntersect = true;
    STANDING_BANNER.solid = false;
    STANDING_BANNER.invisible = true; // Rendered as an entity.
    WALL_BANNER.solid = false;
    WALL_BANNER.invisible = true; // Rendered as an entity.
    INVERTED_DAYLIGHTSENSOR.solid = false;
    INVERTED_DAYLIGHTSENSOR.localIntersect = true;
    REDSANDSTONE.opaque = true;
    REDSANDSTONE.localIntersect = true;
    REDSANDSTONESTAIRS.localIntersect = true;
    DOUBLESLAB2.opaque = true;
    DOUBLESLAB2.localIntersect = true;
    SLAB2.solid = false;
    SLAB2.localIntersect = true;
    SPRUCEFENCEGATE.solid = false;
    SPRUCEFENCEGATE.localIntersect = true;
    BIRCHFENCEGATE.solid = false;
    BIRCHFENCEGATE.localIntersect = true;
    JUNGLEFENCEGATE.solid = false;
    JUNGLEFENCEGATE.localIntersect = true;
    DARKOAKFENCEGATE.solid = false;
    DARKOAKFENCEGATE.localIntersect = true;
    ACACIAFENCEGATE.solid = false;
    ACACIAFENCEGATE.localIntersect = true;
    SPRUCEFENCE.localIntersect = true;
    BIRCHFENCE.localIntersect = true;
    JUNGLEFENCE.localIntersect = true;
    DARKOAKFENCE.localIntersect = true;
    ACACIAFENCE.localIntersect = true;
    SPRUCEDOOR.solid = false;
    SPRUCEDOOR.localIntersect = true;
    BIRCHDOOR.solid = false;
    BIRCHDOOR.localIntersect = true;
    JUNGLEDOOR.solid = false;
    JUNGLEDOOR.localIntersect = true;
    ACACIADOOR.solid = false;
    ACACIADOOR.localIntersect = true;
    DARKOAKDOOR.solid = false;
    DARKOAKDOOR.localIntersect = true;
    ENDROD.solid = false;
    ENDROD.localIntersect = true;
    ENDROD.emittance = 1.0f;
    CHORUSPLANT.solid = false;
    CHORUSPLANT.localIntersect = true;
    CHORUSFLOWER.solid = false;
    CHORUSFLOWER.localIntersect = true;
    PURPURBLOCK.opaque = true;
    PURPURPILLAR.opaque = true;
    PURPURPILLAR.localIntersect = true;
    PURPURSTAIRS.localIntersect = true;
    PURPURDOUBLESLAB.opaque = true;
    PURPURSLAB.solid = false;
    PURPURSLAB.localIntersect = true;
    ENDBRICKS.opaque = true;
    BEETROOTS.solid = false;
    BEETROOTS.localIntersect = true;
    GRASSPATH.solid = false;
    GRASSPATH.localIntersect = true;
    END_GATEWAY.solid = false;
    REPEATING_COMMAND_BLOCK.opaque = true;
    REPEATING_COMMAND_BLOCK.localIntersect = true;
    CHAIN_COMMAND_BLOCK.opaque = true;
    CHAIN_COMMAND_BLOCK.localIntersect = true;
    FROSTEDICE.ior = 1.31f;
    MAGMA.opaque = true;
    MAGMA.emittance = 0.6f;
    NETHER_WART_BLOCK.opaque = true;
    RED_NETHER_BRICK.opaque = true;
    BONE.opaque = true;
    BONE.localIntersect = true;
    OBSERVER.opaque = true;
    OBSERVER.localIntersect = true;
    SHULKERBOX_WHITE.opaque = true;
    SHULKERBOX_WHITE.localIntersect = true;
    SHULKERBOX_ORANGE.opaque = true;
    SHULKERBOX_ORANGE.localIntersect = true;
    SHULKERBOX_MAGENTA.opaque = true;
    SHULKERBOX_MAGENTA.localIntersect = true;
    SHULKERBOX_LIGHTBLUE.opaque = true;
    SHULKERBOX_LIGHTBLUE.localIntersect = true;
    SHULKERBOX_YELLOW.opaque = true;
    SHULKERBOX_YELLOW.localIntersect = true;
    SHULKERBOX_LIME.opaque = true;
    SHULKERBOX_LIME.localIntersect = true;
    SHULKERBOX_PINK.opaque = true;
    SHULKERBOX_PINK.localIntersect = true;
    SHULKERBOX_GRAY.opaque = true;
    SHULKERBOX_GRAY.localIntersect = true;
    SHULKERBOX_SILVER.opaque = true;
    SHULKERBOX_SILVER.localIntersect = true;
    SHULKERBOX_CYAN.opaque = true;
    SHULKERBOX_CYAN.localIntersect = true;
    SHULKERBOX_PURPLE.opaque = true;
    SHULKERBOX_PURPLE.localIntersect = true;
    SHULKERBOX_BLUE.opaque = true;
    SHULKERBOX_BLUE.localIntersect = true;
    SHULKERBOX_BROWN.opaque = true;
    SHULKERBOX_BROWN.localIntersect = true;
    SHULKERBOX_GREEN.opaque = true;
    SHULKERBOX_GREEN.localIntersect = true;
    SHULKERBOX_RED.opaque = true;
    SHULKERBOX_RED.localIntersect = true;
    SHULKERBOX_BLACK.opaque = true;
    SHULKERBOX_BLACK.localIntersect = true;
    WHITE_TERRACOTTA.opaque = true;
    WHITE_TERRACOTTA.localIntersect = true;
    ORANGE_TERRACOTTA.opaque = true;
    ORANGE_TERRACOTTA.localIntersect = true;
    MAGENTA_TERRACOTTA.opaque = true;
    MAGENTA_TERRACOTTA.localIntersect = true;
    LIGHT_BLUE_TERRACOTTA.opaque = true;
    LIGHT_BLUE_TERRACOTTA.localIntersect = true;
    YELLOW_TERRACOTTA.opaque = true;
    YELLOW_TERRACOTTA.localIntersect = true;
    LIME_TERRACOTTA.opaque = true;
    LIME_TERRACOTTA.localIntersect = true;
    PINK_TERRACOTTA.opaque = true;
    PINK_TERRACOTTA.localIntersect = true;
    GRAY_TERRACOTTA.opaque = true;
    GRAY_TERRACOTTA.localIntersect = true;
    SILVER_TERRACOTTA.opaque = true;
    SILVER_TERRACOTTA.localIntersect = true;
    CYAN_TERRACOTTA.opaque = true;
    CYAN_TERRACOTTA.localIntersect = true;
    PURPLE_TERRACOTTA.opaque = true;
    PURPLE_TERRACOTTA.localIntersect = true;
    BLUE_TERRACOTTA.opaque = true;
    BLUE_TERRACOTTA.localIntersect = true;
    BROWN_TERRACOTTA.opaque = true;
    BROWN_TERRACOTTA.localIntersect = true;
    GREEN_TERRACOTTA.opaque = true;
    GREEN_TERRACOTTA.localIntersect = true;
    RED_TERRACOTTA.opaque = true;
    RED_TERRACOTTA.localIntersect = true;
    BLACK_TERRACOTTA.opaque = true;
    BLACK_TERRACOTTA.localIntersect = true;
    CONCRETE.opaque = true;
    CONCRETE_POWDER.opaque = true;
    STRUCTURE_BLOCK.solid = false;

    // TODO: render these:
    END_GATEWAY.invisible = UNKNOWN_INVISIBLE;
    STRUCTURE_BLOCK.invisible = UNKNOWN_INVISIBLE; // TODO: render this.

    // Unknown blocks.
    UNKNOWN0xFD.invisible = UNKNOWN_INVISIBLE;
    UNKNOWN0xFE.invisible = UNKNOWN_INVISIBLE;
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

  public boolean isNetherBrickFenceConnector(int data, int direction) {
    return solid || this == NETHERBRICKFENCE || isFenceGate();
  }

  public boolean isFenceConnector(int data, int direction) {
    return solid || isFence() || isFenceGate();
  }

  protected boolean isFence() {
    return false;
  }

  public boolean isFenceGate() {
    return false;
  }

  public boolean isStoneWallConnector(int data, int direction) {
    return solid || this == FENCEGATE || this == STONEWALL;
  }

  public boolean isWallTopConnector() {
    return solid || isWater() || isLava();
  }

  public boolean isGlassPaneConnector(int data, int direction) {
    return solid || this == GLASSPANE || this == STAINED_GLASSPANE;
  }

  public boolean isIronBarsConnector(int data, int direction) {
    return solid || this == IRONBARS;
  }

  public boolean isChorusPlant() {
    return this == CHORUSPLANT || this == CHORUSFLOWER;
  }

  public boolean isRedstoneWireConnector() {
    return redstoneConnectors.contains(this);
  }

  public boolean isCave() {
    return !solid && !this.isWater();
  }

  public boolean isStair() {
    return this == OAKWOODSTAIRS || this == STONESTAIRS || this == BRICKSTAIRS
        || this == STONEBRICKSTAIRS || this == NETHERBRICKSTAIRS || this == SANDSTONESTAIRS
        || this == SPRUCEWOODSTAIRS || this == BIRCHWOODSTAIRS || this == JUNGLEWOODSTAIRS
        || this == QUARTZSTAIRS || this == ACACIASTAIRS || this == DARKOAKSTAIRS
        || this == REDSANDSTONESTAIRS || this == PURPURSTAIRS;
  }

  public boolean isGroundBlock() {
    return id != IdBlock.AIR_ID &&
        id != IdBlock.LEAVES_ID &&
        id != IdBlock.LEAVES2_ID &&
        id != IdBlock.WOOD_ID &&
        id != IdBlock.WOOD2_ID;
  }

  public static IdBlock get(int id) {
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
  public static void set(int id, IdBlock newBlock) {
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

  public Block asNewBlock() {
    return null;
  }

  @Override public JsonValue toJson() {
    return new JsonString(name);
  }

}
