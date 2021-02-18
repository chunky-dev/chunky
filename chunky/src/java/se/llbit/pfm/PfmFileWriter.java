/* Copyright (c) 2020-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2020-2021 Chunky contributors
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
package se.llbit.pfm;

import se.llbit.chunky.renderer.Postprocess;
import se.llbit.chunky.renderer.scene.SampleBuffer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Portable FloatMap image file writer.
 */
public class PfmFileWriter implements AutoCloseable {
  private final DataOutputStream out;

  public PfmFileWriter(OutputStream out) {
    this.out = new DataOutputStream(out);
  }

  public PfmFileWriter(File file) throws IOException {
    this(new FileOutputStream(file));
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

  public void write(Scene scene, TaskTracker.Task task) throws IOException {
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    writeHeader(scene, byteOrder);

    // Image's actual data.
    writePixelData(scene, byteOrder, task);

    // No footer data to write.
  }

  private void writeHeader(Scene scene, ByteOrder byteOrder) throws IOException {
    //Declare File Type
    out.write("PF".getBytes(StandardCharsets.US_ASCII));
    out.write(0x0a);

    // Declare Image Size
    out.write((scene.subareaWidth()+" "+scene.subareaHeight()).getBytes(StandardCharsets.US_ASCII));
    out.write(0x0a);

    // Declare Byte Order
    out.write((byteOrder == ByteOrder.LITTLE_ENDIAN ? "-1.0" : "1.0").getBytes(StandardCharsets.US_ASCII));
    out.write(0x0a);
  }

  private void writePixelData(Scene scene, ByteOrder byteOrder, TaskTracker.Task task) throws IOException
  {
    int width = scene.subareaWidth();
    int height = scene.subareaHeight();

    // one or the other will be used, depending on if postprocessing is enabled.
    double[] pixel = new double[3];
    SampleBuffer sampleBuffer = scene.getSampleBuffer();

    // write each row...
    for (int y = height-1; y >= 0; y--) {
      task.update(height, height-y-1);

      // Prepare our row buffers
      ByteBuffer buffer = ByteBuffer.allocate(width*3*4).order(byteOrder);
      FloatBuffer floatBuffer = buffer.asFloatBuffer();

      // get the row's data as floats
      if (scene.postprocess == Postprocess.NONE)
        // from raw pixel data
        for (int x = 0; x < width; x++) {
          floatBuffer.put((float)sampleBuffer.get(x, y, 0));
          floatBuffer.put((float)sampleBuffer.get(x, y, 1));
          floatBuffer.put((float)sampleBuffer.get(x, y, 2));
        }
      else
        // or from post processor
        for (int x = 0; x < width; x++) {
          scene.postProcessPixel(x, y, pixel);
          floatBuffer.put((float)pixel[0]);
          floatBuffer.put((float)pixel[1]);
          floatBuffer.put((float)pixel[2]);
        }

      // Write buffer to stream
      out.write(buffer.array());
    }
  }
}
