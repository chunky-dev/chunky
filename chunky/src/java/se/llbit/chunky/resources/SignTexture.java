/* Copyright (c) 2015-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2015-2021 Chunky contributors
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

import se.llbit.chunky.entity.SignEntity.Color;
import se.llbit.chunky.resources.texturepack.FontTexture.Glyph;
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
  private final Texture signTexture;

  public SignTexture(JsonArray[] text, Texture signTexture) {
    this.signTexture = signTexture;
    int ymargin = 1;
    int lineHeight = 10;
    int width = 96;
    int height = 48;
    BitmapImage img = new BitmapImage(width, height);
    int ystart = ymargin;
    for (JsonArray line : text) {
      if (line.isEmpty()) {
        ystart += lineHeight;
        continue;
      }
      int lineWidth = 0;
      for (JsonValue textItem : line) {
        String textLine = textItem.object().get("text").stringValue("");
        for (int c : textLine.codePoints().toArray()) {
          Glyph glyph = Texture.fonts.getGlyph(c);
          lineWidth += glyph != null ? glyph.width : 0;
        }
      }
      int xstart = (int) Math.ceil((width - lineWidth) / 2.0);
      for (JsonValue textItem : line) {
        String textLine = textItem.object().get("text").stringValue("");
        Color color = Color.get(textItem.object().get("color").intValue(0));

        for (int c : textLine.codePoints().toArray()) {
          Glyph glyph = Texture.fonts.getGlyph(c);
          if (glyph != null) {
            int y = ystart - glyph.ascent + lineHeight;

            for (int py = 0; py < glyph.height; ++py) {
              int x = xstart;
              for (int px = glyph.xmin; px <= glyph.xmax; ++px) {
                if ((glyph.lines[py] & (1 << px)) != 0 && x >= 0 && x < width) {
                  img.setPixel(x, y, color.rgbColor);
                }
                x += 1;
              }
              y += 1;
            }
            xstart += glyph.width;
          }
        }
      }
      ystart += lineHeight;
    }
    texture = new Texture(img);
  }

  @Override
  public void getColor(double u, double v, Vector4 c) {
    texture.getColor(u, v, c);
    if (c.w == 0) {
      signTexture.getColor(u * ww + u0, v * hh + v0, c);
    }
  }

  @Override
  public float[] getColor(double u, double v) {
    float[] rgba = texture.getColor(u, v);
    if (rgba[3] == 0) {
      return signTexture.getColor(u * ww + u0, v * hh + v0);
    }
    return rgba;
  }
}
