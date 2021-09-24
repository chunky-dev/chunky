/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world.region;

import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.world.ChunkView;

/**
 * Monitors filesystem for changes to region files.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class RegionChangeWatcher extends Thread implements ChunkViewListener {
  protected final WorldMapLoader mapLoader;
  protected final MapView mapView;
  protected volatile ChunkView view = ChunkView.EMPTY;

  protected RegionChangeWatcher(WorldMapLoader loader, MapView mapView, String name) {
    super(name);
    this.mapLoader = loader;
    this.mapView = mapView;
    mapView.addViewListener(this);
  }

  @Override public abstract void run();

  @Override public synchronized void viewUpdated(ChunkView mapView) {
    this.view = mapView;
  }
}
