/* Copyright (c) 2010-2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.resources;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.log.Log;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Utility class for image loading.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public final class ImageLoader {
  /** The missing image is a 16x16 image with black background and red border and cross. */
  public final static BitmapImage missingImage;

  static {
    missingImage = new BitmapImage(16, 16);
    for (int y = 0; y < 16; ++y) {
      for (int x = 0; x < 16; ++x) {
        if (x == 0 || x == 15 || y == 0 || y == 15 || x == y || x == 16 - y) {
          missingImage.data[y * 16 + x] = 0xFFFF0000;
        } else {
          missingImage.data[y * 16 + x] = 0xFF000000;
        }
      }
    }
  }

  private ImageLoader() {
  }

  /**
   * Attempt to load an image with the given resource name.
   * If no image is found for that resource name the default
   * missing image is returned.
   *
   * @return Image for the given resource name
   */
  public static synchronized BitmapImage readNonNull(String resourceName) {
    URL url = ImageLoader.class.getResource("/" + resourceName);
    if (url == null) {
      Log.info("Could not find image: " + resourceName);
      return missingImage;
    }
    try {
      return read(url);
    } catch (IOException e) {
      Log.info("Failed to read image: " + resourceName, e);
      return missingImage;
    }
  }

  public static BitmapImage read(URL url) throws IOException {
    return fromBufferedImage(ImageIO.read(url));
  }

  public static BitmapImage read(InputStream in) throws IOException {
    return fromBufferedImage(ImageIO.read(in));
  }

  public static BitmapImage read(File file) throws IOException {
    return fromBufferedImage(ImageIO.read(file));
  }

  /**
   * Converts an AWT BufferedImage to BitmapImage.
   */
  private static BitmapImage fromBufferedImage(BufferedImage newImage) {
    int width = newImage.getWidth();
    int height = newImage.getHeight();
    BufferedImage image;
    if (newImage.getType() == BufferedImage.TYPE_INT_ARGB) {
      image = newImage;
    } else {
      // Convert to ARGB.
      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.createGraphics();
      g.drawImage(newImage, 0, 0, null);
      g.dispose();
    }
    // Copy the BufferedImage data into a new bitmap image.
    DataBufferInt dataBuffer = (DataBufferInt) image.getRaster().getDataBuffer();
    int[] data = dataBuffer.getData();
    BitmapImage bitmap = new BitmapImage(width, height);
    System.arraycopy(data, 0, bitmap.data, 0, width * height);
    return bitmap;
  }

}
