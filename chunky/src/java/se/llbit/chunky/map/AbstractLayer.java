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
package se.llbit.chunky.map;

import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Heightmap;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A layer describes the visible part of a chunk.
 * <p>
 * A chunk typically stores three layers;
 * current layer, cave layer and surface layer.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
abstract public class AbstractLayer {

  /**
   * Render this layer to a map tile.
   */
  abstract public void render(MapTile tile);

  public void renderTopography(ChunkPosition position, Heightmap heightmap) {
  }

  public int getAvgColor() {
    return 0xFF000000;
  }

  /**
   * Write a PNG scanline.
   *
   * @throws IOException
   */
  public void writePngLine(int scanline, OutputStream out) throws IOException {
    byte[] white = new byte[] {-1, -1, -1};
    byte[] black = new byte[] {0, 0, 0};
    for (int x = 0; x < 16; ++x) {
      if (x == scanline) {
        out.write(black);
      } else {
        out.write(white);
      }
    }
  }
}
