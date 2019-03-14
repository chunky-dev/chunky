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
package se.llbit.chunky.map;

import javafx.scene.paint.Color;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.Region;
import se.llbit.math.ColorUtil;

/**
 * A tile in the 2D world map or minimap. The tile contains either a chunk or a region.
 * The scale of the tile can not be changed.
 */
public class MapTile {
  int[] pixels;

  public ChunkPosition pos;

  /**
   * Decides if each tile is a chunk or a region (tile=chunk if true).
   */
  private boolean chunkMode;

  /**
   * Size of a single chunk in pixels.
   */
  public int scale;

  /**
   * Size of the pixel buffer.
   */
  public int size;

  public boolean isCached = false;

  public MapTile(ChunkPosition position, ChunkView view) {
    rebuild(position, view);
  }

  public void drawCached(MapBuffer buffer, WorldMapLoader mapLoader, ChunkView view) {
    if (isCached) {
      drawCached(buffer, view);
    } else {
      draw(buffer, mapLoader, view);
    }
  }

  public void draw(MapBuffer buffer, WorldMapLoader mapLoader, ChunkView view) {
    if (scale >= 16) {
      Chunk chunk = mapLoader.getWorld().getChunk(pos);
      view.renderer.render(chunk, this);
      if (mapLoader.getChunkSelection().isSelected(pos)) {
        for (int i = 0; i < size * size; ++i) {
          pixels[i] = selectionTint(pixels[i]);
        }
      }
    } else {
      Region region = mapLoader.getWorld().getRegion(pos);
      int pixelOffset = 0;
      for (int z = 0; z < 32; ++z) {
        for (int x = 0; x < 32; ++x) {
          Chunk chunk = region.getChunk(x, z);
          pixels[pixelOffset] = view.renderer.getChunkColor(chunk);
          if (mapLoader.getChunkSelection().isSelected(chunk.getPosition())) {
            pixels[pixelOffset] = selectionTint(pixels[pixelOffset]);
          }
          pixelOffset += 1;
        }
      }
    }
    drawCached(buffer, view);
    isCached = true;
  }

  /** @return the argb color value tinted with the selection color. */
  private int selectionTint(int argb) {
    int red = (argb >> 16) & 0xFF;
    int green = (argb >> 8) & 0xFF;
    int blue = argb & 0xFF;
    return (argb & 0xFF000000) | ((red / 2 + 0x7F) << 16) | (green / 2) << 8 | (blue / 2);
  }

  private void drawCached(MapBuffer buffer, ChunkView view) {
    if (view.isVisible(pos)) {
      int x0;
      int z0;
      if (chunkMode) {
        x0 = size * (pos.x - view.px0);
        z0 = size * (pos.z - view.pz0);
      } else {
        x0 = size * (pos.x - view.prx0);
        z0 = size * (pos.z - view.prz0);
      }
      int srcPos = 0;
      for (int z = 0; z < size; ++z) {
        buffer.copyPixels(pixels, srcPos, x0, z0 + z, size);
        srcPos += size;
      }
    }
  }

  public void setPixel(int x, int z, int argb) {
    pixels[z * size + x] = argb;

  }

  public void setPixels(int[] newPixels) {
    System.arraycopy(newPixels, 0, pixels, 0, size * size);
  }

  public void fill(int argb) {
    int[] pixels = new int[size * size];
    for (int i = 0; i < size * size; ++i) {
      pixels[i] = argb;
    }
    setPixels(pixels);
  }

  public void rebuild(ChunkPosition newPos, ChunkView view) {
    isCached = false;
    pos = newPos;
    scale = view.chunkScale;
    chunkMode = scale >= 16;
    int newSize = chunkMode ? scale : 32;
    // Resize buffer if needed.
    if (newSize != size) {
      size = newSize;
      pixels = new int[size * size];
    }
  }

  /**
   * Draw a bitmap image to this map tile.
   */
  public void drawImage(BitmapImage image) {
    // Safety check to see that the image has the correct size.
    // This check fails when trying to draw static icons not rendered to the tile size.
    // TODO: ensure that the image is always scaled to the tile size.
    if (image.width == size || image.height == size) {
      System.arraycopy(image.data, 0, pixels, 0, size * size);
    }
  }
}
