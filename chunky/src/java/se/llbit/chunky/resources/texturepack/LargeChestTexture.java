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
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LargeChestTexture extends TextureLoader {
  private final String file;
  private final ChestTexture.Textures texturesLeft;
  private final ChestTexture.Textures texturesRight;

  public LargeChestTexture(
    String file,
    ChestTexture.Textures texturesLeft,
    ChestTexture.Textures texturesRight) {
    this.file = file;
    this.texturesLeft = texturesLeft;
    this.texturesRight = texturesRight;
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

    texturesRight.right.setTexture(
      BitmapImage.concatY(
        getSprite(spritemap, scale, 0, 14, 14, 4, true, false),
        getSprite(spritemap, scale, 0, 33, 14, 10, true, false)));
    texturesLeft.left.setTexture(
      BitmapImage.concatY(
        getSprite(spritemap, scale, 44, 14, 14, 4, false, false),
        getSprite(spritemap, scale, 44, 33, 14, 10, false, false)));
    texturesRight.top.setTexture(getSprite(spritemap, scale, 14, 0, 15, 14, true, false));
    texturesLeft.top.setTexture(getSprite(spritemap, scale, 29, 0, 15, 14, true, false));
    texturesRight.front.setTexture(
      BitmapImage.concatY(
        getSprite(spritemap, scale, 14, 14, 15, 4, true, false),
        getSprite(spritemap, scale, 14, 33, 15, 10, true, false)));
    texturesLeft.front.setTexture(
      BitmapImage.concatY(
        getSprite(spritemap, scale, 29, 14, 15, 4, true, false),
        getSprite(spritemap, scale, 29, 33, 15, 10, true, false)));
    texturesRight.bottom.setTexture(getSprite(spritemap, scale, 44, 19, 15, 14, true, false));
    texturesLeft.bottom.setTexture(getSprite(spritemap, scale, 59, 19, 15, 14, true, false));
    texturesLeft.back.setTexture(
      BitmapImage.concatY(
        getSprite(spritemap, scale, 58, 14, 15, 4, false, false),
        getSprite(spritemap, scale, 58, 33, 15, 10, false, false)));
    texturesRight.back.setTexture(
      BitmapImage.concatY(
        getSprite(spritemap, scale, 73, 14, 15, 4, false, false),
        getSprite(spritemap, scale, 73, 33, 15, 10, false, false)));
    return true;
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
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
