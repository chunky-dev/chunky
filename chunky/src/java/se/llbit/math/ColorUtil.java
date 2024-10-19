/* Copyright (c) 2012-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2012-2021 Chunky contributors
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
package se.llbit.math;

import javafx.scene.paint.Color;
import org.apache.commons.math3.util.FastMath;

import se.llbit.chunky.renderer.scene.Scene;

/**
 * Collection of utility methods for converting between different color representations.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public final class ColorUtil {

  // Look up table used to speed up gamma correction
  private static final float[] toLinearLut = new float[256];

  static {
    for (int i = 0; i < 256; i++) {
      toLinearLut[i] = (float)Math.pow(i / 255.0, Scene.DEFAULT_GAMMA);
    }
  }

  private ColorUtil() {
  }

  /**
   * @param c RGB color vector
   * @return INT RGB value corresponding to the given color vector
   */
  public static int getRGB(Vector3 c) {
    return (0xFF << 24) |
        ((int) (255 * c.x + .5) << 16) |
        ((int) (255 * c.y + .5) << 8) |
        (int) (255 * c.z + .5);
  }

  /**
   * @return INT RGB value corresponding to the given color
   */
  public static int getRGB(float r, float g, float b) {
    return 0xFF000000 |
        ((int) (255 * r + .5f) << 16) |
        ((int) (255 * g + .5f) << 8) |
        (int) (255 * b + .5f);
  }

  /**
   * @return INT RGB value corresponding to the given color
   */
  public static int getRGB(double r, double g, double b) {
    return 0xFF000000 |
        ((int) (255 * r + .5) << 16) |
        ((int) (255 * g + .5) << 8) |
        (int) (255 * b + .5);
  }

  /**
   * @return INT ARGB value corresponding to the given color
   */
  public static int getArgb(float r, float g, float b, float a) {
    return ((int) (255 * a + .5f) << 24) |
        ((int) (255 * r + .5f) << 16) |
        ((int) (255 * g + .5f) << 8) |
        (int) (255 * b + .5f);
  }

  /**
   * @param c RGBA color vector
   * @return INT ARGB value corresponding to the given color vector
   */
  public static int getArgb(Vector4 c) {
    return ((int) (255 * c.w + .5f) << 24) |
        ((int) (255 * c.x + .5f) << 16) |
        ((int) (255 * c.y + .5f) << 8) |
        (int) (255 * c.z + .5f);
  }

  /**
   * @param c RGB color vector
   * @return INT RGB value corresponding to the given color vector
   */
  public static int getRGB(Vector4 c) {
    return 0xFF000000 |
        ((int) (255 * c.x + .5f) << 16) |
        ((int) (255 * c.y + .5f) << 8) |
        (int) (255 * c.z + .5f);
  }

  /**
   * @return INT ARGB value corresponding to the given color
   */
  public static int getArgb(double r, double g, double b, double a) {
    return ((int) (255 * a + .5) << 24) |
        ((int) (255 * r + .5) << 16) |
        ((int) (255 * g + .5) << 8) |
        (int) (255 * b + .5);
  }

  /**
   * Get the RGB color components from an INT RGB value.
   */
  public static void getRGBComponents(int irgb, float[] frgb) {
    frgb[0] = (0xFF & (irgb >> 16)) / 255.f;
    frgb[1] = (0xFF & (irgb >> 8)) / 255.f;
    frgb[2] = (0xFF & irgb) / 255.f;
  }

  /**
   * Get the RGB color components from an INT RGB value.
   */
  public static void getRGBComponents(int irgb, Vector4 v) {
    v.x = (0xFF & (irgb >> 16)) / 255.f;
    v.y = (0xFF & (irgb >> 8)) / 255.f;
    v.z = (0xFF & irgb) / 255.f;
  }

  /**
   * Get the RGB color components from an INT RGB value.
   */
  public static void getRGBComponents(int irgb, double[] frgb) {
    frgb[0] = (0xFF & (irgb >> 16)) / 255.0;
    frgb[1] = (0xFF & (irgb >> 8)) / 255.0;
    frgb[2] = (0xFF & irgb) / 255.0;
  }

  /**
   * Get the RGBA color components from an INT ARGB value.
   */
  public static void getRGBAComponents(int irgb, float[] frgb) {
    frgb[3] = (irgb >>> 24) / 255.f;
    frgb[0] = (0xFF & (irgb >> 16)) / 255.f;
    frgb[1] = (0xFF & (irgb >> 8)) / 255.f;
    frgb[2] = (0xFF & irgb) / 255.f;
  }

  /**
   * Get the RGBA color components from an INT ARGB value.
   */
  public static void getRGBAComponents(int irgb, Vector4 v) {
    v.w = (irgb >>> 24) / 255.f;
    v.x = (0xFF & (irgb >> 16)) / 255.f;
    v.y = (0xFF & (irgb >> 8)) / 255.f;
    v.z = (0xFF & irgb) / 255.f;
  }

  /**
   * Get the RGBA color components from an INT ARGB value.
   */
  public static void getRGBAComponents(int irgb, Vector3 v) {
    v.x = (0xFF & (irgb >> 16)) / 255.f;
    v.y = (0xFF & (irgb >> 8)) / 255.f;
    v.z = (0xFF & irgb) / 255.f;
  }

  /**
   * Get the RGBA color components from an INT ARGB value.
   */
  public static void getRGBAComponents(int irgb, double[] frgb) {
    frgb[3] = (irgb >>> 24) / 255.0;
    frgb[0] = (0xFF & (irgb >> 16)) / 255.0;
    frgb[1] = (0xFF & (irgb >> 8)) / 255.0;
    frgb[2] = (0xFF & irgb) / 255.0;
  }

  /**
   * Get the RGBA color component gamma corrected from an ARGB int
   */
  public static void getRGBAComponentsGammaCorrected(int argb, float[] components) {
    components[3] = (argb >>> 24) / 255.0f;
    components[0] = toLinearLut[(0xFF & (argb >> 16))];
    components[1] = toLinearLut[(0xFF & (argb >> 8))];
    components[2] = toLinearLut[(0xFF & argb)];
  }

  /**
   * Wraps {@link ColorUtil#getRGBAComponentsGammaCorrected} creating a new float[4]
   */
  public static float[] getRGBAComponentsGammaCorrected(int src) {
    float[] color = new float[4];
    getRGBAComponentsGammaCorrected(src, color);
    return color;
  }

  /**
   * @return Get INT RGB value corresponding to the given color
   */
  public static int getRGB(float[] frgb) {
    return 0xFF000000 |
        ((int) (255 * frgb[0] + .5f) << 16) |
        ((int) (255 * frgb[1] + .5f) << 8) |
        (int) (255 * frgb[2] + .5f);
  }

  /**
   * @return Get INT RGB value corresponding to the given color
   */
  public static int getRGB(double[] frgb) {
    return 0xFF000000 |
        ((int) (255 * frgb[0] + .5) << 16) |
        ((int) (255 * frgb[1] + .5) << 8) |
        (int) (255 * frgb[2] + .5);
  }

  /**
   * Transform from xyY colorspace to XYZ colorspace.
   */
  public static void xyYtoXYZ(Vector3 in, Vector3 out) {
    if (in.y <= Ray.EPSILON) {
      out.set(0, 0, 0);
      return;
    }
    double f = (in.z / in.y);
    out.x = in.x * f;
    out.z = (1 - in.x - in.y) * f;
    out.y = in.z;
  }

  /**
   * http://www.w3.org/Graphics/Color/sRGB
   */
  public static void XYZtoRGB(Vector3 in, Vector3 out) {
    out.x = 3.2410 * in.x - 1.5374 * in.y - 0.4986 * in.z;
    out.y = -0.9692 * in.x + 1.8760 * in.y + 0.0416 * in.z;
    out.z = 0.0556 * in.x - 0.2040 * in.y + 1.0570 * in.z;
  }

  /**
   * Convert color components to linear color space
   */
  public static void toLinear(float[] components) {
    for (int i = 0; i < components.length; ++i) {
      components[i] = (float) FastMath.pow(components[i], Scene.DEFAULT_GAMMA);
    }
  }

  public static String toString(double r, double g, double b) {
    int rgb = getRGB(r, g, b);
    return String.format("%02X%02X%02X", (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
  }

  public static String toString(Vector3 color) {
    int rgb = getRGB(color.x, color.y, color.z);
    return String.format("%02X%02X%02X", (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
  }

  public static void RGBtoHSL(Vector3 hsl, double r, double g, double b) {
    r = QuickMath.clamp(r, 0, 1);
    g = QuickMath.clamp(g, 0, 1);
    b = QuickMath.clamp(b, 0, 1);

    double cmax = FastMath.max(FastMath.max(r, g), b);
    double cmin = FastMath.min(FastMath.min(r, g), b);
    double delta = cmax - cmin;
    double lightness = (cmax + cmin) / 2;

    double hue;
    if (delta == 0) {
      hue = 0;
    } else if (cmax == r) {
      hue = (((g - b) / delta) % 6) / 6.0;
    } else if (cmax == g) {
      hue = (((b - r) / delta) + 2) / 6.0;
    } else {
      hue = (((r - g) / delta) + 4) / 6.0;
    }

    hsl.set(hue, delta < Ray.EPSILON ? 0 : delta / (1 - FastMath.abs(2*lightness - 1)), lightness);
  }

  public static Vector3 RGBtoHSL(double r, double g, double b) {
    Vector3 color = new Vector3();
    RGBtoHSL(color, r, g, b);
    return color;
  }

  public static void RGBfromHSL(Vector3 rgb, double hue, double saturation, double lightness) {
    double c = Math.min(1, (1 - Math.abs(2 * lightness - 1)) * saturation);
    double h = hue * 6;
    double x = c * (1 - Math.abs(h % 2 - 1));
    if (h < 1) {
      rgb.set(c, x, 0);
    } else if (h < 2) {
      rgb.set(x, c, 0);
    } else if (h < 3) {
      rgb.set(0, c, x);
    } else if (h < 4) {
      rgb.set(0, x, c);
    } else if (h < 5) {
      rgb.set(x, 0, c);
    } else {
      rgb.set(c, 0, x);
    }
    double m = Math.max(0, lightness - 0.5 * c);
    rgb.x += m;
    rgb.y += m;
    rgb.z += m;
  }

  public static Vector3 RGBfromHSL(double hue, double saturation, double lightness) {
    double c = Math.min(1, (1 - Math.abs(2 * lightness - 1)) * saturation);
    double h = hue * 6;
    double x = c * (1 - Math.abs(h % 2 - 1));
    Vector3 rgb;
    if (h < 1) {
      rgb = new Vector3(c, x, 0);
    } else if (h < 2) {
      rgb = new Vector3(x, c, 0);
    } else if (h < 3) {
      rgb = new Vector3(0, c, x);
    } else if (h < 4) {
      rgb = new Vector3(0, x, c);
    } else if (h < 5) {
      rgb = new Vector3(x, 0, c);
    } else {
      rgb = new Vector3(c, 0, x);
    }
    double m = Math.max(0, lightness - 0.5 * c);
    rgb.x += m;
    rgb.y += m;
    rgb.z += m;
    return rgb;
  }

  public static java.awt.Color toAWT(Vector3 color) {
    return new java.awt.Color((float) color.x, (float) color.y, (float) color.z);
  }

  public static void fromString(String text, int radix, Vector3 color)
      throws NumberFormatException {
    int rgb = Integer.parseInt(text, radix);
    ColorUtil.getRGBAComponents(rgb, color);
  }

  public static void fromHexString(String hex, Vector3 color) {
    if (hex.startsWith("#")) {
      hex = hex.substring(1);
    }

    if (hex.length() == 3) {
      hex = "" + hex.charAt(0) + hex.charAt(0)
        + hex.charAt(1) + hex.charAt(1)
        + hex.charAt(2) + hex.charAt(2);
    }

    if (hex.length() != 6) {
      throw new IllegalArgumentException("Expected three or six digit hex color");
    }

    fromString(hex, 16, color);
  }

  public static javafx.scene.paint.Color toFx(Vector3 color) {
    return new javafx.scene.paint.Color(color.x, color.y, color.z, 1);
  }

  public static Vector3 fromFx(javafx.scene.paint.Color color) {
    return new Vector3(color.getRed(), color.getGreen(), color.getBlue());
  }

  public static Color toFx(int argb) {
    double[] rgba = new double[4];
    getRGBAComponents(argb, rgba);
    return Color.color(rgba[0], rgba[1], rgba[2], rgba[3]);
  }

  /**
   * Overlay the two colors. The result is written into the first vector.
   * @param target RGBA color vector, will be set to the result
   * @param overlayColor Second color (RGBA)
   */
  public static void overlayColor(Vector4 target, float[] overlayColor) {
    double a = overlayColor[3];
    double alpha = (1 - target.w) * a + target.w;
    target.x = ((1 - target.w) * a * overlayColor[0] + target.w * target.x) / alpha;
    target.y = ((1 - target.w) * a * overlayColor[1] + target.w * target.y) / alpha;
    target.z = ((1 - target.w) * a * overlayColor[2] + target.w * target.z) / alpha;
    target.w = alpha;
  }

  /**
   * Overlay the two colors. The result is written into the first vector.
   * @param target RGBA color vector, will be set to the result
   * @param overlayColor Second color vector (RGBA)
   */
  public static void overlayColor(Vector4 target, Vector4 overlayColor) {
    double a = overlayColor.w;
    double alpha = (1 - target.w) * a + target.w;
    target.x = ((1 - target.w) * a * overlayColor.x + target.w * target.x) / alpha;
    target.y = ((1 - target.w) * a * overlayColor.y + target.w * target.y) / alpha;
    target.z = ((1 - target.w) * a * overlayColor.z + target.w * target.z) / alpha;
    target.w = alpha;
  }

  /**
   * Convert single color component from linear to non-linear (sRGB?)
   */
  public static byte RGBComponentFromLinear(float linearValue) {
    float value = (float) Math.pow(linearValue, 1 / Scene.DEFAULT_GAMMA);
    value = Math.min(1.0f, value);
    return (byte)(value * 255);
  }

  /**
   * Convert a single component from non-linear to linear
   */
  public static float RGBComponentToLinear(byte value) {
    return toLinearLut[value & 0xFF];
  }
}
