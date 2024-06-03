/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
import se.llbit.chunky.resources.texturepack.FontTexture.Glyph;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.resources.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class JsonFontTextureLoader extends TextureLoader {
  private final String rootFontDefinitionPath;

  public JsonFontTextureLoader(String fontDefinitionPath) {
    this.rootFontDefinitionPath = fontDefinitionPath;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }

  @Override
  public boolean loadFromFile(File file) {
    return false;
  }

  @Override
  public boolean load(LayeredResourcePacks texturePack) {
    Texture.fonts.clear();
    Texture.fonts.setGlyph(' ', new Glyph(new int[8], 0, 2, 8, 8, 7));

    return loadFontDefinitions(texturePack, rootFontDefinitionPath);
  }

  private boolean loadFontDefinitions(LayeredResourcePacks texturePack, String fontDefinitionPath) {
    Optional<InputStream> inputStream;
    try {
      inputStream = texturePack.getInputStream(fontDefinitionPath);
    } catch (IOException e) {
      return false;
    }
    if (!inputStream.isPresent()) {
      return false;
    }

    JsonArray fontDefinitions;
    try (InputStream is = inputStream.get()) {
      fontDefinitions = new JsonParser(is).parse().asObject().get("providers").asArray();
    } catch (IOException | SyntaxError e) {
      // Safe to ignore - will be handled implicitly later.
      Log.error("Failed to load font definition: " + fontDefinitionPath, e);
      return false;
    }

    for (JsonValue fontDefinition : fontDefinitions) {
      JsonObject definition = fontDefinition.asObject();
      if (definition.get("type").stringValue("").equals("bitmap")) {
        BitmapImage spritemap;
        String texture = definition.get("file").stringValue("").split(":")[1];
        Optional<LayeredResourcePacks.Entry> texturePath = texturePack.getFirstEntry("assets/minecraft/textures/" + texture);
        if (texturePath.isPresent()) {
          try (InputStream imageStream = texturePath.get().getInputStream()) {
            spritemap = ImageLoader.read(imageStream);
          } catch (IOException e) {
            Log.error("Failed to load font texture: " + texture, e);
            continue;
          }
        } else {
          Log.warnf(
            "Could not find font texture: %s (defined in \"%s\")",
            texture,
            fontDefinitionPath
          );
          continue;
        }

        int width = spritemap.width / 16;
        int height = fontDefinition.asObject().get("height").asInt(8);
        int ascent =
          fontDefinition
            .asObject()
            .get("ascent")
            .asInt(7); // distance (from top) of the letter base

        int x, y = 0;
        for (JsonValue charactersLine : fontDefinition.asObject().get("chars").asArray()) {
          x = 0;
          for (int codePoint : charactersLine.stringValue("").codePoints().toArray()) {
            if (!Texture.fonts.containsGlyph(codePoint)) {
              Texture.fonts.loadGlyph(spritemap, x, y, codePoint, width, height, ascent);
            }
            x++;
          }
          y++;
        }
      } else if (definition.get("type").stringValue("").equals("reference")) {
        String fontInclude = definition.get("id").stringValue("").split(":")[1];
        loadFontDefinitions(texturePack, "assets/minecraft/font/" + fontInclude + ".json");
      }
    }

    return true;
  }

  @Override
  protected boolean load(String file, LayeredResourcePacks texturePack) {
    return super.load(file, texturePack);
  }
}
