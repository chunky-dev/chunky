/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
import java.awt.image.DataBufferInt;

import se.llbit.chunky.resources.texturepack.FontTexture;
import se.llbit.chunky.resources.texturepack.FontTexture.Glyph;
import se.llbit.math.Vector4d;

public class SignTexture extends Texture {

	private static final double ww, hh, u0, v0;

	static {
		// Set up texture coordinates.
		u0 = 2/64.;
		double u1 = 26/64.;
		v0 = 18/32.;
		double v1 = 30/32.;
		ww = u1-u0;
		hh = v1-v0;
	}

	private final Texture texture;

	public SignTexture(String[] textLines) {
		int xmargin = 4;
		int ymargin = 4;
		int gh = 10;
		int width = 90+xmargin*2;
		int height = gh*4+ymargin*2;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		DataBufferInt db = (DataBufferInt) img.getRaster().getDataBuffer();
		int[] data = db.getData();
		int ystart = ymargin;
		for (String line : textLines) {
			if (line.isEmpty()) {
				ystart += gh;
				continue;
			}
			int lineWidth = 0;
			for (int j = 0; j < line.length(); ++j) {
				char c = line.charAt(j);
				Glyph glyph = FontTexture.glyphs[0xFF&c];
				lineWidth += glyph.width;
			}
			int xstart = (width-lineWidth)/2;
			for (int j = 0; j < line.length(); ++j) {
				char c = line.charAt(j);
				Glyph glyph = FontTexture.glyphs[0xFF & c];
				int k = 0;
				int y = ystart;
				for (int py = 0; py < 8; ++py) {
					k += glyph.xmin;
					int x = xstart;
					for (int px = glyph.xmin; px <= glyph.xmax; ++px) {
						int bit;
						if (k < 32) {
							bit = glyph.top & (1 << k);
						} else {
							bit = glyph.bot & (1 << (k - 32));
						}
						if (bit != 0) {
							data[y * width + x] = 0xFF000000;
						}
						k += 1;
						x += 1;
					}
					k += 7-glyph.xmax;
					y += 1;
				}
				xstart += glyph.width;
			}
			ystart += gh;
		}
		texture = new Texture(img);
	}

	@Override
	public void getColor(double u, double v, Vector4d c) {
		texture.getColor(u, v, c);
		if (c.w == 0) {
			Texture.signPost.getColor(u*ww+u0, v*hh+v0, c);
		} else {
			c.set(0, 0, 0, c.w);
		}
	}

}
