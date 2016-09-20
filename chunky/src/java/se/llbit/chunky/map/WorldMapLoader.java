/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.ZipExportJob;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.ui.ChunkyFxController;
import se.llbit.chunky.ui.MapViewMode;
import se.llbit.chunky.ui.ProgressTracker;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkSelectionTracker;
import se.llbit.chunky.world.ChunkTopographyUpdater;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.DeleteChunksJob;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.RegionChangeMonitor;
import se.llbit.chunky.world.RegionParser;
import se.llbit.chunky.world.RegionQueue;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.listeners.ChunkTopographyListener;
import se.llbit.math.Vector3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorldMapLoader implements ChunkTopographyListener {
  private final ChunkyFxController controller;

  private BooleanProperty trackPlayer =
      new SimpleBooleanProperty(PersistentSettings.getFollowPlayer());
  private BooleanProperty trackCamera =
      new SimpleBooleanProperty(PersistentSettings.getFollowCamera());

  private World world = EmptyWorld.instance;

  private final RegionQueue regionQueue = new RegionQueue();

  private final ChunkTopographyUpdater topographyUpdater = new ChunkTopographyUpdater();
  private final RegionChangeMonitor refresher = new RegionChangeMonitor(this);

  private int currentDimension = PersistentSettings.getDimension();
  protected ChunkSelectionTracker chunkSelection = new ChunkSelectionTracker();

  private BooleanProperty highlightEnabled = new SimpleBooleanProperty(false);
  private Block highlightBlock = Block.DIAMONDORE;
  private Color highlightColor = Color.CRIMSON;

  private volatile ObjectProperty<ChunkView> map = new SimpleObjectProperty<>(ChunkView.EMPTY);
  private volatile ChunkView minimap = ChunkView.EMPTY;

  int minimapWidth = 100;
  int minimapHeight = 100;

  private List<ChunkViewListener> viewListeners = new ArrayList<>();
  private List<Runnable> worldLoadListeners = new ArrayList<>();

  public WorldMapLoader(ChunkyFxController controller) {
    this.controller = controller;

    map.addListener((observable, oldValue, newValue) -> {
      if (oldValue.layer != newValue.layer) {
        world.setCurrentLayer(newValue.layer);
        for (ChunkViewListener listener : viewListeners) {
          listener.layerChanged(newValue.layer);
        }
      }
      if (newValue.shouldRepaint(oldValue)) {
        viewUpdated(newValue);
      }
    });

    trackPlayer.addListener(e -> {
      boolean track = trackPlayer.get();
      PersistentSettings.setFollowPlayer(track);
      if (track) {
        panToPlayer();
      }
    });

    trackCamera.addListener(e -> {
      boolean track = trackCamera.get();
      PersistentSettings.setFollowCamera(track);
      if (track) {
        controller.panToCamera();
      }
    });

    highlightEnabled.addListener(e -> {
      setRenderer(MapViewMode.LAYER);
      notifyViewUpdated();
    });

    // Start worker threads.
    RegionParser[] regionParsers = new RegionParser[3];
    for (int i = 0; i < regionParsers.length; ++i) {
      regionParsers[i] = new RegionParser(this, regionQueue);
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
    newWorld.addChunkUpdateListener(controller.getMinimap());
    chunkSelection.clearSelection();

    world = newWorld;
    world.addChunkDeletionListener(chunkSelection);
    world.addChunkTopographyListener(this);

    // Dimension must be set before chunks are loaded.
    world.setDimension(currentDimension);

    Vector3 playerPos = world.playerPos();
    if (playerPos != null) {
      panToPlayer();
    } else {
      panTo(0, 0);
    }

    File newWorldDir = world.getWorldDirectory();
    if (newWorldDir != null) {
      PersistentSettings.setLastWorld(newWorldDir);
    }

    worldLoadListeners.forEach(Runnable::run);
  }

  public void addWorldLoadListener(Runnable callback) {
    worldLoadListeners.add(callback);
  }

  /**
   * Called when the map view has changed.
   */
  public synchronized void viewUpdated(ChunkView mapView) {
    refresher.setView(mapView);

    minimap = new ChunkView(mapView.x, mapView.z, minimapWidth, minimapHeight, 1,
        MapViewMode.BIOMES, mapView.layer);

    int rx0 = Math.min(minimap.prx0, mapView.prx0);
    int rx1 = Math.max(minimap.prx1, mapView.prx1);
    int rz0 = Math.min(minimap.prz0, mapView.prz0);
    int rz1 = Math.max(minimap.prz1, mapView.prz1);

    // Enqueue visible regions and chunks.
    for (int rx = rx0; rx <= rx1; ++rx) {
      for (int rz = rz0; rz <= rz1; ++rz) {
        regionQueue.add(ChunkPosition.get(rx, rz));
      }
    }

    notifyViewUpdated();
  }

  /**
   * Called when the map has been resized.
   */
  public void setMapSize(int width, int height) {
    ChunkView mapView = map.get();
    if (width != mapView.width || height != mapView.height) {
      map.set(new ChunkView(mapView.x, mapView.z, width, height, mapView.scale, mapView.renderer,
          mapView.layer));
    }
  }

  /**
   * Called when the minimap has been resized.
   */
  public void setMinimapSize(int width, int height) {
    if (width != minimapWidth || height != minimapHeight) {
      minimapWidth = width;
      minimapHeight = height;
      viewUpdated(map.get());
    }
  }

  /**
   * Move the map view.
   */
  public synchronized void moveView(double dx, double dz) {
    ChunkView mapView = map.get();
    panTo(mapView.x + dx, mapView.z + dz);
    viewUpdated(map.get());
  }

  /** Set the map view by block coordinates. */
  public void panTo(Vector3 pos) {
    // Convert from block coordinates to chunk coordinates.
    panTo(pos.x / 16, pos.z / 16);
  }

  /**
   * Move the map view.
   */
  public synchronized void panTo(double x, double z) {
    ChunkView mapView = map.get();
    map.set(new ChunkView(x, z,
        mapView.width, mapView.height, mapView.scale, mapView.renderer,
        mapView.layer));
  }
  /**
   * Set the currently viewed layer.
   */
  public synchronized void setLayer(int layer) {
    ChunkView mapView = map.get();
    map.set(new ChunkView(mapView.x, mapView.z,
        mapView.width, mapView.height, mapView.scale, mapView.renderer,
        layer));
  }

  /**
   * Select specific chunk.
   */
  public synchronized void selectChunk(int cx, int cz) {
    chunkSelection.selectChunk(world, cx, cz);
  }

  /**
   * @return The current world
   */
  public World getWorld() {
    return world;
  }

  /**
   * @return The name of the current world
   */
  public String getWorldName() {
    return world.levelName();
  }

  /**
   * @return The current map view
   */
  public ChunkView getMapView() {
    return map.get();
  }

  /**
   * @return The map view property.
   */
  public ObjectProperty<ChunkView> getMapViewProperty() {
    return map;
  }
  /**
   * @return The current minimap view
   */
  public ChunkView getMinimapView() {
    return minimap;
  }

  /**
   * @return The current map renderer
   */
  public MapViewMode getChunkRenderer() {
    return map.get().renderer;
  }

  /**
   * @return The chunk selection tracker
   */
  public ChunkSelectionTracker getChunkSelection() {
    return chunkSelection;
  }

  public void panToPlayer() {
    Vector3 pos = world.playerPos();
    if (pos != null) {
      panTo(pos);
    }
  }

  public void addViewListener(ChunkViewListener listener) {
    viewListeners.add(listener);
  }

  /**
   * The region was changed.
   */
  public void regionUpdated(ChunkPosition region) {
    regionQueue.add(region);
  }

  public synchronized Vector3 getPosition() {
    ChunkView mapView = map.get();
    return new Vector3(mapView.x, world.currentLayer(), mapView.z);
  }

  /**
   * @return The currently viewed layer
   */
  public synchronized int getLayer() {
    return world.currentLayer();
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
    notifyViewUpdated();
  }

  /**
   * Set the current map renderer.
   */
  public synchronized void setRenderer(MapViewMode renderer) {
    ChunkView mapView = map.get();
    if (renderer != mapView.renderer) {
      map.set(new ChunkView(mapView.x, mapView.z,
              mapView.width, mapView.height, mapView.scale, renderer,
              mapView.layer));
    }
  }

  /**
   * Toggle chunk selection.
   */
  public synchronized void toggleChunkSelection(int cx, int cz) {
    chunkSelection.toggleChunk(world, cx, cz);
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
   * Clears the chunk selection.
   */
  public synchronized void clearChunkSelection() {
    chunkSelection.clearSelection();
  }

  /**
   * @return The current block scale of the map view
   */
  public int getScale() {
    return map.get().scale;
  }

  /**
   * Called when the map view has been dragged by the user.
   */
  public void viewDragged(int dx, int dy) {
    double scale = getScale();
    moveView(dx / scale, dy / scale);
    viewListeners.forEach(ChunkViewListener::viewMoved);
  }

  /**
   * Select chunks within a rectangle.
   */
  public void selectChunks(int cx0, int cx1, int cz0, int cz1) {
    chunkSelection.selectChunks(world, cx0, cz0, cx1, cz1);
  }

  /**
   * Deselect chunks within a rectangle.
   */
  public void deselectChunks(int cx0, int cx1, int cz0, int cz1) {
    chunkSelection.deselectChunks(cx0, cz0, cx1, cz1);
  }

  /**
   * Delete the currently selected chunks from the current world.
   */
  public void deleteSelectedChunks(ProgressTracker progress) {
    Collection<ChunkPosition> selected = chunkSelection.getSelection();
    if (!selected.isEmpty() && !progress.isBusy()) {
      DeleteChunksJob job = new DeleteChunksJob(world, selected, progress);
      job.start();
    }
  }

  /**
   * Export the selected chunks to a zip file.
   */
  public synchronized void exportZip(File targetFile, ProgressTracker progress) {
    new ZipExportJob(world, chunkSelection.getSelection(), targetFile, progress).start();
  }

  /**
   * Select the region containing the given chunk.
   */
  public void selectRegion(int cx, int cz) {
    chunkSelection.selectRegion(world, cx, cz);
  }

  /**
   * Modify the block scale of the map view.
   */
  public synchronized void setScale(int blockScale) {
    ChunkView mapView = map.get();
    blockScale = ChunkView.clampScale(blockScale);
    if (blockScale != mapView.scale) {
      map.set(new ChunkView(mapView.x, mapView.z,
          mapView.width, mapView.height, blockScale,
          mapView.renderer, mapView.layer));
    }
  }

  /**
   * @return The current highlight color
   */
  public Color highlightColor() {
    return highlightColor;
  }

  /**
   * @return The currently highlighted block type
   */
  public Block highlightBlock() {
    return highlightBlock;
  }

  public BooleanProperty highlightEnabledProperty() {
    return highlightEnabled;
  }

  public boolean highlightEnabled() {
    return highlightEnabled.get();
  }

  /**
   * Set a new block type to highlight.
   */
  public void highlightBlock(Block hlBlock) {
    this.highlightBlock = hlBlock;
    if (highlightEnabled.get()) {
      // TODO: make separate highlight update event.
      notifyViewUpdated();
    }
  }

  /**
   * Set a new highlight color.
   */
  public void highlightColor(Color newColor) {
    highlightColor = newColor;
    if (highlightEnabled.get()) {
      // TODO: make separate highlight update event.
      notifyViewUpdated();
    }
  }

  /**
   * Notify view listeners that the view has changed.
   */
  private void notifyViewUpdated() {
    viewListeners.forEach(ChunkViewListener::viewUpdated);
  }

  public int getDimension() {
    return currentDimension;
  }

  public BooleanProperty trackPlayerProperty() {
    return trackPlayer;
  }

  public BooleanProperty trackCameraProperty() {
    return trackCamera;
  }

  public void drawCameraVisualization() {
    viewListeners.forEach(ChunkViewListener::cameraPositionUpdated);
  }
}
