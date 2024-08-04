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


  /**
   * sets a chunk without checking whether it exists in the world
   */
  private boolean setChunk(ChunkPosition pos, boolean selected) {
    long regionPosLong = ChunkPosition.positionToLong(pos.x >> 5, pos.z >> 5);
    BitSet selectedChunksForRegion = selectedChunksByRegion.computeIfAbsent(regionPosLong, p -> new BitSet(MCRegion.CHUNKS_X * MCRegion.CHUNKS_Z));
    int bitIndex = (pos.x & 31) + ((pos.z & 31) << 5);
    boolean previousValue = selectedChunksForRegion.get(bitIndex);
    if(previousValue != selected) {
      selectedChunksForRegion.set(bitIndex, selected);

      if (selectedChunksForRegion.nextSetBit(0) == -1) {
        //all bits are 0, we don't need to track this region anymore
        selectedChunksByRegion.remove(regionPosLong);
      }

      notifyChunkUpdated(pos);
      return true;
    }
    return false;
  }
  /**
   * @return Whether the selection changed
   */
  private boolean setChunk(Dimension dimension, ChunkPosition pos, boolean selected) {
    //Only need to check if the chunk isn't empty on selecting a chunk, as it must exist if it's already selected
    Chunk chunk = dimension.getChunk(pos);
    if(selected && (chunk == EmptyRegionChunk.INSTANCE)) {
      return false;
    }
    return setChunk(pos, selected);
  }

  /**
   * Minimum and maximum are both INCLUSIVE
   * @return Whether the selection changed
   */
  private boolean setChunksWithinRegion(Dimension dimension, RegionPosition regionPos, int minX, int maxX, int minZ, int maxZ, boolean selected) {
    BitSet selectedChunksForRegion = selectedChunksByRegion.computeIfAbsent(regionPos.getLong(), p -> new BitSet(MCRegion.CHUNKS_X * MCRegion.CHUNKS_Z));

    Collection<ChunkPosition> changedChunks = new ArrayList<>();
    boolean selectionChanged = false;

    for (int chunkX = minX; chunkX <= maxX; chunkX++) {
      for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
        int bitIndex = (chunkX & 31) + ((chunkZ & 31) << 5);
        boolean previousValue = selectedChunksForRegion.get(bitIndex);
        if(previousValue != selected) {
          ChunkPosition chunkPos = new ChunkPosition(chunkX, chunkZ);
          Chunk chunk = dimension.getChunk(chunkPos);
          if(chunk != EmptyRegionChunk.INSTANCE) {
            selectionChanged = true;
            selectedChunksForRegion.set(bitIndex, selected);

            changedChunks.add(chunkPos);
          }
        }
      }
    }

    if (selectedChunksForRegion.nextSetBit(0) == -1) {
      //all bits are 0, we don't need to track this region anymore
      selectedChunksByRegion.remove(regionPos.getLong());
    }

    if(selectionChanged) {
      notifyRegionChunksUpdated(regionPos, changedChunks);
    }
    return selectionChanged;
  }

  /**
   * @return Whether the selection changed
   */
  private boolean setRegion(Dimension dimension, RegionPosition regionPos, boolean selected) {
    return setChunks(dimension, regionPos.x << 5, regionPos.z << 5, (regionPos.x << 5) + 31, (regionPos.z << 5) + 31, selected);
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
   * Notify the chunk update listeners that chunks have been updated
   *
   * @param region the region with updated chunks
   */
  private void notifyRegionChunksUpdated(RegionPosition region) {
    for (ChunkUpdateListener listener : chunkUpdateListeners) {
      listener.regionChunksUpdated(region);
    }
  }

  /**
   * Notify the chunk update listeners that chunks have been updated
   *
   * @param region the region with updated chunks
   * @param chunks the chunks within the region that have been updated
   */
  private void notifyRegionChunksUpdated(RegionPosition region, Collection<ChunkPosition> chunks) {
    for (ChunkUpdateListener listener : chunkUpdateListeners) {
      listener.regionChunksUpdated(region, chunks);
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
  public synchronized void toggleChunk(Dimension dimension, int cx, int cz) {
    ChunkPosition chunk = new ChunkPosition(cx, cz);
    if (isChunkSelected(chunk)) {
      setChunk(dimension, chunk, false);
    } else if (!dimension.getChunk(chunk).isEmpty()) {
      setChunk(dimension, chunk, true);
    }
    notifyChunkSelectionChange();
  }

  /**
   * Adds a chunk to the selection.
   *
   * @param cx chunk x-position
   * @param cz chunk z-position
   */
  public synchronized void selectChunk(Dimension dimension, int cx, int cz) {
    ChunkPosition chunk = new ChunkPosition(cx, cz);
    if (!dimension.getChunk(chunk).isEmpty()) {
      setChunk(dimension, chunk, true);
      notifyChunkSelectionChange();
    }
  }

  /**
   * Select the region containing the given chunk.
   *
   * @param cx chunk x-position
   * @param cz chunk z-position
   */
  public synchronized void toggleRegion(Dimension dimension, int cx, int cz) {
    ChunkPosition chunk = new ChunkPosition(cx, cz);
    setRegion(dimension, chunk.asRegionPosition(), !isChunkSelected(chunk));
    notifyChunkSelectionChange();
  }

  /**
   * Select chunks within rectangle.
   * Minimum and maximum are both INCLUSIVE
   *
   * @return true if anything was changed, false if no chunks were selected.
   */
  public synchronized boolean setChunks(Dimension dimension, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ, boolean selected) {
    boolean selectionChanged = false;

    // If selection area must contain complete regions
    if(maxChunkX - minChunkX >= MCRegion.CHUNKS_X*2 && maxChunkZ - minChunkZ >= MCRegion.CHUNKS_Z*2) {
      // All full regions are set first, then any chunks on the borders are set, top and bottom include corners, left and right don't

      // left, right, top, bottom are unrelated to the actual map view, and are just treating XZ as if they were XY on traditional cartesian coordinate axes
      int leftBorder = MCRegion.CHUNKS_X - (minChunkX & (MCRegion.CHUNKS_X-1)); // want the border from minimum region corner to minimum chunk corner, so 32 - borderSize
      int rightBorder = maxChunkX & (MCRegion.CHUNKS_X-1);

      int bottomBorder = MCRegion.CHUNKS_Z - (minChunkZ & (MCRegion.CHUNKS_Z-1));
      int topBorder = maxChunkZ & (MCRegion.CHUNKS_Z-1);

      int minInnerRegionX = (minChunkX + leftBorder) >> 5;
      int maxInnerRegionX = (maxChunkX - rightBorder) >> 5;

      int minInnerRegionZ = (minChunkZ + bottomBorder) >> 5;
      int maxInnerRegionZ = (maxChunkZ - topBorder) >> 5;


      int minRegionX = minChunkX >> 5;
      int maxRegionX = maxChunkX >> 5;

      int minRegionZ = minChunkZ >> 5;
      int maxRegionZ = maxChunkZ >> 5;

      // set all inner regions from the selection
      for (int regionX = minRegionX; regionX < maxRegionX + 1; regionX++) {
        for (int regionZ = minRegionZ; regionZ < maxRegionZ + 1; regionZ++) {
          if(regionX >= minInnerRegionX && regionX < maxInnerRegionX && regionZ >= minInnerRegionZ && regionZ < maxInnerRegionZ) {
            // this region is an inner region, we set all chunks within it
            selectionChanged |= setRegion(dimension, new RegionPosition(regionX, regionZ), selected);
          } else {
            // this region is an outer region, we set only the chunks within the bounds of the selection area
            selectionChanged |= setChunksWithinRegion(dimension, new RegionPosition(regionX, regionZ),
              Math.max(regionX << 5, minChunkX), Math.min(((regionX + 1) << 5) - 1, maxChunkX),
              Math.max(regionZ << 5, minChunkZ), Math.min(((regionZ + 1) << 5) - 1, maxChunkZ),
              selected);
          }
        }
      }

    } else { //selection area likely doesn't contain complete regions, but is also small so do it per-chunk
      int minRegionX = minChunkX >> 5;
      int maxRegionX = maxChunkX >> 5;

      int minRegionZ = minChunkZ >> 5;
      int maxRegionZ = maxChunkZ >> 5;

      for (int regionX = minRegionX; regionX < maxRegionX + 1; regionX++) {
        for (int regionZ = minRegionZ; regionZ < maxRegionZ + 1; regionZ++) {
          selectionChanged |= setChunksWithinRegion(dimension, new RegionPosition(regionX, regionZ),
            Math.max(regionX << 5, minChunkX), Math.min(((regionX + 1) << 5) - 1, maxChunkX),
            Math.max(regionZ << 5, minChunkZ), Math.min(((regionZ + 1) << 5) - 1, maxChunkZ),
            selected);
        }
      }
    }

    if (selectionChanged) {
      notifyChunkSelectionChange();
    }
    return selectionChanged;
  }

  /**
   * Select chunks within circle.
   *
   * @return true if anything was changed, false if no chunks were selected.
   */
  public synchronized boolean setChunkRadius(Dimension dimension, int centerChunkX, int centerChunkZ, float chunksRadius, boolean selected) {
    // could be optimised to call setRegion() for regions entirely inside the circle, if necessary
    boolean selectionChanged = false;

    int minChunkX = (int) (centerChunkX - chunksRadius);
    int minChunkZ = (int) (centerChunkZ - chunksRadius);

    int maxChunkX = (int) (centerChunkX + chunksRadius);
    int maxChunkZ = (int) (centerChunkZ + chunksRadius);

    float radiusSquared = chunksRadius * chunksRadius;

    for (int chunkX = minChunkX; chunkX < maxChunkX + 1; chunkX++) {
      for (int chunkZ = minChunkZ; chunkZ < maxChunkZ + 1; chunkZ++) {
        int chunkXOffset = chunkX - centerChunkX;
        int chunkZOffset = chunkZ - centerChunkZ;
        if (chunkXOffset * chunkXOffset + chunkZOffset * chunkZOffset < radiusSquared) {
          selectionChanged |= setChunk(dimension, ChunkPosition.get(chunkX, chunkZ), selected);
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
      //Collect all region positions currently selected (slightly weird because concurrent map)
      Enumeration<Long> keys = selectedChunksByRegion.keys();
      Collection<RegionPosition> regionPositions = new ArrayList<>();
      while (keys.hasMoreElements()) {
        regionPositions.add(new RegionPosition(keys.nextElement()));
      }

      selectedChunksByRegion.clear();
      for (RegionPosition regionPosition : regionPositions) {
        notifyRegionChunksUpdated(regionPosition);
      }
      notifyChunkSelectionChange();
    }
  }

  /**
   * Select the given chunks.
   * @param chunks Chunks to select
   */
  public void setSelection(Collection<ChunkPosition> chunks) {
    clearSelection();
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
      RegionPosition regionPos = new RegionPosition(regionPosition);
      List<ChunkPosition> positions = new ArrayList<>();
      for (int localX = 0; localX < MCRegion.CHUNKS_X; localX++) {
        for (int localZ = 0; localZ < MCRegion.CHUNKS_Z; localZ++) {
          int idx = localX + (localZ * MCRegion.CHUNKS_X);
          if(selectedChunksBitSet.get(idx)) {
            positions.add(regionPos.asChunkPosition(localX, localZ));
          }
        }
      }
      selectedChunksByRegionPosition.put(new ChunkPosition(regionPosition), positions);
    });
    return selectedChunksByRegionPosition;
  }

  /**
   * @return The number of selected chunks
   */
  public synchronized int size() {
    int size = 0;
    for (BitSet selectedChunks : selectedChunksByRegion.values()) {
      size += selectedChunks.cardinality();
    }
    return size;
  }

  public synchronized boolean isEmpty() {
    return selectedChunksByRegion.isEmpty();
  }
}
