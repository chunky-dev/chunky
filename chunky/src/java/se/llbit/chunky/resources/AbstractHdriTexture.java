/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.math.Vector4d;

public class AbstractHdriTexture extends Texture {
	public float[] buf;

	@Override
	public void getColorInterpolated(double u, double v, Vector4d sample) {
		double x = width * u;
		double y = height * v;
		int x0 = clamp(floor(x), width);
		int x1 = clamp(ceil(x), width);
		int y0 = clamp(floor(y), height);
		int y1 = clamp(ceil(y), height);
		double xw = 1 - x + x0;
		double yw = 1 - y + y0;
		int offset = (y0*width + x0)*3;
		double r0 = buf[offset+0];
		double g0 = buf[offset+1];
		double b0 = buf[offset+2];
		offset = (y0*width + x1)*3;
		double r1 = buf[offset+0];
		double g1 = buf[offset+1];
		double b1 = buf[offset+2];
		offset = (y1*width + x0)*3;
		double r2 = buf[offset+0];
		double g2 = buf[offset+1];
		double b2 = buf[offset+2];
		offset = (y1*width + x1)*3;
		double r3 = buf[offset+0];
		double g3 = buf[offset+1];
		double b3 = buf[offset+2];
		sample.set(
			r0*xw*yw + r1*(1-xw)*yw + r2*xw*(1-yw) + r3*(1-xw)*(1-yw),
			g0*xw*yw + g1*(1-xw)*yw + g2*xw*(1-yw) + g3*(1-xw)*(1-yw),
			b0*xw*yw + b1*(1-xw)*yw + b2*xw*(1-yw) + b3*(1-xw)*(1-yw),
			1
		);
	}

	@Override
	public void getColor(double u, double v, Vector4d c) {
		int x = (int) (width * u);
		int y = (int) (height * v);
		x = (x<0)?0:(x>=width)?width-1:x;
		y = (y<0)?0:(y>=height)?height-1:y;
		int offset = (y*width + x)*3;
		c.set(buf[offset+0], buf[offset+1], buf[offset+2], 1);
	}

	/**
 	 * Clamp image coordinate.
 	 */
	private static final int clamp(int i, int end) {
		return i < 0 ? 0 : (i >= end ? end-1 : i);
	}

	private static final int floor(double d) {
		int i = (int) d;
		return d < i ? i-1 : i;
	}

	private static final int ceil(double d) {
		int i = (int) d;
		return d > i ? i+1 : i;
	}
}
