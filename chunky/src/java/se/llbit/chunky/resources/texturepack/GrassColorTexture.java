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
import se.llbit.chunky.world.Biomes;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class GrassColorTexture extends TextureLoader {
  private final String file;

  public GrassColorTexture(String file) {
    this.file = file;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage grassColor = ImageLoader.read(imageStream);
    if (grassColor.width != 256 || grassColor.height != 256) {
      throw new TextureFormatError("Grass color texture must be 256 by 256 pixels!");
    }
    Biomes.loadGrassColors(grassColor);
    return true;
  }

  @Override public boolean load(ZipFile texturePack) {
    return load(file, texturePack);
  }
}

