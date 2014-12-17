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
package se.llbit.chunky.world;

import se.llbit.chunky.main.Chunky;
import se.llbit.log.Log;

/**
 * Parses regions
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class RegionParser extends Thread {

	private final Chunky chunky;
	private final RegionQueue queue;

	/**
	 * Create new region parser
	 */
	public RegionParser(Chunky chunky, RegionQueue queue) {
	    super("Region Parser");
	    this.chunky = chunky;
	    this.queue = queue;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			ChunkPosition position = queue.poll();
			if (position == null) {
				Log.warn("Region parser shutting down abnormally.");
				return;
			}
			ChunkView map = chunky.getMapView();
			ChunkView minimap = chunky.getMinimapView();
			if (map.isRegionVisible(position) ||
					minimap.isRegionVisible(position)) {
				Region region = chunky.getWorld().getRegion(position);
				region.parse();
				for (Chunk chunk: region) {
					if (map.isVisible(chunk)) {
						chunk.loadChunk(chunky);
					}
				}
			}
		}
	}
}
