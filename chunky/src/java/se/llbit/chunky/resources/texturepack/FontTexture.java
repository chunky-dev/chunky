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

  /** Maps code points to glyphs */
  private Map<Integer, Glyph> glyphs = new HashMap<>();

  public Glyph getGlyph(int codePoint) {
    return glyphs.get(codePoint);
  }

  public void setGlyph(int codePoint, Glyph glyph) {
    glyphs.put(codePoint, glyph);
  }

  public boolean containsGlyph(int codePoint) {
    return glyphs.containsKey(codePoint);
  }

  public void clear() {
    glyphs.clear();
  }

  /**
   * Load a glyph from a spritemap where all glyphs have the same dimensions.
   *
   * @param spritemap Spritemap
   * @param x0 Column of the glyph
   * @param y0 Row of the glyph
   * @param codePoint Code point the glyph corresponds to
   * @param width Width of the glyphs, in pixels
   * @param height Height of the glyphs, in pixels
   * @param ascent The number of pixels to move the glyph up in the line, used e.g. for accents on
   *     capital letters
   */
  void loadGlyph(
      BitmapImage spritemap, int x0, int y0, int codePoint, int width, int height, int ascent) {
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
    setGlyph(codePoint, new Glyph(lines, xmin, xmax, width, height, ascent));
  }
}
