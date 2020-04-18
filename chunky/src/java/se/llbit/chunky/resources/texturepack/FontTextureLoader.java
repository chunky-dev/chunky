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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonParser;
import se.llbit.json.JsonParser.SyntaxError;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.resources.ImageLoader;

/** @author Jesper Öqvist <jesper@llbit.se> */
public class FontTextureLoader extends TextureLoader {
  public static Map<Character, Glyph> glyphs = new HashMap<>();

  public static class Glyph {
    public final int[] lines;
    public final int xmin;
    public final int xmax;
    public final int width;
    public final int spriteWidth;
    public final int height;
    public final int ascent;

    public Glyph(int[] lines, int xmin, int xmax, int width, int height, int ascent) {
      this.lines = lines;
      this.xmin = xmin;
      this.xmax = xmax;
      if (xmax >= xmin) {
        this.width = xmax - xmin + 2;
      } else {
        this.width = 8;
      }
      this.spriteWidth = width;
      this.height = height;
      this.ascent = ascent;
    }
  }

  private final String fontDefinition;

  public FontTextureLoader(String fontDefinition) {
    this.fontDefinition = fontDefinition;
  }

  @Override
  protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    return false;
  }

  @Override
  public boolean load(File file) throws IOException, TextureFormatError {
    return false;
  }

  @Override
  public boolean load(ZipFile texturePack, String topLevelDir) {
    glyphs.clear();
    glyphs.put(' ', new Glyph(new int[8], 0, 2, 8, 8, 7));

    JsonArray fontDefinitions;
    try (InputStream is = texturePack.getInputStream(new ZipEntry(topLevelDir + fontDefinition))) {
      if (is == null) {
        return false;
      }
      fontDefinitions = new JsonParser(is).parse().asObject().get("providers").asArray();
    } catch (IOException | SyntaxError e) {
      // Safe to ignore - will be handled implicitly later.
      return false;
    }

    for (JsonValue fontDefinition : fontDefinitions) {
      if (!fontDefinition.asObject().get("type").stringValue("").equals("bitmap")) {
        continue;
      }

      BitmapImage spritemap;
      String texture = fontDefinition.asObject().get("file").stringValue("").split(":")[1];
      try (InputStream imageStream =
          texturePack.getInputStream(
              new ZipEntry(topLevelDir + "assets/minecraft/textures/" + texture))) {
        if (imageStream == null) {
          Log.error("Could not load font texture " + texture);
          return false;
        }
        spritemap = ImageLoader.read(imageStream);
      } catch (IOException e) {
        Log.error("Could not load font texture " + texture, e);
        return false;
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
        for (char character : charactersLine.stringValue("").toCharArray()) {
          if (!glyphs.containsKey(character)) {
            loadGlyph(spritemap, x, y, character, width, height, ascent);
          }
          x++;
        }
        y++;
      }
    }

    return true;
  }

  @Override
  protected boolean load(String file, ZipFile texturePack) {
    return super.load(file, texturePack);
  }

  private static void loadGlyph(
      BitmapImage spritemap, int x0, int y0, char ch, int width, int height, int ascent) {
    int xmin = width;
    int xmax = 0;
    int[] lines = new int[height];
    for (int i = 0; i < height; ++i) {
      for (int j = 0; j < width; ++j) {
        int rgb = spritemap.getPixel(x0 * width + j, y0 * height + i);
        if (rgb != 0) {
          lines[i] |= 1 << j;
          if (j < xmin) {
            xmin = j;
          }
          if (j > xmax) {
            xmax = j;
          }
        }
      }
    }
    glyphs.put(ch, new Glyph(lines, xmin, xmax, width, height, ascent));
  }
}
