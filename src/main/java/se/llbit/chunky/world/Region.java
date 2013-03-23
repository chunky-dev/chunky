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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import se.llbit.chunky.world.storage.RegionFile;
import se.llbit.chunky.world.storage.RegionFileCache;

/**
 * Abstract region representation.
 * Tracks chunks.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Region {

	/**
	 * Region X chunk width
	 */
	public static final int CHUNKS_X = 32;

	/**
	 * Region Z chunk width
	 */
	public static final int CHUNKS_Z = 32;

	private Chunk[][] chunks = new Chunk[CHUNKS_X][CHUNKS_Z];
	private final ChunkPosition position;
	private final World world;
	private boolean parsed = false;

	/**
	 * Create new region
	 * @param pos
	 * @param world
	 */
	public Region(ChunkPosition pos, World world) {
		this.world = world;
		position = pos;
		for (int z = 0; z < CHUNKS_Z; ++z) {
			for (int x = 0; x < CHUNKS_X; ++x) {
				chunks[z][x] = EmptyChunk.instance;
			}
		}
	}

	/**
	 * @param x
	 * @param z
	 * @return Chunk at (x, z)
	 */
	public Chunk getChunk(int x, int z) {
		return chunks[z&31][x&31];
	}

	/**
	 * @param pos Chunk position
	 * @return Chunk at given position
	 */
	public Chunk getChunk(ChunkPosition pos) {
		return chunks[pos.z&31][pos.x&31];
	}

	/**
	 * Set chunk at given position
	 * @param pos
	 * @param chunk
	 */
	public void setChunk(ChunkPosition pos, Chunk chunk) {
		chunks[pos.z&31][pos.x&31] = chunk;
	}

	/**
	 * Add chunk to parse queue, if it exists
	 * @param pos
	 * @param currentLayer
	 * @param parseQueue
	 */
	public void updateChunk(ChunkPosition pos, int currentLayer,
			Queue<Chunk> parseQueue) {

		Chunk chunk = getChunk(pos);
		if (chunk.isEmpty())
			return;
		if (chunk.getLoadedLayer() != currentLayer) {
			parseQueue.add(chunk);
			notifyAll();
		}
	}

	/**
	 * Delete a chunk
	 * @param pos
	 */
	public synchronized void deleteChunk(ChunkPosition pos) {
		Chunk chunk = getChunk(pos);
		if (!chunk.isEmpty()) {
			chunk.delete();
			setChunk(pos, EmptyChunk.instance);
			world.chunkDeleted(pos);
		}
	}

	/**
	 * @return <code>true</code> if this region has been parsed
	 */
	public synchronized boolean isParsed() {
		return parsed;
	}

	/**
	 * Parse the region file to discover chunks
	 */
	public synchronized void parse() {
		if (parsed)
			return;
		parsed = true;

		Collection<Chunk> discovered = new LinkedList<Chunk>();
		RegionFile regionFile = RegionFileCache.getRegionFile(
				world.getRegionDirectory(), position.x<<5, position.z<<5);
		for (int z = 0; z < 32; ++z) {
			for (int x = 0; x < 32; ++x) {
				if (regionFile.hasChunk(x, z)) {
					ChunkPosition pos = ChunkPosition.get((position.x<<5) + x,
							(position.z<<5) + z);
					Chunk chunk = new Chunk(pos, world);
					setChunk(pos, chunk);
					discovered.add(chunk);
				}
			}
		}
		world.chunksDiscovered(discovered);
	}

	/**
	 * @return <code>true</code> if this is an empty or non-existent region
	 */
	public boolean isEmpty() {
		return false;
	}

	/**
	 * @return The region position
	 */
	public final ChunkPosition getPosition() {
		return position;
	}

	@Override
    public String toString() {
    	return "Region " + position.toString();
    }

	/**
	 * Add preloaded chunks to chunk parse queue
	 * @param view
	 * @param chunkParser
	 */
	public void preloadChunks(ChunkView view, ChunkParser chunkParser) {
		for (int z = 0; z < CHUNKS_Z; ++z) {
			for (int x = 0; x < CHUNKS_X; ++x) {
				Chunk chunk = getChunk(x, z);
				if (!chunk.isEmpty() && !chunk.isLayerParsed() &&
						view.shouldPreload(chunk)) {

					chunkParser.addChunk(chunk);
				}
			}
		}
	}

	/**
	 * @param pos A region position
	 * @return The region file name corresponding to the given region position
	 */
	public static String getFileName(ChunkPosition pos) {
		return String.format("r.%d.%d.mca", pos.x, pos.z);
	}
}
