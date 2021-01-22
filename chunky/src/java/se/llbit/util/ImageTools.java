/* Copyright (c) 2010 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.util;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.math.ColorUtil;

/**
 * Image manipulation utility methods.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class ImageTools {

  /**
   * Calculate the average color across an image.
   *
   * @return average color value
   */
  public static int calcAvgColor(BitmapImage image) {
    float ra = 0;
    float ga = 0;
    float ba = 0;
    float aa = 0;
    int n = 0;
    for (int x = 0; x < image.width; ++x) {
      for (int y = 0; y < image.height; ++y) {
        int cv = image.getPixel(x, y);
        float alpha = (cv >>> 24) / 255.f;
        aa += alpha;
        n++;
        ra += alpha * (0xFF & (cv >>> 16)) / 255.f;
        ga += alpha * (0xFF & (cv >>> 8)) / 255.f;
        ba += alpha * (0xFF & cv) / 255.f;
      }
    }

    if (aa == 0.f) {
      return 0;
    } else {
      return ColorUtil.getArgb(ra / aa, ga / aa, ba / aa, aa / n);
    }
  }
}
