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
	
	// visible chunks [integer coordinates]
	public final int ix0;
	public final int iz0;
	public final int ix1;
	public final int iz1;
	
	// preloaded chunks
	public final int px0;
	public final int pz0;
	public final int px1;
	public final int pz1;
	
	// visible regions
	public final int rx0;
	public final int rz0;
	public final int rx1;
	public final int rz1;
	
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
		ix0 = (int) QuickMath.floor(x0);
		ix1 = (int) QuickMath.floor(x1);
		iz0 = (int) QuickMath.floor(z0);
		iz1 = (int) QuickMath.floor(z1);
		px0 = ix0-1;
		px1 = ix1+1;
		pz0 = iz0-1;
		pz1 = iz1+1;
		rx0 = ix0>>5;
		rx1 = ix1>>5;
		rz0 = iz0>>5;
		rz1 = iz1>>5;
		prx0 = px0>>5;
		prx1 = px1>>5;
		prz0 = pz0>>5;
		prz1 = pz1>>5;
		this.chunkScale = scale;
	}
	
	public boolean shouldPreload(Chunk chunk) {
		ChunkPosition pos = chunk.getPosition();
		return shouldPreloadChunk(pos.x, pos.z);
	}
	
	public boolean shouldPreloadChunk(int x, int z) {
		return px0 <= x && px1 >= x &&
				pz0 <= z && pz1 >= z;
	}

	public boolean isVisible(Chunk chunk) {
		ChunkPosition pos = chunk.getPosition();
		return isChunkVisible(pos.x, pos.z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof ChunkView) {
			ChunkView other = (ChunkView) obj;
			if (chunkScale == other.chunkScale
					&& ix0 == other.ix0
					&& ix1 == other.ix1
					&& iz0 == other.iz0
					&& iz1 == other.iz1)
				return true;
		}
		
		return false;
	}

	public boolean isChunkVisible(int x, int z) {
		return ix0 <= x && ix1 >= x &&
				iz0 <= z && iz1 >= z;
	}

	public boolean isVisible(Region region) {
		ChunkPosition pos = region.getPosition();
		return isRegionVisible(pos.x, pos.z);
	}
	
	public boolean isRegionVisible(int x, int z) {
		return rx0 <= x && rx1 >= x &&
				rz0 <= z && rz1 >= z;
	}
	
	public boolean shouldPreload(Region region) {
		ChunkPosition pos = region.getPosition();
		return shouldPreloadRegion(pos.x, pos.z);
	}
	
	public boolean shouldPreloadRegion(int x, int z) {
		return prx0 <= x && prx1 >= x &&
				prz0 <= z && prz1 >= z;
	}
}
