/*
 * Copyright (c) 2022 Chunky contributors
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
package se.llbit.chunky.resources;

import se.llbit.chunky.resources.texturepack.TextureLoader;
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ResourcePackTextureLoader implements ResourcePackLoader.PackLoader {
  private final HashMap<String, TextureLoader> texturesToLoad;

  /**
   * Create a texture loader.
   *
   * @param textures Textures to load. This map will be duplicated and will not be modified.
   */
  public ResourcePackTextureLoader(Map<String, TextureLoader> textures) {
    texturesToLoad = new HashMap<>(textures);
  }

  /**
   * Create a texture loader for a single texture.
   */
  public static ResourcePackTextureLoader singletonLoader(String textureId, TextureLoader loader) {
    return new ResourcePackTextureLoader(Collections.singletonMap(textureId, loader));
  }

  @Override
  public boolean load(LayeredResourcePacks resourcePacks) {
    if (texturesToLoad.isEmpty()) {
      return true;
    }

    // Keep track of which textures have been loaded and may be removed
    ArrayList<String> toRemove = new ArrayList<>();

    for (Map.Entry<String, TextureLoader> texture : texturesToLoad.entrySet()) {
      if (texture.getValue().load(resourcePacks)) {
        toRemove.add(texture.getKey());
      }
    }
    loadTerrainTextures(resourcePacks, toRemove);

    // Remove all textures which have been loaded
    toRemove.forEach(texturesToLoad::remove);

    return texturesToLoad.isEmpty();
  }

  @Override
  public Collection<String> notLoaded() {
    return Collections.unmodifiableCollection(texturesToLoad.keySet());
  }

  private void loadTerrainTextures(LayeredResourcePacks root, ArrayList<String> toRemove) {
    Optional<InputStream> in = Optional.empty();
    try {
      in = root.getInputStream("terrain.png");
      if (!in.isPresent()) {
        return;
      }
      BitmapImage spriteMap = ImageLoader.read(in.get());
      BitmapImage[] terrainTextures = getTerrainTextures(spriteMap);

      for (Map.Entry<String, TextureLoader> texture : texturesToLoad.entrySet()) {
        if (texture.getValue().loadFromTerrain(terrainTextures)) {
          toRemove.add(texture.getKey());
        }
      }
    } catch (IOException e) {
      // Failed to load terrain textures - this is handled implicitly.
    } finally {
      if (in.isPresent()) {
        try {
          in.get().close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }

  /**
   * Load a 16x16 spritemap.
   *
   * @return A bufferedImage containing the spritemap
   * @throws IOException if the image dimensions are incorrect
   */
  private static BitmapImage[] getTerrainTextures(BitmapImage spritemap) throws IOException {
    if (spritemap.width != spritemap.height || spritemap.width % 16 != 0) {
      throw new IOException(
        "Error: terrain.png file must have equal width and height, divisible by 16!");
    }

    int imgW = spritemap.width;
    int spriteW = imgW / 16;
    BitmapImage[] tex = new BitmapImage[256];

    for (int i = 0; i < 256; ++i) {
      tex[i] = new BitmapImage(spriteW, spriteW);
    }

    for (int y = 0; y < imgW; ++y) {
      int sy = y / spriteW;
      for (int x = 0; x < imgW; ++x) {
        int sx = x / spriteW;
        BitmapImage texture = tex[sx + sy * 16];
        texture.setPixel(x % spriteW, y % spriteW, spritemap.getPixel(x, y));
      }
    }
    return tex;
  }
}
