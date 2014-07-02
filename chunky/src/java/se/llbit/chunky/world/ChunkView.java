/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.math.QuickMath;

/**
 * Abstract representation of a view over a map of chunks.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("javadoc")
public class ChunkView {

	public final double x;
	public final double z;

	// visible chunks
	public final double x0;
	public final double z0;
	public final double x1;
	public final double z1;

	// preloaded chunks
	public final int px0;
	public final int pz0;
	public final int px1;
	public final int pz1;

	// preloaded regions
	public final int prx0;
	public final int prz0;
	public final int prx1;
	public final int prz1;

	public final int width;
	public final int height;
	public final int chunkScale;


	public ChunkView() {
		this(0, 0, 0, 0);
	}

	public ChunkView(double x, double z, int width, int height) {
		this(x, z, width, height, 16);
	}

	public ChunkView(double x, double z, int width, int height, int scale) {
		this.x = x;
		this.z = z;
		double cw = width / (2. * scale);
		double ch = height / (2. * scale);
		this.x0 = x - cw;
		this.x1 = x + cw;
		this.z0 = z - ch;
		this.z1 = z + ch;
		this.width = width;
		this.height = height;
		// visible chunks [integer coordinates]
		int ix0 = (int) QuickMath.floor(x0);
		int ix1 = (int) QuickMath.floor(x1);
		int iz0 = (int) QuickMath.floor(z0);
		int iz1 = (int) QuickMath.floor(z1);
		if (scale >= 16) {
			px0 = ix0-1;
			px1 = ix1+1;
			pz0 = iz0-1;
			pz1 = iz1+1;
			prx0 = px0>>5;
			prx1 = px1>>5;
			prz0 = pz0>>5;
			prz1 = pz1>>5;
		} else {
			// visible regions
			int irx0 = ix0>>5;
			int irx1 = ix1>>5;
			int irz0 = iz0>>5;
			int irz1 = iz1>>5;
			prx0 = irx0-1;
			prx1 = irx1+1;
			prz0 = irz0-1;
			prz1 = irz1+1;
			px0 = prx0<<5;
			px1 = (prx1<<5)+31;
			pz0 = prz0<<5;
			pz1 = (prz1<<5)+31;
		}
		this.chunkScale = scale;
	}

	public boolean isVisible(Chunk chunk) {
		ChunkPosition pos = chunk.getPosition();
		return isChunkVisible(pos.x, pos.z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ChunkView) {
			ChunkView other = (ChunkView) obj;
			if (chunkScale == other.chunkScale
					&& px0 == other.px0
					&& px1 == other.px1
					&& pz0 == other.pz0
					&& pz1 == other.pz1)
				return true;
		}

		return false;
	}

	public boolean isChunkVisible(ChunkPosition chunk) {
		return isChunkVisible(chunk.x, chunk.z);
	}

	public boolean isChunkVisible(int x, int z) {
		return px0 <= x && px1 >= x &&
				pz0 <= z && pz1 >= z;
	}

	public boolean isVisible(Region region) {
		return isRegionVisible(region.getPosition());
	}

	public boolean isRegionVisible(ChunkPosition pos) {
		return isRegionVisible(pos.x, pos.z);
	}

	public boolean isRegionVisible(int x, int z) {
		return prx0 <= x && prx1 >= x &&
				prz0 <= z && prz1 >= z;
	}

	/**
	 * @param newView
	 * @param x
	 * @param z
	 * @return {@code true} if all chunks in the region (x,z) in newView are
	 * visible in this view
	 */
	public boolean isRegionFullyVisible(ChunkView newView, int x, int z) {
		int x0 = x<<5;
		int x1 = x0 + 31;
		int z0 = z<<5;
		int z1 = z0 + 31;
		x0 = Math.max(newView.px0, x0);
		x1 = Math.min(newView.px1, x1);
		z0 = Math.max(newView.pz0, z0);
		z1 = Math.min(newView.pz1, z1);
		return isChunkVisible(x0, z0) && isChunkVisible(x1, z1);
	}

	@Override
	public String toString() {
		return String.format("[(%d, %d), (%d, %d)]", px0, pz0, px1, pz1);
	}
}
