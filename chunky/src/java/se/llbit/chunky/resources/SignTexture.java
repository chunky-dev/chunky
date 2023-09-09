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

import se.llbit.chunky.entity.SignEntity.Color;
import se.llbit.chunky.resources.texturepack.FontTexture.Glyph;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonValue;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;
import se.llbit.math.Vector4;

public class SignTexture extends Texture {
  private static final int FONT_PIXELS_PER_PIXEL = 4;
  private final double hh, ww, u0, v0;
  private final Texture signTexture;
  private final PalettizedBitmapImage textColor;
  private final BinaryBitmapImage textMask;

  static private boolean hasVisibleCharacter(JsonArray line) {
    for (JsonValue textItem : line) {
      if (!textItem.object().get("text").stringValue("").trim().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  public SignTexture(JsonArray[] text, Texture signTexture, int signWidth, int signHeight, double x0, double y0, double x1, double y1) {
    this.signTexture = signTexture;
    int ymargin = 1;
    int lineHeight = 10;
    int width = signWidth * FONT_PIXELS_PER_PIXEL;
    int height = signHeight * FONT_PIXELS_PER_PIXEL;
    int ystart = ymargin;
    boolean allEmpty = true;
    for (JsonArray line : text) {
      if (hasVisibleCharacter(line)) {
        allEmpty = false;
        break;
      }
    }
    if (allEmpty) {
      textColor = null;
      textMask = null;
    } else {
      textColor = new PalettizedBitmapImage(width, height);
      textMask = new BinaryBitmapImage(width, height);
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
                  if ((glyph.lines[py] & (1 << px)) != 0) {
                    textColor.setPixel(x, y, color.id);
                    textMask.setPixel(x, y, true);
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
    }

    ww = x1 - x0;
    hh = y1 - y0;
    u0 = x0;
    v0 = y0;
  }

  @Override
  public float[] getColor(double u, double v) {
    int x = (int) (u * textColor.width - Ray.EPSILON);
    int y = (int) ((1 - v) * textColor.height - Ray.EPSILON);
    if (textMask != null && textMask.getPixel(x, y)) {
      Color characterColor = Color.get(textColor.getPixel(x, y));
      return characterColor.linearColor;
    } else {
      return signTexture.getColor(u * ww + u0, v * hh + v0);
    }
  }
}
