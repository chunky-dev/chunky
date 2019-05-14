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

import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.ui.ChunkyFxController;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkTopographyUpdater;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.RegionChangeWatcher;
import se.llbit.chunky.world.RegionParser;
import se.llbit.chunky.world.RegionQueue;
import se.llbit.chunky.world.World;
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

  private World world = EmptyWorld.INSTANCE;

  private final RegionQueue regionQueue = new RegionQueue();

  private final ChunkTopographyUpdater topographyUpdater = new ChunkTopographyUpdater();

  /** The dimension to load in the current world. */
  private int currentDimension = PersistentSettings.getDimension();

  private List<Consumer<World>> worldLoadListeners = new ArrayList<>();

  public WorldMapLoader(ChunkyFxController controller, MapView mapView) {
    this.controller = controller;
    mapView.addViewListener(this);
    RegionChangeWatcher regionWatcher = new RegionChangeWatcher(this, mapView);

    // Start worker threads.
    RegionParser[] regionParsers = new RegionParser[3];
    for (int i = 0; i < regionParsers.length; ++i) {
      regionParsers[i] = new RegionParser(this, regionQueue, mapView);
      regionParsers[i].start();
    }
    topographyUpdater.start();
    regionWatcher.start();
  }

  /**
   * This is called when a new world is loaded, and when switching to a
   * different dimension.
   */
  public void loadWorld(File worldDir) {
    if (World.isWorldDir(worldDir)) {
      World newWorld = World.loadWorld(worldDir, currentDimension, World.LoggedWarnings.NORMAL);
      newWorld.addChunkUpdateListener(controller);
      newWorld.addChunkUpdateListener(controller.getMap());
      newWorld.addChunkTopographyListener(this);
      synchronized (this) {
        world = newWorld;
        File newWorldDir = world.getWorldDirectory();
        if (newWorldDir != null) {
          PersistentSettings.setLastWorld(newWorldDir);
        }
      }
      worldLoadListeners.forEach(listener -> listener.accept(newWorld));
    }
  }

  /** Adds a listener to be notified when a new world has been loaded. */
  public void addWorldLoadListener(Consumer<World> callback) {
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
        regionQueue.add(ChunkPosition.get(rx, rz));
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
  public synchronized void reloadWorld() {
    world = World.loadWorld(world.getWorldDirectory(), currentDimension,
        World.LoggedWarnings.NORMAL);
  }

  /**
   * Set the current dimension.
   *
   * @param value Must be a valid dimension index (0, -1, 1)
   */
  public void setDimension(int value) {
    if (value != currentDimension) {
      currentDimension = value;
      PersistentSettings.setDimension(currentDimension);

      // Note: here we are loading the same world again just to load the
      // next dimension. However, this is a very ugly way to handle dimension
      // switching and it would probably be much better to just create a fresh
      // world instance to load.
      loadWorld(world.getWorldDirectory());
    }
  }

  /** Get the currently loaded dimension. */
  public int getDimension() {
    return currentDimension;
  }
}
