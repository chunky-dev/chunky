/* Copyright (c) 2020-2021 Chunky contributors
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
 * A image type using 1 bit per pixel
 */
public class BinaryBitmapImage {
  public final int width;
  public final int height;
  private final byte[] data;

  public BinaryBitmapImage(int width, int height) {
    this.width = width;
    this.height = height;
    int pixelCount = width*height;
    int byteCount = (pixelCount + 7) / 8;
    data = new byte[byteCount];
  }

  public void setPixel(int x, int y, boolean value) {
    int index = y * width + x;
    int byteIndex = index / 8;
    int bitIndex = index % 8;
    int bit = 1 << bitIndex;
    if(value)
      // set the bit
      data[byteIndex] |= bit;
    else
      // clear the bit
      data[byteIndex] &= ~bit;
  }

  public boolean getPixel(int x, int y) {
    int index = y * width + x;
    int byteIndex = index / 8;
    int bitIndex = index % 8;
    int bit = 1 << bitIndex;
    return (data[byteIndex] & bit) != 0;
  }
}
