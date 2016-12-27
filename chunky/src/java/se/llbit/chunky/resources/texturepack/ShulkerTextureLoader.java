/* Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.resources.ShulkerTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * Loads a set of shulker textures for rendering shulker boxes.
 *
 * <p>The shulker texture is an entity texture with top and bottom parts for the sides.
 * For rendering shulker boxes we are only interested in a complete side texture, so
 * we manually merge the top and bottom parts to form a complete side.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ShulkerTextureLoader extends TextureLoader {
  private final SimpleTexture topLoader;
  private final String entityTexture;
  private final ShulkerTexture texture;

  public ShulkerTextureLoader(String topTexture, String entityTexture,
      ShulkerTexture texture) {
    this.topLoader = new SimpleTexture(topTexture, texture.top);
    this.entityTexture = entityTexture;
    this.texture = texture;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BufferedImage spritemap = ImageIO.read(imageStream);
    if (spritemap.getWidth() != spritemap.getHeight() || spritemap.getWidth() % 16 != 0) {
      throw new TextureFormatError(
          "Shulker texture must have equal width and height, divisible by 16!");
    }

    int imgW = spritemap.getWidth();
    int scale = imgW / (16 * 4);

    texture.bottom.setTexture(loadBottom(spritemap, scale, 32, 28));
    texture.side.setTexture(loadSide(spritemap, scale));
    return true;
  }

  private static BitmapImage loadBottom(BufferedImage spritemap, int scale, int u, int v) {
    BitmapImage image = new BitmapImage(scale * 16, scale * 16);
    int x0 = u * scale;
    int x1 = (u + 16) * scale;
    int y0 = v * scale;
    int y1 = (v + 16) * scale;
    for (int y = y0; y < y1; ++y) {
      int sy = y - y0;
      for (int x = x0; x < x1; ++x) {
        int sx = x - x0;
        image.setPixel(sx, sy, spritemap.getRGB(x, y));
      }
    }
    return image;
  }

  /**
   * Load the side texture from the shulker entity texture.
   * This stitches together the top and bottom parts of the first
   * side texture.
   */
  private static BitmapImage loadSide(BufferedImage spritemap, int scale) {
    BitmapImage image = new BitmapImage(scale * 16, scale * 16);

    // Load the top part:
    int x0 = 0;
    int x1 = 16 * scale;
    int y0 = 16 * scale;
    int y1 = (16 + 12) * scale;
    for (int y = y0; y < y1; ++y) {
      int sy = y - y0;
      for (int x = x0; x < x1; ++x) {
        int sx = x - x0;
        image.setPixel(sx, sy, spritemap.getRGB(x, y));
      }
    }

    // Load the bottom lip:
    x0 = 4 * scale;
    x1 = (4 + 8) * scale;
    y0 = 44 * scale;
    y1 = 48 * scale;
    for (int y = y0; y < y1; ++y) {
      int sy = y - y0 + 8 * scale;
      for (int x = x0; x < x1; ++x) {
        int sx = x - x0 + 4 * scale;
        image.setPixel(sx, sy, spritemap.getRGB(x, y));
      }
    }

    // Load the rest of the bottom part:
    x0 = 0;
    x1 = 16 * scale;
    y0 = 48 * scale;
    y1 = 52 * scale;
    for (int y = y0; y < y1; ++y) {
      int sy = y - y0 + 12 * scale;
      for (int x = x0; x < x1; ++x) {
        int sx = x - x0;
        image.setPixel(sx, sy, spritemap.getRGB(x, y));
      }
    }
    return image;
  }

  @Override public boolean load(ZipFile texturePack) {
    return topLoader.load(texturePack) && load(entityTexture, texturePack);
  }
}

