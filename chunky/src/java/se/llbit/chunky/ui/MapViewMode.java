/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.world.Chunk;

/**
 * View mode for the 2D map.
 */
public enum MapViewMode {
  AUTO("Auto") {
    @Override public Chunk.Renderer getRenderer() {
      return Chunk.AUTO_RENDERER;
    }
  },
  LAYER("Layer") {
    @Override public Chunk.Renderer getRenderer() {
      return Chunk.LAYER_RENDERER;
    }
  },
  SURFACE("Surface") {
    @Override public Chunk.Renderer getRenderer() {
      return Chunk.SURFACE_RENDERER;
    }
  },
  CAVES("Caves") {
    @Override public Chunk.Renderer getRenderer() {
      return Chunk.CAVE_RENDERER;
    }
  },
  BIOMES("Biomes") {
    @Override public Chunk.Renderer getRenderer() {
      return Chunk.BIOME_RENDERER;
    }
  };

  private final String name;

  MapViewMode(String name) {
    this.name = name;
  }

  @Override public String toString() {
    return name;
  }

  public abstract Chunk.Renderer getRenderer();
}
