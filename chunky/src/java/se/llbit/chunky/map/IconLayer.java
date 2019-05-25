/* Copyright (c) 2014, 2019 Jesper Öqvist <jesper@llbit.se>
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
 * Draws an icon in place of a chunk.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class IconLayer extends AbstractLayer {

  public static final IconLayer CORRUPT = new IconLayer(Icon.corruptLayer);
  public static final IconLayer UNKNOWN = new IconLayer(Icon.unknown);
  public static final IconLayer MC_1_13 = new IconLayer(Icon.MC_1_13);

  private Icon icon;
  private final int averageColor;

  private IconLayer(Icon icon) {
    this.icon = icon;
    averageColor = icon.getAvgColor();
  }

  @Override public synchronized void render(MapTile tile) {
    if (tile.scale == 1) {
      tile.setPixel(0, 0, averageColor);
    } else {
      tile.drawImage(icon.getBitmap());
    }
  }

  @Override public int getAvgColor() {
    return averageColor;
  }

}
