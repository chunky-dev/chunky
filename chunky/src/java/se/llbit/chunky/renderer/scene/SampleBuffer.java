/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer.scene;

import se.llbit.math.ColorUtil;

import java.util.Arrays;

/**
 * Holds the samples (render's pixels) as a 2d array of doubles, also tracking SPP per pixel (not fully implemented, eg.
 * Scene#spp is still in use.)
 */
public class SampleBuffer {

  public final int width;      // = width        "width"
  public final int rowSize;    // = width * 3    used for samples, (x3 for rgb next to each other)
  public final int rowSizeSpp; // = width        used for SPP and alpha

  public final int height;      // = height      "height"
  public final int rowCount;    // = height * 1  used for samples
  public final int rowCountSpp; // = height      used for SPP and alpha

  protected int[][] spp;     // for non-uniform sample distribution
  protected final double[][] samples;
  protected byte[][] alpha;
  protected long totalSamples;

  public SampleBuffer(int width, int height) {
    this.height = height;
    this.rowCount = height;
    this.rowCountSpp = height;

    this.width = width;
    this.rowSize = 3 * width;
    this.rowSizeSpp = width;

    this.samples = new double[rowCount][rowSize];
    this.spp = new int[rowCountSpp][rowSizeSpp];
  }

  public SampleBuffer(SampleBuffer toCopy) {
    this.height = toCopy.height;
    this.rowCount = toCopy.height;
    this.rowCountSpp = toCopy.height;

    this.width = toCopy.width;
    this.rowSize = 3 * toCopy.width;
    this.rowSizeSpp = toCopy.width;

    this.samples = new double[rowCount][rowSize];
    for (int row = 0; row < rowCount; row++) {
      System.arraycopy(toCopy.samples[row], 0, this.samples[row], 0, rowSize);
    }

    this.spp = new int[rowCountSpp][rowSizeSpp];
    for (int row = 0; row < rowCountSpp; row++) {
      System.arraycopy(toCopy.spp[row], 0, this.spp[row], 0, rowSizeSpp);
    }

    if (toCopy.alpha != null) {
      enableAlpha();
      for (int row = 0; row < rowCount; row++) {
        System.arraycopy(toCopy.alpha[row], 0, this.alpha[row], 0, width);
      }
    }
  }

  public long numberOfPixels() {
    return width * (long) height;
  }

  public long numberOfDoubles() {
    return 3L * width * height;
  }

  public long numberOfSamples() {
    return totalSamples;
  }

  public double get(long index) {
    return samples[(int) (index / rowSize)][(int) (index % rowSize)];
  }

  public double get(int x, int y, int o) {
    return samples[y][3 * x + o];
  }

  public void set(long index, double value) {
    samples[(int) (index / rowSize)][(int) (index % rowSize)] = value;
  }

  public void set(int x, int y, int o, double value) {
    samples[y][x * 3 + o] = value;
  }

  public int getArgb(int x, int y) {
    return ColorUtil.getArgb(get(x, y, 0), get(x, y, 1), get(x, y, 2), 0xFF);
  }

  /**
   * @param r     Sum of red values (Not average, this will be divided by 'weight')
   * @param g     Sum of green values (Not average, this will be divided by 'weight')
   * @param b     Sum of blue values (Not average, this will be divided by 'weight')
   * @param count The number of samples that this is the sum of. Use for "sppPerPass" where multiple SPP is added each
   *              pass.
   */
  public void addSamples(int x, int y, double r, double g, double b, int count) {
    int old_spp = getSpp(x, y);
    double sinv = 1.0 / (old_spp + count);
    set(x, y, 0, sinv * (get(x, y, 0) * old_spp + r));
    set(x, y, 1, sinv * (get(x, y, 1) * old_spp + g));
    set(x, y, 2, sinv * (get(x, y, 2) * old_spp + b));
    addSpp(x, y, count);
  }

  /**
   * @param r     Sum of red values (Not average, this will be divided by 'weight')
   * @param g     Sum of green values (Not average, this will be divided by 'weight')
   * @param b     Sum of blue values (Not average, this will be divided by 'weight')
   * @param count The number of samples that this is the sum of. Use for "sppPerPass" where multiple SPP is added each
   *              pass.
   */
  public void addSamples(long index, double r, double g, double b, int count) {
    int oldSpp = getSpp(index);
    index *= 3;
    double sinv = 1.0 / (oldSpp + count);
    set(index, sinv * (get(index) * oldSpp + r));
    set(index + 1, sinv * (get(index + 1) * oldSpp + g));
    set(index + 2, sinv * (get(index + 2) * oldSpp + b));
    addSpp(index / 3, count);
  }

  /**
   * r g b values should be actual rgb value: eg for a red value of 90% and 20 samples, 0.9 should be passed as r.
   */
  public void mergeSamples(int x, int y, double r, double g, double b, int count) {
    int oldSpp = getSpp(x, y);
    double sinv = 1.0 / (oldSpp + count);
    set(x, y, 0, sinv * (get(x, y, 0) * oldSpp + r * count));
    set(x, y, 1, sinv * (get(x, y, 1) * oldSpp + g * count));
    set(x, y, 2, sinv * (get(x, y, 2) * oldSpp + b * count));
    addSpp(x, y, count);
  }

  /**
   * r g b values should be actual rgb value: eg for a red value of 90% and 20 samples, 0.9 should be passed as r.
   */
  public void mergeSamples(long index, double r, double g, double b, int count) {
    int oldSpp = getSpp(index);
    index *= 3;
    double sinv = 1.0 / (oldSpp + count);
    set(index, sinv * (get(index) * oldSpp + r * count));
    set(index + 1, sinv * (get(index + 1) * oldSpp + g * count));
    set(index + 2, sinv * (get(index + 2) * oldSpp + b * count));
    addSpp(index / 3, count);
  }

  public void setPixel(int x, int y, double r, double g, double b) {
    set(x, y, 0, r);
    set(x, y, 1, g);
    set(x, y, 2, b);
  }


  public int getSpp(int x, int y) {
    return spp[y][x];
  }

  public int getSpp(long index) {
    return spp[(int) (index / rowSizeSpp)][(int) (index % rowSizeSpp)];
  }

  protected void addSpp(int x, int y, int sppIncrease) {
    totalSamples += sppIncrease;
    spp[y][x] += sppIncrease;
  }

  public void setSpp(int x, int y, int spp) {
    totalSamples += spp - this.spp[y][x];
    this.spp[y][x] = spp;
  }

  protected void addSpp(long index, int sppIncrease) {
    totalSamples += sppIncrease;
    spp[(int) (index / rowSizeSpp)][(int) (index % rowSizeSpp)] += sppIncrease;
  }

  public void setSpp(long index, int spp) {
    int x = (int) (index % rowSizeSpp);
    int y = (int) (index / rowSizeSpp);
    totalSamples += spp-this.spp[y][x];
    this.spp[y][x] = spp;
  }

  public void setPixelWithSpp(int x, int y, double r, double g, double b, int spp) {
    set(x, y, 0, r);
    set(x, y, 1, g);
    set(x, y, 2, b);
    totalSamples += spp - this.spp[y][x];
    this.spp[y][x] = spp;
  }

  public void copyPixels(SampleBuffer src, int src_x, int src_y, int dest_x, int dest_y, int width, int height) {

    for (int y = 0; y < height; y++)
      System.arraycopy(src.samples[src_y + y], 3 * src_x, this.samples[dest_y + y], 3 * dest_x, 3 * width);

    for (int y=0; y<height; y++)
      for (int x=0; x<width; x++)
        totalSamples-=getSpp(x+dest_x,y+dest_y);

    for (int y = 0; y < height; y++)
      System.arraycopy(src.spp[src_y + y], src_x, this.spp[dest_y + y], dest_x, width);

    for (int y=0; y<height; y++)
      for (int x=0; x<width; x++)
        totalSamples+=getSpp(x+dest_x,y+dest_y);
  }

  public void reset() {
    setGlobalSpp(0);
  }

  public void enableAlpha() {
    alpha = new byte[rowCountSpp][rowSizeSpp];
  }

  public byte getAlpha(int x, int y) {
    if (alpha == null) {
      return 1;
    }
    return alpha[y][x];
  }

  public void setAlpha(int x, int y, byte value) {
    if (alpha == null) {
      enableAlpha();
    }
    alpha[y][x] = value;
  }

  public byte[] getAlphaChannelAsArray() {
    // This fails at an image size of about 46 000 x 46 000. (2100 megapixels)
    if (((long) width) * height > Integer.MAX_VALUE) {
      throw new IndexOutOfBoundsException(
          "Alpha too large. Cannot fit within a single array. Single arrays should " +
              "only be used for small images, such as textures or MapTiles, not full renders!");
    }

    byte[] copy = new byte[width * height];
    if (alpha != null) {
      for (int row = 0; row < rowCount; row++) {
        System.arraycopy(alpha[row], 0, copy, row * width, width);
      }
    } else {
      for (int idx = 0; idx < width * height; idx++) {
        copy[idx] = 1;
      }
    }
    return copy;
  }

  public void setGlobalSpp(int spp) {
    totalSamples = 0;
    for (int[] ints : this.spp) {
      Arrays.fill(ints, spp);
    }
  }
}
