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

import javafx.application.Platform;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.renderer.ChunkViewListener;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.World;

/**
 * Monitors filesystem for changes to region files.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RegionChangeWatcher extends Thread implements ChunkViewListener {
  private final WorldMapLoader mapLoader;
  private final MapView mapView;
  private volatile ChunkView view = ChunkView.EMPTY;

  public RegionChangeWatcher(WorldMapLoader loader, MapView mapView) {
    super("Region Refresher");
    this.mapLoader = loader;
    this.mapView = mapView;
    mapView.addViewListener(this);
  }

  @Override public void run() {
    try {
      while (!isInterrupted()) {
        sleep(3000);
        World world = mapLoader.getWorld();
        if (world.reloadPlayerData()) {
          if (PersistentSettings.getFollowPlayer()) {
            Platform.runLater(() -> world.playerPos().ifPresent(mapView::panTo));
          }
        }
        ChunkView theView = view;
        for (int rx = theView.prx0; rx <= theView.prx1; ++rx) {
          for (int rz = theView.prz0; rz <= theView.prz1; ++rz) {
            Region region = world.getRegion(ChunkPosition.get(rx, rz));
            if (region.isEmpty()) {
              ChunkPosition pos = ChunkPosition.get(rx, rz);
              if (world.regionExists(pos)) {
                region = world.createRegion(pos);
              }
              world.setRegion(pos, region);
              region.parse(theView.yMin, theView.yMax);
              world.regionDiscovered(pos);
              mapLoader.regionUpdated(pos);
            } else if (region.hasChanged()) {
              region.parse(theView.yMin, theView.yMax);
              ChunkPosition pos = region.getPosition();
              mapLoader.regionUpdated(pos);
            }
          }
        }
      }
    } catch (InterruptedException e) {
      // Interrupted.
    }
  }

  @Override public synchronized void viewUpdated(ChunkView mapView) {
    this.view = mapView;
  }
}
