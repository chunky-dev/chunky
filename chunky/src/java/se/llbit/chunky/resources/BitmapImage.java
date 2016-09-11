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
package se.llbit.chunky.resources;

/**
 * A container for bitmap image data in int ARGB format.
 *
 * <p>The width and height of the image are immutable, but the
 * raw pixel data is mutable. External synchronization is needed
 * if concurrent modification of pixels needs to be done.
 */
public class BitmapImage {
  public final int[] data;
  public final int width;
  public final int height;

  public BitmapImage(int width, int height) {
    this.width = width;
    this.height = height;
    data = new int[width * height];
  }

  /** @return the ARGB value of the pixel (x, y). */
  public int getPixel(int x, int y) {
    return data[y * width + x];
  }

  /** Sets the ARGB value of the pixel (x, y). */
  public void setPixel(int x, int y, int argb) {
    data[y * width + x] = argb;
  }
}
