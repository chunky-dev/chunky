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
import se.llbit.chunky.resources.Texture;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class SplitLargeChestTexture extends TextureLoader {
  public enum Part {
    LEFT,
    RIGHT
  }

  private final String file;
  private final Part part;
  private final Texture left;
  private final Texture top;
  private final Texture right;
  private final Texture bottom;
  private final Texture front;
  private final Texture back;

  public SplitLargeChestTexture(
          String file,
          Part part,
          Texture top,
          Texture bottom,
          Texture left,
          Texture right,
          Texture front,
          Texture back) {
    this.file = file;
    this.part = part;
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
    if (spritemap.width % 16 != 0 || spritemap.height % 16 != 0) {
      throw new TextureFormatError(
              "Large chest texture file must have width and height divisible by 16!");
    }

    int imgW = spritemap.width;
    int scale = imgW / (16 * 4);

    top.setTexture(getSprite(spritemap, scale, 29, 0, 15, 14, true, true));
    bottom.setTexture(getSprite(spritemap, scale, 14, 19, 15, 14, true, true));
    front.setTexture(
            BitmapImage.concatY(
                    getSprite(spritemap, scale, 43, 15, 15, 4, false, true),
                    getSprite(spritemap, scale, 43, 33, 15, 10, false, true)));
    back.setTexture(
            BitmapImage.concatY(
                    getSprite(spritemap, scale, 14, 15, 15, 4, true, true),
                    getSprite(spritemap, scale, 14, 33, 15, 10, true, true)));

    if (this.part == Part.LEFT) {
      left.setTexture(
              BitmapImage.concatY(
                      getSprite(spritemap, scale, 29, 15, 14, 4, true, true),
                      getSprite(spritemap, scale, 29, 33, 14, 10, true, true)));
    } else {
      right.setTexture(
              BitmapImage.concatY(
                      getSprite(spritemap, scale, 0, 15, 14, 4, false, true),
                      getSprite(spritemap, scale, 0, 33, 14, 10, false, true)));
    }

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

  @Override
  public void reset() {
    if(left!=null) {
      left.reset();
    }
    if (right != null) {
      right.reset();
    }
    top.reset();
    bottom.reset();
    front.reset();
    back.reset();
  }
}
