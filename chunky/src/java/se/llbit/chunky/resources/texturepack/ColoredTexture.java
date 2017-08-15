/*
 * Copyright (c) 2017 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.math.ColorUtil;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * This works like a simple texture loader, but it colors the texture with a base color.
 *
 * <p>This is used for coloring leather armor textures.
 */
public class ColoredTexture extends TextureLoader {
  private final String file;
  private final float[] color = new float[4];
  private final Texture texture;

  public ColoredTexture(String file, int color, Texture texture) {
    this.file = file;
    this.texture = texture;
    ColorUtil.getRGBAComponents(color, this.color);
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage image = ImageLoader.read(imageStream);
    float[] pixel = new float[4];
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int argb = image.getPixel(x, y);
        ColorUtil.getRGBAComponents(argb, pixel);
        image.setPixel(x, y, ColorUtil.getArgb(
            color[0] * pixel[0],
            color[1] * pixel[1],
            color[2] * pixel[2],
            pixel[3]));
      }
    }
    texture.setTexture(image);
    return true;
  }

  @Override public boolean load(ZipFile texturePack, String topLevelDir) {
    return load(topLevelDir + file, texturePack);
  }
}
