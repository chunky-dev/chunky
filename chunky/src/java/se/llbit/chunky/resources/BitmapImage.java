/*
 * Copyright (c) 2016-2017 Jesper Öqvist <jesper@llbit.se>
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

import org.apache.commons.math3.util.FastMath;

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

  /**
   * Create an empty bitmap.
   */
  public BitmapImage(int width, int height) {
    this.width = width;
    this.height = height;
    data = new int[width * height];
  }

  /**
   * Create a copy of another image.
   */
  public BitmapImage(BitmapImage image) {
    this.width = image.width;
    this.height = image.height;
    data = new int[width * height];
    System.arraycopy(image.data, 0, data, 0, width * height);
  }

  /** @return the ARGB value of the pixel (x, y). */
  public int getPixel(int x, int y) {
    return data[y * width + x];
  }

  /** Sets the ARGB value of the pixel (x, y). */
  public void setPixel(int x, int y, int argb) {
    data[y * width + x] = argb;
  }

  /**
   * Copies the source bitmap into this bitmap at the given (x0, y0) position.
   */
  public void blit(BitmapImage source, int x0, int y0) {
    for (int y = 0; y < source.height; ++y) {
      System.arraycopy(source.data, y * source.width, data, (y0 + y) * width + x0, source.width);
    }
  }

  /**
   * Copies a region of the source bitmap into this bitmap at the given (x0, y0) position.
   * @param x0 destination x position
   * @param y0 destination y position
   * @param sx0 source x start position
   * @param sy0 source y start position
   * @param sx1 source x end position
   * @param sy1 source y end position
   */
  public void blit(BitmapImage source, int x0, int y0, int sx0, int sy0, int sx1, int sy1) {
    for (int y = 0; y < sy1 - sy0; ++y) {
      System.arraycopy(source.data, (sy0 + y) * source.width + sx0,
          data, (y0 + y) * width + x0,
          sx1 - sx0);
    }
  }

  /**
   * @return a copy of this bitmap that is vertically flipped.
   */
  public BitmapImage vFlipped() {
    BitmapImage rotated = new BitmapImage(width, height);
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        rotated.setPixel(x, height - y - 1, getPixel(x, y));
      }
    }
    return rotated;
  }

  /**
   * @return a copy of this bitmap that is horizontally flipped.
   */
  public BitmapImage hFlipped() {
    BitmapImage rotated = new BitmapImage(width, height);
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        rotated.setPixel(width - x - 1, y, getPixel(x, y));
      }
    }
    return rotated;
  }

  /**
   * @return a copy of this bitmap that is flipped in the diagonal.
   */
  public BitmapImage diagonalFlipped() {
    BitmapImage rotated = new BitmapImage(height, width);
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        rotated.setPixel(y, x, getPixel(x, y));
      }
    }
    return rotated;
  }

  /**
   * @return a copy of this bitmap rotated 90 degrees clockwise.
   */
  public BitmapImage rotated() {
    BitmapImage rotated = new BitmapImage(height, width);
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        rotated.setPixel(height - y - 1, x, getPixel(x, y));
      }
    }
    return rotated;
  }

  /**
   * @return a copy of this bitmap rotated 180 degrees.
   */
  public BitmapImage rotated180() {
    BitmapImage rotated = new BitmapImage(width, height);
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        rotated.setPixel(width - x - 1, height - y - 1, getPixel(x, y));
      }
    }
    return rotated;
  }

  /**
   * @return a copy of this bitmap rotated 270 degrees clockwise.
   */
  public BitmapImage rotated270() {
    BitmapImage rotated = new BitmapImage(height, width);
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        rotated.setPixel(y, width - x - 1, getPixel(x, y));
      }
    }
    return rotated;
  }

  /**
   * Puts image A above image B and returns the result.
   * @param a Image A
   * @param b Image B
   * @return a new image with a on top of b
   */
  public static BitmapImage concatY(BitmapImage a, BitmapImage b) {
    BitmapImage img = new BitmapImage(FastMath.max(a.width, b.width), a.height + b.height);
    img.blit(a, 0, 0);
    img.blit(b, 0, a.height, 0, 0, b.width, b.height);
    return img;
  }
}
