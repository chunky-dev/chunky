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

import java.util.HashMap;
import java.util.Map;

/**
 * A chunk position consists of two integer coordinates x and z.
 *
 * The filename of a chunk is uniquely defined by it's position.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
@SuppressWarnings("javadoc")
public final class ChunkPosition {
	public int x, z;

	private ChunkPosition(int x, int z) {
		this.x = x;
		this.z = z;
	}

	@Override
	public String toString() {
		return "["+x+", "+z+"]";
	}

	public ChunkPosition regionPosition() {
		return get(x >> 5, z >> 5);
	}

	private final static Map<Integer, Map<Integer, ChunkPosition>> map =
			new HashMap<Integer, Map<Integer,ChunkPosition>>();

	public synchronized static ChunkPosition get(int x, int z) {
		Map<Integer, ChunkPosition> submap = map.get(x);
		if (submap == null) {
			submap = new HashMap<Integer, ChunkPosition>();
			map.put(x, submap);
		}

		ChunkPosition chunkPosition = submap.get(z);
		if (chunkPosition == null) {
			chunkPosition = new ChunkPosition(x, z);
			submap.put(z, chunkPosition);
		}

		return chunkPosition;
	}

	/**
	 * @return The .mca name for the region with this position
	 */
	public String getMcaName() {
		return String.format("r.%d.%d.mca", x, z);
	}

	/**
	 * @return The long representation of the chunk position
	 */
	public long getLong() {
		return (((long) x) << 32) | (0xFFFFFFFFL & z);
	}

	/**
	 * @param longValue
	 * @return Decoded chunk position
	 */
	public static ChunkPosition get(long longValue) {
		return get((int) (longValue >>> 32), (int) longValue);
	}

	/**
	 * @return Integer representation of chunk position
	 */
	public int getInt() {
		return (((int) x) << 16) | (0xFFFF & z);
	}

	/**
	 * @param x
	 * @param z
	 * @return Integer representation of chunk position
	 */
	public static final int getInt(int x, int z) {
		return (((int) x) << 16) | (0xFFFF & z);
	}

	/**
	 * @return The region position of a this chunk position
	 */
	public ChunkPosition getRegionPosition() {
		return get(x >> 5, z >> 5);
	}
}
