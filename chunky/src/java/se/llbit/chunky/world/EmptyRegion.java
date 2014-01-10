/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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
 * An empty or non-existent region.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EmptyRegion extends Region {

	/**
	 * Singleton instance
	 */
	public static final EmptyRegion instance =
			new EmptyRegion();

	/**
	 * Create the empty region.
	 */
	private EmptyRegion() {
		super(ChunkPosition.get(0, 0), EmptyWorld.instance);
	}

	@Override
	public Chunk getChunk(ChunkPosition pos) {
		return EmptyChunk.instance;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean hasChanged() {
		return false;
	}
}
