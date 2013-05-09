/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Color;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector4d;
import se.llbit.util.ImageTools;

/**
 * Specialized skymap texture with multithreaded gamma correction
 * preprocessing.
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class SkymapTexture extends Texture {

	private static final Logger logger =
			Logger.getLogger(SkymapTexture.class);

	class TexturePreprocessor extends Thread {
		private final int x0;
		private final int x1;
		private final int y0;
		private final int y1;

		TexturePreprocessor(int x0, int x1, int y0, int y1) {
			super("Texture Preprocessor");

			this.x0 = x0;
			this.x1 = x1;
			this.y0 = y0;
			this.y1 = y1;
		}

		@Override
		public void run() {
			float[] c = new float[4];
			for (int y = y0; y <= y1; ++y) {
				for (int x = x0; x <= x1; ++x) {
					int index =  width*y + x;
					Color.getRGBAComponents(data[index], c);
					Color.getRGBAComponents(image.getRGB(x, y), c);
					c[0] = (float) FastMath.pow(c[0], Scene.DEFAULT_GAMMA);
					c[1] = (float) FastMath.pow(c[1], Scene.DEFAULT_GAMMA);
					c[2] = (float) FastMath.pow(c[2], Scene.DEFAULT_GAMMA);
					data[index] = Color.getRGB(c);
				}
			}
		}
	}

	private int[] data;

	/**
	 * Create new skymap
	 * @param image
	 */
	public SkymapTexture(BufferedImage image) {
		super(image);
	}

	@Override
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

		DataBufferInt dataBuffer = (DataBufferInt) image.getRaster().getDataBuffer();
		data = dataBuffer.getData();

		width = image.getWidth();
		height = image.getHeight();
		avgColor = ImageTools.calcAvgColor(image);

		logger.info("Preprocessing skymap texture");
		long start = System.currentTimeMillis();

		// gamma correct the texture

		int segX = 4;
		int segY = 4;
		if (width < segX)
			segX = 1;
		if (height < segY)
			segY = 1;
		TexturePreprocessor[][] preprocessor =
				new TexturePreprocessor[segX][segY];
		int w = width / segX;
		int h = height / segY;
		for (int i = 0; i < segX; ++i) {
			int x0 = w * i;
			int x1 = x0 + w-1;
			if ((i+1) == segX)
				x1 = width-1;
			for (int j = 0; j < segY; ++j) {
				int y0 = h * j;
				int y1 = y0 + h-1;
				if ((j+1) == segY)
					y1 = height-1;
				preprocessor[i][j] = new TexturePreprocessor(
						x0, x1, y0, y1);
				preprocessor[i][j].start();
			}
		}

		for (int i = 0; i < segX; ++i) {
			for (int j = 0; j < segY; ++j) {
				try {
					preprocessor[i][j].join();
				} catch (InterruptedException e) {
				}
			}
		}

		long time = System.currentTimeMillis() - start;

		logger.info("Skymap preprocessing took " + time + "ms");

	}

	@Override
	public void getColor(double u, double v, Vector4d c) {
		Color.getRGBComponents(
				data[(int) (u * width - Ray.EPSILON) +
				width * (int) ((1-v) * height - Ray.EPSILON)],
				c);
	}

	/**
	 * Get skymap color at (x, y)
	 * @param x
	 * @param y
	 * @param c
	 */
	public void getColor(int x, int y, Vector4d c) {
		Color.getRGBComponents(data[x + width * y], c);
	}

	@Override
	public void getColor(Ray ray) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float[] getColor(double u, double v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getColorInterpolated(double u, double v, Vector4d c) {
		double x = u * (width-1);
		double y = (1-v) * (height-1);
		double weight;
		int fx = (int) QuickMath.floor(x);
		int cx = (int) QuickMath.ceil(x);
		int fy = (int) QuickMath.floor(y);
		int cy = (int) QuickMath.ceil(y);

		double r, g, b;
		getColor(fx, fy, c);
		weight = (1 - (y-fy)) * (1 - (x-fx));
		r = weight * c.x;
		g = weight * c.y;
		b = weight * c.z;
		getColor(cx, fy, c);
		weight = (1 - (y-fy)) * (1 - (cx-x));
		r += weight * c.x;
		g += weight * c.y;
		b += weight * c.z;
		getColor(fx, cy, c);
		weight = (1 - (cy-y)) * (1 - (x-fx));
		r += weight * c.x;
		g += weight * c.y;
		b += weight * c.z;
		getColor(cx, cy, c);
		weight = (1 - (cy-y)) * (1 - (cx-x));
		r += weight * c.x;
		g += weight * c.y;
		b += weight * c.z;
		c.set(r, g, b, 1);
	}

	@Override
	public int getColorWrapped(int u, int v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getAvgColor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getAvgColorLinear(Vector4d c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BufferedImage getImage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}


}
