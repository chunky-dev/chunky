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
 * Iterates over chunks within a view.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkViewIterator implements ChunkIterator {

	private ChunkView view;
	private int x;
	private int z;

	/**
	 * Create new chunk view iterator
	 * @param view
	 */
	public ChunkViewIterator(ChunkView view) {
		this.view = view;
		x = view.ix0;
		z = view.iz0;
	}

	@Override
	public boolean hasNext() {
		return x <= view.ix1 && z <= view.iz1;
	}

	@Override
	public ChunkPosition next() {
		ChunkPosition pos = ChunkPosition.get(x, z);
		x += 1;
		if (x > view.ix1) {
			x = view.ix0;
			z += 1;
		}
		return pos;
	}

	@Override
	public void addChunk(ChunkPosition chunk) {
		// do nothing
	}

}
