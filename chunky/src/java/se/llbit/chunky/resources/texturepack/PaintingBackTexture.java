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
import se.llbit.chunky.resources.LayeredResourcePacks;
import se.llbit.chunky.resources.Texture;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Texture loader for the 1.14+ painting back texture.
 * <p>
 * In 1.13 and earlier, this used to be a 64x64 texture from the paintings texture atlas. Starting
 * with 1.14, it is a 16x16 texture that then gets tiled as needed. For easier usage in Chunky, this
 * loader does the tiling so {@link Texture#paintingBack} always has the size of a 4x4 blocks
 * picture frame regardless of the resource pack version.
 */
public class PaintingBackTexture extends TextureLoader {

  public final String file;
  protected Texture texture;

  public PaintingBackTexture(String file, Texture texture) {
    this.file = file;
    this.texture = texture;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException {
    BitmapImage tile = ImageLoader.read(imageStream);
    BitmapImage finalTexture = new BitmapImage(tile.width * 4, tile.height * 4);
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        finalTexture.blit(tile, x * tile.width, y * tile.height);
      }
    }
    texture.setTexture(finalTexture);
    return true;
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    return load(file, texturePack);
  }

  @Override
  public String toString() {
    return "texture:" + file;
  }
}
