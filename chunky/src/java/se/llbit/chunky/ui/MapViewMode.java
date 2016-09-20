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

    @Override public int getLayers(ChunkView view) {
      if (view.scale >= 10) {
        return Chunk.SURFACE_LAYER | Chunk.BIOME_LAYER;
      } else {
        return Chunk.BIOME_LAYER;
      }
    }

    @Override
    public boolean bufferValid(ChunkView oldView, ChunkView newView, int oldLayer, int newLayer) {
      return super.bufferValid(oldView, newView, oldLayer, newLayer) && (
          oldView.scale >= 10 && newView.scale >= 10 || oldView.scale < 10 && newView.scale < 10);
    }

    @Override public int getChunkColor(Chunk chunk) {
      return chunk.biomeColor();
    }
  },

  /**
   * Renders a single layer.
   */
  LAYER("Layer") {
    @Override public void render(Chunk chunk, MapTile tile) {
      chunk.renderLayer(tile);
    }

    @Override public int getLayers(ChunkView view) {
      return Chunk.BLOCK_LAYER;
    }

    @Override
    public boolean bufferValid(ChunkView oldView, ChunkView newView, int oldLayer, int newLayer) {
      return super.bufferValid(oldView, newView, oldLayer, newLayer) && oldLayer == newLayer;
    }

    @Override public int getChunkColor(Chunk chunk) {
      return chunk.layerColor();
    }
  },

  /**
   * Renders the default surface view
   */
  SURFACE("Surface") {
    @Override public void render(Chunk chunk, MapTile tile) {
      chunk.renderSurface(tile);
    }

    @Override public int getLayers(ChunkView view) {
      return Chunk.SURFACE_LAYER;
    }

    @Override public int getChunkColor(Chunk chunk) {
      return chunk.surfaceColor();
    }
  },

  /**
   * Visualizes underground cavities.
   */
  CAVES("Caves") {
    @Override public void render(Chunk chunk, MapTile tile) {
      chunk.renderCaves(tile);
    }

    @Override public int getLayers(ChunkView view) {
      return Chunk.CAVE_LAYER;
    }

    @Override public int getChunkColor(Chunk chunk) {
      return chunk.caveColor();
    }
  },

  /**
   * Renders biome values only.
   */
  BIOMES("Biomes") {
    @Override public void render(Chunk chunk, MapTile tile) {
      chunk.renderBiomes(tile);
    }

    @Override public int getLayers(ChunkView view) {
      return Chunk.BIOME_LAYER;
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

  /**
   * Layers to be loaded for this renderer.
   */
  abstract public int getLayers(ChunkView view);

  public Set<String> getRequest(ChunkView view) {
    int layers = getLayers(view);
    Set<String> request = new HashSet<>();
    request.add(Chunk.LEVEL_SECTIONS);
    if ((layers & Chunk.BLOCK_LAYER) != 0 || (layers & Chunk.SURFACE_LAYER) != 0
        || (layers & Chunk.BIOME_LAYER) != 0) {
      request.add(Chunk.LEVEL_BIOMES);
    }
    if ((layers & Chunk.SURFACE_LAYER) != 0 || (layers & Chunk.CAVE_LAYER) != 0) {
      request.add(Chunk.LEVEL_HEIGHTMAP);
    }
    return request;
  }

  /**
   * @return {@code true} if the render buffer is still valid
   */
  public boolean bufferValid(ChunkView oldView, ChunkView newView, int oldLayer, int newLayer) {
    return oldView.chunkScale == newView.chunkScale;
  }

  public abstract int getChunkColor(Chunk chunk);
}
