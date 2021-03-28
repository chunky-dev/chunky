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
 * A image type using 4 bit per pixel
 */
public class PalettizedBitmapImage {
  public final int width;
  public final int height;
  private final byte[] data;

  public PalettizedBitmapImage(int width, int height) {
    this.width = width;
    this.height = height;
    int pixelCount = width*height;
    int byteCount = (pixelCount + 1) / 2;
    data = new byte[byteCount];
  }

  public void setPixel(int x, int y, int value) {
    value &= 0xf;
    int index = y * width + x;
    int byteIndex = index / 2;
    int shift = index % 2 * 4;
    byte mask = (byte) (0xf << shift);
    data[byteIndex] &= ~mask; // Clear the pixel
    data[byteIndex] |= value << shift; // Clear the pixel
  }

  public int getPixel(int x, int y) {
    int index = y * width + x;
    int byteIndex = index / 2;
    int shift = index % 2 * 4;
    byte mask = (byte) (0xf << shift);
    return (data[byteIndex] & mask) >> shift;
  }
}
