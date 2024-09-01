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
import se.llbit.chunky.resources.texture.BitmapTexture;

import java.io.IOException;
import java.io.InputStream;

/**
 * A texture indexed by position in Minecraft's old terrain.png.
 *
 * <p>This kind of texture has largely become obsolete since all
 * textures are now stored as individual PNGs.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class IndexedTexture extends TextureLoader {

  private final int index;
  private final BitmapTexture texture;

  /**
   * @param index   Index of the texture in the terrain file
   * @param texture The loaded image is written to this texture
   */
  public IndexedTexture(int index, BitmapTexture texture) {
    this.index = index;
    this.texture = texture;
  }

  @Override public boolean loadFromTerrain(BitmapImage[] terrain) {
    texture.setTexture(terrain[index]);
    return true;
  }

  @Override public boolean load(LayeredResourcePacks texturePack) {
    return false;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }
}
