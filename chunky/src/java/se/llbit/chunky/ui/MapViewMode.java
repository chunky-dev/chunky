/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.map.MapTile;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkView;

import java.util.HashSet;
import java.util.Set;

/**
 * View mode for the 2D map.
 */
public enum MapViewMode {
  /**
   * Switches between surface, layer and biome modes.
   */
  AUTO("Auto") {
    @Override public void render(Chunk chunk, MapTile tile) {
      if (tile.scale >= 10) {
        chunk.renderSurface(tile);
      } else {
        chunk.renderBiomes(tile);
      }
    }

    @Override
    public boolean bufferValid(ChunkView oldView, ChunkView newView) {
      return super.bufferValid(oldView, newView) && (
          oldView.scale >= 10 && newView.scale >= 10 || oldView.scale < 10 && newView.scale < 10);
    }

    @Override public int getChunkColor(Chunk chunk) {
      return chunk.biomeColor();
    }
  };

  private final String name;

  MapViewMode(String name) {
    this.name = name;
  }

  @Override public String toString() {
    return name;
  }

  /**
   * Render the chunk to a map tile.
   */
  abstract public void render(Chunk chunk, MapTile tile);

  public static Set<String> getRequest() {
    Set<String> request = new HashSet<>();
    request.add(Chunk.LEVEL_SECTIONS);
    request.add(Chunk.LEVEL_BIOMES);
    request.add(Chunk.LEVEL_HEIGHTMAP);
    return request;
  }

  /**
   * @return {@code true} if the render buffer is still valid
   */
  public boolean bufferValid(ChunkView oldView, ChunkView newView) {
    return oldView.chunkScale == newView.chunkScale;
  }

  public abstract int getChunkColor(Chunk chunk);
}
