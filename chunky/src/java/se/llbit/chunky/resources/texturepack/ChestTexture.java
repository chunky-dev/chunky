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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.resources.ImageLoader;

/** @author Jesper Öqvist <jesper@llbit.se> */
public class ChestTexture extends TextureLoader {
  public enum Layout {
    OLD_LAYOUT,
    NEW_LAYOUT, // new texture layout introduced in MC 1.15
  }

  private final String file;
  private Layout layout;
  private final Texture lock;
  private final Texture top;
  private final Texture bottom;
  private final Texture left;
  private final Texture right;
  private final Texture front;
  private final Texture back;

  public ChestTexture(
      String file,
      Texture lock,
      Texture top,
      Texture bottom,
      Texture left,
      Texture right,
      Texture front,
      Texture back) {
    this(file, Layout.OLD_LAYOUT, lock, top, bottom, left, right, front, back);
  }

  public ChestTexture(
      String file,
      Layout layout,
      Texture lock,
      Texture top,
      Texture bottom,
      Texture left,
      Texture right,
      Texture front,
      Texture back) {
    this.file = file;
    this.layout = layout;
    this.lock = lock;
    this.top = top;
    this.bottom = bottom;
    this.left = left;
    this.right = right;
    this.front = front;
    this.back = back;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage spritemap = ImageLoader.read(imageStream);
    if (spritemap.width != spritemap.height || spritemap.width % 16 != 0) {
      throw new TextureFormatError(
          "Chest texture files must have equal width and height, divisible by 16!");
    }

    int imgW = spritemap.width;
    int scale = imgW / (16 * 4);

    if (layout == Layout.NEW_LAYOUT) {
      lock.setTexture(getSprite(spritemap, scale, 0, 0, 8, 8, false, false)); // TODO flip
      top.setTexture(getSprite(spritemap, scale, 28, 0, 14, 14, true, true));
      bottom.setTexture(getSprite(spritemap, scale, 14, 19, 14, 14, true, true));
      right.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 0, 15, 14, 4, false, true),
              getSprite(spritemap, scale, 0, 33, 14, 10, false, true)));
      left.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 28, 15, 14, 4, true, true),
              getSprite(spritemap, scale, 28, 33, 14, 10, true, true)));
      front.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 42, 15, 14, 4, false, true),
              getSprite(spritemap, scale, 42, 33, 14, 10, false, true)));
      back.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 14, 15, 14, 4, true, true),
              getSprite(spritemap, scale, 14, 33, 14, 10, true, true)));
    } else {
      lock.setTexture(getSprite(spritemap, scale, 0, 0, 8, 8, false, false));
      top.setTexture(getSprite(spritemap, scale, 14, 0, 14, 14, true, false));
      bottom.setTexture(getSprite(spritemap, scale, 28, 19, 14, 14, true, false));
      right.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 0, 14, 14, 4, true, false),
              getSprite(spritemap, scale, 0, 33, 14, 10, true, false)));
      left.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 28, 14, 14, 4, false, false),
              getSprite(spritemap, scale, 28, 33, 14, 10, false, false)));
      front.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 14, 14, 14, 4, true, false),
              getSprite(spritemap, scale, 14, 33, 14, 10, true, false)));
      back.setTexture(
          BitmapImage.concatY(
              getSprite(spritemap, scale, 42, 14, 14, 4, false, false),
              getSprite(spritemap, scale, 42, 33, 14, 10, false, false)));
    }
    return true;
  }

  private static BitmapImage getSprite(
      BitmapImage spritemap,
      int scale,
      int x0,
      int y0,
      int width,
      int height,
      boolean hFlip,
      boolean vFlip) {
    BitmapImage img = new BitmapImage(scale * width, scale * height);

    img.blit(spritemap, 0, 0, x0 * scale, y0 * scale, (x0 + width) * scale, (y0 + height) * scale);

    if (hFlip) {
      img = img.hFlipped();
    }
    if (vFlip) {
      img = img.vFlipped();
    }

    return img;
  }

  @Override
  public boolean load(ZipFile texturePack, String topLevelDir) {
    return load(topLevelDir + file, texturePack);
  }
}
