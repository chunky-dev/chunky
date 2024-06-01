/* Copyright (c) 2012-2015 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2015-2022 Chunky contributors
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
package se.llbit.imageformats.png;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import se.llbit.chunky.renderer.scene.AlphaBuffer;
import se.llbit.util.TaskTracker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class PngFileWriter implements AutoCloseable {

  /**
   * PNG magic value
   */
  public static final long PNG_SIGNATURE = 0x89504E470D0A1A0AL;

  public static final int MAX_CHUNK_BYTES = 0x100000; // Max input/output buffer size = 1 MiB.

  private final DataOutputStream out;

  /**
   * @throws IOException
   */
  public PngFileWriter(OutputStream out) throws IOException {
    this.out = new DataOutputStream(out);
    this.out.writeLong(PNG_SIGNATURE);
  }

  /**
   * @throws IOException
   */
  public PngFileWriter(File file) throws IOException {
    this(new FileOutputStream(file));
  }

  /**
   * @throws IOException
   */
  public void writeChunk(PngChunk chunk) throws IOException {
    chunk.writeChunk(out);
  }

  /**
   * Writes the IEND chunk and closes the stream.
   */
  @Override public void close() throws IOException {
    try {
      writeChunk(new IEND());
    } finally {
      out.close();
    }
  }

  /**
   * Write the image to a PNG file.
   */
  public void write(int[] data, int width, int height, TaskTracker.Task task)
      throws IOException {
    writeChunk(new IHDR(width, height));
    IDATWriter idat = new IDATWriter();
    int i = 0;
    for (int y = 0; y < height; ++y) {
      task.update(height, y);
      idat.write(IDAT.FILTER_TYPE_NONE); // Scanline header.
      for (int x = 0; x < width; ++x) {
        int rgb = data[i++];
        idat.write((rgb >> 16) & 0xFF);
        idat.write((rgb >> 8) & 0xFF);
        idat.write(rgb & 0xFF);
      }
      task.update(height, y + 1);
    }
    idat.close();
  }

  /**
   * Write the image to a PNG file.
   */
  public void write(int[] data, ByteBuffer alpha, int width, int height,
      TaskTracker.Task task) throws IOException {
    writeChunk(new IHDR(width, height, IHDR.COLOR_TYPE_RGBA));
    IDATWriter idat = new IDATWriter();
    int i = 0;
    for (int y = 0; y < height; ++y) {
      task.update(height, y);
      idat.write(IDAT.FILTER_TYPE_NONE); // Scanline header.
      for (int x = 0; x < width; ++x) {
        int rgb = data[i];
        idat.write((rgb >> 16) & 0xFF);
        idat.write((rgb >> 8) & 0xFF);
        idat.write(rgb & 0xFF);
        idat.write(alpha.get(i));
        i += 1;
      }
      task.update(height, y + 1);
    }
    idat.close();
  }

  class IDATWriter {
    Deflater deflater = new Deflater();
    int inputSize = 0;
    byte[] inputBuf = new byte[MAX_CHUNK_BYTES];
    int outputSize = 0;
    byte[] outputBuf = new byte[MAX_CHUNK_BYTES];

    void write(int b) throws IOException {
      if (inputSize == MAX_CHUNK_BYTES) {
        deflater.setInput(inputBuf, 0, inputSize);
        inputSize = 0;
        deflate();
      }
      inputBuf[inputSize++] = (byte) b;
    }

    void write16(int bb) throws IOException {
      write(bb >> 8);
      write(bb & 0xFF);
    }

    private void deflate() throws IOException {
      int deflated;
      do {
        if (outputSize == MAX_CHUNK_BYTES) {
          writeChunk();
        }
        deflated = deflater.deflate(outputBuf, outputSize, MAX_CHUNK_BYTES - outputSize);
        outputSize += deflated;
      } while (deflated != 0);
    }

    private void writeChunk() throws IOException {
      out.writeInt(outputSize);

      try (
        CrcOutputStream crcOut = new CrcOutputStream();
        DataOutputStream crc = new DataOutputStream(crcOut);
      ) {
        crc.writeInt(IDAT.CHUNK_TYPE);
        out.writeInt(IDAT.CHUNK_TYPE);

        crc.write(outputBuf, 0, outputSize);
        out.write(outputBuf, 0, outputSize);

        out.writeInt(crcOut.getCRC());
      }

      outputSize = 0;
    }

    void close() throws IOException {
      if (inputSize > 0) {
        deflater.setInput(inputBuf, 0, inputSize);
        deflater.finish();
        inputSize = 0;
        deflate();
      }
      if (outputSize > 0) {
        writeChunk();
      }
      deflater.end();
    }
  }
}
