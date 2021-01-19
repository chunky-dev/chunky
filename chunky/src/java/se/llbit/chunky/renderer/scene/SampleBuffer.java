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

public class SampleBuffer {
  public final int width;   // = rowSize/3, used for alpha
  public final int rowSize; // = width * 3, used for samples

  public final int height;   // = rowCount
  public final int rowCount; // = height

  protected final double[][] samples;
  // protected int[][] spp;     // for non-uniform sample distribution
  protected byte[][] alpha;

  public SampleBuffer(int width, int height) {
    this.height = height;
    this.rowCount = height;

    this.width = width;
    this.rowSize = 3 * width;

    this.samples = new double[rowCount][rowSize];
  }

  public SampleBuffer(SampleBuffer toCopy) {
    this.height = toCopy.height;
    this.rowCount = toCopy.height;

    this.width = toCopy.width;
    this.rowSize = 3 * toCopy.width;

    this.samples = new double[rowCount][rowSize];
    for (int row = 0; row < rowCount; row++)
      System.arraycopy(toCopy.samples[row],0,this.samples[row],0,rowSize);

    if (toCopy.alpha!=null)
    {
      alphaInit();
      for (int row = 0; row < rowCount; row++)
        System.arraycopy(toCopy.alpha[row], 0, this.alpha[row], 0, width);
    }
  }

  public long pixelCount() {
    return width*(long)height;
  }

  public long sampleCount() {
    return 3L*width*height;
  }

  public double get(long index) {
    return samples[(int)(index/rowSize)][(int)(index%rowSize)];
  }

  public double get(int x, int y, int o) {
    return samples[y][3*x+o];
  }

  public void set(long index, double value) {
    samples[(int)(index/rowSize)][(int)(index%rowSize)] = value;
  }

  public void set(int x, int y, int o, double value) {
    samples[y][x*3+o] = value;
  }

  public int getArgb(int x, int y) {
    return ColorUtil.getArgb(get(x,y,0),get(x,y,1),get(x,y,2),0xFF);
  }

  public void addSample(int x, int y, int old_spp, int spp_increase, double r, double g, double b) {
    double sinv = 1.0 / (old_spp + spp_increase);
    set(x,y,0,sinv*(get(x,y,0)*old_spp+r));
    set(x,y,1,sinv*(get(x,y,1)*old_spp+g));
    set(x,y,2,sinv*(get(x,y,2)*old_spp+b));
  }

  public void addSample(long index, double old_spp, double spp_increase, double r, double g, double b) {
    double sinv = 1.0 / (old_spp + spp_increase);
    set(index, sinv * (get(index) * old_spp + r));
    set(index + 1, sinv * (get(index + 1) * old_spp + g));
    set(index + 2, sinv * (get(index + 2) * old_spp + b));
  }

  public void setPixel(int x, int y, double r, double g, double b) {
    set(x,y,0,r);
    set(x,y,1,g);
    set(x,y,2,b);
  }


  public void alphaInit() {
    alpha = new byte[rowCount][rowSize];
  }

  public byte alphaGet(int x, int y) {
    return alpha[y][x];
  }

  public void alphaSet(int x, int y, byte value) {
    alpha[y][x] = value;
  }

  public byte[] alphaCompile() {
    // This fails at an image size of about 46 000 x 46 000. (2100 megapixels)
    if (((long)width)*height>Integer.MAX_VALUE)
      throw new IndexOutOfBoundsException("Alpha too large. Cannot fit within a single array. Single arrays should only be used for small images, such as textures or MapTiles, not full renders!");

    byte[] copy = new byte[width*height];
    for (int row = 0; row < rowCount; row++)
      System.arraycopy(alpha[row],0,copy,row*width,width);
    return copy;
  }
}
