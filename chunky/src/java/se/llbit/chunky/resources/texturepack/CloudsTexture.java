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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import se.llbit.chunky.world.Clouds;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class CloudsTexture extends TextureRef {
  private final String file;

  public CloudsTexture(String file) {
    this.file = file;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {

    BufferedImage texture = ImageIO.read(imageStream);
    if (texture.getWidth() != texture.getHeight() || texture.getWidth() != 256) {
      throw new TextureFormatError("Clouds texture size must be 256 by 256 pixels!");
    }

    for (int y = 0; y < 256; ++y) {
      for (int x = 0; x < 256; ++x) {
        int v = texture.getRGB(x, y) >>> 31;
        Clouds.setCloud(x, y, v);
      }
    }
    return true;
  }

  @Override public boolean load(ZipFile texturePack) {
    return load(file, texturePack);
  }
}

