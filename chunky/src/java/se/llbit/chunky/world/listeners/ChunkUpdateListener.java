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
package se.llbit.chunky.world.listeners;

import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.RegionPosition;
import se.llbit.chunky.world.region.MCRegion;

import java.util.Collection;

public interface ChunkUpdateListener {
  default void regionUpdated(RegionPosition region) {}

  default void chunkUpdated(ChunkPosition chunkPosition) {}

  /**
   * All chunks within a region have been updated
   */
  default void regionChunksUpdated(RegionPosition region) {
    int minChunkX = region.x << 5;
    int minChunkZ = region.z << 5;
    for (int chunkX = minChunkX; chunkX < minChunkX + MCRegion.CHUNKS_X; chunkX++) {
      for (int chunkZ = minChunkZ; chunkZ < minChunkZ + MCRegion.CHUNKS_Z; chunkZ++) {
        this.chunkUpdated(new ChunkPosition(chunkX, chunkZ));
      }
    }
  }

  /**
   * Some chunks within a region have been updated
   */
  default void regionChunksUpdated(RegionPosition region, Collection<ChunkPosition> chunks) {
    for (ChunkPosition chunk : chunks) {
      this.chunkUpdated(chunk);
    }
  }
}
