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
import se.llbit.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class loads textures from a Minecraft resource pack.
 * Subclasses of this class are used for loading different kinds of
 * textures, e.g. entity textures, simple textures, chest textures etc.
 *
 * <p>Some textures need special processing to load, especially when
 * different texture formats are used in different Minecraft versions.
 * For example, block textures used to be stored in a texture atlas but now
 * are stored in separate files. We first try to load the texture from the
 * newest location, then try the texture atlas. Sometimes textures have
 * been renamed multiple times, and we try to load from several different files.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class TextureLoader {

  protected TextureLoader() {
  }

  /**
   * Attempt to load a texture from a texture pack.
   *
   * @param texturePack Reference to the texture pack zip file
   * @param topLevelDir The top-level directory of the resource pack, with
   * trailing slash. The assets directory should be inside the top-level directory.
   * This can be empty, if the assets directory is a top-level directory of the
   * Zip file.
   * @return <code>true</code> if the texture was successfully loaded
   */
  public abstract boolean load(ZipFile texturePack, String topLevelDir);

  /**
   * Attempt to load a texture from a PNG image file.
   *
   * @param file the texture file
   * @return <code>true</code> if the texture was successfully loaded
   * @throws TextureFormatError
   * @throws IOException
   */
  public boolean load(File file) throws IOException, TextureFormatError {
    try (FileInputStream in = new FileInputStream(file)) {
      return load(in);
    }
  }

  /**
   * Attempt to load a texture from a texture pack.
   *
   * @param file        Path of texture in texture pack
   * @param texturePack Reference to the texture pack zip file
   * @return <code>true</code> if the texture was successfully loaded
   */
  protected boolean load(String file, ZipFile texturePack) {
    try (InputStream in = texturePack.getInputStream(new ZipEntry(file + ".png"))) {
      if (in != null) {
        return load(in);
      }
    } catch (TextureFormatError e) {
      Log.info(e.getMessage());
    } catch (IOException e) {
      // Safe to ignore - will be handled implicitly later.
    }
    return false;
  }

  /**
   * Load this texture from the terrain spritemap.
   *
   * @return <code>true</code> if the texture was successfully loaded
   */
  public boolean loadFromTerrain(BitmapImage[] terrain) {
    return false;
  }

  protected abstract boolean load(InputStream imageStream) throws IOException, TextureFormatError;
}
