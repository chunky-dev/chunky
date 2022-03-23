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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.*;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.log.Log;
import se.llbit.util.Mutable;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.net.URL;

/**
 * Utility class for image loading.
 *
 * @author Jesper Öqvist (jesper@llbit.se)
 */
public final class ImageLoader {
  /** The missing image is a 16x16 image with black background and red border and cross. */
  public final static BitmapImage missingImage;

  /**
   * ImageIO doesn't support PNGs with RGB and a transparent color before JDK 11. Since Chunky only
   * supports Java 8 and 11+, this is a reasonable check.
   * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6788458
   */
  private static final boolean IMAGEIO_PNG_TRANSPARENT_COLOR_SUPPORTED = !System.getProperty("java.version").startsWith("1.");

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
    // TODO remove this when java 8 support is dropped
    if (!IMAGEIO_PNG_TRANSPARENT_COLOR_SUPPORTED) {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[4096];
      while ((nRead = in.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      byte[] imgData = buffer.toByteArray();
      try {
        Image img = Toolkit.getDefaultToolkit().createImage(imgData);
        return fromAwtImage(img);
      } catch (Exception e) {
        Log.info("Failed to load image with AWT. Trying with ImageIO.");
        return fromBufferedImage(ImageIO.read(new ByteArrayInputStream(imgData)));
      }
    }
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

  /**
   * Converts an AWT Image to BitmapImage.
   */
  private static BitmapImage fromAwtImage(Image newImage) {
    {
      // AWT Image doesn't load until it is used so we draw it onto a 1x1 BufferedImage and wait
      // until it is drawn
      BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
      Graphics g = tmp.getGraphics();
      Mutable<Boolean> stop = new Mutable<>(false);
      ImageObserver observer = (img, infoflags, x, y, width, height) -> {
        boolean fail = (infoflags &
            (ImageObserver.ERROR | ImageObserver.ABORT)) != 0;
        stop.set(fail);
        return !fail;
      };
      while (!stop.get() && !g.drawImage(newImage, 0, 0, observer)) {}
      g.dispose();
      if (stop.get()) {
        throw new IllegalArgumentException("Invalid image.");
      }
    }

    int width = newImage.getWidth(null);
    int height = newImage.getHeight(null);
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.createGraphics();
    g.drawImage(newImage, 0, 0, null);
    g.dispose();

    // Copy the BufferedImage data into a new bitmap image.
    DataBufferInt dataBuffer = (DataBufferInt) image.getRaster().getDataBuffer();
    int[] data = dataBuffer.getData();
    BitmapImage bitmap = new BitmapImage(width, height);
    System.arraycopy(data, 0, bitmap.data, 0, width * height);
    return bitmap;
  }
}
