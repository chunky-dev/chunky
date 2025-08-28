/*
 * Copyright (c) 2021 Chunky contributors
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
import java.nio.file.Path;

/**
 * Non-animated texture loader for painting textures (which are not squares).
 */
public class PaintingTexture extends TextureLoader {

  public final String file;
  protected Texture texture;

  public PaintingTexture(String file, Texture texture) {
    this.file = file;
    this.texture = texture;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException {
    BitmapImage image = ImageLoader.read(imageStream);
    texture.setTexture(image);
    return true;
  }

  @Override
  public boolean load(Path texturePack) {
    return load(file, texturePack);
  }

  @Override
  public String toString() {
    return "texture:" + file;
  }
}
