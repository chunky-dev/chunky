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
package se.llbit.tiff;

import java.io.File;
import java.io.FileOutputStream;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * TIFF image output. This supports 32-bit floating point channel output.
 *
 * <p>Non-32bit output has been removed sine it was unused.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TiffFileWriter implements AutoCloseable {

  private static final int ASCII = 2;
  private static final int SHORT = 3;
  private static final int LONG = 4;
  private static final int RATIONAL = 5;

  private final DataOutputStream out;

  public TiffFileWriter(OutputStream out) throws IOException {
    this.out = new DataOutputStream(out);
    out.write(0x4D);
    out.write(0x4D);
    out.write(0x00);
    out.write(0x2A);
  }

  /**
   * @throws IOException
   */
  public TiffFileWriter(File file) throws IOException {
    this(new FileOutputStream(file));
  }

  /**
   * @throws IOException
   */
  @Override public void close() throws IOException {
    out.close();
  }

  private void writeHeader(int width, int height, int bytesPerSample) throws IOException {
    out.writeInt(ifdOffset(width, height, bytesPerSample));
  }

  private void writeFooter(int width, int height, int bytesPerSample) throws IOException {
    int ifdOffset = ifdOffset(width, height, bytesPerSample);
    int numEntries = 15;

    // Number of IFD entries.
    out.writeShort(numEntries);

    // 1: Width.
    out.writeShort(0x0100);
    out.writeShort(SHORT);
    out.writeInt(1);
    out.writeShort(width);
    out.writeShort(0);

    // 2: Height.
    out.writeShort(0x0101);
    out.writeShort(SHORT);
    out.writeInt(1);
    out.writeShort(height);
    out.writeShort(0);

    // 3: Bits per sample.
    out.writeShort(0x0102);
    out.writeShort(SHORT);
    out.writeInt(3);
    int offsetBps = ifdOffset + 2 + 12 * numEntries + 2;
    out.writeInt(offsetBps);

    // 4: Compression type.
    out.writeShort(0x0103);
    out.writeShort(SHORT);
    out.writeInt(1);
    out.writeShort(1);
    out.writeShort(0);

    // 5: PhotometricInterpretation
    out.writeShort(0x0106);
    out.writeShort(SHORT);
    out.writeInt(1);
    out.writeShort(2);
    out.writeShort(0);

    // 7: StripOffsets
    out.writeShort(0x0111);
    out.writeShort(LONG);
    out.writeInt(1);
    out.writeInt(8);

    // 6: Orientation
    out.writeShort(0x0112);
    out.writeShort(SHORT);
    out.writeInt(1);
    out.writeShort(1); // First row is at the top of the image.
    out.writeShort(0);

    // 8: SamplesPerPixel
    out.writeShort(0x0115);
    out.writeShort(SHORT);
    out.writeInt(1);
    out.writeShort(3);
    out.writeShort(0);

    // 9: RowsPerStrip
    out.writeShort(0x0116);
    out.writeShort(LONG);
    out.writeInt(1);
    out.writeInt(height);

    // 10: StripByteCounts
    out.writeShort(0x0117);
    out.writeShort(LONG);
    out.writeInt(1);
    int offsetSbc = ifdOffset + 2 + 12 * numEntries + 2 + 2 * 3;
    out.writeInt(offsetSbc);

    // 11: XResolution
    out.writeShort(0x011A);
    out.writeShort(RATIONAL);
    out.writeInt(1);
    int offsetXres = ifdOffset + 2 + 12 * numEntries + 2 + 2 * 3 + 4;
    out.writeInt(offsetXres);

    // 12: YResolution
    out.writeShort(0x011B);
    out.writeShort(RATIONAL);
    out.writeInt(1);
    int offsetYres = ifdOffset + 2 + 12 * numEntries + 2 + 2 * 3 + 4 + 8;
    out.writeInt(offsetYres);

    // 13: ResolutionUnit
    out.writeShort(0x0128);
    out.writeShort(SHORT);
    out.writeInt(1);
    out.writeShort(1);
    out.writeShort(0);

    // 14: SampleFormat
    out.writeShort(0x0153);
    out.writeShort(SHORT);
    out.writeInt(3);
    int offsetSampleFormat = ifdOffset + 2 + 12 * numEntries + 2 + 2 * 3 + 4 + 8 + 8;
    out.writeInt(offsetSampleFormat);

    // 15: Software
    out.writeShort(0x0131);
    out.writeShort(ASCII);
    out.writeInt("Chunky".length() + 1);
    int offsetSoftware = ifdOffset + 2 + 12 * numEntries + 2 + 2 * 3 + 4 + 8 + 8 + 2 * 3;
    out.writeInt(offsetSoftware);

    // End of IFD.
    out.writeShort(0);

    // Bits per sample, values.
    out.writeShort(8 * bytesPerSample);
    out.writeShort(8 * bytesPerSample);
    out.writeShort(8 * bytesPerSample);

    // Strip byte count.
    out.writeInt(width * height * 3 * bytesPerSample);

    // X resolution.
    out.writeInt(0);
    out.writeInt(1);

    // Y resolution.
    out.writeInt(0);
    out.writeInt(1);

    // Sample formats.
    if (bytesPerSample == 1) {
      out.writeShort(1);
      out.writeShort(1);
      out.writeShort(1);
    } else {
      out.writeShort(3);
      out.writeShort(3);
      out.writeShort(3);
    }

    for (byte b : "Chunky".getBytes()) {
      out.write(b);
    }
    out.write(0);
  }

  /**
   * Write an image as a 32-bit per channel TIFF file.
   */
  public void write32(Scene scene, TaskTracker.Task task) throws IOException {
    int width = scene.canvasWidth();
    int height = scene.canvasHeight();
    writeHeader(width, height, 4);
    for (int y = 0; y < height; ++y) {
      task.update(height, y);
      for (int x = 0; x < width; ++x) {
        double[] pixel = new double[3];
        scene.postProcessPixel(x, y, pixel);
        out.writeFloat((float) pixel[0]);
        out.writeFloat((float) pixel[1]);
        out.writeFloat((float) pixel[2]);
      }
      task.update(height, y + 1);
    }
    writeFooter(width, height, 4);
  }

  private int ifdOffset(int width, int height, int bytesPerSample) {
    return 8 + width * height * 3 * bytesPerSample; // Offset to first IFD from file start.
  }
}
