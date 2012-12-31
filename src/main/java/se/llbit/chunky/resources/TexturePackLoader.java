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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.renderer.Sun;
import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Clouds;
import se.llbit.chunky.world.Icon;
import se.llbit.util.ProgramProperties;

/**
 * Utility methods to load Minecraft texture packs.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TexturePackLoader {
	
	private static final Logger logger =
			Logger.getLogger(TexturePackLoader.class);
	
	enum TextureFile {
		TERRAIN,
		CHEST,
		LARGECHEST,
		ENDERCHEST,
		SUN,
		SIGN,
		CLOUDS,
		GRASSCOLOR,
	}
	
	/**
	 * Load default texture files
	 * @param textures
	 */
	private static void loadDefaultTextures(Collection<TextureFile> textures) {
		File tpFile = Chunky.getMinecraftJar();
		InputStream imageStream;
		try {
			ZipFile tpZip = new ZipFile(tpFile);
			
			for (TextureFile file: textures) {
				switch (file) {
				case TERRAIN:
					imageStream = tpZip.getInputStream(new ZipEntry("terrain.png"));
					if (imageStream == null) {
						logger.warn("Could not load terrain.png from default texture pack!");
					} else {
						loadTerrainTextures(imageStream);
					}
					break;
					
				case CHEST:
					imageStream = tpZip.getInputStream(new ZipEntry("item/chest.png"));
					if (imageStream == null) {
						logger.info("Could not load file item/chest.png from default texture pack!");
					} else {
						loadChestTextures(imageStream);
					}
					break;
				
				case LARGECHEST:
					imageStream = tpZip.getInputStream(new ZipEntry("item/largechest.png"));
					if (imageStream == null) {
						logger.info("Could not load file item/largechest.png from default texture pack!");
					} else {
						loadLargeChestTextures(imageStream);
					}
					break;
				
				case ENDERCHEST:
					imageStream = tpZip.getInputStream(new ZipEntry("item/enderchest.png"));
					if (imageStream == null) {
						logger.info("Could not load file item/enderchest.png from default texture pack!");
					} else {
						loadEnderChestTextures(imageStream);
					}
					break;
					
				case SUN:
					imageStream = tpZip.getInputStream(new ZipEntry("terrain/sun.png"));
					if (imageStream == null) {
						logger.info("Could not load file terrain/sun.png from default texture pack!");
					} else {
						loadSunTexture(imageStream);
					}
					break;
					
				case SIGN:
					imageStream = tpZip.getInputStream(new ZipEntry("item/sign.png"));
					if (imageStream == null) {
						logger.info("Could not load file item/sign.png from default texture pack!");
					} else {
						loadSignTexture(imageStream);
					}
					break;
					
				case CLOUDS:
					imageStream = tpZip.getInputStream(new ZipEntry("environment/clouds.png"));
					if (imageStream == null) {
						logger.info("Could not load file environment/clouds.png from default texture pack!");
					} else {
						loadCloudsTexture(imageStream);
					}
					break;
					
				case GRASSCOLOR:
					imageStream = tpZip.getInputStream(new ZipEntry("misc/grasscolor.png"));
					if (imageStream == null) {
						logger.info("Could not load file misc/grasscolor.png from default texture pack!");
					} else {
						loadGrassColorTexture(imageStream);
					}
					
				}
			}
			
			tpZip.close();
			
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
		Collection<TextureFile> defaultTextures =
				new LinkedList<TexturePackLoader.TextureFile>();
		InputStream imageStream;
		try {
			ZipFile tpZip = new ZipFile(tpFile);
			imageStream = tpZip.getInputStream(new ZipEntry("terrain.png"));
			if (imageStream == null) {
				logger.warn("Could not load terrain.png from texture pack!");
				defaultTextures.add(TextureFile.TERRAIN);
			} else {
				loadTerrainTextures(imageStream);
			}
			
			imageStream = tpZip.getInputStream(new ZipEntry("item/chest.png"));
			if (imageStream == null) {
				logger.info("Could not load file item/chest.png from texture pack!");
				defaultTextures.add(TextureFile.CHEST);
			} else {
				loadChestTextures(imageStream);
			}
			
			imageStream = tpZip.getInputStream(new ZipEntry("item/largechest.png"));
			if (imageStream == null) {
				logger.info("Could not load file item/largechest.png from texture pack!");
				defaultTextures.add(TextureFile.LARGECHEST);
			} else {
				loadLargeChestTextures(imageStream);
			}
			
			imageStream = tpZip.getInputStream(new ZipEntry("item/enderchest.png"));
			if (imageStream == null) {
				logger.info("Could not load file item/enderchest.png from texture pack!");
				defaultTextures.add(TextureFile.ENDERCHEST);
			} else {
				loadEnderChestTextures(imageStream);
			}
			
			imageStream = tpZip.getInputStream(new ZipEntry("terrain/sun.png"));
			if (imageStream == null) {
				logger.info("Could not load file terrain/sun.png from texture pack!");
				defaultTextures.add(TextureFile.SUN);
			} else {
				loadSunTexture(imageStream);
			}
			
			imageStream = tpZip.getInputStream(new ZipEntry("item/sign.png"));
			if (imageStream == null) {
				logger.info("Could not load file item/sign.png from texture pack!");
				defaultTextures.add(TextureFile.SIGN);
			} else {
				loadSignTexture(imageStream);
			}
			
			imageStream = tpZip.getInputStream(new ZipEntry("environment/clouds.png"));
			if (imageStream == null) {
				logger.info("Could not load file environment/clouds.png from texture pack!");
				defaultTextures.add(TextureFile.CLOUDS);
			} else {
				loadCloudsTexture(imageStream);
			}
			
			imageStream = tpZip.getInputStream(new ZipEntry("misc/grasscolor.png"));
			if (imageStream == null) {
				logger.info("Could not load file misc/grasscolor.png from texture pack!");
				defaultTextures.add(TextureFile.GRASSCOLOR);
			} else {
				loadGrassColorTexture(imageStream);
			}
			
			tpZip.close();
			
			ProgramProperties.setProperty("lastTexturePack", tpFile.getAbsolutePath());
		} catch (IOException e) {
			logger.warn("Failed to load texture pack: " + tpFile.getPath(), e);
		}
		
		if (!defaultTextures.isEmpty()) {
			loadDefaultTextures(defaultTextures);
		}
	}
	
	private static void loadSignTexture(InputStream imageStream) throws IOException {
		BufferedImage image = ImageIO.read(imageStream);
		Texture.signPost.setTexture(image);
	}
	
	private static void loadSunTexture(InputStream imageStream) throws IOException {
		BufferedImage image = ImageIO.read(imageStream);
		Sun.texture = new Texture(image);
	}
	
	private static void loadCloudsTexture(InputStream imageStream)
			throws IOException {
		
		BufferedImage texture = ImageIO.read(imageStream);
		if (texture.getWidth() != texture.getHeight() ||
				texture.getWidth() != 256) {
			throw new IOException("Error: Clouds texture size must be 256 by 256 pixels!");
		}

		for (int y = 0; y < 256; ++y) {
			for (int x = 0; x < 256; ++x) {
				int v = texture.getRGB(x, y) & 1;
				Clouds.setCloud(x, y, v);
			}
		}
	}
	
	private static void loadGrassColorTexture(InputStream imageStream)
			throws IOException {
		
		Texture grasscolor = new Texture(ImageIO.read(imageStream));
		Biomes.loadGrassColors(grasscolor);
	}
	
	private static void loadChestTextures(InputStream imageStream)
			throws IOException {
		
		BufferedImage spritemap = ImageIO.read(imageStream);
		if (spritemap.getWidth() != spritemap.getHeight() ||
				spritemap.getWidth() % 16 != 0) {
			throw new IOException("Error: Chest texture files must have equal width and height, divisible by 16!");
		}

		int imgW = spritemap.getWidth();
		int scale = imgW / (16 * 4);
		
		Texture.chestLock.setTexture(loadChestTexture(spritemap, scale, 0, 0));
		Texture.chestTop.setTexture(loadChestTexture(spritemap, scale, 1, 0));
		Texture.chestBottom.setTexture(loadChestTexture(spritemap, scale, 2, 1));
		Texture.chestLeft.setTexture(loadChestTexture(spritemap, scale, 0, 2));
		Texture.chestFront.setTexture(loadChestTexture(spritemap, scale, 1, 2));
		Texture.chestRight.setTexture(loadChestTexture(spritemap, scale, 2, 2));
		Texture.chestBack.setTexture(loadChestTexture(spritemap, scale, 3, 2));
	}
	
	private static BufferedImage loadChestTexture(
			BufferedImage spritemap, int scale, int u, int v) {
		
		BufferedImage img = new BufferedImage(scale*16, scale*16,
				BufferedImage.TYPE_INT_ARGB);
		int x0 = 14*u*scale;
		int x1 = 14*(u+1)*scale;
		if (v == 0) {
			int y0 = 0;
			int y1 = 14*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
		} else if (v == 1) {
			int y0 = (14+5)*scale;
			int y1 = (14*2+5)*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
		} else /*if (v == 2)*/ {
			int y0 = 14*scale;
			int y1 = (14+5)*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
			y0 = (14*2+6)*scale;
			y1 = (14*3+1)*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + 6*scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
			
		}
		return img;
	}

	private static void loadEnderChestTextures(InputStream imageStream) throws IOException {
		if (imageStream == null)
			return;

		BufferedImage spritemap = ImageIO.read(imageStream);
		if (spritemap.getWidth() != spritemap.getHeight() ||
				spritemap.getWidth() % 16 != 0) {
			throw new IOException("Error: Ender chest texture file must have equal width and height, divisible by 16!");
		}

		int imgW = spritemap.getWidth();
		int scale = imgW / (16 * 4);

		Texture.enderChestLock.setTexture(loadChestTexture(spritemap, scale, 0, 0));
		Texture.enderChestTop.setTexture(loadChestTexture(spritemap, scale, 1, 0));
		Texture.enderChestBottom.setTexture(loadChestTexture(spritemap, scale, 2, 1));
		Texture.enderChestLeft.setTexture(loadChestTexture(spritemap, scale, 0, 2));
		Texture.enderChestFront.setTexture(loadChestTexture(spritemap, scale, 1, 2));
		Texture.enderChestRight.setTexture(loadChestTexture(spritemap, scale, 2, 2));
		Texture.enderChestBack.setTexture(loadChestTexture(spritemap, scale, 3, 2));
	}
	
	private static void loadLargeChestTextures(InputStream imageStream) throws IOException {
		if (imageStream == null)
			return;
		BufferedImage spritemap = ImageIO.read(imageStream);
		if (spritemap.getWidth() % 16 != 0 || spritemap.getHeight() % 16 != 0) {
			throw new IOException("Error: Large chest texture file must have width and height divisible by 16!");
		}

		int imgW = spritemap.getWidth();
		int scale = imgW / (16 * 8);

		Texture.largeChestLeft.setTexture(loadLargeChestTexture(spritemap, scale, 0, 2));
		Texture.largeChestTopLeft.setTexture(loadLargeChestTexture(spritemap, scale, 1, 0));
		Texture.largeChestFrontLeft.setTexture(loadLargeChestTexture(spritemap, scale, 1, 2));
		Texture.largeChestTopRight.setTexture(loadLargeChestTexture(spritemap, scale, 2, 0));
		Texture.largeChestFrontRight.setTexture(loadLargeChestTexture(spritemap, scale, 2, 2));
		Texture.largeChestBottomLeft.setTexture(loadLargeChestTexture(spritemap, scale, 3, 1));
		Texture.largeChestRight.setTexture(loadLargeChestTexture(spritemap, scale, 3, 2));
		Texture.largeChestBottomRight.setTexture(loadLargeChestTexture(spritemap, scale, 4, 1));
		Texture.largeChestBackLeft.setTexture(loadLargeChestTexture(spritemap, scale, 4, 2));
		Texture.largeChestBackRight.setTexture(loadLargeChestTexture(spritemap, scale, 5, 2));
	}
	
	private static BufferedImage loadLargeChestTexture(BufferedImage spritemap, int scale, int u, int v) {
		BufferedImage img = new BufferedImage(scale*16, scale*16,
				BufferedImage.TYPE_INT_ARGB);
		
		int x0 = 14*u*scale;
		int x1 = 14*(u+1)*scale;
		int xo = 0;
		int[][][] offsets = {
				// v == 0
				{
					{0, 0, 0},
					{0, 1, 0},
					{1, 2, -1},
				},
				// v == 1
				{
					{0, 0, 0},
					{0, 1, 0},
					{1, 2, -1},
					{2, 3, 0},
					{3, 4, -1},
				},
				// v == 2
				{
					{0, 0, 0},
					{0, 1, 0},
					{1, 2, -1},
					{2, 2, 0},
					{2, 3, 0},
					{3, 4, -1},
				},
		};
		x0 += offsets[v][u][0]*scale;
		x1 += offsets[v][u][1]*scale;
		xo += offsets[v][u][2]*scale;
		
		if (v == 0) {
			int y0 = 0;
			int y1 = 14*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale + xo;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
		} else if (v == 1) {
			int y0 = (14+5)*scale;
			int y1 = (14*2+5)*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale + xo;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
		} else /*if (v == 2)*/ {
			int y0 = 14*scale;
			int y1 = (14+5)*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale + xo;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
			y0 = (14*2+6)*scale;
			y1 = (14*3+1)*scale;
			for (int y = y0; y < y1; ++y) {
				int sy = y - y0 + 6*scale;
				for (int x = x0; x < x1; ++x) {
					int sx = x - x0 + scale + xo;
					img.setRGB(sx, sy, spritemap.getRGB(x, y));
				}
			}
			
		}
		return img;
	}
	
	private static void loadTerrainTextures(InputStream imageStream)
			throws IOException {
		
		BufferedImage spritemap = ImageIO.read(imageStream);
		BufferedImage[] texture = getTerrainTextures(spritemap);
		
		Texture.grassTop.setTexture(texture[0x00]);
		Texture.stone.setTexture(texture[0x01]);
		Texture.dirt.setTexture(texture[0x02]);
		Texture.grassSideSaturated.setTexture(texture[0x03]);
		Texture.oakPlanks.setTexture(texture[0x04]);
		Texture.slabSide.setTexture(texture[0x05]);
		Texture.slabTop.setTexture(texture[0x06]);
		Texture.brick.setTexture(texture[0x07]);
		Texture.tntSide.setTexture(texture[0x08]);
		Texture.tntTop.setTexture(texture[0x09]);
		Texture.tntBottom.setTexture(texture[0x0A]);
		Texture.cobweb.setTexture(texture[0x0B]);
		Texture.redRose.setTexture(texture[0x0C]);
		Texture.yellowFlower.setTexture(texture[0x0D]);
		Texture.portal.setTexture(texture[0x0E]);
		Texture.oakSapling.setTexture(texture[0x0F]);
		
		Texture.cobblestone.setTexture(texture[0x10]);
		Texture.bedrock.setTexture(texture[0x11]);
		Texture.sand.setTexture(texture[0x12]);
		Texture.gravel.setTexture(texture[0x13]);
		Texture.oakWood.setTexture(texture[0x14]);
		Texture.woodTop.setTexture(texture[0x15]);
		Texture.ironBlock.setTexture(texture[0x16]);
		Texture.goldBlock.setTexture(texture[0x17]);
		Texture.diamondBlock.setTexture(texture[0x18]);
		Texture.emeraldBlock.setTexture(texture[0x19]);
		// skip 1A
		// skip 1B
		Texture.redMushroom.setTexture(texture[0x1C]);
		Texture.brownMushroom.setTexture(texture[0x1D]);
		Texture.jungleTreeSapling.setTexture(texture[0x1E]);
		// skip 1F
		
		Texture.goldOre.setTexture(texture[0x20]);
		Texture.ironOre.setTexture(texture[0x21]);
		Texture.coalOre.setTexture(texture[0x22]);
		Texture.bookshelf.setTexture(texture[0x23]);
		Texture.mossStone.setTexture(texture[0x24]);
		Texture.obsidian.setTexture(texture[0x25]);
		Texture.grassSide.setTexture(texture[0x26]);
		Texture.tallGrass.setTexture(texture[0x27]);
		// skip 28
		// skip 29
		// skip 2A
		Texture.workbenchTop.setTexture(texture[0x2B]);
		Texture.furnaceUnlitFront.setTexture(texture[0x2C]);
		Texture.furnaceSide.setTexture(texture[0x2D]);
		Texture.dispenserFront.setTexture(texture[0x2E]);
		// skip 2F
		
		Texture.sponge.setTexture(texture[0x30]);
		Texture.glass.setTexture(texture[0x31]);
		Texture.diamondOre.setTexture(texture[0x32]);
		Texture.redstoneOre.setTexture(texture[0x33]);
		Texture.oakLeaves.setTexture(texture[0x34]);
		// skip 35
		Texture.stoneBrick.setTexture(texture[0x36]);
		Texture.deadBush.setTexture(texture[0x37]);
		Texture.fern.setTexture(texture[0x38]);
		// skip 39
		// skip 3A
		Texture.workbenchSide.setTexture(texture[0x3B]);
		Texture.workbenchFront.setTexture(texture[0x3C]);
		Texture.furnaceLitFront.setTexture(texture[0x3D]);
		Texture.furnaceTop.setTexture(texture[0x3E]);
		Texture.pineSapling.setTexture(texture[0x3F]);
		
		Texture.whiteWool.setTexture(texture[0x40]);
		Texture.monsterSpawner.setTexture(texture[0x41]);
		Texture.snowBlock.setTexture(texture[0x42]);
		Texture.ice.setTexture(texture[0x43]);
		Texture.snowSide.setTexture(texture[0x44]);
		Texture.cactusTop.setTexture(texture[0x45]);
		Texture.cactusSide.setTexture(texture[0x46]);
		Texture.cactusBottom.setTexture(texture[0x47]);
		Texture.clay.setTexture(texture[0x48]);
		Texture.sugarCane.setTexture(texture[0x49]);
		Texture.jukeboxSide.setTexture(texture[0x4A]);
		Texture.jukeboxTop.setTexture(texture[0x4B]);
		Texture.lilyPad.setTexture(texture[0x4C]);
		Texture.myceliumSide.setTexture(texture[0x4D]);
		Texture.myceliumTop.setTexture(texture[0x4E]);
		Texture.birchSapling.setTexture(texture[0x4F]);
		
		Texture.torch.setTexture(texture[0x50]);
		Texture.woodenDoorTop.setTexture(texture[0x51]);
		Texture.ironDoorTop.setTexture(texture[0x52]);
		Texture.ladder.setTexture(texture[0x53]);
		Texture.trapdoor.setTexture(texture[0x54]);
		Texture.ironBars.setTexture(texture[0x55]);
		Texture.farmlandWet.setTexture(texture[0x56]);
		Texture.farmlandDry.setTexture(texture[0x57]);
		Texture.wheat1.setTexture(texture[0x58]);
		Texture.wheat2.setTexture(texture[0x59]);
		Texture.wheat3.setTexture(texture[0x5A]);
		Texture.wheat4.setTexture(texture[0x5B]);
		Texture.wheat5.setTexture(texture[0x5C]);
		Texture.wheat6.setTexture(texture[0x5D]);
		Texture.wheat7.setTexture(texture[0x5E]);
		Texture.wheat8.setTexture(texture[0x5F]);

		Texture.lever.setTexture(texture[0x60]);
		Texture.woodenDoorBottom.setTexture(texture[0x61]);
		Texture.ironDoorBottom.setTexture(texture[0x62]);
		Texture.redstoneTorchOn.setTexture(texture[0x63]);
		Texture.mossyStoneBrick.setTexture(texture[0x64]);
		Texture.crackedStoneBrick.setTexture(texture[0x65]);
		Texture.pumpkinTop.setTexture(texture[0x66]);
		Texture.netherrack.setTexture(texture[0x67]);
		Texture.soulsand.setTexture(texture[0x68]);
		Texture.glowstone.setTexture(texture[0x69]);
		Texture.stickyPistonArm.setTexture(texture[0x6A]);
		Texture.pistonArm.setTexture(texture[0x6B]);
		Texture.pistonSide.setTexture(texture[0x6C]);
		Texture.pistonBack.setTexture(texture[0x6D]);
		Texture.pistonFront.setTexture(texture[0x6E]);
		Texture.melonStem.setTexture(texture[0x6F]);
		
		Texture.railsCurved.setTexture(texture[0x70]);
		Texture.blackWool.setTexture(texture[0x71]);
		Texture.grayWool.setTexture(texture[0x72]);
		Texture.redstoneTorchOff.setTexture(texture[0x73]);
		Texture.spruceWood.setTexture(texture[0x74]);
		Texture.birchWood.setTexture(texture[0x75]);
		Texture.pumpkinSide.setTexture(texture[0x76]);
		Texture.pumpkinFront.setTexture(texture[0x77]);
		Texture.jackolanternFront.setTexture(texture[0x78]);
		Texture.cakeTop.setTexture(texture[0x79]);
		Texture.cakeSide.setTexture(texture[0x7A]);
		Texture.cakeInside.setTexture(texture[0x7B]);
		Texture.cakeBottom.setTexture(texture[0x7C]);
		Texture.hugeRedMushroom.setTexture(texture[0x7D]);
		Texture.hugeBrownMushroom.setTexture(texture[0x7E]);
		Texture.stemWithMelon.setTexture(texture[0x7F]);
		
		Texture.rails.setTexture(texture[0x80]);
		Texture.redWool.setTexture(texture[0x81]);
		Texture.pinkWool.setTexture(texture[0x82]);
		Texture.redstoneRepeaterOff.setTexture(texture[0x83]);
		Texture.spruceLeaves.setTexture(texture[0x84]);
		// skip 85
		Texture.bedFootTop.setTexture(texture[0x86]);
		Texture.bedHeadTop.setTexture(texture[0x87]);
		Texture.melonSide.setTexture(texture[0x88]);
		Texture.melonTop.setTexture(texture[0x89]);
		Texture.cauldronTop.setTexture(texture[0x8A]);
		Texture.cauldronInside.setTexture(texture[0x8B]);
		Icon.cake.setTexture(texture[0x8C]);
		Texture.mushroomStem.setTexture(texture[0x8D]);
		Texture.mushroomPores.setTexture(texture[0x8E]);
		Texture.vines.setTexture(texture[0x8F]);

		Texture.lapislazuliBlock.setTexture(texture[0x90]);
		Texture.greenWool.setTexture(texture[0x91]);
		Texture.limeWool.setTexture(texture[0x92]);
		Texture.redstoneRepeaterOn.setTexture(texture[0x93]);
		Texture.glassPaneSide.setTexture(texture[0x94]);
		Texture.bedFootEnd.setTexture(texture[0x95]);
		Texture.bedFootSide.setTexture(texture[0x96]);
		Texture.bedHeadSide.setTexture(texture[0x97]);
		Texture.bedHeadEnd.setTexture(texture[0x98]);
		Texture.jungleTreeWood.setTexture(texture[0x99]);
		Texture.cauldronSide.setTexture(texture[0x9A]);
		Texture.cauldronBottom.setTexture(texture[0x9B]);
		Texture.brewingStandBase.setTexture(texture[0x9C]);
		Texture.brewingStandSide.setTexture(texture[0x9D]);
		Texture.endPortalFrameTop.setTexture(texture[0x9E]);
		Texture.endPortalFrameSide.setTexture(texture[0x9F]);

		Texture.lapislazuliOre.setTexture(texture[0xA0]);
		Texture.brownWool.setTexture(texture[0xA1]);
		Texture.yellowWool.setTexture(texture[0xA2]);
		Texture.poweredRailsOff.setTexture(texture[0xA3]);
		Texture.redstoneWireCross.setTexture(texture[0xA4]);
		Texture.redstoneWire.setTexture(texture[0xA5]);
		Texture.enchantmentTableTop.setTexture(texture[0xA6]);
		Texture.dragonEgg.setTexture(texture[0xA7]);
		Texture.cocoaPlantLarge.setTexture(texture[0xA8]);
		Texture.cocoaPlantMedium.setTexture(texture[0xA9]);
		Texture.cocoaPlantSmall.setTexture(texture[0xAA]);
		Texture.emeraldOre.setTexture(texture[0xAB]);
		Texture.tripwireHook.setTexture(texture[0xAC]);
		Texture.tripwire.setTexture(texture[0xAD]);
		Texture.eyeOfTheEnder.setTexture(texture[0xAE]);
		Texture.endStone.setTexture(texture[0xAF]);
		
		Texture.sandstoneTop.setTexture(texture[0xB0]);
		Texture.blueWool.setTexture(texture[0xB1]);
		Texture.lightBlueWool.setTexture(texture[0xB2]);
		Texture.poweredRailsOn.setTexture(texture[0xB3]);
		//skip B4, B5
		Texture.enchantmentTableSide.setTexture(texture[0xB6]);
		Texture.enchantmentTableBottom.setTexture(texture[0xB7]);
		Texture.commandBlock.setTexture(texture[0xB8]);
		
		Texture.sandstoneSide.setTexture(texture[0xC0]);
		Texture.purpleWool.setTexture(texture[0xC1]);
		Texture.magentaWool.setTexture(texture[0xC2]);
		Texture.detectorRails.setTexture(texture[0xC3]);
		Texture.jungleTreeLeaves.setTexture(texture[0xC4]);
		// skip C5
		Texture.sprucePlanks.setTexture(texture[0xC6]);
		Texture.jungleTreePlanks.setTexture(texture[0xC7]);
		Texture.carrotsPotatoes1.setTexture(texture[0xC8]);
		Texture.carrotsPotatoes2.setTexture(texture[0xC9]);
		Texture.carrotsPotatoes3.setTexture(texture[0xCA]);
		Texture.carrotsMature.setTexture(texture[0xCB]);
		Texture.potatoesMature.setTexture(texture[0xCC]);
		Texture.water.setTexture(texture[0xCD]);
		// skip CE, CF (water)
		
		Texture.sandstoneBottom.setTexture(texture[0xD0]);
		Texture.cyanWool.setTexture(texture[0xD1]);
		Texture.orangeWool.setTexture(texture[0xD2]);
		Texture.redstoneLampOff.setTexture(texture[0xD3]);
		Texture.redstoneLampOn.setTexture(texture[0xD4]);
		Texture.circleStoneBrick.setTexture(texture[0xD5]);
		Texture.birchPlanks.setTexture(texture[0xD6]);
		// skip D7-DF
		
		Texture.netherBrick.setTexture(texture[0xE0]);
		Texture.lightGrayWool.setTexture(texture[0xE1]);
		Texture.netherWart1.setTexture(texture[0xE2]);
		Texture.netherWart2.setTexture(texture[0xE3]);
		Texture.netherWart3.setTexture(texture[0xE4]);
		Texture.sandstoneDecorated.setTexture(texture[0xE5]);
		Texture.sandstoneSmooth.setTexture(texture[0xE6]);
		// skip E7-EC
		Texture.lava.setTexture(texture[0xED]);
		// skip EE, EF
		
		// skip F0-FF
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
