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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import se.llbit.chunky.world.listeners.ChunkDeletionListener;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;

/**
 * Tracks chunk selections.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkSelectionTracker implements ChunkDeletionListener {

	private final Set<ChunkPosition> selected = new HashSet<ChunkPosition>();
	private final Collection<ChunkUpdateListener> chunkUpdateListeners =
			new LinkedList<ChunkUpdateListener>();

	/**
	 * Add a chunk update listener to listen for selection changes
	 *
	 * @param listener
	 */
	public void addRegionUpdateListener(ChunkUpdateListener listener) {
		synchronized (chunkUpdateListeners) {
			chunkUpdateListeners.add(listener);
		}
	}

	/**
	 * Remove a chunk update listener
	 *
	 * @param listener
	 */
	public synchronized void removeRegionUpdateListener(
			ChunkUpdateListener listener) {
		synchronized (chunkUpdateListeners) {
			chunkUpdateListeners.remove(listener);
		}
	}

	/**
	 * Notify the chunk update listeners that chunks have been updated
	 *
	 * @param chunks
	 *            the updated chunks
	 */
	private void fireChunksUpdated(Collection<ChunkPosition> chunks) {
		for (ChunkPosition chunk : chunks) {
			fireChunkUpdated(chunk);
		}
	}

	/**
	 * Notify the chunk update listeners that a chunk has been updated.
	 *
	 * @param chunk
	 *            the updated chunk
	 */
	private void fireChunkUpdated(ChunkPosition chunk) {
		for (ChunkUpdateListener listener : chunkUpdateListeners) {
			listener.chunkUpdated(chunk);
		}
	}

	@Override
	public void chunkDeleted(ChunkPosition chunk) {
		selected.remove(chunk);
		fireChunkUpdated(chunk);
	}

	/**
	 * Toggle the selected status of the chunk at the given coordinates.
	 *
	 * @param world
	 * @param cx
	 * @param cz
	 */
	public synchronized void selectChunk(World world, int cx, int cz) {
		ChunkPosition chunk = ChunkPosition.get(cx, cz);
		if (selected.contains(chunk)) {
			selected.remove(chunk);
			fireChunkUpdated(chunk);
		} else if (!world.getChunk(chunk).isEmpty()) {
			selected.add(chunk);
			fireChunkUpdated(chunk);
		}
	}

	/**
	 * @return The number of selected chunks
	 */
	public synchronized int numSelectedChunks() {
		return selected.size();
	}

	/**
	 * Select chunks within rectangle
	 *
	 * @param world
	 * @param cx0
	 * @param cz0
	 * @param cx1
	 * @param cz1
	 */
	public synchronized void selectChunks(World world, int cx0, int cz0,
			int cx1, int cz1) {
		for (int cx = cx0; cx <= cx1; ++cx) {
			for (int cz = cz0; cz <= cz1; ++cz) {
				ChunkPosition chunk = ChunkPosition.get(cx, cz);
				if (!selected.contains(chunk) &&
						!world.getChunk(chunk).isEmpty()) {
					selected.add(chunk);
					fireChunkUpdated(chunk);
				}
			}
		}
	}

	/**
	 * Deselect chunks within rectangle
	 *
	 * @param world
	 * @param cx0
	 * @param cz0
	 * @param cx1
	 * @param cz1
	 */
	public synchronized void deselectChunks(World world, int cx0, int cz0,
			int cx1, int cz1) {
		for (int cx = cx0; cx <= cx1; ++cx) {
			for (int cz = cz0; cz <= cz1; ++cz) {
				ChunkPosition chunk = ChunkPosition.get(cx, cz);
				if (selected.contains(chunk)) {
					selected.remove(chunk);
					fireChunkUpdated(chunk);
				}
			}
		}
	}

	/**
	 * Deselect all chunks
	 */
	public synchronized void clearSelection() {
		if (!selected.isEmpty()) {
			fireChunksUpdated(selected);
			selected.clear();
		}
	}

	/**
	 * @param chunk
	 * @return <code>true</code> if the given chunk position is selected
	 */
	public boolean isSelected(ChunkPosition chunk) {
		return selected.contains(chunk);
	}

	/**
	 * @return The currently selected chunks
	 */
	public synchronized Collection<ChunkPosition> getSelection() {
		return new LinkedList<ChunkPosition>(selected);
	}
}
