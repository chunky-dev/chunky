/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.world.ChunkView;
import se.llbit.math.Vector2;
import se.llbit.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the view for the 2D world map.
 *
 * <p>ChunkViewListeners can be registered in order to listen to changes to the view.
 */
public class MapView {
  private List<ChunkViewListener> viewListeners = new ArrayList<>();

  private volatile ObjectProperty<ChunkView> map = new SimpleObjectProperty<>(ChunkView.EMPTY);

  public void addViewListener(ChunkViewListener listener) {
    viewListeners.add(listener);
  }

  public synchronized Vector2 getPosition() {
    ChunkView mapView = map.get();
    return new Vector2(mapView.x, mapView.z);
  }

  /**
   * Called when the map has been resized.
   */
  public void setMapSize(int width, int height) {
    ChunkView mapView = map.get();
    if (width != mapView.width || height != mapView.height) {
      map.set(new ChunkView(mapView.x, mapView.z, width, height, mapView.scale));
    }
  }

  /**
   * Move the map view.
   */
  public synchronized void moveView(double dx, double dz) {
    ChunkView mapView = map.get();
    panTo(mapView.x + dx, mapView.z + dz);
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
        mapView.width, mapView.height, mapView.scale));
    notifyViewUpdated();
  }

  /**
   * Notify view listeners that the view has changed.
   */
  void notifyViewUpdated() {
    ChunkView view = map.get();
    viewListeners.forEach(listener -> listener.viewUpdated(view));
  }

  /**
   * Modify the block scale of the map view.
   */
  public synchronized void setScale(int blockScale) {
    ChunkView mapView = map.get();
    blockScale = ChunkView.clampScale(blockScale);
    if (blockScale != mapView.scale) {
      map.set(new ChunkView(mapView.x, mapView.z,
          mapView.width, mapView.height, blockScale));
      notifyViewUpdated();
    }
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
}
