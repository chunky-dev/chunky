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

import se.llbit.chunky.resources.texture.AnimatedTexture;
import se.llbit.chunky.resources.texture.BitmapTexture;
import se.llbit.chunky.resources.texture.SolidColorTexture;
import se.llbit.chunky.resources.texturepack.FontTexture;
import se.llbit.chunky.resources.texturepack.TexturePath;

/**
 * This class contains static fields for common textures.
 *
 * <p>The Texture type is used for all textures used in the renderer.
 * It mostly serves as a data object wrapping a BitmapImage.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Texture {
  public static SolidColorTexture black = new SolidColorTexture(0, 0, 0, 1);

  public static final BitmapTexture paintingAlban = new BitmapTexture();
  public static final BitmapTexture paintingAztec2 = new BitmapTexture();
  public static final BitmapTexture paintingAztec = new BitmapTexture();
  public static final BitmapTexture paintingBack = new BitmapTexture();
  public static final BitmapTexture paintingBomb = new BitmapTexture();
  public static final BitmapTexture paintingBurningSkull = new BitmapTexture();
  public static final BitmapTexture paintingBust = new BitmapTexture();
  public static final BitmapTexture paintingCourbet = new BitmapTexture();
  public static final BitmapTexture paintingCreebet = new BitmapTexture();
  public static final BitmapTexture paintingDonkeyKong = new BitmapTexture();
  public static final BitmapTexture paintingFighters = new BitmapTexture();
  public static final BitmapTexture paintingGraham = new BitmapTexture();
  public static final BitmapTexture paintingKebab = new BitmapTexture();
  public static final BitmapTexture paintingMatch = new BitmapTexture();
  public static final BitmapTexture paintingPigscene = new BitmapTexture();
  public static final BitmapTexture paintingPlant = new BitmapTexture();
  public static final BitmapTexture paintingPointer = new BitmapTexture();
  public static final BitmapTexture paintingPool = new BitmapTexture();
  public static final BitmapTexture paintingSea = new BitmapTexture();
  public static final BitmapTexture paintingSkeleton = new BitmapTexture();
  public static final BitmapTexture paintingSkullAndRoses = new BitmapTexture();
  public static final BitmapTexture paintingStage = new BitmapTexture();
  public static final BitmapTexture paintingSunset = new BitmapTexture();
  public static final BitmapTexture paintingVoid = new BitmapTexture();
  public static final BitmapTexture paintingWanderer = new BitmapTexture();
  public static final BitmapTexture paintingWasteland = new BitmapTexture();
  public static final BitmapTexture paintingWither = new BitmapTexture();

  public static final FontTexture fonts = new FontTexture();

  public static final BitmapTexture air = new BitmapTexture("air");
  public static final BitmapTexture stone = new BitmapTexture("stone");
  public static final BitmapTexture prismarine = new BitmapTexture();
  public static final BitmapTexture prismarineBricks = new BitmapTexture();
  public static final BitmapTexture darkPrismarine = new BitmapTexture();
  public static final BitmapTexture granite = new BitmapTexture();
  public static final BitmapTexture smoothGranite = new BitmapTexture();
  public static final BitmapTexture diorite = new BitmapTexture();
  public static final BitmapTexture smoothDiorite = new BitmapTexture();
  public static final BitmapTexture andesite = new BitmapTexture();
  public static final BitmapTexture smoothAndesite = new BitmapTexture();
  public static final BitmapTexture dirt = new BitmapTexture("dirt");
  public static final BitmapTexture coarseDirt = new BitmapTexture("dirt");
  public static final BitmapTexture grassSideSaturated = new BitmapTexture("grass-side-saturated");
  public static final BitmapTexture grassTop = new BitmapTexture("grass");
  public static final BitmapTexture grassSide = new BitmapTexture();
  public static final BitmapTexture water = new BitmapTexture("water");
  public static final BitmapTexture cauldronSide = new BitmapTexture();
  public static final BitmapTexture cauldronInside = new BitmapTexture();
  public static final BitmapTexture cauldronTop = new BitmapTexture();
  public static final BitmapTexture cauldronBottom = new BitmapTexture();
  public static final BitmapTexture smoothStone = new BitmapTexture();
  public static final BitmapTexture smoothStoneSlabSide = new BitmapTexture();
  public static final BitmapTexture brick = new BitmapTexture("bricks");
  public static final BitmapTexture tntTop = new BitmapTexture();
  public static final BitmapTexture tntSide = new BitmapTexture("tnt");
  public static final BitmapTexture tntBottom = new BitmapTexture();
  public static final BitmapTexture cobweb = new BitmapTexture("cobweb");
  public static final BitmapTexture portal = new BitmapTexture("nether-portal");
  public static final BitmapTexture cobblestone = new BitmapTexture("cobblestone");
  public static final BitmapTexture bedrock = new BitmapTexture("bedrock");
  public static final BitmapTexture sand = new BitmapTexture("sand");
  public static final BitmapTexture gravel = new BitmapTexture("gravel");
  public static final BitmapTexture ironBlock = new BitmapTexture("iron-block");
  public static final BitmapTexture goldBlock = new BitmapTexture("gold-block");
  public static final BitmapTexture diamondBlock = new BitmapTexture("diamond-block");
  public static final BitmapTexture chestTop = new BitmapTexture();
  public static final BitmapTexture chestBottom = new BitmapTexture();
  public static final BitmapTexture chestLeft = new BitmapTexture();
  public static final BitmapTexture chestRight = new BitmapTexture();
  public static final BitmapTexture chestFront = new BitmapTexture("chest");
  public static final BitmapTexture chestBack = new BitmapTexture();
  public static final BitmapTexture chestLock = new BitmapTexture();
  public static final BitmapTexture enderChestTop = new BitmapTexture();
  public static final BitmapTexture enderChestBottom = new BitmapTexture();
  public static final BitmapTexture enderChestLeft = new BitmapTexture();
  public static final BitmapTexture enderChestRight = new BitmapTexture();
  public static final BitmapTexture enderChestFront = new BitmapTexture();
  public static final BitmapTexture enderChestBack = new BitmapTexture();
  public static final BitmapTexture enderChestLock = new BitmapTexture();
  public static final BitmapTexture redMushroom = new BitmapTexture("red-mushroom");
  public static final BitmapTexture brownMushroom = new BitmapTexture("brown-mushroom");
  public static final BitmapTexture goldOre = new BitmapTexture("gold-ore");
  public static final BitmapTexture ironOre = new BitmapTexture("iron-ore");
  public static final BitmapTexture coalOre = new BitmapTexture("coal-ore");
  public static final BitmapTexture netherQuartzOre = new BitmapTexture();
  public static final BitmapTexture bookshelf = new BitmapTexture("bookshelf");
  public static final BitmapTexture mossStone = new BitmapTexture("moss-stone");
  public static final BitmapTexture obsidian = new BitmapTexture("obsidian");
  public static final BitmapTexture workbenchTop = new BitmapTexture();
  public static final BitmapTexture workbenchSide = new BitmapTexture("workbench");
  public static final BitmapTexture workbenchFront = new BitmapTexture("workbench");
  public static final BitmapTexture furnaceTop = new BitmapTexture();
  public static final BitmapTexture furnaceSide = new BitmapTexture();
  public static final BitmapTexture furnaceUnlitFront = new BitmapTexture("furnace");
  public static final BitmapTexture furnaceLitFront = new BitmapTexture("furnace-lit");
  public static final BitmapTexture dispenserFront = new BitmapTexture("dispenser");
  public static final BitmapTexture dispenserFrontVertical = new BitmapTexture("dispenser");
  public static final BitmapTexture dropperFront = new BitmapTexture();
  public static final BitmapTexture dropperFrontVertical = new BitmapTexture();
  public static final BitmapTexture sponge = new BitmapTexture("sponge");
  public static final BitmapTexture wetSponge = new BitmapTexture("sponge");
  public static final BitmapTexture glass = new BitmapTexture("glass");
  public static final BitmapTexture diamondOre = new BitmapTexture("diamond-ore");
  public static final BitmapTexture redstoneOre = new BitmapTexture("redstone-ore");
  public static final BitmapTexture stoneBrick = new BitmapTexture("stone-bricks");
  public static final BitmapTexture mossyStoneBrick = new BitmapTexture();
  public static final BitmapTexture crackedStoneBrick = new BitmapTexture();
  public static final BitmapTexture circleStoneBrick = new BitmapTexture();
  public static final BitmapTexture monsterSpawner = new BitmapTexture("spawner");
  public static final BitmapTexture snowBlock = new BitmapTexture("snow");
  public static final BitmapTexture snowSide = new BitmapTexture();
  public static final BitmapTexture ice = new BitmapTexture("ice");
  public static final BitmapTexture cactusTop = new BitmapTexture();
  public static final BitmapTexture cactusSide = new BitmapTexture();
  public static final BitmapTexture cactusBottom = new BitmapTexture();
  public static final BitmapTexture clay = new BitmapTexture("clay");
  public static final BitmapTexture sugarCane = new BitmapTexture("sugar-canes");
  public static final BitmapTexture jukeboxSide = new BitmapTexture();
  public static final BitmapTexture jukeboxTop = new BitmapTexture();
  public static final BitmapTexture noteBlock = new BitmapTexture();
  public static final BitmapTexture torch = new BitmapTexture("torch");
  public static final BitmapTexture oakDoorTop = new BitmapTexture();
  public static final BitmapTexture oakDoorBottom = new BitmapTexture();
  public static final BitmapTexture ironDoorTop = new BitmapTexture();
  public static final BitmapTexture ironDoorBottom = new BitmapTexture();
  public static final BitmapTexture ladder = new BitmapTexture("ladder");
  public static final BitmapTexture trapdoor = new BitmapTexture("trapdoor");
  public static final BitmapTexture ironTrapdoor = new BitmapTexture("trapdoor");
  public static final BitmapTexture birchTrapdoor = new BitmapTexture("trapdoor");
  public static final BitmapTexture spruceTrapdoor = new BitmapTexture("trapdoor");
  public static final BitmapTexture jungleTrapdoor = new BitmapTexture("trapdoor");
  public static final BitmapTexture acaciaTrapdoor = new BitmapTexture("trapdoor");
  public static final BitmapTexture darkOakTrapdoor = new BitmapTexture("trapdoor");
  public static final BitmapTexture ironBars = new BitmapTexture("iron-bars");
  public static final BitmapTexture farmlandWet = new BitmapTexture();
  public static final BitmapTexture farmlandDry = new BitmapTexture();
  public static final BitmapTexture lever = new BitmapTexture();
  public static final BitmapTexture redstoneTorchOn = new BitmapTexture("redstone-torch-on");
  public static final BitmapTexture redstoneTorchOff = new BitmapTexture("redstone-torch-off");
  public static final BitmapTexture redstoneWireCross = new BitmapTexture("redstone-wire-off-intersect");
  public static final BitmapTexture redstoneWire = new BitmapTexture();
  public static final BitmapTexture pumpkinTop = new BitmapTexture();
  public static final BitmapTexture pumpkinSide = new BitmapTexture();
  public static final BitmapTexture pumpkinFront = new BitmapTexture();
  public static final BitmapTexture jackolanternFront = new BitmapTexture();
  public static final BitmapTexture netherrack = new BitmapTexture("netherrack");
  public static final BitmapTexture soulsand = new BitmapTexture("soul-sand");
  public static final BitmapTexture glowstone = new BitmapTexture("glowstone");
  public static final BitmapTexture seaLantern = new BitmapTexture();
  public static final BitmapTexture sandstoneSide = new BitmapTexture("sandstone");
  public static final BitmapTexture sandstoneTop = new BitmapTexture();
  public static final BitmapTexture sandstoneBottom = new BitmapTexture();
  public static final BitmapTexture sandstoneDecorated = new BitmapTexture();
  public static final BitmapTexture sandstoneCut = new BitmapTexture();
  public static final BitmapTexture redSandstoneSide = new BitmapTexture();
  public static final BitmapTexture redSandstoneTop = new BitmapTexture();
  public static final BitmapTexture redSandstoneBottom = new BitmapTexture();
  public static final BitmapTexture redSandstoneDecorated = new BitmapTexture();
  public static final BitmapTexture redSandstoneCut = new BitmapTexture();
  public static final BitmapTexture deadBush = new BitmapTexture("dead-bush");
  public static final BitmapTexture tallGrass = new BitmapTexture("tall-grass");
  public static final BitmapTexture fern = new BitmapTexture();
  public static final BitmapTexture vines = new BitmapTexture("vines");
  public static final BitmapTexture crops0 = new BitmapTexture();
  public static final BitmapTexture crops1 = new BitmapTexture();
  public static final BitmapTexture crops2 = new BitmapTexture();
  public static final BitmapTexture crops3 = new BitmapTexture();
  public static final BitmapTexture crops4 = new BitmapTexture();
  public static final BitmapTexture crops5 = new BitmapTexture();
  public static final BitmapTexture crops6 = new BitmapTexture();
  public static final BitmapTexture crops7 = new BitmapTexture();
  public static final BitmapTexture rails = new BitmapTexture("minecart-track");
  public static final BitmapTexture railsCurved = new BitmapTexture();
  public static final BitmapTexture poweredRailOn = new BitmapTexture();
  public static final BitmapTexture poweredRailOff = new BitmapTexture();
  public static final BitmapTexture detectorRail = new BitmapTexture();
  public static final BitmapTexture detectorRailOn = new BitmapTexture();
  public static final BitmapTexture activatorRail = new BitmapTexture();
  public static final BitmapTexture activatorRailPowered = new BitmapTexture();
  public static final BitmapTexture whiteWool = new BitmapTexture("wool");
  public static final BitmapTexture orangeWool = new BitmapTexture("wool");
  public static final BitmapTexture magentaWool = new BitmapTexture("wool");
  public static final BitmapTexture lightBlueWool = new BitmapTexture("wool");
  public static final BitmapTexture yellowWool = new BitmapTexture("wool");
  public static final BitmapTexture limeWool = new BitmapTexture("wool");
  public static final BitmapTexture pinkWool = new BitmapTexture("wool");
  public static final BitmapTexture grayWool = new BitmapTexture("wool");
  public static final BitmapTexture lightGrayWool = new BitmapTexture("wool");
  public static final BitmapTexture cyanWool = new BitmapTexture("wool");
  public static final BitmapTexture purpleWool = new BitmapTexture("wool");
  public static final BitmapTexture blueWool = new BitmapTexture("wool");
  public static final BitmapTexture brownWool = new BitmapTexture("wool");
  public static final BitmapTexture greenWool = new BitmapTexture("wool");
  public static final BitmapTexture redWool = new BitmapTexture("wool");
  public static final BitmapTexture blackWool = new BitmapTexture("wool");
  public static final BitmapTexture lava = new BitmapTexture("lava");
  public static final BitmapTexture lapisOre = new BitmapTexture("lapis-lazuli-ore");
  public static final BitmapTexture lapisBlock = new BitmapTexture("lapis-lazuli-block");
  public static final BitmapTexture pistonSide = new BitmapTexture("piston");
  public static final BitmapTexture pistonInnerTop = new BitmapTexture();
  public static final BitmapTexture pistonBottom = new BitmapTexture();
  public static final BitmapTexture pistonTop = new BitmapTexture("piston-extension");
  public static final BitmapTexture pistonTopSticky = new BitmapTexture();
  public static final BitmapTexture fire = new BitmapTexture("fire");
  public static final AnimatedTexture fireLayer0 = new AnimatedTexture("fire");
  public static final AnimatedTexture fireLayer1 = new AnimatedTexture("fire");
  public static final BitmapTexture redstoneRepeaterOn = new BitmapTexture("redstone-repeater-off");
  public static final BitmapTexture redstoneRepeaterOff = new BitmapTexture("redstone-repeater-on");
  public static final BitmapTexture redstoneLampOn = new BitmapTexture("redstone-lamp-off");
  public static final BitmapTexture redstoneLampOff = new BitmapTexture("redstone-lamp-on");
  public static final BitmapTexture endPortalFrameTop = new BitmapTexture();
  public static final BitmapTexture endPortalFrameSide = new BitmapTexture();
  public static final BitmapTexture endPortal = new BitmapTexture("end-portal");
  public static final BitmapTexture endStone = new BitmapTexture("end-stone");
  public static final BitmapTexture brewingStandSide = new BitmapTexture();
  public static final BitmapTexture brewingStandBase = new BitmapTexture();
  public static final BitmapTexture cakeTop = new BitmapTexture();
  public static final BitmapTexture cakeSide = new BitmapTexture();
  public static final BitmapTexture cakeInside = new BitmapTexture();
  public static final BitmapTexture cakeBottom = new BitmapTexture();
  public static final BitmapTexture hugeBrownMushroom = new BitmapTexture();
  public static final BitmapTexture hugeRedMushroom = new BitmapTexture();
  public static final BitmapTexture mushroomStem = new BitmapTexture();
  public static final BitmapTexture mushroomPores = new BitmapTexture();
  public static final BitmapTexture melonSide = new BitmapTexture();
  public static final BitmapTexture melonTop = new BitmapTexture();
  public static final BitmapTexture stemStraight = new BitmapTexture();
  public static final BitmapTexture stemBent = new BitmapTexture();
  public static final BitmapTexture myceliumTop = new BitmapTexture();
  public static final BitmapTexture myceliumSide = new BitmapTexture();
  public static final BitmapTexture lilyPad = new BitmapTexture("lily-pad");
  public static final BitmapTexture netherBrick = new BitmapTexture();
  public static final BitmapTexture netherWart0 = new BitmapTexture();
  public static final BitmapTexture netherWart1 = new BitmapTexture();
  public static final BitmapTexture netherWart2 = new BitmapTexture();
  public static final BitmapTexture largeChestFrontLeft = new BitmapTexture();
  public static final BitmapTexture largeChestFrontRight = new BitmapTexture();
  public static final BitmapTexture largeChestBackLeft = new BitmapTexture();
  public static final BitmapTexture largeChestBackRight = new BitmapTexture();
  public static final BitmapTexture largeChestTopLeft = new BitmapTexture();
  public static final BitmapTexture largeChestTopRight = new BitmapTexture();
  public static final BitmapTexture largeChestBottomLeft = new BitmapTexture();
  public static final BitmapTexture largeChestBottomRight = new BitmapTexture();
  public static final BitmapTexture largeChestLeft = new BitmapTexture();
  public static final BitmapTexture largeChestRight = new BitmapTexture();
  public static final BitmapTexture enchantmentTableSide = new BitmapTexture();
  public static final BitmapTexture enchantmentTableTop = new BitmapTexture();
  public static final BitmapTexture enchantmentTableBottom = new BitmapTexture();

  public static final BitmapTexture commandBlockBack = new BitmapTexture();
  public static final BitmapTexture commandBlockFront = new BitmapTexture();
  public static final BitmapTexture commandBlockSide = new BitmapTexture();
  public static final BitmapTexture commandBlockConditional = new BitmapTexture();

  public static final BitmapTexture repeatingCommandBlockBack = new BitmapTexture();
  public static final BitmapTexture repeatingCommandBlockFront = new BitmapTexture();
  public static final BitmapTexture repeatingCommandBlockSide = new BitmapTexture();
  public static final BitmapTexture repeatingCommandBlockConditional = new BitmapTexture();

  public static final BitmapTexture chainCommandBlockBack = new BitmapTexture();
  public static final BitmapTexture chainCommandBlockFront = new BitmapTexture();
  public static final BitmapTexture chainCommandBlockSide = new BitmapTexture();
  public static final BitmapTexture chainCommandBlockConditional = new BitmapTexture();

  public static final BitmapTexture eyeOfTheEnder = new BitmapTexture();
  public static final BitmapTexture dragonEgg = new BitmapTexture();
  public static final BitmapTexture cocoaPlantSmall = new BitmapTexture();
  public static final BitmapTexture cocoaPlantMedium = new BitmapTexture();
  public static final BitmapTexture cocoaPlantLarge = new BitmapTexture();
  public static final BitmapTexture emeraldOre = new BitmapTexture();
  public static final BitmapTexture emeraldBlock = new BitmapTexture();
  public static final BitmapTexture redstoneBlock = new BitmapTexture();
  public static final BitmapTexture tripwireHook = new BitmapTexture();
  public static final BitmapTexture tripwire = new BitmapTexture();
  public static final BitmapTexture carrots0 = new BitmapTexture();
  public static final BitmapTexture carrots1 = new BitmapTexture();
  public static final BitmapTexture carrots2 = new BitmapTexture();
  public static final BitmapTexture carrots3 = new BitmapTexture();
  public static final BitmapTexture potatoes0 = new BitmapTexture();
  public static final BitmapTexture potatoes1 = new BitmapTexture();
  public static final BitmapTexture potatoes2 = new BitmapTexture();
  public static final BitmapTexture potatoes3 = new BitmapTexture();
  public static final BitmapTexture beacon = new BitmapTexture();
  public static final BitmapTexture beaconBeam = new BitmapTexture();
  public static final BitmapTexture anvilSide = new BitmapTexture();
  public static final BitmapTexture anvilTop = new BitmapTexture();
  public static final BitmapTexture anvilTopDamaged1 = new BitmapTexture();
  public static final BitmapTexture anvilTopDamaged2 = new BitmapTexture();
  public static final BitmapTexture flowerPot = new BitmapTexture();
  public static final BitmapTexture quartzSide = new BitmapTexture();
  public static final BitmapTexture quartzTop = new BitmapTexture();
  public static final BitmapTexture quartzBottom = new BitmapTexture();
  public static final BitmapTexture quartzChiseled = new BitmapTexture();
  public static final BitmapTexture quartzChiseledTop = new BitmapTexture();
  public static final BitmapTexture quartzPillar = new BitmapTexture();
  public static final BitmapTexture quartzPillarTop = new BitmapTexture();
  public static final BitmapTexture daylightDetectorTop = new BitmapTexture();
  public static final BitmapTexture daylightDetectorInvertedTop = new BitmapTexture();
  public static final BitmapTexture daylightDetectorSide = new BitmapTexture();
  public static final BitmapTexture comparatorOff = new BitmapTexture();
  public static final BitmapTexture comparatorOn = new BitmapTexture();
  public static final BitmapTexture hopperOutside = new BitmapTexture();
  public static final BitmapTexture hopperInside = new BitmapTexture();
  public static final BitmapTexture hopperTop = new BitmapTexture();
  public static final BitmapTexture slime = new BitmapTexture();

  // [1.6] Hay Block, Hardened Clay, Coal Block
  public static final BitmapTexture hayBlockSide = new BitmapTexture();
  public static final BitmapTexture hayBlockTop = new BitmapTexture();
  public static final BitmapTexture hardenedClay = new BitmapTexture();
  public static final BitmapTexture coalBlock = new BitmapTexture();

  // [1.6] Colored Clay
  public static final BitmapTexture whiteClay = new BitmapTexture();
  public static final BitmapTexture orangeClay = new BitmapTexture();
  public static final BitmapTexture magentaClay = new BitmapTexture();
  public static final BitmapTexture lightBlueClay = new BitmapTexture();
  public static final BitmapTexture yellowClay = new BitmapTexture();
  public static final BitmapTexture limeClay = new BitmapTexture();
  public static final BitmapTexture pinkClay = new BitmapTexture();
  public static final BitmapTexture grayClay = new BitmapTexture();
  public static final BitmapTexture lightGrayClay = new BitmapTexture();
  public static final BitmapTexture cyanClay = new BitmapTexture();
  public static final BitmapTexture purpleClay = new BitmapTexture();
  public static final BitmapTexture blueClay = new BitmapTexture();
  public static final BitmapTexture brownClay = new BitmapTexture();
  public static final BitmapTexture greenClay = new BitmapTexture();
  public static final BitmapTexture redClay = new BitmapTexture();
  public static final BitmapTexture blackClay = new BitmapTexture();

  // [1.7.2] Stained Glass
  public static final BitmapTexture whiteGlass = new BitmapTexture("glass");
  public static final BitmapTexture orangeGlass = new BitmapTexture("glass");
  public static final BitmapTexture magentaGlass = new BitmapTexture("glass");
  public static final BitmapTexture lightBlueGlass = new BitmapTexture("glass");
  public static final BitmapTexture yellowGlass = new BitmapTexture("glass");
  public static final BitmapTexture limeGlass = new BitmapTexture("glass");
  public static final BitmapTexture pinkGlass = new BitmapTexture("glass");
  public static final BitmapTexture grayGlass = new BitmapTexture("glass");
  public static final BitmapTexture lightGrayGlass = new BitmapTexture("glass");
  public static final BitmapTexture cyanGlass = new BitmapTexture("glass");
  public static final BitmapTexture purpleGlass = new BitmapTexture("glass");
  public static final BitmapTexture blueGlass = new BitmapTexture("glass");
  public static final BitmapTexture brownGlass = new BitmapTexture("glass");
  public static final BitmapTexture greenGlass = new BitmapTexture("glass");
  public static final BitmapTexture redGlass = new BitmapTexture("glass");
  public static final BitmapTexture blackGlass = new BitmapTexture("glass");

  // [1.7.2] Podzol
  public static final BitmapTexture podzolTop = new BitmapTexture();
  public static final BitmapTexture podzolSide = new BitmapTexture();

  // [1.7.2] Flowers
  public static final BitmapTexture dandelion = new BitmapTexture("yellow-flower");
  public static final BitmapTexture poppy = new BitmapTexture("rose");
  public static final BitmapTexture blueOrchid = new BitmapTexture();
  public static final BitmapTexture allium = new BitmapTexture();
  public static final BitmapTexture azureBluet = new BitmapTexture();
  public static final BitmapTexture redTulip = new BitmapTexture();
  public static final BitmapTexture orangeTulip = new BitmapTexture();
  public static final BitmapTexture whiteTulip = new BitmapTexture();
  public static final BitmapTexture pinkTulip = new BitmapTexture();
  public static final BitmapTexture oxeyeDaisy = new BitmapTexture();

  // [1.7.2] Large Flowers
  public static final BitmapTexture sunflowerBottom = new BitmapTexture();
  public static final BitmapTexture sunflowerTop = new BitmapTexture();
  public static final BitmapTexture sunflowerFront = new BitmapTexture();
  public static final BitmapTexture sunflowerBack = new BitmapTexture();
  public static final BitmapTexture lilacBottom = new BitmapTexture();
  public static final BitmapTexture lilacTop = new BitmapTexture();
  public static final BitmapTexture doubleTallGrassBottom = new BitmapTexture();
  public static final BitmapTexture doubleTallGrassTop = new BitmapTexture();
  public static final BitmapTexture largeFernBottom = new BitmapTexture();
  public static final BitmapTexture largeFernTop = new BitmapTexture();
  public static final BitmapTexture roseBushBottom = new BitmapTexture();
  public static final BitmapTexture roseBushTop = new BitmapTexture();
  public static final BitmapTexture peonyBottom = new BitmapTexture();
  public static final BitmapTexture peonyTop = new BitmapTexture();

  // [1.7.2] Colored Glass Panes
  public static final BitmapTexture glassPaneTop = new BitmapTexture();
  public static final BitmapTexture whiteGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture orangeGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture magentaGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture lightBlueGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture yellowGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture limeGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture pinkGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture grayGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture lightGrayGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture cyanGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture purpleGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture blueGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture brownGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture greenGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture redGlassPaneSide = new BitmapTexture();
  public static final BitmapTexture blackGlassPaneSide = new BitmapTexture();

  // Minecraft 1.9 blocks.
  public static final BitmapTexture grassPathSide = new BitmapTexture();
  public static final BitmapTexture grassPathTop = new BitmapTexture();
  public static final BitmapTexture endBricks = new BitmapTexture();
  public static final BitmapTexture purpurBlock = new BitmapTexture();
  public static final BitmapTexture purpurPillarTop = new BitmapTexture();
  public static final BitmapTexture purpurPillarSide = new BitmapTexture();
  public static final BitmapTexture chorusFlower = new BitmapTexture();
  public static final BitmapTexture chorusFlowerDead = new BitmapTexture();
  public static final BitmapTexture chorusPlant = new BitmapTexture();
  public static final BitmapTexture endRod = new BitmapTexture();

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
  public static final BitmapTexture observerBack = new BitmapTexture();
  public static final BitmapTexture observerBackOn = new BitmapTexture();
  public static final BitmapTexture observerFront = new BitmapTexture();
  public static final BitmapTexture observerSide = new BitmapTexture();
  public static final BitmapTexture observerTop = new BitmapTexture();

  // Trapped Chest.
  public static final BitmapTexture trappedChestTop = new BitmapTexture();
  public static final BitmapTexture trappedChestBottom = new BitmapTexture();
  public static final BitmapTexture trappedChestLeft = new BitmapTexture();
  public static final BitmapTexture trappedChestRight = new BitmapTexture();
  public static final BitmapTexture trappedChestFront = new BitmapTexture("chest");
  public static final BitmapTexture trappedChestBack = new BitmapTexture();
  public static final BitmapTexture trappedChestLock = new BitmapTexture();

  public static final BitmapTexture largeTrappedChestFrontLeft = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestFrontRight = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestBackLeft = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestBackRight = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestTopLeft = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestTopRight = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestBottomLeft = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestBottomRight = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestLeft = new BitmapTexture();
  public static final BitmapTexture largeTrappedChestRight = new BitmapTexture();

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
  public static final BitmapTexture boneSide = new BitmapTexture();
  public static final BitmapTexture boneTop = new BitmapTexture();
  public static final BitmapTexture magma = new BitmapTexture();
  public static final BitmapTexture netherWartBlock = new BitmapTexture();
  public static final BitmapTexture redNetherBrick = new BitmapTexture();

  // [1.12] Glazed Terracotta:
  public static final BitmapTexture terracottaBlack = new BitmapTexture();
  public static final BitmapTexture terracottaBlue = new BitmapTexture();
  public static final BitmapTexture terracottaBrown = new BitmapTexture();
  public static final BitmapTexture terracottaCyan = new BitmapTexture();
  public static final BitmapTexture terracottaGray = new BitmapTexture();
  public static final BitmapTexture terracottaGreen = new BitmapTexture();
  public static final BitmapTexture terracottaLightBlue = new BitmapTexture();
  public static final BitmapTexture terracottaLime = new BitmapTexture();
  public static final BitmapTexture terracottaMagenta = new BitmapTexture();
  public static final BitmapTexture terracottaOrange = new BitmapTexture();
  public static final BitmapTexture terracottaPink = new BitmapTexture();
  public static final BitmapTexture terracottaPurple = new BitmapTexture();
  public static final BitmapTexture terracottaRed = new BitmapTexture();
  public static final BitmapTexture terracottaSilver = new BitmapTexture();
  public static final BitmapTexture terracottaWhite = new BitmapTexture();
  public static final BitmapTexture terracottaYellow = new BitmapTexture();

  // [1.12] Concrete:
  public static final BitmapTexture concreteBlack = new BitmapTexture();
  public static final BitmapTexture concreteBlue = new BitmapTexture();
  public static final BitmapTexture concreteBrown = new BitmapTexture();
  public static final BitmapTexture concreteCyan = new BitmapTexture();
  public static final BitmapTexture concreteGray = new BitmapTexture();
  public static final BitmapTexture concreteGreen = new BitmapTexture();
  public static final BitmapTexture concreteLightBlue = new BitmapTexture();
  public static final BitmapTexture concreteLime = new BitmapTexture();
  public static final BitmapTexture concreteMagenta = new BitmapTexture();
  public static final BitmapTexture concreteOrange = new BitmapTexture();
  public static final BitmapTexture concretePink = new BitmapTexture();
  public static final BitmapTexture concretePurple = new BitmapTexture();
  public static final BitmapTexture concreteRed = new BitmapTexture();
  public static final BitmapTexture concreteSilver = new BitmapTexture();
  public static final BitmapTexture concreteWhite = new BitmapTexture();
  public static final BitmapTexture concreteYellow = new BitmapTexture();

  public static final BitmapTexture concretePowderBlack = new BitmapTexture();
  public static final BitmapTexture concretePowderBlue = new BitmapTexture();
  public static final BitmapTexture concretePowderBrown = new BitmapTexture();
  public static final BitmapTexture concretePowderCyan = new BitmapTexture();
  public static final BitmapTexture concretePowderGray = new BitmapTexture();
  public static final BitmapTexture concretePowderGreen = new BitmapTexture();
  public static final BitmapTexture concretePowderLightBlue = new BitmapTexture();
  public static final BitmapTexture concretePowderLime = new BitmapTexture();
  public static final BitmapTexture concretePowderMagenta = new BitmapTexture();
  public static final BitmapTexture concretePowderOrange = new BitmapTexture();
  public static final BitmapTexture concretePowderPink = new BitmapTexture();
  public static final BitmapTexture concretePowderPurple = new BitmapTexture();
  public static final BitmapTexture concretePowderRed = new BitmapTexture();
  public static final BitmapTexture concretePowderSilver = new BitmapTexture();
  public static final BitmapTexture concretePowderWhite = new BitmapTexture();
  public static final BitmapTexture concretePowderYellow = new BitmapTexture();

  // [1.12] Beetroots:
  public static final BitmapTexture beets0 = new BitmapTexture();
  public static final BitmapTexture beets1 = new BitmapTexture();
  public static final BitmapTexture beets2 = new BitmapTexture();
  public static final BitmapTexture beets3 = new BitmapTexture();

  /**
   * Missing or unknown texture.
   */
  public static final BitmapTexture unknown = new BitmapTexture("unknown");

  public static final BitmapTexture oakSignPost = new BitmapTexture();
  public static final BitmapTexture packedIce = new BitmapTexture();
  public static final BitmapTexture redSand = new BitmapTexture();

  // Tree variants.
  public static final BitmapTexture oakWoodTop = new BitmapTexture("wood-top");
  public static final BitmapTexture spruceWoodTop = new BitmapTexture("wood-top");
  public static final BitmapTexture birchWoodTop = new BitmapTexture("wood-top");
  public static final BitmapTexture jungleTreeTop = new BitmapTexture("wood-top");
  public static final BitmapTexture acaciaWoodTop = new BitmapTexture("wood-top");
  public static final BitmapTexture darkOakWoodTop = new BitmapTexture("wood-top");

  public static final BitmapTexture oakLeaves = new BitmapTexture("leaves");
  public static final BitmapTexture spruceLeaves = new BitmapTexture("leaves");
  public static final BitmapTexture birchLeaves = new BitmapTexture("leaves");
  public static final BitmapTexture jungleTreeLeaves = new BitmapTexture("leaves");
  public static final BitmapTexture acaciaLeaves = new BitmapTexture("leaves");
  public static final BitmapTexture darkOakLeaves = new BitmapTexture("leaves");

  public static final BitmapTexture oakSapling = new BitmapTexture("sapling");
  public static final BitmapTexture spruceSapling = new BitmapTexture("sapling");
  public static final BitmapTexture birchSapling = new BitmapTexture("sapling");
  public static final BitmapTexture jungleSapling = new BitmapTexture("sapling");
  public static final BitmapTexture acaciaSapling = new BitmapTexture("sapling");
  public static final BitmapTexture darkOakSapling = new BitmapTexture("sapling");

  public static final BitmapTexture oakPlanks = new BitmapTexture("wooden-planks");
  public static final BitmapTexture sprucePlanks = new BitmapTexture("wooden-planks");
  public static final BitmapTexture birchPlanks = new BitmapTexture("wooden-planks");
  public static final BitmapTexture jungleTreePlanks = new BitmapTexture("wooden-planks");
  public static final BitmapTexture acaciaPlanks = new BitmapTexture("wooden-planks");
  public static final BitmapTexture darkOakPlanks = new BitmapTexture("wooden-planks");

  public static final BitmapTexture oakWood = new BitmapTexture("wood");
  public static final BitmapTexture spruceWood = new BitmapTexture("wood");
  public static final BitmapTexture birchWood = new BitmapTexture("wood");
  public static final BitmapTexture jungleWood = new BitmapTexture("wood");
  public static final BitmapTexture acaciaWood = new BitmapTexture("wood");
  public static final BitmapTexture darkOakWood = new BitmapTexture("wood");

  public static final BitmapTexture frostedIce0 = new BitmapTexture();
  public static final BitmapTexture frostedIce1 = new BitmapTexture();
  public static final BitmapTexture frostedIce2 = new BitmapTexture();
  public static final BitmapTexture frostedIce3 = new BitmapTexture();

  public static final BitmapTexture spruceDoorTop = new BitmapTexture();
  public static final BitmapTexture birchDoorTop = new BitmapTexture();
  public static final BitmapTexture jungleDoorTop = new BitmapTexture();
  public static final BitmapTexture acaciaDoorTop = new BitmapTexture();
  public static final BitmapTexture darkOakDoorTop = new BitmapTexture();

  public static final BitmapTexture spruceDoorBottom = new BitmapTexture();
  public static final BitmapTexture birchDoorBottom = new BitmapTexture();
  public static final BitmapTexture jungleDoorBottom = new BitmapTexture();
  public static final BitmapTexture acaciaDoorBottom = new BitmapTexture();
  public static final BitmapTexture darkOakDoorBottom = new BitmapTexture();

  public static final BitmapTexture strippedOakLog = new BitmapTexture();
  public static final BitmapTexture strippedOakLogTop = new BitmapTexture();

  public static final BitmapTexture strippedDarkOakLog = new BitmapTexture();
  public static final BitmapTexture strippedDarkOakLogTop = new BitmapTexture();

  public static final BitmapTexture strippedSpruceLog = new BitmapTexture();
  public static final BitmapTexture strippedSpruceLogTop = new BitmapTexture();

  public static final BitmapTexture strippedBirchLog = new BitmapTexture();
  public static final BitmapTexture strippedBirchLogTop = new BitmapTexture();

  public static final BitmapTexture strippedJungleLog = new BitmapTexture();
  public static final BitmapTexture strippedJungleLogTop = new BitmapTexture();

  public static final BitmapTexture strippedAcaciaLog = new BitmapTexture();
  public static final BitmapTexture strippedAcaciaLogTop = new BitmapTexture();

  public static final BitmapTexture bedWhite = new BitmapTexture();
  public static final BitmapTexture bedOrange = new BitmapTexture();
  public static final BitmapTexture bedMagenta = new BitmapTexture();
  public static final BitmapTexture bedLightBlue = new BitmapTexture();
  public static final BitmapTexture bedYellow = new BitmapTexture();
  public static final BitmapTexture bedLime = new BitmapTexture();
  public static final BitmapTexture bedPink = new BitmapTexture();
  public static final BitmapTexture bedGray = new BitmapTexture();
  public static final BitmapTexture bedSilver = new BitmapTexture();
  public static final BitmapTexture bedCyan = new BitmapTexture();
  public static final BitmapTexture bedPurple = new BitmapTexture();
  public static final BitmapTexture bedBlue = new BitmapTexture();
  public static final BitmapTexture bedBrown = new BitmapTexture();
  public static final BitmapTexture bedGreen = new BitmapTexture();
  public static final BitmapTexture bedRed = new BitmapTexture();
  public static final BitmapTexture bedBlack = new BitmapTexture();

  // [1.13]
  public static final BitmapTexture kelp = new BitmapTexture();
  public static final BitmapTexture kelpPlant = new BitmapTexture();
  public static final BitmapTexture seagrass = new BitmapTexture();
  public static final BitmapTexture tallSeagrassTop = new BitmapTexture();
  public static final BitmapTexture tallSeagrassBottom = new BitmapTexture();

  public static final BitmapTexture driedKelpSide = new BitmapTexture();
  public static final BitmapTexture driedKelpTop = new BitmapTexture();
  public static final BitmapTexture driedKelpBottom = new BitmapTexture();

  public static final BitmapTexture tubeCoral = new BitmapTexture();
  public static final BitmapTexture brainCoral = new BitmapTexture();
  public static final BitmapTexture bubbleCoral = new BitmapTexture();
  public static final BitmapTexture fireCoral = new BitmapTexture();
  public static final BitmapTexture hornCoral = new BitmapTexture();

  public static final BitmapTexture tubeCoralBlock = new BitmapTexture();
  public static final BitmapTexture brainCoralBlock = new BitmapTexture();
  public static final BitmapTexture bubbleCoralBlock = new BitmapTexture();
  public static final BitmapTexture fireCoralBlock = new BitmapTexture();
  public static final BitmapTexture hornCoralBlock = new BitmapTexture();

  public static final BitmapTexture deadTubeCoralBlock = new BitmapTexture();
  public static final BitmapTexture deadBrainCoralBlock = new BitmapTexture();
  public static final BitmapTexture deadBubbleCoralBlock = new BitmapTexture();
  public static final BitmapTexture deadFireCoralBlock = new BitmapTexture();
  public static final BitmapTexture deadHornCoralBlock = new BitmapTexture();

  public static final BitmapTexture tubeCoralFan = new BitmapTexture();
  public static final BitmapTexture brainCoralFan = new BitmapTexture();
  public static final BitmapTexture bubbleCoralFan = new BitmapTexture();
  public static final BitmapTexture fireCoralFan = new BitmapTexture();
  public static final BitmapTexture hornCoralFan = new BitmapTexture();

  public static final BitmapTexture deadTubeCoral = new BitmapTexture();
  public static final BitmapTexture deadBrainCoral = new BitmapTexture();
  public static final BitmapTexture deadBubbleCoral = new BitmapTexture();
  public static final BitmapTexture deadFireCoral = new BitmapTexture();
  public static final BitmapTexture deadHornCoral = new BitmapTexture();

  public static final BitmapTexture deadTubeCoralFan = new BitmapTexture();
  public static final BitmapTexture deadBrainCoralFan = new BitmapTexture();
  public static final BitmapTexture deadBubbleCoralFan = new BitmapTexture();
  public static final BitmapTexture deadFireCoralFan = new BitmapTexture();
  public static final BitmapTexture deadHornCoralFan = new BitmapTexture();

  public static final BitmapTexture turtleEgg = new BitmapTexture();
  public static final BitmapTexture turtleEggSlightlyCracked = new BitmapTexture();
  public static final BitmapTexture turtleEggVeryCracked = new BitmapTexture();

  public static final BitmapTexture blueIce = new BitmapTexture();
  public static final BitmapTexture seaPickle = new BitmapTexture();
  public static final BitmapTexture conduit = new BitmapTexture();
  public static final BitmapTexture structureBlock = new BitmapTexture();
  public static final BitmapTexture structureBlockCorner = new BitmapTexture();
  public static final BitmapTexture structureBlockData = new BitmapTexture();
  public static final BitmapTexture structureBlockLoad = new BitmapTexture();
  public static final BitmapTexture structureBlockSave = new BitmapTexture();

  // [1.14]
  public static final BitmapTexture barrelTop = new BitmapTexture();
  public static final BitmapTexture barrelOpen = new BitmapTexture();
  public static final BitmapTexture barrelSide = new BitmapTexture();
  public static final BitmapTexture barrelBottom = new BitmapTexture();
  public static final BitmapTexture loomBottom = new BitmapTexture();
  public static final BitmapTexture loomFront = new BitmapTexture();
  public static final BitmapTexture loomSide = new BitmapTexture();
  public static final BitmapTexture loomTop = new BitmapTexture();
  public static final BitmapTexture acaciaSignPost = new BitmapTexture();
  public static final BitmapTexture birchSignPost = new BitmapTexture();
  public static final BitmapTexture darkOakSignPost = new BitmapTexture();
  public static final BitmapTexture jungleSignPost = new BitmapTexture();
  public static final BitmapTexture spruceSignPost = new BitmapTexture();
  public static final BitmapTexture cartographyTableSide1 = new BitmapTexture();
  public static final BitmapTexture cartographyTableSide2 = new BitmapTexture();
  public static final BitmapTexture cartographyTableSide3 = new BitmapTexture();
  public static final BitmapTexture cartographyTableTop = new BitmapTexture();
  public static final BitmapTexture fletchingTableFront = new BitmapTexture();
  public static final BitmapTexture fletchingTableSide = new BitmapTexture();
  public static final BitmapTexture fletchingTableTop = new BitmapTexture();
  public static final BitmapTexture smithingTableFront = new BitmapTexture();
  public static final BitmapTexture smithingTableSide = new BitmapTexture();
  public static final BitmapTexture smithingTableTop = new BitmapTexture();
  public static final BitmapTexture smithingTableBottom = new BitmapTexture();
  public static final BitmapTexture blastFurnaceTop = new BitmapTexture();
  public static final BitmapTexture blastFurnaceSide = new BitmapTexture();
  public static final BitmapTexture blastFurnaceFrontOn = new BitmapTexture();
  public static final BitmapTexture blastFurnaceFront = new BitmapTexture();
  public static final BitmapTexture smokerTop = new BitmapTexture();
  public static final BitmapTexture smokerSide = new BitmapTexture();
  public static final BitmapTexture smokerBottom = new BitmapTexture();
  public static final BitmapTexture smokerFrontOn = new BitmapTexture();
  public static final BitmapTexture smokerFront = new BitmapTexture();
  public static final BitmapTexture sweetBerryBushStage0 = new BitmapTexture();
  public static final BitmapTexture sweetBerryBushStage1 = new BitmapTexture();
  public static final BitmapTexture sweetBerryBushStage2 = new BitmapTexture();
  public static final BitmapTexture sweetBerryBushStage3 = new BitmapTexture();
  public static final BitmapTexture cornflower = new BitmapTexture();
  public static final BitmapTexture lilyOfTheValley = new BitmapTexture();
  public static final BitmapTexture witherRose = new BitmapTexture();
  public static final BitmapTexture bambooSapling = new BitmapTexture();
  public static final BitmapTexture lecternBase = new BitmapTexture();
  public static final BitmapTexture lecternFront = new BitmapTexture();
  public static final BitmapTexture lecternSides = new BitmapTexture();
  public static final BitmapTexture lecternTop = new BitmapTexture();
  public static final BitmapTexture composterTop = new BitmapTexture();
  public static final BitmapTexture composterBottom = new BitmapTexture();
  public static final BitmapTexture composterSide = new BitmapTexture();
  public static final BitmapTexture composterCompost = new BitmapTexture();
  public static final BitmapTexture composterReady = new BitmapTexture();
  public static final BitmapTexture bambooStalk = new BitmapTexture();
  public static final BitmapTexture bambooSmallLeaves = new BitmapTexture();
  public static final BitmapTexture bambooLargeLeaves = new BitmapTexture();
  public static final BitmapTexture bambooSingleLeaf = new BitmapTexture();
  public static final BitmapTexture stonecutterBottom = new BitmapTexture();
  public static final BitmapTexture stonecutterTop = new BitmapTexture();
  public static final BitmapTexture stonecutterSide = new BitmapTexture();
  public static final BitmapTexture stonecutterSaw = new BitmapTexture();
  public static final BitmapTexture grindstonePivot = new BitmapTexture();
  public static final BitmapTexture grindstoneRound = new BitmapTexture();
  public static final BitmapTexture grindstoneSide = new BitmapTexture();
  public static final BitmapTexture campfireLog = new BitmapTexture();
  public static final BitmapTexture campfireLogLit = new BitmapTexture();
  public static final BitmapTexture campfireFire = new BitmapTexture();
  public static final BitmapTexture lantern = new BitmapTexture();
  public static final BitmapTexture bellBody = new BitmapTexture();
  public static final BitmapTexture scaffoldingTop = new BitmapTexture();
  public static final BitmapTexture scaffoldingSide = new BitmapTexture();
  public static final BitmapTexture scaffoldingBottom = new BitmapTexture();
  public static final BitmapTexture jigsawTop = new BitmapTexture();
  public static final BitmapTexture jigsawSide = new BitmapTexture();
  public static final BitmapTexture jigsawBottom = new BitmapTexture();

  // [1.15]
  public static final BitmapTexture honeyBlockTop = new BitmapTexture();
  public static final BitmapTexture honeyBlockSide = new BitmapTexture();
  public static final BitmapTexture honeyBlockBottom = new BitmapTexture();
  public static final BitmapTexture beeNestFront = new BitmapTexture();
  public static final BitmapTexture beeNestFrontHoney = new BitmapTexture();
  public static final BitmapTexture beeNestSide = new BitmapTexture();
  public static final BitmapTexture beeNestTop = new BitmapTexture();
  public static final BitmapTexture beeNestBottom = new BitmapTexture();
  public static final BitmapTexture beehiveFront = new BitmapTexture();
  public static final BitmapTexture beehiveFrontHoney = new BitmapTexture();
  public static final BitmapTexture beehiveSide = new BitmapTexture();
  public static final BitmapTexture beehiveEnd = new BitmapTexture();
  public static final BitmapTexture honeycombBlock = new BitmapTexture();

  // [1.16]
  public static final BitmapTexture soulSoil = new BitmapTexture();
  public static final BitmapTexture crimsonNylium = new BitmapTexture();
  public static final BitmapTexture crimsonNyliumSide = new BitmapTexture();
  public static final BitmapTexture warpedNylium = new BitmapTexture();
  public static final BitmapTexture warpedNyliumSide = new BitmapTexture();
  public static final BitmapTexture netherGoldOre = new BitmapTexture();
  public static final BitmapTexture targetSide = new BitmapTexture();
  public static final BitmapTexture targetTop = new BitmapTexture();
  public static final BitmapTexture netheriteBlock = new BitmapTexture();
  public static final BitmapTexture shroomlight = new BitmapTexture();
  public static final BitmapTexture warpedWartBlock = new BitmapTexture();
  public static final BitmapTexture basaltSide = new BitmapTexture();
  public static final BitmapTexture basaltTop = new BitmapTexture();
  public static final BitmapTexture polishedBasaltSide = new BitmapTexture();
  public static final BitmapTexture polishedBasaltTop = new BitmapTexture();
  public static final BitmapTexture ancientDebrisSide = new BitmapTexture();
  public static final BitmapTexture ancientDebrisTop = new BitmapTexture();
  public static final BitmapTexture warpedFungus = new BitmapTexture();
  public static final BitmapTexture crimsonFungus = new BitmapTexture();
  public static final BitmapTexture netherSprouts = new BitmapTexture();
  public static final BitmapTexture warpedRoots = new BitmapTexture();
  public static final BitmapTexture crimsonRoots = new BitmapTexture();
  public static final BitmapTexture warpedRootsPot = new BitmapTexture();
  public static final BitmapTexture crimsonRootsPot = new BitmapTexture();
  public static final BitmapTexture cryingObsidian = new BitmapTexture();
  public static final BitmapTexture warpedStem = new BitmapTexture();
  public static final BitmapTexture warpedStemTop = new BitmapTexture();
  public static final BitmapTexture strippedWarpedStem = new BitmapTexture();
  public static final BitmapTexture strippedWarpedStemTop = new BitmapTexture();
  public static final BitmapTexture crimsonStem = new BitmapTexture();
  public static final BitmapTexture crimsonStemTop = new BitmapTexture();
  public static final BitmapTexture strippedCrimsonStem = new BitmapTexture();
  public static final BitmapTexture strippedCrimsonStemTop = new BitmapTexture();
  public static final BitmapTexture soulFireLantern = new BitmapTexture();
  public static final BitmapTexture twistingVines = new BitmapTexture();
  public static final BitmapTexture twistingVinesPlant = new BitmapTexture();
  public static final BitmapTexture weepingVines = new BitmapTexture();
  public static final BitmapTexture weepingVinesPlant = new BitmapTexture();
  public static final BitmapTexture soulFireTorch = new BitmapTexture();
  public static final BitmapTexture respawnAnchorTop = new BitmapTexture();
  public static final BitmapTexture respawnAnchorBottom = new BitmapTexture();
  public static final BitmapTexture respawnAnchorSide0 = new BitmapTexture();
  public static final BitmapTexture respawnAnchorSide1 = new BitmapTexture();
  public static final BitmapTexture respawnAnchorSide2 = new BitmapTexture();
  public static final BitmapTexture respawnAnchorSide3 = new BitmapTexture();
  public static final BitmapTexture respawnAnchorSide4 = new BitmapTexture();
  public static final BitmapTexture crimsonSignPost = new BitmapTexture();
  public static final BitmapTexture warpedSignPost = new BitmapTexture();
  public static final BitmapTexture crimsonPlanks = new BitmapTexture();
  public static final BitmapTexture warpedPlanks = new BitmapTexture();
  public static final BitmapTexture crimsonDoorTop = new BitmapTexture();
  public static final BitmapTexture crimsonDoorBottom = new BitmapTexture();
  public static final BitmapTexture warpedDoorTop = new BitmapTexture();
  public static final BitmapTexture warpedDoorBottom = new BitmapTexture();
  public static final BitmapTexture crimsonTrapdoor = new BitmapTexture();
  public static final BitmapTexture warpedTrapdoor = new BitmapTexture();
  public static final BitmapTexture soulFire = new BitmapTexture();
  public static final AnimatedTexture soulFireLayer0 = new AnimatedTexture();
  public static final AnimatedTexture soulFireLayer1 = new AnimatedTexture();
  public static final BitmapTexture lodestoneSide = new BitmapTexture();
  public static final BitmapTexture lodestoneTop = new BitmapTexture();
  public static final BitmapTexture blackstone = new BitmapTexture();
  public static final BitmapTexture blackstoneTop = new BitmapTexture();
  public static final BitmapTexture chiseledNetherBricks = new BitmapTexture();
  public static final BitmapTexture crackedNetherBricks = new BitmapTexture();
  public static final BitmapTexture gildedBlackstone = new BitmapTexture();
  public static final BitmapTexture soulCampfireLogLit = new BitmapTexture();
  public static final BitmapTexture soulCampfireFire = new BitmapTexture();
  public static final BitmapTexture polishedBlackstone = new BitmapTexture();
  public static final BitmapTexture chiseledPolishedBlackstone = new BitmapTexture();
  public static final BitmapTexture polishedBlackstoneBricks = new BitmapTexture();
  public static final BitmapTexture crackedPolishedBlackstoneBricks = new BitmapTexture();
  public static final BitmapTexture quartzBricks = new BitmapTexture();
  public static final BitmapTexture chain = new BitmapTexture();
  public static final BitmapTexture jigsawLock = new BitmapTexture();

  // [1.17]
  public static final BitmapTexture candle = new BitmapTexture();
  public static final BitmapTexture whiteCandle = new BitmapTexture();
  public static final BitmapTexture orangeCandle = new BitmapTexture();
  public static final BitmapTexture magentaCandle = new BitmapTexture();
  public static final BitmapTexture lightBlueCandle = new BitmapTexture();
  public static final BitmapTexture yellowCandle = new BitmapTexture();
  public static final BitmapTexture limeCandle = new BitmapTexture();
  public static final BitmapTexture pinkCandle = new BitmapTexture();
  public static final BitmapTexture grayCandle = new BitmapTexture();
  public static final BitmapTexture lightGrayCandle = new BitmapTexture();
  public static final BitmapTexture cyanCandle = new BitmapTexture();
  public static final BitmapTexture purpleCandle = new BitmapTexture();
  public static final BitmapTexture blueCandle = new BitmapTexture();
  public static final BitmapTexture brownCandle = new BitmapTexture();
  public static final BitmapTexture greenCandle = new BitmapTexture();
  public static final BitmapTexture redCandle = new BitmapTexture();
  public static final BitmapTexture blackCandle = new BitmapTexture();
  public static final BitmapTexture candleLit = new BitmapTexture();
  public static final BitmapTexture whiteCandleLit = new BitmapTexture();
  public static final BitmapTexture orangeCandleLit = new BitmapTexture();
  public static final BitmapTexture magentaCandleLit = new BitmapTexture();
  public static final BitmapTexture lightBlueCandleLit = new BitmapTexture();
  public static final BitmapTexture yellowCandleLit = new BitmapTexture();
  public static final BitmapTexture limeCandleLit = new BitmapTexture();
  public static final BitmapTexture pinkCandleLit = new BitmapTexture();
  public static final BitmapTexture grayCandleLit = new BitmapTexture();
  public static final BitmapTexture lightGrayCandleLit = new BitmapTexture();
  public static final BitmapTexture cyanCandleLit = new BitmapTexture();
  public static final BitmapTexture purpleCandleLit = new BitmapTexture();
  public static final BitmapTexture blueCandleLit = new BitmapTexture();
  public static final BitmapTexture brownCandleLit = new BitmapTexture();
  public static final BitmapTexture greenCandleLit = new BitmapTexture();
  public static final BitmapTexture redCandleLit = new BitmapTexture();
  public static final BitmapTexture blackCandleLit = new BitmapTexture();
  public static final BitmapTexture flameParticle = new BitmapTexture();
  public static final BitmapTexture copperOre = new BitmapTexture();
  public static final BitmapTexture calcite = new BitmapTexture();
  public static final BitmapTexture tuff = new BitmapTexture();
  public static final BitmapTexture amethyst = new BitmapTexture();
  public static final BitmapTexture buddingAmethyst = new BitmapTexture();
  public static final BitmapTexture copperBlock = new BitmapTexture();
  public static final BitmapTexture exposedCopper = new BitmapTexture();
  public static final BitmapTexture weatheredCopper = new BitmapTexture();
  public static final BitmapTexture oxidizedCopper = new BitmapTexture();
  public static final BitmapTexture cutCopper = new BitmapTexture();
  public static final BitmapTexture exposedCutCopper = new BitmapTexture();
  public static final BitmapTexture weatheredCutCopper = new BitmapTexture();
  public static final BitmapTexture oxidizedCutCopper = new BitmapTexture();
  public static final BitmapTexture lightningRod = new BitmapTexture();
  public static final BitmapTexture smallAmethystBud = new BitmapTexture();
  public static final BitmapTexture mediumAmethystBud = new BitmapTexture();
  public static final BitmapTexture largeAmethystBud = new BitmapTexture();
  public static final BitmapTexture amethystCluster = new BitmapTexture();
  public static final BitmapTexture tintedGlass = new BitmapTexture();
  public static final BitmapTexture powderSnow = new BitmapTexture();
  public static final BitmapTexture dripstoneBlock = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneDownBase = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneDownFrustum = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneDownMiddle = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneDownTip = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneDownTipMerge = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneUpBase = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneUpFrustum = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneUpMiddle = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneUpTip = new BitmapTexture();
  public static final BitmapTexture pointedDripstoneUpTipMerge = new BitmapTexture();
  public static final BitmapTexture sculkSensorBottom = new BitmapTexture();
  public static final BitmapTexture sculkSensorSide = new BitmapTexture();
  public static final BitmapTexture sculkSensorTendrilActive = new BitmapTexture();
  public static final BitmapTexture sculkSensorTendrilInactive = new BitmapTexture();
  public static final BitmapTexture sculkSensorTop = new BitmapTexture();
  public static final BitmapTexture glowLichen = new BitmapTexture();
  public static final BitmapTexture azaleaTop = new BitmapTexture();
  public static final BitmapTexture azaleaSide = new BitmapTexture();
  public static final BitmapTexture azaleaPlant = new BitmapTexture();
  public static final BitmapTexture floweringAzaleaTop = new BitmapTexture();
  public static final BitmapTexture floweringAzaleaSide = new BitmapTexture();
  public static final BitmapTexture azaleaLeaves = new BitmapTexture();
  public static final BitmapTexture floweringAzaleaLeaves = new BitmapTexture();
  public static final BitmapTexture mossBlock = new BitmapTexture();
  public static final BitmapTexture caveVinesPlant = new BitmapTexture();
  public static final BitmapTexture caveVinesPlantLit = new BitmapTexture();
  public static final BitmapTexture caveVines = new BitmapTexture();
  public static final BitmapTexture caveVinesLit = new BitmapTexture();
  public static final BitmapTexture hangingRoots = new BitmapTexture();
  public static final BitmapTexture rootedDirt = new BitmapTexture();
  public static final BitmapTexture bigDripleafStem = new BitmapTexture();
  public static final BitmapTexture bigDripleafTop = new BitmapTexture();
  public static final BitmapTexture bigDripleafSide = new BitmapTexture();
  public static final BitmapTexture bigDripleafTip = new BitmapTexture();
  public static final BitmapTexture smallDripleafTop = new BitmapTexture();
  public static final BitmapTexture smallDripleafSide = new BitmapTexture();
  public static final BitmapTexture smallDripleafStemTop = new BitmapTexture();
  public static final BitmapTexture smallDripleafStemBottom = new BitmapTexture();
  public static final BitmapTexture sporeBlossom = new BitmapTexture();
  public static final BitmapTexture sporeBlossomBase = new BitmapTexture();
  public static final BitmapTexture deepslate = new BitmapTexture();
  public static final BitmapTexture deepslateTop = new BitmapTexture();
  public static final BitmapTexture polishedDeepslate = new BitmapTexture();
  public static final BitmapTexture chiseledDeepslate = new BitmapTexture();
  public static final BitmapTexture deepslateBricks = new BitmapTexture();
  public static final BitmapTexture deepslateTiles = new BitmapTexture();
  public static final BitmapTexture smoothBasalt = new BitmapTexture();
  public static final BitmapTexture cobbledDeepslate = new BitmapTexture();
  public static final BitmapTexture deepslateGoldOre = new BitmapTexture();
  public static final BitmapTexture deepslateIronOre = new BitmapTexture();
  public static final BitmapTexture deepslateDiamondOre = new BitmapTexture();
  public static final BitmapTexture deepslateLapisOre = new BitmapTexture();
  public static final BitmapTexture deepslateRedstoneOre = new BitmapTexture();
  public static final BitmapTexture crackedDeepslateBricks = new BitmapTexture();
  public static final BitmapTexture crackedDeepslateTiles = new BitmapTexture();
  public static final BitmapTexture deepslateCoalOre = new BitmapTexture();
  public static final BitmapTexture deepslateCopperOre = new BitmapTexture();
  public static final BitmapTexture deepslateEmeraldOre = new BitmapTexture();
  public static final BitmapTexture lightningRodOn = new BitmapTexture();
  public static final BitmapTexture light = new BitmapTexture();
  public static final BitmapTexture rawCopperBlock = new BitmapTexture();
  public static final BitmapTexture rawGoldBlock = new BitmapTexture();
  public static final BitmapTexture rawIronBlock = new BitmapTexture();
  public static final BitmapTexture pottedAzaleaBushTop = new BitmapTexture();
  public static final BitmapTexture pottedAzaleaBushSide = new BitmapTexture();
  public static final BitmapTexture pottedAzaleaBushPlant = new BitmapTexture();
  public static final BitmapTexture pottedFloweringAzaleaBushTop = new BitmapTexture();
  public static final BitmapTexture pottedFloweringAzaleaBushSide = new BitmapTexture();

  // [1.19]
  @TexturePath("assets/minecraft/textures/block/mud")
  public static final BitmapTexture mud = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mud_bricks")
  public static final BitmapTexture mudBricks = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/packed_mud")
  public static final BitmapTexture packedMud = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/muddy_mangrove_roots_side")
  public static final BitmapTexture muddyMangroveRootsSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/muddy_mangrove_roots_top")
  public static final BitmapTexture muddyMangroveRootsTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/reinforced_deepslate_top")
  public static final BitmapTexture reinforcedDeepslateTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/reinforced_deepslate_side")
  public static final BitmapTexture reinforcedDeepslateSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/reinforced_deepslate_bottom")
  public static final BitmapTexture reinforcedDeepslateBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/frogspawn")
  public static final BitmapTexture frogspawn = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/ochre_froglight_side")
  public static final BitmapTexture ochreFroglightSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/ochre_froglight_top")
  public static final BitmapTexture ochreFroglightTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/verdant_froglight_side")
  public static final BitmapTexture verdantFroglightSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/verdant_froglight_top")
  public static final BitmapTexture verdantFroglightTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pearlescent_froglight_side")
  public static final BitmapTexture pearlescentFroglightSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pearlescent_froglight_top")
  public static final BitmapTexture pearlescentFroglightTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_planks")
  public static final BitmapTexture mangrovePlanks = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_door_top")
  public static final BitmapTexture mangroveDoorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_door_bottom")
  public static final BitmapTexture mangroveDoorBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_leaves")
  public static final BitmapTexture mangroveLeaves = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_log")
  public static final BitmapTexture mangroveLog = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_log_top")
  public static final BitmapTexture mangroveLogTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/stripped_mangrove_log")
  public static final BitmapTexture strippedMangroveLog = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/stripped_mangrove_log_top")
  public static final BitmapTexture strippedMangroveLogTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/mangrove")
  public static final BitmapTexture mangroveSignPost = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_trapdoor")
  public static final BitmapTexture mangroveTrapdoor = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_roots_side")
  public static final BitmapTexture mangroveRootsSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_roots_top")
  public static final BitmapTexture mangroveRootsTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_propagule")
  public static final BitmapTexture mangrovePropagule = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/mangrove_propagule_hanging")
  public static final BitmapTexture mangrovePropaguleHanging = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_top")
  public static final BitmapTexture sculkCatalystTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_side")
  public static final BitmapTexture sculkCatalystSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_bottom")
  public static final BitmapTexture sculkCatalystBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_top_bloom")
  public static final BitmapTexture sculkCatalystTopBloom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_catalyst_side_bloom")
  public static final BitmapTexture sculkCatalystSideBloom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk")
  public static final BitmapTexture sculk = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_top")
  public static final BitmapTexture sculkShriekerTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_side")
  public static final BitmapTexture sculkShriekerSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_bottom")
  public static final BitmapTexture sculkShriekerBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_can_summon_inner_top")
  public static final BitmapTexture sculkShriekerCanSummonInnerTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_shrieker_inner_top")
  public static final BitmapTexture sculkShriekerInnerTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sculk_vein")
  public static final BitmapTexture sculkVein = new BitmapTexture();

  //1.20
  @TexturePath("assets/minecraft/textures/block/bamboo_planks")
  public static final BitmapTexture bambooPlanks = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/bamboo_mosaic")
  public static final BitmapTexture bambooMosaic = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/bamboo_door_top")
  public static final BitmapTexture bambooDoorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/bamboo_door_bottom")
  public static final BitmapTexture bambooDoorBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/bamboo_block")
  public static final BitmapTexture bambooBlock = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/bamboo_block_top")
  public static final BitmapTexture bambooBlockTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/stripped_bamboo_block")
  public static final BitmapTexture strippedBambooBlock = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/stripped_bamboo_block_top")
  public static final BitmapTexture strippedBambooBlockTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/bamboo")
  public static final BitmapTexture bambooSignPost = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/bamboo_trapdoor")
  public static final BitmapTexture bambooTrapdoor = new BitmapTexture();

  @TexturePath("assets/minecraft/textures/block/cherry_planks")
  public static final BitmapTexture cherryPlanks = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/cherry_door_top")
  public static final BitmapTexture cherryDoorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/cherry_door_bottom")
  public static final BitmapTexture cherryDoorBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/cherry_leaves")
  public static final BitmapTexture cherryLeaves = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/cherry_log")
  public static final BitmapTexture cherryLog = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/cherry_log_top")
  public static final BitmapTexture cherryLogTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/stripped_cherry_log")
  public static final BitmapTexture strippedCherryLog = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/stripped_cherry_log_top")
  public static final BitmapTexture strippedCherryLogTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/cherry")
  public static final BitmapTexture cherrySignPost = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/cherry_trapdoor")
  public static final BitmapTexture cherryTrapdoor = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/cherry_sapling")
  public static final BitmapTexture cherrySapling = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/torchflower")
  public static final BitmapTexture torchflower = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/torchflower_crop_stage0")
  public static final BitmapTexture torchflowerCropStage0 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/torchflower_crop_stage1")
  public static final BitmapTexture torchflowerCropStage1 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_empty")
  public static final BitmapTexture chiseledBookshelfEmpty = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_occupied")
  public static final BitmapTexture chiseledBookshelfOccupied = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_side")
  public static final BitmapTexture chiseledBookshelfSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/chiseled_bookshelf_top")
  public static final BitmapTexture chiseledBookshelfTop = new BitmapTexture();

  public static final ChiseledBookshelfTexture[] chiseledBookshelfCombinations = new ChiseledBookshelfTexture[64];
  static {
    for(int i = 0; i < chiseledBookshelfCombinations.length; i++) {
      chiseledBookshelfCombinations[i] = new ChiseledBookshelfTexture(Texture.chiseledBookshelfEmpty, Texture.chiseledBookshelfOccupied,
        i % 2 == 1, (i >> 1) % 2 == 1, (i >> 2) % 2 == 1, (i >> 3) % 2 == 1, (i >> 4) % 2 == 1, (i >> 5) % 2 == 1);
    }
  }

  @TexturePath("assets/minecraft/textures/block/suspicious_sand_0")
  public static final BitmapTexture suspiciousSandStage0 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/suspicious_sand_1")
  public static final BitmapTexture suspiciousSandStage1 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/suspicious_sand_2")
  public static final BitmapTexture suspiciousSandStage2 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/suspicious_sand_3")
  public static final BitmapTexture suspiciousSandStage3 = new BitmapTexture();

  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_0")
  public static final BitmapTexture suspiciousGravelStage0 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_1")
  public static final BitmapTexture suspiciousGravelStage1 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_2")
  public static final BitmapTexture suspiciousGravelStage2 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/suspicious_gravel_3")
  public static final BitmapTexture suspiciousGravelStage3 = new BitmapTexture();

  @TexturePath("assets/minecraft/textures/entity/decorated_pot/decorated_pot_base")
  public static final BitmapTexture decoratedPotBase = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/decorated_pot_side")
  public static final BitmapTexture decoratedPotSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/angler_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternAngler = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/archer_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternArcher = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/arms_up_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternArmsUp = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/blade_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternBlade = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/brewer_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternBrewer = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/burn_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternBurn = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/danger_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternDanger = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/explorer_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternExplorer = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/friend_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternFriend = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/heartbreak_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternHeartbreak = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/heart_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternHeart = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/howl_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternHowl = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/miner_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternMiner = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/mourner_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternMourner = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/plenty_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternPlenty = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/prize_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternPrize = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/sheaf_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternSheaf = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/shelter_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternShelter = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/skull_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternSkull = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/snort_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternSnort = new BitmapTexture();

  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_bottom")
  public static final BitmapTexture snifferEggNotCrackedBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_east")
  public static final BitmapTexture snifferEggNotCrackedEast = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_north")
  public static final BitmapTexture snifferEggNotCrackedNorth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_south")
  public static final BitmapTexture snifferEggNotCrackedSouth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_top")
  public static final BitmapTexture snifferEggNotCrackedTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_not_cracked_west")
  public static final BitmapTexture snifferEggNotCrackedWest = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_bottom")
  public static final BitmapTexture snifferEggSlightlyCrackedBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_east")
  public static final BitmapTexture snifferEggSlightlyCrackedEast = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_north")
  public static final BitmapTexture snifferEggSlightlyCrackedNorth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_south")
  public static final BitmapTexture snifferEggSlightlyCrackedSouth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_top")
  public static final BitmapTexture snifferEggSlightlyCrackedTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_slightly_cracked_west")
  public static final BitmapTexture snifferEggSlightlyCrackedWest = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_bottom")
  public static final BitmapTexture snifferEggVeryCrackedBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_east")
  public static final BitmapTexture snifferEggVeryCrackedEast = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_north")
  public static final BitmapTexture snifferEggVeryCrackedNorth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_south")
  public static final BitmapTexture snifferEggVeryCrackedSouth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_top")
  public static final BitmapTexture snifferEggVeryCrackedTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/sniffer_egg_very_cracked_west")
  public static final BitmapTexture snifferEggVeryCrackedWest = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pink_petals")
  public static final BitmapTexture pinkPetals = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pink_petals_stem")
  public static final BitmapTexture pinkPetalsStem = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_1")
  public static final BitmapTexture pitcherCropBottomStage1 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_2")
  public static final BitmapTexture pitcherCropBottomStage2 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_3")
  public static final BitmapTexture pitcherCropBottomStage3 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom_stage_4")
  public static final BitmapTexture pitcherCropBottomStage4 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_top_stage_3")
  public static final BitmapTexture pitcherCropTopStage3 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_top_stage_4")
  public static final BitmapTexture pitcherCropTopStage4 = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_top")
  public static final BitmapTexture pitcherCropTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_side")
  public static final BitmapTexture pitcherCropSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/pitcher_crop_bottom")
  public static final BitmapTexture pitcherCropBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/calibrated_sculk_sensor_amethyst")
  public static final BitmapTexture calibratedSculkSensorAmethyst = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/calibrated_sculk_sensor_top")
  public static final BitmapTexture calibratedSculkSensorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/calibrated_sculk_sensor_input_side")
  public static final BitmapTexture calibratedSculkSensorInputSide = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/oak")
  public static final BitmapTexture oakHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/spruce")
  public static final BitmapTexture spruceHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/birch")
  public static final BitmapTexture birchHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/jungle")
  public static final BitmapTexture jungleHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/acacia")
  public static final BitmapTexture acaciaHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/dark_oak")
  public static final BitmapTexture darkOakHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/crimson")
  public static final BitmapTexture crimsonHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/warped")
  public static final BitmapTexture warpedHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/mangrove")
  public static final BitmapTexture mangroveHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/bamboo")
  public static final BitmapTexture bambooHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/signs/hanging/cherry")
  public static final BitmapTexture cherryHangingSign = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/piglin/piglin")
  public static final BitmapTexture piglin = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/polished_tuff")
  public static final BitmapTexture polishedTuff = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/tuff_bricks")
  public static final BitmapTexture tuffBricks = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/chiseled_tuff")
  public static final BitmapTexture chiseledTuff = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/chiseled_tuff_bricks")
  public static final BitmapTexture chiseledTuffBricks = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/chiseled_copper")
  public static final BitmapTexture chiseledCopper = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_grate")
  public static final BitmapTexture copperGrate = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb")
  public static final BitmapTexture copperBulb = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb_lit")
  public static final BitmapTexture copperBulbLit = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb_powered")
  public static final BitmapTexture copperBulbPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_bulb_lit_powered")
  public static final BitmapTexture copperBulbLitPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_door_top")
  public static final BitmapTexture copperDoorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_door_bottom")
  public static final BitmapTexture copperDoorBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/copper_trapdoor")
  public static final BitmapTexture copperTrapdoor = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_chiseled_copper")
  public static final BitmapTexture exposedChiseledCopper = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_grate")
  public static final BitmapTexture exposedCopperGrate = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb")
  public static final BitmapTexture exposedCopperBulb = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb_lit")
  public static final BitmapTexture exposedCopperBulbLit = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb_powered")
  public static final BitmapTexture exposedCopperBulbPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_bulb_lit_powered")
  public static final BitmapTexture exposedCopperBulbLitPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_door_top")
  public static final BitmapTexture exposedCopperDoorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_door_bottom")
  public static final BitmapTexture exposedCopperDoorBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/exposed_copper_trapdoor")
  public static final BitmapTexture exposedCopperTrapdoor = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_chiseled_copper")
  public static final BitmapTexture weatheredChiseledCopper = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_grate")
  public static final BitmapTexture weatheredCopperGrate = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb")
  public static final BitmapTexture weatheredCopperBulb = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb_lit")
  public static final BitmapTexture weatheredCopperBulbLit = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb_powered")
  public static final BitmapTexture weatheredCopperBulbPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_bulb_lit_powered")
  public static final BitmapTexture weatheredCopperBulbLitPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_door_top")
  public static final BitmapTexture weatheredCopperDoorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_door_bottom")
  public static final BitmapTexture weatheredCopperDoorBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/weathered_copper_trapdoor")
  public static final BitmapTexture weatheredCopperTrapdoor = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_chiseled_copper")
  public static final BitmapTexture oxidizedChiseledCopper = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_grate")
  public static final BitmapTexture oxidizedCopperGrate = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb")
  public static final BitmapTexture oxidizedCopperBulb = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb_lit")
  public static final BitmapTexture oxidizedCopperBulbLit = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb_powered")
  public static final BitmapTexture oxidizedCopperBulbPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_bulb_lit_powered")
  public static final BitmapTexture oxidizedCopperBulbLitPowered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_door_top")
  public static final BitmapTexture oxidizedCopperDoorTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_door_bottom")
  public static final BitmapTexture oxidizedCopperDoorBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/oxidized_copper_trapdoor")
  public static final BitmapTexture oxidizedCopperTrapdoor = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_north")
  public static final BitmapTexture crafterNorth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_north_crafting")
  public static final BitmapTexture crafterNorthCrafting = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_east")
  public static final BitmapTexture crafterEast = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_east_crafting")
  public static final BitmapTexture crafterEastCrafting = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_east_triggered")
  public static final BitmapTexture crafterEastTriggered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_south")
  public static final BitmapTexture crafterSouth = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_south_triggered")
  public static final BitmapTexture crafterSouthTriggered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_west")
  public static final BitmapTexture crafterWest = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_west_crafting")
  public static final BitmapTexture crafterWestCrafting = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_west_triggered")
  public static final BitmapTexture crafterWestTriggered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_top")
  public static final BitmapTexture crafterTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_top_crafting")
  public static final BitmapTexture crafterTopCrafting = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_top_triggered")
  public static final BitmapTexture crafterTopTriggered = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/crafter_bottom")
  public static final BitmapTexture crafterBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_top")
  public static final BitmapTexture vaultTop = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_top_ejecting")
  public static final BitmapTexture vaultTopEjecting = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_bottom")
  public static final BitmapTexture vaultBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_front_on")
  public static final BitmapTexture vaultFrontOn = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_front_off")
  public static final BitmapTexture vaultFrontOff = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_front_ejecting")
  public static final BitmapTexture vaultFrontEjecting = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_side_off")
  public static final BitmapTexture vaultSideOff = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_side_on")
  public static final BitmapTexture vaultSideOn = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/flow_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternFlow = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/guster_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternGuster = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/entity/decorated_pot/scrape_pottery_pattern")
  public static final BitmapTexture decoratedPotPatternScrape = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/heavy_core")
  public static final BitmapTexture heavyCore = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_bottom_ominous")
  public static final BitmapTexture vaultBottomOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_front_off_ominous")
  public static final BitmapTexture vaultFrontOffOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_side_off_ominous")
  public static final BitmapTexture vaultSideOffOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_top_ominous")
  public static final BitmapTexture vaultTopOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_side_on_ominous")
  public static final BitmapTexture vaultSideOnOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_front_on_ominous")
  public static final BitmapTexture vaultFrontOnOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_top_ejecting_ominous")
  public static final BitmapTexture vaultTopEjectingOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/vault_front_ejecting_ominous")
  public static final BitmapTexture vaultFrontEjectingOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_bottom")
  public static final BitmapTexture trialSpawnerBottom = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_inactive")
  public static final BitmapTexture trialSpawnerSideInactive = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_inactive_ominous")
  public static final BitmapTexture trialSpawnerSideInactiveOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_inactive")
  public static final BitmapTexture trialSpawnerTopInactive = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_inactive_ominous")
  public static final BitmapTexture trialSpawnerTopInactiveOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_active")
  public static final BitmapTexture trialSpawnerSideActive = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_side_active_ominous")
  public static final BitmapTexture trialSpawnerSideActiveOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_active")
  public static final BitmapTexture trialSpawnerTopActive = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_active_ominous")
  public static final BitmapTexture trialSpawnerTopActiveOminous = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_ejecting_reward")
  public static final BitmapTexture trialSpawnerTopEjectingReward = new BitmapTexture();
  @TexturePath("assets/minecraft/textures/block/trial_spawner_top_ejecting_reward_ominous")
  public static final BitmapTexture trialSpawnerTopEjectingRewardOminous = new BitmapTexture();

  /** Banner base texture. */
  public static final BitmapTexture bannerBase = new BitmapTexture();

  public static final BitmapTexture armorStand = new BitmapTexture();

  private Texture() {}
}
