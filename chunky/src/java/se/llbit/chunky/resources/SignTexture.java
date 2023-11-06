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
import se.llbit.math.Ray;
import se.llbit.util.annotation.Nullable;

public class SignTexture extends Texture {
  /**
   * We use this for the glow dye color to not require more memory (color palette only has 16 colors, the dye color is a 17th color).
   * If the text mask is false but the color is set to this value, we use the glow dye color (the bright one).
   */
  private static final int GLOW_DYE_COLOR = 1;
  /**
   * We use this for the glow dye outline color to not require more memory (color palette only has 16 colors, the glow dye color is an 18th color).
   * If the text mask is false but the color is set to this value, we use the dye color (the darker one).
   */
  private static final int GLOW_DYE_OUTLINE_COLOR = 2;

  private final double hh, ww, u0, v0;
  private final Color dyeColor;
  private final boolean isGlowing;
  private final Texture signTexture;
  @Nullable
  private final PalettizedBitmapImage textColor;
  @Nullable
  private final BinaryBitmapImage textMask;

  static private boolean hasVisibleCharacter(JsonArray line) {
    for (JsonValue textItem : line) {
      if (!textItem.object().get("text").stringValue("").trim().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  public SignTexture(JsonArray[] text, Color dyeColor, boolean isGlowing, Texture signTexture, int signWidth, int signHeight, double x0, double y0, double x1, double y1, double fontSize, int ymargin, int lineHeight) {
    this.dyeColor = dyeColor;
    this.signTexture = signTexture;
    this.isGlowing = isGlowing;
    int width = (int) Math.ceil(signWidth * fontSize);
    int height = (int) Math.ceil(signHeight * fontSize);
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
          int textItemColor = textItem.object().get("color").intValue(-1);
          Color color = textItemColor >= 0 ? Color.get(textItemColor) : dyeColor;

          for (int c : textLine.codePoints().toArray()) {
            Glyph glyph = Texture.fonts.getGlyph(c);
            if (glyph != null) {
              int y = ystart - glyph.ascent + lineHeight;

              for (int py = 0; py < glyph.height; ++py) {
                int x = xstart;
                for (int px = glyph.xmin; px <= glyph.xmax; ++px) {
                  if ((glyph.lines[py] & (1 << px)) != 0) {
                    if (textItemColor >= 0) {
                      textColor.setPixel(x, y, color.id);
                      textMask.setPixel(x, y, true);
                    } else if (dyeColor != null) {
                      textColor.setPixel(x, y, 1);
                    }
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

      if (isGlowing) {
        // add the text outline (textmask=0, textcolor=GLOW_DYE_OUTLINE_COLOR)
        for (int y = 0; y < textColor.height; y++) {
          for (int x = 0; x < textColor.width; x++) {
            if (textMask.getPixel(x, y) || textColor.getPixel(x, y) == GLOW_DYE_COLOR) {
              continue;
            }
            for (int dy = -1; dy <= 1; dy++) {
              for (int dx = -1; dx <= 1; dx++) {
                if (dy == 0 && dx == 0) {
                  continue;
                }
                if (x + dx >= 0 && x + dx < textColor.width && y + dy >= 0 && y + dy < textColor.height
                  && (textMask.getPixel(x + dx, y + dy) || textColor.getPixel(x + dx, y + dy) == GLOW_DYE_COLOR)) {
                  textColor.setPixel(x, y, GLOW_DYE_OUTLINE_COLOR);
                }
              }
            }
          }
        }
      }
    }

    ww = x1 - x0;
    hh = y1 - y0;
    u0 = x0;
    v0 = y0;
  }

  @Override
  public float[] getColor(double u, double v) {
    if (textColor != null) {
      int x = (int) (u * textColor.width - Ray.EPSILON);
      int y = (int) ((1 - v) * textColor.height - Ray.EPSILON);
      if (textMask != null && textMask.getPixel(x, y)) {
        Color characterColor = Color.get(textColor.getPixel(x, y));
        return characterColor.linearColor;
      } else if (textColor.getPixel(x, y) == GLOW_DYE_COLOR) {
        if (this.isGlowing) {
          return dyeColor.getGlowingDyeColor().linearColor;
        } else {
          return dyeColor.linearColor;
        }
      } else if (textColor.getPixel(x, y) == GLOW_DYE_OUTLINE_COLOR) {
        return dyeColor.getGlowingOutlineColor().linearColor;
      }
    }
    return signTexture.getColor(u * ww + u0, v * hh + v0);
  }
}
