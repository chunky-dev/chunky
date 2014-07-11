/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources.texturepack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.resources.Texture;
import se.llbit.util.MCDownloader;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SteveTexture extends TextureRef {
	private final String file;

	/**
	 * Constructor
	 * @param file texture filename
	 */
	public SteveTexture(String file) {
		this.file = file;
	}

	@Override
	protected boolean load(InputStream imageStream) throws IOException,
			TextureFormatError {

		BufferedImage spritemap = ImageIO.read(imageStream);
		if (spritemap.getWidth() != 64) {
			throw new TextureFormatError(
					"Skin texture must have width = 64 pixels");
		}
		if (spritemap.getHeight() != 32 && spritemap.getHeight() != 64) {
			throw new TextureFormatError(
					"Chest texture files must have height = 32 or 64 pixels!");
		}

		boolean extended = spritemap.getHeight() == 64;
		loadTexturePart(spritemap, Texture.steveHeadRight, 0, 8, 8, 16);
		loadTexturePart(spritemap, Texture.steveHeadFront, 8, 16, 8, 16);
		loadTexturePart(spritemap, Texture.steveHeadLeft, 16, 24, 8, 16);
		loadTexturePart(spritemap, Texture.steveHeadBack, 24, 32, 8, 16);
		loadTexturePart(spritemap, Texture.steveHeadTop, 8, 16, 0, 8);
		loadTexturePart(spritemap, Texture.steveHeadBottom, 16, 24, 0, 8);
		loadTexturePart(spritemap, Texture.steveChestRight, 16, 20, 20, 32);
		loadTexturePart(spritemap, Texture.steveChestFront, 20, 28, 20, 32);
		loadTexturePart(spritemap, Texture.steveChestLeft, 28, 32, 20, 32);
		loadTexturePart(spritemap, Texture.steveChestBack, 32, 40, 20, 32);
		loadTexturePart(spritemap, Texture.steveChestTop, 20, 28, 16, 20);
		loadTexturePart(spritemap, Texture.steveChestBottom, 28, 36, 16, 20);
		loadTexturePart(spritemap, Texture.steveRightLegRight, 0, 4, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightLegFront, 4, 8, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightLegLeft, 8, 12, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightLegBack, 12, 16, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightLegTop, 4, 8, 16, 20);
		loadTexturePart(spritemap, Texture.steveRightLegBottom, 8, 12, 16, 20);
		loadTexturePart(spritemap, Texture.steveRightArmRight, 40, 44, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightArmFront, 44, 48, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightArmLeft, 48, 52, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightArmBack, 52, 56, 20, 32);
		loadTexturePart(spritemap, Texture.steveRightArmTop, 44, 48, 16, 20);
		loadTexturePart(spritemap, Texture.steveRightArmBottom, 48, 52, 16, 20);
		if (extended) {
			loadTexturePart(spritemap, Texture.steveLeftLegRight, 16, 20, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftLegFront, 20, 24, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftLegLeft, 24, 28, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftLegBack, 28, 32, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftLegTop, 20, 24, 48, 52);
			loadTexturePart(spritemap, Texture.steveLeftLegBottom, 24, 28, 48, 52);
			loadTexturePart(spritemap, Texture.steveLeftArmRight, 32, 36, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftArmFront, 36, 40, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftArmLeft, 40, 44, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftArmBack, 44, 48, 52, 64);
			loadTexturePart(spritemap, Texture.steveLeftArmTop, 36, 40, 48, 52);
			loadTexturePart(spritemap, Texture.steveLeftArmBottom, 40, 44, 48, 52);
		} else {
			Texture.steveLeftLegRight.setTexture(Texture.steveRightLegLeft);
			Texture.steveLeftLegFront.setTexture(Texture.steveRightLegFront);
			Texture.steveLeftLegFront.mirror();
			Texture.steveLeftLegLeft.setTexture(Texture.steveRightLegRight);
			Texture.steveLeftLegBack.setTexture(Texture.steveRightLegBack);
			Texture.steveLeftLegBack.mirror();
			Texture.steveLeftLegTop.setTexture(Texture.steveRightLegTop);
			Texture.steveLeftLegBottom.setTexture(Texture.steveRightLegBottom);
			Texture.steveLeftArmRight.setTexture(Texture.steveRightArmRight);
			Texture.steveLeftArmFront.setTexture(Texture.steveRightArmFront);
			Texture.steveLeftArmLeft.setTexture(Texture.steveRightArmLeft);
			Texture.steveLeftArmBack.setTexture(Texture.steveRightArmBack);
			Texture.steveLeftArmTop.setTexture(Texture.steveRightArmTop);
			Texture.steveLeftArmBottom.setTexture(Texture.steveRightArmBottom);
		}
		return true;
	}

	private static void loadTexturePart(BufferedImage spritemap, Texture dest,
			int u0, int u1, int v0, int v1) {

		int w = u1-u0;
		int h = v1-v0;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < h; ++y) {
			for (int x = 0; x < w; ++x) {
				img.setRGB(x, y, spritemap.getRGB(x+u0, y+v0));
			}
		}
		dest.setTexture(img);
	}

	@Override
	public boolean load(ZipFile texturePack) {
		File dir = new File(PersistentSettings.getSettingsDirectory(), "resources");
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!dir.isDirectory()) {
			System.err.println("Failed to create destination directory " +
				dir.getAbsolutePath());
			return load(file, texturePack);
		}
		String player = "kurtmac";
		File skinFile = new File(dir, player+".skin.png");
		if (!skinFile.isFile()) {
			try {
				MCDownloader.downloadSkin(player, dir);
			} catch (IOException e) {
				e.printStackTrace();
				return load(file, texturePack);
			}
		}
		try {
			return load(new FileInputStream(skinFile));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (TextureFormatError e) {
			e.printStackTrace();
		}
		return false;
	}

}

