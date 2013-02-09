/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Color;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector4d;
import se.llbit.resources.ImageLoader;

/**
 * Texture object.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("javadoc")
public class Texture {

	public static final Texture air = new Texture("air");
	public static final Texture stone = new Texture("stone");
	public static final Texture dirt = new Texture("dirt");
	public static final Texture grassSideSaturated = new Texture("grass-side-saturated");
	public static final Texture grassTop = new Texture("grass");
	public static final Texture grassSide = new Texture("air");// TODO
	public static final Texture water = new Texture("water");
	public static final Texture cauldronSide = new Texture();
	public static final Texture cauldronInside = new Texture();
	public static final Texture cauldronTop = new Texture();
	public static final Texture cauldronBottom = new Texture();
	public static final Texture slabTop = new Texture("stone-slab");
	public static final Texture slabSide = new Texture("double-stone-slab");
	public static final Texture brick = new Texture("bricks");
	public static final Texture tntTop = new Texture();
	public static final Texture tntSide = new Texture("tnt");
	public static final Texture tntBottom = new Texture();
	public static final Texture cobweb = new Texture("cobweb");
	public static final Texture redRose = new Texture("rose");
	public static final Texture yellowFlower = new Texture("yellow-flower");
	public static final Texture portal = new Texture("nether-portal");
	public static final Texture cobblestone = new Texture("cobblestone");
	public static final Texture bedrock = new Texture("bedrock");
	public static final Texture sand = new Texture("sand");
	public static final Texture gravel = new Texture("gravel");
	public static final Texture woodTop = new Texture("wood-top");
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
	public static final Texture sponge = new Texture("sponge");
	public static final Texture glass = new Texture("glass");
	public static final Texture glassPaneSide = new Texture();
	public static final Texture diamondOre = new Texture("diamond-ore");
	public static final Texture redstoneOre = new Texture("redstone-ore");
	public static final Texture stoneBrick = new Texture("stone-bricks");
	public static final Texture mossyStoneBrick = new Texture();
	public static final Texture crackedStoneBrick = new Texture();
	public static final Texture circleStoneBrick = new Texture();
	public static final Texture monsterSpawner = new Texture("spawner");
	public static final Texture snowBlock = new Texture("snow");
	public static final Texture snowSide = new Texture("dirt");// TODO
	public static final Texture ice = new Texture("ice");
	public static final Texture cactusTop = new Texture();
	public static final Texture cactusSide = new Texture();
	public static final Texture cactusBottom = new Texture();
	public static final Texture clay = new Texture("clay");
	public static final Texture sugarCane = new Texture("sugar-canes");
	public static final Texture jukeboxSide = new Texture();
	public static final Texture jukeboxTop = new Texture();
	public static final Texture torch = new Texture("torch");
	public static final Texture woodenDoorTop = new Texture();
	public static final Texture woodenDoorBottom = new Texture();
	public static final Texture ironDoorTop = new Texture();
	public static final Texture ironDoorBottom = new Texture();
	public static final Texture ladder = new Texture("ladder");
	public static final Texture trapdoor = new Texture("trapdoor");
	public static final Texture ironBars = new Texture("iron-bars");
	public static final Texture farmlandWet = new Texture("soil");// TODO: wet variant
	public static final Texture farmlandDry = new Texture("soil");// TODO: dry variant
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
	public static final Texture sandstoneSide = new Texture("sandstone");
	public static final Texture sandstoneTop = new Texture();
	public static final Texture sandstoneBottom = new Texture();
	public static final Texture sandstoneDecorated = new Texture();
	public static final Texture sandstoneSmooth = new Texture();
	public static final Texture bedFootTop = new Texture();
	public static final Texture bedHeadTop = new Texture();
	public static final Texture bedFootEnd = new Texture();
	public static final Texture bedFootSide = new Texture();
	public static final Texture bedHeadSide = new Texture();
	public static final Texture bedHeadEnd = new Texture();
	public static final Texture deadBush = new Texture("dead-bush");
	public static final Texture tallGrass = new Texture("tall-grass");
	public static final Texture fern = new Texture();
	public static final Texture vines = new Texture("vines");
	public static final Texture wheat1 = new Texture();
	public static final Texture wheat2 = new Texture();
	public static final Texture wheat3 = new Texture();
	public static final Texture wheat4 = new Texture();
	public static final Texture wheat5 = new Texture();
	public static final Texture wheat6 = new Texture();
	public static final Texture wheat7 = new Texture();
	public static final Texture wheat8 = new Texture();
	public static final Texture rails = new Texture("minecart-track");
	public static final Texture railsCurved = new Texture();
	public static final Texture poweredRailsOn = new Texture();
	public static final Texture poweredRailsOff = new Texture();
	public static final Texture detectorRails = new Texture();
	public static final Texture whiteWool = new Texture("wool");// TODO: variants
	public static final Texture orangeWool = new Texture("wool");// TODO: variants
	public static final Texture magentaWool = new Texture("wool");// TODO: variants
	public static final Texture lightBlueWool = new Texture("wool");// TODO: variants
	public static final Texture yellowWool = new Texture("wool");// TODO: variants
	public static final Texture limeWool = new Texture("wool");// TODO: variants
	public static final Texture pinkWool = new Texture("wool");// TODO: variants
	public static final Texture grayWool = new Texture("wool");// TODO: variants
	public static final Texture lightGrayWool = new Texture("wool");// TODO: variants
	public static final Texture cyanWool = new Texture("wool");// TODO: variants
	public static final Texture purpleWool = new Texture("wool");// TODO: variants
	public static final Texture blueWool = new Texture("wool");// TODO: variants
	public static final Texture brownWool = new Texture("wool");// TODO: variants
	public static final Texture greenWool = new Texture("wool");// TODO: variants
	public static final Texture redWool = new Texture("wool");// TODO: variants
	public static final Texture blackWool = new Texture("wool");// TODO: variants
	public static final Texture lava = new Texture("lava");
	public static final Texture lapislazuliOre = new Texture("lapis-lazuli-ore");
	public static final Texture lapislazuliBlock = new Texture("lapis-lazuli-block");
	public static final Texture pistonSide = new Texture("piston");
	public static final Texture pistonFront = new Texture();
	public static final Texture pistonBack = new Texture();
	public static final Texture pistonArm = new Texture("piston-extension");
	public static final Texture stickyPistonArm = new Texture();
	public static final Texture fire = new Texture("fire");
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
	public static final Texture melonStem = new Texture();
	public static final Texture stemWithMelon = new Texture();
	public static final Texture myceliumTop = new Texture();
	public static final Texture myceliumSide = new Texture();
	public static final Texture lilyPad = new Texture("lily-pad");
	public static final Texture netherBrick = new Texture();
	public static final Texture netherWart1 = new Texture();
	public static final Texture netherWart2 = new Texture();
	public static final Texture netherWart3 = new Texture();
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
	public static final Texture commandBlock = new Texture();
	public static final Texture eyeOfTheEnder = new Texture();
	public static final Texture dragonEgg = new Texture();
	public static final Texture cocoaPlantSmall = new Texture();
	public static final Texture cocoaPlantMedium = new Texture();
	public static final Texture cocoaPlantLarge = new Texture();
	public static final Texture emeraldOre = new Texture();
	public static final Texture emeraldBlock = new Texture();
	public static final Texture tripwireHook = new Texture();
	public static final Texture tripwire = new Texture();
	public static final Texture carrotsPotatoes1 = new Texture();
	public static final Texture carrotsPotatoes2 = new Texture();
	public static final Texture carrotsPotatoes3 = new Texture();
	public static final Texture carrotsMature = new Texture();
	public static final Texture potatoesMature = new Texture();
	public static final Texture beacon = new Texture();
	public static final Texture anvilSide = new Texture();
	public static final Texture anvilTop1 = new Texture();
	public static final Texture anvilTop2 = new Texture();
	public static final Texture anvilTop3 = new Texture();
	public static final Texture flowerPot = new Texture();
	public static final Texture unknown = new Texture("unknown");
	
	public static final Texture signPost = new Texture();
	
	// Tree variants
	
	public static final Texture oakLeaves = new Texture("leaves");// TODO: variants
	public static final Texture spruceLeaves = new Texture("leaves");// TODO: variants
	public static final Texture birchLeaves = oakLeaves;
	public static final Texture jungleTreeLeaves = new Texture("leaves");// TODO: variants
	
	public static final Texture oakSapling = new Texture("sapling");// TODO: variants
	public static final Texture pineSapling = new Texture("sapling");// TODO: variants
	public static final Texture birchSapling = new Texture("sapling");// TODO: variants
	public static final Texture jungleTreeSapling = new Texture("sapling");// TODO: variants
	
	public static final Texture oakPlanks = new Texture("wooden-planks");// TODO: variants
	public static final Texture sprucePlanks = new Texture("wooden-planks");// TODO: variants
	public static final Texture birchPlanks = new Texture("wooden-planks");// TODO: variants
	public static final Texture jungleTreePlanks = new Texture("wooden-planks");// TODO: variants
	
	public static final Texture oakWood = new Texture("wood");// TODO: variants
	public static final Texture spruceWood = new Texture("wood");// TODO: variants
	public static final Texture birchWood = new Texture("wood");// TODO: variants
	public static final Texture jungleTreeWood = new Texture("wood");// TODO: variants
	
	public static final Texture[] woodPlank =
		{ oakPlanks, sprucePlanks, birchPlanks, jungleTreePlanks };
	
	public static final Texture[] leaves =
		{ oakLeaves, spruceLeaves, birchLeaves, jungleTreeLeaves };
	
	public static final Texture[] sapling = {
		oakSapling, pineSapling, birchSapling, jungleTreeSapling };
	
	public static final Texture[] wheat =
		{ wheat1, wheat2, wheat3, wheat4, wheat5, wheat6, wheat7, wheat8 };
	
	public static final Texture[] carrots =
		{ carrotsPotatoes1, carrotsPotatoes1, carrotsPotatoes2, carrotsPotatoes2,
		carrotsPotatoes3, carrotsPotatoes3, carrotsPotatoes3, carrotsMature };
	
	public static final Texture[] potatoes =
		{ carrotsPotatoes1, carrotsPotatoes1, carrotsPotatoes2, carrotsPotatoes2,
		carrotsPotatoes3, carrotsPotatoes3, carrotsPotatoes3, potatoesMature };
	
	public static final Texture[] netherWart =
		{ netherWart1, netherWart2, netherWart2, netherWart3 };
	
	public static final Texture[] woodenDoor =
		{ woodenDoorBottom, woodenDoorTop };
	
	public static final Texture[] ironDoor =
		{ ironDoorBottom, ironDoorTop };
	
	public static final Texture[] poweredRails =
		{ poweredRailsOff, poweredRailsOn };
	
	public static final Texture[] railsType =
		{ rails, rails, rails, rails, rails, rails,
		railsCurved, railsCurved, railsCurved, railsCurved };
	
	public static final Texture[] wool =
		{ whiteWool, orangeWool, magentaWool, lightBlueWool,
		yellowWool, limeWool, pinkWool, grayWool, lightGrayWool,
		cyanWool, purpleWool, blueWool, brownWool, greenWool,
		redWool, blackWool };
	
	public static final Texture[] stoneBrickType =
		{ stoneBrick, mossyStoneBrick, crackedStoneBrick, circleStoneBrick };
	
	public static final Texture[] anvilTop =
		{ anvilTop1, anvilTop2, anvilTop3, anvilTop3 };
	
	protected BufferedImage image;
	protected int width;
	protected int height;
	protected int avgColor;
	private float[] avgColorLinear;
	private float[][] linear;
	private BufferedImage prescaled;
	private int bufferedScale = -1;
	
	public Texture() {
		this(ImageLoader.get("missing-image"));
	}
	
	public Texture(String resourceName) {
		setTexture(ImageLoader.get("textures/" + resourceName + ".png"));
	}
	
	public Texture(BufferedImage img) {
		setTexture(img);
	}
	
	public void setTexture(BufferedImage newImage) {
		if (newImage.getType() == BufferedImage.TYPE_INT_ARGB) {
			image = newImage;
		} else {
			// convert to ARGB
			image = new BufferedImage(newImage.getWidth(),
					newImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.createGraphics();
			g.drawImage(newImage, 0, 0, null);
			g.dispose();
		}
		
		// gamma correct the texture
		avgColorLinear = new float[] { 0, 0, 0, 0 };
		
		DataBufferInt dataBuffer = (DataBufferInt) image.getRaster().getDataBuffer();
		int[] data = dataBuffer.getData();
		width = image.getWidth();
		height = image.getHeight();
		linear = new float[width*height][4];
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int index =  width*y + x;
				Color.getRGBAComponents(data[index], linear[index]);
				linear[index][0] = (float) Math.pow(linear[index][0], Scene.DEFAULT_GAMMA);
				linear[index][1] = (float) Math.pow(linear[index][1], Scene.DEFAULT_GAMMA);
				linear[index][2] = (float) Math.pow(linear[index][2], Scene.DEFAULT_GAMMA);
				avgColorLinear[0] += linear[index][3] * linear[index][0];
				avgColorLinear[1] += linear[index][3] * linear[index][1];
				avgColorLinear[2] += linear[index][3] * linear[index][2];
				avgColorLinear[3] += linear[index][3];
			}
		}
		
		avgColorLinear[0] /= width*height;
		avgColorLinear[1] /= width*height;
		avgColorLinear[2] /= width*height;
		avgColorLinear[3] /= width*height;
		
		avgColor = Color.getRGBA(Math.pow(avgColorLinear[0], 1/Scene.DEFAULT_GAMMA),
				Math.pow(avgColorLinear[1], 1/Scene.DEFAULT_GAMMA),
				Math.pow(avgColorLinear[2], 1/Scene.DEFAULT_GAMMA), avgColorLinear[3]);
	}

	/**
	 * Get linear color values
	 * @param u
	 * @param v
	 * @param c
	 */
	public void getColor(double u, double v, Vector4d c) {
		c.set(getColor(u, v));
	}
	
	/**
	 * Get linear color values
	 * @param ray
	 */
	public void getColor(Ray ray) {
		getColor(ray.u, ray.v, ray.color);
	}
	
	public float[] getColor(double u, double v) {
		return getColor(
				(int) (u * width - Ray.EPSILON),
				(int) ((1-v) * height - Ray.EPSILON));
	}
	
	private final float[] getColor(int x, int y) {
		return linear[width*y + x];
	}
	
	/**
	 * Get bilinear interpolated color value
	 * @param u
	 * @param v
	 * @param c
	 */
	public void getColorInterpolated(double u, double v, Vector4d c) {

		double x = u * width;
		double y = v * (height-1);
		double weight;
		int fx = QuickMath.floor(x);
		int cx = QuickMath.ceil(x);
		int fy = QuickMath.floor(y);
		int cy = QuickMath.ceil(y);
		
		float[] rgb = getColor(fx % width, fy);
		weight = (1 - (y-fy)) * (1 - (x-fx));
		c.x = weight * rgb[0];
		c.y = weight * rgb[1];
		c.z = weight * rgb[2];
		rgb = getColor(cx % width, fy);
		weight = (1 - (y-fy)) * (1 - (cx-x));
		c.x += weight * rgb[0];
		c.y += weight * rgb[1];
		c.z += weight * rgb[2];
		rgb = getColor(fx % width, cy);
		weight = (1 - (cy-y)) * (1 - (x-fx));
		c.x += weight * rgb[0];
		c.y += weight * rgb[1];
		c.z += weight * rgb[2];
		rgb = getColor(cx % width, cy);
		weight = (1 - (cy-y)) * (1 - (cx-x));
		c.x += weight * rgb[0];
		c.y += weight * rgb[1];
		c.z += weight * rgb[2];
	}
	
	public int getColorWrapped(int u, int v) {
		return image.getRGB((u + width) % width, (v + height) % height);
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
	public void getAvgColorLinear(Vector4d c) {
		c.set(avgColorLinear);
	}
	
	/**
	 * @return The average color of this texture
	 */
	public float[] getAvgColorLinear() {
		return avgColorLinear;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * Get a scaled version of the texture
	 * @param scale
	 * @return
	 */
	public BufferedImage getScaledImage(int scale) {
		if (bufferedScale == scale) {
			return prescaled;
		} else {
			prescaled = new BufferedImage(scale, scale, BufferedImage.TYPE_INT_ARGB);
			Graphics g = prescaled.getGraphics();
			g.drawImage(image, 0, 0, scale, scale, null);
			g.dispose();
			bufferedScale = scale;
			return prescaled;
		}
	}

	/**
	 * @return Texture width
	 */
	public int getWidth() {
		return width;
	}
}
