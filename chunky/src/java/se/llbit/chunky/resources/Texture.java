/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
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

import javafx.scene.image.Image;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.texturepack.FontTexture;
import se.llbit.chunky.resources.texturepack.TexturePath;
import se.llbit.fxutil.FxImageUtil;
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;
import se.llbit.resources.ImageLoader;
import se.llbit.util.annotation.NotNull;

/**
 * This class contains static fields for common textures.
 *
 * <p>The Texture type is used for all textures used in the renderer.
 * It mostly serves as a data object wrapping a BitmapImage.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Texture {

  public static final Texture EMPTY_TEXTURE = new Texture() {
    @Override public void getColor(double u, double v, Vector4 c) {
      c.set(0, 0, 0, 0);
    }

    @Override public void getColorInterpolated(double u, double v, Vector4 c) {
      c.set(0, 0, 0, 0);
    }

    @Override public boolean isEmptyTexture() {
      return true;
    }
  };

  public static Texture black = new SolidColorTexture(new Vector4(0, 0, 0, 1));

  public static final Texture paintingAlban = new Texture();
  public static final Texture paintingAztec2 = new Texture();
  public static final Texture paintingAztec = new Texture();
  public static final Texture paintingBack = new Texture();
  public static final Texture paintingBomb = new Texture();
  public static final Texture paintingBurningSkull = new Texture();
  public static final Texture paintingBust = new Texture();
  public static final Texture paintingCourbet = new Texture();
  public static final Texture paintingCreebet = new Texture();
  public static final Texture paintingDonkeyKong = new Texture();
  public static final Texture paintingFighters = new Texture();
  public static final Texture paintingGraham = new Texture();
  public static final Texture paintingKebab = new Texture();
  public static final Texture paintingMatch = new Texture();
  public static final Texture paintingPigscene = new Texture();
  public static final Texture paintingPlant = new Texture();
  public static final Texture paintingPointer = new Texture();
  public static final Texture paintingPool = new Texture();
  public static final Texture paintingSea = new Texture();
  public static final Texture paintingSkeleton = new Texture();
  public static final Texture paintingSkullAndRoses = new Texture();
  public static final Texture paintingStage = new Texture();
  public static final Texture paintingSunset = new Texture();
  public static final Texture paintingVoid = new Texture();
  public static final Texture paintingWanderer = new Texture();
  public static final Texture paintingWasteland = new Texture();
  public static final Texture paintingWither = new Texture();

  public static final FontTexture fonts = new FontTexture();

  public static final Texture air = new Texture("air");
  public static final Texture stone = new Texture("stone");
  public static final Texture prismarine = new Texture();
  public static final Texture prismarineBricks = new Texture();
  public static final Texture darkPrismarine = new Texture();
  public static final Texture granite = new Texture();
  public static final Texture smoothGranite = new Texture();
  public static final Texture diorite = new Texture();
  public static final Texture smoothDiorite = new Texture();
  public static final Texture andesite = new Texture();
  public static final Texture smoothAndesite = new Texture();
  public static final Texture dirt = new Texture("dirt");
  public static final Texture coarseDirt = new Texture("dirt");
  public static final Texture grassSideSaturated = new Texture("grass-side-saturated");
  public static final Texture grassTop = new Texture("grass");
  public static final Texture grassSide = new Texture();
  public static final Texture water = new Texture("water");
  public static final Texture cauldronSide = new Texture();
  public static final Texture cauldronInside = new Texture();
  public static final Texture cauldronTop = new Texture();
  public static final Texture cauldronBottom = new Texture();
  public static final Texture smoothStone = new Texture();
  public static final Texture smoothStoneSlabSide = new Texture();
  public static final Texture brick = new Texture("bricks");
  public static final Texture tntTop = new Texture();
  public static final Texture tntSide = new Texture("tnt");
  public static final Texture tntBottom = new Texture();
  public static final Texture cobweb = new Texture("cobweb");
  public static final Texture portal = new Texture("nether-portal");
  public static final Texture cobblestone = new Texture("cobblestone");
  public static final Texture bedrock = new Texture("bedrock");
  public static final Texture sand = new Texture("sand");
  public static final Texture gravel = new Texture("gravel");
  public static final Texture ironBlock = new Texture("iron-block");
  public static final Texture goldBlock = new Texture("gold-block");
  public static final Texture diamondBlock = new Texture("diamond-block");
  public static final Texture chestTop = new Texture();
  public static final Texture chestBottom = new Texture();
  public static final Texture chestLeft = new Texture();
  public static final Texture chestRight = new Texture();
  public static final Texture chestFront = new Texture("chest");
  public static final Texture chestBack = new Texture();
  public static final Texture chestLock = new Texture();
  public static final Texture enderChestTop = new Texture();
  public static final Texture enderChestBottom = new Texture();
  public static final Texture enderChestLeft = new Texture();
  public static final Texture enderChestRight = new Texture();
  public static final Texture enderChestFront = new Texture();
  public static final Texture enderChestBack = new Texture();
  public static final Texture enderChestLock = new Texture();
  public static final Texture redMushroom = new Texture("red-mushroom");
  public static final Texture brownMushroom = new Texture("brown-mushroom");
  public static final Texture goldOre = new Texture("gold-ore");
  public static final Texture ironOre = new Texture("iron-ore");
  public static final Texture coalOre = new Texture("coal-ore");
  public static final Texture netherQuartzOre = new Texture();
  public static final Texture bookshelf = new Texture("bookshelf");
  public static final Texture mossStone = new Texture("moss-stone");
  public static final Texture obsidian = new Texture("obsidian");
  public static final Texture workbenchTop = new Texture();
  public static final Texture workbenchSide = new Texture("workbench");
  public static final Texture workbenchFront = new Texture("workbench");
  public static final Texture furnaceTop = new Texture();
  public static final Texture furnaceSide = new Texture();
  public static final Texture furnaceUnlitFront = new Texture("furnace");
  public static final Texture furnaceLitFront = new Texture("furnace-lit");
  public static final Texture dispenserFront = new Texture("dispenser");
  public static final Texture dispenserFrontVertical = new Texture("dispenser");
  public static final Texture dropperFront = new Texture();
  public static final Texture dropperFrontVertical = new Texture();
  public static final Texture sponge = new Texture("sponge");
  public static final Texture wetSponge = new Texture("sponge");
  public static final Texture glass = new Texture("glass");
  public static final Texture diamondOre = new Texture("diamond-ore");
  public static final Texture redstoneOre = new Texture("redstone-ore");
  public static final Texture stoneBrick = new Texture("stone-bricks");
  public static final Texture mossyStoneBrick = new Texture();
  public static final Texture crackedStoneBrick = new Texture();
  public static final Texture circleStoneBrick = new Texture();
  public static final Texture monsterSpawner = new Texture("spawner");
  public static final Texture snowBlock = new Texture("snow");
  public static final Texture snowSide = new Texture();
  public static final Texture ice = new Texture("ice");
  public static final Texture cactusTop = new Texture();
  public static final Texture cactusSide = new Texture();
  public static final Texture cactusBottom = new Texture();
  public static final Texture clay = new Texture("clay");
  public static final Texture sugarCane = new Texture("sugar-canes");
  public static final Texture jukeboxSide = new Texture();
  public static final Texture jukeboxTop = new Texture();
  public static final Texture noteBlock = new Texture();
  public static final Texture torch = new Texture("torch");
  public static final Texture oakDoorTop = new Texture();
  public static final Texture oakDoorBottom = new Texture();
  public static final Texture ironDoorTop = new Texture();
  public static final Texture ironDoorBottom = new Texture();
  public static final Texture ladder = new Texture("ladder");
  public static final Texture trapdoor = new Texture("trapdoor");
  public static final Texture ironTrapdoor = new Texture("trapdoor");
  public static final Texture birchTrapdoor = new Texture("trapdoor");
  public static final Texture spruceTrapdoor = new Texture("trapdoor");
  public static final Texture jungleTrapdoor = new Texture("trapdoor");
  public static final Texture acaciaTrapdoor = new Texture("trapdoor");
  public static final Texture darkOakTrapdoor = new Texture("trapdoor");
  public static final Texture ironBars = new Texture("iron-bars");
  public static final Texture farmlandWet = new Texture();
  public static final Texture farmlandDry = new Texture();
  public static final Texture lever = new Texture();
  public static final Texture redstoneTorchOn = new Texture("redstone-torch-on");
  public static final Texture redstoneTorchOff = new Texture("redstone-torch-off");
  public static final Texture redstoneWireCross = new Texture("redstone-wire-off-intersect");
  public static final Texture redstoneWire = new Texture();
  public static final Texture pumpkinTop = new Texture();
  public static final Texture pumpkinSide = new Texture();
  public static final Texture pumpkinFront = new Texture();
  public static final Texture jackolanternFront = new Texture();
  public static final Texture netherrack = new Texture("netherrack");
  public static final Texture soulsand = new Texture("soul-sand");
  public static final Texture glowstone = new Texture("glowstone");
  public static final Texture seaLantern = new Texture();
  public static final Texture sandstoneSide = new Texture("sandstone");
  public static final Texture sandstoneTop = new Texture();
  public static final Texture sandstoneBottom = new Texture();
  public static final Texture sandstoneDecorated = new Texture();
  public static final Texture sandstoneCut = new Texture();
  public static final Texture redSandstoneSide = new Texture();
  public static final Texture redSandstoneTop = new Texture();
  public static final Texture redSandstoneBottom = new Texture();
  public static final Texture redSandstoneDecorated = new Texture();
  public static final Texture redSandstoneCut = new Texture();
  public static final Texture deadBush = new Texture("dead-bush");
  public static final Texture tallGrass = new Texture("tall-grass");
  public static final Texture fern = new Texture();
  public static final Texture vines = new Texture("vines");
  public static final Texture crops0 = new Texture();
  public static final Texture crops1 = new Texture();
  public static final Texture crops2 = new Texture();
  public static final Texture crops3 = new Texture();
  public static final Texture crops4 = new Texture();
  public static final Texture crops5 = new Texture();
  public static final Texture crops6 = new Texture();
  public static final Texture crops7 = new Texture();
  public static final Texture rails = new Texture("minecart-track");
  public static final Texture railsCurved = new Texture();
  public static final Texture poweredRailOn = new Texture();
  public static final Texture poweredRailOff = new Texture();
  public static final Texture detectorRail = new Texture();
  public static final Texture detectorRailOn = new Texture();
  public static final Texture activatorRail = new Texture();
  public static final Texture activatorRailPowered = new Texture();
  public static final Texture whiteWool = new Texture("wool");
  public static final Texture orangeWool = new Texture("wool");
  public static final Texture magentaWool = new Texture("wool");
  public static final Texture lightBlueWool = new Texture("wool");
  public static final Texture yellowWool = new Texture("wool");
  public static final Texture limeWool = new Texture("wool");
  public static final Texture pinkWool = new Texture("wool");
  public static final Texture grayWool = new Texture("wool");
  public static final Texture lightGrayWool = new Texture("wool");
  public static final Texture cyanWool = new Texture("wool");
  public static final Texture purpleWool = new Texture("wool");
  public static final Texture blueWool = new Texture("wool");
  public static final Texture brownWool = new Texture("wool");
  public static final Texture greenWool = new Texture("wool");
  public static final Texture redWool = new Texture("wool");
  public static final Texture blackWool = new Texture("wool");
  public static final Texture lava = new Texture("lava");
  public static final Texture lapisOre = new Texture("lapis-lazuli-ore");
  public static final Texture lapisBlock = new Texture("lapis-lazuli-block");
  public static final Texture pistonSide = new Texture("piston");
  public static final Texture pistonInnerTop = new Texture();
  public static final Texture pistonBottom = new Texture();
  public static final Texture pistonTop = new Texture("piston-extension");
  public static final Texture pistonTopSticky = new Texture();
  public static final Texture fire = new Texture("fire");
  public static final AnimatedTexture fireLayer0 = new AnimatedTexture("fire");
  public static final AnimatedTexture fireLayer1 = new AnimatedTexture("fire");
  public static final Texture redstoneRepeaterOn = new Texture("redstone-repeater-off");
  public static final Texture redstoneRepeaterOff = new Texture("redstone-repeater-on");
  public static final Texture redstoneLampOn = new Texture("redstone-lamp-off");
  public static final Texture redstoneLampOff = new Texture("redstone-lamp-on");
  public static final Texture endPortalFrameTop = new Texture();
  public static final Texture endPortalFrameSide = new Texture();
  public static final Texture endPortal = new Texture("end-portal");
  public static final Texture endStone = new Texture("end-stone");
  public static final Texture brewingStandSide = new Texture();
  public static final Texture brewingStandBase = new Texture();
  public static final Texture cakeTop = new Texture();
  public static final Texture cakeSide = new Texture();
  public static final Texture cakeInside = new Texture();
  public static final Texture cakeBottom = new Texture();
  public static final Texture hugeBrownMushroom = new Texture();
  public static final Texture hugeRedMushroom = new Texture();
  public static final Texture mushroomStem = new Texture();
  public static final Texture mushroomPores = new Texture();
  public static final Texture melonSide = new Texture();
  public static final Texture melonTop = new Texture();
  public static final Texture stemStraight = new Texture();
  public static final Texture stemBent = new Texture();
  public static final Texture myceliumTop = new Texture();
  public static final Texture myceliumSide = new Texture();
  public static final Texture lilyPad = new Texture("lily-pad");
  public static final Texture netherBrick = new Texture();
  public static final Texture netherWart0 = new Texture();
  public static final Texture netherWart1 = new Texture();
  public static final Texture netherWart2 = new Texture();
  public static final Texture largeChestFrontLeft = new Texture();
  public static final Texture largeChestFrontRight = new Texture();
  public static final Texture largeChestBackLeft = new Texture();
  public static final Texture largeChestBackRight = new Texture();
  public static final Texture largeChestTopLeft = new Texture();
  public static final Texture largeChestTopRight = new Texture();
  public static final Texture largeChestBottomLeft = new Texture();
  public static final Texture largeChestBottomRight = new Texture();
  public static final Texture largeChestLeft = new Texture();
  public static final Texture largeChestRight = new Texture();
  public static final Texture enchantmentTableSide = new Texture();
  public static final Texture enchantmentTableTop = new Texture();
  public static final Texture enchantmentTableBottom = new Texture();

  public static final Texture commandBlockBack = new Texture();
  public static final Texture commandBlockFront = new Texture();
  public static final Texture commandBlockSide = new Texture();
  public static final Texture commandBlockConditional = new Texture();

  public static final Texture repeatingCommandBlockBack = new Texture();
  public static final Texture repeatingCommandBlockFront = new Texture();
  public static final Texture repeatingCommandBlockSide = new Texture();
  public static final Texture repeatingCommandBlockConditional = new Texture();

  public static final Texture chainCommandBlockBack = new Texture();
  public static final Texture chainCommandBlockFront = new Texture();
  public static final Texture chainCommandBlockSide = new Texture();
  public static final Texture chainCommandBlockConditional = new Texture();

  public static final Texture eyeOfTheEnder = new Texture();
  public static final Texture dragonEgg = new Texture();
  public static final Texture cocoaPlantSmall = new Texture();
  public static final Texture cocoaPlantMedium = new Texture();
  public static final Texture cocoaPlantLarge = new Texture();
  public static final Texture emeraldOre = new Texture();
  public static final Texture emeraldBlock = new Texture();
  public static final Texture redstoneBlock = new Texture();
  public static final Texture tripwireHook = new Texture();
  public static final Texture tripwire = new Texture();
  public static final Texture carrots0 = new Texture();
  public static final Texture carrots1 = new Texture();
  public static final Texture carrots2 = new Texture();
  public static final Texture carrots3 = new Texture();
  public static final Texture potatoes0 = new Texture();
  public static final Texture potatoes1 = new Texture();
  public static final Texture potatoes2 = new Texture();
  public static final Texture potatoes3 = new Texture();
  public static final Texture beacon = new Texture();
  public static final Texture beaconBeam = new Texture();
  public static final Texture anvilSide = new Texture();
  public static final Texture anvilTop = new Texture();
  public static final Texture anvilTopDamaged1 = new Texture();
  public static final Texture anvilTopDamaged2 = new Texture();
  public static final Texture flowerPot = new Texture();
  public static final Texture quartzSide = new Texture();
  public static final Texture quartzTop = new Texture();
  public static final Texture quartzBottom = new Texture();
  public static final Texture quartzChiseled = new Texture();
  public static final Texture quartzChiseledTop = new Texture();
  public static final Texture quartzPillar = new Texture();
  public static final Texture quartzPillarTop = new Texture();
  public static final Texture daylightDetectorTop = new Texture();
  public static final Texture daylightDetectorInvertedTop = new Texture();
  public static final Texture daylightDetectorSide = new Texture();
  public static final Texture comparatorOff = new Texture();
  public static final Texture comparatorOn = new Texture();
  public static final Texture hopperOutside = new Texture();
  public static final Texture hopperInside = new Texture();
  public static final Texture hopperTop = new Texture();
  public static final Texture slime = new Texture();

  // [1.6] Hay Block, Hardened Clay, Coal Block
  public static final Texture hayBlockSide = new Texture();
  public static final Texture hayBlockTop = new Texture();
  public static final Texture hardenedClay = new Texture();
  public static final Texture coalBlock = new Texture();

  // [1.6] Colored Clay
  public static final Texture whiteClay = new Texture();
  public static final Texture orangeClay = new Texture();
  public static final Texture magentaClay = new Texture();
  public static final Texture lightBlueClay = new Texture();
  public static final Texture yellowClay = new Texture();
  public static final Texture limeClay = new Texture();
  public static final Texture pinkClay = new Texture();
  public static final Texture grayClay = new Texture();
  public static final Texture lightGrayClay = new Texture();
  public static final Texture cyanClay = new Texture();
  public static final Texture purpleClay = new Texture();
  public static final Texture blueClay = new Texture();
  public static final Texture brownClay = new Texture();
  public static final Texture greenClay = new Texture();
  public static final Texture redClay = new Texture();
  public static final Texture blackClay = new Texture();

  // [1.7.2] Stained Glass
  public static final Texture whiteGlass = new Texture("glass");
  public static final Texture orangeGlass = new Texture("glass");
  public static final Texture magentaGlass = new Texture("glass");
  public static final Texture lightBlueGlass = new Texture("glass");
  public static final Texture yellowGlass = new Texture("glass");
  public static final Texture limeGlass = new Texture("glass");
  public static final Texture pinkGlass = new Texture("glass");
  public static final Texture grayGlass = new Texture("glass");
  public static final Texture lightGrayGlass = new Texture("glass");
  public static final Texture cyanGlass = new Texture("glass");
  public static final Texture purpleGlass = new Texture("glass");
  public static final Texture blueGlass = new Texture("glass");
  public static final Texture brownGlass = new Texture("glass");
  public static final Texture greenGlass = new Texture("glass");
  public static final Texture redGlass = new Texture("glass");
  public static final Texture blackGlass = new Texture("glass");

  // [1.7.2] Podzol
  public static final Texture podzolTop = new Texture();
  public static final Texture podzolSide = new Texture();

  // [1.7.2] Flowers
  public static final Texture dandelion = new Texture("yellow-flower");
  public static final Texture poppy = new Texture("rose");
  public static final Texture blueOrchid = new Texture();
  public static final Texture allium = new Texture();
  public static final Texture azureBluet = new Texture();
  public static final Texture redTulip = new Texture();
  public static final Texture orangeTulip = new Texture();
  public static final Texture whiteTulip = new Texture();
  public static final Texture pinkTulip = new Texture();
  public static final Texture oxeyeDaisy = new Texture();

  // [1.7.2] Large Flowers
  public static final Texture sunflowerBottom = new Texture();
  public static final Texture sunflowerTop = new Texture();
  public static final Texture sunflowerFront = new Texture();
  public static final Texture sunflowerBack = new Texture();
  public static final Texture lilacBottom = new Texture();
  public static final Texture lilacTop = new Texture();
  public static final Texture doubleTallGrassBottom = new Texture();
  public static final Texture doubleTallGrassTop = new Texture();
  public static final Texture largeFernBottom = new Texture();
  public static final Texture largeFernTop = new Texture();
  public static final Texture roseBushBottom = new Texture();
  public static final Texture roseBushTop = new Texture();
  public static final Texture peonyBottom = new Texture();
  public static final Texture peonyTop = new Texture();

  // [1.7.2] Colored Glass Panes
  public static final Texture glassPaneTop = new Texture();
  public static final Texture whiteGlassPaneSide = new Texture();
  public static final Texture orangeGlassPaneSide = new Texture();
  public static final Texture magentaGlassPaneSide = new Texture();
  public static final Texture lightBlueGlassPaneSide = new Texture();
  public static final Texture yellowGlassPaneSide = new Texture();
  public static final Texture limeGlassPaneSide = new Texture();
  public static final Texture pinkGlassPaneSide = new Texture();
  public static final Texture grayGlassPaneSide = new Texture();
  public static final Texture lightGrayGlassPaneSide = new Texture();
  public static final Texture cyanGlassPaneSide = new Texture();
  public static final Texture purpleGlassPaneSide = new Texture();
  public static final Texture blueGlassPaneSide = new Texture();
  public static final Texture brownGlassPaneSide = new Texture();
  public static final Texture greenGlassPaneSide = new Texture();
  public static final Texture redGlassPaneSide = new Texture();
  public static final Texture blackGlassPaneSide = new Texture();

  // Minecraft 1.9 blocks.
  public static final Texture grassPathSide = new Texture();
  public static final Texture grassPathTop = new Texture();
  public static final Texture endBricks = new Texture();
  public static final Texture purpurBlock = new Texture();
  public static final Texture purpurPillarTop = new Texture();
  public static final Texture purpurPillarSide = new Texture();
  public static final Texture chorusFlower = new Texture();
  public static final Texture chorusFlowerDead = new Texture();
  public static final Texture chorusPlant = new Texture();
  public static final Texture endRod = new Texture();

  // [1.11] Shulker boxes.
  public static final ShulkerTexture shulker = new ShulkerTexture();
  public static final ShulkerTexture shulkerBlack = new ShulkerTexture();
  public static final ShulkerTexture shulkerBlue = new ShulkerTexture();
  public static final ShulkerTexture shulkerBrown = new ShulkerTexture();
  public static final ShulkerTexture shulkerCyan = new ShulkerTexture();
  public static final ShulkerTexture shulkerGray = new ShulkerTexture();
  public static final ShulkerTexture shulkerGreen = new ShulkerTexture();
  public static final ShulkerTexture shulkerLightBlue = new ShulkerTexture();
  public static final ShulkerTexture shulkerLime = new ShulkerTexture();
  public static final ShulkerTexture shulkerMagenta = new ShulkerTexture();
  public static final ShulkerTexture shulkerOrange = new ShulkerTexture();
  public static final ShulkerTexture shulkerPink = new ShulkerTexture();
  public static final ShulkerTexture shulkerPurple = new ShulkerTexture();
  public static final ShulkerTexture shulkerRed = new ShulkerTexture();
  public static final ShulkerTexture shulkerSilver = new ShulkerTexture();
  public static final ShulkerTexture shulkerWhite = new ShulkerTexture();
  public static final ShulkerTexture shulkerYellow = new ShulkerTexture();

  // [1.11] Observer block.
  public static final Texture observerBack = new Texture();
  public static final Texture observerBackOn = new Texture();
  public static final Texture observerFront = new Texture();
  public static final Texture observerSide = new Texture();
  public static final Texture observerTop = new Texture();

  // Trapped Chest.
  public static final Texture trappedChestTop = new Texture();
  public static final Texture trappedChestBottom = new Texture();
  public static final Texture trappedChestLeft = new Texture();
  public static final Texture trappedChestRight = new Texture();
  public static final Texture trappedChestFront = new Texture("chest");
  public static final Texture trappedChestBack = new Texture();
  public static final Texture trappedChestLock = new Texture();

  public static final Texture largeTrappedChestFrontLeft = new Texture();
  public static final Texture largeTrappedChestFrontRight = new Texture();
  public static final Texture largeTrappedChestBackLeft = new Texture();
  public static final Texture largeTrappedChestBackRight = new Texture();
  public static final Texture largeTrappedChestTopLeft = new Texture();
  public static final Texture largeTrappedChestTopRight = new Texture();
  public static final Texture largeTrappedChestBottomLeft = new Texture();
  public static final Texture largeTrappedChestBottomRight = new Texture();
  public static final Texture largeTrappedChestLeft = new Texture();
  public static final Texture largeTrappedChestRight = new Texture();

  // Entity textures.
  public static final PlayerTexture alex = new PlayerTexture();
  public static final PlayerTexture steve = new PlayerTexture();
  public static final EntityTexture zombie = new EntityTexture();
  public static final EntityTexture creeper = new EntityTexture();
  public static final EntityTexture skeleton = new EntityTexture();
  public static final EntityTexture wither = new EntityTexture();
  public static final EntityTexture dragon = new EntityTexture();
  public static final EntityTexture book = new EntityTexture();

  // [1.10] Bone, magma, nether wart block, red nether brick.
  public static final Texture boneSide = new Texture();
  public static final Texture boneTop = new Texture();
  public static final Texture magma = new Texture();
  public static final Texture netherWartBlock = new Texture();
  public static final Texture redNetherBrick = new Texture();

  // [1.12] Glazed Terracotta:
  public static final Texture terracottaBlack = new Texture();
  public static final Texture terracottaBlue = new Texture();
  public static final Texture terracottaBrown = new Texture();
  public static final Texture terracottaCyan = new Texture();
  public static final Texture terracottaGray = new Texture();
  public static final Texture terracottaGreen = new Texture();
  public static final Texture terracottaLightBlue = new Texture();
  public static final Texture terracottaLime = new Texture();
  public static final Texture terracottaMagenta = new Texture();
  public static final Texture terracottaOrange = new Texture();
  public static final Texture terracottaPink = new Texture();
  public static final Texture terracottaPurple = new Texture();
  public static final Texture terracottaRed = new Texture();
  public static final Texture terracottaSilver = new Texture();
  public static final Texture terracottaWhite = new Texture();
  public static final Texture terracottaYellow = new Texture();

  // [1.12] Concrete:
  public static final Texture concreteBlack = new Texture();
  public static final Texture concreteBlue = new Texture();
  public static final Texture concreteBrown = new Texture();
  public static final Texture concreteCyan = new Texture();
  public static final Texture concreteGray = new Texture();
  public static final Texture concreteGreen = new Texture();
  public static final Texture concreteLightBlue = new Texture();
  public static final Texture concreteLime = new Texture();
  public static final Texture concreteMagenta = new Texture();
  public static final Texture concreteOrange = new Texture();
  public static final Texture concretePink = new Texture();
  public static final Texture concretePurple = new Texture();
  public static final Texture concreteRed = new Texture();
  public static final Texture concreteSilver = new Texture();
  public static final Texture concreteWhite = new Texture();
  public static final Texture concreteYellow = new Texture();

  public static final Texture concretePowderBlack = new Texture();
  public static final Texture concretePowderBlue = new Texture();
  public static final Texture concretePowderBrown = new Texture();
  public static final Texture concretePowderCyan = new Texture();
  public static final Texture concretePowderGray = new Texture();
  public static final Texture concretePowderGreen = new Texture();
  public static final Texture concretePowderLightBlue = new Texture();
  public static final Texture concretePowderLime = new Texture();
  public static final Texture concretePowderMagenta = new Texture();
  public static final Texture concretePowderOrange = new Texture();
  public static final Texture concretePowderPink = new Texture();
  public static final Texture concretePowderPurple = new Texture();
  public static final Texture concretePowderRed = new Texture();
  public static final Texture concretePowderSilver = new Texture();
  public static final Texture concretePowderWhite = new Texture();
  public static final Texture concretePowderYellow = new Texture();

  // [1.12] Beetroots:
  public static final Texture beets0 = new Texture();
  public static final Texture beets1 = new Texture();
  public static final Texture beets2 = new Texture();
  public static final Texture beets3 = new Texture();

  /**
   * Missing or unknown texture.
   */
  public static final Texture unknown = new Texture("unknown");

  public static final Texture oakSignPost = new Texture();
  public static final Texture packedIce = new Texture();
  public static final Texture redSand = new Texture();

  // Tree variants.
  public static final Texture oakWoodTop = new Texture("wood-top");
  public static final Texture spruceWoodTop = new Texture("wood-top");
  public static final Texture birchWoodTop = new Texture("wood-top");
  public static final Texture jungleTreeTop = new Texture("wood-top");
  public static final Texture acaciaWoodTop = new Texture("wood-top");
  public static final Texture darkOakWoodTop = new Texture("wood-top");

  public static final Texture oakLeaves = new Texture("leaves");
  public static final Texture spruceLeaves = new Texture("leaves");
  public static final Texture birchLeaves = new Texture("leaves");
  public static final Texture jungleTreeLeaves = new Texture("leaves");
  public static final Texture acaciaLeaves = new Texture("leaves");
  public static final Texture darkOakLeaves = new Texture("leaves");

  public static final Texture oakSapling = new Texture("sapling");
  public static final Texture spruceSapling = new Texture("sapling");
  public static final Texture birchSapling = new Texture("sapling");
  public static final Texture jungleSapling = new Texture("sapling");
  public static final Texture acaciaSapling = new Texture("sapling");
  public static final Texture darkOakSapling = new Texture("sapling");

  public static final Texture oakPlanks = new Texture("wooden-planks");
  public static final Texture sprucePlanks = new Texture("wooden-planks");
  public static final Texture birchPlanks = new Texture("wooden-planks");
  public static final Texture jungleTreePlanks = new Texture("wooden-planks");
  public static final Texture acaciaPlanks = new Texture("wooden-planks");
  public static final Texture darkOakPlanks = new Texture("wooden-planks");

  public static final Texture oakWood = new Texture("wood");
  public static final Texture spruceWood = new Texture("wood");
  public static final Texture birchWood = new Texture("wood");
  public static final Texture jungleWood = new Texture("wood");
  public static final Texture acaciaWood = new Texture("wood");
  public static final Texture darkOakWood = new Texture("wood");

  public static final Texture frostedIce0 = new Texture();
  public static final Texture frostedIce1 = new Texture();
  public static final Texture frostedIce2 = new Texture();
  public static final Texture frostedIce3 = new Texture();

  public static final Texture spruceDoorTop = new Texture();
  public static final Texture birchDoorTop = new Texture();
  public static final Texture jungleDoorTop = new Texture();
  public static final Texture acaciaDoorTop = new Texture();
  public static final Texture darkOakDoorTop = new Texture();

  public static final Texture spruceDoorBottom = new Texture();
  public static final Texture birchDoorBottom = new Texture();
  public static final Texture jungleDoorBottom = new Texture();
  public static final Texture acaciaDoorBottom = new Texture();
  public static final Texture darkOakDoorBottom = new Texture();

  public static final Texture strippedOakLog = new Texture();
  public static final Texture strippedOakLogTop = new Texture();

  public static final Texture strippedDarkOakLog = new Texture();
  public static final Texture strippedDarkOakLogTop = new Texture();

  public static final Texture strippedSpruceLog = new Texture();
  public static final Texture strippedSpruceLogTop = new Texture();

  public static final Texture strippedBirchLog = new Texture();
  public static final Texture strippedBirchLogTop = new Texture();

  public static final Texture strippedJungleLog = new Texture();
  public static final Texture strippedJungleLogTop = new Texture();

  public static final Texture strippedAcaciaLog = new Texture();
  public static final Texture strippedAcaciaLogTop = new Texture();

  public static final Texture bedWhite = new Texture();
  public static final Texture bedOrange = new Texture();
  public static final Texture bedMagenta = new Texture();
  public static final Texture bedLightBlue = new Texture();
  public static final Texture bedYellow = new Texture();
  public static final Texture bedLime = new Texture();
  public static final Texture bedPink = new Texture();
  public static final Texture bedGray = new Texture();
  public static final Texture bedSilver = new Texture();
  public static final Texture bedCyan = new Texture();
  public static final Texture bedPurple = new Texture();
  public static final Texture bedBlue = new Texture();
  public static final Texture bedBrown = new Texture();
  public static final Texture bedGreen = new Texture();
  public static final Texture bedRed = new Texture();
  public static final Texture bedBlack = new Texture();

  // [1.13]
  public static final Texture kelp = new Texture();
  public static final Texture kelpPlant = new Texture();
  public static final Texture seagrass = new Texture();
  public static final Texture tallSeagrassTop = new Texture();
  public static final Texture tallSeagrassBottom = new Texture();

  public static final Texture driedKelpSide = new Texture();
  public static final Texture driedKelpTop = new Texture();
  public static final Texture driedKelpBottom = new Texture();

  public static final Texture tubeCoral = new Texture();
  public static final Texture brainCoral = new Texture();
  public static final Texture bubbleCoral = new Texture();
  public static final Texture fireCoral = new Texture();
  public static final Texture hornCoral = new Texture();

  public static final Texture tubeCoralBlock = new Texture();
  public static final Texture brainCoralBlock = new Texture();
  public static final Texture bubbleCoralBlock = new Texture();
  public static final Texture fireCoralBlock = new Texture();
  public static final Texture hornCoralBlock = new Texture();

  public static final Texture deadTubeCoralBlock = new Texture();
  public static final Texture deadBrainCoralBlock = new Texture();
  public static final Texture deadBubbleCoralBlock = new Texture();
  public static final Texture deadFireCoralBlock = new Texture();
  public static final Texture deadHornCoralBlock = new Texture();

  public static final Texture tubeCoralFan = new Texture();
  public static final Texture brainCoralFan = new Texture();
  public static final Texture bubbleCoralFan = new Texture();
  public static final Texture fireCoralFan = new Texture();
  public static final Texture hornCoralFan = new Texture();

  public static final Texture deadTubeCoral = new Texture();
  public static final Texture deadBrainCoral = new Texture();
  public static final Texture deadBubbleCoral = new Texture();
  public static final Texture deadFireCoral = new Texture();
  public static final Texture deadHornCoral = new Texture();

  public static final Texture deadTubeCoralFan = new Texture();
  public static final Texture deadBrainCoralFan = new Texture();
  public static final Texture deadBubbleCoralFan = new Texture();
  public static final Texture deadFireCoralFan = new Texture();
  public static final Texture deadHornCoralFan = new Texture();

  public static final Texture turtleEgg = new Texture();
  public static final Texture turtleEggSlightlyCracked = new Texture();
  public static final Texture turtleEggVeryCracked = new Texture();

  public static final Texture blueIce = new Texture();
  public static final Texture seaPickle = new Texture();
  public static final Texture conduit = new Texture();
  public static final Texture structureBlock = new Texture();
  public static final Texture structureBlockCorner = new Texture();
  public static final Texture structureBlockData = new Texture();
  public static final Texture structureBlockLoad = new Texture();
  public static final Texture structureBlockSave = new Texture();

  // [1.14]
  public static final Texture barrelTop = new Texture();
  public static final Texture barrelOpen = new Texture();
  public static final Texture barrelSide = new Texture();
  public static final Texture barrelBottom = new Texture();
  public static final Texture loomBottom = new Texture();
  public static final Texture loomFront = new Texture();
  public static final Texture loomSide = new Texture();
  public static final Texture loomTop = new Texture();
  public static final Texture acaciaSignPost = new Texture();
  public static final Texture birchSignPost = new Texture();
  public static final Texture darkOakSignPost = new Texture();
  public static final Texture jungleSignPost = new Texture();
  public static final Texture spruceSignPost = new Texture();
  public static final Texture cartographyTableSide1 = new Texture();
  public static final Texture cartographyTableSide2 = new Texture();
  public static final Texture cartographyTableSide3 = new Texture();
  public static final Texture cartographyTableTop = new Texture();
  public static final Texture fletchingTableFront = new Texture();
  public static final Texture fletchingTableSide = new Texture();
  public static final Texture fletchingTableTop = new Texture();
  public static final Texture smithingTableFront = new Texture();
  public static final Texture smithingTableSide = new Texture();
  public static final Texture smithingTableTop = new Texture();
  public static final Texture smithingTableBottom = new Texture();
  public static final Texture blastFurnaceTop = new Texture();
  public static final Texture blastFurnaceSide = new Texture();
  public static final Texture blastFurnaceFrontOn = new Texture();
  public static final Texture blastFurnaceFront = new Texture();
  public static final Texture smokerTop = new Texture();
  public static final Texture smokerSide = new Texture();
  public static final Texture smokerBottom = new Texture();
  public static final Texture smokerFrontOn = new Texture();
  public static final Texture smokerFront = new Texture();
  public static final Texture sweetBerryBushStage0 = new Texture();
  public static final Texture sweetBerryBushStage1 = new Texture();
  public static final Texture sweetBerryBushStage2 = new Texture();
  public static final Texture sweetBerryBushStage3 = new Texture();
  public static final Texture cornflower = new Texture();
  public static final Texture lilyOfTheValley = new Texture();
  public static final Texture witherRose = new Texture();
  public static final Texture bambooSapling = new Texture();
  public static final Texture lecternBase = new Texture();
  public static final Texture lecternFront = new Texture();
  public static final Texture lecternSides = new Texture();
  public static final Texture lecternTop = new Texture();
  public static final Texture composterTop = new Texture();
  public static final Texture composterBottom = new Texture();
  public static final Texture composterSide = new Texture();
  public static final Texture composterCompost = new Texture();
  public static final Texture composterReady = new Texture();
  public static final Texture bambooStalk = new Texture();
  public static final Texture bambooSmallLeaves = new Texture();
  public static final Texture bambooLargeLeaves = new Texture();
  public static final Texture bambooSingleLeaf = new Texture();
  public static final Texture stonecutterBottom = new Texture();
  public static final Texture stonecutterTop = new Texture();
  public static final Texture stonecutterSide = new Texture();
  public static final Texture stonecutterSaw = new Texture();
  public static final Texture grindstonePivot = new Texture();
  public static final Texture grindstoneRound = new Texture();
  public static final Texture grindstoneSide = new Texture();
  public static final Texture campfireLog = new Texture();
  public static final Texture campfireLogLit = new Texture();
  public static final Texture campfireFire = new Texture();
  public static final Texture lantern = new Texture();
  public static final Texture bellBody = new Texture();
  public static final Texture scaffoldingTop = new Texture();
  public static final Texture scaffoldingSide = new Texture();
  public static final Texture scaffoldingBottom = new Texture();
  public static final Texture jigsawTop = new Texture();
  public static final Texture jigsawSide = new Texture();
  public static final Texture jigsawBottom = new Texture();

  // [1.15]
  public static final Texture honeyBlockTop = new Texture();
  public static final Texture honeyBlockSide = new Texture();
  public static final Texture honeyBlockBottom = new Texture();
  public static final Texture beeNestFront = new Texture();
  public static final Texture beeNestFrontHoney = new Texture();
  public static final Texture beeNestSide = new Texture();
  public static final Texture beeNestTop = new Texture();
  public static final Texture beeNestBottom = new Texture();
  public static final Texture beehiveFront = new Texture();
  public static final Texture beehiveFrontHoney = new Texture();
  public static final Texture beehiveSide = new Texture();
  public static final Texture beehiveEnd = new Texture();
  public static final Texture honeycombBlock = new Texture();

  // [1.16]
  public static final Texture soulSoil = new Texture();
  public static final Texture crimsonNylium = new Texture();
  public static final Texture crimsonNyliumSide = new Texture();
  public static final Texture warpedNylium = new Texture();
  public static final Texture warpedNyliumSide = new Texture();
  public static final Texture netherGoldOre = new Texture();
  public static final Texture targetSide = new Texture();
  public static final Texture targetTop = new Texture();
  public static final Texture netheriteBlock = new Texture();
  public static final Texture shroomlight = new Texture();
  public static final Texture warpedWartBlock = new Texture();
  public static final Texture basaltSide = new Texture();
  public static final Texture basaltTop = new Texture();
  public static final Texture polishedBasaltSide = new Texture();
  public static final Texture polishedBasaltTop = new Texture();
  public static final Texture ancientDebrisSide = new Texture();
  public static final Texture ancientDebrisTop = new Texture();
  public static final Texture warpedFungus = new Texture();
  public static final Texture crimsonFungus = new Texture();
  public static final Texture netherSprouts = new Texture();
  public static final Texture warpedRoots = new Texture();
  public static final Texture crimsonRoots = new Texture();
  public static final Texture warpedRootsPot = new Texture();
  public static final Texture crimsonRootsPot = new Texture();
  public static final Texture cryingObsidian = new Texture();
  public static final Texture warpedStem = new Texture();
  public static final Texture warpedStemTop = new Texture();
  public static final Texture strippedWarpedStem = new Texture();
  public static final Texture strippedWarpedStemTop = new Texture();
  public static final Texture crimsonStem = new Texture();
  public static final Texture crimsonStemTop = new Texture();
  public static final Texture strippedCrimsonStem = new Texture();
  public static final Texture strippedCrimsonStemTop = new Texture();
  public static final Texture soulFireLantern = new Texture();
  public static final Texture twistingVines = new Texture();
  public static final Texture twistingVinesPlant = new Texture();
  public static final Texture weepingVines = new Texture();
  public static final Texture weepingVinesPlant = new Texture();
  public static final Texture soulFireTorch = new Texture();
  public static final Texture respawnAnchorTop = new Texture();
  public static final Texture respawnAnchorBottom = new Texture();
  public static final Texture respawnAnchorSide0 = new Texture();
  public static final Texture respawnAnchorSide1 = new Texture();
  public static final Texture respawnAnchorSide2 = new Texture();
  public static final Texture respawnAnchorSide3 = new Texture();
  public static final Texture respawnAnchorSide4 = new Texture();
  public static final Texture crimsonSignPost = new Texture();
  public static final Texture warpedSignPost = new Texture();
  public static final Texture crimsonPlanks = new Texture();
  public static final Texture warpedPlanks = new Texture();
  public static final Texture crimsonDoorTop = new Texture();
  public static final Texture crimsonDoorBottom = new Texture();
  public static final Texture warpedDoorTop = new Texture();
  public static final Texture warpedDoorBottom = new Texture();
  public static final Texture crimsonTrapdoor = new Texture();
  public static final Texture warpedTrapdoor = new Texture();
  public static final Texture soulFire = new Texture();
  public static final AnimatedTexture soulFireLayer0 = new AnimatedTexture();
  public static final AnimatedTexture soulFireLayer1 = new AnimatedTexture();
  public static final Texture lodestoneSide = new Texture();
  public static final Texture lodestoneTop = new Texture();
  public static final Texture blackstone = new Texture();
  public static final Texture blackstoneTop = new Texture();
  public static final Texture chiseledNetherBricks = new Texture();
  public static final Texture crackedNetherBricks = new Texture();
  public static final Texture gildedBlackstone = new Texture();
  public static final Texture soulCampfireLogLit = new Texture();
  public static final Texture soulCampfireFire = new Texture();
  public static final Texture polishedBlackstone = new Texture();
  public static final Texture chiseledPolishedBlackstone = new Texture();
  public static final Texture polishedBlackstoneBricks = new Texture();
  public static final Texture crackedPolishedBlackstoneBricks = new Texture();
  public static final Texture quartzBricks = new Texture();
  public static final Texture chain = new Texture();
  public static final Texture jigsawLock = new Texture();

  // [1.17]
  public static final Texture candle = new Texture();
  public static final Texture whiteCandle = new Texture();
  public static final Texture orangeCandle = new Texture();
  public static final Texture magentaCandle = new Texture();
  public static final Texture lightBlueCandle = new Texture();
  public static final Texture yellowCandle = new Texture();
  public static final Texture limeCandle = new Texture();
  public static final Texture pinkCandle = new Texture();
  public static final Texture grayCandle = new Texture();
  public static final Texture lightGrayCandle = new Texture();
  public static final Texture cyanCandle = new Texture();
  public static final Texture purpleCandle = new Texture();
  public static final Texture blueCandle = new Texture();
  public static final Texture brownCandle = new Texture();
  public static final Texture greenCandle = new Texture();
  public static final Texture redCandle = new Texture();
  public static final Texture blackCandle = new Texture();
  public static final Texture candleLit = new Texture();
  public static final Texture whiteCandleLit = new Texture();
  public static final Texture orangeCandleLit = new Texture();
  public static final Texture magentaCandleLit = new Texture();
  public static final Texture lightBlueCandleLit = new Texture();
  public static final Texture yellowCandleLit = new Texture();
  public static final Texture limeCandleLit = new Texture();
  public static final Texture pinkCandleLit = new Texture();
  public static final Texture grayCandleLit = new Texture();
  public static final Texture lightGrayCandleLit = new Texture();
  public static final Texture cyanCandleLit = new Texture();
  public static final Texture purpleCandleLit = new Texture();
  public static final Texture blueCandleLit = new Texture();
  public static final Texture brownCandleLit = new Texture();
  public static final Texture greenCandleLit = new Texture();
  public static final Texture redCandleLit = new Texture();
  public static final Texture blackCandleLit = new Texture();
  public static final Texture flameParticle = new Texture();
  public static final Texture copperOre = new Texture();
  public static final Texture calcite = new Texture();
  public static final Texture tuff = new Texture();
  public static final Texture amethyst = new Texture();
  public static final Texture buddingAmethyst = new Texture();
  public static final Texture copperBlock = new Texture();
  public static final Texture exposedCopper = new Texture();
  public static final Texture weatheredCopper = new Texture();
  public static final Texture oxidizedCopper = new Texture();
  public static final Texture cutCopper = new Texture();
  public static final Texture exposedCutCopper = new Texture();
  public static final Texture weatheredCutCopper = new Texture();
  public static final Texture oxidizedCutCopper = new Texture();
  public static final Texture lightningRod = new Texture();
  public static final Texture smallAmethystBud = new Texture();
  public static final Texture mediumAmethystBud = new Texture();
  public static final Texture largeAmethystBud = new Texture();
  public static final Texture amethystCluster = new Texture();
  public static final Texture tintedGlass = new Texture();
  public static final Texture powderSnow = new Texture();
  public static final Texture dripstoneBlock = new Texture();
  public static final Texture pointedDripstoneDownBase = new Texture();
  public static final Texture pointedDripstoneDownFrustum = new Texture();
  public static final Texture pointedDripstoneDownMiddle = new Texture();
  public static final Texture pointedDripstoneDownTip = new Texture();
  public static final Texture pointedDripstoneDownTipMerge = new Texture();
  public static final Texture pointedDripstoneUpBase = new Texture();
  public static final Texture pointedDripstoneUpFrustum = new Texture();
  public static final Texture pointedDripstoneUpMiddle = new Texture();
  public static final Texture pointedDripstoneUpTip = new Texture();
  public static final Texture pointedDripstoneUpTipMerge = new Texture();
  public static final Texture sculkSensorBottom = new Texture();
  public static final Texture sculkSensorSide = new Texture();
  public static final Texture sculkSensorTendrilActive = new Texture();
  public static final Texture sculkSensorTendrilInactive = new Texture();
  public static final Texture sculkSensorTop = new Texture();
  public static final Texture glowLichen = new Texture();
  public static final Texture azaleaTop = new Texture();
  public static final Texture azaleaSide = new Texture();
  public static final Texture azaleaPlant = new Texture();
  public static final Texture floweringAzaleaTop = new Texture();
  public static final Texture floweringAzaleaSide = new Texture();
  public static final Texture azaleaLeaves = new Texture();
  public static final Texture floweringAzaleaLeaves = new Texture();
  public static final Texture mossBlock = new Texture();
  public static final Texture caveVinesPlant = new Texture();
  public static final Texture caveVinesPlantLit = new Texture();
  public static final Texture caveVines = new Texture();
  public static final Texture caveVinesLit = new Texture();
  public static final Texture hangingRoots = new Texture();
  public static final Texture rootedDirt = new Texture();
  public static final Texture bigDripleafStem = new Texture();
  public static final Texture bigDripleafTop = new Texture();
  public static final Texture bigDripleafSide = new Texture();
  public static final Texture bigDripleafTip = new Texture();
  public static final Texture smallDripleafTop = new Texture();
  public static final Texture smallDripleafSide = new Texture();
  public static final Texture smallDripleafStemTop = new Texture();
  public static final Texture smallDripleafStemBottom = new Texture();
  public static final Texture sporeBlossom = new Texture();
  public static final Texture sporeBlossomBase = new Texture();
  public static final Texture deepslate = new Texture();
  public static final Texture deepslateTop = new Texture();
  public static final Texture polishedDeepslate = new Texture();
  public static final Texture chiseledDeepslate = new Texture();
  public static final Texture deepslateBricks = new Texture();
  public static final Texture deepslateTiles = new Texture();
  public static final Texture smoothBasalt = new Texture();
  public static final Texture cobbledDeepslate = new Texture();
  public static final Texture deepslateGoldOre = new Texture();
  public static final Texture deepslateIronOre = new Texture();
  public static final Texture deepslateDiamondOre = new Texture();
  public static final Texture deepslateLapisOre = new Texture();
  public static final Texture deepslateRedstoneOre = new Texture();
  public static final Texture crackedDeepslateBricks = new Texture();
  public static final Texture crackedDeepslateTiles = new Texture();
  public static final Texture deepslateCoalOre = new Texture();
  public static final Texture deepslateCopperOre = new Texture();
  public static final Texture deepslateEmeraldOre = new Texture();
  public static final Texture lightningRodOn = new Texture();
  public static final Texture light = new Texture();
  public static final Texture rawCopperBlock = new Texture();
  public static final Texture rawGoldBlock = new Texture();
  public static final Texture rawIronBlock = new Texture();
  public static final Texture pottedAzaleaBushTop = new Texture();
  public static final Texture pottedAzaleaBushSide = new Texture();
  public static final Texture pottedAzaleaBushPlant = new Texture();
  public static final Texture pottedFloweringAzaleaBushTop = new Texture();
  public static final Texture pottedFloweringAzaleaBushSide = new Texture();

  // [1.19]
  @TexturePath("assets/minecraft/textures/block/mud")
  public static final Texture mud = new Texture();
  @TexturePath("assets/minecraft/textures/block/mud_bricks")
  public static final Texture mudBricks = new Texture();
  @TexturePath("assets/minecraft/textures/block/packed_mud")
  public static final Texture packedMud = new Texture();
  @TexturePath("assets/minecraft/textures/block/muddy_mangrove_roots_side")
  public static final Texture muddyMangroveRootsSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/muddy_mangrove_roots_top")
  public static final Texture muddyMangroveRootsTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/reinforced_deepslate_top")
  public static final Texture reinforcedDeepslateTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/reinforced_deepslate_side")
  public static final Texture reinforcedDeepslateSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/reinforced_deepslate_bottom")
  public static final Texture reinforcedDeepslateBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/frogspawn")
  public static final Texture frogspawn = new Texture();
  @TexturePath("assets/minecraft/textures/block/ochre_froglight_side")
  public static final Texture ochreFroglightSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/ochre_froglight_top")
  public static final Texture ochreFroglightTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/verdant_froglight_side")
  public static final Texture verdantFroglightSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/verdant_froglight_top")
  public static final Texture verdantFroglightTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/pearlescent_froglight_side")
  public static final Texture pearlescentFroglightSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/pearlescent_froglight_top")
  public static final Texture pearlescentFroglightTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_planks")
  public static final Texture mangrovePlanks = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_door_top")
  public static final Texture mangroveDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_door_bottom")
  public static final Texture mangroveDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_leaves")
  public static final Texture mangroveLeaves = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_log")
  public static final Texture mangroveLog = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_log_top")
  public static final Texture mangroveLogTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_mangrove_log")
  public static final Texture strippedMangroveLog = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_mangrove_log_top")
  public static final Texture strippedMangroveLogTop = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/mangrove")
  public static final Texture mangroveSignPost = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_trapdoor")
  public static final Texture mangroveTrapdoor = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_roots_side")
  public static final Texture mangroveRootsSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_roots_top")
  public static final Texture mangroveRootsTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_propagule")
  public static final Texture mangrovePropagule = new Texture();
  @TexturePath("assets/minecraft/textures/block/mangrove_propagule_hanging")
  public static final Texture mangrovePropaguleHanging = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_top")
  public static final Texture sculkCatalystTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_side")
  public static final Texture sculkCatalystSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_bottom")
  public static final Texture sculkCatalystBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_top_bloom")
  public static final Texture sculkCatalystTopBloom = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_side_bloom")
  public static final Texture sculkCatalystSideBloom = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk")
  public static final Texture sculk = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_top")
  public static final Texture sculkShriekerTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_side")
  public static final Texture sculkShriekerSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_bottom")
  public static final Texture sculkShriekerBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_can_summon_inner_top")
  public static final Texture sculkShriekerCanSummonInnerTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_inner_top")
  public static final Texture sculkShriekerInnerTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/sculk_vein")
  public static final Texture sculkVein = new Texture();

  // [1.20]
  @TexturePath("assets/minecraft/textures/block/bamboo_planks")
  public static final Texture bambooPlanks = new Texture();
  @TexturePath("assets/minecraft/textures/block/bamboo_mosaic")
  public static final Texture bambooMosaic = new Texture();
  @TexturePath("assets/minecraft/textures/block/bamboo_door_top")
  public static final Texture bambooDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/bamboo_door_bottom")
  public static final Texture bambooDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/bamboo_block")
  public static final Texture bambooBlock = new Texture();
  @TexturePath("assets/minecraft/textures/block/bamboo_block_top")
  public static final Texture bambooBlockTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_bamboo_block")
  public static final Texture strippedBambooBlock = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_bamboo_block_top")
  public static final Texture strippedBambooBlockTop = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/bamboo")
  public static final Texture bambooSignPost = new Texture();
  @TexturePath("assets/minecraft/textures/block/bamboo_trapdoor")
  public static final Texture bambooTrapdoor = new Texture();

  @TexturePath("assets/minecraft/textures/block/cherry_planks")
  public static final Texture cherryPlanks = new Texture();
  @TexturePath("assets/minecraft/textures/block/cherry_door_top")
  public static final Texture cherryDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/cherry_door_bottom")
  public static final Texture cherryDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/cherry_leaves")
  public static final Texture cherryLeaves = new Texture();
  @TexturePath("assets/minecraft/textures/block/cherry_log")
  public static final Texture cherryLog = new Texture();
  @TexturePath("assets/minecraft/textures/block/cherry_log_top")
  public static final Texture cherryLogTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_cherry_log")
  public static final Texture strippedCherryLog = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_cherry_log_top")
  public static final Texture strippedCherryLogTop = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/cherry")
  public static final Texture cherrySignPost = new Texture();
  @TexturePath("assets/minecraft/textures/block/cherry_trapdoor")
  public static final Texture cherryTrapdoor = new Texture();
  @TexturePath("assets/minecraft/textures/block/cherry_sapling")
  public static final Texture cherrySapling = new Texture();
  @TexturePath("assets/minecraft/textures/block/torchflower")
  public static final Texture torchflower = new Texture();
  @TexturePath("assets/minecraft/textures/block/torchflower_crop_stage0")
  public static final Texture torchflowerCropStage0 = new Texture();
  @TexturePath("assets/minecraft/textures/block/torchflower_crop_stage1")
  public static final Texture torchflowerCropStage1 = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_empty")
  public static final Texture chiseledBookshelfEmpty = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_occupied")
  public static final Texture chiseledBookshelfOccupied = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_side")
  public static final Texture chiseledBookshelfSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_top")
  public static final Texture chiseledBookshelfTop = new Texture();

  public static final Texture[] chiseledBookshelfCombinations = new ChiseledBookshelfTexture[64];
  static {
    for(int i = 0; i < chiseledBookshelfCombinations.length; i++) {
      chiseledBookshelfCombinations[i] = new ChiseledBookshelfTexture(Texture.chiseledBookshelfEmpty, Texture.chiseledBookshelfOccupied,
        i % 2 == 1, (i >> 1) % 2 == 1, (i >> 2) % 2 == 1, (i >> 3) % 2 == 1, (i >> 4) % 2 == 1, (i >> 5) % 2 == 1);
    }
  }

  @TexturePath("assets/minecraft/textures/block/suspicious_sand_0")
  public static final Texture suspiciousSandStage0 = new Texture();
  @TexturePath("assets/minecraft/textures/block/suspicious_sand_1")
  public static final Texture suspiciousSandStage1 = new Texture();
  @TexturePath("assets/minecraft/textures/block/suspicious_sand_2")
  public static final Texture suspiciousSandStage2 = new Texture();
  @TexturePath("assets/minecraft/textures/block/suspicious_sand_3")
  public static final Texture suspiciousSandStage3 = new Texture();

  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_0")
  public static final Texture suspiciousGravelStage0 = new Texture();
  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_1")
  public static final Texture suspiciousGravelStage1 = new Texture();
  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_2")
  public static final Texture suspiciousGravelStage2 = new Texture();
  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_3")
  public static final Texture suspiciousGravelStage3 = new Texture();

  @TexturePath("assets/minecraft/textures/entity/decorated_pot/decorated_pot_base")
  public static final Texture decoratedPotBase = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/decorated_pot_side")
  public static final Texture decoratedPotSide = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/angler_pottery_pattern")
  public static final Texture decoratedPotPatternAngler = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/archer_pottery_pattern")
  public static final Texture decoratedPotPatternArcher = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/arms_up_pottery_pattern")
  public static final Texture decoratedPotPatternArmsUp = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/blade_pottery_pattern")
  public static final Texture decoratedPotPatternBlade = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/brewer_pottery_pattern")
  public static final Texture decoratedPotPatternBrewer = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/burn_pottery_pattern")
  public static final Texture decoratedPotPatternBurn = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/danger_pottery_pattern")
  public static final Texture decoratedPotPatternDanger = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/explorer_pottery_pattern")
  public static final Texture decoratedPotPatternExplorer = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/friend_pottery_pattern")
  public static final Texture decoratedPotPatternFriend = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/heartbreak_pottery_pattern")
  public static final Texture decoratedPotPatternHeartbreak = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/heart_pottery_pattern")
  public static final Texture decoratedPotPatternHeart = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/howl_pottery_pattern")
  public static final Texture decoratedPotPatternHowl = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/miner_pottery_pattern")
  public static final Texture decoratedPotPatternMiner = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/mourner_pottery_pattern")
  public static final Texture decoratedPotPatternMourner = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/plenty_pottery_pattern")
  public static final Texture decoratedPotPatternPlenty = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/prize_pottery_pattern")
  public static final Texture decoratedPotPatternPrize = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/sheaf_pottery_pattern")
  public static final Texture decoratedPotPatternSheaf = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/shelter_pottery_pattern")
  public static final Texture decoratedPotPatternShelter = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/skull_pottery_pattern")
  public static final Texture decoratedPotPatternSkull = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/snort_pottery_pattern")
  public static final Texture decoratedPotPatternSnort = new Texture();

  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_bottom")
  public static final Texture snifferEggNotCrackedBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_east")
  public static final Texture snifferEggNotCrackedEast = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_north")
  public static final Texture snifferEggNotCrackedNorth = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_south")
  public static final Texture snifferEggNotCrackedSouth = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_top")
  public static final Texture snifferEggNotCrackedTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_west")
  public static final Texture snifferEggNotCrackedWest = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_bottom")
  public static final Texture snifferEggSlightlyCrackedBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_east")
  public static final Texture snifferEggSlightlyCrackedEast = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_north")
  public static final Texture snifferEggSlightlyCrackedNorth = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_south")
  public static final Texture snifferEggSlightlyCrackedSouth = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_top")
  public static final Texture snifferEggSlightlyCrackedTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_west")
  public static final Texture snifferEggSlightlyCrackedWest = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_bottom")
  public static final Texture snifferEggVeryCrackedBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_east")
  public static final Texture snifferEggVeryCrackedEast = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_north")
  public static final Texture snifferEggVeryCrackedNorth = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_south")
  public static final Texture snifferEggVeryCrackedSouth = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_top")
  public static final Texture snifferEggVeryCrackedTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_west")
  public static final Texture snifferEggVeryCrackedWest = new Texture();
  @TexturePath("assets/minecraft/textures/block/pink_petals")
  public static final Texture pinkPetals = new Texture();
  @TexturePath("assets/minecraft/textures/block/pink_petals_stem")
  public static final Texture pinkPetalsStem = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_1")
  public static final Texture pitcherCropBottomStage1 = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_2")
  public static final Texture pitcherCropBottomStage2 = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_3")
  public static final Texture pitcherCropBottomStage3 = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_4")
  public static final Texture pitcherCropBottomStage4 = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_top_stage_3")
  public static final Texture pitcherCropTopStage3 = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_top_stage_4")
  public static final Texture pitcherCropTopStage4 = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_top")
  public static final Texture pitcherCropTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_side")
  public static final Texture pitcherCropSide = new Texture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom")
  public static final Texture pitcherCropBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/calibrated_sculk_sensor_amethyst")
  public static final Texture calibratedSculkSensorAmethyst = new Texture();
  @TexturePath("assets/minecraft/textures/block/calibrated_sculk_sensor_top")
  public static final Texture calibratedSculkSensorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/calibrated_sculk_sensor_input_side")
  public static final Texture calibratedSculkSensorInputSide = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/oak")
  public static final Texture oakHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/spruce")
  public static final Texture spruceHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/birch")
  public static final Texture birchHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/jungle")
  public static final Texture jungleHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/acacia")
  public static final Texture acaciaHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/dark_oak")
  public static final Texture darkOakHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/crimson")
  public static final Texture crimsonHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/warped")
  public static final Texture warpedHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/mangrove")
  public static final Texture mangroveHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/bamboo")
  public static final Texture bambooHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/cherry")
  public static final Texture cherryHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/entity/piglin/piglin")
  public static final Texture piglin = new Texture();

  // [1.21]
  @TexturePath("assets/minecraft/textures/block/polished_tuff")
  public static final Texture polishedTuff = new Texture();
  @TexturePath("assets/minecraft/textures/block/tuff_bricks")
  public static final Texture tuffBricks = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_tuff")
  public static final Texture chiseledTuff = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_tuff_bricks")
  public static final Texture chiseledTuffBricks = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_copper")
  public static final Texture chiseledCopper = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_grate")
  public static final Texture copperGrate = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb")
  public static final Texture copperBulb = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb_lit")
  public static final Texture copperBulbLit = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb_powered")
  public static final Texture copperBulbPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb_lit_powered")
  public static final Texture copperBulbLitPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_door_top")
  public static final Texture copperDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_door_bottom")
  public static final Texture copperDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/copper_trapdoor")
  public static final Texture copperTrapdoor = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_chiseled_copper")
  public static final Texture exposedChiseledCopper = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_grate")
  public static final Texture exposedCopperGrate = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb")
  public static final Texture exposedCopperBulb = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb_lit")
  public static final Texture exposedCopperBulbLit = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb_powered")
  public static final Texture exposedCopperBulbPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb_lit_powered")
  public static final Texture exposedCopperBulbLitPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_door_top")
  public static final Texture exposedCopperDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_door_bottom")
  public static final Texture exposedCopperDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_trapdoor")
  public static final Texture exposedCopperTrapdoor = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_chiseled_copper")
  public static final Texture weatheredChiseledCopper = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_grate")
  public static final Texture weatheredCopperGrate = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb")
  public static final Texture weatheredCopperBulb = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb_lit")
  public static final Texture weatheredCopperBulbLit = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb_powered")
  public static final Texture weatheredCopperBulbPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb_lit_powered")
  public static final Texture weatheredCopperBulbLitPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_door_top")
  public static final Texture weatheredCopperDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_door_bottom")
  public static final Texture weatheredCopperDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_trapdoor")
  public static final Texture weatheredCopperTrapdoor = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_chiseled_copper")
  public static final Texture oxidizedChiseledCopper = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_grate")
  public static final Texture oxidizedCopperGrate = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb")
  public static final Texture oxidizedCopperBulb = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb_lit")
  public static final Texture oxidizedCopperBulbLit = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb_powered")
  public static final Texture oxidizedCopperBulbPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb_lit_powered")
  public static final Texture oxidizedCopperBulbLitPowered = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_door_top")
  public static final Texture oxidizedCopperDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_door_bottom")
  public static final Texture oxidizedCopperDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_trapdoor")
  public static final Texture oxidizedCopperTrapdoor = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_north")
  public static final Texture crafterNorth = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_north_crafting")
  public static final Texture crafterNorthCrafting = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_east")
  public static final Texture crafterEast = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_east_crafting")
  public static final Texture crafterEastCrafting = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_east_triggered")
  public static final Texture crafterEastTriggered = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_south")
  public static final Texture crafterSouth = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_south_triggered")
  public static final Texture crafterSouthTriggered = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_west")
  public static final Texture crafterWest = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_west_crafting")
  public static final Texture crafterWestCrafting = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_west_triggered")
  public static final Texture crafterWestTriggered = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_top")
  public static final Texture crafterTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_top_crafting")
  public static final Texture crafterTopCrafting = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_top_triggered")
  public static final Texture crafterTopTriggered = new Texture();
  @TexturePath("assets/minecraft/textures/block/crafter_bottom")
  public static final Texture crafterBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_top")
  public static final Texture vaultTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_top_ejecting")
  public static final Texture vaultTopEjecting = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_bottom")
  public static final Texture vaultBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_front_on")
  public static final Texture vaultFrontOn = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_front_off")
  public static final Texture vaultFrontOff = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_front_ejecting")
  public static final Texture vaultFrontEjecting = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_side_off")
  public static final Texture vaultSideOff = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_side_on")
  public static final Texture vaultSideOn = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/flow_pottery_pattern")
  public static final Texture decoratedPotPatternFlow = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/guster_pottery_pattern")
  public static final Texture decoratedPotPatternGuster = new Texture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/scrape_pottery_pattern")
  public static final Texture decoratedPotPatternScrape = new Texture();
  @TexturePath("assets/minecraft/textures/block/heavy_core")
  public static final Texture heavyCore = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_bottom_ominous")
  public static final Texture vaultBottomOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_front_off_ominous")
  public static final Texture vaultFrontOffOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_side_off_ominous")
  public static final Texture vaultSideOffOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_top_ominous")
  public static final Texture vaultTopOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_side_on_ominous")
  public static final Texture vaultSideOnOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_front_on_ominous")
  public static final Texture vaultFrontOnOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_top_ejecting_ominous")
  public static final Texture vaultTopEjectingOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/vault_front_ejecting_ominous")
  public static final Texture vaultFrontEjectingOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_bottom")
  public static final Texture trialSpawnerBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_inactive")
  public static final Texture trialSpawnerSideInactive = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_inactive_ominous")
  public static final Texture trialSpawnerSideInactiveOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_inactive")
  public static final Texture trialSpawnerTopInactive = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_inactive_ominous")
  public static final Texture trialSpawnerTopInactiveOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_active")
  public static final Texture trialSpawnerSideActive = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_active_ominous")
  public static final Texture trialSpawnerSideActiveOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_active")
  public static final Texture trialSpawnerTopActive = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_active_ominous")
  public static final Texture trialSpawnerTopActiveOminous = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_ejecting_reward")
  public static final Texture trialSpawnerTopEjectingReward = new Texture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_ejecting_reward_ominous")
  public static final Texture trialSpawnerTopEjectingRewardOminous = new Texture();

  // [1.21.4]
  @TexturePath("assets/minecraft/textures/block/pale_moss_block")
  public static final Texture paleMossBlock = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_leaves")
  public static final Texture paleOakLeaves = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_log")
  public static final Texture paleOakLog= new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_log_top")
  public static final Texture paleOakLogTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_hanging_moss")
  public static final Texture paleHangingMoss = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_hanging_moss_tip")
  public static final Texture paleHangingMossTip = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_planks")
  public static final Texture paleOakPlanks = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_trapdoor")
  public static final Texture paleOakTrapdoor = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_door_top")
  public static final Texture paleOakDoorTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_door_bottom")
  public static final Texture paleOakDoorBottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_pale_oak_log")
  public static final Texture strippedPaleOakLog = new Texture();
  @TexturePath("assets/minecraft/textures/block/stripped_pale_oak_log_top")
  public static final Texture strippedPaleOakLogTop = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_oak_sapling")
  public static final Texture paleOakSapling = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/pale_oak")
  public static final Texture paleOakSignPost = new Texture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/pale_oak")
  public static final Texture paleOakHangingSign = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_moss_carpet")
  public static final Texture paleMossCarpet = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_moss_carpet_side_small")
  public static final Texture paleMossCarpetSideSmall = new Texture();
  @TexturePath("assets/minecraft/textures/block/pale_moss_carpet_side_tall")
  public static final Texture paleMossCarpetSideTall = new Texture();
  @TexturePath("assets/minecraft/textures/block/creaking_heart")
  public static final Texture creakingHeart = new Texture();
  @TexturePath("assets/minecraft/textures/block/creaking_heart_top")
  public static final Texture creakingHeartTop = new Texture();
  @TexturePath(value = "assets/minecraft/textures/block/creaking_heart_awake", alternatives = { "assets/minecraft/textures/block/creaking_heart_active" })
  public static final Texture creakingHeartAwake = new Texture();
  @TexturePath(value = "assets/minecraft/textures/block/creaking_heart_top_awake", alternatives = { "assets/minecraft/textures/block/creaking_heart_top_active" })
  public static final Texture creakingHeartTopAwake = new Texture();
  @TexturePath("assets/minecraft/textures/block/chiseled_resin_bricks")
  public static final Texture chiseledResinBricks = new Texture();
  @TexturePath("assets/minecraft/textures/block/closed_eyeblossom")
  public static final Texture closedEyeblossom = new Texture();
  @TexturePath("assets/minecraft/textures/block/open_eyeblossom")
  public static final Texture openEyeblossom = new Texture();
  @TexturePath("assets/minecraft/textures/block/open_eyeblossom_emissive")
  public static final Texture openEyeblossomEmissive = new Texture();
  @TexturePath("assets/minecraft/textures/block/resin_block")
  public static final Texture resinBlock = new Texture();
  @TexturePath("assets/minecraft/textures/block/resin_bricks")
  public static final Texture resinBricks = new Texture();
  @TexturePath("assets/minecraft/textures/block/resin_clump")
  public static final Texture resinClump = new Texture();

  // [1.21.5]
  @TexturePath("assets/minecraft/textures/block/leaf_litter")
  public static final Texture leafLitter = new Texture();
  @TexturePath("assets/minecraft/textures/block/creaking_heart_dormant")
  public static final Texture creakingHeartDormant = new Texture();
  @TexturePath("assets/minecraft/textures/block/creaking_heart_top_dormant")
  public static final Texture creakingHeartTopDormant = new Texture();
  @TexturePath("assets/minecraft/textures/block/wildflowers")
  public static final Texture wildflowers = new Texture();
  @TexturePath("assets/minecraft/textures/block/wildflowers_stem")
  public static final Texture wildflowersStem = new Texture();
  @TexturePath("assets/minecraft/textures/block/bush")
  public static final Texture bush = new Texture();
  @TexturePath("assets/minecraft/textures/block/firefly_bush")
  public static final Texture fireflyBush = new Texture();
  @TexturePath("assets/minecraft/textures/block/firefly_bush_emissive")
  public static final Texture fireflyBushEmissive = new AnimatedTexture();
  @TexturePath("assets/minecraft/textures/block/cactus_flower")
  public static final Texture cactusFlower = new Texture();
  @TexturePath("assets/minecraft/textures/block/short_dry_grass")
  public static final Texture shortDryGrass = new Texture();
  @TexturePath("assets/minecraft/textures/block/tall_dry_grass")
  public static final Texture tallDryGrass = new Texture();

  // [1.21.6]
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_0_bottom")
  public static final Texture driedGhastHydration0Bottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_1_bottom")
  public static final Texture driedGhastHydration1Bottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_2_bottom")
  public static final Texture driedGhastHydration2Bottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_3_bottom")
  public static final Texture driedGhastHydration3Bottom = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_0_east")
  public static final Texture driedGhastHydration0East = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_1_east")
  public static final Texture driedGhastHydration1East = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_2_east")
  public static final Texture driedGhastHydration2East = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_3_east")
  public static final Texture driedGhastHydration3East = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_0_north")
  public static final Texture driedGhastHydration0North = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_1_north")
  public static final Texture driedGhastHydration1North = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_2_north")
  public static final Texture driedGhastHydration2North = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_3_north")
  public static final Texture driedGhastHydration3North = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_0_south")
  public static final Texture driedGhastHydration0South = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_1_south")
  public static final Texture driedGhastHydration1South = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_2_south")
  public static final Texture driedGhastHydration2South = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_3_south")
  public static final Texture driedGhastHydration3South = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_0_top")
  public static final Texture driedGhastHydration0Top = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_1_top")
  public static final Texture driedGhastHydration1Top = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_2_top")
  public static final Texture driedGhastHydration2Top = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_3_top")
  public static final Texture driedGhastHydration3Top = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_0_west")
  public static final Texture driedGhastHydration0West = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_1_west")
  public static final Texture driedGhastHydration1West = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_2_west")
  public static final Texture driedGhastHydration2West = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_3_west")
  public static final Texture driedGhastHydration3West = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_0_tentacles")
  public static final Texture driedGhastHydration0Tentacles = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_1_tentacles")
  public static final Texture driedGhastHydration1Tentacles = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_2_tentacles")
  public static final Texture driedGhastHydration2Tentacles = new Texture();
  @TexturePath("assets/minecraft/textures/block/dried_ghast_hydration_3_tentacles")
  public static final Texture driedGhastHydration3Tentacles = new Texture();

  /** Banner base texture. */
  public static final Texture bannerBase = new Texture();

  public static final Texture armorStand = new Texture();

  protected static boolean useAverageColor = PersistentSettings.getSingleColorTextures();

  @NotNull protected BitmapImage image;
  protected int width;
  protected int height;
  protected int avgColor;
  private float[] avgColorLinear;
  private float[] avgColorFlat;

  private Image fxImage = null;

  public Texture() {
    this(ImageLoader.missingImage);
  }

  public Texture(String resourceName) {
    this(ImageLoader.readResourceNonNull("textures/" + resourceName + ".png"));
  }

  public Texture(BitmapImage img) {
    setTexture(img);
  }

  public void setTexture(Texture texture) {
    setTexture(texture.image);
  }

  public void setTexture(BitmapImage newImage) {
    image = newImage;

    // Gamma correct the texture.
    avgColorLinear = new float[] {0, 0, 0, 0};

    int[] data = image.data;
    width = image.width;
    height = image.height;
    float[] pixelBuffer = new float[4];
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        int index = width * y + x;
        ColorUtil.getRGBAComponentsGammaCorrected(data[index], pixelBuffer);
        avgColorLinear[0] += pixelBuffer[3] * pixelBuffer[0];
        avgColorLinear[1] += pixelBuffer[3] * pixelBuffer[1];
        avgColorLinear[2] += pixelBuffer[3] * pixelBuffer[2];
        avgColorLinear[3] += pixelBuffer[3];
      }
    }

    avgColorFlat = new float[4];
    if (avgColorLinear[3] > 0.001) {
      avgColorFlat[0] = avgColorLinear[0] / avgColorLinear[3];
      avgColorFlat[1] = avgColorLinear[1] / avgColorLinear[3];
      avgColorFlat[2] = avgColorLinear[2] / avgColorLinear[3];
      avgColorFlat[3] = 1;
    }

    avgColorLinear[0] /= width * height;
    avgColorLinear[1] /= width * height;
    avgColorLinear[2] /= width * height;
    avgColorLinear[3] /= width * height;

    avgColor = ColorUtil.getArgb(FastMath.pow(avgColorLinear[0], 1 / Scene.DEFAULT_GAMMA),
        FastMath.pow(avgColorLinear[1], 1 / Scene.DEFAULT_GAMMA),
        FastMath.pow(avgColorLinear[2], 1 / Scene.DEFAULT_GAMMA), avgColorLinear[3]);
  }

  /**
   * Get linear color values.
   */
  public void getColor(double u, double v, Vector4 c) {
    c.set(getColor(u, v));
  }

  /**
   * Get linear color values.
   *
   * @param ray ray to store color value in.
   */
  public void getColor(Ray ray) {
    getColor(ray.u, ray.v, ray.color);
  }

  /**
   * Get linear color values.
   *
   * @return color
   */
  public float[] getColor(double u, double v) {
    return getColor((int) (u * width - Ray.EPSILON), (int) ((1 - v) * height - Ray.EPSILON));
  }

  /**
   * Get linear color values
   *
   * @return color
   */
  public float[] getColor(int x, int y) {
    if(useAverageColor)
      return avgColorFlat;
    float[] result = new float[4];
    ColorUtil.getRGBAComponentsGammaCorrected(image.data[width*y + x], result);
    return result;
  }

  /**
   * Get bilinear interpolated color value.
   */
  public void getColorInterpolated(double u, double v, Vector4 c) {

    double x = u * (width - 1);
    double y = (1 - v) * (height - 1);
    double weight;
    int fx = (int) QuickMath.floor(x);
    int cx = (int) QuickMath.ceil(x);
    int fy = (int) QuickMath.floor(y);
    int cy = (int) QuickMath.ceil(y);

    float[] rgb = getColor(fx, fy);
    weight = (1 - (y - fy)) * (1 - (x - fx));
    c.x = weight * rgb[0];
    c.y = weight * rgb[1];
    c.z = weight * rgb[2];
    rgb = getColor(cx, fy);
    weight = (1 - (y - fy)) * (1 - (cx - x));
    c.x += weight * rgb[0];
    c.y += weight * rgb[1];
    c.z += weight * rgb[2];
    rgb = getColor(fx, cy);
    weight = (1 - (cy - y)) * (1 - (x - fx));
    c.x += weight * rgb[0];
    c.y += weight * rgb[1];
    c.z += weight * rgb[2];
    rgb = getColor(cx, cy);
    weight = (1 - (cy - y)) * (1 - (cx - x));
    c.x += weight * rgb[0];
    c.y += weight * rgb[1];
    c.z += weight * rgb[2];
  }

  public int getColorWrapped(int u, int v) {
    return image.getPixel((u + width) % width, (v + height) % height);
  }

  /**
   * @return The average color of this texture
   */
  public int getAvgColor() {
    return avgColor;
  }

  /**
   * Get the average linear color of this texture
   */
  public void getAvgColorLinear(Vector4 c) {
    c.set(avgColorLinear);
  }

  /**
   * @return The average color of this texture
   */
  public float[] getAvgColorLinear() {
    return avgColorLinear;
  }
  /**
   * @return The average flat color of this texture
   */
  public float[] getAvgColorFlat() {
    return avgColorFlat;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  /**
   * @return {@code true} if this is the dedicated empty texture
   */
  public boolean isEmptyTexture() {
    return false;
  }

  public Image fxImage() {
    if (fxImage == null) {
      fxImage = FxImageUtil.toFxImage(image);
    }
    return fxImage;
  }

  /** Access the raw image data for this texture. */
  public int[] getData() {
    return image.data;
  }

  public BitmapImage getBitmap() {
    return image;
  }

  public static void setUseAverageColor(boolean useAverageColor) {
    Texture.useAverageColor = useAverageColor;
  }
}
