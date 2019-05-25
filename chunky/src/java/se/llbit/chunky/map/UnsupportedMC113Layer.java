/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.world.Icon;

/**
 * Represents a chunk with corrupt chunk data.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class UnsupportedMC113Layer extends AbstractLayer {

  /**
   * Singleton instance.
   */
  public static final UnsupportedMC113Layer INSTANCE = new UnsupportedMC113Layer();

  private UnsupportedMC113Layer() {
  }

  @Override public synchronized void render(MapTile tile) {
    if (tile.scale == 1) {
      tile.setPixel(0, 0, averageColor);
    } else {
      tile.drawImage(Icon.MC_1_13.getBitmap());
    }
  }

  private final int averageColor = Icon.MC_1_13.getAvgColor();

  @Override public int getAvgColor() {
    return averageColor;
  }

}
