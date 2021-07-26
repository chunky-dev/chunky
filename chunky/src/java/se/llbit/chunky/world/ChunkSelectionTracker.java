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

import se.llbit.chunky.world.listeners.ChunkDeletionListener;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Tracks chunk selections.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkSelectionTracker implements ChunkDeletionListener {

  private final Set<ChunkPosition> selected = new HashSet<>();
  private final Collection<ChunkUpdateListener> chunkUpdateListeners = new LinkedList<>();
  private final Collection<ChunkSelectionListener> selectionListeners = new LinkedList<>();

  /**
   * Add a chunk update listener to listen for selection changes.
   */
  public void addChunkUpdateListener(ChunkUpdateListener listener) {
    synchronized (chunkUpdateListeners) {
      chunkUpdateListeners.add(listener);
    }
  }

  /**
   * Add a chunk update listener to listen for selection changes.
   */
  public void addSelectionListener(ChunkSelectionListener listener) {
    synchronized (chunkUpdateListeners) {
      selectionListeners.add(listener);
    }
  }

  /**
   * Notify the chunk update listeners that chunks have been updated
   *
   * @param chunks the updated chunks
   */
  private void notifyChunksUpdated(Collection<ChunkPosition> chunks) {
    for (ChunkPosition chunk : chunks) {
      notifyChunkUpdated(chunk);
    }
  }

  /**
   * Notify the chunk update listeners that a chunk has been updated.
   *
   * @param chunk the updated chunk
   */
  private void notifyChunkUpdated(ChunkPosition chunk) {
    for (ChunkUpdateListener listener : chunkUpdateListeners) {
      listener.chunkUpdated(chunk);
    }
  }

  private void notifyChunkSelectionChange() {
    for (ChunkSelectionListener listener : selectionListeners) {
      listener.chunkSelectionChanged();
    }
  }

  @Override public void chunkDeleted(ChunkPosition chunk) {
    selected.remove(chunk);
    notifyChunkUpdated(chunk);
    notifyChunkSelectionChange();
  }

  /**
   * Toggle the selected status of the chunk at the given coordinates.
   *
   * @param cx    chunk x-position
   * @param cz    chunk z-position
   */
  public synchronized void toggleChunk(World world, int cx, int cz) {
    ChunkPosition chunk = ChunkPosition.get(cx, cz);
    if (selected.contains(chunk)) {
      selected.remove(chunk);
      notifyChunkUpdated(chunk);
      notifyChunkSelectionChange();
    } else if (!world.getChunk(chunk).isEmpty()) {
      selected.add(chunk);
      notifyChunkUpdated(chunk);
      notifyChunkSelectionChange();
    }
  }

  /**
   * Adds a chunk to the selection.
   *
   * @param cx chunk x-position
   * @param cz chunk z-position
   */
  public synchronized void selectChunk(World world, int cx, int cz) {
    ChunkPosition chunk = ChunkPosition.get(cx, cz);
    if (!selected.contains(chunk) && !world.getChunk(chunk).isEmpty()) {
      selected.add(chunk);
      notifyChunkUpdated(chunk);
      notifyChunkSelectionChange();
    }
  }

  /**
   * Select the region containing the given chunk.
   *
   * @param cx chunk x-position
   * @param cz chunk z-position
   */
  public synchronized void selectRegion(World world, int cx, int cz) {
    ChunkPosition chunk = ChunkPosition.get(cx, cz);
    int rx = cx >> 5;
    int rz = cz >> 5;
    if (selected.contains(chunk)) {
      deselectChunks(rx * 32, rz * 32, rx * 32 + 31, rz * 32 + 31);
    } else {
      selectChunks(world, rx * 32, rz * 32, rx * 32 + 31, rz * 32 + 31);
    }
  }

  /**
   * Select chunks within rectangle.
   * @return true if anything was changed, false if no chunks were selected.
   */
  public synchronized boolean selectChunks(World world, int cx0, int cz0, int cx1, int cz1) {
    boolean selectionChanged = false;
    for (int cx = cx0; cx <= cx1; ++cx) {
      for (int cz = cz0; cz <= cz1; ++cz) {
        ChunkPosition chunk = ChunkPosition.get(cx, cz);
        if (!selected.contains(chunk) && !world.getChunk(chunk).isEmpty()) {
          selected.add(chunk);
          selectionChanged = true;
          notifyChunkUpdated(chunk);
        }
      }
    }
    if (selectionChanged) {
      notifyChunkSelectionChange();
    }
    return selectionChanged;
  }

  /**
   * Deselect chunks within rectangle.
   * @return true if anything was changed, false if no chunks were deselected.
   */
  public synchronized boolean deselectChunks(int cx0, int cz0, int cx1, int cz1) {
    boolean selectionChanged = false;
    for (int cx = cx0; cx <= cx1; ++cx) {
      for (int cz = cz0; cz <= cz1; ++cz) {
        ChunkPosition chunk = ChunkPosition.get(cx, cz);
        if (selected.contains(chunk)) {
          selected.remove(chunk);
          selectionChanged = true;
          notifyChunkUpdated(chunk);
        }
      }
    }
    if (selectionChanged) {
      notifyChunkSelectionChange();
    }
    return selectionChanged;
  }

  /**
   * Deselect all chunks.
   */
  public void clearSelection() {
    if (!selected.isEmpty()) {
      Set<ChunkPosition> prev = new HashSet<>(selected);
      selected.clear();
      notifyChunksUpdated(prev);
      notifyChunkSelectionChange();
    }
  }

  /**
   * Select the given chunks.
   * @param chunks Chunks to select
   */
  public void setSelection(Collection<ChunkPosition> chunks) {
    Set<ChunkPosition> prev = new HashSet<>(selected);
    selected.clear();
    selected.addAll(chunks);
    prev.addAll(chunks);
    notifyChunksUpdated(prev);
    notifyChunkSelectionChange();
  }

  /**
   * @return <code>true</code> if the given chunk position is selected
   */
  public boolean isSelected(ChunkPosition chunk) {
    return selected.contains(chunk);
  }

  /**
   * @return The currently selected chunks
   */
  public synchronized Collection<ChunkPosition> getSelection() {
    return new LinkedList<>(selected);
  }

  /**
   * @return The number of selected chunks
   */
  public int size() {
    return selected.size();
  }
}
