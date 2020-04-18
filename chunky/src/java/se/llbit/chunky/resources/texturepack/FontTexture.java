package se.llbit.chunky.resources.texturepack;

import java.util.HashMap;
import java.util.Map;
import se.llbit.chunky.resources.BitmapImage;

public class FontTexture {
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

  private Map<Character, Glyph> glyphs = new HashMap<>();

  public Glyph getGlyph(char c) {
    return glyphs.get(c);
  }

  public void setGlyph(char c, Glyph glyph) {
    glyphs.put(c, glyph);
  }

  public boolean containsGlyph(char c) {
    return glyphs.containsKey(c);
  }

  public void clear() {
    glyphs.clear();
  }

  void loadGlyph(
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
    setGlyph(ch, new Glyph(lines, xmin, xmax, width, height, ascent));
  }
}
