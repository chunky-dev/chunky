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
import se.llbit.math.ColorUtil;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;
import se.llbit.resources.ImageLoader;
import se.llbit.util.ImageTools;
import se.llbit.util.NotNull;

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

  public static final Texture paintings = new Texture();
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
  public static final Texture slabTop = new Texture("stone-slab");
  public static final Texture slabSide = new Texture("double-stone-slab");
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
  public static final EntityTexture alex = new EntityTexture();
  public static final EntityTexture steve = new EntityTexture();
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

  public static final Texture signPost = new Texture();
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
  public static final Texture oakSignPost = new Texture();
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
  public static final AnimatedTexture soulFireLayer0 = new AnimatedTexture("soul_fire");
  public static final AnimatedTexture soulFireLayer1 = new AnimatedTexture("soul_fire");
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
  public static final Texture copperOre = new Texture();
  public static final Texture calcite = new Texture();
  public static final Texture tuff = new Texture();
  public static final Texture amethyst = new Texture();
  public static final Texture buddingAmethyst = new Texture();
  public static final Texture copperBlock = new Texture();
  public static final Texture lightlyWeatheredCopperBlock = new Texture();
  public static final Texture semiWeatheredCopperBlock = new Texture();
  public static final Texture weatheredCopperBlock = new Texture();
  public static final Texture cutCopperBlock = new Texture();
  public static final Texture lightlyWeatheredCutCopperBlock = new Texture();
  public static final Texture semiWeatheredCutCopperBlock = new Texture();
  public static final Texture weatheredCutCopperBlock = new Texture();

  /** Banner base texture. */
  public static final Texture bannerBase = new Texture();

  public static final Texture armorStand = new Texture();

  // All the wool variants, ordered after block ID.
  public static final Texture[] wool = {
      whiteWool, orangeWool, magentaWool, lightBlueWool, yellowWool, limeWool, pinkWool, grayWool,
      lightGrayWool, cyanWool, purpleWool, blueWool, brownWool, greenWool, redWool, blackWool
  };

  public static final Texture[] concrete = {
      concreteWhite, concreteOrange, concreteMagenta, concreteLightBlue, concreteYellow,
      concreteLime, concretePink, concreteGray, concreteSilver, concreteCyan, concretePurple,
      concreteBlue, concreteBrown, concreteGreen, concreteRed, concreteBlack
  };

  public static final Texture[] concretePowder = {
      concretePowderWhite, concretePowderOrange, concretePowderMagenta, concretePowderLightBlue, concretePowderYellow,
      concretePowderLime, concretePowderPink, concretePowderGray, concretePowderSilver, concretePowderCyan, concretePowderPurple,
      concretePowderBlue, concretePowderBrown, concretePowderGreen, concretePowderRed, concretePowderBlack
  };

  public static final Texture[] stainedGlass = {
      whiteGlass, orangeGlass, magentaGlass, lightBlueGlass, yellowGlass, limeGlass, pinkGlass,
      grayGlass, lightGrayGlass, cyanGlass, purpleGlass, blueGlass, brownGlass, greenGlass,
      redGlass, blackGlass
  };

  public static final Texture[] stainedGlassPaneSide = {
      whiteGlassPaneSide, orangeGlassPaneSide, magentaGlassPaneSide, lightBlueGlassPaneSide,
      yellowGlassPaneSide, limeGlassPaneSide, pinkGlassPaneSide, grayGlassPaneSide,
      lightGrayGlassPaneSide, cyanGlassPaneSide, purpleGlassPaneSide, blueGlassPaneSide,
      brownGlassPaneSide, greenGlassPaneSide, redGlassPaneSide, blackGlassPaneSide
  };

  public static final Texture[] stainedClay = {
      whiteClay, orangeClay, magentaClay, lightBlueClay, yellowClay, limeClay, pinkClay, grayClay,
      lightGrayClay, cyanClay, purpleClay, blueClay, brownClay, greenClay, redClay, blackClay
  };

  @NotNull protected BitmapImage image;
  protected int width;
  protected int height;
  protected int avgColor;
  private float[] avgColorLinear;
  private boolean useAverageColor = false;
  private float[] avgColorFlat;

  private Image fxImage = null;

  public Texture() {
    this(ImageLoader.missingImage);
  }

  public Texture(String resourceName) {
    this(ImageLoader.readNonNull("textures/" + resourceName + ".png"));
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

    useAverageColor = PersistentSettings.getSingleColorTextures();
    if (useAverageColor) {
      avgColorFlat = new float[4];
      if (avgColorLinear[3] > 0.001) {
        avgColorFlat[0] = avgColorLinear[0] / avgColorLinear[3];
        avgColorFlat[1] = avgColorLinear[1] / avgColorLinear[3];
        avgColorFlat[2] = avgColorLinear[2] / avgColorLinear[3];
        avgColorFlat[3] = 1;
      }
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
  public final float[] getColor(int x, int y) {
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
      fxImage = ImageTools.toFxImage(image);
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
}
