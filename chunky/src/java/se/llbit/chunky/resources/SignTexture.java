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
package se.llbit.chunky.resources;

import se.llbit.chunky.resources.texturepack.FontTexture;
import se.llbit.chunky.resources.texturepack.FontTexture.Glyph;
import se.llbit.chunky.world.entity.SignEntity.Color;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonValue;
import se.llbit.math.Vector4;

public class SignTexture extends Texture {

  private static final double ww, hh, u0, v0;

  static {
    // Set up texture coordinates.
    u0 = 2 / 64.;
    double u1 = 26 / 64.;
    v0 = 18 / 32.;
    double v1 = 30 / 32.;
    ww = u1 - u0;
    hh = v1 - v0;
  }

  private final Texture texture;

  public SignTexture(JsonArray[] text) {
    int xmargin = 4;
    int ymargin = 4;
    int gh = 10;
    int width = 90 + xmargin * 2;
    int height = gh * 4 + ymargin * 2;
    BitmapImage img = new BitmapImage(width, height);
    int[] data = img.data;
    int ystart = ymargin;
    for (JsonArray line : text) {
      if (line.getNumElement() == 0) {
        ystart += gh;
        continue;
      }
      int lineWidth = 0;
      for (JsonValue textItem : line.getElementList()) {
        String textLine = textItem.object().get("text").stringValue("");
        for (int j = 0; j < textLine.length(); ++j) {
          char c = textLine.charAt(j);
          Glyph glyph = FontTexture.glyphs[0xFF & c];
          lineWidth += glyph.width;
        }
      }
      int xstart = (width - lineWidth) / 2;
      for (JsonValue textItem : line.getElementList()) {
        String textLine = textItem.object().get("text").stringValue("");
        Color color = Color.get(textItem.object().get("color").intValue(0));

        for (int j = 0; j < textLine.length(); ++j) {
          char c = textLine.charAt(j);
          Glyph glyph = FontTexture.glyphs[0xFF & c];
          int k = 0;
          int y = ystart;
          for (int py = 0; py < 8; ++py) {
            k += glyph.xmin;
            int x = xstart;
            for (int px = glyph.xmin; px <= glyph.xmax; ++px) {
              int bit;
              if (k < 32) {
                bit = glyph.top & (1 << k);
              } else {
                bit = glyph.bot & (1 << (k - 32));
              }
              if (bit != 0) {
                data[y * width + x] = color.rgbColor;
              }
              k += 1;
              x += 1;
            }
            k += 7 - glyph.xmax;
            y += 1;
          }
          xstart += glyph.width;
        }
      }
      ystart += gh;
    }
    texture = new Texture(img);
  }

  @Override public void getColor(double u, double v, Vector4 c) {
    texture.getColor(u, v, c);
    if (c.w == 0) {
      Texture.signPost.getColor(u * ww + u0, v * hh + v0, c);
    }
  }

}
