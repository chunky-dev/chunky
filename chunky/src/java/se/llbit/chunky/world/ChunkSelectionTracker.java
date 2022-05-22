/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import se.llbit.chunky.world.listeners.ChunkDeletionListener;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;
import se.llbit.chunky.world.region.MCRegion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks chunk selections.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChunkSelectionTracker implements ChunkDeletionListener {

  private final ConcurrentHashMap<Long, BitSet> selectedChunksByRegion = new ConcurrentHashMap<>();
  private final Collection<ChunkUpdateListener> chunkUpdateListeners = new LinkedList<>();
  private final Collection<ChunkSelectionListener> selectionListeners = new LinkedList<>();

  private boolean setChunk(ChunkPosition pos, boolean selected) {
    BitSet selectedChunksForRegion = selectedChunksByRegion.computeIfAbsent(ChunkPosition.positionToLong(pos.x >> 5, pos.z >> 5), p -> new BitSet(MCRegion.CHUNKS_X * MCRegion.CHUNKS_Z));
    int bitIndex = (pos.x & 31) + ((pos.z & 31) << 5);
    boolean previousValue = selectedChunksForRegion.get(bitIndex);
    selectedChunksForRegion.set(bitIndex, selected);

    if(selectedChunksForRegion.nextSetBit(0) >= selectedChunksForRegion.length()) {
      //all bits are 0, we don't need to track this region anymore
      selectedChunksByRegion.remove(ChunkPosition.positionToLong(pos.x >> 5, pos.z >> 5));
    }

    notifyChunkUpdated(pos);

    return previousValue != selected; //return if changed
  }
  private boolean setRegion(ChunkPosition regionPos, boolean selected) {
    long positionAsLong = regionPos.getLong();
    BitSet selectedChunksForRegion = selectedChunksByRegion.computeIfAbsent(positionAsLong, p -> new BitSet(32 * 32));
    int size = selectedChunksForRegion.size();
    // We know the bitset will change if all the bits are the same, and the first bit isn't equal to selected
    // or if all the bits aren't the same
    boolean allBitsAreSame = selectedChunksForRegion.nextSetBit(0) == size
      || selectedChunksForRegion.nextClearBit(0) == size;
    boolean willChange = !allBitsAreSame || selectedChunksForRegion.get(0) != selected;

    selectedChunksForRegion.set(0, size, selected);

    if(selectedChunksForRegion.nextSetBit(0) >= size) {
      //all bits are 0, we don't need to track this region anymore
      selectedChunksByRegion.remove(positionAsLong);
    }

    //fire notifyChunkUpdated for every chunk within the region
    for (int chunkX = regionPos.x << 5, maxChunkX = chunkX+MCRegion.CHUNKS_X; chunkX < maxChunkX; chunkX++) {
      for (int chunkZ = regionPos.z << 5, maxChunkZ = chunkZ+MCRegion.CHUNKS_Z; chunkZ < maxChunkZ; chunkZ++) {
        notifyChunkUpdated(ChunkPosition.get(chunkX, chunkZ));
      }
    }

    return willChange;
  }
  private boolean isChunkSelected(ChunkPosition pos) {
    BitSet selectedChunksForRegion = selectedChunksByRegion.get(ChunkPosition.positionToLong(pos.x >> 5, pos.z >> 5));
    if(selectedChunksForRegion == null) {
      return false;
    }
    return selectedChunksForRegion.get((pos.x & 31) + ((pos.z & 31) << 5));
  }

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
    setChunk(chunk, false);
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
    if (isChunkSelected(chunk)) {
      setChunk(chunk, false);
    } else if (!world.getChunk(chunk).isEmpty()) {
      setChunk(chunk, true);
    }
    notifyChunkSelectionChange();
  }

  /**
   * Adds a chunk to the selection.
   *
   * @param cx chunk x-position
   * @param cz chunk z-position
   */
  public synchronized void selectChunk(World world, int cx, int cz) {
    ChunkPosition chunk = ChunkPosition.get(cx, cz);
    if (!world.getChunk(chunk).isEmpty()) {
      setChunk(chunk, true);
      notifyChunkSelectionChange();
    }
  }

  /**
   * Select the region containing the given chunk.
   *
   * @param cx chunk x-position
   * @param cz chunk z-position
   */
  public synchronized void toggleRegion(World world, int cx, int cz) {
    ChunkPosition chunk = ChunkPosition.get(cx, cz);
    setRegion(ChunkPosition.get(cx >> 5, cz >> 5), !isChunkSelected(chunk));
    notifyChunkSelectionChange();
  }

  /**
   * Select chunks within rectangle.
   * @return true if anything was changed, false if no chunks were selected.
   */
  public synchronized boolean setChunks(World world, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ, boolean selected) {
    boolean selectionChanged = false;

    // If selection area must contain complete regions
    if(maxChunkX - minChunkX >= MCRegion.CHUNKS_X*2 && maxChunkZ - minChunkZ >= MCRegion.CHUNKS_Z*2) {
      int minBorderX = MCRegion.CHUNKS_X - (minChunkX & (MCRegion.CHUNKS_X-1)); // want the border from minimum region corner to minimum chunk corner, so 32 - borderSize
      int maxBorderX = maxChunkX & (MCRegion.CHUNKS_X-1);

      int minBorderZ = MCRegion.CHUNKS_Z - (minChunkZ & (MCRegion.CHUNKS_Z-1));
      int maxBorderZ = maxChunkZ & (MCRegion.CHUNKS_Z-1);

      int minInnerRegionX = (minChunkX + minBorderX) >> 5;
      int maxInnerRegionX = (maxChunkX - maxBorderX) >> 5;

      int minInnerRegionZ = (minChunkZ + minBorderZ) >> 5;
      int maxInnerRegionZ = (maxChunkZ - maxBorderZ) >> 5;

      // set all inner regions from the selection
      for (int regionX = minInnerRegionX; regionX < maxInnerRegionX; regionX++) {
        for (int regionZ = minInnerRegionZ; regionZ < maxInnerRegionZ; regionZ++) {
          selectionChanged |= setRegion(ChunkPosition.get(regionX, regionZ), selected);
        }
      }

      //set top border
      for (int x = minChunkX; x < maxChunkX; x++) {
        for (int z = maxChunkZ - maxBorderZ; z < maxChunkZ; z++) {
          ChunkPosition chunkPos = ChunkPosition.get(x, z);
          if(!world.getChunk(chunkPos).isEmpty()) {
            selectionChanged |= setChunk(chunkPos, selected);
          }
        }
      }
      //set bottom border
      for (int x = minChunkX; x < maxChunkX; x++) {
        for (int z = minChunkZ; z < minChunkZ + minBorderZ; z++) {
          ChunkPosition chunkPos = ChunkPosition.get(x, z);
          if(!world.getChunk(chunkPos).isEmpty()) {
            selectionChanged |= setChunk(chunkPos, selected);
          }
        }
      }
      //set left border without corners
      for (int x = minChunkX; x < minChunkX + minBorderX; x++) {
        for (int z = minChunkZ + minBorderZ; z < maxChunkZ - maxBorderZ; z++) {
          ChunkPosition chunkPos = ChunkPosition.get(x, z);
          if(!world.getChunk(chunkPos).isEmpty()) {
            selectionChanged |= setChunk(chunkPos, selected);
          }
        }
      }

      //set right border without corners
      for (int x = maxChunkX - maxBorderX; x < maxChunkX; x++) {
        for (int z = minChunkZ + minBorderZ; z < maxChunkZ - maxBorderZ; z++) {
          ChunkPosition chunkPos = ChunkPosition.get(x, z);
          if(!world.getChunk(chunkPos).isEmpty()) {
            selectionChanged |= setChunk(chunkPos, selected);
          }
        }
      }

    } else { //selection area likely doesn't contain complete regions, but is also small so do it per-chunk
      for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
        for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
          ChunkPosition chunkPos = ChunkPosition.get(cx, cz);
          if (!world.getChunk(chunkPos).isEmpty()) {
            selectionChanged |= setChunk(chunkPos, selected);
          }
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
  public synchronized void clearSelection() {
    if (!selectedChunksByRegion.isEmpty()) {
      Collection<ChunkPosition> selection = getSelection();
      selectedChunksByRegion.clear();

      notifyChunksUpdated(selection);
      notifyChunkSelectionChange();
    }
  }

  /**
   * Select the given chunks.
   * @param chunks Chunks to select
   */
  public void setSelection(Collection<ChunkPosition> chunks) {
    if (!selectedChunksByRegion.isEmpty()) {
      notifyChunksUpdated(getSelection());
      selectedChunksByRegion.clear();
    }
    for (ChunkPosition chunk : chunks) {
      setChunk(chunk, true);
    }
    notifyChunkSelectionChange();
  }

  /**
   * @return <code>true</code> if the given chunk position is selected
   */
  public boolean isSelected(ChunkPosition chunk) {
    return isChunkSelected(chunk);
  }

  /**
   * @return The currently selected chunks
   */
  public synchronized Collection<ChunkPosition> getSelection() {
    Map<ChunkPosition, List<ChunkPosition>> selectionByRegion = getSelectionByRegion();
    List<ChunkPosition> selectedChunks = new ArrayList<>();
    selectionByRegion.forEach((regionPosition, chunks) -> selectedChunks.addAll(chunks));
    return selectedChunks;
  }

  public synchronized Map<ChunkPosition, List<ChunkPosition>> getSelectionByRegion() {
    Map<ChunkPosition, List<ChunkPosition>> selectedChunksByRegionPosition = new Object2ReferenceOpenHashMap<>();
    selectedChunksByRegion.forEach((regionPosition, selectedChunksBitSet) -> {
      ChunkPosition regionPos = ChunkPosition.get(regionPosition);
      List<ChunkPosition> positions = new ArrayList<>();
      for (int localX = 0; localX < MCRegion.CHUNKS_X; localX++) {
        for (int localZ = 0; localZ < MCRegion.CHUNKS_Z; localZ++) {
          int idx = localX + (localZ * MCRegion.CHUNKS_X);
          if(selectedChunksBitSet.get(idx)) {
            positions.add(ChunkPosition.get((regionPos.x << 5) + localX, (regionPos.z << 5) + localZ));
          }
        }
      }
      selectedChunksByRegionPosition.put(ChunkPosition.get(regionPosition), positions);
    });
    return selectedChunksByRegionPosition;
  }

  /**
   * @return The number of selected chunks
   */
  public synchronized int size() {
    int size = 0;
    for (BitSet selectedChunks : selectedChunksByRegion.values()) {
      for (int idx = 0; idx < selectedChunks.size(); idx++) {
        if(selectedChunks.get(idx)) {
          size++;
        }
      }
    }
    return size;
  }

  public synchronized boolean isEmpty() {
    return selectedChunksByRegion.isEmpty();
  }
}
