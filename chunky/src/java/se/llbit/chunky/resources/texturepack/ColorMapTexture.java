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
import se.llbit.chunky.world.biome.Biomes;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ColorMapTexture extends TextureLoader {
  private final String file;
  private final Type type;

  public ColorMapTexture(String file, Type type) {
    this.file = file;
    this.type = type;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage colorMap = ImageLoader.read(imageStream);
    if (colorMap.width != 256 || colorMap.height != 256) {
      throw new TextureFormatError("Color map texture must be 256 by 256 pixels");
    }
    if (this.type == Type.GRASS) {
      Biomes.loadGrassColors(colorMap);
    } else if (this.type == Type.FOLIAGE) {
      Biomes.loadFoliageColors(colorMap);
    } else if (this.type == Type.DRY_FOLIAGE) {
      Biomes.loadDryFoliageColors(colorMap);
    }
    return true;
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    return load(file, texturePack);
  }

  public enum Type {
    GRASS,
    FOLIAGE,
    DRY_FOLIAGE
  }
}

