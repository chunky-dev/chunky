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
package se.llbit.chunky.world.java.region;

import javafx.application.Platform;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.HeightRange;
import se.llbit.chunky.world.RegionPosition;
import se.llbit.chunky.world.java.JavaDimension;
import se.llbit.chunky.world.region.Region;
import se.llbit.chunky.world.region.RegionChangeWatcher;

/**
 * Monitors filesystem for changes to region files.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class JavaRegionChangeWatcher extends RegionChangeWatcher {
  public JavaRegionChangeWatcher(WorldMapLoader loader, MapView mapView) {
    super(loader, mapView, "Region Refresher");
  }

  @Override public void run() {
    try {
      while (!isInterrupted()) {
        sleep(3000);
        // MCRegionChangeWatcher is only created by JavaDimension, so this cast is always safe.
        JavaDimension dimension = (JavaDimension) mapLoader.getWorld().currentDimension();
        if (dimension.reloadPlayerData()) {
          if (PersistentSettings.getFollowPlayer()) {
            Platform.runLater(() -> dimension.getPlayerPos().ifPresent(mapView::panTo));
          }
        }
        ChunkView theView = view;
        for (int rx = theView.prx0; rx <= theView.prx1; ++rx) {
          for (int rz = theView.prz0; rz <= theView.prz1; ++rz) {
            RegionPosition pos = new RegionPosition(rx, rz);
            HeightRange heightRange = new HeightRange(theView.yMin, theView.yMax);
            Region region = dimension.getRegionWithinRange(pos, heightRange);
            if (region.isEmpty()) {
              if (dimension.hasRegionWithinRange(pos, heightRange)) {
                region = dimension.createRegion(pos);
              }
              dimension.setRegion(pos, region);
              region.parse(theView.yMin, theView.yMax);
              dimension.regionDiscovered(pos);
              mapLoader.regionUpdated(pos);
            } else if (region.hasChanged()) {
              region.parse(theView.yMin, theView.yMax);
              mapLoader.regionUpdated(region.getPosition());
            }
          }
        }
      }
    } catch (InterruptedException e) {
      // Interrupted.
    }
  }
}
