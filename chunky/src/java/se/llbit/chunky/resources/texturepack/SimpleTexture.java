/* Copyright (c) 2013-2015 Jesper Öqvist <jesper@llbit.se>
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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Non-animated texture loader.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class SimpleTexture extends TextureLoader {

  public final String file;
  protected Texture texture;

  public SimpleTexture(String file, Texture texture) {
    this.file = file;
    this.texture = texture;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException {
    BitmapImage image = ImageLoader.read(imageStream);

    if (image.height > image.width) {
      // Assuming this is an animated texture.
      // Just grab the first frame.
      int frameW = image.width;

      BitmapImage frame0 = new BitmapImage(frameW, frameW);
      for (int y = 0; y < frameW; ++y) {
        for (int x = 0; x < frameW; ++x) {
          frame0.setPixel(x, y, image.getPixel(x, y));
        }
      }
      texture.setTexture(frame0);
    } else {
      texture.setTexture(image);
    }
    return true;
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    return load(file, texturePack);
  }

  @Override
  public boolean load(String file, Path texturePack) {
    boolean loaded = super.load(file, texturePack);
    if (loaded) {
      try (InputStream in = Files.newInputStream(texturePack.resolve(file + "_s.png"))) {
        // LabPBR uses the alpha channel for the emission map
        // Some resource packs use the blue channel (Red=Smoothness, Green=Metalness, Blue=Emission)
        // (In BSL, this option is called "Old PBR + Emissive")
        BitmapImage specularMap = ImageLoader.read(in);
        byte[] emissionMap = new byte[specularMap.width * specularMap.height];
        boolean hasEmission = false;
        for (int y = 0; y < specularMap.height; ++y) {
          for (int x = 0; x < specularMap.width; ++x) {
            // blue
            if ((emissionMap[y * specularMap.width + x] = (byte) (
              specularMap.data[y * specularMap.width + x] & 0xFF)) != (byte) 0x00) {
              hasEmission = true;
            }

            // alpha
            // emissionMap[y * specularMap.width + x] = (byte) (
            //    specularMap.data[(y * specularMap.width + x) * 4] >>> 24);
          }
        }
        if (hasEmission) {
          texture.setEmissionMap(emissionMap);
        }
      } catch (IOException e) {
        // Safe to ignore
      }
    }
    return loaded;
  }

  @Override
  public String toString() {
    return "texture:" + file;
  }
}
