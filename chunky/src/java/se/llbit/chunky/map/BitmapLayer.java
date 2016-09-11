/* Copyright (c) 2014-2016 Jesper Öqvist <jesper@llbit.se>
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

abstract public class BitmapLayer extends AbstractLayer {

  /**
   * Render this layer to a tile.
   */
  @Override final public void render(MapTile tile) {
    if (tile.scale == 1) {
      tile.setPixel(0, 0, getAvgColor());
    } else {
      int[] pixels = new int[tile.scale * tile.scale];
      if (tile.scale == 16) {
        for (int z = 0; z < 16; ++z) {
          for (int x = 0; x < 16; ++x) {
            pixels[z * 16 + x] = colorAt(x, z);
          }
        }
      } else {
        float scale = tile.scale / 16.f;
        float diffx = 0;
        float diffz = 0;
        int index = 0;
        for (int z = 0; z < 16; ++z) {
          while (diffz < scale) {
            for (int x = 0; x < 16; ++x) {
              int pixel = colorAt(x, z);
              while (diffx < scale) {
                pixels[index] = pixel;
                index += 1;
                diffx += 1;
              }
              diffx -= scale;
            }
            diffz += 1;
          }
          diffz -= scale;
        }
      }
      tile.setPixels(pixels);
    }
  }

  abstract public int colorAt(int x, int z);

}
