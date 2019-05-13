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
import se.llbit.math.Vector3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dynamically loads regions and chunks for the 2D world map.
 */
public class WorldMapLoader implements ChunkTopographyListener, ChunkViewListener {
  private final ChunkyFxController controller;
  private MapView mapView;

  private World world = EmptyWorld.instance;

  private final RegionQueue regionQueue = new RegionQueue();

  private final ChunkTopographyUpdater topographyUpdater = new ChunkTopographyUpdater();
  private final RegionChangeWatcher refresher = new RegionChangeWatcher(this);

  private int currentDimension = PersistentSettings.getDimension();

  private List<Consumer<World>> worldLoadListeners = new ArrayList<>();

  public WorldMapLoader(ChunkyFxController controller, MapView mapView) {
    this.controller = controller;
    this.mapView = mapView;

    // Start worker threads.
    RegionParser[] regionParsers = new RegionParser[3];
    for (int i = 0; i < regionParsers.length; ++i) {
      regionParsers[i] = new RegionParser(this, regionQueue, mapView);
      regionParsers[i].start();
    }
    topographyUpdater.start();
    refresher.start();
  }

  public void loadWorld(File worldDir) {
    if (worldDir != null && World.isWorldDir(worldDir)) {
      loadWorld(new World(worldDir, false));
    }
  }

  /**
   * This is called when a new world is loaded, and when switching to a
   * different dimension.
   *
   * <p>NB: When switching dimension this is called with newWorld = world.
   */
  public synchronized void loadWorld(World newWorld) {
    // Dispose old world.
    world.dispose();

    newWorld.reload();

    newWorld.addChunkUpdateListener(controller);
    newWorld.addChunkUpdateListener(controller.getMap());

    world = newWorld;
    world.addChunkTopographyListener(this);

    // Dimension must be set before chunks are loaded.
    world.setDimension(currentDimension);

    Vector3 playerPos = world.playerPos();
    if (playerPos != null) {
      panToPlayer();
    } else {
      mapView.panTo(0, 0);
    }

    File newWorldDir = world.getWorldDirectory();
    if (newWorldDir != null) {
      PersistentSettings.setLastWorld(newWorldDir);
    }

    worldLoadListeners.forEach(listener -> listener.accept(newWorld));
  }

  public void addWorldLoadListener(Consumer<World> callback) {
    worldLoadListeners.add(callback);
  }

  /**
   * Called when the map view has changed.
   */
  @Override public synchronized void viewUpdated(ChunkView mapView) {
    refresher.setView(mapView);

    int rx0 = mapView.prx0;
    int rx1 = mapView.prx1;
    int rz0 = mapView.prz0;
    int rz1 = mapView.prz1;

    // Enqueue visible regions and chunks.
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

  /**
   * Get the name of the current loaded world.
   *
   * @return The name of the current world
   */
  public String getWorldName() {
    return world.levelName();
  }

  public void panToPlayer() {
    Vector3 pos = world.playerPos();
    if (pos != null) {
      mapView.panTo(pos);
    }
  }

  /**
   * Called to notify the world loader that a region was changed.
   */
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
    world.reload();
  }

  /**
   * Set the current dimension.
   *
   * @param value Must be a valid dimension index
   */
  public void setDimension(int value) {
    if (value != currentDimension) {
      currentDimension = value;
      PersistentSettings.setDimension(currentDimension);

      // Note: here we are loading the same world again just to load the
      // next dimension. However, this is a very ugly way to handle dimension
      // switching and it would probably be much better to just create a fresh
      // world instance to load.
      loadWorld(world);
    }
  }

  /**
   * @return <code>true</code> if chunks or regions are currently being parsed
   */
  public boolean isLoading() {
    return !regionQueue.isEmpty();
  }

  /**
   * Get the currently loaded dimension.
   */
  public int getDimension() {
    return currentDimension;
  }
}
