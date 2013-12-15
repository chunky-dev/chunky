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

import java.util.HashMap;
import java.util.Map;

/**
 * Chunk heightmap.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Heightmap {

	private Map<ChunkPosition, ChunkHeightmap> map =
			new HashMap<ChunkPosition, ChunkHeightmap>();

	/**
	 * Set height y at (x, z)
	 * @param y
	 * @param x
	 * @param z
	 */
	public synchronized void set(int y, int x, int z) {
		ChunkPosition cp = ChunkPosition.get(x >> 5, z >> 5);
		ChunkHeightmap hm = map.get(cp);
		if (hm == null) {
			hm = new ChunkHeightmap();
			map.put(cp, hm);
		}
		hm.set(y, x & 0x1F, z & 0x1F);
	}

	/**
	 * @param x
	 * @param z
	 * @return Height at (x, z)
	 */
	public synchronized int get(int x, int z) {
		ChunkPosition cp = ChunkPosition.get(x >> 5, z >> 5);
		ChunkHeightmap hm = map.get(cp);
		if (hm == null) {
			hm = new ChunkHeightmap();
			map.put(cp, hm);
		}
		return hm.get(x & 0x1F, z & 0x1F);
	}

}
