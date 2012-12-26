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
package se.llbit.chunky.world;

/**
 * Stores the cloud bits from the cloud texture
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Clouds {
	private static long[][] clouds = new long[32][32];
	
	/**
	 * Get cloud bit at position (x, y)
	 * @param x
	 * @param y
	 * @return 0 = no cloud, 1 = cloud
	 */
	public static int getCloud(int x, int y) {
		x = ((x % 256) + 256) % 256;// ensure >= 0 and < 256
		y = ((y % 256) + 256) % 256;// ensure >= 0 and < 256
		int tilex = x / 8;
		int tiley = y / 8;
		int subx = x & 7;
		int suby = y & 7;
		return (int) ((clouds[tilex][tiley] >>> (suby*8 + subx)) & 1);
	}
	
	/**
	 * Set cloud bit at position (x, y)
	 * @param x
	 * @param y
	 * @param v 0 = no cloud, 1 = cloud
	 */
	public static void setCloud(int x, int y, int v) {
		x = ((x % 256) + 256) % 256;// ensure >= 0 and < 256
		y = ((y % 256) + 256) % 256;// ensure >= 0 and < 256
		int tilex = x / 8;
		int tiley = y / 8;
		int subx = x & 7;
		int suby = y & 7;
		clouds[tilex][tiley] |= (v & 1) << (suby*8 + subx);
	}
}
