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
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;

/**
 * Iterate over a list of chunks.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkListIterator implements ChunkIterator {

	List<ChunkPosition> list = new ArrayList<ChunkPosition>();
	int index = 0;

	@Override
	public boolean hasNext() {
		return index < list.size();
	}

	@Override
	public ChunkPosition next() {
		ChunkPosition pos = list.get(index);
		index += 1;
		return pos;
	}

	/**
	 * Add a chunk position to iterate over
	 * @param x
	 * @param z
	 */
	public void addChunk(int x, int z) {
		list.add(ChunkPosition.get(x, z));
	}

	@Override
	public void addChunk(ChunkPosition chunk) {
		//if (!list.contains(chunk))
		list.add(chunk);
	}

}
