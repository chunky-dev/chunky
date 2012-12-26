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
 * Iterates over chunk positions.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public interface ChunkIterator {

	/**
	 * @return <code>true</code> if the iterator has another chunk position
	 */
	boolean hasNext();
	
	/**
	 * @return The next chunk position
	 */
	ChunkPosition next();
	
	/**
	 * @param chunk Add a chunk position to be iterated over
	 */
	void addChunk(ChunkPosition chunk);
}
