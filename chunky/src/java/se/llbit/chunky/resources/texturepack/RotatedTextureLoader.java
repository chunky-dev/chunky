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
import se.llbit.chunky.resources.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * Loads a single texture and rotates it 90 degrees clockwise and
 * flips it horizontally (x & z coordinate swap).
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class RotatedTextureLoader extends TextureLoader {
  protected TextureLoader loader;
  protected Texture texture;

  public RotatedTextureLoader(String file, Texture texture) {
    this.texture = texture;
    loader = new SimpleTexture(file, texture);
  }

  @Override public boolean load(ZipFile texturePack, String topLevelDir) {
    if (!loader.load(texturePack, topLevelDir)) {
      return false;
    }

    BitmapImage source = texture.getBitmap();
    texture.setTexture(source.diagonalFlipped());
    return true;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    throw new TextureFormatError("Call simple texture sub-loader instead.");
  }

}
