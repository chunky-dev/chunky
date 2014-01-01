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
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import se.llbit.chunky.resources.Texture;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LargeChestTexture extends TextureRef {
	private final String file;
	private final Texture left;
	private final Texture topLeft;
	private final Texture topRight;
	private final Texture frontLeft;
	private final Texture frontRight;
	private final Texture bottomLeft;
	private final Texture right;
	private final Texture bottomRight;
	private final Texture backLeft;
	private final Texture backRight;

	/**
	 * Constructor
	 * @param file
	 */
	public LargeChestTexture(String file,
			Texture left, Texture right,
			Texture topLeft, Texture topRight,
			Texture frontLeft, Texture frontRight,
			Texture bottomLeft, Texture bottomRight,
			Texture backLeft, Texture backRight) {
		this.file = file;
		this.left = left;
		this.right = right;
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
		this.backLeft = backLeft;
		this.backRight = backRight;
	}

	@Override
	protected boolean load(InputStream imageStream) throws IOException,
			TextureFormatError {
		BufferedImage spritemap = ImageIO.read(imageStream);
		if (spritemap.getWidth() % 16 != 0 || spritemap.getHeight() % 16 != 0) {
			throw new TextureFormatError(
					"Large chest texture file must have width and height divisible by 16!");
		}

		int imgW = spritemap.getWidth();
		int scale = imgW / (16 * 8);

		left.setTexture(loadLargeChestTexture(spritemap, scale, 0, 2));
		topLeft.setTexture(loadLargeChestTexture(spritemap, scale, 1, 0));
		frontLeft.setTexture(loadLargeChestTexture(spritemap, scale, 1, 2));
		topRight.setTexture(loadLargeChestTexture(spritemap, scale, 2, 0));
		frontRight.setTexture(loadLargeChestTexture(spritemap, scale, 2, 2));
		bottomLeft.setTexture(loadLargeChestTexture(spritemap, scale, 3, 1));
		right.setTexture(loadLargeChestTexture(spritemap, scale, 3, 2));
		bottomRight.setTexture(loadLargeChestTexture(spritemap, scale, 4, 1));
		backLeft.setTexture(loadLargeChestTexture(spritemap, scale, 4, 2));
		backRight.setTexture(loadLargeChestTexture(spritemap, scale, 5, 2));
		return true;
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

	@Override
	public boolean load(ZipFile texturePack) {
		return load(file, texturePack);
	}
}

