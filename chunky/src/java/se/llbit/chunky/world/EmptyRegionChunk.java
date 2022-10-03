/* Copyright (c) 2010-2014 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.map.IconLayer;
import se.llbit.chunky.map.MapTile;
import se.llbit.chunky.world.biome.BiomePalette;
import se.llbit.util.Mutable;

/**
 * Empty or non-existent chunk.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EmptyRegionChunk extends Chunk {

  /**
   * Singleton instance
   */
  public static final EmptyRegionChunk INSTANCE = new EmptyRegionChunk();

  private static final int COLOR = 0xFFEEEEEE;

  @Override public boolean isEmpty() {
    return true;
  }

  private EmptyRegionChunk() {
    super(new ChunkPosition(0, 0), EmptyWorld.INSTANCE);
    surface = IconLayer.CORRUPT;
  }

  @Override public synchronized void getChunkData(Mutable<ChunkData> reuseChunkData, BlockPalette palette, BiomePalette biomePalette, int yMin, int yMax) { }

  @Override public void renderSurface(MapTile tile) {
    renderEmpty(tile);
  }

  @Override public void renderBiomes(MapTile tile) {
    renderEmpty(tile);
  }

  @Override public int biomeColor() {
    return 0xFF777777;
  }

  private void renderEmpty(MapTile tile) {
    int[] pixels = new int[tile.tileWidth * tile.tileWidth];
    for (int z = 0; z < tile.tileWidth; ++z) {
      for (int x = 0; x < tile.tileWidth; ++x) {
        if (x == z || x - tile.tileWidth / 2 == z || x + tile.tileWidth / 2 == z) {
          pixels[z * tile.tileWidth + x] = 0xFF000000;
        } else {
          pixels[z * tile.tileWidth + x] = COLOR;
        }
      }
    }
    tile.setPixels(pixels);
  }

  @Override public synchronized void reset() {
    // do nothing
  }

  @Override public synchronized boolean loadChunk(Mutable<ChunkData> chunkData, int yMin, int yMax) {
    return false;
  }

  @Override public String toString() {
    return "Chunk: [empty region]";
  }
}
