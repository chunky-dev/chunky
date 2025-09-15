/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.*;
import se.llbit.log.Log;
import se.llbit.util.Mutable;

/**
 * Asynchronous region/chunk parser.
 *
 * <p>This is a worker thread for the dynamic world loader.
 * It waits for region parse requests and loads visible chunks inside
 * the requested region.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class RegionParser extends Thread {

  private final WorldMapLoader mapLoader;
  private final RegionQueue queue;
  private MapView mapView;

  /**
   * Create new region parser
   */
  public RegionParser(WorldMapLoader loader, RegionQueue queue, MapView mapView) {
    super("Region Parser");
    this.mapLoader = loader;
    this.queue = queue;
    this.mapView = mapView;
  }

  @Override public void run() {
    while (!isInterrupted()) {
      try {
        RegionPosition position = queue.poll();
        if (position == null) {
          Log.warn("Region parser shutting down abnormally.");
          return;
        }
        ChunkView map = mapView.getMapView();
        if (map.isRegionVisible(position)) {
          Dimension dimension = mapLoader.getWorld().currentDimension();
          Region region = dimension.getRegionWithinRange(position, mapView.getYMin(), mapView.getYMax());
          region.parse(mapView.getYMin(), mapView.getYMax());
          Mutable<ChunkData> chunkData = new Mutable<>(null);
          for (Chunk chunk : region) {
            if (map.shouldPreload(chunk)) {
              if (chunk.loadChunk(chunkData, mapView.getYMin(), mapView.getYMax())) {
                chunkData.get().clear();
              }
            }
          }
        }
      } catch (Throwable t) {
        Log.warn("Region Parser Error", t);
      }
    }
  }
}
