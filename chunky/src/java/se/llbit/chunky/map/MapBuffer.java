/* Copyright (c) 2010-2016 Jesper Öqvist <jesper@llbit.se>
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

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.png.PngFileWriter;
import se.llbit.util.RingBuffer;
import se.llbit.util.TaskTracker;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Keeps a buffered image of rendered map tiles. We only re-render chunks when
 * they are not buffered. The buffer contains all visible chunks, plus some
 * outside of the view. Chunks outside the view are rendered so that the
 * rendering and chunk loading delay when panning is minimized.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public class MapBuffer {
  private static final WritablePixelFormat<IntBuffer> PIXEL_FORMAT =
      PixelFormat.getIntArgbInstance();

  private int[] pixels;
  private int width;
  private int height;

  private WritableImage image = null;
  private boolean cached = false;

  private ChunkView view = ChunkView.EMPTY;

  private RingBuffer<MapTile> tileCache = new RingBuffer<>(140);
  private Map<ChunkPosition, MapTile> activeTiles = new HashMap<>();

  public MapBuffer() {
    updateView(ChunkView.EMPTY, true);
  }

  /**
   * Called when this render buffer should buffer another view.
   */
  public synchronized void updateView(ChunkView newView) {
    boolean rebuild = newView.scale != view.scale;
    updateView(newView, rebuild);
  }

  private synchronized void updateView(ChunkView newView, boolean rebuild) {
    int newWidth = newView.chunkScale * (newView.px1 - newView.px0 + 1);
    int newHeight = newView.chunkScale * (newView.pz1 - newView.pz0 + 1);
    if (newWidth != width || newHeight != height || pixels == null) {
      width = newWidth;
      height = newHeight;
      pixels = new int[width * height];
    }
    updateActiveTiles(newView, rebuild);
    view = newView;
  }

  private synchronized void updateActiveTiles(ChunkView newView, boolean rebuild) {
    Collection<MapTile> discarded = new LinkedList<>();
    for (MapTile tile : activeTiles.values()) {
      if (!newView.shouldPreload(tile.pos)) {
        discarded.add(tile);
      } else if (rebuild) {
        tile.rebuild(tile.pos, newView);
      }
    }
    for (MapTile tile : discarded) {
      tileCache.append(tile);
      activeTiles.remove(tile.pos);
    }
    int x0, x1, z0, z1;
    if (newView.chunkScale >= 16) {
      x0 = newView.px0;
      x1 = newView.px1;
      z0 = newView.pz0;
      z1 = newView.pz1;
    } else {
      x0 = newView.prx0;
      x1 = newView.prx1;
      z0 = newView.prz0;
      z1 = newView.prz1;
    }
    for (int x = x0; x <= x1; ++x) {
      for (int z = z0; z <= z1; ++z) {
        ChunkPosition pos = ChunkPosition.get(x, z);
        if (!activeTiles.containsKey(pos)) {
          activeTiles.put(pos, newTile(pos, newView));
        }
      }
    }
  }

  /**
   * @return The buffered view
   */
  public ChunkView getView() {
    return view;
  }

  /**
   * Redraws the given tile.
   */
  public synchronized void drawTile(WorldMapLoader mapLoader, ChunkPosition chunk) {
    MapTile tile = activeTiles.get(chunk);
    if (tile != null) {
      tile.draw(this, mapLoader, view);
      cached = false;
    }
  }

  /**
   * Attempts to draw the tile using cached image.
   */
  public synchronized void drawTileCached(WorldMapLoader mapLoader, ChunkPosition chunk) {
    MapTile tile = activeTiles.get(chunk);
    if (tile != null) {
      tile.drawCached(this, mapLoader, view);
      cached = false;
    }
  }

  /**
   * Redraw all tiles in the current view.
   * This draws to the map buffer, it does not render to the map canvas.
   */
  public synchronized void redrawView(WorldMapLoader mapLoader) {
    int x0, x1, z0, z1;
    if (view.chunkScale >= 16) {
      x0 = view.px0;
      x1 = view.px1;
      z0 = view.pz0;
      z1 = view.pz1;
    } else {
      x0 = view.prx0;
      x1 = view.prx1;
      z0 = view.prz0;
      z1 = view.prz1;
    }
    for (int x = x0; x <= x1; ++x) {
      for (int z = z0; z <= z1; ++z) {
        drawTileCached(mapLoader, ChunkPosition.get(x, z));
      }
    }
  }

  /**
   * Create a new map tile to use in the map buffer.
   * This reuses existing map tiles when possible.
   */
  private MapTile newTile(ChunkPosition pos, ChunkView view) {
    if (tileCache.isEmpty()) {
      return new MapTile(pos, view);
    } else {
      MapTile tile = tileCache.remove();
      tile.rebuild(pos, view);
      return tile;
    }
  }

  /**
   * Copies a contiguous block of pixels into the buffer.
   */
  public void copyPixels(int[] data, int srcPos, int x, int z, int size) {
    System.arraycopy(data, srcPos, pixels, z * width + x, size);
  }

  /**
   * Draws the current buffered map to a map canvas (via a GraphicsContext).
   * We use a manual scaling implementation to avoid the blurry JavaFX upscaling.
   *
   * <p>Drawing the image would be much simpler if we could rely on JavaFX
   * for scaling the image, unfortunately if JavaFX is used to scale the image
   * it will use a blurry upscaling algorithm which looks bad for the 2D map.
   * It is not possible to disable the JavaFX scaling interpolation,
   * so we do our own scaling here instead.
   */
  public synchronized void drawBuffered(GraphicsContext gc) {
    if (!cached) {
      if (image == null || image.getWidth() != view.width || image.getHeight() != view.height) {
        image = new WritableImage(view.width, view.height);
      }

      // Here we make sure to scale only the part of the image that will be drawn.
      float scale = view.scale / (float) view.chunkScale;
      double x0 = view.chunkScale * (view.x0 - view.px0);
      double z0 = view.chunkScale * (view.z0 - view.pz0);
      float diffY = 0;
      int[] scaled = new int[view.width * view.height];
      int index = 0;
      int sourceX = (int) (0.5 + x0);
      int sourceY = (int) (0.5 + z0);
      int destY = 0;
      for (int y = 0; y < (view.height / scale); ++y) {
        while (diffY < scale && destY < view.height) {
          float diffX = 0;
          int destX = 0;
          int pixelOffset = (sourceY + y) * width + sourceX;
          for (int x = 0; x < (view.width / scale); ++x) {
            int pixel = pixels[pixelOffset++];
            while (diffX < scale && destX < view.width) {
              scaled[index++] = pixel;
              diffX += 1;
              destX += 1;
            }
            diffX -= scale;
          }
          diffY += 1;
          destY += 1;
        }
        diffY -= scale;
      }
      image.getPixelWriter()
          .setPixels(0, 0, view.width, view.height, PIXEL_FORMAT, scaled, 0, view.width);
      cached = true;
    }
    gc.clearRect(0, 0, view.width, view.height);
    gc.drawImage(image, 0, 0);
  }

  /**
   * Forces all tiles to be redrawn on the next draw operation.
   */
  public synchronized void clearBuffer() {
    updateActiveTiles(view, true);
  }

  /**
   * Write the map to a PNG image.
   */
  public synchronized void renderPng(File targetFile) throws IOException {
    int width = view.width;
    int height = view.height;
    int[] pixels = new int[width * height];
    image.getPixelReader().getPixels(0, 0, width, height, PIXEL_FORMAT, pixels, 0, width);
    try (PngFileWriter pngWriter = new PngFileWriter(targetFile)) {
      pngWriter.write(pixels, width, height, TaskTracker.Task.NONE);
    }
  }
}
