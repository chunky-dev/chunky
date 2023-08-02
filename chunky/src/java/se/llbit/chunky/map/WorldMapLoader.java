/* Copyright (c) 2010-2019 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.map;

import java.util.function.BiConsumer;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.ui.controller.ChunkyFxController;
import se.llbit.chunky.world.*;
import se.llbit.chunky.world.region.RegionChangeWatcher;
import se.llbit.chunky.world.region.RegionParser;
import se.llbit.chunky.world.region.RegionQueue;
import se.llbit.chunky.world.listeners.ChunkTopographyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dynamically loads regions and chunks for the 2D world map.
 */
public class WorldMapLoader implements ChunkTopographyListener, ChunkViewListener {
  private final ChunkyFxController controller;
  private final MapView mapView;

  private World world = EmptyWorld.INSTANCE;

  private final RegionQueue regionQueue = new RegionQueue();
  private RegionChangeWatcher regionChangeWatcher = null;

  private final ChunkTopographyUpdater topographyUpdater = new ChunkTopographyUpdater();

  /** The dimension to load in the current world. */
  private int currentDimensionId = PersistentSettings.getDimension();

  private List<BiConsumer<World, Boolean>> worldLoadListeners = new ArrayList<>();

  public WorldMapLoader(ChunkyFxController controller, MapView mapView) {
    this.controller = controller;
    this.mapView = mapView;
    mapView.addViewListener(this);

    // Start worker threads.
    RegionParser[] regionParsers = new RegionParser[Integer.parseInt(System.getProperty("chunky.mapLoaderThreads", String.valueOf(PersistentSettings.getNumThreads())))];
    for (int i = 0; i < regionParsers.length; ++i) {
      regionParsers[i] = new RegionParser(this, regionQueue, mapView);
      regionParsers[i].start();
    }
    topographyUpdater.start();
  }

  /**
   * This is called when a new world is loaded
   */
  public void loadWorld(File worldDir) {
    if (World.isWorldDir(worldDir)) {
      if (world != null) {
        world.currentDimension().removeChunkTopographyListener(this);
      }
      boolean isSameWorld = !(world instanceof EmptyWorld) && worldDir.equals(world.getWorldDirectory());
      World newWorld = World.loadWorld(worldDir, currentDimensionId, World.LoggedWarnings.NORMAL);
      newWorld.currentDimension().addChunkTopographyListener(this);
      synchronized (this) {
        world = newWorld;
        updateRegionChangeWatcher(newWorld.currentDimension());

        File newWorldDir = world.getWorldDirectory();
        if (newWorldDir != null) {
          PersistentSettings.setLastWorld(newWorldDir);
        }
      }
      worldLoadListeners.forEach(listener -> listener.accept(newWorld, isSameWorld));
    }
  }

  /**
   * This is called when switching to a different dimension within the same world
   */
  public void loadDimension() {
    world.currentDimension().removeChunkTopographyListener(this);

    world.loadDimension(currentDimensionId);
    world.currentDimension().addChunkTopographyListener(this);
    synchronized (this) {
      updateRegionChangeWatcher(world.currentDimension());
    }
    worldLoadListeners.forEach(listener -> listener.accept(world, false));
  }

  /** Adds a listener to be notified when a new world has been loaded. */
  public void addWorldLoadListener(BiConsumer<World, Boolean> callback) {
    worldLoadListeners.add(callback);
  }

  /** Called when the map view has changed to load the visible chunks. */
  @Override public synchronized void viewUpdated(ChunkView mapView) {
    int rx0 = mapView.prx0;
    int rx1 = mapView.prx1;
    int rz0 = mapView.prz0;
    int rz1 = mapView.prz1;

    // Enqueue visible regions and chunks to be loaded.
    for (int rx = rx0; rx <= rx1; ++rx) {
      for (int rz = rz0; rz <= rz1; ++rz) {
        regionQueue.add(new ChunkPosition(rx, rz));
      }
    }
  }

  /**
   * Get the current loaded world.
   *
   * @return The current world
   */
  public synchronized World getWorld() {
    return world;
  }

  public synchronized void withWorld(Consumer<World> fun) {
    fun.accept(world);
  }

  /** Called to notify the world loader that a region was changed. */
  public void regionUpdated(ChunkPosition region) {
    regionQueue.add(region);
  }

  @Override public void chunksTopographyUpdated(Chunk chunk) {
    topographyUpdater.addChunk(chunk);
  }

  /**
   * Flush all cached chunks and regions, forcing them to be reloaded
   * for the current world.
   */
  public void reloadWorld() {
    topographyUpdater.clearQueue();
    world.currentDimension().removeChunkTopographyListener(this);
    World newWorld = World.loadWorld(world.getWorldDirectory(), currentDimensionId,
        World.LoggedWarnings.NORMAL);
    newWorld.currentDimension().addChunkTopographyListener(this);
    synchronized (this) {
      world = newWorld;
      updateRegionChangeWatcher(newWorld.currentDimension());
    }
    worldLoadListeners.forEach(listener -> listener.accept(newWorld, true));
    viewUpdated(mapView.getMapView()); // update visible chunks immediately
  }

  /** Stops the current RegionChangeWatcher, and creates a new one for the specified world */
  private void updateRegionChangeWatcher(Dimension dimension) {
    if(regionChangeWatcher != null) {
      regionChangeWatcher.interrupt();
    }
    regionChangeWatcher = dimension.createRegionChangeWatcher(this, mapView);
    regionChangeWatcher.start();
  }

  /**
   * Set the current dimension.
   *
   * @param value Must be a valid dimension index (0, -1, 1)
   */
  public void setDimension(int value) {
    if (value != currentDimensionId) {
      currentDimensionId = value;
      PersistentSettings.setDimension(currentDimensionId);

      loadDimension();
      viewUpdated(mapView.getMapView()); // update visible chunks immediately
    }
  }

  /** Get the currently loaded dimension. */
  public int getDimension() {
    return currentDimensionId;
  }
}
