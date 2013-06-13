/* Copyright (c) 2010 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.util;
import org.apache.commons.math3.util.FastMath;

import java.awt.image.BufferedImage;

import se.llbit.math.Color;

/**
 * Image manipulation utility methods.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ImageTools {

	/**
	 * Calculate the average color across an image.
	 *
	 * @param img
	 * @return average color value
	 */
	public static int calcAvgColor(BufferedImage img) {
		float ra = 0;
		float ga = 0;
		float ba = 0;
		float aa = 0;
		int n = 0;
		for (int x = 0; x < img.getWidth(); ++x) {
			for (int y = 0; y < img.getHeight(); ++y) {
				int cv = img.getRGB(x, y);
				float alpha = (cv >>> 24)/255.f;
				aa += alpha;
				n++;
				ra += alpha * (0xFF & (cv >>> 16))/255.f;
				ga += alpha * (0xFF & (cv >>> 8))/255.f;
				ba += alpha * (0xFF & cv)/255.f;
			}
		}

		if (aa == 0.f)
		    return 0;
		else
		    return Color.getRGBA(ra/aa, ga/aa, ba/aa, aa/n);
	}
}
