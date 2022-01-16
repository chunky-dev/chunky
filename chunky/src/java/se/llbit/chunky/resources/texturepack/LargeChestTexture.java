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
import java.nio.file.Path;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.resources.ImageLoader;

/** @author Jesper Öqvist <jesper@llbit.se> */
public class LargeChestTexture extends TextureLoader {
  private final String file;
  private final Texture left;
  private final Texture topLeft;
  private final Texture topRight;
  private final Texture frontLeft;
  private final Texture frontRight;
  private final Texture bottomLeft;
  private final Texture right;
  private final Texture bottomRight;
  private final Texture backLeft;
  private final Texture backRight;

  public LargeChestTexture(
      String file,
      Texture left,
      Texture right,
      Texture topLeft,
      Texture topRight,
      Texture frontLeft,
      Texture frontRight,
      Texture bottomLeft,
      Texture bottomRight,
      Texture backLeft,
      Texture backRight) {
    this.file = file;
    this.left = left;
    this.right = right;
    this.topLeft = topLeft;
    this.topRight = topRight;
    this.frontLeft = frontLeft;
    this.frontRight = frontRight;
    this.bottomLeft = bottomLeft;
    this.bottomRight = bottomRight;
    this.backLeft = backLeft;
    this.backRight = backRight;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage spritemap = ImageLoader.read(imageStream);
    if (spritemap.width % 16 != 0 || spritemap.height % 16 != 0) {
      throw new TextureFormatError(
          "Large chest texture file must have width and height divisible by 16!");
    }

    int imgW = spritemap.width;
    int scale = imgW / (16 * 8);

    right.setTexture(
        BitmapImage.concatY(
            getSprite(spritemap, scale, 0, 14, 14, 4, true, false),
            getSprite(spritemap, scale, 0, 33, 14, 10, true, false)));
    left.setTexture(
        BitmapImage.concatY(
            getSprite(spritemap, scale, 44, 14, 14, 4, false, false),
            getSprite(spritemap, scale, 44, 33, 14, 10, false, false)));
    topRight.setTexture(getSprite(spritemap, scale, 14, 0, 15, 14, true, false));
    topLeft.setTexture(getSprite(spritemap, scale, 29, 0, 15, 14, true, false));
    frontRight.setTexture(
        BitmapImage.concatY(
            getSprite(spritemap, scale, 14, 14, 15, 4, true, false),
            getSprite(spritemap, scale, 14, 33, 15, 10, true, false)));
    frontLeft.setTexture(
        BitmapImage.concatY(
            getSprite(spritemap, scale, 29, 14, 15, 4, true, false),
            getSprite(spritemap, scale, 29, 33, 15, 10, true, false)));
    bottomRight.setTexture(getSprite(spritemap, scale, 44, 19, 15, 14, true, false));
    bottomLeft.setTexture(getSprite(spritemap, scale, 59, 19, 15, 14, true, false));
    backLeft.setTexture(
        BitmapImage.concatY(
            getSprite(spritemap, scale, 58, 14, 15, 4, false, false),
            getSprite(spritemap, scale, 58, 33, 15, 10, false, false)));
    backRight.setTexture(
        BitmapImage.concatY(
            getSprite(spritemap, scale, 73, 14, 15, 4, false, false),
            getSprite(spritemap, scale, 73, 33, 15, 10, false, false)));
    return true;
  }

  @Override
  public boolean load(Path texturePack) {
    return load(file, texturePack);
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
}
