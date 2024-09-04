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
package se.llbit.chunky.resources;

import se.llbit.math.ColorUtil;
import se.llbit.math.Vector4;

public class SolidColorTexture extends Texture {
  public static final SolidColorTexture EMPTY = new SolidColorTexture(new Vector4(1, 1, 1, 1));

  private final Vector4 color;

  public SolidColorTexture(Vector4 color) {
    this.color = color;
    this.avgColor = ColorUtil.getArgb(color);
  }

  @Override public void getColor(double u, double v, Vector4 c) {
    c.set(color);
  }

}
