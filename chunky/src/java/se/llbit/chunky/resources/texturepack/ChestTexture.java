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

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ChestTexture extends TextureLoader {
  public enum Layout {
    OLD_LAYOUT,
    NEW_LAYOUT, // new texture layout introduced in MC 1.15
  }

  public static class Textures {
    public final Texture lock;
    public final Texture top = new Texture();
    public final Texture bottom = new Texture();
    public final Texture left = new Texture();
    public final Texture right = new Texture();
    public final Texture front = new Texture();
    public final Texture back = new Texture();

    public Textures() {
      this(new Texture());
    }

    private Textures(Texture lock) {
      this.lock = lock;
    }

    public static Textures newWithLockFrom(Textures textures) {
      return new Textures(textures.lock);
    }
  }

  private final String file;
  private final Layout layout;
  private final Textures textures;

  public ChestTexture(
    String file,
    Layout layout,
    Textures textures) {
    this.file = file;
    this.layout = layout;
    this.textures = textures;
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
      textures.lock.setTexture(getSprite(spritemap, scale, 0, 0, 8, 8, false, false)); // TODO flip
      textures.top.setTexture(getSprite(spritemap, scale, 28, 0, 14, 14, true, true));
      textures.bottom.setTexture(getSprite(spritemap, scale, 14, 19, 14, 14, true, true));
      textures.right.setTexture(
        BitmapImage.concatY(
          getSprite(spritemap, scale, 0, 15, 14, 4, false, true),
          getSprite(spritemap, scale, 0, 33, 14, 10, false, true)));
      textures.left.setTexture(
        BitmapImage.concatY(
          getSprite(spritemap, scale, 28, 15, 14, 4, true, true),
          getSprite(spritemap, scale, 28, 33, 14, 10, true, true)));
      textures.front.setTexture(
        BitmapImage.concatY(
          getSprite(spritemap, scale, 42, 15, 14, 4, false, true),
          getSprite(spritemap, scale, 42, 33, 14, 10, false, true)));
      textures.back.setTexture(
        BitmapImage.concatY(
          getSprite(spritemap, scale, 14, 15, 14, 4, true, true),
          getSprite(spritemap, scale, 14, 33, 14, 10, true, true)));
    } else {
      textures.lock.setTexture(getSprite(spritemap, scale, 0, 0, 8, 8, false, false));
      textures.top.setTexture(getSprite(spritemap, scale, 14, 0, 14, 14, true, false));
      textures.bottom.setTexture(getSprite(spritemap, scale, 28, 19, 14, 14, true, false));
      textures.right.setTexture(
        BitmapImage.concatY(
          getSprite(spritemap, scale, 0, 14, 14, 4, true, false),
          getSprite(spritemap, scale, 0, 33, 14, 10, true, false)));
      textures.left.setTexture(
        BitmapImage.concatY(
          getSprite(spritemap, scale, 28, 14, 14, 4, false, false),
          getSprite(spritemap, scale, 28, 33, 14, 10, false, false)));
      textures.front.setTexture(
        BitmapImage.concatY(
          getSprite(spritemap, scale, 14, 14, 14, 4, true, false),
          getSprite(spritemap, scale, 14, 33, 14, 10, true, false)));
      textures.back.setTexture(
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
  public boolean load(LayeredResourcePacks texturePack) {
    return load(file, texturePack);
  }
}
