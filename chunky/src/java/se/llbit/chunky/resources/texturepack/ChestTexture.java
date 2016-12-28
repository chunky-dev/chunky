/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChestTexture extends TextureLoader {
  private final String file;
  private final Texture lock;
  private final Texture top;
  private final Texture bottom;
  private final Texture left;
  private final Texture right;
  private final Texture front;
  private final Texture back;

  public ChestTexture(String file, Texture lock, Texture top, Texture bottom, Texture left,
      Texture right, Texture front, Texture back) {
    this.file = file;
    this.lock = lock;
    this.top = top;
    this.bottom = bottom;
    this.left = left;
    this.right = right;
    this.front = front;
    this.back = back;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage spritemap = ImageLoader.read(imageStream);
    if (spritemap.width != spritemap.height || spritemap.width % 16 != 0) {
      throw new TextureFormatError(
          "Chest texture files must have equal width and height, divisible by 16!");
    }

    int imgW = spritemap.width;
    int scale = imgW / (16 * 4);

    lock.setTexture(loadChestTexture(spritemap, scale, 0, 0));
    top.setTexture(loadChestTexture(spritemap, scale, 1, 0));
    bottom.setTexture(loadChestTexture(spritemap, scale, 2, 1));
    left.setTexture(loadChestTexture(spritemap, scale, 0, 2));
    front.setTexture(loadChestTexture(spritemap, scale, 1, 2));
    right.setTexture(loadChestTexture(spritemap, scale, 2, 2));
    back.setTexture(loadChestTexture(spritemap, scale, 3, 2));
    return true;
  }

  private static BitmapImage loadChestTexture(BitmapImage spritemap, int scale, int u, int v) {
    BitmapImage img = new BitmapImage(scale * 16, scale * 16);
    int x0 = 14 * u * scale;
    int x1 = 14 * (u + 1) * scale;
    if (v == 0) {
      int y0 = 0;
      int y1 = 14 * scale;
      for (int y = y0; y < y1; ++y) {
        int sy = y - y0 + scale;
        for (int x = x0; x < x1; ++x) {
          int sx = x - x0 + scale;
          img.setPixel(sx, sy, spritemap.getPixel(x, y));
        }
      }
    } else if (v == 1) {
      int y0 = (14 + 5) * scale;
      int y1 = (14 * 2 + 5) * scale;
      for (int y = y0; y < y1; ++y) {
        int sy = y - y0 + scale; // TODO: why + scale?
        for (int x = x0; x < x1; ++x) {
          int sx = x - x0 + scale;
          img.setPixel(sx, sy, spritemap.getPixel(x, y));
        }
      }
    } else /*if (v == 2)*/ {
      int y0 = 14 * scale;
      int y1 = (14 + 5) * scale;
      for (int y = y0; y < y1; ++y) {
        int sy = y - y0 + scale;
        for (int x = x0; x < x1; ++x) {
          int sx = x - x0 + scale;
          img.setPixel(sx, sy, spritemap.getPixel(x, y));
        }
      }
      y0 = (14 * 2 + 6) * scale;
      y1 = (14 * 3 + 1) * scale;
      for (int y = y0; y < y1; ++y) {
        int sy = y - y0 + 6 * scale;
        for (int x = x0; x < x1; ++x) {
          int sx = x - x0 + scale;
          img.setPixel(sx, sy, spritemap.getPixel(x, y));
        }
      }

    }
    return img;
  }

  @Override public boolean load(ZipFile texturePack) {
    return load(file, texturePack);
  }
}

