/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import se.llbit.chunky.map.MapBuffer;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.listeners.ChunkUpdateListener;

/**
 * Extracts common behaviour for the 2D maps (chunk map and minimap).
 */
public class Map2D implements ChunkUpdateListener, ChunkViewListener {
  protected final WorldMapLoader mapLoader;
  protected final ChunkyFxController controller;
  protected final MapBuffer mapBuffer;

  /**
   * The map view can be modified by external threads.
   * Objects of type ChunkView are immutable, but each time
   * we need to read the view we should only read the view
   * reference once.
   */
  protected volatile ChunkView view = ChunkView.EMPTY;

  private Canvas canvas;
  volatile boolean repaintQueued = false;

  public Map2D(final WorldMapLoader loader, final ChunkyFxController controller) {
    this.mapLoader = loader;
    this.controller = controller;
    mapBuffer = new MapBuffer();
  }

  public void setCanvas(Canvas canvas) {
    this.canvas = canvas;
  }

  @Override public void chunkUpdated(ChunkPosition chunk) {
    if (view.chunkScale >= 16) {
      mapBuffer.drawTile(mapLoader, chunk);
    } else {
      regionUpdated(chunk.getRegionPosition());
    }
    repaintDeferred();
  }

  protected final void repaintDirect() {
    if (!repaintQueued) {
      repaint(canvas.getGraphicsContext2D());
    }
  }

  protected final void repaintDeferred() {
    if (!repaintQueued) {
      repaintQueued = true;
      Platform.runLater(() -> {
        repaint(canvas.getGraphicsContext2D());
        repaintQueued = false;
      });
    }
  }

  protected void repaint(GraphicsContext gc) {
    mapBuffer.drawBuffered(gc);
  }

  @Override public void regionUpdated(ChunkPosition region) {
    if (view.scale < 16) {
      mapBuffer.drawTile(mapLoader, region);
      mapLoader.regionUpdated(region);
      repaintDeferred();
    }
  }

  /**
   * Called when the map view has changed.
   */
  public synchronized void viewUpdated(ChunkView newView) {
    setView(newView);
    mapBuffer.updateView(view, mapLoader);
  }

  protected void setView(ChunkView newView) {
    view = newView;
  }

  @Override public void viewUpdated() {
    viewUpdated(mapLoader.getMapView());
    mapBuffer.redrawView(mapLoader);
    repaintDirect();
  }

  @Override public void viewMoved() {
    mapBuffer.redrawView(mapLoader);
    repaintDirect();
  }

  @Override public void cameraPositionUpdated() {
  }

  public ChunkView getView() {
    return view;
  }

  public void redrawMap() {
    mapBuffer.clearBuffer();
    mapBuffer.redrawView(mapLoader);
    repaintDeferred();
  }
}
