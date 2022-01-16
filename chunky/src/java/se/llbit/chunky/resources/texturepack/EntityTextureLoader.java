/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Helper to load entity textures, i.e. creeper, zombie, skeleton etc. textures.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EntityTextureLoader extends TextureLoader {

  private final String file;
  protected final EntityTexture texture;

  public EntityTextureLoader(String file, EntityTexture texture) {
    this.file = file;
    this.texture = texture;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage image = ImageLoader.read(imageStream);

    if (image.width != image.height && image.width != 2 * image.height) {
      throw new TextureFormatError("Entity texture should be 64x64 or 64x32 pixels, "
              + "or a multiple of those dimensions.");
    }

    texture.setTexture(image);
    return true;
  }

  @Override
  public boolean load(Path texturePack) {
    return load(file, texturePack);
  }
}
