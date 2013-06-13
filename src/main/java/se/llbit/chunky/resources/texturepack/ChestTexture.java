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
import org.apache.commons.math3.util.FastMath;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import se.llbit.chunky.resources.Texture;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChestTexture extends TextureRef {
	private Texture lock;
	private Texture top;
	private Texture bottom;
	private Texture left;
	private Texture right;
	private Texture front;
	private Texture back;

	/**
	 * Constructor
	 * @param file
	 * @param lock
	 * @param top
	 * @param bottom
	 * @param left
	 * @param right
	 * @param front
	 * @param back
	 */
	public ChestTexture(String file, Texture lock, Texture top, Texture bottom,
			Texture left, Texture right, Texture front, Texture back) {
		super(file);
		this.lock = lock;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.front = front;
		this.back = back;
	}

	@Override
	boolean load(InputStream imageStream) throws IOException, TextureFormatError {
		BufferedImage spritemap = ImageIO.read(imageStream);
		if (spritemap.getWidth() != spritemap.getHeight() ||
				spritemap.getWidth() % 16 != 0) {
			throw new TextureFormatError(
					"Chest texture files must have equal width and height, divisible by 16!");
		}

		int imgW = spritemap.getWidth();
		int scale = imgW / (16 * 4);

		lock.setTexture(loadChestTexture(spritemap, scale, 0, 0));
		top.setTexture(loadChestTexture(spritemap, scale, 1, 0));
		bottom.setTexture(loadChestTexture(spritemap, scale, 2, 1));
		left.setTexture(loadChestTexture(spritemap, scale, 0, 2));
		front.setTexture(loadChestTexture(spritemap, scale, 1, 2));
		right.setTexture(loadChestTexture(spritemap, scale, 2, 2));
		back.setTexture(loadChestTexture(spritemap, scale, 3, 2));
		return true;
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

}

