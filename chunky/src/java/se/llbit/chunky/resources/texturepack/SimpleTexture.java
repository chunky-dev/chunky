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
import se.llbit.chunky.resources.pbr.*;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

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
    texture.setTexture(getTextureOrFirstFrame(imageStream));
    return true;
  }

  private static BitmapImage getTextureOrFirstFrame(InputStream imageStream) throws IOException {
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
      return frame0;
    } else {
      return image;
    }
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    return load(file, texturePack);
  }

  @Override
  public boolean load(String file, LayeredResourcePacks texturePack) {
    boolean loaded = super.load(file, texturePack);
    String specularFormat = System.getProperty("chunky.pbr.specular", "");
    if (specularFormat.equals("oldpbr") || specularFormat.equals("labpbr")) {
      Optional<InputStream> in;
      try {
        in = texturePack.getInputStream(file + "_s.png");
      } catch (IOException e) {
        return false;
      }
      if (in.isEmpty()) {
        return false;
      }
      try {
        // LabPBR uses the alpha channel for the emission map
        // Some resource packs use the blue channel (Red=Smoothness, Green=Metalness, Blue=Emission)
        // (In BSL, this option is called "Old PBR + Emissive")
        if (specularFormat.equals("oldpbr")) {
          OldPbrSpecularMap specular = new OldPbrSpecularMap(getTextureOrFirstFrame(in.get()));
          texture.setEmissionMap(specular.hasEmission() ? specular : EmissionMap.EMPTY);
          texture.setReflectanceMap(specular.hasReflectance() ? specular : ReflectanceMap.EMPTY);
          texture.setRoughnessMap(specular.hasRoughness() ? specular : RoughnessMap.EMPTY);
        } else if (specularFormat.equals("labpbr")) {
          LabPbrSpecularMap specular = new LabPbrSpecularMap(getTextureOrFirstFrame(in.get()));
          texture.setEmissionMap(specular.hasEmission() ? specular : EmissionMap.EMPTY);
          texture.setReflectanceMap(specular.hasReflectance() ? specular : ReflectanceMap.EMPTY);
          texture.setRoughnessMap(specular.hasRoughness() ? specular : RoughnessMap.EMPTY);
        }
      } catch (IOException e) {
        // Safe to ignore
        texture.setEmissionMap(EmissionMap.EMPTY);
        texture.setReflectanceMap(ReflectanceMap.EMPTY);
        texture.setRoughnessMap(RoughnessMap.EMPTY);
      } finally {
        try {
          in.get().close();
        } catch (IOException e) {
          // ignore
        }
      }
    }

    return loaded;
  }

  @Override
  public String toString() {
    return "texture:" + file;
  }

  @Override
  public void reset() {
    texture.reset();
  }
}
