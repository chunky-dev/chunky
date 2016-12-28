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
import se.llbit.resources.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class FontTexture extends TextureLoader {
  private final String file;

  public static Glyph[] glyphs = new Glyph[256];

  static {
    for (int i = 0; i < 256; ++i) {
      glyphs[i] = new Glyph(0, 0, 0, 0);
    }
  }

  public static class Glyph {

    public final int top;
    public final int bot;
    public final int xmin;
    public final int xmax;
    public final int width;

    public Glyph(int top, int bot, int xmin, int xmax) {
      this.top = top;
      this.bot = bot;
      this.xmin = xmin;
      this.xmax = xmax;
      if (xmax >= xmin) {
        width = xmax - xmin + 2;
      } else {
        width = 8;
      }
    }
  }

  public FontTexture(String file) {
    this.file = file;
  }

  @Override protected boolean load(InputStream imageStream) throws IOException, TextureFormatError {
    BitmapImage spritemap = ImageLoader.read(imageStream);
    if (spritemap.width != 128 || spritemap.height != 128) {
      throw new TextureFormatError("Font texture must be 128 by 128 pixels");
    }

    for (int i = 0; i < 255; ++i) {
      loadGlyph(spritemap, i);
    }
    return true;
  }

  private static void loadGlyph(BitmapImage spritemap, int ch) {
    int x = 0x0F & ch;
    int y = (0xF0 & ch) >> 4;
    int x0 = x * 8;
    int y0 = y * 8;
    int top = 0;
    int bot = 0;
    int bit = 0;
    int xmin = 8;
    int xmax = 0;
    if (ch == ' ') {
      // Space glyph gets a fixed width of 2 pixels.
      glyphs[ch] = new Glyph(top, bot, xmin, xmin + 2);
      return;
    }
    for (int i = 0; i < 8; ++i) {
      for (int j = 0; j < 8; ++j) {
        int rgb = spritemap.getPixel(x0 + j, y0 + i);
        if (rgb != 0) {
          if (bit < 32) {
            top |= 1 << bit;
          } else {
            bot |= 1 << (bit - 32);
          }
          if (j < xmin) {
            xmin = j;
          }
          if (j > xmax) {
            xmax = j;
          }
        }
        bit += 1;
      }
    }
    glyphs[ch] = new Glyph(top, bot, xmin, xmax);
  }

  @Override public boolean load(ZipFile texturePack) {
    return load(file, texturePack);
  }
}

