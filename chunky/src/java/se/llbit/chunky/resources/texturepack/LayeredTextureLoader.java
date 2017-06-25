/*
 * Copyright (c) 2016 Jesper Öqvist <jesper@llbit.se>
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
 * Loads two textures and overlays them on top of each other.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class LayeredTextureLoader extends TextureLoader {
  private final TextureLoader subLoader;
  private final String textureName;
  private final Texture texture;

  public LayeredTextureLoader(String file, Texture texture, TextureLoader subLoader) {
    this.textureName = file;
    this.texture = texture;
    this.subLoader = subLoader;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage image = ImageLoader.read(imageStream);
    if (image.width != texture.getWidth() || image.height != texture.getHeight()) {
      throw new TextureFormatError("Can't layer textures with different sizes.");
    }

    BitmapImage result = new BitmapImage(texture.getBitmap());
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int pixel = image.getPixel(x, y);
        if (pixel != 0) {
          result.setPixel(x, y, pixel);
        }
      }
    }
    texture.setTexture(result);
    return true;
  }

  @Override public boolean load(ZipFile texturePack, String topLevelDir) {
    return subLoader.load(texturePack, topLevelDir)
        && load(topLevelDir + textureName, texturePack);
  }
}

